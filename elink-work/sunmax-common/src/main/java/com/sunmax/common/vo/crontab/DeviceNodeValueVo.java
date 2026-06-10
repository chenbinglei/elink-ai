package com.sunmax.common.vo.crontab;

import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@Schema(description = "设备计算节点数据查询实体类")
public class DeviceNodeValueVo {

    /**
     * 多个设备节点编号数据
     * 站点id/设备id -> 多个计算节点编号
     */
    @Schema(description = "多个设备节点编号数据")
    private Map<String, Set<String>> deviceNodeCodeMap = Maps.newHashMap();

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
