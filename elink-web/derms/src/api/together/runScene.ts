import request from "@/utils/request";

export default class RunSceneController {
  /**
   * 根据id查询电桩详情信息
   * @param {String} id
   * @param {number} [ts=0]
   * @returns {Promise<Object>} 请求结果
   */
  static findPileDetailById(id, ts = 0) {
    return request({
      url: `/together/runScene/findPileDetailById?ts=${ts}`,
      portNum: 60009,
      method: "post",
      data: { id },
    });
  }
}
