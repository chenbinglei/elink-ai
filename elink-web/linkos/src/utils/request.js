import { linkosAuth } from "@elink/shared/auth";
import { createHttpClient } from "@elink/shared/http";
import { isDev } from "@elink/shared/utils";
import store from "@/store/index.js";

const portNum = ":5000";
const locationHost = location.hostname;
const locationProtocol = location.protocol;
const onlineServerIpAddress = `${locationProtocol}//${locationHost}${locationProtocol === "http:" ? portNum : ""}`;

const { request: service, cancelAbleService } = createHttpClient({
  auth: linkosAuth,
  baseURL: isDev() ? "/proxy" : onlineServerIpAddress,
  getStoreGetters: () => store.getters,
});

export { cancelAbleService };
export default service;
