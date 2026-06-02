import {createRouter, createWebHistory} from 'vue-router'
import {filterTreeArray, getParameterByName} from "@/utils";

/*重写路由的push方法*/
const routerPush = createRouter.prototype.push;
createRouter.prototype.push = function push(location) {
    return routerPush.call(this, location).catch((error) => error);
};

let authListArray = [];
let authListString = localStorage.getItem("AUTH_ROUTER");
if (authListString) authListArray = JSON.parse(authListString);
// console.log(authListArray);

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
        name: 'topology_3d',
        path: '/topology_3d',
        component: getComponent("threeTopology/indexThree")
    },
    {
        path: '/',
        name: 'layout',
        // redirect: 'blank',
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
    //不识别的path自动匹配404
    {path: '/:pathMatch(.*)*', name: 'NotFound', redirect: '/404'},
]

// 返回树形结构权限
function makeSidebarTreeNode(id = "root") {
    let sidebar_list = [];
    // 循环动态生成 权限路由
    for (let i = 0; i < authListArray.length; i++) {
        // 生成某一 模块的 顶级路由
        let new_router_path = authListArray[i].router_path.split('?'); // 兼容部分导航页面需要跳转
        let new_router_path_name = new_router_path && new_router_path[0];
        let router_path_array = new_router_path_name.split('/');

        if (authListArray[i].parent_id === id) {
            let new_router_name = router_path_array[1];
            if(id === "root" && new_router_name === "index")new_router_name = router_path_array[0];

            let findItem = sidebar_list.find(item => item.name === new_router_name);
            if (!findItem) {
                // console.log(authListArray[i].name);
                // console.log(authListArray[i].router_path);
                const obj = getParameterByName(authListArray[i].router_path); // 额外参数配置
                if (authListArray[i].router_layout === 1) {
                    obj["component"] = getComponent(new_router_path_name);
                }

                sidebar_list.push({
                    ...obj,
                    name: new_router_name,
                    path: `/${new_router_path_name}`,
                    hidden: authListArray[i].router_hidden,
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
        return () => import("@/views/layout/components/AppMain");
    }
    switch (comp_str) {
        case "root":
            return () => import("@/views/layout");
        default:
            return () => require.ensure([], (require) => require(`@/views/${comp_str}`))
    }
}

// console.log(routes);
let sidebar_list = [];
// let getRoutesArray = JSON.parse(JSON.stringify(router.getRoutes()));
// console.log(getRoutesArray);
let findItem = routes.find(item => item.path === '/');
if (findItem.children && findItem.children.length) {
    for (let i = 0; i < findItem.children.length; i++) {
        if (!findItem.children[i].isAuthority) {
            sidebar_list.push(findItem.children[i]);
        }
    }
}
let new_sidebar_list = filterTreeArray(sidebar_list);
// console.log(new_sidebar_list);
//获取到的权限树存储到本地
// console.log(router.getRoutes());
localStorage.setItem("SIDEBAR", JSON.stringify(new_sidebar_list)); //去除隐藏的路由权限
localStorage.setItem("ALL_SIDEBAR", JSON.stringify(sidebar_list)); // 所有的权限路由


const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
});

export default router
