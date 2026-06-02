package com.sunmax.crontab.service.impl;

import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.crontab.PileStartControlVo;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import com.sunmax.crontab.service.ProtocolFeignService;
import com.sunmax.crontab.service.feign.DeviceService;
import com.sunmax.crontab.service.feign.TogetherService;
import com.sunmax.crontab.util.StrategyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProtocolFeignServiceImpl implements ProtocolFeignService {

    @Autowired
    private TogetherService togetherService;

    @Autowired
    private DeviceService deviceService;

    @Override
    public ResponseResult<Boolean> checkPileStartControl(PileStartControlVo pileStartControlVo) {
        //1.根据站点id查询策略任务数据
        String siteId = pileStartControlVo.getSiteId();
        List<StrategyTaskVo> strategyTaskVoList = togetherService.findStrategyHandTaskList(siteId).getData();
        if (CollectionUtils.isEmpty(strategyTaskVoList)) {
            return ResponseResult.ok(false);
        }

        //2.校验当前启动的电桩是否在策略配置里面
        StrategyTaskVo strategyTaskVo = strategyTaskVoList.get(0);
        String keyId = pileStartControlVo.getPileCode() + pileStartControlVo.getGunCode();
        if (!strategyTaskVo.getStrategyPileMap().containsKey(keyId)) {
            return ResponseResult.ok(false);
        }

        //3.把启动控制时间存入到电桩启动map里
        StrategyUtil.pileOccupyMap.put(keyId, pileStartControlVo);

        //4.根据站点id查询来源类型
        Integer sourceType = 1;
        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId))
                .getData().get(siteId);
        if (siteInfo != null && StringUtil.isNotEmpty(siteInfo.getSourceType())) {
            sourceType = siteInfo.getSourceType();
        }

        //5.响应自动控制计算数据
        Boolean result = StrategyUtil.platformAutoControlTask(strategyTaskVo, sourceType);
        log.info("启动电桩控制状态: " + result);
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<Boolean> updatePileStartControl(PileStartControlVo pileStartControlVo) {
        String keyId = pileStartControlVo.getPileCode() + pileStartControlVo.getGunCode();
        //1.把电桩占用map删除掉
        Map<String, PileStartControlVo> pileOccupyMap = StrategyUtil.pileOccupyMap;
        if (pileOccupyMap.containsKey(keyId)) {
            //执行结果 -1-执行超时 0-执行成功 1-执行失败 255-其他原因 500-平台处理报错
            switch (pileStartControlVo.getResult()) {
                case -1:
                case 255:
                case 500:
                    //1.电桩离线(资源占用至上线,最大占用5h)
                    //2.实际启动成功/失败(电桩状态结合输出情况判断 释放占用功率)
                    PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileStartControlVo.getPileCode());
                    if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) {
                        Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileRealModel.getGunRealModelMap();
                        if (StringUtil.isNotEmpty(gunRealModelMap) && gunRealModelMap.containsKey(pileStartControlVo.getGunCode())) {
                            Integer gunStatus = gunRealModelMap.get(pileStartControlVo.getGunCode()).getGunStatus();
                            if (StringUtil.isNotEmpty(gunStatus) && (gunStatus != 1 && gunStatus != 4)) {
                                pileOccupyMap.remove(keyId);
                            }
                        }
                    }
                    break;
                case 0:
                case 1:
                    pileOccupyMap.remove(keyId);
                    break;
            }

        }

        //2.根据站点记录id查询策略任务数据
        String siteId = pileStartControlVo.getSiteId();
        List<StrategyTaskVo> strategyTaskVoList = togetherService.findStrategyHandTaskList(siteId).getData();
        if (CollectionUtils.isEmpty(strategyTaskVoList)) {
            return ResponseResult.ok(false);
        }

        //3.校验当前启动的电桩是否在策略配置里面
        StrategyTaskVo strategyTaskVo = strategyTaskVoList.get(0);
        if (!strategyTaskVo.getStrategyPileMap().containsKey(keyId)) {
            return ResponseResult.ok(false);
        }

        //4.根据站点id查询来源类型
        Integer sourceType = 1;
        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId))
                .getData().get(siteId);
        if (siteInfo != null && StringUtil.isNotEmpty(siteInfo.getSourceType())) {
            sourceType = siteInfo.getSourceType();
        }

        //5.响应自动控制计算数据
        Boolean result = StrategyUtil.platformAutoControlTask(strategyTaskVo, sourceType);
        log.info("更新电桩控制状态: " + result);
        return ResponseResult.ok(result);
    }

}
