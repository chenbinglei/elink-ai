package com.sunmax.common.util.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.sunmax.common.vo.AliYunParamVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @Description: 阿里云OSS服务器工具类
 * @author:
 * @date:
 */
@Component
public class OssImageUtil {

    protected static final Logger log = LoggerFactory.getLogger(OssImageUtil.class);

    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    private static String uploadImg2Oss(MultipartFile file) {
        if (file.getSize() > 1024 * 1024 * 20) {
            return "图片太大";//RestResultGenerator.createErrorResult(ResponseEnum.PHOTO_TOO_MAX);
        }
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String substring = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        Random random = new Random();
        String name = random.nextInt(10000) + System.currentTimeMillis() + substring;
        try {
            InputStream inputStream = file.getInputStream();
            uploadImg2(inputStream, name);
            return name;//RestResultGenerator.createSuccessResult(name);
        } catch (IOException e) {
            return "上传失败";//RestResultGenerator.createErrorResult(ResponseEnum.PHOTO_UPLOAD);
        }
    }

    /**
     * 上传图片获取fileUrl
     *
     * @param inStream
     * @param fileName
     * @return
     */
    private static String uploadImg2(InputStream inStream, String fileName) {
        String ret = "";
        try {
            //创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(getcontentType(fileName.substring(fileName.lastIndexOf("."))));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            //上传文件
            OSS ossClient = new OSSClientBuilder().build(AliYunParamVo.ENDPOINT, AliYunParamVo.ACCESS_KEY_ID, AliYunParamVo.ACCESS_KEY_SECRET);
            PutObjectResult putResult = ossClient.putObject(AliYunParamVo.BUCKET_NAME, AliYunParamVo.IMAGER_DIR + fileName, inStream, objectMetadata);
            ret = putResult.getETag();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return ret;
    }

    public static String getcontentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase(".jpeg") ||
                FilenameExtension.equalsIgnoreCase(".jpg") ||
                FilenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpg";
        }
        if (FilenameExtension.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase(".txt") ||
                FilenameExtension.equalsIgnoreCase(".fw")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase(".pptx") ||
                FilenameExtension.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase(".docx") ||
                FilenameExtension.equalsIgnoreCase(".doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        if (FilenameExtension.equalsIgnoreCase(".mp3")) {
            return "application/ogg";
        }
        return "image/jpg";
    }

    /**
     * 获取图片路径
     *
     * @param fileUrl
     * @return
     */
    public static String getImgUrl(String fileUrl) {
        if (!StringUtils.isEmpty(fileUrl)) {
            String[] split = fileUrl.split("/");
            String url = getUrl(AliYunParamVo.IMAGER_DIR + split[split.length - 1]);
            return url;
        }
        return null;
    }

    /**
     * 获得url链接
     *
     * @param key
     * @return
     */
    public static String getUrl(String key) {
        // 设置URL过期时间为10年  3600l* 1000*24*365*10
        Date expiration = new Date(new Date().getTime() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        OSS ossClient = new OSSClientBuilder().build(AliYunParamVo.ENDPOINT, AliYunParamVo.ACCESS_KEY_ID, AliYunParamVo.ACCESS_KEY_SECRET);
        URL url = ossClient.generatePresignedUrl(AliYunParamVo.BUCKET_NAME, key, expiration);
        if (url != null) {
            return url.toString();
        }
        return null;
    }


    /**
     * 多图片上传
     *
     * @param fileList
     * @return
     */
    public static String checkList(List<MultipartFile> fileList) {
        String fileUrl = "";
        String str = "";
        StringBuilder photoUrl = new StringBuilder();
        for (int i = 0; i < fileList.size(); i++) {
            fileUrl = uploadImg2Oss(fileList.get(i));
            str = getImgUrl(fileUrl);
            if (i == 0) {
                assert str != null;
                photoUrl = new StringBuilder(str);
            } else {
                photoUrl.append(",").append(str);
            }
        }
        return photoUrl.toString().trim();
    }

    /**
     * 单个图片上传
     *
     * @param file
     * @return
     */
    public static String checkImage(MultipartFile file) {
        String fileUrl = uploadImg2Oss(file);
        String str = getImgUrl(fileUrl);
        return str.trim();
    }

    /**
     * 获取本地文件名称
     */
    public static String getFileNames(List<MultipartFile> files) {
        List<String> pictureNames = new ArrayList<>();
        files.forEach(p -> pictureNames.add(p.getOriginalFilename()));
        return String.join(",", pictureNames);
    }

    /**
     * 根据文件名称删除单个文件
     */
    public static void deleteImage(String fileName) {
        try {
            OSS ossClient = new OSSClientBuilder().build(AliYunParamVo.ENDPOINT, AliYunParamVo.ACCESS_KEY_ID, AliYunParamVo.ACCESS_KEY_SECRET);
            ossClient.deleteObject(AliYunParamVo.BUCKET_NAME, AliYunParamVo.IMAGER_DIR + fileName);
            ossClient.shutdown();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 根据多个文件名称删除多张图片
     */
    public static void deleteAllImage(List<String> fileNames) {
        try {
            OSS ossClient = new OSSClientBuilder().build(AliYunParamVo.ENDPOINT, AliYunParamVo.ACCESS_KEY_ID, AliYunParamVo.ACCESS_KEY_SECRET);
            fileNames.forEach(f -> ossClient.deleteObject(AliYunParamVo.BUCKET_NAME, AliYunParamVo.IMAGER_DIR + f));
            ossClient.shutdown();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}