package com.sunmax.together.dao;

import com.sunmax.together.entity.AppletCancelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AppletCancelDao extends JpaRepository<AppletCancelEntity, String>, JpaSpecificationExecutor<AppletCancelEntity> {
    AppletCancelEntity findByAppletUserIdAndApplyState(String appletUserId, int applyState);
}
