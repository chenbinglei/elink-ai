const getters = {
    //app.js
    userInfo: state => state.app.userInfo,
    oldUserId: state => state.app.oldUserId,
    systemConfigInfo: state => state.app.systemConfigInfo,

    //sidebar
    isCollapse: state => state.sidebar.isCollapse,

    // tagsView
    cachedViews: state => state.tagsView.cachedViews,
    visitedViews: state => state.tagsView.visitedViews,
    backButArray: state => state.tagsView.backButArray,

    //meta2d
    canvasMeta2d: state => state.meta2d.canvasMeta2d,
    canvas_scale: state => state.meta2d.canvas_scale,
    disableScale: state => state.meta2d.disableScale,
    canvasLocked: state => state.meta2d.canvasLocked,
    canvasEyeMap: state => state.meta2d.canvasEyeMap,
    canvasModeType: state => state.meta2d.canvasModeType,
    drawLineStatus: state => state.meta2d.drawLineStatus,
    canvasMeta2dData: state => state.meta2d.canvasMeta2dData,
    canvasMeta2dAllLoad: state => state.meta2d.canvasMeta2dAllLoad,
    current_active_pel_num: state => state.meta2d.current_active_pel_num,
    current_active_pel_list: state => state.meta2d.current_active_pel_list,
    customCanvasOptionsList: state => state.meta2d.customCanvasOptionsList,
    setValueDeleteFieldList: state => state.meta2d.customCanvasOptionsList,
}
export default getters
