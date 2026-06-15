import axios from "axios";
import qs from "qs";
import { ElMessage } from "element-plus";
import { createHttpClient } from "@elink/shared/http";
import { tycvsAuth } from "./auth.js";
import { useAppStore } from "@/stores/index";
import { requestPath } from "@/utils/requestPath";

const { request: service, cancelAbleService } = createHttpClient({
  axios,
  ElMessage,
  qs,
  auth: tycvsAuth,
  baseURL: requestPath,
  getStoreGetters: () => {
    const appStore = useAppStore();
    return { oldUserId: appStore.oldUserId, userInfo: appStore.userInfo };
  },
});

export { cancelAbleService };
export default service;
