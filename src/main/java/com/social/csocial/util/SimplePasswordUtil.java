
package com.social.csocial.util;

import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class SimplePasswordUtil {

    /**
     * 简单的密码加密（SHA-256 + Base64）
     */
    public String encodePassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        String newEncodedPassword = encodePassword(rawPassword);
        return newEncodedPassword.equals(encodedPassword);
    }
}