import Cookies from 'js-cookie';

const sidebar = {
    state: {
        isCollapse: !+Cookies.get('CHARGING_PF_SIDEBARSTATUS'),
    },
    mutations: {
        TOGGLE_SIDEBAR: state => {
            if (state.isCollapse) {
                Cookies.set('CHARGING_PF_SIDEBARSTATUS', 1);
            } else {
                Cookies.set('CHARGING_PF_SIDEBARSTATUS', 0);
            }
            state.isCollapse = !state.isCollapse;
        }
    },
    actions: {
        toggleSideBar: ({ commit }) => {
            commit('TOGGLE_SIDEBAR');
        }
    }
};

export default sidebar;
