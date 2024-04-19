package fakedata;

import com.github.javafaker.Faker;

import java.io.FileWriter;
import java.io.IOException;

public class DomainDataGenerator {
    public static void main(String[] args) {
        Faker faker = new Faker();

        String[] categories = {"the thao", "game", "tin tuc", "thoi trang", "cong nghe"};

        try (FileWriter writer = new FileWriter("domains.csv")) {
            // Write header
            writer.append("host,web_category/n");

            // Generate and write fake data
            for (int i = 0; i < 1000000; i++) {
                String domain = faker.internet().domainName();
                String category = categories[faker.number().numberBetween(0, categories.length)];
                writer.append(domain).append(",").append(category).append("\n");
            }

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
