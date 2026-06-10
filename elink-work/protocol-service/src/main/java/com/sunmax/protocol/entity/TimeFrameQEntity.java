package com.sunmax.protocol.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 时段电量
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_timeframeq")
public class TimeFrameQEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 订单号
     */
    @Column(name = "order_num", columnDefinition = "varchar(32) comment '订单号'")
    private String orderNum;

    /**
     * 时段1电量
     */
    @Column(name = "timeFrame1", columnDefinition = "double(10,3) comment '时段1电量(00:00:00-00:30:00)'")
    private Double timeFrame1;
    /**
     * 时段2电量
     */
    @Column(name = "timeFrame2", columnDefinition = "double(10,3) comment '时段2电量(00:30:00-01:00:00)'")
    private Double timeFrame2;
    /**
     * 时段3电量
     */
    @Column(name = "timeFrame3", columnDefinition = "double(10,3) comment '时段3电量)'")
    private Double timeFrame3;
    /**
     * 时段4电量
     */
    @Column(name = "timeFrame4", columnDefinition = "double(10,3) comment '时段4电量)'")
    private Double timeFrame4;
    /**
     * 时段5电量
     */
    @Column(name = "timeFrame5", columnDefinition = "double(10,3) comment '时段5电量)'")
    private Double timeFrame5;
    /**
     * 时段6电量
     */
    @Column(name = "timeFrame6", columnDefinition = "double(10,3) comment '时段6电量)'")
    private Double timeFrame6;
    /**
     * 时段7电量
     */
    @Column(name = "timeFrame7", columnDefinition = "double(10,3) comment '时段7电量)'")
    private Double timeFrame7;/**
     * 时段8电量
     */
    @Column(name = "timeFrame8", columnDefinition = "double(10,3) comment '时段8电量)'")
    private Double timeFrame8;
    /**
     * 时段9电量
     */
    @Column(name = "timeFrame9", columnDefinition = "double(10,3) comment '时段9电量)'")
    private Double timeFrame9;
    /**
     * 时段10电量
     */
    @Column(name = "timeFrame10", columnDefinition = "double(10,3) comment '时段10电量)'")
    private Double timeFrame10;
    /**
     * 时段11电量
     */
    @Column(name = "timeFrame11", columnDefinition = "double(10,3) comment '时段11电量)'")
    private Double timeFrame11;
    /**
     * 时段12电量
     */
    @Column(name = "timeFrame12", columnDefinition = "double(10,3) comment '时段12电量)'")
    private Double timeFrame12;
    /**
     * 时段13电量
     */
    @Column(name = "timeFrame13", columnDefinition = "double(10,3) comment '时段13电量)'")
    private Double timeFrame13;
    /**
     * 时段14电量
     */
    @Column(name = "timeFrame14", columnDefinition = "double(10,3) comment '时段14电量)'")
    private Double timeFrame14;
    /**
     * 时段15电量
     */
    @Column(name = "timeFrame15", columnDefinition = "double(10,3) comment '时段15电量)'")
    private Double timeFrame15;
    /**
     * 时段16电量
     */
    @Column(name = "timeFrame16", columnDefinition = "double(10,3) comment '时段16电量)'")
    private Double timeFrame16;
    /**
     * 时段17电量
     */
    @Column(name = "timeFrame17", columnDefinition = "double(10,3) comment '时段17电量)'")
    private Double timeFrame17;
    /**
     * 时段18电量
     */
    @Column(name = "timeFrame18", columnDefinition = "double(10,3) comment '时段18电量)'")
    private Double timeFrame18;
    /**
     * 时段19电量
     */
    @Column(name = "timeFrame19", columnDefinition = "double(10,3) comment '时段19电量)'")
    private Double timeFrame19;
    /**
     * 时段20电量
     */
    @Column(name = "timeFrame20", columnDefinition = "double(10,3) comment '时段20电量)'")
    private Double timeFrame20;
    /**
     * 时段21电量
     */
    @Column(name = "timeFrame21", columnDefinition = "double(10,3) comment '时段21电量)'")
    private Double timeFrame21;
    /**
     * 时段22电量
     */
    @Column(name = "timeFrame22", columnDefinition = "double(10,3) comment '时段22电量)'")
    private Double timeFrame22;
    /**
     * 时段23电量
     */
    @Column(name = "timeFrame23", columnDefinition = "double(10,3) comment '时段23电量)'")
    private Double timeFrame23;
    /**
     * 时段24电量
     */
    @Column(name = "timeFrame24", columnDefinition = "double(10,3) comment '时段24电量)'")
    private Double timeFrame24;
    /**
     * 时段25电量
     */
    @Column(name = "timeFrame25", columnDefinition = "double(10,3) comment '时段25电量)'")
    private Double timeFrame25;
    /**
     * 时段26电量
     */
    @Column(name = "timeFrame26", columnDefinition = "double(10,3) comment '时段26电量)'")
    private Double timeFrame26;
    /**
     * 时段27电量
     */
    @Column(name = "timeFrame27", columnDefinition = "double(10,3) comment '时段27电量)'")
    private Double timeFrame27;
    /**
     * 时段28电量
     */
    @Column(name = "timeFrame28", columnDefinition = "double(10,3) comment '时段28电量)'")
    private Double timeFrame28;
    /**
     * 时段29电量
     */
    @Column(name = "timeFrame29", columnDefinition = "double(10,3) comment '时段29电量)'")
    private Double timeFrame29;
    /**
     * 时段30电量
     */
    @Column(name = "timeFrame30", columnDefinition = "double(10,3) comment '时段30电量)'")
    private Double timeFrame30;
    /**
     * 时段31电量
     */
    @Column(name = "timeFrame31", columnDefinition = "double(10,3) comment '时段31电量)'")
    private Double timeFrame31;
    /**
     * 时段32电量
     */
    @Column(name = "timeFrame32", columnDefinition = "double(10,3) comment '时段32电量)'")
    private Double timeFrame32;
    /**
     * 时段33电量
     */
    @Column(name = "timeFrame33", columnDefinition = "double(10,3) comment '时段33电量)'")
    private Double timeFrame33;
    /**
     * 时段34电量
     */
    @Column(name = "timeFrame34", columnDefinition = "double(10,3) comment '时段34电量)'")
    private Double timeFrame34;
    /**
     * 时段35电量
     */
    @Column(name = "timeFrame35", columnDefinition = "double(10,3) comment '时段35电量)'")
    private Double timeFrame35;
    /**
     * 时段36电量
     */
    @Column(name = "timeFrame36", columnDefinition = "double(10,3) comment '时段36电量)'")
    private Double timeFrame36;
    /**
     * 时段37电量
     */
    @Column(name = "timeFrame37", columnDefinition = "double(10,3) comment '时段37电量)'")
    private Double timeFrame37;
    /**
     * 时段38电量
     */
    @Column(name = "timeFrame38", columnDefinition = "double(10,3) comment '时段38电量)'")
    private Double timeFrame38;
    /**
     * 时段39电量
     */
    @Column(name = "timeFrame39", columnDefinition = "double(10,3) comment '时段39电量)'")
    private Double timeFrame39;
    /**
     * 时段40电量
     */
    @Column(name = "timeFrame40", columnDefinition = "double(10,3) comment '时段40电量)'")
    private Double timeFrame40;
    /**
     * 时段41电量
     */
    @Column(name = "timeFrame41", columnDefinition = "double(10,3) comment '时段41电量)'")
    private Double timeFrame41;
    /**
     * 时段42电量
     */
    @Column(name = "timeFrame42", columnDefinition = "double(10,3) comment '时段42电量)'")
    private Double timeFrame42;
    /**
     * 时段43电量
     */
    @Column(name = "timeFrame43", columnDefinition = "double(10,3) comment '时段43电量)'")
    private Double timeFrame43;
    /**
     * 时段44电量
     */
    @Column(name = "timeFrame44", columnDefinition = "double(10,3) comment '时段44电量)'")
    private Double timeFrame44;
    /**
     * 时段45电量
     */
    @Column(name = "timeFrame45", columnDefinition = "double(10,3) comment '时段45电量)'")
    private Double timeFrame45;
    /**
     * 时段46电量
     */
    @Column(name = "timeFrame46", columnDefinition = "double(10,3) comment '时段46电量)'")
    private Double timeFrame46;
    /**
     * 时段47电量
     */
    @Column(name = "timeFrame47", columnDefinition = "double(10,3) comment '时段47电量)'")
    private Double timeFrame47;
    /**
     * 时段48电量
     */
    @Column(name = "timeFrame48", columnDefinition = "double(10,3) comment '时段48电量)'")
    private Double timeFrame48;




}
