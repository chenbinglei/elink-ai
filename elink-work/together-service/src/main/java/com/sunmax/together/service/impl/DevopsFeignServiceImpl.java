package com.sunmax.together.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.dto.together.ElectConfigDto;
import com.sunmax.common.dto.together.ops.AppInspectHandTaskListDto;
import com.sunmax.common.dto.together.ops.AppInspectItemDto;
import com.sunmax.common.dto.together.ops.AppInspectSiteHistoryDto;
import com.sunmax.common.dto.together.ops.AppInspectTaskHistoryDto;
import com.sunmax.common.entity.BaseTimeEntity;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.together.AppInspectSiteVo;
import com.sunmax.together.dao.asset.ElectConfigDao;
import com.sunmax.together.dao.asset.ElectTimeFrameDao;
import com.sunmax.together.dao.ops.InspectionItemDao;
import com.sunmax.together.dao.ops.InspectionSiteDao;
import com.sunmax.together.dao.ops.InspectionTaskDao;
import com.sunmax.together.entity.assets.ElectConfigEntity;
import com.sunmax.together.entity.assets.ElectTimeFrameEntity;
import com.sunmax.together.entity.ops.InspectionItemEntity;
import com.sunmax.together.entity.ops.InspectionSiteEntity;
import com.sunmax.together.entity.ops.InspectionTaskEntity;
import com.sunmax.together.service.DevopsFeignService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
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
public class DevopsFeignServiceImpl implements DevopsFeignService {

    @Resource
    private InspectionTaskDao inspectionTaskDao;

    @Resource
    private InspectionItemDao inspectionItemDao;

    @Resource
    private InspectionSiteDao inspectionSiteDao;

    @Resource
    private SystemService systemService;

    @Resource
    private DeviceService deviceService;

    @Resource
    private ElectConfigDao electConfigDao;

    @Resource
    private ElectTimeFrameDao electTimeFrameDao;

    @Override
    public ResponseResult<List<AppInspectHandTaskListDto>> findAppInspectHandTaskList(String userId) {
        //返回的集合
        List<AppInspectHandTaskListDto> resultList = Lists.newArrayList();
        //根据用户id查询巡检任务列表
        List<InspectionTaskEntity> inspectionTaskList = inspectionTaskDao.findAll(Example.of(InspectionTaskEntity.builder().userId(userId).build()));
        if (CollectionUtils.isNotEmpty(inspectionTaskList)) {
            inspectionTaskList = inspectionTaskList.stream().filter(it -> StringUtil.isNotEmpty(it.getTaskStatus())
                    && !Objects.equals(it.getTaskStatus(), 1) && !Objects.equals(it.getTaskStatus(), 5)).collect(Collectors.toList());
            //对数据进行组装
            resultList = inspectionTaskList.stream().map(inspectionTask -> {
                AppInspectHandTaskListDto result = new AppInspectHandTaskListDto();
                BeanUtils.copyProperties(inspectionTask, result);
                if (StringUtil.isNotEmpty(inspectionTask.getCreateTime())) {
                    result.setCreateTime(DateUtil.localDateTimeToStr(inspectionTask.getCreateTime()));
                }
                if (StringUtil.isNotEmpty(inspectionTask.getUpdateTime())) {
                    result.setUpdateTime(DateUtil.localDateTimeToStr(inspectionTask.getUpdateTime()));
                }
                return result;
            }).collect(Collectors.toList());

        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<AppInspectItemDto>> findInspectionItemListBySiteId(String siteId) {
        //返回的集合
        List<AppInspectItemDto> resultList = Lists.newArrayList();
        //根据站点id查询巡检项数据列表
        List<InspectionItemEntity> inspectionItemList = inspectionItemDao.findAll(Example.of(InspectionItemEntity.builder().siteId(siteId).build()));
        if (CollectionUtils.isNotEmpty(inspectionItemList)) {
            resultList = inspectionItemList.stream().map(inspectionItem -> {
                AppInspectItemDto result = new AppInspectItemDto();
                BeanUtils.copyProperties(inspectionItem, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> updateInspectSite(AppInspectSiteVo inspectSiteVo) {
        //根据主键id查询巡检站点数据
        Optional<InspectionSiteEntity> optional = inspectionSiteDao.findById(inspectSiteVo.getId());
        if (optional.isPresent()) {
            InspectionSiteEntity inspectionSite = optional.get();
            if (StringUtil.isNotEmpty(inspectSiteVo.getStatus())) {
                inspectionSite.setStatus(inspectSiteVo.getStatus());
                if (Objects.equals(inspectSiteVo.getStatus(), 3) || Objects.equals(inspectSiteVo.getStatus(), 4)) {
                    inspectionSite.setFinishTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                }
            }
            if (StringUtil.isNotEmpty(inspectSiteVo.getItemStates())) {
                inspectionSite.setItemStates(inspectSiteVo.getItemStates());
            }
            inspectionSite.setRemark(inspectSiteVo.getRemark());
            //批量删除附件
            if (StringUtil.isNotEmpty(inspectSiteVo.getDeletePaths()) && StringUtil.isNotEmpty(inspectionSite.getAnnexPath())) {
                List<String> deletePaths = Arrays.stream(inspectSiteVo.getDeletePaths().split(FileUtil.COMMA)).collect(Collectors.toList());
                FileUtil.deleteAllFile(deletePaths);
                List<String> annexPaths = Arrays.stream(inspectionSite.getAnnexPath().split(FileUtil.COMMA)).filter(d -> !deletePaths.contains(d))
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(annexPaths)) {
                    inspectionSite.setAnnexPath(String.join(FileUtil.COMMA, annexPaths));
                }
            }
            inspectionSiteDao.save(inspectionSite);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> uploadInspectSiteFile(String id, MultipartFile annexFile) {
        //根据主键id查询巡检站点数据
        Optional<InspectionSiteEntity> optional = inspectionSiteDao.findById(id);
        if (optional.isPresent() && annexFile != null && !annexFile.isEmpty()) {
            InspectionSiteEntity inspectionSite = optional.get();
            //上传附件
            if (StringUtil.isEmpty(inspectionSite.getAnnexPath())) {
                inspectionSite.setAnnexPath(FileUtil.getFilePath(annexFile, null));
            } else {
                inspectionSite.setAnnexPath(inspectionSite.getAnnexPath() + FileUtil.COMMA + FileUtil.getFilePath(annexFile, null));
            }
            inspectionSiteDao.save(inspectionSite);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<PageDto<AppInspectSiteHistoryDto>> findAppInspectSiteHistoryList(String userId, Integer page, Integer size) {
        //返回的集合
        List<AppInspectSiteHistoryDto> resultList = Lists.newArrayList();
        //校验入参
        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(page) || StringUtil.isEmpty(size)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //根据用户id查询租户id
        UserDto user = systemService.findUserInfoByIdsFeign(Collections.singletonList(userId)).getData().get(userId);
        if (user == null || StringUtil.isEmpty(user.getTenantId())) {
            return ResponseResult.paramError("未找到该用户对应的租户信息");
        }
        //根据租户id查询巡检任务列表
        List<InspectionTaskEntity> inspectionTaskList = inspectionTaskDao.findAll(Example.of(InspectionTaskEntity.builder().tenantId(user.getTenantId()).build()),
                Sort.by(Sort.Direction.DESC, "createTime"));
        if (CollectionUtils.isNotEmpty(inspectionTaskList)) {
            List<String> taskIds = inspectionTaskList.stream().map(BaseTimeEntity::getId).collect(Collectors.toList());
            Map<String, List<InspectionSiteEntity>> inspectionSiteMap = inspectionSiteDao.findAllByTaskIdIn(taskIds).stream()
                    .collect(Collectors.groupingBy(InspectionSiteEntity::getSiteId));
            //根据多个站点id查询站点名称
            Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(new ArrayList<>(inspectionSiteMap.keySet())).getData();
            //根据多个用户id查询用户名称
            List<String> userIds = inspectionSiteMap.values().stream().flatMap(i -> i.stream().map(InspectionSiteEntity::getUserId)
                    .filter(StringUtil::isNotEmpty).distinct()).collect(Collectors.toList());
            Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(userIds).getData();
            inspectionSiteMap.forEach((siteId, inspectionSiteList) -> {
                AppInspectSiteHistoryDto result = new AppInspectSiteHistoryDto();
                result.setSiteId(siteId);
                if (siteInfoMap.containsKey(siteId)) {
                    result.setSiteName(siteInfoMap.get(siteId).getSiteName());
                }
                List<InspectionSiteEntity> lastInspectionSiteList = inspectionSiteList.stream().filter(s -> StringUtil.isNotEmpty(s.getFinishTime()))
                        .sorted(Comparator.comparing(InspectionSiteEntity::getFinishTime).reversed()).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(lastInspectionSiteList)) {
                    InspectionSiteEntity inspectionSite = lastInspectionSiteList.get(0);
                    result.setInspectCount(inspectionSiteList.size()); //巡检次数
                    result.setDistanceDay(DateUtil.compareDiffBetweenDays(DateUtil.strToLocalDateTime(inspectionSite.getFinishTime()), LocalDateTime.now())); //距离上次巡检天数
                    if (StringUtil.isNotEmpty(inspectionSite.getUserId()) && userMap.containsKey(inspectionSite.getUserId())) {
                        result.setUserName(userMap.get(inspectionSite.getUserId()).getFullName());
                    }
                    result.setFinishTime(inspectionSite.getFinishTime()); //完成时间
                    result.setStatus(inspectionSite.getStatus()); //巡检状态

                    //该站点历史数据
                    result.setSiteHistoryList(inspectionSiteList.stream().map(inspectSite -> {
                        AppInspectSiteHistoryDto.AppSiteHistory appSiteHistory = new AppInspectSiteHistoryDto.AppSiteHistory();
                        BeanUtils.copyProperties(inspectSite, appSiteHistory);
                        if (StringUtil.isNotEmpty(inspectionSite.getUserId()) && userMap.containsKey(inspectionSite.getUserId())) {
                            appSiteHistory.setUserName(userMap.get(inspectionSite.getUserId()).getFullName());
                        }
                        //统计异常数量
                        if (StringUtil.isNotEmpty(inspectionSite.getItemStates())) {
                            appSiteHistory.setExceptionNum(JSON.parseObject(inspectionSite.getItemStates()).values().stream().filter(state ->
                                    StringUtil.isNotEmpty(state) && Integer.parseInt(String.valueOf(state)) == 3).count());
                        } else {
                            appSiteHistory.setExceptionNum(0L);
                        }
                        return appSiteHistory;
                    }).collect(Collectors.toList()));
                }
                resultList.add(result);
            });
        }
        return ResponseResult.ok(new PageDto<>(resultList, page, size));
    }

    @Override
    public ResponseResult<PageDto<AppInspectTaskHistoryDto>> findAppInspectTaskHistoryList(String userId, Integer page, Integer size) {
        //返回的集合
        List<AppInspectTaskHistoryDto> resultList = Lists.newArrayList();
        //校验入参
        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(page) || StringUtil.isEmpty(size)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //根据用户id查询租户id
        UserDto user = systemService.findUserInfoByIdsFeign(Collections.singletonList(userId)).getData().get(userId);
        if (user == null || StringUtil.isEmpty(user.getTenantId())) {
            return ResponseResult.paramError("未找到该用户对应的租户信息");
        }
        //根据查询条件查询巡检任务列表
        Page<InspectionTaskEntity> inspectionTaskPage = inspectionTaskDao.findAll((Specification<InspectionTaskEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("tenantId"), user.getTenantId())); //租户id
            return cb.and(list.toArray(new Predicate[0]));
        }, PageRequest.of(page - 1, size, Sort.by("createTime").descending()));
        if (CollectionUtils.isNotEmpty(inspectionTaskPage.getContent())) {
            //根据多个用户id查询用户名称
            List<String> userIds = inspectionTaskPage.getContent().stream().map(InspectionTaskEntity::getUserId).filter(StringUtil::isNotEmpty)
                    .distinct().collect(Collectors.toList());
            Map<String, UserDto> userMap = systemService.findUserInfoByIdsFeign(userIds).getData();

            //根据多个任务id查询待验收或者完结的巡检项异常数量
            List<Integer> taskStatus = Arrays.asList(4, 5);
            Set<String> taskIds = inspectionTaskPage.getContent().stream().filter(i -> taskStatus.contains(i.getTaskStatus()))
                    .map(InspectionTaskEntity::getId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
            Map<String, Integer> taskItemExceptionNumMap = inspectionSiteDao.findAllByTaskIdIn(taskIds).stream().filter(s -> StringUtil.isNotEmpty(s.getItemStates()))
                    .collect(Collectors.groupingBy(InspectionSiteEntity::getTaskId, Collectors.summingInt(s ->
                            Math.toIntExact(JSON.parseObject(s.getItemStates()).values().stream().filter(state -> StringUtil.isNotEmpty(state)
                                    && Integer.parseInt(String.valueOf(state)) == 3).count()))));

            resultList = inspectionTaskPage.getContent().stream().map(t -> {
                AppInspectTaskHistoryDto result = new AppInspectTaskHistoryDto();
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
    public ResponseResult<Map<String, List<ElectConfigDto>>> findElectConfigListBySiteIds(List<String> siteIds, String moduleTypes, String startDate, String endDate) {
        //返回的对象
        Map<String, List<ElectConfigDto>> resultMap = Maps.newHashMap();
        //根据多个站点id及时间查询站点的电价配置数据
        List<ElectConfigEntity> electConfigList = electConfigDao.findElectConfigListBySiteIds(siteIds, Arrays.stream(moduleTypes.split(FileUtil.COMMA))
                .map(Integer::parseInt).collect(Collectors.toList()), startDate, endDate);
        if (CollectionUtils.isNotEmpty(electConfigList)) {
            //根据多个电站配置id查询尖峰平谷电价
            Set<String> electConfigIds = electConfigList.stream().map(ElectConfigEntity::getId).collect(Collectors.toSet());
            Map<String, List<ElectTimeFrameEntity>> electTimeFrameMap = electTimeFrameDao.findAllByElectConfigIdIn(electConfigIds).stream()
                    .collect(Collectors.groupingBy(ElectTimeFrameEntity::getElectConfigId));
            //对数据进行组装
            resultMap = electConfigList.stream().map(e -> {
                ElectConfigDto result = new ElectConfigDto();
                BeanUtils.copyProperties(e, result);
                if (electTimeFrameMap.containsKey(e.getId())) {
                    result.setElectTimeFrameList(electTimeFrameMap.get(e.getId()).stream().map(t -> {
                        ElectConfigDto.ElectTimeFrame electTimeFrame = new ElectConfigDto.ElectTimeFrame();
                        BeanUtils.copyProperties(t, electTimeFrame);
                        return electTimeFrame;
                    }).collect(Collectors.toList()));
                }
                return result;
            }).collect(Collectors.groupingBy(ElectConfigDto::getSiteId));
        }
        return ResponseResult.ok(resultMap);
    }


}
