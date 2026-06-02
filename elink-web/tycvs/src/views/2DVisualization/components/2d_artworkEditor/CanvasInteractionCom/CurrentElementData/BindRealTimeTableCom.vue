<template>
  <div class="bindRealTimeTableCom">
    <el-table :data="list" :max-height="tableMaxHeight">
      <el-table-column label="数据名" show-overflow-tooltip>
        <template #default="{ row }">
          <el-tooltip effect="dark" placement="top">
            <span>{{ row.label }}</span>
            <template #content>{{ row.key }}</template>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="变量">
        <template #default="{ row, $index }">
          <div class="pointer variableClass" @click="clickButItemFun(3, row, $index)">
            <template v-if="row.variableName || row.variableId">
              <el-tooltip effect="dark" placement="top">
                <span>{{ row.variableName }}</span>
                <template #content>
                  <span v-if="row.variableType">{{ $filters.variableType(row.variableType) }}：</span>
                  <span>{{ row.variableId }}</span>
                </template>
              </el-tooltip>
            </template>
            <span v-else class="iconfont icon-guanlian"></span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="45">
        <template #default="{ row, $index }">
          <el-dropdown>
            <div class="iconfont_item"><span class="iconfont icon-caidan"></span></div>
            <template #dropdown>
              <el-dropdown-menu>
                <template v-for="(item,index) in dropdown_menu_list" :key="index">
                  <el-dropdown-item @click="clickButItemFun(item.id, row, $index)">{{ item.name }}</el-dropdown-item>
                </template>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <PelBindVariableDialog v-if="pelBindVariableVisible" v-model:isVisible="pelBindVariableVisible" :activeEditInfo="activeEditInfo" @changeEvent="changeEvent" />
    <PelFieldAssociationVarDialog v-if="pelFieldAssociationVarVisible" v-model:isVisible="pelFieldAssociationVarVisible" :activeEditInfo="activeEditInfo" @changeEvent="changeEvent" />
  </div>
</template>

<script>
import PelBindVariableDialog from "./PelBindVariableDialog.vue";
import PelFieldAssociationVarDialog from "./PelFieldAssociationVarDialog.vue";
import {reactive, toRefs, defineComponent, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "BindRealTimeTableCom",
  components: {PelFieldAssociationVarDialog,PelBindVariableDialog},
  props: {
    realTimes: {
      type: Array,
      default: () => []
    }
  },
  setup(props) {

    const {emit} = getCurrentInstance();

    const that = reactive({
      list: [],
      operateType: 0, // 1: 编辑数据   2：删除数据  3：绑定变量
      tableMaxHeight: 380,
      activeEditInfo: {},
      activeEditIndex: -1,
      pelBindVariableVisible: false,
      pelFieldAssociationVarVisible: false,
      dropdown_menu_list: [{id: 1, name: "编辑"},{id: 2, name: "删除"}]
    })

    const clickButItemFun = (operateType, row, index)=>{
      that.operateType = operateType;

      if(operateType === 1){
        that.activeEditIndex = index;
        that.activeEditInfo = JSON.parse(JSON.stringify(row));
        that.pelFieldAssociationVarVisible = true;
      }

      if(operateType === 2){
        that.list.splice(index,1);
        updateRealTimeFun();
      }

      if(operateType === 3){
        that.activeEditIndex = index;
        that.activeEditInfo = JSON.parse(JSON.stringify(row));
        that.pelBindVariableVisible = true;
      }
    }

    const changeEvent = (data) => {
      // console.log(data)
      // console.log(that.operateType)
      // console.log(that.activeEditIndex)

      // if(that.operateType === 1){
        that.list[that.activeEditIndex] = JSON.parse(JSON.stringify(data));
        updateRealTimeFun();
      // }

      // if(that.operateType === 3){
      //   that.list[that.activeEditIndex].variableId = data.variableId;
      //   that.list[that.activeEditIndex].variableName = data.variableName;
      //   updateRealTimeFun();
      // }
    }

    // 更新画笔实时数据
    const updateRealTimeFun = ()=>{
      emit("changeEvent",{
        operateData: "updateRealTime",
        realTimes: JSON.parse(JSON.stringify(that.list))
      });
    }

    const watchRealTimes = watch(() => props.realTimes, (newRealTimes) => {
      that.list = JSON.parse(JSON.stringify(newRealTimes));

    }, {deep: true, immediate: true})

    return {...toRefs(that), watchRealTimes, clickButItemFun, changeEvent, updateRealTimeFun}
  }
})
</script>

<style lang="scss" scoped>
.bindRealTimeTableCom {
  margin-bottom: 12px;

  .iconfont {
    font-size: 18px;
    cursor: pointer;
  }

  :deep(.el-table){
    .cell{
      padding: 0 2px;
    }

    .variableClass{
      color: #1F74E2;
    }
  }
}
</style>