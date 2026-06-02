const loadFont = async (fontName, fontUrl) => {
  let exists = false;
  // 检查字体是否已经加载
  for (const font of document.fonts.values()) {
    if (font.family === fontName) {
      exists = true;
      break;
    }
  }
  // 如果字体已经加载，则直接返回
  if (exists) {
    console.log(`Font ${fontName} already loaded`);
    return;
  }
  // 定义字体
  const font = new FontFace(fontName, `url(${fontUrl})`);
  // 加载字体
  await font.load();
  // 添加字体到文档
  document.fonts.add(font);
  // 字体加载完成日志
  console.log(`Font ${fontName} loaded from ${fontUrl}`);
};

export { loadFont };
