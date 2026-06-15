<template>
  <div class="triangleArrowTabs">
    <div class="triangle_left">
      <template v-for="(item,index) in tabs_array" :key="index">
        <div :class="{ active_class: activeIndex === item.id }" class="tabs_li" @click.stop="selectIndex(item.id)">
          <div class="tabs_li_text">{{ item[label] }}</div>
          <div class="tabs_li_icon">
<!--            <span class="iconfont icon-shangjiantou"></span>-->
            <img class="title_icon" src="@/assets/image/title_icon.png" alt="" />
          </div>
        </div>
      </template>
    </div>
    <div v-if="isRightSlot" class="triangle_right">
      <slot name="rightButtonSlot"></slot>
    </div>
  </div>
</template>

<script lang="ts">
import {reactive, defineComponent, toRefs, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "TriangleArrowTabs",
  props: {
    tabsArray: {
      type: Array,
      default: () => []
    },
    // 默认 获取第一个 (数组id)
    tabsIndex: {
      type: [Number, String],
      default: ""
    },
    // 导航右侧插槽
    isRightSlot: {
      type: Boolean,
      default: false
    },
    label: {
      type: String,
      default: "name"
    },
  },
  emits: ["update:tabsIndex"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      tabs_array: [],
      activeIndex: props.tabsIndex,
      oldActiveIndex: props.tabsIndex,
    });

    const selectIndex = (tabsIndex) => {
      that.activeIndex = tabsIndex;
      that.oldActiveIndex = that.activeIndex;
      emit("update:tabsIndex", that.activeIndex);
    };

    const watchTabsArray = watch(() => props.tabsArray, (newTabsArray) => {
      let tabs_array = JSON.parse(JSON.stringify(newTabsArray ?? []));
      // 列表发生改变，查看当前数组是否存在已选中，没有则取第一项。
      let findItem = tabs_array.find(item => item.id === that.oldActiveIndex);
      let firstTableId = tabs_array && tabs_array.length ? tabs_array[0].id : "";
      that.activeIndex = findItem ? findItem.id : firstTableId;
      that.tabs_array = JSON.parse(JSON.stringify(tabs_array));
      selectIndex(that.activeIndex);
    }, {immediate: true, deep: true});

    return {...toRefs(that), watchTabsArray, selectIndex};
  }
});
</script>

<style lang="scss" scoped>
.triangleArrowTabs {
  display: flex;
  align-items: center;

  .triangle_left {
    display: flex;

    .tabs_li {
      padding: 0 12px;
      cursor: pointer;
      text-align: center;
      box-sizing: border-box;

      .tabs_li_text {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.6);
        white-space: nowrap;
        margin-bottom: 2px;
      }

      .tabs_li_icon {
        display: none;
        justify-content: center;

        img{
          width: 16px;
          transform: rotate(-90deg);
          transition: all .2s;
        }
      }

      &:hover {
        .tabs_li_text {
          color: rgba(255, 255, 255, 0.8);
        }
      }
    }

    .active_class {
      .tabs_li_text {
        color: var(--el-color-primary);
      }

      .tabs_li_icon {
        display: flex;
        //color: var(--el-color-primary);
      }
    }
  }

  .triangle_right {
    flex: 1;
  }
}
</style>