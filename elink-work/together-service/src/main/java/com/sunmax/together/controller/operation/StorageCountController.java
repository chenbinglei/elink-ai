package com.sunmax.together.controller.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.storageCount.MeterListDto;
import com.sunmax.together.dto.operation.storageCount.StorageIncomeCountDto;
import com.sunmax.together.dto.operation.storageCount.StorageKwhIncomeDto;
import com.sunmax.together.dto.operation.storageCount.StorageQtCountDto;
import com.sunmax.together.service.operation.StorageCountService;
import com.sunmax.together.vo.operation.storageCount.StorageCountVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("storageCount")
@Tag(name = "储能生产统计管理")
public class StorageCountController {

    @Autowired
    private StorageCountService storageCountService;

    @PostMapping("getMeterListBySiteId")
    @Operation(summary = "根据站点id查询并网点电表数据")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<List<MeterListDto>> getMeterListBySiteId(String siteId) {
        return storageCountService.getMeterListBySiteId(siteId);
    }

    @PostMapping("countStorageQt")
    @Operation(summary = "统计储能充放电量")
    
    public ResponseResult<StorageQtCountDto> countStorageQt(StorageCountVo storageCountVo) {
        return storageCountService.countStorageQt(storageCountVo);
    }

    @PostMapping("countStorageIncome")
    @Operation(summary = "统计储能收益分析")
    
    public ResponseResult<StorageIncomeCountDto> countStorageIncome(StorageCountVo storageCountVo) {
        return storageCountService.countStorageIncome(storageCountVo);
    }

    @PostMapping("countStorageKwhIncome")
    @Operation(summary = "统计储能度电收益")
    
    public ResponseResult<StorageKwhIncomeDto> countStorageKwhIncome(StorageCountVo storageCountVo) {
        return storageCountService.countStorageKwhIncome(storageCountVo);
    }

}
