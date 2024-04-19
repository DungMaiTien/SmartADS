package org.example;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Properties;

public class OfflineDataConsumer {
    private static final String TOPIC_NAME = "realtime_data";
    private static final String CSV_FILE_PATH = "D:\\TTVNPT\\SmartADS\\offline_data.csv";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "offline-data-consumer-group";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC_NAME));

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH, true))) {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    records.forEach(record -> {
                        try {
                            // Lấy thời gian hiện tại
                            LocalDateTime currentTime = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String formattedTime = currentTime.format(formatter);


                            // Phân tách dữ liệu từ Kafka và ghi vào file CSV với thời gian và vị trí hiện tại
                            String[] columns = record.value().split(",");
                            writer.write(columns[81] + "," + columns[13] + "," + columns[82] + "," + columns[83] + "," + formattedTime + "\n" );
                            System.out.println("Received message from Kafka and wrote to CSV: " + record.value());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
