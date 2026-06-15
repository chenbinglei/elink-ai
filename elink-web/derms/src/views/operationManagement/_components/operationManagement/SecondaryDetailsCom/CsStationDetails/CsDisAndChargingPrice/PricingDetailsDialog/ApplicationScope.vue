<template>
  <div class="content_body">
    <el-table :data="list" :max-height="tableMaxHeight">
      <el-table-column label="桩编码">
        <template #default="{ row }">{{ $filters.moreData(row.pileCode) }}</template>
      </el-table-column>
      <el-table-column label="桩名称">
        <template #default="{ row }">{{ $filters.moreData(row.pileName) }}</template>
      </el-table-column>
      <el-table-column label="下发结果">
        <template #default="{ row }">
          <div class="takeResult" :class="'takeResult' + row.takeResult">
            <span>{{ $filters.takeResult(row.takeResult) }}</span>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script lang="ts">
import {defineComponent, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "ApplicationScope",
  props: {
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {
    const that = reactive({
      list: [],
      tableMaxHeight: 320,
    });

    const watchReturnDataInfo = watch(() => props.returnDataInfo, (newReturnDataInfo) => {
      that.list = newReturnDataInfo.pirceAppliedRangeList;
    }, {deep: true});

    return {...toRefs(that), watchReturnDataInfo};
  }
});
</script>
<style lang="scss" scoped>
.takeResult{
  width: fit-content;
  height: 32px;
  padding: 0 16px;
  border-radius: 8px;
  font-size: 14px;
  color: #888888;
  line-height: 32px;
  background: rgba(136, 136, 136, .2);
}
.takeResult0{
  color: #41CB4A;
  background: rgba(65, 203, 74, .2);
}
</style>