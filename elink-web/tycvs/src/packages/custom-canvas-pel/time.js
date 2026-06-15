export function time(pen, ctx) {
    let path = !ctx ? new Path2D() : ctx;
    let _a = pen.calculative.worldRect, x = _a.x, y = _a.y, width = _a.width, height = _a.height;
    path.rect(x, y, width, height);
    if (!pen.onAdd) {
        pen.onAdd = onAdd;
        pen.onDestroy = onDestroy;
        if (pen.interval) {
            pen.onDestroy(pen);
            pen.onAdd(pen);
        }
    }
    if (!pen.interval) {
        pen.onAdd(pen);
    }
    if (path instanceof Path2D) return path;
}
function formatTime(pen) {
    //更多 https://blog.csdn.net/Endeavorseven/article/details/101310628
    let weeks = ['天', '一', '二', '三', '四', '五', '六'];
    let now = new Date();
    let year = now.getFullYear();
    let pad = '';
    if (pen.fillZero) {
        pad = '0';
    }
    let month = (now.getMonth() + 1 + '').padStart(2, pad);
    let day = (now.getDate() + '').padStart(2, pad);
    let week = now.getDay();
    let hours = (now.getHours() + '').padStart(2, pad);
    let minutes = (now.getMinutes() + '').padStart(2, pad);
    let seconds = (now.getSeconds() + '').padStart(2, pad);
    let fn = new Function('year', 'month', 'day', 'week', 'hours', 'minutes', 'seconds', pen.timeFormat
        ? "return " + pen.timeFormat
        : 'return `${year}:${month}:${day} ${hours}:${minutes}:${seconds} 星期${week}`');
    return fn(year, month, day, weeks[week], hours, minutes, seconds);
}
function onAdd(pen) {
    pen.interval = setInterval(function () {
        let text = formatTime(pen);
        pen.calculative.canvas.parent.setValue({ id: pen.id, text: text }, { history: false, doEvent: false, render: false });
        pen.calculative.canvas.render();
    }, pen.timeout || 1000);
}
function onDestroy(pen) {
    if (pen.interval) {
        clearInterval(pen.interval);
        pen.interval = undefined;
    }
}