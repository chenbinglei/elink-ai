package com.sunmax.log.service.impl;

import com.google.common.collect.Lists;
import com.sunmax.log.dao.AccessLogDao;
import com.sunmax.log.dto.AccessLogDto;
import com.sunmax.log.dto.PageLogDto;
import com.sunmax.log.entity.AccessLogEntity;
import com.sunmax.log.service.AccessLogService;
import com.sunmax.log.util.DateUtils;
import com.sunmax.log.util.StringUtils;
import com.sunmax.log.vo.AccessLogQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.MapUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccessLogServiceImpl implements AccessLogService {

    @Autowired
    private AccessLogDao accessLogDao;

    @Override
    public PageLogDto<AccessLogDto> queryAccessLogList(AccessLogQueryVo accessLogQueryVo, Map<String, String> userAccountNameMap) {
        //返回的集合
        List<AccessLogDto> resultList = Lists.newArrayList();

        if (MapUtils.isEmpty(userAccountNameMap)) {
            return new PageLogDto<>(resultList, accessLogQueryVo.getPage(), accessLogQueryVo.getSize());
        }

        //根据查询条件查询日志列表数据
        Page<AccessLogEntity> accessLogPage = accessLogDao.findAll((Specification<AccessLogEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            //客户端id为空 默认derms-client
            if (StringUtils.isNotEmpty(accessLogQueryVo.getClientId())) {
                list.add(cb.equal(root.get("clientId"), accessLogQueryVo.getClientId()));
            } else {
                list.add(cb.equal(root.get("clientId"), "derms-client"));
            }
            list.add(cb.in(root.get("userAccount")).value(userAccountNameMap.keySet()));
            if (StringUtils.isNotEmpty(accessLogQueryVo.getUserAccount())) { //用户账号
                list.add(cb.like(root.get("userAccount"), "%" + accessLogQueryVo.getUserAccount() + "%"));
            }
            if (StringUtils.isNotEmpty(accessLogQueryVo.getStartDate()) && StringUtils.isNotEmpty(accessLogQueryVo.getEndDate())) { //日期
                LocalDateTime startTime = DateUtils.strToLocalDateTime(DateUtils.getDayStart(accessLogQueryVo.getStartDate()));
                LocalDateTime endTime = DateUtils.strToLocalDateTime(DateUtils.getDayEnd(accessLogQueryVo.getEndDate()));
                list.add(cb.between(root.get("createTime"), startTime, endTime));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, PageRequest.of(accessLogQueryVo.getPage() - 1, accessLogQueryVo.getSize(), Sort.by("createTime").descending()));

        if (!accessLogPage.getContent().isEmpty()) {
            //对数据进行组装
            resultList = accessLogPage.getContent().stream().map(accessLog -> {
                AccessLogDto result = new AccessLogDto();
                BeanUtils.copyProperties(accessLog, result);
                if (StringUtils.isNotEmpty(accessLog.getCreateTime())) {
                    result.setCreateTime(DateUtils.localDateTimeToStr(accessLog.getCreateTime()));
                }
                if (StringUtils.isNotEmpty(accessLog.getUserAccount()) && userAccountNameMap.containsKey(accessLog.getUserAccount())) {
                    result.setUserName(userAccountNameMap.get(accessLog.getUserAccount()));
                }
                return result;
            }).collect(Collectors.toList());
        }
        return new PageLogDto<>(resultList, accessLogPage.getNumber() + 1, accessLogPage.getSize(), (int) accessLogPage.getTotalElements());
    }

}
