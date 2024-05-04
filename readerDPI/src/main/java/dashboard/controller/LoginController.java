package dashboard.controller;

import dashboard.models.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;

    @FXML
    public void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (UserManager.authenticateUser(username, password)) {
            // Đăng nhập thành công
            statusLabel.setText("Đăng nhập thành công!");

            // Đóng cửa sổ đăng nhập
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();

            // Mở trang quảng cáo
            openAdvertisingPage();
        } else {
            // Đăng nhập không thành công
            statusLabel.setText("Đăng nhập thất bại,kiểm tra lại thông tin đăng nhập!");
        }
    }

    private void openAdvertisingPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/views/Advertising.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Manage Stores");
            stage.setScene(new Scene(root, 1000, 800));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
