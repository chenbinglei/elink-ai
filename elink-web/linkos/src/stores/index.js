import { defineStore } from 'pinia';
import Cookies from 'js-cookie';

export const useAppStore = defineStore('app', {
  state: () => ({
    oldUserId: '',
    userInfo: null,
    permissionList: [],
    contentMainMaxHeight: 520,
    TopoNodeLocationValue: [],
  }),
  actions: {
    updateOldUserId(id) {
      this.oldUserId = id;
    },
    updateUserInfo(userInfo) {
      this.userInfo = userInfo;
    },
    updatePermissionList(permissionList) {
      this.permissionList = permissionList;
    },
    updateContentMainMaxHeight(contentMainMaxHeight) {
      this.contentMainMaxHeight = contentMainMaxHeight;
    },
    updateNewTopoNode(value) {
      this.TopoNodeLocationValue = value;
    },
  },
});

export const useSidebarStore = defineStore('sidebar', {
  state: () => ({
    isCollapse: !+Cookies.get('SUN_OS_SIDEBARSTATUS'),
  }),
  actions: {
    toggleSideBar() {
      if (this.isCollapse) {
        Cookies.set('SUN_OS_SIDEBARSTATUS', 1);
      } else {
        Cookies.set('SUN_OS_SIDEBARSTATUS', 0);
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
  }),
  actions: {
    addVisitedViews(view) {
      if (view.path === '/' || view.path === '/blank' || view.path === '/layout/blank') return;
      const viewData = { name: view.name, path: view.path, meta: view.meta, query: view.query };
      if (this.visitedviews.some(v => JSON.stringify(v) === JSON.stringify(viewData))) return;
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
        if (v.showButRoute === view.path && v.id === view.query.id) {
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
        return item.showButRoute === view.path && item.id === view.query.id;
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
