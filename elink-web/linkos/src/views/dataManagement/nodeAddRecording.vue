<template>
  <div class="app-container" v-resize="setTableMaxHeight">
    <HandleMenus :handleMenuArray="handleMenuArray" :isShowHeader="false" @handleMenuEvent="handleMenuEvent"></HandleMenus>
    <div class="app-container-right">
      <div class="header-form" ref="headerFormRef">
        <el-form :model="formInline" inline>
          <el-form-item>
            <el-input v-model="formInline.keyValue" :suffix-icon="Search" clearable placeholder="请输入关键字" @keyup.enter="listArray('refresh')">
              <template #prepend>
                <el-select v-model="formInline.keyType" placeholder="请选择" style="width: 110px">
                  <el-option v-for="item in keyTypeArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button class="whiteFontButtons" @click="listArray('refresh')">查询</el-button>
          </el-form-item>

          <el-form-item style="float: right">
            <el-button :icon="CirclePlus" class="whiteFontButtons" :disabled="isAddButtonClick" @click="clickAddBut">创建任务</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="tableContent">
        <div ref="tableCenterRef" class="tableCenter">
          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
            <el-table-column align="center" label="序号" width="60">
              <template #default="scope">{{ scope.$index + 1 + (currentPage - 1) * pageNum }}</template>
            </el-table-column>
            <el-table-column align="center" label="节点名称" prop="nodeName">
              <template #default="{ row }">{{ $filters.moreData(row.nodeName) }}</template>
            </el-table-column>
            <el-table-column align="center" label="节点编码" prop="nodeCode" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.nodeCode) }}</template>
            </el-table-column>
            <el-table-column align="center" label="补录范围" width="300">
              <template #default="{ row }">
                <span>{{ $filters.moreData(row.startTime) }}</span>
                <span>~</span>
                <span>{{ $filters.moreData(row.endTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="创建时间" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.createTime) }}</template>
            </el-table-column>
            <el-table-column align="center" label="状态">
              <template #default="{ row }">
                <span :class="'taskStatus' + row.addRecordState">{{ $filters.taskStatus(row.addRecordState) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="tablePagination">
          <Pagination v-model:currentPage="currentPage" v-model:pageSize="pageNum" :totalNumber="totalNumber" @pageChange="listArray"/>
        </div>
      </div>
    </div>

    <!--    创建任务-->
    <CreateNodeTask v-if="computeNodeVisible" v-model:isVisible="computeNodeVisible" :recordId="activeRecordId" @changEvent="listArray('refresh')"/>
  </div>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {reactive, toRefs, onMounted, ref, nextTick, computed} from "vue";
import {operateButtonIsClick, setTreeData} from "@/utils";
import {CirclePlus, Search} from "@element-plus/icons-vue";
import {CreateNodeTask} from "@/views/dataManagement/components";
import {getSiteDeviceTreeList} from "@/api/deviceCenter/deviceList";
import {findNodeAddRecordListByPage} from "@/api/dataManagement/nodeAddRecording";

export default {
  name: "nodeAddRecording",
  components: {CreateNodeTask},
  props:{
    contentMaxHeight:{
      type:Number,
      default: 520
    }
  },
  setup(props) {

    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/crontab/nodeAddRecord/saveNodeAddRecord')
    })

    const that = reactive({
      Search,
      CirclePlus,
      oldFormInline: {},
      formInline: {keyType: 1},
      keyTypeArray: [{id: 1, name: "名称"}, {id: 2, name: "编码"}],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 320,

      activeRecordId: "",  //记录id
      handleMenuArray: [],
      computeNodeVisible: false,
    })

    // 获取站点设备树形结构
    const querySiteDeviceTreeList = () => {
      getSiteDeviceTreeList({type: 0, timer: new Date()}).then(res => {
        let handleMenuArray = res.data ? res.data : [];
        handleMenuArray.forEach(element => {
          if (element.type === 1) {
            element.iconName = "icon-zhandian";
          }

          if (element.type !== 1) {
            let typeDetailText = "--";
            if (element.typeDetail === 1) typeDetailText = "直连";
            if (element.typeDetail === 2) typeDetailText = "网关";
            if (element.typeDetail === 3) typeDetailText = "子设备";
            element.name = `<span class="textTwo flex-all">${ element.name }</span><span class='typeDetailClass'>${ typeDetailText }</span>`;
          }
        });
        that.handleMenuArray = setTreeData(handleMenuArray);
        // that.allHandleMenuArray = handleMenuArray;
      })
    }

    const handleMenuEvent = (menuButDate) => {
      // console.log(menuButDate);
      if (menuButDate.menuType === "clickTreeNode") {
        that.activeRecordId = menuButDate.id;
        listArray('refresh');
      }
    }

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findNodeAddRecordListByPage({...that.formInline, deviceId: that.activeRecordId, page: that.currentPage, size: that.pageNum}).then(res => {
        that.listLoading = false;
        that.list = res.data.items;
        that.totalNumber = res.data.totalSize;
        if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return
        that.totalNumber = 0;
        that.list = [];
      });
    }

    const clickAddBut = () => {
      that.computeNodeVisible = true;
    }

    const clickResetForm = () => {
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
      listArray("resetPage");
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight - 110;
    }

    onMounted(() => {
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
      querySiteDeviceTreeList();
    })

    return {...toRefs(that), isAddButtonClick, clickResetForm, handleMenuEvent, querySiteDeviceTreeList, listArray, clickAddBut, headerFormRef, setTableMaxHeight}

  }
}
</script>

<style lang="scss" scoped>
.taskStatus1 {
  color: #FD393A;
}

.taskStatus2 {
  color: #56E540;
}
</style>
