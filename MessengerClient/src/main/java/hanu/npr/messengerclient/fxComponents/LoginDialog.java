package hanu.npr.messengerclient.fxComponents;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginDialog extends JFXDialog implements Initializable {

    @FXML
    public JFXTextField usernameField;

    @FXML
    public JFXPasswordField passwordField;

    @FXML
    public JFXButton loginButton;

    @FXML
    public JFXButton registerButton;

    private final Interface callback;

    public LoginDialog(Interface callback) {
        this.callback = callback;

        JFXDialogLayout root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("LoginDialog.fxml")));
            fxmlLoader.setController(this);
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContent(root);
        setTransitionType(JFXDialog.DialogTransition.CENTER);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registerButton.setOnAction(actionEvent -> {
            callback.onRegisterButtonClicked();
        });

        loginButton.setOnAction(actionEvent -> {
            callback.onLoginSubmitted(usernameField.getText(), passwordField.getText());
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                callback.onLoginSubmitted(usernameField.getText(), passwordField.getText());
            }
        });
    }

    public interface Interface {
        void onRegisterButtonClicked();
        void onLoginSubmitted(String username, String password);
    }
}
