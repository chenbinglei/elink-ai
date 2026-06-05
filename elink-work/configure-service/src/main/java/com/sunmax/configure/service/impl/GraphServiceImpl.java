package com.sunmax.configure.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.configure.DeviceVariableDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.entity.BaseEntity;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.local.LocalFileUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.util.oss.OssFileUtil;
import com.sunmax.common.vo.LocalParamVo;
import com.sunmax.configure.dao.DataSourceDao;
import com.sunmax.configure.dao.GraphDao;
import com.sunmax.configure.dao.GraphSourceDao;
import com.sunmax.configure.dao.VariableDao;
import com.sunmax.configure.dto.*;
import com.sunmax.configure.entity.DataSourceEntity;
import com.sunmax.configure.entity.GraphEntity;
import com.sunmax.configure.entity.GraphSourceEntity;
import com.sunmax.configure.entity.VariableEntity;
import com.sunmax.configure.service.GraphService;
import com.sunmax.configure.service.feign.DeviceService;
import com.sunmax.configure.util.DataHandleUtil;
import com.sunmax.configure.util.HttpUtil;
import com.sunmax.configure.util.WebSocketClientUtil;
import com.sunmax.configure.vo.GraphChangeVo;
import com.sunmax.configure.vo.GraphSourceChangeVo;
import com.sunmax.configure.vo.GraphVariableChangeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GraphServiceImpl implements GraphService {

    @Resource
    private GraphDao graphDao;

    @Resource
    private GraphSourceDao graphSourceDao;

    @Resource
    private DataSourceDao dataSourceDao;

    @Resource
    private VariableDao variableDao;

    @Autowired
    private DeviceService deviceService;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveGraph(GraphChangeVo graphChangeVo, MultipartFile file) {
        //新增图模或文件夹
        if (graphChangeVo.getUpdateType() == 1 && StringUtil.isEmpty(graphChangeVo.getId())) {
            GraphEntity graphEntity = new GraphEntity();
            BeanUtils.copyProperties(graphChangeVo, graphEntity);
            if (file != null && !file.isEmpty()) { //文件
                graphEntity.setFilePath(FileUtil.getFilePath(file, null));
            }
            graphEntity.setCreateId(graphChangeVo.getUserId());
            graphEntity.setUpdateId(graphChangeVo.getUserId());
            graphEntity.setStatus(0);
            graphEntity.setLockStatus(0);
            return ResponseResult.ok(graphDao.save(graphEntity).getId());
        } else {
            Optional<GraphEntity> optional = graphDao.findById(graphChangeVo.getId());
            if (optional.isPresent()) {
                GraphEntity graphEntity = optional.get();
                //编辑类型 1-新增 2-移动 3-重命名 4-锁定状态 5-保存 6-发布 7-关联站点
                switch (graphChangeVo.getUpdateType()) {
                    case 2:
                        graphEntity.setParentId(graphChangeVo.getParentId());
                        break;
                    case 3:
                        graphEntity.setName(graphChangeVo.getName());
                        break;
                    case 4:
                        graphEntity.setLockStatus(graphChangeVo.getLockStatus());
                        break;
                    case 5:
                        if (StringUtil.isNotEmpty(graphChangeVo.getName())) {
                            graphEntity.setName(graphChangeVo.getName());
                        }
                        if (file != null && !file.isEmpty()) {
                            graphEntity.setFilePath(FileUtil.getFilePath(file, graphEntity.getFilePath()));
                            graphEntity.setDeviceVariables(graphChangeVo.getDeviceVariables()); //设备变量数据
                        }
                        graphEntity.setStatus(1);
                        break;
                    case 6:
                        if (StringUtil.isNotEmpty(graphChangeVo.getName())) {
                            graphEntity.setName(graphChangeVo.getName());
                        }
                        List<GraphEntity> graphList = graphDao.findAll(Example.of(GraphEntity.builder().domainId(graphChangeVo.getDomainId()).build()))
                                .stream().filter(g -> !Objects.equals(g.getId(), graphChangeVo.getId())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(graphList)) {
                            return ResponseResult.paramShow(graphChangeVo.getDomainId(), ResponseResult.PARAM_EXIST);
                        }
                        graphEntity.setDomainId(graphChangeVo.getDomainId());
                        if (file != null && !file.isEmpty()) {
                            if (LocalParamVo.FILE_TYPE) { //文件类型
                                //校验保存文件路径和发布路径是否一致
                                if (StringUtil.isNotEmpty(graphEntity.getFilePath()) && StringUtil.isNotEmpty(graphEntity.getPublicFilePath())) {
                                    if (Objects.equals(graphEntity.getFilePath(), graphEntity.getPublicFilePath())) {
                                        OssFileUtil.deleteFile(FileUtil.subString(graphEntity.getFilePath(), FileUtil.SLASH, FileUtil.QUESTION));
                                    } else {
                                        OssFileUtil.deleteFile(FileUtil.subString(graphEntity.getFilePath(), FileUtil.SLASH, FileUtil.QUESTION));
                                        OssFileUtil.deleteFile(FileUtil.subString(graphEntity.getPublicFilePath(), FileUtil.SLASH, FileUtil.QUESTION));
                                    }
                                } else {
                                    if (StringUtil.isNotEmpty(graphEntity.getFilePath())) { //删除阿里云上面的图形
                                        OssFileUtil.deleteFile(FileUtil.subString(graphEntity.getFilePath(), FileUtil.SLASH, FileUtil.QUESTION));
                                    }
                                    if (StringUtil.isNotEmpty(graphEntity.getPublicFilePath())) { //删除阿里云上面的图形
                                        OssFileUtil.deleteFile(FileUtil.subString(graphEntity.getPublicFilePath(), FileUtil.SLASH, FileUtil.QUESTION));
                                    }
                                }
                                String filePath = OssFileUtil.checkFile(file);
                                graphEntity.setFilePath(filePath);
                                graphEntity.setPublicFilePath(filePath);
                            } else {
                                if (StringUtil.isNotEmpty(graphEntity.getFilePath()) && StringUtil.isNotEmpty(graphEntity.getPublicFilePath())) {
                                    if (Objects.equals(graphEntity.getFilePath(), graphEntity.getPublicFilePath())) {
                                        OssFileUtil.deleteFile(FileUtil.subString(graphEntity.getFilePath(), FileUtil.SLASH, FileUtil.QUESTION));
                                    } else {
                                        LocalFileUtil.deleteFile(graphEntity.getFilePath());
                                        LocalFileUtil.deleteFile(graphEntity.getPublicFilePath());
                                    }
                                } else {
                                    if (StringUtil.isNotEmpty(graphEntity.getFilePath())) { //删除本地服务器上面的图形
                                        LocalFileUtil.deleteFile(graphEntity.getFilePath());
                                    }
                                    if (StringUtil.isNotEmpty(graphEntity.getPublicFilePath())) { //删除本地服务器上面的图形
                                        LocalFileUtil.deleteFile(graphEntity.getPublicFilePath());
                                    }
                                }
                                String filePath = LocalFileUtil.checkFile(file);
                                graphEntity.setFilePath(filePath);
                                graphEntity.setPublicFilePath(filePath);
                            }
                            graphEntity.setDeviceVariables(graphChangeVo.getDeviceVariables()); //设备变量数据
                        } else { //发布不上传文件 直接从保存文件路径里面取
                            graphEntity.setPublicFilePath(graphEntity.getFilePath());
                        }
                        graphEntity.setStatus(2);
                        break;
                    case 7:
                        graphEntity.setSiteId(graphChangeVo.getSiteId());
                        break;
                }
                graphEntity.setUpdateId(graphChangeVo.getUserId());
                return ResponseResult.ok(graphDao.save(graphEntity).getId());
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteGraphByIds(List<String> ids) {
        //根据多个图模id查询图形数据
        List<GraphEntity> graphList = graphDao.findAllById(ids);
        if (CollectionUtils.isNotEmpty(graphList)) {
            //根据文件路径删除阿里云上面的文件
            List<String> filePathList = Lists.newArrayList();
            filePathList.addAll(graphList.stream().map(GraphEntity::getFilePath).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
            filePathList.addAll(graphList.stream().map(GraphEntity::getPublicFilePath).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(filePathList)) {
                FileUtil.deleteAllFile(filePathList);
            }
            //删除图形数据
            graphDao.deleteAll(graphList);
            //根据多个图模id查询关联数据源数据
            graphSourceDao.deleteAll(graphSourceDao.findAllByGraphIdIn(ids));
            //根据多个图模id查询变量数据
            variableDao.deleteAll(variableDao.findAllByGraphIdIn(ids));
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<GraphListDto>> queryGraphList(String name) {
        //返回的集合
        List<GraphListDto> resultList = Lists.newArrayList();

        //根据查询条件查询图模数据
        List<GraphEntity> graphList = graphDao.findAll((Specification<GraphEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtil.isNotEmpty(name)) {//关键词
                list.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());
        if (CollectionUtils.isNotEmpty(graphList)) {
            //根据多个站点id查询站点名称
            List<String> siteIds = graphList.stream().map(GraphEntity::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();
            resultList = graphList.stream().map(graph -> {
                GraphListDto result = new GraphListDto();
                BeanUtils.copyProperties(graph, result);
                if (StringUtil.isNotEmpty(graph.getSiteId()) && siteInfoMap.containsKey(graph.getSiteId())) {
                    result.setSiteName(siteInfoMap.get(graph.getSiteId()).getSiteName());
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<GraphDetailDto> findGraphById(String id) {
        if (StringUtil.isEmpty(id)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //返回的对象
        GraphDetailDto result = new GraphDetailDto();
        Optional<GraphEntity> optional = graphDao.findById(id);
        if (optional.isPresent()) {
            GraphEntity graph = optional.get();
            BeanUtils.copyProperties(graph, result);
            if (StringUtil.isNotEmpty(graph.getSiteId())) {
                SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(graph.getSiteId()))
                        .getData().get(graph.getSiteId());
                if (siteInfo != null && StringUtil.isNotEmpty(siteInfo.getSiteName())) {
                    result.setSiteName(siteInfo.getSiteName());
                }
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> relationDataSource(GraphSourceChangeVo graphSourceVo) {
        if (StringUtil.isNotEmpty(graphSourceVo.getUpdateType())) {
            GraphSourceEntity graphSource = new GraphSourceEntity();
            //编辑类型 1-新增 2-编辑 3-删除
            switch (graphSourceVo.getUpdateType()) {
                case 1:
                case 2:
                    //根据名称查询图模资源数据
                    List<GraphSourceEntity> graphSourceList = graphSourceDao.findAll(Example.of(GraphSourceEntity.builder().name(graphSourceVo.getName()).build()));
                    if (StringUtil.isNotEmpty(graphSourceVo.getId())) {
                        graphSourceList = graphSourceList.stream().filter(g -> !g.getId().equals(graphSourceVo.getId())).collect(Collectors.toList());
                    }
                    if (CollectionUtils.isNotEmpty(graphSourceList)) {
                        return ResponseResult.paramShow(graphSourceVo.getName(), ResponseResult.PARAM_EXIST);
                    }
                    BeanUtils.copyProperties(graphSourceVo, graphSource);
                    graphSourceDao.save(graphSource);
                    break;
                case 3:
                    graphSourceDao.deleteById(graphSourceVo.getId());
                    break;
            }
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<String> checkDataSource(String dataSourceId, String requestValue) {
        Optional<DataSourceEntity> optional = dataSourceDao.findById(dataSourceId);
        if (optional.isPresent()) {
            String result = null;
            DataSourceEntity dataSource = optional.get();
            //通信方式 1-websocket 2-http 3-mqtt
            switch (dataSource.getType()) {
                case 1:
                    try {
                        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                        JSON.parseObject(requestValue).forEach((key,value) -> {
                            if (dataSource.getUrl().contains(key)) {
                                String paramKey = "{" + key + "}";
                                String paramValue = StringUtil.isNotEmpty(value) ? String.valueOf(value) : "undefined";
                                dataSource.setUrl(dataSource.getUrl().replace(paramKey, paramValue));
                            }
                        });
                        //设置25MB
                        container.setDefaultMaxTextMessageBufferSize(26214400);
                        container.setDefaultMaxBinaryMessageBufferSize(26214400);
                        container.connectToServer(WebSocketClientUtil.class, new URI(dataSource.getUrl()));
                        do {
                            Thread.sleep(1000);
                        } while (WebSocketClientUtil.data == null);
                        result = WebSocketClientUtil.data;
                        log.info("websocket返回数据字节长度: {}", result.length());
                        WebSocketClientUtil.data = null;
                    } catch (Exception e) {
                        log.error("测试websocket接口报错", e);
                        return ResponseResult.paramError("测试websocket接口错误");
                    }
                case 2:
                    try {
                        JSONObject dynamicField = JSON.parseObject(dataSource.getDynamicField());
//                        JSONObject requestHeader = dynamicField.getJSONObject("requestHeader");
                        String requestMethod = dynamicField.getString("requestMethod");
//                        String requestInterval = dynamicField.getString("requestInterval");
                        if (Objects.equals(requestMethod, "GET")) {
                            result = DataHandleUtil.getResponseData(HttpUtil.sendGet(dataSource.getUrl() + HttpUtil.jsonObjectToString(JSON.parseObject(requestValue))));
                        }
                        if (Objects.equals(requestMethod, "POST")) {
                            result = DataHandleUtil.getResponseData(HttpUtil.sendPost(dataSource.getUrl(), requestValue));
                        }
                    } catch (Exception e) {
                        log.error("测试http接口错误", e);
                        return ResponseResult.paramError("测试http接口错误");
                    }

                    break;
                case 3:
                    break;
            }
            if (StringUtil.isEmpty(result)) {
                return ResponseResult.paramError("测试接口响应数据为空,请重新测试");
            }
            if (StringUtil.isNotEmpty(result)) {
                return ResponseResult.ok(result);
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<GraphSourceDto>> findGraphSourceByGraphId(String graphId, String name) {
        //返回的集合
        List<GraphSourceDto> resultList = Lists.newArrayList();
        //根据图模id查询图模关联数据源数据
        List<GraphSourceEntity> graphSourceList = graphSourceDao.findAll(Example.of(GraphSourceEntity.builder().graphId(graphId).build()));
        if (CollectionUtils.isNotEmpty(graphSourceList)) {
            if (StringUtil.isNotEmpty(name)) { //名称搜索查询
                graphSourceList = graphSourceList.stream().filter(g -> g.getName().contains(name)).collect(Collectors.toList());
            }
            Set<String> dataSourceIds = graphSourceList.stream().map(GraphSourceEntity::getDataSourceId).collect(Collectors.toSet());
            Map<String, DataSourceEntity> dataSourceMap = dataSourceDao.findAllById(dataSourceIds).stream().collect(Collectors
                    .toMap(BaseEntity::getId, a -> a, (k1, k2) -> k1));
            resultList = graphSourceList.stream().map(g -> {
                GraphSourceDto result = new GraphSourceDto();
                BeanUtils.copyProperties(g, result);
                DataSourceEntity dataSource = dataSourceMap.get(g.getDataSourceId());
                if (dataSource != null) {
                    result.setDataSourceName(dataSource.getName());
                    result.setType(dataSource.getType());
                    result.setUrl(dataSource.getUrl());
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveGraphVariable(GraphVariableChangeVo graphVariableVo) {
        //根据名称查询图模变量数据
        List<VariableEntity> variableList = variableDao.findAll(Example.of(VariableEntity.builder().graphId(graphVariableVo.getGraphId())
                .name(graphVariableVo.getName()).build()));
        //新增图模变量数据
        if (StringUtil.isEmpty(graphVariableVo.getId())) {
            if (CollectionUtils.isNotEmpty(variableList)) {
                return ResponseResult.paramShow(graphVariableVo.getName(), ResponseResult.PARAM_EXIST);
            }
            VariableEntity variable = new VariableEntity();
            BeanUtils.copyProperties(graphVariableVo, variable);
            variableDao.save(variable);
            return ResponseResult.ok();
        } else { //编辑图模变量数据
            variableList = variableList.stream().filter(v -> !Objects.equals(v.getId(), graphVariableVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(variableList)) {
                return ResponseResult.paramShow(graphVariableVo.getName(), ResponseResult.PARAM_EXIST);
            }
            Optional<VariableEntity> optional = variableDao.findById(graphVariableVo.getId());
            if (optional.isPresent()) {
                VariableEntity variable = optional.get();
                BeanUtils.copyProperties(graphVariableVo, variable);
                variableDao.save(variable);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveAllGraphVariable(List<GraphVariableChangeVo> graphVariableChangeVos) {
        if (CollectionUtils.isNotEmpty(graphVariableChangeVos)) {
//            String graphId = graphVariableChangeVos.get(0).getGraphId();
//            variableDao.deleteAllByGraphId(graphId);
            variableDao.batchUpdate(graphVariableChangeVos.stream().map(variable -> {
                VariableEntity variableEntity = new VariableEntity();
                BeanUtils.copyProperties(variable, variableEntity);
                return variableEntity;
            }).collect(Collectors.toList()));
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteGraphVariableByIds(List<String> ids) {
        //根据多个id查询图模变量数据
        List<VariableEntity> variableList = variableDao.findAllById(ids);
        if (CollectionUtils.isNotEmpty(variableList)) {
            variableDao.deleteAll(variableList);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<GraphVariableListDto>> findGraphVariableByGraphId(String graphId, String keyword) {
        List<GraphVariableListDto> resultList = Lists.newArrayList();
        List<VariableEntity> variableList = variableDao.findAll(Example.of(VariableEntity.builder().graphId(graphId).build()));
        if (CollectionUtils.isNotEmpty(variableList)) {
            //根据多个图模数据源id查询图模数据源数据
            Set<String> graphSourceIds = variableList.stream().map(VariableEntity::getGraphSourceId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
            Map<String, String> graphSourceMap = graphSourceDao.findAllById(graphSourceIds).stream().collect(Collectors
                    .toMap(GraphSourceEntity::getId, GraphSourceEntity::getName));
            resultList = variableList.stream().map(v -> {
                GraphVariableListDto result = new GraphVariableListDto();
                BeanUtils.copyProperties(v, result);
                if (StringUtil.isNotEmpty(v.getGraphSourceId())) {
                    result.setGraphSourceName(graphSourceMap.get(v.getGraphSourceId()));
                }
                return result;
            }).collect(Collectors.toList());
            if (StringUtil.isNotEmpty(keyword)) {
                resultList = resultList.stream().filter(v -> (StringUtil.isNotEmpty(v.getName()) && v.getName().contains(keyword)) ||
                        (StringUtil.isNotEmpty(v.getGraphSourceName()) && v.getGraphSourceName().contains(keyword))).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<GraphDataDto> findGraphDataListByGraphId(String id, Integer type) {
        //返回的对象
        GraphDataDto result = new GraphDataDto();
        //根据图模id查询图模关联数据源数据
        List<GraphSourceEntity> graphSourceList = Lists.newArrayList();
        Optional<GraphEntity> optional = Optional.empty();
        if (type == 1) {
            optional = graphDao.findById(id);
        }
        if (type == 2) {
            optional = graphDao.findOne(Example.of(GraphEntity.builder().domainId(id).build()));
        }
        if (optional.isPresent()) {
            GraphEntity graph = optional.get();
            if (StringUtil.isNotEmpty(graph.getFilePath())) { //绑定数据解析
                result.setPublicFilePath(graph.getPublicFilePath());
            }
            graphSourceList = graphSourceDao.findAllByGraphIdIn(Collections.singletonList(graph.getId()));
        }
        if (CollectionUtils.isNotEmpty(graphSourceList)) {
            //根据多个数据源id查询数据源数据
            Set<String> dataSourceIds = graphSourceList.stream().map(GraphSourceEntity::getDataSourceId).collect(Collectors.toSet());
            Map<String, DataSourceEntity> dataSourceMap = dataSourceDao.findAllById(dataSourceIds).stream().collect(Collectors
                    .toMap(BaseEntity::getId, a -> a, (k1, k2) -> k1));
            //根据多个关联数据源id查询关联变量数据
            Set<String> graphSourceIds = graphSourceList.stream().map(GraphSourceEntity::getId).collect(Collectors.toSet());
            Map<String, List<VariableEntity>> variableMap = variableDao.findAllByGraphSourceIdIn(graphSourceIds).stream()
                    .collect(Collectors.groupingBy(VariableEntity::getGraphSourceId));

            //对数据进行组装
            result.setDataSourceList(graphSourceList.stream().map(graphSource -> {
                GraphDataDto.DataSource dataSource = new GraphDataDto.DataSource();
                dataSource.setRequestValue(graphSource.getRequestValue());
                dataSource.setResponseValue(graphSource.getResponseValue());
                if (dataSourceMap.containsKey(graphSource.getDataSourceId())) {
                    DataSourceEntity dataSourceEntity = dataSourceMap.get(graphSource.getDataSourceId());
                    dataSource.setType(dataSourceEntity.getType());
                    dataSource.setUrl(dataSourceEntity.getUrl());
                    dataSource.setDynamicField(dataSourceEntity.getDynamicField());
                    dataSource.setRequestKey(dataSourceEntity.getRequestKey());
                }
                if (variableMap.containsKey(graphSource.getId())) {
                    dataSource.setVariableList(variableMap.get(graphSource.getId()).stream().map(v -> {
                        GraphDataDto.Variable variable = new GraphDataDto.Variable();
                        BeanUtils.copyProperties(v, variable);
                        return variable;
                    }).collect(Collectors.toList()));
                }
                return dataSource;
            }).collect(Collectors.toList()));
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> cloneGraph(String id, String userId, MultipartFile file) {
        //根据id查询图模数据
        Optional<GraphEntity> optional = graphDao.findById(id);
        if (optional.isPresent()) {
            GraphEntity graphEntity = new GraphEntity();
            graphEntity.setName(optional.get().getName());
            if (file != null && !file.isEmpty()) { //文件
                graphEntity.setFilePath(FileUtil.getFilePath(file, null));
            }
            graphEntity.setType(optional.get().getType());
            graphEntity.setCreateId(userId);
            graphEntity.setUpdateId(userId);
            graphEntity.setStatus(0);
            graphEntity.setLockStatus(0);
            return ResponseResult.ok(graphDao.save(graphEntity).getId());
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<DeviceVariableDto> findAllVariableByDomainId(String domainId) {
        DeviceVariableDto result = new DeviceVariableDto();
        //根据域名id查询图模数据
        List<GraphEntity> graphList = graphDao.findAllByDomainId(domainId);
        if (CollectionUtils.isNotEmpty(graphList)) {
            GraphEntity graph = graphList.get(0);
            if (StringUtil.isNotEmpty(graph.getSiteId()) && StringUtil.isNotEmpty(graph.getDeviceVariables())) {
                result.setSiteId(graph.getSiteId());
                Map<String, DeviceVariableDto.VariableData> variableDataMap = Maps.newHashMap();
                JSON.parseObject(graph.getDeviceVariables()).forEach((deviceId, value) -> {
                    JSONObject dataMap = JSON.parseObject(JSON.toJSONString(value));
                    DeviceVariableDto.VariableData variableData = new DeviceVariableDto.VariableData();
                    if (dataMap.containsKey("nodeList")) {
                        variableData.setNodeList(dataMap.getJSONArray("nodeList").stream().filter(StringUtil::isNotEmpty).map(s -> (String) s).collect(Collectors.toSet()));
                    }
                    if (dataMap.containsKey("functionList")) {
                        variableData.setFunctionList(dataMap.getJSONArray("functionList").stream().filter(StringUtil::isNotEmpty).map(s -> (String) s).collect(Collectors.toSet()));
                    }
                    variableDataMap.put(deviceId, variableData);
                });
                result.setVariableDataMap(variableDataMap);
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> importGraph(String userId, MultipartFile file) {
        GraphEntity graphEntity = new GraphEntity();
        graphEntity.setName("新建图纸");
        graphEntity.setType(2);
        if (file != null && !file.isEmpty()) { //文件
            graphEntity.setFilePath(FileUtil.getFilePath(file, null));
        }
        graphEntity.setCreateId(userId);
        graphEntity.setUpdateId(userId);
        graphEntity.setStatus(1);
        graphEntity.setLockStatus(0);
        graphDao.save(graphEntity);
        return ResponseResult.ok();
    }

    public static void main(String[] args) throws IOException {
        String requestValue = "{\"deviceIds\":\"ff8080818ef3fa91018ef51cc8b90005\",\"varCodes\":\"gunpower,gunoutputvoltage,gunoutputcurrent\"}";
        String responseData = DataHandleUtil.getResponseData(HttpUtil.sendPost("http://192.168.2.251:60006/scrontab/configFuncPoint/findSystemVarNewValue", requestValue));
        System.out.println(responseData);
    }

}
