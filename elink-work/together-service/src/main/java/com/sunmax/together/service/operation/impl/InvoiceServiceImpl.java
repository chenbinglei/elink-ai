package com.sunmax.together.service.operation.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.dto.together.AppInvoiceDetailDto;
import com.sunmax.common.dto.together.AppInvoiceListDto;
import com.sunmax.common.dto.together.InvoiceTitleDto;
import com.sunmax.common.dto.webapp.OrderAppShowDto;
import com.sunmax.common.dto.webapp.RechargeTradeDto;
import com.sunmax.common.entity.BaseTimeEntity;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.InvoiceTitleVo;
import com.sunmax.common.vo.webapp.OrderInvoicesVo;
import com.sunmax.together.dao.AppletUserDao;
import com.sunmax.together.dao.SiteAccountDao;
import com.sunmax.together.dao.invoice.InvoiceDao;
import com.sunmax.together.dao.invoice.InvoiceOrderDao;
import com.sunmax.together.dao.invoice.InvoiceRecordDao;
import com.sunmax.together.dao.invoice.InvoiceTitleDao;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.dao.order.SettlementRecordDao;
import com.sunmax.together.dto.operation.invoice.InvoiceDetailDto;
import com.sunmax.together.dto.operation.invoice.InvoiceListDto;
import com.sunmax.together.dto.operation.invoice.InvoiceOrderDto;
import com.sunmax.together.dto.operation.invoice.InvoiceRecordDto;
import com.sunmax.together.entity.AppletUserEntity;
import com.sunmax.together.entity.SiteAccountEntity;
import com.sunmax.together.entity.invoice.InvoiceEntity;
import com.sunmax.together.entity.invoice.InvoiceOrderEntity;
import com.sunmax.together.entity.invoice.InvoiceRecordEntity;
import com.sunmax.together.entity.invoice.InvoiceTitleEntity;
import com.sunmax.together.entity.order.OrderRecordEntity;
import com.sunmax.together.entity.order.SettlementRecordEntity;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.feign.WebAppService;
import com.sunmax.together.service.operation.InvoiceService;
import com.sunmax.together.util.EmailUtil;
import com.sunmax.together.vo.operation.invoice.InvoiceQueryVo;
import com.sunmax.together.vo.operation.invoice.InvoiceRecordVo;
import com.sunmax.together.vo.operation.invoice.InvoiceStatusVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    @Resource
    private InvoiceDao invoiceDao;

    @Resource
    private InvoiceOrderDao invoiceOrderDao;

    @Resource
    private InvoiceRecordDao invoiceRecordDao;

    @Resource
    private SettlementRecordDao settlementRecordDao;

    @Resource
    private OrderRecordDao orderRecordDao;

    @Resource
    private DeviceService deviceService;

    @Resource
    private InvoiceTitleDao invoiceTitleDao;

    @Resource
    private AppletUserDao appletUserDao;

    @Resource
    private WebAppService webAppService;

    @Resource
    private SystemService systemService;

    @Resource
    private EmailUtil emailUtil;

    @Resource
    private SiteAccountDao siteAccountDao;

    @Override
    public ResponseResult<PageDto<InvoiceListDto>> queryInvoiceList(InvoiceQueryVo invoiceQueryVo) {
        //返回的集合
        List<InvoiceListDto> resultList = Lists.newArrayList();
        //根据用户id查询用户下面的账号id
        Map<String, AccountDto> accountMap = Maps.newHashMap();
        UserDto user = systemService.findUserInfoByIdsFeign(Collections.singletonList(invoiceQueryVo.getUserId()))
                .getData().get(invoiceQueryVo.getUserId());
        if (user != null && StringUtil.isNotEmpty(user.getTenantId())) {
            accountMap.putAll(systemService.findAccountListByTenantId(user.getTenantId(), 1).getData().stream()
                    .collect(Collectors.toMap(AccountDto::getId, account -> account, (k1, k2) -> k1)));
        }
        //根据用户id查询所有站点id
        List<String> siteIds = systemService.findAllOrganEmpowerByUserId(invoiceQueryVo.getUserId()).getData().stream().map(OrganEmpowerListDto::getSiteId)
                .distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(siteIds)) {
            //根据多个站点id查询结算账户数据
            Set<String> accountIds = siteAccountDao.findAllBySiteIdInAndPayPlatform(siteIds, 1).stream().map(SiteAccountEntity::getAccountId)
                    .filter(StringUtil::isNotEmpty).filter(accountId -> !accountMap.containsKey(accountId)).collect(Collectors.toSet());
            accountMap.putAll(systemService.findAccountListByAccountIds(accountIds).getData());
        }
        if (MapUtils.isEmpty(accountMap)) {
            return ResponseResult.ok(new PageDto<>(resultList, invoiceQueryVo.getPage(), invoiceQueryVo.getSize()));
        }
        // 构造动态查询条件
        List<InvoiceEntity> invoiceList = invoiceDao.findAll((Specification<InvoiceEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.in(root.get("accountId")).value(accountMap.keySet())); //商户id
            if (StringUtil.isNotEmpty(invoiceQueryVo.getId())) { //申请单号查询
                predicates.add(cb.like(root.get("id"), "%" + invoiceQueryVo.getId() + "%"));
            }

            if (StringUtil.isNotEmpty(invoiceQueryVo.getInvoiceTitle())) { //发票抬头名称查询
                predicates.add(cb.equal(root.get("invoiceTitle"), invoiceQueryVo.getInvoiceTitle()));
            }

            if (StringUtil.isNotEmpty(invoiceQueryVo.getStartTime()) && StringUtil.isNotEmpty(invoiceQueryVo.getEndTime())) { //创建时间范围查询
                LocalDateTime startTime = DateUtil.strToLocalDateTime(DateUtil.getDayStart(invoiceQueryVo.getStartTime()));
                LocalDateTime endTime = DateUtil.strToLocalDateTime(DateUtil.getDayEnd(invoiceQueryVo.getEndTime()));
                predicates.add(cb.between(root.get("createTime"), startTime, endTime));
            }

            if (StringUtil.isNotEmpty(invoiceQueryVo.getInvoiceStatus())) { //发票状态查询
                predicates.add(cb.equal(root.get("invoiceStatus"), invoiceQueryVo.getInvoiceStatus()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });

        if (CollectionUtils.isNotEmpty(invoiceList)) {
            //根据多个小程序用户id查询用户手机号
            Set<String> appletUserIds = invoiceList.stream().map(InvoiceEntity::getAppletUserId).collect(Collectors.toSet());
            Map<String, String> appletPhoneMap = appletUserDao.findAllById(appletUserIds).stream().collect(Collectors.toMap(AppletUserEntity::getId,
                    AppletUserEntity::getPhoneNum, (k1, k2) -> k1));

            //对数据进行组装
            resultList = invoiceList.stream().map(invoice -> {
                InvoiceListDto result = new InvoiceListDto();
                BeanUtils.copyProperties(invoice, result);
                if (StringUtil.isNotEmpty(invoice.getAppletUserId()) && appletPhoneMap.containsKey(invoice.getAppletUserId())) {
                    result.setPhoneNum(appletPhoneMap.get(invoice.getAppletUserId()));
                }
                if (StringUtil.isNotEmpty(invoice.getAccountId()) && accountMap.containsKey(invoice.getAccountId())) {
                    AccountDto account = accountMap.get(invoice.getAccountId());
                    result.setMchId(account.getMchId());
                    result.setMchName(account.getMchName());
                }
                return result;
            }).collect(Collectors.toList());

            //对查询条件过滤
            if (StringUtil.isNotEmpty(invoiceQueryVo.getMchId())) {
                resultList = resultList.stream().filter(r -> StringUtil.isNotEmpty(r.getMchId()) && r.getMchId().equals(invoiceQueryVo.getMchId())).collect(Collectors.toList());
            }
            if (StringUtil.isNotEmpty(invoiceQueryVo.getPhoneNum())) {
                resultList = resultList.stream().filter(r -> StringUtil.isNotEmpty(r.getPhoneNum()) && r.getPhoneNum().equals(invoiceQueryVo.getPhoneNum())).collect(Collectors.toList());
            }
            //按照创建时间降序
            resultList = resultList.stream().sorted(Comparator.comparing(InvoiceListDto::getCreateTime).reversed()).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(resultList, invoiceQueryVo.getPage(), invoiceQueryVo.getSize()));
    }

    @Override
    public ResponseResult<InvoiceOrderDto> findInvoiceOrderById(String id) {
        //返回的对象
        InvoiceOrderDto result = new InvoiceOrderDto();

        //根据主键id查询发票数据
        Optional<InvoiceEntity> optional = invoiceDao.findById(id);
        if (optional.isPresent()) {
            InvoiceEntity invoice = optional.get();
            result.setId(invoice.getId());
            result.setTotalMoney(invoice.getInvoiceAmount());
            //根据申请单号id查询关联的订单号
            Set<String> orderNums = invoiceOrderDao.findAllByInvoiceIdIn(Collections.singleton(id)).stream().map(InvoiceOrderEntity::getOrderNum)
                    .collect(Collectors.toSet());
            //根据多个订单号查询订单数据
            List<OrderRecordEntity> orderRecordList = orderRecordDao.findAllByOrderNumIn(orderNums);
            if (CollectionUtils.isNotEmpty(orderRecordList)) {
                //根据多个订单号查询结算数据
                Map<String, SettlementRecordEntity> settlementRecordMap = settlementRecordDao.findAllByOrderNumIn(orderNums).stream().collect(Collectors
                        .toMap(SettlementRecordEntity::getOrderNum, a -> a, (k1, k2) -> k1));
                //根据多个站点id查询站点名称
                List<String> siteIds = orderRecordList.stream().map(OrderRecordEntity::getSiteId).filter(StringUtil::isNotEmpty).distinct()
                        .collect(Collectors.toList());
                Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();
                //对发票订单数据进行组装
                List<InvoiceOrderDto.OrderDto> invoiceOrderList = orderRecordList.stream().map(orderRecord -> {
                    InvoiceOrderDto.OrderDto invoiceOrder = new InvoiceOrderDto.OrderDto();
                    BeanUtils.copyProperties(orderRecord, invoiceOrder);
                    if (StringUtil.isNotEmpty(orderRecord.getSiteId()) && siteInfoMap.containsKey(orderRecord.getSiteId())) {
                        invoiceOrder.setSiteName(siteInfoMap.get(orderRecord.getSiteId()).getSiteName());//站点名称
                    }
                    if (settlementRecordMap.containsKey(orderRecord.getOrderNum())) {
                        SettlementRecordEntity settlementRecord = settlementRecordMap.get(orderRecord.getOrderNum());
                        invoiceOrder.setActualTotalCost(settlementRecord.getActualTotalCost()); //实付金额
                        invoiceOrder.setActualTotalElect(settlementRecord.getActualTotalElect()); //实付电费
                        invoiceOrder.setActualTotalFee(settlementRecord.getActualTotalFee()); //实付服务费
                    }
                    return invoiceOrder;
                }).collect(Collectors.toList());
                result.setOrderList(invoiceOrderList);
                result.setTotalElect(DoubleUtil.getToBigDecimal(invoiceOrderList.stream().map(InvoiceOrderDto.OrderDto::getActualTotalElect)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))); //总实付电费
                result.setTotalFee(DoubleUtil.getToBigDecimal(invoiceOrderList.stream().map(InvoiceOrderDto.OrderDto::getActualTotalFee)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))); //总实付服务费
                result.setTotalQt(DoubleUtil.getToDouble(invoiceOrderList.stream().filter(i -> StringUtil.isNotEmpty(i.getTotalQt()))
                        .mapToDouble(InvoiceOrderDto.OrderDto::getTotalQt).sum())); //总电量
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> updateInvoiceStatus(InvoiceStatusVo invoiceStatusVo, MultipartFile invoiceFile) {
        Optional<InvoiceEntity> optional = invoiceDao.findById(invoiceStatusVo.getId());
        if (optional.isPresent()) {
            InvoiceEntity invoice = optional.get();
            //创建开票操作记录
            InvoiceRecordEntity invoiceRecord = new InvoiceRecordEntity();
            invoiceRecord.setInvoiceId(invoice.getId());
            invoiceRecord.setUserId(invoiceStatusVo.getUserId());
            if (invoiceStatusVo.getInvoiceStatus() == 2) { //受理确认 状态给开票中
                invoice.setInvoiceStatus(invoiceStatusVo.getInvoiceStatus());
                invoiceRecord.setOperationType(1);
            } else if (invoiceStatusVo.getInvoiceStatus() == 3) { //开票确认 已开票
                invoice.setInvoiceStatus(invoiceStatusVo.getInvoiceStatus());
                invoiceRecord.setOperationType(2);
                if (invoiceFile != null && invoiceFile.getSize() > 0) { //发票图片
                    try {
                        //发送邮箱
                        String subject = "充电发票"; //邮件主题
                        String text = "您好，您的电子发票已开具，请查收。"; //邮件内容
                        Boolean status = emailUtil.sendEmail(invoice.getReceiptEmail(), subject, text, invoiceFile);
                        if (!status) {
                            log.error("发送邮件失败,发票申请单号:{},收票人邮箱:{}", invoice.getId(), invoice.getReceiptEmail());
                        }
                    } catch (Exception e) {
                        log.error("发送邮件报错,发票申请单号:{},收票人邮箱:{}", invoice.getId(), invoice.getReceiptEmail());
                    }
                    invoice.setInvoiceFilePath(FileUtil.getFilePath(invoiceFile, invoice.getInvoiceFilePath()));//发票文件地址
                } /*else {
                    return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
                }*/
            } else {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            invoiceRecord.setCreateTime(LocalDateTime.now());
            invoiceDao.save(invoice);
            invoiceRecordDao.save(invoiceRecord);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<InvoiceDetailDto> findInvoiceDetailById(String id) {
        //返回的对象
        InvoiceDetailDto result = new InvoiceDetailDto();
        Optional<InvoiceEntity> optional = invoiceDao.findById(id);
        if (optional.isPresent()) {
            InvoiceEntity invoice = optional.get();
            BeanUtils.copyProperties(invoice, result);
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<PageDto<InvoiceRecordDto>> queryInvoiceRecordList(InvoiceRecordVo invoiceRecordVo) {
        //返回的集合
        List<InvoiceRecordDto> resultList = Lists.newArrayList();

        List<InvoiceRecordEntity> invoiceRecordList = invoiceRecordDao.findAll();
        if (CollectionUtils.isNotEmpty(invoiceRecordList)) {
            //根据发票申请单号查询
            if (StringUtil.isNotEmpty(invoiceRecordVo.getInvoiceId())) {
                invoiceRecordList = invoiceRecordList.stream().filter(i -> i.getInvoiceId().contains(invoiceRecordVo.getInvoiceId()))
                        .collect(Collectors.toList());
            }
            //根据多个发票申请单号查询发票数据
            List<String> invoiceIds = invoiceRecordList.stream().map(InvoiceRecordEntity::getInvoiceId).distinct().collect(Collectors.toList());
            Map<String, InvoiceEntity> invoiceMap = invoiceDao.findAllById(invoiceIds).stream().collect(Collectors
                    .toMap(InvoiceEntity::getId, i -> i, (k1, k2) -> k1));
            Map<String, String> appletUserPhoneMap = Maps.newHashMap();
            Map<String, AccountDto> accountMap = Maps.newHashMap();
            if (MapUtils.isNotEmpty(invoiceMap)) {
                //根据多个小程序id查询手机号
                Set<String> appletUserIds = invoiceMap.values().stream().map(InvoiceEntity::getAppletUserId).collect(Collectors.toSet());
                appletUserPhoneMap = appletUserDao.findAllById(appletUserIds).stream().collect(Collectors
                        .toMap(AppletUserEntity::getId, AppletUserEntity::getPhoneNum));
                //根据多个账户id查询商户名称
                Set<String> accountIds = invoiceMap.values().stream().map(InvoiceEntity::getAccountId).collect(Collectors.toSet());
                accountMap = systemService.findAccountListByAccountIds(accountIds).getData();
            }

            //根据多个用户id查询用户名称
            List<String> userIds = invoiceRecordList.stream().map(InvoiceRecordEntity::getUserId).filter(StringUtil::isNotEmpty)
                    .distinct().collect(Collectors.toList());
            Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(userIds).getData();

            //对数据进行组装
            Map<String, AccountDto> finalAccountMap = accountMap;
            Map<String, String> finalAppletUserPhoneMap = appletUserPhoneMap;
            resultList = invoiceRecordList.stream().map(invoiceRecord -> {
                InvoiceRecordDto result = new InvoiceRecordDto();
                BeanUtils.copyProperties(invoiceRecord, result);
                if (invoiceMap.containsKey(invoiceRecord.getInvoiceId())) {
                    InvoiceEntity invoice = invoiceMap.get(invoiceRecord.getInvoiceId());
                    if (finalAppletUserPhoneMap.containsKey(invoice.getAppletUserId())) {
                        result.setPhoneNum(finalAppletUserPhoneMap.get(invoice.getAppletUserId())); //小程序用户手机号
                    }
                    if (finalAccountMap.containsKey(invoice.getAccountId())) {
                        AccountDto account = finalAccountMap.get(invoice.getAccountId());
                        result.setMchId(account.getMchId()); //商户id
                        result.setMchName(account.getMchName()); //商户名称
                    }
                    if (userMap.containsKey(invoiceRecord.getUserId())) {
                        result.setUserName(userMap.get(invoiceRecord.getUserId()).getFullName()); //平台用户名称
                    }
                }
                return result;
            }).collect(Collectors.toList());
            //根据查询条件过滤
            if (StringUtil.isNotEmpty(invoiceRecordVo.getMchId())) { //商户id
                resultList = resultList.stream().filter(r -> StringUtil.isNotEmpty(r.getMchId())
                        && r.getMchId().equals(invoiceRecordVo.getMchId())).collect(Collectors.toList());
            }
            if (StringUtil.isNotEmpty(invoiceRecordVo.getPhoneNum())) { //手机号
                resultList = resultList.stream().filter(r -> StringUtil.isNotEmpty(r.getPhoneNum())
                        && r.getPhoneNum().contains(invoiceRecordVo.getPhoneNum())).collect(Collectors.toList());
            }
            //根据创建时间降序排序
            resultList = resultList.stream().sorted(Comparator.comparing(InvoiceRecordDto::getCreateTime).reversed()).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(resultList, invoiceRecordVo.getPage(), invoiceRecordVo.getSize()));
    }


    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveInvoiceTitle(InvoiceTitleVo invoiceTitleVo) {
        // 1. 校验必填字段
        if (!isInvoiceVoValid(invoiceTitleVo)) {
            return ResponseResult.paramError("请填写必填字段");
        }

        // 2. 查询当前用户下同名抬头，防止重复
        List<InvoiceTitleEntity> sameTitleList = invoiceTitleDao.findAllByAppletUserIdAndInvoiceTitle(invoiceTitleVo.getAppletUserId(), invoiceTitleVo.getInvoiceTitle());

        //3.新增或编辑开票记录
        if (StringUtil.isEmpty(invoiceTitleVo.getId())) {
            // 若存在同名记录，则拒绝重复添加
            if (CollectionUtils.isNotEmpty(sameTitleList)) {
                return ResponseResult.paramError("该发票抬头已存在，不能重复添加");
            }
            // 拷贝属性并设置所属用户，保存新记录
            InvoiceTitleEntity entity = new InvoiceTitleEntity();
            BeanUtils.copyProperties(invoiceTitleVo, entity);
            InvoiceTitleEntity save = invoiceTitleDao.save(entity);
            //更新其它默认地址
            if (invoiceTitleVo.getIsDefault() == 1) {
                //根据小程序用户id查询默认的发票抬头
                List<InvoiceTitleEntity> invoiceDefaultList = invoiceTitleDao.findAllByAppletUserIdAndIsDefault(invoiceTitleVo.getAppletUserId(),
                                invoiceTitleVo.getIsDefault()).stream().filter(i -> !Objects.equals(i.getId(), save.getId()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(invoiceDefaultList)) {
                    invoiceTitleDao.saveAll(invoiceDefaultList.stream().peek(e -> e.setIsDefault(2)).collect(Collectors.toList()));
                }
            }
            return ResponseResult.ok();
        } else {
            // 检查除自身外是否有重复名称
            List<InvoiceTitleEntity> dupList = sameTitleList.stream().filter(e -> !Objects.equals(e.getId(), invoiceTitleVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dupList)) {
                return ResponseResult.paramError("该发票抬头已存在，不能重复编辑");
            }
            Optional<InvoiceTitleEntity> optional = invoiceTitleDao.findById(invoiceTitleVo.getId());
            if (optional.isPresent()) {
                InvoiceTitleEntity entity = optional.get();
                InvoiceTitleEntity invoiceTitle = new InvoiceTitleEntity();
                BeanUtils.copyProperties(invoiceTitleVo, entity);
                invoiceTitle.setCreateTime(optional.get().getCreateTime());
                invoiceTitleDao.save(entity);
                //更新其它默认地址
                if (invoiceTitleVo.getIsDefault() == 1) {
                    //根据小程序用户id查询默认的发票抬头
                    List<InvoiceTitleEntity> invoiceDefaultList = invoiceTitleDao.findAllByAppletUserIdAndIsDefault(invoiceTitleVo.getAppletUserId(),
                                    invoiceTitleVo.getIsDefault()).stream().filter(i -> !Objects.equals(i.getId(), invoiceTitleVo.getId()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(invoiceDefaultList)) {
                        invoiceTitleDao.saveAll(invoiceDefaultList.stream().peek(e -> e.setIsDefault(2)).collect(Collectors.toList()));
                    }
                }
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteInvoiceTitleById(String id) {
        // 1. 校验参数
        if (StringUtil.isEmpty(id)) {
            return ResponseResult.paramError("模板ID不能为空");
        }
        // 2. 按 ID + 用户 查询，确保只能删除自己的模板
        Optional<InvoiceTitleEntity> optional = invoiceTitleDao.findById(id);
        if (!optional.isPresent()) {
            return ResponseResult.paramError("未找到对应的发票抬头信息");
        }
        // 3. 删除操作
        invoiceTitleDao.delete(optional.get());
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<List<InvoiceTitleDto>> findInvoiceTitleList(String appletUserId) {
        //返回的集合
        List<InvoiceTitleDto> resultList = Lists.newArrayList();
        //根据小程序用户id查询发票抬头数据
        List<InvoiceTitleEntity> invoiceTitleList = invoiceTitleDao.findAllByAppletUserId(appletUserId);
        if (CollectionUtils.isNotEmpty(invoiceTitleList)) {
            resultList = invoiceTitleList.stream().map(i -> {
                InvoiceTitleDto result = new InvoiceTitleDto();
                BeanUtils.copyProperties(i, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<OrderAppShowDto>> findOrderAppShowList(String appletUserId, String queryDate) {
        //返回的集合
        List<OrderAppShowDto> resultList = Lists.newArrayList();
        //根据小程序用户id查询小程序手机号
        Optional<AppletUserEntity> optional = appletUserDao.findById(appletUserId);
        String phoneNum = optional.map(AppletUserEntity::getPhoneNum).orElse(null);
        //根据小程序用户id查询订单开票记录id
        List<String> invoiceIds = invoiceDao.findAllByAppletUserId(appletUserId).stream()
                .filter(i -> StringUtil.isNotEmpty(i.getInvoiceStatus()) && i.getInvoiceStatus() != 4)
                .map(InvoiceEntity::getId)
                .collect(Collectors.toList());
        Set<String> filterOrderNums = invoiceOrderDao.findAllByInvoiceIdIn(invoiceIds).stream().map(InvoiceOrderEntity::getOrderNum).collect(Collectors.toSet());

        //根据查询条件查询订单数据记录
        List<OrderRecordEntity> orderRecordList = orderRecordDao.findAll((Specification<OrderRecordEntity>) (root, cq, cb) -> {
            List<Predicate> list = Lists.newArrayList();
            if (StringUtil.isNotEmpty(phoneNum)) { //手机号查询
                list.add(cb.equal(root.get("accountType"), 3));
                list.add(cb.equal(root.get("accountData"), phoneNum));
            }
            if (StringUtil.isNotEmpty(queryDate)) { //日期查询 根据结算时间查询
                LocalDate startDate = DateUtil.strToLocalDate(queryDate + "-01");
                LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
                String startTime = DateUtil.localDateTimeToStr(LocalDateTime.of(startDate, LocalTime.MIN));
                String endTime = DateUtil.localDateTimeToStr(LocalDateTime.of(endDate, LocalTime.MAX));
                list.add(cb.between(root.get("endTime"), startTime, endTime));
            }
            list.add(cb.equal(root.get("runMode"), 0));
            if (CollectionUtils.isNotEmpty(filterOrderNums)) {
                list.add(cb.not(root.get("orderNum").in(filterOrderNums)));
            }
            return cb.and(list.toArray(new Predicate[0]));
        });

        if (CollectionUtils.isNotEmpty(orderRecordList)) {
            //根据多个站点id查询站点名称和租户(运营商)名称
            List<String> siteIds = orderRecordList.stream().map(OrderRecordEntity::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();

            //根据多个订单记录id查询结算记录
            List<String> orderNums = orderRecordList.stream().map(OrderRecordEntity::getOrderNum).collect(Collectors.toList());
            Map<String, SettlementRecordEntity> settlementRecordMap = settlementRecordDao.findAllByOrderNumIn(orderNums).stream()
                    .collect(Collectors.toMap(SettlementRecordEntity::getOrderNum, a -> a, (k1, k2) -> k1));
            orderRecordList.forEach(orderRecord -> {
                if (settlementRecordMap.containsKey(orderRecord.getOrderNum())) {
                    SettlementRecordEntity settlementRecord = settlementRecordMap.get(orderRecord.getOrderNum());
                    if (StringUtil.isNotEmpty(settlementRecord.getPayWay()) && settlementRecord.getPayWay() == 2
                            && settlementRecord.getActualTotalCost().compareTo(BigDecimal.ZERO) > 0) {
                        OrderAppShowDto result = new OrderAppShowDto();
                        result.setId(orderRecord.getId());
                        result.setOrderNum(orderRecord.getOrderNum());
                        result.setChargeQt(orderRecord.getTotalQt());
                        result.setActualTotalCost(settlementRecord.getActualTotalCost());
                        result.setStartTime(orderRecord.getStartTime());
                        result.setEndTime(orderRecord.getEndTime());
                        if (siteInfoMap.containsKey(orderRecord.getSiteId())) {
                            SiteInfoDto siteInfo = siteInfoMap.get(orderRecord.getSiteId());
                            result.setSiteName(siteInfo.getSiteName());
                        }
                        resultList.add(result);
                    }
                }
            });
        }
        return ResponseResult.ok(resultList);
    }

    //验证订单开票申请参数
    private boolean isOrderInvoiceVoValid(OrderInvoicesVo orderInvoicesVo) {
        // 检查输入对象是否为null
        if (orderInvoicesVo == null) {
            // 可以考虑添加日志记录这里的失败原因
            return false;
        }

        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                orderInvoicesVo.getAppletUserId(),
                orderInvoicesVo.getInvoiceType(),
                orderInvoicesVo.getTitleType(),
                orderInvoicesVo.getInvoiceTitle(),
                orderInvoicesVo.getReceiptEmail(),
                orderInvoicesVo.getOrderIds()
        };

        if (StringUtil.isNotEmpty(orderInvoicesVo.getTitleType()) && orderInvoicesVo.getTitleType() == 2 && StringUtil.isEmpty(orderInvoicesVo.getTaxNumber())) {
            return false;
        }

        // 它同时处理了空和只含空格的字符串
        return Arrays.stream(fieldsToValidate).noneMatch(StringUtil::isEmpty);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> applyInvoice(OrderInvoicesVo orderInvoicesVo) {
        if (!isOrderInvoiceVoValid(orderInvoicesVo)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ISNULL);
        }
        //根据多个订单id查询多个订单号
        List<String> orderIds = JSON.parseArray(orderInvoicesVo.getOrderIds(), String.class);
        //订单编号 -> 订单id
        List<String> orderNums = orderRecordDao.findAllById(orderIds).stream().map(OrderRecordEntity::getOrderNum)
                .filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        //校验订单号是否已开过发票
        List<InvoiceOrderEntity> checkInvoiceList = invoiceOrderDao.findAllByOrderNumIn(orderNums);
        if (CollectionUtils.isNotEmpty(checkInvoiceList)) {
            List<String> invoiceIds = checkInvoiceList.stream().map(InvoiceOrderEntity::getInvoiceId).filter(StringUtil::isNotEmpty)
                    .distinct().collect(Collectors.toList());
            List<InvoiceEntity> invoiceList = invoiceDao.findAllById(invoiceIds).stream().filter(d -> StringUtil.isNotEmpty(d.getInvoiceStatus())
                    && !Objects.equals(d.getInvoiceStatus(), 4)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(invoiceList)) {
                return ResponseResult.paramError("本次申请发票存在已经开过票的订单,请返回上一级重新申请");
            }
        }
        //根据多个订单号查询结算记录,交易账户
        Map<String, BigDecimal> orderNumCostMap = settlementRecordDao.findAllByOrderNumIn(orderNums).stream()
                .filter(s -> StringUtil.isNotEmpty(s.getActualTotalCost()))
                .collect(Collectors.toMap(SettlementRecordEntity::getOrderNum, SettlementRecordEntity::getActualTotalCost, (k1, k2) -> k1));
        Map<String, List<String>> tradeOrderNumMap = webAppService.findRechargeTradeListByOrderNums(orderNums).getData().stream()
                .filter(t -> StringUtil.isNotEmpty(t.getAccountId()))
                .collect(Collectors.groupingBy(RechargeTradeDto::getAccountId, Collectors.mapping(RechargeTradeDto::getOrderNum, Collectors.toList())));

        //定义发票订单实体类集合
        List<InvoiceEntity> addInvoiceList = Lists.newArrayList();
        //定义发票关联订单实体类集合
        Map<String, List<InvoiceOrderEntity>> accountOrderMap = Maps.newHashMap();
        tradeOrderNumMap.forEach((accountId, orderNumList) -> {
            InvoiceEntity invoice = new InvoiceEntity();
            BeanUtils.copyProperties(orderInvoicesVo, invoice);
            invoice.setAccountId(accountId); //账户id
            invoice.setInvoiceAmount(orderNumList.stream().filter(orderNumCostMap::containsKey).map(orderNumCostMap::get)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)); //发票金额
            invoice.setInvoiceStatus(1); //发票状态 待开票
            List<InvoiceOrderEntity> invoiceOrderList = orderNumList.stream().map(orderNum -> {
                InvoiceOrderEntity invoiceOrder = new InvoiceOrderEntity();
                invoiceOrder.setOrderNum(orderNum);
                invoiceOrder.setCreateTime(LocalDateTime.now());
                return invoiceOrder;
            }).collect(Collectors.toList());
            addInvoiceList.add(invoice);
            accountOrderMap.put(accountId, invoiceOrderList);
        });
        if (CollectionUtils.isNotEmpty(addInvoiceList)) {
            //批量添加发票数据
            Map<String, String> addInvoiceMap = invoiceDao.saveAll(addInvoiceList).stream().collect(Collectors.toMap(InvoiceEntity::getAccountId,
                    BaseTimeEntity::getId));
            List<InvoiceOrderEntity> addInvoiceOrderList = Lists.newArrayList();
            accountOrderMap.forEach((accountId, invoiceOrderList) -> addInvoiceOrderList.addAll(invoiceOrderList
                    .stream().peek(invoiceOrder -> invoiceOrder.setInvoiceId(addInvoiceMap.get(accountId)))
                    .collect(Collectors.toList())));
            invoiceOrderDao.saveAll(addInvoiceOrderList);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<AppInvoiceListDto>> findInvoiceRecordList(String appletUserId) {
        //返回的集合
        List<AppInvoiceListDto> resultList = Lists.newArrayList();
        //根据小程序用户id查询开票记录列表
        List<InvoiceEntity> invoiceList = invoiceDao.findAllByAppletUserId(appletUserId);
        if (CollectionUtils.isNotEmpty(invoiceList)) {
            resultList = invoiceList.stream().filter(invoice -> StringUtil.isNotEmpty(invoice.getCreateTime())).map(invoice -> {
                AppInvoiceListDto result = new AppInvoiceListDto();
                BeanUtils.copyProperties(invoice, result);
                result.setCreateTime(DateUtil.localDateTimeToStr(invoice.getCreateTime()));
                return result;
            }).sorted(Comparator.comparing(AppInvoiceListDto::getCreateTime).reversed()).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<AppInvoiceDetailDto> findAppInvoiceDetailById(String id) {
        //返回的对象
        AppInvoiceDetailDto result = new AppInvoiceDetailDto();

        //根据发票单号查询开票详情数据
        Optional<InvoiceEntity> optional = invoiceDao.findById(id);
        if (optional.isPresent()) {
            InvoiceEntity invoice = optional.get();
            BeanUtils.copyProperties(invoice, result);
            if (StringUtil.isNotEmpty(invoice.getCreateTime())) {
                result.setCreateTime(DateUtil.localDateTimeToStr(invoice.getCreateTime()));
            }
            //根据账号id查询运营商名称
            String operatorName;
            AccountDto account = systemService.findAccountListByAccountIds(Collections.singleton(invoice.getAccountId()))
                    .getData().get(invoice.getAccountId());
            if (account != null && StringUtil.isNotEmpty(account.getTenantId())) {
                List<TenantDetailsDto> tenantList = systemService.findAllTenantInfoByIds(Collections.singletonList(account.getTenantId())).getData();
                if (CollectionUtils.isNotEmpty(tenantList)) {
                    operatorName = tenantList.get(0).getTenantName();
                } else {
                    operatorName = null;
                }
            } else {
                operatorName = null;
            }

            //根据发票申请单号查询关联的订单id列表
            List<InvoiceOrderEntity> invoiceOrderList = invoiceOrderDao.findAllByInvoiceIdIn(Collections.singleton(id));
            if (CollectionUtils.isNotEmpty(invoiceOrderList)) {
                //根据多个订单id查询订单详情
                List<String> orderNums = invoiceOrderList.stream().map(InvoiceOrderEntity::getOrderNum).collect(Collectors.toList());
                List<OrderRecordEntity> orderList = orderRecordDao.findAllByOrderNumIn(orderNums);
                if (CollectionUtils.isNotEmpty(orderList)) {
                    //根据多个订单编号查询结算记录
                    Map<String, SettlementRecordEntity> settlementRecordMap = settlementRecordDao.findAllByOrderNumIn(orderNums).stream()
                            .collect(Collectors.toMap(SettlementRecordEntity::getOrderNum, a -> a, (k1, k2) -> k1));
                    //根据多个站点id查询站点名称
                    List<String> siteIds = orderList.stream().map(OrderRecordEntity::getSiteId).collect(Collectors.toList());
                    Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();
                    //对发票单号的订单列表进行组合
                    result.setInvoiceOrderList(orderList.stream().map(order -> {
                        AppInvoiceDetailDto.InvoiceOrderDto invoiceOrder = new AppInvoiceDetailDto.InvoiceOrderDto();
                        invoiceOrder.setOrderId(order.getId());
                        invoiceOrder.setOrderNum(order.getOrderNum());
                        invoiceOrder.setChargeQt(order.getTotalQt());
                        if (settlementRecordMap.containsKey(order.getOrderNum())) {
                            SettlementRecordEntity settlementRecord = settlementRecordMap.get(order.getOrderNum());
                            invoiceOrder.setActualTotalCost(settlementRecord.getActualTotalCost());
                        }
                        if (siteInfoMap.containsKey(order.getSiteId())) {
                            invoiceOrder.setSiteName(siteInfoMap.get(order.getSiteId()).getSiteName());
                        }
                        invoiceOrder.setOperatorName(operatorName);
                        return invoiceOrder;
                    }).collect(Collectors.toList()));
                }
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> revokeInvoice(String id) {
        Optional<InvoiceEntity> optional = invoiceDao.findById(id);
        if (optional.isPresent()) {
            InvoiceEntity invoice = optional.get();
            if (StringUtil.isEmpty(invoice.getInvoiceStatus())) {
                return ResponseResult.paramError("发票单号状态异常");
            }
            //发票状态 1-待开票 2-开票中 3-已开票 4-已撤销
            switch (invoice.getInvoiceStatus()) {
                case 2:
                    return ResponseResult.paramError("该发票订单开票中,不允许撤销");
                case 3:
                    return ResponseResult.paramError("该发票订单已开票,不允许撤销");
                case 4:
                    return ResponseResult.paramError("该发票订单已撤销,不允许重复撤销");
            }
            invoice.setInvoiceStatus(4);
            invoiceDao.save(invoice);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError("发票单号不存在");
    }


    private static boolean isInvoiceVoValid(InvoiceTitleVo invoiceTitleVo) {
        // 检查输入对象是否为null
        if (invoiceTitleVo == null) {
            // 可以考虑添加日志记录这里的失败原因
            return false;
        }

        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                invoiceTitleVo.getInvoiceType(),
                invoiceTitleVo.getTitleType(),
                invoiceTitleVo.getInvoiceTitle(),
                invoiceTitleVo.getIsDefault()
        };

        if (StringUtil.isNotEmpty(invoiceTitleVo.getTitleType()) && invoiceTitleVo.getTitleType() == 2 && StringUtil.isEmpty(invoiceTitleVo.getTaxNumber())) {
            return false;
        }

        // 它同时处理了空和只含空格的字符串
        return Arrays.stream(fieldsToValidate).noneMatch(StringUtil::isEmpty);
    }

}



















