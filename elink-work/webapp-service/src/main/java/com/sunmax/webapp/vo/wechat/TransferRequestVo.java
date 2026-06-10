package com.sunmax.webapp.vo.wechat;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema(description = "微信商户转账参数实体类")
public class TransferRequestVo {

    @SerializedName("appid")
    @Schema(description = "商户id")
    private String appid;

    @SerializedName("out_bill_no")
    @Schema(description = "商户订单号")
    private String outBillNo;

    @SerializedName("transfer_scene_id")
    @Schema(description = "转账场景id")
    private String transferSceneId;

    @SerializedName("openid")
    @Schema(description = "收款用户OpenId")
    private String openid;

    @SerializedName("user_name")
    @Schema(description = "收款用户姓名")
    private String userName;

    @SerializedName("transfer_amount")
    @Schema(description = "转账金额")
    private Long transferAmount;

    @SerializedName("transfer_remark")
    @Schema(description = "转账备注")
    private String transferRemark;

    @SerializedName("notify_url")
    @Schema(description = "转账结果通知地址")
    private String notifyUrl;

    @SerializedName("user_recv_perception")
    @Schema(description = "用户收款感知")
    private String userRecvPerception;

    @SerializedName("transfer_scene_report_infos")
    @Schema(description = "转账场景报备信息")
    public List<SceneReportInfo> sceneReportInfos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SceneReportInfo {

        @SerializedName("info_type")
        @Schema(description = "信息类型")
        public String infoType;

        @SerializedName("info_content")
        @Schema(description = "信息内容")
        public String infoContent;
    }

}
