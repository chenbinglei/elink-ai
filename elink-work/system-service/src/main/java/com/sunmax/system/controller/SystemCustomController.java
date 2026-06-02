package com.sunmax.system.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.system.dto.CustomChangeDto;
import com.sunmax.system.service.SystemCustomService;
import com.sunmax.system.vo.CustomChangeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("systemCustom")
@Api(tags = "系统自定义管理")
public class SystemCustomController {

    @Autowired
    private SystemCustomService systemCustomService;

    @PostMapping("saveSystemCustom")
    @ApiOperation("新增或编辑系统自定义数据")
    @WebLog("系统自定义管理-新增或编辑系统自定义数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveSystemCustom(CustomChangeVo customChangeVo, MultipartFile logoFile) {
        return systemCustomService.saveSystemCustom(customChangeVo, logoFile);
    }

    @PostMapping("queryLargeSetting")
    @ApiOperation("查询系统自定义数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<CustomChangeDto> querySystemCustom() {
        return systemCustomService.querySystemCustom();
    }

}
