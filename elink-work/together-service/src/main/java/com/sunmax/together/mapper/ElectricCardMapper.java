package com.sunmax.together.mapper;

import com.sunmax.together.model.ElectricCardModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ElectricCardMapper {

    /**
     * 根据电卡条件查询电卡信息
     * @param id 电卡ID（可选）
     * @param cardHolder 持卡人（可选）
     * @param cardNumber 卡面号（可选）
     * @param startDate 创建时间开始（字符串，格式如 "2025-03-20T16:32:05"）
     * @param endDate 创建时间结束（字符串，格式如 "2025-03-21T16:32:05"）
     * @return 返回符合条件的电卡列表
     */
    List<ElectricCardModel> queryElectricCards(@Param("electricCardId") String id,
                                               @Param("cardHolder") String cardHolder,
                                               @Param("cardNumber") String cardNumber,
                                               @Param("startDate") String startDate,
                                               @Param("endDate") String endDate);
}
