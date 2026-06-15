<template>
  <div :class="{contentHeight:isContentHeight}" class="title-icon-view">
    <div class="header_title">
      <div class="title_content">
        <slot name="icon"></slot>
        <div class="title">{{ title }}</div>
      </div>
      <div class="header_title_bg"></div>
    </div>
    <div class="collapse_content scrollbarStyle">
      <slot name="content"></slot>
    </div>
  </div>
</template>
<script lang="ts">
import {reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "TitleIconView",
  props: {
    title: {
      type: String,
      default: "标题"
    },
    // 内容是否 剩余铺满
    isContentHeight: {
      type: Boolean,
      default: false
    }
  },
  setup() {

    const that = reactive({})

    return {...toRefs(that)}
  }
})
</script>
<style lang="scss" scoped>
.title-icon-view {
  width: 100%;
  color: #FFFFFF;

  .header_title {
    height: 30px;
    position: relative;
    border-radius: 4px;
    margin-bottom: 12px;

    .title_content {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      padding-left: 16px;
      box-sizing: border-box;
      position: absolute;
      z-index: 10;

      .title {
        flex: 1;
        padding-left: 8px;
        -webkit-line-clamp: 1;
        box-sizing: border-box;
      }
    }

    .header_title_bg {
      position: absolute;
      left: 0;
      top: 0;
      width: 100%;
      height: 100%;
      max-width: 400px;
      border-radius: 4px;
      clip-path: polygon(0 0, 100% 0, 100% 0, 100% 100%, 12px 100%, 0 calc(100% - 30px), 0 0);
      background: linear-gradient(45deg, rgba(63, 163, 255, 1) 0%, rgba(2, 165, 255, 0.04) 65%, rgba(0, 163, 255, 0) 100%);
    }
  }
}

.contentHeight {
  height: 100%;
  display: flex;
  flex-direction: column;

  .collapse_content {
    flex: 1;
    height: 2px;
    overflow-y: auto;
    box-sizing: border-box;
  }
}
</style>