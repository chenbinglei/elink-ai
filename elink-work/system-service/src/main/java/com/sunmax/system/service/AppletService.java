package com.sunmax.system.service;

import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.dto.AppletDetailDto;
import com.sunmax.system.dto.AppletListDto;
import com.sunmax.system.vo.AppletChangeVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AppletService {

    /**
     * 新增小程序数据
     * @param appletChangeVo 小程序编辑参数
     * @return 状态码
     */
    ResponseResult<Void> saveApplet(AppletChangeVo appletChangeVo, MultipartFile appletLogo, MultipartFile tencentImage);

    /**
     * 查询小程序列表数据
     * @param keyword 关键字
     * @return 小程序列表数据
     */
    ResponseResult<List<AppletListDto>> queryAppletList(String keyword);

    /**
     * 根据主键id查询小程序详情数据
     * @param id 主键id
     * @return 小程序详情数据
     */
    ResponseResult<AppletDetailDto> findAppletDetailById(String id);

    /**
     * 根据小程序id查询小程序数据
     * @param id 小程序主键id
     * @return 小程序数据
     */
    ResponseResult<AppletDto> findAppletById(String id);

    /**
     * 根据小程序id查询小程序数据
     * @param appletCode 小程序id
     * @return 小程序数据
     */
    ResponseResult<AppletDto> findAppletByAppletCode(String appletCode);

    /**
     * 根据小程序id和小程序类型查询小程序数据
     * @param appletCode 小程序id
     * @param appletType 小程序类型 1-微信 2-支付宝
     * @return
     */
    ResponseResult<AppletDto> findByAppletCodeAndAppletType(String appletCode, Integer appletType);

    /**
     * 根据多个小程序主键id查询小程序数据
     * @param appletIds 多个小程序主键id
     * @return 小程序数据
     */
    ResponseResult<Map<String, AppletDto>> findAppletListByIds(Set<String> appletIds);

}
