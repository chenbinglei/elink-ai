package com.sunmax.configure.service;

import com.sunmax.common.dto.configure.DeviceVariableDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.configure.dto.*;
import com.sunmax.configure.vo.GraphChangeVo;
import com.sunmax.configure.vo.GraphSourceChangeVo;
import com.sunmax.configure.vo.GraphVariableChangeVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GraphService {

    /**
     * 新建或编辑图模数据
     * @param graphChangeVo 图模编辑参数
     * @param file 图模文件
     * @return 状态码
     */
    ResponseResult<String> saveGraph(GraphChangeVo graphChangeVo, MultipartFile file);

    /**
     * 根据多个id删除数据
     * @param ids 多个主键id
     * @return 状态码
     */
    ResponseResult<Void> deleteGraphByIds(List<String> ids);

    /**
     * 查询图模管理列表
     * @param name 名称
     * @return 图模管理数据
     */
    ResponseResult<List<GraphListDto>> queryGraphList(String name);

    /**
     * 根据图模id查询图模详情数据
     * @param id 图模id
     * @return 图模详情数据
     */
    ResponseResult<GraphDetailDto> findGraphById(String id);

    /**
     * 图模关联数据源
     * @param graphSourceChangeVo 图模数据源编辑参数
     * @return 状态码
     */
    ResponseResult<Void> relationDataSource(GraphSourceChangeVo graphSourceChangeVo);

    /**
     * 校验数据源是否调通
     * @param dataSourceId 数据源id
     * @param requestValue 请求数据
     * @return 响应数据参数
     */
    ResponseResult<String> checkDataSource(String dataSourceId, String requestValue);

    /**
     * 根据图模id查询关联数据源数据
     * @param graphId 图模id
     * @param name 名称
     * @return 数据源数据
     */
    ResponseResult<List<GraphSourceDto>> findGraphSourceByGraphId(String graphId, String name);

    /**
     * 编辑变量管理数据
     * @param graphVariableChangeVo 图模变量编辑参数
     * @return 状态码
     */
    ResponseResult<Void> saveGraphVariable(GraphVariableChangeVo graphVariableChangeVo);

    /**
     * 批量编辑图模变量数据
     * @param graphVariableChangeVos 图模变量数据
     * @return 状态码
     */
    ResponseResult<Void> saveAllGraphVariable(List<GraphVariableChangeVo> graphVariableChangeVos);

    /**
     * 根据多个id批量删除图模变量数据
     * @param ids 多个id
     * @return 状态码
     */
    ResponseResult<Void> deleteGraphVariableByIds(List<String> ids);

    /**
     * 根据图模id查询关联变量数据
     * @param graphId 图模id
     * @param keyword 关键字
     * @return 变量数据列表
     */
    ResponseResult<List<GraphVariableListDto>> findGraphVariableByGraphId(String graphId, String keyword);

    /**
     * 根据图模id查询图模接口数据
     * @param id 数据id
     * @param type 类型 1-图模id 2-域名id
     * @return 图模接口数据
     */
    ResponseResult<GraphDataDto> findGraphDataListByGraphId(String id, Integer type);

    /**
     * 克隆图模
     * @param id 图模id
     * @param userId 用户id
     * @param file 图模文件
     * @return 新图模id
     */
    ResponseResult<String> cloneGraph(String id, String userId, MultipartFile file);

    /**
     * 根据域名id查询所有变量标识
     * @param domainId 域名id
     * @return 设备id -> 多个变量标识
     */
    ResponseResult<DeviceVariableDto> findAllVariableByDomainId(String domainId);

    /**
     * 导入图模数据
     * @param userId 用户id
     * @param file 导入文件
     * @return 状态码
     */
    ResponseResult<Void> importGraph(String userId, MultipartFile file);

}
