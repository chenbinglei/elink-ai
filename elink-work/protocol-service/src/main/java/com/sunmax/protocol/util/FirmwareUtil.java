package com.sunmax.protocol.util;

import com.sunmax.common.util.local.LocalFileUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.util.oss.OssFileUtil;
import com.sunmax.common.vo.LocalParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class FirmwareUtil {

    //存储充电桩固件包数据块 充电桩编号 -> (固件包类型 -> 数据块)
    private static final Map<String, Map<Integer, List<byte[]>>> firmwareDataMap = new ConcurrentHashMap<>();

    /**
     * 获取充电桩固件包数据块
     * @param pilesCode  充电桩编号
     * @param deviceType 固件类型 1-V2G_1.0 TCP控制板 2-V2G_2.0 TCP控制板 3-V2G_3.0 TCP控制板 4-V2G_4.0 TPU控制板 5-V2G_4.0 CCU控制板 6-V2G_6.0 TCP控制板 7-V2G_7.0 TPU控制板 8-V2G_8.0 CCU控制板
     */
    public static List<byte[]> getFirmwareByPilesCode(String pilesCode, Integer deviceType) {
        if (firmwareDataMap.containsKey(pilesCode)) {
            return firmwareDataMap.get(pilesCode).get(deviceType);
        }
        return Lists.newArrayList();
    }

    /**
     * 清除当前充电桩固件包数据块
     * @param pilesCode 充电桩编号
     * @param deviceType 固件类型 1-V2G_1.0 TCP控制板 2-V2G_2.0 TCP控制板 3-V2G_3.0 TCP控制板 4-V2G_4.0 TPU控制板 5-V2G_4.0 CCU控制板 6-V2G_6.0 TCP控制板 7-V2G_7.0 TPU控制板 8-V2G_8.0 CCU控制板
     */
    public static void clearFirmwareByPilesCode(String pilesCode, Integer deviceType) {
        if (firmwareDataMap.containsKey(pilesCode)) {
            Map<Integer, List<byte[]>> pileDataMap = firmwareDataMap.get(pilesCode);
            pileDataMap.remove(deviceType);
            if (MapUtils.isEmpty(pileDataMap)) {
                firmwareDataMap.remove(pilesCode);
            }
        }
    }

    /**
     * 解析FW文件内容，拆分成数据块
     *
     * @param pilesCode     充电桩编号
     * @param deviceType 固件类型 1-V2G_1.0 TCP控制板 2-V2G_2.0 TCP控制板 3-V2G_3.0 TCP控制板 4-V2G_4.0 TPU控制板 5-V2G_4.0 CCU控制板 6-V2G_6.0 TCP控制板 7-V2G_7.0 TPU控制板 8-V2G_8.0 CCU控制板
     * @param firmwarePath  固件包路径
     * @param dataBlockNum  数据块标号
     * @param dataBlockSize 数据块大小
     * @return 该数据块标号对应的数据块
     */
    public static byte[] parseFile(String pilesCode, Integer deviceType, String firmwarePath, Integer dataBlockNum, Integer dataBlockSize) {
        //各个数据块字节
        List<byte[]> blockDataList = new ArrayList<>();
        //获取固件包所有字节
        byte[] firmwareBytes;
        if (LocalParamVo.FILE_TYPE) { //文件类型
            firmwareBytes = OssFileUtil.readFile(FileUtil.subString(firmwarePath, FileUtil.SLASH, FileUtil.QUESTION));
        } else {
            firmwareBytes = LocalFileUtil.readFile(firmwarePath);
        }
        //获取固件包需要发送的字节 (截取第32位之后的数据)
        byte[] dataBytes = Arrays.copyOfRange(firmwareBytes, 32, firmwareBytes.length);
        //获取该设备的数据包块数
        int size = (int) Math.ceil((float) dataBytes.length / dataBlockSize);
        for (int i = 0; i < size; i++) {
            byte[] bytes = Arrays.copyOfRange(dataBytes, i * dataBlockSize, (i + 1) * dataBlockSize);
            if (i == size - 1) {
                bytes = Arrays.copyOfRange(dataBytes, i * dataBlockSize, dataBytes.length);
            }
            blockDataList.add(bytes);
        }
        if (!blockDataList.isEmpty()) {
            if (firmwareDataMap.containsKey(pilesCode)) {
                firmwareDataMap.get(pilesCode).put(deviceType, blockDataList);
            } else {
                Map<Integer, List<byte[]>> map = new HashMap<>();
                map.put(deviceType, blockDataList);
                firmwareDataMap.put(pilesCode, map);
            }
            return blockDataList.get(dataBlockNum);
        }
        return null;
    }

}
