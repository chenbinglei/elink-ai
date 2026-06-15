package com.sunmax.common.vo.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "设备事件编辑参数实体类")
public class DeviceEventChangeVo {

    /**
     * 关联设备id
     */
    @Schema(description = "关联设备id")
    private String deviceId;

    /**
     * 关联模型事件id
     */
    @Schema(description = "关联模型事件id")
    private String eventId;

    /**
     * 事件来源
     */
    @Schema(description = "事件来源")
    private String eventSource;

    /**
     * 事件状态 0-未恢复 1-已修复
     */
    @Schema(description = "事件状态 0-未恢复 1-已修复")
    private Integer eventStatus;

}
