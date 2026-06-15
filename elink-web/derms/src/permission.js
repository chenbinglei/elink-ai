import router from "./router";
import "nprogress/nprogress.css";
import NProgress from "nprogress";
import { getToken } from "@/utils/auth";
import { ElMessage } from "element-plus";
import { useAppStore } from "@/stores/index";
import { removeToken } from "@/utils/auth";

// 不重定向白名单
const whiteList = ["/login", "/externalJump"];

// 辅助函数：查找第一个可见路由
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

// 关键修复：获取最新的用户信息和sidebar
const getLatestUserInfo = () => {
  try {
    const userInfoStr = localStorage.getItem("USER_INFO");
    if (!userInfoStr) return null;
    return JSON.parse(userInfoStr);
  } catch (error) {
    console.error('解析USER_INFO失败:', error);
    return null;
  }
};

const getLatestSidebar = () => {
  try {
    const sidebarStr = localStorage.getItem("SIDEBAR");
    if (!sidebarStr) return null;
    return JSON.parse(sidebarStr);
  } catch (error) {
    console.error('解析SIDEBAR失败:', error);
    return null;
  }
};

// 系统管理模块特殊处理
const isSystemModule = (path) => {
  return path.startsWith('/system');
};

router.beforeEach((to, from, next) => {
  NProgress.start();
  const appStore = useAppStore();
  appStore.isSwitching = true;

  // 关键：每次路由跳转都获取最新的数据
  const sidebar = getLatestSidebar();
  const userInfo = getLatestUserInfo();



  if (getToken()) {
    if (to.path === "/login") {
      const firstVisiblePath = findFirstVisibleWithChildren(sidebar || []);
      next(firstVisiblePath || "/");
      NProgress.done();
      return;
    } else {
      // 系统管理模块特殊验证
      if (isSystemModule(to.path)) {
        console.log('访问系统管理模块，验证用户信息:', userInfo);

        // 额外检查用户信息完整性
        if (!userInfo || !userInfo.userId || !userInfo.tenantId) {
          console.warn('系统管理模块：用户信息不完整');

          // 测试环境下尝试从其他位置恢复
          if (window.location.hostname.includes('test')) {
            console.log('测试环境：尝试恢复用户信息...');

            // 尝试从sessionStorage恢复
            const sessionUserInfo = sessionStorage.getItem("USER_INFO");
            if (sessionUserInfo) {
              try {
                const parsed = JSON.parse(sessionUserInfo);
                localStorage.setItem("USER_INFO", sessionUserInfo);
                console.log('从sessionStorage恢复用户信息成功');

                // 重定向到当前页面，重新验证
                next({ ...to, replace: true });
                return;
              } catch (e) {
                console.error('恢复用户信息失败:', e);
              }
            }
          }

          // 验证失败，清除并跳转到登录页
          ElMessage({
            message: "用户信息不完整，请重新登录！",
            showClose: true,
            type: "error",
          });

          removeToken();
          localStorage.removeItem("USER_INFO");
          localStorage.removeItem("SIDEBAR");
          sessionStorage.removeItem("USER_INFO");

          next({
            path: "/login",
            query: {
              redirect: to.fullPath,
              module: 'system'
            }
          });
          NProgress.done();
          appStore.isSwitching = false;
          return;
        }
      }

      // 通用验证
      if (!sidebar || !Array.isArray(sidebar) || sidebar.length === 0 || !userInfo) {
        console.error('路由守卫：验证失败', {
          sidebar: sidebar ? `存在，长度: ${sidebar.length}` : '不存在',
          userInfo: userInfo ? '存在' : '不存在'
        });

        removeToken();
        localStorage.clear();
        sessionStorage.clear();

        next({
          path: "/login",
          query: {
            redirect: to.fullPath,
            reason: 'auth_failed'
          }
        });
        NProgress.done();
        appStore.isSwitching = false;
        return;
      }

      if (to.path === '/404') {
        const firstVisiblePath = findFirstVisibleWithChildren(sidebar);
        next(firstVisiblePath || "/");
        return;
      }

      next();
    }
  } else {
    if (
      whiteList.indexOf(to.path) !== -1 ||
      (to.matched[0] && whiteList.indexOf(to.matched[0].path) !== -1)
    ) {
      // next();


      if (from.name === 'undefind' && (to.fullPath.includes('/login?') || to.fullPath.includes('?id'))) {
        next();
      } else {
        const cleanQuery = {};
        // 只保留有用的参数
        if (to.query.redirect) {
          cleanQuery.redirect = to.query.redirect;
        }
        if (to.query.reason) {
          cleanQuery.reason = to.query.reason;
        }
        if (to.query.module) {
          cleanQuery.module = to.query.module;
        }
        if (JSON.stringify(to.query) !== JSON.stringify(cleanQuery)) {
          next({
            path: "/login",
            query: cleanQuery,
            replace: true
          });
          return;
        }
        next()
      }
    } else {
      if (window.top !== window) {
        window.top.postMessage({
          action: "unAuth",
        });
        NProgress.done();
        appStore.isSwitching = false;
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
      }
      NProgress.done();
      appStore.isSwitching = false;
    }
  }
});

router.afterEach(() => {
  NProgress.done();
  // 修复：appStore 在 beforeEach 闭包内定义，afterEach 闭包不可见，需重新获取
  const appStore = useAppStore();
  appStore.isSwitching = false;
});