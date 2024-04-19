package dashboard.models;

import java.sql.*;

public class Location {
    private static double latMin;
    private static double latMax;
    private static double lonMin;
    private static double lonMax;

    public static void main(String[] args) {
        // Thực hiện truy vấn và tính toán
        try (Connection conn = DatabaseManager.getConnection()) {
            // Truy vấn cơ sở dữ liệu
            String query = "SELECT store.store_id, store.latitude, store.longitude, advertising.target_radius_km " +
                    "FROM advertising " +
                    "JOIN store ON advertising.store_id = store.store_id";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    double latitude = rs.getDouble("latitude");
                    double longitude = rs.getDouble("longitude");
                    double targetRadius = rs.getDouble("target_radius_km");

                    // Thực hiện tính toán
                    double latMin = latitude - (targetRadius / 111.0);
                    double latMax = latitude + (targetRadius / 111.0);
                    double lonMin = longitude - (targetRadius / (111.0 * Math.cos(Math.toRadians(latitude))));
                    double lonMax = longitude + (targetRadius / (111.0 * Math.cos(Math.toRadians(latitude))));

                    // In ra kết quả
                    System.out.println("Khu vực xung quanh cửa hàng với store_id: " + rs.getInt("store_id"));
                    System.out.println("Latitude từ " + latMin + " đến " + latMax);
                    System.out.println("Longitude từ " + lonMin + " đến " + lonMax);
                    System.out.println();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static double getLatMin() {
        return latMin;
    }

    public static double getLatMax() {
        return latMax;
    }

    public static double getLonMin() {
        return lonMin;
    }

    public static double getLonMax() {
        return lonMax;
    }
}
