package com.sunmax.configure.service.feign;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.operate.InterflowOrderRecordDto;
import com.sunmax.common.dto.operate.OrderRecordDto;
import com.sunmax.common.dto.together.SystemDeviceListDto;
import com.sunmax.common.util.ResponseResult;
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
@FeignClient(value = "together-service", path = "/together/feign/configure")
public interface TogetherService {

    @PostMapping("queryOrderRecordByOrderCode")
    @Operation(summary = "根据订单号查询订单信息")
    
    ResponseResult<InterflowOrderRecordDto> queryOrderRecordByOrderCode(@RequestParam String orderNum);

    @PostMapping("saveOrUpdateOrderRecord")
    @Operation(summary = "保存或编辑订单记录信息")
    
    ResponseResult<String> saveOrUpdateOrderRecord(@RequestBody InterflowOrderRecordDto interflowOrderRecordDto);

    @PostMapping("queryOrderRecordByPileCodes")
    @Operation(summary = "根据多个电桩编码查询订单记录数据")
    
    ResponseResult<Map<String, List<OrderRecordDto>>> queryOrderRecordByPileCodes(@RequestBody List<String> pileCodeList,
                                                                                  @RequestParam(required = false) String startTime,
                                                                                  @RequestParam(required = false) String endTime);

    @PostMapping("findChargerRateListBySiteIds")
    @Operation(summary = "根据多个站点id查询充放电费率列表")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "多个站点id"),
            @Parameter(name = "priceType", description = "价格类型 1-充电 2-放电")
    })
    ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListBySiteIds(@RequestBody List<String> siteIds, @RequestParam Integer priceType);

    @PostMapping("querySystemDeviceList")
    @Operation(summary = "查询系统设备列表")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    ResponseResult<SystemDeviceListDto> querySystemDeviceList(@RequestParam String siteId);
}
