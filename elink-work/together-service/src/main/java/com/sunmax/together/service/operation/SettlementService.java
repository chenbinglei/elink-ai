package com.sunmax.together.service.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.settlement.SiteAccountDetailDto;
import com.sunmax.together.dto.operation.settlement.SiteAccountListDto;
import com.sunmax.together.vo.operation.settlement.SiteAccountChangeVo;
import com.sunmax.together.vo.operation.settlement.SiteAccountQueryVo;

import java.util.List;

public interface SettlementService {

    /**
     * 查询站点账户列表
     * @param siteAccountQueryVo 站点账户查询条件
     * @return 站点收款付款账户列表
     */
    ResponseResult<PageDto<SiteAccountListDto>> querySiteAccountList(SiteAccountQueryVo siteAccountQueryVo);

    /**
     * 根据站点id和账户类型查询站点收款付款账户列表
     * @param siteId 站点id
     * @param type 账户类型 1-收款账户 2-付款账户 3-分帐账户
     * @return 收款付款账户列表
     */
    ResponseResult<List<SiteAccountDetailDto>> findSiteAccountListBySiteIdAndType(String siteId, Integer type);

    /**
     * 根据账户id查询账户信息
     * @param accountId 账户id
     * @return 账户信息
     */
    ResponseResult<AccountDto> findAccountByAccountId(String accountId);

    /**
     * 站点账户新增或修改
     * @param siteAccountChangeVo 站点账户新增或修改参数
     * @return 新增或修改结果
     */
    ResponseResult<Void> saveSiteAccount(SiteAccountChangeVo siteAccountChangeVo);

    /**
     * 根据主键id删除站点账户数据
     * @param id 主键id
     * @return 状态码
     */
    ResponseResult<Void> deleteSiteAccountById(String id);

    /**
     * 根据用户id查询账户列表
     * @param userId 用户id
     * @param platformType 平台类型 1-微信 2-支付宝
     * @return 账户列表
     */
    ResponseResult<List<AccountDto>> findAccountListByUserId(String userId, Integer platformType);

}
