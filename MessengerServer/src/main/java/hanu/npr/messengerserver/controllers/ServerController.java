package hanu.npr.messengerserver.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import hanu.npr.messengerserver.config.InitData;
import hanu.npr.messengerserver.models.ChatMessage;
import hanu.npr.messengerserver.models.Conversation;
import hanu.npr.messengerserver.models.GroupConversation;
import hanu.npr.messengerserver.models.PrivateConversation;
import hanu.npr.messengerserver.models.User;
import hanu.npr.messengerserver.models.dtos.BaseDTO;
import hanu.npr.messengerserver.models.dtos.ChatMessageDTO;
import hanu.npr.messengerserver.models.dtos.ErrorDTO;
import hanu.npr.messengerserver.models.dtos.InitialDataDTO;
import hanu.npr.messengerserver.models.dtos.LoggedInUserDTO;
import hanu.npr.messengerserver.models.dtos.LoginDTO;
import hanu.npr.messengerserver.models.dtos.NewUserJoinedDTO;
import hanu.npr.messengerserver.models.dtos.PrivateConversationDTO;
import hanu.npr.messengerserver.models.dtos.RegistrationDTO;
import hanu.npr.messengerserver.repositories.ChatMessageRepository;
import hanu.npr.messengerserver.repositories.ConversationRepository;
import hanu.npr.messengerserver.repositories.GroupConversationRepository;
import hanu.npr.messengerserver.repositories.PrivateConversationRepository;
import hanu.npr.messengerserver.repositories.UserRepository;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class ServerController {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final HashMap<String, UserSessionThread> userThreadMap = new HashMap<>();

    private final ChatMessageRepository chatMessageRepository = new ChatMessageRepository();

    private final ConversationRepository conversationRepository = new ConversationRepository();

    private final PrivateConversationRepository privateConversationRepository = new PrivateConversationRepository();

    private final GroupConversationRepository groupConversationRepository = new GroupConversationRepository();

    private final UserRepository userRepository = new UserRepository();

    private final InitData initData;

    final Interface callback;

    public ServerController(int port, Interface callback) throws IOException {
        this.callback = callback;

        //! Initialize data
        initData = new InitData();

        ServerSocket serverSocket = new ServerSocket(port);
        callback.onPortOpened(port);

        new Thread(() -> {
            while (true) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callback.onNewConnectionCreated();

                UserSessionThread newUser = new UserSessionThread(socket, this);
                newUser.start();
            }
        }).start();
    }

    private UserSessionThread getUserThreadByUsername(String username) {
        return userThreadMap.get(username);
    }

    @SneakyThrows
    private void sendToAll(BaseDTO data, String excludedUsername) {
        for (UserSessionThread userServerThread : userThreadMap.values()) {
            // Exclude sender
            if (excludedUsername != null && excludedUsername.equals(userServerThread.getUser().getUsername()))
                continue;
            userServerThread.send(objectMapper.writeValueAsString(data));
        }
    }

    @SneakyThrows
    private void sendToUser(String username, BaseDTO data) {
        UserSessionThread userSessionThread = getUserThreadByUsername(username);
        if (userSessionThread != null)
            userSessionThread.send(objectMapper.writeValueAsString(data));
    }

    private void sendMessageToUser(ChatMessageDTO chatMessageDTO, PrivateConversation privateConversation) {

        User user1 = privateConversation.getUser1();
        User user2 = privateConversation.getUser2();

        User sender;
        User receiver;

        if (chatMessageDTO.getSenderUsername().equals(user1.getUsername())) {
            sender = user1;
            receiver = user2;
        } else {
            sender = user2;
            receiver = user1;
        }

        ChatMessage chatMessage = convertDtoToMessage(chatMessageDTO);
        chatMessage.setSender(sender);

        privateConversation.addMessage(chatMessage);

        // Save to database
        chatMessageRepository.addOne(chatMessage);
        chatMessageDTO.setId(chatMessage.getId());

        if (chatMessageDTO.getAttachment() != null) {
            // Important: Don't send it right away
            chatMessageDTO.getAttachment().setFile(null);
        }

        sendToUser(receiver.getUsername(), chatMessageDTO);
        sendToUser(sender.getUsername(), chatMessageDTO);
    }

    private ChatMessage convertDtoToMessage(ChatMessageDTO chatMessageDTO) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(chatMessageDTO.getContent());

        if (chatMessageDTO.getAttachment() != null) {
            chatMessage.getAttachment().setId(null);
        }

        return chatMessage;
    }

    private void sendMessageToGroup(ChatMessageDTO chatMessageDTO, GroupConversation groupConversation) {

        User sender = userRepository.getById(chatMessageDTO.getSenderUsername());

        ChatMessage chatMessage = convertDtoToMessage(chatMessageDTO);
        chatMessage.setSender(sender);

        groupConversation.addMessage(chatMessage);
        chatMessageRepository.addOne(chatMessage);
        chatMessageDTO.setId(chatMessage.getId());

        if (chatMessageDTO.getAttachment() != null) {
            // Important: Don't send it right away
            chatMessageDTO.getAttachment().setFile(null);
        }

        for (User user : groupConversation.getMembers())
            sendToUser(user.getUsername(), chatMessageDTO);
    }

    public void processMessage(ChatMessageDTO chatMessageDTO, UserSessionThread userSessionThread) throws JsonProcessingException {
        if (chatMessageDTO.getConversationId() == null) {
            sendErrorToUser("Please select a conversation", userSessionThread);
            return;
        }
        Conversation conversation = conversationRepository.getById(chatMessageDTO.getConversationId());

        if (conversation == null) {
            sendErrorToUser("Conversation is non-existent!", userSessionThread);
            return;
        }
        // Decide how to notify observers
        if (conversation instanceof GroupConversation) {
            sendMessageToGroup(chatMessageDTO, (GroupConversation) conversation);
        } else if (conversation instanceof PrivateConversation) {
            sendMessageToUser(chatMessageDTO, (PrivateConversation) conversation);
        }
    }

    @SneakyThrows
    private void sendErrorToUser(String errorMessage, UserSessionThread userSessionThread) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(errorMessage);
        userSessionThread.send(objectMapper.writeValueAsString(errorDTO));
    }

    public void registerUser(RegistrationDTO data, UserSessionThread userSessionThread) {
        String username = data.getUsername();

        if (username.equals("admin")) {
            sendErrorToUser("Cannot login as admin!", userSessionThread);
            return;
        }

        if (data.getFullName().startsWith("#")) {
            sendErrorToUser("Username cannot starts with '#'", userSessionThread);
            return;
        }

        if (userRepository.getById(username) != null) {
            sendErrorToUser("Username already exists!", userSessionThread);
            return;
        }

        User user = objectMapper.convertValue(data, User.class);

        userRepository.addOne(user);

        GroupConversation groupConversation = initData.getGroupConversation();
        groupConversation.addMember(user);
        groupConversationRepository.updateById(groupConversation.getId(), groupConversation);

        userSessionThread.setUser(user);

        userThreadMap.put(username, userSessionThread);
        callback.onNewUserRegistered(user);

        // Log the user in after registration success
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(data.getUsername());
        loginDTO.setPassword(data.getPassword());

        loginUser(loginDTO, userSessionThread);
    }

    public void loginUser(LoginDTO data, UserSessionThread userSessionThread) {
        String username = data.getUsername();
        String password = data.getPassword();

        User user = userRepository.getById(username);

        if (user != null && password.equals(user.getPassword())) {

            userSessionThread.setUser(user);
            userThreadMap.put(username, userSessionThread);

            // Send User info to thread
            LoggedInUserDTO loggedInUserDTO = objectMapper.convertValue(user, LoggedInUserDTO.class);
            sendToUser(username, loggedInUserDTO);

            callback.onUserLogin(user);

            InitialDataDTO initialDataDTO = new InitialDataDTO();

            // Send info about all Online Users
            List<User> onlineUsers = userThreadMap
                    .values()
                    .stream()
                    .map(UserSessionThread::getUser)
                    .filter(u -> !u.getUsername().equals(username))
                    .collect(Collectors.toList());
            initialDataDTO.setOnlineUsers(onlineUsers);

            List<GroupConversation> groupConversations = groupConversationRepository.getAllByMemberUsername(username);
            for (GroupConversation groupConversation: groupConversations)
                // Fetch all chat messages
                groupConversation.setChatMessages(groupConversation.getChatMessages());
            initialDataDTO.setGroupConversations(groupConversations);

            List<PrivateConversation> privateConversations = privateConversationRepository.getAllByUsername(username);
            for (PrivateConversation privateConversation: privateConversations)
                // Fetch all chat messages
                privateConversation.setChatMessages(privateConversation.getChatMessages());
            initialDataDTO.setPrivateConversations(privateConversations);

            sendToUser(username, initialDataDTO);

            NewUserJoinedDTO newUserJoinedDTO = new NewUserJoinedDTO();
            newUserJoinedDTO.setUsername(username);
            newUserJoinedDTO.setFullName(loggedInUserDTO.getFullName());
            sendToAll(newUserJoinedDTO, username);
        } else {
            sendErrorToUser("Invalid Login", userSessionThread);
        }
    }

    public void logoutUser(UserSessionThread userSessionThread) {
        String username = userSessionThread.getUser().getUsername();
        callback.onUserLogout(userSessionThread.getUser());
        userThreadMap.remove(username);
    }

    public void createPrivateConversation(String username1, String username2) {

        User user1 = userRepository.getById(username1);
        User user2 = userRepository.getById(username2);

        PrivateConversation privateConversation = new PrivateConversation();
        privateConversation.setUser1(user1);
        privateConversation.setUser2(user2);
        privateConversation.setChatMessages(new ArrayList<>());

        privateConversationRepository.addOne(privateConversation);

        PrivateConversationDTO privateConversationDTO = new PrivateConversationDTO();
        privateConversationDTO.setId(privateConversation.getId());
        privateConversationDTO.setUsername1(username1);
        privateConversationDTO.setUsername2(username2);
        privateConversationDTO.setFullName1(user1.getFullName());
        privateConversationDTO.setFullName2(user2.getFullName());

        sendToUser(username1, privateConversationDTO);
        sendToUser(username2, privateConversationDTO);
    }

    public interface Interface {

        void onPortOpened(int port);

        void onNewConnectionCreated();

        void onNewUserRegistered(User user);

        void onUserLogin(User user);

        void onUserLogout(User user);
    }
}