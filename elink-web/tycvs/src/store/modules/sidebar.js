import Cookies from 'js-cookie'

const sidebar = {
    state: {
        isCollapse: !+Cookies.get('TY_CANVAS_SIDEBARSTATUS'),
    },
    mutations: {
        TOGGLE_SIDEBAR: state => {
            if (state.isCollapse) {
                Cookies.set('TY_CANVAS_SIDEBARSTATUS', 1)
            } else {
                Cookies.set('TY_CANVAS_SIDEBARSTATUS', 0)
            }
            state.isCollapse = !state.isCollapse
        }
    },
    actions: {
        toggleSideBar: ({ commit }) => {
            commit('TOGGLE_SIDEBAR')
        }
    }
}

export default sidebar
