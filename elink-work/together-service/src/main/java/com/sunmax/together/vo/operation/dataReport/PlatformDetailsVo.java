package com.sunmax.together.vo.operation.dataReport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电桩列表查询参数")
public class PlatformDetailsVo {

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数(传0不分页，返回所有列表)")
    private Integer size;

    /**
     * 多个站点id
     */
    @Schema(description = "多个站点id")
    private String siteIds;

    /**
     * 开始时间(yyyy-MM-dd HH:mm:ss)
     */
    @Schema(description = "开始时间(yyyy-MM-dd HH:mm:ss)")
    private String startTime;

    /**
     * 结束时间(yyyy-MM-dd HH:mm:ss)
     */
    @Schema(description = "结束时间(yyyy-MM-dd HH:mm:ss)")
    private String endTime;

    /**
     * 时间类型 1-逐日 2-逐月
     */
    @Schema(description = "时间类型 1-逐日 2-逐月")
    private Integer timeType;
}
