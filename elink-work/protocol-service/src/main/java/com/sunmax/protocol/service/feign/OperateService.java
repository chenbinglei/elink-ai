package com.sunmax.protocol.service.feign;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.operate.OccupyPileRateDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.mqtt.web.response.EventRateReqPublicVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "operate-service")
@RestController
@RequestMapping("/operate/feign/protocol")
public interface OperateService {

    @PostMapping("findSiteRateInfoByPileCodes")
    @ApiOperation("根据多个电桩编码查询站点充放电费率")
    @ApiOperationSupport(order = 1)
    ResponseResult<Map<String, EventRateReqPublicVo>> findSiteRateInfoByPileCodes(@RequestBody List<String> pileCodes);

    @PostMapping("checkAccountCode")
    @ApiOperation("校验账号是否在白名单中")
    @ApiOperationSupport(order = 2)
    ResponseResult<Boolean> checkAccountCode(@RequestParam String pileCode, @RequestParam String accountCode);

    @PostMapping("findOccupyPileRateByPileCodes")
    @ApiOperation("根据多个电桩编码查询占桩计费数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<Map<String, OccupyPileRateDto>> findOccupyPileRateByPileCodes(@RequestBody List<String> pileCodes);

    @PostMapping("findChargerRateListByPileCodes")
    @ApiOperation("根据多个电桩编码查询充放电费率列表")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pileCodes", value = "多个电桩编码", dataType = "String", required = true),
            @ApiImplicitParam(name = "priceType", value = "价格类型 1-充电 2-放电", paramType = "query", required = true)
    })
    ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPileCodes(@RequestBody List<String> pileCodes, @RequestParam Integer priceType);

}
