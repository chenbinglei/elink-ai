package com.sunmax.crontab.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.crontab.dao.ComputeNodeDao;
import com.sunmax.crontab.dao.SystemVariableDao;
import com.sunmax.crontab.dao.VariableNodeDao;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.crontab.dto.SystemVariableDto;
import com.sunmax.crontab.entity.ComputeNodeEntity;
import com.sunmax.crontab.entity.SystemVariableEntity;
import com.sunmax.crontab.entity.VariableNodeEntity;
import com.sunmax.crontab.service.SystemVariableService;
import com.sunmax.crontab.service.feign.DeviceService;
import com.sunmax.crontab.service.feign.SystemService;
import com.sunmax.crontab.vo.ComputeNodeListVo;
import com.sunmax.crontab.vo.SystemVariableChangeVo;
import com.sunmax.crontab.vo.SystemVariableQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.localDateTimeToStr;

@Slf4j
@Service
public class SystemVariableServiceImpl implements SystemVariableService {

    @Autowired
    private SystemVariableDao systemVariableDao;

    @Autowired
    private VariableNodeDao variableNodeDao;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ComputeNodeDao computeNodeDao;

    @Autowired
    private SystemService systemService;

    /**
     * 添加或编辑系统变量数据
     * @param systemVariableChangeVo
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdateSystemVariable(SystemVariableChangeVo systemVariableChangeVo) {
        //新增系统变量数据
        if (StringUtil.isEmpty(systemVariableChangeVo.getId())) {
            //验证标识是不是唯一
            Optional<SystemVariableEntity> systemVariableCodeOne = systemVariableDao.findOne(Example.of(SystemVariableEntity.builder().varCode(systemVariableChangeVo.getVarCode()).build()));
            if (systemVariableCodeOne.isPresent()) {
                return ResponseResult.paramShow("添加失败，变量标识重复");
            }
            //验证名称是不是唯一
            Optional<SystemVariableEntity> systemVariableNameOne = systemVariableDao.findOne(Example.of(SystemVariableEntity.builder().varName(systemVariableChangeVo.getVarName()).build()));
            if (systemVariableNameOne.isPresent()) {
                return ResponseResult.paramShow("添加失败，变量名称重复");
            }
            SystemVariableEntity systemVariableEntity = new SystemVariableEntity();
            BeanUtils.copyProperties(systemVariableChangeVo, systemVariableEntity);
            systemVariableEntity.setCreateTime(LocalDateTime.now());
            systemVariableEntity.setCreateId(systemVariableChangeVo.getUserId());
            systemVariableEntity.setUpdateId(systemVariableChangeVo.getUserId());
            systemVariableEntity.setUpdateTime(LocalDateTime.now());
            systemVariableDao.save(systemVariableEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        } else {
            //编辑系统变量数据
            Optional<SystemVariableEntity> optional = systemVariableDao.findById(systemVariableChangeVo.getId());
            if (optional.isPresent()) {
                SystemVariableEntity systemVariableEntity = new SystemVariableEntity();
                BeanUtils.copyProperties(systemVariableChangeVo, systemVariableEntity);
                systemVariableEntity.setCreateTime(optional.get().getCreateTime());
                systemVariableEntity.setCreateId(optional.get().getCreateId());
                systemVariableEntity.setUpdateId(systemVariableChangeVo.getUserId());
                systemVariableEntity.setUpdateTime(LocalDateTime.now());
                systemVariableDao.save(systemVariableEntity);
                return ResponseResult.ok(ResponseResult.SUCCESS);
            }
        }
        return ResponseResult.error(ResponseResult.PARAM_ERROR);
    }

    /**
     * 分页查询系统变量数据
     * @param systemVariableQueryVo
     * @return
     */
    @Override
    public ResponseResult<PageDto<SystemVariableDto>> findSystemVariableListByPage(SystemVariableQueryVo systemVariableQueryVo) {
        //返回数据
        List<SystemVariableDto> resultList = Lists.newArrayList();

        //根据查询条件查询系统变量数据
        List<SystemVariableEntity> systemVariablePage = systemVariableDao.findAll((Specification<SystemVariableEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtil.isNotEmpty(systemVariableQueryVo.getKeyword()) && StringUtil.isNotEmpty(systemVariableQueryVo.getKeywordType())) { //关键字
                //根据变量名称模糊查询
                if (systemVariableQueryVo.getKeywordType() == 1) {
                    list.add(cb.like(root.get("varName"), "%" + systemVariableQueryVo.getKeyword() + "%"));
                } else {//根据变量标识模糊查询
                    list.add(cb.like(root.get("varCode"), "%" + systemVariableQueryVo.getKeyword() + "%"));
                }
            }
            //根据变量类型查询
            if (StringUtil.isNotEmpty(systemVariableQueryVo.getVarType())) {
                list.add(cb.equal(root.get("varType"), systemVariableQueryVo.getVarType()));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());
        if (CollectionUtils.isNotEmpty(systemVariablePage)) {
            //设备类型变量
            List<SystemVariableEntity> deviceSystemVariableList = systemVariablePage.stream().filter(s -> s.getVarType() == 1).collect(Collectors.toList());
            Map<String, List<SystemVariableDto.VariableExampleInfo>> deviceVariableExampleMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(deviceSystemVariableList)) {
                //过滤出计算节点变量
                List<SystemVariableEntity> nodeSystemVariableList = deviceSystemVariableList.stream().filter(s -> StringUtil.isNotEmpty(s.getDataSource()) && s.getDataSource() == 1).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(nodeSystemVariableList)) {
                    List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(nodeSystemVariableList.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
                    if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                        //获取所有设备id，并查询设备信息
                        List<String> deviceIdList = variableNodeEntityList.stream().map(VariableNodeEntity::getDeviceId).distinct().collect(Collectors.toList());
                        Map<String, DeviceBasicInfoDto> deviceBasicInfoMap = deviceService.findDeviceBasicInfoByIds(deviceIdList).getData();
                        Map<String, List<SystemVariableDto.VariableExampleInfo>> variableExampleInfo = queryVariableExampleInfo(variableNodeEntityList, deviceBasicInfoMap, null, null, 1);
                        deviceVariableExampleMap.putAll(variableExampleInfo);
                    }
                }
                //过滤出模型功能点变量
                List<SystemVariableEntity> modelSystemVariableList = deviceSystemVariableList.stream().filter(s -> StringUtil.isNotEmpty(s.getDataSource()) && s.getDataSource() == 2).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(modelSystemVariableList)) {
                    List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(modelSystemVariableList.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
                    if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                        //获取所有模型id，并查询模型信息
                        List<String> deviceIdList = variableNodeEntityList.stream().map(VariableNodeEntity::getDeviceId).distinct().collect(Collectors.toList());
                        Map<String, ModelDetailDto> modelBasicInfoMap = deviceService.findModelDetailByIds(deviceIdList).getData();
                        Map<String, List<SystemVariableDto.VariableExampleInfo>> variableExampleInfo = queryVariableExampleInfo(variableNodeEntityList, null, null, modelBasicInfoMap, 1);
                        deviceVariableExampleMap.putAll(variableExampleInfo);
                    }
                }
            }
            //站点类型变量
            List<SystemVariableEntity> siteSystemVariableList = systemVariablePage.stream().filter(s -> s.getVarType() == 2).collect(Collectors.toList());
            Map<String, List<SystemVariableDto.VariableExampleInfo>> siteVariableExampleMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(siteSystemVariableList)) {
                List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(siteSystemVariableList.stream().map(SystemVariableEntity::getId).collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                    //获取所有站点id，并查询站点信息
                    List<String> deviceIdList = variableNodeEntityList.stream().map(VariableNodeEntity::getDeviceId).distinct().collect(Collectors.toList());
                    Map<String, SiteInfoDto> siteBasicInfoDtoMap = deviceService.findSiteBasicInfoByIds(deviceIdList).getData();
                    siteVariableExampleMap = queryVariableExampleInfo(variableNodeEntityList, null, siteBasicInfoDtoMap, null, 2);
                }
            }
            //获取更新人id，并查询用户信息
            List<String> updateIdList = systemVariablePage.stream().map(SystemVariableEntity::getUpdateId).distinct().collect(Collectors.toList());
            Map<String, UserDto> userDtoMap = systemService.findUserInfoByIdsFeign(updateIdList).getData();
            //对数据进行组装
            Map<String, List<SystemVariableDto.VariableExampleInfo>> finalSiteVariableExampleMap = siteVariableExampleMap;
            resultList = systemVariablePage.stream().map(systemVariableEntity -> {
                SystemVariableDto result = new SystemVariableDto();
                BeanUtils.copyProperties(systemVariableEntity, result);
                if (StringUtil.isNotEmpty(systemVariableEntity.getUpdateTime())) {
                    result.setUpadteTime(localDateTimeToStr(systemVariableEntity.getUpdateTime()));
                }
                if (StringUtil.isNotEmpty(systemVariableEntity.getUpdateId()) && userDtoMap.containsKey(systemVariableEntity.getUpdateId())) {
                    result.setUpadteUserName(userDtoMap.get(systemVariableEntity.getUpdateId()).getFullName());
                }
                if (systemVariableEntity.getVarType() == 1) {
                    if (deviceVariableExampleMap.containsKey(systemVariableEntity.getId())) {
                        List<SystemVariableDto.VariableExampleInfo> variableExampleInfoList = deviceVariableExampleMap.get(systemVariableEntity.getId());
                        result.setExampleNumber(variableExampleInfoList.size());
                        result.setVariableExampleInfoList(variableExampleInfoList);
                    }
                } else {
                    if (finalSiteVariableExampleMap.containsKey(systemVariableEntity.getId())) {
                        List<SystemVariableDto.VariableExampleInfo> variableExampleInfoList = finalSiteVariableExampleMap.get(systemVariableEntity.getId());
                        result.setExampleNumber(variableExampleInfoList.size());
                        result.setVariableExampleInfoList(variableExampleInfoList);
                    }
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(resultList, systemVariableQueryVo.getPage(), systemVariableQueryVo.getSize()));
    }

    /**
     * 组装关联实例数据
     * @param variableNodeEntityList 关联实例信息
     * @param deviceBasicInfoMap 设备基本信息对象
     * @param siteBasicInfoDtoMap 站点基本信息对象
     * @param deviceType 设备类型 1-设备 2-站点
     * @return
     */
    private Map<String, List<SystemVariableDto.VariableExampleInfo>> queryVariableExampleInfo(List<VariableNodeEntity> variableNodeEntityList, Map<String, DeviceBasicInfoDto> deviceBasicInfoMap,
                                                                                              Map<String, SiteInfoDto> siteBasicInfoDtoMap, Map<String, ModelDetailDto> modelBasicInfoMap, Integer deviceType) {

        //返回数据对象
        Map<String, List<SystemVariableDto.VariableExampleInfo>> variableExampleMap = Maps.newHashMap();
        //获取所有节点id，并查询节点数据
        List<String> nodeIdList = variableNodeEntityList.stream().map(VariableNodeEntity::getNodeId).distinct().collect(Collectors.toList());
        Map<String, ComputeNodeEntity> computeNodeEntityMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(nodeIdList)) {
            computeNodeEntityMap = computeNodeDao.findAllById(nodeIdList).stream().collect(Collectors.toMap(ComputeNodeEntity::getId, computeNodeEntity -> computeNodeEntity, (k1, k2) -> k1));
        }
        //获取所有功能点id，并查询功能点信息
        List<String> functionIdList = variableNodeEntityList.stream().map(VariableNodeEntity::getFunctionId).distinct().collect(Collectors.toList());
        Map<String, FunctionDetailDto> functionDetailMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(functionIdList)) {
            functionDetailMap = deviceService.findFunctionDetailByIds(functionIdList).getData();
        }
        //根据变量id分组，并循环组装数据
        Map<String, List<VariableNodeEntity>> groupByVarIdMap = variableNodeEntityList.stream().collect(Collectors.groupingBy(VariableNodeEntity::getVarId));
        Map<String, ComputeNodeEntity> finalComputeNodeEntityMap = computeNodeEntityMap;
        Map<String, FunctionDetailDto> finalFunctionDetailMap = functionDetailMap;
        groupByVarIdMap.forEach((varId, variableNodeList) -> {
            List<SystemVariableDto.VariableExampleInfo> variableExampleInfoList = Lists.newArrayList();
            variableNodeList.forEach(variableNodeEntity -> {
                SystemVariableDto.VariableExampleInfo variableExampleInfo = new SystemVariableDto.VariableExampleInfo();
                variableExampleInfo.setId(variableNodeEntity.getId());
                variableExampleInfo.setDeviceId(variableNodeEntity.getDeviceId());
                //获取设备信息
                if (deviceBasicInfoMap != null && deviceBasicInfoMap.containsKey(variableNodeEntity.getDeviceId())) {
                    DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoMap.get(variableNodeEntity.getDeviceId());
                    variableExampleInfo.setDeviceName(deviceBasicInfoDto.getDeviceName());
                }
                //获取模型信息
                if (modelBasicInfoMap != null && modelBasicInfoMap.containsKey(variableNodeEntity.getDeviceId())) {
                    ModelDetailDto modelDetailDto = modelBasicInfoMap.get(variableNodeEntity.getDeviceId());
                    variableExampleInfo.setDeviceName(modelDetailDto.getModelName());
                }
                //获取站点信息
                if (siteBasicInfoDtoMap != null && siteBasicInfoDtoMap.containsKey(variableNodeEntity.getDeviceId())) {
                    SiteInfoDto siteBasicInfoDto = siteBasicInfoDtoMap.get(variableNodeEntity.getDeviceId());
                    variableExampleInfo.setDeviceName(siteBasicInfoDto.getSiteName());
                }
                //获取节点信息
                if (!finalComputeNodeEntityMap.isEmpty() && finalComputeNodeEntityMap.containsKey(variableNodeEntity.getNodeId())) {
                    ComputeNodeEntity computeNodeEntity = finalComputeNodeEntityMap.get(variableNodeEntity.getNodeId());
                    variableExampleInfo.setDataSourceName(computeNodeEntity.getNodeName());
                }
                //获取功能点信息
                if (!finalFunctionDetailMap.isEmpty() && finalFunctionDetailMap.containsKey(variableNodeEntity.getFunctionId())) {
                    FunctionDetailDto functionDetailDto = finalFunctionDetailMap.get(variableNodeEntity.getFunctionId());
                    variableExampleInfo.setDataSourceName(functionDetailDto.getFunctionName());
                }
                variableExampleInfoList.add(variableExampleInfo);
            });
            variableExampleMap.put(varId, variableExampleInfoList);
        });
        return variableExampleMap;
    }

    /**
     * 根据变量id删除系统变量数据
     * @param id
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteSystemVariableById(String id) {
        systemVariableDao.deleteById(id);
        //根据变量id删除关联实例数据
        variableNodeDao.deleteAllByVarId(id);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 根据关联实例id删除关联实例数据
     * @param id
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteVariableNodeById(String id) {
        variableNodeDao.deleteById(id);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 添加实例
     *
     * @param varId
     * @param deviceId
     * @param nodeId
     * @param storageId
     * @param functionId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> addVariableNode(String varId, String deviceId, String nodeId, Long storageId, String functionId) {
        if (StringUtil.isNotEmpty(varId) && StringUtil.isNotEmpty(deviceId)) {
            VariableNodeEntity variableNodeEntity = new VariableNodeEntity();
            variableNodeEntity.setVarId(varId);
            variableNodeEntity.setDeviceId(deviceId);
            variableNodeEntity.setNodeId(nodeId);
            variableNodeEntity.setStorageId(storageId);
            variableNodeEntity.setFunctionId(functionId);
            variableNodeDao.save(variableNodeEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.paramError("有参数为空");
    }

    /**
     * 分页查询未被关联的计算节点数据
     * @param computeNodeListVo
     * @param varId
     * @return
     */
    @Override
    public ResponseResult<PageDto<ComputeNodeListDto>> findNotComputeNodeByPage(ComputeNodeListVo computeNodeListVo, String varId) {
        //返回数据
        List<ComputeNodeListDto> resultList = Lists.newArrayList();
        //根据查询条件插叙计算节点数据
        List<ComputeNodeEntity> computeNodeEntityList = computeNodeDao.findAll((Specification<ComputeNodeEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("deviceId"), computeNodeListVo.getDeviceId()));
            if (StringUtil.isNotEmpty(computeNodeListVo.getKeyword()) && StringUtil.isNotEmpty(computeNodeListVo.getKeywordType())) { //关键字
                //根据节点名称模糊查询
                if (computeNodeListVo.getKeywordType() == 1) {
                    list.add(cb.like(root.get("nodeName"), "%" + computeNodeListVo.getKeyword() + "%"));
                } else {//根据节点标识模糊查询
                    list.add(cb.like(root.get("nodeCode"), "%" + computeNodeListVo.getKeyword() + "%"));
                }
            }
            //根据策略类型查询
            if (StringUtil.isNotEmpty(computeNodeListVo.getStrategyType())) {
                list.add(cb.equal(root.get("strategyType"), computeNodeListVo.getStrategyType()));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());
        if (CollectionUtils.isNotEmpty(computeNodeEntityList)) {
            //查询指定变量下已关联的实例信息
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByVarIdIn(Collections.singletonList(varId));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                computeNodeEntityList = computeNodeEntityList.stream().filter(c -> variableNodeEntityList.stream().noneMatch(v -> Objects.equals(c.getId(), v.getNodeId()))).collect(Collectors.toList());

            }
            if (CollectionUtils.isNotEmpty(computeNodeEntityList)) {
                //对数据进行组装
                resultList = computeNodeEntityList.stream().map(computeNodeEntity -> {
                    ComputeNodeListDto result = new ComputeNodeListDto();
                    BeanUtils.copyProperties(computeNodeEntity, result);
                    result.setCreateTime(localDateTimeToStr(computeNodeEntity.getCreateTime()));
                    return result;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList, computeNodeListVo.getPage(), computeNodeListVo.getSize()));
    }

    /**
     * 分页查询未被关联的模型功能点
     * @param modelId
     * @param varId
     * @param functionName
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult<PageDto<ModelFunctionListDto>> findModelFunctionListByPage(String modelId, String varId, String functionName, Integer page, Integer size) {
        List<ModelFunctionListDto> resultList = Lists.newArrayList();
        //根据模型id查询模型功能点列表
        ResponseResult<Map<String, List<ModelFunctionListDto>>> modelFunctionListByModelIds = deviceService.findModelFunctionListByModelIds(Collections.singletonList(modelId));
        if (modelFunctionListByModelIds.isSuccess() && !modelFunctionListByModelIds.getData().isEmpty()) {
            resultList = modelFunctionListByModelIds.getData().get(modelId);
            //根据变量id查询已关联的模型功能点
            List<VariableNodeEntity> variableNodeEntityList = variableNodeDao.findAllByDeviceIdAndVarIdIn(modelId, Collections.singletonList(varId));
            if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                //过滤掉已关联的模型功能点
                resultList = resultList.stream().filter(m -> variableNodeEntityList.stream().noneMatch(v -> Objects.equals(m.getFunctionId(), v.getFunctionId()))).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(resultList)) {
                if (StringUtil.isNotEmpty(functionName)) {
                    resultList = resultList.stream().filter(m -> m.getFunctionName().contains(functionName)).collect(Collectors.toList());
                }
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList, page, size));
    }
}
