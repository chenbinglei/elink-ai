package com.sunmax.together.entity.assets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 组串配置信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_series_config")
public class SeriesConfigEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 所属设备id
     */
    @Column(name = "device_id",columnDefinition = "varchar(32) comment '所属设备id'")
    private String deviceId;

    /**
     * 组串名称
     */
    @Column(name = "series_name",columnDefinition = "varchar(16) comment '组串名称'")
    private String seriesName;

    /**
     * 组件数量
     */
    @Column(name = "module_num",columnDefinition = "int(10) NOT NULL comment '组件数量'")
    private Integer moduleNum;

    /**
     * 组串容量
     */
    @Column(name = "series_capacity",columnDefinition = "double(9,2) NOT NULL comment '组串容量'")
    private Double seriesCapacity;

    /**
     * 组件厂家
     */
    @Column(name = "module_factory", columnDefinition = "varchar(255) NOT NULL comment '组件厂家'")
    private String moduleFactory;

    /**
     * 组件型号
     */
    @Column(name = "module_model", columnDefinition = "varchar(32) NOT NULL comment '组件型号'")
    private String moduleModel;

    /**
     * 组件类型 1-多晶 2-单晶 3-叠瓦 4-P型双面 5-N型双面
     */
    @Column(name = "module_type", columnDefinition = "tinyint(1) NOT NULL comment '组件类型 1-多晶 2-单晶 3-叠瓦 4-P型双面 5-N型双面'")
    private Integer moduleType;

    /**
     * 组件电池片数（片/组件）
     */
    @Column(name = "battery_pieces", columnDefinition = "int(10) NOT NULL comment '组件电池片数（片/组件）'")
    private Integer batteryPieces;

    /**
     * 填充因子
     */
    @Column(name = "fill_factor", columnDefinition = "double(9,2) comment '填充因子'")
    private Double fillFactor;

    /**
     * 组件最大功率(Pmax)(W)
     */
    @Column(name = "max_power", columnDefinition = "double(9,2) NOT NULL comment '组件最大功率(Pmax)(W)'")
    private Double maxPower;

    /**
     * 组件最佳工作电压(Vmp) (V)
     */
    @Column(name = "best_work_voltage", columnDefinition = "double(9,2) NOT NULL comment '组件最佳工作电压(Vmp) (V)'")
    private Double bestWorkVoltage;

    /**
     * 组件最佳工作电流(Imp) (A)
     */
    @Column(name = "best_work_current", columnDefinition = "double(9,2) NOT NULL comment '组件最佳工作电流(Imp) (A)'")
    private Double bestWorkCurrent;

    /**
     * 组件开路电压(Voc)(V)
     */
    @Column(name = "open_voltage", columnDefinition = "double(9,2) NOT NULL comment '组件开路电压(Voc)(V)'")
    private Double openVoltage;

    /**
     * 组件短路电流(Isc)(A)
     */
    @Column(name = "short_current", columnDefinition = "double(9,2) NOT NULL comment '组件短路电流(Isc)(A)'")
    private Double shortCurrent;

    /**
     * 最大功率(Pmax)的温度系数 (%/°C)
     */
    @Column(name = "max_power_temp_coeff", columnDefinition = "double(9,4) NOT NULL comment '最大功率(Pmax)的温度系数 (%/°C)'")
    private Double maxPowerTempCoeff;

    /**
     * 开路电压(Voc)的温度系数 (%/°C)
     */
    @Column(name = "open_volt_temp_coeff", columnDefinition = "double(9,4) NOT NULL comment '开路电压(Voc)的温度系数 (%/°C)'")
    private Double openVoltTempCoeff;

    /**
     * 短路电流(Isc)的温度系数 (%/°C)
     */
    @Column(name = "short_curr_temp_coeff", columnDefinition = "double(9,4) NOT NULL comment '短路电流(Isc)的温度系数 (%/°C)'")
    private Double shortCurrTempCoeff;

    /**
     * 标称组件转换效率(%)
     */
    @Column(name = "convert_effi", columnDefinition = "double(9,4) comment '标称组件转换效率(%)'")
    private Double convertEffi;

    /**
     * 组件首年衰减率(%/y)
     */
    @Column(name = "first_decay_rate", columnDefinition = "double(9,4) NOT NULL comment '组件首年衰减率(%/y)'")
    private Double firstDecayRate;

    /**
     * 组件逐年衰减率(%/y)
     */
    @Column(name = "passing_decay_rate", columnDefinition = "double(9,4) NOT NULL comment '组件逐年衰减率(%/y)'")
    private Double passingDecayRate;
}
