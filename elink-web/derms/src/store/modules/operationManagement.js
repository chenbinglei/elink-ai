const operationManagement = {
    state: {
        secondaryDetailsInfo: {},
        secondaryDetailsVisible: false, // 二级详情页 是否显示
    },
    mutations: {
        // 更换展示类型
        UPDATE_SECONDARY_Info: (state, secondaryDetailsInfo) => {
            state.secondaryDetailsInfo = secondaryDetailsInfo;
        },
        UPDATE_SECONDARY_Visible: (state, secondaryDetailsVisible) => {
            state.secondaryDetailsVisible = secondaryDetailsVisible;
        },
    },
    actions: {
        //通过解构赋值得到commit方法
        updateSecondaryInfo({commit}, secondaryDetailsInfo) {
            commit("UPDATE_SECONDARY_Info", secondaryDetailsInfo);
        },
        updateSecondaryVisible({commit}, secondaryDetailsVisible) {
            commit("UPDATE_SECONDARY_Visible", secondaryDetailsVisible);
        },
    }
};

export default operationManagement;