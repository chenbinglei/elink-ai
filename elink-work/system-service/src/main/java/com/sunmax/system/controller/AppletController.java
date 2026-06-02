package com.sunmax.system.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.dto.AppletDetailDto;
import com.sunmax.system.dto.AppletListDto;
import com.sunmax.system.service.AppletService;
import com.sunmax.system.vo.AppletChangeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
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
@Api(tags = "小程序管理")
public class AppletController {

    @Autowired
    private AppletService appletService;

    @PostMapping("saveApplet")
    @ApiOperation("新增或编辑小程序信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveApplet(AppletChangeVo appletChangeVo, MultipartFile appletLogo, MultipartFile tencentImage) {
        return appletService.saveApplet(appletChangeVo, appletLogo, tencentImage);
    }

    @PostMapping("queryAppletList")
    @ApiOperation("查询小程序列表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<List<AppletListDto>> queryAppletList(String keyword) {
        return appletService.queryAppletList(keyword);
    }

    @PostMapping("findAppletDetailById")
    @ApiOperation("根据主键id查询小程序详情数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<AppletDetailDto> findAppletDetailById(String id) {
        return appletService.findAppletDetailById(id);
    }

}
