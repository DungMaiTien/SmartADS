package dashboard.controller;

import com.sun.javafx.stage.StageHelper;
import dashboard.models.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddStoreController {

    @FXML
    private TextField storeIdField;

    @FXML
    private TextField storeNameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField latitudeField;

    @FXML
    private TextField longitudeField;

    @FXML
    private TextField domainIdField;

    private Integer domainId; // Biến để lưu trữ domainId

    public void setDomainId(Integer domainId) {
        this.domainId = domainId;
        if (domainId != null) {
            domainIdField.setText(String.valueOf(domainId));
        } else {
            domainIdField.setText("null");
        }
    }

    @FXML
    private void saveButtonClicked(ActionEvent actionEvent) {
        // Lấy dữ liệu từ các trường nhập liệu
        String storeId = storeIdField.getText();
        String storeName = storeNameField.getText();
        String address = addressField.getText();
        String phoneNumber = phoneNumberField.getText();
        String latitude = latitudeField.getText();
        String longitude = longitudeField.getText();
        if (!isNumeric(storeId)) {
            showAlert(AlertType.ERROR, "Error", "The format of store_id must be numeric and not empty.");
            return;
        }

        // Kiểm tra xem store_id đã tồn tại trong cơ sở dữ liệu chưa
        if (isStoreIdExists(storeId)) {
            showAlert(AlertType.ERROR, "Error", "Store_id already exists.");
            return;
        }
        // Kết nối đến cơ sở dữ liệu
        try (Connection conn = DatabaseManager.getConnection()) {
            // Tạo truy vấn SQL để thêm dữ liệu vào bảng store
            String sql = "INSERT INTO store (store_id, store_name, address, phone_number, latitude, longitude, domain_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, storeId);
                stmt.setString(2, storeName);
                stmt.setString(3, address);
                stmt.setString(4, phoneNumber);
                stmt.setString(5, latitude);
                stmt.setString(6, longitude);
                stmt.setObject(7, domainId); // Sử dụng setObject để thêm giá trị null vào cột domain_id nếu domainId là null
                stmt.executeUpdate();

                // Hiển thị thông báo thành công
                showAlert(AlertType.INFORMATION, "Success", "Store added successfully.");

                // Đóng tất cả các cửa sổ hiện tại và mở lại cửa sổ Advertising
                closeAndOpenAdvertising();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý ngoại lệ hoặc hiển thị thông báo lỗi cho người dùng nếu cần
        }
    }
    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
    private boolean isStoreIdExists(String storeId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT COUNT(*) FROM store WHERE store_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, storeId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đóng tất cả các cửa sổ hiện tại và mở lại cửa sổ Advertising
    private void closeAndOpenAdvertising() {
        // Đóng tất cả các cửa sổ
        for (Stage stage : StageHelper.getStages()) {
            stage.close();
        }

        // Mở lại cửa sổ Advertising
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/views/Advertising.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage(); // Tạo một stage mới cho trang Advertising
            stage.setTitle("Manage Stores");
            stage.setScene(new Scene(root, 1000, 800));
            stage.show();
            Stage currentStage = (Stage) storeIdField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Hiển thị thông báo
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}


