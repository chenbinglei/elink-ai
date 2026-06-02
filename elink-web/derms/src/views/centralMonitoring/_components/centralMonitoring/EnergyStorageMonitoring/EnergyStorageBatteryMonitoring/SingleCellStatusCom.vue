<template>
  <TitleView :isTitleIcon="false" :title="titleName">
    <template #headerRight>
      <div class="header_form">
        <ButtonsTabs :tabsIndex="tabsIndex" :tabsArray="tabsArray"/>
      </div>
    </template>
    <template #content>
      <div class="content_body">
        <el-row v-loading="listLoading" :gutter="12">
          <el-col :lg="activeCellIndex || activeCellIndex === 0 ? 12 : 24" :md="24" :sm="24" class="content_body_li">
            <CellListCom :activeDeviceId="activeDeviceId" :tabsIndex="tabsIndex" :listLoading="listLoading" :activeCellIndex="activeCellIndex"/>
          </el-col>
          <template v-if="activeCellIndex || activeCellIndex === 0">
            <el-col :lg="12" :md="24" :sm="24" class="content_body_li">
              <template v-if="tabsIndex === 1">
                <SystemVarTimeChartCom ref="systemVarTimeChartComRef" titleName="电芯电压曲线" :fileName="fileName" :siteId="activeDeviceId" :isRequestType="2"
                                       :yAxisName="tempYAxisName" :dataIndex="activeCellIndex" :seriesListArray="voltageListArray" />
              </template>
              <template v-if="tabsIndex === 2">
                <SystemVarTimeChartCom ref="systemVarTimeChartComRef" titleName="电芯温度曲线" :fileName="fileName" :siteId="activeDeviceId" :isRequestType="2"
                                       :yAxisName="voltageYAxisName" :dataIndex="activeCellIndex" :seriesListArray="tempListArray" />
              </template>
            </el-col>
          </template>
        </el-row>
      </div>
    </template>
  </TitleView>
</template>

<script>
import CellListCom from "./CellListCom.vue";
import {reactive, defineComponent, toRefs} from "vue";
import {SystemVarTimeChartCom} from "@/views/centralMonitoring/_components/centralMonitoring/PublicComponents";

export default defineComponent({
  name: "SingleCellStatusCom",
  components:{CellListCom,SystemVarTimeChartCom},
  props: {
    activeDeviceId: {
      type: [Number, String],
      default: ""
    },
    fileName: {
      type: String,
      default: ""
    },
    titleName: {
      type: String,
      default: ""
    },
  },
  setup() {

    const that = reactive({
      tabsIndex: 1,
      listLoading: false,
      activeCellIndex: "",
      tabsArray: [{name: "电压", id: 1, iconName: "icon-dianya"}, {name: "温度", id: 2, iconName: "icon-wendu"}],
      tempYAxisName: "℃",
      tempListArray: [
        {
          data: [],
          type: 'line',
          name: '单体温度',
          color: "#79FFCC",
          fieldName: 'cell_temp',
        }
      ],
      voltageYAxisName: "V",
      voltageListArray: [
        {
          data: [],
          type: 'line',
          name: '单体电压',
          color: "#79C3FF",
          showSymbol: false,
          fieldName: 'cell_voltage',
        }
      ],
    });

    return {...toRefs(that)};
  }
});
</script>

<style lang="scss" scoped>
.header_form {

  :deep(.buttonsTabs) {
    width: fit-content;
    margin-right: 8px;

    .tabs_li {
      max-height: 32px;
      margin-right: 4px;
      padding: 6px 10px;
    }
  }
}

.content_body_li {
  height: 360px;
}
</style>