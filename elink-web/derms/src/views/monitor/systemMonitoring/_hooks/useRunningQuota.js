import { ref, unref, inject, reactive, watchEffect } from "vue";
import { INJECT_KEY_DEVICE_INFO } from "../constant";

export default function useRunningQuota({
  keyReflectMap,
  runningQuotaList,
  api,
}) {
  const { deviceId } = inject(INJECT_KEY_DEVICE_INFO);
  // 运行指标中的指标列表
  const runningQuotaData = reactive({});
  const loading = ref(false);
  const getOverviewData = async () => {
    const deviceIdWithoutRef = unref(deviceId);
    if (!deviceIdWithoutRef) {
      return;
    }
    loading.value = true;
    try {
      const { success, data } = await api(deviceIdWithoutRef);
      if (!success) {
        return;
      }
      // 后端值与前端值映射
      let reflectData = data;
      if (Object.keys(keyReflectMap).length > 0) {
        reflectData = Object.keys(data).reduce((map, item) => {
          const reflectKey = keyReflectMap[item];
          if (reflectKey) {
            if (typeof reflectKey === "function") {
              const { key, value } = reflectKey(data, item);
              map[key] = value;
            } else {
              map[reflectKey] = data[item];
            }
          } else {
            map[item] = data[item];
          }

          return map;
        }, {});
      }
      Object.assign(runningQuotaData, reflectData);
    } finally {
      loading.value = false;
    }
  };
  watchEffect(() => {
    getOverviewData();
  });

  return { loading, runningQuotaList, runningQuotaData };
}
