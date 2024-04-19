package org.example;

import org.json.JSONObject;

public class SMSSender {
    public static void sendSMS(String phoneNumber, String message) {
        // Tạo JSON object chứa thông tin tin nhắn
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("phone", phoneNumber);
        jsonMessage.put("message", message);

        // In ra thông tin tin nhắn dạng JSON
        System.out.println("da gui tin nhan den");
        System.out.println(jsonMessage.toString());
    }
}
