package com.sunmax.together.service.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.PileGunChangeVo;
import com.sunmax.together.dto.operation.runScene.PileDetailDto;
import com.sunmax.together.dto.operation.runScene.SitePileMonitorDto;
import com.sunmax.together.vo.operation.runScene.SitePileMonitorQueryVo;

public interface RunSceneService {

    /**
     * 统计站点电桩监视数据
     * @param sitePileQueryVo 站点监视查询条件
     * @return 站点监视数据
     */
    ResponseResult<SitePileMonitorDto> countSitePileMonitor(SitePileMonitorQueryVo sitePileQueryVo);

    /**
     * 根据电桩id查询电桩详情信息
     * @param id 电桩id
     * @return 电桩详情信息
     */
    ResponseResult<PileDetailDto> findPileDetailById(String id);

    /**
     * 编辑电桩扩展属性数据
     * @param id 电桩唯一id
     * @param userId 用户id
     * @param readwriteObject 读写对象数据
     * @return 状态码
     */
    ResponseResult<Void> updatePileRea(String id, String userId, String readwriteObject);

    /**
     * 编辑电枪数据
     * @param pileGunChangeVo 电枪数据
     * @return 状态码
     */
    ResponseResult<Void> updatePileGun(PileGunChangeVo pileGunChangeVo);

}
