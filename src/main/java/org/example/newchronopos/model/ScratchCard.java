package org.example.newchronopos.model;

import java.time.LocalDateTime;

public class ScratchCard {
    private String scratchCode;
    private String salesPersonId;
    private String salesPersonName;
    private String embeddedPassword;
    private boolean isUsed;
    private LocalDateTime createdAt;
    private LocalDateTime usedAt;
    private String territory;

    public ScratchCard() {}

    public ScratchCard(String scratchCode, String salesPersonId, String salesPersonName,
                      String embeddedPassword, String territory) {
        this.scratchCode = scratchCode;
        this.salesPersonId = salesPersonId;
        this.salesPersonName = salesPersonName;
        this.embeddedPassword = embeddedPassword;
        this.territory = territory;
        this.isUsed = false;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getScratchCode() { return scratchCode; }
    public void setScratchCode(String scratchCode) { this.scratchCode = scratchCode; }

    public String getSalesPersonId() { return salesPersonId; }
    public void setSalesPersonId(String salesPersonId) { this.salesPersonId = salesPersonId; }

    public String getSalesPersonName() { return salesPersonName; }
    public void setSalesPersonName(String salesPersonName) { this.salesPersonName = salesPersonName; }

    public String getEmbeddedPassword() { return embeddedPassword; }
    public void setEmbeddedPassword(String embeddedPassword) { this.embeddedPassword = embeddedPassword; }

    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { isUsed = used; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }

    public String getTerritory() { return territory; }
    public void setTerritory(String territory) { this.territory = territory; }
}
