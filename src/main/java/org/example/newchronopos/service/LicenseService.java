package org.example.newchronopos.service;

import org.example.newchronopos.model.License;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

public class LicenseService {

    private static final String LICENSE_FILE = "system.license";
    private static final String SYSTEM_CONFIG_FILE = "system.config";

    // Get the application data directory
    private static Path getAppDataDirectory() {
        String userHome = System.getProperty("user.home");
        String appDataDir = System.getProperty("os.name").toLowerCase().contains("windows")
            ? System.getenv("APPDATA") + File.separator + "ChronoPos"
            : userHome + File.separator + ".chronopos";

        Path appDir = Paths.get(appDataDir);
        try {
            Files.createDirectories(appDir);
        } catch (Exception e) {
            // Fall back to user home directory if APPDATA fails
            appDir = Paths.get(userHome, ".chronopos");
            try {
                Files.createDirectories(appDir);
            } catch (Exception ex) {
                // Final fallback to current directory
                appDir = Paths.get(".");
            }
        }
        return appDir;
    }

    // Get full path for license file
    private static Path getLicenseFilePath() {
        return getAppDataDirectory().resolve(LICENSE_FILE);
    }

    // Get full path for config file
    private static Path getConfigFilePath() {
        return getAppDataDirectory().resolve(SYSTEM_CONFIG_FILE);
    }

    public static boolean isSystemLicensed() {
        try {
            Path licenseFilePath = getLicenseFilePath();
            if (!Files.exists(licenseFilePath)) {
                return false;
            }

            String licenseContent = Files.readString(licenseFilePath);
            License license = CryptographicService.decryptData(licenseContent, License.class);

            // Check if license is still valid
            if (!license.isActive() || license.getExpiresAt().isBefore(LocalDateTime.now())) {
                return false;
            }

            // Verify system fingerprint matches
            String currentFingerprint = SystemFingerprintService.generateSystemFingerprint();
            return license.getSystemFingerprint().equals(currentFingerprint);

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validateScratchCard(String scratchCode, String embeddedPassword) {
        try {
            // Decrypt embedded password to verify scratch card
            Map<String, Object> scratchData = CryptographicService.decryptScratchCardData(embeddedPassword);

            // Verify scratch code matches
            Object scratchCodeObj = scratchData.get("scratchCode");
            return scratchCodeObj != null && scratchCodeObj.toString().equals(scratchCode);

        } catch (Exception e) {
            return false;
        }
    }

    public static Map<String, Object> getSalesPersonInfo(String embeddedPassword) throws Exception {
        return CryptographicService.decryptScratchCardData(embeddedPassword);
    }

    public static String generateSalesPersonKey(String scratchCode, String customerName,
                                              String customerEmail, String customerPhone,
                                              String customerAddress) throws Exception {
        String systemFingerprint = SystemFingerprintService.generateSystemFingerprint();
        return CryptographicService.generateSalesPersonKey(scratchCode, customerName,
                                                          customerEmail, customerPhone,
                                                          customerAddress, systemFingerprint);
    }

    public static boolean activateLicense(String licenseKey) {
        try {
            System.out.println("Attempting to activate license...");
            System.out.println("License key length: " + licenseKey.length());

            // First, try to decrypt the license key to get license information
            Map<String, Object> licenseData = CryptographicService.decryptData(licenseKey, Map.class);
            System.out.println("License data decrypted successfully");

            // Extract license information from the decrypted data
            String extractedLicenseKey = (String) licenseData.get("licenseKey");
            String scratchCode = (String) licenseData.get("scratchCode");
            String salesPersonId = (String) licenseData.get("salesPersonId");
            String customerDetails = (String) licenseData.get("customerDetails");
            String systemFingerprint = (String) licenseData.get("systemFingerprint");
            String adminSignature = (String) licenseData.get("adminSignature");
            String issuedAtStr = (String) licenseData.get("issuedAt");
            String expiresAtStr = (String) licenseData.get("expiresAt");

            System.out.println("Extracted license info - Scratch Code: " + scratchCode);
            System.out.println("System fingerprint from license: " + systemFingerprint);

            // Verify system fingerprint matches
            String currentFingerprint = SystemFingerprintService.generateSystemFingerprint();
            System.out.println("Current system fingerprint: " + currentFingerprint);

            if (!systemFingerprint.equals(currentFingerprint)) {
                System.err.println("System fingerprint mismatch!");
                System.err.println("License fingerprint: " + systemFingerprint);
                System.err.println("Current fingerprint: " + currentFingerprint);
                throw new Exception("License not valid for this system - fingerprint mismatch");
            }

            // Create License object
            License license = new License();
            license.setLicenseKey(extractedLicenseKey);
            license.setScratchCode(scratchCode);
            license.setSalesPersonId(salesPersonId);
            license.setCustomerDetails(customerDetails);
            license.setSystemFingerprint(systemFingerprint);
            license.setAdminSignature(adminSignature);
            license.setActive(true);

            // Parse dates
            if (issuedAtStr != null) {
                license.setIssuedAt(java.time.LocalDateTime.parse(issuedAtStr));
            } else {
                license.setIssuedAt(java.time.LocalDateTime.now());
            }

            if (expiresAtStr != null) {
                license.setExpiresAt(java.time.LocalDateTime.parse(expiresAtStr));
            } else {
                license.setExpiresAt(java.time.LocalDateTime.now().plusYears(1));
            }

            System.out.println("License object created successfully");

            // Save license to file
            String encryptedLicense = CryptographicService.encryptData(license);
            Files.writeString(getLicenseFilePath(), encryptedLicense);

            // Save system configuration
            saveSystemConfig(license);

            System.out.println("License activated and saved successfully!");
            return true;

        } catch (Exception e) {
            System.err.println("License activation failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void saveSystemConfig(License license) throws Exception {
        Map<String, String> config = new HashMap<>();
        config.put("licenseKey", license.getLicenseKey());
        config.put("activatedAt", LocalDateTime.now().toString());
        config.put("systemFingerprint", license.getSystemFingerprint());

        String encryptedConfig = CryptographicService.encryptData(config);
        Files.writeString(getConfigFilePath(), encryptedConfig);
    }

    public static License getCurrentLicense() {
        try {
            if (!isSystemLicensed()) {
                return null;
            }

            String licenseContent = Files.readString(getLicenseFilePath());
            return CryptographicService.decryptData(licenseContent, License.class);

        } catch (Exception e) {
            return null;
        }
    }
}
