package hanu.npr.messengerserver.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hanu.npr.messengerserver.models.User;
import hanu.npr.messengerserver.models.dtos.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class UserSessionThread extends Thread {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final Socket socket;
    private final ServerController server;

    private PrintWriter writer;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    private User user;

    public UserSessionThread(Socket socket, ServerController server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            String clientPayload = reader.readLine();

            while (clientPayload != null) {
                processPayload(clientPayload);
                clientPayload = reader.readLine();
            }

            if (user != null)
                server.logoutUser(this);

            reader.close();
            input.close();

            writer.close();
            output.close();

            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void processPayload(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);

            System.out.println(payload);

            String type = jsonNode.at("/type").asText();

            // Decide what to do base on the type of the payload
            switch (type) {
                case ChatMessageDTO.TYPE:
                    ChatMessageDTO chatMessageDTO = objectMapper.readValue(payload, ChatMessageDTO.class);
                    server.processMessage(chatMessageDTO, this);
                    break;
                case RegistrationDTO.TYPE:
                    RegistrationDTO registrationDTO = objectMapper.readValue(payload, RegistrationDTO.class);
                    server.registerUser(registrationDTO, this);
                    break;
                case LoginDTO.TYPE:
                    LoginDTO loginDTO = objectMapper.readValue(payload, LoginDTO.class);
                    server.loginUser(loginDTO, this);
                    break;
                case PrivateConversationDTO.TYPE:
                    PrivateConversationDTO privateConversationDTO = objectMapper.readValue(payload, PrivateConversationDTO.class);
                    // Do not trust username1 sent by client
                    privateConversationDTO.setUsername1(user.getUsername());
                    server.createPrivateConversation(privateConversationDTO.getUsername1(), privateConversationDTO.getUsername2());
                    break;
                case DownloadAttachmentRequestDTO.TYPE:
                    DownloadAttachmentRequestDTO downloadAttachmentRequestDTO = objectMapper.readValue(payload, DownloadAttachmentRequestDTO.class);
                    server.sendAttachmentToUser(downloadAttachmentRequestDTO.getAttachmentId(), this);
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a payload to the client.
     */
    public void send(String payload) {
        writer.println(payload);
    }
}