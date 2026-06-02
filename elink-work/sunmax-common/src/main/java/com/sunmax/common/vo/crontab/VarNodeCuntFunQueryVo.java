package com.sunmax.common.vo.crontab;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "VarNodeCuntFunQueryVo", description = "变量计算节点统计函数值查询实体类")
public class VarNodeCuntFunQueryVo {

    /**
     * 多个设备/站点id
     */
    @ApiModelProperty(value = "多个设备/站点id", required = true)
    private List<String> queryIdList;

    /**
     * 多个系统变量编码
     */
    @ApiModelProperty(value = "多个系统变量编码", required = true)
    private List<String> varCodeList;

    /**
     * 开始时间-可为空
     */
    @ApiModelProperty(value = "开始时间-可为空")
    private String startTime;

    /**
     * 结束时间-可为空
     */
    @ApiModelProperty(value = "结束时间-可为空")
    private String endTime;

    /**
     * 时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年(可为空)
     */
    @ApiModelProperty(value = "时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年(可为空)")
    private String timeInterval;

    /**
     * 统计函数 求和-SUM;最大值-MAX;最小值-MIN;平均值-AVG
     */
    @ApiModelProperty(value = "统计函数 求和-SUM;最大值-MAX;最小值-MIN;平均值-AVG", required = true)
    private String cuntFun;
}
