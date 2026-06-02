const meta2d = {
    state: {

        canvasMeta2d: null,  // 画布实列
        canvasMeta2dAllLoad: false, // meta2d 是否全部加载完成
        drawLineStatus: false, // 钢笔是否 连续绘画
        canvasEyeMap: false, // 鹰眼地图
        canvas_scale: 0.55, // 默认缩放级别 1为 100%
        disableScale: false,  //禁止画布缩放
        canvasLocked: 0, // 画布模式 未锁定
        current_active_pel_num: 0, // 画布上缩放、旋转图元计数器（同步更新单个图元操作栏数据）
        current_active_pel_list: [], // 画布当前选中的图元id数组
        canvasMeta2dData: null, // 画布详情数据

        // 画布上可自定义的配置属性字段
        customCanvasOptionsList: [
            {name: "背景颜色", fieldName: "background", default: "#FFFFFF"},
            {name: "画笔默认颜色", fieldName: "color", default: "#222222"},
            {name: "宽度", fieldName: "width", default: 1920},
            {name: "高度", fieldName: "height", default: 1080},
            {name: "缩放方式", fieldName: "scaleMode", default: "1"},
            {name: "主题", fieldName: "theme", default: "light"},
            {name: "禁止缩放", fieldName: "isDisableScale", default: false},
            {name: "显示滚动条", fieldName: "isScroll", default: false},
            {name: "禁止移动", fieldName: "isDisableTranslate", default: false},
            {name: "连线相交弯曲", fieldName: "lineCross", default: false},
        ],

        canvasModeType: "normal", //normal:正常开发   module： 组件开发
        // 更新画布执行 删除的元素
        setValueDeleteFieldList: ["x", "y", "ex", "ey", "width", "height", "rotate"]
    },
    // 页面使用方式：this.$store.commit('方法名', 参数);
    mutations: {
        // 初始化 meta2d 实列
        INIT_CANVAS_META2D(state, canvasMeta2d) {
            state.canvasMeta2d = canvasMeta2d;
        },
        // meta2d 是否全部加载完成
        UPDATE_CANVAS_META2D_ALL_LOAD(state, canvasMeta2dAllLoad) {
            state.canvasMeta2dAllLoad = canvasMeta2dAllLoad;
        },
        // 更新 钢笔绘画状态
        UPDATE_DRAW_LINE_STATUS(state, drawLineStatus) {
            state.drawLineStatus = drawLineStatus;
        },
        // 更新缩放级别
        UPDATE_CANVAS_SCALE(state, canvas_scale) {
            state.canvas_scale = canvas_scale;
        },
        //禁止画布缩放
        UPDATE_DISABLE_SCALE(state, disableScale) {
            state.disableScale = disableScale;
        },
        //画布上缩放、旋转图元计数器（同步更新单个图元操作栏数据）
        UPDATE_CUR_ACTIVE_PEL_NUM(state, current_active_pel_num) {
            state.current_active_pel_num = current_active_pel_num;
        },
        // 画布当前选中的图元id数组
        UPDATE_CUR_ACTIVE_PEL_LIST(state, current_active_pel_list = []) {
            // 数据不相同再更新
            if (JSON.stringify(state.current_active_pel_list) !== JSON.stringify(current_active_pel_list)) {
                state.current_active_pel_list = current_active_pel_list;
            }
        },
        // 更新图模数据、图模名称
        UPDATE_CANVAS_META2D_DATA(state, canvasMeta2dData) {
            state.canvasMeta2dData = canvasMeta2dData;
        },
        // 更新画布模式 锁定 --- 编辑
        UPDATE_CANVAS_LOCKED(state, canvasLocked) {
            state.canvasLocked = canvasLocked;
        },
        UPDATE_MODE_TYPE(state, canvasModeType) {
            state.canvasModeType = canvasModeType;
        },
        UPDATE_EYE_MAP(state, canvasEyeMap) {
            state.canvasEyeMap = canvasEyeMap;
        },
    },
    //调用这里去触发mutations，如何调用？在组件内使用this.$store.dispatch('action中对应名字', 参数)
    actions: {
        // 初始化 meta2d 实列
        initCanvasMeta2d: ({commit}, canvasMeta2d) => {
            commit('INIT_CANVAS_META2D', canvasMeta2d);
        },
        // meta2d 是否全部加载完成
        updateCanvasMeta2dAllLoad: ({commit}, canvasMeta2dAllLoad) => {
            commit('UPDATE_CANVAS_META2D_ALL_LOAD', canvasMeta2dAllLoad)
        },
        // 更新 钢笔绘画状态
        updateDrawLineStatus: ({commit}, drawLineStatus) => {
            commit('UPDATE_DRAW_LINE_STATUS', drawLineStatus)
        },
        updateCanvasScale: ({commit}, canvas_scale) => {
            commit('UPDATE_CANVAS_SCALE', canvas_scale)
        },
        // 设置缩放
        updateDisableScale: ({commit}, disableScale) => {
            commit('UPDATE_DISABLE_SCALE', disableScale)
        },
        //画布上缩放、旋转图元计数器（同步更新单个图元操作栏数据）
        updateCurActivePelNum: ({commit}, current_active_pel_num) => {
            commit('UPDATE_CUR_ACTIVE_PEL_NUM', current_active_pel_num)
        },
        // 更新选中图元id数组
        updateCurActivePelList: ({commit}, current_active_pel_list) => {
            commit('UPDATE_CUR_ACTIVE_PEL_LIST', current_active_pel_list)
        },
        // 更新图模数据、图模名称
        updateCanvasMeta2dData: ({commit}, canvasMeta2dData) => {
            commit('UPDATE_CANVAS_META2D_DATA', canvasMeta2dData)
        },
        // 更新画布模式 锁定 --- 编辑
        updateCanvasLocked: ({commit}, canvasLocked) => {
            commit('UPDATE_CANVAS_LOCKED', canvasLocked)
        },
        // 更新画布模式
        updateCanvasModeType: ({commit}, canvasModeType) => {
            commit('UPDATE_MODE_TYPE', canvasModeType)
        },
        updateCanvasEyeMap: ({commit}, canvasEyeMap) => {
            commit('UPDATE_EYE_MAP', canvasEyeMap)
        },
    }
}

export default meta2d
