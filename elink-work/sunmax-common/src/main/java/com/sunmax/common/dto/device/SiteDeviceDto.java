package com.sunmax.common.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * 站点设备返回实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "站点设备树形结构返回实体类")
public class SiteDeviceDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 站点编码
     */
    @Schema(description = "站点编码")
    private String siteCode;

    /**
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @Schema(description = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    private Integer siteStatus;

    /**
     * 设备数据
     */
    @Schema(description = "设备数据")
    @Builder.Default
    private List<Device> deviceList = Lists.newArrayList();

    @Data
    public static class Device {

        /**
         * 主键id
         */
        @Schema(description = "主键id")
        private String id;

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
         * 接入类型 1-直连设备 2-网关设备 3-网关子设备
         */
        @Schema(description = "接入类型 1-直连设备 2-网关设备 3-网关子设备")
        private Integer accessType;

        /**
         * 通信状态 0-未注册 1-在线 2-故障 88-离线
         */
        @Schema(description = "通信状态 0-未注册 1-在线 2-故障 88-离线")
        private Integer txStatus = 0;

        /**
         * 资产分类id
         */
        @Schema(description = "资产分类id")
        private String typeId;

        /**
         * 额定功率
         */
        @Schema(description = "额定功率")
        private Double ratedPower;

        /**
         * 设备枪数据列表
         */
        @Schema(description = "设备枪数据列表")
        private List<DeviceGun> deviceGunList = Lists.newArrayList();

    }

    @Data
    public static class DeviceGun {

        /**
         * 枪编号
         */
        @Schema(description = "枪编号")
        private String gunCode;

        /**
         * 枪名称
         */
        @Schema(description = "枪名称")
        private String gunName;

        /**
         * 额定功率
         */
        @Schema(description = "额定功率")
        private Double ratedPower;

    }

}
