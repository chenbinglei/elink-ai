package com.sunmax.together.service.energy.impl;

import java.io.IOException;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.protocol.GateWayPolicyDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.TimestampUtil;
import com.sunmax.common.util.local.LocalFileUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.util.oss.OssFileUtil;
import com.sunmax.common.vo.LocalParamVo;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import com.sunmax.common.vo.protocol.GateWayPolicyVo;
import com.sunmax.together.dao.strategy.StrategyDao;
import com.sunmax.together.dao.strategy.TemplateDao;
import com.sunmax.together.dto.energy.GatewayDataDto;
import com.sunmax.together.dto.energy.StrategyDataDto;
import com.sunmax.together.dto.energy.StrategyListDto;
import com.sunmax.together.service.feign.CrontabService;
import com.sunmax.together.service.feign.ProtocolService;
import com.sunmax.together.vo.energy.StrategySaveVo;
import com.sunmax.together.vo.energy.StrategyUpdateVo;
import com.sunmax.together.dto.energy.TemplateListDto;
import com.sunmax.together.entity.strategy.StrategyEntity;
import com.sunmax.together.entity.strategy.TemplateEntity;
import com.sunmax.together.service.energy.StrategyService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.vo.energy.TemplateChangeVo;
import com.sunmax.together.vo.energy.TemplateQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StrategyServiceImpl implements StrategyService {

    @Autowired
    private TemplateDao templateDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private StrategyDao strategyDao;

    @Autowired
    private ProtocolService protocolService;

    @Autowired
    private CrontabService crontabService;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveOrUpdateTemplate(TemplateChangeVo templateChangeVo, MultipartFile explainFile, MultipartFile configFile) {
        //根据模板名称查询策略模板数据
        List<TemplateEntity> templateList = templateDao.findAll(Example.of(TemplateEntity.builder().templateName(templateChangeVo.getTemplateName()).isDelete(1).build()));
        //新增策略模板数据
        if (StringUtil.isEmpty(templateChangeVo.getId())) {
            //校验模板名称是否存在
            if (CollectionUtils.isNotEmpty(templateList)) {
                return ResponseResult.paramShow(templateChangeVo.getTemplateName(), ResponseResult.PARAM_EXIST);
            }
            TemplateEntity template = new TemplateEntity();
            BeanUtils.copyProperties(templateChangeVo, template);
            //策略说明新文件上传
            if (explainFile != null && !explainFile.isEmpty()) {
                template.setExplainName(explainFile.getOriginalFilename());
                template.setExplainPath(FileUtil.getFilePath(explainFile, null));
            }
            //策略配置文件新文件上传
            if (configFile != null && !configFile.isEmpty()) {
                template.setConfigPath(FileUtil.getFilePath(configFile, null));
                template.setConfigName(configFile.getOriginalFilename());
            }
            template.setCreateId(templateChangeVo.getUserId());
            template.setUpdateId(templateChangeVo.getUserId());
            template.setIsDelete(1);
            templateDao.save(template);
            return ResponseResult.ok();
        } else {
            //校验模板名称是否存在
            templateList = templateList.stream().filter(t -> !Objects.equals(t.getId(), templateChangeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(templateList)) {
                return ResponseResult.paramShow(templateChangeVo.getTemplateName(), ResponseResult.PARAM_EXIST);
            }
            //编辑策略模板数据
            Optional<TemplateEntity> optional = templateDao.findById(templateChangeVo.getId());
            if (optional.isPresent()) {
                TemplateEntity template = new TemplateEntity();
                BeanUtils.copyProperties(templateChangeVo, template);
                if (StringUtil.isNotEmpty(optional.get().getExplainName())) {
                    template.setExplainName(optional.get().getExplainName());
                }
                if (StringUtil.isNotEmpty(optional.get().getExplainPath())) {
                    template.setExplainPath(optional.get().getExplainPath());
                }
                if (StringUtil.isNotEmpty(optional.get().getConfigName())) {
                    template.setConfigName(optional.get().getConfigName());
                }
                if (StringUtil.isNotEmpty(optional.get().getConfigPath())) {
                    template.setConfigPath(optional.get().getConfigPath());
                }
                //策略说明新文件上传
                if (explainFile != null && !explainFile.isEmpty()) {
                    template.setExplainName(explainFile.getOriginalFilename());
                    template.setExplainPath(FileUtil.getFilePath(explainFile, optional.get().getExplainPath()));
                }
                //策略配置文件新文件上传
                if (configFile != null && !configFile.isEmpty()) {
                    template.setConfigPath(FileUtil.getFilePath(configFile, optional.get().getConfigPath()));
                    template.setConfigName(configFile.getOriginalFilename());
                }
                if (StringUtil.isNotEmpty(optional.get().getCreateId())) {
                    template.setCreateId(optional.get().getCreateId());
                }
                if (StringUtil.isNotEmpty(optional.get().getCreateTime())) {
                    template.setCreateTime(optional.get().getCreateTime());
                }
                template.setIsDelete(1);
                template.setUpdateId(templateChangeVo.getUserId());
                templateDao.save(template);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<PageDto<TemplateListDto>> queryTemplateList(TemplateQueryVo templateQueryVo) {
        //返回的集合
        List<TemplateListDto> resultList = Lists.newArrayList();

        List<TemplateEntity> templateList = templateDao.findAll((Specification<TemplateEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("isDelete"), 1));
            if (StringUtil.isNotEmpty(templateQueryVo.getTemplateName())) { //模板名称
                list.add(cb.like(root.get("templateName"), "%" + templateQueryVo.getTemplateName() + "%"));
            }
            if (StringUtil.isNotEmpty(templateQueryVo.getStrategyType())) { //策略类型
                list.add(cb.equal(root.get("strategyType"), templateQueryVo.getStrategyType()));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());
        if (!templateList.isEmpty()) {
            //根据多个用户id查询用户名称
            List<String> userIds = Lists.newArrayList();
            templateList.forEach(b -> {
                userIds.add(b.getCreateId());
                userIds.add(b.getUpdateId());
            });
            Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(userIds.stream().distinct().collect(Collectors.toList())).getData();
            resultList = templateList.stream().map(t -> {
                TemplateListDto result = new TemplateListDto();
                BeanUtils.copyProperties(t, result);
                //获取创建人名称和编辑人名称
                if (userMap.containsKey(t.getCreateId())) {
                    result.setCreateName(userMap.get(t.getCreateId()).getFullName());
                }
                if (userMap.containsKey(t.getUpdateId())) {
                    result.setUpdateName(userMap.get(t.getUpdateId()).getFullName());
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(resultList, templateQueryVo.getPage(), templateQueryVo.getSize()));
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteTemplateById(String id) {
        //根据多个id查询策略模板数据
        Optional<TemplateEntity> optional = templateDao.findById(id);
        if (optional.isPresent()) {
            TemplateEntity template = optional.get();
            //删除说明文件和配置文件
            List<String> filePaths = Lists.newArrayList();
            if (StringUtil.isNotEmpty(template.getExplainPath())) {
                filePaths.add(template.getExplainPath());
            }
            if (StringUtil.isNotEmpty(template.getConfigPath())) {
                filePaths.add(template.getConfigPath());
            }
            if (!filePaths.isEmpty()) {
                if (LocalParamVo.FILE_TYPE) {
                    OssFileUtil.deleteAllFile(FileUtil.subList(filePaths, FileUtil.SLASH, FileUtil.QUESTION));
                } else {
                    LocalFileUtil.deleteAllFile(filePaths);
                }
            }
            template.setIsDelete(2);
            templateDao.save(template);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<String> parseTemplateContent(String id, Integer type) {
        Optional<TemplateEntity> optional = templateDao.findById(id);
        if (optional.isPresent() && optional.get().getIsDelete() != 2) {
            String explainPath = optional.get().getExplainPath();
            if (type == 1 && StringUtil.isNotEmpty(explainPath)) {
                return ResponseResult.ok(FileUtil.readFile(explainPath));
            }
            String configPath = optional.get().getConfigPath();
            if (type == 2 && StringUtil.isNotEmpty(configPath)) {
                return ResponseResult.ok(FileUtil.readFile(configPath));
            }
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<TemplateListDto> findTemplateById(String id) {
        //返回的数据
        TemplateListDto result = new TemplateListDto();

        Optional<TemplateEntity> optional = templateDao.findById(id);
        if (optional.isPresent()) {
            TemplateEntity templateEntity = optional.get();
            //根据多个用户id查询用户名称
            List<String> userIds = Lists.newArrayList();
            userIds.add(templateEntity.getCreateId());
            userIds.add(templateEntity.getUpdateId());
            Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(userIds.stream().distinct().collect(Collectors.toList())).getData();
            BeanUtils.copyProperties(templateEntity, result);
            //获取创建人名称和编辑人名称
            if (userMap.containsKey(templateEntity.getCreateId())) {
                result.setCreateName(userMap.get(templateEntity.getCreateId()).getFullName());
            }
            if (userMap.containsKey(templateEntity.getUpdateId())) {
                result.setUpdateName(userMap.get(templateEntity.getUpdateId()).getFullName());
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<GatewayDataDto>> findGatewayDataBySiteId(String siteId) {
        //返回的集合
        List<GatewayDataDto> resultList = Lists.newArrayList();

        //根据站点id查询网关设备信息
        List<DeviceBasicInfoDto> gatewayList = deviceService.findDeviceBasicInfoBySiteIds(Lists.newArrayList(siteId), null)
                .getData().values().stream().flatMap(Collection::stream).collect(Collectors.toList())
                .stream().filter(d -> Objects.equals(2, d.getAccessType())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(gatewayList)) {
            resultList = gatewayList.stream().filter(g -> Objects.equals(g.getTypeId(), "31")
                    || Objects.equals(g.getTypeId(), "32")).map(gateway -> {
                GatewayDataDto result = new GatewayDataDto();
                BeanUtils.copyProperties(gateway, result);
                if (Objects.equals(gateway.getTypeId(), "31")) {
                    result.setStrategyType(1);
                }
                if (Objects.equals(gateway.getTypeId(), "32")) {
                    result.setStrategyType(2);
                }
                return result;
            }).collect(Collectors.toList());
        }
        resultList.add(GatewayDataDto.builder().deviceName("云平台").deviceNumber("ypt").strategyType(3).build());
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveStrategy(StrategySaveVo strategySaveVo) {
        StrategyEntity strategy = new StrategyEntity();
        BeanUtils.copyProperties(strategySaveVo, strategy);
        strategy.setExecuteStatus(0);
        strategy.setUpdateId(strategySaveVo.getUserId());
        strategy.setPolicyId((long) TimestampUtil.getUniqueTimestamp());
        strategyDao.save(strategy);
        return ResponseResult.ok();
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> updateStrategy(StrategyUpdateVo strategyUpdateVo, MultipartFile configFile) {
        //1.解析配置文件内容
        String configContent = null;
        if (configFile != null && !configFile.isEmpty()) {
            configContent = this.parseFileContent(configFile);
        }
        if (StringUtil.isEmpty(configContent)) {
            return ResponseResult.paramError(ResponseResult.PARAM_PARSE_ERROR);
        }
        Optional<StrategyEntity> optional = strategyDao.findById(strategyUpdateVo.getId());
        if (optional.isPresent()) {
            StrategyEntity strategy = optional.get();
            if (StringUtil.isNotEmpty(strategyUpdateVo.getStrategyName())) {
                strategy.setStrategyName(strategyUpdateVo.getStrategyName());
            }
            strategy.setConfigContent(configContent);
            strategy.setUpdateId(strategyUpdateVo.getUserId());
            strategyDao.save(strategy);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<StrategyListDto>> queryStrategyList(String siteId, String deviceId) {
        //返回的集合
        List<StrategyListDto> resultList = Lists.newArrayList();

        //根据站点id和设备id查询策略列表数据
        List<StrategyEntity> strategyList;
        if (StringUtil.isNotEmpty(deviceId)) {
            strategyList = strategyDao.findAll(Example.of(StrategyEntity.builder().siteId(siteId).deviceId(deviceId).build()));
        } else {
            strategyList = strategyDao.findAll(Example.of(StrategyEntity.builder().siteId(siteId).strategyType(3).build()));
        }
        if (CollectionUtils.isNotEmpty(strategyList)) {
            //根据多个模板id查询模板名称
            Set<String> templateIds = strategyList.stream().map(StrategyEntity::getTemplateId).collect(Collectors.toSet());
            Map<String, TemplateEntity> templateMap = templateDao.findAllById(templateIds).stream().collect(Collectors
                    .toMap(TemplateEntity::getId, template -> template));
            resultList = strategyList.stream().map(strategy -> {
                StrategyListDto result = new StrategyListDto();
                BeanUtils.copyProperties(strategy, result);
                if (templateMap.containsKey(strategy.getTemplateId())) {
                    TemplateEntity template = templateMap.get(strategy.getTemplateId());
                    result.setTemplateName(template.getTemplateName());
                    result.setStrategyType(template.getStrategyType());
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<StrategyDataDto> findStrategyById(String id, Integer type) {
        StrategyDataDto result = new StrategyDataDto();
        result.setId(id);
        Optional<StrategyEntity> optional = strategyDao.findById(id);
        if (optional.isPresent()) {
            StrategyEntity strategy = optional.get();
            result.setIssueTime(strategy.getIssueTime());
            //获取模板内容
            templateDao.findById(strategy.getTemplateId()).ifPresent(template -> {
                if (template.getIsDelete() != 2 && StringUtil.isNotEmpty(template.getConfigPath())) {
                    if (Boolean.TRUE.equals(LocalParamVo.FILE_TYPE)) {
                        result.setTemplateContent(new String(OssFileUtil.readFile(FileUtil.subString(template.getConfigPath(), FileUtil.SLASH, FileUtil.QUESTION)), StandardCharsets.UTF_8));
                    } else {
                        result.setTemplateContent(new String(LocalFileUtil.readFile(template.getConfigPath()), StandardCharsets.UTF_8));
                    }
                }
            });
            //保存数据
            if (type == 1) {
                result.setConfigContent(strategy.getConfigContent());
            }
            //读取数据
            if (type == 2) {
                //边缘网关
                if (strategy.getStrategyType() == 1) {
                    //获取设备编号
                    DeviceBasicInfoDto device = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(strategy.getDeviceId()))
                            .getData().get(strategy.getDeviceId());
                    if (device == null || StringUtil.isEmpty(device.getDeviceNumber())) {
                        return ResponseResult.paramError("设备不存在");
                    }
                    //根据模板id查询模板策略名称
                    Optional<TemplateEntity> templateOptional = templateDao.findById(strategy.getTemplateId());
                    String name = null;
                    if (templateOptional.isPresent() && StringUtil.isNotEmpty(templateOptional.get().getConfigPath())) {
                        String fileContent = FileUtil.readFile(templateOptional.get().getConfigPath());
                        name = JSON.parseObject(fileContent).getString("name");
                    }
                    if (StringUtil.isEmpty(name)) {
                        return ResponseResult.paramError("边缘网关解析模板策略名称为空, 不允许读取");
                    }
                    //下发参数
                    GateWayPolicyVo policyVo = new GateWayPolicyVo();
                    policyVo.setDeviceCode(device.getDeviceNumber());
                    policyVo.setPolicyId(strategy.getPolicyId());
                    policyVo.setName(name);
                    policyVo.setCmdType("get");

                    GateWayPolicyDto gateWayPolicy = protocolService.issuedPolicyParam(policyVo).getData();
                    if (gateWayPolicy != null && StringUtil.isNotEmpty(gateWayPolicy.getIssuedStatus())) {
                        //下发状态 -1-执行超时 0-执行成功 1-执行失败 9-无效策略 255-其他原因
                        switch (gateWayPolicy.getIssuedStatus()) {
                            case -1:
                                return ResponseResult.paramError("边缘网关读取超时");
                            case 0:
                                JSONObject contentMap = new JSONObject();
                                contentMap.put("name", name);
                                contentMap.put("policyCfg", gateWayPolicy.getPolicyCfg());
                                result.setConfigContent(JSON.toJSONString(contentMap));
                                break;
                            case 1:
                                return ResponseResult.paramError("边缘网关读取失败");
                            case 9:
                                return ResponseResult.paramError("边缘网关读取无效(网关不支持)");
                            default:
                                return ResponseResult.paramError("边缘网关读取异常(未知错误)");
                        }
                    }
                }
                //云网关策略
                if (strategy.getStrategyType() == 2) {
                    result.setConfigContent(strategy.getConfigParam());
                }

                //云平台策略
                if (strategy.getStrategyType() == 3) {
                    result.setConfigContent(strategy.getConfigParam());
                }
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteStrategyById(String id) {
        //根据策略id查询策略数据
        Optional<StrategyEntity> optional = strategyDao.findById(id);
        if (optional.isPresent()) {
            StrategyEntity strategy = optional.get();
            if (StringUtil.isNotEmpty(strategy.getExecuteStatus()) && StringUtil.isNotEmpty(strategy.getDeviceId()) && strategy.getExecuteStatus() == 1) {
//                if (strategy.getStrategyType() == 1) {
//                    return ResponseResult.paramError("边缘网关策略正在执行中，不能删除");
//                }
//                if (strategy.getStrategyType() == 2) {
//                    return ResponseResult.paramError("云网关策略正在执行中，不能删除");
//                }
                //平台策略 可以直接删除
                if (strategy.getStrategyType() == 3) {
                    crontabService.batchDeleteStrategyTask(Collections.singletonList(id));
                }
            }
            strategyDao.delete(strategy);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> cloneStrategy(String id, String siteId, String deviceId, Integer strategyType) {
        if (StringUtil.isNotEmpty(id)) {
            Optional<StrategyEntity> optional = strategyDao.findById(id);
            if (optional.isPresent()) {
                StrategyEntity strategyEntity = optional.get();
                if (StringUtil.isEmpty(strategyEntity.getStrategyType()) || !Objects.equals(strategyType, strategyEntity.getStrategyType())) {
                    return ResponseResult.paramError("策略类型不一致, 克隆失败");
                }
                StrategyEntity strategy = new StrategyEntity();
                BeanUtils.copyProperties(strategyEntity, strategy);
                strategy.setId(null);
                strategy.setSiteId(siteId);
                strategy.setDeviceId(deviceId);
                strategy.setIssueTime(null);
                strategy.setConfigName(null);
                strategy.setConfigParam(null);
                strategy.setExecuteStatus(0);
                strategy.setPolicyId((long)TimestampUtil.getUniqueTimestamp());
                strategyDao.save(strategy);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<Void> issuedStrategy(String id) {
        Optional<StrategyEntity> optional = strategyDao.findById(id);
        if (optional.isPresent()) {
            StrategyEntity strategy = optional.get();
            if (StringUtil.isEmpty(strategy.getConfigContent())) {
                return ResponseResult.paramError("策略配置为空，不能下发");
            }

            //边缘网关下发
            if (strategy.getStrategyType() == 1) {
                //获取设备编号
                DeviceBasicInfoDto device = deviceService.findDeviceBasicInfoByIds(Collections.singletonList(strategy.getDeviceId()))
                        .getData().get(strategy.getDeviceId());
                if (device == null || StringUtil.isEmpty(device.getDeviceNumber())) {
                    return ResponseResult.paramError("设备不存在");
                }
                //下发参数
                JSONObject contentMap = parseConfigContent(strategy.getConfigContent());
                //网关配置参数设置下发
                String name = contentMap.getString("name");
                if (StringUtil.isEmpty(name)) {
                    return ResponseResult.paramError("策略名称不能为空");
                }
                Object policyCfg = contentMap.get("policyCfg");
                if (StringUtil.isEmpty(policyCfg)) {
                    return ResponseResult.paramError("策略参数不能为空");
                }
                GateWayPolicyVo policyVo = new GateWayPolicyVo();
                policyVo.setDeviceCode(device.getDeviceNumber());
                policyVo.setPolicyId(strategy.getPolicyId());
                policyVo.setName(name);
                policyVo.setPolicyCfg(policyCfg);

                GateWayPolicyVo.PolicyPeriod policyPeriod = new GateWayPolicyVo.PolicyPeriod();
                policyPeriod.setControlSwitch(true);
                policyPeriod.setExecuteType(1);
                policyPeriod.setExecuteTime("00:00:00");
                policyPeriod.setPlanType(1);
                policyPeriod.setFilterDates(null);
                policyVo.setPolicyPeriod(policyPeriod);
                policyVo.setCmdType("set");
                GateWayPolicyDto result = protocolService.issuedPolicyParam(policyVo).getData();
                if (result != null && StringUtil.isNotEmpty(result.getIssuedStatus())) {
                    //下发状态 -1-执行超时 0-执行成功 1-执行失败 255-其他原因
                    switch (result.getIssuedStatus()) {
                        case 0:
                            //存取下发配置参数
                            strategy.setConfigName(name);
                            strategy.setConfigParam(JSON.toJSONString(contentMap));
                            strategy.setIssueTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                            strategy.setExecuteStatus(1);
                            strategyDao.save(strategy);
                            return ResponseResult.ok();
                        case -1:
                            return ResponseResult.paramError("边缘网关策略下发超时");
                        case 1:
                            return ResponseResult.paramError("边缘网关策略下发失败");
                        case 9:
                            return ResponseResult.paramError("边缘网关策略下发无效(网关不支持)");
                        default:
                            return ResponseResult.paramError("边缘网关策略下发异常(未知原因)");
                    }
                }
            }

            //TODO 云网关暂时不支持
            if (strategy.getStrategyType() == 2) {
                return ResponseResult.paramError("云网关策略暂时不支持下发");
            }

            //云平台下发
            if (strategy.getStrategyType() == 3) {
                //校验该站点是否有正在执行的策略
                List<StrategyEntity> checkList = strategyDao.findAll(Example.of(StrategyEntity.builder().siteId(strategy.getSiteId())
                        .deviceId(null).strategyType(3).executeStatus(1).build())).stream()
                        .filter(s -> !Objects.equals(s.getId(), strategy.getId()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(checkList)) {
                    return ResponseResult.paramError("该站点的云平台有策略正在执行中，不能下发新策略");
                }
                StrategyTaskVo strategyTaskVo = new StrategyTaskVo();
                BeanUtils.copyProperties(strategy, strategyTaskVo);
                strategyTaskVo.setFilterDates(null);
                strategyTaskVo.setExecuteType(1);
                strategyTaskVo.setExecuteTime("00:00:00");
                JSONObject contentMap = parseConfigContent(strategy.getConfigContent());
                platformPolicyCfg(strategyTaskVo, contentMap);
                ResponseResult<Void> result = crontabService.batchUpdateStrategyTask(Collections.singletonList(strategyTaskVo));
                if (result.isSuccess()) {
                    //存取下发配置参数
                    strategy.setConfigName(contentMap.getString("name"));
                    strategy.setConfigParam(JSON.toJSONString(contentMap));
                    strategy.setIssueTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                    strategy.setExecuteStatus(1);
                    strategyDao.save(strategy);
                    return ResponseResult.ok();
                } else {
                    return ResponseResult.error(ResponseResult.FAIL);
                }
            }

        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<StrategyTaskVo>> findStrategyHandTaskList(String siteId) {
        //返回的集合
        List<StrategyTaskVo> resultList = Lists.newArrayList();

        List<StrategyEntity> strategyList;
        if (StringUtil.isNotEmpty(siteId)) {
            strategyList = strategyDao.findAll(Example.of(StrategyEntity.builder().siteId(siteId).strategyType(3).executeStatus(1).build()));
        } else {
            strategyList = strategyDao.findAll(Example.of(StrategyEntity.builder().strategyType(3).executeStatus(1).build()));
        }
        if (CollectionUtils.isNotEmpty(strategyList)) {
            resultList = strategyList.stream().map(strategyEntity -> {
                StrategyTaskVo strategyTaskVo = new StrategyTaskVo();
                BeanUtils.copyProperties(strategyEntity, strategyTaskVo);
                strategyTaskVo.setFilterDates(null);
                strategyTaskVo.setExecuteType(1);
                strategyTaskVo.setExecuteTime("00:00:00");
                platformPolicyCfg(strategyTaskVo, parseConfigContent(strategyEntity.getConfigContent()));
                return strategyTaskVo;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    //解析文件内容
    private static JSONObject parseConfigContent(String configContent) {
        JSONObject resultMap = new JSONObject();
        if (StringUtil.isNotEmpty(configContent)) {
            JSONObject mainObject = JSON.parseObject(configContent);
            //获取策略名称
            String name = mainObject.getString("name");
            resultMap.put("name", name);
            JSONArray policyCfgArray = mainObject.getJSONArray("policyCfg");
            if (policyCfgArray != null) {
                Map<String, Object> result = Maps.newHashMap();
                policyCfgArray.forEach(p -> {
                    JSONObject policy = JSON.parseObject(p.toString());
                    String key = policy.getString("en_name");
                    String type = policy.getString("type");
                    switch (type) {
                        case "jsonObject":
                            result.put(key, parseJsonObject(policy));
                            break;
                        case "jsonArray":
                            result.put(key, policy.getJSONArray("body"));
                            break;
                        case "string":
                        case "date":
                            result.put(key, policy.getString("value"));
                            break;
                        case "int":
                            result.put(key, policy.getIntValue("value"));
                            break;
                        case "bool":
                            result.put(key, policy.getBooleanValue("value"));
                            break;
                        case "double":
                            result.put(key, policy.getDoubleValue("value"));
                            break;
                        case "long":
                            result.put(key, policy.getLongValue("value"));
                            break;
                        case "float":
                            result.put(key, policy.getFloatValue("value"));
                            break;
                    }
                });
                resultMap.put("policyCfg", result);
            }
        }
        return resultMap;
    }

    /**
     * 递归解析parseObject
     *
     * @param policy 参数
     * @return key-value数据
     */
    private static Map<String, Object> parseJsonObject(JSONObject policy) {
        Map<String, Object> resultMap = Maps.newHashMap();
        policy.getJSONArray("body").forEach(data -> {
            JSONObject jsonObject = JSON.parseObject(data.toString());
            switch (jsonObject.getString("type")) {
                case "string":
                case "date":
                    resultMap.put(jsonObject.getString("en_name"), jsonObject.getString("value"));
                    break;
                case "int":
                    resultMap.put(jsonObject.getString("en_name"), jsonObject.getIntValue("value"));
                    break;
                case "bool":
                    resultMap.put(jsonObject.getString("en_name"), jsonObject.getBooleanValue("value"));
                    break;
                case "double":
                    resultMap.put(jsonObject.getString("en_name"), jsonObject.getDoubleValue("value"));
                    break;
                case "long":
                    resultMap.put(jsonObject.getString("en_name"), jsonObject.getLongValue("value"));
                    break;
                case "float":
                    resultMap.put(jsonObject.getString("en_name"), jsonObject.getFloatValue("value"));
                    break;
                case "jsonArray":
                    resultMap.put(jsonObject.getString("en_name"), jsonObject.getJSONArray("body"));
                    break;
                case "jsonObject":
                    resultMap.put(jsonObject.getString("en_name"), parseJsonObject(jsonObject));
                    break;
            }
        });
        return resultMap;
    }

    private String parseFileContent(MultipartFile configFile) {
        try {
            return new String(configFile.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("读取文件内容报错", e);
            return null;
        }
    }

    private void platformPolicyCfg(StrategyTaskVo strategyTaskVo, JSONObject contentMap) {
        if (contentMap.containsKey("policyCfg")) {
            JSONObject policyCfg = contentMap.getJSONObject("policyCfg");
            if (policyCfg.containsKey("EnergyStrategyPlugins")) {
                JSONObject strategyPlugins = policyCfg.getJSONObject("EnergyStrategyPlugins");
                strategyTaskVo.setEnabled(strategyPlugins.getBoolean("enabled"));
                strategyTaskVo.setMaxP(strategyPlugins.getDouble("maxP"));
                strategyTaskVo.setWavePower(strategyPlugins.getDouble("wavePower"));
                strategyTaskVo.setPriority(strategyPlugins.getInteger("priority"));
                strategyTaskVo.setPeriod(strategyPlugins.getInteger("period"));
            }
            if (policyCfg.containsKey("EnergyStrategyPile")) {
                Map<String, StrategyTaskVo.StrategyPile> strategyPileMap = policyCfg.getJSONArray("EnergyStrategyPile").stream().map(s -> {
                    JSONObject strategyPile = JSON.parseObject(JSON.toJSONString(s), JSONObject.class);
                    StrategyTaskVo.StrategyPile strategyPileVo = new StrategyTaskVo.StrategyPile();
                    strategyPileVo.setCid(strategyPile.getString("cid"));
                    strategyPileVo.setGid(strategyPile.getInteger("gid"));
                    strategyPileVo.setRatedP(strategyPile.getDouble("ratedP"));
                    strategyPileVo.setMaxP(strategyPile.getDouble("maxP"));
                    strategyPileVo.setMinP(strategyPile.getDouble("minP"));
                    strategyPileVo.setType(strategyPile.getInteger("type"));
                    strategyPileVo.setControlType(strategyPile.getInteger("controlType"));
                    strategyPileVo.setOfflineDuration(strategyPile.getDouble("offlineDuration"));
                    return strategyPileVo;
                }).collect(Collectors.toMap(a -> a.getCid() + a.getGid(), a -> a, (k1, k2) -> k1));
                strategyTaskVo.setStrategyPileMap(strategyPileMap);
            }
            if (policyCfg.containsKey("EnergyStrategyValleyTime")) {
                strategyTaskVo.setStrategyTimeList(policyCfg.getJSONArray("EnergyStrategyValleyTime").stream().map(s -> {
                    JSONObject strategyPile = JSON.parseObject(JSON.toJSONString(s), JSONObject.class);
                    StrategyTaskVo.StrategyTime strategyTime = new StrategyTaskVo.StrategyTime();
                    strategyTime.setBeginTime(strategyPile.getString("begin"));
                    strategyTime.setEndTime(strategyPile.getString("end"));
                    strategyTime.setType(strategyPile.getInteger("type"));
                    strategyTime.setRepeatNum(strategyPile.getInteger("num"));
                    return strategyTime;
                }).collect(Collectors.toList()));
            }
        }
    }

}
