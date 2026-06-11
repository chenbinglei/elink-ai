package com.sunmax.crontab.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.crontab.dto.DataQueryDto;
import com.sunmax.crontab.service.DataQueryService;
import com.sunmax.crontab.vo.DataQueryVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据查询管理
 */
@RestController
@CrossOrigin
@RequestMapping("dataQuery")
@Tag(name = "数据查询管理")
public class DataQueryController {

    @Autowired
    private DataQueryService dataQueryService;

    @PostMapping("findDataQueryList")
    @Operation(summary = "查询图表数据")
    
    public ResponseResult<DataQueryDto> findDataQueryList(DataQueryVo dataQueryVo) {
        return dataQueryService.findDataQueryList(dataQueryVo);
    }
}
