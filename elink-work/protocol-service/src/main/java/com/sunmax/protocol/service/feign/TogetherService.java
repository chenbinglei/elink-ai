package com.sunmax.protocol.service.feign;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.operate.OccupyPileRateDto;
import com.sunmax.common.dto.together.UserGroupInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.mqtt.web.response.EventRateReqPublicVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 接收聚合服务提供的接口
 */
@FeignClient(value = "together-service", path = "/together/feign/protocol")
public interface TogetherService {

    @PostMapping("queryUserDiscountByPhoneNum")
    @Operation(summary = "根据电桩编码和用户手机号查询用户折扣")
    
    ResponseResult<UserGroupInfoDto> queryUserDiscountByPhoneNum(@RequestParam String pileCode, @RequestParam String phoneNum);

    @PostMapping("checkAccountCode")
    @Operation(summary = "校验账号是否在白名单中")
    
    @Parameters({
            @Parameter(name = "pileCode", description = "电桩编码"),
            @Parameter(name = "accountCode", description = "账号")
    })
    ResponseResult<Boolean> checkAccountCode(@RequestParam String pileCode, @RequestParam String accountCode);

    @PostMapping("findOccupyPileRateByPileCodes")
    @Operation(summary = "根据多个电桩编码查询占桩计费数据")
    
    ResponseResult<Map<String, OccupyPileRateDto>> findOccupyPileRateByPileCodes(@RequestBody List<String> pileCodes);

    @PostMapping("findChargerRateListByPileCodes")
    @Operation(summary = "根据多个电桩编码查询充放电费率列表")
    
    @Parameters({
            @Parameter(name = "pileCodes", description = "多个电桩编码"),
            @Parameter(name = "priceType", description = "价格类型 1-充电 2-放电")
    })
    ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPileCodes(@RequestBody List<String> pileCodes, @RequestParam Integer priceType);

    @PostMapping("findSiteRateInfoByPileCodes")
    @Operation(summary = "根据多个电桩编码查询站点充放电费率")
    
    @Parameter(name = "pileCodes", description = "多个电桩编码")
    ResponseResult<Map<String, EventRateReqPublicVo>> findSiteRateInfoByPileCodes(@RequestBody List<String> pileCodes);

    @PostMapping("findChargerRateListByPriceInfoIds")
    @Operation(summary = "根据多个费率id查询充放电费率列表")
    
    @Parameters({
            @Parameter(name = "priceInfoIds", description = "多个费率id")
    })
    ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPriceInfoIds(@RequestBody List<String> priceInfoIds);

}
