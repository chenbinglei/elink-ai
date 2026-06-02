<template>
  <div class="deviceTaskRecordListCom">
    <el-table v-loading="listLoading" :data="list" border :max-height="tableMaxHeight">
      <el-table-column fixed="left" label="序号" type="index" width="80"></el-table-column>
      <el-table-column fixed="left" show-overflow-tooltip>
        <template #header>
          <TableFilterPopCom v-model:value="formInline.siteName" fieldName="siteName" placeholder="请输入电站名称"
            tableName="电站名称" @changEvent="findDeviceTaskRecordList" />
        </template>
        <template #default="{ row }">{{ $filters.moreData(row.siteName) }}</template>
      </el-table-column>
      <el-table-column show-overflow-tooltip>
        <template #header>
          <TableFilterPopCom v-model:value="formInline.deviceName" fieldName="deviceName" placeholder="请输入设备名称"
            tableName="设备名称" @changEvent="findDeviceTaskRecordList" />
        </template>
        <template #default="{ row }">{{ $filters.moreData(row.deviceName) }}</template>
      </el-table-column>
      <el-table-column show-overflow-tooltip>
        <template #header>
          <TableFilterPopCom v-model:value="formInline.deviceNumber" fieldName="deviceNumber" placeholder="请输入SN号"
            tableName="SN号" @changEvent="findDeviceTaskRecordList" />
        </template>
        <template #default="{ row }">{{ $filters.moreData(row.deviceNumber) }}</template>
      </el-table-column>
      <el-table-column label="状态">
        <template #default="{ row }">
          <span class="status" :class="'status_' + row.status">{{ $filters.deviceTaskRecordStatus(row.status) }}</span>
          <template v-if="row.status === 3 || row.status === 5">
            <el-tooltip effect="dark" placement="right">
              <span class="iconfont icon-bangzhu" style="margin-left: 4px"></span>
              <template #content>{{ $filters.moreData(row.reason) }}</template>
            </el-tooltip>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="源版本">
        <template #default="{ row }">{{ $filters.moreData(row.sourceVersion) }}</template>
      </el-table-column>
      <el-table-column label="目标版本">
        <template #default="{ row }">{{ $filters.moreData(row.targetVersion) }}</template>
      </el-table-column>
      <el-table-column label="进度" min-width="150">
        <template #default="{ row }">
          <template v-if="!row.status || row.status === 1">
            <el-progress :percentage="row.progress"></el-progress>
          </template>
          <template v-if="row.status === 2">
            <el-progress :percentage="row.progress" striped striped-flow></el-progress>
          </template>
          <template v-if="row.status === 3 || row.status === 5">
            <el-progress :percentage="row.progress" status="exception"></el-progress>
          </template>
          <template v-if="row.status === 4 || row.status === 6">
            <el-progress percentage="100" status="success"></el-progress>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="升级时间" show-overflow-tooltip>
        <template #default="{ row }">{{ $filters.moreData(row.upgradeTime) }}</template>
      </el-table-column>
      <el-table-column label="结束时间" show-overflow-tooltip>
        <template #default="{ row }">{{ $filters.moreData(row.endTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right">
        <!--        <template #header>-->
        <!--          <div class="flex ai-center jc-space-between">-->
        <!--            <span class="label" style="margin-right: 4px">操作</span>-->
        <!--            <el-button :icon="Refresh" type="text" @click="findDeviceTaskRecordList">刷新</el-button>-->
        <!--          </div>-->
        <!--        </template>-->
        <template #default="{ row }">
          <template v-if="row.status !== 2 && row.status !== 4">
            <el-link :underline="false" type="danger" @click="clickOperateButFun(1, row)">删除</el-link>
          </template>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from "element-plus";
import { Refresh, Search } from "@element-plus/icons-vue";
import { defineComponent, onMounted, reactive, toRefs, watch } from "vue";
import TableFilterPopCom from "@/components/FromFilterComponent/TableFilterPopCom.vue";
import { deleteDeviceTaskById, queryDeviceTaskRecordList } from "@/api/deviceCenter/deviceUpgrade";

export default defineComponent({
  name: "DeviceTaskRecordListCom",
  components: { TableFilterPopCom },
  props: {
    taskId: {
      type: String,
      default: ""
    },
    // 实时更新进度条、状态等数据
    deviceUpdateWsList: {
      type: Array,
      default: () => []
    }
  },
  setup (props) {

    const that = reactive({
      Search,
      Refresh,
      formInline: {},
      oldFormInline: {},

      list: [],
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 380,
    })

    const findDeviceTaskRecordList = () => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));

      queryDeviceTaskRecordList({ taskId: props.taskId, ...formInline }).then(res => {

        that.list = res.data ?? [];
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886) return
        that.list = [];
      })
    }

    const clickOperateButFun = (index, row) => {
      if (index === 1) {
        ElMessageBox.confirm(`您确定要删除当前设备任务（<span class="deleteName">${row.deviceName}</span>）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning', closeOnClickModal: false,
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteDeviceTaskById({ id: row.id, type: 2 }).then(() => {
                done();
                instance.confirmButtonLoading = false;
              }).catch(() => {
                instance.confirmButtonText = '确定';
                instance.confirmButtonLoading = false;
              });
            } else {
              done();
            }
          }
        }).then(() => {
          findDeviceTaskRecordList();
          ElMessage({ type: "success", showClose: true, message: "操作成功！" });
        }).catch(() => {
          console.log("取消删除");
        });
      }
    }

    // // 更新进度条数据以及状态
    // const watchDeviceUpdateWsList = watch(()=>props.deviceUpdateWsList,(newDeviceUpdateWsList)=>{
    //   console.log("接受的数据", newDeviceUpdateWsList)
    //    if(newDeviceUpdateWsList && newDeviceUpdateWsList.length){
    //     console.log("that.list", '满足条件')
    //      let findTaskItem = newDeviceUpdateWsList.find(item => item.taskId === props.taskId);
    //      if(findTaskItem && findTaskItem.deviceDataList && findTaskItem.deviceDataList.length){
    //        for(let i = 0;i < that.list.length;i++){
    //          try {
    //            let findItem = findTaskItem.deviceDataList.find(item => item.deviceNumber === that.list[i].deviceNumber);
    //            if(findItem) that.list[i] = Object.assign({},findItem,that.list[i]);
    //          } catch (e) {}
    //        }
    //      }
    //    }
    // },{ deep: true })
    const watchDeviceUpdateWsList = watch(() => props.deviceUpdateWsList, (newDeviceUpdateWsList) => {
      if (newDeviceUpdateWsList && newDeviceUpdateWsList.length) {
        let findTaskItem = newDeviceUpdateWsList.find(item => item.taskId === props.taskId);

        if (findTaskItem && findTaskItem.deviceDataList && findTaskItem.deviceDataList.length) {
          // 创建新数组确保响应式更新
          const newList = that.list.map(item => {
            let findItem = findTaskItem.deviceDataList.find(data => data.deviceNumber === item.deviceNumber);
            if (findItem) {
              // 使用展开运算符创建新对象
              return { ...item, ...findItem };
            }
            return item;
          });

          // 直接赋值新数组
          that.list = newList;
        }
      }
    }, { deep: true });

    // 在 DeviceTaskRecordListCom 的 setup 中
    const watchTaskId = watch(() => props.taskId, (newTaskId) => {
      // findDeviceTaskRecordList();
    });

    onMounted(() => {
      findDeviceTaskRecordList();
    })

    return { ...toRefs(that), findDeviceTaskRecordList, clickOperateButFun, watchDeviceUpdateWsList, watchTaskId }
  }
})
</script>

<style lang="scss" scoped>
.deviceTaskRecordListCom {
  width: 100%;
  padding: 16px 14px;
  background: #E6E6E6FF;
  box-sizing: border-box;

  .status_1 {
    color: #FF6600;
  }

  .status_2,
  .status_4 {
    color: #2b66feff;
  }

  .status_3,
  .status_5 {
    color: #FF696AFF;
  }

  .status_6 {
    color: #00BD55FF;
  }
}
</style>