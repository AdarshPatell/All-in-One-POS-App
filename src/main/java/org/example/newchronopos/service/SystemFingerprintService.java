package org.example.newchronopos.service;

import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

public class SystemFingerprintService {

    public static String generateSystemFingerprint() {
        try {
            StringBuilder fingerprint = new StringBuilder();

            // Get system properties that are available in JDK 17
            fingerprint.append(System.getProperty("os.name", "unknown"));
            fingerprint.append(System.getProperty("os.arch", "unknown"));
            fingerprint.append(System.getProperty("os.version", "unknown"));
            fingerprint.append(System.getProperty("user.name", "unknown"));
            fingerprint.append(System.getProperty("java.home", "unknown"));

            // Get available processors
            fingerprint.append(Runtime.getRuntime().availableProcessors());

            // Get total memory
            fingerprint.append(Runtime.getRuntime().totalMemory());

            // Get MAC address
            String macAddress = getMacAddress();
            if (macAddress != null) {
                fingerprint.append(macAddress);
            }

            // Get system environment variables that are commonly available
            String computerName = System.getenv("COMPUTERNAME");
            if (computerName == null) {
                computerName = System.getenv("HOSTNAME");
            }
            if (computerName != null) {
                fingerprint.append(computerName);
            }

            String userDomain = System.getenv("USERDOMAIN");
            if (userDomain != null) {
                fingerprint.append(userDomain);
            }

            // Generate hash of the fingerprint
            return generateHash(fingerprint.toString());

        } catch (Exception e) {
            // Fallback fingerprint if system info gathering fails
            String fallbackFingerprint = "FALLBACK-" +
                System.getProperty("user.name", "unknown") + "-" +
                System.getProperty("os.name", "unknown") + "-" +
                System.currentTimeMillis();
            return generateHash(fallbackFingerprint);
        }
    }

    private static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // Skip loopback and virtual interfaces
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }

                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null && mac.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            // Ignore and return null if MAC address cannot be obtained
        }
        return null;
    }

    private static String generateHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString().toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            // This should never happen as SHA-256 is always available
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
