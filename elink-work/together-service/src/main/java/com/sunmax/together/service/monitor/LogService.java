package com.sunmax.together.service.monitor;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.dto.AccessLogDto;
import com.sunmax.log.dto.PageLogDto;
import com.sunmax.log.vo.AccessLogQueryVo;

public interface LogService {

    /**
     * 查询访问日志列表
     * @param accessLogQueryVo 查询参数
     * @return 访问日志列表
     */
    ResponseResult<PageLogDto<AccessLogDto>> queryAccessLogList(AccessLogQueryVo accessLogQueryVo);

}
