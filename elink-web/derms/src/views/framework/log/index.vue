<template>
  <div class="w-full h-full wrap">
    <div class="h-32px pb-15px pt-15px box-content flex-center-between">

      <SearchBar :config="searchConfig" v-model="formData" :rules="rules" :initData="searchInitData"
      @search="tiggerSearch" @export="tiggerExport" @reset="onReset" />
    </div>
    <!-- 表格 -->
    <div class="flex-1 overflow-hidden" style="height: calc(100vh - 200px);">
      <SearchTable ref="tableRef" :fetch-table-data="onSearch" :export-config="exportConfig" :columns="columns" @operationBtn="operationBtn"
          pagination-way="back">
      </SearchTable>
    </div>
  </div>
</template>
<script setup>
import SearchBar from "@/components/searchBar/index.vue";
import SearchTable from "@/components/table/index.vue";
import useSearchBar from "./useSearchBar";
import useTable from "./useTable";
const { searchConfig, rules, formData, searchInitData } = useSearchBar();
const { tableRef, columns, exportConfig, tiggerSearch,tiggerExport, onSearch,onReset } = useTable({ formData });
</script>
<style lang="scss" scoped>
.wrap{
  box-sizing: border-box;
  background: url("@/assets/image/station-details/sta-detail-bg.png") no-repeat;
  background-size: 100% 100%;
  width: 100%;
  height: 100%;
  padding: 0 24px;
}
</style>