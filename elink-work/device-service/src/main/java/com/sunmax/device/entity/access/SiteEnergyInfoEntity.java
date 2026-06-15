package com.sunmax.device.entity.access;

import com.sunmax.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 站点能源信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_site_energy_info")
public class SiteEnergyInfoEntity extends BaseEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    public String id;

    /**
     * 站点id
     */
    @Column(name = "site_id",columnDefinition = "varchar(32) comment '站点id'")
    private String siteId;

    /**
     * 电网状态 1-开启 2-关闭
     */
    @Column(name = "power_grid_state",columnDefinition = "tinyint(1) comment '电网状态 1-开启 2-关闭'")
    private Integer powerGridState;

    /**
     * 电网线条方向 1-单向 2-双向
     */
    @Column(name = "power_line_direction",columnDefinition = "tinyint(1) comment '电网线条方向 1-开启 2-关闭'")
    private Integer powerLineDirection;

    /**
     * 电压等级 1-空 2-0.4kV 3-10kV 4-20kV
     */
    @Column(name = "voltage_grade",columnDefinition = "tinyint(1) comment '电压等级 1-空 2-0.4kV 3-10kV 4-20kV'")
    private Integer voltageGrade;

    /**
     * 变配电状态 1-开启 2-关闭
     */
    @Column(name = "tran_state",columnDefinition = "tinyint(1) comment '变配电状态 1-开启 2-关闭'")
    private Integer tranState;

    /**
     * 变配电线条方向 1-单向 2-双向
     */
    @Column(name = "tran_line_direction",columnDefinition = "tinyint(1) comment '变配电线条方向 1-开启 2-关闭'")
    private Integer tranLineDirection;

    /**
     * 产权类型 1-空 2-专变 3-公变
     */
    @Column(name = "property_right_type",columnDefinition = "tinyint(1) comment '产权类型 1-空 2-专变 3-公变'")
    private Integer propertyRightType;

    /**
     * 配变容量
     */
    @Column(name = "tran_capacity",columnDefinition = "bigint(20) comment '配变容量'")
    private Long tranCapacity;

    /**
     * 电桩状态 1-开启 2-关闭
     */
    @Column(name = "pile_state",columnDefinition = "tinyint(1) comment '电桩状态 1-开启 2-关闭'")
    private Integer pileState;

    /**
     * 电桩线条方向 1-单向 2-双向
     */
    @Column(name = "pile_line_direction",columnDefinition = "tinyint(1) comment '电桩线条方向 1-开启 2-关闭'")
    private Integer pileLineDirection;

    /**
     * 电桩装机量
     */
    @Column(name = "pile_install_capacity",columnDefinition = "bigint(20) comment '电桩装机量'")
    private Long pileInstallCapacity;

    /**
     * 是否支持V2G 1-是 2-否
     */
    @Column(name = "is_support_v2g",columnDefinition = "tinyint(1) comment '是否支持V2G 1-是 2-否'")
    private Integer isSupportV2g;

    /**
     * 光伏状态 1-开启 2-关闭
     */
    @Column(name = "pv_state",columnDefinition = "tinyint(1) comment '光伏状态 1-开启 2-关闭'")
    private Integer pvState;

    /**
     * 光伏线条方向 1-单向 2-双向
     */
    @Column(name = "pv_line_direction",columnDefinition = "tinyint(1) comment '光伏线条方向 1-开启 2-关闭'")
    private Integer pvLineDirection;

    /**
     * 光伏装机量
     */
    @Column(name = "pv_install_capacity",columnDefinition = "bigint(20) comment '光伏装机量'")
    private Long pvInstallCapacity;

    /**
     * 光伏运行方式 1-空 2-并网运行 3-离网运行
     */
    @Column(name = "pv_run_way",columnDefinition = "tinyint(1) comment '光伏运行方式 1-空 2-并网运行 3-离网运行'")
    private Integer pvRunWay;

    /**
     * 并网模式 1-空 2-自发自用余电上网 3-全额上网
     */
    @Column(name = "on_grid_mode",columnDefinition = "tinyint(1) comment '并网模式 1-空 2-自发自用余电上网 3-全额上网'")
    private Integer onGridMode;

    /**
     * 储能状态 1-开启 2-关闭
     */
    @Column(name = "storage_state",columnDefinition = "tinyint(1) comment '储能状态 1-开启 2-关闭'")
    private Integer storageState;

    /**
     * 储能线条方向 1-单向 2-双向
     */
    @Column(name = "storage_line_direction",columnDefinition = "tinyint(1) comment '储能线条方向 1-开启 2-关闭'")
    private Integer storageLineDirection;

    /**
     * 储能装机量
     */
    @Column(name = "storage_install_capacity",columnDefinition = "bigint(20) comment '储能装机量'")
    private Long storageInstallCapacity;

    /**
     * 储能额定功率
     */
    @Column(name = "storage_rated_power",columnDefinition = "bigint(20) comment '储能额定功率'")
    private Long storageRatedPower;

    /**
     * 储能运行方式 1-空 2-并网运行 3-离网运行
     */
    @Column(name = "storage_run_way",columnDefinition = "tinyint(1) comment '储能运行方式 1-空 2-并网运行 3-离网运行'")
    private Integer storageRunWay;
}
