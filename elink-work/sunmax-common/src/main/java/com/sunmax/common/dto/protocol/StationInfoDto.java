package com.sunmax.common.dto.protocol;

import com.google.common.collect.Lists;
import com.sunmax.common.vo.protocol.mqtt.web.data.StationInfoSubscribeVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 站点信息返回实体类
 */
@Data
@Schema(description = "站点信息返回实体类")
public class StationInfoDto {

    /**
     * 场站id
     */
    @Schema(description = "场站id")
    private Long stationId;

    /**
     * 当前配电负荷
     */
    @Schema(description = "当前配电负荷")
    private Double curLoad;

    /**
     * 当前充电功率
     */
    @Schema(description = "当前充电功率")
    private Double curPc;

    /**
     * 当前放电功率
     */
    @Schema(description = "当前放电功率")
    private Double curPd;

    /**
     * 剩余可调充电功率
     */
    @Schema(description = "剩余可调充电功率")
    private Double remainPc;

    /**
     * 剩余可调放电功率
     */
    @Schema(description = "剩余可调放电功率")
    private Double remainPd;

    /**
     * 充电桩数量
     */
    @Schema(description = "充电桩数量")
    private Integer pileNum;

    /**
     * 充电桩信息表
     */
    @Schema(description = "充电桩信息表")
    private List<StationInfoSubscribeVo.PileInfoDto> pileInfoList = Lists.newArrayList();


}
