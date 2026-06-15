import axios from "axios";
import qs from "qs";
import { ElMessage } from "element-plus";
import { createHttpClient } from "@elink/shared/http";
import { isDev } from "@elink/shared/utils";
import { dermsAuth } from "./auth.js";
import store from "@/store/index.js";

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
  getStoreGetters: () => store.getters,
  perRequestIsolation: true, // derms 每请求独立实例：并发请求不会互相取消
  enablePortNum: true, // derms 旧逻辑启用 portNum 动态端口路由
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
