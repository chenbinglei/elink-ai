package com.sunmax.common.dto.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电枪监控数据返回实体类")
public class PileGunMonitorDataDto {

    /**
     * 桩编号
     */
    @Schema(description = "桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @Schema(description = "枪编号")
    private String gunCode;

    /**
     * 枪名称
     */
    @Schema(description = "枪名称")
    private String gunName;

    /**
     * 今日充电量
     */
    @Schema(description = "今日充电量")
    private Double dayChargeQt = 0.0;

    /**
     * 今日放电量
     */
    @Schema(description = "今日放电量")
    private Double dayV2gQt = 0.0;

    /**
     * 枪状态
     */
    @Schema(description = "枪状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
    private Integer gunWorkState = -1;

    /**
     * 枪原始状态
     */
    @Schema(description = "枪原始状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障")
    private Integer gunStatus = -1;

}
