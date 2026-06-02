package com.sunmax.together.controller.monitor;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.dto.AccessLogDto;
import com.sunmax.log.dto.PageLogDto;
import com.sunmax.log.vo.AccessLogQueryVo;
import com.sunmax.together.service.monitor.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("log")
@Api(tags = "日志管理控制层")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping("queryAccessLogList")
    @ApiOperation("查询日志列表数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<PageLogDto<AccessLogDto>> queryAccessLogList(AccessLogQueryVo accessLogQueryVo) {
        return logService.queryAccessLogList(accessLogQueryVo);
    }

}
