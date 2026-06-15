package com.sunmax.system.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.system.dto.CustomChangeDto;
import com.sunmax.system.service.SystemCustomService;
import com.sunmax.system.vo.CustomChangeVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("systemCustom")
@Tag(name = "系统自定义管理")
public class SystemCustomController {

    @Autowired
    private SystemCustomService systemCustomService;

    @PostMapping("saveSystemCustom")
    @Operation(summary = "新增或编辑系统自定义数据")
    @WebLog("系统自定义管理-新增或编辑系统自定义数据")
    
    public ResponseResult<Void> saveSystemCustom(CustomChangeVo customChangeVo, MultipartFile logoFile) {
        return systemCustomService.saveSystemCustom(customChangeVo, logoFile);
    }

    @PostMapping("queryLargeSetting")
    @Operation(summary = "查询系统自定义数据")
    
    public ResponseResult<CustomChangeDto> querySystemCustom() {
        return systemCustomService.querySystemCustom();
    }

}
