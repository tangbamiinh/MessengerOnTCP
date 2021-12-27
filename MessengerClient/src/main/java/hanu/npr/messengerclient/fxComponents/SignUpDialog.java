package hanu.npr.messengerclient.fxComponents;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SignUpDialog extends JFXDialog implements Initializable {

    @FXML
    private JFXDialogLayout root;

    @FXML
    public JFXTextField fullNameField;

    @FXML
    public JFXTextField usernameField;

    @FXML
    public JFXPasswordField passwordField;

    @FXML
    public JFXPasswordField confirmPasswordField;

    @FXML
    public JFXButton registerButton;

    private final Interface callback;

    public SignUpDialog(Interface callback) {

        this.callback = callback;
        root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("SignUpDialog.fxml")));
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
        confirmPasswordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                submit();
            }
        });
        registerButton.setOnAction(actionEvent -> {
            submit();
        });
    }

    private void submit() {
        if (passwordField.getText().equals(confirmPasswordField.getText()))
            callback.onSignUpSubmitted(fullNameField.getText(), usernameField.getText(), passwordField.getText());
        else {
            ErrorDialog errorDialog = new ErrorDialog();
            errorDialog.setErrorMessage("Passwords do not match!");
        }
    }

    public interface Interface {
        void onSignUpSubmitted(String fullName, String username, String password);
    }
}
