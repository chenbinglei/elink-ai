import {deepClone, deepSetValue, setElemPosition} from '@meta2d/core';

let keyWords = [
    'fontSize',
    'nameGap',
    'margin',
    'width' /*线条宽度*/,
    'symbolSize' /*结点大小*/,
    'itemWidth', // 图例宽度
    'itemHeight', // 图例高度
    'fontWeight',
    'top',
    'left',
    'right',
    'bottom',
    'zoom',
    'edgeSymbolSize',
    'nodeWidth',
    'nodeGap',
    'distance',
    'length',
    'length2',
    'offsetCenter',
    'size',
    'symbolOffset',
    'padding',
    'barWidth',
    'shadowOffsetY',
    'shadowOffsetX',
];

export function echartsComponent(pen) {
    if (!pen.echarts) {
        return;
    }

    if (typeof pen.echarts === 'string') {
        try {
            pen.echarts = JSON.parse(pen.echarts);
        } catch (e) {
        }
    }

    keyWords = pen.calculative.canvas.store.options.diagramOptions['chart']?.keyWords || keyWords;

    if (!pen.onDestroy) {
        pen.onDestroy = destroy;
        pen.onMove = move;
        pen.onResize = resize;
        pen.onRotate = move;
        pen.onValue = value;
        pen.onMouseEnter = move;
        pen.onRenderPenRaw = onRenderPenRaw;
    }

    // 创建html 对象
    if (!pen.calculative.singleton) {
        pen.calculative.singleton = {};
    }

    const path = new Path2D();
    const worldRect = pen.calculative.worldRect;
    if (!pen.calculative.singleton.div) {
        // 1. 创建父容器
        let div = document.createElement('div');
        div.id = pen.id;
        div.className = pen.id;
        div.style.top = '-9999px';
        div.style.outline = 'none';
        div.style.left = '-9999px';
        div.style.position = 'absolute';
        div.style.width = worldRect.width + 'px';
        div.style.height = worldRect.height + 'px';
        div.innerHTML = `<meta2d-echarts-component pen_id="${pen.id}"></meta2d-echarts-component>`;
        document.body.appendChild(div);

        // 2. 加载到div layer
        pen.calculative.canvas.externalElements?.parentElement.appendChild(div);
        setElemPosition(pen, div);

        // 3. 解析echarts数据
        pen.calculative.singleton.div = div;
        pen.calculative.singleton.echartsReady = true;

        // 4. 加载echarts
        if (pen.calculative.singleton.echartsReady) {
            // 初始化时，等待父div先渲染完成，避免初始图表控件太大。
            setTimeout(() => {
                const {refs, ctx} = pen.calculative.singleton.div.children[0]._instance;
                refs.echartsComponentRef.setChartOptionFun(updateOption(pen, pen.calculative.canvas.store.data.scale));
                setTimeout(() => onRenderPenRaw(pen), 300);
            });
        }
    }

    return path;
}

function destroy(pen) {
    if (pen.calculative.singleton && pen.calculative.singleton.div) {
        pen.calculative.singleton.div.remove();
        delete pen.calculative.singleton.div;
    }
}

function move(pen) {
    pen.calculative.singleton.div && setElemPosition(pen, pen.calculative.singleton.div);
}

function resize(pen) {
    if (!pen.calculative.singleton.div) {
        return;
    }
    setElemPosition(pen, pen.calculative.singleton.div);

    setTimeout(()=>{
        const {refs, ctx} = pen.calculative.singleton.div.children[0]._instance;
        refs.echartsComponentRef.setChartOptionFun(updateOption(pen, pen.calculative.canvas.store.data.scale));
    })
}

function value(pen) {
    if (!pen.calculative.singleton.div) {
        return;
    }
    setElemPosition(pen, pen.calculative.singleton.div);

    if (pen.calculative.singleton.echartsReady) {
        const {refs, ctx} = pen.calculative.singleton.div.children[0]._instance;
        refs.echartsComponentRef.setChartOptionFun(updateOption(pen, pen.calculative.canvas.store.data.scale));
    }
}

function onRenderPenRaw(pen) {
    let _a;
    if (!((_a = pen.calculative) === null || _a === void 0 ? void 0 : _a.singleton)) {
        return;
    }
    let img = new Image();
    try {
        const {refs} = pen.calculative.singleton.div.children[0]._instance;
        img.src = refs.meta2dEchartsComponentRef.getDataURL({pixelRatio: 2});
    } catch (e) {
    }
    pen.calculative.img = img;
}

function updateOption(_option, ratio) {
    const option = deepClone(_option);
    if (option.dataZoom) {
        let props = ['right', 'top', 'width', 'height', 'left', 'bottom'];
        for (let i = 0; i < props.length; i++) {
            option.dataZoom.forEach((item) => {
                if (!isNaN(item[props[i]])) {
                    item[props[i]] *= ratio;
                }
            });
        }
    }
    deepSetValue(option, keyWords, ratio);
    return option;
}