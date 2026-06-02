const getters = {
    //app.js
    userInfo: state => state.app.userInfo,
    oldUserId: state => state.app.oldUserId,
    permissionList: state => state.app.permissionList,
    contentMainMaxHeight: state => state.app.contentMainMaxHeight,

    //sidebar
    isCollapse: state => state.sidebar.isCollapse,

    // tagsView
    cachedViews: state => state.tagsView.cachedViews,
    visitedviews: state => state.tagsView.visitedviews,
    backButArray: state => state.tagsView.backButArray,
    
    // 拓扑图中需要变化的值
     TopoNodeLocationValue: state => state.app.TopoNodeLocationValue,  
}
export default getters
