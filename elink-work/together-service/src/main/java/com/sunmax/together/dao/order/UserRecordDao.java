package com.sunmax.together.dao.order;

import com.sunmax.together.entity.order.UserRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRecordDao extends JpaRepository<UserRecordEntity, String>, JpaSpecificationExecutor<UserRecordEntity> {

    //根据多个订单id查询用户记录数据
    List<UserRecordEntity> findAllByOrderNumIn(List<String> orderNumList);
}
