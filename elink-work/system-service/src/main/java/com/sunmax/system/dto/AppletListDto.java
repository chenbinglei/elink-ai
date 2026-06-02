package com.sunmax.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "AppletListDto", description = "小程序列表返回实体类")
public class AppletListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 小程序名称
     */
    @ApiModelProperty(value = "小程序名称", required = true)
    private String appletName;

    /**
     * 小程序id
     */
    @ApiModelProperty(value = "小程序id", required = true)
    private String appletCode;

    /**
     * 小程序类型 1-微信 2-支付宝
     */
    @ApiModelProperty(value = "小程序类型 1-微信 2-支付宝", required = true)
    private Integer appletType;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String phone;

    /**
     * 多个租户id
     */
    @ApiModelProperty(value = "多个租户id")
    private String tenantIds;

    /**
     * 多个企业名称
     */
    @ApiModelProperty(value = "多个租户名称")
    private String tenantNames;

    /**
     * 创建人名称
     */
    @ApiModelProperty("创建人名称")
    private String createName;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改人名称
     */
    @ApiModelProperty("修改人名称")
    private String updateName;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


}
