/**
 * 创建请求参数转换函数（工厂函数）
 *
 * 【依赖注入设计】不再 import qs，由调用方注入。
 *
 * @param qs - qs 模块实例
 * @returns axios transformRequest 回调
 */
export function createTransform(qs: { stringify: (obj: Record<string, unknown>) => string }): (data: unknown) => string | FormData | null | undefined {
  if (!qs || typeof qs.stringify !== "function") {
    throw new Error(
      "[@elink/shared/transformRequest] qs 实例必须由调用方注入（import qs from 'qs'）"
    );
  }
  return function transform(data: unknown): string | FormData | null | undefined {
    if (data instanceof FormData) return data;
    if (data === null || data === undefined) return data;
    if (typeof data !== "object") return data;
    const obj = data as Record<string, unknown>;
    Object.keys(obj).forEach((key) => {
      if (obj[key] !== null && typeof obj[key] === "object") {
        obj[key] = JSON.stringify(obj[key]);
      }
    });
    return qs.stringify(obj);
  };
}
