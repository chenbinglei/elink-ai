package com.sunmax.system.entity;

import com.sunmax.common.entity.BaseEntity;
import com.sunmax.system.vo.OperatorInfoVo;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

/**
 * @Author: yqz
 * @Date: 2023/10/711:11
 * @version: 1.0
 * @注释: 运营商信息表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_operator_info")
public class OperatorInfoEntity extends BaseEntity {

    /**
     * 运营商ID
     */
    @Column(name = "operator_id", columnDefinition = "varchar(9) comment '运营商ID(使用组织机构代码（去掉”-“符号的9位字符）)'")
    private String operatorId;

    /**
     * 运营商名称
     */
    @Column(name = "operator_name", columnDefinition = "varchar(64) comment '运营商名称'")
    private String operatorName;

    /**
     * 运营商简称
     */
    @Column(name = "operator_short_name", columnDefinition = "varchar(64) comment '运营商简称'")
    private String operatorShortName;

    /**
     * 运营商统一社会信用代码
     */
    @Column(name = "operator_credit_code", columnDefinition = "varchar(18) comment '运营商统一社会信用代码'")
    private String operatorCreditCode;

    /**
     * 联系人
     */
    @Column(name = "operator_contact", columnDefinition = "varchar(32) comment '联系人'")
    private String operatorContact;

    /**
     * 运营商电话1
     */
    @Column(name = "operator_tel1", columnDefinition = "varchar(32) comment '运营商电话1'")
    private String operatorTel1;

    /**
     * 运营商电话2
     */
    @Column(name = "operator_tel2", columnDefinition = "varchar(32) comment '运营商电话2'")
    private String operatorTel2;

    /**
     * 运营商注册地址
     */
    @Column(name = "operator_reg_address", columnDefinition = "varchar(64) comment '运营商注册地址'")
    private String operatorRegAddress;

    /**
     * 备注
     */
    @Column(name = "operator_note", columnDefinition = "varchar(255) comment '备注'")
    private String operatorNote;

    public OperatorInfoEntity(OperatorInfoVo operatorInfoVo) {
        BeanUtils.copyProperties(operatorInfoVo, this);
    }
}
