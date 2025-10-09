package org.example.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class SecurityUtil {

    // Hash PIN/password using SHA-256
    public static String hashPin(String pin) {
        if (pin == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(pin.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm missing", e);
        }
    }

    // Verify plain pin against stored hash
    public static boolean verifyPin(String plain, String storedHash) {
        if (plain == null || storedHash == null) return false;
        return hashPin(plain).equals(storedHash);
    }

    // Simple email validator
    public static boolean isValidEmail(String e) {
        if (e == null) return false;
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(regex, e);
    }

    // Phone: 7-15 digits
    public static boolean isValidPhone(String p) {
        if (p == null) return false;
        return Pattern.matches("\\d{7,15}", p);
    }

    // PIN: 4-digit numeric (change if you want alphanumeric or 4-6)
    public static boolean isValidPinFormat(String pin) {
        if (pin == null) return false;
        return Pattern.matches("\\d{4}", pin);
    }
}
