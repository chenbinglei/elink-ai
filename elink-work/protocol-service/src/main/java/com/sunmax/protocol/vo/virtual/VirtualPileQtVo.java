package com.sunmax.protocol.vo.virtual;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电桩电量参数实体类")
public class VirtualPileQtVo {

    /**
     * 多个充电桩编号(多个用逗号隔开)
     */
    @Schema(description = "多个充电桩编号(多个用逗号隔开)")
    private String pileCodes;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间(yyyy-MM-dd HH:mm:ss)")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    private String endTime;

}
