package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class RealtimeDataProducer {
    private static final String TOPIC_NAME = "realtime_data";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public static void main(String[] args) {
        // Kafka producer configuration
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            // Read data from DPI file and send to Kafka
            try (BufferedReader br = new BufferedReader(new FileReader("D:\\TTVNPT\\dpi_web.csv"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    producer.send(new ProducerRecord<>(TOPIC_NAME, line));
                    System.out.println("Sent message to Kafka: " + line);
                    // Simulate delay between sending messages (1 second in this example)
                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
