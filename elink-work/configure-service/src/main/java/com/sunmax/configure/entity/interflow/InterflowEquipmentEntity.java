package com.sunmax.configure.entity.interflow;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

/**
 * 互联互通充电设备信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_interflow_equipment")
public class InterflowEquipmentEntity {

    /**
     * 设备编码
     *
     * 设备唯一编码,对同一运营商,保证唯一
     */
    @Id
    @Column(name = "equipment_id", columnDefinition = "varchar(64) NOT NULL comment '设备编码'")
    private String equipmentId;

    /**
     * 充电设备名称
     */
    @Column(name = "equipment_name", columnDefinition = "varchar(64) comment '充电设备名称'")
    private String equipmentName;

    /**
     * 设备生产商组织机构代码
     */
    @Column(name = "manufacturer_id", columnDefinition = "varchar(64) comment '设备生产商组织机构代码'")
    private String manufacturerId;

    /**
     * 设备生产商名称
     */
    @Column(name = "manufacturer_name", columnDefinition = "varchar(64) comment '设备生产商名称'")
    private String manufacturerName;

    /**
     * 设备型号
     */
    @Column(name = "equipment_model", columnDefinition = "varchar(64) comment '设备型号'")
    private String equipmentModel;

    /**
     * 设备生产日期
     */
    @Column(name = "production_date", columnDefinition = "varchar(20) comment '设备生产日期'")
    private String productionDate;

    /**
     * 设备类型
     * 1:直流设备
     * 2:交流设备
     * 3:交直流一体设备
     * 4:无线设备
     * 5:其他
     */
    @Column(name = "equipment_type", columnDefinition = "tinyint(1) NOT NULL comment '设备类型 \n" +
            "     * 1:直流设备\n" +
            "     * 2:交流设备\n" +
            "     * 3:交直流一体设备\n" +
            "     * 4:无线设备\n" +
            "     * 5:其他'")
    private Integer equipmentType;

    /**
     * 充电设备总功率
     */
    @Column(name = "power", columnDefinition = "double(9,4) NOT NULL comment '充电设备总功率'")
    private Double power;

    /**
     * 充电设备经度
     */
    @Column(name = "equipment_lng", columnDefinition = "varchar(30) comment '充电设备经度'")
    private Double equipmentLng;

    /**
     * 充电设备纬度
     */
    @Column(name = "equipment_lat", columnDefinition = "varchar(30) comment '充电设备纬度'")
    private Double equipmentLat;

    /**
     * 所属充电站ID
     */
    @Column(name = "station_id", columnDefinition = "varchar(64) NOT NULL comment '所属充电站ID,关联b_interflow_station表的station_id'")
    private String stationId;

    /**
     * 充电设备接口列表信息
     */
    @Getter
    private static String connectorInfos;


    public void setConnectorInfos(String connectorInfos) {
        InterflowEquipmentEntity.connectorInfos = connectorInfos;
    }
}
