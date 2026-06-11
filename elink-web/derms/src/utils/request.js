import { dermsAuth } from "@elink/shared/auth";
import { createHttpClient } from "@elink/shared/http";
import { isDev } from "@elink/shared/utils";
import store from "@/store/index.js";

const portNum = ":5000";
const locationHost = location.hostname;
const locationProtocol = location.protocol;
const onlineServerIpAddress = `${locationProtocol}//${locationHost}${locationProtocol === "http:" ? portNum : ""}`;

const { request, cancelAbleService } = createHttpClient({
  auth: dermsAuth,
  baseURL: isDev() ? "/proxy" : onlineServerIpAddress,
  getStoreGetters: () => store.getters,
  perRequestIsolation: true,  // derms 每请求独立实例模式：并发请求不会被互相取消
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
