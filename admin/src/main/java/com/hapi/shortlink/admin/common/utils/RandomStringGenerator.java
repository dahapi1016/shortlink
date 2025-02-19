package com.hapi.shortlink.admin.common.utils;

import java.util.Random;

public class RandomStringGenerator {

    // 定义允许的字符集（字母和数字）
    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // 生成随机字符串的方法
    public static String generateRandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // 从字符集中随机选择一个字符
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    // 生成六位随机字符串
    public static String generateSixDigitRandomString() {
        return generateRandomString(6);
    }
}