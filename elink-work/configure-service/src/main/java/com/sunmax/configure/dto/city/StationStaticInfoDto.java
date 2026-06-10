package com.sunmax.configure.dto.city;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @Date: 2023/9/2016:26
 * @version: 1.0
 * @注释:
 */
@Data
@Schema(description = "功率控制站桩信息实体类")
public class StationStaticInfoDto {

    /**
     * 站名
     */
    @Schema(description = "站名")
    @JSONField(name = "StationName")
    private String stationName;

    /**
     * 所在区域
     */
    @Schema(description = "所在区域")
    @JSONField(name = "AreaCode")
    private String areaCode;

    /**
     * 全站容量
     */
    @Schema(description = "全站容量")
    @JSONField(name = "StationCapacity")
    private Double stationCapacity;

    /**
     * 全站桩数量
     */
    @Schema(description = "全站桩数量")
    @JSONField(name = "StakeNum")
    private Integer stakeNum;

    /**
     * 运营系统内部ID
     */
    @Schema(description = "运营系统内部ID")
    @JSONField(name = "StationId")
    private String stationId;

    /**
     * 站类型
     * 1 高速
     * 2 城市公共
     * 3 单位内部
     * 4 公交
     * 5 广告
     * 6 出租车
     * 10 环卫
     * 11 物流
     * 99 岸电
     * 98 换电站
     */
    @Schema(description = "站类型")
    @JSONField(name = "StationType")
    private Integer stationType;

    /**
     * 是否可控
     * 0:不可控 1 可控
     */
    @Schema(description = "是否可控")
    @JSONField(name = "IsControllable")
    private Integer isControllable;

    /**
     * 所属运营商id
     */
    @Schema(description = "所属运营商id")
    @JSONField(name = "OperatorId")
    private String operatorId;

    /**
     * 桩信息
     */
    @Schema(description = "桩信息")
    @JSONField(name = "EquipmentInfos")
    private List<PileInfoDetailDto> equipmentInfos;
}
