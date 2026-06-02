package com.sunmax.device.dto.model;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "ModelBindFunctionDto", description = "模型绑定标准功能列表返回实体类")
public class ModelBindFunctionDto {

    /**
     * 选中数据列表
     */
    @ApiModelProperty(value = "选中列表")
    private List<FunctionData> checkList = Lists.newArrayList();

    /**
     * 未选中数据列表
     */
    @ApiModelProperty(value = "未选中数据列表")
    private List<FunctionData> uncheckList = Lists.newArrayList();

    @Data
    public static class FunctionData {

        /**
         * 主键id
         */
        @ApiModelProperty(value = "主键id")
        private String id;

        /**
         * 功能名称
         */
        @ApiModelProperty(value = "功能名称")
        private String functionName;

        /**
         * 功能标识
         */
        @ApiModelProperty(value = "功能标识")
        private String functionLogo;

        /**
         * 功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调
         */
        @ApiModelProperty(value = "功能类型 1-遥测 2-遥信 3-遥脉 4-遥控 5-遥调")
        private Integer functionType;

        /**
         * 数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
         */
        @ApiModelProperty(value = "数据类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)", required = true)
        private Integer dataType;

        /**
         * 单位
         */
        @ApiModelProperty(value = "单位")
        private String unit;

        /**
         * 序列号
         */
        @ApiModelProperty(value = "序列号")
        private Integer serialNum;

    }

}
