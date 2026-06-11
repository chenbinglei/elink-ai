import router from "./router";
import "nprogress/nprogress.css"; // Progress 进度条样式
import NProgress from "nprogress"; // Progress 进度条
import {getToken} from "@/utils/auth"; // 验权
import {ElMessage} from "element-plus";

// NProgress 配置：减少闪屏感知，提升加载流畅度
NProgress.configure({
    showSpinner: false,
    trickleSpeed: 200,
    minimum: 0.15,
    easing: "ease",
    speed: 400,
});

// 不重定向白名单
const whiteList = ["/login"];

//路由拦截器(页面每次跳转都会执行的)
router.beforeEach((to, from, next) => {

    NProgress.start();

    if (getToken()) {
        if (to.path === "/login") {
            next({path: "/"});
        } else {
            next();
        }
    } else {
        if (whiteList.indexOf(to.path) !== -1 || whiteList.indexOf(to.matched[0].path) !== -1) {
            next();
        } else {
            if (from.fullPath === "/") {
                next("/login");
            } else {
                next("/login");
                ElMessage({message: "登录已失效，请重新登录", showClose: true, type: "error"});
            }
            NProgress.done();
        }
    }
});

router.afterEach(() => {
    NProgress.done(); // 结束Progress
});

// 路由切换异常兜底，避免 NProgress 卡住造成顶部进度条残留观感
router.onError(() => {
    NProgress.done();
});
