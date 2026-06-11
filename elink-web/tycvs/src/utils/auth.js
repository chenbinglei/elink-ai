// auth模块已迁移至 @elink/shared/auth
// 保留此文件作为兼容层，后续P4-BC阶段可完全移除
import { tycvsAuth } from "@elink/shared/auth";
export const getToken = tycvsAuth.getToken;
export const setToken = tycvsAuth.setToken;
export const removeToken = tycvsAuth.removeToken;
