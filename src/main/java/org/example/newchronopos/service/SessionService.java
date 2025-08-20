package org.example.newchronopos.service;

import org.example.newchronopos.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class SessionService {
    private static final String SESSION_FILE = "data/user_session.properties";
    private static final String SESSION_TIMEOUT_HOURS = "24"; // 24 hours session timeout
    private static User currentUser;

    /**
     * Check if a user session exists and is still valid
     */
    public static boolean isUserLoggedIn() {
        try {
            Path sessionPath = Paths.get(SESSION_FILE);
            if (!Files.exists(sessionPath)) {
                return false;
            }

            Properties sessionProps = new Properties();
            try (FileInputStream fis = new FileInputStream(SESSION_FILE)) {
                sessionProps.load(fis);
            }

            String userId = sessionProps.getProperty("user.id");
            String email = sessionProps.getProperty("user.email");
            String loginTimeStr = sessionProps.getProperty("login.time");
            String rememberMe = sessionProps.getProperty("remember.me", "false");

            if (userId == null || email == null || loginTimeStr == null) {
                return false;
            }

            // Check if session has expired (if remember me is not checked)
            if (!"true".equals(rememberMe)) {
                LocalDateTime loginTime = LocalDateTime.parse(loginTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                LocalDateTime expiryTime = loginTime.plusHours(Long.parseLong(SESSION_TIMEOUT_HOURS));

                if (LocalDateTime.now().isAfter(expiryTime)) {
                    clearSession();
                    return false;
                }
            }

            // Create user object from session data
            currentUser = new User();
            currentUser.setId(Integer.parseInt(userId));
            currentUser.setEmail(email);
            currentUser.setFullName(sessionProps.getProperty("user.name", ""));
            currentUser.setRole(sessionProps.getProperty("user.role", ""));
            currentUser.setShopid(Integer.parseInt(sessionProps.getProperty("user.shopid", "1")));

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            clearSession();
            return false;
        }
    }

    /**
     * Save user session after successful login
     */
    public static void saveUserSession(User user, boolean rememberMe) {
        try {
            // Ensure data directory exists
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            Properties sessionProps = new Properties();
            sessionProps.setProperty("user.id", String.valueOf(user.getId()));
            sessionProps.setProperty("user.email", user.getEmail());
            sessionProps.setProperty("user.name", user.getFullName() != null ? user.getFullName() : "");
            sessionProps.setProperty("user.role", user.getRole() != null ? user.getRole() : "");
            sessionProps.setProperty("user.shopid", String.valueOf(user.getShopid()));
            sessionProps.setProperty("login.time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            sessionProps.setProperty("remember.me", String.valueOf(rememberMe));

            try (FileOutputStream fos = new FileOutputStream(SESSION_FILE)) {
                sessionProps.store(fos, "ChronoPOS User Session");
            }

            currentUser = user;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear user session (logout)
     */
    public static void clearSession() {
        try {
            Path sessionPath = Paths.get(SESSION_FILE);
            if (Files.exists(sessionPath)) {
                Files.delete(sessionPath);
            }
            currentUser = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get current logged in user
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if current user has specific role
     */
    public static boolean hasRole(String role) {
        return currentUser != null && role.equals(currentUser.getRole());
    }

    /**
     * Check if current user is admin
     */
    public static boolean isAdmin() {
        return hasRole("Administrator") || hasRole("Admin");
    }
}
