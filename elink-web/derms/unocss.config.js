import { defineConfig, presetUno } from "unocss";

export default defineConfig({
  presets: [
    presetUno(), // 默认预设
  ],
  rules: [
    // 自定义规则
    [
      /^(text|placeholder)-(.*)$/,
      ([, c], { theme }) => {
        return { color: theme.colors[c] };
      },
    ],
    [
      /^family-(.*)$/,
      ([, f], { theme }) => {
        if (theme.fontFamily[f]) return { "font-family": theme.fontFamily[f],"font-display": "swap" };
      },
    ],
    [
      /^border-(.*)$/,
      ([, c], { theme }) => {
        if (theme.colors[c]) return { "border-color": theme.colors[c] };
      },
    ],
    [
      /^bg-(.*)$/,
      ([, c], { theme }) => {
        if (theme.colors[c]) return { "background-color": theme.colors[c] };
      },
    ],
    [
      /^bs-(.*)$/,
      ([, c], { theme }) => {
        if (theme.colors[c]) return { "--bs-color": theme.colors[c] };
      },
    ],
  ],
  theme: {
    // 自定义主题
    colors: {
      main: "#7DCBFF",
      second: "#0098DC",
      normal: "#30DE00", // 正常
      fail: "#FF0000", // 故障
      abnormal: "#AAAAAA", // 异常
      warn: "#FF8900", // 预警
    },
    fontFamily: {
      normal: "微软雅黑, Microsoft YaHei, sans-serif",
      zihun: "ZiHunYaHei, Microsoft YaHei, sans-serif",
      fb: "AgencyFB, fantasy, sans-serif"
    },
  },
  shortcuts: {
    // 自定义快捷方式
    btn: "px-4 py-2 rounded bg-blue-500 text-white hover:bg-blue-600",
      "flex-center": "flex items-center justify-center"
  },
});
