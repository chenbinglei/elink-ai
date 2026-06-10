package com.sunmax.crontab.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.common.dto.crontab.LocalCacheDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.util.CronUtil;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.crontab.config.cache.LocalCache;
import com.sunmax.crontab.dao.ComputeNodeDao;
import com.sunmax.crontab.dao.NodeLogInfoDao;
import com.sunmax.crontab.dao.NodeParamDao;
import com.sunmax.crontab.dao.VariableNodeDao;
import com.sunmax.crontab.dto.*;
import com.sunmax.crontab.entity.ComputeNodeEntity;
import com.sunmax.crontab.entity.NodeLogInfoEntity;
import com.sunmax.crontab.entity.NodeParamEntity;
import com.sunmax.crontab.mapper.tdengine.ComputeNodeMapper;
import com.sunmax.crontab.service.ComputeNodeService;
import com.sunmax.crontab.service.NodeTaskService;
import com.sunmax.crontab.service.feign.DeviceService;
import com.sunmax.crontab.vo.ComputeNodeChangeVo;
import com.sunmax.crontab.vo.ComputeNodeListVo;
import com.sunmax.crontab.vo.ComputeNodeTaskVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.crontab.util.ScheduleTaskUtil.NODE_TABLE;
import static com.sunmax.crontab.util.ScheduleTaskUtil.STABLE_NAME;

@Slf4j
@Service
public class ComputeNodeServiceImpl implements ComputeNodeService {

    @Autowired
    private ComputeNodeDao computeNodeDao;

    @Autowired
    private ComputeNodeMapper computeNodeMapper;

    @Autowired
    private NodeParamDao nodeParamDao;

    @Autowired
    private NodeTaskService nodeTaskService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private LocalCache localCache;

    @Autowired
    private VariableNodeDao variableNodeDao;

    @Autowired
    private NodeLogInfoDao nodeLogInfoDao;

    /**
     * 保存或编辑计算节点信息
     * @param computeNodeChangeVo
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdateComputeNodeInfo(ComputeNodeChangeVo computeNodeChangeVo) {
        if (StringUtil.isNotEmpty(computeNodeChangeVo)) {
            //节点id
            String nodeId;
            //新增计算节点数据
            if (StringUtil.isEmpty(computeNodeChangeVo.getId())) {
                //验证标识是不是唯一
                Optional<ComputeNodeEntity> computeNodeDaoOne = computeNodeDao.findOne(Example.of(ComputeNodeEntity.builder().nodeCode(computeNodeChangeVo.getNodeCode()).build()));
                if (computeNodeDaoOne.isPresent()) {
                    return ResponseResult.paramShow("添加失败，节点标识重复");
                }
                ComputeNodeEntity computeNodeEntity = new ComputeNodeEntity();
                BeanUtils.copyProperties(computeNodeChangeVo, computeNodeEntity);
                String computePeriod = computeNodeChangeVo.getComputePeriod();
                if (StringUtil.isNotEmpty(computePeriod)) {
                    computeNodeEntity.setCronExpression(CronUtil.getCronTriggerByType(Integer.valueOf(computePeriod.substring(0, computePeriod.length() - 1)),
                            computePeriod.substring(computePeriod.length() - 1)));
                }
                computeNodeEntity.setCreateTime(LocalDateTime.now());
                computeNodeEntity.setCreateId(computeNodeChangeVo.getUserId());
                computeNodeEntity.setUpdateTime(LocalDateTime.now());
                //查询数据库中最新的一个存储id
                Integer computeNodes = computeNodeDao.findNodeStorageId();
                long storageId = 1L;
                if (StringUtil.isNotEmpty(computeNodes)) {
                    storageId = computeNodes + 1;
                }
                computeNodeEntity.setStorageId(storageId);
                ComputeNodeEntity computeNode = computeNodeDao.save(computeNodeEntity);

                nodeId = computeNode.getId();

                //把节点添加到定时任务里面
                ComputeNodeTaskVo computeNodeTaskVo = new ComputeNodeTaskVo();
                BeanUtils.copyProperties(computeNode, computeNodeTaskVo);
                computeNodeTaskVo.setTaskSource(1);
                //处理参数
                String nodeParamInfos = computeNodeChangeVo.getNodeParamInfos();
                if (StringUtil.isNotEmpty(nodeParamInfos)) {
                    computeNodeTaskVo.setNodeParamInfoList(JSON.parseArray(nodeParamInfos, NodeParamInfoDto.class));
                }
                //查询超级表在不在
                Map<String, Object> stableIfExists = computeNodeMapper.findSTableIfExists(STABLE_NAME);
                if (stableIfExists == null || stableIfExists.isEmpty()) {
                    computeNodeMapper.createSuperTable(STABLE_NAME);
                }
                //先创建taos表
                computeNodeMapper.createTaosTable(NODE_TABLE + computeNode.getStorageId(), STABLE_NAME, computeNode.getSiteId(), computeNode.getStorageId());
                //把节点加入定时任务
                nodeTaskService.addAllComputeNodeTask(Collections.singletonList(computeNodeTaskVo));
            } else {
                nodeId = computeNodeChangeVo.getId();
                //编辑计算节点数据
                Optional<ComputeNodeEntity> optional = computeNodeDao.findById(computeNodeChangeVo.getId());
                if (optional.isPresent()) {
                    ComputeNodeEntity computeNodeEntity = new ComputeNodeEntity();
                    BeanUtils.copyProperties(computeNodeChangeVo, computeNodeEntity);
                    String computePeriod = computeNodeChangeVo.getComputePeriod();
                    if (StringUtil.isNotEmpty(computePeriod)) {
                        computeNodeEntity.setCronExpression(CronUtil.getCronTriggerByType(Integer.valueOf(computePeriod.substring(0, computePeriod.length() - 1)),
                                computePeriod.substring(computePeriod.length() - 1)));
                    }
                    computeNodeEntity.setStorageId(optional.get().getStorageId());
                    computeNodeEntity.setUpdateId(computeNodeChangeVo.getUserId());
                    computeNodeEntity.setCreateTime(optional.get().getCreateTime());
                    computeNodeEntity.setUpdateTime(LocalDateTime.now());
                    ComputeNodeEntity nodeEntity = computeNodeDao.save(computeNodeEntity);
                    //把节点添加到定时任务里面
                    ComputeNodeTaskVo computeNodeTaskVo = new ComputeNodeTaskVo();
                    BeanUtils.copyProperties(nodeEntity, computeNodeTaskVo);
                    computeNodeTaskVo.setTaskSource(2);
                    //处理参数
                    String nodeParamInfos = computeNodeChangeVo.getNodeParamInfos();
                    if (StringUtil.isNotEmpty(nodeParamInfos)) {
                        computeNodeTaskVo.setNodeParamInfoList(JSON.parseArray(nodeParamInfos, NodeParamInfoDto.class));
                    }
                    nodeTaskService.updateComputeNodeListTask(Collections.singletonList(computeNodeTaskVo));
                }
            }
            //处理参数信息
            String nodeParamInfos = computeNodeChangeVo.getNodeParamInfos();
            if (StringUtil.isNotEmpty(nodeParamInfos)) {
                //根据节点id查询参数信息，如果有则删除重新插入
                List<NodeParamEntity> nodeParamEntityList = nodeParamDao.findAllByNodeId(nodeId);
                if (CollectionUtils.isNotEmpty(nodeParamEntityList)) {
                    nodeParamDao.deleteAll(nodeParamEntityList);
                }
                nodeParamDao.saveAll(JSON.parseArray(nodeParamInfos, NodeParamEntity.class).stream().peek(n -> n.setNodeId(nodeId)).collect(Collectors.toList()));
            }
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.PARAM_ERROR);
    }

    /**
     * 分页查询计算节点数据
     * @param computeNodeListVo
     * @return
     */
    @Override
    public ResponseResult<PageDto<ComputeNodeListDto>> findComputeNodeByPage(ComputeNodeListVo computeNodeListVo) {
        //返回数据数组
        List<ComputeNodeListDto> resultList = Lists.newArrayList();

        //根据查询条件插叙计算节点数据
        Page<ComputeNodeEntity> computeNodeDaoAll = computeNodeDao.findAll((Specification<ComputeNodeEntity>) (root, cq, cb) -> {
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
        }, PageRequest.of(computeNodeListVo.getPage() - 1, computeNodeListVo.getSize(), Sort.by("createTime").descending()));
        if (CollectionUtils.isNotEmpty(computeNodeDaoAll.getContent())) {
            //对数据进行组装
            resultList = computeNodeDaoAll.getContent().stream().map(computeNodeEntity -> {
                ComputeNodeListDto result = new ComputeNodeListDto();
                BeanUtils.copyProperties(computeNodeEntity, result);
                result.setCreateTime(localDateTimeToStr(computeNodeEntity.getCreateTime()));
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(resultList, computeNodeDaoAll.getNumber() + 1, computeNodeDaoAll.getSize(), (int) computeNodeDaoAll.getTotalElements()));
    }

    /**
     * 根据id查询计算节点详情
     * @param id
     * @return
     */
    @Override
    public ResponseResult<ComputeNodeInfoDto> findComputeNodeInfoById(String id) {
        ComputeNodeInfoDto computeNodeInfoDto = new ComputeNodeInfoDto();
        Optional<ComputeNodeEntity> computeNodeDaoById = computeNodeDao.findById(id);
        if (computeNodeDaoById.isPresent()) {
            BeanUtils.copyProperties(computeNodeDaoById.get(), computeNodeInfoDto);
            computeNodeInfoDto.setCreateTime(localDateTimeToStr(computeNodeDaoById.get().getCreateTime()));
            //根据节点id查询参数信息
            List<NodeParamEntity> nodeParamEntityList = nodeParamDao.findAllByNodeId(id);
            if (CollectionUtils.isNotEmpty(nodeParamEntityList)) {
                computeNodeInfoDto.setNodeParamInfoList(nodeParamEntityList.stream().map(nodeParamEntity -> {
                    ComputeNodeInfoDto.NodeParamInfo nodeParamInfo = new ComputeNodeInfoDto.NodeParamInfo();
                    BeanUtils.copyProperties(nodeParamEntity, nodeParamInfo);
                    return nodeParamInfo;
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(computeNodeInfoDto);
    }

    /**
     * 根据节点id删除计算节点数据
     * @param id
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteAllComputeNodeById(String id) {
        Optional<ComputeNodeEntity> computeNodeDaoById = computeNodeDao.findById(id);
        if (computeNodeDaoById.isPresent()) {
            computeNodeDao.deleteById(id);
            //删除节点下参数信息
            nodeParamDao.deleteAllByNodeId(id);
            //删除该节点定时任务以及节点表
            nodeTaskService.deleteComputeNodeTaskAndTaosData(computeNodeDaoById.get().getStorageId());
            //删除节点和变量关联信息
            variableNodeDao.deleteAllByNodeId(id);
        }
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 根据设备id查询计算节点列表
     * @param deviceId
     * @return
     */
    @Override
    public ResponseResult<List<ComputeNodeListDto>> findNodeListByDeviceId(String deviceId) {
        //返回数据数组
        List<ComputeNodeListDto> resultList = Lists.newArrayList();

        //根据查询条件插叙计算节点数据
        List<ComputeNodeEntity> computeNodeEntityList = computeNodeDao.findAll((Specification<ComputeNodeEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("deviceId"), deviceId));
            return cb.and(list.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());
        if (CollectionUtils.isNotEmpty(computeNodeEntityList)) {
            //对数据进行组装
            resultList = computeNodeEntityList.stream().map(computeNodeEntity -> {
                ComputeNodeListDto result = new ComputeNodeListDto();
                BeanUtils.copyProperties(computeNodeEntity, result);
                result.setCreateTime(localDateTimeToStr(computeNodeEntity.getCreateTime()));
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 查询全部计算节点任务列表
     * @return
     */
    @Override
    public List<ComputeNodeTaskVo> findAllComputeNodeList() {
        List<ComputeNodeTaskVo> computeNodeTaskVoList = Lists.newArrayList();
        List<ComputeNodeEntity> computeNodeDaoAll = computeNodeDao.findAll();
        if (CollectionUtils.isNotEmpty(computeNodeDaoAll)) {
            //获取所有节点id，并查询相应参数列表信息
            Map<String, List<NodeParamEntity>> groupByNodeIdMap = nodeParamDao.findAllByNodeIdIn(computeNodeDaoAll.stream().map(ComputeNodeEntity::getId).collect(Collectors.toList()))
                    .stream().collect(Collectors.groupingBy(NodeParamEntity::getNodeId));
            computeNodeTaskVoList = computeNodeDaoAll.stream().map(computeNodeEntity -> {
                ComputeNodeTaskVo computeNodeTaskVo = new ComputeNodeTaskVo();
                BeanUtils.copyProperties(computeNodeEntity, computeNodeTaskVo);
                computeNodeTaskVo.setTaskSource(2);
                if (groupByNodeIdMap.containsKey(computeNodeEntity.getId())) {
                    //存储参数列表
                    computeNodeTaskVo.setNodeParamInfoList(groupByNodeIdMap.get(computeNodeEntity.getId()).stream().map(nodeParamEntity -> {
                        NodeParamInfoDto nodeParamInfo = new NodeParamInfoDto();
                        BeanUtils.copyProperties(nodeParamEntity, nodeParamInfo);
                        return nodeParamInfo;
                    }).collect(Collectors.toList()));
                }
                return computeNodeTaskVo;
            }).collect(Collectors.toList());

        }
        return computeNodeTaskVoList;
    }

    /**
     * 根据站点id查询下面设备列表
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<List<DeviceBasicInfoDto>> findSiteDeviceDataById(String siteId) {
        return ResponseResult.ok(deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null).getData().get(siteId));
    }

    /**
     * 根据设备id查询功能点列表
     * @param deviceId
     * @return
     */
    @Override
    public ResponseResult<List<ModelFunctionListDto>> findDeviceFunctionListById(String deviceId) {
        return ResponseResult.ok(deviceService.findDeviceFunctionListByIds(Collections.singletonList(deviceId)).getData().get(deviceId));
    }

    /**
     * 根据站点/设备id查询计算节点列表
     *
     * @param deviceId
     * @return
     */
    @Override
    public ResponseResult<List<ComputeNodeListDto>> findComputeNodeListById(String deviceId) {
        //返回数据
        List<ComputeNodeListDto> computeNodeListDtos = Lists.newArrayList();

        List<ComputeNodeEntity> computeNodeEntityList = computeNodeDao.findAllByDeviceId(deviceId);
        if (CollectionUtils.isNotEmpty(computeNodeEntityList)) {
            computeNodeListDtos = computeNodeEntityList.stream().map(computeNodeEntity -> {
                ComputeNodeListDto computeNodeListDto = new ComputeNodeListDto();
                BeanUtils.copyProperties(computeNodeEntity, computeNodeListDto);
                return computeNodeListDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(computeNodeListDtos);
    }

    /**
     * 根据多个节点id查询本地缓存数据
     * @param ids
     * @return
     */
    @Override
    public ResponseResult<Map<String, LocalCacheDto>> findLocalCacheDataByIds(String ids) {
        Map<String, LocalCacheDto> dataMap = Maps.newHashMap();
        if (StringUtil.isNotEmpty(ids)) {
            List<String> idList = Arrays.stream(ids.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toList());
            idList.forEach(id -> {
                LocalCacheDto localCacheDto = localCache.getValue(id);
                dataMap.put(id, localCacheDto);
            });
        }
        return ResponseResult.ok(dataMap);
    }

    /**
     * 分页查询节点日志信息
     * @param nodeId
     * @param queryDate
     * @param queryType
     * @return
     */
    @Override
    public ResponseResult<PageDto<NodeLogInfoDto>> findNodeLogInfoListByPage(String nodeId, String queryDate, Integer queryType, Integer page, Integer size) {
        //返回数据
        List<NodeLogInfoDto> resultList = Lists.newArrayList();
        //根据查询条件插叙计算节点数据
        Page<NodeLogInfoEntity> nodeLogInfoEntities = nodeLogInfoDao.findAll((Specification<NodeLogInfoEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("nodeId"), nodeId));
            if (StringUtil.isNotEmpty(queryDate)) {
                list.add(cb.between(root.get("tsTime"), DateUtil.getDayStart(queryDate), DateUtil.getDayEnd(queryDate)));
            }
            //根据日志类型查询
            if (StringUtil.isNotEmpty(queryType)) {
                list.add(cb.equal(root.get("logType"), queryType));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, PageRequest.of(page - 1, size, Sort.by("logTime").descending()));
        if (CollectionUtils.isNotEmpty(nodeLogInfoEntities.getContent())) {
            resultList = nodeLogInfoEntities.getContent().stream().map(nodeLogInfoEntity -> {
                NodeLogInfoDto nodeLogInfoDto = new NodeLogInfoDto();
                BeanUtils.copyProperties(nodeLogInfoEntity, nodeLogInfoDto);
                return nodeLogInfoDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(resultList, nodeLogInfoEntities.getNumber() + 1, nodeLogInfoEntities.getSize(), (int) nodeLogInfoEntities.getTotalElements()));
    }

    /**
     * 清除日志
     *
     * @param nodeId
     * @param removeDate
     * @param removeType
     * @param queryType
     * @return
     */
    @Override
    public ResponseResult<String> removeNodeLogInfo(String nodeId, String removeDate, Integer removeType, Integer queryType) {
        if (removeType == 1) {//清除本次查询
            String dayStart = getDayStart(removeDate);
            String dayEnd = getDayEnd(removeDate);
            List<NodeLogInfoEntity> nodeLogInfoEntityList = nodeLogInfoDao.findAllByNodeIdAndTsTimeBetween(nodeId, dayStart, dayEnd);
            if (CollectionUtils.isNotEmpty(nodeLogInfoEntityList)) {
                if (StringUtil.isNotEmpty(queryType)) {
                    nodeLogInfoEntityList = nodeLogInfoEntityList.stream().filter(n -> n.getLogType().equals(queryType)).collect(Collectors.toList());
                }
                nodeLogInfoDao.deleteAll(nodeLogInfoEntityList);
                return ResponseResult.ok(ResponseResult.SUCCESS);
            } else {//清除全部
                nodeLogInfoDao.deleteAllByNodeId(nodeId);
                return ResponseResult.ok(ResponseResult.SUCCESS);
            }
        }
        return ResponseResult.error(ResponseResult.PARAM_ERROR);
    }
}
