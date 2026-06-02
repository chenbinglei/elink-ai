import {getNowDateAll} from "@/utils/dateTime";

const assetManagement = {
    state: {
        siteAllIds: [], // 全部站点ids
        activeSiteId: "", // 当前选中的站点id
        scenarioType: 1, // 1: 光伏  2： 储能  3： 电桩
        updateTimeNum: getNowDateAll(), // 控制首页资产数据是否更新
    },
    mutations: {
        // 更换展示类型
        UPDATE_SCENARIO_TYPE: (state, scenarioType) => {
            state.scenarioType = scenarioType;
        },
        UPDATE_ACTIVE_SITE_ID: (state, activeSiteId) => {
            state.activeSiteId = activeSiteId;
        },
        // 更新全部站点ids
        UPDATE_SITE_ALL_IDS: (state, siteAllIds = []) => {
            state.siteAllIds = siteAllIds;
        },
        UPDATE_TIME_NUM: (state, updateTimeNum) => {
            state.updateTimeNum = updateTimeNum;
        },
    },
    actions: {
        //通过解构赋值得到commit方法
        updateScenarioType({commit}, scenarioType) {
            commit("UPDATE_SCENARIO_TYPE", scenarioType);
        },
        updateActiveSiteId({commit}, activeSiteId) {
            commit("UPDATE_ACTIVE_SITE_ID", activeSiteId);
        },
        // 更新全部站点ids
        updateSiteAllIds({commit}, siteAllIds = []) {
            commit("UPDATE_SITE_ALL_IDS", siteAllIds);
        },
        updateTimeNumFun({commit}, updateTimeNum) {
            commit("UPDATE_TIME_NUM", updateTimeNum);
        },
    }
};

export default assetManagement;