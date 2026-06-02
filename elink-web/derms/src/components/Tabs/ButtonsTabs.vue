<template>
  <div class="buttonsTabs">
    <div class="button_tabs_left">
      <div v-for="(item, index) in tabs_array" :key="index" :class="{ active_class: activeIndex === item.id }"
        class="tabs_li flex-jc-ai-center" @click.stop="selectIndex(item.id)">
        <span v-if="item.iconName" class="iconfont" :class="item.iconName"></span>
        <span class="tabs_li_text">{{ item.name }}</span>
      </div>
    </div>
    <div class="button_tabs_right">
      <slot name="content"></slot>
    </div>
  </div>
</template>

<script>
import { reactive, defineComponent, toRefs, getCurrentInstance, watch } from "vue";

export default defineComponent({
  name: "ButtonsTabs",
  props: {
    tabsArray: {
      type: Array,
      default: () => []
    },
    // 默认 获取第一个 (数组id)
    tabsIndex: {
      type: [Number, String],
      default: ""
    }
  },
  emits: ["update:tabsIndex", "changeEvent"],
  setup(props) {
    const { emit } = getCurrentInstance();

    const that = reactive({
      tabs_array: props.tabsArray,
      activeIndex: props.tabsIndex,
      oldActiveIndex: props.tabsIndex,
    });

    const selectIndex = (tabsIndex) => {
      that.activeIndex = tabsIndex;
      that.oldActiveIndex = that.activeIndex;
      emit("update:tabsIndex", that.activeIndex);
      if (that.activeIndex !== props.tabsIndex) emit("changeEvent", that.activeIndex);
    };

    const watchTabsIndex = watch(() => props.tabsIndex, (newTabsIndex) => {
      that.activeIndex = newTabsIndex;
    }, { deep: true });

    const watchTabsArray = watch(() => props.tabsArray, (newTabsArray) => {
      // 列表发生改变，查看当前数组是否存在已选中，没有则取第一项。
      let findItem = newTabsArray.find(item => item.id === that.oldActiveIndex);
      let firstTableId = newTabsArray && newTabsArray.length ? newTabsArray[0].id : "";
      that.activeIndex = findItem ? findItem.id : firstTableId;
      selectIndex(that.activeIndex);
    }, { immediate: true, deep: true });

    return { ...toRefs(that), watchTabsArray, selectIndex, watchTabsIndex };
  }
});

</script>

<style lang="scss" scoped>
.buttonsTabs {
  width: 100%;
  display: flex;
  align-items: center;

  .button_tabs_left {
    display: flex;
    align-items: center;

    .tabs_li {
      cursor: pointer;
      max-height: 38px;
      margin-right: 5px;
      padding: 8px 21px;
      border-radius: 4px;
      border: 1px solid #135278;
      box-sizing: border-box;

      .tabs_li_text {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
      }

      &:last-child {
        margin-right: 0;
      }
    }

    .active_class {
      background: var(--el-color-primary);

      .tabs_li_text {
        color: rgba(255, 255, 255, 1);
      }
    }
  }

  .button_tabs_right {
    flex: 1;
    display: flex;
    justify-content: flex-end;
  }
}
</style>