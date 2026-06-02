package com.sunmax.crontab.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.crontab.dto.DataQueryDto;
import com.sunmax.crontab.vo.DataQueryVo;

public interface DataQueryService {

    /**
     * 查询图标数据
     * @param dataQueryVo
     * @return
     */
    ResponseResult<DataQueryDto> findDataQueryList(DataQueryVo dataQueryVo);
}
