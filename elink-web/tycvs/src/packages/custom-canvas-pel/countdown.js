export function countdown(pen, ctx) {
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
    let pad = pen.fillZero ? '0' : '';
    let millisecondsPerDay = 24 * 60 * 60 * 1000; // 一天的毫秒数
    let d1 = new Date();
    let d2 = new Date(pen.deadline ? pen.deadline : getFirstDayOfNextYear());

    // 计算两个日期的时间差（毫秒）
    let difference = d2 - d1;
    let day = (Math.floor(difference / millisecondsPerDay) + '').padStart(2, pad);
    let hours = (Math.floor((difference % millisecondsPerDay) / (60 * 60 * 1000)) + '').padStart(2, pad);
    let minutes = (Math.floor((difference % (60 * 60 * 1000)) / (60 * 1000)) + '').padStart(2, pad);
    let seconds = (Math.floor((difference % (60 * 1000)) / 1000) + '').padStart(2, pad);

    let fn = new Function('day', 'hours', 'minutes', 'seconds', pen.timeFormat ? "return " + pen.timeFormat : 'return `${day}${hours}:${minutes}:${seconds}`');
    return fn(day, hours, minutes, seconds);
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

function getFirstDayOfNextYear() {
    const now = new Date();
    now.setFullYear(now.getFullYear() + 1);
    now.setMonth(0); // 设置月份为0（即1月）
    now.setDate(1); // 设置日期为1
    now.setHours(0);
    now.setMinutes(0);
    now.setSeconds(0);
    now.setMilliseconds(0);
    return now;
}