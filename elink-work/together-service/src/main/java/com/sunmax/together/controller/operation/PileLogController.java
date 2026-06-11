package com.sunmax.together.controller.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.protocol.ProtocolListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.ProtocolQueryVo;
import com.sunmax.together.service.feign.ProtocolService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("pileLog")
@Tag(name = "电桩日志控制层")
public class PileLogController {

    @Autowired
    private ProtocolService protocolService;

    @PostMapping("queryProtocolList")
    @Operation(summary = "查询协议日志列表")
    
    public ResponseResult<PageDto<ProtocolListDto>> queryProtocolList(ProtocolQueryVo protocolQueryVo) {
        return protocolService.queryProtocolList(protocolQueryVo);
    }

}
