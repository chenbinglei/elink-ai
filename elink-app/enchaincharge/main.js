import Vue from 'vue';
import App from './App';
import store from './store'; //引入vuex
import '@/utils/serverDomainName.js'; // 设置请求环境
import common from '@/common/common.js'; //配置公共方法
import filters from "@/common/filters.js"; //全局过滤器

//当前用户登录的平台
// #ifdef MP-WEIXIN
App.mpType = 'wechat';
Vue.prototype.$platform = 'wechat';
// #endif

// #ifdef MP-ALIPAY 
App.mpType = 'alipay';
Vue.prototype.$platform = 'alipay';
// #endif

// #ifdef APP-VUE || H5 
App.mpType = 'app';
Vue.prototype.$platform = 'app';
// #endif

Vue.config.productionTip = false;
Vue.prototype.$store = store; //把vuex挂载到全局
Vue.prototype.getStaticFilePath = (url) => { // 服务器静态资源 路径
	let static_path = "/image/app/";
	// if (uni.getStorageSync('BASE_URL').indexOf('jshqjk.com') !== -1) static_path = "/imageApp/app/";
	return uni.getStorageSync('STATIC_PATH') + static_path + url;
}

for (let key in filters) Vue.filter(key, filters[key]); //全局注册过滤器
for (let key in common) Vue.prototype[key] = common[key]; //全局挂载方法

const app = new Vue({
	...App
});

app.$mount()