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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.KafkaDataConsumer;
import org.example.OfflineDataConsumer;
import org.example.RealtimeDataProducer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    @FXML
    private TableColumn<Store, String> describeColumn;

    private Thread producerThread;
    private Thread consumerThread;
    private Thread offlineConsumerThread;
    private boolean isRunning = false;

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

        // Tạo cột Describe
        describeColumn = new TableColumn<>("Describe");
        describeColumn.setCellValueFactory(new PropertyValueFactory<>("describe"));
        advertisingTable.getColumns().add(describeColumn);
    }

    public void handleAdd(ActionEvent actionEvent) {
        try {
            // Load Add_domain.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/views/Add_domain.fxml"));
            Parent root = loader.load();

            // Tạo một scene mới với nội dung của Add_domain.fxml
            Scene scene = new Scene(root);

            // Tạo một Stage mới
            Stage stage = new Stage();
            stage.setTitle("Add NEW");
            stage.setScene(scene);

            // Hiển thị cửa sổ mới
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        // Lấy dòng được chọn từ bảng
        Store selectedStore = advertisingTable.getSelectionModel().getSelectedItem();

        if (selectedStore != null) {
            int storeId = selectedStore.getId();
            int domainId = selectedStore.getDomainId();

            try {
                // Kết nối đến cơ sở dữ liệu
                Connection conn = DatabaseManager.getConnection();

                // Xóa cửa hàng từ bảng store
                String deleteStoreQuery = "DELETE FROM store WHERE store_id = " + storeId;
                Statement deleteStoreStmt = conn.createStatement();
                deleteStoreStmt.executeUpdate(deleteStoreQuery);

                // Xóa dữ liệu tương ứng từ bảng domains
                String deleteDomainQuery = "DELETE FROM domains WHERE domain_id = " + domainId;
                Statement deleteDomainStmt = conn.createStatement();
                deleteDomainStmt.executeUpdate(deleteDomainQuery);

                // Hiển thị thông báo xóa thành công
                showAlert(Alert.AlertType.INFORMATION, "Success", "Store deleted successfully.");

                // Cập nhật lại TableView sau khi xóa
                advertisingTable.getItems().remove(selectedStore);
            } catch (SQLException e) {
                e.printStackTrace();
                // Xử lý ngoại lệ hoặc hiển thị thông báo lỗi cho người dùng nếu cần
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the store.");
            }
        } else {
            // Hiển thị thông báo nếu không có cửa hàng nào được chọn
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a store to delete.");
        }
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void handleEdit(ActionEvent actionEvent) {
    }

    public void handleDetail(ActionEvent actionEvent) {
        // Lấy dòng được chọn từ bảng
        Store selectedStore = advertisingTable.getSelectionModel().getSelectedItem();

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
                stage.setScene(new Scene(root, 1000, 800));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleperformance(ActionEvent actionEvent) {
    }

    public void handleStart_All(ActionEvent actionEvent) {
        // Xử lý sự kiện khi nút Start được nhấn
        if (!isRunning) {
            // Khởi tạo và khởi chạy producerThread

            producerThread = new Thread(() -> RealtimeDataProducer.main(new String[]{}));
            producerThread.start();

            // Khởi tạo và khởi chạy consumerThread
            consumerThread = new Thread(() -> KafkaDataConsumer.main(new String[]{}));
            consumerThread.start();

            // Khởi tạo và khởi chạy offlineConsumerThread
            offlineConsumerThread = new Thread(() -> OfflineDataConsumer.main(new String[]{}));
            offlineConsumerThread.start();


            for (Store store : advertisingTable.getItems()) {
                store.setDescribe("on");
            }
            // Đổi văn bản nút thành "Stop"
            ((Button) actionEvent.getSource()).setText("Stop All");
        } else {
            // Dừng producerThread nếu đang chạy và chờ cho đến khi nó kết thúc
            if (producerThread != null && producerThread.isAlive()) {
                producerThread.interrupt();
                try {
                    producerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Dừng consumerThread nếu đang chạy và chờ cho đến khi nó kết thúc
            if (consumerThread != null && consumerThread.isAlive()) {
                consumerThread.interrupt();
                try {
                    consumerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Dừng offlineConsumerThread nếu đang chạy và chờ cho đến khi nó kết thúc
            if (offlineConsumerThread != null && offlineConsumerThread.isAlive()) {
                offlineConsumerThread.interrupt();
                try {
                    offlineConsumerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            for (Store store : advertisingTable.getItems()) {
                store.setDescribe("off");
            }
            // Đổi văn bản nút thành "Start"
            ((Button) actionEvent.getSource()).setText("Start All");
        }

        // Đảo ngược trạng thái của biến isRunning
        isRunning = !isRunning;

        // Cập nhật lại TableView để hiển thị thay đổi trạng thái
        advertisingTable.refresh();
    }


    public void handleStart_Only(ActionEvent actionEvent) {
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
        private String describe = "off"; // Mặc định trạng thái off

        public Store(int id, String storeName, String address, String phoneNumber, double latitude, double longitude, int domainId) {
            this.id = id;
            this.storeName = storeName;
            this.address = address;
            this.phoneNumber = phoneNumber;
            this.latitude = latitude;
            this.longitude = longitude;
            this.domainId = domainId;
        }

        // Getter và Setter methods
        public int getId() {
            return id;
        }

        public String getStoreName() {
            return storeName;
        }

        public String getAddress() {
            return address;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public int getDomainId() {
            return domainId;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }
    }

}
