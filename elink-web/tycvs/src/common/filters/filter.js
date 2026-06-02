export default {
    //  空数据处理
    moreData(data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "":
                return "--";
            case "null":
                return "--";
            case "undefined":
                return "--";
            default:
                return filterStr;
        }
    },
    pelType(data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "文件图元";
            case "2":
                return "自定义图元";
            default:
                return "--";
        }
    },
    // 图元类型
    pelFileType(data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "文件夹";
            case "2":
                return "图元";
            default:
                return "--";
        }
    },
    // 通信方式
    communicationType(data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "websocket";
            case "2":
                return "http";
            case "3":
                return "mqtt";
            default:
                return "--";
        }
    },
    // 2d图状态
    graphStatus(data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "0":
                return "无变化";
            case "1":
                return "有更新";
            case "2":
                return "已发布";
            default:
                return "";
        }
    },
    graphType(data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "文件夹";
            case "2":
                return "图模文件";
            default:
                return "--";
        }
    },
    // 变量类型
    variableType(data = "") {
        let filterStr = String(data);
        switch (filterStr) {
            case "1":
                return "系统变量";
            case "2":
                return "设备功能点";
            case "3":
                return "自定义变量";
            default:
                return "--";
        }
    }
}
