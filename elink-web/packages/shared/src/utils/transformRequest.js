import qs from "qs";

/**
 * 请求数据转换函数
 * 将对象数据转为qs格式，处理嵌套对象和FormData
 */
export function transform() {
  return function (data) {
    if (data instanceof FormData) {
      return data;
    }
    Object.keys(data).forEach((key) => {
      if (typeof data[key] === "object" && data[key] === null) data[key] = "";
      if (typeof data[key] === "object") data[key] = JSON.stringify(data[key]);
    });
    data = qs.stringify(data);
    return data;
  };
}
