import "./customComponent"; // 蒋自定义vue组件生成全局可以用的html标签


import {flowPens} from '@meta2d/flow-diagram';
import {classPens} from '@meta2d/class-diagram';
import {activityDiagram} from '@meta2d/activity-diagram';
import {formPens, formPath2DPens} from '@meta2d/form-diagram';
import {register as registerEcharts} from "@meta2d/chart-diagram";
import {sequencePens, sequencePensbyCtx} from '@meta2d/sequence-diagram';
import {register, registerCanvasDraw, registerAnchors} from '@meta2d/core';

// 自定义html图元
import {echartsComponent, countdown} from '@/packages/custom-canvas-pel';


/*
	1. 编写图形绘画函数
	其中，calculative.worldRect为canvas的世界坐标。更多信息，参考 “架构” - “概要” 和 Pen 相关文档
	2. 如果需要，编写锚点函数。通常，可以使用默认锚点，然后通过快捷键动态添加锚点
	注意，锚点左边为相对宽高的百分比小数（0-1之间的小数）
 */

// 注册自定义图形库
export function canvasMeta2dRegisterFun() {
    registerEcharts(); // 注册echarts
    register(flowPens());
    register(classPens());
    register(sequencePens());
    register(formPath2DPens());
    register(activityDiagram());
    register({echartsComponent, countdown});

    registerCanvasDraw(formPens());
    registerCanvasDraw(formPens());
    registerCanvasDraw(sequencePensbyCtx());
    // registerAnchors({})
}



