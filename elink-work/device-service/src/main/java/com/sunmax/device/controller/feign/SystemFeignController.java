package com.sunmax.device.controller.feign;

import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.device.SiteSetUpDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.service.SystemFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;

/**
 * @Author: yqz
 * @注释: 提供给系统管理服务调用的远程接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/system")
@Tag(name = "提供给系统管理服务调用的远程接口")
@Hidden()
public class SystemFeignController {

    @Autowired
    private SystemFeignService systemFeignService;

    @PostMapping("findAllSiteBasicInfoList")
    @Operation(summary = "查询全部站点详情列表")
    
    public ResponseResult<List<SiteInfoDto>> findAllSiteBasicInfoList(@RequestParam(required = false) String siteNameLike) {
        return systemFeignService.findAllSiteBasicInfoList(siteNameLike);
    }

    @PostMapping("findSiteBasicInfoByIds")
    @Operation(summary = "根据多个站点id查询站站点详情数据")
    
    public ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList) {
        return systemFeignService.findSiteBasicInfoByIds(siteIdList);
    }

    @PostMapping("findSiteSetUpBySiteIds")
    @Operation(summary = "根据多个站点id查询站站点设置数据")
    
    public ResponseResult<Map<String, SiteSetUpDto>> findSiteSetUpBySiteIds(@RequestBody List<String> siteIdList) {
        return systemFeignService.findSiteSetUpBySiteIds(siteIdList);
    }

    @PostMapping("findSiteInfoListByIds")
    @Operation(summary = "根据多个站点id查询站站点详情数据(只查询站点本体表结构数据，关于其他关联和设计别的表字段信息不查询返回,使用时看清楚)")
    
    public ResponseResult<List<SiteInfoDto>> findSiteInfoListByIds(@RequestBody List<String> siteIdList) {
        return systemFeignService.findSiteInfoListByIds(siteIdList);
    }
}
