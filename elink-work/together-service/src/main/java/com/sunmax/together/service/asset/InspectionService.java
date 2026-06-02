package com.sunmax.together.service.asset;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.together.ops.InspectionSiteListDto;
import com.sunmax.common.dto.together.ops.InspectionTaskDetailDto;
import com.sunmax.common.dto.together.ops.InspectionUserNameDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.ops.InspectionTaskUpdateVo;
import com.sunmax.together.dto.asset.inspection.InspectionItemListDto;
import com.sunmax.together.dto.asset.inspection.InspectionTaskListDto;
import com.sunmax.together.dto.asset.inspection.InspectionUserListDto;
import com.sunmax.together.dto.asset.inspection.SiteSaveTaskListDto;
import com.sunmax.together.vo.asset.InspectionItemVo;
import com.sunmax.together.vo.asset.InspectionTaskQueryVo;
import com.sunmax.together.vo.asset.InspectionTaskSaveVo;
import com.sunmax.together.vo.asset.InspectionUserVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InspectionService {

    /**
     * 新增或编辑巡检项配置数据
     * @param inspectionItemVo 巡检项配置数据
     * @param iconFile 图标文件
     * @return 状态码
     */
    ResponseResult<Void> saveInspectionItem(InspectionItemVo inspectionItemVo, MultipartFile iconFile);

    /**
     * 批量删除巡检项配置数据
     * @param ids 多个巡检项配置主键id
     * @return 状态码
     */
    ResponseResult<Void> deleteAllInspectionItemByIds(List<String> ids);

    /**
     * 查询巡检项配置数据列表
     * @param siteId 站点id
     * @param name 名称
     * @param page 页码
     * @param size 页大小
     * @return 状态码
     */
    ResponseResult<PageDto<InspectionItemListDto>> queryInspectionItemList(String siteId, String name, Integer page, Integer size);

    /**
     * 导入巡检项配置数据
     * @param siteId 站点id
     * @param itemFile 巡检项配置文件
     * @return 重复巡检项名称列表
     */
    ResponseResult<List<String>> importInspectionItem(String siteId, MultipartFile itemFile);

    /**
     * 获取节点人员设置列表
     * @param tenantId 租户id
     * @return 节点人员设置列表
     */
    ResponseResult<List<InspectionUserListDto>> getInspectionUserList(String tenantId);

    /**
     * 新增或编辑节点人员设置数据
     * @param inspectionUserVo 节点人员设置数据
     * @return 状态码
     */
    ResponseResult<Void> saveInspectionUser(InspectionUserVo inspectionUserVo);

    /**
     * 查询巡检任务列表
     * @param taskQueryVo 查询参数
     * @return 巡检任务数据列表
     */
    ResponseResult<PageDto<InspectionTaskListDto>> queryInspectionTaskList(InspectionTaskQueryVo taskQueryVo);

    /**
     * 新增或编辑巡检任务数据
     * @param inspectionTaskVo 巡检任务新增参数
     * @return 状态码
     */
    ResponseResult<Void> saveInspectionTask(InspectionTaskSaveVo inspectionTaskVo);

    /**
     * 获取新增任务站点列表
     * @param userId 用户id
     * @param siteName 站点名称
     * @param page 当前页
     * @param size 当前页条数
     * @return 状态码
     */
    ResponseResult<PageDto<SiteSaveTaskListDto>> getSiteSaveTaskList(String userId, String siteName, Integer page, Integer size);

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
     * 批量删除巡检任务
     * @param ids 多个巡检任务id
     * @return 状态码
     */
    ResponseResult<Void> deleteInspectionTaskByIds(List<String> ids);

    /**
     * 根据巡检站点主键id查询巡检站点详情数据
     * @param inspectionSiteId 巡检站点主键id
     * @return 巡检站点详情数据
     */
    ResponseResult<InspectionSiteListDto> findInspectionSiteListById(String inspectionSiteId);

    /**
     * 根据用户id和节点类型查询巡检站点详情数据
     * @param userId 用户id
     * @param type 节点类型 0-创建任务 1-启动巡检 2-现场巡检 3-巡检结果确认
     * @return 巡检人员信息
     */
    ResponseResult<List<InspectionUserNameDto>> findInspectionUserList(String userId, Integer type);

}
