package com.sunmax.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * Author     ：tk-pc-40
 * ModifiedBy ：tk-pc-40
 * CreatedAt  ：2022/3/23
 */
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
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 解密方法
     *
     * @param data 要解密的数据
     * @return 解密的结果
     */
    public static String desEncrypt(String data) {
        try {
            byte[] encrypted1 = Base64.getDecoder().decode(data);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original).trim();
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String encrypt = encrypt("tyweb1@2025");
        System.out.println(encrypt);
        System.out.println(desEncrypt(encrypt));
        System.out.println(Objects.equals("tyweb1@2025", desEncrypt(encrypt)));
    }

}
