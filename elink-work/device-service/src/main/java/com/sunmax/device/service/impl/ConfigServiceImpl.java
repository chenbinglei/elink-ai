package com.sunmax.device.service.impl;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.ImportResultDto;
import com.sunmax.device.dto.model.ConfigListDto;
import com.sunmax.device.service.ConfigService;
import com.sunmax.device.vo.model.ConfigChangeVo;
import com.sunmax.device.vo.model.ConfigQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ConfigServiceImpl implements ConfigService {

    @Override
    public ResponseResult<Void> saveConfig(ConfigChangeVo configChangeVo) {
        //新增参数配置

        return null;
    }

    @Override
    public ResponseResult<PageDto<ConfigListDto>> queryConfigList(ConfigQueryVo configQueryVo) {
        return null;
    }

    @Override
    public ResponseResult<Void> deleteConfigById(String id) {
        return null;
    }

    @Override
    public ResponseResult<ConfigListDto> findConfigDetailById(String id) {
        return null;
    }

    @Override
    public ResponseResult<ImportResultDto> importConfigList(MultipartFile file) {
        return null;
    }

}
