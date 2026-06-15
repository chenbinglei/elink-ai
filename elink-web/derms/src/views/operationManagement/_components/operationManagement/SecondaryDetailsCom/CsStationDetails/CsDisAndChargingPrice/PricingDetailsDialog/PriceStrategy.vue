<template>
  <div class="content_body">
    <div class="content_body_title">{{ $filters.pileDeviceType(returnDataInfo.deviceType) }}计费</div>
    <div class="content_table">
      <el-table :data="list" :max-height="tableMaxHeight">
        <el-table-column label="时间">
          <template #default="{ row }">
            <span>{{ $filters.moreData(row.startTime) }}</span>
            <span>~</span>
            <span>{{ $filters.moreData(row.endTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="时段">
          <template #default="{ row }">
            <div class="periodType" :class="'periodType' + row.periodType">
              <span>{{ $filters.periodType(row.periodType) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="电费（元）">
          <template #default="{ row }">{{ $filters.moreData(row.electMoney) }}</template>
        </el-table-column>
        <el-table-column label="服务费（元）" v-if="chargingType === 1">
          <template #default="{ row }">{{ $filters.moreData(row.serviceMoney) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script lang="ts">
import {defineComponent, reactive, toRefs, watch} from "vue";

export default defineComponent({
  name: "PriceStrategy",
  props: {
    returnDataInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
    // 1： 充   2；放
    chargingType: {
      type: Number,
      default: 1
    },
  },
  setup(props) {
    const that = reactive({
      list: [],
      tableMaxHeight: 320,
    });

    const watchReturnDataInfo = watch(() => props.returnDataInfo, (newReturnDataInfo) => {
      that.list = newReturnDataInfo.priceConfigList;
    }, {deep: true});

    return {...toRefs(that), watchReturnDataInfo};
  }
});
</script>
<style lang="scss" scoped>
.content_body {
  margin-bottom: 12px;

  .content_body_title {
    color: #121C3F;
    font-size: 14px;
    margin-bottom: 16px;
    font-weight: bold;
    padding-left: 12px;
    box-sizing: border-box;
  }

  .periodType{
    width: fit-content;
    height: 32px;
    padding: 0 16px;
    border-radius: 8px;
    background: #56ADF7;
    font-size: 14px;
    color: #FFFFFF;
    line-height: 32px;
  }
  .periodType1{
    background: #FB6868;
  }
  .periodType2{
    background: #FD9449;
  }
  .periodType4{
    background: #6DCF36;
  }
  .periodType5{
    background: #36CFC2;
  }
}

</style>