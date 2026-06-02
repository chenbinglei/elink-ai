package com.sunmax.together.dao;

import com.sunmax.together.entity.UserFeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserFeedbackDao extends JpaRepository<UserFeedbackEntity, String>, JpaSpecificationExecutor<UserFeedbackEntity> {

}
