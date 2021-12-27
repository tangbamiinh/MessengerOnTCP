package hanu.npr.messengerclient.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hanu.npr.messengerclient.models.Attachment;
import hanu.npr.messengerclient.models.GroupConversation;
import hanu.npr.messengerclient.models.PrivateConversation;
import hanu.npr.messengerclient.models.User;
import hanu.npr.messengerclient.models.dtos.*;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;


public class MainController {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final ObjectWriter objectWriter = objectMapper.writer();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final InputStream input;
    private final BufferedReader reader;

    private final OutputStream output;
    private final PrintWriter writer;

    private final Socket socket;

    private final Interface callback;

    private User currentUser;

    public MainController(String host, int port, Interface callback) throws IOException {
        socket = new Socket(host, port);
        this.callback = callback;

        input = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(input));

        output = socket.getOutputStream();
        writer = new PrintWriter(output, true);

        new Thread(this::listenToServer).start();
        callback.onConnectionSuccess(host, port);
    }

    private void listenToServer() {
        try {
            String serverPayload = reader.readLine();

            while (serverPayload != null) {
                processJSONPayload(serverPayload);
                serverPayload = reader.readLine();
            }
            reader.close();
            input.close();

            writer.close();
            output.close();

            socket.close();

            callback.onServerShutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void login(String username, String password) {
        LoginDTO loginDTO = new LoginDTO(username, password);
        sendDTO(loginDTO);
    }

    public void signup(String fullName, String username, String password) {
        RegistrationDTO registrationDTO = new RegistrationDTO(fullName, username, password);
        sendDTO(registrationDTO);
    }

    public void createPrivateChatWith(String username) {
        PrivateConversationDTO privateConversationDTO = new PrivateConversationDTO();
        privateConversationDTO.setUsername1(currentUser.getUsername());
        privateConversationDTO.setUsername2(username);
        sendDTO(privateConversationDTO);
    }

    @SneakyThrows
    public void sendMessage(Long conversationId, String content, File file) {
        Attachment attachment = null;
        if (file != null) {
            attachment = new Attachment();
            attachment.setFileName(file.getName());
            attachment.setFile(FileUtils.readFileToByteArray(file));
        }
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(null, conversationId, currentUser.getUsername(), content, attachment);
        sendDTO(chatMessageDTO);
    }

    @SneakyThrows
    private void sendDTO(BaseDTO data) {
        writer.println(objectWriter.writeValueAsString(data));
    }

    @SneakyThrows
    private void processJSONPayload(String jsonPayload) throws JsonProcessingException {

        if (jsonPayload == null) return;

        System.out.println(jsonPayload);

        JsonNode jsonNode = objectMapper.readTree(jsonPayload);

        String type = jsonNode.at("/type").asText();
        // Decide what to do base on the type of the payload

        switch (type) {
            case ChatMessageDTO.TYPE: {
                ChatMessageDTO msg = objectMapper.readValue(jsonPayload, ChatMessageDTO.class);
                callback.onChatMessageReceived(msg.getConversationId(), msg.getSenderUsername(), msg.getContent(), msg.getAttachment());
                break;
            }
            case NewUserJoinedDTO.TYPE: {
                NewUserJoinedDTO data = objectMapper.readValue(jsonPayload, NewUserJoinedDTO.class);
                callback.onNewUserJoined(data.getUsername(), data.getFullName());
                break;
            }
            case InitialDataDTO.TYPE: {
                InitialDataDTO data = objectMapper.readValue(jsonPayload, InitialDataDTO.class);
                callback.onInitialDataReceived(data.getOnlineUsers(), data.getGroupConversations(), data.getPrivateConversations());
                break;
            }
            case ErrorDTO.TYPE: {
                ErrorDTO data = objectMapper.readValue(jsonPayload, ErrorDTO.class);
                callback.onErrorReceived(data.getMessage());
                break;
            }
            case SuccessDTO.TYPE: {
                SuccessDTO data = objectMapper.readValue(jsonPayload, SuccessDTO.class);
                callback.onSuccessReceived(data.getMessage());
                break;
            }

            case LoggedInUserDTO.TYPE: {
                LoggedInUserDTO data = objectMapper.readValue(jsonPayload, LoggedInUserDTO.class);

                currentUser = new User();
                currentUser.setUsername(data.getUsername());
                currentUser.setFullName(data.getFullName());

                callback.onLogInSuccess(data.getFullName(), data.getUsername());
                break;
            }
            case PrivateConversationDTO.TYPE: {
                PrivateConversationDTO data = objectMapper.readValue(jsonPayload, PrivateConversationDTO.class);
                String username = data.getUsername1();
                String fullName = data.getFullName1();

                if (username.equals(currentUser.getUsername())) {
                    username = data.getUsername2();
                    fullName = data.getFullName2();
                }
                callback.onPrivateConversationReceived(data.getId(), username, fullName);
                break;
            }
            case DownloadAttachmentResponseDTO.TYPE: {
                DownloadAttachmentResponseDTO data = objectMapper.readValue(jsonPayload, DownloadAttachmentResponseDTO.class);
                callback.onAttachmentDownloaded(data.getAttachment());
                break;
            }
        }
    }

    public void downloadAttachment(Attachment attachment) {
        DownloadAttachmentRequestDTO downloadAttachmentRequestDTO = new DownloadAttachmentRequestDTO();
        downloadAttachmentRequestDTO.setAttachmentId(attachment.getId());
        sendDTO(downloadAttachmentRequestDTO);
    }

    public interface Interface {

        void onConnectionSuccess(String host, int port);

        void onChatMessageReceived(Long conversationId, String senderUsername, String content, Attachment attachment);

        void onNewUserJoined(String username, String fullName);

        void onInitialDataReceived(List<User> onlineUsers, List<GroupConversation> groupConversations, List<PrivateConversation> privateConversations);

        void onErrorReceived(String errorMessage);

        void onSuccessReceived(String successMessage);

        void onLogInSuccess(String fullName, String username);

        void onServerShutdown();

        void onPrivateConversationReceived(Long conversationId, String username, String fullName);

        void onAttachmentDownloaded(Attachment attachment);
    }
}
