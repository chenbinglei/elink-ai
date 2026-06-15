// auth 模块通过依赖注入获得 Cookies 实例
import Cookies from "js-cookie";
import { createAuthManager, AUTH_PREFIX } from "@elink/shared/auth";

export const linkosAuth = createAuthManager(Cookies, AUTH_PREFIX.LINKOS);

export const getToken = linkosAuth.getToken;
export const setToken = linkosAuth.setToken;
export const removeToken = linkosAuth.removeToken;
