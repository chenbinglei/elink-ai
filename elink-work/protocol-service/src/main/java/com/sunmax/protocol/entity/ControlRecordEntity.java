package com.sunmax.protocol.entity;

import com.sunmax.common.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_control_record")
public class ControlRecordEntity extends BaseTimeEntity {

    /**
     * 设备编号
     */
    @Column(name = "device_code", columnDefinition = "varchar(50) NOT NULL comment '设备编号'")
    private String deviceCode;

    /**
     * 操作内容
     */
    @Column(name = "operate_content", columnDefinition = "varchar(50) comment '操作内容'")
    private String operateContent;

    /**
     * 控制参数
     */
    @Column(name = "control_param", columnDefinition = "longtext comment '控制参数'")
    private String controlParam;

    /**
     * 执行结果 --1-执行超时 0-执行成功 1-执行失败 255-其他原因
     */
    @Column(name = "execute_status", columnDefinition = "tinyint(1) comment '执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因'")
    private Integer executeStatus;

    /**
     * 控制类型 1-平台 2-网关
     */
    @Column(name = "control_type", columnDefinition = "tinyint(1) comment '控制类型 1-平台 2-网关'")
    private Integer controlType;

    /**
     * 控制参数(预留)
     */
    @Column(name = "control_value", columnDefinition = "varchar(50) comment '控制参数(预留)'")
    private String controlValue;

    /**
     * 终端编号
     */
    @Column(name = "terminal_code", columnDefinition = "varchar(64) comment '终端编号'")
    private String terminalCode;

    /**
     * 交互充电桩keyId
     */
    @Column(name = "key_id", columnDefinition = "varchar(255) comment '交互充电桩keyId'")
    private String keyId;

    /**
     * 是否存储
     */
    @Transient
    private Boolean isStore;

}
