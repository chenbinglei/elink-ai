package com.sunmax.log.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "日志查询条件实体类")
public class AccessLogQueryVo {

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    private String userAccount;

    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    private String startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    private String endDate;

    /**
     * 客户端id
     */
    @Schema(description = "客户端id")
    private String clientId;

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
