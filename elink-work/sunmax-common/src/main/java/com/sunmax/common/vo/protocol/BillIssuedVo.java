package com.sunmax.common.vo.protocol;

import com.google.common.collect.Lists;
import com.sunmax.common.dto.protocol.mqtt.web.model.CostFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "多个充电桩编号")
    private List<String> pileCodes = Lists.newArrayList();

    /**
     * 费率类型 0-充电费率模型  1-放电费率模型
     * */
    @Schema(description = "费率类型 0-充电费率模型  1-放电费率模型")
    private Integer type;

    /**
     * 费率模型id
     */
    @Schema(description = "费率模型id")
    private String templateId;

    /**
     * 时段数量 N 范围 1～48
     */
    @Schema(description = "时段数量")
    private Integer timeFrameNum = 48;

    /**
     * 时段费率
     */
    @Schema(description = "时段费率")
    private List<CostFormat> timeFrameRates;

}
