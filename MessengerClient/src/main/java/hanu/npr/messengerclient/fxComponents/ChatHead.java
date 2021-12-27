package hanu.npr.messengerclient.fxComponents;

import hanu.npr.messengerclient.models.ChatMessage;
import hanu.npr.messengerclient.models.Conversation;
import hanu.npr.messengerclient.models.GroupConversation;
import hanu.npr.messengerclient.models.PrivateConversation;
import hanu.npr.messengerclient.models.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import java.io.IOException;
import java.util.Objects;

public class ChatHead extends ListCell<Conversation> {

    @FXML
    private Label conversationNameLabel;

    @FXML
    private Label latestMessageLabel;

    private FXMLLoader fxmlLoader;

    private final String currentLoggedInUsername;

    public ChatHead(String currentLoggedInUsername) {
        this.currentLoggedInUsername = currentLoggedInUsername;
        try {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("ChatHead.fxml")));
                fxmlLoader.setController(this);
                fxmlLoader.setRoot(this);
                fxmlLoader.load();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Conversation conversation, boolean empty) {
        super.updateItem(conversation, empty);

        if (empty || conversation == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {

            if (conversation instanceof GroupConversation) {
                conversationNameLabel.setText(((GroupConversation) conversation).getName());

            } else if (conversation instanceof PrivateConversation) {
                User otherUser = ((PrivateConversation) conversation).getUser1();

                if (otherUser.getUsername().equals(currentLoggedInUsername))
                    otherUser = ((PrivateConversation) conversation).getUser2();

                conversationNameLabel.setText(otherUser.getFullName());
            }
            ChatMessage latestMessage = conversation.getLatestMessage();
            if (latestMessage != null) {
                String content = latestMessage.getSender().getFullName() + ": " + latestMessage.getContent();
                latestMessageLabel.setText(content);
            } else {
                latestMessageLabel.setText("New conversation");
            }
            setUserData(conversation);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    public void setLatestMessage(String latestMessage) {
        Platform.runLater(() -> latestMessageLabel.setText(latestMessage));
    }

    public void setConversationName(String conversationName) {
        Platform.runLater(() -> conversationNameLabel.setText(conversationName));
    }
}
