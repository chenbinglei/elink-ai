
import { createApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia'
import router from './router'

//引入资源文档
import "@/permission"; // permissionU control

// 引入echarts
import "echarts-liquidfill";
import ECharts from "vue-echarts";
import * as echarts from "echarts";

//引入 ElementPlus（必须在 main.scss 之前引入，确保自定义样式可覆盖默认样式）
import ElementPlus from "element-plus";
import "element-plus/theme-chalk/index.css";
import zhCn from "element-plus/es/locale/lang/zh-cn";

// 自定义全局样式（在 Element Plus 默认样式之后引入，确保覆盖 is-transparent 等默认行为）
import "@/styles/main.scss";

// 全局组件引入
import Tabs from '@/components/Tabs/Tabs'; //Tabs
import Dialog from "@/components/Dialog"; // 自定义弹框
import NullData from '@/components/component/NullData'; // 空数据
import TitleView from "@/components/component/TitleView"; // 自定义文本标题
import TabBorderCard from '@/components/Tabs/TabBorderCard'; //TabBorderCard
import TabBackground from '@/components/Tabs/TabBackground'; //TabBorderCard
import HandleMenus from "@/components/handleMenu/HandleMenus"; //自定义操作菜单
import ElementPagination from '@/components/Pagination/ElementPagination'; //自定义表格分页
import HandleTree from "@/components/handleTree/HandleTree";

import filters from "@/common/filters"; // 过滤器
import { resizeDocument } from '@/common/directive/directive.js';// 自定义全局指令

//  组织架构图
import vue3TreeOrg from 'vue3-tree-org';
import "vue3-tree-org/lib/vue3-tree-org.css";

import VxeUIBase from 'vxe-pc-ui'
import 'vxe-pc-ui/es/style.css'

import VxeUITable from 'vxe-table'
import 'vxe-table/es/style.css'

const pinia = createPinia();
const app = createApp(App);

// 全局错误捕获（仅控制台输出，不显示在页面上）
app.config.errorHandler = (err, instance, info) => {
  console.error('[Vue Error]', info, err);
};

resizeDocument(app); //注册全局指令

//注册自定义组件
app.component('Tabs', Tabs);
app.component("Dialog", Dialog);
app.component("v-chart", ECharts);
app.component('NullData', NullData);
app.component('TitleView', TitleView);
app.component("HandleMenus", HandleMenus);
app.component('HandleTree', HandleTree);
app.component("TabBackground", TabBackground);
app.component("TabBorderCard", TabBorderCard);
app.component('Pagination', ElementPagination);

//屏蔽警告信息
app.config.warnHandler = () => null;
app.config.globalProperties.echarts = echarts;
app.config.globalProperties.$filters = filters;

app.use(pinia).use(router).use(ElementPlus, { locale: zhCn }).use(vue3TreeOrg).use(VxeUIBase).use(VxeUITable).mount("#app");
