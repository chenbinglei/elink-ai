package com.sunmax.system.service.feign;

import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.device.SiteSetUpDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "device-service")
@RestController
@RequestMapping("/device/feign/system")
public interface DeviceService {

    @PostMapping("findAllSiteBasicInfoList")
    @ApiOperation("查询全部站点详情列表")
    @ApiOperationSupport(order = 1)
    ResponseResult<List<SiteInfoDto>> findAllSiteBasicInfoList(@RequestParam(required = false) String siteNameLike);

    @PostMapping("findSiteBasicInfoByIds")
    @ApiOperation("根据多个站点id查询站站点详情数据")
    @ApiOperationSupport(order = 2)
    ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList);

    @PostMapping("findSiteSetUpBySiteIds")
    @ApiOperation("根据多个站点id查询站站点设置数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<Map<String, SiteSetUpDto>> findSiteSetUpBySiteIds(@RequestBody List<String> siteIdList);

    @PostMapping("findSiteInfoListByIds")
    @ApiOperation("根据多个站点id查询站站点详情数据(只查询站点本体表结构数据，关于其他关联和设计别的表字段信息不查询返回,使用时看清楚)")
    @ApiOperationSupport(order = 4)
    ResponseResult<List<SiteInfoDto>> findSiteInfoListByIds(@RequestBody List<String> siteIdList);
}
