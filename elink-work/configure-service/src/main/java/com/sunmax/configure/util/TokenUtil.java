package com.sunmax.configure.util;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TokenUtil {

    private static final long EXPIRATION_TIME = 7200; // token有效期为7200秒

    private static final int TOKEN_LENGTH = 32;

    private static final SecureRandom random = new SecureRandom();

    private static final Map<String, TokenInfo> tokens = new HashMap<>();

    public static String generateToken(String userId) {
        StringBuilder sb = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            int randomNum = random.nextInt(62);
            char c;
            if (randomNum < 10) {
                c = (char) (randomNum + '0');
            } else if (randomNum < 36) {
                c = (char) (randomNum - 10 + 'a');
            } else {
                c = (char) (randomNum - 36 + 'A');
            }
            sb.append(c);
        }
        String token = sb.toString();
        TokenInfo tokenInfo = new TokenInfo(userId, System.currentTimeMillis() + EXPIRATION_TIME * 1000);
        tokens.put(token, tokenInfo);
        return token;
    }

    public static boolean validateToken(String token, String userId) {
        TokenInfo tokenInfo = tokens.get(token);
        return tokenInfo != null && tokenInfo.getUserId().equals(userId) && System.currentTimeMillis() < tokenInfo.getExpirationTime();
    }

    private static class TokenInfo {
        private final String userId;
        private final long expirationTime;

        public TokenInfo(String userId, long expirationTime) {
            this.userId = userId;
            this.expirationTime = expirationTime;
        }

        public String getUserId() {
            return userId;
        }

        public long getExpirationTime() {
            return expirationTime;
        }
    }

    public static void main(String[] args) {
        String token = generateToken("002485048");
        log.info(token);
        log.info("{}", validateToken(token, "002485048"));
    }

}
