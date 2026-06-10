package com.sunmax.log.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "日志查询返回实体类")
public class AccessLogDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    private String userAccount;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 远程ip地址
     */
    @Schema(description = "远程ip地址")
    private String remoteAddr;

    /**
     * 远程端口
     */
    @Schema(description = "远程端口")
    private Integer remotePort;

    /**
     * 本地ip地址
     */
    @Schema(description = "本地ip地址")
    private String localAddr;

    /**
     * 本地端口
     */
    @Schema(description = "本地端口")
    private Integer localPort;

    /**
     * url
     */
    @Schema(description = "url")
    private String url;

    /**
     * 方法
     */
    @Schema(description = "方法")
    private String method;

    /**
     * 操作内容
     */
    @Schema(description = "操作内容")
    private String content;

    /**
     * 响应状态码
     */
    @Schema(description = "响应状态码")
    private Integer code;

    /**
     * 响应结果描述
     */
    @Schema(description = "响应结果描述")
    private String message;

}
