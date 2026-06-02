package com.sunmax.common.vo.together;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "OperationCurveVo", description = "运行总览曲线查询条件")
public class OperationCurveVo {

    /**
     * 多个站点id
     */
    @ApiModelProperty(value = "多个站点id", required = true)
    private String siteIds;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", required = true)
    private String startDate;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", required = true)
    private String endDate;

    /**
     * 类型 1-充电订单金额(元) 2-充电实付金额(元) 3-充电电量(度) 4-充电订单数量(笔) 5-V2G放电订单金额(元) 6-V2G放电电量(度) 7-枪均电量(度) 8-时间利用率(%) 9-充电时长(小时) 10-度均服务费(元) 11-功率利用率(%) 12-一次充电成功率(%)
     */
    @ApiModelProperty(value = "类型 1-充电订单金额(元) 2-充电实付金额(元) 3-充电电量(度) 4-充电订单数量(笔) 5-V2G放电订单金额(元) 6-V2G放电电量(度) 7-枪均电量(度) 8-时间利用率(%) 9-充电时长(小时) 10-度均服务费(元) 11-功率利用率(%) 12-一次充电成功率(%)", required = true)
    private String types;

    /**
     * 日期类型 1-天 2-月
     */
    @ApiModelProperty(value = "日期类型 1-天 2-月", required = true)
    private Integer dateType;

}
