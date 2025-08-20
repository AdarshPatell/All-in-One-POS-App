package org.example.newchronopos.model;

import java.time.LocalDateTime;

public class Supplier {
    private int id;  // Changed from String supplierId to int id
    private String name;  // Changed from companyName to name
    private String contactPerson;
    private String email;
    private String phone;
    private String address;  // Added missing field
    private String city;     // Added missing field
    private String state;    // Added missing field
    private String country;  // Added missing field
    private String postalCode; // Added missing field
    private String status;
    private LocalDateTime createdAt; // Added missing field
    private LocalDateTime updatedAt; // Added missing field

    public Supplier() {}

    public Supplier(int id, String name, String contactPerson, String email, String phone, String status) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Backward compatibility methods
    public String getSupplierId() { return String.valueOf(id); }
    public void setSupplierId(String supplierId) { this.id = Integer.parseInt(supplierId); }

    public String getCompanyName() { return name; }
    public void setCompanyName(String companyName) { this.name = companyName; }

    @Override
    public String toString() {
        return name;
    }
}
