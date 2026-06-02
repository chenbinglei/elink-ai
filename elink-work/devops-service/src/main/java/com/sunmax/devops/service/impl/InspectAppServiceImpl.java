package com.sunmax.devops.service.impl;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.together.ops.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.AppInspectSiteVo;
import com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo;
import com.sunmax.devops.service.InspectAppService;
import com.sunmax.devops.service.feign.TogetherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
public class InspectAppServiceImpl implements InspectAppService {

    @Autowired
    private TogetherService togetherService;

    @Override
    public ResponseResult<List<AppInspectHandTaskListDto>> findAppInspectHandTaskList(String userId) {
        return togetherService.findAppInspectHandTaskList(userId);
    }

    @Override
    public ResponseResult<Void> updateInspectionTask(InspectionTaskUpdateVo inspectionTaskVo) {
        return togetherService.updateInspectionTask(inspectionTaskVo);
    }

    @Override
    public ResponseResult<InspectionTaskDetailDto> findInspectionTaskDetailById(String id) {
        return togetherService.findInspectionTaskDetailById(id);
    }

    @Override
    public ResponseResult<InspectionSiteListDto> findInspectionSiteListById(String inspectionSiteId) {
        return togetherService.findInspectionSiteListById(inspectionSiteId);
    }

    @Override
    public ResponseResult<List<InspectionUserNameDto>> findInspectionUserList(String userId, Integer type) {
        return togetherService.findInspectionUserList(userId, type);
    }

    @Override
    public ResponseResult<List<AppInspectItemDto>> findInspectionItemListBySiteId(String siteId) {
        return togetherService.findInspectionItemListBySiteId(siteId);
    }

    @Override
    public ResponseResult<Void> updateInspectSite(AppInspectSiteVo inspectSiteVo) {
        return togetherService.updateInspectSite(inspectSiteVo);
    }

    @Override
    public ResponseResult<Void> uploadInspectSiteFile(String id, MultipartFile annexFile) {
        return togetherService.uploadInspectSiteFile(id, annexFile);
    }

    @Override
    public ResponseResult<PageDto<AppInspectSiteHistoryDto>> findAppInspectSiteHistoryList(String userId, Integer page, Integer size) {
        return togetherService.findAppInspectSiteHistoryList(userId, page, size);
    }

    @Override
    public ResponseResult<PageDto<AppInspectTaskHistoryDto>> findAppInspectTaskHistoryList(String userId, Integer page, Integer size) {
        return togetherService.findAppInspectTaskHistoryList(userId, page, size);
    }

}
