import { createRouter, createWebHistory } from "vue-router";
import { filterTreeArray } from "@/utils";
// 引入约定式路由文件: 名称这种带下划线的不会被引入
const rawViewMap = import.meta.glob("@/views/([a-zA-Z0-9]+/)+?*.vue");

// 处理路由懒加载
const viewMap = Object.keys(rawViewMap).reduce((acc, key) => {
  const path = key
    .replace(/\/src\/views\/(.*?)\/index\.vue$/, "/$1")
    .replace(/\/src\/views\/(.*)\.vue$/, "/$1");
  // 去除布局组件
  if (path.indexOf("layout") > -1 || path.indexOf("slice") > -1) {
    return acc;
  }
  acc[path] = rawViewMap[key];
  return acc;
}, {});
/*重写路由的push方法*/
const routerPush = createRouter.prototype.push;
createRouter.prototype.push = function push (location) {
  return routerPush.call(this, location).catch((error) => error);
};

let authListArray = [];
let authListString = localStorage.getItem("AUTH_ROUTER");
if (authListString) authListArray = JSON.parse(authListString);
// 进行排序
authListArray = authListArray.sort((a, b) => {
  let a_router_order = Number(a.directoryDesc);
  let b_router_order = Number(b.directoryDesc);
  return a_router_order > b_router_order
    ? -1
    : a_router_order < b_router_order
      ? 1
      : 0;
});
const routes = [
  {
    name: "login",
    path: "/login",
    component: getComponent("login"),
  },
  {
    name: "404",
    path: "/404",
    component: getComponent("help/404"),
  },
  {
    name: "externalJump",
    path: "/externalJump",
    component: getComponent("login/externalJump"),
  },
  {
    name: 'panoramicMonitor',
    path: '/panoramicMonitor',
    component: getComponent("panoramicMonitor")
    //component: () => import('@/views/panoramicMonitor/index.vue')
  },
  // {
  //   name: 'stationDetails',
  //   path: '/stationDetails/stationDetails',
  //   component: getComponent("stationDetails/stationDetails")
  // },
  // 监控页面拆分
  {
    name: "monitor",
    path: "/monitor/:id",
    component: getComponent("monitor"),
    children: [
      {
        name: "mainWiringDiagram",
        path: "mainWiringDiagram",
        meta: {
          title: "主接线图",
        },
        isAuthority: true,
        component: getComponent("monitor/mainWiringDiagram"),
      },
      {
        name: "systemMonitoring",
        path: "systemMonitoring",
        meta: {
          title: "系统监控",
        },
        isAuthority: true,
        component: getComponent("monitor/systemMonitoring"),
        children: [
          {
            name: "photovoltaics",
            path: "photovoltaics",
            meta: {
              title: "光伏监控",
            },
            isAuthority: true,
            component: getComponent("monitor/systemMonitoring/photovoltaics"),
          },
          {
            name: "energyStorage",
            path: "energyStorage",
            meta: {
              title: "储能监控",
            },
            isAuthority: true,
            component: getComponent("monitor/systemMonitoring/energyStorage"),
          },
          {
            name: "electricPiles",
            path: "electricPiles",
            meta: {
              title: "电桩监控",
            },
            isAuthority: true,
            component: getComponent("monitor/systemMonitoring/electricPiles"),
          },
        ],
      },
      {
        name: "monitorHome",
        path: "",
        meta: {
          title: "监控主页",
        },
        isAuthority: true,
        component: getComponent("monitor/home"),
      },
      {
        name: "alarmLog",
        path: "alarmLog",
        meta: {
          title: "告警日志",
        },
        isAuthority: true,
        component: getComponent("monitor/alarmLog"),
        children: [
          {
            name: "faultAlarm",
            path: "faultAlarm",
            meta: {
              title: "故障告警",
            },
            isAuthority: true,
            component: getComponent("monitor/alarmLog/faultAlarm"),
          },
        ],
      },
      {
        name: "dataAnalysis",
        path: "dataAnalysis",
        meta: {
          title: "数据分析",
        },
        isAuthority: true,
        component: getComponent("monitor/dataAnalysis"),
        children: [
          {
            name: "historyData",
            path: "historyData",
            meta: {
              title: "历史数据",
            },
            isAuthority: true,
            component: getComponent("monitor/dataAnalysis/historyData"),
          },
        ],
      },
    ],
  },
  {
    path: "/",
    name: "layout",
    component: getComponent("root"),
    redirect: '/home',
    children: [
      ...makeSidebarTreeNode(),
      {
        name: "blank",
        path: "/blank",
        meta: {
          title: "空白页",
        },
        isAuthority: true,
        component: getComponent("layout/blank"),
      },
      {
        name: "personalCenter",
        path: "/personalCenter",
        meta: {
          title: "账号信息",
        },
        isAuthority: true,
        component: getComponent("personalCenter/index"),
      },
    ],
  },
  //不识别的path自动匹配404
  { path: "/:pathMatch(.*)*", name: "NotFound", redirect: "/404" },
];

// 返回树形结构权限
function makeSidebarTreeNode (id = "root") {
  let sidebar_list = [];
  // 循环动态生成 权限路由
  for (let i = 0; i < authListArray.length; i++) {
    let router_path_array = []; // 生成某一 模块的 顶级路由
    let activeRouteInfo = JSON.parse(JSON.stringify(authListArray[i] ?? {}));
    if (!authListArray[i].router_path) {
      activeRouteInfo = JSON.parse(
        JSON.stringify(unifiedRouteFieldAttrFun(authListArray[i]) ?? {})
      );
    }
    try {
      if (activeRouteInfo?.router_path) {
        router_path_array = activeRouteInfo?.router_path?.split("/");
      }
    } catch (e) {
      //TODO handle the exception
    }

    if (activeRouteInfo.parent_id === id) {
      let new_router_name = router_path_array[1];
      
      if ((id === "root" && new_router_name === "index")||router_path_array[0]=='home')
        new_router_name = router_path_array[0];
      let findItem = sidebar_list.find((item) => item.name === new_router_name);
      if (!findItem) {
        const obj = {};
        if (activeRouteInfo?.router_layout === 1) {
          obj["component"] = getComponent(activeRouteInfo.router_path);
        }
         if (activeRouteInfo?.router_layout === 2) {
          obj["component"] = getComponent(activeRouteInfo.router_path);
        }

        sidebar_list.push({
          ...obj,
          name: new_router_name,
          hidden: activeRouteInfo.router_hidden,
          path: `/${activeRouteInfo.router_path}`,
          children: makeSidebarTreeNode(activeRouteInfo.id),
          title: activeRouteInfo.name,
          meta: {
            title: activeRouteInfo.name,
            icon: activeRouteInfo.router_icon,
          },
        });
      }
    }
   
  }

  return sidebar_list;
}

// 获取模块的组件信息
function getComponent (comp_str) {
  let asyncFun;
  if (!comp_str) {
    asyncFun = () => import("@/views/_layout/components/AppMain.vue");
  } else if (comp_str === "root") {
    asyncFun = () => import("@/views/_layout/index.vue");
  } else {
    asyncFun = viewMap[`/${comp_str}`];
  }
  return asyncFun;
}

// 把路由字段进行统一处理
function unifiedRouteFieldAttrFun (routeInfo = {}) {
  const {
    id,
    name,
    url,
    iconPath,
    isLayout,
    isHidden,
    directoryDesc,
    parentId,
  } = routeInfo;
  return {
    id,
    name,
    router_path: url,
    router_icon: iconPath,
    router_layout: isLayout * 1,
    router_hidden: isHidden * 1,
    directoryDesc: Number(directoryDesc),
    parent_id: parentId ? parentId : "root",
  };
}

let sidebar_list = [];
let findItem = routes.find((item) => item.path === "/");
if (findItem.children && findItem.children.length) {
  for (let i = 0; i < findItem.children.length; i++) {
    if (!findItem.children[i].isAuthority) {
      sidebar_list.push(findItem.children[i]);
    }
  }
}
let new_sidebar_list = filterTreeArray(sidebar_list);
//获取到的权限树存储到本地
localStorage.setItem("SIDEBAR", JSON.stringify(new_sidebar_list)); //去除隐藏的路由权限
localStorage.setItem("ALL_SIDEBAR", JSON.stringify(sidebar_list)); // 所有的权限路由

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 添加导航守卫实现页面存在性检查和权限控制
router.beforeEach((to, from, next) => {
  // 1. 检查authListArray是否为空（未登录状态）
  if (authListArray.length === 0) {
    // 如果不是去登录页则重定向到登录页
    if (to.name !== 'login') {
      return next('/login');
    }
  } else {
    // 2. 已登录状态下检查页面是否存在
    const isRouteExists = routes.some(route => {
      // 检查直接匹配的路由
      if (route.path === to.path) return true;
      // 检查动态路由（如/monitor/:id）
      if (route.path.includes(':') && to.path.match(new RegExp(route.path.replace(/:\w+/, '\\w+')))) return true;
      // 检查嵌套路由
      if (route.children) {
        return route.children.some(child => {
          if (child.path === to.path) return true;
          if (child.path.includes(':') && to.path.match(new RegExp(child.path.replace(/:\w+/, '\\w+')))) return true;
          return false;
        });
      }
      return false;
    });

    // 3. 页面不存在且不是404页则重定向到404
    if (!isRouteExists && to.name !== '404' && to.name !== 'NotFound') {
      return next('/404');
    }
  }
  next();
});

export default router;