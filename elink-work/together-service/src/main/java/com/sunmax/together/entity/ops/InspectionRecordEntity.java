package com.sunmax.together.entity.ops;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.AUTO;

/**
 * 巡检任务记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_inspection_record")
public class InspectionRecordEntity {

    /**
     * 主键id
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(32) comment '主键id'")
    @GeneratedValue(strategy = AUTO, generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    private String id;

    /**
     * 任务id
     */
    @Column(name = "task_id", columnDefinition = "varchar(32) comment '任务id'")
    private String taskId;

    /**
     * 节点名称
     */
    @Column(name = "node_name", columnDefinition = "varchar(32) comment '节点名称'")
    private String nodeName;

    /**
     * 处理结果 1-已提交 2-已退回 3-已交接
     */
    @Column(name = "result", columnDefinition = "tinyint(1) comment '处理结果 1-已提交 2-已退回 3-已交接'")
    private Integer result;

    /**
     * 创建人id
     */
    @Column(name = "create_id", columnDefinition = "varchar(32) comment '创建人id'", updatable = false)
    private String createId;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", columnDefinition = "datetime(0) comment '创建时间'", updatable = false)
    private LocalDateTime createTime;

    /**
     * 流转意见
     */
    @Column(name = "flow_opinion", columnDefinition = "varchar(32) comment '流转意见'")
    private String flowOpinion;

}
