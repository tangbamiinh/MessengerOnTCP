package hanu.npr.messengerclient.fxComponents;

import com.jfoenix.controls.JFXButton;
import hanu.npr.messengerclient.models.Attachment;
import hanu.npr.messengerclient.models.ChatMessage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.Objects;

public class MessageCell extends ListCell<ChatMessage> {

    @FXML
    private HBox messageCellRoot;

    @FXML
    private Label messageCellContent;

    @FXML
    private Label conversationNameLabel;

    @FXML
    private FontIcon downloadIcon;

    @FXML
    private JFXButton fileDownloadButton;

    @FXML
    private VBox messageVBox;

    private final Interface callback;

    public MessageCell(Interface callback) {
        this.callback = callback;
    }

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

            if (chatMessage.getAttachment() != null) {
                Attachment attachment = chatMessage.getAttachment();
                fileDownloadButton.setOnAction(event -> callback.downloadAttachment(attachment));
                fileDownloadButton.setMaxHeight(Double.MAX_VALUE);
                fileDownloadButton.setText(attachment.getFileName());
                downloadIcon.setIconSize(16);
            } else {
                if (fileDownloadButton.getParent() != null) {
                    messageVBox.getChildren().remove(fileDownloadButton);
                }
            }
            setText(null);
            setGraphic(messageCellRoot);
        }
    }

    public interface Interface {
        void downloadAttachment(Attachment attachment);
    }
}
