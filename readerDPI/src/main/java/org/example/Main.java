package org.example;

import dashboard.models.Login;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {

//        Thread producerThread = new Thread(() -> RealtimeDataProducer.main(new String[]{}));
//        producerThread.start();
//
//
//        Thread consumerThread = new Thread(() -> KafkaDataConsumer.main(new String[]{}));
//        consumerThread.start();
//
//
//        Thread offlineConsumerThread = new Thread(() -> OfflineDataConsumer.main(new String[]{}));
//        offlineConsumerThread.start();

        // Chạy Dashboard
        Application.launch(Login.class, args);
    }
}
