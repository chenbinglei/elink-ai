
const getters = {
    //app.js
    userInfo: state => state.app.userInfo,
    oldUserId: state => state.app.oldUserId,
    systemConfigInfo: state => state.app.systemConfigInfo,

    //sidebar
    isCollapse: state => state.sidebar.isCollapse,

    // tagsView
    cachedViews: state => state.tagsView.cachedViews,
    visitedviews: state => state.tagsView.visitedviews,
    backButArray: state => state.tagsView.backButArray,

    // assetManagement
    siteAllIds: state => state.assetManagement.siteAllIds,
    scenarioType: state => state.assetManagement.scenarioType,
    activeSiteId: state => state.assetManagement.activeSiteId,
    updateTimeNum: state => state.assetManagement.updateTimeNum,

    // operationManagement
    secondaryDetailsInfo: state => state.operationManagement.secondaryDetailsInfo,
    secondaryDetailsVisible: state => state.operationManagement.secondaryDetailsVisible,

    // energyManagement
    strategyName: state => state.energyManagement.strategyName,
    issueStrategyTime: state => state.energyManagement.issueStrategyTime,
    activeSlSiteInfo: state => state.energyManagement.activeSlSiteInfo,
};

export default getters;
