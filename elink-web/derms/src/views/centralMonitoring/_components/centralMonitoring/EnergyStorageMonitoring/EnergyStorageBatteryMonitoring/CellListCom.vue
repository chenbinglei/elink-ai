<template>
  <div class="cellListCom">
    <template v-if="cellList && cellList.length">
      <Pagination :currentPage="currentPage" :pageSize="pageNum" :layout="layout" :totalNumber="totalNumber"
        @pageChange="queryCellListByPage" />
      <div class="content_bottom">
        <el-row :gutter="8">
          <el-col v-for="(item, index) in cellList" :key="index" :lg="8" :md="12" :sm="24" :xl="6">
            <div :class="{ active_class: activeCellIndex === index }" class="content_bt_card flex-jc-ai-center"
              @click="clickCellItemFun(index)">
              <div class="bt_card_top">
                <span v-if="index < 9" class="index_text">0</span>
                <span class="index_text">{{ index + 1 }}</span>
              </div>
              <div class="bt_card_bottom flex-ai-center">
                <div class="number_left flex-ai-center">
                  <div class="number_text textTwo">{{ $filters.moreData(item) }}</div>
                  <div class="number_line"></div>
                </div>
                <div class="unit">{{ tabsIndex === 1 ? "V" : "°C" }}</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </template>
    <null-data v-else words="无单体电芯数据"></null-data>
  </div>
</template>

<script>
import { reactive, defineComponent, toRefs, watch, getCurrentInstance } from "vue";
import { findCellListByPage } from "@/api/centralMonitoring/centralMonitoring";

export default defineComponent({
  name: "CellListCom",
  props: {
    activeDeviceId: {
      type: [Number, String],
      default: ""
    },
    // 查询类型 1-温度 2-电压
    tabsIndex: {
      type: Number,
      default: 1
    },
    // 当前选中的id
    activeCellIndex: {
      type: [Number, String],
      default: ""
    },
    listLoading: {
      type: Object,
      default: false
    }
  },
  emits: ["update:activeCellIndex", "changeEvent", "update:listLoading",],
  setup(props) {

    const { emit } = getCurrentInstance();

    const that = reactive({
      pageNum: 20,
      cellList: [],
      currentPage: 1,
      totalNumber: 0,
      layout: "total, prev, pager, next, sizes",
    });

    // 分页查询电芯列表
    const queryCellListByPage = () => {
      emit("update:listLoading", true);
      findCellListByPage({ size: that.pageNum, page: that.currentPage, queryType: props.tabsIndex, deviceId: props.activeDeviceId }).then(res => {
        that.totalNumber = res.data.totalSize;
        that.cellList = res.data.items;
        emit("update:listLoading", false);

        let activeCellIndex = "";
        if (that.cellList && that.cellList.length) {
          activeCellIndex = props.activeCellIndex;
          if (!activeCellIndex || (props.activeCellIndex + 1) > that.cellList.length) activeCellIndex = 0;
        }
        clickCellItemFun(activeCellIndex);
      }).catch((error) => {
        emit("update:listLoading", false);
        if (error && error.code === 88886) return;
        clickCellItemFun("");
        that.totalNumber = 0;
        that.cellList = [];
      });
    };

    const clickCellItemFun = (index) => {
      emit("update:activeCellIndex", index);
      emit("changeEvent");
    };

    const watchDeviceId = watch([() => props.activeDeviceId, () => props.tabsIndex], ([newActiveDeviceId, newTabsIndex]) => {
      if (newActiveDeviceId) queryCellListByPage();
    }, { deep: true, immediate: true });

    return { ...toRefs(that), queryCellListByPage, watchDeviceId, clickCellItemFun };
  }
});
</script>

<style lang="scss" scoped>
.cellListCom {
  height: 100%;
  display: flex;
  flex-direction: column;

  .content_bottom {
    flex: 1;
    width: 100%;
    padding-top: 10px;
    flex-basis: auto;
    overflow-y: auto;
    box-sizing: border-box;

    .content_bt_card {
      cursor: pointer;
      padding: 5px 12px;
      border-radius: 6px;
      margin-bottom: 8px;
      flex-direction: column;
      box-sizing: border-box;
      border: 1px solid #2deaff1a;
      background: linear-gradient(205.99deg, #09545580 14.49%, #1594bb80 89.5%);

      .bt_card_top {
        width: 48px;
        height: 16px;
        text-align: center;
        line-height: 16px;
        border-radius: 11px;
        background: #0000001a;
        box-shadow: 0 0 5px 1px #00f7ff33;

        .index_text {
          color: #08f7fd;
          font-size: 14px;
        }
      }

      .bt_card_bottom {
        width: 100%;
        margin-top: 8px;

        .number_left {
          flex: 1;
          flex-basis: auto;
          margin-right: 2px;
          flex-direction: column;

          .number_text {
            width: 100%;
            color: #ffffff;
            font-size: 18px;
            font-weight: 700;
          }

          .number_line {
            width: 100%;
            height: 6px;
            margin-top: -6px;
            border-radius: 4px;
            background: #ffffff1a;
          }
        }

        .unit {
          font-size: 14px;
          color: #ffffffcc;
        }
      }
    }

    .active_class {
      border: 1px solid #2deaff;
      background: linear-gradient(206.57deg, #016a774d 15.8%, #46e8dd4d 90.57%);
    }
  }
}
</style>