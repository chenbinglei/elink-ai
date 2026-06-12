import router from "./router";
import "nprogress/nprogress.css"; // Progress 进度条样式
import NProgress from "nprogress"; // Progress 进度条
import { getToken } from "@/utils/auth"; // 验权
import { ElMessage } from "element-plus";
import { useAppStore } from "@/stores/index";
import { removeToken } from "@/utils/auth";

// 不重定向白名单
const whiteList = ["/login", "/externalJump"];
// // const 
const sidebar = JSON.parse(localStorage.getItem("SIDEBAR"));
const userInfo = JSON.parse(localStorage.getItem("USER_INFO"));
//路由拦截器(页面每次跳转都会执行的)
function findFirstVisibleWithChildren (data) {
  for (let item of data) {
    if (item.children && item.children.length > 0) {
      const result = findFirstVisibleWithChildren(item.children);
      if (result) {
        return result;
      }
    } else {
      // 找到叶子节点，返回它的 path
      return item.path;
    }
  }
}
router.beforeEach((to, from, next) => {

  NProgress.start();
  const appStore = useAppStore();
  appStore.isSwitching = true;
  if (getToken()) {
    if (to.path === "/login") {
      console.log(findFirstVisibleWithChildren(sidebar));
      const firstVisiblePath = findFirstVisibleWithChildren(sidebar);
      next(firstVisiblePath);
      // next({ path: "/" });
    }
    else {
      if (!sidebar || !Array.isArray(sidebar) || sidebar.length === 0||userInfo==null) {
        removeToken(); // 清除 token
        localStorage.clear(); // 清除本地存储
        next("/login"); // 强制跳转到登录页
        return;
      }
      if (to.path === '/404' ) {
        return next(findFirstVisibleWithChildren(sidebar))
      }
      next();
    }
  } else {
    if (
      whiteList.indexOf(to.path) !== -1 ||
      whiteList.indexOf(to.matched[0].path) !== -1
    ) {
      next();
    } else {
      if (window.top !== window) {
        window.top.postMessage({
          action: "unAuth",
        });
        return;
      }
      const query = to.query ?? {};
      if (from.fullPath === "/") {
        next({
          path: "/login",
          query,
        });
      } else {
        next({
          path: "/login",
          query,
        });
        ElMessage({
          message: "登录已失效，请重新登录！",
          showClose: true,
          type: "error",
        });
        return;
      }
      NProgress.done();
      appStore.isSwitching = false;
    }
  }
});

router.afterEach(() => {
  NProgress.done(); // 结束Progress
  appStore.isSwitching = false);
});
