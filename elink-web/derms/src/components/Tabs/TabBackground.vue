<template>
  <div class="tabBackground">
    <!--  鼠标滑过背景修改-->
    <div class="content_left whiteBgConnect">
      <template v-for="(items,index) in tabsArray" :key="items.id">
        <div :class="{ 'but-group-content' : activeIndex === items.id, lastChildClass: index + 1 === tabsArray.length }"
             class="but_list_li flex-jc-ai-center" @click="selectIndex(items.id)">
          <span class="name_text">{{ items.name }}</span>
        </div>
      </template>
    </div>
    <div class="content_right">
      <slot name="content"></slot>
    </div>
  </div>
</template>

<script>
import {defineComponent, getCurrentInstance, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "TabBackground",
  props: {
    tabsArray: {
      type: Array,
      default: () => []
    },
    // 默认 获取第一个 (数组id)
    tabsIndex: {
      type: [Number, String],
      default: -1
    }
  },
  emits: ["update:tabsIndex","changeEvent"],
  setup(props) {
    const {emit} = getCurrentInstance();
    const that = reactive({
      tabsArray: props.tabsArray,
      activeIndex: props.tabsIndex
    });

    const selectIndex = (tabsIndex) => {
      if (that.tabsArray.length <= 0) return;
      that.activeIndex = tabsIndex;
      emit("update:tabsIndex", that.activeIndex);
      emit("changeEvent", that.activeIndex);
    };

    const watchArray = watch([() => props.tabsArray, () => props.tabsIndex], ([newTabsArray, newTabsIndex]) => {
      that.tabsArray = newTabsArray;
      that.activeIndex = newTabsIndex;
    }, {deep: true, immediate: true});

    return {...toRefs(that), watchArray, selectIndex};
  }
});
</script>

<style lang="scss" scoped>
.tabBackground {
  width: 100%;
  display: flex;
  align-items: center;

  .content_left {
    display: flex;
    align-items: center;

    .but_list_li {
      max-height: 38px;
      padding: 8px 21px;
      background: #ffffff08;
      box-sizing: border-box;
      border: 1px solid #106ec499;
      border-right: none;

      .name_text {
        color: #ffffff;
        font-size: 14px;
        white-space: nowrap;
      }

      &:first-child {
        border-radius: 4px 0 0 4px;
      }

      &:last-child {
        border-radius: 0 4px 4px 0;
        border-right: 1px solid #106ec499;
      }

      &:hover {
        cursor: pointer;

        .name_text {
          color: #01BFFF;
        }
      }
    }

    .but-group-content {
      border-color: #01bfff;

      .name_text {
        color: #01bfff;
      }

      & + .but_list_li {
        border-left-color: #01bfff;
      }

      &.lastChildClass {
        border-color: #01bfff;
      }
    }
  }

  .content_right {
    flex: 1;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
