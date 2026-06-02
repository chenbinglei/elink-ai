package com.sunmax.configure.entity.interflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

/**
 * 互联互通充电设备接口信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_interflow_connector")
public class InterflowConnectorEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 充电设备接口编码
     *
     * 充电设备接口编码,同一运营商内唯一
     */
    @Column(name = "connector_id", columnDefinition = "varchar(64) NOT NULL comment '充电设备接口编码(充电设备接口编码,同一运营商内唯一)'")
    private String connectorId;

    /**
     * 充电设备接口名称
     */
    @Column(name = "connector_name", columnDefinition = "varchar(64) comment '充电设备接口名称'")
    private String connectorName;

    /**
     * 充电设备接口类型
     */
    @Column(name = "connector_type", columnDefinition = "tinyint(1) NOT NULL comment '充电设备接口类型 " +
            "1:家用插座(模式 2)\n" +
            "2:交流接口插座(模式3,连接方式 B)\n" +
            "3:交流接口插头(带枪线,模式 3,连接方式 C)\n" +
            "4:直流接口枪头(带枪线,模式 4)\n" +
            "5:无线充电座\n" +
            "6:其他'")
    private Integer connectorType;

    /**
     * 额定电压上限
     */
    @Column(name = "voltage_upper_limits", columnDefinition = "int(10) NOT NULL comment '额定电压上限'")
    private Integer voltageUpperLimits;

    /**
     * 额定电压下限
     */
    @Column(name = "voltage_lower_limits", columnDefinition = "int(10) NOT NULL comment '额定电压下限'")
    private Integer voltageLowerLimits;

    /**
     * 额定电流
     */
    @Column(name = "current", columnDefinition = "int(10) NOT NULL comment '额定电流'")
    private Integer current;

    /**
     * 额定功率
     */
    @Column(name = "power", columnDefinition = "double(9,4) NOT NULL comment '额定功率'")
    private Double power;

    /**
     * 车位号
     */
    @Column(name = "park_no", columnDefinition = "varchar(64) comment '车位号'")
    private String parkNo;

    /**
     * 国家标准
     */
    @Column(name = "national_standard", columnDefinition = "tinyint(1) NOT NULL comment '国家标准 1:2011 2:2015'")
    private Integer nationalStandard;

    /**
     * 充电设备接口模式
     */
    @Column(name = "connector_model", columnDefinition = "tinyint(1) comment '充电设备接口模式 0:慢充 1:快充 2:超充'")
    private Integer connectorModel;

    /**
     * 所属设备编码
     */
    @Column(name = "equipment_id", columnDefinition = "varchar(64) NOT NULL comment '所属设备编码 关联b_interflow_equipment表中的equipment_id'")
    private String equipmentId;

    /**
     * 所属运营商id
     */
    @Column(name = "operator_id", columnDefinition = "varchar(64) NOT NULL comment '所属运营商id'")
    private String operatorId;
}
