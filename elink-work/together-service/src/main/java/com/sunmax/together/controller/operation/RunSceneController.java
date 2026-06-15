package com.sunmax.together.controller.operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.PileGunChangeVo;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.operation.runScene.PileDetailDto;
import com.sunmax.together.dto.operation.runScene.SitePileMonitorDto;
import com.sunmax.together.service.operation.RunSceneService;
import com.sunmax.together.vo.operation.runScene.SitePileMonitorQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("runScene")
@Tag(name = "运行实况")
public class RunSceneController {

    @Autowired
    private RunSceneService runSceneService;

    @PostMapping("countSitePileMonitor")
    @Operation(summary = "统计站点电桩监视数据")
    
    public ResponseResult<SitePileMonitorDto> countSitePileMonitor(SitePileMonitorQueryVo sitePileMonitorQueryVo) {
        return runSceneService.countSitePileMonitor(sitePileMonitorQueryVo);
    }

    @PostMapping("findPileDetailById")
    @Operation(summary = "根据id查询电桩详情信息")
    
    @Parameter(name = "id", description = "电桩唯一id")
    public ResponseResult<PileDetailDto> findPileDetailById(String id) {
        return runSceneService.findPileDetailById(id);
    }

    @PostMapping("updatePileRea")
    @Operation(summary = "编辑电桩扩展属性数据")
    @WebLog("运行实况-编辑电桩扩展属性数据")
    
    @Parameters({
            @Parameter(name = "id", description = "电桩唯一id"),
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "readwriteObject", description = "读写对象数据")
    })
    public ResponseResult<Void> updatePileRea(String id, String userId, String readwriteObject) {
        return runSceneService.updatePileRea(id, userId, readwriteObject);
    }

    @PostMapping("updatePileGun")
    @Operation(summary = "编辑电枪数据")
    @WebLog("运行实况-编辑电枪数据")
    
    public ResponseResult<Void> updatePileGun(PileGunChangeVo pileGunChangeVo) {
        return runSceneService.updatePileGun(pileGunChangeVo);
    }

}
