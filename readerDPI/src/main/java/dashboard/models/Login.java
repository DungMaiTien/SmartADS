package dashboard.models;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Login extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load UI from FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/views/login.fxml"));
        Parent root = loader.load();

        // Set the stage title and scene
        primaryStage.setTitle("Smart Ads Dashboard - Đăng nhập");
        primaryStage.setScene(new Scene(root, 350, 300));

        // Show the stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
