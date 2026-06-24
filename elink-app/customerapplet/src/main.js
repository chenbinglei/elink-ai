import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

//设置html根元素字体大小
import Vant from 'vant';
import 'vant/lib/index.css';
import "@/utils/setFontSize";

// import * as echarts from "echarts";

// 自定义全局指令
import filters from "@/common/filters";
import { resizeDocument } from "@/common/directive/directive.js";

import NullData from "@/components/component/NullData";// 自定义空数据

const app = createApp(App);
resizeDocument(app); //注册全局指令

//屏蔽警告信息
// window.echarts = echarts;
app.config.warnHandler = () => null;
// app.config.globalProperties.echarts = echarts;
app.config.globalProperties.$filters = filters;

app.component("NullData", NullData);
app.use(store).use(router).use(Vant).mount('#app')
