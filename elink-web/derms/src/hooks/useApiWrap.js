import { ref, nextTick } from "vue";

/**
 * @typedef {Object} CommonResponse
 * @property {Boolean} success
 * @property {*} data
 * @property {String} message
 *
 * @typedef {Object} UseApiWrapParam
 * @property {()=> Promise<CommonResponse>} api
 * @property {(e: Error)=>void} onError 接口报错处理
 * @property {()=> Data} guarante 接口出错时,返回数据兜底
 * @property {Function} formatter 接口的数据格式化函数
 * @property {Boolean} debounce 是否开启执行节流,只有上一个接口返回才能发下一个,默认true
 *
 * @param {UseApiWrapParam} param0
 * @returns
 */
export default function useApiWrap({
  api,
  onError,
  guarante = () => ({}),
  formatter,
  debounce = false,
}) {
  const loading = ref(false);
  const run = async (params) => {
    if (debounce && loading.value) {
      loading.value = false;
      return;
    }
    loading.value = true;
    try {
      const { success, data, message } = await api(params);
      if (!success) {
        throw new Error(message);
      }
      return formatter ? formatter(data) : data;
    } catch (e) {
      onError?.(e);
      return guarante();
    } finally {
      nextTick(() => {
        loading.value = false;
      });
    }
  };

  return {
    loading,
    run,
  };
}
