package com.sunmax.common.util.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.google.common.collect.Maps;
import com.sunmax.common.vo.AliYunParamVo;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Configuration
public class OssFileUtil {

    protected static final Logger log = LoggerFactory.getLogger(OssFileUtil.class);

    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    public static String uploadFile(MultipartFile file) {
        if (file.getSize() > 1024 * 1024 * 20) {
            return "图片太大";//RestResultGenerator.createErrorResult(ResponseEnum.PHOTO_TOO_MAX);
        }
        String originalFilename = file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        Random random = new Random();
        String name = random.nextInt(10000) + System.currentTimeMillis() + substring;
        try {
            InputStream inputStream = file.getInputStream();
            uploadFile2(inputStream, name);
            return name;//RestResultGenerator.createSuccessResult(name);
        } catch (Exception e) {
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
    private static String uploadFile2(InputStream inStream, String fileName) {
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
            OSSClient ossClient = new OSSClient(AliYunParamVo.ENDPOINT, AliYunParamVo.ACCESS_KEY_ID, AliYunParamVo.ACCESS_KEY_SECRET);
            PutObjectResult putResult = ossClient.putObject(AliYunParamVo.BUCKET_NAME, AliYunParamVo.FILE_DIR + fileName, inStream, objectMetadata);
            ret = putResult.getETag();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
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
        if (FilenameExtension.equalsIgnoreCase(".jpeg") || FilenameExtension.equalsIgnoreCase(".jpg") ||
                FilenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpg";
        }
        if (FilenameExtension.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase(".svg")) {
            return "image/svg+xml";
        }
        if (FilenameExtension.equalsIgnoreCase(".txt") || FilenameExtension.equalsIgnoreCase(".fw")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase(".pptx") || FilenameExtension.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase(".docx") || FilenameExtension.equalsIgnoreCase(".doc")) {
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
            String url = getUrl(AliYunParamVo.FILE_DIR + split[split.length - 1]);
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
        OSSClient ossClient = new OSSClient(AliYunParamVo.ENDPOINT, AliYunParamVo.ACCESS_KEY_ID, AliYunParamVo.ACCESS_KEY_SECRET);
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
    public static String checkFileList(List<MultipartFile> fileList) {
        String fileUrl = "";
        String str = "";
        StringBuilder photoUrl = new StringBuilder();
        for (int i = 0; i < fileList.size(); i++) {
            fileUrl = uploadFile(fileList.get(i));
            str = getImgUrl(fileUrl);
            if (i == 0) {
                photoUrl = new StringBuilder(str);
            } else {
                photoUrl.append(",").append(str);
            }
        }
        return photoUrl.toString().trim();
    }

    /**
     * 单个文件上传
     *
     * @param file
     * @return
     */
    public static String checkFile(MultipartFile file) {
        String fileUrl = uploadFile(file);
        String str = getImgUrl(fileUrl);
        assert str != null;
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
     * 解析文件内容
     */
    public static String parseFile(String fileName) {
        OSSClient ossClient = new OSSClient(AliYunParamVo.ENDPOINT, AliYunParamVo.ACCESS_KEY_ID, AliYunParamVo.ACCESS_KEY_SECRET);
        try {
            OSSObject ossObj = ossClient.getObject(AliYunParamVo.BUCKET_NAME, AliYunParamVo.FILE_DIR + fileName);
            InputStream inputStream = ossObj.getObjectContent();
            BufferedReader dataReader = new BufferedReader(new InputStreamReader(inputStream));
            String result = new String(IOUtils.toByteArray(inputStream), StandardCharsets.UTF_8);
            dataReader.close();
            inputStream.close();
            ossClient.shutdown();
            return result;
        } catch (Exception ex) {
            log.error("解析文件错误",ex);
            ossClient.shutdown();
            return null;
        }
    }

    /**
     * 解析文件内容
     */
    public static Map<String, String> parseFiles(List<String> fileNames) {
        Map<String, String> resultMap = Maps.newHashMap();
        OSSClient ossClient = new OSSClient(AliYunParamVo.ENDPOINT, AliYunParamVo.ACCESS_KEY_ID, AliYunParamVo.ACCESS_KEY_SECRET);
        try {
            fileNames.forEach(fileName -> {
                try {
                    OSSObject ossObj = ossClient.getObject(AliYunParamVo.BUCKET_NAME, AliYunParamVo.FILE_DIR + fileName);
                    InputStream inputStream = ossObj.getObjectContent();
                    BufferedReader dataReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = dataReader.readLine()) != null) {
                        result.append(line).append(System.lineSeparator());
                    }
                    inputStream.close();
                    dataReader.close();
                    resultMap.put(fileName, result.toString());
                } catch (IOException e) {
                    log.error("解析"+fileName+"报错:", e);
                    resultMap.put(fileName, null);
                }
            });
            ossClient.shutdown();
        } catch (Exception ex) {
            log.error("解析文件错误",ex);
            ossClient.shutdown();
        }
        return resultMap;
    }

    /**
     * 根据文件名称删除单个文件
     */
    public static void deleteFile(String fileName) {
        try {
            OSSClient ossClient = new OSSClient(AliYunParamVo.ENDPOINT, AliYunParamVo.ACCESS_KEY_ID, AliYunParamVo.ACCESS_KEY_SECRET);
            ossClient.deleteObject(AliYunParamVo.BUCKET_NAME, AliYunParamVo.FILE_DIR + fileName);
            ossClient.shutdown();
        } catch (Exception e) {
            log.error("根据文件名称删除文件错误", e);
        }
    }

    /**
     * 根据多个文件名称删除多张图片
     */
    public static void deleteAllFile(List<String> fileNames) {
        try {
            OSSClient ossClient = new OSSClient(AliYunParamVo.ENDPOINT, AliYunParamVo.ACCESS_KEY_ID, AliYunParamVo.ACCESS_KEY_SECRET);
            fileNames.forEach(f->ossClient.deleteObject(AliYunParamVo.BUCKET_NAME, AliYunParamVo.FILE_DIR + f));
            ossClient.shutdown();
        } catch (Exception e) {
            log.error("根据多个文件名称删除多张图片", e);
        }
    }

    /**
     * 单个获取
     * @param ossFileName 阿里云文件名称
     * @return byte[]
     **/
    public static byte[] readFile(String ossFileName) {
        byte[] content = null;
        try {
            OSSClient ossClient = new OSSClient(AliYunParamVo.ENDPOINT, AliYunParamVo.ACCESS_KEY_ID, AliYunParamVo.ACCESS_KEY_SECRET);
            content = ossRead(ossClient, ossFileName);
            ossClient.shutdown();
            return content;
        } catch (Exception e) {
            log.error("读取文件失败", e);
            return content;
        }
    }

    public static byte[] ossRead(OSSClient ossClient, String ossFileName) throws Exception {
        OSSObject ossObject = ossClient.getObject(AliYunParamVo.BUCKET_NAME, AliYunParamVo.FILE_DIR + ossFileName);
        InputStream in = ossObject.getObjectContent();
        byte[] byteArray = IOUtils.toByteArray(in);
        in.close();
        return byteArray;
    }

    public static InputStream getFileInputStream(String ossFileName) {
        try {
            OSSClient ossClient = new OSSClient(AliYunParamVo.ENDPOINT, AliYunParamVo.ACCESS_KEY_ID, AliYunParamVo.ACCESS_KEY_SECRET);
            OSSObject ossObject = ossClient.getObject(AliYunParamVo.BUCKET_NAME, AliYunParamVo.FILE_DIR + ossFileName);
            InputStream in = ossObject.getObjectContent();
            ossClient.shutdown();
            return in;
        } catch (Exception e) {
            log.error("读取文件失败", e);
            return null;
        }
    }

    public static byte[] readAllBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }

    public static MultipartFile fileToMultipartFile(File file) throws IOException {
        // 读取文件内容
        byte[] content  = readAllBytes(file);// Java 11+，旧版本可用 ByteArrayOutputStream

        // 创建 MockMultipartFile
        // 参数: name, originalFilename, contentType, content
        return new MockMultipartFile(
                "file",                          // 表单字段名（如 <input name="file">）
                file.getName(),                  // 原始文件名
                "application/octet-stream",      // MIME 类型（可设为 null 或自动检测）
                content                          // 文件字节数组
        );
    }

    // 生成临时签名 URL（有效期 5 分钟）
    public static String generatePrivateFileUrl(String fileName) {

        // 使用专用 RAM 用户的 AK（权限最小化！）
        OSSClient ossClient = new OSSClient(AliYunParamVo.ENDPOINT, AliYunParamVo.ACCESS_KEY_ID, AliYunParamVo.ACCESS_KEY_SECRET);

        Date expiration = new Date(System.currentTimeMillis() + 5 * 60 * 1000); // 5分钟
        URL url = ossClient.generatePresignedUrl(
                AliYunParamVo.BUCKET_NAME,
                AliYunParamVo.FILE_DIR + fileName,
                expiration
        );
        ossClient.shutdown();
        return url.toString();
    }


}
