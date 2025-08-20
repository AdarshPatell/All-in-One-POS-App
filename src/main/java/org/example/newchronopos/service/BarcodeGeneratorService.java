package org.example.newchronopos.service;

import java.util.Random;

public class BarcodeGeneratorService {

    private static final Random random = new Random();

    /**
     * Generates a standard EAN-13 barcode
     * @return A 13-digit barcode string
     */
    public String generateBarcode() {
        // Generate a 12-digit number (last digit will be check digit)
        StringBuilder barcode = new StringBuilder();

        // Country code (971 for UAE)
        barcode.append("971");

        // Company code (4 digits)
        for (int i = 0; i < 4; i++) {
            barcode.append(random.nextInt(10));
        }

        // Product code (5 digits)
        for (int i = 0; i < 5; i++) {
            barcode.append(random.nextInt(10));
        }

        // Calculate and append check digit
        String barcodeStr = barcode.toString();
        int checkDigit = calculateEAN13CheckDigit(barcodeStr);
        barcode.append(checkDigit);

        return barcode.toString();
    }

    /**
     * Generates a product-specific barcode based on product info
     * @param productName Product name
     * @param categoryId Category ID
     * @return Generated barcode
     */
    public String generateProductBarcode(String productName, int categoryId) {
        StringBuilder barcode = new StringBuilder();

        // Country code
        barcode.append("971");

        // Category-based prefix (2 digits)
        barcode.append(String.format("%02d", categoryId % 100));

        // Product name hash (2 digits)
        int nameHash = Math.abs(productName.hashCode()) % 100;
        barcode.append(String.format("%02d", nameHash));

        // Random product code (5 digits)
        for (int i = 0; i < 5; i++) {
            barcode.append(random.nextInt(10));
        }

        // Calculate and append check digit
        String barcodeStr = barcode.toString();
        int checkDigit = calculateEAN13CheckDigit(barcodeStr);
        barcode.append(checkDigit);

        return barcode.toString();
    }

    /**
     * Calculates the EAN-13 check digit
     * @param barcode 12-digit barcode string
     * @return Check digit (0-9)
     */
    private int calculateEAN13CheckDigit(String barcode) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(barcode.charAt(i));
            if (i % 2 == 0) {
                sum += digit;
            } else {
                sum += digit * 3;
            }
        }

        int remainder = sum % 10;
        return (remainder == 0) ? 0 : (10 - remainder);
    }

    /**
     * Validates if a barcode is a valid EAN-13
     * @param barcode Barcode to validate
     * @return true if valid, false otherwise
     */
    public boolean validateBarcode(String barcode) {
        if (barcode == null || barcode.length() != 13) {
            return false;
        }

        try {
            String first12 = barcode.substring(0, 12);
            int providedCheckDigit = Character.getNumericValue(barcode.charAt(12));
            int calculatedCheckDigit = calculateEAN13CheckDigit(first12);

            return providedCheckDigit == calculatedCheckDigit;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Generates a simple sequential barcode for internal use
     * @param sequenceNumber Sequence number
     * @return Internal barcode
     */
    public String generateInternalBarcode(int sequenceNumber) {
        return String.format("CHR%010d", sequenceNumber);
    }
}
