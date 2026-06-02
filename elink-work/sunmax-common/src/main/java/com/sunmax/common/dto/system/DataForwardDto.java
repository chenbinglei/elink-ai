package com.sunmax.common.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "DataForwardDto", description = "数据转发响应实体类")
public class DataForwardDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 通道名称
     */
    @ApiModelProperty(value = "通道名称")
    private String channelName;

    /**
     * 接入协议类型 1-mqtt 2-http
     */
    @ApiModelProperty(value = "接入协议类型 1-mqtt 2-http")
    private Integer protocolType;

    /**
     * 接入协议标识
     */
    @ApiModelProperty(value = "接入协议标识")
    private String protocolCode;

    /**
     * 动态字段
     */
    @ApiModelProperty(value = "动态字段")
    private String dynamicFields;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String address;

    /**
     * 数据配置列表
     */
    @ApiModelProperty(value = "数据配置列表")
    private List<DataConfigDto> dataConfigList = Lists.newArrayList();

}
