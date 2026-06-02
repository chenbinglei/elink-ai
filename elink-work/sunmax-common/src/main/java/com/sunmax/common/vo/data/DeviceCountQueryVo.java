package com.sunmax.common.vo.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
@ApiModel(value = "DeviceCountQueryVo", description = "设备统计值历史数据查询实体类")
public class DeviceCountQueryVo {

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
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", required = true)
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", required = true)
    private String endTime;

    /**
     * 时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年
     */
    @ApiModelProperty(value = "时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年")
    private String timeInterval;

    /**
     * 统计函数 求和-SUM;最大值-MAX;最小值-MIN;平均值-AVG;最后一次值-LAST
     */
    @ApiModelProperty(value = "统计函数 求和-SUM;最大值-MAX;最小值-MIN;平均值-AVG;最后一次值-LAST", required = true)
    private String cuntFun;
}
