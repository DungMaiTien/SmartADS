package dashboard.controller;

import dashboard.models.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddDomainController {
    @FXML
    private TableView<AdvertisingController.Store> advertisingTable;
    @FXML
    private CheckBox noDomainCheckbox;

    @FXML
    private TextField hostField;

    @FXML
    private TextField webCategoryField;

    @FXML
    private Button nextButton;

    @FXML
    private void initialize() {
        // Disable hostField và webCategoryField nếu noDomainCheckbox được chọn
        noDomainCheckbox.setOnAction(event -> {
            boolean isNoDomainSelected = noDomainCheckbox.isSelected();
            hostField.setDisable(isNoDomainSelected);
            webCategoryField.setDisable(isNoDomainSelected);
        });
    }

    @FXML
    private void nextButtonClicked() {
        try {
            if (noDomainCheckbox.isSelected()) {
                // Di chuyển sang Addstore.fxml và thiết lập domain_Id là null
                moveToAddStore(null);
            } else {
                // Kiểm tra xem các trường host và web_category có được nhập không
                String host = hostField.getText().trim();
                String webCategory = webCategoryField.getText().trim();

                if (host.isEmpty() || webCategory.isEmpty()) {
                    // Hiển thị thông báo nếu các trường không được nhập
                    showAlert("Warning", "Không được để trống", "Vui lòng nhập host và web category.");
                } else {
                    // Thêm một bản ghi mới vào bảng domains với host và web_category từ người dùng
                    int domainId = addDomainToDatabase(host, webCategory);

                    // Di chuyển sang Addstore.fxml và thiết lập domain_Id là domainId mới tạo
                    moveToAddStore(domainId);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // Hiển thị thông báo
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private int addDomainToDatabase(String host, String webCategory) throws SQLException {
        int domainId = -1;
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO domains (host, web_category) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, host);
                stmt.setString(2, webCategory);
                stmt.executeUpdate();

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    domainId = generatedKeys.getInt(1);
                }
            }
        }
        return domainId;
    }

    private void moveToAddStore(Integer domainId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/views/Addstore.fxml"));
        Parent root = loader.load();
        AddStoreController addStoreController = loader.getController();
        addStoreController.setDomainId(domainId);
//        Scene scene = new Scene(root);
        Stage stage = (Stage) nextButton.getScene().getWindow();
        stage.setScene(new Scene(root, 350, 400));
    }
}
