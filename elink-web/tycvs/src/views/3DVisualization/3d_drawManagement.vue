<template>
  <div class="app-container fd-column">

    <div class="app-container-right">
      <div class="header-form">
        <Tabs isRightSlot :tabsArray="tabs_list" v-model:tabsIndex="tabsIndex">
          <template #rightButtonSlot>
            <el-button :icon="Plus" class="whiteFontButtons" @click="clickAddButFun">新建</el-button>
          </template>
        </Tabs>
        <div class="header-form-bottom">
          <el-form :model="formInline" inline @submit.prevent="listArray">
            <el-row :gutter="16">
              <el-col :md="8" :sm="12" :xl="6" :xs="24">
                <el-form-item label="关键词：">
                  <el-input v-model="formInline.pileName" clearable placeholder="搜索文件/文件夹名称"/>
                </el-form-item>
              </el-col>
              <el-col :md="8" :sm="12" :xl="6" :xs="24">
                <el-form-item>
                  <el-button :icon="Search" class="whiteFontButtons" @click="listArray">查询</el-button>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
      </div>

      <div v-resize="setTableMaxHeight" class="tableContent bg_color_class">
        <TableHeaderTitle title="3D可视化"></TableHeaderTitle>
        <div ref="tableCenterRef" class="tableCenter">
          <el-table v-loading="listLoading" row-key="id" border :data="list" :max-height="tableMaxHeight">
            <el-table-column fixed label="序号" type="index" width="80"></el-table-column>
            <el-table-column label="名称">
              <template #default="{ row }">{{ $filters.moreData(row.deviceNumber) }}</template>
            </el-table-column>
            <el-table-column label="状态">
              <template #default="{ row }">{{ $filters.moreData(row.deviceNumber) }}</template>
            </el-table-column>
            <el-table-column label="类型">
              <template #default="{ row }">{{ $filters.moreData(row.deviceNumber) }}</template>
            </el-table-column>
            <el-table-column label="更新时间">
              <template #default="{ row }">{{ $filters.moreData(row.deviceNumber) }}</template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }"></template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {Plus, Search} from "@element-plus/icons-vue";
import {reactive, ref, toRefs} from "vue";

export default {
  name: "3d_drawManagement",
  setup() {
    const that = reactive({
      Plus,
      Search,
      formInline: {},
      oldFormInline: {},

      list: [],
      listLoading: false,
      tableMaxHeight: 300,

      tabsIndex: 1,
      tabs_list:[{name:"文件",id: 1},{name:"回收站",id: 2}]
    })


    const clickAddButFun = ()=>{

    }

    // 计算出表格最大高度
    const tableCenterRef = ref(null);
    const setTableMaxHeight = () => {
      that.tableMaxHeight = tableCenterRef.value.offsetHeight;
    }

    return {...toRefs(that), setTableMaxHeight, tableCenterRef, clickAddButFun}
  }
}
</script>

<style lang="scss" scoped>
.header-form-bottom{
  margin-top: 12px;
}
</style>