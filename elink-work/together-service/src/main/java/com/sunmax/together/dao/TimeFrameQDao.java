package com.sunmax.together.dao;

import com.sunmax.together.entity.order.TimeFrameQEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TimeFrameQDao extends JpaRepository<TimeFrameQEntity, String>, JpaSpecificationExecutor<TimeFrameQEntity> {
}
