package org.example.newchronopos.service.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Settings Validation Framework
 */
public class SettingsValidator {

    // Validation result class
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;

        public ValidationResult() {
            this.valid = true;
            this.errors = new ArrayList<>();
        }

        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }

        public void addError(String error) {
            this.valid = false;
            this.errors.add(error);
        }

        public void addErrors(List<String> errors) {
            if (!errors.isEmpty()) {
                this.valid = false;
                this.errors.addAll(errors);
            }
        }
    }

    // Data type patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^https?://[A-Za-z0-9.-]+\\.[A-Za-z]{2,}(/.*)?$"
    );
    
    private static final Pattern COLOR_HEX_PATTERN = Pattern.compile(
        "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"
    );

    /**
     * Validate a single setting
     */
    public static ValidationResult validateSetting(String key, String value, String dataType) {
        ValidationResult result = new ValidationResult();
        
        if (key == null || key.trim().isEmpty()) {
            result.addError("Setting key cannot be null or empty");
            return result;
        }

        if (value == null) {
            return result; // null values are generally allowed
        }

        String trimmedValue = value.trim();
        
        switch (dataType.toUpperCase()) {
            case "STRING":
                result.addErrors(validateString(key, trimmedValue));
                break;
            case "INTEGER":
                result.addErrors(validateInteger(key, trimmedValue));
                break;
            case "DECIMAL":
                result.addErrors(validateDecimal(key, trimmedValue));
                break;
            case "BOOLEAN":
                result.addErrors(validateBoolean(key, trimmedValue));
                break;
            case "EMAIL":
                result.addErrors(validateEmail(key, trimmedValue));
                break;
            case "URL":
                result.addErrors(validateUrl(key, trimmedValue));
                break;
            case "COLOR":
                result.addErrors(validateColor(key, trimmedValue));
                break;
            case "PERCENTAGE":
                result.addErrors(validatePercentage(key, trimmedValue));
                break;
            case "CURRENCY":
                result.addErrors(validateCurrency(key, trimmedValue));
                break;
            case "DATE_FORMAT":
                result.addErrors(validateDateFormat(key, trimmedValue));
                break;
            default:
                // Unknown data type, treat as string
                result.addErrors(validateString(key, trimmedValue));
        }

        // Key-specific validations
        result.addErrors(validateKeySpecificRules(key, trimmedValue));

        return result;
    }

    /**
     * Validate string values
     */
    private static List<String> validateString(String key, String value) {
        List<String> errors = new ArrayList<>();
        
        if (value.length() > 1000) {
            errors.add("String value for '" + key + "' is too long (max 1000 characters)");
        }
        
        return errors;
    }

    /**
     * Validate integer values
     */
    private static List<String> validateInteger(String key, String value) {
        List<String> errors = new ArrayList<>();
        
        try {
            long longValue = Long.parseLong(value);
            if (longValue > Integer.MAX_VALUE || longValue < Integer.MIN_VALUE) {
                errors.add("Integer value for '" + key + "' is out of range");
            }
        } catch (NumberFormatException e) {
            errors.add("Value for '" + key + "' must be a valid integer");
        }
        
        return errors;
    }

    /**
     * Validate decimal values
     */
    private static List<String> validateDecimal(String key, String value) {
        List<String> errors = new ArrayList<>();
        
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            errors.add("Value for '" + key + "' must be a valid decimal number");
        }
        
        return errors;
    }

    /**
     * Validate boolean values
     */
    private static List<String> validateBoolean(String key, String value) {
        List<String> errors = new ArrayList<>();
        
        String lowerValue = value.toLowerCase();
        if (!("true".equals(lowerValue) || "false".equals(lowerValue) ||
              "1".equals(value) || "0".equals(value) ||
              "yes".equals(lowerValue) || "no".equals(lowerValue))) {
            errors.add("Value for '" + key + "' must be a valid boolean (true/false, 1/0, yes/no)");
        }
        
        return errors;
    }

    /**
     * Validate email addresses
     */
    private static List<String> validateEmail(String key, String value) {
        List<String> errors = new ArrayList<>();
        
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            errors.add("Value for '" + key + "' must be a valid email address");
        }
        
        return errors;
    }

    /**
     * Validate URLs
     */
    private static List<String> validateUrl(String key, String value) {
        List<String> errors = new ArrayList<>();
        
        if (!URL_PATTERN.matcher(value).matches()) {
            errors.add("Value for '" + key + "' must be a valid URL");
        }
        
        return errors;
    }

    /**
     * Validate color hex codes
     */
    private static List<String> validateColor(String key, String value) {
        List<String> errors = new ArrayList<>();
        
        if (!COLOR_HEX_PATTERN.matcher(value).matches()) {
            errors.add("Value for '" + key + "' must be a valid hex color code (e.g., #FF0000)");
        }
        
        return errors;
    }

    /**
     * Validate percentage values
     */
    private static List<String> validatePercentage(String key, String value) {
        List<String> errors = new ArrayList<>();
        
        try {
            double percentage = Double.parseDouble(value);
            if (percentage < 0 || percentage > 100) {
                errors.add("Percentage value for '" + key + "' must be between 0 and 100");
            }
        } catch (NumberFormatException e) {
            errors.add("Value for '" + key + "' must be a valid percentage (0-100)");
        }
        
        return errors;
    }

    /**
     * Validate currency codes
     */
    private static List<String> validateCurrency(String key, String value) {
        List<String> errors = new ArrayList<>();
        
        // ISO 4217 currency codes are 3 letters
        if (value.length() != 3 || !value.matches("[A-Z]{3}")) {
            errors.add("Currency code for '" + key + "' must be a valid 3-letter ISO code (e.g., USD, EUR)");
        }
        
        return errors;
    }

    /**
     * Validate date format patterns
     */
    private static List<String> validateDateFormat(String key, String value) {
        List<String> errors = new ArrayList<>();
        
        try {
            // Try to create a date formatter with the pattern
            java.time.format.DateTimeFormatter.ofPattern(value);
        } catch (IllegalArgumentException e) {
            errors.add("Date format for '" + key + "' is not a valid pattern");
        }
        
        return errors;
    }

    /**
     * Validate key-specific business rules
     */
    private static List<String> validateKeySpecificRules(String key, String value) {
        List<String> errors = new ArrayList<>();
        
        switch (key) {
            case "security.session_timeout":
                try {
                    int timeout = Integer.parseInt(value);
                    if (timeout < 5 || timeout > 480) {
                        errors.add("Session timeout must be between 5 and 480 minutes");
                    }
                } catch (NumberFormatException e) {
                    // Already handled by integer validation
                }
                break;
                
            case "security.password_min_length":
                try {
                    int minLength = Integer.parseInt(value);
                    if (minLength < 4 || minLength > 50) {
                        errors.add("Password minimum length must be between 4 and 50 characters");
                    }
                } catch (NumberFormatException e) {
                    // Already handled by integer validation
                }
                break;
                
            case "app.decimal_places":
                try {
                    int decimalPlaces = Integer.parseInt(value);
                    if (decimalPlaces < 0 || decimalPlaces > 6) {
                        errors.add("Decimal places must be between 0 and 6");
                    }
                } catch (NumberFormatException e) {
                    // Already handled by integer validation
                }
                break;
                
            case "backup.backup_interval":
                try {
                    int interval = Integer.parseInt(value);
                    if (interval < 1 || interval > 168) { // 1 hour to 1 week
                        errors.add("Backup interval must be between 1 and 168 hours");
                    }
                } catch (NumberFormatException e) {
                    // Already handled by integer validation
                }
                break;
                
            case "app.language":
                if (value.length() != 2 || !value.matches("[a-z]{2}")) {
                    errors.add("Language code must be a valid 2-letter ISO code (e.g., en, ar)");
                }
                break;
                
            case "tax.default_rate":
                try {
                    double taxRate = Double.parseDouble(value);
                    if (taxRate < 0 || taxRate > 100) {
                        errors.add("Tax rate must be between 0 and 100 percent");
                    }
                } catch (NumberFormatException e) {
                    // Already handled by decimal validation
                }
                break;
        }
        
        return errors;
    }

    /**
     * Validate multiple settings at once
     */
    public static ValidationResult validateSettings(List<SettingValidationRequest> requests) {
        ValidationResult overallResult = new ValidationResult();
        
        for (SettingValidationRequest request : requests) {
            ValidationResult result = validateSetting(request.key, request.value, request.dataType);
            if (!result.isValid()) {
                overallResult.addErrors(result.getErrors());
            }
        }
        
        return overallResult;
    }

    /**
     * Request class for batch validation
     */
    public static class SettingValidationRequest {
        public String key;
        public String value;
        public String dataType;

        public SettingValidationRequest(String key, String value, String dataType) {
            this.key = key;
            this.value = value;
            this.dataType = dataType;
        }
    }

    /**
     * Check if a setting key requires a specific data type
     */
    public static String getRequiredDataType(String key) {
        switch (key) {
            case "company.email":
            case "admin.email":
                return "EMAIL";
            case "company.website":
                return "URL";
            case "ui.primary_color":
            case "ui.secondary_color":
                return "COLOR";
            case "tax.default_rate":
            case "discount.max_percentage":
                return "PERCENTAGE";
            case "app.currency":
                return "CURRENCY";
            case "app.date_format":
                return "DATE_FORMAT";
            case "security.session_timeout":
            case "security.password_min_length":
            case "app.decimal_places":
            case "backup.backup_interval":
                return "INTEGER";
            case "backup.auto_backup":
            case "security.require_password_change":
            case "ui.dark_mode":
                return "BOOLEAN";
            default:
                return "STRING";
        }
    }
}
