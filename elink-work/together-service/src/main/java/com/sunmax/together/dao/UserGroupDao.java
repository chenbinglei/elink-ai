package com.sunmax.together.dao;

import com.sunmax.together.entity.UserGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserGroupDao extends JpaRepository<UserGroupEntity, String>, JpaSpecificationExecutor<UserGroupEntity> {
}
