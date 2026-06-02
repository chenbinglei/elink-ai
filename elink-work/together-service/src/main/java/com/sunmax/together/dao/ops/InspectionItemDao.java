package com.sunmax.together.dao.ops;

import com.sunmax.together.entity.ops.InspectionItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InspectionItemDao extends JpaRepository<InspectionItemEntity, String>, JpaSpecificationExecutor<InspectionItemEntity> {

}
