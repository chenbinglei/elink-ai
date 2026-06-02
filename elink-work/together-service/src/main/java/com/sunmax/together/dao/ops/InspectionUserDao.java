package com.sunmax.together.dao.ops;

import com.sunmax.together.entity.ops.InspectionUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InspectionUserDao extends JpaRepository<InspectionUserEntity, String>, JpaSpecificationExecutor<InspectionUserEntity> {
}
