import { tycvsAuth } from "@elink/shared/auth";
import { createHttpClient } from "@elink/shared/http";
import store from "@/store/index.js";
import { requestPath } from "@/utils/requestPath";

const { request: service, cancelAbleService } = createHttpClient({
  auth: tycvsAuth,
  baseURL: requestPath,
  getStoreGetters: () => store.getters,
});

export { cancelAbleService };
export default service;
