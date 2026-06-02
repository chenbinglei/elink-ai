package com.sunmax.device.entity.access;

import com.sunmax.device.vo.PointTableChangeVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.AUTO;

/**
 * 点表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_point_table")
public class PointTableEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 通道id
     */
    @Column(name = "channel_id", columnDefinition = "varchar(32) not null comment '通道id'")
    private String channelId;

    /**
     * 设备id
     */
    @Column(name = "device_id", columnDefinition = "varchar(32) not null comment '设备id'")
    private String deviceId;

    /**
     * 功能点id
     */
    @Column(name = "function_id", columnDefinition = "varchar(32) not null comment '功能点id'")
    private String functionId;

    /**
     * 功能点下标
     */
    @Column(name = "function_index", columnDefinition = "int(3) comment '功能点下标'")
    private Integer functionIndex;

    /**
     * 数据点号
     */
    @Column(name = "data_id", columnDefinition = "bigint(20) comment '数据点号'")
    private Long dataId;

    /**
     * 系数
     */
    @Column(name = "coefficient", columnDefinition = "float(9,2) default 1.0 comment '系数'")
    private Float coefficient;

    /**
     * 偏移量
     */
    @Column(name = "offset", columnDefinition = "int(10) default 0 comment '偏移量'")
    private Integer offset;

//    /**
//     * 下发状态 1-未下发 2-下发
//     */
//    @Column(name = "issued_status", columnDefinition = "tinyint(1) default 1 comment '下发状态 1-未下发 2-下发'")
//    private Integer issuedStatus;

    /**
     * 最后修改时间
     */
    @LastModifiedDate
    @Column(name = "update_time", columnDefinition = "datetime(0) comment '最后修改时间'")
    private LocalDateTime updateTime;

    public PointTableEntity(PointTableChangeVo pointTableVo) {
        BeanUtils.copyProperties(pointTableVo, this);
    }

}
