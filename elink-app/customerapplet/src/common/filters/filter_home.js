
// 首页过滤器
export default {
  // 组件类型
  home_component_type(value) {
    switch (String(value)) {
      case "1":
        return "运行数据类";
      case "11":
        return "运行数据类-功率";
      case "12":
        return "运行数据类-电量";
      case "13":
        return "运行数据类-状态";
      case "2":
        return "运营数据类";
      case "21":
        return "运营数据类-金额";
      case "22":
        return "运营数据类-次数";
      case "3":
        return "运维数据类";
      case "31":
        return "运维数据类-告警";
      case "4":
        return "资产数据类";
      case "41":
        return "资产数据类-资产";
      case "5":
        return "其他类";
      case "51":
        return "其他类-面板";
      default:
        return value;
    }
  },
  //组件图库类型
  home_icon_type(value) {
    switch (String(value)) {
      case "1":
        return "折线图";
      case "2":
        return "柱状图";
      case "3":
        return "饼图";
      case "4":
        return "仪表盘";
      case "5":
        return "指标面板";
      case "6":
        return "其他";
      default:
        return value;
    }
  },
};
