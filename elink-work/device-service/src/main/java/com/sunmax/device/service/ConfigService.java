package com.sunmax.device.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.model.ConfigListDto;
import com.sunmax.device.vo.model.ConfigChangeVo;
import com.sunmax.device.vo.model.ConfigQueryVo;
import org.springframework.web.multipart.MultipartFile;

public interface ConfigService {

    /**
     * 新增或编辑参数配置
     * @param configChangeVo 参数配置编辑参数
     * @return 状态码
     */
    ResponseResult<Void> saveConfig(ConfigChangeVo configChangeVo);

    /**
     * 查询参数配置列表
     * @param configQueryVo 参数配置查询参数
     * @return 参数配置数据
     */
    ResponseResult<PageDto<ConfigListDto>> queryConfigList(ConfigQueryVo configQueryVo);

    /**
     * 删除参数配置
     * @param id 参数配置id
     * @return 状态码
     */
    ResponseResult<Void> deleteConfigById(String id);

    /**
     * 查询参数配置详情
     * @param id 参数配置id
     * @return 参数配置详情
     */
    ResponseResult<ConfigListDto> findConfigDetailById(String id);

    /**
     * 导入参数配置数据
     * @param file 导入文件
     * @return 导入结果
     */
    ResponseResult<ImportResultDto> importConfigList(MultipartFile file);

}
