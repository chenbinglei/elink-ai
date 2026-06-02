package com.sunmax.together.controller.feign;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.operate.OccupyPileRateDto;
import com.sunmax.common.dto.together.UserGroupInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.mqtt.web.response.EventRateReqPublicVo;
import com.sunmax.together.service.operation.AppletUserService;
import com.sunmax.together.service.operation.SiteInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/feign/protocol")
@Api(tags = "提供给协议服务调用的远程接口")
@ApiIgnore()
public class ProtocolFeignController {

    @Autowired
    private SiteInfoService siteInfoService;

    @Autowired
    private AppletUserService appletUserService;

    @PostMapping("queryUserDiscountByPhoneNum")
    @ApiOperation("根据电桩编码和用户手机号查询用户折扣")
    @ApiOperationSupport(order = 1)
    public ResponseResult<UserGroupInfoDto> queryUserDiscountByPhoneNum(@RequestParam String pileCode, @RequestParam String phoneNum) {
        return appletUserService.queryUserDiscountByPhoneNum(pileCode, phoneNum);
    }

    @PostMapping("checkAccountCode")
    @ApiOperation("校验账号是否在白名单中")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pileCode", value = "电桩编码", dataType = "String", required = true),
            @ApiImplicitParam(name = "accountCode", value = "账号", paramType = "query", required = true)
    })
    public ResponseResult<Boolean> checkAccountCode(@RequestParam String pileCode,@RequestParam String accountCode) {
        return siteInfoService.checkAccountCode(pileCode, accountCode);
    }

    @PostMapping("findOccupyPileRateByPileCodes")
    @ApiOperation("根据多个电桩编码查询占桩计费数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Map<String, OccupyPileRateDto>> findOccupyPileRateByPileCodes(@RequestBody List<String> pileCodes) {
        return siteInfoService.findOccupyPileRateByPileCodes(pileCodes);
    }

    @PostMapping("findChargerRateListByPileCodes")
    @ApiOperation("根据多个电桩编码查询充放电费率列表")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pileCodes", value = "多个电桩编码", dataType = "String", required = true),
            @ApiImplicitParam(name = "priceType", value = "价格类型 1-充电 2-放电", paramType = "query", required = true)
    })
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPileCodes(@RequestBody List<String> pileCodes, @RequestParam Integer priceType) {
        return siteInfoService.findChargerRateListByPileCodes(pileCodes, priceType);
    }

    @PostMapping("findSiteRateInfoByPileCodes")
    @ApiOperation("根据多个电桩编码查询站点充放电费率")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParam(name = "pileCodes", value = "多个电桩编码", dataType = "String", required = true)
    public ResponseResult<Map<String, EventRateReqPublicVo>> findSiteRateInfoByPileCodes(@RequestBody List<String> pileCodes) {
        return siteInfoService.findSiteRateInfoByPileCodes(pileCodes);
    }

    @PostMapping("findChargerRateListByPriceInfoIds")
    @ApiOperation("根据多个费率id查询充放电费率列表")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "priceInfoIds", value = "多个费率id", dataType = "String", required = true)
    })
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPriceInfoIds(@RequestBody List<String> priceInfoIds) {
        return siteInfoService.findChargerRateListByPriceInfoIds(priceInfoIds);
    }

}
