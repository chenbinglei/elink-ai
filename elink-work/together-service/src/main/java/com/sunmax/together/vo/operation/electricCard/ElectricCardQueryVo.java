package com.sunmax.together.vo.operation.electricCard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电卡查询参数")
public class ElectricCardQueryVo {
    /**
     * 电卡 ID
     */
    @Schema(description = "电卡ID")
    private String id;    // 电卡 ID
    /**
     * 持卡人
     */
    @Schema(description = "持卡人")
    private String cardHolder;        // 持卡人
    /**
     * 卡面号
     */
    @Schema(description = "卡面号")
    private String cardNumber;        // 卡面号
    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startDate;
    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endDate;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

}
