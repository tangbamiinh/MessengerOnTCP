package hanu.npr.messengerclient.fxComponents;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ErrorDialog extends JFXDialog implements Initializable {

    @FXML
    private JFXButton okButton;

    @FXML
    private Label errorMessageLabel;


    public ErrorDialog() {
        JFXDialogLayout root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("ErrorDialog.fxml")));
            fxmlLoader.setController(this);
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContent(root);
        setTransitionType(JFXDialog.DialogTransition.CENTER);
    }

    public void setErrorMessage(String errorMessage) {
        Platform.runLater(() -> {
            errorMessageLabel.setText(errorMessage);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        okButton.setOnAction(event -> this.close());
    }
}
