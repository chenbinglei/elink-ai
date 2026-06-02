package com.sunmax.crontab.dto;

import com.sunmax.common.vo.crontab.StrategyTaskVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HdPowerCtrlDto {

    /**
     * 策略任务
     */
    private StrategyTaskVo strategyTask;

    /**
     * 来源类型 1-晟曼接入 2-城市充电接入
     */
    private Integer sourceType;

    /**
     * 调控入参
     */
    private String params;

    /**
     * 调控出参
     */
    private String result;

}
