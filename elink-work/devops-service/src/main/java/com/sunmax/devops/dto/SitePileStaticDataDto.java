package com.sunmax.devops.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SitePileStaticDataDto", description = "站点电桩概览静态数据返回实体类")
public class SitePileStaticDataDto {

    /**
     * 直流桩数量
     */
    @ApiModelProperty(value = "直流桩数量")
    private Integer dcPileNum = 0;

    /**
     * 直流枪数量
     */
    @ApiModelProperty(value = "直流枪数量")
    private Integer dcGunNum = 0;

    /**
     * 交流桩数量
     */
    @ApiModelProperty(value = "交流桩数量")
    private Integer acPileNum = 0;

    /**
     * 交流枪数量
     */
    @ApiModelProperty(value = "交流枪数量")
    private Integer acGunNum = 0;

    /**
     * 全部
     */
    @ApiModelProperty("全部")
    private Long whole = 0L;

    /**
     * 空闲
     */
    @ApiModelProperty("空闲")
    private Long idle = 0L;

    /**
     * 充电中
     */
    @ApiModelProperty("充电")
    private Long charge = 0L;

    /**
     * 放电中
     */
    @ApiModelProperty("放电")
    private Long discharge = 0L;

    /**
     * 占用
     */
    @ApiModelProperty("占用")
    private Long employ = 0L;

    /**
     * 占用
     */
    @ApiModelProperty("其他")
    private Long other = 0L;

}
