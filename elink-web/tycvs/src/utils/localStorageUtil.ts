/**
 * 二次封装localStorage，实现追加key前缀，代码中使用直接调用localStorage.xxx_func
 * 通过webpack配置实现localStorage重写configureWebpack
 * 在vue.config.js的configureWebpack中添加：
 * plugins: [
 *   new webpack.ProvidePlugin({
 *     'localStorage': resolve('./src/utils/localStorageUtil.js'),
 *   })
 * ],
 */

// key的通用前缀，不同项目区分开来，避免key重复,
const _key_pre_ = `TY_CANVAS_`;

export function setItem(key, val) {
	// console.log("localStorageUtil setItem",key,val);
	return window.localStorage.setItem(_key_pre_ + key, val);
}

export function getItem(key) {
	// console.log("localStorageUtil getItem",key);
	return window.localStorage.getItem(_key_pre_ + key);
}

export function removeItem(key) {
	// console.log("localStorageUtil removeItem",key);
	return window.localStorage.removeItem(_key_pre_ + key);
}

export function clear() {
	// console.log("localStorageUtil clear");
	return window.localStorage.clear();
}

export function key(index) {
	// console.log("localStorageUtil key");
	return window.localStorage.key(index);
}
