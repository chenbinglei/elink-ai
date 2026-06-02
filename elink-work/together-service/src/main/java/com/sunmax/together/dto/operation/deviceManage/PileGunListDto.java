package com.sunmax.together.dto.operation.deviceManage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "PileGunListDto", description = "桩枪设备列表返回实体类")
public class PileGunListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    private String deviceNumber;

    /**
     * 运营商名称
     */
    @ApiModelProperty(value = "运营商名称")
    private String operateName;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 设备类型 28-交流 29-直流 30-V2G
     */
    @ApiModelProperty(value = "设备类型 28-交流 29-直流 30-V2G")
    private String typeId;

    /**
     * 额定功率
     */
    @ApiModelProperty(value = "额定功率")
    private Double ratedPower;

    /**
     * 电桩工作状态 -1-未知 1-在线 2-维护 3-故障 88-离线
     */
    @ApiModelProperty(value = "电桩工作状态 -1-未知 1-在线 2-维护 3-故障 88-离线")
    private Integer workStatus;

    /**
     * 生产厂商
     */
    @ApiModelProperty(value = "生产厂商")
    private String manufacturer;

    /**
     * 运营状态 0-未知 1-投运 2-检修 3-退役
     */
    @ApiModelProperty(value = "运营状态 0-未知 1-投运 2-检修 3-退役")
    private Integer operateStatus;

    /**
     * 枪状态列表
     */
    @ApiModelProperty(value = "枪状态列表")
    private List<PileGunListDto.GunStateInfo> gunStateInfoList;

    /**
     * 电枪状态信息
     */
    @Data
    public static class GunStateInfo {

        /**
         * 电枪名称
         */
        @ApiModelProperty(value = "电枪名称")
        private String gunName;

        /**
         * 电枪编码
         */
        @ApiModelProperty(value = "电枪编码")
        private String gunCode;

        /**
         * 状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
         */
        @ApiModelProperty("状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
        private Integer gunWorkState;

        /**
         * 二维码解析地址清单
         */
        @ApiModelProperty(value = "二维码解析地址清单")
        private String qrCodes;
    }
}
