import axios from "axios";
import qs from "qs";
import { ElMessage } from "element-plus";
import { createHttpClient } from "@elink/shared/http";
import { tycvsAuth } from "./auth.js";
import store from "@/store/index.js";
import { requestPath } from "@/utils/requestPath";

const { request: service, cancelAbleService } = createHttpClient({
  axios,
  ElMessage,
  qs,
  auth: tycvsAuth,
  baseURL: requestPath,
  getStoreGetters: () => store.getters,
});

export { cancelAbleService };
export default service;
