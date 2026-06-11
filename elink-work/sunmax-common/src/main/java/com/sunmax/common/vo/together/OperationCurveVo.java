package com.sunmax.common.vo.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "运行总览曲线查询条件")
public class OperationCurveVo {

    /**
     * 多个站点id
     */
    @Schema(description = "多个站点id")
    private String siteIds;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startDate;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endDate;

    /**
     * 类型 1-充电订单金额(元) 2-充电实付金额(元) 3-充电电量(度) 4-充电订单数量(笔) 5-V2G放电订单金额(元) 6-V2G放电电量(度) 7-枪均电量(度) 8-时间利用率(%) 9-充电时长(小时) 10-度均服务费(元) 11-功率利用率(%) 12-一次充电成功率(%)
     */
    @Schema(description = "类型 1-充电订单金额(元) 2-充电实付金额(元) 3-充电电量(度) 4-充电订单数量(笔) 5-V2G放电订单金额(元) 6-V2G放电电量(度) 7-枪均电量(度) 8-时间利用率(%) 9-充电时长(小时) 10-度均服务费(元) 11-功率利用率(%) 12-一次充电成功率(%)")
    private String types;

    /**
     * 日期类型 1-天 2-月
     */
    @Schema(description = "日期类型 1-天 2-月")
    private Integer dateType;

}
