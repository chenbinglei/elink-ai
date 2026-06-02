<template>
  <div class="app-container">
    <div class="app-container-right">
      <div ref="headerFormRef" class="header-form">
        <el-form :model="formInline" inline @submit.native.prevent>
          <el-form-item label="关键字：">
            <el-input v-model="formInline.operatorName" clearable placeholder="请输入关键词搜索">
              <template #append>
                <el-button :icon="Search" @click="listArray('resetPage')"></el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item style="float: right">
            <el-button :disabled="isAddButtonClick" :icon="CirclePlus" class="whiteFontButtons" @click="clickAddBut">新增运营商</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="tableContent">
        <div ref="tableCenterRef" v-resize="setTableMaxHeight" class="tableCenter">
          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight" border stripe>
            <el-table-column align="center" label="序号" type="index" width="80">
              <template #default="{ $index }">{{ $index + 1 + (currentPage - 1) * pageNum }}</template>
            </el-table-column>
            <el-table-column align="center" label="运营商 ID">
              <template #default="{ row }">{{ $filters.moreData(row.operatorId) }}</template>
            </el-table-column>
            <el-table-column align="center" label="运营商名称" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.operatorName) }}</template>
            </el-table-column>
            <el-table-column align="center" label="运营商电话1">
              <template #default="{ row }">{{ $filters.moreData(row.operatorTel1) }}</template>
            </el-table-column>
            <el-table-column align="center" label="备注" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.operatorNote) }}</template>
            </el-table-column>
            <el-table-column align="center" label="创建时间">
              <template #default="{ row }">{{ $filters.moreData(row.createTime) }}</template>
            </el-table-column>
            <!--            <el-table-column align="center" label="加密设置">-->
            <!--              <template #default="{ row }">-->
            <!--                <el-link :underline="false" @click="clickOperate(3, row)">查看</el-link>-->
            <!--              </template>-->
            <!--            </el-table-column>-->
            <el-table-column align="center" label="操作">
              <template #default="{ row }">
                <el-link :underline="false" @click="clickOperate(1, row)">编辑</el-link>
                <span style="margin:0 8px">|</span>
                <el-link :underline="false" type="danger" @click="clickOperate(2, row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination">
          <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
        </div>
      </div>
    </div>
    <EncryptedInfoDialog v-if="encryptedInfoVisible" v-model:isVisible="encryptedInfoVisible" :operatorId="operatorId"/>
    <AddOperatorDialog v-if="addOperatorVisible" v-model:isVisible="addOperatorVisible" :operatorId="operatorId" :titleName="titleName" @changeEvent="listArray('resetPage')"/>
  </div>
</template>

<script>
import {ElMessage, ElMessageBox} from "element-plus";
import {computed, onMounted, reactive, ref, toRefs} from "vue";
import {CirclePlus, Search} from '@element-plus/icons-vue';
import {AddOperatorDialog, EncryptedInfoDialog} from "@/views/configCenter/component";
import {deleteOperatorInfoById, findOperatorInfoByPage} from "@/api/configCenter/operatorManagement";
import {operateButtonIsClick} from "@/utils";

export default {
  name: "operatorManagement",
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    }
  },
  components: {AddOperatorDialog, EncryptedInfoDialog},
  setup(props) {
    const that = reactive({
      Search,
      CirclePlus,
      formInline: {},

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      operatorId: "",
      titleName: "新增运营商",
      addOperatorVisible: false,
      encryptedInfoVisible: false,
    })

    const listArray = (operateType) => {
      that.listLoading = true;

      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findOperatorInfoByPage({page: that.currentPage, size: that.pageNum, ...that.formInline}).then(res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if (operateType === "refresh") ElMessage({type: "success", message: "刷新成功", showClose: true});
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return
        that.totalNumber = 0;
        that.list = [];
      })
    }

    const clickAddBut = () => {
      that.operatorId = "";
      that.titleName = "新增运营商";
      that.addOperatorVisible = true;
    }

    const clickOperate = (operateType, row) => {

      if (operateType === 1) {
        that.operatorId = row.id;
        that.titleName = "编辑运营商";
        that.addOperatorVisible = true;
      }

      if (operateType === 2) {
        ElMessageBox.confirm(`确定删除（${row.operatorName}）吗？`, "提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "deleteMsgBoxClass", showClose: false, type: 'warning',
        }).then(() => {
          deleteOperatorInfoById({id: row.id}).then(() => {
            listArray("resetPage");
            ElMessage({type: "success", message: "删除成功!", showClose: true});
          })
        }).catch(() => {
          console.log("取消操作！");
        });
      }

      if (operateType === 3) {
        that.operatorId = row.id;
        that.encryptedInfoVisible = true;
      }
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight - 110;
    }

    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/system/configureCenter/saveOrUpdateOperatorInfo')
    })

    onMounted(() => {
      listArray();
    })

    return {...toRefs(that), headerFormRef, setTableMaxHeight, listArray, clickAddBut, clickOperate, isAddButtonClick}
  }
}
</script>

<style scoped>

</style>
