import {createApp} from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

//引入资源文档
import "@/permission"; // permissionU control
import "@/styles/main.scss";

// 引入echarts
import ECharts from "vue-echarts";
import * as echarts from "echarts";
import "echarts-liquidfill";

//引入 ElementPlus
import ElementPlus from "element-plus";
import "element-plus/theme-chalk/index.css";
import zhCn from "element-plus/es/locale/lang/zh-cn";

// 全局组件引入
import Tabs from '@/components/Tabs/Tabs'; //Tabs
import Dialog from "@/components/Dialog"; // 自定义弹框
import NullData from '@/components/component/NullData'; // 空数据
import TitleView from "@/components/component/TitleView"; // 自定义文本标题
import TabBackground from '@/components/Tabs/TabBackground'; //TabBorderCard
import HandleMenus from "@/components/handleMenu/HandleMenus"; //自定义操作菜单
import TableHeaderTitle from '@/components/component/TableHeaderTitle'; // 表格 或者列表 头部标题
import ElementPagination from '@/components/Pagination/ElementPagination'; //自定义表格分页

import {resizeDocument} from '@/common/directive/directive.js';// 自定义全局指令
import filters from "@/common/filters"; // 过滤器

const pinia = createPinia();
const app = createApp(App);

// 临时全局错误捕获，用于调试白屏问题
app.config.errorHandler = (err, instance, info) => {
  const div = document.createElement('div');
  div.style.cssText = 'position:fixed;top:0;left:0;right:0;z-index:99999;background:red;color:white;padding:10px;font-size:14px;word-break:break-all;';
  div.textContent = `[Vue Error] ${info}: ${err && err.message || err}`;
  document.body.appendChild(div);
};

resizeDocument(app); //注册全局指令

//注册自定义组件
app.component('Tabs', Tabs);
app.component('Dialog', Dialog);
app.component('v-chart', ECharts);
app.component('NullData', NullData);
app.component('TitleView', TitleView);
app.component('HandleMenus', HandleMenus);
app.component("TabBackground", TabBackground);
app.component('Pagination', ElementPagination);
app.component('TableHeaderTitle', TableHeaderTitle);


window.echarts = echarts;// 全局注册 echarts
app.config.warnHandler = () => null;  //屏蔽警告信息
app.config.globalProperties.echarts = echarts;
app.config.globalProperties.$filters = filters;

app.use(pinia).use(router).use(ElementPlus, {locale: zhCn}).mount("#app");
