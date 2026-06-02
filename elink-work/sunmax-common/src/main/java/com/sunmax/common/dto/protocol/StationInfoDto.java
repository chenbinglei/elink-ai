package com.sunmax.common.dto.protocol;

import com.google.common.collect.Lists;
import com.sunmax.common.vo.protocol.mqtt.web.data.StationInfoSubscribeVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 站点信息返回实体类
 */
@Data
@ApiModel(value = "StationInfoDto", description = "站点信息返回实体类")
public class StationInfoDto {

    /**
     * 场站id
     */
    @ApiModelProperty(value = "场站id")
    private Long stationId;

    /**
     * 当前配电负荷
     */
    @ApiModelProperty(value = "当前配电负荷")
    private Double curLoad;

    /**
     * 当前充电功率
     */
    @ApiModelProperty(value = "当前充电功率")
    private Double curPc;

    /**
     * 当前放电功率
     */
    @ApiModelProperty(value = "当前放电功率")
    private Double curPd;

    /**
     * 剩余可调充电功率
     */
    @ApiModelProperty(value = "剩余可调充电功率")
    private Double remainPc;

    /**
     * 剩余可调放电功率
     */
    @ApiModelProperty(value = "剩余可调放电功率")
    private Double remainPd;

    /**
     * 充电桩数量
     */
    @ApiModelProperty(value = "充电桩数量")
    private Integer pileNum;

    /**
     * 充电桩信息表
     */
    @ApiModelProperty(value = "充电桩信息表")
    private List<StationInfoSubscribeVo.PileInfoDto> pileInfoList = Lists.newArrayList();


}
