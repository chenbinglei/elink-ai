package com.sunmax.common.util.oss;


import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.local.LocalFileUtil;
import com.sunmax.common.util.local.LocalImageUtil;
import com.sunmax.common.vo.LocalParamVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 文件工具类
 */
public class FileUtil {

    public static final String SLASH = "/";

    public static final String COMMA = ",";

    public static final String QUESTION = "?";

    public static final String separator = "";

    public static final String SPACE = " ";

    public static final String BAR = "-";

    public static final String POINT = ".";

    public static final String SLASH_POINT = "\\.";

    public static final String VERSION = "V";

    public static final String SEMICOLON = ";";

    public static final String COLON = ":";

    public static final String BARS = "--";

    public static final String LEFT_BRACKET = "(";

    public static final String RIGHT_BRACKET = ")";

    public static final String LEFT_BRACE = "{";

    public static final String RIGHT_BRACE = "}";

    public static final String ASTERISK = "*";

    public static final String LEFT_SQUARE = "[";

    public static final String RIGHT_SQUARE = "]";

    public static final String PLUS = "+";

    public static final String UNDERLINE = "_";

    public static final String EQUAL = "=";

    public static final String EIT = "@";

    /**
     * 截取中间的字符串
     *
     * @param name  字符串名称
     * @param start 截取最后一次出现的字符串中的字符
     * @param end   第一次出现的字符串中的字符
     */
    public static String subString(String name, String start, String end) {
        if (StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end)) {
            return name.substring(name.lastIndexOf(start) + 1, name.indexOf(end));
        } else {
            return null;
        }
    }

    /**
     * 截取中间的字符串
     *
     * @param stringList 多个字符串名称
     * @param start      截取最后一次出现的字符串中的字符
     * @param end        第一次出现的字符串中的字符
     */
    public static List<String> subList(List<String> stringList, String start, String end) {
        List<String> resultList = new ArrayList<>();
        if (stringList != null && !stringList.isEmpty()) {
            stringList.forEach(s -> resultList.add(s.substring(s.lastIndexOf(start) + 1, s.indexOf(end))));
        }
        return resultList;
    }

    /**
     * MultipartFile 转换为 File 文件
     *
     * @param multipartFile
     * @return
     */
    public static File transferToFile(MultipartFile multipartFile) {
        //选择用缓冲区来实现这个转换即使用java 创建的临时文件 使用 MultipartFile.transferto()方法 。
        File file = null;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            //获取文件后缀
            if (originalFilename != null) {
                String prefix = originalFilename.substring(originalFilename.lastIndexOf("."));
                file = File.createTempFile(originalFilename.substring(0, originalFilename.lastIndexOf(".")), prefix);    //创建临时文件
                //String[] filename = originalFilename.split("\\.");
                //file = File.createTempFile(filename[0], filename[1]);
                multipartFile.transferTo(file);
                //删除
                file.deleteOnExit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 获取本机网内地址
     */
    public static String getInet4Address() {
        try {
            //获取所有网络接口
            Enumeration<NetworkInterface> allNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            //遍历所有网络接口
            while (allNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = allNetworkInterfaces.nextElement();
                //如果此网络接口为 回环接口 或者 虚拟接口(子接口) 或者 未启用 或者 描述中包含VM
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp() || networkInterface.getDisplayName().contains("VM")) {
                    //继续下次循环
                    continue;
                }
                //遍历此接口下的所有IP（因为包括子网掩码各种信息）
                for (Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses(); inetAddressEnumeration.hasMoreElements(); ) {
                    InetAddress inetAddress = inetAddressEnumeration.nextElement();
                    //如果此IP不为空
                    if (inetAddress != null) {
                        //如果此IP为IPV4 则返回
                        if (inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress();
                        }

                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param len  文件长度
     * @param size 限制大小
     * @param unit 限制单位（B,K,M,G）
     * @描述 判断文件大小
     */
    public static boolean checkFileSize(Long len, int size, String unit) {
        double fileSize = 0;
        if ("B".equalsIgnoreCase(unit)) {
            fileSize = (double) len;
        } else if ("K".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1024;
        } else if ("M".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1048576;
        } else if ("G".equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1073741824;
        }
        return !(fileSize > size);
    }

    //获取新文件路径
    public static String getFilePath(MultipartFile file, String oldFilePath) {
        String result = null;
        if (file != null && !file.isEmpty()) {
            if (LocalParamVo.FILE_TYPE) { //文件类型
                if (StringUtil.isNotEmpty(oldFilePath)) { //删除阿里云上面的文件
                    OssFileUtil.deleteFile(FileUtil.subString(oldFilePath, FileUtil.SLASH, FileUtil.QUESTION));
                }
                result = OssFileUtil.checkFile(file);
            } else {
                if (StringUtil.isNotEmpty(oldFilePath)) { //删除本地服务器上面的文件
                    LocalFileUtil.deleteFile(oldFilePath);
                }
                result = LocalFileUtil.checkFile(file);
            }
        }
        return result;
    }

    //获取多个新文件路径
    public static String getFilePathList(List<MultipartFile> files, List<String> oldFilePaths) {
        String result = null;
        if (CollectionUtils.isNotEmpty(files)) {
            if (LocalParamVo.FILE_TYPE) { //文件类型
                if (CollectionUtils.isNotEmpty(oldFilePaths)) { //删除阿里云上面的文件
                    OssFileUtil.deleteAllFile(FileUtil.subList(oldFilePaths, FileUtil.SLASH, FileUtil.QUESTION));
                }
                result = OssFileUtil.checkFileList(files);
            } else {
                if (CollectionUtils.isNotEmpty(oldFilePaths)) { //删除本地服务器上面的文件
                    LocalFileUtil.deleteAllFile(oldFilePaths);
                }
                result = LocalFileUtil.checkList(files);
            }
        }
        return result;
    }

    public static String readFile(String filePath) {
        if (LocalParamVo.FILE_TYPE) {
            return new String(OssFileUtil.readFile(FileUtil.subString(filePath, FileUtil.SLASH, FileUtil.QUESTION)), StandardCharsets.UTF_8);
        } else {
            return new String(LocalFileUtil.readFile(filePath), StandardCharsets.UTF_8);
        }
    }

    public static byte[] readFileByte(String filePath) {
        if (LocalParamVo.FILE_TYPE) {
            return OssFileUtil.readFile(FileUtil.subString(filePath, FileUtil.SLASH, FileUtil.QUESTION));
        } else {
            return LocalFileUtil.readFile(filePath);
        }
    }

    public static InputStream readFileInputStream(String filePath) {
        if (LocalParamVo.FILE_TYPE) {
            return OssFileUtil.getFileInputStream(FileUtil.subString(filePath, FileUtil.SLASH, FileUtil.QUESTION));
        } else {
            return LocalFileUtil.getFileInputStream(filePath);
        }
    }

    public static void deleteFile(String filePath) {
        if (StringUtil.isNotEmpty(filePath)) {
            if (LocalParamVo.FILE_TYPE) { //文件类型
                OssFileUtil.deleteFile(FileUtil.subString(filePath, FileUtil.SLASH, FileUtil.QUESTION));
            } else {
                LocalFileUtil.deleteFile(filePath);
            }
        }
    }

    public static void deleteAllFile(List<String> filePaths) {
        if (CollectionUtils.isNotEmpty(filePaths)) {
            if (LocalParamVo.FILE_TYPE) { //文件类型
                OssFileUtil.deleteAllFile(FileUtil.subList(filePaths, FileUtil.SLASH, FileUtil.QUESTION));
            } else {
                LocalFileUtil.deleteAllFile(filePaths);
            }
        }
    }

    //获取新图片路径
    public static String getImagePath(MultipartFile image, String oldImagePath) {
        String result = null;
        if (image != null && !image.isEmpty()) {
            if (LocalParamVo.FILE_TYPE) { //文件类型
                if (StringUtil.isNotEmpty(oldImagePath)) { //删除阿里云上面的图片
                    OssImageUtil.deleteImage(FileUtil.subString(oldImagePath, FileUtil.SLASH, FileUtil.QUESTION));
                }
                result = OssImageUtil.checkImage(image);
            } else {
                if (StringUtil.isNotEmpty(oldImagePath)) { //删除本地服务器上面的图片
                    LocalImageUtil.deleteImage(oldImagePath);
                }
                result = LocalImageUtil.checkImage(image);
            }
        }
        return result;
    }

    //获取多个新图片路径
    public static String getImagePathList(List<MultipartFile> images, List<String> oldImagePaths) {
        String result = null;
        if (CollectionUtils.isNotEmpty(images)) {
            if (LocalParamVo.FILE_TYPE) { //文件类型
                if (CollectionUtils.isNotEmpty(oldImagePaths)) { //删除阿里云上面的图片
                    OssImageUtil.deleteAllImage(FileUtil.subList(oldImagePaths, FileUtil.SLASH, FileUtil.QUESTION));
                }
                result = OssImageUtil.checkList(images);
            } else {
                if (CollectionUtils.isNotEmpty(oldImagePaths)) { //删除本地服务器上面的图片
                    LocalImageUtil.deleteAllImage(oldImagePaths);
                }
                result = LocalImageUtil.checkList(images);
            }
        }
        return result;
    }

}
