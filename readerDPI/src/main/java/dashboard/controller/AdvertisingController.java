package dashboard.controller;

import dashboard.models.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AdvertisingController {

    @FXML
    private TableView<Store> advertisingTable;

    @FXML
    private TableColumn<Store, Integer> idColumn;

    @FXML
    private TableColumn<Store, String> storeNameColumn;

    @FXML
    private TableColumn<Store, String> addressColumn;

    @FXML
    private TableColumn<Store, String> phoneNumberColumn;

    @FXML
    private TableColumn<Store, Double> latitudeColumn;

    @FXML
    private TableColumn<Store, Double> longitudeColumn;

    @FXML
    private TableColumn<Store, Integer> domainIdColumn;

    public void initialize() {
        try {
            // Kết nối đến cơ sở dữ liệu
            Connection conn = DatabaseManager.getConnection();

            // Truy vấn dữ liệu từ bảng store
            String query = "SELECT * FROM store";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Tạo một ObservableList để lưu trữ dữ liệu từ ResultSet
            ObservableList<Store> storeList = FXCollections.observableArrayList();

            // Chuyển dữ liệu từ ResultSet vào danh sách
            while (resultSet.next()) {
                Store store = new Store(
                        resultSet.getInt("store_id"),
                        resultSet.getString("store_name"),
                        resultSet.getString("address"),
                        resultSet.getString("phone_number"),
                        resultSet.getDouble("latitude"),
                        resultSet.getDouble("longitude"),
                        resultSet.getInt("domain_id")
                );
                storeList.add(store);
            }

            // Đặt dữ liệu vào bảng TableView
            advertisingTable.setItems(storeList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Đặt các cột vào TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        storeNameColumn.setCellValueFactory(new PropertyValueFactory<>("storeName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        latitudeColumn.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longitudeColumn.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        domainIdColumn.setCellValueFactory(new PropertyValueFactory<>("domainId"));
    }

    public void handleAdd(ActionEvent actionEvent) {
    }

    public void handleLogout(ActionEvent actionEvent) {
        try {
            // Load UI from FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/views/login.fxml"));
            Parent root = loader.load();

            // Create a new stage for the login screen
            Stage loginStage = new Stage();
            loginStage.setTitle("Smart Ads Dashboard - Đăng nhập");
            loginStage.setScene(new Scene(root, 300, 300));

            // Close the current stage (advertising screen)
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.close();

            // Show the login stage
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handleDelete(ActionEvent actionEvent) {
    }

    public void handleEdit(ActionEvent actionEvent) {
    }

    public void handleDetail(ActionEvent actionEvent) {
        // Lấy dòng được chọn từ bảng
        AdvertisingController.Store selectedStore = advertisingTable.getSelectionModel().getSelectedItem();

        if (selectedStore != null) {
            int storeId = selectedStore.getId();
            // Chuyển sang màn hình Detail.fxml và truyền storeId
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/views/Detail.fxml"));
                Parent root = loader.load();

                DetailController detailController = loader.getController();
                detailController.initialize(storeId);

                Stage stage = new Stage();
                stage.setTitle("Detail Advertising store");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleperformance(ActionEvent actionEvent) {
    }

    // Định nghĩa lớp Store để lưu trữ dữ liệu từ ResultSet
    public static class Store {
        private final int id;
        private final String storeName;
        private final String address;
        private final String phoneNumber;
        private final double latitude;
        private final double longitude;
        private final int domainId;

        public Store(int id, String storeName, String address, String phoneNumber, double latitude, double longitude, int domainId) {
            this.id = id;
            this.storeName = storeName;
            this.address = address;
            this.phoneNumber = phoneNumber;
            this.latitude = latitude;
            this.longitude = longitude;
            this.domainId = domainId;
        }

        // Getter methods
        public int getId() { return id; }
        public String getStoreName() { return storeName; }
        public String getAddress() { return address; }
        public String getPhoneNumber() { return phoneNumber; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public int getDomainId() { return domainId; }
    }
}




