import {createStore} from 'vuex'
import getters from './getters'

import app from './modules/app'
import meta2d from './modules/meta2d'
import sidebar from './modules/sidebar'
import tagsView from './modules/tagsView'

export default createStore({
    modules: {app, sidebar, tagsView, meta2d},
    getters
})
