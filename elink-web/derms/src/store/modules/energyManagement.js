
const energyManagement = {
    state: {
        strategyName: "", // 当前站点下控制节点的选中的当前策略名成
        issueStrategyTime: "", // 下发时间
        activeSlSiteInfo: {}, // 集中监控当前选中的站点信息
    },
    mutations: {
        // 更换展示类型
        UPDATE_ISSUE_STRATEGY_TIME:(state, issueTime)=> {
            state.issueStrategyTime = issueTime;
        },
        UPDATE_STRATEGY_NAME:(state, strategyName)=> {
            state.strategyName = strategyName;
        },
        UPDATE_ACTIVE_SL_SITE_INFO:(state, activeSlSiteInfo)=> {
            state.activeSlSiteInfo = activeSlSiteInfo;
        },
    },
    actions: {
        //通过解构赋值得到commit方法
        updateIssueStrategyTime({ commit }, issueTime) {
            commit("UPDATE_ISSUE_STRATEGY_TIME", issueTime);
        },
        updateStrategyName({ commit }, strategyName) {
            commit("UPDATE_STRATEGY_NAME", strategyName);
        },
        updateActiveSlSiteInfo({ commit }, activeSlSiteInfo) {
            commit("UPDATE_ACTIVE_SL_SITE_INFO", activeSlSiteInfo);
        },
    }
};

export default energyManagement;