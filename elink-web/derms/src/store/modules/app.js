import { removeToken } from "@/utils/auth";

const app = {
  state: {
    oldUserId: "", //在该页卡登录时的用户id
    userInfo: null, //用户信息
    isSwitching: false,
    scale: 1,
  },
  // 页面使用方式：this.$store.commit('方法名', 参数);
  mutations: {
    UPDATE_OLD_USERID(state, id) {
      state.oldUserId = id;
    },
    UPDATE_USERINFO: (state, userInfo) => {
      state.userInfo = userInfo;
    },
    UPDATE_SWITCHING: (state, isSwitching) => {
      state.isSwitching = isSwitching;
    },
    UPDATE_SCALE: (state, scale) => {
      state.scale = scale;
    },
  },
  //调用这里去触发mutations，如何调用？在组件内使用this.$store.dispatch('action中对应名字', 参数)
  actions: {
    updateOldUserId: ({ commit }, oldUserId) => {
      commit("UPDATE_OLD_USERID", oldUserId);
    },
    updateUserInfo: ({ commit }, userInfo) => {
      commit("UPDATE_USERINFO", userInfo);
    },
    exitSystem: ({ commit }, { noReload }) => {
      commit("UPDATE_OLD_USERID", "");
      commit("UPDATE_USERINFO", null);
      removeToken();
      if (!noReload) {
        location.reload();
      }
    },
  },
};

export default app;
