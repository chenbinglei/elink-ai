package com.sunmax.together.service.asset.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.dto.together.ops.InspectionSiteListDto;
import com.sunmax.common.dto.together.ops.InspectionTaskDetailDto;
import com.sunmax.common.dto.together.ops.InspectionUserNameDto;
import com.sunmax.common.entity.BaseTimeEntity;
import com.sunmax.common.util.CommonUtil;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo;
import com.sunmax.together.constant.InspectionConstant;
import com.sunmax.together.dao.ops.*;
import com.sunmax.together.dto.asset.inspection.InspectionItemListDto;
import com.sunmax.together.dto.asset.inspection.InspectionTaskListDto;
import com.sunmax.together.dto.asset.inspection.InspectionUserListDto;
import com.sunmax.together.dto.asset.inspection.SiteSaveTaskListDto;
import com.sunmax.together.entity.ops.*;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.asset.InspectionService;
import com.sunmax.together.vo.asset.InspectionItemVo;
import com.sunmax.together.vo.asset.InspectionTaskQueryVo;
import com.sunmax.together.vo.asset.InspectionTaskSaveVo;
import com.sunmax.together.vo.asset.InspectionUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InspectionServiceImpl implements InspectionService {

    @Resource
    private InspectionItemDao inspectionItemDao;

    @Resource
    private InspectionUserDao inspectionUserDao;

    @Resource
    private InspectionTaskDao inspectionTaskDao;

    @Resource
    private SystemService systemService;

    @Resource
    private InspectionSiteDao inspectionSiteDao;

    @Resource
    private InspectionRecordDao inspectionRecordDao;

    @Resource
    private DeviceService deviceService;

    public static final String ICON_PATH = System.getenv().getOrDefault("ICON_OSS_PATH", "");

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveInspectionItem(InspectionItemVo itemVo, MultipartFile iconFile) {
        if (StringUtil.isEmpty(itemVo.getSiteId()) || StringUtil.isEmpty(itemVo.getName())) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //根据站点id和巡检项名称查询巡检项配置数据
        List<InspectionItemEntity> inspectionItemList = inspectionItemDao.findAll(Example.of(InspectionItemEntity.builder()
                .siteId(itemVo.getSiteId()).name(itemVo.getName()).build()));
        //新增巡检项配置
        if (StringUtil.isEmpty(itemVo.getId())) {
            //校验巡检项名称
            if (CollectionUtils.isNotEmpty(inspectionItemList)) {
                return ResponseResult.paramError(ResponseResult.PARAM_EXIST);
            }
            InspectionItemEntity inspectionItem = new InspectionItemEntity();
            BeanUtils.copyProperties(itemVo, inspectionItem);
            if (iconFile != null && iconFile.getSize() > 0) {
                inspectionItem.setIconPath(FileUtil.getFilePath(iconFile, inspectionItem.getIconPath()));
            }
            inspectionItemDao.save(inspectionItem);
            return ResponseResult.ok();
        } else { //编辑巡检项配置
            //校验巡检项名称
            inspectionItemList = inspectionItemList.stream().filter(item -> !item.getId().equals(itemVo.getId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(inspectionItemList)) {
                return ResponseResult.paramError(ResponseResult.PARAM_EXIST);
            }
            Optional<InspectionItemEntity> optional = inspectionItemDao.findById(itemVo.getId());
            if (optional.isPresent()) {
                InspectionItemEntity inspectionItem = new InspectionItemEntity();
                BeanUtils.copyProperties(itemVo, inspectionItem);
                if (iconFile != null && iconFile.getSize() > 0) {
                    String oldIconPath = optional.get().getIconPath();
                    if (Objects.equal(optional.get().getIconPath(), ICON_PATH)) {
                        oldIconPath = null;
                    }
                    inspectionItem.setIconPath(FileUtil.getFilePath(iconFile, oldIconPath));
                }
                inspectionItem.setCreateTime(optional.get().getCreateTime());
                inspectionItemDao.save(inspectionItem);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteAllInspectionItemByIds(List<String> ids) {
        //根据多个电价策略配置id查询电价策略配置
        List<InspectionItemEntity> inspectionItemList = inspectionItemDao.findAllById(ids);
        if (CollectionUtils.isNotEmpty(inspectionItemList)) {
            inspectionItemDao.deleteAll(inspectionItemList);
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<PageDto<InspectionItemListDto>> queryInspectionItemList(String siteId, String name, Integer page, Integer size) {
        Page<InspectionItemEntity> inspectionItemPage = inspectionItemDao.findAll((Specification<InspectionItemEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("siteId"), siteId)); //站点id
            if (StringUtil.isNotEmpty(name)) { //名称模糊查询
                list.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, PageRequest.of(page - 1, size, Sort.by("createTime").descending()));
        List<InspectionItemListDto> resultList = inspectionItemPage.getContent().stream().map(i -> {
            InspectionItemListDto result = new InspectionItemListDto();
            BeanUtils.copyProperties(i, result);
            return result;
        }).collect(Collectors.toList());
        return ResponseResult.ok(new PageDto<>(resultList, inspectionItemPage.getNumber() + 1, inspectionItemPage.getSize(), (int) inspectionItemPage.getTotalElements()));
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<List<String>> importInspectionItem(String siteId, MultipartFile itemFile) {

        if (StringUtil.isEmpty(siteId)) {
            return ResponseResult.paramError("站点id为空");
        }

        //校验文件名是否存在
        String filename = itemFile.getOriginalFilename();
        if (StringUtil.isEmpty(filename)) {
            return ResponseResult.paramError("文件名称为空");
        }
        //校验文件名称格式
        if (!filename.endsWith("xls") && !filename.endsWith("xlsx")) {
            return ResponseResult.paramError("文件不是Excel文件");
        }

        //解析文件内容
        try {
            //根据站点id查询巡检项配置名称列表
            List<String> names = inspectionItemDao.findAll(Example.of(InspectionItemEntity.builder().siteId(siteId).build()))
                    .stream().map(InspectionItemEntity::getName).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
            //获取第一个shell
            Sheet sheet = WorkbookFactory.create(itemFile.getInputStream()).getSheetAt(0);
            //获取Excel的行数
            int totalRows = sheet.getPhysicalNumberOfRows();
            // 得到Excel的列数(前提是有行数)
            if (totalRows <= 1 || StringUtil.isEmpty(sheet.getRow(0))) {
                return ResponseResult.paramError("没有解析到文件中的数据");
            }
            List<InspectionItemEntity> inspectionItemList = Lists.newArrayList();
            //定义错误信息列表
            List<String> errDescList = Lists.newArrayList();
            for (int i = 1; i < totalRows; i++) {
                Row row = sheet.getRow(i);
                String name = CommonUtil.getCellValue(row.getCell(0)); //巡检项名称
                String description = CommonUtil.getCellValue(row.getCell(1)); //巡检内容描述
                if (StringUtil.isEmpty(name)) {
                    errDescList.add("第" + i + "行：【巡检名称】数据为空");
                    continue;
                }
                if (names.contains(name)) {
                    errDescList.add("第" + i + "行：【巡检名称】数据重复");
                    continue;
                }
                InspectionItemEntity inspectionItem = new InspectionItemEntity();
                inspectionItem.setSiteId(siteId);
                inspectionItem.setName(name);
                inspectionItem.setDescription(description);
                inspectionItem.setIconPath(ICON_PATH);
                inspectionItemList.add(inspectionItem);
            }
            //批量添加设备数据
            if (CollectionUtils.isNotEmpty(inspectionItemList)) {
                inspectionItemDao.saveAll(inspectionItemList);
            }
            return ResponseResult.ok(errDescList);
        } catch (Exception e) {
            log.error("导入巡检项配置数据异常", e);
            return ResponseResult.error(ResponseResult.FAIL);
        }
    }

    @Override
    public ResponseResult<List<InspectionUserListDto>> getInspectionUserList(String tenantId) {
        return ResponseResult.ok(inspectionUserDao.findAll(Example.of(InspectionUserEntity.builder().tenantId(tenantId).build())).stream().map(i -> {
            InspectionUserListDto result = new InspectionUserListDto();
            BeanUtils.copyProperties(i, result);
            //获取用户数量
            if (StringUtil.isNotEmpty(i.getUserIds())) {
                result.setUserNum(JSON.parseArray(i.getUserIds(), String.class).size());
            }
            return result;
        }).collect(Collectors.toList()));
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveInspectionUser(InspectionUserVo inspectionUserVo) {
        InspectionUserEntity inspectionUser = new InspectionUserEntity();
        BeanUtils.copyProperties(inspectionUserVo, inspectionUser);
        inspectionUserDao.save(inspectionUser);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<PageDto<InspectionTaskListDto>> queryInspectionTaskList(InspectionTaskQueryVo taskQueryVo) {
        //返回的集合
        List<InspectionTaskListDto> resultList = Lists.newArrayList();
        //根据查询条件查询巡检任务列表
        Page<InspectionTaskEntity> inspectionTaskPage = inspectionTaskDao.findAll((Specification<InspectionTaskEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("tenantId"), taskQueryVo.getTenantId())); //租户id
            if (StringUtil.isNotEmpty(taskQueryVo.getTaskName())) { //任务名称
                list.add(cb.like(root.get("taskName"), "%" + taskQueryVo.getTaskName() + "%"));
            }
            if (StringUtil.isNotEmpty(taskQueryVo.getStartDate()) && StringUtil.isNotEmpty(taskQueryVo.getEndDate())) { //开始时间查询
                LocalDateTime startTime = DateUtil.strToLocalDateTime(DateUtil.getDayStart(taskQueryVo.getStartDate()));
                LocalDateTime endTime = DateUtil.strToLocalDateTime(DateUtil.getDayEnd(taskQueryVo.getEndDate()));
                list.add(cb.between(root.get("createTime"), startTime, endTime));
            }
            if (StringUtil.isNotEmpty(taskQueryVo.getTaskStatus())) { //任务状态 1-未分配 2-未开启 3-巡检中 4-待验收 5-完结
                list.add(cb.equal(root.get("taskStatus"), taskQueryVo.getTaskStatus()));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, PageRequest.of(taskQueryVo.getPage() - 1, taskQueryVo.getSize(), Sort.by("createTime").descending()));
        if (CollectionUtils.isNotEmpty(inspectionTaskPage.getContent())) {
            //根据多个用户id查询用户名称
            List<String> userIds = inspectionTaskPage.getContent().stream().map(InspectionTaskEntity::getUserId).filter(StringUtil::isNotEmpty)
                    .distinct().collect(Collectors.toList());
            Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(userIds).getData();

            //根据多个任务id查询待验收或者完结的巡检项异常数量
            List<Integer> taskStatus = Arrays.asList(4, 5);
            Set<String> taskIds = inspectionTaskPage.getContent().stream().filter(i -> taskStatus.contains(i.getTaskStatus()))
                    .map(InspectionTaskEntity::getId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
            Map<String, Integer> taskItemExceptionNumMap = inspectionSiteDao.findAllByTaskIdIn(taskIds).stream()
                    .filter(s -> StringUtil.isNotEmpty(s.getItemStates())).collect(Collectors
                            .groupingBy(InspectionSiteEntity::getTaskId, Collectors.summingInt(s ->
                            Math.toIntExact(JSON.parseObject(s.getItemStates()).values().stream().filter(v -> StringUtil.isNotEmpty(v)
                                    && Integer.parseInt(String.valueOf(v)) == 3).count()))));

            resultList = inspectionTaskPage.getContent().stream().map(t -> {
                InspectionTaskListDto result = new InspectionTaskListDto();
                BeanUtils.copyProperties(t, result);
                if (userMap.containsKey(t.getUserId())) {
                    result.setUserName(userMap.get(t.getUserId()).getFullName());
                }
                if (taskItemExceptionNumMap.containsKey(t.getId())) {
                    result.setExceptionNum(taskItemExceptionNumMap.get(t.getId()));
                }
                if (t.getTaskStatus() != 5) {
                    result.setUpdateTime(null);
                }
                return result;
            }).collect(Collectors.toList());

        }
        return ResponseResult.ok(new PageDto<>(resultList, inspectionTaskPage.getNumber() + 1, inspectionTaskPage.getSize(), (int) inspectionTaskPage.getTotalElements()));
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveInspectionTask(InspectionTaskSaveVo inspectionTaskVo) {
        if (StringUtil.isEmpty(inspectionTaskVo.getTaskName())) {
            return ResponseResult.paramError("请输入任务名称");
        }
        if (StringUtil.isEmpty(inspectionTaskVo.getSiteIds())) {
            return ResponseResult.paramError("请选择巡检站点");
        }
        InspectionTaskEntity inspectionTask = new InspectionTaskEntity();
        inspectionTask.setTenantId(inspectionTaskVo.getTenantId());
        inspectionTask.setTaskName(inspectionTaskVo.getTaskName());
        inspectionTask.setTaskDesc(inspectionTaskVo.getTaskDesc());
        inspectionTask.setUserId(inspectionTaskVo.getUserId());
        inspectionTask.setTaskStatus(1); //任务状态 1-未分配 2-未开启 3-巡检中 4-待验收 5-完结
        InspectionTaskEntity save = inspectionTaskDao.save(inspectionTask);
        //保存巡检站点数据
        List<InspectionSiteEntity> inspectionSiteList = JSON.parseArray(inspectionTaskVo.getSiteIds(), String.class).stream().map(siteId -> {
            InspectionSiteEntity inspectionSite = new InspectionSiteEntity();
            inspectionSite.setSiteId(siteId);
            inspectionSite.setTaskId(save.getId());
            inspectionSite.setStatus(1); //巡检状态 1-未开始 2-巡检中 3-已完成 4-已放弃
            return inspectionSite;
        }).collect(Collectors.toList());
        inspectionSiteDao.saveAll(inspectionSiteList);
        //保存巡检流水记录
        inspectionRecordDao.save(getInspectionRecordEntity(save.getId(), InspectionConstant.CREATE_TASK, 1, inspectionTaskVo.getUserId(), null));
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<PageDto<SiteSaveTaskListDto>> getSiteSaveTaskList(String userId, String siteName, Integer page, Integer size) {
        //返回的集合
        List<SiteSaveTaskListDto> resultList = Lists.newArrayList();
        //根据用户id查询用户资产授权下的站点
        List<OrganEmpowerListDto> organEmpowerList = systemService.findAllOrganEmpowerByUserId(userId).getData();
        if (CollectionUtils.isNotEmpty(organEmpowerList)) {
            //根据多个站点id查询站点信息
            List<String> siteIds = organEmpowerList.stream().map(OrganEmpowerListDto::getSiteId).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
            Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();
            if (MapUtils.isNotEmpty(siteInfoMap)) {
                //查询进行中的巡检任务
                List<String> taskIds = inspectionTaskDao.findAllByTaskStatusNot(5).stream().map(BaseTimeEntity::getId).collect(Collectors.toList());
                Map<String, Integer> siteStatusMap = inspectionSiteDao.findAllByTaskIdIn(taskIds).stream().collect(Collectors
                        .toMap(InspectionSiteEntity::getSiteId, InspectionSiteEntity::getStatus, (k1, k2) -> k1));
                //根据多个站点id查询巡检任务站点数据
                Map<String, String> siteFinishTimeMap = inspectionSiteDao.findAllBySiteIdInAndFinishTimeNotNull(siteIds).stream()
                        .sorted(Comparator.comparing(InspectionSiteEntity::getFinishTime).reversed())
                        .collect(Collectors.toMap(InspectionSiteEntity::getSiteId, InspectionSiteEntity::getFinishTime, (k1, k2) -> k1));
                //根据多个站点id查询巡检中的任务站点数据
                resultList = siteInfoMap.values().stream().map(siteInfo -> {
                    SiteSaveTaskListDto result = new SiteSaveTaskListDto();
                    result.setId(siteInfo.getId());
                    result.setSiteName(siteInfo.getSiteName());
                    //获取位置
                    if (StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
                        JSONObject readwriteMap = JSON.parseObject(siteInfo.getSiteReadwriteObject());
                        //地址对象
                        if (readwriteMap.containsKey(SiteFieldParamVo.LOCATION)) {
                            JSONObject locationMap = readwriteMap.getJSONObject(SiteFieldParamVo.LOCATION);
                            StringBuilder location = new StringBuilder();
                            //省份
                            if (locationMap.containsKey(SiteFieldParamVo.PROVINCE) && StringUtil.isNotEmpty(locationMap.get(SiteFieldParamVo.PROVINCE))) {
                                location.append(locationMap.getString(SiteFieldParamVo.PROVINCE));
                            }
                            //市级
                            if (locationMap.containsKey(SiteFieldParamVo.CITY) && StringUtil.isNotEmpty(locationMap.get(SiteFieldParamVo.CITY))) {
                                location.append(locationMap.getString(SiteFieldParamVo.CITY));
                            }
                            if (location.length() > 0) {
                                result.setLocation(location.toString());
                            }
                        }
                    }
                    result.setTenantName(siteInfo.getTenantName());
                    if (siteFinishTimeMap.containsKey(siteInfo.getId())) {
                        result.setLastInspectionTime(siteFinishTimeMap.get(siteInfo.getId()));
                    }
                    if (siteStatusMap.containsKey(siteInfo.getId())) {
                        result.setIsInspection(1);
                    } else {
                        result.setIsInspection(2);
                    }
                    return result;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList, page, size));
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> updateInspectionTask(InspectionTaskUpdateVo inspectionTaskVo) {
        if (StringUtil.isEmpty(inspectionTaskVo.getId()) || StringUtil.isEmpty(inspectionTaskVo.getOperationType())) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        Optional<InspectionTaskEntity> optional = inspectionTaskDao.findById(inspectionTaskVo.getId());
        if (optional.isPresent()) {
            InspectionTaskEntity inspectionTask = optional.get();
            //操作类型 0-交接分配 1-提交任务 2-交接任务 3-退回任务 4-开始巡检 5-完成巡检 6-确认验收 7-退回验收 8-交接验收
            switch (inspectionTaskVo.getOperationType()) {
                case 0: //交接分配
                    inspectionTask.setTaskStatus(1); //未分配
                    inspectionTask.setUserId(inspectionTaskVo.getOperationUserId());
                    inspectionRecordDao.save(getInspectionRecordEntity(inspectionTask.getId(), InspectionConstant.CREATE_TASK, 3, inspectionTaskVo.getUserId(), inspectionTaskVo.getOperationOpinion()));
                    break;
                case 1: //提交任务
                    inspectionTask.setTaskStatus(2); //未开启
                    inspectionTask.setUserId(inspectionTaskVo.getOperationUserId());
                    //保存提交任务巡检记录
                    inspectionRecordDao.save(getInspectionRecordEntity(inspectionTask.getId(), InspectionConstant.DISPATCH_TASK, 1, inspectionTaskVo.getUserId(), inspectionTaskVo.getOperationOpinion()));
                    break;
                case 2: //交接任务
                    inspectionTask.setTaskStatus(2); //未开启
                    inspectionTask.setUserId(inspectionTaskVo.getOperationUserId());
                    //保存交接任务巡检记录
                    inspectionRecordDao.save(getInspectionRecordEntity(inspectionTask.getId(), InspectionConstant.DISPATCH_TASK, 3, inspectionTaskVo.getUserId(), inspectionTaskVo.getOperationOpinion()));
                    break;
                case 3: //退回任务
                    inspectionTask.setTaskStatus(1); //未分配
                    //根据任务id查询上一次巡检记录的用户id
                    Optional<InspectionRecordEntity> recordOptional = inspectionRecordDao.findAll(Example.of(InspectionRecordEntity.builder().taskId(inspectionTask.getId()).build()))
                            .stream().max(Comparator.comparing(InspectionRecordEntity::getCreateTime));
                    if (recordOptional.isPresent()) {
                        inspectionTask.setUserId(inspectionTaskVo.getUserId());
                    } else {
                        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
                    }
                    //保存退回任务巡检记录
                    inspectionRecordDao.save(getInspectionRecordEntity(inspectionTask.getId(), InspectionConstant.DISPATCH_TASK, 2, inspectionTaskVo.getUserId(), inspectionTaskVo.getOperationOpinion()));
                    break;
                case 4: //开始巡检
                    inspectionTask.setTaskStatus(3); //巡检中
                    //更新巡检任务站点数据
                    List<InspectionSiteEntity> inspectionSiteList = inspectionSiteDao.findAllByTaskIdIn(Collections.singleton(inspectionTask.getId()));
                    if (CollectionUtils.isNotEmpty(inspectionSiteList)) {
                        inspectionSiteDao.saveAll(inspectionSiteList.stream().peek(inspectionSite -> {
                            inspectionSite.setInspectTime(DateUtil.localDateTimeToStr(LocalDateTime.now())); //巡检时间
                            inspectionSite.setStatus(2); //巡检状态
                            inspectionSite.setUserId(inspectionTask.getUserId()); //巡检用户id
                        }).collect(Collectors.toList()));
                    }
                    //保存开始巡检巡检记录
                    inspectionRecordDao.save(getInspectionRecordEntity(inspectionTask.getId(), InspectionConstant.START_TASK, 1, inspectionTaskVo.getUserId(), inspectionTaskVo.getOperationOpinion()));
                    break;
                case 5: //完成巡检
                    inspectionTask.setTaskStatus(4); //待验收
                    inspectionTask.setUserId(inspectionTaskVo.getOperationUserId());
                    //保存完成巡检巡检记录
                    inspectionRecordDao.save(getInspectionRecordEntity(inspectionTask.getId(), InspectionConstant.COMMIT_TASK, 1, inspectionTaskVo.getUserId(), inspectionTaskVo.getOperationOpinion()));
                    break;
                case 6: //确认验收
                    inspectionTask.setTaskStatus(5); //完结
                    inspectionTask.setUserId(null); //删除当前处理人
                    //保存确认验收巡检记录
                    inspectionRecordDao.save(getInspectionRecordEntity(inspectionTask.getId(), InspectionConstant.ACCEPTANCE_TASK, 1, inspectionTaskVo.getUserId(), inspectionTaskVo.getOperationOpinion()));
                    break;
                case 7: //退回验收
                    inspectionTask.setTaskStatus(3); //巡检中
                    //根据任务id查询上一次巡检记录的用户id
                    Optional<InspectionRecordEntity> acceptanceOptional = inspectionRecordDao.findAll(Example.of(InspectionRecordEntity.builder().taskId(inspectionTask.getId()).build()))
                            .stream().max(Comparator.comparing(InspectionRecordEntity::getCreateTime));
                    if (acceptanceOptional.isPresent()) {
                        inspectionTask.setUserId(inspectionTaskVo.getUserId());
                    } else {
                        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
                    }
                    //保存确认验收巡检记录
                    inspectionRecordDao.save(getInspectionRecordEntity(inspectionTask.getId(), InspectionConstant.ACCEPTANCE_TASK, 2, inspectionTaskVo.getUserId(), inspectionTaskVo.getOperationOpinion()));
                    break;
                case 8: //交接验收
                    inspectionTask.setTaskStatus(4);
                    inspectionTask.setUserId(inspectionTaskVo.getOperationUserId());
                    //保存交接验收巡检记录
                    inspectionRecordDao.save(getInspectionRecordEntity(inspectionTask.getId(), InspectionConstant.COMMIT_TASK, 3, inspectionTaskVo.getUserId(), inspectionTaskVo.getOperationOpinion()));
                    break;
                default:
                    return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            inspectionTaskDao.save(inspectionTask);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<InspectionTaskDetailDto> findInspectionTaskDetailById(String id) {
        //返回的对象
        InspectionTaskDetailDto result = new InspectionTaskDetailDto();

        //根据巡检任务id查询任务详情
        Optional<InspectionTaskEntity> optional = inspectionTaskDao.findById(id);
        if (optional.isPresent()) {
            InspectionTaskEntity inspectionTask = optional.get();
            BeanUtils.copyProperties(inspectionTask, result);
            //根据巡检任务id查询巡检站点数据
            List<InspectionSiteEntity> inspectionSiteList = inspectionSiteDao.findAllByTaskIdIn(Collections.singleton(inspectionTask.getId()));
            if (CollectionUtils.isNotEmpty(inspectionSiteList)) {
                //根据多个站点id查询站点名称和经纬度
                List<String> siteIds = inspectionSiteList.stream().map(InspectionSiteEntity::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();

                result.setInspectionSiteList(inspectionSiteList.stream().map(inspectionSite -> {
                    InspectionTaskDetailDto.InspectionSiteDto inspectionSiteDto = new InspectionTaskDetailDto.InspectionSiteDto();
                    BeanUtils.copyProperties(inspectionSite, inspectionSiteDto);
                    if (siteInfoMap.containsKey(inspectionSite.getSiteId())) {
                        SiteInfoDto siteInfo = siteInfoMap.get(inspectionSite.getSiteId());
                        inspectionSiteDto.setSiteName(siteInfo.getSiteName()); //站点名称
                        if (StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
                            JSONObject readwriteMap = JSON.parseObject(siteInfo.getSiteReadwriteObject());
                            //地址对象
                            if (readwriteMap.containsKey(SiteFieldParamVo.LOCATION)) {
                                JSONObject locationMap = readwriteMap.getJSONObject(SiteFieldParamVo.LOCATION);
                                if (locationMap.containsKey(SiteFieldParamVo.LONGITUDE)) { //经度
                                    inspectionSiteDto.setLongitude(locationMap.getString(SiteFieldParamVo.LONGITUDE));
                                }
                                if (locationMap.containsKey(SiteFieldParamVo.LATITUDE)) { //纬度
                                    inspectionSiteDto.setLatitude(locationMap.getString(SiteFieldParamVo.LATITUDE));
                                }
                            }
                        }
                    }
                    //统计异常数量
                    if (StringUtil.isNotEmpty(inspectionSite.getItemStates())) {
                        inspectionSiteDto.setExceptionNum(JSON.parseObject(inspectionSite.getItemStates()).values().stream().filter(state ->
                                StringUtil.isNotEmpty(state) && Integer.parseInt(String.valueOf(state)) == 3).count());
                    } else {
                        inspectionSiteDto.setExceptionNum(0L);
                    }
                    return inspectionSiteDto;
                }).collect(Collectors.toList()));
            }
            //根据巡检任务id查询巡检记录数据
            List<InspectionRecordEntity> inspectionRecordList = inspectionRecordDao.findAllByTaskId(inspectionTask.getId());
            if (CollectionUtils.isNotEmpty(inspectionRecordList)) {
                //根据多个创建人id查询创建人名称
                List<String> userIds = inspectionRecordList.stream().map(InspectionRecordEntity::getCreateId).filter(StringUtil::isNotEmpty)
                        .distinct().collect(Collectors.toList());
                Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(userIds).getData();

                result.setInspectionRecordList(inspectionRecordList.stream().map(inspectionRecord -> {
                    InspectionTaskDetailDto.InspectionRecordDto inspectionRecordDto = new InspectionTaskDetailDto.InspectionRecordDto();
                    BeanUtils.copyProperties(inspectionRecord, inspectionRecordDto);
                    if (userMap.containsKey(inspectionRecord.getCreateId())) {
                        UserDto user = userMap.get(inspectionRecord.getCreateId());
                        inspectionRecordDto.setCreateName(user.getFullName()); //创建人名称
                    }
                    return inspectionRecordDto;
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteInspectionTaskByIds(List<String> ids) {
        //批量删除巡检任务
        inspectionTaskDao.deleteAllByIdIn(ids);
        //批量删除巡检站点
        inspectionSiteDao.deleteAllByTaskIdIn(ids);
        //批量删除巡检记录
        inspectionRecordDao.deleteAllByTaskIdIn(ids);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult<InspectionSiteListDto> findInspectionSiteListById(String inspectionSiteId) {
        //返回的对象
        InspectionSiteListDto result = new InspectionSiteListDto();

        //根据巡检站点主键id查询巡检站点详情数据
        Optional<InspectionSiteEntity> optional = inspectionSiteDao.findById(inspectionSiteId);
        if (optional.isPresent()) {
            InspectionSiteEntity inspectionSite = optional.get();
            BeanUtils.copyProperties(inspectionSite, result);
            //获取巡检站点检查项列表
            if (StringUtil.isNotEmpty(inspectionSite.getItemStates())) {
                JSONObject itemStateMap = JSON.parseObject(inspectionSite.getItemStates());
                //根据多个巡检项id查询巡检项名称
                Map<String, InspectionItemEntity> itemMap = inspectionItemDao.findAllById(itemStateMap.keySet()).stream().collect(Collectors
                        .toMap(InspectionItemEntity::getId, a -> a, (k1, k2) -> k1));
                result.setItemStateList(itemStateMap.entrySet().stream().map(entry -> {
                    InspectionSiteListDto.ItemState itemState = new InspectionSiteListDto.ItemState();
                    String itemId = entry.getKey();
                    itemState.setItemId(itemId); //巡检项id
                    if (itemMap.containsKey(itemId)) {
                        InspectionItemEntity inspectionItem = itemMap.get(itemId);
                        itemState.setItemName(inspectionItem.getName()); //巡检项名称
                        itemState.setIconPath(inspectionItem.getIconPath()); //巡检项图标路径
                    }
                    if (StringUtil.isNotEmpty(entry.getValue())) {
                        itemState.setItemState(Integer.parseInt(String.valueOf(entry.getValue())));
                    } else {
                        itemState.setItemState(1); //未检查
                    }
                    return itemState;
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<InspectionUserNameDto>> findInspectionUserList(String userId, Integer type) {
        //返回的集合
        List<InspectionUserNameDto> resultList = Lists.newArrayList();
        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(type)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //根据用户id查询租户id
        UserDto user = systemService.findUserInfoByIdsFeign(Collections.singletonList(userId)).getData().get(userId);
        if (user == null || StringUtil.isEmpty(user.getTenantId())) {
            return ResponseResult.paramError("未找到该用户对应的租户信息");
        }

        if (type == 0) {
            resultList = systemService.findUserListByUserId(userId).getData().stream().map(entry -> {
                InspectionUserNameDto result = new InspectionUserNameDto();
                result.setUserId(entry.getId());
                if (StringUtil.isNotEmpty(entry.getFullName())) {
                    result.setUserName(entry.getFullName());
                }
                return result;
            }).collect(Collectors.toList());
        } else {
            List<InspectionUserEntity> inspectionUserList = inspectionUserDao.findAll(Example.of(InspectionUserEntity.builder().tenantId(user.getTenantId()).type(type).build()));
            if (CollectionUtils.isNotEmpty(inspectionUserList) && StringUtil.isNotEmpty(inspectionUserList.get(0).getUserIds())) {
                //根据多个用户id查询用户名称
                List<String> userIds = JSON.parseArray(inspectionUserList.get(0).getUserIds(), String.class);
                resultList = systemService.findUserInfoByIdsFeign(userIds).getData().entrySet().stream().map(entry -> {
                    InspectionUserNameDto result = new InspectionUserNameDto();
                    result.setUserId(entry.getKey());
                    if (StringUtil.isNotEmpty(entry.getValue().getFullName())) {
                        result.setUserName(entry.getValue().getFullName());
                    }
                    return result;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 获取巡检记录实体类
     *
     * @param taskId      任务id
     * @param nodeName    节点名称
     * @param result      处理结果 1-已提交 2-已退回 3-已交接
     * @param createId    创建人id
     * @param flowOpinion 流转意见
     * @return 巡检记录实体类
     */
    private InspectionRecordEntity getInspectionRecordEntity(String taskId, String nodeName, Integer result, String createId, String flowOpinion) {
        InspectionRecordEntity inspectionRecord = new InspectionRecordEntity();
        inspectionRecord.setTaskId(taskId);
        inspectionRecord.setNodeName(nodeName);
        inspectionRecord.setResult(result);
        inspectionRecord.setCreateId(createId);
        inspectionRecord.setFlowOpinion(flowOpinion);
        return inspectionRecord;
    }

}
