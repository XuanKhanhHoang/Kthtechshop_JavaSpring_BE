package com.kth.kthtechshop.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final int HASH_ROUNDS = 12;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(HASH_ROUNDS);

    public static String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}
