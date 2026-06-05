package com.sunmax.configure.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sunmax.common.dto.system.OperatorInfoDto;
import com.sunmax.common.dto.system.SiteOperateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 响应参数实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto {

    /**
     * 返回值
     * -1:系统繁忙， 此时请求方稍后重试
     * 0:请求成功
     * 4001:签名错误
     * 4002:Token 错误
     * 4003:POST 参数不合法,缺少必须的示例：OperatorID,sig,TimeStamp,Data，Seq 五个参数
     * 4004:请求的业务参数不合法， 各接口定义自己的必须参数
     * 500:系统错误
     */
    @JSONField(name = "Ret")
    @JsonProperty("Ret")
    private Integer ret;

    /**
     * 返回信息
     */
    @JSONField(name = "Msg")
    @JsonProperty("Msg")
    private String msg;

    /**
     * 参数内容
     */
    @JSONField(name = "Data")
    @JsonProperty("Data")
    private String data;

    /**
     * 数字签名
     */
    @JSONField(name = "Sig")
    @JsonProperty("Sig")
    private String sig;

    /**
     * 站点关联运营商信息
     */
    @JSONField(serialize=false)
    private List<SiteOperateDto> siteOperateList;

    /**
     * 运营商信息
     */
    @JSONField(serialize=false)
    private List<OperatorInfoDto> operatorInfoList;

    public static ResponseDto error(Integer ret, String msg) {
        return ResponseDto.builder().ret(ret).msg(msg).data(null).build();
    }

    public static ResponseDto error(Integer ret, String msg, String sig) {
        return ResponseDto.builder().ret(ret).msg(msg).sig(sig).build();
    }

    public static ResponseDto ok(String data, String sig, List<SiteOperateDto> siteOperateList, List<OperatorInfoDto> operatorInfoList) {
        return ResponseDto.builder().ret(0).msg("请求成功").data(data).sig(sig).siteOperateList(siteOperateList).operatorInfoList(operatorInfoList).build();
    }

}
