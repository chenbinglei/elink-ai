package com.sunmax.webapp.service.feign;

import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.together.*;
import com.sunmax.common.dto.webapp.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.InvoiceTitleVo;
import com.sunmax.common.vo.together.*;
import com.sunmax.common.vo.webapp.OrderInvoicesVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(value = "together-service", path = "/together/feign/swebapp")
public interface TogetherService {

    @PostMapping(value = "generateOrderNum")
    @Operation(summary = "生成订单号")
    
    ResponseResult<String> generateOrderNum(@RequestParam String pileCode, @RequestParam String serialNumber);

    @PostMapping(value = "findAppletUserByIds")
    @Operation(summary = "根据多个小程序用户id查询小程序用户信息")
    
    ResponseResult<Map<String, AppletUserInfoDto>> findAppletUserByIds(@RequestBody Set<String> appletUserIds);

    @PostMapping(value = "createPileOrder")
    @Operation(summary = "创建电桩充放电订单")
    
    ResponseResult<Void> createPileOrder(@RequestBody OrderChangeVo orderChangeVo);

    @PostMapping(value = "findSiteAccountListBySiteIds")
    @Operation(summary = "根据多个站点id和类型查询站点账户信息")
    
    ResponseResult<Map<String, List<SiteAccountDto>>> findSiteAccountListBySiteIds(@RequestBody Set<String> siteIds, @RequestParam Integer type);

    @PostMapping(value = "updatePileFailOrder")
    @Operation(summary = "更新电桩充放电失败订单")
    
    ResponseResult<Void> updatePileFailOrder(@RequestBody OrderUpdateVo orderUpdateVo);

    @PostMapping("findChargerRateListBySiteIds")
    @Operation(summary = "根据多个站点id查询充放电费率列表")
    
    @Parameters({
            @Parameter(name = "siteIds", description = "多个站点id"),
            @Parameter(name = "priceType", description = "价格类型 1-充电 2-放电"),
            @Parameter(name = "deviceType", description = "设备类型 1-直流 2-交流")
    })
    ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListBySiteIds(@RequestBody List<String> siteIds, @RequestParam Integer priceType, @RequestParam Integer deviceType);

    @PostMapping("findDevicePriceById")
    @Operation(summary = "根据设备id查询充放电和占桩价格信息")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备id"),
            @Parameter(name = "priceType", description = "价格类型 1-充电 2-放电")
    })
    ResponseResult<ChargePriceInfoDto> findDevicePriceById(@RequestParam String deviceId, @RequestParam Integer priceType);

    @PostMapping("updateAppletUser")
    @Operation(summary = "编辑小程序用户")
    
    ResponseResult<String> updateAppletUser(@RequestBody AppletUserChangeVo appletUserChangeVo);

    @PostMapping("findOrderDetailByOrderNum")
    @Operation(summary = "根据订单编号查询订单详情信息")
    
    ResponseResult<OrderDetailDto> findOrderDetailByOrderNum(@RequestParam String orderNum);

    @PostMapping("findOrderListByAccountData")
    @Operation(summary = "根据账号数据查询指定类型订单列表")
    
    ResponseResult<List<OrderInfoDto>> findOrderListByAccountData(@RequestBody List<String> accountDataList, @RequestParam(required = false) Integer runMode);

    @PostMapping("findAppInHandOrderListByAppletUserId")
    @Operation(summary = "根据小程序用户id查询进行中的订单列表")
    
    ResponseResult<List<AppInHandOrderDto>> findAppInHandOrderListByAppletUserId(@RequestParam String appletUserId, @RequestParam(required = false) Integer orderType);

    @PostMapping("findSiteWhiteRosterById")
    @Operation(summary = "根据站点id查询白名单信息")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "queryType", description = "查询类型 1-用户手机号 2-车辆vin码"),
            @Parameter(name = "queryData", description = "查询数据")
    })
    ResponseResult<SiteRosterInfoDto> findSiteWhiteRosterById(@RequestParam String siteId, @RequestParam(required = false) Integer queryType, @RequestParam(required = false) String queryData);

    @PostMapping("findChargerRateListByPriceInfoIds")
    @Operation(summary = "根据多个费率id查询充放电费率列表")
    
    @Parameters({
            @Parameter(name = "priceInfoIds", description = "多个费率id")
    })
    ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPriceInfoIds(@RequestBody List<String> priceInfoIds);

    @PostMapping("queryAppletUserInfoByPhoneNum")
    @Operation(summary = "根据小程序用户手机号查询用户信息")
    
    ResponseResult<AppletUserInfoDto> queryAppletUserInfoByPhoneNum(@RequestParam String phoneNum);

    @PostMapping(value = "updateUserDisWallet")
    @Operation(summary = "更新用户V2G钱包")
    
    ResponseResult<BigDecimal> updateUserDisWallet(@RequestBody UserDisWalletChangeVo userDisWalletVo);

    @PostMapping(value = "saveRefundRecord")
    @Operation(summary = "保存退款记录")
    
    ResponseResult<String> saveRefundRecord(@RequestBody RefundRecordChangeVo refundRecordChangeVo);

    @PostMapping("findUserDisWalletListById")
    @Operation(summary = "根据小程序用户id查询用户V2G钱包列表")
    
    @Parameter(name = "appletUserId", description = "小程序用户唯一id")
    ResponseResult<List<UserDisWalletDto>> findUserDisWalletListById(@RequestParam String appletUserId);

    @PostMapping("submitAppletCancel")
    @Operation(summary = "提交小程序用户注销申请")
    
    @Parameters({
            @Parameter(name = "appletUserId", description = "小程序用户id"),
            @Parameter(name = "appletName", description = "小程序名称")
    })
    ResponseResult<String> submitAppletCancel(@RequestParam String appletUserId, @RequestParam String appletName);

    @PostMapping("updateAppletUserPhoneById")
    @Operation(summary = "根据小程序用户id更新手机号")
    
    ResponseResult<String> updateAppletUserPhoneById(@RequestParam String appletUserId, @RequestParam String phoneNum);

    @PostMapping("findOrderListBySiteIdS")
    @Operation(summary = "根据多个站点id查询订单数据")
    
    ResponseResult<List<OrderInfoDto>> findOrderListBySiteIdS(@RequestBody List<String> siteIdList, @RequestParam(required = false) String accountData, @RequestParam(required = false) Integer runMode);

    @PostMapping("saveInvoiceTitle")
    @Operation(summary = "添加或编辑发票抬头")
    
    ResponseResult<Void> saveInvoiceTitle(@RequestBody InvoiceTitleVo invoiceTitleVo);

    @PostMapping("deleteInvoiceTitleById")
    @Operation(summary = "根据主键id删除发票抬头")
    
    ResponseResult<Void> deleteInvoiceTitleById(@RequestParam String id);

    @PostMapping("findInvoiceTitleList")
    @Operation(summary = "根据小程序用户id查询发票抬头列表")
    
    ResponseResult<List<InvoiceTitleDto>> findInvoiceTitleList(@RequestParam String appletUserId);

    @PostMapping("findOrderAppShowList")
    @Operation(summary = "根据查询条件查询订单展示列表")
    
    ResponseResult<List<OrderAppShowDto>> findOrderAppShowList(@RequestParam String appletUserId, @RequestParam(required = false) String queryDate);

    @PostMapping("applyInvoice")
    @Operation(summary = "申请开票")
    
    ResponseResult<Void> applyInvoice(@RequestBody OrderInvoicesVo orderInvoicesVo);

    @PostMapping("findInvoiceRecordList")
    @Operation(summary = "根据小程序用户id查询开票记录列表")
    
    ResponseResult<List<AppInvoiceListDto>> findInvoiceRecordList(@RequestParam String appletUserId);

    @PostMapping("findInvoiceDetailById")
    @Operation(summary = "根据发票申请单号查询开票详情数据")
    
    ResponseResult<AppInvoiceDetailDto> findInvoiceDetailById(@RequestParam String id);

    @PostMapping("revokeInvoice")
    @Operation(summary = "撤销开票")
    
    ResponseResult<Void> revokeInvoice(@RequestParam String id);

}
