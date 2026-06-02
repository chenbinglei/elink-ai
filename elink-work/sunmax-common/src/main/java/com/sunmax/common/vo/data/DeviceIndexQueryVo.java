package com.sunmax.common.vo.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@ApiModel(value = "设备历史下标查询参数实体类")
public class DeviceIndexQueryVo {

    /**
     * 多个功能点标识 设备id -> 功能点标识 + index + 0
     */
    @ApiModelProperty(value = "多个功能点标识", required = true)
    private Map<String, Set<String>> deviceFuctionMap;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
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
