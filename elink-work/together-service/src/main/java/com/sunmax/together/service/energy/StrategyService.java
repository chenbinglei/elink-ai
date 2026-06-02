package com.sunmax.together.service.energy;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.StrategyTaskVo;
import com.sunmax.together.dto.energy.GatewayDataDto;
import com.sunmax.together.dto.energy.StrategyDataDto;
import com.sunmax.together.dto.energy.StrategyListDto;
import com.sunmax.together.vo.energy.StrategySaveVo;
import com.sunmax.together.vo.energy.StrategyUpdateVo;
import com.sunmax.together.dto.energy.TemplateListDto;
import com.sunmax.together.vo.energy.TemplateChangeVo;
import com.sunmax.together.vo.energy.TemplateQueryVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StrategyService {

    /**
     * 新增或编辑策略模板数据
     * @param templateChangeVo 策略模板参数
     * @param explainFile 策略说明文件
     * @param configFile 配置文件
     * @return 状态码
     */
    ResponseResult<Void> saveOrUpdateTemplate(TemplateChangeVo templateChangeVo, MultipartFile explainFile, MultipartFile configFile);

    /**
     * 查询策略模板列表数据
     * @param templateQueryVo 策略模板查询条件
     * @return 策略模板数据
     */
    ResponseResult<PageDto<TemplateListDto>> queryTemplateList(TemplateQueryVo templateQueryVo);

    /**
     * 根据主键id批量删除策略模板数据
     * @param id 主键id
     * @return 状态码
     */
    ResponseResult<Void> deleteTemplateById(String id);

    /**
     * 查看策略模板文件内容
     * @param id 主键id
     * @param type 类型 1-策略说明 2-配置文件
     * @return 字节内容
     */
    ResponseResult<String> parseTemplateContent(String id, Integer type);

    /**
     * 根据id查询策略模板详情
     * @param id 主键id
     * @return 策略模板详情
     */
    ResponseResult<TemplateListDto> findTemplateById(String id);

    /**
     * 根据站点id查询网关数据
     * @param siteId 站点id
     * @return 状态码
     */
    ResponseResult<List<GatewayDataDto>> findGatewayDataBySiteId(String siteId);

    /**
     * 新增策略数据
     * @param strategySaveVo 策略数据
     * @return 状态码
     */
    ResponseResult<Void> saveStrategy(StrategySaveVo strategySaveVo);

    /**
     * 编辑策略数据
     * @param strategyUpdateVo 策略数据
     * @param configFile 配置文件
     * @return 状态码
     */
    ResponseResult<Void> updateStrategy(StrategyUpdateVo strategyUpdateVo, MultipartFile configFile);

    /**
     * 根据站点id和设备id查询策略列表数据
     * @param siteId 站点id
     * @param deviceId 设备id
     * @return 策略列表数据
     */
    ResponseResult<List<StrategyListDto>> queryStrategyList(String siteId, String deviceId);

    /**
     * 根据策略id和类型查询策略配置
     * @param id 策略id
     * @param type 类型 1-保存数据 2-读取数据
     * @return 策略配置数据
     */
    ResponseResult<StrategyDataDto> findStrategyById(String id, Integer type);

    /**
     * 根据策略id删除策略数据
     * @param id 策略id
     * @return 状态码
     */
    ResponseResult<Void> deleteStrategyById(String id);

    /**
     * 克隆策略数据
     * @param id 策略id
     * @param siteId 站点id
     * @param deviceId 设备id
     * @param strategyType 策略类型 1-边缘网关 2-云网关 3-云平台
     * @return 状态码
     */
    ResponseResult<Void> cloneStrategy(String id, String siteId, String deviceId, Integer strategyType);

    /**
     * 配置下发
     * @param id 策略id
     * @return 状态码
     */
    ResponseResult<Void> issuedStrategy(String id);

    /**
     * 查询平台进行中的自动策略任务列表
     * @param siteId 站点id
     * @return 执行中的自动策略任务列表
     */
    ResponseResult<List<StrategyTaskVo>> findStrategyHandTaskList(String siteId);

}
