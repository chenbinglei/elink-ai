// auth模块已迁移至 @elink/shared/auth
// 保留此文件作为兼容层，后续P4-BC阶段可完全移除
import { linkosAuth } from "@elink/shared/auth";
export const getToken = linkosAuth.getToken;
export const setToken = linkosAuth.setToken;
export const removeToken = linkosAuth.removeToken;
