package com.sunmax.common.vo.together;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "FunctionValueVo", description = "功能点数据查询参数")
public class FunctionValueVo {
    /**
     * 多个功能点标识
     */
    @ApiModelProperty(value = "多个功能点标识")
    private List<String> functionLogos;

    /**
     * 开始时间(yyyy-MM-dd HH:mm:ss)
     */
    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm:ss)", required = true)
    private String startTime;

    /**
     * 结束时间(yyyy-MM-dd HH:mm:ss)
     */
    @ApiModelProperty(value = "结束时间(yyyy-MM-dd HH:mm:ss)", required = true)
    private String endTime;

    /**
     * 多个设备id
     */
    @ApiModelProperty(value = "多个设备id")
    private List<String> deviceIdList;

    /**
     * 查询条数
     */
    @ApiModelProperty(value = "查询条数")
    private Integer limitSize;

    /**
     * 索引号
     */
    @ApiModelProperty(value = "索引号")
    private Integer index;

    /**
     * 时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年
     */
    @ApiModelProperty(value = "时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年")
    private String timeInterval;
}
