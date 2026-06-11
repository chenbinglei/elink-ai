package com.sunmax.webapp.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.webapp.ChargePriceInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.webapp.dto.ChargeDeviceInfoDto;
import com.sunmax.webapp.dto.SiteDetailsDataDto;
import com.sunmax.webapp.dto.SitePileDetailsDto;
import com.sunmax.webapp.service.FindPileService;
import com.sunmax.webapp.vo.SiteQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("findPile")
@Tag(name = "找桩管理")
public class FindPileController {

    @Autowired
    private FindPileService findPileService;

    @PostMapping("querySiteList")
    @Operation(summary = "查询站点列表")
    
    public ResponseResult<?> querySiteList(SiteQueryVo siteQueryVo) {
        return findPileService.querySiteList(siteQueryVo);
    }

    @PostMapping("querySiteDetailsById")
    @Operation(summary = "根据站点id查询详情信息")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点唯一id")
    })
    public ResponseResult<SiteDetailsDataDto> querySiteDetailsById(String siteId) {
        return findPileService.querySiteDetailsById(siteId);
    }

    @PostMapping("querySitePileDetailsById")
    @Operation(summary = "根据站点id查询站点电桩详情信息")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点唯一id")
    })
    public ResponseResult<SitePileDetailsDto> querySitePileDetailsById(String siteId) {
        return findPileService.querySitePileDetailsById(siteId);
    }

    @PostMapping("findBillStrategyById")
    @Operation(summary = "根据站点id查询站点充放电计费策略数据")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点唯一id"),
            @Parameter(name = "priceType", description = "价格类型 1-充电 2-放电")
    })
    public ResponseResult<List<ChargerPriceRateDto>> findBillStrategyById(String siteId, Integer priceType) {
        return findPileService.findBillStrategyById(siteId, priceType);
    }

    @PostMapping(value = "queryDeviceInfoByPileCode")
    @Operation(summary = "输入终端编号获取设备详情")
    
    @Parameters({
            @Parameter(name = "pileCode", description = "电桩编码"),
            @Parameter(name = "appletUserId", description = "小程序用户id"),
            @Parameter(name = "appletKey", description = "小程序登录标识")
    })
    public ResponseResult<ChargeDeviceInfoDto> queryDeviceInfoByPileCode(String pileCode, String appletUserId, String appletKey) {
        /*appletUserId = SecretUtil.desEncrypt(appletUserId);
        if (StringUtil.isEmpty(appletUserId)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }*/
        return findPileService.queryDeviceInfoByPileCode(pileCode, appletUserId, appletKey);
    }

    @PostMapping("findDevicePriceById")
    @Operation(summary = "根据设备id查询充放电和占桩价格信息")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id"),
            @Parameter(name = "priceType", description = "价格类型 1-充电 2-放电")
    })
    public ResponseResult<ChargePriceInfoDto> findDevicePriceById(String deviceId, Integer priceType) {
        return findPileService.findDevicePriceById(deviceId, priceType);
    }
}
