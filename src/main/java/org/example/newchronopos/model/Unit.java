package org.example.newchronopos.model;

import java.time.LocalDateTime;

public class Unit {
    private int id;
    private String name;
    private String abbreviation;
    private String description;
    private String baseUnit;
    private String conversionFactor;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Unit() {}

    public Unit(int id, String name, String abbreviation, String description, String status) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.description = description;
        this.status = status;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAbbreviation() { return abbreviation; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBaseUnit() { return baseUnit; }
    public void setBaseUnit(String baseUnit) { this.baseUnit = baseUnit; }

    public String getConversionFactor() { return conversionFactor; }
    public void setConversionFactor(String conversionFactor) { this.conversionFactor = conversionFactor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Backward compatibility methods for existing controllers
    public String getUnitId() { return String.valueOf(id); }
    public void setUnitId(String unitId) { this.id = Integer.parseInt(unitId); }

    public String getUnitName() { return name; }
    public void setUnitName(String unitName) { this.name = unitName; }
}
