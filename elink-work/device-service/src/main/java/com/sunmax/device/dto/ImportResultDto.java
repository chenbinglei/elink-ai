package com.sunmax.device.dto;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据导入返回实体类
 */
@Data
@ApiModel("ImportResultDto")
public class ImportResultDto {

    /**
     * 正常数量
     */
    @ApiModelProperty("正常数量")
    private Integer normalNum = 0;

    /**
     * 异常数量
     */
    @ApiModelProperty("异常数量")
    private Integer errorNum = 0;

    /**
     * 异常错误数据
     */
    @ApiModelProperty("异常错误数据")
    private List<Map<String, String>> errDataList = Lists.newArrayList();

    /**
     * 异常错误描述
     */
    @ApiModelProperty("异常错误描述")
    private List<String> errDescList = Lists.newArrayList();

}
