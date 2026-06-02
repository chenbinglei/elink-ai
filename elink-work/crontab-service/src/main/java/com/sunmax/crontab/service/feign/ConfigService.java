package com.sunmax.crontab.service.feign;

import com.sunmax.common.dto.configure.DeviceVariableDto;
import com.sunmax.common.dto.configure.PowerControlResDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.PowerControlParamVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "configure-service")
@RestController
@RequestMapping("/configure/feign/crontab")
public interface ConfigService {

    @PostMapping("interflowPowerControl")
    @ApiOperation("城市充电功率控制")
    @ApiOperationSupport(order = 1)
    ResponseResult<String> interflowPowerControl(@RequestParam String pileCode, @RequestParam Integer gunCode, @RequestParam Double outPower);

    @PostMapping("interflowBatchPowerControl")
    @ApiOperation("城市充电批量功率控制")
    @ApiOperationSupport(order = 2)
    ResponseResult<List<PowerControlResDto>> interflowBatchPowerControl(@RequestBody List<PowerControlParamVo> powerControlParamVos);

    @PostMapping("findAllVariableByDomainId")
    @ApiOperation("根据域名id查询所有变量标识")
    @ApiOperationSupport(order = 3)
    ResponseResult<DeviceVariableDto> findAllVariableByDomainId(@RequestParam String domainId);
}
