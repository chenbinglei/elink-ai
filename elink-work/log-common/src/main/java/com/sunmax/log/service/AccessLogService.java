package com.sunmax.log.service;

import com.sunmax.log.dto.AccessLogDto;
import com.sunmax.log.dto.PageLogDto;
import com.sunmax.log.vo.AccessLogQueryVo;

import java.util.Map;

public interface AccessLogService {

    /**
     * 查询日志管理
     * @param accessLogQueryVo 日志查询参数
     * @param userAccountNameMap 用户账号->用户名称
     * @return 日志查询列表
     */
    PageLogDto<AccessLogDto> queryAccessLogList(AccessLogQueryVo accessLogQueryVo, Map<String, String> userAccountNameMap);

}
