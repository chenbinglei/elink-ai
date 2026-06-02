package com.sunmax.crontab.controller;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.crontab.dto.NodeAddRecordListDto;
import com.sunmax.crontab.service.AddRecordTaskService;
import com.sunmax.crontab.service.ComputeNodeService;
import com.sunmax.crontab.service.NodeAddRecordService;
import com.sunmax.crontab.vo.NodeAddRecordChangeVo;
import com.sunmax.crontab.vo.NodeAddRecordQueryVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释: 节点数据补录管理
 */
@RestController
@CrossOrigin
@RequestMapping("nodeAddRecord")
@Api(tags = "数据补录管理")
public class NodeAddRecordController {

    @Autowired
    private NodeAddRecordService nodeAddRecordService;

    @Resource
    private ComputeNodeService computeNodeService;

    @Resource
    private AddRecordTaskService addRecordTaskService;

    @PostMapping("saveNodeAddRecord")
    @ApiOperation("添加数据补录信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<String> saveNodeAddRecord(NodeAddRecordChangeVo nodeAddRecordChangeVo) {
        return nodeAddRecordService.saveNodeAddRecord(nodeAddRecordChangeVo);
    }

    @PostMapping("findNodeListByDeviceId")
    @ApiOperation("根据设备id查询计算节点列表")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "所选设备/站点id", dataType = "String", required = true)
    })
    public ResponseResult<List<ComputeNodeListDto>> findNodeListByDeviceId(String deviceId) {
        return computeNodeService.findNodeListByDeviceId(deviceId);
    }

    @PostMapping("findNodeAddRecordListByPage")
    @ApiOperation("分页查询数据补录信息")
    @ApiOperationSupport(order = 3)
    public ResponseResult<PageDto<NodeAddRecordListDto>> findNodeAddRecordListByPage(NodeAddRecordQueryVo nodeAddRecordQueryVo) {
        return nodeAddRecordService.findNodeAddRecordListByPage(nodeAddRecordQueryVo);
    }

    @PostMapping("addRecordNodeDataById")
    @ApiOperation("根据节点id和时间补录taos数据")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataType = "String", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", required = true),
            @ApiImplicitParam(name = "addRecordId", value = "补录id", dataType = "String", required = true)
    })
    public ResponseResult<String> addRecordNodeDataById(String nodeId, String startTime, String endTime, String addRecordId) {
        return addRecordTaskService.addRecordNodeDataById(nodeId, startTime, endTime, addRecordId);
    }
}
