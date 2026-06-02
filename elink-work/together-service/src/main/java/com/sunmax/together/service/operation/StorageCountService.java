package com.sunmax.together.service.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.storageCount.MeterListDto;
import com.sunmax.together.dto.operation.storageCount.StorageIncomeCountDto;
import com.sunmax.together.dto.operation.storageCount.StorageKwhIncomeDto;
import com.sunmax.together.dto.operation.storageCount.StorageQtCountDto;
import com.sunmax.together.vo.operation.storageCount.StorageCountVo;

import java.util.List;

public interface StorageCountService {

    /**
     * 根据站点id查询并网点电表数据
     * @param siteId 站点id
     * @return 并网点电表数据
     */
    ResponseResult<List<MeterListDto>> getMeterListBySiteId(String siteId);

    /**
     * 统计储能充放电量
     * @param storageCountVo 储能统计入参
     * @return 储能充放电量数据
     */
    ResponseResult<StorageQtCountDto> countStorageQt(StorageCountVo storageCountVo);

    /**
     * 统计储能收益分析
     * @param storageCountVo 储能统计入参
     * @return 储能收益分析数据
     */
    ResponseResult<StorageIncomeCountDto> countStorageIncome(StorageCountVo storageCountVo);

    /**
     * 统计储能度电收益
     * @param storageCountVo 储能统计入参
     * @return 储能度电收益数据
     */
    ResponseResult<StorageKwhIncomeDto> countStorageKwhIncome(StorageCountVo storageCountVo);
}
