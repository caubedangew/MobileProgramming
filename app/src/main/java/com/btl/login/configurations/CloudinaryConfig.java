package com.btl.login.configurations;

import android.annotation.SuppressLint;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class CloudinaryConfig {
    private static final String CLOUD_NAME = "dn84ltxow";
    private static final String API_KEY = "251333144845721";
    private static final String API_SECRET = "YFNzWK9pM2ktEb30zBbmIfMpczs";

    public static Cloudinary getCloudinaryClient() {
        // Cấu hình Cloudinary với thông tin của bạn
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET
        );

        return new Cloudinary(config);
    }

    public static void deleteImage(String publicId) {
        try {
            String urlString = "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/destroy";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Thiết lập phương thức HTTP và các header
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String auth = API_KEY + ":" + API_SECRET;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

            // Thêm các tham số vào request body
            String params = "public_id=" + publicId + "&invalidate=true";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = params.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Đọc phản hồi từ Cloudinary
            int responseCode = connection.getResponseCode();
            String message = connection.getResponseMessage();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Image successfully deleted");
            } else {
                System.out.println("Failed to delete image. Response message: " + message);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
