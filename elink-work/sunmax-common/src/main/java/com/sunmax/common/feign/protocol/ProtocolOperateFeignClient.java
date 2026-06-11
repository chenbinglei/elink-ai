package com.sunmax.common.feign.protocol;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.operate.OccupyPileRateDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.mqtt.web.response.EventRateReqPublicVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "operate-service", path = "/operate/feign/protocol", fallbackFactory = GenericFeignFallbackFactory.class)
public interface ProtocolOperateFeignClient {

    @PostMapping("findSiteRateInfoByPileCodes")
    @Operation(summary = "根据多个电桩编码查询站点充放电费率")
    
    ResponseResult<Map<String, EventRateReqPublicVo>> findSiteRateInfoByPileCodes(@RequestBody List<String> pileCodes);

    @PostMapping("checkAccountCode")
    @Operation(summary = "校验账号是否在白名单中")
    
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

}
