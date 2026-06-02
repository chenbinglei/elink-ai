<template>
  <div class="tabs">
    <div class="tabs_list">
      <template v-for="(item,index) in tabsArray" :key="index">
        <div :class="{ active: tabsIndex === item.id }" class="tabs_li" @click="selectIndex(item.id)">
          <span>{{ item.channelName }}</span>
          <el-button v-if="tabsIndex === item.id" :icon="Close" type="text" @click="clickClose(item.id)"></el-button>
        </div>
      </template>
    </div>
  </div>
</template>
<script>
import {Close} from '@element-plus/icons-vue';
import {getCurrentInstance, reactive, toRefs} from "vue";

export default {
  name: "TabActiveBorder",
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
  },
  setup(props) {

    const {emit} = getCurrentInstance();
    const that = reactive({
      Close
    })

    const selectIndex = (activeId) => {
      let findItem = props.tabsArray.find(item => item.id === activeId);
      emit("changeEvent", { type: 'selectIndex',...findItem });
    }

    const clickClose = (activeId) => {
      let findItem = props.tabsArray.find(item => item.id === activeId);
      emit("changeEvent", { type: 'clickClose',...findItem });
    }

    return {...toRefs(that), selectIndex, clickClose}
  }
}
</script>
<style lang="scss" scoped>
.tabs {
  width: 100%;

  .tabs_list {
    width: 100%;
    padding: 0 24px;
    border-bottom: 1px solid #E3E3E3;
    box-sizing: border-box;
    display: flex;

    .tabs_li {
      width: 144px;
      padding: 8px;
      font-size: 14px;
      color: #242424;
      cursor: pointer;
      background: #ffffff;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .el-button {
        width: 18px;
        height: 18px;
      }
    }

    .active {
      margin-bottom: -1px;
      color: #1F74E2;
      border: 1px solid #E3E3E3;
      border-bottom: none;
    }
  }
}
</style>
