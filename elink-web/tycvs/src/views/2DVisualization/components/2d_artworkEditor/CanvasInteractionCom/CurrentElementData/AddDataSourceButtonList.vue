<template>
  <el-popover :width="200" placement="bottom-start" trigger="hover">
    <template #reference>
      <div class="content_body">
        <el-button v-if="buttonType === 1" class="whiteFontButtons">添加动态数据</el-button>
        <el-link v-else type="primary">
          <span class="iconfont icon-tianjia"></span>
          <span class="link_text">添加动态数据</span>
        </el-link>
      </div>
    </template>
    <div class="content_list scrollbarStyle">
      <template v-for="(item,index) in dynamic_data_list" :key="index">
        <div v-if="item.key" class="content_list_li" @click="clickButItemFun(item)">
          <div class="content_list_li_left">{{ item.label }}</div>
          <div class="content_list_li_right"></div>
        </div>
        <div v-else class="content_list_line"></div>
      </template>
    </div>
    <PelFieldAssociationVarDialog v-if="pelFieldAssociationVarVisible" v-model:isVisible="pelFieldAssociationVarVisible" :activeEditInfo="activeEditInfo" @changeEvent="changeEvent" />
  </el-popover>
</template>

<script lang="ts">
import {pelAttributeNameList} from "@/utils/publicParam";
import {reactive, toRefs, defineComponent, getCurrentInstance} from "vue";
import PelFieldAssociationVarDialog from "./PelFieldAssociationVarDialog.vue";

export default defineComponent({
  name: "AddDataSourceButtonList",
  components: {PelFieldAssociationVarDialog},
  props: {
    buttonType: {
      type: Number,
      default: 1
    }
  },
  emits:["changeEvent"],
  setup() {
    const {emit} = getCurrentInstance();

    const that = reactive({
      activeEditInfo: {},
      pelFieldAssociationVarVisible: false,
      dynamic_data_list: [
        {label: "自定义", key: "custom"},
        {},
        ...pelAttributeNameList
      ]
    })

    const clickButItemFun = (data) => {
      that.activeEditInfo = data;
      that.pelFieldAssociationVarVisible = true;
    }

    const changeEvent = (data) => {
      // console.log(data);
      emit("changeEvent",{ operateData: "realTimes",...data });
    }

    return {...toRefs(that), clickButItemFun, changeEvent}
  }
})

</script>

<style lang="scss" scoped>
.content_body{
  width: fit-content;
}

.content_list {
  width: 100%;
  max-height: 380px;
  overflow-y: auto;

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

    .link_text{
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