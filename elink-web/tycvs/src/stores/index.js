import { defineStore } from 'pinia';
import Cookies from 'js-cookie';

export const useAppStore = defineStore('app', {
  state: () => ({
    oldUserId: '',
    userInfo: null,
  }),
  actions: {
    updateOldUserId(id) {
      this.oldUserId = id;
    },
    updateUserInfo(userInfo) {
      this.userInfo = userInfo;
    },
  },
});

export const useSidebarStore = defineStore('sidebar', {
  state: () => ({
    isCollapse: !+Cookies.get('TY_CANVAS_SIDEBARSTATUS'),
  }),
  actions: {
    toggleSideBar() {
      if (this.isCollapse) {
        Cookies.set('TY_CANVAS_SIDEBARSTATUS', 1);
      } else {
        Cookies.set('TY_CANVAS_SIDEBARSTATUS', 0);
      }
      this.isCollapse = !this.isCollapse;
    },
  },
});

export const useTagsViewStore = defineStore('tagsView', {
  state: () => ({
    visitedViews: [],
    cachedViews: [],
    backButArray: [],
    routerIsSingleOpen: ['/orderManagement/orderAllRecord', '/orderManagement/orderRecordDetails'],
  }),
  actions: {
    addvisitedViews(view) {
      if (view.path === '/' || view.path === '/blank' || view.path === '/layout/blank') return;
      const viewData = { name: view.name, path: view.path, meta: view.meta, query: view.query };
      const findIndex = this.routerIsSingleOpen.findIndex(item => item === view.path);
      if (findIndex === -1) {
        if (this.visitedViews.some(v => JSON.stringify(v) === JSON.stringify(viewData))) return;
      } else {
        const findRouterIndex = this.visitedViews.findIndex(item => item.path === view.path);
        if (findRouterIndex !== -1) {
          this.visitedViews[findRouterIndex] = viewData;
          return;
        }
      }
      const pageNameArray = view.name.split('/');
      const pageName = pageNameArray[pageNameArray.length - 1];
      this.visitedViews.push(viewData);
      this.cachedViews.push(pageName);
    },
    delvisitedViews(view) {
      for (const [i, v] of this.visitedViews.entries()) {
        const viewData = { name: view.name, path: view.path, meta: view.meta, query: view.query };
        if (JSON.stringify(v) === JSON.stringify(viewData)) {
          this.visitedViews.splice(i, 1);
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
      return [...this.visitedViews];
    },
    delOthersViews(view) {
      this.visitedViews = this.visitedViews.filter(item => {
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
      this.visitedViews = views;
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

export const useMeta2dStore = defineStore('meta2d', {
  state: () => ({
    canvasMeta2d: null,
    canvasMeta2dAllLoad: false,
    drawLineStatus: false,
    canvasEyeMap: false,
    canvas_scale: 0.55,
    disableScale: false,
    canvasLocked: 0,
    current_active_pel_num: 0,
    current_active_pel_list: [],
    canvasMeta2dData: null,
    customCanvasOptionsList: [
      { name: '背景颜色', fieldName: 'background', default: '#FFFFFF' },
      { name: '画笔默认颜色', fieldName: 'color', default: '#222222' },
      { name: '宽度', fieldName: 'width', default: 1920 },
      { name: '高度', fieldName: 'height', default: 1080 },
      { name: '缩放方式', fieldName: 'scaleMode', default: '1' },
      { name: '主题', fieldName: 'theme', default: 'light' },
      { name: '禁止缩放', fieldName: 'isDisableScale', default: false },
      { name: '显示滚动条', fieldName: 'isScroll', default: false },
      { name: '禁止移动', fieldName: 'isDisableTranslate', default: false },
      { name: '连线相交弯曲', fieldName: 'lineCross', default: false },
    ],
    canvasModeType: 'normal',
    setValueDeleteFieldList: ['x', 'y', 'ex', 'ey', 'width', 'height', 'rotate'],
  }),
  actions: {
    initCanvasMeta2d(canvasMeta2d) {
      this.canvasMeta2d = canvasMeta2d;
    },
    updateCanvasMeta2dAllLoad(canvasMeta2dAllLoad) {
      this.canvasMeta2dAllLoad = canvasMeta2dAllLoad;
    },
    updateDrawLineStatus(drawLineStatus) {
      this.drawLineStatus = drawLineStatus;
    },
    updateCanvasScale(canvas_scale) {
      this.canvas_scale = canvas_scale;
    },
    updateDisableScale(disableScale) {
      this.disableScale = disableScale;
    },
    updateCurActivePelNum(current_active_pel_num) {
      this.current_active_pel_num = current_active_pel_num;
    },
    updateCurActivePelList(current_active_pel_list = []) {
      if (JSON.stringify(this.current_active_pel_list) !== JSON.stringify(current_active_pel_list)) {
        this.current_active_pel_list = current_active_pel_list;
      }
    },
    updateCanvasMeta2dData(canvasMeta2dData) {
      this.canvasMeta2dData = canvasMeta2dData;
    },
    updateCanvasLocked(canvasLocked) {
      this.canvasLocked = canvasLocked;
    },
    updateCanvasModeType(canvasModeType) {
      this.canvasModeType = canvasModeType;
    },
    updateCanvasEyeMap(canvasEyeMap) {
      this.canvasEyeMap = canvasEyeMap;
    },
  },
});
