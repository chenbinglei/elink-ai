<template>
  <div class="lx-collapse">
    <div class="content_title" @click.stop="clickTitleFun">
      <span :class="{ rotate_class: isCollapse }" class="iconfont icon-zuojiantou"></span>
      <div class="flex-all">
        <slot class="flex-all" name="title">
          <span class="title noSelect">{{ title }}</span>
        </slot>
      </div>
    </div>
    <div class="content_body">
      <div v-if="isCollapse" class="transition">
        <slot name="content"></slot>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import {defineComponent, reactive, toRefs} from "vue";

export default defineComponent({
  name: "lx-collapse",
  props: {
    title: {
      type: String,
      default: "标题"
    }
  },
  setup() {
    const that = reactive({
      isCollapse: true
    })

    const clickTitleFun = () => {
      that.isCollapse = !that.isCollapse;
    }

    return {...toRefs(that), clickTitleFun}
  }
})
</script>
<style lang="scss" scoped>
.lx-collapse {
  width: 100%;
  --color-title: #666D79;

  .content_title {
    display: flex;
    align-items: center;
    box-sizing: border-box;
    padding: 8px 8px 8px 16px;
    color: var(--color-title);

    .iconfont {
      font-size: 14px;
      margin-right: 6px;
      transition: all .25s;
    }

    .rotate_class {
      transform: rotate(-90deg);
    }

    .title {
      font-size: 12px;
    }

    &:hover {
      cursor: pointer;

      .title {
        color: var(--color-primary);
      }
    }
  }

  .content_body {
    padding: 4px 4px 12px;
    box-sizing: border-box;

    .transition {
      transition: all .28s;
    }
  }
}
</style>