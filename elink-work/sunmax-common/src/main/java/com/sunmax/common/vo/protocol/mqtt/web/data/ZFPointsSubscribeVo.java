package com.sunmax.common.vo.protocol.mqtt.web.data;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 转发数据表
 */
@Data
public class ZFPointsSubscribeVo implements Serializable {

    /**
     * 点表数组字符串列表
     */
    private List<PointVo> iegGroupList = new ArrayList<>();

    /**
     * 点表单个字段列表
     */
    private List<PointVo> iegPointList = new ArrayList<>();

    @Data
    public static class PointVo implements Serializable {

        /**
         * 点号
         */
        private Integer pId;

        /**
         * 数值
         */
        private Object val;

        /**
         * 数值类型 0-无 1-遥测(浮点型) 2-遥信(整形) 3-遥控(暂无) 4-遥调(暂无)
         */
        private Integer appType;

    }


}
