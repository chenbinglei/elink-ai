package com.sunmax.common.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.TreeCommonDto;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.StaticParamVo;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.jxls.common.Context;
import org.jxls.transform.Transformer;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 *
 * @author shanlingdai
 * @2019年10月10日
 */
public class CommonUtil {

    public static void exportExcel(HttpServletResponse response, String cnFileName, InputStream input,
                                   OutputStream output, List<?> dataList) throws IOException {
        cnFileName = URLEncoder.encode(cnFileName, StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;filename=" + cnFileName);
        response.setContentType("application/vnd.ms-excel");
        Map<String, Object> params = new HashMap<>();
        params.put("dataList", dataList);
        exportExcel(input, output, params);
        output.flush();
    }

    public static void exportExcel(InputStream is, OutputStream os, Map<String, Object> params) throws IOException {
        Context context = PoiTransformer.createInitialContext();
        if (MapUtils.isNotEmpty(params)) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                context.putVar(entry.getKey(), entry.getValue());
            }
        }
        JxlsHelper jxlsHelper = JxlsHelper.getInstance();
        Transformer transformer = jxlsHelper.createTransformer(is, os);

        // 必须要这个，否者表格函数统计会错乱
        jxlsHelper.setUseFastFormulaProcessor(false).processTemplate(context, transformer);
    }


    /**
     * 获取子节点数据
     */
    public static List<TreeCommonDto> setChild(TreeCommonDto parent, List<TreeCommonDto> childrenList) {
        //返回的集合
        List<TreeCommonDto> children = Lists.newArrayList();
        children.addAll(getChildren(parent.getId(), childrenList));
        return children;
    }

    private static List<TreeCommonDto> getChildren(String id, List<TreeCommonDto> childrenList) {
        List<TreeCommonDto> result = Lists.newArrayList();
        for (TreeCommonDto menu : childrenList) {
            if (id.equals(menu.getParentId())) {
                result.add(menu);
                //childrenList.remove(menu);
                result.addAll(getChildren(menu.getId(), childrenList));
            }
        }
        return result;
    }


    public static List<TreeCommonDto> setParent(TreeCommonDto children, List<TreeCommonDto> childrenList) {
        //返回所有父节点id
        List<TreeCommonDto> parent = new ArrayList<>();
        while (!children.getParentId().equals("0")) {
            for (TreeCommonDto menu : childrenList) {
                if (children.getParentId().equals(menu.getId())) {
                    //childrenList.remove(menu);
                    parent.add(menu);
                    children = menu;
                }
            }
        }
        return parent;
    }

    /**
     * 计算中心经纬度与目标经纬度的距离（米）
     *
     * @param centerLon 中心精度
     * @param centerLat 中心纬度
     * @param targetLon 需要计算的精度
     * @param targetLat 需要计算的纬度
     * @return 米
     */
    public static double distance(double centerLon, double centerLat, double targetLon, double targetLat) {

        double jl_jd = 102834.74258026089786013677476285;// 每经度单位米;
        double jl_wd = 111712.69150641055729984301412873;// 每纬度单位米;
        double b = Math.abs((centerLat - targetLat) * jl_jd);
        double a = Math.abs((centerLon - targetLon) * jl_wd);
        return Math.sqrt((a * a + b * b));
    }

    /**
     * 功能:获取单元格的值
     */
    public static String getCellValue(Cell cell) {
        String value = null;
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            value = null;
        } else {
            //判断数据类型
            switch (cell.getCellType()) {
                case FORMULA:
                    value = cell.getCellFormula();
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return com.sunmax.common.util.DateUtil.format(cell.getDateCellValue());
                    }
                    value = "" + cell.getNumericCellValue();
                    break;
                case STRING:
                    value = cell.getStringCellValue();
                    break;
                case BOOLEAN:
                    value = "" + cell.getBooleanCellValue();
                    break;
                default:
                    break;
            }
        }
        if (value != null && StringUtil.isNotEmpty(value)) {
            value = value.replace(".0", FileUtil.separator);
        }
        return value;
    }

    /**
     * 根据时间间隔和功率计算得出积分电量
     */
    public static Double getIntegralQt(int interval, Double power) {
        //间隔 / 60(小时分钟数) = 系数
        BigDecimal intervalBig = new BigDecimal(interval);
        BigDecimal hourBig = new BigDecimal(60);
        BigDecimal divide = intervalBig.divide(hourBig, 2, RoundingMode.HALF_UP);
        //功率 * 时间系数 = 积分电量
        BigDecimal powerBig = new BigDecimal(power);
        BigDecimal multiply = powerBig.multiply(divide);
        return multiply.doubleValue();
    }

    /**
     * 根据详细地址，获取省市县
     *
     * @param address
     * @return
     */
    public static Map<String, String> parseAddress(String address) {
        String regex = "(?<province>[^省]+省|[^市]+市|.*?自治区|.*?特别行政区)"
                + "(?<city>[^市]+市|.*?自治州|.*?地区|.*?盟)"
                + "(?<district>[^区]+区|[^县]+县|.*?旗|.*?自治县|.*?市)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(address);

        Map<String, String> dataMap = Maps.newHashMap();
        String province = null;
        String city = null;
        String district = null;
        if (matcher.find()) {
            province = matcher.group("province");
            city = matcher.group("city");
            district = matcher.group("district");
        }
        dataMap.put("province", province);
        dataMap.put("city", city);
        dataMap.put("county", district);
        return dataMap;
    }

    /**
     * 获取数据范围值
     *
     * @param valueRange 取值范围
     * @param dataType 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     * @param dataObject 数据对象
     * @param dataValue 数据值
     * @return 数据范围值
     */
    public static Object getRangeValue(String valueRange, Integer dataType, String dataObject, Object dataValue) {
        //校验是否在取值范围内 不在取值范围内 数据给空
        if (StringUtil.isNotEmpty(valueRange) && StringUtil.isNotEmpty(dataType) && StringUtil.isNotEmpty(dataValue)) {
            JSONObject valueRangeObj = JSON.parseObject(valueRange);
            if (valueRangeObj.containsKey(StaticParamVo.MIN_VALUE) && valueRangeObj.containsKey(StaticParamVo.MAX_VALUE)) {
                double minValue = valueRangeObj.getDoubleValue(StaticParamVo.MIN_VALUE);
                double maxValue = valueRangeObj.getDoubleValue(StaticParamVo.MAX_VALUE);
                //数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
                switch (dataType) {
                    case 1:
                    case 2:
                    case 5:
                        if (dataValue instanceof Integer) {
                            int value = (Integer) dataValue;
                            if (value < minValue || value > maxValue) {
                                return null;
                            }
                        }
                        break;
                    case 3:
                    case 4:
                        if (dataValue instanceof Double || dataValue instanceof Float) {
                            double value = Double.parseDouble(String.valueOf(dataValue));
                            if (value < minValue || value > maxValue) {
                                return null;
                            }
                        }
                        break;
                    case 8:
                        if (dataValue instanceof List && StringUtil.isNotEmpty(dataObject) && JSON.parseObject(dataObject).containsKey(StaticParamVo.ELEMENT_TYPE)) {
                            Integer elementType = JSON.parseObject(dataObject).getInteger(StaticParamVo.ELEMENT_TYPE);
                            switch (elementType) {
                                case 1:
                                case 2:
                                    List<Integer> intValueList = Lists.newArrayList();
                                    for (Integer value : JSON.parseArray(String.valueOf(dataValue), Integer.class)) {
                                        if (value < minValue || value > maxValue) {
                                            intValueList.add(null);
                                        } else {
                                            intValueList.add(value);
                                        }
                                    }
                                    return intValueList;
                                case 3:
                                case 4:
                                    List<Double> doubleValueList = Lists.newArrayList();
                                    for (Double value : JSON.parseArray(String.valueOf(dataValue), Double.class)) {
                                        if (value < minValue || value > maxValue) {
                                            doubleValueList.add(null);
                                        } else {
                                            doubleValueList.add(value);
                                        }
                                    }
                                    return doubleValueList;
                            }
                        }
                        break;
                }
            }
        }
        return dataValue;
    }

    /**
     * 获取数据时间值
     * @param dateTime 数据时间
     * @param dataType 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     * @param dataValue 数据值
     * @return 数据时间值
     */
    public static Object getDateTimeValue(String dateTime, Integer dataType, Object dataValue) {
        //获取当前时间15分钟之前的时间(多给两秒 以免出现时间差)
        LocalDateTime beforeTime = LocalDateTime.now().minusMinutes(15).minusSeconds(2);
        if (StringUtil.isNotEmpty(dateTime) && StringUtil.isNotEmpty(dataType) && StringUtil.isNotEmpty(dataValue)) {
            LocalDateTime localDateTime = com.sunmax.common.util.DateUtil.strToLocalDateTime(dateTime);
            if (localDateTime.isBefore(beforeTime)) {
                //数组置空
                if (dataType == 8) {
                    return Arrays.toString(new String[JSON.parseArray(String.valueOf(dataValue)).size()]);
                }
                return null;
            }
        }
        return dataValue;
    }

}
