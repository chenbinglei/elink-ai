const app = {
    state: {
        oldUserId: "", //在该页卡登录时的用户id
        userInfo: null, //用户信息
        permissionList: [], // 控件权限列表
        contentMainMaxHeight: 520, // 内容区域最大高度
        // 拓扑图中需要变化的值
        TopoNodeLocationValue: [],
    },
    // 页面使用方式：this.$store.commit('方法名', 参数);
    mutations: {
        UPDATE_OLD_USERID (state, id) {
            state.oldUserId = id
        },
        UPDATE_USERINFO: (state, userInfo) => {
            state.userInfo = userInfo;
        },
        UPDATE_PERMISSION_LIST: (state, permissionList) => {
            state.permissionList = permissionList;
        },
        UPDATE_CONTENT_MAIN_HEIGHT: (state, contentMainMaxHeight) => {
            state.contentMainMaxHeight = contentMainMaxHeight;
        },
        // 拓扑图中需要变化的值
        SET_NEW_CUSTOM_VALUE (state, value) {
            state.TopoNodeLocationValue = value;
        }
    },
    //调用这里去触发mutations，如何调用？在组件内使用this.$store.dispatch('action中对应名字', 参数)
    actions: {
        // 拓扑图中需要变化的值
        updateNewTopoNode ({ commit }, value) {
            commit('SET_NEW_CUSTOM_VALUE', value);
        },
        updateOldUserId: ({ commit }, oldUserId) => {
            commit('UPDATE_OLD_USERID', oldUserId)
        },
        updateUserInfo: ({ commit }, userInfo) => {
            commit('UPDATE_USERINFO', userInfo)
        },
        updatePermissionList: ({ commit }, permissionList) => {
            commit('UPDATE_PERMISSION_LIST', permissionList)
        },
        updateContentMainMaxHeight: ({ commit }, contentMainMaxHeight) => {
            commit('UPDATE_CONTENT_MAIN_HEIGHT', contentMainMaxHeight)
        },
    }
}

export default app
