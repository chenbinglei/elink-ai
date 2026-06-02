// 图形数据列表
export const basicGraphicsCom = [
    {
        type: 1,
        fieldNameCn: "基础图形",
        fieldNameEn: "basicGraphics",
        children: [
            {
                name: "正方形",
                icon: "iconfont icon-zhengfangxing",
                data: {
                    width: 100,
                    height: 100,
                    name: "square"
                }
            },
            {
                name: "矩形",
                icon: "iconfont icon-juxing",
                data: {
                    width: 200,
                    height: 50,
                    borderRadius: 0.1,
                    name: "rectangle"
                }
            },
            {
                name: "圆",
                icon: "iconfont icon-circle",
                data: {
                    width: 100,
                    height: 100,
                    name: "circle",
                    textMaxLine: 1
                }
            },
            {
                name: "三角形",
                icon: "iconfont icon-sanjiaoxing",
                data: {
                    width: 80,
                    height: 80,
                    name: "triangle"
                }
            },
            {
                name: "菱形",
                icon: "iconfont icon-lingxing",
                data: {
                    width: 80,
                    height: 80,
                    name: "diamond"
                }
            },
            {
                name: "五边形",
                icon: "iconfont icon-wubianxing",
                data: {
                    width: 100,
                    height: 100,
                    name: "pentagon"
                }
            },
            {
                name: "六边形",
                icon: "iconfont icon-liubianxing",
                data: {
                    width: 100,
                    height: 100,
                    name: "hexagon"
                }
            },
            {
                name: "左箭头",
                icon: "iconfont icon-arrow-left",
                data: {
                    width: 120,
                    height: 60,
                    name: "leftArrow"
                }
            },
            {
                name: "右箭头",
                icon: "iconfont icon-arrow-right",
                data: {
                    width: 120,
                    height: 60,
                    name: "rightArrow"
                }
            },
            {
                name: "双向箭头",
                icon: "iconfont icon-double-arrow",
                data: {
                    width: 150,
                    height: 60,
                    name: "twowayArrow"
                }
            },
            {
                name: "立方体",
                icon: "iconfont icon-lifangti",
                data: {
                    width: 100,
                    height: 100,
                    name: "cube"
                }
            },
            {
                name: "云",
                icon: "iconfont icon-cloud",
                data: {
                    width: 100,
                    height: 100,
                    name: "cloud"
                }
            },
            {
                name: "消息框",
                icon: "iconfont icon-xiaoxikuang",
                data: {
                    width: 100,
                    height: 100,
                    name: "message"
                }
            },
            {
                name: "文档",
                icon: "iconfont icon-wenjian",
                data: {
                    width: 80,
                    height: 100,
                    name: "file"
                }
            },
        ]
    },
    {
        type: 1,
        fieldNameCn: "流程图",
        fieldNameEn: "flowChart",
        children: [
            {
                name: "开始/结束",
                icon: "iconfont icon-yuanjiaojuxing",
                data: {
                    width: 120,
                    height: 40,
                    borderRadius: 0.5,
                    name: "rectangle",
                    text: "开始/结束"
                }
            },
            {
                name: "流程",
                icon: "iconfont icon-juxing",
                data: {
                    width: 120,
                    height: 40,
                    name: "rectangle",
                    text: "流程"
                }
            },
            {
                name: "判定",
                icon: "iconfont icon-panding",
                data: {
                    width: 120,
                    height: 50,
                    name: "diamond",
                    text: "判定"
                }
            },
            {
                name: "数据",
                icon: "iconfont icon-pinghangsibianxing",
                data: {
                    width: 120,
                    height: 50,
                    name: "flowData",
                    text: "数据"
                }
            },
            {
                name: "准备",
                icon: "iconfont icon-zhunbei font-weight-bold",
                data: {
                    width: 120,
                    height: 50,
                    name: "hexagon",
                    text: "准备"
                }
            },
            {
                name: "子流程",
                icon: "iconfont icon-ziliucheng",
                data: {
                    width: 100,
                    height: 50,
                    name: "flowSubprocess"
                }
            },
            {
                name: "数据库",
                icon: "iconfont icon-yuanzhu",
                data: {
                    width: 50,
                    height: 100,
                    name: "flowDb"
                }
            },
            {
                name: "队列",
                icon: "iconfont icon-duilie",
                data: {
                    width: 100,
                    height: 100,
                    name: "flowQueue",
                    text: "队列"
                }
            },
            {
                name: "并行模式",
                icon: "iconfont icon-binghangmoshi",
                data: {
                    width: 120,
                    height: 50,
                    name: "flowParallel",
                    text: "并行模式"
                }
            },
        ]
    },
]


// 控件数据列表
export const customControlCom = [
    {
        type: 1,
        fieldNameCn: "基础",
        fieldNameEn: "basis",
        children: [
            {
                name: "文本",
                icon: "iconfont icon-wenben",
                data: {
                    width: 120,
                    height: 30,
                    name: "text",
                    text: "晟曼电力科技",
                    // textAutoAdjust: true,
                    whiteSpace: "break-all",
                }
            },
            {
                name: "数字",
                icon: "iconfont icon-shuzi",
                data: {
                    width: 120,
                    height: 30,
                    name: "text",
                    // text: "3.1415926",
                    text: "-/-",
                    keepDecimal: 2,
                    inputType: "number",
                    whiteSpace: "break-all",
                }
            },
            {
                name: "进度条",
                icon: "iconfont icon-jindutiao",
                data: {
                    width: 150,
                    height: 5,
                    name: "rectangle",
                    progress: 0.62,
                    borderRadius: 0.5,
                    background: "#303746",
                    progressColor: "#4583FF",
                    disableAnchor: true,
                    disableInput: true,
                    borderWidth: 0,
                }
            },
            {
                name: "ICON",
                icon: "iconfont icon-iconName",
                data: {
                    width: 80,
                    height: 80,
                    name: "image",
                    icon: "\ue61d",
                    iconFamily: "iconfont",
                }
            },
            {
                name: "图片",
                icon: "iconfont icon-tupian",
                data: {
                    width: 80,
                    height: 80,
                    name: "image",
                    imageRatio: true,
                    crossOrigin: "undefined",
                    image: require("@/assets/meta2d/image.png")
                }
            },
            {
                name: "GIF",
                icon: "iconfont icon-GIF",
                data: {
                    width: 80,
                    height: 80,
                    name: "gif",
                    imageRatio: true,
                    crossOrigin: "undefined",
                    image: require("@/assets/meta2d/firewall.gif")
                }
            },
        ]
    },
    {
        type: 1,
        fieldNameCn: "音视频",
        fieldNameEn: "audio-visual",
        children: [
            {
                name: "视频",
                icon: "iconfont icon-shipin",
                data: {
                    width: 200,
                    height: 200,
                    name: "video",
                    autoPlay: true,
                    videoType: "video",
                    externElement: true,
                    video: require("@/assets/image/video.mp4")
                }
            },
            {
                name: "FLV视频流",
                icon: "iconfont icon-shipinliu",
                data: {
                    width: 200,
                    height: 200,
                    autoPlay: true,
                    optionalConfig: {},
                    mediaDataSource: {},
                    externElement: true,
                    name: "flvPlayerDom",
                    video: "https://sf1-hscdn-tos.pstatp.com/obj/media-fe/xgplayer_doc_video/flv/xgplayer-demo-360p.flv",
                }
            },
            {
                name: "音频",
                icon: "iconfont icon-yinpin",
                data: {
                    width: 200,
                    height: 20,
                    name: "video",
                    autoPlay: true,
                    videoType: "audio",
                    externElement: true,
                    audio: require("@/assets/image/audio.mp3")
                }
            },
            {
                name: "摄像头",
                icon: "iconfont icon-shexiangtou",
                data: {
                    mse: false,
                    width: 200,
                    height: 200,
                    autoPlay: true,
                    externElement: true,
                    name: "rtspPlayerDom",
                }
            },
        ]
    },
    {
        type: 1,
        fieldNameCn: "时间",
        fieldNameEn: "time",
        children: [
            {
                name: "时间",
                icon: "iconfont icon-shijian",
                data: {
                    width: 300,
                    height: 40,
                    text: "时间",
                    name: "time",
                    fillZero: true,
                    timeFormat: "`${year}-${month}-${day} ${hours}:${minutes}:${seconds} 星期${week}`",
                }
            },
            {
                name: "倒计时",
                icon: "iconfont icon-naozhong",
                data: {
                    width: 300,
                    height: 40,
                    text: "倒计时",
                    name: "countdown",
                    //deadline: "2025/1/1 00:00:00", //配置未来的时间，不填默认是下一年
                    timeFormat: "`距离下一年还有：${day}天${hours}时${minutes}分${seconds}秒`",
                }
            },
        ]
    },
    {
        type: 1,
        fieldNameCn: "面板",
        fieldNameEn: "panel",
        children: [
            // {
            //     name: "列表",
            //     icon: "iconfont icon-liebiaofenlei",
            //     data: {}
            // },
            // {
            //     name: "表格",
            //     icon: "iconfont icon-biaoge",
            //     data: {}
            // },
            // {
            //     name: "斑马纹表格",
            //     icon: "iconfont icon-biaoge",
            //     data: {}
            // },
            // {
            //     name: "滚动表格",
            //     icon: "iconfont icon-biaoge",
            //     data: {}
            // },
            // {
            //     name: "树",
            //     icon: "iconfont icon-wenjianshu",
            //     data: {}
            // },
            {
                name: "网页",
                icon: "iconfont icon-wangye",
                data: {
                    width: 500,
                    height: 400,
                    name: "iframe",
                    externElement: true,
                    operationalRect: {},
                    iframe: "https://www.baidu.com",
                }
            },
        ]
    },
    {
        type: 1,
        fieldNameCn: "提醒",
        fieldNameEn: "remind",
        children: [
            {
                name: "全局消息",
                icon: "iconfont icon-tixing",
                data: {
                    width: 240,
                    height: 40,
                    borderRadius: 6,
                    name: "rectangle",
                    background: "rgba(255,125,30,0.2)",

                    textLeft: 32,
                    color: "#FF7D1E",
                    textAlign: "left",
                    textColor: "#FF7D1E",
                    hoverTextColor: "#FF7D1E",
                    text: "用于表示普通操作信息提示",

                    iconSize: 16,
                    iconLeft: 10,
                    icon: "\ue73e",
                    iconAlign: "left",
                    iconColor: "#FF7D1E",
                    iconFamily: "iconfont",
                }
            }
        ]
    },
    {
        type: 1,
        fieldNameCn: "表单",
        fieldNameEn: "form",
        children: [
            {
                name: "开关",
                icon: "iconfont icon-kaiguan",
                data: {
                    width: 60,
                    height: 30,
                    name: "switch",
                    checked: false,
                    onColor: "#1890ff",
                    offColor: "#BFBFBF",
                    disableOnColor: "#A3D3FF",
                    disableOffColor: "#E5E5E5",
                }
            },
            {
                name: "单选框",
                icon: "iconfont icon-danxuankuang",
                data: {
                    width: 360,
                    height: 36,
                    name: "radio",
                    checked: "选项一",
                    disableAnchor: true,
                    direction: "horizontal", // horizontal  vertical
                    options: [
                        {"text": "选项一", background: "#1890ff"},  // isForbidden ：是否禁用
                        {"text": "选项二", background: "#1890ff"},
                        {"text": "选项三", background: "#1890ff"}
                    ],
                }
            },
            {
                name: "日期选择器",
                icon: "iconfont icon-riqixuanzeqi",
                data: {

                }
            },
            {
                name: "日期区间选择器",
                icon: "iconfont icon-riqixuanzeqi",
                data: {

                }
            },
        ]
    },
    // {
    //     type: 1,
    //     fieldNameCn: "工控",
    //     fieldNameEn: "control",
    //     children: [
    // {
    //     name: "圆柱水位",
    //     icon: "iconfont icon-kaiguan",
    //     data: {
    //         width: 80,
    //         height: 120,
    //         name: "waterTank",
    //         progress: 0.26,
    //         progressColor: "#4583FF",
    //     }
    // },
    // {
    //     name: "球形水位",
    //     icon: "iconfont icon-kaiguan",
    //     data: {
    //         width: 60,
    //         height: 30,
    //         name: "watermeter",
    //     }
    // },
    // {
    //     name: "水箱",
    //     icon: "iconfont icon-kaiguan",
    //     data: {
    //         width: 60,
    //         height: 30,
    //         name: "tank",
    //     }
    // },
    // {
    //     name: "水柱温度计",
    //     icon: "iconfont icon-kaiguan",
    //     data: {
    //         width: 60,
    //         height: 30,
    //         name: "switch",
    //     }
    // },
    // {
    //     name: "扁平温度计",
    //     icon: "iconfont icon-kaiguan",
    //     data: {
    //         width: 60,
    //         height: 30,
    //         name: "switch",
    //     }
    // },
    // {
    //     name: "电池",
    //     icon: "iconfont icon-kaiguan",
    //     data: {
    //         width: 80,
    //         height: 120,
    //         name: "battery",
    //         gap: 1,
    //         min: 0,
    //         max: 100,
    //         splitNumber: 100,
    //         background: "#4583FF33",
    //         progressColor: "#58CC84",
    //     }
    // }
    // ]
    // }
]


// 图表数据列表
export const customChartCom = [
    {
        type: 1,
        fieldNameCn: "折线图",
        fieldNameEn: "lineChart",
        children: [
            {
                name: "基础折线图",
                icon: "iconfont icon-zhexiantu",
                data: {
                    width: 500,
                    height: 240,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    chartName: "lineChart",
                    name: "echartsComponent",
                    echarts: {
                        "option": {
                            "grid": {
                                "left": "0%",
                                "right": "0%",
                                "top": "22%",
                                "bottom": "2%",
                                "containLabel": true
                            },
                            "title": {
                                "top": 0,
                                "text": "标题",
                                "left": "center",
                                "textStyle": {
                                    "fontSize": 16,
                                    "color": "#9EA9B5"
                                }
                            },
                            "legend": {
                                "top": 0,
                                "right": 0,
                                "icon": "rect",
                                "itemGap": 15,
                                "itemWidth": 15,
                                "itemHeight": 10,
                                "textStyle": {
                                    "fontSize": 12,
                                    "color": "#9EA9B5"
                                }
                            },
                            "tooltip": {
                                "show": true,
                                "confine": true,
                                "trigger": "axis",
                                "borderColor": "rgba(255, 255, 255, 0.2)",
                                "backgroundColor": "rgba(255, 255, 255, 0.9)",
                                "textStyle": {
                                    "fontSize": 12,
                                    "fontWeight": 400,
                                    "color": "#94a1a8"
                                }
                            },
                            "xAxis": {
                                "type": "category",
                                "boundaryGap": false,
                                "data": ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
                                "axisLabel": {
                                    "fontSize": 14,
                                    "color": "#94a1a8"
                                },
                                "axisLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF05"
                                    }
                                },
                                "axisTick": {
                                    "show": false,
                                    "alignWithLabel": true
                                }
                            },
                            "yAxis": {
                                "type": "value",
                                "name": "单位",
                                "nameGap": 14,
                                "nameTextStyle": {
                                    "fontSize": 12,
                                    "align": "right"
                                },
                                "splitNumber": 4,
                                "splitLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF1A"
                                    }
                                },
                                "axisLabel": {
                                    "fontSize": 14,
                                    "textStyle": {
                                        "color": "#94a1a8"
                                    }
                                }
                            },
                            "series": [
                                {
                                    "name": "系列1",
                                    "type": "line",
                                    "smooth": true,
                                    "symbol": "none",
                                    "data": [22.97, 1.37, 49.71, 47.20, 79.12, 71.02, 1.37, 49.71, 47.20, 79.12, 71.02, 57.62]
                                }
                            ]
                        }
                    },
                }
            },
            {
                name: "渐变折线图",
                icon: "iconfont icon-zhexiantu",
                data: {
                    width: 500,
                    height: 240,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    chartName: "lineChart",
                    name: "echartsComponent",
                    echarts: {
                        "option": {
                            "grid": {
                                "left": "0%",
                                "right": "0%",
                                "top": "22%",
                                "bottom": "2%",
                                "containLabel": true
                            },
                            "title": {
                                "top": 0,
                                "text": "标题",
                                "left": "center",
                                "textStyle": {
                                    "fontSize": 16,
                                    "color": "#9EA9B5"
                                }
                            },
                            "legend": {
                                "top": 0,
                                "right": 0,
                                "icon": "rect",
                                "itemGap": 15,
                                "itemWidth": 15,
                                "itemHeight": 10,
                                "textStyle": {
                                    "fontSize": 12,
                                    "color": "#9EA9B5"
                                }
                            },
                            "tooltip": {
                                "confine": true,
                                "trigger": "axis",
                                "borderColor": "rgba(255, 255, 255, 0.2)",
                                "backgroundColor": "rgba(255, 255, 255, 0.9)",
                                "textStyle": {
                                    "fontSize": 12,
                                    "fontWeight": 400,
                                    "color": "#94a1a8"
                                }
                            },
                            "xAxis": {
                                "type": "category",
                                "data": ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
                                "axisLabel": {
                                    "fontSize": 14,
                                    "color": "#94a1a8"
                                },
                                "axisLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF05"
                                    }
                                },
                                "axisTick": {
                                    "show": false,
                                    "alignWithLabel": true
                                }
                            },
                            "yAxis": {
                                "type": "value",
                                "name": "单位",
                                "nameGap": 14,
                                "nameTextStyle": {
                                    "fontSize": 12,
                                    "align": "right"
                                },
                                "splitNumber": 4,
                                "splitLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF1A"
                                    }
                                },
                                "axisLabel": {
                                    "fontSize": 14,
                                    "textStyle": {
                                        "color": "#94a1a8"
                                    }
                                }
                            },
                            "series": [
                                {
                                    "name": "系列1",
                                    "type": "line",
                                    "smooth": true,
                                    "symbol": "none",
                                    "lineStyle": {
                                        "width": 2,
                                        "color": {
                                            "type": "linear",
                                            "x": 0,
                                            "y": 0,
                                            "x2": 1,
                                            "y2": 0,
                                            "colorStops": [
                                                {
                                                    "offset": 0,
                                                    "color": "rgba(69,131,255,0.0)"
                                                },
                                                {
                                                    "offset": 0.5,
                                                    "color": "rgba(69,131,255,1.0)"
                                                },
                                                {
                                                    "offset": 1,
                                                    "color": "rgba(69,131,255,0.0)"
                                                }
                                            ],
                                            "global": false
                                        }
                                    },
                                    "areaStyle": {
                                        "color": {
                                            "type": "linear",
                                            "x": 0,
                                            "y": 0,
                                            "x2": 1,
                                            "y2": 0,
                                            "colorStops": [
                                                {
                                                    "offset": 0,
                                                    "color": "rgba(69,131,255,0.0)"
                                                },
                                                {
                                                    "offset": 0.5,
                                                    "color": "rgba(69,131,255,0.50)"
                                                },
                                                {
                                                    "offset": 1,
                                                    "color": "rgba(69,131,255,0.0)"
                                                }
                                            ],
                                            "global": false
                                        }
                                    },
                                    "data": [22.97, 1.37, 49.71, 47.20, 79.12, 71.02, 1.37, 49.71, 47.20, 79.12, 71.02, 57.62]
                                }
                            ]
                        }
                    },
                }
            },
            {
                name: "阶梯折线图",
                icon: "iconfont icon-jietizhexiantu",
                data: {
                    width: 500,
                    height: 240,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    chartName: "lineChart",
                    name: "echartsComponent",
                    echarts: {
                        "option": {
                            "grid": {
                                "left": "0%",
                                "right": "0%",
                                "top": "22%",
                                "bottom": "2%",
                                "containLabel": true
                            },
                            "legend": {
                                "top": 0,
                                "right": 0,
                                "icon": "rect",
                                "itemGap": 15,
                                "itemWidth": 15,
                                "itemHeight": 10,
                                "textStyle": {
                                    "fontSize": 12,
                                    "color": "#9EA9B5"
                                }
                            },
                            "tooltip": {
                                "confine": true,
                                "trigger": "axis",
                                "borderColor": "rgba(255, 255, 255, 0.2)",
                                "backgroundColor": "rgba(255, 255, 255, 0.9)",
                                "textStyle": {
                                    "fontSize": 12,
                                    "fontWeight": 400,
                                    "color": "#94a1a8"
                                }
                            },
                            "xAxis": {
                                "type": "category",
                                "data": ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
                                "axisLabel": {
                                    "fontSize": 14,
                                    "color": "#94a1a8"
                                },
                                "axisLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF05"
                                    }
                                },
                                "axisTick": {
                                    "show": false,
                                    "alignWithLabel": true
                                }
                            },
                            "yAxis": {
                                "type": "value",
                                "name": "单位",
                                "nameGap": 14,
                                "nameTextStyle": {
                                    "fontSize": 12,
                                    "align": "right"
                                },
                                "splitNumber": 4,
                                "splitLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF1A"
                                    }
                                },
                                "axisLabel": {
                                    "fontSize": 14,
                                    "textStyle": {
                                        "color": "#94a1a8"
                                    }
                                }
                            },
                            "series": [
                                {
                                    "name": "系列1",
                                    "type": "line",
                                    "step": "start",
                                    "lineStyle": {
                                        "width": 2
                                    },
                                    "symbolSize": 5,
                                    "data": [22.97, 1.37, 49.71, 47.20, 79.12, 71.02, 1.37, 49.71, 47.20, 79.12, 71.02, 57.62]
                                }
                            ]
                        }
                    },
                }
            },
        ]
    },
    {
        type: 1,
        fieldNameCn: "柱状图",
        fieldNameEn: "barChart",
        children: [
            {
                name: "基础柱状图",
                icon: "iconfont icon-zhuzhuangtu",
                data: {
                    width: 500,
                    height: 240,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    chartName: "barChart",
                    name: "echartsComponent",
                    echarts: {
                        "max": 100,
                        "option": {
                            "grid": {
                                "left": "0%",
                                "right": "0%",
                                "top": "22%",
                                "bottom": "2%",
                                "containLabel": true
                            },
                            "title": {
                                "text": "标题",
                                "top": 0,
                                "left": "center",
                                "textStyle": {
                                    "fontSize": 16,
                                    "color": "#9EA9B5"
                                }
                            },
                            "legend": {
                                "top": 0,
                                "right": 0,
                                "icon": "rect",
                                "itemGap": 15,
                                "itemWidth": 15,
                                "itemHeight": 10,
                                "textStyle": {
                                    "fontSize": 12,
                                    "color": "#9EA9B5"
                                }
                            },
                            "tooltip": {
                                "confine": true,
                                "trigger": "axis",
                                "borderColor": "rgba(255, 255, 255, 0.2)",
                                "backgroundColor": "rgba(255, 255, 255, 0.9)",
                                "textStyle": {
                                    "fontSize": 12,
                                    "fontWeight": 400,
                                    "color": "#94a1a8"
                                }
                            },
                            "xAxis": {
                                "type": "category",
                                "data": ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
                                "axisLabel": {
                                    "fontSize": 14,
                                    "color": "#94a1a8"
                                },
                                "axisLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF05"
                                    }
                                },
                                "axisTick": {
                                    "show": false,
                                    "alignWithLabel": true
                                }
                            },
                            "yAxis": {
                                "type": "value",
                                "name": "单位",
                                "nameGap": 14,
                                "nameTextStyle": {
                                    "fontSize": 12,
                                    "align": "right"
                                },
                                "splitNumber": 4,
                                "splitLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF1A"
                                    }
                                },
                                "axisLabel": {
                                    "fontSize": 14,
                                    "textStyle": {
                                        "color": "#94a1a8"
                                    }
                                }
                            },
                            "series": [
                                {
                                    "name": "系列1",
                                    "type": "bar",
                                    "data": [22.97, 10.37, 49.71, 47.20, 68.12, 71.02, 18.37, 49.71, 47.20, 59.12, 62.02, 57.62]
                                }
                            ]
                        }
                    },
                }
            },
            {
                name: "堆叠柱状图",
                icon: "iconfont icon-duidiezhuzhuangtu",
                data: {
                    width: 500,
                    height: 240,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    chartName: "barChart",
                    name: "echartsComponent",
                    echarts: {
                        "max": 100,
                        "option": {
                            "grid": {
                                "left": "0%",
                                "right": "0%",
                                "top": "22%",
                                "bottom": "2%",
                                "containLabel": true
                            },
                            "legend": {
                                "top": 0,
                                "right": 0,
                                "icon": "rect",
                                "itemGap": 15,
                                "itemWidth": 15,
                                "itemHeight": 10,
                                "textStyle": {
                                    "fontSize": 12,
                                    "color": "#9EA9B5"
                                }
                            },
                            "tooltip": {
                                "confine": true,
                                "trigger": "axis",
                                "borderColor": "rgba(255, 255, 255, 0.2)",
                                "backgroundColor": "rgba(255, 255, 255, 0.9)",
                                "textStyle": {
                                    "fontSize": 12,
                                    "fontWeight": 400,
                                    "color": "#94a1a8"
                                }
                            },
                            "xAxis": {
                                "type": "category",
                                "data": ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
                                "axisLabel": {
                                    "fontSize": 14,
                                    "color": "#94a1a8"
                                },
                                "axisLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF05"
                                    }
                                },
                                "axisTick": {
                                    "show": false,
                                    "alignWithLabel": true
                                }
                            },
                            "yAxis": {
                                "type": "value",
                                "name": "单位",
                                "nameGap": 14,
                                "nameTextStyle": {
                                    "fontSize": 12,
                                    "align": "right"
                                },
                                "splitNumber": 4,
                                "splitLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF1A"
                                    }
                                },
                                "axisLabel": {
                                    "fontSize": 14,
                                    "textStyle": {
                                        "color": "#94a1a8"
                                    }
                                }
                            },
                            "series": [
                                {
                                    "name": "系列1",
                                    "type": "bar",
                                    "stack": "one",
                                    "emphasis": {
                                        "itemStyle": {
                                            "shadowBlur": 10,
                                            "shadowColor": "rgba(0,0,0,0.3)"
                                        },
                                    },
                                    "data": [22.97, 10.37, 49.71, 47.20, 68.12, 71.02, 18.37, 49.71, 47.20, 59.12, 62.02, 57.62]
                                },
                                {
                                    "name": "系列2",
                                    "type": "bar",
                                    "stack": "one",
                                    "emphasis": {
                                        "itemStyle": {
                                            "shadowBlur": 10,
                                            "shadowColor": "rgba(0,0,0,0.3)"
                                        },
                                    },
                                    "data": [49.71, 47.20, 45.62, 22.55, 28.78, 27.16, 11.58, 65.12, 58.02, 15.37, 49.71, 47.20]
                                }
                            ]
                        }
                    },
                }
            },
            {
                name: "基础条形图",
                icon: "iconfont icon-tiaoxingtu",
                data: {
                    width: 500,
                    height: 240,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    chartName: "basicBarChart",
                    name: "echartsComponent",
                    echarts: {
                        "max": 100,
                        "option": {
                            "grid": {
                                "left": "0%",
                                "right": "0%",
                                "top": "22%",
                                "bottom": "2%",
                                "containLabel": true
                            },
                            "legend": {
                                "top": 0,
                                "right": 0,
                                "icon": "rect",
                                "itemGap": 15,
                                "itemWidth": 15,
                                "itemHeight": 10,
                                "textStyle": {
                                    "fontSize": 12,
                                    "color": "#9EA9B5"
                                }
                            },
                            "tooltip": {
                                "confine": true,
                                "trigger": "axis",
                                "borderColor": "rgba(255, 255, 255, 0.2)",
                                "backgroundColor": "rgba(255, 255, 255, 0.9)",
                                "textStyle": {
                                    "fontSize": 12,
                                    "fontWeight": 400,
                                    "color": "#94a1a8"
                                }
                            },
                            "xAxis": {
                                "type": "value",
                                "splitNumber": 4,
                                "axisLabel": {
                                    "fontSize": 14,
                                    "color": "#94a1a8"
                                },
                                "axisLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF05"
                                    }
                                },
                                "axisTick": {
                                    "show": false,
                                    "alignWithLabel": true
                                }
                            },
                            "yAxis": {
                                "type": "category",
                                "data": ["1月", "2月", "3月", "4月", "5月", "6月"],
                                "splitLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF1A"
                                    }
                                },
                                "axisLabel": {
                                    "fontSize": 14,
                                    "textStyle": {
                                        "color": "#94a1a8"
                                    }
                                }
                            },
                            "series": [
                                {
                                    "name": "系列1",
                                    "type": "bar",
                                    "data": [22.97, 10.37, 49.71, 47.20, 68.12, 71.02]
                                }
                            ]
                        }
                    },
                }
            },
            {
                name: "堆叠条形图",
                icon: "iconfont icon-duixingtiaoxingtu",
                data: {
                    width: 500,
                    height: 240,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    chartName: "basicBarChart",
                    name: "echartsComponent",
                    echarts: {
                        "max": 100,
                        "option": {
                            "grid": {
                                "left": "0%",
                                "right": "0%",
                                "top": "22%",
                                "bottom": "2%",
                                "containLabel": true
                            },
                            "legend": {
                                "top": 0,
                                "right": 0,
                                "icon": "rect",
                                "itemGap": 15,
                                "itemWidth": 15,
                                "itemHeight": 10,
                                "textStyle": {
                                    "fontSize": 12,
                                    "color": "#9EA9B5"
                                }
                            },
                            "tooltip": {
                                "confine": true,
                                "trigger": "axis",
                                "borderColor": "rgba(255, 255, 255, 0.2)",
                                "backgroundColor": "rgba(255, 255, 255, 0.9)",
                                "textStyle": {
                                    "fontSize": 12,
                                    "fontWeight": 400,
                                    "color": "#94a1a8"
                                }
                            },
                            "xAxis": {
                                "type": "value",
                                "splitNumber": 4,
                                "axisLabel": {
                                    "fontSize": 14,
                                    "color": "#94a1a8"
                                },
                                "axisLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF05"
                                    }
                                },
                                "axisTick": {
                                    "show": false,
                                    "alignWithLabel": true
                                }
                            },
                            "yAxis": {
                                "type": "category",
                                "data": ["1月", "2月", "3月", "4月", "5月", "6月"],
                                "splitLine": {
                                    "lineStyle": {
                                        "color": "#FFFFFF1A"
                                    }
                                },
                                "axisLabel": {
                                    "fontSize": 14,
                                    "textStyle": {
                                        "color": "#94a1a8"
                                    }
                                }
                            },
                            "series": [
                                {
                                    "name": "系列1",
                                    "type": "bar",
                                    "stack": "one",
                                    "emphasis": {
                                        "itemStyle": {
                                            "shadowBlur": 10,
                                            "shadowColor": "rgba(0,0,0,0.3)"
                                        },
                                    },
                                    "data": [22.97, 10.37, 49.71, 47.20, 68.12, 71.02]
                                },
                                {
                                    "name": "系列2",
                                    "type": "bar",
                                    "stack": "one",
                                    "emphasis": {
                                        "itemStyle": {
                                            "shadowBlur": 10,
                                            "shadowColor": "rgba(0,0,0,0.3)"
                                        },
                                    },
                                    "data": [49.71, 47.20, 45.62, 22.55, 49.71, 47.20]
                                }
                            ]
                        }
                    },
                }
            },
        ]
    },
    {
        type: 1,
        fieldNameCn: "饼环图",
        fieldNameEn: "pieChart",
        children: [
            {
                name: "饼图",
                icon: "iconfont icon-bingtu",
                data: {
                    width: 340,
                    height: 240,
                    globalAlpha: 1,
                    disableAnchor: true,
                    chartName: "pieChart",
                    name: "echarts",
                    echarts: {
                        "option": {
                            "tooltip": {
                                "trigger": "item"
                            },
                            "legend": {
                                "show": false,
                                "icon": "circle",
                                "orient": "vertical",
                                "top": "center",
                                "right": "10%",
                                "textStyle": {
                                    "fontSize": 12,
                                    "color": "#9EA9B5"
                                }
                            },
                            "series": [
                                {
                                    "type": "pie",
                                    "radius": "70%",
                                    // "center": ["30%", "middle"],
                                    "label": {
                                        "show": true,
                                        "textStyle": {
                                            "fontSize": 12,
                                            "color": "#9EA9B5"
                                        }
                                    },
                                    "labelLine": {
                                        "length": 25,
                                        "length2": 15,
                                        "show": true
                                    },
                                    "data": [
                                        {"value": 542, "name": "数值1"},
                                        {"value": 820, "name": "数值2"},
                                        {"value": 841, "name": "数值3"},
                                        {"value": 597, "name": "数值4"},
                                        {"value": 983, "name": "数值5"}
                                    ]
                                }
                            ]
                        }
                    }
                }
            },
            {
                name: "圆环",
                icon: "iconfont icon-huanxingtu",
                data: {
                    width: 340,
                    height: 240,
                    globalAlpha: 1,
                    disableAnchor: true,
                    chartName: "pieChart",
                    name: "echarts",
                    echarts: {
                        "option": {
                            "tooltip": {
                                "trigger": "item"
                            },
                            "legend": {
                                "show": false,
                                "icon": "circle",
                                "orient": "vertical",
                                "top": "center",
                                "right": "10%",
                                "textStyle": {
                                    "fontSize": 12,
                                    "color": "#9EA9B5"
                                }
                            },
                            "series": [
                                {
                                    "type": "pie",
                                    "radius": ["50%", "70%"],
                                    // "center": ["30%", "middle"],
                                    "label": {
                                        "show": true,
                                        "textStyle": {
                                            "fontSize": 12,
                                            "color": "#9EA9B5"
                                        }
                                    },
                                    "labelLine": {
                                        "length": 25,
                                        "length2": 15,
                                        "show": true,
                                    },
                                    "data": [
                                        {"value": 542, "name": "数值1"},
                                        {"value": 820, "name": "数值2"},
                                        {"value": 841, "name": "数值3"},
                                        {"value": 597, "name": "数值4"},
                                        {"value": 983, "name": "数值5"}
                                    ]
                                }
                            ]
                        }
                    }
                }
            },
            {
                name: "圆角环图",
                icon: "iconfont icon-yuanhuantu",
                data: {
                    width: 340,
                    height: 240,
                    globalAlpha: 1,
                    disableAnchor: true,
                    chartName: "pieChart",
                    name: "echarts",
                    echarts: {
                        "option": {
                            "tooltip": {
                                "trigger": "item"
                            },
                            "legend": {
                                "show": false,
                                "icon": "circle",
                                "orient": "vertical",
                                "top": "center",
                                "right": "10%",
                                "textStyle": {
                                    "fontSize": 12,
                                    "color": "#9EA9B5"
                                }
                            },
                            "series": [
                                {
                                    "type": "pie",
                                    "radius": ["50%", "70%"],
                                    // "center": ["30%", "middle"],
                                    "label": {
                                        "show": true,
                                        "textStyle": {
                                            "fontSize": 12,
                                            "color": "#9EA9B5"
                                        }
                                    },
                                    "itemStyle": {
                                        "borderRadius": 4,
                                        "borderWidth": 2,
                                    },
                                    "labelLine": {
                                        "length": 25,
                                        "length2": 15
                                    },
                                    "data": [
                                        {"value": 542, "name": "数值1"},
                                        {"value": 820, "name": "数值2"},
                                        {"value": 841, "name": "数值3"},
                                        {"value": 597, "name": "数值4"},
                                        {"value": 983, "name": "数值5"}
                                    ]
                                }
                            ]
                        }
                    }
                }
            },
        ]
    },
    {
        type: 1,
        fieldNameCn: "进度条",
        fieldNameEn: "progressBarChart",
        children: [
            {
                name: "环形进度条",
                icon: "iconfont icon-huanxingjindutiao",
                data: {
                    width: 240,
                    height: 240,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    chartName: "circularProgressBar",
                    name: "echartsComponent",
                    echarts: {
                        "value": 50,
                        "option": {
                            "title": [{
                                "text": "50%",
                                "unit": "%",
                                "isTitleNum": true, // 代表当前数据的text 需要被替换
                                "left": "center",
                                "top": "center",
                                "textStyle": {
                                    "color": "#0ff",
                                    "fontSize": "12",
                                    "fontWeight": "600",
                                },
                            }],
                            "polar": {
                                "radius": ["95%", "85%"],
                                "center": ["50%", "50%"],
                            },
                            "angleAxis": {
                                "max": 100,
                                "show": false,
                            },
                            "radiusAxis": {
                                "type": "category",
                                "show": true,
                                "axisLabel": {
                                    "show": false,
                                },
                                "axisLine": {
                                    "show": false,
                                },
                                "axisTick": {
                                    "show": false,
                                },
                            },
                            "series": [{
                                "type": "bar",
                                "roundCap": true,
                                "barWidth": 90,
                                "showBackground": true,
                                "backgroundStyle": {
                                    "color": "#2e2856",
                                },
                                "data": [50],
                                "coordinateSystem": "polar",
                                "itemStyle": {
                                    "normal": {
                                        "color": {
                                            "colorStops": [
                                                {
                                                    "offset": 0,
                                                    "color": "#0ff"
                                                },
                                                {
                                                    "offset": 1,
                                                    "color": "#6648FF"
                                                }
                                            ],
                                            "x": 0,
                                            "y": 1,
                                            "x2": 0,
                                            "y2": 0,
                                            "type": "linear",
                                            "global": false
                                        }
                                    }
                                }
                            },
                                {
                                    "type": "pie",
                                    "startAngle": 80,
                                    "radius": ["99%"],
                                    "hoverAnimation": false,
                                    "center": ["50%", "50%"],
                                    "itemStyle": {
                                        "borderWidth": 1,
                                        "borderColor": "#5269EE",
                                        "color": "rgba(255, 255, 255, .1)"
                                    },
                                    "data": [100],
                                },
                                {
                                    "type": "pie",
                                    "startAngle": 80,
                                    "radius": ["60%"],
                                    "hoverAnimation": false,
                                    "center": ["50%", "50%"],
                                    "itemStyle": {
                                        "borderWidth": 1,
                                        "borderColor": "#5269EE",
                                        "color": "rgba(255, 255, 255, .1)"
                                    },
                                    "data": [100],
                                }
                            ]
                        }
                    }
                }
            },
            {
                name: "环形进度条",
                icon: "iconfont icon-xiangxinghuanxingjindutiao",
                data: {
                    width: 240,
                    height: 240,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    chartName: "pgCircularProgressBar",
                    name: "echartsComponent",
                    echarts: {
                        "value": 50,
                        "color0": "#0ff", // 内环刻度 值起始色
                        "color1": "#6648FF", // 内环刻度 值结束色
                        "scaleColor": "#9EA9B5", // 内环刻度默认色
                        "option": {
                            "title": [{
                                "text": "50%",
                                "unit": "%",
                                "isTitleNum": true, // 代表当前数据的text 需要被替换
                                "left": "center",
                                "top": "30%",
                                "textStyle": {
                                    "color": "#0ff",
                                    "fontSize": "14",
                                    "fontWeight": "600",
                                },
                            }, {
                                text: "DESIGN ELEMENTS",
                                left: "50%",
                                top: "45%",
                                textAlign: "center",
                                textStyle: {
                                    fontSize: "12",
                                    color: "#9EA9B5",
                                    fontWeight: "400",
                                    textAlign: "center",
                                },
                            }, {
                                text: "DONUT CHART",
                                left: "50%",
                                top: "57%",
                                textAlign: "center",
                                textStyle: {
                                    fontSize: "10",
                                    fontWeight: "400",
                                    textAlign: "center",
                                    color: "rgba(65,63,112,1)"
                                },
                            }],
                            "polar": {
                                "radius": ["99%", "89%"],
                                "center": ["50%", "50%"],
                            },
                            "angleAxis": {
                                "max": 100,
                                "show": false,
                            },
                            "radiusAxis": {
                                "type": "category",
                                "show": true,
                                "axisLabel": {
                                    "show": false,
                                },
                                "axisLine": {
                                    "show": false,
                                },
                                "axisTick": {
                                    "show": false
                                },
                            },
                            "series": [{
                                "type": "bar",
                                "roundCap": true,
                                "showBackground": true,
                                "backgroundStyle": {
                                    "color": "#2e2856",
                                },
                                "data": [50],
                                "coordinateSystem": "polar",
                                "itemStyle": {
                                    "normal": {
                                        "color": {
                                            "colorStops": [
                                                {
                                                    "offset": 0,
                                                    "color": "#0ff"
                                                },
                                                {
                                                    "offset": 1,
                                                    "color": "#6648FF"
                                                }
                                            ],
                                            "x": 0,
                                            "y": 1,
                                            "x2": 0,
                                            "y2": 0,
                                            "type": "linear",
                                            "global": false
                                        }
                                    }
                                }
                            },
                                {
                                    z: 2,
                                    zlevel: -2,
                                    type: "pie",
                                    hoverAnimation: false,
                                    radius: ["88%", "78%"],
                                    itemStyle: {
                                        normal: {
                                            borderColor: "#FFFFFF",
                                            borderWidth: 1,
                                        }
                                    },
                                    label: {
                                        normal: {
                                            position: "inside",
                                            show: false,
                                        }
                                    },
                                    data: []
                                }
                            ]
                        }
                    }
                }
            },
            {
                name: "单轴进度条",
                icon: "iconfont icon-progressLine",
                data: {
                    width: 380,
                    height: 36,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    name: "echartsComponent",
                    echarts: {
                        option: {
                            grid: {
                                left: "0%",
                                top: "0%",
                                right: "0%",
                                bottom: "0%",
                                containLabel: false
                            },
                            xAxis: {
                                max: 100,
                                type: "value",
                                splitLine: {show: false},
                                axisLine: {show: false},
                                axisLabel: {show: false},
                                axisTick: {show: false},

                            },
                            yAxis: [{
                                type: "category",
                                inverse: false,
                                data: [],
                                axisLine: {show: false},
                                axisTick: {show: false}
                            }],
                            series: [{
                                name: "内",
                                type: "bar",
                                barWidth: 30,
                                label: {
                                    normal: {
                                        show: true,
                                        position: "right",
                                        textStyle: {
                                            color: "#9EA9B5",
                                            "fontSize": 14
                                        },
                                        formatter: "{c}%"
                                    }
                                },
                                itemStyle: {
                                    color: {
                                        type: "linear",
                                        x: 0,
                                        y: 0,
                                        x2: 1,
                                        y2: 0,
                                        colorStops: [{
                                            offset: 0,
                                            color: "#0FFFFF" // 0% 处的颜色
                                        }, {
                                            offset: 1,
                                            color: "#6648FF" // 100% 处的颜色
                                        }],
                                        globalCoord: false // 缺省为 false
                                    }
                                },
                                z: 1,
                                data: [50]
                            },
                                {
                                    name: "框",
                                    type: "bar",
                                    barWidth: 35,
                                    barGap: "-110%",
                                    itemStyle: {
                                        normal: {
                                            borderWidth: 1,
                                            borderColor: "#9EA9B5",
                                            color: "rgba(0,0,0,0)", //底色
                                        }
                                    },
                                    data: [100],
                                    z: 4,
                                },
                                {
                                    z: 2,
                                    name: "分隔",
                                    type: "pictorialBar",
                                    itemStyle: {
                                        color: "#FFFFFF"
                                    },
                                    symbolRepeat: "fixed",
                                    symbolMargin: 2,
                                    symbol: "rect",
                                    symbolClip: true,
                                    symbolSize: [2, 32],
                                    symbolPosition: "start",
                                    symbolOffset: [0, -2],
                                    symbolBoundingData: 100,
                                    data: [50]
                                }
                            ]
                        }
                    }
                }
            },
            {
                name: "单轴进度条",
                icon: "iconfont icon-progressLine",
                data: {
                    width: 380,
                    height: 30,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    name: "echartsComponent",
                    echarts: {
                        option: {
                            grid: {
                                left: "0%",
                                right: "0%",
                                top: "0%",
                                bottom: "0%",
                            },
                            tooltip: {
                                show: true,
                                formatter: "{a}：{c}%"
                            },
                            yAxis: [
                                {
                                    type: "category",
                                    boundaryGap: false,
                                    data: [],
                                    axisLine: {
                                        show: false,
                                    },
                                    axisTick: {
                                        show: false,
                                    },
                                    splitLine: {
                                        show: false,
                                    },
                                },
                            ],
                            xAxis: {
                                type: "value",
                                axisLine: {
                                    show: false,
                                },
                                axisTick: {
                                    show: false,
                                },
                                splitLine: {
                                    show: false,
                                },
                                axisLabel: {
                                    show: false,
                                },
                            },
                            series: [
                                {
                                    name: "bg",
                                    type: "pictorialBar",
                                    barWidth: 4,
                                    silent: true,
                                    symbol: "rect",
                                    symbolRepeat: true,
                                    symbolMargin: 2,
                                    symbolSize: [2, 24],
                                    itemStyle: {color: "#9EA9B5"},
                                    data: [100]
                                },
                                {
                                    name: "当前进度",
                                    type: "pictorialBar",
                                    animation: true,
                                    animationDuration: 600,
                                    symbol: "rect",
                                    symbolRepeat: true,
                                    symbolSize: [2, 24],
                                    symbolMargin: 2,
                                    barWidth: 4,
                                    itemStyle: {
                                        normal:{
                                            color: "#0FFFFF",
                                            label: {
                                                show: false,
                                                fontSize: "14",
                                                color: "#9EA9B5",
                                                position: "right",
                                            },
                                        },
                                    },
                                    data: [50]
                                },
                            ]
                        }
                    }
                }
            },
            {
                name: "条形进度条",
                icon: "iconfont icon-m-jindutiao",
                data: {
                    width: 320,
                    height: 220,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    chartName: "barProgressBar",
                    name: "echartsComponent",
                    echarts: {
                        "option": {
                            "grid": {
                                "left": "5%",
                                "top": "0%",
                                "right": "5%",
                                "bottom": "-14%",
                                "containLabel": true
                            },
                            "xAxis": {
                                "show": false
                            },
                            "yAxis": [
                                {
                                    "show": true,
                                    "axisTick": "none",
                                    "axisLine": "none",
                                    "axisLabel": {
                                        // "inside": true,
                                        // "align": "left",
                                        "textStyle": {
                                            "color": "#9EA9B5",
                                            "fontSize": "12"
                                        }
                                    },
                                    "z": 10,
                                    "data": ["系列一", "系列二", "系列三", "系列四", "系列五"]
                                },
                                {
                                    "show": true,
                                    "axisTick": "none",
                                    "axisLine": "none",
                                    "axisLabel": {
                                        // "inside": true,
                                        // "align": "right",
                                        "textStyle": {
                                            "color": "#9EA9B5",
                                            "fontSize": "12"
                                        }
                                    },
                                    "z": 10,
                                    "data": [40, 56, 23, 15, 15]
                                },
                                {
                                    "axisLine": {
                                        "lineStyle": {
                                            "color": "rgba(0,0,0,0)"
                                        }
                                    },
                                    "data": []
                                }
                            ],
                            "series": [
                                {
                                    "z": 3,
                                    "type": "bar",
                                    "barWidth": 20,
                                    "stack": "b",
                                    "legendHoverLink": false,
                                    "itemStyle": {
                                        "normal": {
                                            "color": "rgba(0,0,0,0)"
                                        }
                                    },
                                    "data": [0, 0, 0, 0, 0]
                                },
                                {
                                    "z": 2,
                                    "name": "条",
                                    "type": "bar",
                                    "stack": "b",
                                    "yAxisIndex": 0,
                                    "barWidth": 10,
                                    "data": [40, 56, 23, 15, 15],
                                    "itemStyle": {
                                        "color": {
                                            "image": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA8AAAAoCAYAAAAhf6DEAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAA6SURBVEhLY2x8/vY/A4mg3zwcTDOBSTLBqGYSwahmEsGoZhLBqGYSwahmEsGoZhLBqGYSwZDUzMAAAJldBMF2UASmAAAAAElFTkSuQmCC",
                                            "repeat": "repeat"
                                        }
                                    }
                                },
                                {
                                    "z": 1,
                                    "name": "白框",
                                    "type": "bar",
                                    "yAxisIndex": 1,
                                    "barGap": "-100%",
                                    "data": [99.9, 99.9, 99.9, 99.9, 99.9],
                                    "barWidth": 30,
                                    "itemStyle": {
                                        "normal": {
                                            "color": "#0e2147",
                                            "barBorderRadius": 2
                                        }
                                    }
                                },
                                {
                                    "z": 0,
                                    "name": "外框",
                                    "type": "bar",
                                    "yAxisIndex": 2,
                                    "barGap": "-100%",
                                    "data": [100, 100, 100, 100, 100],
                                    "barWidth": 32,
                                    "itemStyle": {
                                        "normal": {
                                            "color": "#81E7ED",
                                            "barBorderRadius": [0, 6, 0, 6]
                                        }
                                    }
                                }
                            ]
                        }
                    }
                }
            },
            {
                name: "条形进度条",
                icon: "iconfont icon-m-jindutiao",
                data: {
                    width: 320,
                    height: 220,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    chartName: "barProgressBar",
                    name: "echartsComponent",
                    echarts: {
                        "option": {
                            xAxis: {
                                splitLine: {
                                    show: false
                                },
                                axisLine: {
                                    show: false
                                },
                                axisLabel: {
                                    show: false
                                },
                                axisTick: {
                                    show: false
                                },
                            },
                            grid: {
                                "left": "22%",
                                "top": "0%",
                                "right": "2%",
                                "bottom": "0%",
                                "containLabel": false
                            },
                            yAxis: [{
                                type: "category",
                                inverse: false,
                                axisLine: {
                                    show: false
                                },
                                axisTick: {
                                    show: false
                                },
                                axisLabel: {
                                    show: false
                                },
                            }],
                            series: [
                                {
                                    // 内
                                    type: "bar",
                                    barWidth: 15,
                                    silent: true,
                                    itemStyle: {
                                        normal: {
                                            color: "#81E7ED"
                                        }
                                    },
                                    label: {
                                        normal: {
                                            formatter: "{b}",
                                            textStyle: {
                                                color: "#9EA9B5",
                                                fontSize: 14
                                            },
                                            position: "left",
                                            distance: 24, // 向右偏移位置
                                            show: true
                                        }
                                    },
                                    data: [
                                        {name: "系列一", value: 612.5},
                                        {name: "系列二", value: 548.7},
                                        {name: "系列三", value: 300.2},
                                        {name: "系列四", value: 300},
                                    ],
                                    z: 1,
                                    animationEasing: "elasticOut"
                                },
                                {
                                    name: "分隔",
                                    type: "pictorialBar",
                                    itemStyle: {
                                        normal: {
                                            color: "#07314a"
                                        }
                                    },
                                    symbolRepeat: "fixed",
                                    symbolMargin: 2,
                                    symbol: "rect",
                                    symbolClip: true,
                                    symbolSize: [2, 12],
                                    symbolPosition: "start",
                                    symbolOffset: [2, -2],
                                    symbolBoundingData: 1000,
                                    data: [1000, 1000, 1000, 1000],
                                    z: 2,
                                    animationEasing: "elasticOut"
                                },
                                {
                                    name: "外框",
                                    type: "bar",
                                    barGap: "-130%", // 设置外框粗细
                                    data: [1000, 1000, 1000, 1000],
                                    barWidth: 24,
                                    itemStyle: {
                                        normal: {
                                            barBorderRadius: [2, 2, 2, 2],
                                            color: "transparent", // 填充色
                                            barBorderColor: "#81E7ED", // 边框色
                                            barBorderWidth: 2, // 边框宽度
                                        }
                                    },
                                    z: 0
                                },
                                {
                                    type: "scatter",
                                    name: "条形",
                                    symbol: "roundRect",
                                    symbolSize: [3, 14],
                                    symbolOffset: [2, -2],
                                    symbolKeepAspect: true,
                                    itemStyle: {
                                        normal: {
                                            color: "#81E7ED"
                                        }
                                    },
                                    data: [1000, 1000, 1000, 1000],
                                }
                            ]
                        }
                    }
                }
            },
            {
                name: "电池",
                icon: "iconfont icon-dianchi",
                data: {
                    width: 120,
                    height: 120,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    chartName: "batteryChart",
                    name: "echartsComponent",
                    echarts: {
                        "value": 50,
                        "option": {
                            tooltip: {
                                trigger: "item",
                                formatter: "{a}: {c}"
                            },
                            series: {
                                name: "电量",
                                type: "liquidFill",
                                center: ["center", "center"],
                                data: [2, 0.5],
                                radius: "100%",
                                waveLength: "99%",
                                waveHeight: 100,
                                amplitude: 5, // 波动幅度
                                outline: {show: false},
                                backgroundStyle: {
                                    borderWidth: 3,
                                    borderColor: "#353C43",
                                    color: "rgba(255, 255, 255, 1)"
                                },
                                label: {normal: {formatter: ""}},
                                color: ["rgba(0, 0, 0, 0)", "#33FF00"],
                                shape: "path://M14.42 168.27a115.38 115.38 0 0 1 115.39-115.39h336.54a115.38 115.38 0 0 1 115.38 115.39v701.92a115.38 115.38 0 0 1-115.38 115.39h-336.54a115.38 115.38 0 0 1-115.39-115.39z M201.63 0h186.06v57.69h-186.06z"
                            }
                        }
                    }
                }
            },
            {
                name: "进度仪表盘",
                icon: "iconfont icon-jinduyibiaopan",
                data: {
                    width: 320,
                    height: 320,
                    globalAlpha: 1,
                    externElement: true,
                    disableAnchor: true,
                    chartName: "progressDashboard",
                    name: "echartsComponent",
                    echarts: {
                        value: 50,
                        "option": {
                            "title": {
                                "show": true,
                                "text": "任务进度",
                                "x": "50%",
                                "y": "57%",
                                "z": 8,
                                "textAlign": "center",
                                "textStyle": {
                                    "fontSize": 12,
                                    "color": "#FFFFFF",
                                    "fontWeight": "normal"
                                }
                            },
                            "series": [
                                {
                                    "name": "内部（环形）进度条",
                                    "type": "gauge",
                                    "radius": "65%",
                                    "splitNumber": 10,
                                    "axisLine": {
                                        "lineStyle": {
                                            "color": [
                                                [
                                                    0.5,
                                                    "#458EFD"
                                                ],
                                                [
                                                    1,
                                                    "#FFFFFF"
                                                ]
                                            ],
                                            "width": 14
                                        }
                                    },
                                    "axisLabel": {
                                        "show": false
                                    },
                                    "axisTick": {
                                        "show": false
                                    },
                                    "splitLine": {
                                        "show": false
                                    },
                                    "pointer": {
                                        "show": false
                                    }
                                },
                                {
                                    "name": "外部刻度",
                                    "type": "gauge",
                                    "radius": "98%",
                                    "min": 0,
                                    "max": 100,
                                    "splitNumber": 10,
                                    "startAngle": 225,
                                    "endAngle": -45,
                                    "axisLine": {
                                        "show": true,
                                        "lineStyle": {
                                            "width": 2,
                                            "color": [
                                                [
                                                    1,
                                                    "#FFFFFF"
                                                ]
                                            ]
                                        }
                                    },
                                    "axisLabel": {
                                        "show": true,
                                        "distance": 30,
                                        "fontSize": 10,
                                        "color": "#868FDF"
                                    },
                                    "axisTick": {
                                        "show": true,
                                        "splitNumber": 7,
                                        "lineStyle": {
                                            "color": "#3082FE",
                                            "width": 1
                                        },
                                        "length": -6
                                    },
                                    "splitLine": {
                                        "show": true,
                                        "length": -12,
                                        "lineStyle": {
                                            "color": "#458EFD"
                                        }
                                    },
                                    "detail": {
                                        "show": false
                                    },
                                    "pointer": {
                                        "show": false
                                    }
                                },
                                {
                                    "type": "pie",
                                    "radius": [
                                        "0",
                                        "45%"
                                    ],
                                    "center": [
                                        "50%",
                                        "50%"
                                    ],
                                    "z": 8,
                                    "hoverAnimation": false,
                                    "data": [
                                        {
                                            "name": "检查进度",
                                            "value": 50,
                                            "itemStyle": {
                                                "normal": {
                                                    "color": {
                                                        "colorStops": [
                                                            {
                                                                "offset": 0,
                                                                "color": "#3398ff"
                                                            },
                                                            {
                                                                "offset": 1,
                                                                "color": "#7db0fd"
                                                            }
                                                        ],
                                                        "x": 0,
                                                        "y": 0,
                                                        "x2": 0,
                                                        "y2": 1,
                                                        "type": "linear",
                                                        "global": false
                                                    }
                                                }
                                            },
                                            "label": {
                                                "normal": {
                                                    "show": true,
                                                    "fontSize": 14,
                                                    "color": "#FFFFFF",
                                                    "fontWeight": "bold",
                                                    "position": "center",
                                                    "formatter": "{c}%"
                                                }
                                            },
                                            "labelLine": {
                                                "show": false
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "pie",
                                    "radius": "50%",
                                    "startAngle": 220,
                                    "endAngle": -40,
                                    "hoverAnimation": false,
                                    "center": [
                                        "50%",
                                        "50%"
                                    ],
                                    "avoidLabelOverlap": false,
                                    "label": {
                                        "show": false
                                    },
                                    "labelLine": {
                                        "show": false
                                    },
                                    "data": [
                                        {
                                            "value": 1,
                                            "itemStyle": {
                                                "normal": {
                                                    "color": "#8DC4FD"
                                                }
                                            }
                                        }
                                    ]
                                },
                                {
                                    "z": 0,
                                    "type": "pie",
                                    "radius": "55%",
                                    "center": [
                                        "50%",
                                        "50%"
                                    ],
                                    "avoidLabelOverlap": false,
                                    "hoverAnimation": false,
                                    "label": {
                                        "show": false
                                    },
                                    "labelLine": {
                                        "show": false
                                    },
                                    "data": [
                                        {
                                            "value": 1,
                                            "itemStyle": {
                                                "normal": {
                                                    "color": "#e3edf8"
                                                }
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    }
                }
            }
        ]
    },
    {
        type: 1,
        fieldNameCn: "仪表盘",
        fieldNameEn: "dashboardChart",
        children: [
            {
                name: "基础仪表盘",
                icon: "iconfont icon-yibiaopan",
                data: {
                    width: 300,
                    height: 300,
                    globalAlpha: 1,
                    disableAnchor: true,
                    name: "echarts",
                    echarts: {
                        "option": {
                            "fontSize": 10,
                            "series": [
                                {
                                    "type": "gauge",
                                    "radius": "100%",
                                    "axisLine": {
                                        "roundCap": true,
                                        "lineStyle": {
                                            "color": [
                                                [
                                                    1,
                                                    "rgba(107,157,215,.25)"
                                                ]
                                            ],
                                            "width": 8
                                        }
                                    },
                                    "axisTick": {
                                        "distance": 4,
                                        "length": 8,
                                        "lineStyle": {
                                            "color": "#6B9DD7"
                                        }
                                    },
                                    "splitLine": {
                                        "distance": 4,
                                        "length": 10,
                                        "lineStyle": {
                                            "width": 2,
                                            "color": "rgb(107,157,215)"
                                        }
                                    },
                                    "detail": {
                                        "color": "#0c56eb",
                                        "fontSize": 16
                                    },
                                    "progress": {
                                        "show": true,
                                        "roundCap": true,
                                        "width": 8
                                    },
                                    "axisLabel": {
                                        "color": "#ddd",
                                        "fontSize": 12,
                                        "distance": 15
                                    },
                                    "itemStyle": {
                                        "color": "#0c56eb"
                                    },
                                    "pointer": {
                                        "length": 100,
                                        "width": 6,
                                        "itemStyle": {
                                            "borderWidth": 0
                                        }
                                    },
                                    "data": [
                                        {
                                            "value": 50
                                        }
                                    ]
                                }
                            ]
                        }
                    }
                }
            },
            {
                name: "圆盘仪表盘",
                icon: "iconfont icon-yuanpanyibiaopan",
                data: {
                    width: 300,
                    height: 300,
                    globalAlpha: 1,
                    disableAnchor: true,
                    name: "echarts",
                    echarts: {
                        "option": {
                            "fontSize": 10,
                            "series": [
                                {
                                    "name": "最外部进度条",
                                    "type": "gauge",
                                    "radius": "96%",
                                    "splitNumber": 10,
                                    "axisLine": {
                                        "lineStyle": {
                                            "color": [
                                                [
                                                    1,
                                                    "rgba(107,157,215,.25)"
                                                ]
                                            ],
                                            "width": 8
                                        }
                                    },
                                    "progress": {
                                        "show": true,
                                        "width": 8
                                    },
                                    "axisTick": {
                                        "distance": 8,
                                        "splitNumber": 5,
                                        "lineStyle": {
                                            "color": "#42E5FB",
                                            "width": 1
                                        },
                                        "length": 8
                                    },
                                    "splitLine": {
                                        "distance": 8,
                                        "length": 15,
                                        "lineStyle": {
                                            "width": 2,
                                            "color": "#42E5FB"
                                        }
                                    },
                                    "axisLabel": {
                                        "show": false,
                                        "fontSize": 12
                                    },
                                    "itemStyle": {
                                        "show": false
                                    },
                                    "detail": {
                                        "color": "#ACCFFF",
                                        "fontSize": 16,
                                        "offsetCenter": [
                                            0,
                                            50
                                        ]
                                    },
                                    "data": [
                                        {
                                            "value": 50
                                        }
                                    ],
                                    "pointer": {
                                        "length": "40%",
                                        "radius": "20%",
                                        "width": 4,
                                        "itemStyle": {
                                            "color": "#45CAED",
                                            "borderWidth": 0
                                        }
                                    }
                                },
                                {
                                    "name": "指针上的圆",
                                    "type": "pie",
                                    "z": 5,
                                    "hoverAnimation": false,
                                    "legendHoverLink": false,
                                    "radius": [
                                        "0%",
                                        "6%"
                                    ],
                                    "center": [
                                        "50%",
                                        "50%"
                                    ],
                                    "label": {
                                        "normal": {
                                            "show": false
                                        }
                                    },
                                    "labelLine": {
                                        "normal": {
                                            "show": false
                                        }
                                    },
                                    "data": [
                                        {
                                            "value": 10,
                                            "itemStyle": {
                                                "normal": {
                                                    "color": "#45CAED",
                                                    "borderWidth": 0
                                                }
                                            }
                                        }
                                    ]
                                },
                                {
                                    "name": "外层透明圆",
                                    "type": "pie",
                                    "radius": "60%",
                                    "startAngle": 0,
                                    "endAngle": 360,
                                    "hoverAnimation": false,
                                    "center": [
                                        "50%",
                                        "50%"
                                    ],
                                    "avoidLabelOverlap": false,
                                    "label": {
                                        "show": false
                                    },
                                    "labelLine": {
                                        "show": false
                                    },
                                    "data": [
                                        {
                                            "value": 1
                                        }
                                    ],
                                    "itemStyle": {
                                        "normal": {
                                            "color": {
                                                "type": "linear",
                                                "x": 0,
                                                "y": 0,
                                                "x2": 0,
                                                "y2": 1,
                                                "colorStops": [
                                                    {
                                                        "offset": 0,
                                                        "color": "#17A1FF"
                                                    },
                                                    {
                                                        "offset": 1,
                                                        "color": "rgba(17, 90, 233, 0.16)"
                                                    }
                                                ]
                                            },
                                            "opacity": 0.2
                                        }
                                    }
                                },
                                {
                                    "name": "内圆",
                                    "type": "pie",
                                    "radius": "48%",
                                    "center": [
                                        "50%",
                                        "50%"
                                    ],
                                    "startAngle": 0,
                                    "endAngle": 360,
                                    "label": {
                                        "show": false
                                    },
                                    "labelLine": {
                                        "show": false
                                    },
                                    "data": [
                                        {
                                            "value": 1
                                        }
                                    ],
                                    "itemStyle": {
                                        "color": {
                                            "colorStops": [
                                                {
                                                    "offset": 0,
                                                    "color": "#23A6FF"
                                                },
                                                {
                                                    "offset": 1,
                                                    "color": "rgba(17, 90, 233, 0.21)  "
                                                }
                                            ],
                                            "x": 0,
                                            "y": 0,
                                            "x2": 0,
                                            "y2": 1,
                                            "type": "linear",
                                            "global": false
                                        },
                                        "opacity": 0.5,
                                        "borderWidth": 0
                                    }
                                }
                            ]
                        }
                    }
                }
            },
            {
                name: "进度仪表盘",
                icon: "iconfont icon-jinduyibiaopan",
                data: {
                    width: 300,
                    height: 300,
                    globalAlpha: 1,
                    disableAnchor: true,
                    name: "echarts",
                    echarts: {
                        "option": {
                            "fontSize": 10,
                            "series": [
                                {
                                    "name": "最外部环",
                                    "type": "gauge",
                                    "radius": "96%",
                                    "splitNumber": 10,
                                    "axisLine": {
                                        "lineStyle": {
                                            "color": [
                                                [
                                                    1,
                                                    "#33507A"
                                                ]
                                            ],
                                            "width": 8
                                        }
                                    },
                                    "axisTick": {
                                        "distance": 8,
                                        "splitNumber": 10,
                                        "lineStyle": {
                                            "color": "#42E5FB",
                                            "width": 1
                                        },
                                        "length": 8
                                    },
                                    "splitLine": {
                                        "distance": 8,
                                        "length": 15,
                                        "lineStyle": {
                                            "width": 2,
                                            "color": "#42E5FB"
                                        }
                                    },
                                    "axisLabel": {
                                        "show": false,
                                        "fontSize": 12
                                    },
                                    "itemStyle": {
                                        "show": false
                                    },
                                    "detail": {
                                        "show": false,
                                        "fontSize": 16
                                    },
                                    "title": {
                                        "show": false
                                    },
                                    "data": [],
                                    "pointer": {
                                        "show": false
                                    }
                                },
                                {
                                    "name": "仪表盘",
                                    "type": "gauge",
                                    "radius": "70%",
                                    "z": 4,
                                    "axisLine": {
                                        "lineStyle": {
                                            "color": [
                                                [
                                                    1,
                                                    "rgba(0,0,0,0)"
                                                ]
                                            ],
                                            "width": 20
                                        }
                                    },
                                    "axisLabel": {
                                        "show": false
                                    },
                                    "axisTick": {
                                        "show": false
                                    },
                                    "splitLine": {
                                        "show": false
                                    },
                                    "itemStyle": {
                                        "color": "rgba(0,191,194,0.5)"
                                    },
                                    "progress": {
                                        "width": 20,
                                        "show": true
                                    },
                                    "detail": {
                                        "offsetCenter": [
                                            0,
                                            50
                                        ],
                                        "textStyle": {
                                            "padding": [
                                                0,
                                                0,
                                                0,
                                                0
                                            ],
                                            "fontSize": 30,
                                            "color": "#468EFD"
                                        }
                                    },
                                    "data": [
                                        {
                                            "value": 50
                                        }
                                    ],
                                    "pointer": {
                                        "width": 3,
                                        "itemStyle": {
                                            "borderWidth": 0
                                        }
                                    }
                                }
                            ]
                        }
                    }
                }
            },
            {
                name: "仪表盘1",
                icon: "iconfont icon-yibiaopan1",
                data: {
                    width: 300,
                    height: 300,
                    globalAlpha: 1,
                    disableAnchor: true,
                    name: "echarts",
                    echarts: {
                        "option": {
                            "fontSize": 10,
                            "series": [
                                {
                                    "type": "gauge",
                                    "radius": "98%",
                                    "splitNumber": 10,
                                    "axisLine": {
                                        "lineStyle": {
                                            "color": [
                                                [
                                                    1,
                                                    "rgba(107,157,215,.25)"
                                                ]
                                            ],
                                            "width": 8
                                        }
                                    },
                                    "axisLabel": {
                                        "color": "#4d5bd1",
                                        "distance": 16,
                                        "fontSize": 12
                                    },
                                    "axisTick": {
                                        "distance": 16,
                                        "splitNumber": 5,
                                        "lineStyle": {
                                            "color": "#468EFD",
                                            "width": 1
                                        },
                                        "length": 8
                                    },
                                    "splitLine": {
                                        "distance": 16,
                                        "length": 16,
                                        "lineStyle": {
                                            "color": "#468EFD",
                                            "width": 3
                                        }
                                    },
                                    "itemStyle": {
                                        "show": false,
                                        "color": "#0c56eb"
                                    },
                                    "progress": {
                                        "show": true,
                                        "width": 10
                                    },
                                    "detail": {
                                        "offsetCenter": [
                                            0,
                                            50
                                        ],
                                        "textStyle": {
                                            "padding": [
                                                0,
                                                0,
                                                0,
                                                0
                                            ],
                                            "fontSize": 20,
                                            "color": "#0c56eb"
                                        }
                                    },
                                    "data": [
                                        {
                                            "value": 50
                                        }
                                    ],
                                    "pointer": {
                                        "show": true,
                                        "radius": "20%",
                                        "width": 5,
                                        "itemStyle": {
                                            "borderWidth": 0
                                        }
                                    }
                                }
                            ]
                        }
                    }
                }
            },
            {
                name: "仪表盘2",
                icon: "iconfont icon-yibiaopan2",
                data: {
                    width: 300,
                    height: 300,
                    globalAlpha: 1,
                    disableAnchor: true,
                    name: "echarts",
                    echarts: {
                        "option": {
                            "fontSize": 10,
                            "series": [
                                {
                                    "type": "gauge",
                                    "radius": "60%",
                                    "splitNumber": 10,
                                    "axisLine": {
                                        "lineStyle": {
                                            "color": [
                                                [
                                                    1,
                                                    "rgba(107,157,215,.25)"
                                                ]
                                            ],
                                            "width": 8
                                        }
                                    },
                                    "axisLabel": {
                                        "color": "#4d5bd1",
                                        "distance": 16,
                                        "fontSize": 12
                                    },
                                    "axisTick": {
                                        "distance": -50,
                                        "splitNumber": 5,
                                        "lineStyle": {
                                            "color": "#468EFD",
                                            "width": 1
                                        },
                                        "length": 8
                                    },
                                    "splitLine": {
                                        "distance": -58,
                                        "length": 16,
                                        "lineStyle": {
                                            "color": "#468EFD",
                                            "width": 3
                                        }
                                    },
                                    "itemStyle": {
                                        "show": false,
                                        "color": "#0c56eb"
                                    },
                                    "progress": {
                                        "show": true,
                                        "width": 10
                                    },
                                    "detail": {
                                        "offsetCenter": [
                                            0,
                                            50
                                        ],
                                        "textStyle": {
                                            "padding": [
                                                0,
                                                0,
                                                0,
                                                0
                                            ],
                                            "fontSize": 20,
                                            "color": "#0c56eb"
                                        }
                                    },
                                    "data": [
                                        {
                                            "value": 50
                                        }
                                    ],
                                    "pointer": {
                                        "radius": "20%",
                                        "width": 5,
                                        "itemStyle": {
                                            "borderWidth": 0
                                        }
                                    }
                                }
                            ]
                        }
                    }
                }
            },
            {
                name: "仪表盘3",
                icon: "iconfont icon-yibiaopan3",
                data: {
                    width: 300,
                    height: 300,
                    globalAlpha: 1,
                    disableAnchor: true,
                    name: "echarts",
                    echarts: {
                        "option": {
                            "fontSize": 10,
                            "series": [
                                {
                                    "name": "最外部进度条",
                                    "type": "gauge",
                                    "radius": "100%",
                                    "z": 3,
                                    "axisLine": {
                                        "lineStyle": {
                                            "color": [
                                                [
                                                    1,
                                                    "rgba(28,128,245,0)"
                                                ]
                                            ],
                                            "width": 3
                                        }
                                    },
                                    "axisLabel": {
                                        "show": false,
                                        "fontSize": 16
                                    },
                                    "axisTick": {
                                        "show": false
                                    },
                                    "splitLine": {
                                        "show": false
                                    },
                                    "pointer": {
                                        "show": false
                                    },
                                    "progress": {
                                        "show": true,
                                        "width": 5,
                                        "itemStyle": {
                                            "color": "rgba(133,165,255,0.3)",
                                            "borderWidth": 0
                                        }
                                    },
                                    "detail": {
                                        "offsetCenter": [
                                            0,
                                            2
                                        ],
                                        "textStyle": {
                                            "fontSize": 16,
                                            "color": "#EDFFFD"
                                        },
                                        "fontSize": 16
                                    },
                                    "data": [
                                        {
                                            "value": 50
                                        }
                                    ]
                                },
                                {
                                    "name": "刻度线",
                                    "type": "gauge",
                                    "radius": "80%",
                                    "splitNumber": 10,
                                    "axisLine": {
                                        "lineStyle": {
                                            "color": [
                                                [
                                                    1,
                                                    "#0063E7"
                                                ]
                                            ],
                                            "width": -3
                                        }
                                    },
                                    "axisLabel": {
                                        "color": "#69b1ff",
                                        "distance": 8,
                                        "fontSize": 12
                                    },
                                    "axisTick": {
                                        "distance": 6,
                                        "splitNumber": 5,
                                        "lineStyle": {
                                            "color": "#002c8c",
                                            "width": 1
                                        },
                                        "length": 4
                                    },
                                    "splitLine": {
                                        "distance": 6,
                                        "length": 10,
                                        "lineStyle": {
                                            "color": "#002c8c",
                                            "width": 2
                                        }
                                    },
                                    "progress": {
                                        "show": true,
                                        "width": 60,
                                        "itemStyle": {
                                            "color": "rgba(145,207,255,0.12)",
                                            "borderWidth": 0
                                        }
                                    },
                                    "detail": {
                                        "show": false
                                    },
                                    "pointer": {
                                        "radius": "20%",
                                        "width": 3,
                                        "itemStyle": {
                                            "borderWidth": 0,
                                            "color": "rgba(69,131,255,0.3)"
                                        }
                                    },
                                    "anchor": {
                                        "show": true,
                                        "showAbove": true,
                                        "size": 50,
                                        "itemStyle": {
                                            "color": "rgb(42,65,111)"
                                        }
                                    },
                                    "data": [
                                        {
                                            "value": 50
                                        }
                                    ]
                                }
                            ]
                        }
                    }
                }
            }
        ]
    }
]
