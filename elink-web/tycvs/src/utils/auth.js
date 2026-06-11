// auth 模块通过依赖注入获得 Cookies 实例
import Cookies from "js-cookie";
import { createAuthManager, AUTH_PREFIX } from "@elink/shared/auth";

export const tycvsAuth = createAuthManager(Cookies, AUTH_PREFIX.TYCVS);

export const getToken = tycvsAuth.getToken;
export const setToken = tycvsAuth.setToken;
export const removeToken = tycvsAuth.removeToken;
