import request from "@/utils/request";

export default class SiteInfoController {
  /**
   * 获取资产分类列表
   */
  static findSiteSetUpBySiteId(data) {
    return request({
      url: `/device/siteInfo/findSiteSetUpBySiteId`,
      portNum: 60009,
      method: "post",
      data,
    });
  }
}
