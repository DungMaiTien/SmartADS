package fakedata;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Location {
    private static final String CSV_FILE_PATH = "location.csv";
    private static final double MIN_LATITUDE = 20.927050;
    private static final double MAX_LATITUDE = 21.111009;
    private static final double MIN_LONGITUDE = 105.642290;
    private static final double MAX_LONGITUDE = 105.976484;
    private static final int NUM_RECORDS = 500000;

    public static void main(String[] args) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(CSV_FILE_PATH));

            Random random = new Random();

            for (int i = 0; i < NUM_RECORDS; i++) {
                double latitude = MIN_LATITUDE + (MAX_LATITUDE - MIN_LATITUDE) * random.nextDouble();
                double longitude = MIN_LONGITUDE + (MAX_LONGITUDE - MIN_LONGITUDE) * random.nextDouble();

                String formattedLatitude = String.format("%.6f", latitude);
                String formattedLongitude = String.format("%.6f", longitude);

                String[] record = {formattedLatitude, formattedLongitude};
                writer.writeNext(record);
            }

            writer.close();
            System.out.println("CSV file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
