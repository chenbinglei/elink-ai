package com.sunmax.common.util.local;

import com.google.common.collect.Maps;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.LocalParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@Slf4j
public class LocalFileUtil {


    /**
     * 单个图片上传
     *
     * @param fileList 多个文件
     * @return http访问路径
     */
    public static String checkList(List<MultipartFile> fileList) {
        String str = "";
        StringBuilder photoUrl = new StringBuilder();
        for (int i = 0; i < fileList.size(); i++) {
            str = checkFile(fileList.get(i));
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
     * @param file 文件类型
     * @return http访问路径
     */
    public static String checkFile(MultipartFile file) {
        StringBuilder filePath = new StringBuilder();

        File fileDirectory = new File(LocalParamVo.FILE_DIR);
        if (!fileDirectory.isDirectory()) {
            fileDirectory.mkdirs();
        }

        // 对上传的文件重命名，避免文件重名
        String oldName = file.getOriginalFilename();
        assert oldName != null;
        String newName = StringUtil.getUUID() + oldName.substring(oldName.lastIndexOf("."));
        try {
            // 文件保存
            file.transferTo(new File(fileDirectory, newName));
//            filePath.append("https:/").append(LocalParamVo.DOMAIN_NAME).append(FileUtil.COLON);
            filePath.append("http://").append(FileUtil.getInet4Address()).append(FileUtil.COLON);
            filePath.append(LocalParamVo.ACCESS_PORT).append(FileUtil.SLASH);
            filePath.append(LocalParamVo.PATH_PREFIX).append(FileUtil.SLASH).append(newName);
        } catch (IOException e) {
            log.error("上传文件失败", e);
        }
        return filePath.toString();
    }

    /**
     * 单个文件上传 只用于负荷预测文件上传
     *
     * @param file 文件类型
     * @return http访问路径
     */
    public static String uploadModelFile(MultipartFile file, String fileDir) {
        StringBuilder filePath = new StringBuilder();

        File fileDirectory = new File(fileDir);
        if (!fileDirectory.isDirectory()) {
            fileDirectory.mkdirs();
        }

        // 对上传的文件重命名，避免文件重名
        String oldName = file.getOriginalFilename();
        assert oldName != null;
        String newName = StringUtil.getUUID() + oldName.substring(oldName.lastIndexOf("."));
        try {
            // 文件保存
            file.transferTo(new File(fileDirectory, newName));
            filePath.append(fileDir).append(FileUtil.SLASH).append(newName);
        } catch (IOException e) {
            log.error("上传文件失败", e);
        }
        return filePath.toString();
    }

    /**
     * 根据文件名称删除单个文件
     */
    public static void deleteFile(String filePath) {
        if (StringUtil.isNotEmpty(filePath)) {
            FileSystemUtils.deleteRecursively(new File(LocalParamVo.FILE_DIR + FileUtil.SLASH + filePath.substring(filePath.lastIndexOf("/") + 1)));
        }
    }

    /**
     * 根据多个文件名称删除多张文件
     */
    public static void deleteAllFile(List<String> filePaths) {
        try {
            filePaths.forEach(LocalFileUtil::deleteFile);
        } catch (Exception e) {
            log.error("根据多个文件名称删除多个文件失败", e);
        }
    }

    /**
     * 解析文件内容
     */
    public static String parseFile(String filePath) {
        File file = new File(LocalParamVo.FILE_DIR + FileUtil.SLASH + filePath.substring(filePath.lastIndexOf("/") + 1));
        try {
            InputStreamReader inputStream = new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8);
            BufferedReader dataReader = new BufferedReader(inputStream);
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = dataReader.readLine()) != null) {
                result.append(line).append(System.lineSeparator());
            }
            inputStream.close();
            dataReader.close();
            return result.toString();
        } catch (Exception ex) {
            log.error("解析文件错误", ex);
            return null;
        }
    }

    /**
     * 解析文件内容
     */
    public static Map<String, String> parseFiles(List<String> filePaths) {
        Map<String, String> resultMap = Maps.newHashMap();
        filePaths.forEach(filePath -> {
            if (StringUtil.isNotEmpty(filePath)) {
                File file = new File(LocalParamVo.FILE_DIR + FileUtil.SLASH + filePath.substring(filePath.lastIndexOf("/") + 1));
                try {
                    InputStreamReader inputStream = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                    BufferedReader dataReader = new BufferedReader(inputStream);
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = dataReader.readLine()) != null) {
                        result.append(line).append(System.lineSeparator());
                    }
                    inputStream.close();
                    dataReader.close();
                    resultMap.put(filePath, result.toString());
                } catch (Exception ex) {
                    log.error("解析文件路径" + filePath + "错误", ex);
                    resultMap.put(filePath, null);
                }
            }
        });
        return resultMap;
    }

    /**
     * 读取文件
     *
     * @param filePath 文件路径
     * @return byte[]
     **/
    public static byte[] readFile(String filePath) {
        byte[] content = null;
        try {
            content = localRead(filePath);
            return content;
        } catch (Exception e) {
            log.error("读取文件失败", e);
            return content;
        }
    }

    public static byte[] localRead(String filePath) throws Exception {
        if (StringUtil.isNotEmpty(filePath)) {
            File file = new File(LocalParamVo.FILE_DIR + FileUtil.SLASH + filePath.substring(filePath.lastIndexOf("/") + 1));
            InputStream in = Files.newInputStream(file.toPath());
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            IOUtils.copyLarge(in, out);
            in.close();
            out.close();
            return out.toByteArray();
        }
        return null;
    }

    public static InputStream getFileInputStream(String filePath) {
        try {
            File file = new File(LocalParamVo.FILE_DIR + FileUtil.SLASH + filePath.substring(filePath.lastIndexOf("/") + 1));
            InputStream in = Files.newInputStream(file.toPath());
            in.close();
            return in;
        } catch (IOException e) {
            log.error("读取文件失败", e);
            return null;
        }
    }

}
