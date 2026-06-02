// 引入echarts
import ECharts from "vue-echarts";
import "echarts-liquidfill";
// 全局组件引入
import Tabs from "@/components/Tabs/Tabs.vue"; //Tabs
import Dialog from "@/components/Dialog/index.vue"; // 自定义弹框
import TextTabs from "@/components/Tabs/TextTabs.vue"; // 文字切换样式
import SvgIcon from "@/components/component/SvgIcon.vue"; //引入SVG组件
import NullData from "@/components/component/NullData.vue"; // 空数据
import ButtonsTabs from "@/components/Tabs/ButtonsTabs.vue"; // 按钮组切换样式
import TitleView from "@/components/component/TitleView.vue"; // 标题组件
import TitleIconView from "@/components/component/TitleIconView.vue"; // 标题组件
import TriangleArrowTabs from "@/components/Tabs/TriangleArrowTabs.vue"; // 切换样式显示三角箭头
import TableHeaderTitle from "@/components/component/TableHeaderTitle.vue"; // 表格 或者列表 头部标题
import ElementPagination from "@/components/Pagination/ElementPagination.vue"; //自定义表格分页
import LineTabs from "@/components/Tabs/lineTabs.vue";
export default (app) => {
  //注册自定义组件
  app.component("Tabs", Tabs);
  app.component("Dialog", Dialog);
  app.component("v-chart", ECharts);
  app.component("SvgIcon", SvgIcon);
  app.component("null-data", NullData);
  app.component("TextTabs", TextTabs);
  app.component("TitleView", TitleView);
  app.component("ButtonsTabs", ButtonsTabs);
  app.component("Pagination", ElementPagination);
  app.component("TitleIconView", TitleIconView);
  app.component("TableHeaderTitle", TableHeaderTitle);
  app.component("triangle-arrow-tabs", TriangleArrowTabs);
  app.component("line-tabs", LineTabs);
};
