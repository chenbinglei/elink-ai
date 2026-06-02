//定义连点位置


export function up(pen = {}) {
    const anchors = [];
    anchors.push({id: '0', penId: pen.id, x: 0.5, y: 1});
    pen.anchors = anchors;
}

export function down(pen = {}) {
    const anchors = [];
    anchors.push({id: '0', penId: pen.id, x: 0.5, y: 0});
    pen.anchors = anchors;
}

export function left(pen = {}) {
    const anchors = [];
    anchors.push({id: '0', penId: pen.id, x: 1, y: 0.5});
    pen.anchors = anchors;
}

export function right(pen = {}) {
    const anchors = [];
    anchors.push({id: '0', penId: pen.id, x: 0, y: 0.5});
    pen.anchors = anchors;
}

//上下
export function upAndDown(pen = {}) {
    const anchors = [];
    anchors.push({id: '0', penId: pen.id, x: 0.5, y: 0});
    anchors.push({id: '1', penId: pen.id, x: 0.5, y: 1});
    pen.anchors = anchors;
}

//左右
export function leftAndRight(pen = {}) {
    const anchors = [];
    anchors.push({id: '0', penId: pen.id, x: 1, y: 0.5});
    anchors.push({id: '1', penId: pen.id, x: 0, y: 0.5});
    pen.anchors = anchors;
}

//上下左右
export function upAndDownAndLeftAndRight(pen = {}) {
    const anchors = [];
    anchors.push({id: '0', penId: pen.id, x: 0.5, y: 0});
    anchors.push({id: '1', penId: pen.id, x: 0.5, y: 1});
    anchors.push({id: '0', penId: pen.id, x: 1, y: 0.5});
    anchors.push({id: '1', penId: pen.id, x: 0, y: 0.5});
    pen.anchors = anchors;
}