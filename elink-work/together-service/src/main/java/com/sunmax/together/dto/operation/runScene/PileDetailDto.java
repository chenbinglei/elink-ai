package com.sunmax.together.dto.operation.runScene;

import com.google.common.collect.Lists;
import com.sunmax.common.dto.device.DeviceReaDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "电桩详情返回实体类")
public class PileDetailDto {

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @Schema(description = "设备序列号")
    private String deviceNumber;

    /**
     * 父节点名称
     */
    @Schema(description = "父节点名称")
    private String parentName;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @Schema(description = "接入类型 1-直连设备 2-网关设备 3-网关子设备")
    private Integer accessType;

    /**
     * 设备类型 28-交流 29-直流 30-V2G
     */
    @Schema(description = "设备类型 28-交流 29-直流 30-V2G")
    private String typeId;

    /**
     * 读写数据对象
     */
    @Schema(description = "读写数据对象")
    private String readwriteObject;

    /**
     * 设备描述
     */
    @Schema(description = "设备描述")
    private String deviceDesc;

    /**
     * 设备图片路径
     */
    @Schema(description = "设备图片路径")
    private String imagePaths;

    /**
     * 运营状态 0-未知 1-投运 2-检修 3-退役
     */
    @Schema(description = "运营状态 0-未知 1-投运 2-检修 3-退役")
    private Integer operateStatus;

    /**
     * 创建人名称
     */
    @Schema(description = "创建人名称")
    private String createName;

    /**
     * 修改人名称
     */
    @Schema(description = "修改人名称")
    private String updateName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private String updateTime;

    /**
     * 扩展属性对象列表
     */
    @Schema(description = "扩展属性对象列表")
    private List<DeviceReaDto> deviceReaList = Lists.newArrayList();

    /**
     * 电枪详情列表
     */
    @Schema(description = "电枪详情列表")
    private List<GunDetail> gunDetailList = Lists.newArrayList();

    /**
     * 电枪详情
     */
    @Data
    @Schema(description = "电枪详情")
    public static class GunDetail {

        /**
         * 充电枪id
         */
        @Schema(description = "充电枪id")
        private String id;

        /**
         * 充电枪编号
         */
        @Schema(description = "充电枪编号")
        private String gunCode;

        /**
         * 充电枪名称
         */
        @Schema(description = "充电枪名称")
        private String gunName;

        /**
         * 充电枪类型
         * 1：家用插座（模式 2）
         * 2：交流接口插座（模式3， 连接方式 B ）
         * 3：交流接口插头（带枪线，模式 3，连接方式C）
         * 4：直流接口枪头（带枪线，模式 4）
         */
        @Schema(description = "充电枪类型 1-家用插座（模式 2） 2-交流接口插座（模式3， 连接方式 B ） 3-交流接口插头（带枪线，模式 3，连接方式C） 4-直流接口枪头（带枪线，模式 4）")
        private Integer type;

        /**
         * 浙江省充电设备接口唯一码
         */
        @Schema(description = "浙江省充电设备接口唯一码")
        private String connectorUniqueId;

        /**
         * 外观
         */
        @Schema(description = "外观")
        private String appearance;

        /**
         * 防护等级
         */
        @Schema(description = "防护等级")
        private String ipGrade;

        /**
         * 额定电流 单位A
         */
        @Schema(description = "额定电流 单位A")
        private Integer ratedCurrent;

        /**
         * 额定功率 单位kW
         */
        @Schema(description = "额定功率 单位kW")
        private Double ratedPower;

        /**
         * 额定电压上限 单位V
         */
        @Schema(description = "额定电压上限 单位V")
        private Integer voltageUpperLimits;

        /**
         * 额定电压下限 单位V
         */
        @Schema(description = "额定电压下限 单位V")
        private Integer voltageLowerLimits;

        /**
         * 车位号
         */
        @Schema(description = "车位号")
        private String parkNo;

        /**
         * 国家标准 1:2011 2:2015
         */
        @Schema(description = "国家标准 1-2011 2-2015")
        private Integer nationalStandard;

        /**
         * 二维码解析地址清单
         */
        @Schema(description = "二维码解析地址清单")
        private String qrCodes;

        /**
         * 辅助电源 1-12V 2-24V 3-兼容12V和24V
         */
        @Schema(description = "辅助电源 1-12V 2-24V 3-兼容12V和24V")
        private Integer auxPower;
    }
}
