<template>
    <div class="w-full h-full flex flex-col justify-start items-stretch box-border">
        <!-- 查询栏目 123-->
        <div class="h-32px pt-17px pb-20px box-content">
            <SearchBar :config="searchConfig" v-model="formData" :rules="rules" :initData="searchInitData"
                @search="tiggerSearch" @export="tiggerExport" @reset="onReset" />
        </div>
        <!-- 表格 -->
        <div class="flex-1 overflow-hidden">
            <SearchTable ref="tableRef" :fetch-table-data="onSearch" :export-config="exportConfig" :columns="columns"
                pagination-way="back">
            </SearchTable>
        </div>
    </div>
</template>

<script setup>


import SearchBar from "@/components/searchBar/index.vue";
import SearchTable from "@/components/table/index.vue";
// hooks
import useSearchBar from "./useSearchBar";
import useTable from "./useTable";

const { searchConfig, rules, formData, searchInitData } = useSearchBar();
const { tableRef, columns, exportConfig, tiggerSearch,tiggerExport, onSearch,onReset } = useTable({ formData });
</script>

<style lang="scss" scoped>
</style>