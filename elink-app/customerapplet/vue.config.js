const path = require("path");
const webpack = require("webpack");
const {defineConfig} = require("@vue/cli-service");
const CompressionPlugin = require("compression-webpack-plugin")

function resolve(dir) {
    return path.join(__dirname, dir);
}

module.exports = defineConfig({
    // 默认在生成的静态资源文件名中包含hash以控制缓存
    filenameHashing: true,
    transpileDependencies: true,
    // build时构建文件的目录 构建时传入 --no-clean 可关闭该行为
    outputDir: 'customerapplet',
    // build时放置生成的静态资源 (js、css、img、fonts) 的 (相对于 outputDir 的) 目录
    assetsDir: "static",
    // 是否在开发环境下通过 eslint-loader 在每次保存时 lint 代码 (在生产构建时禁用 eslint-loader)
    // lintOnSave: process.env.NODE_ENV !== 'production',
    lintOnSave: false,
    // 是否使用包含运行时编译器的 Vue 构建版本
    runtimeCompiler: false,
    productionSourceMap: false,
    // 设置生成的 HTML 中 <link rel="stylesheet"> 和 <script> 标签的 crossorigin 属性（注：仅影响构建时注入的标签）
    crossorigin: "",
    // 在生成的 HTML 中的 <link rel="stylesheet"> 和 <script> 标签上启用 Subresource Integrity (SRI)
    integrity: false,
    // 如果这个值是一个对象，则会通过 webpack-merge 合并到最终的配置中
    configureWebpack: {
        // 覆盖webpack默认配置的都在这里
        plugins: [
            new webpack.ProvidePlugin({
                localStorage: resolve("./src/utils/localStorageUtil.js"),
            })
        ],
    },
    css: {
        // 是否为 CSS 开启 source map。设置为 true 之后可能会影响构建的性能
        sourceMap: false,
        loaderOptions: {
            // 给 sass-loader 传递选项
            scss: {
                // 注意：在 sass-loader v8 中，这个选项名是 "prependData"
                additionalData: `
                    @import "~@/styles/public.scss";
                    @import "~@/styles/reset.scss";
                    @import "~@/styles/transition.scss";
                `,
            },
        },
    },
    chainWebpack: (config) => {
        config.plugin("html").tap((args) => {
            args[0].title = "打开小程序";
            return args;
        });

        // 生产环境，开启js\css压缩
        if (process.env.NODE_ENV === 'production') {
            config.plugin('compressionPlugin').use(new CompressionPlugin({
                test: /\.(js|css|less|map)$/, // 匹配文件名
                threshold: 1024, // 对超过10k的数据压缩
                minRatio: 0.8,
            }))
        }
    },
});
