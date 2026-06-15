
/*
* 元素大小发生改变执行  v-resize=”function“
* */
function resizeDocument(app) {
    app.directive('resize', {
        mounted(el, binding) {
            // v-resize 未绑定回调函数时直接跳过，避免运行时 TypeError
            if (typeof binding.value !== 'function') return;
            // 这里使用debounce防抖处理，防抖的延时时间可以通过自定义指令的参数传过来，如`v-resize:300`表示300ms延时
            // 也可以将此处延时去掉，放在绑定的函数中自定义
            function debounce(fn, delay = 16) {
                let t = null;
                return function () {
                    if (t) {
                        clearTimeout(t);
                    }
                    const context = this;
                    const args = arguments;
                    t = setTimeout(function () {
                        if (typeof fn === 'function') {
                            fn.apply(context, args);
                        }
                    }, delay);
                };
            }
            el._resizer = new window.ResizeObserver(debounce(binding.value, Number(binding.arg) || 16));
            el._resizer.observe(el);
        },
        unmounted(el) {
            // 当 mounted 中因 binding.value 非函数提前返回时，_resizer 可能为 undefined
            if (el._resizer && typeof el._resizer.disconnect === 'function') {
                el._resizer.disconnect();
            }
        }
    });
}

export { resizeDocument };