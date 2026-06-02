import request from "@/utils/request";

export default class SystemsetController {
  /**
   * 查询用户角色
   */
  static findUserGroupListByPage(data) {
    return request({
      url: `system/systemManage/findUserGroupListByPage`,
      method: "post",
      data,
    });
  }
  // 新增用户组 & 编辑用户组
  static saveOrUpdateUserGroup(data) {
    return request({
      url: `/system/systemManage/saveOrUpdateUserGroup`,
      method: "post",
      data,
    });
  }
  // 删除
  static deleteUserGroupById(data) {
    return request({
      url: `/system/systemManage/deleteUserGroupById`,
      method: "post",
      data,
    });
  }
  // 添加/修改用户组信息-人员管理
  static updateUserByGroupId(data) {
    return request({
      url: `/system/systemManage/updateUserByGroupId`,
      method: "post",
      data,
    });
  }
  // 查询平台
  static queryProductList(data) {
    return request({
      url: `/system/configureCenter/queryProductList`,
      method: "post",
      data,
    });
  }
  // 查询平台下面的产品根据角色
  static findGroupApplyEmpowerInfoById(data) {
    return request({
      url: `/system/systemManage/findGroupApplyEmpowerInfoById`,
      method: "post",
      data,
    });
  }
  // 查询平台下面的产品不不不根据角色
  static findTenantApplyEmpowerInfoById(data) {
    return request({
      url: `/system/tenantManage/findTenantApplyEmpowerInfoById`,
      method: "post",
      data,
    });
  }
  // 修改角色下面的产品
  static saveGroupApplyEmpowerInfo(data) {
    return request({
      url: `/system/systemManage/saveGroupApplyInfo`,
      method: "post",
      data,
    });
  }
  // 修改平台下面的产品
  static saveTenantApplyEmpowerInfo(data) {
    return request({
      url: `/system/tenantManage/saveTenantApplyInfo`,
      method: "post",
      data,
    });
  }
  // 人员列表
  static findUserListByTenantId(data) {
    return request({
      url: `/system/tenantManage/findUserListByTenantId`,
      method: "post",
      data,
    });
  }
  // 新增&编辑用户
  static saveOrUpdateUserInfo(data) {
    return request({
      url: `/system/systemManage/saveOrUpdateUserInfo`,
      method: "post",
      data,
    });
  }
  // 查询用户列表
  static findUserListByPage(data) {
    return request({
      url: `/system/systemManage/findUserListByPage`,
      method: "post",
      data,
    });
  }
  // 删除用户
  static deleteUserInfoById(data) {
    return request({
      url: `/system/systemManage/deleteUserInfoById`,
      method: "post",
      data,
    });
  }
  /**
   * 查询日志
   */
  static queryAccessLogList(data) {
    return request({
      url: `/energy/log/queryAccessLogList`,
      portNum: 60009,
      method: "post",
      data,
    });
  }
  /**
   * 查询组织
   */
  static findOrganStructureListByTenantId(data) {
    return request({
      url: `/system/tenantManage/findOrganStructureListByTenantId`,
      portNum: 60009,
      method: "post",
      data,
    });
  }

  /**
   * 查询企业组织场站
   */
  static findEmpowerListByPage(data) {
    return request({
      url: `/system/tenantManage/findEmpowerListByPage`,
      portNum: 60009,
      method: "post",
      data,
    });
  }

  /**
   * 企业详情
   */
  static findTenantDetailsById(data) {
    return request({
      url: `/system/tenantManage/findTenantDetailsById`,
      portNum: 60009,
      method: "post",
      data,
    });
  }

  /**
   * 根据id修改资产授权信息
   */
  static updateEmpowerAuthorityById(data) {
    return request({
      url: `/system/tenantManage/updateEmpowerAuthorityById`,
      portNum: 60009,
      method: "post",
      data,
    });
  }
  /**
   *根据id删除资产授权信息
   */
  static deleteEmpowerInfoById(data) {
    return request({
      url: `/system/tenantManage/deleteEmpowerInfoById`,
      portNum: 60009,
      method: "post",
      data,
    });
  }
  /**
   *添加站点
   */
  static batchSaveOrganEmpower(data) {
    return request({
      url: `/system/tenantManage/batchSaveOrganEmpower`,
      portNum: 60009,
      method: "post",
      data,
    });
  }
 /**
   *站点列表
   */
  static findOrganEmpowerSiteList(data) {
    return request({
      url: `/system/tenantManage/findTenantOrganSiteList`,
      portNum: 60009,
      method: "post",
      data,
    });
  }
  /**
   *组织架构
   */
  static findOrganStructureByTenantId(data) {
    return request({
      url: `/system/tenantManage/findOrganStructureByTenantId`,
      portNum: 60009,
      method: "post",
      data,
    });
  }
  /**
   *新增组织
   */
  static saveOrUpdateOrganStructure(data) {
    return request({
      url: `system/tenantManage/saveOrUpdateOrganStructure`,
      portNum: 60009,
      method: "post",
      data,
    });
  }
  /**
   *删除组织
   */
  static deleteOrganStructureById(data) {
    return request({
      url: `/system/tenantManage/deleteOrganStructureById`,
      portNum: 60009,
      method: "post",
      data,
    });
  }
}
