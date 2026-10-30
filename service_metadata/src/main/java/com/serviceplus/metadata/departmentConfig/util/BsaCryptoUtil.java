package com.serviceplus.metadata.departmentConfig.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public final class BsaCryptoUtil {

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}";
    private static final String ALL_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL;

    private static final String HEX_CHARS = "0123456789ABCDEF";

    // Internal master key used to encrypt BSA credentials at rest in PostgreSQL
    private static final String MASTER_KEY = "D3H56E89G52H5D678H95F3C5E78D9H39";
    private static final String MASTER_IV = "3916547354863579";

    private static final SecureRandom RANDOM = new SecureRandom();

    private BsaCryptoUtil() {
    }

    public static String generateStrongPassword(int length) {
        if (length < 16) {
            length = 16;
        }
        StringBuilder sb = new StringBuilder(length);

        // Ensure at least one of each category
        sb.append(CHAR_LOWER.charAt(RANDOM.nextInt(CHAR_LOWER.length())));
        sb.append(CHAR_UPPER.charAt(RANDOM.nextInt(CHAR_UPPER.length())));
        sb.append(NUMBER.charAt(RANDOM.nextInt(NUMBER.length())));
        sb.append(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));

        for (int i = 4; i < length; i++) {
            sb.append(ALL_CHARS.charAt(RANDOM.nextInt(ALL_CHARS.length())));
        }

        // Shuffle
        char[] chars = sb.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    public static String generateEncryptionKey(int length) {
        if (length <= 0) {
            length = 32; // 256 bits = 32 characters
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(HEX_CHARS.charAt(RANDOM.nextInt(HEX_CHARS.length())));
        }
        return sb.toString();
    }

    public static String generateAes256Key() {
        return generateEncryptionKey(32);
    }

    public static String generateInitializationVector(int length) {
        if (length <= 0) {
            length = 16; // 128 bits = 16 characters
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUMBER.charAt(RANDOM.nextInt(NUMBER.length())));
        }
        return sb.toString();
    }

    public static String generateIv16() {
        return generateInitializationVector(16);
    }

    public static String generateRandomHex(int length) {
        if (length <= 0) {
            length = 4;
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(HEX_CHARS.charAt(RANDOM.nextInt(HEX_CHARS.length())));
        }
        return sb.toString();
    }

    public static String encryptForStorage(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(MASTER_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(MASTER_IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to encrypt sensitive data for storage", ex);
        }
    }

    public static String encryptAes(String plainText) {
        return encryptForStorage(plainText);
    }

    public static String decryptFromStorage(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(MASTER_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(MASTER_IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to decrypt sensitive data from storage", ex);
        }
    }

    public static String decryptAes(String cipherText) {
        return decryptFromStorage(cipherText);
    }

    public static String maskPassword(String password) {
        return "********";
    }

    public static String maskSecret(String secret) {
        if (secret == null || secret.isEmpty()) {
            return "********";
        }
        if (secret.length() <= 6) {
            return "******";
        }
        String prefix = secret.substring(0, 2);
        String suffix = secret.substring(secret.length() - 2);
        return prefix + "*".repeat(secret.length() - 4) + suffix;
    }
}
