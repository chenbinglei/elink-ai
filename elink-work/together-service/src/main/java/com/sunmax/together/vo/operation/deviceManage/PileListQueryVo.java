package com.sunmax.together.vo.operation.deviceManage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电桩列表查询参数")
public class PileListQueryVo {

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 桩名称/编码
     */
    @Schema(description = "桩名称/编码")
    private String pileName;

    /**
     * 厂商名称
     */
    @Schema(description = "厂商名称")
    private String manufacturersName;

    /**
     * 区域类型 1-省级 2-市级
     */
    @Schema(description = "区域类型 1-省级 2-市级")
    private Integer areaType;

    /**
     * 区域
     */
    @Schema(description = "区域")
    private String area;

    /**
     * 设备类型 28-交流 29-直流 30-V2G
     */
    @Schema(description = "设备类型 28-交流 29-直流 30-V2G")
    private String typeId;

    /**
     * 多个站点id
     */
    @Schema(description = "多个站点id")
    private String siteId;

    /**
     * 电桩工作状态 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "电桩工作状态 1-在线 2-维护 3-故障 88-离线")
    private Integer workStatus;

    /**
     * 枪状态 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
     */
    @Schema(description = "枪状态 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
    private Integer gunWorkState;
}
