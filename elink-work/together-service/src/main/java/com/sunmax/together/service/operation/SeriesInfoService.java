package com.sunmax.together.service.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.seriesInfo.ModuleLibraryDto;
import com.sunmax.together.dto.operation.seriesInfo.SeriesConfigInfoDto;
import com.sunmax.together.dto.operation.seriesInfo.SeriesDeviceListDto;
import com.sunmax.together.vo.operation.seriesInfo.ModuleLibraryChangeVo;
import com.sunmax.together.vo.operation.seriesInfo.ModuleLibraryQueryVo;
import com.sunmax.together.vo.operation.seriesInfo.SeriesConfigChangeVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SeriesInfoService {

    /**
     * 新增或编辑组件库信息
     *
     * @param moduleChangeVo
     * @param userId
     * @return
     */
    ResponseResult<String> saveOrUpdateModuleLibrary(ModuleLibraryChangeVo moduleChangeVo, String userId);

    /**
     * 批量删除组件库信息
     * @param ids
     * @return
     */
    ResponseResult<String> batchDeleteModuleLibrary(String ids);

    /**
     * 查询组件库列表
     * @param moduleLibraryQueryVo
     * @return
     */
    ResponseResult<?> findModuleLibraryList(ModuleLibraryQueryVo moduleLibraryQueryVo);

    /**
     * 导入组件库列表数据
     * @param userId
     * @param file
     * @return
     */
    ResponseResult<String> importModuleLibraryList(String userId, MultipartFile file);

    /**
     * 查询组件厂家列表
     * @return
     */
    ResponseResult<List<String>> findModuleFactoryList();

    /**
     * 查询组件型号列表
     * @param moduleFactory 组件厂家
     * @return
     */
    ResponseResult<List<ModuleLibraryDto>> findModuleModelList(String moduleFactory);

    /**
     * 保存组件配置列表
     *
     * @param deviceId
     * @param seriesConfigChangeVos
     * @return
     */
    ResponseResult<String> saveSeriesConfigList(String deviceId, List<SeriesConfigChangeVo> seriesConfigChangeVos);

    /**
     * 根据设备id查询组串配置列表
     * @param deviceId
     * @return
     */
    ResponseResult<List<SeriesConfigInfoDto>> findSeriesConfigInfo(String deviceId);

    /**
     * 根据多个设备id清除组串配置列表
     * @param deviceIdList
     * @return
     */
    ResponseResult<String> purgeSeriesConfigById(List<String> deviceIdList);

    /**
     * 根据站点id查询逆变器设备列表
     * @param siteId
     * @param configStatus
     * @param equipmentModel
     * @return
     */
    ResponseResult<List<SeriesDeviceListDto>> findInverterDeviceList(String siteId, Integer configStatus, String equipmentModel);

    /**
     * 根据站点id查询逆变器设备型号列表
     * @param siteId
     * @return
     */
    ResponseResult<List<String>> findInverterDeviceModelList(String siteId);
}
