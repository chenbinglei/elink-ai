package com.sunmax.device.service.impl;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.dao.access.GraphDao;
import com.sunmax.device.dao.access.GraphTypeDao;
import com.sunmax.device.dto.GraphListDto;
import com.sunmax.device.dto.GraphTypeListDto;
import com.sunmax.device.entity.access.GraphEntity;
import com.sunmax.device.entity.access.GraphTypeEntity;
import com.sunmax.device.service.VisualService;
import com.sunmax.device.vo.GraphChangeVo;
import com.sunmax.device.vo.GraphTypeChangeVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VisualServiceImpl implements VisualService {

    @Resource
    private GraphTypeDao graphTypeDao;

    @Resource
    private GraphDao graphDao;

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveGraphType(GraphTypeChangeVo graphTypeVo) {
        //根据图形编码查询图形类型数据
        List<GraphTypeEntity> graphTypeList = graphTypeDao.findAll(Example.of(GraphTypeEntity.builder().code(graphTypeVo.getCode()).build()));
        //根据图形分类id和名称查询图形分类数据
        if (StringUtil.isEmpty(graphTypeVo.getId())) {
            //校验图形分类编码
            if (CollectionUtils.isNotEmpty(graphTypeList)) {
                return ResponseResult.paramShow(graphTypeVo.getCode(), ResponseResult.PARAM_EXIST);
            }
            GraphTypeEntity graphType = new GraphTypeEntity();
            BeanUtils.copyProperties(graphTypeVo, graphType);
            graphTypeDao.save(graphType);
            return ResponseResult.ok();
        } else {
            //校验图形分类编码
            graphTypeList = graphTypeList.stream().filter(graphType -> !graphType.getId().equals(graphTypeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(graphTypeList)) {
                return ResponseResult.paramShow(graphTypeVo.getCode(), ResponseResult.PARAM_EXIST);
            }
            Optional<GraphTypeEntity> optional = graphTypeDao.findById(graphTypeVo.getId());
            if (optional.isPresent()) {
                GraphTypeEntity graphType = optional.get();
                BeanUtils.copyProperties(graphTypeVo, graphType);
                graphTypeDao.save(graphType);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<GraphTypeListDto>> findGraphTypeListByTypeId(String typeId) {
        //返回的集合
        List<GraphTypeListDto> resultList = Lists.newArrayList();
        //根据资产分类id查询图形分类数据
        List<GraphTypeEntity> graphTypeList = graphTypeDao.findAll(Example.of(GraphTypeEntity.builder().typeId(typeId).build()));
        if (CollectionUtils.isNotEmpty(graphTypeList)) {
            resultList = graphTypeList.stream().map(graphType -> {
                GraphTypeListDto result = new GraphTypeListDto();
                BeanUtils.copyProperties(graphType, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> deleteGraphTypeById(String id) {
        graphTypeDao.deleteById(id);
        return ResponseResult.ok();
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveGraph(GraphChangeVo graphVo) {
        if (StringUtil.isEmpty(graphVo.getDeviceId())) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //修改图模时 判断默认状态是否更新
        if (StringUtil.isNotEmpty(graphVo.getIsDefault()) && graphVo.getIsDefault() == 1) {
            //根据设备id查询图模默认数据
            List<GraphEntity> graphList = graphDao.findAll(Example.of(GraphEntity.builder().deviceId(graphVo.getDeviceId()).isDefault(1).build()));
            if (CollectionUtils.isNotEmpty(graphList)) {
                //把默认的图形改为不默认
                graphDao.saveAll(graphList.stream().peek(g -> g.setIsDefault(2)).collect(Collectors.toList()));
            }
        }
        //根据图形分类id和名称查询图形分类数据
        if (StringUtil.isEmpty(graphVo.getId())) {
            GraphEntity graph = new GraphEntity();
            BeanUtils.copyProperties(graphVo, graph);
            graphDao.save(graph);
            return ResponseResult.ok();
        } else {
            Optional<GraphEntity> optional = graphDao.findById(graphVo.getId());
            if (optional.isPresent()) {
                GraphEntity graph = optional.get();
                BeanUtils.copyProperties(graphVo, graph);
                graphDao.save(graph);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<PageDto<GraphListDto>> findGraphListByDeviceId(String deviceId, Integer page, Integer size) {
        //返回的集合
        List<GraphListDto> resultList = Lists.newArrayList();
        //根据资产分类id查询图形数据
        List<GraphEntity> graphList = graphDao.findAll(Example.of(GraphEntity.builder().deviceId(deviceId).build()));
        if (CollectionUtils.isNotEmpty(graphList)) {
            //根据多个图形id查询图形数据
            Set<String> graphTypeIds = graphList.stream().map(GraphEntity::getGraphTypeId).collect(Collectors.toSet());
            Map<String, String> graphTypeMap = graphTypeDao.findAllById(graphTypeIds).stream().collect(Collectors
                    .toMap(GraphTypeEntity::getId, GraphTypeEntity::getName, (k1, k2) -> k1));
            resultList = graphList.stream().map(graph -> {
                GraphListDto result = new GraphListDto();
                BeanUtils.copyProperties(graph, result);
                //获取图形分类名称
                if (graphTypeMap.containsKey(graph.getGraphTypeId())) {
                    result.setGraphTypeName(graphTypeMap.get(graph.getGraphTypeId()));
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(resultList, page, size));
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> deleteGraphById(String id) {
        graphDao.deleteById(id);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<List<GraphListDto>> findGraphRelevancyListByDeviceId(String deviceId, String graphTypeCode) {
        //返回的集合
        List<GraphListDto> resultList = Lists.newArrayList();
        //根据资产分类id查询图形数据
        List<GraphEntity> graphList = graphDao.findAll(Example.of(GraphEntity.builder().deviceId(deviceId).build()));
        if (CollectionUtils.isNotEmpty(graphList)) {
            //根据多个图形id查询图形数据
            Set<String> graphTypeIds = graphList.stream().map(GraphEntity::getGraphTypeId).collect(Collectors.toSet());
            List<GraphTypeEntity> graphTypeEntityList = graphTypeDao.findAllById(graphTypeIds);
            //如果传了图形分类标识，从图形分类中获取到分类id，再过滤出指定分类下的图形
            if (StringUtil.isNotEmpty(graphTypeCode)) {
                graphTypeEntityList = graphTypeEntityList.stream().filter(graphType -> graphType.getCode().equals(graphTypeCode)).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(graphTypeEntityList)) {
                    return ResponseResult.ok(resultList, "未查询到该分类标识数据！");
                }
                String typeId = graphTypeEntityList.get(0).getId();
                graphList = graphList.stream().filter(g -> g.getGraphTypeId().equals(typeId)).collect(Collectors.toList());
            }
            Map<String, String> graphTypeMap = graphTypeEntityList.stream().collect(Collectors
                    .toMap(GraphTypeEntity::getId, GraphTypeEntity::getName, (k1, k2) -> k1));
            resultList = graphList.stream().map(graph -> {
                GraphListDto result = new GraphListDto();
                BeanUtils.copyProperties(graph, result);
                //获取图形分类名称
                if (graphTypeMap.containsKey(graph.getGraphTypeId())) {
                    result.setGraphTypeName(graphTypeMap.get(graph.getGraphTypeId()));
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

}
