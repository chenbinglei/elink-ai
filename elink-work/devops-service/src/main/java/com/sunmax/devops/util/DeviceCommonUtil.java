package com.sunmax.devops.util;

import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.FunctionLogoParamVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.devops.dto.SiteGwCurveDataDto;
import com.sunmax.devops.dto.SitePileStaticDataDto;
import com.sunmax.devops.dto.SiteSeCurveDataDto;
import com.sunmax.devops.service.feign.DataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DeviceCommonUtil {

    /**
     * 获取设备功率曲线(分钟级)
     *
     * @param deviceIds    多个设备id
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param functionLogo 功能点标识
     * @param dataService  数据服务接口
     */
    public static <T> void getDevicePowerCurve(Set<String> deviceIds, String startTime, String endTime, String functionLogo, DataService dataService, T result) {
        // 参数校验
        if (CollectionUtils.isEmpty(deviceIds) || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)) {
            log.error("Invalid input parameters.");
        }

        // 解析时间并校验格式
        LocalDateTime localDateTime;
        try {
            localDateTime = DateUtil.strToLocalDateTime(startTime).minusDays(1);
        } catch (RuntimeException e) {
            log.error("Invalid date format for startTime: {}", startTime, e);
            return;
        }
        String todayDate = startTime.substring(0, 10);
        String yestTodayDate = DateUtil.localDateToStr(localDateTime.toLocalDate());

        // 获取时间列表
        List<String> dateTimeList = DateUtil.getDateBetweenMinutes(DateUtil.strToLocalDateTime(startTime), DateUtil.strToLocalDateTime(endTime), 5)
                .stream()
                .map(c -> c.substring(11, 16))
                .collect(Collectors.toList());

        // 查询历史数据
        DeviceHistoryQueryVo pilePowerQueryVo = new DeviceHistoryQueryVo();
        pilePowerQueryVo.setDeviceIds(deviceIds);
        pilePowerQueryVo.setFunctionLogos(Collections.singleton(functionLogo));
        pilePowerQueryVo.setStartTime(DateUtil.localDateTimeToStr(localDateTime));
        pilePowerQueryVo.setEndTime(endTime);
        pilePowerQueryVo.setTimeInterval("5m");

        // 安全获取功率数据映射
        Map<String, Double> powerDataMap = Optional.ofNullable(dataService.findDeviceHistoryValueList(pilePowerQueryVo))
                .map(ResponseResult::getData)
                .orElse(Collections.emptyMap())
                .values().stream()
                .flatMap(i -> i.values().stream().flatMap(Collection::stream))
                .filter(i -> StringUtil.isNotEmpty(i.getDataValue()))
                .collect(Collectors.groupingBy(
                        DeviceHistoryDto::getDateTime,
                        Collectors.summingDouble(i -> Double.parseDouble(String.valueOf(i.getDataValue())))
                ));

        // 定义字段名常量
        final String TIME_LIST_FIELD = "timeList";
        final String CURVE_1_LIST_FIELD = "curve1List";
        final String CURVE_2_LIST_FIELD = "curve2List";

        try {
            // 反射获取字段并填充数据
            Field timeListField = result.getClass().getDeclaredField(TIME_LIST_FIELD);
            Field curve1ListField = result.getClass().getDeclaredField(CURVE_1_LIST_FIELD);
            Field curve2ListField = result.getClass().getDeclaredField(CURVE_2_LIST_FIELD);

            timeListField.setAccessible(true);
            curve1ListField.setAccessible(true);
            curve2ListField.setAccessible(true);

            List<String> timeList = (List<String>) timeListField.get(result);
            List<Double> curve1List = (List<Double>) curve1ListField.get(result);
            List<Double> curve2List = (List<Double>) curve2ListField.get(result);

            // 统一处理不同类型的结果对象
            for (String dateTime : dateTimeList) {
                String todayTime = todayDate + FileUtil.SPACE + dateTime + ":00";
                String yesterdayTime = yestTodayDate + FileUtil.SPACE + dateTime + ":00";

                // 填充曲线数据通用方法
                timeList.add(dateTime);
                if (powerDataMap.containsKey(yesterdayTime)) {
                    curve1List.add(DoubleUtil.getToDouble(powerDataMap.get(yesterdayTime)));
                } else {
                    curve1List.add(null);
                }
                if (powerDataMap.containsKey(todayTime)) {
                    curve2List.add(DoubleUtil.getToDouble(powerDataMap.get(todayTime)));
                } else {
                    curve2List.add(null);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Failed to access or modify fields in result object.", e);
        } catch (RuntimeException e) {
            log.error("Unexpected error during curve data population.", e);
        }
    }

    /**
     * 校验日期格式是否合法
     *
     * @param dateStr 日期字符串
     * @return 合法返回true，否则返回false
     */
    public static boolean isValidDateFormat(String dateStr) {
        try {
            // 根据实际日期格式要求进行校验，例如：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDate.parse(dateStr, formatter);
            return false;
        } catch (DateTimeException e) {
            return true;
        }
    }

    /**
     * 处理电表电量 (反射通用版 - 适配字段差异)
     *
     * @param kwhMap  电量数据
     * @param isCurve 是否统计曲线 true-是 false-否
     */
    public static <T> void handleMeterQt(Map<String, Double> kwhMap, T result, Boolean isCurve) {
        // 1. 校验类型：必须是 SiteGwCurveDataDto 或 SiteSeCurveDataDto 之一
        // 【重要修复】原代码 (!(A) || !(B)) 恒为 true，导致直接 return。
        // 逻辑应为：既不是 A 也 不是 B 时，才报错。
        if (!(result instanceof SiteGwCurveDataDto) && !(result instanceof SiteSeCurveDataDto)) {
            log.error("结果对象类型不匹配: {}", result == null ? "null" : result.getClass().getName());
            return;
        }

        try {
            Class<?> clazz = result.getClass();

            // --- 第一步：读取当前值 (通过反射获取字段) ---
            // 即使字段是 private，setAccessible(true) 也能获取
            double supKwh = getDoubleValue(result, clazz, "supKwh");
            double topSupKwh = getDoubleValue(result, clazz, "topSupKwh");
            double peakSupKwh = getDoubleValue(result, clazz, "peakSupKwh");
            double plainSupKwh = getDoubleValue(result, clazz, "plainSupKwh");
            double valleySupKwh = getDoubleValue(result, clazz, "valleySupKwh");
            double deepSupKwh = getDoubleValue(result, clazz, "deepSupKwh");

            double revKwh = getDoubleValue(result, clazz, "revKwh");
            double topRevKwh = getDoubleValue(result, clazz, "topRevKwh");
            double peakRevKwh = getDoubleValue(result, clazz, "peakRevKwh");
            double plainRevKwh = getDoubleValue(result, clazz, "plainRevKwh");
            double valleyRevKwh = getDoubleValue(result, clazz, "valleyRevKwh");
            double deepRevKwh = getDoubleValue(result, clazz, "deepRevKwh");

            // --- 第二步：累加计算 (供能 Sup) ---
            // 【优化】移除 StringUtil.isNotEmpty 对 Double 的错误使用，直接判 null
            double dateSupKwh = 0.0;
            if (kwhMap.containsKey(FunctionLogoParamVo.TOP_SUP_KWH)) {
                Double val = kwhMap.get(FunctionLogoParamVo.TOP_SUP_KWH);
                if (val != null) {
                    topSupKwh += val;
                    supKwh += val;
                    dateSupKwh += val;
                }
            }
            if (kwhMap.containsKey(FunctionLogoParamVo.PEAK_SUP_KWH)) {
                Double val = kwhMap.get(FunctionLogoParamVo.PEAK_SUP_KWH);
                if (val != null) {
                    peakSupKwh += val;
                    supKwh += val;
                    dateSupKwh += val;
                }
            }
            if (kwhMap.containsKey(FunctionLogoParamVo.PLAIN_SUP_KWH)) {
                Double val = kwhMap.get(FunctionLogoParamVo.PLAIN_SUP_KWH);
                if (val != null) {
                    plainSupKwh += val;
                    supKwh += val;
                    dateSupKwh += val;
                }
            }
            if (kwhMap.containsKey(FunctionLogoParamVo.VALLEY_SUP_KWH)) {
                Double val = kwhMap.get(FunctionLogoParamVo.VALLEY_SUP_KWH);
                if (val != null) {
                    valleySupKwh += val;
                    supKwh += val;
                    dateSupKwh += val;
                }
            }
            if (kwhMap.containsKey(FunctionLogoParamVo.DEEP_SUP_KWH)) {
                Double val = kwhMap.get(FunctionLogoParamVo.DEEP_SUP_KWH);
                if (val != null) {
                    deepSupKwh += val;
                    supKwh += val;
                    dateSupKwh += val;
                }
            }

            // --- 第三步：写回数据 (供能 Sup) ---
            setDoubleValue(result, clazz, "supKwh", supKwh);
            setDoubleValue(result, clazz, "topSupKwh", topSupKwh);
            setDoubleValue(result, clazz, "peakSupKwh", peakSupKwh);
            setDoubleValue(result, clazz, "plainSupKwh", plainSupKwh);
            setDoubleValue(result, clazz, "valleySupKwh", valleySupKwh);
            setDoubleValue(result, clazz, "deepSupKwh", deepSupKwh);


            // --- 第四步：累加计算 (受能 Rev) ---
            double dateRevKwh = 0.0;
            if (kwhMap.containsKey(FunctionLogoParamVo.TOP_REV_KWH)) {
                Double val = kwhMap.get(FunctionLogoParamVo.TOP_REV_KWH);
                if (val != null) {
                    topRevKwh += val;
                    revKwh += val;
                    dateRevKwh += val;
                }
            }
            if (kwhMap.containsKey(FunctionLogoParamVo.PEAK_REV_KWH)) {
                Double val = kwhMap.get(FunctionLogoParamVo.PEAK_REV_KWH);
                if (val != null) {
                    peakRevKwh += val;
                    revKwh += val;
                    dateRevKwh += val;
                }
            }
            if (kwhMap.containsKey(FunctionLogoParamVo.PLAIN_REV_KWH)) {
                Double val = kwhMap.get(FunctionLogoParamVo.PLAIN_REV_KWH);
                if (val != null) {
                    plainRevKwh += val;
                    revKwh += val;
                    dateRevKwh += val;
                }
            }
            if (kwhMap.containsKey(FunctionLogoParamVo.VALLEY_REV_KWH)) {
                Double val = kwhMap.get(FunctionLogoParamVo.VALLEY_REV_KWH);
                if (val != null) {
                    valleyRevKwh += val;
                    revKwh += val;
                    dateRevKwh += val;
                }
            }
            if (kwhMap.containsKey(FunctionLogoParamVo.DEEP_REV_KWH)) {
                Double val = kwhMap.get(FunctionLogoParamVo.DEEP_REV_KWH);
                if (val != null) {
                    deepRevKwh += val;
                    revKwh += val;
                    dateRevKwh += val;
                }
            }

            // --- 第五步：写回数据 (受能 Rev) ---
            setDoubleValue(result, clazz, "revKwh", revKwh);
            setDoubleValue(result, clazz, "topRevKwh", topRevKwh);
            setDoubleValue(result, clazz, "peakRevKwh", peakRevKwh);
            setDoubleValue(result, clazz, "plainRevKwh", plainRevKwh);
            setDoubleValue(result, clazz, "valleyRevKwh", valleyRevKwh);
            setDoubleValue(result, clazz, "deepRevKwh", deepRevKwh);

            // 【关键适配】尝试添加 Curve1List
            // 【关键适配】尝试添加 Curve2List
            // 如果类中没有 curve1List 方法，此方法会自动跳过，不报错
            if (isCurve) {
                invokeListAddIfExist(result, clazz, "getCurve1List", dateSupKwh);
                invokeListAddIfExist(result, clazz, "getCurve2List", dateRevKwh);
            }

        } catch (Exception e) {
            log.error("处理电表尖峰平谷深电量失败：反射异常", e);
        }
    }

    // ================= 反射工具方法 =================

    /**
     * 安全获取 Double 字段值，若为 null 或字段不存在则返回 0.0
     */
    public static Double getDoubleValue(Object obj, Class<?> clazz, String fieldName) throws Exception {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Double value = (Double) field.get(obj);
            return value == null ? 0.0 : value;
        } catch (NoSuchFieldException e) {
            return 0.0;
        }
    }

    /**
     * 设置 Double 字段值
     */
    public static void setDoubleValue(Object obj, Class<?> clazz, String fieldName, double value) throws Exception {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, DoubleUtil.getToDouble(value));
        } catch (NoSuchFieldException e) {
            log.warn("反射设置字段失败，字段不存在: {}", fieldName);
        }
    }

    /**
     * 【核心适配方法】
     * 尝试调用 Getter 获取 List 并添加元素。
     * 如果该方法不存在 (NoSuchMethodException)，则安静地跳过，适用于 SiteSeCurveDataDto。
     */
    public static void invokeListAddIfExist(Object obj, Class<?> clazz, String getterName, double value) {
        try {
            // 尝试获取方法
            Method method = clazz.getMethod(getterName);

            // .invoke 可能会抛出异常，但我们在 catch 中处理
            List<Double> list = (List<Double>) method.invoke(obj);

            if (list != null) {
                list.add(DoubleUtil.getToDouble(value));
            } else {
                // 方法存在但返回 null，记录警告但不中断
                log.debug("列表字段为空，跳过添加: {}.{}", clazz.getSimpleName(), getterName);
            }
        } catch (NoSuchMethodException e) {
            // 【关键点】：如果类中没有这个方法 (如 SiteSeCurveDataDto)，直接捕获并忽略，不报错
            // log.debug("类 {} 不包含方法 {}, 跳过处理", clazz.getSimpleName(), getterName);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.warn("反射调用列表添加失败: {}.{}", clazz.getSimpleName(), getterName, e);
        }
    }

    /**
     * 获取超充枪状态
     *
     * @param txStatus     通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     * @param gunState     枪原始状态 0-空闲 1-准备中 2-充电中 3-充电结束 4-启动失败 5-预约中 6-故障
     * @param vehicleState 车辆连接状态 0-断开 1-半连接 2-已连接
     */
    public static void getSuperPileGunStatus(Integer txStatus, Integer gunState, Integer vehicleState, SitePileStaticDataDto result) {
        //枪状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
        if (StringUtil.isEmpty(txStatus)) {
            result.setOther(result.getOther() + 1);
            return;
        }
        switch (txStatus) {
            case 1:
                //枪原始状态 0-空闲 1-准备中 2-充电中 3-充电结束 4-启动失败 5-预约中 6-故障
                if (StringUtil.isEmpty(gunState)) {
                    result.setOther(result.getOther() + 1);
                    return;
                }
                switch (gunState) {
                    case 0:
                    case 3:
                    case 4:
                        if (StringUtil.isEmpty(vehicleState)) {
                            result.setIdle(result.getIdle() + 1);
                            return;
                        }
                        if (vehicleState == 0 || vehicleState == 1) {
                            result.setIdle(result.getIdle() + 1);
                            return;
                        } else if (vehicleState == 2) {
                            result.setEmploy(result.getEmploy() + 1);
                            return;
                        }
                        result.setIdle(result.getIdle() + 1);
                        return;
                    case 1:
                    case 2:
                        result.setCharge(result.getCharge() + 1);
                        return;
                    case 5:
                    case 6:
                    default:
                        result.setOther(result.getOther() + 1);
                        return;
                }
            case 2:
            case 3:
            case 88:
            case 0:
            default:
                result.setOther(result.getOther() + 1);
        }
    }

}
