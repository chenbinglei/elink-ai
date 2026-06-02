// svgToCanvasJson.js

function getStyleAttr(element, prop) {
  const attr = element.getAttribute(prop);
  if (attr) return attr;
  const style = element.getAttribute('style');
  if (style) {
    const match = style.match(new RegExp(`${prop}:([^;]+)`));
    if (match) return match[1].trim();
  }
  return null;
}

function normalizeColor(color) {
  if (!color || color === 'none') return null;
  if (color.startsWith('#')) return color;
  if (color.startsWith('rgb')) return color;
  const map = { black: '#000000', white: '#ffffff', red: '#ff0000', green: '#00ff00', blue: '#0000ff' };
  return map[color.toLowerCase()] || '#000000';
}

function getFillStroke(element) {
  const fill = getStyleAttr(element, 'fill');
  const stroke = getStyleAttr(element, 'stroke');
  const strokeWidth = getStyleAttr(element, 'stroke-width');
  return {
    fill: normalizeColor(fill === 'none' ? null : fill),
    stroke: normalizeColor(stroke === 'none' ? null : stroke) || '#000000',
    strokeWidth: strokeWidth ? parseFloat(strokeWidth) : 1,
  };
}

function getTransform(element) {
  const transform = element.getAttribute('transform');
  if (!transform) return { x: 0, y: 0, scaleX: 1, scaleY: 1, rotate: 0 };
  let x = 0, y = 0, scaleX = 1, scaleY = 1, rotate = 0;
  const tMatch = transform.match(/translate\(([^,]+),([^)]+)\)/);
  if (tMatch) { x = parseFloat(tMatch[1]); y = parseFloat(tMatch[2]); }
  const sMatch = transform.match(/scale\(([^,]+)(?:,([^)]+))?\)/);
  if (sMatch) { scaleX = parseFloat(sMatch[1]); scaleY = sMatch[2] ? parseFloat(sMatch[2]) : scaleX; }
  const rMatch = transform.match(/rotate\(([^)]+)\)/);
  if (rMatch) rotate = parseFloat(rMatch[1]);
  return { x, y, scaleX, scaleY, rotate };
}

function generateId() {
  return `pen_${Date.now()}_${Math.random().toString(36).substr(2, 8)}`;
}

// 矩形
function convertRect(element, viewBoxOffset = { x: 0, y: 0 }) {
  let x = parseFloat(element.getAttribute('x') || 0) - viewBoxOffset.x;
  let y = parseFloat(element.getAttribute('y') || 0) - viewBoxOffset.y;
  let width = parseFloat(element.getAttribute('width') || 0);
  let height = parseFloat(element.getAttribute('height') || 0);
  const { fill, stroke, strokeWidth } = getFillStroke(element);
  const { x: tx, y: ty, scaleX, scaleY, rotate } = getTransform(element);
  return {
    id: generateId(),
    name: '矩形',
    type: 'rect',
    x: (x + tx) * scaleX,
    y: (y + ty) * scaleY,
    width: width * scaleX,
    height: height * scaleY,
    rotate,
    style: {
      fill: fill || 'rgba(0,0,0,0)',
      stroke: stroke || 'rgba(0,0,0,0)',
      strokeWidth,
    },
    text: '',
  };
}

// 圆形
function convertCircle(element, viewBoxOffset = { x: 0, y: 0 }) {
  let cx = parseFloat(element.getAttribute('cx') || 0) - viewBoxOffset.x;
  let cy = parseFloat(element.getAttribute('cy') || 0) - viewBoxOffset.y;
  let r = parseFloat(element.getAttribute('r') || 0);
  const { fill, stroke, strokeWidth } = getFillStroke(element);
  const { x: tx, y: ty, scaleX, rotate } = getTransform(element);
  return {
    id: generateId(),
    name: '圆形',
    type: 'circle',
    x: (cx + tx) * scaleX,
    y: (cy + ty) * scaleX,
    radius: r * scaleX,
    rotate,
    style: {
      fill: fill || 'rgba(0,0,0,0)',
      stroke: stroke || 'rgba(0,0,0,0)',
      strokeWidth,
    },
    text: '',
  };
}

// 椭圆
function convertEllipse(element, viewBoxOffset = { x: 0, y: 0 }) {
  let cx = parseFloat(element.getAttribute('cx') || 0) - viewBoxOffset.x;
  let cy = parseFloat(element.getAttribute('cy') || 0) - viewBoxOffset.y;
  let rx = parseFloat(element.getAttribute('rx') || 0);
  let ry = parseFloat(element.getAttribute('ry') || 0);
  const { fill, stroke, strokeWidth } = getFillStroke(element);
  const { x: tx, y: ty, scaleX, scaleY, rotate } = getTransform(element);
  return {
    id: generateId(),
    name: '椭圆',
    type: 'ellipse',
    x: (cx + tx) * scaleX,
    y: (cy + ty) * scaleY,
    width: rx * scaleX * 2,
    height: ry * scaleY * 2,
    rotate,
    style: { fill, stroke, strokeWidth },
    text: '',
  };
}

// 线
function convertLine(element, viewBoxOffset = { x: 0, y: 0 }) {
  let x1 = parseFloat(element.getAttribute('x1') || 0) - viewBoxOffset.x;
  let y1 = parseFloat(element.getAttribute('y1') || 0) - viewBoxOffset.y;
  let x2 = parseFloat(element.getAttribute('x2') || 0) - viewBoxOffset.x;
  let y2 = parseFloat(element.getAttribute('y2') || 0) - viewBoxOffset.y;
  const { stroke, strokeWidth } = getFillStroke(element);
  const { x: tx, y: ty, scaleX, scaleY, rotate } = getTransform(element);
  return {
    id: generateId(),
    name: '线段',
    type: 'line',
    x1: (x1 + tx) * scaleX,
    y1: (y1 + ty) * scaleY,
    x2: (x2 + tx) * scaleX,
    y2: (y2 + ty) * scaleY,
    rotate,
    style: { stroke, strokeWidth },
    text: '',
  };
}

// 折线/多边形（转为多个线段或闭合多边形，简化处理为 pen 的 points 数组）
function convertPoly(element, isPolygon, viewBoxOffset = { x: 0, y: 0 }) {
  const pointsAttr = element.getAttribute('points');
  if (!pointsAttr) return null;
  const points = pointsAttr.trim().split(/\s+/).map(p => {
    const [x, y] = p.split(',').map(Number);
    return { x: x - viewBoxOffset.x, y: y - viewBoxOffset.y };
  });
  const { fill, stroke, strokeWidth } = getFillStroke(element);
  const { x: tx, y: ty, scaleX, scaleY, rotate } = getTransform(element);
  const transformed = points.map(p => ({
    x: (p.x + tx) * scaleX,
    y: (p.y + ty) * scaleY,
  }));
  // 如果编辑器支持 polygon/polyline 类型，直接使用；否则可以拆成多条线，这里假设支持
  return {
    id: generateId(),
    name: isPolygon ? '多边形' : '折线',
    type: isPolygon ? 'polygon' : 'polyline',
    points: transformed,
    rotate,
    style: {
      fill: fill || (isPolygon ? 'rgba(0,0,0,0)' : null),
      stroke: stroke || '#000000',
      strokeWidth,
    },
    text: '',
  };
}

// 文本
function convertText(element, viewBoxOffset = { x: 0, y: 0 }) {
  let x = parseFloat(element.getAttribute('x') || 0) - viewBoxOffset.x;
  let y = parseFloat(element.getAttribute('y') || 0) - viewBoxOffset.y;
  const content = element.textContent || '';
  const fontSize = getStyleAttr(element, 'font-size') || '14px';
  const fill = getFillStroke(element).fill || '#000000';
  const { x: tx, y: ty, scaleX, scaleY, rotate } = getTransform(element);
  return {
    id: generateId(),
    name: '文本',
    type: 'text',
    x: (x + tx) * scaleX,
    y: (y + ty) * scaleY,
    fontSize,
    fill,
    text: content,
    rotate,
  };
}

/**
 * 主转换函数：将 SVG 字符串转换为编辑器所需的完整 JSON
 * @param {string} svgString SVG 文件内容
 * @returns {object} 包含 canvasMeta2dFileData 和 deviceVariables 的对象
 */
export async function svgToCanvasJson(svgString) {
  const parser = new DOMParser();
  const doc = parser.parseFromString(svgString, 'image/svg+xml');
  const svgRoot = doc.documentElement;

  // 解析 viewBox 或宽高
  let width = 1920, height = 1080, offsetX = 0, offsetY = 0;
  const viewBox = svgRoot.getAttribute('viewBox');
  if (viewBox) {
    const [minX, minY, w, h] = viewBox.split(/\s+/).map(Number);
    width = w;
    height = h;
    offsetX = minX;
    offsetY = minY;
  } else {
    width = parseFloat(svgRoot.getAttribute('width') || 1920);
    height = parseFloat(svgRoot.getAttribute('height') || 1080);
  }

  const pens = [];
  const elements = svgRoot.querySelectorAll('rect, circle, ellipse, line, polyline, polygon, text');
  for (const el of elements) {
    let pen = null;
    const offset = { x: offsetX, y: offsetY };
    switch (el.tagName) {
      case 'rect': pen = convertRect(el, offset); break;
      case 'circle': pen = convertCircle(el, offset); break;
      case 'ellipse': pen = convertEllipse(el, offset); break;
      case 'line': pen = convertLine(el, offset); break;
      case 'polyline': pen = convertPoly(el, false, offset); break;
      case 'polygon': pen = convertPoly(el, true, offset); break;
      case 'text': pen = convertText(el, offset); break;
    }
    if (pen) pens.push(pen);
  }

  // 构建完整的图模数据（保留所有默认字段，参考您提供的示例）
  const canvasMeta2dFileData = {
    x: 0,
    y: 0,
    scale: 1,
    pens: pens,
    origin: { x: 0, y: 0 },
    center: { x: width / 2, y: height / 2 },
    paths: {},
    template: "",
    version: "1.0.64",
    dataPoints: [],
    requestParamsList: [],
    siteId: "",
    siteName: "",
    isRequestWebsocket: false,
    name: "导入的SVG",
    gridColor: "#e2e2e2",
    gridSize: 20,
    grid: false,
    background: "rgba(0, 0, 0, 0)",
    color: "#bdc7db",
    width: width,
    height: height,
    scaleMode: "1",
    theme: "dark",
    isDisableScale: false,
    isScroll: false,
    isDisableTranslate: true,
    lineCross: false,
  };

  return {
    canvasMeta2dFileData,
    deviceVariables: {},
  };
}