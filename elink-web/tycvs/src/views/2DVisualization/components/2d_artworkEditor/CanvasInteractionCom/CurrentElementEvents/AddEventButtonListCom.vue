<template>
  <el-popover :width="200" placement="bottom-start" trigger="click">
    <template #reference>
      <div class="content_body">
        <el-button class="whiteFontButtons">添加交互事件</el-button>
      </div>
    </template>
    <div class="content_list scrollbarStyle">
      <template v-for="(item,index) in dynamic_data_list" :key="index">
        <template v-if="item.key">
          <div class="content_list_li" @click="clickButItemFun(item)">
            <div class="content_list_li_left">{{ item.label }}</div>
            <div class="content_list_li_right"></div>
          </div>
        </template>
        <div v-else class="content_list_line"></div>
      </template>
    </div>
  </el-popover>
</template>

<script>
import {reactive, toRefs, defineComponent, getCurrentInstance} from "vue";

export default defineComponent({
  name: "AddEventButtonListCom",
  emits: ["changeEvent"],
  setup() {
    const {emit} = getCurrentInstance();

    const that = reactive({
      activeEditInfo: {},
      pelFieldAssociationVarVisible: false,
      dynamic_data_list: [
        {label: "单击", key: 'click'},
        {label: "双击", key: 'dblclick'},
        {},
        {label: "鼠标右键", key: 'contextmenu'},
        {label: "鼠标移入", key: 'enter'},
        {label: "鼠标移出", key: 'leave'},
        {},
        {label: "获取焦点", key: 'active'},
        {label: "失去焦点", key: 'inactive'},
        {},
        {label: "鼠标按下", key: 'mousedown'},
        {label: "鼠标抬起", key: 'mouseup'},
        {},
        {label: "值变化", key: 'valueUpdate'},
        {label: "监听全局消息", key: 'message'},
      ]
    })

    const clickButItemFun = (data) => {
      emit("changeEvent", {type: "AddEventButtonListCom", ...data});
    }

    return {...toRefs(that), clickButItemFun}
  }
})

</script>

<style lang="scss" scoped>
.content_body {
  width: 100%;
  padding: 4px 12px;
  box-sizing: border-box;

  .el-button {
    width: 100%;
  }
}

.content_list {
  width: 100%;
  max-height: 380px;

  .content_list_li {
    height: 32px;
    padding: 0 12px;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: var(--color);
    font-size: 12px;

    .iconfont {
      font-size: 12px;
    }

    .link_text {
      margin-left: 6px;
    }

    &:hover {
      cursor: pointer;
      background: var(--el-menu-hover-bg-color);
    }
  }

  .content_list_line {
    height: 1px;
    margin: 4px 0;
    background-color: #EAEEF1;
  }
}

</style>