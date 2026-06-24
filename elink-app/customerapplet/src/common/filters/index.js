import filter from "./filter";

// 有关数字 过滤处理文件
import filter_number from "./filter_number";

// 首页过滤器
import filter_home from "./filter_home";
// 场站监控过滤器
import filter_stations from "./filter_stations";

export default { ...filter, ...filter_number, ...filter_stations, ...filter_home };
