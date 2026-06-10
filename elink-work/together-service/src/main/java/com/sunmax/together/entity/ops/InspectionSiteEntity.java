package com.sunmax.together.entity.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.AUTO;

/**
 * 巡检项实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_inspection_site")
public class InspectionSiteEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 巡检任务id
     */
    @Column(name = "task_id", columnDefinition = "varchar(32) comment '巡检任务id(关联b_inspection_task表里面的id)'")
    private String taskId;

    /**
     * 场站id
     */
    @Column(name = "site_id", columnDefinition = "varchar(32) comment '场站id'")
    private String siteId;

    /**
     * 巡检用户id
     */
    @Column(name = "user_id", columnDefinition = "varchar(32) comment '巡检用户id'")
    private String userId;

    /**
     * 巡检状态 1-未开始 2-巡检中 3-已完成 4-已放弃
     */
    @Column(name = "status", columnDefinition = "tinyint(1) comment '巡检状态 1-未开始 2-巡检中 3-已完成 4-已放弃'")
    private Integer status;

    /**
     * 巡检时间
     */
    @Column(name = "inspect_time", columnDefinition = "varchar(19) comment '巡检时间'")
    private String inspectTime;

    /**
     * 完成时间
     */
    @Column(name = "finish_time", columnDefinition = "varchar(19) comment '完成时间'")
    private String finishTime;

    /**
     * 多个巡检项检查状态(检查状态 1-未检查 2-正常 3-异常) {"巡检项id1": "检查状态","巡检项id2": "检查状态"}
     */
    @Column(name = "item_states", columnDefinition = "longtext comment '多个巡检项检查状态(检查状态 1-未检查 2-正常 3-异常) {\"巡检项id1\": \"检查状态\",\"巡检项id2\": \"检查状态\"}'")
    private String itemStates;

    /**
     * 备注
     */
    @Column(name = "remark", columnDefinition = "varchar(255) comment '备注'")
    private String remark;

    /**
     * 附件路径
     */
    @Column(name = "annex_path", columnDefinition = "longtext comment '附件路径'")
    private String annexPath;

}
