package com.sunmax.configure.util;
import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.enums.ProtocolEnum;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.configure.dto.ResponseDto;
import com.sunmax.configure.runner.SubstationRunner;
import com.sunmax.configure.service.feign.SystemService;
import com.sunmax.configure.vo.RequestCommonVo;
import com.sunmax.configure.vo.RequestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.PostConstruct;
import java.util.Objects;

@Component
@Slf4j
public class HttpResponseUtil {

    public static final String DATA_SECRET = PlatformConfig.DATA_SECRET; //运营商消息密钥

    public static final String DATA_SECRET_IV = PlatformConfig.DATA_SECRET_IV; //运营商消息密钥初始化向量

    public static HttpResponseUtil httpResponseUtil;

    @PostConstruct
    public void init() {
        httpResponseUtil = this;
        httpResponseUtil.systemService = this.systemService;
    }

    @Autowired
    private SystemService systemService;

    public static Boolean checkToken(String operatorId) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            String token = ((ServletRequestAttributes) requestAttributes).getRequest().getHeader("Authorization");
            if (StringUtil.isNotEmpty(token)) {
                return TokenUtil.validateToken(token.replace("Bearer ", FileUtil.separator), operatorId);
            }
        }
        return false;
    }

    /**
     * 校验请求参数
     *
     * @param request      请求参数
     * @param protocolCode 协议标识
     * @return 状态码
     */
    public static ResponseDto checkData(RequestVo request, String protocolCode) {
        if (request != null) {
            String operatorId = request.getOperatorId();
            String data = request.getData();
            String timeStamp = request.getTimeStamp();
            String seq = request.getSeq();
//        String sig = request.getSig();
            if (StringUtil.isEmpty(operatorId)) {
                return ResponseDto.error(4003, "POST参数不合法,缺少必须的实例: OperatorID");
            }
            if (StringUtil.isEmpty(data)) {
                return ResponseDto.error(4003, "POST参数不合法,缺少必须的实例: Data");
            }
            if (StringUtil.isEmpty(timeStamp)) {
                return ResponseDto.error(4003, "POST参数不合法,缺少必须的实例: TimeStamp");
            }
            if (StringUtil.isEmpty(seq)) {
                return ResponseDto.error(4003, "POST参数不合法,缺少必须的实例: Seq");
            }
            PlatformDataForwardDto platformDataForward = new PlatformDataForwardDto();
            if (!Objects.equals(protocolCode, ProtocolEnum.INTERFLOW.getCode())) {
                //如果当前类型为省或市或互联互通，查询运营商关联转发数据配置信息
                ResponseResult<PlatformDataForwardDto> platformDataForwardResult = httpResponseUtil.systemService.getPlatformDataForward(operatorId, protocolCode);
                if (!platformDataForwardResult.isSuccess() || StringUtil.isEmpty(platformDataForwardResult.getData())) {
                    return ResponseDto.error(4003, "请求的业务不合法,数据转发配置运营商信息为空");
                }
                platformDataForward = platformDataForwardResult.getData();
                if (!Objects.equals(operatorId, platformDataForward.getPlatformId())) { //运营商ID校验
                    return ResponseDto.error(4003, "POST参数不合法,运营商ID不正确");
                }
            } else {
                if (SubstationRunner.getInterFlowField(operatorId) == null) {
                    ResponseResult<PlatformDataForwardDto> platformDataForwardResult = httpResponseUtil.systemService.getPlatformDataForward(operatorId, protocolCode);
                    if (!platformDataForwardResult.isSuccess() || StringUtil.isEmpty(platformDataForwardResult.getData())) {
                        return ResponseDto.error(4003, "请求的业务不合法,数据转发配置运营商信息为空");
                    }
                    platformDataForward = platformDataForwardResult.getData();
                    if (!Objects.equals(operatorId, platformDataForward.getPlatformId())) { //运营商ID校验
                        return ResponseDto.error(4003, "POST参数不合法,运营商ID不正确");
                    }
                    //把互联互通的数据存到缓存中
                    SubstationRunner.interflowMap.put(platformDataForward.getPlatformId(), RequestCommonVo.builder()
                            .url(platformDataForward.getAddress())
                            .platformId(platformDataForward.getPlatformId())
                            .platformSecret(platformDataForward.getPlatformSecret())
                            .dataSecret(platformDataForward.getDataSecret())
                            .dataSecretIv(platformDataForward.getDataSecretIv())
                            .sigSecret(platformDataForward.getSigSecret()).build());
                }
            }
            if (!checkToken(operatorId)) { //运营商对应的token校验
                return ResponseDto.error(4002, "Token错误");
            }
//        String generateSig = HMacMD5Util.getHmacMd5HexString(SIG_SECRET, HMacMD5Util.dataSplice(data, timeStamp, seq));
//        if (StringUtil.isEmpty(sig) || !Objects.equals(sig, generateSig)) {
//            return ResponseDto.error(4001, "签名错误", generateSig);
//        }
            return ResponseDto.ok(AESUtil.desEncrypt(DATA_SECRET, DATA_SECRET_IV, data), null, platformDataForward.getSiteOperateList(), platformDataForward.getOperatorInfoList());
        }
        return ResponseDto.error(4004, "请求的业务参数不合法， 各接口定义自己的必须参数");
    }

    public static String responseData(ResponseDto response) {
        if (StringUtil.isNotEmpty(response.getData())) {
            response.setData(AESUtil.encrypt(DATA_SECRET, DATA_SECRET_IV, response.getData()));
        }
        return JSON.toJSONString(response);
    }

    public static void main(String[] args) {
        String data = "{\"PageNo\":1,\"PageSize\":10}";
        String encrypt = AESUtil.encrypt(DATA_SECRET, DATA_SECRET_IV, data);
        log.info(encrypt);
    }

}
