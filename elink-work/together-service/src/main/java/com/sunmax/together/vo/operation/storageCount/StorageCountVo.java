package com.sunmax.together.vo.operation.storageCount;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "储能统计入参实体类")
public class StorageCountVo {

    /**
     * 查询类型 1-站点 2-设备
     */
    @Schema(description = "查询类型 1-站点 2-设备")
    private Integer queryType;

    /**
     * 查询数据id
     */
    @Schema(description = "查询数据id")
    private String dataId;

    /**
     * 日期类型 1-日 2-月 3-年
     */
    @Schema(description = "日期类型 1-日 2-月 3-年")
    private Integer dateType;

    /**
     * 开始日期(yyyy-MM-dd)
     */
    @Schema(description = "开始日期(yyyy-MM-dd)")
    private String startDate;

    /**
     * 结束日期(yyyy-MM-dd)
     */
    @Schema(description = "结束日期(yyyy-MM-dd)")
    private String endDate;

}
