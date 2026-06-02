package com.sunmax.configure.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.configure.dto.PelDetailDto;
import com.sunmax.configure.dto.PelListDto;
import com.sunmax.configure.vo.PelChangeVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PelService {

    /**
     * 新建或编辑图元数据
     * @param pelChangeVo 图元编辑数据
     * @param file 图元文件
     * @param dataFile 数据文件
     * @return 状态码
     */
    ResponseResult<Void> savePel(PelChangeVo pelChangeVo, MultipartFile file, MultipartFile dataFile);

    /**
     * 根据多个图元id删除图元数据
     * @param ids 多个图元id
     * @return 状态码
     */
    ResponseResult<Void> deletePelByIds(List<String> ids);

    /**
     * 查询图元管理列表
     * @param name 名称
     * @return 图元管理列表数据
     */
    ResponseResult<List<PelListDto>> queryPelList(String name);

    /**
     * 根据id查询图元详情数据
     * @param id 主键id
     * @return 图元详情数据
     */
    ResponseResult<PelDetailDto> findPelById(String id);

    /**
     * 查询所有图元详情数据
     * @return 图元详情数据
     */
    ResponseResult<List<PelDetailDto>> findPelList();

}
