import ModelController from "@/api/device/model";
import SystemMonitorController from "@/api/together/systemMonitor";

//
import { transformTreeData } from "@/utils/transform"; // 引入转换函数

// 监控模块
const monitor = {
  state: {
    siteId: "", //当前站点ID
    siteName: "", //当前站点名称
    isFullscreen: false, //是否全屏
    systemName: "", //系统名称
    assetTypeList: [], // 设备类型列表
    deviceTypeMap: {}, // 设备类型映射
    deviceFieldList: [], // 设备字段列表
    //
    authMenuList: null,
  },
  mutations: {
    UPDATE_IS_FULLSCREEN(state, isFullscreen) {
      state.isFullscreen = isFullscreen;
    },
    UPDATE_SITE_INFO(state, { siteId, siteName, systemName }) {
      Object.assign(state, {
        siteId,
        siteName,
        systemName: systemName?.substr(0, 13), //截取系统名称前12位
      });
    },
    UPDATE_ASSET_TYPE_LIST(state, assetTypeList) {
      state.assetTypeList = assetTypeList;
    },
    UPDATE_DEVICE_TYPE_MAP(state, deviceTypeMap) {
      state.deviceTypeMap = deviceTypeMap;
    },
    UPDATE_DEVICE_FIELD_LIST(state, deviceFieldList) {
      state.deviceFieldList = deviceFieldList;
    },
    UPDATE_AUTH_MENU_LIST(state, authMenuList) {
      state.authMenuList = authMenuList;
    },
  },
  //调用这里去触发mutations，如何调用？在组件内使用this.$store.dispatch('action中对应名字', 参数)
  actions: {
    updateIsFullscreen: ({ commit }, isFullscreen) => {
      commit("UPDATE_IS_FULLSCREEN", isFullscreen);
    },
    updateSiteInfo: ({ commit }, siteInfo) => {
      commit("UPDATE_SITE_INFO", siteInfo);
      const readwriteObjectStr = siteInfo.readwriteObject;
      let authMenuList;
      try {
        authMenuList = JSON.parse(readwriteObjectStr) ?? [];
      } catch (e) {
        authMenuList = [];
      }
      commit("UPDATE_AUTH_MENU_LIST", authMenuList);
    },
    // 获取设备类型列表
    getAssetTypeList: async ({ commit, state }) => {
      if (state.assetTypeList.length > 0) return; // 如果已经获取过设备类型列表，则不再请求
      try {
        const { success, data } = await ModelController.getAssetTypeList();
        if (success) {
          const deviceList = data.filter((item) => item.type === 2); // 过滤出设备类型
          const deviceTypeMap = deviceList.reduce((map, item) => {
            map[item.id] = item.typeName; // 将设备类型映射到 ID 上
            return map;
          }, {});
          commit("UPDATE_DEVICE_TYPE_MAP", deviceTypeMap); // 提交到 Vuex 状态管理
          const treeData = transformTreeData(data); // 转换为树形结构
          commit("UPDATE_ASSET_TYPE_LIST", treeData); // 提交到 Vuex 状态管理
        } else {
          console.error("获取设备类型列表失败:", data);
        }
      } catch (error) {
        console.error("获取设备类型列表失败:", error);
      }
    },
    // 获取设备字段列表
    getDeviceFieldList: async ({ commit }, siteId) => {
      try {
        const { success, data } =
          await SystemMonitorController.getDeviceFieldList(siteId);
        if (success) {
          const { functionList, nodeList } = data;
          // 功能点数据列表
          const functionNode = {
            value: "function",
            label: "功能点",
            children: [],
          };
          functionList.forEach((item) => {
            const { id, deviceName, fieldList } = item;
            const deviceNode = {
              parentId: functionNode.value,
              value: `${functionNode.value}_${id}`,
              label: deviceName,
              raw: item,
              children: fieldList.map((field) => ({
                parentId: `${functionNode.value}_${id}`,
                value: `${functionNode.value}_${id}_${field.fieldCode}`,
                label: field.fieldName,
                raw: field,
                children:
                  field.indexes?.length > 0
                    ? JSON.parse(field.indexes).map((index) => ({
                        parentId: `${functionNode.value}_${id}_${field.fieldCode}`,
                        value: `${functionNode.value}_${id}_${field.fieldCode}_${index}`,
                        label: `${index}`,
                        index,
                      }))
                    : [], // 处理索引数据
              })),
            };
            functionNode.children.push(deviceNode);
          });
          //计算节点数据列表
          const computeNode = {
            value: "compute",
            label: "计算节点",
            children: [],
          };
          nodeList.forEach((item) => {
            const { id, deviceName, fieldList } = item;
            const deviceNode = {
              parentId: computeNode.value,
              value: `${computeNode.value}_${id}`,
              label: deviceName,
              raw: item,
              children: fieldList.map((field) => ({
                parentId: `${computeNode.value}_${id}`,
                value: `${computeNode.value}_${id}_${field.fieldCode}`,
                label: field.fieldName,
                raw: field,
                children:
                  field.indexes?.length > 0
                    ? JSON.parse(field.indexes).map((index) => ({
                        parentId: `${computeNode.value}_${id}_${field.fieldCode}`,
                        value: `${computeNode.value}_${id}_${field.fieldCode}_${index}`,
                        label: `${index}`,
                        index,
                      }))
                    : [], // 处理索引数据
              })),
            };
            computeNode.children.push(deviceNode);
          });
          commit("UPDATE_DEVICE_FIELD_LIST", [functionNode, computeNode]); // 提交到 Vuex 状态管理
        } else {
          console.error("获取设备字段列表失败:", data);
        }
      } catch (error) {
        console.error("获取设备字段列表失败:", error);
      }
    },
  },
};

export default monitor;
