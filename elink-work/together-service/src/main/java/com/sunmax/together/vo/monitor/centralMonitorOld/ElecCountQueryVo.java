package com.sunmax.together.vo.monitor.centralMonitorOld;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电量统计查询参数")
public class ElecCountQueryVo {

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
     * 时间类型 1-日 2-月 3-年
     */
    @Schema(description = "时间类型 1-日 2-月 3-年")
    private Integer dateType;
}
