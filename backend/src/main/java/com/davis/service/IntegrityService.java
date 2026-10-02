package com.davis.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class IntegrityService {

    public String computeSha256(String data) {
        if (data == null) data = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm unavailable", e);
        }
    }

    public Map<String, Object> verifyIntegrity(String rawData, String expectedHash) {
        String computed = computeSha256(rawData);
        boolean valid = computed.equalsIgnoreCase(expectedHash);
        Map<String, Object> result = new HashMap<>();
        result.put("expectedHash", expectedHash);
        result.put("computedHash", computed);
        result.put("status", valid ? "VALID" : "MODIFIED");
        result.put("match", valid);
        return result;
    }
}
