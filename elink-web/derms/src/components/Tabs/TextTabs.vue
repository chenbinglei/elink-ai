<template>
  <div :class="isTableClass" class="textTabs">
    <template v-for="(item, index) in tabs_array" :key="index">
      <div
        :class="{ active_class: activeIndex === item.id }"
        class="tabs_li"
        @click.stop="selectIndex(item.id)"
      >
        <div class="tabs_li_text">{{ item[label] }}</div>
      </div>
    </template>
  </div>
</template>

<script>
import {
  reactive,
  defineComponent,
  toRefs,
  getCurrentInstance,
  watch,
} from "vue";

export default defineComponent({
  name: "TextTabs",
  props: {
    tabsArray: {
      type: Array,
      default: () => [],
    },
    // 默认 获取第一个 (数组id)
    tabsIndex: {
      type: [Number, String],
      default: "",
    },
    label: {
      type: String,
      default: "name",
    },
    isTableClass: {
      type: String,
      default: "textTableClass", //  textTableClass   bgTableClass
    },
  },
  emits: ["update:tabsIndex"],
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
      console.log(that.activeIndex);
    };

    const watchTabsIndex = watch(
      () => props.tabsIndex,
      (newTabsIndex) => {
        that.activeIndex = newTabsIndex;
      },
      { deep: true }
    );

    const watchTabsArray = watch(
      () => props.tabsArray,
      (newTabsArray) => {
        let tabs_array = JSON.parse(JSON.stringify(newTabsArray ?? []));
        // 列表发生改变，查看当前数组是否存在已选中，没有则取第一项。
        let findItem = tabs_array.find(
          (item) => item.id === that.oldActiveIndex
        );
        let firstTableId =
          tabs_array && tabs_array.length ? tabs_array[0].id : "";
        that.activeIndex = findItem ? findItem.id : firstTableId;
        that.tabs_array = JSON.parse(JSON.stringify(tabs_array));
        selectIndex(that.activeIndex);
      },
      { immediate: true, deep: true }
    );

    return { ...toRefs(that), watchTabsArray, selectIndex, watchTabsIndex };
  },
});
</script>


<style lang="scss" scoped>
.textTabs {
  display: flex;
  align-items: center;
  background: #094269;

  .tabs_li {
    cursor: pointer;
    box-sizing: border-box;

    .tabs_li_text {
      font-size: 16px;
    }
  }
}

.textTableClass {
  .tabs_li {
    margin-right: 5px;
    padding: 10px 12px;

    .tabs_li_text {
      color: #c7e3f3;
    }

    &:last-child {
      margin-right: 0;
    }
  }

  .active_class {
    .tabs_li_text {
      font-weight: bold;
      /* 设置文本的背景为线性渐变 */
      background: linear-gradient(180deg, #ffffff 33.5%, #416d89 100%);
      /* 使用 blend-mode 实现文本颜色与背景渐变颜色的混合 */
      -webkit-background-clip: text;
      background-clip: text;
      /* 使用 color 作为遮罩，实际看到的是背景渐变 */
      -webkit-text-fill-color: transparent;
      text-fill-color: transparent;
      /* 防止文本阴影遮挡背景渐变 */
      -webkit-box-decoration-break: clone;
      box-decoration-break: clone;
    }
  }
}

.bgTableClass {
  .tabs_li_text {
    color: #ffffff;
    font-weight: 700;
    box-sizing: border-box;
    padding: 0 28px 6px 28px;
    border-bottom: 4px solid transparent;
  }

  .active_class {
    .tabs_li_text {
      border-image-slice: 1;
      border-image-source: linear-gradient(
        90deg,
        #3fcfff00 5%,
        #3fcfff 50%,
        #3fcfff00 95%
      );
      background: radial-gradient(
        59.11% 148.25% at 50% 100%,
        #3fcfff99 0%,
        #3fcfff00 60%
      );
    }
  }
}
</style>