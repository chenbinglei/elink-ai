package com.sunmax.common.vo.protocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "协议查询参数实体类")
public class ProtocolQueryVo {

    /**
     * 电桩编号
     */
    @Schema(description = "电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @Schema(description = "枪编号")
    private String gunCode;

    /**
     * 协议类型 1-启动命令 2-启动响应 3-启动事件 4-停止命令 5-停止响应 6-停止事件 7-记录上报 8-日志数据上报 9-复位命令 10-复位响应 11-设置二维码命令 12-设置二维码响应
     */
    @Schema(description = "协议类型 1-启动命令 2-启动响应 3-启动事件 4-停止命令 5-停止响应 6-停止事件 7-记录上报 8-日志数据上报 9-复位命令 10-复位响应 11-设置二维码命令 12-设置二维码响应")
    private Integer protocolType;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

}
