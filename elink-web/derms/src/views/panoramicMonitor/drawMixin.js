// 屏幕适配 mixin 函数

// * 默认缩放值
const scale = {
    width: '1',
    height: '1'
}

// * 设计稿尺寸（px）
const baseWidth = 1920
const baseHeight = 1080

// * 需保持的比例（默认1.77778）
const baseProportion = parseFloat((baseWidth / baseHeight).toFixed(5))

export default function drawMixin(_appRef) {
    let data = {
        // * 定时函数
        drawTiming: null,
        scaleWidth: 1,
    };
    let appRef = _appRef;
    let appdata =  {
        mounted() {
            this.calcRate()
            window.addEventListener('resize', appdata.resize)
        },
        unMounted() {
            window.removeEventListener('resize', appdata.resize)
        },
        setAppRef(_appRef) {
            appRef = _appRef
        },
        calcRate() {
            if (!appRef) return
            // 当前宽高比
            const currentRate = parseFloat(
                (window.innerWidth / window.innerHeight).toFixed(5)
            )
            document.documentElement.style.fontSize = '192px'; // 1920 / 10 = 192
            if (appRef) {
                if (currentRate > baseProportion) {
                    // 表示更宽
                    this.scaleWidth = scale.width = (
                        (window.innerHeight * baseProportion) /
                        baseWidth
                    ).toFixed(5)
                    scale.height = (window.innerHeight / baseHeight).toFixed(5)
                    appRef.style.transform = `scale(${scale.width}, ${scale.height}) translate(-50%, -50%)`
                    appRef.style.width = `${100 / scale.width}%`
                } else {
                    // 表示更高
                    scale.height = (
                        window.innerWidth /
                        baseProportion /
                        baseHeight
                    ).toFixed(5)
                    this.scaleWidth = scale.width = (window.innerWidth / baseWidth).toFixed(5)
                    appRef.style.transform = `scale(${scale.width}, ${scale.height}) translate(-50%, -50%)`
                    appRef.style.width = `${100 / scale.width}%`
                }
            }
        },
        resize() {
            clearTimeout(data.drawTiming)
            data.drawTiming = setTimeout(() => {
                appdata.calcRate()
            }, 200)
        }
    };
    return appdata;
}
