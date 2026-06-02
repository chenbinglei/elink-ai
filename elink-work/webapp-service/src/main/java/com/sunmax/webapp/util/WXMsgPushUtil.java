package com.sunmax.webapp.util;

import java.security.MessageDigest;
import java.util.Arrays;

public class WXMsgPushUtil {

    /**
     * 用SHA1算法生成安全签名
     */
    public static String getSHA1(String... values) throws Exception {

        try {
            String[] array = new String[values.length];
            System.arraycopy(values, 0, array, 0, values.length);
            StringBuilder sb = new StringBuilder();
            // 字符串排序
            Arrays.sort(array);
            for (int i = 0; i < values.length; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            // SHA1签名生成
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();

            StringBuilder hexStr = new StringBuilder();
            String shaHex = "";
            for (byte b : digest) {
                shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    hexStr.append(0);
                }
                hexStr.append(shaHex);
            }
            return hexStr.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("SHA1加密失败");
        }

    }

}
