package com.sunmax.common.vo.crontab;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "变量计算节点统计函数值查询实体类")
public class VarNodeCuntFunQueryVo {

    /**
     * 多个设备/站点id
     */
    @Schema(description = "多个设备/站点id")
    private List<String> queryIdList;

    /**
     * 多个系统变量编码
     */
    @Schema(description = "多个系统变量编码")
    private List<String> varCodeList;

    /**
     * 开始时间-可为空
     */
    @Schema(description = "开始时间-可为空")
    private String startTime;

    /**
     * 结束时间-可为空
     */
    @Schema(description = "结束时间-可为空")
    private String endTime;

    /**
     * 时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年(可为空)
     */
    @Schema(description = "时间间隔 s-秒;m-分钟;h-小时;d-天;n-月;y-年(可为空)")
    private String timeInterval;

    /**
     * 统计函数 求和-SUM;最大值-MAX;最小值-MIN;平均值-AVG
     */
    @Schema(description = "统计函数 求和-SUM;最大值-MAX;最小值-MIN;平均值-AVG")
    private String cuntFun;
}
