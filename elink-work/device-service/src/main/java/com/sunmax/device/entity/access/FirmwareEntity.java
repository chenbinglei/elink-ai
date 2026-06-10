package com.sunmax.device.entity.access;

import com.sunmax.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

/**
 * 固件包实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_firmware")
public class FirmwareEntity extends BaseEntity {

    /**
     * 设备类型id
     */
    @Column(name = "type_id",columnDefinition = "varchar(32) comment '设备类型id'")
    private String typeId;

    /**
     * 设备型号(多选,例如[’型号1‘,'型号2'])
     */
    @Column(name = "equipment_models",columnDefinition = "varchar(1024) comment '设备型号(多选,例如[型号1,型号2])'")
    private String equipmentModels;

    /**
     * 固件包名称
     */
    @Column(name = "firmware_name",columnDefinition = "varchar(64) comment '固件包名称'")
    private String firmwareName;

    /**
     * 固件包路径
     */
    @Column(name = "firmware_path",columnDefinition = "varchar(255) comment '固件包路径'")
    private String firmwarePath;

    /**
     * 固件类型
     * 1-V2G_1.0 TCP控制板
     * 2-V2G_2.0 TCP控制板
     * 3-V2G_3.0 TCP控制板
     * 4-V2G_4.0 TPU控制板
     * 5-V2G_4.0 CCU控制板
     * 6-V2G_6.0 TCP控制板
     * 7-V2G_7.0 TPU控制板
     * 8-V2G_8.0 CCU控制板
     */
    @Column(name = "firmware_type",columnDefinition = "int(3) comment '固件类型 1-V2G_1.0 TCP控制板 2-V2G_2.0 TCP控制板 3-V2G_3.0 TCP控制板 4-V2G_4.0 TPU控制板 5-V2G_4.0 CCU控制板 6-V2G_6.0 TCP控制板 7-V2G_7.0 TPU控制板 8-V2G_8.0 CCU控制板'")
    private Integer firmwareType;

    /**
     * 固件版本号
     */
    @Column(name = "firmware_version",columnDefinition = "varchar(20) comment '固件版本号'")
    private String firmwareVersion;

    /**
     * 固件包文件大小
     */
    @Column(name = "firmware_size",columnDefinition = "int(10) comment '固件包文件大小'")
    private Long firmwareSize;

    /**
     * 固件包描述
     */
    @Column(name = "firmware_desc",columnDefinition = "varchar(255) comment '固件包描述'")
    private String firmwareDesc;

}
