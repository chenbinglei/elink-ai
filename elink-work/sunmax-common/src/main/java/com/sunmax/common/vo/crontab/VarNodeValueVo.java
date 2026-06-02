package com.sunmax.common.vo.crontab;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "VarNodeValueVo", description = "设备系统变量节点数据查询实体类")
public class VarNodeValueVo {

    /**
     * 多个变量编码
     */
    @ApiModelProperty(value = "多个变量编码")
    private List<String> varCodeList;

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
     * 多个站点/设备id
     */
    @ApiModelProperty(value = "多个站点/设备id")
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
