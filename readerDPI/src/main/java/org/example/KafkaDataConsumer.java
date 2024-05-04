package org.example;

import dashboard.models.DatabaseManager;
import dashboard.models.Location;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaDataConsumer {
    private static final String TOPIC_NAME = "realtime_data";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "kafka-data-consumer-group";
    private static Map<Integer, Integer> messageCountMap = new HashMap<>();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        Location.init();

        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC_NAME));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                records.forEach(record -> {
                    String dpiData = record.value();
                    String[] columns = dpiData.split(",");

                    String hostToCheck = columns[13];

                    boolean hostExists = checkHostExistsInDomains(hostToCheck);
                    boolean sendSMS = false;
                    boolean hostConditionMet = false;
                    boolean locationConditionMet = false;

                    double latitude = Double.parseDouble(columns[82]);
                    double longitude = Double.parseDouble(columns[83]);

                    for (int storeId : Location.getStoreIds()) {
                        double latMin = Location.getLatMin(storeId);
                        double latMax = Location.getLatMax(storeId);
                        double lonMin = Location.getLonMin(storeId);
                        double lonMax = Location.getLonMax(storeId);

                        if (latitude >= latMin && latitude <= latMax && longitude >= lonMin && longitude <= lonMax) {
                            System.out.println("Vị trí nằm trong phạm vi của store_id " + storeId);
                            sendSuccessfulSMS(storeId);
                            String phoneNumber = columns[81];
                            String adContent = fetchAdContentForStore(storeId);
                            if (adContent != null) {
                                System.out.println("Truy cập của khách hàng được quảng cáo. Gửi tin nhắn đến số điện thoại: " + phoneNumber);
                                SMSSender.sendSMS(phoneNumber, adContent);
                                sendSMS = true;
                            }
                        }
                    }

                    if (hostExists) {
                        hostConditionMet = true;
                        int storeIdFromHost = getStoreIdFromHost(hostToCheck); // Lấy storeId từ host

                        if (storeIdFromHost != -1) { // Kiểm tra storeId có hợp lệ
                            sendSuccessfulSMS(storeIdFromHost); // Gửi tin nhắn thành công cho storeId tương ứng
                            System.out.println("Khach hang truy cap website:    " + hostToCheck);
                        }
                    }

                    if (hostConditionMet && locationConditionMet) {
                        sendSMS = true;
                    }

                    if (sendSMS) {
                        String phoneNumber = columns[81];
                        String adContent = fetchAdContentForHost(hostToCheck);
                        if (adContent != null) {
                            System.out.println("Truy cập của khách hàng được quảng cáo. Gửi tin nhắn đến số điện thoại: " + phoneNumber);
                            SMSSender.sendSMS(phoneNumber, adContent);
                        }
                    } else {
                        System.out.println(hostToCheck + "\n" + "Không được quảng cáo hoặc nằm ngoài phạm vi, bỏ qua gửi sms");
                    }
                });

//                printMessageCounts();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean checkHostExistsInDomains(String host) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT domains.host " +
                    "FROM domains " +
                    "JOIN store_domains ON domains.domain_id = store_domains.target_domain " +
                    "JOIN advertising ON store_domains.store_id = advertising.store_id " +
                    "WHERE domains.host = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, host);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next(); // Return true if host exists in domains table
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void sendSuccessfulSMS(int storeId) {
        messageCountMap.put(storeId, messageCountMap.getOrDefault(storeId, 0) + 1);
    }

    private static String fetchAdContentForStore(int storeId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT ad_content FROM advertising WHERE store_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, storeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("ad_content");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String fetchAdContentForHost(String host) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT advertising.ad_content " +
                    "FROM domains " +
                    "JOIN store_domains ON domains.domain_id = store_domains.target_domain " +
                    "JOIN advertising ON store_domains.store_id = advertising.store_id " +
                    "WHERE domains.host = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, host);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("ad_content");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static int getStoreIdFromHost(String host) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT store_domains.store_id " +
                    "FROM domains " +
                    "JOIN store_domains ON domains.domain_id = store_domains.target_domain " +
                    "WHERE domains.host = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, host);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("store_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Trả về -1 nếu không tìm thấy storeId
    }
    private static void printMessageCounts() {
        System.out.println("Số lượng tin nhắn đã gửi thành công cho từng store_id:");
        for (Map.Entry<Integer, Integer> entry : messageCountMap.entrySet()) {
            int storeId = entry.getKey();
            int messageCount = entry.getValue();
            System.out.println("Store ID " + storeId + ": " + messageCount + " tin nhắn");
        }
    }
}
