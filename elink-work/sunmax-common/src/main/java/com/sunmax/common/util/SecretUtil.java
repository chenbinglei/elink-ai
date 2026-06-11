package com.sunmax.common.util;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.security.GeneralSecurityException;

/**
 * Author     ：tk-pc-40
 * ModifiedBy ：tk-pc-40
 * CreatedAt  ：2022/3/23
 */
@Slf4j
public class SecretUtil {

    /***
     * key和iv值需要和前端一致
     */
    public static final String KEY = "sunmaxkey0503000";

    public static final String IV = "sunmaxiv05030000";

    /**
     * 加密方法
     *
     * @param data 要加密的数据
     * @return 加密的结果
     */
    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException e) {
            return null;
        }
    }


    /**
     * 解密方法
     * 兼容标准Base64和URL-safe Base64（RFC 4648）格式
     * URL-safe Base64: - → +, _ → /, 自动补齐 = 填充
     *
     * @param data 要解密的数据（标准Base64或URL-safe Base64格式）
     * @return 解密的结果
     */
    public static String desEncrypt(String data) {
        try {
            // URL-safe Base64 还原为标准 Base64
            String standardBase64 = data.replace('-', '+').replace('_', '/');
            // 补齐 = 填充（Base64长度必须是4的倍数）
            int padding = (4 - standardBase64.length() % 4) % 4;
            if (padding > 0) {
                standardBase64 += "=".repeat(padding);
            }

            byte[] encrypted1 = Base64.getDecoder().decode(standardBase64);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original).trim();
        } catch (GeneralSecurityException e) {
//            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static void main(String[] args) {
        String encrypt = desEncrypt("9FlYo6cKxTFqp3U1WvDpeQ%3D%3D");
        log.info("{}", encrypt);
        log.info("{}", desEncrypt(encrypt));
        log.info("{}", Objects.equals("", desEncrypt(encrypt)));
    }

}
