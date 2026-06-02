package com.sunmax.common.dto.device;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "DevicePointDto", description = "设备点号返回实体类")
public class DevicePointDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    private String deviceNumber;

    /**
     * 枪编号列表数据
     */
    @ApiModelProperty(value = "枪编号列表数据")
    private List<String> gunCodeList = Lists.newArrayList();

    /**
     * 功能点点号数据
     * 数据点号 ->  功能点数据
     */
    @ApiModelProperty(value = "功能点点号数据")
    private Map<Long, FunctionPointData> functionPointDataMap = Maps.newHashMap();

    @Data
    public static class FunctionPointData {

        /**
         * 功能点标识
         */
        @ApiModelProperty(value = "功能点标识")
        private String functionLogo;

        /**
         * 功能点下标
         */
        @ApiModelProperty(value = "功能点下标")
        private Integer functionIndex;

        /**
         * 字段编号
         */
        @ApiModelProperty(value = "字段编号")
        private String fieldCode;

        /**
         * 字段类型
         */
        @ApiModelProperty(value = "字段类型 1-充电桩类型 2-充电枪类型")
        private Integer fieldType;

        /**
         * 数据点号
         */
        @ApiModelProperty(value = "数据点号")
        private Long dataId;

        /**
         * 系数
         */
        @ApiModelProperty(value = "系数")
        private Float coefficient;

        /**
         * 偏移量
         */
        @ApiModelProperty(value = "偏移量")
        private Integer offset;

    }

}
