(function () {
  if (typeof window === "undefined") {
    return;
  }
  try {
    var config = {
      showSysLogo: false, // 是否显示系统logo
      systemName: "综合能源聚合管理平台", // 系统名称
      siteSystemName: "综合场站监控系统", // 单站点模式下系统名称
      systemVersion: "1.0.0", // 系统版本
    };
    Object.freeze(config);
    Object.seal(config);
    // 设置全局配置
    window.IEMSConfig = config;
  } catch (e) {
    console.error("Error setting IEMSConfig:", e);
  }
})();
