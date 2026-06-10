package com.sunmax.together.controller.feign;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.operate.OccupyPileRateDto;
import com.sunmax.common.dto.together.UserGroupInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.mqtt.web.response.EventRateReqPublicVo;
import com.sunmax.together.service.operation.AppletUserService;
import com.sunmax.together.service.operation.SiteInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/feign/protocol")
@Tag(name = "提供给协议服务调用的远程接口")
@Hidden()
public class ProtocolFeignController {

    @Autowired
    private SiteInfoService siteInfoService;

    @Autowired
    private AppletUserService appletUserService;

    @PostMapping("queryUserDiscountByPhoneNum")
    @Operation(summary = "根据电桩编码和用户手机号查询用户折扣")
    
    public ResponseResult<UserGroupInfoDto> queryUserDiscountByPhoneNum(@RequestParam String pileCode, @RequestParam String phoneNum) {
        return appletUserService.queryUserDiscountByPhoneNum(pileCode, phoneNum);
    }

    @PostMapping("checkAccountCode")
    @Operation(summary = "校验账号是否在白名单中")
    
    @Parameters({
            @Parameter(name = "pileCode", description = "电桩编码"),
            @Parameter(name = "accountCode", description = "账号")
    })
    public ResponseResult<Boolean> checkAccountCode(@RequestParam String pileCode,@RequestParam String accountCode) {
        return siteInfoService.checkAccountCode(pileCode, accountCode);
    }

    @PostMapping("findOccupyPileRateByPileCodes")
    @Operation(summary = "根据多个电桩编码查询占桩计费数据")
    
    public ResponseResult<Map<String, OccupyPileRateDto>> findOccupyPileRateByPileCodes(@RequestBody List<String> pileCodes) {
        return siteInfoService.findOccupyPileRateByPileCodes(pileCodes);
    }

    @PostMapping("findChargerRateListByPileCodes")
    @Operation(summary = "根据多个电桩编码查询充放电费率列表")
    
    @Parameters({
            @Parameter(name = "pileCodes", description = "多个电桩编码"),
            @Parameter(name = "priceType", description = "价格类型 1-充电 2-放电")
    })
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPileCodes(@RequestBody List<String> pileCodes, @RequestParam Integer priceType) {
        return siteInfoService.findChargerRateListByPileCodes(pileCodes, priceType);
    }

    @PostMapping("findSiteRateInfoByPileCodes")
    @Operation(summary = "根据多个电桩编码查询站点充放电费率")
    
    @Parameter(name = "pileCodes", description = "多个电桩编码")
    public ResponseResult<Map<String, EventRateReqPublicVo>> findSiteRateInfoByPileCodes(@RequestBody List<String> pileCodes) {
        return siteInfoService.findSiteRateInfoByPileCodes(pileCodes);
    }

    @PostMapping("findChargerRateListByPriceInfoIds")
    @Operation(summary = "根据多个费率id查询充放电费率列表")
    
    @Parameters({
            @Parameter(name = "priceInfoIds", description = "多个费率id")
    })
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPriceInfoIds(@RequestBody List<String> priceInfoIds) {
        return siteInfoService.findChargerRateListByPriceInfoIds(priceInfoIds);
    }

}
