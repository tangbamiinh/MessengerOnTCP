package hanu.npr.messengerclient.fxComponents;

import hanu.npr.messengerclient.models.ChatMessage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Objects;

public class MessageCell extends ListCell<ChatMessage> {

    @FXML
    private HBox messageCellRoot;

    @FXML
    private Label messageCellContent;

    @FXML
    private Label conversationNameLabel;


    @Override
    protected void updateItem(ChatMessage chatMessage, boolean empty) {
        super.updateItem(chatMessage, empty);

        if (empty || chatMessage == null) {

            setText(null);
            setGraphic(null);

        } else {

            String xmlFileName = chatMessage.isMyMessage() ? "MyMessageCell.fxml" : "OtherMessageCell.fxml";

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(xmlFileName)));
                fxmlLoader.setController(this);
                fxmlLoader.setRoot(this);
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String displayName = chatMessage.isMyMessage() ? "You" : chatMessage.getSender().getFullName();
            conversationNameLabel.setText(displayName);
            messageCellContent.setText(chatMessage.getContent());

            setText(null);
            setGraphic(messageCellRoot);
        }
    }

    public interface Interface {
    }
}
