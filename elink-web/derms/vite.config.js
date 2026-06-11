import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";
import process from "process";
import UnoCSS from "unocss/vite";
import { createSvgIconsPlugin } from "vite-plugin-svg-icons";
import postcsspxtoviewport from "postcss-px-to-viewport"; // px转vw插件
import fs from "fs";
import { analyzer } from "vite-bundle-analyzer";

function resolve (dir) {
  return path.join(process.cwd(), dir);
}

// 自定义Vite插件：解析@elink/shared中的依赖到项目node_modules
function sharedResolvePlugin() {
  const sharedDir = path.resolve(__dirname, '../packages/shared/src');
  return {
    name: 'shared-resolve',
    resolveId(source, importer) {
      if (importer && importer.startsWith(sharedDir) && !source.startsWith('.') && !source.startsWith('@')) {
        const nodeModulesDir = path.resolve(__dirname, 'node_modules');
        const modulePath = path.join(nodeModulesDir, source);
        // 优先通过package.json main字段找到入口文件
        const pkgJsonPath = path.join(modulePath, 'package.json');
        if (fs.existsSync(pkgJsonPath)) {
          try {
            const pkg = JSON.parse(fs.readFileSync(pkgJsonPath, 'utf-8'));
            const mainFile = pkg.main || pkg.module || 'index.js';
            const mainPath = path.join(modulePath, mainFile);
            if (fs.existsSync(mainPath)) return mainPath;
          } catch (e) { /* ignore */ }
        }
        // 尝试index.js
        const indexPath = path.join(modulePath, 'index.js');
        if (fs.existsSync(indexPath)) return indexPath;
      }
      return null;
    }
  };
}

function bypass (req, res) {
  const mockUrl = path.join(
    process.cwd(),
    "/mock/",
    `${req.originalUrl.replace("/proxy", "")}.json`
  );
  const data = fs.readFileSync(mockUrl, {
    encoding: "utf-8",
    flag: "r",
  });
  res.write(data);
  res.end("");
}

export default defineConfig(({ mode }) => {
  return {
    base: "/", // 等同于 publicPath
    optimizeDeps: {
      include: [
        'echarts',
        'element-plus'
      ]
    },
    build: {
      outDir: "derms", // 等同于 outputDir
      assetsDir: "static", // 等同于 assetsDir
      sourcemap: false, // 等同于 productionSourceMap
      rollupOptions: {
        output: {
          manualChunks: (id) => {
            if (id.includes("echarts") || id.includes("zrender") || id.includes("element-plus")) {
              return "vendor";
            }
            return null;
          },
        },
      },
      terserOptions: {
        compress: {
          drop_console: true, // 移除所有 console 调用
        },
      },
    },
    plugins: [
      mode === "analyze" ? analyzer() : undefined,
      vue(),
      sharedResolvePlugin(),
      // 添加 UnoCSS 插件
      UnoCSS({
        configFile: resolve("unocss.config.ts"), // 指定 UnoCSS 配置文件
      }),
      // 注册所有的svg文件生成svg雪碧图
      createSvgIconsPlugin({
        iconDirs: [resolve("src/assets/svg")], // icon存放的目录
        symbolId: "icon-[name]", // symbol的id
        inject: "body-last", // 插入的位置
        customDomId: "__svg__icons__dom__", // svg的id
      }),
    ],
    resolve: {
      alias: {
        "@": resolve("src"),
        "@elink/shared": path.resolve(__dirname, '../packages/shared/src'),
        localStorage: resolve("./src/utils/localStorageUtil.js"),
      },
    },
    css: {
      devSourcemap: false, // 等同于 CSS sourceMap
      postcss: {
        plugins: [
          postcsspxtoviewport({
            unitToConvert: "px", // 要转化的单位
            viewportWidth: 1920, // UI设计稿的宽度
            unitPrecision: 6, // 转换后的精度，即小数点位数
            propList: ["*"], // 指定转换的css属性的单位，*代表全部css属性的单位都进行转换
            viewportUnit: "vw", // 指定需要转换成的视窗单位，默认vw
            fontViewportUnit: "vw", // 指定字体需要转换成的视窗单位，默认vw
            selectorBlackList: ["ignore-"], // 指定不转换为视窗单位的类名，
            minPixelValue: 1, // 默认值1，小于或等于1px则不进行转换
            mediaQuery: true, // 是否在媒体查询的css代码中也进行转换，默认false
            replace: true, // 是否转换后直接更换属性值
            exclude: [/node_modules/], // 设置忽略文件，用正则做目录名匹配
            landscape: false, // 是否处理横屏情况
          }),
        ],
      },
    },
    server: {
      host: "0.0.0.0",
      allowedHosts: ["2mb6pw894183.vicp.fun"],
      https: false,
      port: 9001,
      open: false,
      overlay: false, // 关闭 Uncaught runtime errors 弹框
      proxy: {
        // "/proxy/together/systemMonitor/getSystemTreeList": {
        //   target: "http://127.0.0.1:5500",
        //   bypass,
        // },
        // 请求目标服务器地址   http://192.168.2.158:5000  https://derms.enlinkitech.com  121.41.109.130
        "/proxy": {
          target: "http://192.168.2.158:5000",

          changeOrigin: true, // 是否跨域
          rewrite: (path) => path.replace(/^\/proxy/, ""),
          ws: true,
        },
      },
      cors: true, // 允许跨域
    },
    define: {
      __VUE_OPTIONS_API__: true,
      __VUE_PROD_DEVTOOLS__: false,
      __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: false,
    },
  };
});
