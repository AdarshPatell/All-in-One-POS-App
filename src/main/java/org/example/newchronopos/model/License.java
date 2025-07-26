package org.example.newchronopos.model;

import java.time.LocalDateTime;

public class License {
    private String licenseKey;
    private String scratchCode;
    private String salesPersonId;
    private String customerDetails;
    private String systemFingerprint;
    private boolean isActive;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private String adminSignature;

    public License() {}

    public License(String licenseKey, String scratchCode, String salesPersonId,
                  String customerDetails, String systemFingerprint, String adminSignature) {
        this.licenseKey = licenseKey;
        this.scratchCode = scratchCode;
        this.salesPersonId = salesPersonId;
        this.customerDetails = customerDetails;
        this.systemFingerprint = systemFingerprint;
        this.adminSignature = adminSignature;
        this.isActive = true;
        this.issuedAt = LocalDateTime.now();
        // License valid for 1 year by default
        this.expiresAt = LocalDateTime.now().plusYears(1);
    }

    // Getters and setters
    public String getLicenseKey() { return licenseKey; }
    public void setLicenseKey(String licenseKey) { this.licenseKey = licenseKey; }

    public String getScratchCode() { return scratchCode; }
    public void setScratchCode(String scratchCode) { this.scratchCode = scratchCode; }

    public String getSalesPersonId() { return salesPersonId; }
    public void setSalesPersonId(String salesPersonId) { this.salesPersonId = salesPersonId; }

    public String getCustomerDetails() { return customerDetails; }
    public void setCustomerDetails(String customerDetails) { this.customerDetails = customerDetails; }

    public String getSystemFingerprint() { return systemFingerprint; }
    public void setSystemFingerprint(String systemFingerprint) { this.systemFingerprint = systemFingerprint; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getAdminSignature() { return adminSignature; }
    public void setAdminSignature(String adminSignature) { this.adminSignature = adminSignature; }
}
