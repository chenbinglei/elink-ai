<template>
  <div ref="tabsRef" class="tabs">
    <!--  底部边框可滑动组件-->
    <div class="tabs_left">
      <div class="tabs_list">
        <template v-for="(item,index) in tabsArray" :key="index">
          <div v-if="!item.isShow" :ref="getMyDom" :class="{ active: activeIndex === item.id }" class="tabs_li"
               @click.stop="selectIndex(item.id)">
            <!-- @mouseleave="mouseIndex" @mousemove="setSlideActiveLeft(index)"  鼠标移入底部线条跟着滑动-->
            <span>{{ item[label] }}</span>
          </div>
        </template>
      </div>
      <div :style="{ height: borderWidth + 'px'}" class="tabs_slide">
        <div :style="style" class="slide_active"></div>
      </div>
    </div>
    <div v-if="isRightSlot" class="tabs_right">
      <slot name="rightButtonSlot"></slot>
    </div>
  </div>
</template>
<script lang="ts">
import {defineComponent, getCurrentInstance, reactive, toRefs, ref, nextTick, watch} from "vue";

export default defineComponent({
  name: "Tabs",
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
    borderWidth: {
      type: [Number, String],
      default: 1
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
  emits: ["update:tabsIndex","changeEvent"],
  setup(props) {

    const {emit} = getCurrentInstance();
    const that = reactive({
      style: {},
      paddingLeft: 0, // 左内边距
      textFontSize: 8, // 字体大小
      tabsArray: props.tabsArray,
      activeIndex: props.tabsIndex,
      oldActiveIndex: props.tabsIndex,
      borderWidth: props.borderWidth
    });

    const tabsRef = ref(null);
    let tabsRefArray = ref([]);
    const getMyDom = (el) => {
      tabsRefArray.value.push(el);
    };

    const selectIndex = (tabsIndex) => {
      if (that.tabsArray.length <= 0) return;

      nextTick(() => {
        that.activeIndex = tabsIndex;
        // 查找当前id 下标
        let tabIndex = that.tabsArray.findIndex(({id}) => id === that.activeIndex);
        if (tabIndex !== -1) setSlideActiveLeft(tabIndex);

        that.oldActiveIndex = that.activeIndex;
        emit("update:tabsIndex", that.activeIndex);
        emit("changeEvent", { operateType: 'updateTabs',tabsIndex: that.activeIndex });
      })
    };

    const setSlideActiveLeft = (tabsIndex) => {
      // 获取li的宽度
      let tabs_li_width = tabsRefArray.value[tabsIndex].offsetWidth;
      let tabs_li_left = tabsRefArray.value[tabsIndex].offsetLeft;
      // console.log(tabs_li_width);
      // 获取当前name 的长度
      let textLength = gbLenStrFun(that.tabsArray[tabsIndex][props.label]);
      let slide_width = textLength * that.textFontSize + that.paddingLeft;
      // console.log(slide_width);
      let offsetLeft = tabs_li_left + (Math.abs(tabs_li_width - slide_width) / 2);
      // console.log(offsetLeft);
      // 获取 父元素的 左边
      let tabsOffsetLeft = tabsRef.value.offsetLeft;

      that.style = {transform: `translate(${offsetLeft - tabsOffsetLeft}px)`, width: `${slide_width}px`};
    };

    const mouseIndex = () => {
      let tabIndex = that.tabsArray.findIndex(({id}) => id === that.activeIndex);
      setSlideActiveLeft(tabIndex);
    };

    //计算字符串长度(英文占1个字符，中文汉字占2个字符)
    const gbLenStrFun = (charStr) => {
      let len = 0;
      for (let i = 0; i < charStr.length; i++) {
        charStr.charCodeAt(i) > 127 || charStr.charCodeAt(i) === 94 ? len += 2 : len++;
      }
      return len;
    }

    const watchTabsArray = watch(() => props.tabsArray, (newTabsArray) => {
      tabsRefArray = ref([]);
      let tabsArrays = [];
      newTabsArray.forEach(item => {
        if (!item.isShow) tabsArrays.push(item);
      });
      that.tabsArray = tabsArrays;
      // 列表发生改变，查看当前数组是否存在已选中，没有则取第一项。
      let firstTableId = that.tabsArray && that.tabsArray.length ? that.tabsArray[0].id : "";
      let findItem = that.tabsArray.find(item => item.id === that.oldActiveIndex);
      that.activeIndex = findItem ? findItem.id : firstTableId;
      selectIndex(that.activeIndex);
    }, {immediate: true, deep: true})

    return {...toRefs(that), selectIndex, getMyDom, setSlideActiveLeft, mouseIndex, tabsRef, tabsRefArray, watchTabsArray, gbLenStrFun};
  }
});
</script>
<style lang="scss" scoped>
.tabs {
  width: 100%;
  display: flex;
  align-items: center;

  .tabs_left {
    flex: 1;

    .tabs_list {
      width: 100%;
      display: flex;

      .tabs_li {
        cursor: pointer;
        padding: 0 22px;
        color: #121C3F;
        font-size: 14px;

        display: flex;
        align-items: center;
      }

      .active {
        color: #007FEB;
      }
    }

    .tabs_slide {
      width: 100%;
      height: 1px;
      position: relative;
      margin-top: 15px;
      background: rgba(224, 224, 224, 0.8);

      .slide_active {
        height: 3px;
        border-radius: 2px;
        transition: all .5s;
        position: absolute;
        top: -3px;
        background-color: #1F74E2;
      }
    }
  }

  .tabs_right {}
}
</style>
