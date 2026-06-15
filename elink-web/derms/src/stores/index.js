import { defineStore } from 'pinia';
import Cookies from 'js-cookie';
import { removeToken } from '@/utils/auth';
import { getNowDateAll } from '@/utils/dateTime';
import ModelController from '@/api/device/model';
import SystemMonitorController from '@/api/together/systemMonitor';
import { transformTreeData } from '@/utils/transform';

export const useAppStore = defineStore('app', {
  state: () => ({
    oldUserId: '',
    userInfo: null,
    isSwitching: false,
    scale: 1,
  }),
  actions: {
    updateOldUserId(id) {
      this.oldUserId = id;
    },
    updateUserInfo(userInfo) {
      this.userInfo = userInfo;
    },
    exitSystem({ noReload } = {}) {
      this.oldUserId = '';
      this.userInfo = null;
      removeToken();
      if (!noReload) {
        location.reload();
      }
    },
  },
});

export const useSidebarStore = defineStore('sidebar', {
  state: () => ({
    isCollapse: !+Cookies.get('CHARGING_PF_SIDEBARSTATUS'),
  }),
  actions: {
    toggleSideBar() {
      if (this.isCollapse) {
        Cookies.set('CHARGING_PF_SIDEBARSTATUS', 1);
      } else {
        Cookies.set('CHARGING_PF_SIDEBARSTATUS', 0);
      }
      this.isCollapse = !this.isCollapse;
    },
  },
});

export const useTagsViewStore = defineStore('tagsView', {
  state: () => ({
    visitedviews: [],
    cachedViews: [],
    backButArray: [],
    routerIsSingleOpen: ['/orderManagement/orderAllRecord', '/orderManagement/orderRecordDetails'],
  }),
  actions: {
    addVisitedViews(view) {
      if (view.path === '/' || view.path === '/blank' || view.path === '/layout/blank') return;
      const viewData = { name: view.name, path: view.path, meta: view.meta, query: view.query };
      const findIndex = this.routerIsSingleOpen.findIndex(item => item === view.path);
      if (findIndex === -1) {
        if (this.visitedviews.some(v => JSON.stringify(v) === JSON.stringify(viewData))) return;
      } else {
        const findRouterIndex = this.visitedviews.findIndex(item => item.path === view.path);
        if (findRouterIndex !== -1) {
          this.visitedviews[findRouterIndex] = viewData;
          return;
        }
      }
      const pageNameArray = view.name.split('/');
      const pageName = pageNameArray[pageNameArray.length - 1];
      this.visitedviews.push(viewData);
      this.cachedViews.push(pageName);
    },
    delVisitedViews(view) {
      for (const [i, v] of this.visitedviews.entries()) {
        const viewData = { name: view.name, path: view.path, meta: view.meta, query: view.query };
        if (JSON.stringify(v) === JSON.stringify(viewData)) {
          this.visitedviews.splice(i, 1);
          this.cachedViews.splice(i, 1);
          break;
        }
      }
      for (const [i, v] of this.backButArray.entries()) {
        if (v.showButRoute === view.name && v.id === view.query.id) {
          this.backButArray.splice(i, 1);
          break;
        }
      }
      localStorage.setItem('TAGS_VIEW_BACK_ARRAY', JSON.stringify(this.backButArray));
      return [...this.visitedviews];
    },
    delOthersViews(view) {
      this.visitedviews = this.visitedviews.filter(item => {
        return JSON.stringify(item) === JSON.stringify(view);
      });
      this.cachedViews = this.cachedViews.filter(item => {
        return item === view.name;
      });
      this.backButArray = this.backButArray.filter(item => {
        return item.showButRoute === view.name && item.id === view.query.id;
      });
      localStorage.setItem('TAGS_VIEW_BACK_ARRAY', JSON.stringify(this.backButArray));
    },
    delAllRouteViews(views) {
      this.visitedviews = views;
      this.cachedViews = views;
      this.backButArray = views;
      localStorage.removeItem('TAGS_VIEW_BACK_ARRAY');
    },
    updateBackButViews(views) {
      this.backButArray = views;
    },
    addBackButViews(view) {
      if (this.backButArray.some(v => JSON.stringify(v) === JSON.stringify(view))) return;
      this.backButArray.push(view);
      localStorage.setItem('TAGS_VIEW_BACK_ARRAY', JSON.stringify(this.backButArray));
    },
  },
});

export const useAssetManagementStore = defineStore('assetManagement', {
  state: () => ({
    siteAllIds: [],
    activeSiteId: '',
    scenarioType: 1,
    updateTimeNum: getNowDateAll(),
  }),
  actions: {
    updateScenarioType(scenarioType) {
      this.scenarioType = scenarioType;
    },
    updateActiveSiteId(activeSiteId) {
      this.activeSiteId = activeSiteId;
    },
    updateSiteAllIds(siteAllIds = []) {
      this.siteAllIds = siteAllIds;
    },
    updateTimeNumFun(updateTimeNum) {
      this.updateTimeNum = updateTimeNum;
    },
  },
});

export const useOperationManagementStore = defineStore('operationManagement', {
  state: () => ({
    secondaryDetailsInfo: {},
    secondaryDetailsVisible: false,
  }),
  actions: {
    updateSecondaryInfo(secondaryDetailsInfo) {
      this.secondaryDetailsInfo = secondaryDetailsInfo;
    },
    updateSecondaryVisible(secondaryDetailsVisible) {
      this.secondaryDetailsVisible = secondaryDetailsVisible;
    },
  },
});

export const useEnergyManagementStore = defineStore('energyManagement', {
  state: () => ({
    strategyName: '',
    issueStrategyTime: '',
    activeSlSiteInfo: {},
  }),
  actions: {
    updateIssueStrategyTime(issueTime) {
      this.issueStrategyTime = issueTime;
    },
    updateStrategyName(strategyName) {
      this.strategyName = strategyName;
    },
    updateActiveSlSiteInfo(activeSlSiteInfo) {
      this.activeSlSiteInfo = activeSlSiteInfo;
    },
  },
});

export const useMonitorStore = defineStore('monitor', {
  state: () => ({
    siteId: '',
    siteName: '',
    isFullscreen: false,
    systemName: '',
    assetTypeList: [],
    deviceTypeMap: {},
    deviceFieldList: [],
    authMenuList: null,
  }),
  actions: {
    updateIsFullscreen(isFullscreen) {
      this.isFullscreen = isFullscreen;
    },
    updateSiteInfo(siteInfo) {
      this.siteId = siteInfo.siteId;
      this.siteName = siteInfo.siteName;
      this.systemName = siteInfo.systemName?.substr(0, 13);
      const readwriteObjectStr = siteInfo.readwriteObject;
      let authMenuList;
      try {
        authMenuList = JSON.parse(readwriteObjectStr) ?? [];
      } catch (e) {
        authMenuList = [];
      }
      this.authMenuList = authMenuList;
    },
    async getAssetTypeList() {
      if (this.assetTypeList.length > 0) return;
      try {
        const { success, data } = await ModelController.getAssetTypeList();
        if (success) {
          const deviceList = data.filter(item => item.type === 2);
          const deviceTypeMap = deviceList.reduce((map, item) => {
            map[item.id] = item.typeName;
            return map;
          }, {});
          this.deviceTypeMap = deviceTypeMap;
          const treeData = transformTreeData(data);
          this.assetTypeList = treeData;
        }
      } catch (error) {
        console.error('获取设备类型列表失败:', error);
      }
    },
    async getDeviceFieldList(siteId) {
      try {
        const { success, data } = await SystemMonitorController.getDeviceFieldList(siteId);
        if (success) {
          const { functionList, nodeList } = data;
          const functionNode = {
            value: 'function',
            label: '功能点',
            children: [],
          };
          functionList.forEach(item => {
            const { id, deviceName, fieldList } = item;
            const deviceNode = {
              parentId: functionNode.value,
              value: `function_${id}`,
              label: deviceName,
              raw: item,
              children: fieldList.map(field => ({
                parentId: `function_${id}`,
                value: `function_${id}_${field.fieldCode}`,
                label: field.fieldName,
                raw: field,
                children: field.indexes?.length > 0
                  ? JSON.parse(field.indexes).map(index => ({
                    parentId: `function_${id}_${field.fieldCode}`,
                    value: `function_${id}_${field.fieldCode}_${index}`,
                    label: `${index}`,
                    index,
                  }))
                  : [],
              })),
            };
            functionNode.children.push(deviceNode);
          });
          const computeNode = {
            value: 'compute',
            label: '计算节点',
            children: [],
          };
          nodeList.forEach(item => {
            const { id, deviceName, fieldList } = item;
            const deviceNode = {
              parentId: computeNode.value,
              value: `compute_${id}`,
              label: deviceName,
              raw: item,
              children: fieldList.map(field => ({
                parentId: `compute_${id}`,
                value: `compute_${id}_${field.fieldCode}`,
                label: field.fieldName,
                raw: field,
                children: field.indexes?.length > 0
                  ? JSON.parse(field.indexes).map(index => ({
                    parentId: `compute_${id}_${field.fieldCode}`,
                    value: `compute_${id}_${field.fieldCode}_${index}`,
                    label: `${index}`,
                    index,
                  }))
                  : [],
              })),
            };
            computeNode.children.push(deviceNode);
          });
          this.deviceFieldList = [functionNode, computeNode];
        }
      } catch (error) {
        console.error('获取设备字段列表失败:', error);
      }
    },
  },
});
