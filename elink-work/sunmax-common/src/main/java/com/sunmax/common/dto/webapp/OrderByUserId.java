package com.sunmax.common.dto.webapp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Schema(description = "订单返回实体类")
public class OrderByUserId {
    @Schema(description = "订单编号")
    private String orderId;
    @Schema(description = "电量")
    private BigDecimal chargeElectricity;
    @Schema(description = "订单推送时间，创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;
    @Schema(description = "订单金额")
    private BigDecimal orderAmount;
}
