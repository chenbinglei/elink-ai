<template>
  <div class="sideToolbarCom">
    <template v-for="(item,index) in list" :key="index">
      <div class="com_list" :class="{ active_class: tabs_index === item.id,notAllowed: location }" @click="clickItemFun(item)">
        <span :class="item.iconName" class="iconfont"></span>
        <div class="com_list_text">{{ item.name }}</div>
      </div>
    </template>
  </div>
</template>
<script>
import {reactive, toRefs, defineComponent, onMounted, getCurrentInstance} from "vue";

export default defineComponent({
  name: "SideToolbarCom",
  props: {
    // 默认 获取第一个 (数组id)
    tabsIndex: {
      type: [Number, String],
      default: 1
    },
    // 组件名称
    componentName: {
      type: [Number, String],
      default: 1
    },
    // 是否可点击 false ： 可以   true: 不可以
    loading: {
      type: Boolean,
      default: false
    },
  },
  emits: ["update:tabsIndex","update:componentName"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      tabs_index: props.tabsIndex,
      component_name: props.componentName,
      list: [
        {id: "CanvasStructureCom", name: "结构", componentName: "CanvasStructureCom", iconName: "icon-tuceng"},
        {id: "BasicGraphicsCom", name: "图形", componentName: "CanvasElementList", iconName: "icon-a-bianzu4"},
        {id: "CustomElementCom", name: "图元", componentName: "CanvasElementList", iconName: "icon-tuyuanku"},
        {id: "CustomControlCom", name: "控件", componentName: "CanvasElementList", iconName: "icon-kongjian"},
        {id: "CustomChartCom", name: "图表", componentName: "CanvasElementList", iconName: "icon-tubiao"},
      ]
    })

    const clickItemFun = (data) => {
      if(props.loading)return
      that.tabs_index = data.id;
      that.component_name = data.componentName;
      emit("update:tabsIndex", that.tabs_index);
      emit("update:componentName", that.component_name);
    }

    onMounted(() => {})

    return {...toRefs(that), clickItemFun}
  }
})
</script>
<style lang="scss" scoped>
.sideToolbarCom {
  width: 50px;
  height: 100%;
  background-color: #F7F8FA;

  .active_class{
    color: var(--color-primary) !important;
    background-color: var(--color-background);
    border-left: 2px solid var(--color-primary);
  }

  .com_list {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    box-sizing: border-box;
    color: var(--color);
    padding: 14px 4px;

    .iconfont {
      font-size: 20px;
      margin-bottom: 2px;
      font-weight: 500;
    }

    .com_list_text {
      font-size: 12px;
    }

    &:hover {
      cursor: pointer;
      color: #1E71EC;
    }
  }
}
</style>