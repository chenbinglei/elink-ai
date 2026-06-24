import Cookies from "js-cookie";

//本js文件为操作用户token值的
const TokenKey = "APPLET_AdminToken";

export function getToken(cookieKey) {
	return Cookies.get(cookieKey ? "APPLET_" + cookieKey : TokenKey);
}

export function setToken(cookieData, cookieKey, expires) {
	//登录有效期  { expires: 1 / 24 }  一小时
	return Cookies.set(cookieKey ? "APPLET_" + cookieKey : TokenKey, cookieData, {
		expires: expires ? expires : 24,
	});
}

export function removeToken(cookieKey) {
	return Cookies.remove(cookieKey ? "APPLET_" + cookieKey : TokenKey);
}
