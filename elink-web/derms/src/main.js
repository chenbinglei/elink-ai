/* eslint-disable vue/multi-word-component-names */
import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";

//引入资源文档
import "@/permission"; // permissionU control
import "@/styles/main.scss";
// import "@/default-passive-events";
import "virtual:svg-icons-register"; // 注册svg图标
import 'virtual:uno.css'; // 引入unocss



//引入 ElementPlus
import ElementPlus from "element-plus";
import "element-plus/theme-chalk/index.css";
import zhCn from "element-plus/es/locale/lang/zh-cn";

// 加载
import VueInfiniteScroll from 'vue-infinite-scroll';

// 引入echarts
import * as echarts from "echarts";
import "echarts-liquidfill";

import filters from "@/common/filters"; // 过滤器
import { resizeDocument } from "@/common/directive/directive.js"; // 自定义全局指令

import customComponentInit from "@/components/index"; // 全局组件引入
import { loadFont } from "@/common/font"; // 字体引入

// 引入字体
requestIdleCallback(() => {
    loadFont(
        "ZiHunYaHei",
        "/fonts/ZiHunYaHei.ttf"
    );
    loadFont(
        "AgencyFB",
        "/fonts/AgencyFB.ttf"
    );
    // 新增 DIN 字体
    loadFont(
        "DIN-Medium",                // 自定义字体名称
        "/fonts/DIN-Medium-2.otf"    // 字体文件路径
    );
    loadFont(
        "YouSheBiaoTiHei",
        "/fonts/YouSheBiaoTiHei-2.ttf"
    );
}, {
    timeout: 10000,
})
// // 全局屏蔽 console 输出
console = {
  log: () => {},
  warn: () => {},
  error: () => {},
  info: () => {},
  debug: () => {},
  table: () => {},
  dir: () => {},
  clear: () => {},
  count: () => {},
  group: () => {},
  groupEnd: () => {},
  time: () => {},
  timeEnd: () => {},
  assert: () => {},
};
const pinia = createPinia();
const app = createApp(App);
// 全局注册图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

resizeDocument(app); //注册全局指令

customComponentInit(app); //注册自定义组件

//屏蔽警告信息
app.config.warnHandler = () => null;
app.config.globalProperties.echarts = echarts;
app.config.globalProperties.$filters = filters;

app.use(pinia).use(router).use(ElementPlus, { locale: zhCn }).use(VueInfiniteScroll).mount("#app");
