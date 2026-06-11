import { dermsAuth } from "@elink/shared/auth";
import { createHttpClient } from "@elink/shared/http";
import { isDev } from "@elink/shared/utils";
import store from "@/store/index.js";

const portNum = ":5000";
const locationHost = location.hostname;
const locationProtocol = location.protocol;
const onlineServerIpAddress = `${locationProtocol}//${locationHost}${locationProtocol === "http:" ? portNum : ""}`;

const { request: service, cancelAbleService } = createHttpClient({
  auth: dermsAuth,
  baseURL: isDev() ? "/proxy" : onlineServerIpAddress,
  getStoreGetters: () => store.getters,
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
export default service;
