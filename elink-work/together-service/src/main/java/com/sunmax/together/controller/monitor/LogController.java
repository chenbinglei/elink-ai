package com.sunmax.together.controller.monitor;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.dto.AccessLogDto;
import com.sunmax.log.dto.PageLogDto;
import com.sunmax.log.vo.AccessLogQueryVo;
import com.sunmax.together.service.monitor.LogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("log")
@Tag(name = "日志管理控制层")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping("queryAccessLogList")
    @Operation(summary = "查询日志列表数据")
    
    public ResponseResult<PageLogDto<AccessLogDto>> queryAccessLogList(AccessLogQueryVo accessLogQueryVo) {
        return logService.queryAccessLogList(accessLogQueryVo);
    }

}
