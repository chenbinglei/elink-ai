package com.sunmax.log.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AccessLogDto", description = "日志查询返回实体类")
public class AccessLogDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号")
    private String userAccount;

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String userName;

    /**
     * 远程ip地址
     */
    @ApiModelProperty(value = "远程ip地址")
    private String remoteAddr;

    /**
     * 远程端口
     */
    @ApiModelProperty(value = "远程端口")
    private Integer remotePort;

    /**
     * 本地ip地址
     */
    @ApiModelProperty(value = "本地ip地址")
    private String localAddr;

    /**
     * 本地端口
     */
    @ApiModelProperty(value = "本地端口")
    private Integer localPort;

    /**
     * url
     */
    @ApiModelProperty(value = "url")
    private String url;

    /**
     * 方法
     */
    @ApiModelProperty(value = "方法")
    private String method;

    /**
     * 操作内容
     */
    @ApiModelProperty(value = "操作内容")
    private String content;

    /**
     * 响应状态码
     */
    @ApiModelProperty(value = "响应状态码")
    private Integer code;

    /**
     * 响应结果描述
     */
    @ApiModelProperty(value = "响应结果描述")
    private String message;

}
