package com.sunmax.common.vo.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "运营总览查询条件实体类")
public class OperationOverviewVo {

    /**
     * 多个站点id 例如['siteId1','siteId2']
     */
    @Schema(description = "多个站点id 例如['siteId1','siteId2']")
    private String siteIds;

    /**
     * 多个系统id 例如['systemId1','systemId2']
     */
    @Schema(description = "多个系统id 例如['systemId1','systemId2']")
    private String systemIds;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startDate;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endDate;

    /**
     * 上一个开始时间
     */
    @Schema(description = "上一个开始时间")
    private String beforeStartDate;

    /**
     * 上一个结束时间
     */
    @Schema(description = "上一个结束时间")
    private String beforeEndDate;

}
