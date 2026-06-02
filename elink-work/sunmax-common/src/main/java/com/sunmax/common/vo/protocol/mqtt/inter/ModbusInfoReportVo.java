package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "ModbusInfoReportVo", description = "Modbus协议数据上报实体类")
public class ModbusInfoReportVo {

    private String chld;

    private Integer comType;

    private String driver;

    /**
     * 点表数组字符串列表
     */
    private List<ModbusInfoReportVo.PointVo> groupList = Lists.newArrayList();

    /**
     * 点表单个字段列表
     */
    private List<ModbusInfoReportVo.PointVo> pointList = Lists.newArrayList();

    @Data
    public static class PointVo {

        /**
         * 点号
         */
        private Integer pId;

        /**
         * 数值
         */
        private Object val;

        /**
         * 数值类型 0-无 1-遥测(浮点型) 2-遥信(整形) 3-遥控(暂无) 4-遥调(暂无)
         */
        private Integer appType;

    }

}
