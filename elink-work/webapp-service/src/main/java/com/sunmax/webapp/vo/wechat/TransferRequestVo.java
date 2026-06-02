package com.sunmax.webapp.vo.wechat;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel(value = "TransferRequestVo", description = "微信商户转账参数实体类")
public class TransferRequestVo {

    @SerializedName("appid")
    @ApiModelProperty(value = "商户id", required = true)
    private String appid;

    @SerializedName("out_bill_no")
    @ApiModelProperty(value = "商户订单号", required = true)
    private String outBillNo;

    @SerializedName("transfer_scene_id")
    @ApiModelProperty(value = "转账场景id", required = true)
    private String transferSceneId;

    @SerializedName("openid")
    @ApiModelProperty(value = "收款用户OpenId", required = true)
    private String openid;

    @SerializedName("user_name")
    @ApiModelProperty(value = "收款用户姓名")
    private String userName;

    @SerializedName("transfer_amount")
    @ApiModelProperty(value = "转账金额", required = true)
    private Long transferAmount;

    @SerializedName("transfer_remark")
    @ApiModelProperty(value = "转账备注", required = true)
    private String transferRemark;

    @SerializedName("notify_url")
    @ApiModelProperty(value = "转账结果通知地址")
    private String notifyUrl;

    @SerializedName("user_recv_perception")
    @ApiModelProperty(value = "用户收款感知")
    private String userRecvPerception;

    @SerializedName("transfer_scene_report_infos")
    @ApiModelProperty(value = "转账场景报备信息", required = true)
    public List<SceneReportInfo> sceneReportInfos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SceneReportInfo {

        @SerializedName("info_type")
        @ApiModelProperty(value = "信息类型", required = true)
        public String infoType;

        @SerializedName("info_content")
        @ApiModelProperty(value = "信息内容", required = true)
        public String infoContent;
    }

}
