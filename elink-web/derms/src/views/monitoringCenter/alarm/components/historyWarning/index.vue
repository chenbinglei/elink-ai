<template>
  <div class="w-full h-full" v-loading="loading">
    <div class="h-32px pt-17px pb-20px box-content">
      <SearchBar :config="searchConfig" v-model="formData" :rules="rules" :initData="searchInitData"
      @search="tiggerSearch" @export="tiggerExport" @reset="onReset" />
    </div>
    <!-- 表格 -->
    <div class="flex-1 overflow-hidden" style="height:90%;">

      <SearchTable ref="tableRef" :fetch-table-data="onSearch" :export-config="exportConfig" :columns="columns" @operationBtn="operationBtn"
          pagination-way="back">
      </SearchTable>
    </div>
    <!-- 弹框 查看-->
    <warningHistory :visible="isVisible" :activeRow="activeRow" @close="isVisible = false"></warningHistory>
  </div>
</template>
<script setup>
import { ref } from "vue";
import SearchBar from "@/components/searchBar/index.vue";
import SearchTable from "@/components/table/index.vue";
import warningHistory from "../warningHistory.vue";
// hooks
import useSearchBar from "./useSearchBar";
import useTable from "./useTable";
const props = defineProps({
  siteList: {
    type: Array,
    default: ''
  },
});
const { searchConfig, rules, formData, searchInitData } = useSearchBar(props);
const { tableRef, columns, exportConfig, tiggerSearch,tiggerExport, onSearch,onReset } = useTable({ formData });
const isVisible = ref(false);
const activeRow = ref({})
// 操作项
const operationBtn = ({row, type}) => {
  activeRow.value = row
  if(type == 'detail') isVisible.value = true;
};
</script>
<style lang="scss" scoped>
.dot-line{
  width: 1px;
  flex: 1;
  border-left: 1px dotted #0070BB;
}
.dialog-title{
  color: #7DCBFF;
  span{ padding: 10px 20px; }
  .active{
    color: #fff;
    background: linear-gradient( 180deg, rgba(0,168,255,0) 0%, rgba(79,176,251,0.3) 100%);
  }
}
.dialog-content{
  padding: 32px;
  .acc-left{box-sizing: border-box; padding: 10px; background: linear-gradient( 90deg, rgba(0,204,255,0) 0%, rgba(0,204,255,0.2) 52%, rgba(0,204,255,0) 100%);height: 55px;font-weight: 400;font-size: 14px;}
  .acc-right{
    box-sizing: border-box;
    flex: 1;
    h3{line-height: 28px;font-size: 14px;font-family: Microsoft YaHei, Microsoft YaHei;font-weight: bold;}
    .acc-r-event{
      padding: 12px 32px 5px 0;
    }
    .acc-r-event-list{
      width: 100%;
      background: rgba(0,204,255,0.1);
      line-height: 34px;
      padding: 0 12px;
      font-size: 14px;
      font-family: Microsoft YaHei, Microsoft YaHei;
      span{
        color: #7DCBFF;
        margin-right: 44px;
      }
    }
  }
}
</style>