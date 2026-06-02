package com.sunmax.together.service.operation;

import com.sunmax.common.dto.device.AffiliatesInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.operate.InterflowOrderRecordDto;
import com.sunmax.common.dto.operate.OccupyPileRecordDto;
import com.sunmax.common.dto.operate.OrderRecordDto;
import com.sunmax.common.dto.together.OrderDetailDto;
import com.sunmax.common.dto.webapp.AppInHandOrderDto;
import com.sunmax.common.dto.webapp.OrderInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.order.*;
import com.sunmax.together.vo.operation.orderRecord.OccupyPileRecordListQueryVo;
import com.sunmax.together.vo.operation.orderRecord.OrderRecordQueryVo;
import com.sunmax.together.vo.operation.orderRecord.OrderRefundVo;

import java.util.List;
import java.util.Map;

public interface OrderRecordService {

    /**
     * 分页查询订单记录列表信息
     *
     * @param orderRecordQueryVo
     * @return
     */
    ResponseResult<OrderRecordListDto> findOrderRecordListByPage(OrderRecordQueryVo orderRecordQueryVo);

    /**
     * 根据订单id查询基本信息
     * @param orderId
     * @return
     */
    ResponseResult<OrderRecordInfoDto> findOrderRecordInfoById(String orderId);

    /**
     * 分页查询占桩订单记录列表信息
     *
     * @param occupyPileRecordListQueryVo
     * @param userId
     * @return
     */
    ResponseResult<OccupyPileRecordListDto> findOccupyPileRecordListByPage(OccupyPileRecordListQueryVo occupyPileRecordListQueryVo, String userId);

    /**
     * 根据占桩订单id查询基本信息
     * @param occupyId
     * @return
     */
    ResponseResult<OccupyPileRecordInfoDto> findOccupyPileRecordInfoById(String occupyId);

    /**
     * 查询站点下拉列表
     *
     * @param tenantId
     * @param userId
     * @return
     */
    ResponseResult<List<SiteInfoDto>> findSiteBasicInfoByTenantId(String tenantId, String userId);

    /**
     * 根据多个充电桩编码查询订单充放电记录
     * @param pileCodeList 多个充电桩编码
     * @param startTime    开始时间(可为空)
     * @param endTime      结束时间(可为空)
     * @return
     */
    ResponseResult<List<OrderRecordDto>> findOrderRecordListByPileCodes(List<String> pileCodeList, String startTime, String endTime);

    /**
     * 根据多个订单记录id查询占桩订单记录
     * @param orderIdList  多个订单记录id
     * @param startTime    开始时间(可为空)
     * @param endTime      结束时间(可为空)
     * @return
     */
    ResponseResult<List<OccupyPileRecordDto>> findOccupyPileRecordListByOrderIds(List<String> orderIdList, String startTime, String endTime);

    /**
     * 根据充放电订单id查询过程分析曲线数据
     * @param orderId
     * @param functionLogoList
     * @return
     */
    ResponseResult<Map<String, Object>> findProcessAnalysisByOrderId(String orderId, List<String> functionLogoList);

    /**
     * 查询订单记录列表信息
     *
     * @param orderRecordQueryVo
     * @return
     */
    ResponseResult<List<OrderRecordExportDto>> findOrderRecordList(OrderRecordQueryVo orderRecordQueryVo);

    /**
     * 根据订单号查询订单信息
     * @param orderNum
     * @return
     */
    ResponseResult<InterflowOrderRecordDto> queryOrderRecordByOrderCode(String orderNum);

    /**
     * 保存或编辑订单记录信息
     * @param interflowOrderRecordDto
     * @return
     */
    ResponseResult<String> saveOrUpdateOrderRecord(InterflowOrderRecordDto interflowOrderRecordDto);

    /**
     * 根据多个电桩编码查询订单记录数据
     * @param pileCodeList
     * @param startTime
     * @param endTime
     * @return
     */
    ResponseResult<Map<String, List<OrderRecordDto>>> queryOrderRecordByPileCodes(List<String> pileCodeList, String startTime, String endTime);

    /**
     * 查询运营商下拉列表
     * @param userId
     * @return
     */
    ResponseResult<List<AffiliatesInfoDto>> findOperatorListByUserId(String userId);

    /**
     * 根据订单编号查询订单详情信息
     * @param orderNum 订单编号
     * @return
     */
    ResponseResult<OrderDetailDto> findOrderDetailByOrderNum(String orderNum);

    /**
     * 根据账号数据查询指定类型订单列表
     * @param accountDataList 多个账号数据 充/放电卡 / VIN码 / 手机号 / 小程序用户id
     * @param runMode 运行模式 0-充电订单 1-放电订单 (可为空)
     * @return
     */
    ResponseResult<List<OrderInfoDto>> findOrderListByAccountData(List<String> accountDataList, Integer runMode);

    /**
     * 根据小程序用户id查询进行中的订单列表
     * @param appletUserId 小程序用户id
     * @param orderType 订单类型 0-充电订单 1-放电订单 2-占用订单
     * @return
     */
    ResponseResult<List<AppInHandOrderDto>> findAppInHandOrderListByAppletUserId(String appletUserId, Integer orderType);

    /**
     * 校验站点密码是否正确
     * @param siteId 站点id
     * @param password 站点编码
     * @return
     */
    ResponseResult<Integer> checkSitePassword(String siteId, String password);

    /**
     * 根据订单id查询订单交易金额信息
     * @param orderId 订单id
     * @return 订单交易金额信息
     */
    ResponseResult<OrderTradeMoneyDto> findOrderTradeMoneyById(String orderId);

    /**
     * 订单退款
     * @param orderRefundVo 退款信息
     * @return 退款状态
     */
    ResponseResult<Void> orderRefund(OrderRefundVo orderRefundVo);

    /**
     * 更新订单状态
     * @param orderId 订单id
     * @param userId 用户id
     * @return 状态码
     */
    ResponseResult<Void> updateOrderStatus(String orderId, String userId);

    /**
     * 根据多个站点id查询订单数据
     * @param siteIdList 多个站点id
     * @param accountData 账号数据 充/放电卡 / VIN码 / 手机号 / 小程序用户id (可为空)
     * @param runMode 运行模式 0-充电订单 1-放电订单 (可为空)
     * @return
     */
    ResponseResult<List<OrderInfoDto>> findOrderListBySiteIdS(List<String> siteIdList, String accountData, Integer runMode);

}
