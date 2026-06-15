package com.sunmax.together.service.operation.impl;

import com.google.common.collect.Lists;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.together.dao.ElectricCardBalanceDao;
import com.sunmax.together.dao.ElectricCardDao;
import com.sunmax.together.dao.ElectricCardRecordDao;
import com.sunmax.together.dto.operation.electricCard.ElectricCardTradeDto;
import com.sunmax.together.dto.operation.electricCard.ElectricCardTradeSummaryDto;
import com.sunmax.together.dto.operation.electricCard.ElectricCardDetailDto;
import com.sunmax.together.dto.operation.electricCard.ElectricCardDto;
import com.sunmax.together.entity.ElectricCardBalanceEntity;
import com.sunmax.together.entity.ElectricCardEntity;
import com.sunmax.together.entity.ElectricCardRecordEntity;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.operation.ElectricCardService;
import com.sunmax.together.vo.operation.electricCard.ElectricCardQueryVo;
import com.sunmax.together.vo.operation.electricCard.ElectricCardTradeVo;
import com.sunmax.together.vo.operation.electricCard.ElectricCardVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ElectricCardServiceImpl implements ElectricCardService {

    @Autowired
    private ElectricCardDao electricCardDao;

    @Autowired
    private ElectricCardRecordDao electricCardRecordDao;

    @Autowired
    private SystemService systemService;
    @Autowired
    private ElectricCardBalanceDao electricCardBalanceDao;


    private static boolean isElectricCard(ElectricCardVo electricCardVo) {
        // 检查输入对象是否为null
        if (electricCardVo == null) {
            // 可以考虑添加日志记录这里的失败原因
            return false;
        }

        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                electricCardVo.getCardType(),
                electricCardVo.getCardNumber(),
                electricCardVo.getPhysicalCard(),
                electricCardVo.getLicenseNumber(),
                electricCardVo.getCardHolder(),
                electricCardVo.getCurrentBalance(),
                electricCardVo.getUserId()
        };

        // 它同时处理了空和只含空格的字符串
        return Arrays.stream(fieldsToValidate).noneMatch(StringUtil::isEmpty);
    }


    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveElectricCard(ElectricCardVo electricCardVo) {
        //校验必填字段是否为空
        if (!isElectricCard(electricCardVo)) {
            return ResponseResult.paramError("请填写必填字段");
        }

        //根据电卡卡号查询电卡数据
        List<ElectricCardEntity> cardNumberList = electricCardDao.findAllByCardNumber(electricCardVo.getCardNumber());

        //根据物理卡号查询电卡数据
        List<ElectricCardEntity> physicalCardList = electricCardDao.findAllByPhysicalCard(electricCardVo.getPhysicalCard());

        //定义电卡记录实体类
        ElectricCardRecordEntity cardRecord = new ElectricCardRecordEntity();
        cardRecord.setCreateId(electricCardVo.getUserId());

        //新增电卡信息
        if (StringUtil.isEmpty(electricCardVo.getId())) {

            if (CollectionUtils.isNotEmpty(cardNumberList)) {
                return ResponseResult.error("卡面号已存在，不能重复添加");
            }

            if (CollectionUtils.isNotEmpty(physicalCardList)) {
                return ResponseResult.error("物理卡号已存在，不能重复添加");
            }

            ElectricCardEntity electricCard = new ElectricCardEntity();
            BeanUtils.copyProperties(electricCardVo, electricCard);
            //创建人id
            electricCard.setCreateId(electricCardVo.getUserId());
            //修改人id
            electricCard.setUpdateId(electricCardVo.getUserId());
            //默认状态正常
            electricCard.setState(1);
            ElectricCardEntity save = electricCardDao.save(electricCard);

            //保持新增电卡信息记录
            cardRecord.setCarId(save.getId());
            cardRecord.setOperationType(1);
            electricCardRecordDao.save(cardRecord);
            return ResponseResult.ok();
        } else {
            //校验卡面号是否存在
            cardNumberList = cardNumberList.stream().filter(c -> !Objects.equals(c.getId(), electricCardVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(cardNumberList)) {
                return ResponseResult.error("卡面号已存在，不能编辑");
            }
            //校验物理卡号是否存在
            physicalCardList = physicalCardList.stream().filter(c -> !Objects.equals(c.getId(), electricCardVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(physicalCardList)) {
                return ResponseResult.error("物理卡号已存在，不能编辑");
            }

            //编辑电卡信息
            Optional<ElectricCardEntity> optional = electricCardDao.findById(electricCardVo.getId());
            if (optional.isPresent()) {
                ElectricCardEntity save = optional.get();

                ElectricCardEntity electricCard = new ElectricCardEntity();
                BeanUtils.copyProperties(electricCardVo, electricCard);
                //创建人id
                electricCard.setCreateId(save.getCreateId());
                //创建时间
                electricCard.setCreateTime(save.getCreateTime());
                //修改人id
                electricCard.setUpdateId(electricCardVo.getUserId());
                electricCard.setState(save.getState());
                electricCardDao.save(electricCard);

                //保持编辑电卡信息记录
                cardRecord.setCarId(electricCardVo.getId());
                cardRecord.setOperationType(2);
                electricCardRecordDao.save(cardRecord);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    /**
     * 查询电卡
     *
     * @param query
     * @return
     */
    @Override
    public ResponseResult<PageDto<ElectricCardDto>> searchElectricCard(ElectricCardQueryVo query) {

        List<ElectricCardEntity> electricCardList = electricCardDao.findAll((Specification<ElectricCardEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtil.isNotEmpty(query.getId())) {
                predicates.add(cb.equal(root.get("id"), query.getId()));
            }

            // 查询持卡人
            if (StringUtil.isNotEmpty(query.getCardHolder())) {
                predicates.add(cb.equal(root.get("cardHolder"), query.getCardHolder()));
            }

            // 查询卡面号
            if (StringUtil.isNotEmpty(query.getCardNumber())) {
                predicates.add(cb.equal(root.get("cardNumber"), query.getCardNumber()));
            }

            // 时间范围查询
            if (StringUtil.isNotEmpty(query.getStartDate()) && StringUtil.isNotEmpty(query.getEndDate())) {
                predicates.add(cb.between(root.get("createTime"),
                        DateUtil.strToLocalDateTime(DateUtil.getDayStart(query.getStartDate())),
                        DateUtil.strToLocalDateTime(DateUtil.getDayEnd(query.getEndDate()))));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });

        //根据多个用户id查询用户名称
        List<String> userIds = electricCardList.stream().map(ElectricCardEntity::getCreateId).distinct().collect(Collectors.toList());
        Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(userIds).getData();

        List<ElectricCardDto> electricCardDto = electricCardList.stream().sorted(Comparator.comparing(ElectricCardEntity::getCreateTime).reversed()).map(entity -> {
            ElectricCardDto dto = new ElectricCardDto();
            BeanUtils.copyProperties(entity, dto);
            //创建人名称
            if (userMap.containsKey(entity.getCreateId())) {
                dto.setCreateName(userMap.get(entity.getCreateId()).getFullName());
            }
            return dto;

        }).collect(Collectors.toList());


        return ResponseResult.ok(new PageDto<>(electricCardDto, query.getPage(), query.getSize()));
    }

    /**
     * 电卡详情
     *
     * @param id
     * @return
     */

    @Override
    public ResponseResult<ElectricCardDetailDto> getElectricCardDetail(String id) {
        try {
            // 根据 ID 查询数据库
            Optional<ElectricCardEntity> optional = electricCardDao.findById(id);
            // 如果查询不到，返回错误信息
            if (!optional.isPresent()) {
                return ResponseResult.error("电卡不存在，ID: " + id);
            }

            // 如果查询到实体，则转换为 DTO
            ElectricCardEntity entity = optional.get();
            ElectricCardDetailDto dto = new ElectricCardDetailDto();
            BeanUtils.copyProperties(entity, dto);


            return ResponseResult.ok(dto, "查询成功");
        } catch (Exception e) {

            return ResponseResult.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 电卡状态修改
     *
     * @param id
     * @param state
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> updateElectricCardState(String id, Integer state, String userId) {
        Optional<ElectricCardEntity> optional = electricCardDao.findById(id);
        if (optional.isPresent()) {
            //保存电卡信息
            ElectricCardEntity electricCard = optional.get();
            electricCard.setState(state);
            electricCardDao.save(electricCard);

            ElectricCardRecordEntity record = new ElectricCardRecordEntity();
            record.setCarId(electricCard.getId());
            if (state == 1) {
                record.setOperationType(4);
            }
            if (state == 2) {
                record.setOperationType(3);
            }
            record.setCreateId(userId);
            electricCardRecordDao.save(record);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    /**
     * 电卡操作记录
     *
     * @param carId
     * @return
     */
    @Override
    public ResponseResult<List<ElectricCardRecordEntity>> getOperationRecords(String carId) {
        try {
            List<ElectricCardRecordEntity> records = electricCardRecordDao.findByCarId(carId);
            return ResponseResult.ok(records, "查询成功");
        } catch (Exception e) {
            return ResponseResult.error("查询失败: " + e.getMessage());
        }
    }


    /**
     * 电卡删除
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteElectricCard(String id) {
        try {
            // 查询电卡是否存在
            Optional<ElectricCardEntity> optional = electricCardDao.findById(id);
            if (!optional.isPresent()) {
                return ResponseResult.error("电卡不存在，ID: " + id);
            }
            // 如果需要，同时删除该电卡的操作记录（如果数据库中没有配置级联删除）
            List<ElectricCardRecordEntity> records = electricCardRecordDao.findByCarId(id);
            if (CollectionUtils.isNotEmpty(records)) {
                electricCardRecordDao.deleteAll(records);
            }
            // 删除电卡信息
            electricCardDao.delete(optional.get());
            return ResponseResult.ok();
        } catch (Exception e) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
    }

    /**
     *
     * @param id
     * @param amount
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> addElectricCardBalance(String id, Double amount) {
        try {
            // 根据电卡ID查询电卡信息
            Optional<ElectricCardEntity> optional = electricCardDao.findById(id);
            if (!optional.isPresent()) {
                return ResponseResult.error("电卡不存在，ID: " + id);
            }
            if (amount <= 0) {
                return ResponseResult.error("充值金额必须大于0");
            }
            ElectricCardEntity card = optional.get();
            double currentBalance = card.getCurrentBalance();
            double newBalance = currentBalance + amount;


            card.setCurrentBalance(newBalance);
            electricCardDao.save(card);
            // 记录操作记录
            ElectricCardBalanceEntity balanceRecord = new ElectricCardBalanceEntity();
            balanceRecord.setCarId(card.getId());
            balanceRecord.setTradeBalance(amount);
            balanceRecord.setAfterTradeBalance(newBalance);
            balanceRecord.setTradeTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
            balanceRecord.setTradeType(5);


            electricCardBalanceDao.save(balanceRecord);

            return ResponseResult.ok();
        } catch (Exception e) {

            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
    }

    /****
     * 电卡退款
     *
     * @param id
     * @param amount
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> reduceElectricCardBalance(String id, Double amount) {
        try {
            // 根据电卡ID查询电卡信息
            Optional<ElectricCardEntity> optional = electricCardDao.findById(id);
            if (!optional.isPresent()) {
                return ResponseResult.error("电卡不存在，ID: " + id);
            }
            ElectricCardEntity card = optional.get();
            double currentBalance = card.getCurrentBalance();
            if (amount <= 0) {
                return ResponseResult.error("退款金额必须大于0");
            }
            // 判断余额是否足够退款
            if (currentBalance < amount) {
                return ResponseResult.error("余额不足，无法进行退款");
            }
            double newBalance = currentBalance - amount;
            card.setCurrentBalance(newBalance);
            electricCardDao.save(card);
            // 记录操作记录
            ElectricCardBalanceEntity balanceRecord = new ElectricCardBalanceEntity();
            balanceRecord.setCarId(card.getId());
            balanceRecord.setTradeBalance(amount);
            balanceRecord.setAfterTradeBalance(newBalance);
            balanceRecord.setTradeTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
            balanceRecord.setTradeType(1);


            electricCardBalanceDao.save(balanceRecord);

            return ResponseResult.ok();
            } catch (Exception e) {

            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
    }

    /**
     *
     * 查询电卡交易记录
     * @param query 查询参数
     * @return ResponseResult
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<ElectricCardTradeSummaryDto> searchTrade(ElectricCardTradeVo query) {
        //返回的对象
        ElectricCardTradeSummaryDto result = new ElectricCardTradeSummaryDto();
        try {
            List<ElectricCardBalanceEntity> electricCardList = electricCardBalanceDao.findAll((Specification<ElectricCardBalanceEntity>) (root, cq, cb) -> {
                List<Predicate> predicates = Lists.newArrayList();
                if (StringUtil.isNotEmpty(query.getTradeType())) {
                    try {
                        Integer tradeType = query.getTradeType();
                        predicates.add(cb.equal(root.get("tradeType"), tradeType));
                    } catch (NumberFormatException ex) {
                        // 可以选择返回错误或者不添加这个条件
                        return cb.disjunction(); // 或者直接抛出异常
                    }
                }

                if (StringUtil.isNotEmpty(query.getStartDate()) && StringUtil.isNotEmpty(query.getEndDate())) {
                    predicates.add(cb.between(root.get("tradeTime"),
                            DateUtil.getDayStart(query.getStartDate()),
                            DateUtil.getDayEnd(query.getEndDate())));
                }

                return cb.and(predicates.toArray(new Predicate[0]));

            });


            // 计算累计金额
            double totalRecharge = 0.0;
            double totalConsume = 0.0;
            double totalRefund = 0.0;
            for (ElectricCardBalanceEntity entity : electricCardList) {
                switch (entity.getTradeType()) {
                    case 5: // 充值
                        totalRecharge += entity.getTradeBalance();
                        break;
                    case 3: // 消费
                        totalConsume += entity.getTradeBalance();
                        break;
                    case 1: // 退款
                        totalRefund += entity.getTradeBalance();
                        break;
                    default:
                        break;
                }
            }
            result.setTotalRecharge(totalRecharge);
            result.setTotalConsume(totalConsume);
            result.setTotalRefund(totalRefund);

            List<ElectricCardTradeDto> tradeDtoList = electricCardList.stream()

                    .map(entity -> {
                        ElectricCardTradeDto dto = new ElectricCardTradeDto();
                        BeanUtils.copyProperties(entity, dto);
                        return dto;
                    })
                    .collect(Collectors.toList());

            result.setPageDto(new PageDto<>(tradeDtoList, query.getPage(), query.getSize()));

            return ResponseResult.ok(result);

        } catch (Exception e) {
            return ResponseResult.error("查询交易记录失败: " + e.getMessage());
        }

    }
}














