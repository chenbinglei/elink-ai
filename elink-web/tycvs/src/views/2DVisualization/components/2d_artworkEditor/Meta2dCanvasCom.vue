<template>
  <div class="meta2dCanvasCom">
    <CanvasToolbarCom ref="canvasToolbarComRef"></CanvasToolbarCom>
    <div id="meta2dCanvas" class="meta2d"></div>
    <RightClickMenuCom v-if="rightClickMenuVisible" v-model:isVisible="rightClickMenuVisible"
      :rightClickMenuX="rightClickMenuX" :rightClickMenuY="rightClickMenuY" />
  </div>
</template>

<script>
import { useStore } from "vuex";
import { Meta2d } from '@meta2d/core';
import { clearLineCross } from "@meta2d/utils";
import RightClickMenuCom from "./RightClickMenuCom.vue";
import { CanvasToolbarCom } from "./Meta2dCanvasCom/index";
import { canvasMeta2dRegisterFun } from "@/packages/register";
import { reactive, toRefs, defineComponent, onUnmounted, computed, nextTick, getCurrentInstance } from "vue";

export default defineComponent({
  name: 'Meta2dCanvasCom',
  components: { CanvasToolbarCom, RightClickMenuCom },
  setup () {

    const store = useStore();
    const { emit } = getCurrentInstance();
    const canvasMeta2dConfig = computed(() => {
      return store.state.meta2d;
    });

    const canvasMeta2d = computed(() => {
      return store.state.meta2d.canvasMeta2d;
    });

    const that = reactive({
      // 画布初始配置信息
      meta2dOptions: {
        width: 1920, //	画布大屏宽度
        height: 1080, //画布大屏高度
        theme: "light",  // 主题  light  dark
        color: "#222222", //画笔默认颜色C9D1E2
        background: "#FFFFFF", //画布背景颜色
        rule: true, //是否显示标尺
        ruleColor: "#C8D0E1", //	标尺颜色
        ruleOptions: {
          height: 18,
          textTop: 5,
          baseline: "bottom",
          textColor: '#C8D0E1',
          background: "#F7F8FA",
        },
        scroll: false,
        scaleMode: "1", // 自动铺满
        drawingLineName: "curve", //默认连线类型名称
        // unavailableKeys: ['Backspace'], //需要屏蔽的快捷键
        domShapes: ["echartsComponent"], //扩展的dom画笔name,处理dom移动过程中会产生新的dom问题
      },

      rightClickMenuX: 0, // 右键菜单显示位置
      rightClickMenuY: 0, // 右键菜单显示位置
      rightClickMenuVisible: false,
    })

    // // 初始化画布
    const initMeta2dFun = () => {
      const meta2d = new Meta2d('meta2dCanvas',{ rotateCursor: "/rotate.cur" });
      meta2d.setOptions(that.meta2dOptions); // 初始化配置信息
      meta2d.scale(canvasMeta2dConfig.value.canvas_scale); //缩放画布
      meta2d.centerSizeView(); //大屏范围居中视图
      meta2d.on('*', bindMeta2dCanvasFun); // 绑定事件
      canvasMeta2dRegisterFun();

      store.dispatch("initCanvasMeta2d", meta2d); //全局注册meta2d实例
      emit("canvasMeta2dFun",{ type: 'initMeta2d',code: 20000 });
    }
    

    // 画布绑定事件
    const bindMeta2dCanvasFun = (event, data) => {
      // console.log(event,data);

      // 画布选中那些图元
      if (event === "active") {
        let current_active_pel_list = [];
        data && data.forEach(item => {
          if (item.id) current_active_pel_list.push(item.id);
        })
        store.dispatch("updateCurActivePelList", current_active_pel_list);
      }

      if (event === "click") {
        // 单击画布的时候，查看是否有选中的图元
        if (!data.pen) store.dispatch("updateCurActivePelList", []);
      }

      if (event === "update") {
        nextTick(() => {
          // 取消钢笔绘画
          if (!canvasMeta2dConfig.value.drawLineStatus) {
            canvasMeta2d.value.finishDrawLine();  //钢笔绘画完成
            canvasMeta2d.value.drawLine(); // 取消绘画
          }
        })

        // 画布上缩放、旋转图元计数器（同步更新单个图元操作栏数据）
        const timestamp = new Date().getTime(); // 利用时间戳
        store.dispatch('updateCurActivePelNum', timestamp);
      }

      // 当前画布进行缩放
      if (event === "scale") {
        store.dispatch('updateCanvasScale', data); // 更新画布缩放参数值
      }

      // 画布上点击右键
      if (event === "contextmenu") {
        that.rightClickMenuVisible = false;
        that.rightClickMenuX = data.e.pageX;
        that.rightClickMenuY = data.e.pageY;
        nextTick(() => that.rightClickMenuVisible = true);
      }
    }

    // 画布重置大小
    const meta2dResizeFun = () => {
      canvasMeta2d.value?.resize();
    }

    // 销毁画布
    const destroyMeta2dFun = () => {
      if (canvasMeta2d.value) {
        canvasMeta2d.value.off('*', bindMeta2dCanvasFun);
        canvasMeta2d.value.destroy();
        console.log("画布销毁完成！！！！！");
      }
      store.dispatch("initCanvasMeta2d", null);  // 全局注册meta2d实例
      store.dispatch("updateCurActivePelList", []); // 画布当前选中的图元id数组
      store.dispatch("updateCanvasMeta2dAllLoad", false);
      console.log("画布资源销毁完成！！！！！");
    }

    onUnmounted(() => {
      clearLineCross();
      destroyMeta2dFun();
      console.log("组件卸载！！！！！");
    });

    return { ...toRefs(that), initMeta2dFun, destroyMeta2dFun, canvasMeta2d, bindMeta2dCanvasFun, meta2dResizeFun, canvasMeta2dConfig }
  }
})
</script>

<style lang="scss" scoped>
.meta2dCanvasCom {
  flex: 1;
  width: 2px;
  height: 100%;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  border-left: 1px solid rgba(224, 224, 224, 0.4);
  border-right: 1px solid rgba(224, 224, 224, 0.4);

  .meta2d {
    flex: 1;
    width: 100%;
    height: 2px;
    min-width: 10px;
    min-height: 10px;
    overflow: hidden;
    background-color: #F0F1F2;
  }
}
</style>