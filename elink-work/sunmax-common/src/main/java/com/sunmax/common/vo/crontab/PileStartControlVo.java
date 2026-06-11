package com.sunmax.common.vo.crontab;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PileStartControlVo {

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
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 运行模式 -1-未知 0-充电模式 1-放电模式
     */
    @Schema(description = "运行模式 -1-未知 0-充电模式 1-放电模式")
    private Integer runMode;

    /**
     * 执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错
     */
    @Schema(description = "执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错")
    private Integer result;

    /**
     * 启动时间
     */
    @Schema(description = "启动时间")
    private String startTime;

    /**
     * 是否更新
     */
    @Schema(description = "是否更新")
    private Boolean isUpdate;

}
