package com.sunmax.configure.util;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.configure.dto.ConfigureResultDto;
import com.sunmax.configure.dto.DataResponseDto;
import com.sunmax.configure.vo.FieldTypeVo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DataHandleUtil {

    public static String getResponseData(String responseData) {
        // 在这里处理接收到的数据
        ConfigureResultDto configureResult = JSON.parseObject(JSON.toJSONString(JSON.parseObject(responseData, ResponseResult.class).getData()), ConfigureResultDto.class);
        if (configureResult != null && MapUtils.isNotEmpty(configureResult.getDataMap())) {
            Map<String, DataResponseDto> resultMap = Maps.newHashMap();
            configureResult.getDataMap().forEach((key, value) -> {
                DataResponseDto result = parseData(value);
                if (result != null) {
                    resultMap.put(key, parseData(value));
                }
            });
            return JSON.toJSONString(resultMap.values());
        }
        return null;
    }

    private static DataResponseDto parseData(ConfigureResultDto.FieldData data) {
        if (data != null && StringUtil.isNotEmpty(data.getFieldType())) {
            DataResponseDto result = new DataResponseDto();
            switch (data.getFieldType()) {
                case FieldTypeVo.STRING:
                case FieldTypeVo.INTEGER:
                case FieldTypeVo.DOUBLE:
                case FieldTypeVo.LONG:
                case FieldTypeVo.BOOLEAN:
                case FieldTypeVo.BIG_DECIMAL:
                case FieldTypeVo.ARRAY_MAP:
                case FieldTypeVo.CURVE_MAP:
                case FieldTypeVo.ARRAY_STRING:
                    result.setChName(data.getChName());
                    result.setEnName(data.getEnName());
                    result.setFieldType(data.getFieldType());
                    break;
                case FieldTypeVo.ARRAY_LIST:
                    result.setChName(data.getChName());
                    result.setEnName(data.getEnName());
                    result.setFieldType(data.getFieldType());
                    List<ConfigureResultDto.FieldData> dataList = JSON.parseArray(JSON.toJSONString(data.getFieldData()), ConfigureResultDto.FieldData.class);
                    List<DataResponseDto> childrenList = Lists.newArrayList();
                    dataList.forEach(item -> {
                        DataResponseDto dataResponse = parseData(item);
                        if (dataResponse != null) {
                            childrenList.add(dataResponse);
                        }
                    });
                    result.setChildren(childrenList);
                    break;
            }
            return result;
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(DataHandleUtil.getResponseData("{\"code\":20000,\"data\":{\"dataMap\":{\"2c99698b9503a6700195120023910060\":{\"chName\":\"交流01\",\"enName\":\"2c99698b9503a6700195120023910060\",\"fieldData\":[{\"chName\":\"通信状态\",\"enName\":\"txStatus\",\"fieldData\":1,\"fieldType\":\"Integer\"},{\"chName\":\"电桩内部温度\",\"enName\":\"piletemp\",\"fieldData\":6503.6,\"fieldType\":\"Double\"},{\"chName\":\"电桩总功率\",\"enName\":\"pilepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出电压\",\"enName\":\"gunvoltage\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"枪状态\",\"enName\":\"gunstatus\",\"fieldData\":\"[3]\",\"fieldDesc\":\"枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩放电功率\",\"enName\":\"piledischargepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出电流\",\"enName\":\"guncurrent\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩充电功率\",\"enName\":\"pilechargepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出功率\",\"enName\":\"gunpower\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩状态\",\"enName\":\"pilestatus\",\"fieldData\":1,\"fieldDesc\":\"电桩状态 -1-未知 1-在线 2-维护 3-故障 88-离线\",\"fieldType\":\"Integer\"}],\"fieldType\":\"ArrayList\"},\"2c99698b9503a6700195120023910061\":{\"chName\":\"交流02\",\"enName\":\"2c99698b9503a6700195120023910061\",\"fieldData\":[{\"chName\":\"通信状态\",\"enName\":\"txStatus\",\"fieldData\":1,\"fieldType\":\"Integer\"},{\"chName\":\"电桩内部温度\",\"enName\":\"piletemp\",\"fieldData\":6503.6,\"fieldType\":\"Double\"},{\"chName\":\"电桩总功率\",\"enName\":\"pilepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出电压\",\"enName\":\"gunvoltage\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"枪状态\",\"enName\":\"gunstatus\",\"fieldData\":\"[0]\",\"fieldDesc\":\"枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩放电功率\",\"enName\":\"piledischargepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出电流\",\"enName\":\"guncurrent\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩充电功率\",\"enName\":\"pilechargepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出功率\",\"enName\":\"gunpower\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩状态\",\"enName\":\"pilestatus\",\"fieldData\":1,\"fieldDesc\":\"电桩状态 -1-未知 1-在线 2-维护 3-故障 88-离线\",\"fieldType\":\"Integer\"}],\"fieldType\":\"ArrayList\"},\"2c99698b9503a6700195120023910062\":{\"chName\":\"交流03\",\"enName\":\"2c99698b9503a6700195120023910062\",\"fieldData\":[{\"chName\":\"通信状态\",\"enName\":\"txStatus\",\"fieldData\":1,\"fieldType\":\"Integer\"},{\"chName\":\"电桩内部温度\",\"enName\":\"piletemp\",\"fieldData\":6503.6,\"fieldType\":\"Double\"},{\"chName\":\"电桩总功率\",\"enName\":\"pilepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出电压\",\"enName\":\"gunvoltage\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"枪状态\",\"enName\":\"gunstatus\",\"fieldData\":\"[0]\",\"fieldDesc\":\"枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩放电功率\",\"enName\":\"piledischargepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出电流\",\"enName\":\"guncurrent\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩充电功率\",\"enName\":\"pilechargepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出功率\",\"enName\":\"gunpower\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩状态\",\"enName\":\"pilestatus\",\"fieldData\":1,\"fieldDesc\":\"电桩状态 -1-未知 1-在线 2-维护 3-故障 88-离线\",\"fieldType\":\"Integer\"}],\"fieldType\":\"ArrayList\"},\"2c99698b9503a6700195120023910063\":{\"chName\":\"交流04\",\"enName\":\"2c99698b9503a6700195120023910063\",\"fieldData\":[{\"chName\":\"通信状态\",\"enName\":\"txStatus\",\"fieldData\":1,\"fieldType\":\"Integer\"},{\"chName\":\"电桩内部温度\",\"enName\":\"piletemp\",\"fieldData\":6503.6,\"fieldType\":\"Double\"},{\"chName\":\"电桩总功率\",\"enName\":\"pilepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出电压\",\"enName\":\"gunvoltage\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"枪状态\",\"enName\":\"gunstatus\",\"fieldData\":\"[0]\",\"fieldDesc\":\"枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩放电功率\",\"enName\":\"piledischargepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出电流\",\"enName\":\"guncurrent\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩充电功率\",\"enName\":\"pilechargepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出功率\",\"enName\":\"gunpower\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩状态\",\"enName\":\"pilestatus\",\"fieldData\":1,\"fieldDesc\":\"电桩状态 -1-未知 1-在线 2-维护 3-故障 88-离线\",\"fieldType\":\"Integer\"}],\"fieldType\":\"ArrayList\"},\"2c99698b9503a6700195120023910064\":{\"chName\":\"交流05\",\"enName\":\"2c99698b9503a6700195120023910064\",\"fieldData\":[{\"chName\":\"通信状态\",\"enName\":\"txStatus\",\"fieldData\":1,\"fieldType\":\"Integer\"},{\"chName\":\"电桩内部温度\",\"enName\":\"piletemp\",\"fieldData\":6503.6,\"fieldType\":\"Double\"},{\"chName\":\"电桩总功率\",\"enName\":\"pilepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出电压\",\"enName\":\"gunvoltage\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"枪状态\",\"enName\":\"gunstatus\",\"fieldData\":\"[0]\",\"fieldDesc\":\"枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩放电功率\",\"enName\":\"piledischargepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出电流\",\"enName\":\"guncurrent\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩充电功率\",\"enName\":\"pilechargepower\",\"fieldData\":0,\"fieldType\":\"Double\"},{\"chName\":\"枪输出功率\",\"enName\":\"gunpower\",\"fieldData\":\"[0]\",\"fieldType\":\"ArrayString\"},{\"chName\":\"电桩状态\",\"enName\":\"pilestatus\",\"fieldData\":1,\"fieldDesc\":\"电桩状态 -1-未知 1-在线 2-维护 3-故障 88-离线\",\"fieldType\":\"Integer\"}],\"fieldType\":\"ArrayList\"}},\"desc\":\"sunos平台推送设备功能点数据接口\"},\"message\":\"操作成功\",\"success\":true}"));
    }

}
