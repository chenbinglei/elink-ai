package com.sunmax.common.vo.crontab;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "节点历史数据查询实体类")
public class NodeHistoryDataVo {

    /**
     * 多个存储id
     */
    @Schema(description = "多个存储id")
    private List<Long> storageIdList;

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
     * 查询条数
     */
    @Schema(description = "查询条数")
    private Integer limitSize;

    /**
     * 时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年
     */
    @Schema(description = "时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年")
    private String timeInterval;
}
