package com.sunmax.common.dto.together.ops;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "巡检站点数据返回实体类")
public class InspectionSiteListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 附件路径
     */
    @Schema(description = "附件路径")
    private String annexPath;

    /**
     * 巡检项状态列表
     */
    @Schema(description = "巡检项状态列表")
    private List<ItemState> itemStateList = Lists.newArrayList();

    @Data
    @Schema(description = "巡检项状态列表返回实体类")
    public static class ItemState {

        /**
         * 巡检项id
         */
        @Schema(description = "巡检项id")
        private String itemId;

        /**
         * 巡检项名称
         */
        @Schema(description = "巡检项名称")
        private String itemName;

        /**
         * 图标地址
         */
        @Schema(description = "图标地址")
        private String iconPath;

        /**
         * 巡检项检查状态 1-未检查 2-正常 3-异常
         */
        @Schema(description = "巡检项检查状态 1-未检查 2-正常 3-异常")
        private Integer itemState;

    }

}
