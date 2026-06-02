package com.sunmax.together.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.together.ElectConfigDto;
import com.sunmax.common.dto.together.ops.AppInspectHandTaskListDto;
import com.sunmax.common.dto.together.ops.AppInspectItemDto;
import com.sunmax.common.dto.together.ops.AppInspectSiteHistoryDto;
import com.sunmax.common.dto.together.ops.AppInspectTaskHistoryDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.AppInspectSiteVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface DevopsFeignService {

    /**
     * 根据用户id查询巡检进行中的任务列表
     * @param userId 用户id
     * @return 巡检进行中的任务列表
     */
    ResponseResult<List<AppInspectHandTaskListDto>> findAppInspectHandTaskList(String userId);

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
     * @param id 主键id
     * @param annexFile 附件
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

    /**
     * 根据多个站点id和类型查询站点电价配置数据
     * @param siteIds 站点id
     * @param moduleTypes 电价模块类型 1-电网电价 2-光伏上网电价 3-光伏消纳电价 4-储能售电电价 5-储能购电电价 6-电桩售电电价 7-电桩购电电价
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 站点id -> 站点电价配置数据列表
     */
    ResponseResult<Map<String, List<ElectConfigDto>>> findElectConfigListBySiteIds(List<String> siteIds,String moduleTypes,String startDate,String endDate);

}
