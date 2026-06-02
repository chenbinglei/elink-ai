<template>
  <div class="tabs_border_card">

    <div class="tabs_top">
      <div class="tabs_left">
        <template v-for="(item,index) in tabsCardArray" :key="index">
          <div v-if="!item.isShow" :class="{ activeClass: activeIndex === item.id,preActiveClass:preFindIndex === index }" class="tabs_li pointer" @click.stop="selectIndex(item.id)">
            <div class="tabs_li_text">{{ item.name }}</div>
          </div>
        </template>
      </div>
      <div class="tabs_right"></div>
    </div>

    <div class="tabs_bottom scrollbarStyle">
      <slot name="content"></slot>
    </div>
  </div>
</template>

<script>
import { getCurrentInstance, onMounted, reactive, toRefs, watch } from "vue";

export default {
  name: "TabBorderCard",
  props: {
    tabsCardArray: {
      type: Array,
      default: () => []
    },
    // 默认 获取第一个 (数组id)
    tabsCardIndex: {
      type: [Number, String],
      default: 1
    },
    //背景颜色
    tabsLiBackground: {
      type: String,
      default: ""
    }
  },
  emits: ["update:tabsCardIndex"],
  setup(props) {
    const { emit } = getCurrentInstance();
    const that = reactive({
      preFindIndex: -1,
      tabsCardArray: props.tabsCardArray,
      activeIndex: props.tabsCardIndex
    });

    const watchTabsBorderCardArray = watch([() => props.tabsCardArray, () => props.tabsCardIndex], ([newTabsCardArray, newTabsCardIndex]) => {
      that.tabsCardArray = newTabsCardArray;
      that.activeIndex = newTabsCardIndex;
    }, { deep: true });

    const selectIndex = (tabsIndex) => {
      if (that.tabsCardArray.length <= 0) return;
      that.activeIndex = tabsIndex;
      setPreFindIndex();
      emit("update:tabsCardIndex", that.activeIndex);
      // emit("butChange", that.activeIndex);
    };

    const setPreFindIndex = ()=>{
      if(that.tabsCardArray && that.tabsCardArray.length){
        let findIndex = that.tabsCardArray.findIndex(item => item.id === that.activeIndex);
        that.preFindIndex = findIndex - 1;
      }
    }

    onMounted(()=>{
      setPreFindIndex();
    })

    return { ...toRefs(that), watchTabsBorderCardArray, selectIndex,setPreFindIndex };
  }
};
</script>

<style lang="scss" scoped>
.tabs_border_card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;

  .tabs_top {
    display: flex;

    .tabs_left {
      display: flex;
      align-items: flex-end;

      .tabs_li {
        padding: 0 2px;
        box-sizing: border-box;
        border-bottom:1px solid #BDCAD4;

        .tabs_li_text{
          color: #333333;
          font-size: 14px;
          padding: 13px 16px;
          box-sizing: border-box;
          border: 1px solid #BDCAD4;
          border-radius: 4px 4px 0 0;
          border-bottom: none;
        }
      }

      .tabs_li:first-child{
        padding-left: 0;
      }

      .activeClass {
        padding: 0;
        border-color: transparent;

        .tabs_li_text{
          color: #4B92FB;
        }

        &+.tabs_li{
          padding-left: 4px;
        }
      }
      .preActiveClass{
        padding-right: 4px;
      }

    }

    .tabs_right {
      flex: 1;
      padding: 0 12px;
      box-sizing: border-box;
      border-bottom: 1px solid #BDCAD4;
    }
  }

  .tabs_bottom {
    flex: 1;
    height: 2px;
    overflow-y: auto;
    box-sizing: border-box;
    border: 1px solid #BDCAD4;
    border-radius: 0 0 4px 4px;
    border-top: none;
  }
}
</style>
