package com.sunmax.together.controller.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.protocol.ProtocolListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.ProtocolQueryVo;
import com.sunmax.together.service.feign.ProtocolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("pileLog")
@Api(tags = "电桩日志控制层")
public class PileLogController {

    @Autowired
    private ProtocolService protocolService;

    @PostMapping("queryProtocolList")
    @ApiOperation("查询协议日志列表")
    @ApiOperationSupport(order = 1)
    public ResponseResult<PageDto<ProtocolListDto>> queryProtocolList(ProtocolQueryVo protocolQueryVo) {
        return protocolService.queryProtocolList(protocolQueryVo);
    }

}
