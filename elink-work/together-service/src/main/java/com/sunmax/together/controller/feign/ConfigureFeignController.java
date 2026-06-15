package com.sunmax.together.controller.feign;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.operate.InterflowOrderRecordDto;
import com.sunmax.common.dto.operate.OrderRecordDto;
import com.sunmax.common.dto.together.SystemDeviceListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.service.operation.OrderRecordService;
import com.sunmax.together.service.operation.SiteInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/feign/configure")
@Tag(name = "提供给配置服务调用的远程接口")
@Hidden()
public class ConfigureFeignController {

    @Autowired
    private OrderRecordService orderRecordService;

    @Autowired
    private SiteInfoService siteInfoService;

    @PostMapping("queryOrderRecordByOrderCode")
    @Operation(summary = "根据订单号查询订单信息")
    
    public ResponseResult<InterflowOrderRecordDto> queryOrderRecordByOrderCode(@RequestParam String orderNum) {
        return orderRecordService.queryOrderRecordByOrderCode(orderNum);
    }

    @PostMapping("saveOrUpdateOrderRecord")
    @Operation(summary = "保存或编辑订单记录信息")
    
    public ResponseResult<String> saveOrUpdateOrderRecord(@RequestBody InterflowOrderRecordDto interflowOrderRecordDto) {
        return orderRecordService.saveOrUpdateOrderRecord(interflowOrderRecordDto);
    }

    @PostMapping("queryOrderRecordByPileCodes")
    @Operation(summary = "根据多个电桩编码查询订单记录数据")
    
    public ResponseResult<Map<String, List<OrderRecordDto>>> queryOrderRecordByPileCodes(@RequestBody List<String> pileCodeList,
                                                                                         @RequestParam(required = false) String startTime,
                                                                                         @RequestParam(required = false) String endTime) {
        return orderRecordService.queryOrderRecordByPileCodes(pileCodeList, startTime, endTime);
    }

    @PostMapping("findChargerRateListBySiteIds")
    @Operation(summary = "根据多个站点id查询充放电费率列表")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "多个站点id"),
            @Parameter(name = "priceType", description = "价格类型 1-充电 2-放电")
    })
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListBySiteIds(@RequestBody List<String> siteIds, @RequestParam Integer priceType) {
        return siteInfoService.findChargerRateListBySiteIds(siteIds, priceType);
    }

    @PostMapping("querySystemDeviceList")
    @Operation(summary = "查询系统设备列表")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id")
    })
    public ResponseResult<SystemDeviceListDto> querySystemDeviceList(@RequestParam String siteId) {
        return siteInfoService.querySystemDeviceList(siteId);
    }
}
