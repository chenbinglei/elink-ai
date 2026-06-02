package com.sunmax.together.dao;

import com.sunmax.together.entity.UserDisWalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserDisWalletDao extends JpaRepository<UserDisWalletEntity, String>, JpaSpecificationExecutor<UserDisWalletEntity> {

    //根据用户id查询用户钱包信息
    List<UserDisWalletEntity> findAllByAppletUserId(String appletUserId);

    //根据用户id和账号id查询用户钱包信息
    UserDisWalletEntity findAllByAppletUserIdAndAccountId(String appletUserId, String accountId);

}
