package com.sunmax.devops.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.together.ops.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.AppInspectSiteVo;
import com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InspectAppService {

    /**
     * 根据用户id查询巡检进行中的任务列表
     * @param userId 用户id
     * @return 巡检进行中的任务列表
     */
    ResponseResult<List<AppInspectHandTaskListDto>> findAppInspectHandTaskList(String userId);

    /**
     * 修改巡检任务
     * @param inspectionTaskVo 巡检任务编辑参数
     * @return 状态码
     */
    ResponseResult<Void> updateInspectionTask(InspectionTaskUpdateVo inspectionTaskVo);

    /**
     * 根据任务id查询巡检任务详情
     * @param id 巡检任务id
     * @return 巡检任务详情
     */
    ResponseResult<InspectionTaskDetailDto> findInspectionTaskDetailById(String id);

    /**
     * 根据巡检站点主键id查询巡检站点详情数据
     * @param inspectionSiteId 巡检站点主键id
     * @return 巡检站点详情数据
     */
    ResponseResult<InspectionSiteListDto> findInspectionSiteListById(String inspectionSiteId);

    /**
     * 根据用户id和节点类型查询巡检节点人员用户名称
     * @param userId 用户id
     * @param type 节点类型 1-启动巡检 2-现场巡检 3-巡检结果确认
     * @return 巡检人员信息
     */
    ResponseResult<List<InspectionUserNameDto>> findInspectionUserList(String userId, Integer type);

    /**
     * 根据站点id查询巡检项列表
     * @param siteId 站点id
     * @return 巡检项列表
     */
    ResponseResult<List<AppInspectItemDto>> findInspectionItemListBySiteId(String siteId);

    /**
     * 更新巡检站点报表
     * @param inspectSiteVo 巡检站点报表数据
     * @return 状态码
     */
    ResponseResult<Void> updateInspectSite(AppInspectSiteVo inspectSiteVo);

    /**
     * 上传巡检站点附件
     * @param id 巡检站点id
     * @param annexFile 附件文件
     * @return 状态码
     */
    ResponseResult<Void> uploadInspectSiteFile(String id, MultipartFile annexFile);

    /**
     * 根据用户id查询历史巡检任务电站列表
     * @param userId 用户id
     * @param page 当前页
     * @param size 当前页条数
     * @return 巡检历史任务电站列表
     */
    ResponseResult<PageDto<AppInspectSiteHistoryDto>> findAppInspectSiteHistoryList(String userId, Integer page, Integer size);

    /**
     * 根据用户id查询巡检历史任务列表
     * @param userId 用户id
     * @param page 当前页
     * @param size 当前页条数
     * @return 巡检历史任务列表
     */
    ResponseResult<PageDto<AppInspectTaskHistoryDto>> findAppInspectTaskHistoryList(String userId, Integer page, Integer size);

}
