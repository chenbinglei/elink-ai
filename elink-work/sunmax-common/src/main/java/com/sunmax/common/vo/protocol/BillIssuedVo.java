package com.sunmax.common.vo.protocol;

import com.google.common.collect.Lists;
import com.sunmax.common.dto.protocol.mqtt.web.model.CostFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 充电桩计费模板
 */
@Data
public class BillIssuedVo {

    /**
     * 多个充电桩编号
     */
    @ApiModelProperty(value = "多个充电桩编号", required = true)
    private List<String> pileCodes = Lists.newArrayList();

    /**
     * 费率类型 0-充电费率模型  1-放电费率模型
     * */
    @ApiModelProperty(value = "费率类型 0-充电费率模型  1-放电费率模型", required = true)
    private Integer type;

    /**
     * 费率模型id
     */
    @ApiModelProperty(value = "费率模型id")
    private String templateId;

    /**
     * 时段数量 N 范围 1～48
     */
    @ApiModelProperty(value = "时段数量", required = true)
    private Integer timeFrameNum = 48;

    /**
     * 时段费率
     */
    @ApiModelProperty("时段费率")
    private List<CostFormat> timeFrameRates;

}
