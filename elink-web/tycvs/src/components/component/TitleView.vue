<template>
  <div :class="{contentHeight:isContentHeight}" class="titleView">
    <div class="header_title flex jc-space-between ai-center">
      <div class="headerLeft">
        <div class="line"></div>
        <div class="title">{{ title }}</div>
      </div>
      <div class="headerRight">
        <slot name="headerRight"></slot>
      </div>
    </div>
    <div class="collapse_Content scrollbarStyle">
      <slot name="content"></slot>
    </div>
  </div>
</template>

<script lang="ts">
import {reactive, toRefs, defineComponent} from "vue";

export default defineComponent({
  name: "TitleView",
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

    return {...toRefs(that),}
  }
})
</script>

<style lang="scss" scoped>
.titleView {
  width: 100%;

  .header_title {
    width: 100%;
    font-size: 16px;
    padding-bottom: 24px;
    box-sizing: border-box;

    .headerLeft {
      display: flex;
      align-items: center;
      font-size: 16px;
      font-weight: bold;
      margin-right: 16px;
      -webkit-line-clamp: 1;

      .line {
        width: 4px;
        height: 16px;
        margin-right: 8px;
        background: #1F74E2;
        border-radius: 0 3px 3px 0;
      }
    }

    .headerRight {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: flex-end;
    }
  }

  .collapse_Content {
    width: 100%;
  }
}

.contentHeight {
  height: 100%;
  display: flex;
  flex-direction: column;

  .collapse_Content {
    flex: 1;
    height: 2px;
    overflow-y: auto;
  }
}
</style>
