import {createRouter, createWebHistory} from 'vue-router'
import {filterTreeArray} from "@/utils";

/*重写路由的push方法*/
const routerPush = createRouter.prototype.push;
createRouter.prototype.push = function push(location) {
    return routerPush.call(this, location).catch((error) => error);
};

// 使用 import.meta.glob 预加载 views 下所有 vue 组件，支持多级路径动态加载
const viewModules = import.meta.glob("@/views/**/*.vue");

// 获取所有页面权限列表数据
let authListArray = [];
let authListString = localStorage.getItem("AUTH_ROUTER");
if (authListString) authListArray = JSON.parse(authListString);
// console.log(authListArray);
const route_full_screen_list = ["2DVisualization/2d_artworkEditor"]; // 页面路由全屏展示白名单

const routes = [
    {
        name: 'login',
        path: '/login',
        component: getComponent("login/index")
    },
    {
        name: '404',
        path: '/404',
        component: getComponent("404")
    },
    {
        name: 'canvasPreview',
        path: '/canvasPreview/:id',
        component: getComponent("2DVisualization/canvasPreview")
    },
    {
        path: '/',
        name: 'layout',
        component: getComponent("root"),
        children: [{
            name: 'blank',
            path: '/blank',
            meta: {
                title: "空白页"
            },
            isAuthority: true,
            component: getComponent("layout/blank"),
        },
            {
                name: "personalCenter",
                path: "/personalCenter",
                meta: {
                    title: "账号信息"
                },
                isAuthority: true,
                component: getComponent("personalCenter/index")
            },
            ...makeSidebarTreeNode()
        ]
    },
    ...makeSidebarTreeNode("root", true), // 全屏页面权限
    //不识别的path自动匹配404
    {path: '/:pathMatch(.*)*', name: 'NotFound', redirect: '/404'},
]

// 返回树形结构权限
function makeSidebarTreeNode(id = "root",isFullScreen = false) {
    let sidebar_list = [];
    // 循环动态生成 权限路由
    for (let i = 0; i < authListArray.length; i++) {
        // 生成某一 模块的 顶级路由
        let router_path_array = authListArray[i].router_path.split('/');
        if (authListArray[i].parent_id === id) {
            let new_router_name = router_path_array[1];
            if(id === "root" && new_router_name === "index")new_router_name = router_path_array[0];
            let findItem = sidebar_list.find(item => item.name === new_router_name);
            const finIndex = route_full_screen_list.indexOf(authListArray[i].router_path);
            if (!findItem && (!isFullScreen && finIndex === -1 || isFullScreen && finIndex !== -1)) {
                // console.log(authListArray[i].name);
                // console.log(authListArray[i].router_path);

                const obj = {};
                if (authListArray[i].router_layout === 1) {
                    obj["component"] = getComponent(authListArray[i].router_path);
                }

                sidebar_list.push({
                    ...obj,
                    name: new_router_name,
                    hidden: authListArray[i].router_hidden,
                    path: `/${authListArray[i].router_path}`,
                    children: makeSidebarTreeNode(authListArray[i].id),
                    meta: {title: authListArray[i].name, icon: authListArray[i].router_icon}
                })
            }
        }
    }

    return sidebar_list
}

// 获取模块的组件信息
function getComponent(comp_str) {
    if (!comp_str) {
        return () => import("@/views/layout/components/AppMain.vue");
    }
    if (comp_str === "root") {
        return () => import("@/views/layout/index.vue");
    }
    // 优先匹配 /src/views/<comp_str>.vue
    const pathDirect = `/src/views/${comp_str}.vue`;
    // 其次匹配 /src/views/<comp_str>/index.vue
    const pathIndex = `/src/views/${comp_str}/index.vue`;
    let loader = viewModules[pathDirect] || viewModules[pathIndex];
    if (!loader) {
        // 兜底：模糊匹配以 comp_str 结尾的文件
        const suffix1 = `${comp_str}.vue`;
        const suffix2 = `${comp_str}/index.vue`;
        const matchedKey = Object.keys(viewModules).find(
            (k) => k.endsWith(suffix1) || k.endsWith(suffix2)
        );
        if (matchedKey) loader = viewModules[matchedKey];
    }
    return loader || (() => import("@/views/404.vue"));
}

// console.log(routes);
let sidebar_list = [...makeSidebarTreeNode(), ...makeSidebarTreeNode("root", true)];
// console.log(sidebar_list);
let new_sidebar_list = filterTreeArray(sidebar_list);
// console.log(new_sidebar_list);
//获取到的权限树存储到本地
localStorage.setItem("SIDEBAR", JSON.stringify(new_sidebar_list)); //去除隐藏的路由权限
localStorage.setItem("ALL_SIDEBAR", JSON.stringify(sidebar_list)); // 所有的权限路由

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});

// let getRoutesArray = JSON.parse(JSON.stringify(router.getRoutes()));
// console.log(getRoutesArray);

export default router
