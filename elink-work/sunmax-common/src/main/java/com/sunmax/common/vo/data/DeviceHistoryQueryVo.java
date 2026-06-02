package com.sunmax.common.vo.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
@ApiModel(value = "DeviceHistoryQueryVo", description = "设备历史数据查询实体类")
public class DeviceHistoryQueryVo {

    /**
     * 多个设备id
     */
    @ApiModelProperty(value = "多个设备id", required = true)
    private Set<String> deviceIds;

    /**
     * 多个功能点标识
     */
    @ApiModelProperty(value = "多个功能点标识", required = true)
    private Set<String> functionLogos;

    /**
     * 开始时间(yyyy-MM-dd HH:mm:ss)
     */
    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm:ss)")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间(yyyy-MM-dd HH:mm:ss)")
    private String endTime;

    /**
     * 查询条数
     */
    @ApiModelProperty(value = "查询条数")
    private Integer limitSize;

    /**
     * 时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年
     */
    @ApiModelProperty(value = "时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年")
    private String timeInterval;

}
