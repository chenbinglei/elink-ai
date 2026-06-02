package com.sunmax.common.dto.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.CostFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 4.2 CMD_RATE_RES,//费率响应 CMD=2
 * 发送方向：前置服务<---平台服务
 */
@Data
public class RateResDto {

    /**
     * 桩编码
     */
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 充电费率id
     */
    @ApiModelProperty(value = "充电费率id", required = true)
    private String cRateId;

    /**
     * 充电费率时段时段数量
     */
    @ApiModelProperty(value = "充电费率时段时段数量", required = true)
    private Integer cTimeFrameNum;

    /**
     * 充电费率
     */
    @ApiModelProperty(value = "充电费率", required = true)
    private List<CostFormat> cTimeFrameRate;

    /**
     * 放电费率id
     */
    @ApiModelProperty(value = "放电费率id", required = true)
    private String dRateId;

    /**
     * 放电费率时段时段数量
     */
    @ApiModelProperty(value = "放电费率时段时段数量", required = true)
    private Integer dTimeFrameNum;

    /**
     * 放电费率
     */
    @ApiModelProperty(value = "放电费率", required = true)
    private List<CostFormat> dTimeFrameRate;

}
