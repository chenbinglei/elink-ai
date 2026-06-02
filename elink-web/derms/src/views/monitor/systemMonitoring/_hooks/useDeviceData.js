import { unref, ref, watchEffect,inject } from "vue";
import SystemMonitorController from "@/api/together/systemMonitor";
import { INJECT_KEY_DEVICE_INFO } from "../constant";

export default function useDeviceData({ apiName = "" }) {
  
  const { deviceId } = inject(INJECT_KEY_DEVICE_INFO);
  const loading = ref(false);
  const api = SystemMonitorController[apiName];
  if (typeof api !== "function") {
    return;
  }
  //设备信息
  const deviceInfoData = ref({});
  const getDeviceInfo = async () => {
    const deviceIdWithoutRef = unref(deviceId);
    if(!deviceIdWithoutRef){return;}
    loading.value = true;
    try {
      const { success, data } = await api(deviceIdWithoutRef);
      if (!success) {
        return;
      }
      deviceInfoData.value = data;
    } finally {
      loading.value = false;
    }
  };

  //
  watchEffect(() => {
    getDeviceInfo();
  });

  return { loading, deviceInfoData };
}
