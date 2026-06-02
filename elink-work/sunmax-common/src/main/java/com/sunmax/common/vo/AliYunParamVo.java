package com.sunmax.common.vo;


public class AliYunParamVo {

    public static final String BUCKET_NAME = System.getenv("ALIYUN_OSS_BUCKET_NAME");

    public static final String ACCESS_KEY_ID = System.getenv("ALIYUN_ACCESS_KEY_ID");

    public static final String ACCESS_KEY_SECRET = System.getenv("ALIYUN_ACCESS_KEY_SECRET");

    public static final String ENDPOINT = System.getenv("ALIYUN_OSS_ENDPOINT");

    /**
     * 文件存储路径
     */
    public static final String FILE_DIR = "sunos/file/";

    /**
     * 图片存储路径
     */
    public static final String IMAGER_DIR = "sunos/image/";

}
