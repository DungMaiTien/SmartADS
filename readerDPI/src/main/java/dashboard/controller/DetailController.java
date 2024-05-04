package dashboard.controller;

import dashboard.models.DatabaseManager;
import dashboard.models.DetailItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DetailController {

    @FXML
    private TableView<DetailItem> detailTable;

    @FXML
    private TableColumn<DetailItem, String> hostColumn;

    @FXML
    private TableColumn<DetailItem, Number> targetDomainColumn;

    @FXML
    private TableColumn<DetailItem, String> Web_categoryColumn;

    @FXML
    private TableColumn<DetailItem, Number> adIdColumn;

    @FXML
    private TableColumn<DetailItem, String> adContentColumn;

    @FXML
    private TableColumn<DetailItem, Number> targetAgeMinColumn;

    @FXML
    private TableColumn<DetailItem, Number> targetAgeMaxColumn;

    @FXML
    private TableColumn<DetailItem, String> targetGenderColumn;

    @FXML
    private TableColumn<DetailItem, Number> targetRadiusKmColumn;

    @FXML
    private Node backButton;

    private Stage stage;

    public void initialize(int storeId) {
        try {
            // Kết nối đến cơ sở dữ liệu
            Connection conn = DatabaseManager.getConnection();

            // Truy vấn dữ liệu từ bảng advertising
            String query = "SELECT ad_id, ad_content, target_age_min, target_age_max, target_gender, target_radius_km, domains.host, store_domains.target_domain, domains.web_category " +
                    "FROM advertising " +
                    "JOIN store_domains ON advertising.store_id = store_domains.store_id " +
                    "JOIN domains ON store_domains.target_domain = domains.domain_id " +
                    "WHERE advertising.store_id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, storeId);
            ResultSet resultSet = statement.executeQuery();

            // Tạo một ObservableList để lưu trữ dữ liệu từ ResultSet
            ObservableList<DetailItem> detailList = FXCollections.observableArrayList();

            // Chuyển dữ liệu từ ResultSet vào danh sách
            while (resultSet.next()) {
                DetailItem detailItem = new DetailItem(
                        resultSet.getString("host"),
                        resultSet.getInt("target_domain"),
                        resultSet.getString("web_category"),
                        resultSet.getInt("ad_id"),
                        resultSet.getString("ad_content"),
                        resultSet.getInt("target_age_min"),
                        resultSet.getInt("target_age_max"),
                        resultSet.getString("target_gender"),
                        resultSet.getInt("target_radius_km")
                );
                detailList.add(detailItem);
            }

            // Đặt dữ liệu vào bảng TableView
            detailTable.setItems(detailList);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Đặt các cột vào TableView
        targetDomainColumn.setCellValueFactory(cellData -> cellData.getValue().targetDomainProperty());
        hostColumn.setCellValueFactory(cellData -> cellData.getValue().hostProperty());
        Web_categoryColumn.setCellValueFactory(cellData -> cellData.getValue().Web_categoryProperty());
        adIdColumn.setCellValueFactory(cellData -> cellData.getValue().adIdProperty());
        adContentColumn.setCellValueFactory(cellData -> cellData.getValue().adContentProperty());
        targetAgeMinColumn.setCellValueFactory(cellData -> cellData.getValue().targetAgeMinProperty());
        targetAgeMaxColumn.setCellValueFactory(cellData -> cellData.getValue().targetAgeMaxProperty());
        targetGenderColumn.setCellValueFactory(cellData -> cellData.getValue().targetGenderProperty());
        targetRadiusKmColumn.setCellValueFactory(cellData -> cellData.getValue().targetRadiusKmProperty());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void handleBack(ActionEvent actionEvent) {
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        currentStage.close();
    }

    public void handleDelete(ActionEvent actionEvent) {
    }

    public void handleEdit(ActionEvent actionEvent) {
    }

    public void handleAdd(ActionEvent actionEvent) {
    }
}
