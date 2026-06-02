package com.sunmax.together.vo.operation.deviceManage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PileListQueryVo", description = "电桩列表查询参数")
public class PileListQueryVo {

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

    /**
     * 桩名称/编码
     */
    @ApiModelProperty(value = "桩名称/编码")
    private String pileName;

    /**
     * 厂商名称
     */
    @ApiModelProperty(value = "厂商名称")
    private String manufacturersName;

    /**
     * 区域类型 1-省级 2-市级
     */
    @ApiModelProperty(value = "区域类型 1-省级 2-市级")
    private Integer areaType;

    /**
     * 区域
     */
    @ApiModelProperty(value = "区域")
    private String area;

    /**
     * 设备类型 28-交流 29-直流 30-V2G
     */
    @ApiModelProperty(value = "设备类型 28-交流 29-直流 30-V2G")
    private String typeId;

    /**
     * 多个站点id
     */
    @ApiModelProperty(value = "多个站点id")
    private String siteId;

    /**
     * 电桩工作状态 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "电桩工作状态 1-在线 2-维护 3-故障 88-离线")
    private Integer workStatus;

    /**
     * 枪状态 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
     */
    @ApiModelProperty("枪状态 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
    private Integer gunWorkState;
}
