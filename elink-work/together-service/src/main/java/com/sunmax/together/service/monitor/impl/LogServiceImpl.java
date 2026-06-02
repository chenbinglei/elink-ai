package com.sunmax.together.service.monitor.impl;

import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.log.dto.AccessLogDto;
import com.sunmax.log.dto.PageLogDto;
import com.sunmax.log.service.AccessLogService;
import com.sunmax.log.vo.AccessLogQueryVo;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.monitor.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private SystemService systemService;

    @Override
    public ResponseResult<PageLogDto<AccessLogDto>> queryAccessLogList(AccessLogQueryVo accessLogQueryVo) {
        //根据用户id查询租户下面的用户账号数据
        Map<String, String> userAccountNameMap = systemService.findUserListByUserId(accessLogQueryVo.getUserId()).getData()
                .stream().filter(u -> StringUtil.isNotEmpty(u.getUserAccount()))
                .collect(Collectors.toMap(UserDto::getUserAccount, UserDto::getFullName, (k1, k2) -> k1));
        //根据查询条件查询日志记录
        return ResponseResult.ok(accessLogService.queryAccessLogList(accessLogQueryVo, userAccountNameMap));
    }

}
