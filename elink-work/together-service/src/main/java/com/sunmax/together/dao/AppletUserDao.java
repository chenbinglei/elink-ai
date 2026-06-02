package com.sunmax.together.dao;

import com.sunmax.together.entity.AppletUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AppletUserDao extends JpaRepository<AppletUserEntity, String>, JpaSpecificationExecutor<AppletUserEntity> {

    //查询指定状态的applet用户
    List<AppletUserEntity> findAllByUserStateIn(List<Integer> userStateLiist);

    //根据分组id查询小程序用户
    List<AppletUserEntity> findAllByGroupIdIn(List<String> groupIdList);

    /**
     * 根据用户手机号，查询小程序用户详情
     * @param phoneNum
     * @return
     */
    AppletUserEntity findByPhoneNum(String phoneNum);
}
