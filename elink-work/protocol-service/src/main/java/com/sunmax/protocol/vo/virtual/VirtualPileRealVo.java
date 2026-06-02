package com.sunmax.protocol.vo.virtual;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileRealVo", description = "电桩实时数据参数实体类")
public class VirtualPileRealVo {

    /**
     * 多个充电桩编号，多个用逗号隔开
     */
    @ApiModelProperty(value = "多个充电桩编号，多个用逗号隔开", required = true)
    private String pileCodes;

}
