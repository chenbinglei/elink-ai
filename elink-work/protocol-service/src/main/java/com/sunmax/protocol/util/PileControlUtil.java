package com.sunmax.protocol.util;

import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.crontab.PileStartControlVo;
import com.sunmax.protocol.service.feign.CrontabService;
import com.sunmax.protocol.task.ControlTask;
import com.sunmax.common.vo.protocol.PileStartVo;
import com.sunmax.common.vo.protocol.PileStopVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
public class PileControlUtil {

    private static String lastOrderNum;

    //根据充电桩编号生成订单号
    public static String createOrderNum(String pilesCode, String gunCode) {
        //离线标识，由平台鉴权充/放电都是1
        int offlineLogo = 1;
        //当前的序列号
        String currentOrderNum = StringUtils.rightPad(pilesCode, 16, "F") + DateTimeFormatter.ofPattern("yyMMddHHmmss").format(LocalDateTime.now());
        //获取顺序号 如果为空，则去数据库获取最新的一次
        //如果上一次的订单号包含当前的订单号 则顺序号+1 否则从001开始
        String orderNum;
        if (StringUtil.isEmpty(lastOrderNum)) {
            orderNum = currentOrderNum + offlineLogo + gunCode + "01";
            lastOrderNum = orderNum;
            return orderNum;
        }
        if (lastOrderNum.contains(currentOrderNum)) {
            //获取上次订单的顺序号+1,产生新的顺序号
            int serialNumber = Integer.parseInt(lastOrderNum.substring(lastOrderNum.length() - 2)) + 1;
            orderNum = currentOrderNum + offlineLogo + gunCode + String.format("%02d", serialNumber);
        } else {
            orderNum = currentOrderNum + offlineLogo + gunCode + "01";
        }
        lastOrderNum = orderNum;
        return orderNum;
    }

    /**
     * 验证开始堆叠的合法性。
     *
     * @param pileStartVo 包含堆叠开始信息的对象。不可为null。
     * @return boolean 如果所有验证字段都不为空，则返回true；如果任何字段为空或只包含空格，则返回false。
     */
    public static boolean isValidPileStart(PileStartVo pileStartVo) {
        // 检查输入对象是否为null
        if (pileStartVo == null) {
            // 可以考虑添加日志记录这里的失败原因
            return false;
        }

        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                pileStartVo.getPileCode(),
                pileStartVo.getGunCode(),
                pileStartVo.getStrategy(),
                pileStartVo.getStrategyCfg(),
                pileStartVo.getRunMode(),
                pileStartVo.getAccountType(),
                pileStartVo.getAccountData(),
//                pileStartVo.getPlatformLogo(),
                pileStartVo.getStarter(),
                pileStartVo.getType()

        };

        // 它同时处理了空和只含空格的字符串
        return Arrays.stream(fieldsToValidate).noneMatch(StringUtil::isEmpty);
    }

    public static boolean isDuplicateStart(String keyId) {
        if (ControlTask.pileDemandMap.containsKey(keyId)) {
            LocalDateTime createTime = ControlTask.pileDemandMap.get(keyId).getCreateTime();
            return StringUtil.isNotEmpty(createTime) && DateUtil.compareDiffBetweenSecond(createTime, LocalDateTime.now()) <= 60;
        }
        return false;
    }

    /**
     * 缓存订单信息到缓存中。
     * 当充电枪启动成功时，将订单信息预缓存到缓存中，以提高数据访问效率。
     *
     * @param pileStartVo 充电控制参数，包含订单的各种信息。
     * @param pileCode    充电桩的编码。
     * @param gunCode     充电枪的编码。
     */
    public static void cacheOrderInfo(PileStartVo pileStartVo, String pileCode, String gunCode) {
        try {
            RedisGeneralUtil.executePile(pileCode, () -> {
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                if (pileRealModel != null) {
                    PileRealModel.GunRealModel gunRealModel = new PileRealModel.GunRealModel();
                    if (pileRealModel.getGunRealModelMap().containsKey(gunCode)) {
                        gunRealModel = pileRealModel.getGunRealModelMap().get(gunCode);
                    }
                    gunRealModel.setGunCode(gunCode);
                    gunRealModel.setSerialNum(pileStartVo.getSerialNum());
    //                gunRealModel.setPlatformLogo(pileStartVo.getPlatformLogo());
                    gunRealModel.setStarter(pileStartVo.getStarter());
                    gunRealModel.setType(pileStartVo.getType());
                    gunRealModel.setStartTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                    gunRealModel.setRunMode(pileStartVo.getRunMode());
                    gunRealModel.setStrategy(pileStartVo.getStrategy());
                    gunRealModel.setStrategyCfg(pileStartVo.getStrategyCfg());
                    gunRealModel.setAccountType(pileStartVo.getAccountType());
                    gunRealModel.setAccountData(pileStartVo.getAccountData());
                    gunRealModel.setClockingTime(pileStartVo.getClockingTime());
                    gunRealModel.setPrepayMoney(pileStartVo.getPrepayMoney());
                    pileRealModel.getGunRealModelMap().put(gunCode, gunRealModel);
                    RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);
                }
            });
        } catch (Exception e) {
            // 记录缓存订单信息失败的日志
            log.error("缓存订单信息失败", e);
        }
    }

    public static boolean isValidPileStop(PileStopVo pileStopVo) {
        // 检查输入对象是否为null
        if (pileStopVo == null) {
            // 可以考虑添加日志记录这里的失败原因
            return false;
        }

        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                pileStopVo.getPileCode(),
                pileStopVo.getGunCode(),
                pileStopVo.getSerialNum(),
                pileStopVo.getType()
        };

        // 使用Apache Commons Lang3的StringUtils.isEmpty()进行联合检查
        // 它同时处理了空和只含空格的字符串
        return Arrays.stream(fieldsToValidate).noneMatch(StringUtil::isEmpty);
    }

    public static PileStartControlVo checkPileControl(PileStartVo pileStartVo, CrontabService crontabService) {
        //校验平台自动控制(充电)
        PileStartControlVo pileStartControlVo = new PileStartControlVo();
        pileStartControlVo.setPileCode(pileStartVo.getPileCode());
        pileStartControlVo.setGunCode(pileStartVo.getGunCode());
        pileStartControlVo.setSiteId(pileStartVo.getSiteId());
        pileStartControlVo.setStartTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
        pileStartControlVo.setResult(0); //启动中
        pileStartControlVo.setRunMode(pileStartVo.getRunMode());
        pileStartControlVo.setIsUpdate(crontabService.checkPileStartControl(pileStartControlVo).getData());
        return pileStartControlVo;
    }

    public static void updatePileControl(PileStartControlVo pileStartControlVo, CrontabService crontabService) {
        //更新平台自动控制(充放电)
        if (pileStartControlVo.getIsUpdate()) {
            crontabService.updatePileStartControl(pileStartControlVo);
        }
    }

}
