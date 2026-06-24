//保留2位小数
export function changeTwoDecimal(x) {
    var f_x = parseFloat(x);
    if (isNaN(f_x)) {
        return 0;
    }
    f_x = Math.round(x * 100) / 100;
    var s_x = f_x.toString();
    var pos_decimal = s_x.indexOf('.');
    if (pos_decimal < 0) {
        pos_decimal = s_x.length;
        s_x += '.';
    }
    while (s_x.length <= pos_decimal + 2) {
        s_x += '0';
    }
    return s_x;
}

export function GetQueryString(name) { // 截取url的字段
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r) return unescape(r[2]);
    return '';
}

export function GetRequest() {
    var url = location.search; //获取url中"?"符后的字串
    var theRequest = {};
    if (url.indexOf("?") !== -1) {
        var str = url.substr(1);
        var strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return theRequest;
}

export function getClientType() {
    var ua = navigator.userAgent.toLowerCase();
    if (/iphone|ipad|ipod/.test(ua)) {
        return "ios";
    }
    if (/android/.test(ua)) {
        return "android";
    }
    return "android";
}

export function queryUrlParam(name) { // 截取url的字段
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r) return decodeURI(r[2]);
    return '';
}

/* 获取字符串url参数 根据 & 符 进行截取 */
export function getParameterByName(url,symbol = "&") {
    let theRequest = {};
    if (url.indexOf("?") !== -1 && url.indexOf("=") !== -1) {
        let strs = url.split('?')[1].split(symbol);
        for (let i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return theRequest
}

//判断当前浏览器类型
export function isBrowserTypeFun() {
    let browserType = "other"; // 默认为其他类型浏览器
    let userAgent = navigator.userAgent.toLowerCase();

    // 此处判断只能用 ==  进行判断

    // 微信端
    if (userAgent.match(/MicroMessenger/i) == 'micromessenger') {
        browserType = "weChat";
    }

    // 支付宝端
    if (userAgent.match(/Alipay/i) == "alipay") {
        browserType = "alipay";
    }

    return browserType
}
