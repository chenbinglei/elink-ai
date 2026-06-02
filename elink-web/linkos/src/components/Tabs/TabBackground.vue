<template>
  <!--  鼠标滑过背景修改-->
  <div class="tabBackground" :class="{ borderClass: isShowBorder,backgroundClass: !isShowBorder }">
    <div v-for="items in tabsArray" :key="items.id" class="butlist-li pointer" @click="selectIndex(items.id)"
         :class="{ 'but-group-content' : activeIndex === items.id ,borderRadius: tabsArray.length === 1,'flex-all':isButBisect, }">
      <span class="name">{{ items.name || items.levelDesc }}</span>
      <span v-if="isShowNumber" class="num">{{ $filters.numberNull(items.num || items.levelNumber) }}</span>
    </div>
  </div>
</template>

<script>
  import { defineComponent, getCurrentInstance, reactive, toRefs, watch } from "vue";

  export default defineComponent({
    name: "tabBackground",
    props: {
      tabsArray: {
        type: Array,
        default: () => []
      },
      // 默认 获取第一个 (数组id)
      tabsIndex: {
        type: [Number, String],
        default: 1
      },
      //是否显示左侧数量
      isShowNumber: {
        type: Boolean,
        default: false
      },
      // 是否等份平分
      isButBisect: {
        type: Boolean,
        default: false
      },
      // 是否显示边框
      isShowBorder:{
        type: Boolean,
        default: true
      }
    },
    emits: ["butChange","update:tabsIndex"],
    setup(props) {
      const { emit } = getCurrentInstance();
      const that = reactive({
        tabsArray: props.tabsArray,
        activeIndex: props.tabsIndex,
        isShowNumber:props.isShowNumber
      });

      const watchArray = watch([() => props.tabsArray, () => props.tabsIndex, () => props.isShowNumber], ([newTabsArray, newTabsIndex,newIsShowNumber]) => {
        that.tabsArray = newTabsArray;
        that.activeIndex = newTabsIndex;
        that.isShowNumber = newIsShowNumber;
      }, {deep:true});

      const selectIndex = (tabsIndex) => {
        if (that.tabsArray.length <= 0) return;

        that.activeIndex = tabsIndex;
        emit("butChange", that.activeIndex);
        emit("update:tabsIndex", that.activeIndex);
      };

      return { ...toRefs(that), watchArray,selectIndex };
    }
  });
</script>

<style lang="scss" scoped>
.tabBackground {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #333333;

  .butlist-li {
    height: 32px;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0 14px;
    box-sizing: border-box;

    .num{
      margin-left: 15px;
    }
  }

  .borderRadius{
    border-radius: 4px !important;
  }

  .butlist-li:first-child{
    border-radius: 4px 0 0 4px;
  }
  .butlist-li:last-child{
    border-radius: 0 4px 4px 0;
  }
}

// 带边框
.borderClass{
  .butlist-li {
    border: 1px solid #BDCAD4;
    border-right: none;

    &:hover{
      border-color: rgba(30,113,236,0.6);
      box-shadow: 0 0 10px 0 rgba(30,113,236,0.6) inset;
      box-sizing: border-box;
    }

    &:last-child{
      border-right: 1px solid #BDCAD4;
    }
  }
  .but-group-content {
    color: #1E71EC;
    border-color: rgba(30,113,236,0.6);
    box-shadow: 0 0 10px 0 rgba(30,113,236,0.6) inset;
  }
}

.backgroundClass{
  color: rgba(30,113,236,0.6);

  .butlist-li {
    background: #FEFEFF;
  }

  .but-group-content {
    color: #333333;
    background: none;
  }
}
</style>
