package com.sunmax.protocol.util.platform;

import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;



@Slf4j
public class AESUtil {

    private static final String MODE = "CBC";

    private static final String PADDING = "PKCS5Padding";

    /**
     * 加密
     *
     * @param data 数据
     * @return 加密后的数据
     */
    public static String encrypt(String key, String iv, String data) {
        try {
            return new AES(MODE, PADDING, key.getBytes(), iv.getBytes()).encryptBase64(data);
        } catch (RuntimeException e) {
//            log.error("数据加密失败", e);
            return null;
        }
    }

    /**
     * 解密
     *
     * @param data 加密的数据
     * @return 解密后的数据
     */
    public static String desEncrypt(String key, String iv, String data) {
        try {
            return new AES(MODE, PADDING, key.getBytes(), iv.getBytes()).decryptStr(data);
        } catch (RuntimeException e) {
//            log.error("数据解密失败", e);
            return null;
        }
    }

    public static void main(String[] args) {
        log.info(AESUtil.encrypt("sunmaxkey0503000","sunmaxiv05030000", "wanneng123456"));
    }


}
