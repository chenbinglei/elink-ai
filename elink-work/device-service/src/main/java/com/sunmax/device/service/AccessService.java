package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.ChannelInfoDto;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.SubDeviceFunctionDto;
import com.sunmax.device.dto.device.DeviceAccessDto;
import com.sunmax.device.dto.PointTableDto;
import com.sunmax.device.vo.ChannelChangeVo;
import com.sunmax.device.vo.PointTableChangeVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AccessService {

    /**
     * 根据设备id查询设备接入详情
     * @param deviceId 设备id
     * @return 设备接入详情
     */
    ResponseResult<DeviceAccessDto> findAccessDetailByDeviceId(String deviceId);

    /**
     * 新增或编辑通道数据
     * @param channelChangeVo 通道编辑参数
     * @return 状态码
     */
    ResponseResult<Void> saveChannel(ChannelChangeVo channelChangeVo);

    /**
     * 删除通道数据
     * @param channelId
     * @return
     */
    ResponseResult<Void> deleteChannelById(String deviceId, String channelId);

    /**
     * 根据设备id查询设备通道信息列表
     * @param deviceId 设备id
     * @return 设备通道信息
     */
    ResponseResult<List<ChannelInfoDto>> findChannelInfoListByDeviceId(String deviceId);

    /**
     * 设备注册注销
     * @param deviceId 设备id
     * @param updateType 更新类型 1-注册 2-注销
     * @return 状态码
     */
    ResponseResult<Void> updateDeviceStatus(String deviceId, Integer updateType);

    /**
     * 根据通道id查询点表数据列表
     * @param channelId 通道id
     * @return 点表数据列表
     */
    ResponseResult<List<PointTableDto>> findPointTableListByChannelId(String channelId);

    /**
     * 新增或编辑或删除点表数据
     * @param pointTableVos 多个点表数据[{点表数据1},{点表数据2}
     * @return 状态码
     */
    ResponseResult<Void> savePointTable(List<PointTableChangeVo> pointTableVos);

    /**
     * 根据网关设备id查询网关设备功能点列表
     * @param deviceId 设备id 标明:类型为1和2时传网关设备id,为3传查询设备id
     * @param type 类型 1-设备及功能点数据 2-设备数据 3-功能点数据
     * @return 网关设备功能点列表
     */
    ResponseResult<List<SubDeviceFunctionDto>> findSubDeviceFunctionListByDeviceId(String deviceId, Integer type);

    /**
     * 导入点表数据
     * @param pointTableFile 点表文件
     * @param channelId 通道id
     * @return 导入结果数据
     */
    ResponseResult<ImportResultDto> importPointTableData(String channelId, MultipartFile pointTableFile);

}
