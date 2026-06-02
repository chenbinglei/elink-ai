import { createStore } from "vuex";

import app from "./modules/app";
import sidebar from "./modules/sidebar";
import tagsView from "./modules/tagsView";
import assetManagement from "./modules/assetManagement";
import energyManagement from "./modules/energyManagement";
import operationManagement from "./modules/operationManagement";
import monitor from "./modules/monitor";

import getters from "./getters";

export default createStore({
  modules: {
    app,
    sidebar,
    tagsView,
    assetManagement,
    operationManagement,
    energyManagement,
    monitor,
  },
  getters,
});
