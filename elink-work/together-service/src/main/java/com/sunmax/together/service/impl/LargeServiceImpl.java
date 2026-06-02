package com.sunmax.together.service.impl;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.together.dao.LargeSettingDao;
import com.sunmax.together.entity.LargeSettingEntity;
import com.sunmax.together.service.LargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LargeServiceImpl implements LargeService {

    @Autowired
    private LargeSettingDao largeSettingDao;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> largeSetting(String userId, String settingData) {
        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(settingData)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //根据用户id查询大屏设置数据
        Optional<LargeSettingEntity> optional = largeSettingDao.findOne(Example.of(LargeSettingEntity.builder().userId(userId).build()));
        if (optional.isPresent()) {
            LargeSettingEntity entity = optional.get();
            entity.setSettingData(settingData);
            largeSettingDao.save(entity);
        } else {
            largeSettingDao.save(LargeSettingEntity.builder().userId(userId).settingData(settingData).build());
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<String> queryLargeSetting(String userId) {
        Optional<LargeSettingEntity> optional = largeSettingDao.findOne(Example.of(LargeSettingEntity.builder().userId(userId).build()));
        if (optional.isPresent()) {
            return ResponseResult.ok(optional.get().getSettingData());
        }
        return ResponseResult.ok();
    }

}
