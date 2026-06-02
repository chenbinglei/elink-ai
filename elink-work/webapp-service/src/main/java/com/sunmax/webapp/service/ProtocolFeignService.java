package com.sunmax.webapp.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.WalletBalanceVo;

public interface ProtocolFeignService {

    /**
     * 更新钱包余额
     * @param walletBalanceVo 钱包余额编辑实体类
     * @return 状态码
     */
    ResponseResult<Void> updateWalletBalance(WalletBalanceVo walletBalanceVo);
}
