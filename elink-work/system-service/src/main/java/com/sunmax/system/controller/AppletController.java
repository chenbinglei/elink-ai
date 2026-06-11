package com.sunmax.system.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.dto.AppletDetailDto;
import com.sunmax.system.dto.AppletListDto;
import com.sunmax.system.service.AppletService;
import com.sunmax.system.vo.AppletChangeVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 企业管理控制层
 */
@RestController
@CrossOrigin
@RequestMapping("applet")
@Tag(name = "小程序管理")
public class AppletController {

    @Autowired
    private AppletService appletService;

    @PostMapping("saveApplet")
    @Operation(summary = "新增或编辑小程序信息")
    
    public ResponseResult<Void> saveApplet(AppletChangeVo appletChangeVo, MultipartFile appletLogo, MultipartFile tencentImage) {
        return appletService.saveApplet(appletChangeVo, appletLogo, tencentImage);
    }

    @PostMapping("queryAppletList")
    @Operation(summary = "查询小程序列表")
    
    public ResponseResult<List<AppletListDto>> queryAppletList(String keyword) {
        return appletService.queryAppletList(keyword);
    }

    @PostMapping("findAppletDetailById")
    @Operation(summary = "根据主键id查询小程序详情数据")
    
    public ResponseResult<AppletDetailDto> findAppletDetailById(String id) {
        return appletService.findAppletDetailById(id);
    }

}
