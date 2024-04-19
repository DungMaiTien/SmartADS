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
import java.util.Properties;

public class KafkaDataConsumer {
    private static final String TOPIC_NAME = "realtime_data";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "kafka-data-consumer-group";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        double latMin = Location.getLatMin();
        double latMax = Location.getLatMax();
        double lonMin = Location.getLonMin();
        double lonMax = Location.getLonMax();
        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC_NAME));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                records.forEach(record -> {
                    // Process the record
                    String dpiData = record.value();
                    String[] columns = dpiData.split(","); // Assuming data is comma-separated

                    // Get the host to check from column 14 of dpi.csv
                    String hostToCheck = columns[13]; // Assuming host to check is in column 14
                    System.out.println("Processing record for host: " + hostToCheck);

                    // Check if the host exists in the domains table
                    boolean hostExists = checkHostExistsInDomains(hostToCheck);

                    // Initialize flags for host and location checks
                    boolean sendSMS = false;
                    boolean hostConditionMet = false;
                    boolean locationConditionMet = false;

                    // If host exists, set hostConditionMet flag to true
                    if (hostExists) {
                        hostConditionMet = true;
                        sendSMS = true; // Send SMS immediately if host exists
                    }

                    // If latitude and longitude are within range, set locationConditionMet flag to true
                    double latitude = Double.parseDouble(columns[82]); // Assuming latitude is in column 83
                    double longitude = Double.parseDouble(columns[83]); // Assuming longitude is in column 84
                    if (latitude >= Location.getLatMin() && latitude <= Location.getLatMax() &&
                            longitude >= Location.getLonMin() && longitude <= Location.getLonMax())
                    {
                        locationConditionMet = true;
                        sendSMS = true; // Send SMS if location condition is met
                        System.out.println("truong hop 1");
                    }

                    // If both host and location conditions are met, send SMS
                    if (hostConditionMet && locationConditionMet) {
                        sendSMS = true;
                        System.out.println("truong hop 2");
                    }

                    // If any of the conditions are met, send SMS
                    if (sendSMS) {
                        String phoneNumber = columns[81]; // Assuming phone number is in column 82
                        String message = "Chuc mung ban da nhan duoc voucher giam 50% cho khach hang moi khi mua bat ky san pham nao tai cua hang:\n Ma voucher cua ban la: VCDC50";
                        System.out.println("Truy cap duoc cua hang quang cao. Sending SMS to phone number: " + phoneNumber);
                        SMSSender.sendSMS(phoneNumber, message);
                    } else {
                        System.out.println("Khong thuoc quang cao. Skipping SMS sending.");
                    }
                });
            }

        }
    }

    private static boolean checkHostExistsInDomains(String host) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT host FROM domains WHERE domain_id IN (SELECT target_domain FROM store_domains WHERE store_id = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Assuming store_id is obtained from somewhere
                stmt.setInt(1, 1); // Change to actual store_id
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String domainHost = rs.getString("host");
                        if (host.equals(domainHost)) {
                            return true; // If host matches, return true
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}