package hanu.npr.messengerclient.fxComponents;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import hanu.npr.messengerclient.models.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class CreateConversationDialog extends JFXDialog implements Initializable {

    @FXML
    private JFXListView<User> usersListView;

    private final Interface callback;

    private final List<User> users;

    public CreateConversationDialog(List<User> users, Interface callback) {
        this.callback = callback;
        this.users = users;

        JFXDialogLayout root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("CreateConversationDialog.fxml")));
            fxmlLoader.setController(this);
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContent(root);
        setTransitionType(JFXDialog.DialogTransition.CENTER);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usersListView.setOnMouseClicked(event -> {
            User selectedUser = usersListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                callback.onConnectToUser(selectedUser);
            }
        });
        usersListView.setItems(FXCollections.observableArrayList(users));
    }

    public interface Interface {
        void onConnectToUser(User user);
    }
}

