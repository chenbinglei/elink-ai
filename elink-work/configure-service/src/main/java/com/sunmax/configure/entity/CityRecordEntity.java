package com.sunmax.configure.entity;

import com.sunmax.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_city_record")
public class CityRecordEntity extends BaseEntity {

    /**
     * 接口名称
     */
    @Column(name = "interface_name", columnDefinition = "varchar(255) NOT NULL comment '接口名称'")
    private String interfaceName;

    /**
     * 推送数据
     */
    @Column(name = "push_data", columnDefinition = "longtext comment '推送数据'")
    private String pushData;

    /**
     * 响应状态
     */
    @Column(name = "response_status", columnDefinition = "int(10) comment '响应状态'")
    private Integer responseStatus;

    /**
     * 响应数据
     */
    @Column(name = "response_data", columnDefinition = "longtext comment '响应数据'")
    private String responseData;

}
