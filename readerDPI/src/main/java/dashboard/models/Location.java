package dashboard.models;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Location {
    private static Map<Integer, double[]> storeCoordinates = new HashMap<>();
    private static Set<Integer> storeIds; // Thêm một tập hợp để lưu trữ các store_id đã tính toán
    private static boolean initialized = false;

    public static void init() {
        if (!initialized) {
            try (Connection conn = DatabaseManager.getConnection()) {
                String query = "SELECT store.store_id, store.latitude, store.longitude, advertising.target_radius_km " +
                        "FROM advertising " +
                        "JOIN store ON advertising.store_id = store.store_id";

                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        int storeId = rs.getInt("store_id");
                        double latitude = rs.getDouble("latitude");
                        double longitude = rs.getDouble("longitude");
                        double targetRadius = rs.getDouble("target_radius_km");

                        double latMin = latitude - (targetRadius / 111.0);
                        double latMax = latitude + (targetRadius / 111.0);
                        double lonMin = longitude - (targetRadius / (111.0 * Math.cos(Math.toRadians(latitude))));
                        double lonMax = longitude + (targetRadius / (111.0 * Math.cos(Math.toRadians(latitude))));

                        double[] coordinates = {latMin, latMax, lonMin, lonMax};
                        storeCoordinates.put(storeId, coordinates);
                    }

                    // Sau khi lấy được tất cả thông tin, khởi tạo tập hợp storeIds
                    storeIds = storeCoordinates.keySet();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            initialized = true;
        }
    }

    // Thêm phương thức để trả về tập hợp store_id
    public static Set<Integer> getStoreIds() {
        return storeIds;
    }

    public static double getLatMin(int storeId) {
        return storeCoordinates.get(storeId)[0];
    }

    public static double getLatMax(int storeId) {
        return storeCoordinates.get(storeId)[1];
    }

    public static double getLonMin(int storeId) {
        return storeCoordinates.get(storeId)[2];
    }

    public static double getLonMax(int storeId) {
        return storeCoordinates.get(storeId)[3];
    }
}
