package com.sunmax.crontab.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.crontab.dto.NodeAddRecordListDto;
import com.sunmax.crontab.service.AddRecordTaskService;
import com.sunmax.crontab.service.ComputeNodeService;
import com.sunmax.crontab.service.NodeAddRecordService;
import com.sunmax.crontab.vo.NodeAddRecordChangeVo;
import com.sunmax.crontab.vo.NodeAddRecordQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 节点数据补录管理
 */
@RestController
@CrossOrigin
@RequestMapping("nodeAddRecord")
@Tag(name = "数据补录管理")
public class NodeAddRecordController {

    @Autowired
    private NodeAddRecordService nodeAddRecordService;

    @Resource
    private ComputeNodeService computeNodeService;

    @Resource
    private AddRecordTaskService addRecordTaskService;

    @PostMapping("saveNodeAddRecord")
    @Operation(summary = "添加数据补录信息")
    
    public ResponseResult<String> saveNodeAddRecord(NodeAddRecordChangeVo nodeAddRecordChangeVo) {
        return nodeAddRecordService.saveNodeAddRecord(nodeAddRecordChangeVo);
    }

    @PostMapping("findNodeListByDeviceId")
    @Operation(summary = "根据设备id查询计算节点列表")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "所选设备/站点id")
    })
    public ResponseResult<List<ComputeNodeListDto>> findNodeListByDeviceId(String deviceId) {
        return computeNodeService.findNodeListByDeviceId(deviceId);
    }

    @PostMapping("findNodeAddRecordListByPage")
    @Operation(summary = "分页查询数据补录信息")
    
    public ResponseResult<PageDto<NodeAddRecordListDto>> findNodeAddRecordListByPage(NodeAddRecordQueryVo nodeAddRecordQueryVo) {
        return nodeAddRecordService.findNodeAddRecordListByPage(nodeAddRecordQueryVo);
    }

    @PostMapping("addRecordNodeDataById")
    @Operation(summary = "根据节点id和时间补录taos数据")
    
    @Parameters({
            @Parameter(name = "nodeId", description = "节点id"),
            @Parameter(name = "startTime", description = "开始时间"),
            @Parameter(name = "endTime", description = "结束时间"),
            @Parameter(name = "addRecordId", description = "补录id")
    })
    public ResponseResult<String> addRecordNodeDataById(String nodeId, String startTime, String endTime, String addRecordId) {
        return addRecordTaskService.addRecordNodeDataById(nodeId, startTime, endTime, addRecordId);
    }
}
