package com.sunmax.together.service.asset.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.entity.BaseTimeEntity;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.together.dao.asset.ElectConfigDao;
import com.sunmax.together.dao.asset.ElectTimeFrameDao;
import com.sunmax.together.dto.asset.electConfig.ElectConfigDetailDto;
import com.sunmax.together.dto.asset.electConfig.ElectConfigListDto;
import com.sunmax.together.dto.asset.electConfig.ElectTimeFrameDto;
import com.sunmax.together.entity.assets.ElectConfigEntity;
import com.sunmax.together.entity.assets.ElectTimeFrameEntity;
import com.sunmax.together.service.asset.ElectConfigService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.vo.operation.electConfig.ElectConfigChangeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ElectConfigServiceImpl implements ElectConfigService {

    @Resource
    private ElectConfigDao electConfigDao;

    @Resource
    private ElectTimeFrameDao electTimeFrameDao;

    @Resource
    private DeviceService deviceService;

    /**
     * 验证电价策略入参
     */
    private Boolean validateElectConfig(ElectConfigChangeVo electConfigVo) {
        // 检查输入对象是否为null
        if (electConfigVo == null) {
            // 可以考虑添加日志记录这里的失败原因
            return false;
        }

        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                electConfigVo.getSiteId(),
                electConfigVo.getModuleType(),
                electConfigVo.getStrategyName(),
                electConfigVo.getStartDate(),
                electConfigVo.getEndDate(),
                electConfigVo.getPriceType(),
                electConfigVo.getElectTimeFrames()
        };

        // 它同时处理了空和只含空格的字符串
        return Arrays.stream(fieldsToValidate).noneMatch(StringUtil::isEmpty);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveElectConfig(ElectConfigChangeVo electConfigVo) {

        //检验入参
        if (!validateElectConfig(electConfigVo)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        //根据查询参数查询电价配置
        List<ElectConfigEntity> electConfigList = electConfigDao.findAll(Example.of(ElectConfigEntity.builder().siteId(electConfigVo.getSiteId())
                .moduleType(electConfigVo.getModuleType()).build()));
        //新增电价策略
        if (StringUtil.isEmpty(electConfigVo.getId())) {
            //判断策略名称是否已存在
            if (CollectionUtils.isNotEmpty(electConfigList)) {
                //根据策略名称过滤
                List<ElectConfigEntity> electNameList = electConfigList.stream().filter(e -> Objects.equals(e.getStrategyName(),
                        electConfigVo.getStrategyName())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(electNameList)) {
                    return ResponseResult.paramError("策略名称已存在");
                }
                //校验时间段是否重叠
                boolean result = electConfigList.stream().anyMatch(p1 -> {
                    LocalDate s1 = LocalDate.parse(electConfigVo.getStartDate());
                    LocalDate e1 = LocalDate.parse(electConfigVo.getEndDate());
                    LocalDate s2 = LocalDate.parse(p1.getStartDate());
                    LocalDate e2 = LocalDate.parse(p1.getEndDate());
                    return !s1.isAfter(e2) && !s2.isAfter(e1);
                });
                if (result) { //有重叠
                    return ResponseResult.paramError("站点电价参数冲突,不允许添加");
                }
            }
            ElectConfigEntity electConfig = new ElectConfigEntity();
            BeanUtils.copyProperties(electConfigVo, electConfig);
            ElectConfigEntity save = electConfigDao.save(electConfig);
            //批量添加电价时间段
            List<ElectConfigChangeVo.ElectTimeFrame> electTimeFrameList = JSON.parseArray(electConfigVo.getElectTimeFrames(),
                    ElectConfigChangeVo.ElectTimeFrame.class);
            if (CollectionUtils.isNotEmpty(electTimeFrameList)) {
                electTimeFrameDao.saveAll(electTimeFrameList.stream().map(electTimeFrameVo -> {
                    ElectTimeFrameEntity electTimeFrame = new ElectTimeFrameEntity();
                    BeanUtils.copyProperties(electTimeFrameVo, electTimeFrame);
                    electTimeFrame.setElectConfigId(save.getId());
                    return electTimeFrame;
                }).collect(Collectors.toList()));
            }
            return ResponseResult.ok();
        } else { //编辑电价策略
            //判断策略名称是否已存在
            electConfigList = electConfigList.stream().filter(electConfig -> !Objects.equals(electConfig.getId(), electConfigVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(electConfigList)) {
                //根据策略名称过滤
                List<ElectConfigEntity> electNameList = electConfigList.stream().filter(e -> Objects.equals(e.getStrategyName(),
                        electConfigVo.getStrategyName())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(electNameList)) {
                    return ResponseResult.paramError("策略名称已存在");
                }
                //校验时间段是否重叠
                boolean result = electConfigList.stream().anyMatch(p1 -> {
                    LocalDate s1 = LocalDate.parse(electConfigVo.getStartDate());
                    LocalDate e1 = LocalDate.parse(electConfigVo.getEndDate());
                    LocalDate s2 = LocalDate.parse(p1.getStartDate());
                    LocalDate e2 = LocalDate.parse(p1.getEndDate());
                    return !s1.isAfter(e2) && !s2.isAfter(e1);
                });
                if (result) { //有重叠
                    return ResponseResult.paramError("站点电价参数冲突,不允许编辑");
                }
            }
            Optional<ElectConfigEntity> optional = electConfigDao.findById(electConfigVo.getId());
            if (optional.isPresent()) {
                //编辑电价策略参数
                ElectConfigEntity electConfig = new ElectConfigEntity();
                BeanUtils.copyProperties(electConfigVo, electConfig);
                electConfig.setCreateTime(optional.get().getCreateTime());
                electConfigDao.save(electConfig);
                //批量删除电价时间段
                electTimeFrameDao.deleteAll(electTimeFrameDao.findAll(Example.of(ElectTimeFrameEntity.builder().electConfigId(electConfigVo.getId()).build())));
                //批量添加电价时间段
                List<ElectConfigChangeVo.ElectTimeFrame> electTimeFrameList = JSON.parseArray(electConfigVo.getElectTimeFrames(),
                        ElectConfigChangeVo.ElectTimeFrame.class);
                if (CollectionUtils.isNotEmpty(electTimeFrameList)) {
                    electTimeFrameDao.saveAll(electTimeFrameList.stream().map(electTimeFrameVo -> {
                        ElectTimeFrameEntity electTimeFrame = new ElectTimeFrameEntity();
                        BeanUtils.copyProperties(electTimeFrameVo, electTimeFrame);
                        electTimeFrame.setElectConfigId(electConfigVo.getId());
                        return electTimeFrame;
                    }).collect(Collectors.toList()));
                }
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteAllElectConfigByIds(List<String> ids) {
        //根据多个电价策略配置id查询电价策略配置
        List<ElectConfigEntity> electConfigList = electConfigDao.findAllById(ids);
        if (CollectionUtils.isNotEmpty(electConfigList)) {
            electConfigDao.deleteAll(electConfigList);
        }
        List<ElectTimeFrameEntity> electTimeFrameList = electTimeFrameDao.findAllByElectConfigIdIn(ids);
        if (CollectionUtils.isNotEmpty(electTimeFrameList)) {
            electTimeFrameDao.deleteAll(electTimeFrameList);
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<Map<Integer, List<ElectConfigListDto>>> queryElectConfigList(String siteId) {
        //返回的对象map
        Map<Integer, List<ElectConfigListDto>> resultMap = Maps.newHashMap();
        //根据站点id查询电价策略配置列表
        List<ElectConfigEntity> electConfigList = electConfigDao.findAll(Example.of(ElectConfigEntity.builder().siteId(siteId).build()));
        if (CollectionUtils.isNotEmpty(electConfigList)) {
            //根据多个电价策略id查询电价策略配置时段数据
            Set<String> electConfigIds = electConfigList.stream().map(BaseTimeEntity::getId).collect(Collectors.toSet());
            Map<String, List<ElectTimeFrameEntity>> electTimeFrameMap = electTimeFrameDao.findAllByElectConfigIdIn(electConfigIds).stream()
                    .collect(Collectors.groupingBy(ElectTimeFrameEntity::getElectConfigId));
            //对数据进行组装
            resultMap = electConfigList.stream().map(electConfig -> {
                ElectConfigListDto result = new ElectConfigListDto();
                BeanUtils.copyProperties(electConfig, result);
                if (electTimeFrameMap.containsKey(electConfig.getId())) {
                    result.setElectTimeFrameList(electTimeFrameMap.get(electConfig.getId()).stream().map(electTimeFrame -> {
                        ElectTimeFrameDto electTimeFrameDto = new ElectTimeFrameDto();
                        BeanUtils.copyProperties(electTimeFrame, electTimeFrameDto);
                        return electTimeFrameDto;
                    }).collect(Collectors.toList()));
                }
                return result;
            }).collect(Collectors.groupingBy(ElectConfigListDto::getModuleType));
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<ElectConfigDetailDto> findElectConfigById(String id) {
        //返回的对象
        ElectConfigDetailDto result = new ElectConfigDetailDto();
        //根据电价配置id查询电价策略配置
        Optional<ElectConfigEntity> optional = electConfigDao.findById(id);
        if (optional.isPresent()) {
            ElectConfigEntity electConfig = optional.get();
            BeanUtils.copyProperties(electConfig, result);
            result.setElectTimeFrameList(electTimeFrameDao.findAll(Example.of(ElectTimeFrameEntity.builder().electConfigId(id).build()))
                    .stream().map(electTimeFrame -> {
                        ElectTimeFrameDto electTimeFrameDto = new ElectTimeFrameDto();
                        BeanUtils.copyProperties(electTimeFrame, electTimeFrameDto);
                        return electTimeFrameDto;
                    }).collect(Collectors.toList()));
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<List<String>> applyElectConfigToOtherSite(List<String> electConfigIds, List<String> siteIds) {
        List<ElectConfigEntity> electConfigList = electConfigDao.findAllById(electConfigIds).stream()
                .filter(e -> StringUtil.isNotEmpty(e.getStartDate()) && StringUtil.isNotEmpty(e.getEndDate()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(electConfigList)) {
            //根据多个站点id查询电价策略配置数据
            List<ElectConfigEntity> siteElectConfigList = electConfigDao.findAllBySiteIdIn(siteIds);
            if (CollectionUtils.isNotEmpty(siteElectConfigList)) {
                Map<String, List<ElectConfigEntity>> siteElectConfigMap = siteElectConfigList.stream()
                        .filter(e -> StringUtil.isNotEmpty(e.getStartDate()) && StringUtil.isNotEmpty(e.getEndDate()))
                        .collect(Collectors.groupingBy(ElectConfigEntity::getSiteId));
                //定义站点id集合
                List<String> siteIdList = Lists.newArrayList();
                for (Map.Entry<String, List<ElectConfigEntity>> entry : siteElectConfigMap.entrySet()) {
                    String siteId = entry.getKey();
                    //校验电价里面的生效时间是否重叠
                    boolean result = electConfigList.stream().anyMatch(p1 ->
                            entry.getValue().stream().anyMatch(p2 -> {
                                LocalDate s1 = LocalDate.parse(p1.getStartDate());
                                LocalDate e1 = LocalDate.parse(p1.getEndDate());
                                LocalDate s2 = LocalDate.parse(p2.getStartDate());
                                LocalDate e2 = LocalDate.parse(p2.getEndDate());
                                return !s1.isAfter(e2) && !s2.isAfter(e1);
                            })
                    );
                    if (result) { //有重叠
                        siteIdList.add(siteId);
                    }
                }
                if (CollectionUtils.isNotEmpty(siteIdList)) {
                    //根据多个站点id查询站点信息
                    return ResponseResult.error(ResponseResult.FAIL, ResponseResult.CodeStatus.SHOW, deviceService.findSiteBasicInfoByIds(siteIdList).getData()
                            .values().stream().map(SiteInfoDto::getSiteName).collect(Collectors.toList()));
                }
            }

            //根据多个电价策略id查询电价策略配置时段数据
            Map<String, List<ElectTimeFrameEntity>> electTimeFrameMap = electTimeFrameDao.findAllByElectConfigIdIn(electConfigIds)
                    .stream().collect(Collectors.groupingBy(ElectTimeFrameEntity::getElectConfigId));

            //定义电价配置列表和电价策略时段列表
            List<ElectConfigEntity> addElectConfigList = Lists.newArrayList();
            Map<String, List<ElectTimeFrameEntity>> siteElectTimeFrameMap = Maps.newHashMap();
            //批量添加站点电价策略配置数据
            for (String siteId : siteIds) {
                addElectConfigList.addAll(electConfigList.stream().map(electConfig -> {
                    ElectConfigEntity electConfigEntity = new ElectConfigEntity();
                    BeanUtils.copyProperties(electConfig, electConfigEntity);
                    electConfigEntity.setId(null);
                    electConfigEntity.setSiteId(siteId);
                    if (electTimeFrameMap.containsKey(electConfig.getId())) {
                        List<ElectTimeFrameEntity> electTimeFrameList = electTimeFrameMap.get(electConfig.getId()).stream().map(electTimeFrame -> {
                            ElectTimeFrameEntity electTimeFrameEntity = new ElectTimeFrameEntity();
                            BeanUtils.copyProperties(electTimeFrame, electTimeFrameEntity);
                            electTimeFrameEntity.setId(null);
                            electTimeFrameEntity.setElectConfigId(null);
                            return electTimeFrameEntity;
                        }).collect(Collectors.toList());
                        siteElectTimeFrameMap.put(electConfigEntity.getSiteId() + electConfigEntity.getModuleType() + electConfigEntity.getStrategyName(), electTimeFrameList);
                    }
                    return electConfigEntity;
                }).collect(Collectors.toList()));
            }

            //根据站点id+模块类型+策略名称进行分组获取策略配置id
            Map<String, String> electConfigIdMap = electConfigDao.saveAll(addElectConfigList).stream().collect(Collectors
                    .toMap(e -> e.getSiteId() + e.getModuleType() + e.getStrategyName(), BaseTimeEntity::getId,
                            (k1, k2) -> k1));
            //批量添加站点电价策略时段数据
            electTimeFrameDao.saveAll(siteElectTimeFrameMap.entrySet().stream()
                    .filter(entry -> electConfigIdMap.containsKey(entry.getKey()))
                    .flatMap(entry -> entry.getValue().stream().peek(t -> t.setElectConfigId(electConfigIdMap.get(entry.getKey()))))
                    .collect(Collectors.toList()));
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }


}
