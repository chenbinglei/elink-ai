package com.sunmax.protocol.vo.virtual;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电桩实时数据参数实体类")
public class VirtualPileRealVo {

    /**
     * 多个充电桩编号，多个用逗号隔开
     */
    @Schema(description = "多个充电桩编号，多个用逗号隔开")
    private String pileCodes;

}
