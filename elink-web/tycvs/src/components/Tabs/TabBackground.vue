<template>
  <div class="tabBackground">
    <!--  鼠标滑过背景修改-->
    <div class="content_left" :class="customClass">
      <template v-for="items in tabsArray" :key="items.id">
        <div class="but_list_li flex-jc-ai-center" @click="selectIndex(items.id)" :class="{ 'but-group-content' : activeIndex === items.id }">
          <span class="name_text">{{ items.name }}</span>
        </div>
      </template>
    </div>
    <div class="content_right"><slot name="content"></slot></div>
  </div>
</template>

<script>
  import { defineComponent, getCurrentInstance, reactive, toRefs, watch } from "vue";

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
      },
      // connectClass ： 按钮全部连接在一起  customBcAndColor: 浅色背景字体  whiteBgConnect: 白色背景 连接在一起的
      customClass: {
        type: String,
        default: "customClass"
      },
    },
    emits: ["update:tabsIndex","changEvent"],
    setup(props) {
      const { emit } = getCurrentInstance();
      const that = reactive({
        tabsArray: props.tabsArray,
        activeIndex: props.tabsIndex
      });

      const selectIndex = (tabsIndex) => {
        if (that.tabsArray.length <= 0) return;
        that.activeIndex = tabsIndex;
        emit("update:tabsIndex", that.activeIndex);
        emit("changEvent");
      };

      const watchArray = watch([() => props.tabsArray, () => props.tabsIndex], ([newTabsArray, newTabsIndex,newIsShowNumber]) => {
        that.tabsArray = newTabsArray;
        that.activeIndex = newTabsIndex;
      }, {deep:true});

      return { ...toRefs(that), watchArray, selectIndex };
    }
  });
</script>

<style lang="scss" scoped>
.tabBackground {
  width: 100%;
  display: flex;
  align-items: center;

  .content_left{
    display: flex;
    align-items: center;

    .but_list_li {
      max-height: 38px;
      padding: 9px 22px;
      border-radius: 8px;
      background: #F1F3FA;
      box-sizing: border-box;
      margin-right: 8px;

      .name_text{
        color: #121C3F;
        font-size: 14px;
        white-space: nowrap;
      }

      &:last-child{
        margin-right: 0;
      }

      &:hover{
        cursor: pointer;
        background: #ebedf0;

        .name_text{
          color: #242424;
        }
      }
    }

    .but-group-content{
      background: #007FEB !important;

      .name_text{
        color: #FFFFFF !important;
      }
    }
  }

  .connectClass{
    border-radius: 8px;

    .but_list_li {
      border-radius: 0;
      margin-right: 1px;

      &:first-child{
        border-radius: 8px 0 0 8px;
      }

      &:last-child{
        border-radius: 0 8px 8px 0;
      }
    }
  }

  // 自定义颜色
  .customBcAndColor{
    .but_list_li {
      padding: 2px 15px !important;
      border: 1px solid rgba(0,127,235,0.1);
    }

    .but-group-content{
      border: 1px solid #007FEB !important;
      background: rgba(0,127,235,0.1) !important;

      .name_text{
        color: #007FEB !important;
      }
    }
  }

  //白色背景连接在一起
  .whiteBgConnect{
    border-radius: 8px;
    background: #FFFFFF;

    .but_list_li {
      border-radius: 0;
      background: none;
      margin-right: 1px;

      &:first-child {
        border-radius: 8px 0 0 8px;
      }

      &:last-child {
        border-radius: 0 8px 8px 0;
      }
    }
  }

  .content_right{
    flex: 1;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
