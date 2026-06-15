import request from "@/utils/request";

export default class ModelController {
  /**
   * 获取资产分类列表
   */
  static getAssetTypeList() {
    return request({
      url: `/device/model/getAssetTypeList`,
      portNum: 60009,
      method: "post",
      data: {},
    });
  }
}
