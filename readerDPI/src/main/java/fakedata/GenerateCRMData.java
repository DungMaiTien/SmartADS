package fakedata;

import com.github.javafaker.Faker;

import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Locale;

class GenerateCRMData {

    public static void main(String[] args) {
        // Khởi tạo Faker với ngôn ngữ tiếng Việt
        Faker faker = new Faker(new Locale("vi"));

        // Tạo dữ liệu và ghi vào file CSV
        try (FileWriter csvWriter = new FileWriter("crm_data.csv")) {
            // Viết tiêu đề cho các cột
            csvWriter.append("msisdn,tuoi,gioi_tinh,dia_chi\n");

            // Tạo 10.000 bản ghi dữ liệu
            for (int i = 0; i < 10000; i++) {
                String msisdn = removeDiacritics(faker.phoneNumber().cellPhone());
                int tuoi = faker.number().numberBetween(18, 80);
                String gioiTinh = faker.options().option("Nam", "Nu");
                String diaChi = removeDiacritics(faker.address().fullAddress());

                // Ghi bản ghi vào file CSV
                csvWriter.append(String.join(",", msisdn, String.valueOf(tuoi), gioiTinh, diaChi));
                csvWriter.append("\n");
            }

            System.out.println("Dữ liệu đã được tạo và lưu vào file 'crm_data.csv'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Phương thức loại bỏ dấu tiếng Việt từ chuỗi
    private static String removeDiacritics(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("Đ", "D").replaceAll("đ", "d"); // Thay thế kí tự "Đ" và "đ" với "D" và "d"
    }
}
