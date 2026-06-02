package com.sunmax.crontab.vo.mqtt;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 指令返回响应
 */
@Data
@ApiModel(value = "HDReplyVo", description = "指令返回响应实体类")
public class HDReplyVo {

    /**
     * 下挂设备的唯一标识，一般为序列号。例如负荷控制终端下挂断路器那么填写断路器的序列号，如果终端直接下挂智能设备（例如可接收modbus指令的空调），那么填写智能设备的唯一编码。
     */
    private String sn;

    /**
     * 消息id，用来唯一确认一条消息,字符串类型
     */
    private String mi;

    /**
     * 设备执行时间戳
     */
    private Long deviceExecutedTime;

    /**
     * 成功状态 1-成功 0-失败
     */
    private Integer successBit;

    /**
     * 功率控制执行结果
     */
    private Except except;

    /**
     * 下面为不同控制动作的返回值，每个控制动作的返回有可能不同
     */
    private Tags tags;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Except {

        /**
         * 0为false时msg描述错误原因,1为true，成功时可以为空字符串
         */
        private String msg;

    }

    @Data
    public static class Tags {

        /**
         * 开关动作之后开关的状态，open代表执行指令后开关当前为分闸状态，close当前为合闸状态
         */
        private String switchState;

    }

}
