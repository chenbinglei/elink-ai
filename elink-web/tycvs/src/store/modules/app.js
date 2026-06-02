const app = {
    state: {
        oldUserId: "", //在该页卡登录时的用户id
        userInfo: null, //用户信息
    },
    // 页面使用方式：this.$store.commit('方法名', 参数);
    mutations: {
        UPDATE_OLD_USERID(state, id) {
            state.oldUserId = id
        },
        UPDATE_USERINFO: (state, userInfo) => {
            state.userInfo = userInfo;
        }
    },
    //调用这里去触发mutations，如何调用？在组件内使用this.$store.dispatch('action中对应名字', 参数)
    actions: {
        updateOldUserId: ({commit}, oldUserId) => {
            commit('UPDATE_OLD_USERID', oldUserId)
        },
        updateUserInfo: ({commit}, userInfo) => {
            commit('UPDATE_USERINFO', userInfo)
        }
    }
}

export default app
