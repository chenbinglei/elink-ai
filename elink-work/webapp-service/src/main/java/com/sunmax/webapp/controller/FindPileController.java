package com.sunmax.webapp.controller;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.webapp.ChargePriceInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.webapp.dto.ChargeDeviceInfoDto;
import com.sunmax.webapp.dto.SiteDetailsDataDto;
import com.sunmax.webapp.dto.SitePileDetailsDto;
import com.sunmax.webapp.service.FindPileService;
import com.sunmax.webapp.vo.SiteQueryVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("findPile")
@Api(tags = "找桩管理")
public class FindPileController {

    @Autowired
    private FindPileService findPileService;

    @PostMapping("querySiteList")
    @ApiOperation(value = "查询站点列表")
    @ApiOperationSupport(order = 1)
    public ResponseResult<?> querySiteList(SiteQueryVo siteQueryVo) {
        return findPileService.querySiteList(siteQueryVo);
    }

    @PostMapping("querySiteDetailsById")
    @ApiOperation(value = "根据站点id查询详情信息")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点唯一id", paramType = "query", required = true)
    })
    public ResponseResult<SiteDetailsDataDto> querySiteDetailsById(String siteId) {
        return findPileService.querySiteDetailsById(siteId);
    }

    @PostMapping("querySitePileDetailsById")
    @ApiOperation(value = "根据站点id查询站点电桩详情信息")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点唯一id", paramType = "query", required = true)
    })
    public ResponseResult<SitePileDetailsDto> querySitePileDetailsById(String siteId) {
        return findPileService.querySitePileDetailsById(siteId);
    }

    @PostMapping("findBillStrategyById")
    @ApiOperation(value = "根据站点id查询站点充放电计费策略数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点唯一id", paramType = "query", required = true),
            @ApiImplicitParam(name = "priceType", value = "价格类型 1-充电 2-放电", paramType = "query", required = true)
    })
    public ResponseResult<List<ChargerPriceRateDto>> findBillStrategyById(String siteId, Integer priceType) {
        return findPileService.findBillStrategyById(siteId, priceType);
    }

    @PostMapping(value = "queryDeviceInfoByPileCode")
    @ApiOperation("输入终端编号获取设备详情")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pileCode", value = "电桩编码", paramType = "query", required = true),
            @ApiImplicitParam(name = "appletUserId", value = "小程序用户id", paramType = "query", required = true),
            @ApiImplicitParam(name = "appletKey", value = "小程序登录标识", paramType = "query", required = true)
    })
    public ResponseResult<ChargeDeviceInfoDto> queryDeviceInfoByPileCode(String pileCode, String appletUserId, String appletKey) {
        /*appletUserId = SecretUtil.desEncrypt(appletUserId);
        if (StringUtil.isEmpty(appletUserId)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }*/
        return findPileService.queryDeviceInfoByPileCode(pileCode, appletUserId, appletKey);
    }

    @PostMapping("findDevicePriceById")
    @ApiOperation("根据设备id查询充放电和占桩价格信息")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true),
            @ApiImplicitParam(name = "priceType", value = "价格类型 1-充电 2-放电", paramType = "query", required = true)
    })
    public ResponseResult<ChargePriceInfoDto> findDevicePriceById(String deviceId, Integer priceType) {
        return findPileService.findDevicePriceById(deviceId, priceType);
    }
}
