package com.sunmax.together.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.AUTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_user_feedback")
public class UserFeedbackEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 反馈类型 1-充电过程 2-发票开具 3-占位费 4-信息不符 5-事故
     */
    @Column(name = "feedback_type", columnDefinition = "tinyint(1) comment '反馈类型 1-充电过程 2-发票开具 3-占位费 4-信息不符 5-事故'")
    private Integer feedbackType;

    /**
     * 站点id
     */
    @Column(name = "site_id", columnDefinition = "varchar(32) comment '站点id'")
    private String siteId;

    /**
     * 描述
     */
    @Column(name = "description", columnDefinition = "varchar(255) comment '描述'")
    private String description;

    /**
     * 图片路径
     */
    @Column(name = "image_url", columnDefinition = "text comment '图片路径'")
    private String imageUrl;

    /**
     * 用户id
     */
    @Column(name = "user_id", columnDefinition = "varchar(32) comment '用户id'")
    private String userId;

    /**
     * 用户手机号
     */
    @Column(name = "user_phone", columnDefinition = "varchar(32) comment '用户手机号'")
    private String userPhone;

    /**
     * 状态 1-待处理 2-处理中 3-已处理
     */
    @Column(name = "status", columnDefinition = "tinyint(1) comment '状态 1-待处理 2-处理中 3-已处理'")
    private Integer status;

    /**
     * 反馈时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '反馈时间'", updatable = false)
    private LocalDateTime createTime;

    /**
     * 受理时间
     */
    @Column(name = "accept_time", columnDefinition = "datetime(0) comment '受理时间'", updatable = false)
    private LocalDateTime acceptTime;

    /**
     * 处理完成时间
     */
    @Column(name = "finish_time", columnDefinition = "datetime(0) comment '处理完成时间'")
    private LocalDateTime finishTime;

}
