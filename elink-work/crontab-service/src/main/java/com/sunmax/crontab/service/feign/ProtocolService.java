package com.sunmax.crontab.service.feign;

import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PilePowerCtrlVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@FeignClient(value = "sunos-protocol-service")
@RestController
@RequestMapping("/protocol/feign/crontab")
public interface ProtocolService {

    @PostMapping("batchPilePowerCtrl")
    @ApiOperation("批量对多个充电桩功率控制")
    @ApiOperationSupport(order = 1)
    ResponseResult<Void> batchPilePowerCtrl(@RequestBody List<PilePowerCtrlVo> pilePowerCtrlVos);

    @PostMapping("batchPilePowerCtrlResult")
    @ApiOperation("批量对多个充电桩功率控制(响应结果)")
    @ApiOperationSupport(order = 2)
    ResponseResult<List<PileResultDto>> batchPilePowerCtrlResult(@RequestBody List<PilePowerCtrlVo> pilePowerCtrlVos);

}
