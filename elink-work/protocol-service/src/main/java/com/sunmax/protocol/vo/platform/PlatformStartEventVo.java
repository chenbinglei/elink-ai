package com.sunmax.protocol.vo.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电桩启动事件实体类")
public class PlatformStartEventVo {

    /**
     * 电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @Schema(description = "充电桩枪编号")
    private Integer gunCode;

    /**
     * 失败原因 0-成功 1-启动失败 2-预约成功 3-预约失败 255-其他原因
     */
    @Schema(description = "失败原因 0-成功 1-启动失败 2-预约成功 3-预约失败 255-其他原因")
    private Integer failReason;

    /**
     * 启动失败详细原因
     */
    @Schema(description = "启动失败详细原因(参考故障码分类)")
    private Integer failDetailReason;

    /**
     * 结束详细描述
     */
    @Schema(description = "结束详细描述")
    private String stopDetail;

    /**
     * 开始充/放电时间
     */
    @Schema(description = "开始充/放电时间")
    private String startTime;

    /**
     * 交易流水号
     */
    @Schema(description = "交易流水号")
    private String serialNum;

}
