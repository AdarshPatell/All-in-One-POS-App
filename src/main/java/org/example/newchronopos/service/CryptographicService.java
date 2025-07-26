package org.example.newchronopos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CryptographicService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String MASTER_KEY = "CHRONOPOS_MASTER_KEY_2025";
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static String encryptData(Object data) throws Exception {
        String jsonData = objectMapper.writeValueAsString(data);

        SecretKeySpec secretKey = generateKeyFromMaster();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedData = cipher.doFinal(jsonData.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    @SuppressWarnings("unchecked")
    public static <T> T decryptData(String encryptedData, Class<T> clazz) throws Exception {
        SecretKeySpec secretKey = generateKeyFromMaster();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);

        String jsonData = new String(decryptedData, StandardCharsets.UTF_8);

        if (clazz == Map.class) {
            return (T) objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {});
        } else {
            return objectMapper.readValue(jsonData, clazz);
        }
    }

    public static String encryptScratchCardData(String scratchCode, String salesPersonId,
                                              String salesPersonName, String territory) throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("scratchCode", scratchCode);
        data.put("salesPersonId", salesPersonId);
        data.put("salesPersonName", salesPersonName);
        data.put("territory", territory);
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return encryptData(data);
    }

    public static Map<String, Object> decryptScratchCardData(String encryptedData) throws Exception {
        return decryptData(encryptedData, Map.class);
    }

    public static String generateSalesPersonKey(String scratchCode, String customerName,
                                              String customerEmail, String customerPhone,
                                              String customerAddress, String systemFingerprint) throws Exception {
        Map<String, String> salesData = new HashMap<>();
        salesData.put("scratchCode", scratchCode);
        salesData.put("customerName", customerName);
        salesData.put("customerEmail", customerEmail);
        salesData.put("customerPhone", customerPhone);
        salesData.put("customerAddress", customerAddress);
        salesData.put("systemFingerprint", systemFingerprint);
        salesData.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return encryptData(salesData);
    }

    public static Map<String, Object> decryptSalesPersonKey(String encryptedKey) throws Exception {
        return decryptData(encryptedKey, Map.class);
    }

    private static SecretKeySpec generateKeyFromMaster() throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(MASTER_KEY.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = new byte[16]; // Use only first 128 bits
        System.arraycopy(key, 0, keyBytes, 0, 16);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static String generateScratchCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }

    public static String generateLicenseKey() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            if (i > 0 && i % 4 == 0) {
                key.append("-");
            }
            key.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return key.toString();
    }
}
