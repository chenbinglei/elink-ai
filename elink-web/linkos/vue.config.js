const { defineConfig } = require('@vue/cli-service');
const webpack = require('webpack');
const path = require("path");

function resolve(dir) {
  return path.join(__dirname, dir);
}

module.exports = defineConfig({
  outputDir: 'sunos',
  assetsDir: "static",  // build时放置生成的静态资源 (js、css、img、fonts) 的 (相对于 outputDir 的) 目录
  filenameHashing: true,  // 默认在生成的静态资源文件名中包含hash以控制缓存
  transpileDependencies: true,
  lintOnSave: false,  // lintOnSave: process.env.NODE_ENV !== 'production',
  // 如果你不需要生产环境的 source map，可以将其设置为 false 以加速生产环境构建
  productionSourceMap: false,
  // 在生成的 HTML 中的 <link rel="stylesheet"> 和 <script> 标签上启用 Subresource Integrity (SRI)
  integrity: false,
  configureWebpack: {
    // 覆盖webpack默认配置的都在这里
    plugins: [
      new webpack.ProvidePlugin({
        localStorage: resolve("./src/utils/localStorageUtil.js")
      })
    ],
    resolve: {
      alias: {
        '@elink/shared': path.resolve(__dirname, '../packages/shared/src')
      }
    }
  },
  css: {
    extract: false,
    // 是否为 CSS 开启 source map。设置为 true 之后可能会影响构建的性能
    sourceMap: false,
    //向 CSS 相关的 loader 传递选项(支持 css-loader postcss-loader sass-loader less-loader stylus-loader)
    loaderOptions: {}
  },
  chainWebpack: (config) => {
    config.plugin("html").tap((args) => {
      args[0].title = "设备管理";
      return args;
    });
  },
  // 所有 webpack-dev-server 的选项都支持
  devServer: {
    https: false,
    host: '0.0.0.0',
    port: 9000,
    proxy: {
      '/proxy': {
        target: 'http://192.168.2.158:5000',
        changeOrigin: true,
        pathRewrite: { '^/proxy': '' },
        ws: true,
      }
    },
    client: {
      overlay: false,
    }
  }
})
