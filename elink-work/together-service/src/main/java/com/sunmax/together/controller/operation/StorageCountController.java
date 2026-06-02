package com.sunmax.together.controller.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.storageCount.MeterListDto;
import com.sunmax.together.dto.operation.storageCount.StorageIncomeCountDto;
import com.sunmax.together.dto.operation.storageCount.StorageKwhIncomeDto;
import com.sunmax.together.dto.operation.storageCount.StorageQtCountDto;
import com.sunmax.together.service.operation.StorageCountService;
import com.sunmax.together.vo.operation.storageCount.StorageCountVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("storageCount")
@Api(tags = "储能生产统计管理")
public class StorageCountController {

    @Autowired
    private StorageCountService storageCountService;

    @PostMapping("getMeterListBySiteId")
    @ApiOperation("根据站点id查询并网点电表数据")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParam(name = "siteId", value = "站点id", dataType = "String", required = true)
    public ResponseResult<List<MeterListDto>> getMeterListBySiteId(String siteId) {
        return storageCountService.getMeterListBySiteId(siteId);
    }

    @PostMapping("countStorageQt")
    @ApiOperation("统计储能充放电量")
    @ApiOperationSupport(order = 2)
    public ResponseResult<StorageQtCountDto> countStorageQt(StorageCountVo storageCountVo) {
        return storageCountService.countStorageQt(storageCountVo);
    }

    @PostMapping("countStorageIncome")
    @ApiOperation("统计储能收益分析")
    @ApiOperationSupport(order = 3)
    public ResponseResult<StorageIncomeCountDto> countStorageIncome(StorageCountVo storageCountVo) {
        return storageCountService.countStorageIncome(storageCountVo);
    }

    @PostMapping("countStorageKwhIncome")
    @ApiOperation("统计储能度电收益")
    @ApiOperationSupport(order = 4)
    public ResponseResult<StorageKwhIncomeDto> countStorageKwhIncome(StorageCountVo storageCountVo) {
        return storageCountService.countStorageKwhIncome(storageCountVo);
    }

}
