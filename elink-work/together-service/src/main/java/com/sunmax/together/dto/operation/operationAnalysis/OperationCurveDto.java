package com.sunmax.together.dto.operation.operationAnalysis;

import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "运营总览曲线数据返回实体类")
public class OperationCurveDto {

    /**
     * 日期列表
     */
    @Schema(description = "日期列表")
    private List<String> dateList;

    /**
     * 运营数据列表
     * key-类型 1-充电订单金额(元) 2-充电实付金额(元) 3-充电电量(度) 4-充电订单数量(笔) 5-V2G订单数量(元) 6-V2G放电电量(度) 7-枪均电量(度) 8-时间利用率(%) 9-充电时长(小时) 10-度均服务费(元)
     * value-数据列表
     */
    @Schema(description = "运营数据列表")
    private Map<Integer, List<CurveData>> curveDataMap = Maps.newHashMap();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "运营曲线数据")
    public static class CurveData {

        /**
         * 数据名称
         */
        @Schema(description = "数据名称")
        private String dataName;

        /**
         * 数据值列表
         */
        @Schema(description = "数据值列表")
        private List<Object> dataValueList;

    }

}
