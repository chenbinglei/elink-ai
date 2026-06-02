package com.sunmax.protocol.mapper;

import com.sunmax.protocol.model.OrderSumModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderRecordMapper {

    //根据多个电桩编号和运行模式统计订单数据
    List<OrderSumModel> countPileOrderSumData(@Param("pileCodes") List<String> pileCodes, @Param("runMode") Integer runMode,
                                             @Param("startTime") String startTime, @Param("endTime") String endTime);

}
