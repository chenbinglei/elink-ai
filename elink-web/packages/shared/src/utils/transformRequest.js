/**
 * 创建请求参数转换函数（工厂函数）
 *
 * 【依赖注入设计】不再 import qs，由调用方注入。
 *
 * @param {object} qs - qs 模块实例
 * @returns {Function} axios transformRequest 回调
 */
export function createTransform(qs) {
  if (!qs || typeof qs.stringify !== "function") {
    throw new Error(
      "[@elink/shared/transformRequest] qs 实例必须由调用方注入（import qs from 'qs'）"
    );
  }
  return function transform(data) {
    if (data instanceof FormData) return data;
    if (data === null || data === undefined) return data;
    if (typeof data !== "object") return data;
    Object.keys(data).forEach((key) => {
      if (data[key] !== null && typeof data[key] === "object") {
        data[key] = JSON.stringify(data[key]);
      }
    });
    return qs.stringify(data);
  };
}
