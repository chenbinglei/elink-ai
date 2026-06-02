const tagsView = {
    state: {
        visitedViews: [],// 存放所有浏览过的且不重复的路由数据
        cachedViews: [], // 缓存的页卡 配合:includews" 做动态缓存
        backButArray: [], //需要展示返回按钮箭头的数组（缓存页卡使用）
        routerIsSingleOpen:["/orderManagement/orderAllRecord","/orderManagement/orderRecordDetails"], //那些路由需要单开，不能存储多个
    },
    mutations: {
        // 打开新页签--添加路由数据的方法
        ADD_VISITED_VIEWS: (state, view) => {
            //空白页不生成标签卡
            if (view.path === "/" || view.path === "/blank" || view.path === "/layout/blank") return;
            // 当前需要存储的页卡数据
            let viewData = { name: view.name, path: view.path, meta: view.meta, query: view.query };
            // 判断当前路由是否可多开
            let findIndex = state.routerIsSingleOpen.findIndex(item=> item === view.path);
            // 默认可同时 缓存同一页面多个标签卡
            if(findIndex === -1){
                // 已存在的路由不进行存储
                if (state.visitedViews.some(v => JSON.stringify(v) === JSON.stringify(viewData))){
                    // console.log("该路由已存在不需要再次存储！！！");
                    return;
                }
            } else {
                // 只能缓存同一个页面 一次 标签卡
                let findRouterIndex = state.visitedViews.findIndex(item=> item.path === view.path);
                if(findRouterIndex !== -1){
                    state.visitedViews[findRouterIndex] = viewData;
                    return;
                }
            }

            // 截取文件name，缓存数组使用
            let pageNameArray = view.name.split("/");
            let pageName = pageNameArray[pageNameArray.length - 1];

            state.visitedViews.push(viewData);
            state.cachedViews.push(pageName);
        },
        //关闭页签--删除路由数据的方法  根据 path 以及 query 参数
        DEL_VISITED_VIEWS: (state, view) => {
            for (let [i, v] of state.visitedViews.entries()) {
                let viewData = { name: view.name, path: view.path, meta: view.meta, query: view.query };

                if (JSON.stringify(v) === JSON.stringify(viewData)) {
                    state.visitedViews.splice(i, 1);
                    state.cachedViews.splice(i, 1);
                    break;
                }
            }

            for (let [i, v] of state.backButArray.entries()) {
                if(v.showButRoute === view.name && v.id === view.query.id){
                    state.backButArray.splice(i, 1);
                    break;
                }
            }

            localStorage.setItem("TAGS_VIEW_BACK_ARRAY", JSON.stringify(state.backButArray)); // 存储到本地
        },
        //关闭其他缓存路由
        DEL_OTHERS_VISITED_VIEWS: (state, view) => {
            state.visitedViews = state.visitedViews.filter(item => {
                return JSON.stringify(item) === JSON.stringify(view);
            });
            state.cachedViews = state.cachedViews.filter(item => {
                return item === view.name;
            });

            state.backButArray = state.backButArray.filter(item => {
                return item.showButRoute === view.name && item.id === view.query.id;
            });
            localStorage.setItem("TAGS_VIEW_BACK_ARRAY", JSON.stringify(state.backButArray)); // 存储到本地
        },
        // 删除所有的路由
        DEL_ALL_VISITED_VIEWS: (state, views) => {
            state.visitedViews = views;
            state.cachedViews = views;
            state.backButArray = views;
            localStorage.removeItem("TAGS_VIEW_BACK_ARRAY");
        },
        UPDATE_BACK_BUT_VIEWS:(state, views)=> {
            state.backButArray = views;
        },
        // 返回按钮列表添加数据
        ADD_BACK_BUT_VIEWS:(state, view)=>{
            // 已存在的路由不进行存储
            if (state.backButArray.some(v => JSON.stringify(v) === JSON.stringify(view))) return;

            state.backButArray.push(view);
            localStorage.setItem("TAGS_VIEW_BACK_ARRAY", JSON.stringify(state.backButArray)); // 存储到本地
        }
    },
    actions: {
        //通过解构赋值得到commit方法
        addvisitedViews({ commit }, view) {
            commit("ADD_VISITED_VIEWS", view);
        },
        //删除数组存放的路由之后，需要再去刷新路由，这是一个异步的过程，需要有回掉函数，所以使用并返回promise对象，也可以让组件在调用的时候接着使用.then的方法
        delvisitedViews({ commit, state }, view) {
            //resolve方法：未来成功后回掉的方法
            return new Promise((resolve) => {
                commit("DEL_VISITED_VIEWS", view);
                resolve([...state.visitedViews]);
            });
        },
        // 删除 除当前之外的 其他路由
        delOthersViews({ commit }, view) {
            commit("DEL_OTHERS_VISITED_VIEWS", view);
        },
        // 删除所有的路由
        delAllRouteViews({ commit, state }, views) {
            return new Promise((resolve) => {
                commit("DEL_OTHERS_VISITED_VIEWS", views);
                resolve([...state.visitedViews]);
            });
        },
        updateBackButViews({ commit }, views) {
            commit("UPDATE_BACK_BUT_VIEWS", views);
        },
        // 返回按钮列表添加数据
        addBackButViews({ commit }, view) {
            commit("ADD_BACK_BUT_VIEWS", view);
        }
    }
};

export default tagsView;
