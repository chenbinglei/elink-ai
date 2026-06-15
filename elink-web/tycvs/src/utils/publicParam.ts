//连线类型
export let dashArray = [
    {name: '实线', action: 0, iconName: "icon-shixian", lineDash: [0, 0]},
    {name: '小虚线', action: 1, iconName: "icon-xiaoxuxian", lineDash: [5, 5]},
    {name: '大虚线', action: 2, iconName: "icon-daxuxian", lineDash: [10, 10]},
    {name: '点短虚线', action: 3, iconName: "icon-diandaunxuxian", lineDash: [10, 10, 2, 10]},
]

//连线类型
export let lineNameArray = [
    {name: '曲线', fieldName: 'curve', iconName: "icon-quxian"},
    {name: '折线', fieldName: 'polyline', iconName: "icon-xianduan"},
    {name: '直线', fieldName: 'line', iconName: "icon-line"},
    {name: '脑图曲线', fieldName: 'mind', iconName: "icon-mind"},
]

//起点箭头
export let fromArrowArray = [
    {name: '直线', fieldName: '', iconName: "icon-line"},
    {name: '左箭头', fieldName: 'line', iconName: "icon-jiantou-xiangzuo"},
    {name: '空心三角箭头', fieldName: 'triangle', iconName: "icon-kongxinjiantou-xiangzuo"},
    {name: '实心三角箭头', fieldName: 'triangleSolid', iconName: "icon-shixinjiantou-xiangzuo"},
    {name: '空心圆形箭头', fieldName: 'circle', iconName: "icon-kongxinyuan-xiangzuo"},
    {name: '实心圆形箭头', fieldName: 'circleSolid', iconName: "icon-shixinyuan-xiangzuo"},
    {name: '空心菱形箭头', fieldName: 'diamond', iconName: "icon-kongxinlingxing-xiangzuo"},
    {name: '实心菱形箭头', fieldName: 'diamondSolid', iconName: "icon-shixinlingxingyuan-xiangzuo"},
]

//终点箭头
export let toArrowArray = [
    {name: '直线', fieldName: '', iconName: "icon-line"},
    {name: '右箭头', fieldName: 'line', iconName: "icon-jiantou-xiangyou"},
    {name: '空心三角箭头', fieldName: 'triangle', iconName: "icon-kongxinjiantou-xiangyou"},
    {name: '实心三角箭头', fieldName: 'triangleSolid', iconName: "icon-shixinjiantou-xiangyou"},
    {name: '空心圆形箭头', fieldName: 'circle', iconName: "icon-kongxinyuan-xiangyou"},
    {name: '实心圆形箭头', fieldName: 'circleSolid', iconName: "icon-shixinyuan-xiangyou"},
    {name: '空心菱形箭头', fieldName: 'diamond', iconName: "icon-kongxinlingxing-xiangyou"},
    {name: '实心菱形箭头', fieldName: 'diamondSolid', iconName: "icon-shixinlingxing-xiangyou"},
]

// 末端样式
export let lineCapArray = [
    {name: "默认", id: "butt"},
    {name: "方形", id: "square"},
    {name: "圆形", id: "round"}
];

// 连接样式
export let lineJoinArray = [
    {name: "默认", id: "miter"},
    {name: "圆形", id: "round"},
    {name: "斜角", id: "bevel"},
];

export let fontFamilyArray = [
    {id: 3, name: "宋体"},
    {id: 1, name: "新宋体"},
    {id: 4, name: "黑体"},
    {id: 5, name: "楷体"},
    {id: 2, name: "微软雅黑"},
    {id: 12, name: "fangsong"},
    {id: 8, name: "PingFang SC"},
    {id: 10, name: "ALBABA Bold"},
    {id: 11, name: "ALBABA Regular"},
    {id: 6, name: "Microsoft YaHei"},
    {id: 7, name: "Microsoft YaHei Ul"},
    {id: 9, name: "BlinkMacSystemFont"},
];


// 水平对齐方式
export let textAlignArray = [
    {name: "左对齐", id: "left"},
    {name: "居中", id: "center"},
    {name: "右对齐", id: "right"}
]

// 水平对齐方式
export let verticalAlignArray = [
    {name: "顶部对齐", id: "top"},
    {name: "居中", id: "middle"},
    {name: "底部对齐", id: "bottom"}
]

// 图元属性列表
export let pelAttributeNameList = [
    {label: "文字", key: "text"},
    {label: "控件数据", key: "controlData", keywords: true},
    {label: "文字颜色", key: "textColor", type: "color", keywords: true},
    {label: "颜色", key: "color", type: "color", keywords: true},
    {label: "背景颜色", key: "background", type: "color", keywords: true},
    {label: "旋转", key: "rotate", type: "integer", keywords: true},
    {label: "高", key: "height", type: "float", keywords: true},
    {label: "旋转", key: "rotate", type: "integer", keywords: true},
    {label: "状态", key: "showChild", type: "integer", keywords: true},
    {label: "进度", key: "progress", type: "float", keywords: true},
    {label: "透明度", key: "globalAlpha", type: "integer", keywords: true},
    {label: "显示", key: "visible", type: "bool", keywords: true},
    {label: "开关", key: "checked", type: "bool", keywords: true},
    {label: "宽", key: "width", type: "float", keywords: true},
    {label: "X", key: "x", type: "float", keywords: true},
    {label: "Y", key: "y", type: "float", keywords: true}
]

// 变量管理-- 变量类型
export let variableTypeArray = [
    {id: "String", name: 'String'},
    {id: "Integer", name: 'Integer'},
    {id: "Double", name: 'Double'},
    {id: "BigDecimal", name: 'BigDecimal'},
    {id: "Boolean", name: 'Boolean'},
    {id: "ArrayList", name: 'ArrayList'},
    {id: "ArrayMap", name: 'ArrayMap'},
    {id: "Long", name: 'Long'},
    {id: "CurveMap", name: 'CurveMap'},
    {id: "ArrayString", name: 'ArrayString'}
]