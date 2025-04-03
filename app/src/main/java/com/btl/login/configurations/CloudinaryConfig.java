package com.btl.login.configurations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

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
}
