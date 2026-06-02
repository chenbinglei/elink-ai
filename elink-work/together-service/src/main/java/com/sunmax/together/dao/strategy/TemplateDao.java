package com.sunmax.together.dao.strategy;

import com.sunmax.together.entity.strategy.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TemplateDao extends JpaRepository<TemplateEntity, String>, JpaSpecificationExecutor<TemplateEntity> {

}
