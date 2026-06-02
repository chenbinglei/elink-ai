package com.sunmax.together.vo.operation.storageCount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StorageCountVo", description = "储能统计入参实体类")
public class StorageCountVo {

    /**
     * 查询类型 1-站点 2-设备
     */
    @ApiModelProperty(value = "查询类型 1-站点 2-设备", required = true)
    private Integer queryType;

    /**
     * 查询数据id
     */
    @ApiModelProperty(value = "查询数据id", required = true)
    private String dataId;

    /**
     * 日期类型 1-日 2-月 3-年
     */
    @ApiModelProperty(value = "日期类型 1-日 2-月 3-年", required = true)
    private Integer dateType;

    /**
     * 开始日期(yyyy-MM-dd)
     */
    @ApiModelProperty(value = "开始日期(yyyy-MM-dd)", required = true)
    private String startDate;

    /**
     * 结束日期(yyyy-MM-dd)
     */
    @ApiModelProperty(value = "结束日期(yyyy-MM-dd)", required = true)
    private String endDate;

}
