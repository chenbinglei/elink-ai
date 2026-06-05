package com.sunmax.together.service.operation.impl;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.together.dao.UserFeedbackDao;
import com.sunmax.together.dto.operation.feedback.UserFeedbackDetailDto;
import com.sunmax.together.dto.operation.feedback.UserFeedbackListDto;
import com.sunmax.together.entity.UserFeedbackEntity;
import com.sunmax.together.service.operation.FeedbackService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.vo.operation.feedback.UserFeedbackQueryVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private UserFeedbackDao userFeedbackDao;

    @Autowired
    private DeviceService deviceService;

    @Override
    public ResponseResult<PageDto<UserFeedbackListDto>> queryUserFeedbackList(UserFeedbackQueryVo feedbackQueryVo) {
        //返回的集合
        List<UserFeedbackListDto> resultList = Lists.newArrayList();
        //查询用户反馈数据
        List<UserFeedbackEntity> userFeedbackList = userFeedbackDao.findAll((Specification<UserFeedbackEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtil.isNotEmpty(feedbackQueryVo.getFeedbackType())) {
                predicates.add(cb.equal(root.get("feedbackType"), feedbackQueryVo.getFeedbackType()));
            }
            if (StringUtil.isNotEmpty(feedbackQueryVo.getSiteIds())) {
                predicates.add(cb.in(root.get("siteId")).value(JSON.parseArray(feedbackQueryVo.getSiteIds())));
            }
            if (StringUtil.isNotEmpty(feedbackQueryVo.getStatus())) {
                predicates.add(cb.equal(root.get("status"), feedbackQueryVo.getStatus()));
            }
            if (StringUtil.isNotEmpty(feedbackQueryVo.getStartDate()) && StringUtil.isNotEmpty(feedbackQueryVo.getEndDate())) {
                predicates.add(cb.between(root.get("createTime"), DateUtil.strToLocalDateTime(DateUtil.getDayStart(feedbackQueryVo.getStartDate())),
                        DateUtil.strToLocalDateTime(DateUtil.getDayEnd(feedbackQueryVo.getEndDate()))));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
        if (CollectionUtils.isNotEmpty(userFeedbackList)) {
            //根据多个站点id查询站点名称
            List<String> siteIds = userFeedbackList.stream().map(UserFeedbackEntity::getSiteId).distinct().collect(Collectors.toList());
            Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();
            resultList = userFeedbackList.stream().map(feedback -> {
                UserFeedbackListDto result = new UserFeedbackListDto();
                BeanUtils.copyProperties(feedback, result);
                if (siteInfoMap.containsKey(feedback.getSiteId())) {
                    result.setSiteName(siteInfoMap.get(feedback.getSiteId()).getSiteName());
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(resultList, feedbackQueryVo.getPage(), feedbackQueryVo.getSize()));
    }

    @Override
    public ResponseResult<UserFeedbackDetailDto> findUserFeedbackById(String id) {
        //返回的对象
        UserFeedbackDetailDto result = new UserFeedbackDetailDto();
        Optional<UserFeedbackEntity> optional = userFeedbackDao.findById(id);
        if (optional.isPresent()) {
            UserFeedbackEntity userFeedback = optional.get();
            BeanUtils.copyProperties(userFeedback, result);
            if (StringUtil.isNotEmpty(userFeedback.getSiteId())) {
                SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(userFeedback.getSiteId()))
                        .getData().get(userFeedback.getSiteId());
                if (siteInfo != null && StringUtil.isNotEmpty(siteInfo.getSiteName())) {
                    result.setSiteName(siteInfo.getSiteName());
                }
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> updateUserFeedbackStatus(String id, Integer status) {
        Optional<UserFeedbackEntity> optional = userFeedbackDao.findById(id);
        if (optional.isPresent()) {
            UserFeedbackEntity userFeedback = optional.get();
            userFeedback.setStatus(status);
            if (status == 2) {
                userFeedback.setAcceptTime(LocalDateTime.now());
            }
            if (status == 3) {
                userFeedback.setFinishTime(LocalDateTime.now());
            }
            userFeedbackDao.save(userFeedback);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }


}
