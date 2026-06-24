package com.sunmax.auth.service;

import com.sunmax.auth.dao.UserDao;
import com.sunmax.auth.entity.UserEntity;
import com.sunmax.auth.service.impl.UserServiceImpl;
import com.sunmax.common.util.ResponseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("根据账号和手机号查询用户-存在时返回成功")
    void findUserIsExitByPhone_exists_returnsSuccess() {
        UserEntity user = UserEntity.builder().userAccount("admin").phone("13800138000").userState(1).build();
        when(userDao.findOne(any(Example.class))).thenReturn(Optional.of(user));

        ResponseResult<String> result = userService.findUserIsExitByPhone("admin", "13800138000");

        assertTrue(result.isSuccess());
        assertEquals(ResponseResult.SUCCESS, result.getData());
    }

    @Test
    @DisplayName("根据账号和手机号查询用户-不存在时返回参数错误")
    void findUserIsExitByPhone_notExists_returnsParamError() {
        when(userDao.findOne(any(Example.class))).thenReturn(Optional.empty());

        ResponseResult<String> result = userService.findUserIsExitByPhone("admin", "13800138000");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("错误"));
    }

    @Test
    @DisplayName("更新用户密码-用户存在时更新成功")
    void updateUserPassword_exists_updatesSuccessfully() {
        UserEntity user = UserEntity.builder().userAccount("admin").phone("13800138000").userState(1).build();
        when(userDao.findOne(any(Example.class))).thenReturn(Optional.of(user));
        when(userDao.save(any(UserEntity.class))).thenReturn(user);

        ResponseResult<String> result = userService.updateUserPassword("admin", "13800138000", "newPassword");

        assertTrue(result.isSuccess());
        verify(userDao).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("更新用户密码-用户不存在时返回失败")
    void updateUserPassword_notExists_returnsFail() {
        when(userDao.findOne(any(Example.class))).thenReturn(Optional.empty());

        ResponseResult<String> result = userService.updateUserPassword("admin", "13800138000", "newPassword");

        assertFalse(result.isSuccess());
        verify(userDao, never()).save(any());
    }
}
