import Vue from 'vue'
import Vuex from 'vuex'
Vue.use(Vuex)
const store = new Vuex.Store({
	state: {
		userInfo: null, //用户信息
		latAndLongitude: {
			cityName: '',
			centerLon: 120.15507, //中心经度
			centerLat: 30.274084 //中心纬度
		}, // 用户位置信息
		appConfig: {}, // 小程序配置信息
		
		authCode:"", //支付宝授权code
	},
	mutations: {
		TOGGLE_USERINFO: (state, userInfo) => {
			state.userInfo = userInfo;
		},
		TOGGLE_LATANDLON: (state, latAndLongitude) => {
			state.latAndLongitude = latAndLongitude;
		},
		TOGGLE_APPCONFIG: (state, appConfig) => {
			state.appConfig = appConfig;
		},
		TOGGLE_AUTHCODE: (state, authCode) => {
			state.authCode = authCode;
		},
	},
	actions: {
		// 修改用户信息
		alterUserInfo: ({ commit }, userInfo) => {
			commit('TOGGLE_USERINFO', userInfo)
		},
		// 修改用户位置信息
		alterlatAndLon: ({ commit }, latAndLongitude) => {
			commit('TOGGLE_LATANDLON', latAndLongitude)
		},
		// 修改App 配置信息
		alterAppConfig: ({ commit }, appConfig) => {
			commit('TOGGLE_APPCONFIG', appConfig)
		},
		// 支付宝授权code
		alterAuthCode: ({ commit }, authCode) => {
			commit('TOGGLE_AUTHCODE', authCode)
		},
	}
})
export default store
