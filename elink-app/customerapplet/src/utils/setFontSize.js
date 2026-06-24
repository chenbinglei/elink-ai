//设置根元素的fontSize值
function setFontSize() {
    let html = document.documentElement;
    let hei = html.clientWidth;
    html.style.fontSize = hei / 375 * 100 + "px";
}

setFontSize();
window.addEventListener("resize", setFontSize, false) //屏幕改变事件
