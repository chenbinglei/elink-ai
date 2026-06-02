package com.sunmax.together.controller.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.PileGunChangeVo;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.operation.runScene.PileDetailDto;
import com.sunmax.together.dto.operation.runScene.SitePileMonitorDto;
import com.sunmax.together.service.operation.RunSceneService;
import com.sunmax.together.vo.operation.runScene.SitePileMonitorQueryVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("runScene")
@Api(tags = "运行实况")
public class RunSceneController {

    @Autowired
    private RunSceneService runSceneService;

    @PostMapping("countSitePileMonitor")
    @ApiOperation("统计站点电桩监视数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<SitePileMonitorDto> countSitePileMonitor(SitePileMonitorQueryVo sitePileMonitorQueryVo) {
        return runSceneService.countSitePileMonitor(sitePileMonitorQueryVo);
    }

    @PostMapping("findPileDetailById")
    @ApiOperation("根据id查询电桩详情信息")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParam(name = "id", value = "电桩唯一id", paramType = "query", required = true)
    public ResponseResult<PileDetailDto> findPileDetailById(String id) {
        return runSceneService.findPileDetailById(id);
    }

    @PostMapping("updatePileRea")
    @ApiOperation("编辑电桩扩展属性数据")
    @WebLog("运行实况-编辑电桩扩展属性数据")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "电桩唯一id", required = true),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),
            @ApiImplicitParam(name = "readwriteObject", value = "读写对象数据", required = true)
    })
    public ResponseResult<Void> updatePileRea(String id, String userId, String readwriteObject) {
        return runSceneService.updatePileRea(id, userId, readwriteObject);
    }

    @PostMapping("updatePileGun")
    @ApiOperation("编辑电枪数据")
    @WebLog("运行实况-编辑电枪数据")
    @ApiOperationSupport(order = 4)
    public ResponseResult<Void> updatePileGun(PileGunChangeVo pileGunChangeVo) {
        return runSceneService.updatePileGun(pileGunChangeVo);
    }

}
