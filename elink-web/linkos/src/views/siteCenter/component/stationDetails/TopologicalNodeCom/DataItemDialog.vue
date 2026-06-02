<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName"
    disabledLoading width="640" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-table :data="dataItem" :max-height="tableMaxHeight" style="margin-bottom: 10px; ">
          <el-table-column label="数据名称" prop="dataName"></el-table-column>
          <el-table-column label="展示名称">
            <template #default="{ row }">
              <el-input v-model="row.showName" />
            </template>
          </el-table-column>
          <el-table-column label="数据展示">
            <template #default="{ row }">
              <div style="display: flex; align-items: center;">
                <span
                  :class="row.showType == 2 ? 'icon iconfont icon-yanjing_yincang_o' : 'iconfont icon-yanjing_xianshi_o'"
                  :style="{ color: row.showType == 1 ? '#2B66FE' : '#999' }" @click="toggleShow(row)"></span>
                <span style="margin-left: 8px;">{{ row.showType == 1 ? '显示' : '隐藏' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="位置">
            <template #default="{ row }">
              <el-select v-model="row.positionType">
                <el-option v-for="item in locationList" :key="item.value" :label="item.name" :value="item.value" />
              </el-select>
            </template>
          </el-table-column>
        </el-table>

      </div>
    </template>
  </Dialog>
</template>
<script>
import { getCurrentInstance, reactive, toRefs, watch, defineComponent, onMounted } from "vue";
import { getTopItemList } from "@/api/siteCenter/stationDetails";

export default defineComponent({
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "数据项配置"
    },
    nodeType: {
      type: String
    },
    siteTopItemList: {
      type: Array
    }
  },
  setup (props) {
    const { emit } = getCurrentInstance();
    const that = reactive({
      dialog_visible: props.isVisible,
      tableMaxHeight: 300,
      dataItem: [],
      locationList: [
        { value: 1, name: '上' },
        { value: 2, name: '下' },
        { value: 3, name: '左' },
        { value: 4, name: '右' },
      ]
    })
    // 在弹窗打开时调用接口
    const querygetTopItemList = () => {
      if (props.siteTopItemList.length) return that.dataItem = props.siteTopItemList
      getTopItemList({ nodeType: props.nodeType }).then(res => {
        that.dataItem = res.data
      })
    }
    const saveDialog = () => {
      emit("changedataItem", that.dataItem);
      that.dialog_visible = false;
    }
    const toggleShow = (row) => {
      row.showType = row.showType === 1 ? 2 : 1;
    };
    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });
    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });
    onMounted(() => {
      querygetTopItemList();
    })

    return { ...toRefs(that), saveDialog, toggleShow, watchVisible, watchDialogVisible, querygetTopItemList }
  }

})
</script>



<style scoped>
/* 添加基本样式确保可见 */
.el-table {
  width: 100%;
  margin-top: 20px;
}
</style>