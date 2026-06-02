package com.sunmax.common.util;

import com.sunmax.common.model.general.PileRealModel;
import jodd.net.URLDecoder;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static boolean isEmpty(Object str) {
        return str == null || "null".equals(str) || "".equals(str) || "\"\"".equals(str) || "[]".equals(str) || "{}".equals(str) || "undefined".equals(str);
    }

    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

    public static String replace(String inString, String oldPattern, String newPattern) {
        if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
            return inString;
        }
        int index = inString.indexOf(oldPattern);
        if (index == -1) {
            // no occurrence -> can return input as-is
            return inString;
        }

        int capacity = inString.length();
        if (newPattern.length() > oldPattern.length()) {
            capacity += 16;
        }
        StringBuilder sb = new StringBuilder(capacity);

        int pos = 0;  // our position in the old string
        int patLen = oldPattern.length();
        while (index >= 0) {
            sb.append(inString, pos, index);
            sb.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }

        // append any characters to the right of a match
        sb.append(inString.substring(pos));
        return sb.toString();
    }

    private static boolean hasLength(String str) {
        return (str != null && !str.isEmpty());
    }

    /**
     * 将一组数据固定分组，每组n个元素
     *
     * @param source 要分组的数据源
     * @param n      每组n个元素
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> fixedGrouping(List<T> source, int n) {

        if (null == source || source.size() == 0 || n <= 0)
            return null;
        List<List<T>> result = new ArrayList<List<T>>();

        int sourceSize = source.size();
        int size = (source.size() / n) + 1;
        for (int i = 0; i < size; i++) {
            List<T> subset = new ArrayList<T>();
            for (int j = i * n; j < (i + 1) * n; j++) {
                if (j < sourceSize) {
                    subset.add(source.get(j));
                }
            }
            result.add(subset);
        }
        return result;
    }

    /**
     * @Description:生成SYS_GUID,如：'167e7a68ef8457f6e05327fc930a438c'
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 把日期往后增加一天,整数  往后推,负数往前移动
     *
     * @param time String 类型时间 yyyy-MM-dd
     * @return
     */
    public static String endTime(String time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dd = null;
        try {
            dd = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dd);
        //加一天
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        String endTime = df.format(calendar.getTime());
        return endTime;
    }

    /**
     * 手机号验证
     *
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        boolean b = false;
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 字符串后面补F
     *
     * @param value  当前字符串
     * @param length 最后字符串长度
     * @return
     */
    public static String getToString(String value, int length) {
        String result;
        if (value.length() == length) {
            return value;
        }
        if (value.length() < length) {
            StringBuilder valueBuilder = new StringBuilder();
            for (int i = 0; i < length - value.length(); i++) {
                valueBuilder.append("F");
            }
            result = value + valueBuilder;
        } else {
            return value.substring(0, length);
        }
        return result;
    }

    /**
     * 获取随机字符串
     *
     * @param length 字符串长度
     */
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 转换充电枪状态
     *
     * @param workState
     * @return
     */
    public static String chargingGunStatus(int workState) {
        if (workState == 0) {
            return "空闲";
        } else if (workState == 1) {
            return "充电准备";
        } else if (workState == 2) {
            return "充电中";
        } else if (workState == 3) {
            return "充电完成";
        } else if (workState == 4) {
            return "放电准备";
        } else if (workState == 5) {
            return "放电中";
        } else if (workState == 6) {
            return "放电完成";
        } else if (workState == 7) {
            return "预约";
        } else if (workState == 255) {
            return "故障";
        }
        return "";
    }

    /**
     * 转换策略状态
     *
     * @return
     */
    public static String conversionStrategyStatus(int strategy) {
        if (strategy == 0) {
            return "满充/放空";
        } else if (strategy == 1) {
            return "定SOC";
        } else if (strategy == 2) {
            return "定金额";
        } else if (strategy == 3) {
            return "定电量";
        }
        return "";
    }

    /**
     * 转换充电桩状态
     *
     * @param workState 0 待机； 1 工作；2 维护；3 故障; 88 离线
     * @return
     */
    public static String chargingTypeConvert(int workState) {
        if (workState == 0 || workState == 1) {
            return "正常";
        } else if (workState == 2 || workState == 3) {
            return "故障";
        } else if (workState == 88) {
            return "离线";
        }
        return "";
    }

    /**
     * 对应协议中的电桩状态
     *
     * @param workState
     * @return
     */
    public static String chargingStateConvertProtocol(int workState) {
        if (workState == 0) {
            return "待机";
        } else if (workState == 1) {
            return "工作";
        } else if (workState == 2) {
            return "维护";
        } else if (workState == 3) {
            return "故障";
        } else if (workState == 88) {
            return "离线";
        }
        return "";
    }

    /**
     * 转换车辆连接状态
     *
     * @param vehicleConnectionState
     * @return
     */
    public static String vehicleConnectionState(int vehicleConnectionState) {
        if (vehicleConnectionState == 0) {
            return "未连接";
        } else if (vehicleConnectionState == 1) {
            return "半连接";
        } else if (vehicleConnectionState == 2) {
            return "已连接";
        }
        return "";
    }

    /**
     * 转换周活跃度类型
     *
     * @param weekActivityNum
     * @return
     */
    public static String convertWeekActivity(int weekActivityNum) {
        if (weekActivityNum == 0) {
            return "不活跃";
        } else if (weekActivityNum >= 1 && weekActivityNum < 3) {
            return "低活跃";
        } else if (weekActivityNum >= 3 && weekActivityNum < 7) {
            return "中活跃";
        } else if (weekActivityNum >= 7) {
            return "高活跃";
        }
        return "";
    }

    /**
     * 转换月活跃度类型
     *
     * @param monthActivityNum
     * @return
     */
    public static String convertMonthActivity(int monthActivityNum) {
        if (monthActivityNum == 0) {
            return "不活跃";
        } else if (monthActivityNum >= 1 && monthActivityNum < 10) {
            return "低活跃";
        } else if (monthActivityNum >= 10 && monthActivityNum < 20) {
            return "中活跃";
        } else if (monthActivityNum >= 20) {
            return "高活跃";
        }
        return "";
    }

    /**
     * 获取姓名全拼和首字母
     *
     * @param chinese 汉语名称
     * @return fullPinyin : 全拼        simplePinyin ： 首字母  groupPinyin：微信用户组第一个字母
     * @throws BadHanyuPinyinOutputFormatCombination
     * @author yqz
     */
    public static Map<String, String> changeChinesePinyin(String chinese) throws BadHanyuPinyinOutputFormatCombination {
        Map<String, String> pinyin = new HashMap<String, String>();

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        StringBuffer fullPinyin = new StringBuffer();
        StringBuffer simplePinyin = new StringBuffer();
        StringBuffer firstPinyin = new StringBuffer();

        char[] chineseChar = chinese.toCharArray();
        for (int i = 0; i < chineseChar.length; i++) {
            String[] str = null;
            try {
                str = PinyinHelper.toHanyuPinyinStringArray(chineseChar[i],
                        format);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
            if (str != null) {
                fullPinyin = fullPinyin.append(str[0]);
                simplePinyin = simplePinyin.append(str[0].charAt(0));

            }
            if (str == null) {
                String regex = "^[0-9]*[a-zA-Z]*+$";
                Pattern pattern = Pattern.compile(regex);
                Matcher m = pattern.matcher(String.valueOf(chineseChar[i]));
                if (m.find()) {
                    fullPinyin = fullPinyin.append(chineseChar[i]);
                    simplePinyin = simplePinyin.append(chineseChar[i]);
                }
            }
        }
        String[] name = PinyinHelper.toHanyuPinyinStringArray(chineseChar[0], format);
        firstPinyin = firstPinyin.append(name[0].charAt(0));
        pinyin.put("fullPinyin", fullPinyin.toString());
        pinyin.put("simplePinyin", simplePinyin.toString().toUpperCase());
        pinyin.put("groupPinyin", firstPinyin.toString().toUpperCase());
        return pinyin;
    }

    /**
     * 判断字符串是否为整数
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        return StringUtils.isNumeric(str);
    }

    /**
     * 判断字符串是否为浮点型(大于等于0)
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        return Pattern.compile("[0-9]*\\.?[0-9]+").matcher(str).matches();
    }

    /**
     * 判断字符串是否为数字 包含负数、整数、小数
     *
     * @param str
     * @return
     */
    public static boolean isNumericAll(String str) {
        return str.matches("^-?\\d+(\\.\\d+)?$");
    }

    // 校验字符串中是否含有非法字符
    public static Boolean legalStringCheck(String content) {
        String illegal = "`-~_!#%^&*=+\\|{};:'\",<>/?○�\u0001";
        StringBuilder stringBuilder = new StringBuilder();
        //L1:
        for (int i = 0; i < content.length(); i++) {
            for (int j = 0; j < illegal.length(); j++) {
                if (content.charAt(i) == illegal.charAt(j)) {
                    //有不符合项就退出循环
                    //break L1;
                    //提示出文件名称中有哪些不合规字符串
                    stringBuilder.append(content.charAt(i));
                }
            }
        }
        return StringUtil.isNotEmpty(stringBuilder.toString());
    }

    /**
     * 获取负荷预测表名
     *
     * @param localDate
     * @param suffix
     * @return
     */
    public static String getTableName(LocalDate localDate, Integer suffix) {
        return "b_predict" + localDate.format(DateTimeFormatter.ofPattern("yyMM")) + "_" + suffix;
    }

    /**
     * 转换电枪状态
     *
     * @param pileRealModel
     * @param gunCode
     * @return 枪状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
     */
    public static Integer convertGunStatus(PileRealModel pileRealModel, String gunCode) {
        //电枪状态
        int gunWorkState = 7;
        if (StringUtil.isNotEmpty(pileRealModel)) {
            int PileWorkStatus = pileRealModel.getWorkStatus();
            Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileRealModel.getGunRealModelMap();
            if (gunRealModelMap != null && !gunRealModelMap.isEmpty()) {
                PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(gunCode);
                if (StringUtil.isNotEmpty(gunRealModel)) {
                    //电枪状态
                    if (PileWorkStatus == 88) {
                        gunWorkState = 6;
                    } else {
                        int workState = gunRealModel.getGunStatus();
                        switch (workState) {
                            case -1:
                                gunWorkState = -1;
                                break;
                            case 0:
                                gunWorkState = 3;
                                break;
                            case 1:
                            case 2:
                                gunWorkState = 1;
                                break;
                            case 7:
                                gunWorkState = 8;
                                break;
                            case 3:
                            case 8:
                                gunWorkState = 4;
                                break;
                            case 4:
                            case 5:
                                gunWorkState = 2;
                                break;
                            case 88:
                                gunWorkState = 6;
                                break;
                            case 255:
                                gunWorkState = 5;
                                break;
                        }
                    }
                }
            }
        }
        return gunWorkState;
    }

    /**
     * 转换电枪状态
     *
     * @param pileRealModel
     * @param gunCode
     * @return 枪状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
     */
    public static Integer convertGunStatus(PileRealModel pileRealModel, Integer txStatus, String gunCode) {
        //电枪状态
        int gunWorkState = -1;
        //未注册
        if (StringUtil.isEmpty(txStatus) || txStatus == 0) {
            gunWorkState = 7;
            return gunWorkState;
        }
        //电桩离线
        if (StringUtil.isEmpty(txStatus) || txStatus == 88) {
            gunWorkState = 6;
            return gunWorkState;
        }
        if (StringUtil.isNotEmpty(pileRealModel) && StringUtil.isNotEmpty(pileRealModel.getWorkStatus())) {
            Integer workStatus = pileRealModel.getWorkStatus();
            Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileRealModel.getGunRealModelMap();
            if (gunRealModelMap != null && !gunRealModelMap.isEmpty()) {
                PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(gunCode);
                if (StringUtil.isNotEmpty(gunRealModel)) {
                    //电枪状态
                    if (workStatus == 88) {
                        gunWorkState = 6;
                    } else {
                        Integer workState = gunRealModel.getGunStatus();
                        if (StringUtil.isEmpty(workState)) {
                            return gunWorkState;
                        }
                        switch (workState) {
                            case -1:
                                break;
                            case 0:
                                gunWorkState = 3;
                                break;
                            case 1:
                            case 2:
                                gunWorkState = 1;
                                break;
                            case 7:
                                gunWorkState = 8;
                                break;
                            case 3:
                            case 8:
                                gunWorkState = 4;
                                break;
                            case 4:
                            case 5:
                                gunWorkState = 2;
                                break;
                            case 88:
                                gunWorkState = 6;
                                break;
                            case 255:
                                gunWorkState = 5;
                                break;
                        }
                    }
                }
            }
        }
        return gunWorkState;
    }

    /**
     * 转换站点状态-省市城市充电转换我们的站点状态
     *
     * @param stationStatus
     * @return
     */
    public static Integer getStationStatus(Integer stationStatus) {
        int status = 0;
        if (StringUtil.isNotEmpty(stationStatus)) {
            if (stationStatus == 50) {
                status = 1;
            } else if (stationStatus == 6) {
                status = 3;
            } else if (stationStatus == 5) {
                status = 2;
            } else if (stationStatus == 1) {
                status = 4;
            }
        }
        return status;
    }

    /**
     * 转换站点状态-我们转换成省市城市充电站点状态
     *
     * @param stationStatus
     * @return
     */
    public static Integer getStationStatusOther(Integer stationStatus) {
        int status = 0;
        if (StringUtil.isNotEmpty(stationStatus)) {
            if (stationStatus == 1) {
                status = 50;
            } else if (stationStatus == 3) {
                status = 6;
            } else if (stationStatus == 2) {
                status = 5;
            } else if (stationStatus == 4) {
                status = 1;
            }
        }
        return status;
    }

    /**
     * 转换互联互通设备资产类型
     *
     * @param equipmentType
     * @return
     */
    public static String getInterflowDeviceAssetType(Integer equipmentType) {
        String assetType = "";
        if (StringUtil.isNotEmpty(equipmentType)) {
            if (equipmentType == 1) {
                assetType = "29";
            } else if (equipmentType == 2) {
                assetType = "28";
            } else if (equipmentType == 3) {
                assetType = "30";
            }
        }
        return assetType;
    }

    /**
     * 转换本地设备资产类型
     *
     * @param deviceType 1-充电桩设备 2-通信设备 3-光伏设备 4-储能设备 5-计量设备 6-配电设备 7-感知设备 8-开关设备 9-视频监控设备 10-保护装置设备 11-负载设备 12-车位管理设备 13-换电设备
     * @return
     */
    public static List<Integer> getDeviceAssetType(Integer deviceType) {
        List<Integer> deviceTypeList = Lists.newArrayList();
        if (StringUtil.isNotEmpty(deviceType)) {
            if (deviceType == 1) {
                deviceTypeList = Arrays.asList(28, 29, 30);
            } else if (deviceType == 2) {
                deviceTypeList = Arrays.asList(31, 32);
            } else if (deviceType == 3) {
                deviceTypeList = Arrays.asList(20, 65, 66, 77);
            } else if (deviceType == 4) {
                deviceTypeList = Arrays.asList(23, 25, 60, 78);
            } else if (deviceType == 5) {
                deviceTypeList = Collections.singletonList(38);
            } else if (deviceType == 6) {
                deviceTypeList = Collections.singletonList(40);
            } else if (deviceType == 7) {
                deviceTypeList = Arrays.asList(35, 36);
            } else if (deviceType == 8) {
                deviceTypeList = Collections.singletonList(50);
            } else if (deviceType == 9) {
                deviceTypeList = Collections.singletonList(53);
            } else if (deviceType == 10) {
                deviceTypeList = Arrays.asList(54, 55);
            } else if (deviceType == 11) {
                deviceTypeList = Collections.singletonList(56);
            } else if (deviceType == 12) {
                deviceTypeList = Arrays.asList(58, 59);
            } else if (deviceType == 13) {
                deviceTypeList = Collections.singletonList(70);
            }
        }
        return deviceTypeList;
    }

    /**
     * 根据城市充电电枪工作状态转换本地电桩工作状态
     *
     * @param connectorStatus 1：空闲,2：占用（未充电）,3：占用（充电中）,4：占用（预约锁定）,255：故障
     * @return
     */
    public static Integer getInterflowPileWorkStatus(Integer connectorStatus) {
        int workStatus = -1;
        if (StringUtil.isNotEmpty(connectorStatus)) {
            if (connectorStatus == 1 || connectorStatus == 2 || connectorStatus == 3 || connectorStatus == 4) {
                workStatus = 1;
            } else if (connectorStatus == 255) {
                workStatus = 3;
            }
        }
        return workStatus;
    }

    /**
     * 根据城市充电电枪工作状态转换本地电桩工作状态
     *
     * @param connectorStatus 1：空闲,2：占用（未充电）,3：占用（充电中）,4：占用（预约锁定）,255：故障
     * @return 电桩原始状态 0-待机 1-工作 2-维护 3-故障 88-离线
     */
    public static Integer getInterflowPileOriginalStatus(Integer connectorStatus) {
        int workStatus = -1;
        if (StringUtil.isNotEmpty(connectorStatus)) {
            switch (connectorStatus) {
                case 1:
                case 2:
                case 4:
                    workStatus = 0;
                    break;
                case 3:
                    workStatus = 1;
                    break;
                case 255:
                    workStatus = 3;
                    break;

            }
        }
        return workStatus;
    }


    /**
     * 根据城市充电电枪工作状态转换本地电枪工作状态
     *
     * @param connectorStatus 0:离网； 1：空闲,2：占用（未充电）,3：占用（充电中）,4：占用（预约锁定）,255：故障
     * @return
     */
    public static Integer getInterflowGunWorkStatus(Integer connectorStatus) {
        int workStatus = -1;
        if (StringUtil.isNotEmpty(connectorStatus)) {
            if (connectorStatus == 1) {
                workStatus = 0;
            } else if (connectorStatus == 255) {
                workStatus = 255;
            } else if (connectorStatus == 2) {
                workStatus = 3;
            } else if (connectorStatus == 3) {
                workStatus = 2;
            } else if (connectorStatus == 4) {
                workStatus = 7;
            } else if (connectorStatus == 0) {
                workStatus = 88;
            }
        }
        return workStatus;
    }

    /**
     * 根据城市充电电枪工作状态转换本地电枪工作状态
     *
     * @param connectorStatus 0:离网； 1：空闲,2：占用（未充电）,3：占用（充电中）,4：占用（预约锁定）,255：故障
     * @return 充电枪原始状态 0-空闲 1-充电准备 2-充电中 3-充电完成 4-放电准备 5-放电中 6-放电完成 7-预约 8-暂停 88-离线 255-故障
     */
    public static Integer getInterflowGunOriginalStatus(Integer connectorStatus) {
        int workStatus = -1;
        if (StringUtil.isNotEmpty(connectorStatus)) {
            switch (connectorStatus) {
                case 0:
                    workStatus = 88;
                    break;
                case 1:
                case 2:
                    workStatus = 0;
                    break;
                case 3:
                    workStatus = 2;
                    break;
                case 4:
                    workStatus = 3;
                    break;
                case 255:
                    workStatus = 255;
                    break;
            }
        }
        return workStatus;
    }

    /**
     * 根据城市充电停止详细原因转换成文字
     *
     * @param stopReason 0：用户手动停止充电；
     *                   1：客户归属地运营商平台停止充电；
     *                   2： BMS 停止充电；
     *                   3：充电机设备故障；
     *                   4：连接器断开；
     * @return
     */
    public static String getInterflowStopReason(Integer stopReason) {
        String stopReasonStr = "";
        if (StringUtil.isNotEmpty(stopReason)) {
            if (stopReason == 0) {
                stopReasonStr = "用户手动停止充电";
            } else if (stopReason == 1) {
                stopReasonStr = "客户归属地运营商平台停止充电";
            } else if (stopReason == 2) {
                stopReasonStr = "BMS停止充电";
            } else if (stopReason == 3) {
                stopReasonStr = "充电机设备故障";
            } else if (stopReason == 4) {
                stopReasonStr = "连接器断开";
            } else if (stopReason > 5) {
                stopReasonStr = "未知情况";
            }
        }
        return stopReasonStr;
    }

    /**
     * 转换省市设备类型
     *
     * @param deviceType
     * @param cityType   1-省级 2-市级
     * @return
     */
    public static Integer getDeviceType(String deviceType, Integer cityType) {
        Integer equipmentType = 1;
        if (StringUtil.isNotEmpty(deviceType) && StringUtil.isNotEmpty(cityType)) {
            if ("28".equals(deviceType)) {//交流
                equipmentType = 2;
            } else if ("29".equals(deviceType)) {//直流
                equipmentType = 1;
            } else if ("30".equals(deviceType)) {//v2g
                if (cityType == 1) {
                    equipmentType = 6;
                } else {
                    equipmentType = 3;
                }
            }
        }
        return equipmentType;
    }

    /**
     * 根据本地电枪工作状态转换省市电枪工作状态
     *
     * @param gunStatus -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障
     * @return
     */
    public static Integer getCityGunWorkStatus(Integer gunStatus) {
        int status = -1;
        if (StringUtil.isNotEmpty(gunStatus)) {
            if (gunStatus == 0) {
                status = 1;
            } else if (gunStatus == 255) {
                status = 255;
            } else if (gunStatus == 3 || gunStatus == 1 || gunStatus == 8) {
                status = 2;
            } else if (gunStatus == 2) {
                status = 3;
            } else if (gunStatus == 7) {
                status = 4;
            } else if (gunStatus == 88) {
                status = 0;
            }
        }
        return status;
    }

    /**
     * 根据本地订单状态转换市级订单状态
     *
     * @param orderStatus 0-未进行 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 7-预约中
     * @return status 1：启动中；2：充电中；3：停止中；4：已结束；5：未知
     */
    public static Integer getCityOrderStatus(Integer orderStatus) {
        int status = 5;
        if (StringUtil.isNotEmpty(orderStatus)) {
            if (orderStatus == 1) {
                status = 2;
            } else if (orderStatus == 3 || orderStatus == 4 || orderStatus == 5) {
                status = 4;
            }
        }
        return status;
    }

    /**
     * 根据本地设备运营状态转换省级电枪运营状态
     *
     * @param operateStatus 0-未知 1-投运 2-检修 3-退役
     * @return status 0：未知 1：建设中 5：关闭下线 6：维护中 50：正常使用
     */
    public static Integer getProvinceOperateStatus(Integer operateStatus) {
        int status = 0;
        if (StringUtil.isNotEmpty(operateStatus)) {
            if (operateStatus == 1) {
                status = 50;
            } else if (operateStatus == 2) {
                status = 6;
            } else if (operateStatus == 3) {
                status = 5;
            }
        }
        return status;
    }

    public static Map<String, String> parseQueryString(String url) {
        Map<String, String> params = new HashMap<>();
        try {
            // 1. 使用 java.net.URI 来解析
            URI uri = new URI(url);

            // 2. 获取 Fragment 部分 (不包含 '#')
            String fragment = uri.getRawFragment(); // "/applet?mc=nlec&prot=ykc&No=1125100001846401"

            // 3. 提取查询字符串
            String queryString = fragment.split("\\?", 2)[1]; // "mc=nlec&prot=ykc&No=1125100001846401"
            if (queryString != null && !queryString.isEmpty()) {
                String[] pairs = queryString.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=", 2);
                    if (keyValue.length == 2) {
                        // 对值进行 URL 解码，防止乱码
                        String key = keyValue[0];
                        String value = URLDecoder.decode(keyValue[1], "UTF-8");
                        params.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

}
