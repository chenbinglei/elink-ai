<template>
  <div :class="{contentHeight:isContentHeight}" class="collapse">
    <div class="header_title">
      <div class="arrow_left" @click="clickArrowButFun">
        <div class="arrow_center" :class="{collapseClass: isCollapse}">
          <span class="iconfont icon-zuojiantou"></span>
        </div>
      </div>
      <div class="headerTitle textTwo">{{ title }}</div>
      <div class="headerRight">
        <slot name="headerRight"></slot>
      </div>
    </div>
    <div class="collapse_content" v-show="isCollapse">
      <slot name="content"></slot>
    </div>
  </div>
</template>

<script>
import {defineComponent, reactive, toRefs} from "vue";

export default defineComponent({
  name: "LxyCollapse",
  props: {
    title: {
      type: [String, Number],
      default: "标题"
    },
    // 内容是否 剩余铺满
    isContentHeight: {
      type: Boolean,
      default: false
    },
    collapse: {
      type: Boolean,
      default: true
    },
  },
  setup(props) {
    const that = reactive({
      isCollapse: props.collapse
    });

    const clickArrowButFun = ()=>{
      that.isCollapse = !that.isCollapse;
    }

    return {...toRefs(that),clickArrowButFun};
  }
});
</script>

<style lang="scss" scoped>
.collapse {
  width: 100%;

  .header_title {
    width: 100%;
    height: 32px;
    display: flex;
    align-items: center;
    margin-bottom: 16px;
    background: #fafafaff;
    box-sizing: border-box;
    padding: 4px 4px 4px 12px;

    .arrow_left {
      position: relative;
      margin-right: 8px;

      .arrow_center {
        font-size: 18px;
        font-weight: bold;
        transition: all .28s;
      }

      .collapseClass{
        transform: rotate(-90deg);
      }
    }

    .headerTitle {
      font-size: 16px;
      color: #333333ff;
      font-weight: bold;
      -webkit-line-clamp: 1;
    }

    .headerRight {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: flex-end;
    }
  }

  .collapse_content {
    width: 100%;
    padding-bottom: 12px;
    box-sizing: border-box;
  }
}

.contentHeight {
  height: 100%;
  display: flex;
  flex-direction: column;

  .collapse_content {
    flex: auto;
    height: 2px;
  }
}
</style>
