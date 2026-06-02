package com.sunmax.common.dto.together.ops;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "SiteItemListDto", description = "巡检站点数据返回实体类")
public class InspectionSiteListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 附件路径
     */
    @ApiModelProperty("附件路径")
    private String annexPath;

    /**
     * 巡检项状态列表
     */
    @ApiModelProperty(value = "巡检项状态列表")
    private List<ItemState> itemStateList = Lists.newArrayList();

    @Data
    @ApiModel(value = "ItemState", description = "巡检项状态列表返回实体类")
    public static class ItemState {

        /**
         * 巡检项id
         */
        @ApiModelProperty(value = "巡检项id")
        private String itemId;

        /**
         * 巡检项名称
         */
        @ApiModelProperty(value = "巡检项名称")
        private String itemName;

        /**
         * 图标地址
         */
        @ApiModelProperty(value = "图标地址")
        private String iconPath;

        /**
         * 巡检项检查状态 1-未检查 2-正常 3-异常
         */
        @ApiModelProperty(value = "巡检项检查状态 1-未检查 2-正常 3-异常")
        private Integer itemState;

    }

}
