package com.sunmax.configure.service.feign;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.operate.InterflowOrderRecordDto;
import com.sunmax.common.dto.operate.OrderRecordDto;
import com.sunmax.common.dto.together.SystemDeviceListDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 接收聚合服务提供的接口
 */
@FeignClient(value = "together-service")
@RestController
@RequestMapping("/together/feign/configure")
public interface TogetherService {

    @PostMapping("queryOrderRecordByOrderCode")
    @ApiOperation("根据订单号查询订单信息")
    @ApiOperationSupport(order = 1)
    ResponseResult<InterflowOrderRecordDto> queryOrderRecordByOrderCode(@RequestParam String orderNum);

    @PostMapping("saveOrUpdateOrderRecord")
    @ApiOperation("保存或编辑订单记录信息")
    @ApiOperationSupport(order = 2)
    ResponseResult<String> saveOrUpdateOrderRecord(@RequestBody InterflowOrderRecordDto interflowOrderRecordDto);

    @PostMapping("queryOrderRecordByPileCodes")
    @ApiOperation("根据多个电桩编码查询订单记录数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<Map<String, List<OrderRecordDto>>> queryOrderRecordByPileCodes(@RequestBody List<String> pileCodeList,
                                                                                  @RequestParam(required = false) String startTime,
                                                                                  @RequestParam(required = false) String endTime);

    @PostMapping("findChargerRateListBySiteIds")
    @ApiOperation("根据多个站点id查询充放电费率列表")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteIds", value = "多个站点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "priceType", value = "价格类型 1-充电 2-放电", paramType = "query", required = true)
    })
    ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListBySiteIds(@RequestBody List<String> siteIds, @RequestParam Integer priceType);

    @PostMapping("querySystemDeviceList")
    @ApiOperation("查询系统设备列表")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", paramType = "query", required = true)
    })
    ResponseResult<SystemDeviceListDto> querySystemDeviceList(@RequestParam String siteId);
}
