<template>
  <el-popover :width="160" placement="bottom-start" trigger="hover">
    <template #reference>
      <span class="iconfont icon-wenjiantianjia"></span>
    </template>
    <div class="content_list scrollbarStyle">
      <template v-for="(item,index) in dynamic_data_list" :key="index">
        <template v-if="item.key && item.key !== 'duration'">
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

<script lang="ts">
import {reactive, toRefs, defineComponent, getCurrentInstance} from "vue";

export default defineComponent({
  name: "AddFrameButtonListCom",
  emits: ["changeEvent"],
  props: {
    attributeNameList: {
      type: Array,
      default: () => []
    },
    activeIndex: {
      type: Number,
      default: 0
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      pelFieldAssociationVarVisible: false,
      dynamic_data_list: props.attributeNameList,
    })

    const clickButItemFun = (data) => {
      emit("changeEvent", {operateType: "AddFrameButtonListCom",activeIndex: props.activeIndex, ...data});
    }

    return {...toRefs(that), clickButItemFun}
  }
})

</script>

<style lang="scss" scoped>
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

.iconfont{
  margin-right: 5px;
}
</style>