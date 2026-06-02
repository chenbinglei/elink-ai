import { createStore } from 'vuex'

import app from './modules/app'
import sidebar from './modules/sidebar'
import tagsView from './modules/tagsView'

import getters from './getters'

export default createStore({
  modules: { app,sidebar,tagsView },
  getters
})
