import router from "./router";
import "nprogress/nprogress.css"; // Progress 进度条样式
import NProgress from "nprogress"; // Progress 进度条
import {getToken} from "@/utils/auth"; // 验权
import {ElMessage} from "element-plus";

// 不重定向白名单
const whiteList = ["/login","/canvasPreview/:id"];

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
