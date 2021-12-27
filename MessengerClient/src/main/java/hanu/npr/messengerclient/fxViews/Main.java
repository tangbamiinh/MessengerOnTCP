package hanu.npr.messengerclient.fxViews;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import hanu.npr.messengerclient.MessengerClientApplication;
import hanu.npr.messengerclient.controllers.MainController;
import hanu.npr.messengerclient.fxComponents.ChatHead;
import hanu.npr.messengerclient.fxComponents.CreateConversationDialog;
import hanu.npr.messengerclient.fxComponents.ErrorDialog;
import hanu.npr.messengerclient.fxComponents.LoginDialog;
import hanu.npr.messengerclient.fxComponents.MessageCell;
import hanu.npr.messengerclient.fxComponents.SignUpDialog;
import hanu.npr.messengerclient.fxComponents.SuccessDialog;
import hanu.npr.messengerclient.models.ChatMessage;
import hanu.npr.messengerclient.models.Conversation;
import hanu.npr.messengerclient.models.GroupConversation;
import hanu.npr.messengerclient.models.PrivateConversation;
import hanu.npr.messengerclient.models.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import lombok.SneakyThrows;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.net.ConnectException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Main implements Initializable, LoginDialog.Interface, SignUpDialog.Interface, MessageCell.Interface, MainController.Interface, CreateConversationDialog.Interface {

    @FXML
    private final ObservableList<Conversation> conversations;

    @FXML
    private final ObservableList<ChatMessage> displayedChatMessages;

    private final Map<String, User> userMap;

    @FXML
    public JFXButton addConversationButton;

    @FXML
    public JFXButton selectAttachmentButton;

    private Conversation currentConversation;

    @FXML
    public JFXListView<Conversation> chatHeadsListView;

    @FXML
    public Label conversationLabel;

    @FXML
    private JFXListView<ChatMessage> chatMessagesListView;

    @FXML
    public StackPane root;

    @FXML
    public JFXButton sendMessageButton;

    @FXML
    private TextField messageBox;

    private final LoginDialog loginDialog;

    private final SignUpDialog signUpDialog;

    private final SuccessDialog successDialog;

    private final ErrorDialog errorDialog;

    private CreateConversationDialog createConversationDialog;

    private MainController mainController;

    private final FileChooser fileChooser;

    private File selectedFile;

    @SneakyThrows
    public Main() {
        try {
            mainController = new MainController("localhost", 60000, this);
        } catch (ConnectException e) {
            System.out.println("Error: Server not reachable");
            System.exit(1);
        }
        fileChooser = new FileChooser();

        loginDialog = new LoginDialog(this);
        signUpDialog = new SignUpDialog(this);

        successDialog = new SuccessDialog();
        errorDialog = new ErrorDialog();

        userMap = new HashMap<>();
        displayedChatMessages = FXCollections.observableArrayList();
        conversations = FXCollections.observableArrayList();
    }

    @FXML
    private void sendMessage() {
        if (currentConversation == null) {
            errorDialog.setErrorMessage("Please select a conversation");
            Platform.runLater(errorDialog::show);
            return;
        }

        mainController.sendMessage(currentConversation.getId(), messageBox.getText(), selectedFile);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loginDialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        loginDialog.setDialogContainer(root);
        loginDialog.setOverlayClose(false);

        signUpDialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        signUpDialog.setDialogContainer(root);

        successDialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        successDialog.setDialogContainer(root);

        errorDialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        errorDialog.setDialogContainer(root);

        conversations.addListener((ListChangeListener<? super Conversation>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    Platform.runLater(() -> chatHeadsListView.getItems().addAll(c.getFrom(), c.getAddedSubList()));
                }
                if (c.wasRemoved()) {
                    Platform.runLater(() -> chatHeadsListView.getItems().removeAll(c.getRemoved()));
                }
            }
        });

        chatHeadsListView.setCellFactory(chatHeadListView -> new ChatHead(mainController.getCurrentUser().getUsername()));

        displayedChatMessages.addListener((ListChangeListener<? super ChatMessage>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    Platform.runLater(() -> chatMessagesListView.getItems().addAll(c.getFrom(), c.getAddedSubList()));
                }
                if (c.wasRemoved()) {
                    Platform.runLater(() -> chatMessagesListView.getItems().removeAll(c.getRemoved()));
                }
            }
        });

        chatMessagesListView.setCellFactory(chatMessageListView -> new MessageCell());

        messageBox.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                sendMessage();
                messageBox.clear();
            }
        });

        sendMessageButton.setOnAction(event -> {
            sendMessage();
            messageBox.clear();
        });

        selectAttachmentButton.setOnAction(event -> selectedFile = fileChooser.showOpenDialog(MessengerClientApplication.getStage()));

        addConversationButton.setOnAction(event -> {
            List<User> users = userMap
                    .values()
                    .stream()
                    .filter(u -> !u.getUsername().equals(mainController.getCurrentUser().getUsername()))
                    .collect(Collectors.toList());

            createConversationDialog = new CreateConversationDialog(users, this);
            createConversationDialog.setDialogContainer(root);
            Platform.runLater(createConversationDialog::show);
        });

        // Show login dialog
        loginDialog.show();
    }

    @Override
    public void onRegisterButtonClicked() {
        signUpDialog.show();
    }

    @Override
    public void onLoginSubmitted(String username, String password) {
        mainController.login(username, password);
    }

    @Override
    public void onConnectionSuccess(String host, int port) {
        System.out.println("Connected to " + host + ":" + port);
    }

    @SneakyThrows
    @Override
    public void onChatMessageReceived(Long conversationId, String senderUsername, String content) {
        Conversation conversation = conversations.stream()
                .filter(c -> Objects.equals(c.getId(), conversationId))
                .findFirst()
                .orElseThrow(Exception::new);


        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMyMessage(mainController.getCurrentUser().getUsername().equals(senderUsername));
        chatMessage.setContent(content);

        User sender = userMap.get(senderUsername);
        chatMessage.setSender(sender);

        conversation.addMessage(chatMessage);
        conversation.setLatestMessage(chatMessage);

        chatHeadsListView.refresh();

        if (currentConversation != null && conversationId.equals(currentConversation.getId())) {
            displayedChatMessages.add(chatMessage);
            Platform.runLater(() -> {
                chatMessagesListView.scrollTo(chatMessage);
                chatMessagesListView.getSelectionModel().select(chatMessage);
            });
        }
    }

    @Override
    public void onNewUserJoined(String username, String fullName) {
        Notifications notification = Notifications.create().title("New user joined!").text(fullName + " has joined the chat!");
        Platform.runLater(notification::showInformation);

        if (!userMap.containsKey(username)) {
            User user = new User();
            user.setUsername(username);
            user.setFullName(fullName);
            userMap.put(username, user);
        }
    }

    @Override
    public void onInitialDataReceived(List<User> users, List<GroupConversation> groupConversations, List<PrivateConversation> privateConversations) {
        for (User user : users) {
            userMap.put(user.getUsername(), user);
        }

        for (Conversation conversation : groupConversations) {
            for (ChatMessage chatMessage : conversation.getChatMessages()) {
                chatMessage.setMyMessage(mainController.getCurrentUser().getUsername().equals(chatMessage.getSender().getUsername()));
            }
        }

        for (Conversation conversation : privateConversations) {
            for (ChatMessage chatMessage : conversation.getChatMessages()) {
                chatMessage.setMyMessage(mainController.getCurrentUser().getUsername().equals(chatMessage.getSender().getUsername()));
            }
        }
        conversations.addAll(groupConversations);
        conversations.addAll(privateConversations);
    }

    @Override
    public void onErrorReceived(String errorMessage) {
        errorDialog.setErrorMessage(errorMessage);
        Platform.runLater(errorDialog::show);
    }

    @Override
    public void onSuccessReceived(String successMessage) {
        successDialog.setSuccessMessage(successMessage);
        Platform.runLater(successDialog::show);
    }

    @Override
    public void onLogInSuccess(String fullName, String username) {
        if (signUpDialog.isVisible())
            signUpDialog.close();
        if (loginDialog.isVisible())
            loginDialog.close();

        userMap.put(username, mainController.getCurrentUser());
        Notifications notification = Notifications.create().title("Login Successfully!").text("Logged in as " + fullName);
        Platform.runLater(() -> {
            notification.showInformation();
            MessengerClientApplication.getStage().setTitle(fullName + " - Messenger");
        });
    }

    @Override
    public void onServerShutdown() {
        errorDialog.setErrorMessage("The server has been shut down!");
        Platform.runLater(errorDialog::show);
    }

    @Override
    public void onPrivateConversationReceived(Long conversationId, String username, String fullName) {
        User user = userMap.get(username);

        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setFullName(fullName);
        }

        PrivateConversation conversation = new PrivateConversation();
        conversation.setId(conversationId);
        conversation.setUser1(mainController.getCurrentUser());
        conversation.setUser2(user);

        for (ChatMessage chatMessage : conversation.getChatMessages()) {
            chatMessage.setMyMessage(mainController.getCurrentUser().getUsername().equals(chatMessage.getSender().getUsername()));
        }
        conversations.add(conversation);
        if (createConversationDialog != null && createConversationDialog.isVisible()) {
            Platform.runLater(createConversationDialog::close);
        }
    }

    @Override
    public void onSignUpSubmitted(String fullName, String username, String password) {
        mainController.signup(fullName, username, password);
    }

    @FXML
    public void handleChatHeadClicked(MouseEvent ignored) {

        Conversation selectedConversation = chatHeadsListView.getSelectionModel().getSelectedItem();

        if (selectedConversation != null) {
            currentConversation = selectedConversation;

            Collection<ChatMessage> conversationChatMessages = selectedConversation.getChatMessages();

            displayedChatMessages.clear();
            displayedChatMessages.addAll(conversationChatMessages);

            String conversationLabelText = selectedConversation.getTitle(mainController.getCurrentUser().getUsername());

            Platform.runLater(() -> conversationLabel.setText(conversationLabelText));
        }
    }

    @Override
    public void onConnectToUser(User user) {
        mainController.createPrivateChatWith(user.getUsername());
    }
}
