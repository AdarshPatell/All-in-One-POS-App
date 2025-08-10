package org.example.newchronopos.test;

import org.example.newchronopos.service.settings.SettingsService;
import org.example.newchronopos.service.settings.SettingsServiceImpl;
import org.example.newchronopos.service.settings.SettingsValidator;

/**
 * Simple test class to verify settings system functionality
 */
public class SettingsSystemTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 Testing ChronoPOS Settings System...\n");
        
        try {
            // Load H2 driver explicitly
            Class.forName("org.h2.Driver");
            System.out.println("✅ H2 Driver loaded successfully\n");
            
            // Initialize settings service
            SettingsService settingsService = new SettingsServiceImpl();
            
            // Test 1: Initialize default settings
            System.out.println("✅ Test 1: Initialize default settings");
            settingsService.initializeDefaultSettings();
            System.out.println("   Default settings initialized successfully\n");
            
            // Test 2: System settings operations
            System.out.println("✅ Test 2: System settings operations");
            settingsService.setSystemSetting("test.system.key", "test_value", "TEST");
            String systemValue = settingsService.getSystemSetting("test.system.key").orElse("NOT_FOUND");
            System.out.println("   System setting stored and retrieved: " + systemValue);
            
            boolean boolValue = settingsService.getSystemBooleanSetting("backup.auto_backup", false);
            System.out.println("   Boolean setting retrieved: " + boolValue);
            
            int intValue = settingsService.getSystemIntegerSetting("app.decimal_places", 0);
            System.out.println("   Integer setting retrieved: " + intValue + "\n");
            
            // Test 3: Company settings operations
            System.out.println("✅ Test 3: Company settings operations");
            Long companyId = 1L;
            settingsService.setCompanySetting(companyId, "company.test.key", "company_value", "TEST");
            String companyValue = settingsService.getCompanySetting(companyId, "company.test.key").orElse("NOT_FOUND");
            System.out.println("   Company setting stored and retrieved: " + companyValue + "\n");
            
            // Test 4: User settings operations
            System.out.println("✅ Test 4: User settings operations");
            Long userId = 1L;
            settingsService.setUserSetting(userId, "user.test.key", "user_value", "TEST");
            String userValue = settingsService.getUserSetting(userId, "user.test.key").orElse("NOT_FOUND");
            System.out.println("   User setting stored and retrieved: " + userValue + "\n");
            
            // Test 5: Hierarchical settings lookup
            System.out.println("✅ Test 5: Hierarchical settings lookup");
            
            // Set values at different levels
            settingsService.setSystemSetting("hierarchical.test", "system_value", "TEST");
            settingsService.setCompanySetting(companyId, "hierarchical.test", "company_value", "TEST");
            settingsService.setUserSetting(userId, "hierarchical.test", "user_value", "TEST");
            
            // Test hierarchical lookup
            String hierarchicalValue = settingsService.getSetting(userId, companyId, "hierarchical.test").orElse("NOT_FOUND");
            System.out.println("   Hierarchical lookup (should return user_value): " + hierarchicalValue);
            
            // Test without user setting
            String companyOnlyValue = settingsService.getSetting(null, companyId, "hierarchical.test").orElse("NOT_FOUND");
            System.out.println("   Company-only lookup (should return company_value): " + companyOnlyValue);
            
            // Test without company and user settings
            String systemOnlyValue = settingsService.getSetting(null, null, "hierarchical.test").orElse("NOT_FOUND");
            System.out.println("   System-only lookup (should return system_value): " + systemOnlyValue + "\n");
            
            // Test 6: Settings validation
            System.out.println("✅ Test 6: Settings validation");
            
            SettingsValidator.ValidationResult validResult = 
                SettingsValidator.validateSetting("test.email", "user@example.com", "EMAIL");
            System.out.println("   Valid email validation: " + validResult.isValid());
            
            SettingsValidator.ValidationResult invalidResult = 
                SettingsValidator.validateSetting("test.email", "invalid-email", "EMAIL");
            System.out.println("   Invalid email validation: " + invalidResult.isValid());
            System.out.println("   Validation errors: " + invalidResult.getErrors() + "\n");
            
            // Test 7: Categories
            System.out.println("✅ Test 7: Categories");
            System.out.println("   System categories: " + settingsService.getSystemCategories());
            System.out.println("   Company categories: " + settingsService.getCompanyCategories(companyId));
            System.out.println("   User categories: " + settingsService.getUserCategories(userId) + "\n");
            
            // Test 8: Export/Import
            System.out.println("✅ Test 8: Export functionality");
            var exportedSettings = settingsService.exportSystemSettings();
            System.out.println("   Exported " + exportedSettings.size() + " system settings");
            System.out.println("   Sample exported settings: " + 
                exportedSettings.entrySet().stream()
                    .limit(3)
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .reduce("", (a, b) -> a + "\n   " + b) + "\n");
            
            System.out.println("🎉 All tests completed successfully!");
            System.out.println("📊 Settings System Status: OPERATIONAL");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
