package com.sunmax.auth.service.impl;

import com.sunmax.auth.dao.UserDao;
import com.sunmax.auth.entity.UserEntity;
import com.sunmax.auth.service.UserService;
import com.sunmax.common.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public ResponseResult<String> findUserIsExitByPhone(String userAccount, String phone) {
        Optional<UserEntity> optional = userDao.findOne(Example.of(UserEntity.builder().userAccount(userAccount)
                .phone(phone).userState(1).build()));
        if (optional.isPresent()) {
            return ResponseResult.ok(ResponseResult.SUCCESS);
        } else {
            return ResponseResult.paramError("账号或手机号错误");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> updateUserPassword(String userAccount, String phone, String password) {
        Optional<UserEntity> optional = userDao.findOne(Example.of(UserEntity.builder().userAccount(userAccount)
                .phone(phone).userState(1).build()));
        if (optional.isPresent()) {
            UserEntity userEntity = optional.get();
            userEntity.setPassword(password);
            userEntity.setUpdateTime(LocalDateTime.now());
            userDao.save(userEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.paramError(ResponseResult.FAIL);
    }


}
