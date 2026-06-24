package com.sunmax.together.service.monitor;

import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.dto.AccessLogDto;
import com.sunmax.log.dto.PageLogDto;
import com.sunmax.log.service.AccessLogService;
import com.sunmax.log.vo.AccessLogQueryVo;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.monitor.impl.LogServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LogService 单元测试")
class LogServiceTest {

    @Mock private AccessLogService accessLogService;
    @Mock private SystemService systemService;

    @InjectMocks private LogServiceImpl logService;

    @Test
    @DisplayName("查询访问日志-成功")
    void queryAccessLogList_success() {
        AccessLogQueryVo vo = new AccessLogQueryVo();
        vo.setUserId("user-001");

        UserDto user = new UserDto();
        user.setUserAccount("admin");
        user.setFullName("管理员");
        when(systemService.findUserListByUserId("user-001")).thenReturn(ResponseResult.ok(List.of(user)));

        PageLogDto<AccessLogDto> pageLog = new PageLogDto<>();
        when(accessLogService.queryAccessLogList(any(), any())).thenReturn(pageLog);

        ResponseResult<PageLogDto<AccessLogDto>> result = logService.queryAccessLogList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询访问日志-无用户返回空Map")
    void queryAccessLogList_noUsers_returnsEmpty() {
        AccessLogQueryVo vo = new AccessLogQueryVo();
        vo.setUserId("user-001");

        when(systemService.findUserListByUserId("user-001")).thenReturn(ResponseResult.ok(Collections.emptyList()));

        PageLogDto<AccessLogDto> pageLog = new PageLogDto<>();
        when(accessLogService.queryAccessLogList(any(), any())).thenReturn(pageLog);

        ResponseResult<PageLogDto<AccessLogDto>> result = logService.queryAccessLogList(vo);
        assertTrue(result.isSuccess());
    }
}
