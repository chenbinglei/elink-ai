<template>
  <div class="chargingCurrentPrice">
    <TitleView title="直流计费">
      <template #content>
        <div class="content_table" ref="tableContentRef"  v-resize="setTableMaxHeight">
          <el-table :data="list" :max-height="tableMaxHeight">
            <template #empty><null-data words="暂无数据"></null-data></template>
            <el-table-column label="时间">
              <template #default="{ row }">
                <span>{{ $filters.moreData(row.startTime) }}</span>
                <span>~</span>
                <span>{{ $filters.moreData(row.endTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="时段">
              <template #default="{ row }">
                <div :class="'periodType' + row.periodType" class="periodType">
                  <span>{{ $filters.periodType(row.periodType) }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="电费（元）">
              <template #default="{ row }">{{ $filters.moneyTwoNum(row.electMoney) }}</template>
            </el-table-column>
          </el-table>
        </div>
      </template>
    </TitleView>
  </div>
</template>

<script lang="ts">
import ChargingDcBilling from "./ChargingDcBilling.vue";
import ChargingAcBilling from "./ChargingAcBilling.vue";
import {defineComponent, onMounted, reactive, ref, toRefs, watch} from "vue";
import {findFixPriceRecordList} from "@/api/operationManagement/CsStationDetails";

export default defineComponent({
  name: "DisChargingCurrentPrice",
  components: {ChargingDcBilling, ChargingAcBilling},
  props: {
    // 1： 充   2；放
    chargingType: {
      type: Number,
      default: 2
    },
    siteId: {
      type: [Number, String],
      default: ""
    }
  },
  setup(props) {
    const that = reactive({
      list: [],
      tableMaxHeight: 380,
    });

    // 根据站点id查询定价记录
    const queryFixPriceRecordList = () => {
      that.listLoading = true;
      findFixPriceRecordList({priceType: props.chargingType, siteId: props.siteId, type: "DisChargingCurrentPrice"}).then(res => {
        let chargingInfo = res.data ? res.data : {};
        that.list = chargingInfo.dcPriceConfigList;
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      });
    };

    // 计算出表格最大高度
    const tableContentRef = ref(null);
    const setTableMaxHeight = ()=>{
      that.tableMaxHeight = tableContentRef.value.offsetHeight;
    };

    const watchSite = watch(() => props.siteId, (newSite) => {
      queryFixPriceRecordList();
    }, {deep: true});

    onMounted(() => {
      queryFixPriceRecordList();
    });

    return {...toRefs(that), queryFixPriceRecordList, watchSite, tableContentRef, setTableMaxHeight};
  }
});
</script>

<style lang="scss" scoped>
.periodType {
  width: fit-content;
  color: #FFFFFF;
  font-size: 14px;
  padding: 7px 16px;
  border-radius: 8px;
  box-sizing: border-box;
  background: rgba(109, 207, 54, .5);
}

.periodType1 {
  background: #FB6868;
}

.periodType2 {
  background: #FD9449;
}

.periodType3 {
  background: #56ADF7;
}

.periodType4 {
  background: #6DCF36;
}
</style>
