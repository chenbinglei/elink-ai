package com.sunmax.webapp.enums;

import lombok.Getter;

@Getter
public enum TransferStatusEnum {

    ACCEPTED("ACCEPTED", "转账已受理"),
    PROCESSING("PROCESSING", "转账锁定资金中。如果一直停留在该状态，建议检查账户余额是否足够，如余额不足，可充值后再原单重试"),
    WAIT_USER_CONFIRM("WAIT_USER_CONFIRM", "待收款用户确认，可拉起微信收款确认页面进行收款确认"),
    TRANSFERING("TRANSFERING", "转账中，可拉起微信收款确认页面再次重试确认收款"),
    SUCCESS("SUCCESS", "转账成功"),
    FAIL("FAIL", "转账失败"),
    CANCELING("CANCELING", "商户撤销请求受理成功，该笔转账正在撤销中"),
    CANCELLED("CANCELLED", "转账撤销完成");

    //字段名
    private final String code;

    //字段中文名
    private final String name;

    TransferStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static TransferStatusEnum getByCode(String code) {
        for (TransferStatusEnum transferStatus : TransferStatusEnum.values()) {
            if (transferStatus.getCode().equals(code)) {
                return transferStatus;
            }
        }
        return null;
    }

}
