import axios from "axios";
import qs from "qs";
import { ElMessage } from "element-plus";
import { createHttpClient } from "@elink/shared/http";
import { isDev } from "@elink/shared/utils";
import { dermsAuth } from "./auth.js";
import { useAppStore } from "@/stores/index";

const portNum = ":5000";
const locationHost = location.hostname;
const locationProtocol = location.protocol;
const onlineServerIpAddress = `${locationProtocol}//${locationHost}${locationProtocol === "http:" ? portNum : ""}`;

const { request, cancelAbleService } = createHttpClient({
  axios,
  ElMessage,
  qs,
  auth: dermsAuth,
  baseURL: isDev() ? "/proxy" : onlineServerIpAddress,
  getStoreGetters: () => {
    const appStore = useAppStore();
    return { oldUserId: appStore.oldUserId, userInfo: appStore.userInfo };
  },
  perRequestIsolation: true,
  enablePortNum: true,
  onAuthExpired: () => {
    if (window.top !== window) {
      window.top.postMessage({ action: "unAuth" });
      return;
    }
    dermsAuth.removeToken();
    location.reload();
  },
});

export { cancelAbleService };
export default request;
