// auth 模块通过依赖注入获得 Cookies 实例
import Cookies from "js-cookie";
import { createAuthManager, AUTH_PREFIX } from "@elink/shared/auth";

export const dermsAuth = createAuthManager(Cookies, AUTH_PREFIX.DERMS);

export const getToken = dermsAuth.getToken;
export const setToken = dermsAuth.setToken;
export const removeToken = dermsAuth.removeToken;
