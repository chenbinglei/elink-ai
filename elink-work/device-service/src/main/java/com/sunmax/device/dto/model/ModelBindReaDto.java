package com.sunmax.device.dto.model;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "ModelBindReaDto", description = "模型绑定扩展属性返回实体类")
public class ModelBindReaDto {

    /**
     * 选中数据列表
     */
    @ApiModelProperty(value = "选中列表")
    private List<ReaData> checkList = Lists.newArrayList();

    /**
     * 未选中数据列表
     */
    @ApiModelProperty(value = "未选中数据列表")
    private List<ReaData> uncheckList = Lists.newArrayList();

    @Data
    public static class ReaData {

        /**
         * 主键id
         */
        @ApiModelProperty(value = "主键id")
        private String id;

        /**
         * 关联资产分类id
         */
        @ApiModelProperty(value = "关联资产分类id")
        private String typeId;

        /**
         * 扩展属性名称
         */
        @ApiModelProperty(value = "扩展属性名称")
        private String reaName;

        /**
         * 字段名称
         */
        @ApiModelProperty(value = "字段名称")
        private String fieldName;

        /**
         * 扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-图片
         */
        @ApiModelProperty(value = "扩展属性类型 1-数值 2-文字 3-选项 4-位置 5-开关 6-时间 7-图片")
        private Integer reaType;

//        /**
//         * 读写类型 1-只读 2-读写
//         */
//        @ApiModelProperty(value = "读写类型 1-只读 2-读写")
//        private Integer readWriteType;

        /**
         * 是否必填 true-是 false-否
         */
        @ApiModelProperty(value = "是否必填 true-是 false-否")
        private Boolean required;

        /**
         * 默认值
         */
        @ApiModelProperty(value = "默认值")
        private String defaultValue;

    }

}
