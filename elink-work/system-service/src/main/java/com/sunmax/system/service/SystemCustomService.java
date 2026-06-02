package com.sunmax.system.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.dto.CustomChangeDto;
import com.sunmax.system.vo.CustomChangeVo;
import org.springframework.web.multipart.MultipartFile;

public interface SystemCustomService {

    /**
     * 新增或编辑系统自定义数据
     * @param customChangeVo 自定义编辑数据
     * @param logoFile logo文件
     * @return 状态码
     */
    ResponseResult<Void> saveSystemCustom(CustomChangeVo customChangeVo, MultipartFile logoFile);

    /**
     * 查询系统自定义数据
     * @return 自定义数据
     */
    ResponseResult<CustomChangeDto> querySystemCustom();

}
