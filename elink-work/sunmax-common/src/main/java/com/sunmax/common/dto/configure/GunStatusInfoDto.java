package com.sunmax.common.dto.configure;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电枪状态返回实体类")
public class GunStatusInfoDto {

    /**
     * 设备编码
     */
    @Schema(description = "设备编码")
    private String equipmentId;

    /**
     * 电枪编码
     */
    @Schema(description = "电枪编码")
    private String gunCode;

    /**
     * 电枪状态  -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障
     */
    @Schema(description = "枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障")
    private Integer gunStatus;

    /**
     * 车位状态
     *
     * 0:未知；
     * 10:空闲；
     * 50:占用
     */
    @Schema(description = "车位状态")
    private Integer parkStatus;

    /**
     * 地锁状态
     *
     * 0:未知；
     * 10:已解锁；
     * 50:已上锁
     */
    @Schema(description = "地锁状态")
    private Integer lockStatus;
}
