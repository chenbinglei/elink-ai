// auth模块已迁移至 @elink/shared/auth
// 保留此文件作为兼容层，后续P4-BC阶段可完全移除
import { dermsAuth } from "@elink/shared/auth";
export const getToken = dermsAuth.getToken;
export const setToken = dermsAuth.setToken;
export const removeToken = dermsAuth.removeToken;
