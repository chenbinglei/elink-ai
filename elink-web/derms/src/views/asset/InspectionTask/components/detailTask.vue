<template>
  <div class="containers-detail">
    <div id="map-container" class="map-container" v-loading="maploading">
      <div class="map-table" ref="tableContentRef" v-resize="setTableMaxHeight">
        <el-table :data="detailsList" border stripe :max-height="tableMaxHeight">
          <el-table-column label="场站名称" prop="siteName" align="center"></el-table-column>
          <el-table-column label="巡检结果" prop="status" align="center">
            <template #default="scope">
              <span v-if="scope.row.status === 1">未分配</span>
              <span v-if="scope.row.status === 2">未开始</span>
              <span v-if="scope.row.status === 3">巡检中</span>
              <span v-if="scope.row.status === 4">待验收</span>
              <span v-if="scope.row.status === 5">完结</span>
            </template>
          </el-table-column>
          <el-table-column label="结束时间" prop="finishTime" align="center">
            <template #default="{ row }">{{ $filters.moreData(row.finishTime) }}</template>
          </el-table-column>
          <el-table-column label="异常数量" prop="exceptionNum" align="center">
            <template #default="{ row }">
              <span class="err-num">{{ $filters.moreData(row.exceptionNum) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <div class="map-from" v-if="titleValue !== '详情'">
      <el-form ref="formRef" :model="formData" :rules="rules">

        <el-form-item label="验收操作：" prop="operationType" v-if="taskStatus === 4" style="float: left;" @change="changeOperationType">
          <el-radio-group v-model="formData.operationType">
            <el-radio label="1">确认完成</el-radio>
            <el-radio label="7">退回</el-radio>
            <el-radio label="2">交接 </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="流转操作：" prop="operationType" v-if="taskStatus === 1" style="float: left;" @change="changeOperationType">
          <el-radio-group v-model="formData.operationType">
            <el-radio label="1">提交</el-radio>
            <el-radio label="0">交接 </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="" prop="operationUserId" v-if="formData.operationType!=='7'">
          <el-select style="width: 200px;padding-left: 15px;" v-model="formData.operationUserId" placeholder="请选择用户" size="small">
            <el-option v-for="item in userList" :key="item.userId" :label="item.userName" :value="item.userId"></el-option>
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="24">
            <el-form-item label="验收意见：" prop="operationOpinion" v-if="taskStatus === 4&&formData.operationType!=='7'">
              <el-input v-model="formData.operationOpinion" placeholder="请输入验收意见" type="textarea"></el-input>
            </el-form-item>
            <el-form-item label="流转意见：" prop="operationOpinion" v-if="taskStatus === 1&&formData.operationType!=='7'">
              <el-input v-model="formData.operationOpinion" placeholder="请输入流转意见" type="textarea"></el-input>
            </el-form-item>
            <el-form-item label="退回意见：" prop="operationOpinion" v-if="formData.operationType==='7'">
              <el-input v-model="formData.operationOpinion" placeholder="请输入退回意见" type="textarea"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div class="btn-group">
        <el-button type="primary" @click="submitForm">提交</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </div>
  </div>
</template>


<script setup>
import AMapLoader from '@amap/amap-jsapi-loader';
import { ref, watch, onMounted, nextTick, getCurrentInstance } from "vue";
import { findInspectionUserList, updateInspectionTask } from '@/api/assetManagement/inspection'
const props = defineProps({
  taskStatus: {
    type: String,
    default: ''
  },
  detailsList: {
    type: Array,
    default: () => []
  },
  inspectionArray: {
    type: Object,
    default: () => {}
  },
  titleValue: {
    type: String,
    default: ''
  }
})
const { emit } = getCurrentInstance();
// 高德地图实例存储
const mapInstances = ref(new Map())
const taskStatus = ref(props.taskStatus)
const detailsList = ref(props.detailsList)
const maploading = ref(false)
console.log(props.detailsList, 'props.taskStatus999')
const userList = ref([])
const formData = ref({
  operationType: '1',
  operationUserId: "",
  operationOpinion: ""


})
const formRef = ref(null)
const rules = ref({
  operationType: [{ required: true, message: '请选择操作类型', trigger: ['blur'] }],
  operationOpinion: [{ required: true, message: '请输入意见', trigger: ['blur'] }],
  operationUserId: [{ required: true, message: '请选择交接用户', trigger: ['change'] }]
})

const tableMaxHeight = ref(300);
// 计算出表格最大高度
const tableContentRef = ref(null);
const setTableMaxHeight = () => {
  let tableContentHeight = tableContentRef.value.offsetHeight;
  tableMaxHeight.value = tableContentHeight;
};
const initMap = () => {
  maploading.value = true
  AMapLoader.load({
    key: '78acf8560d11d6ee253952bc7206bf53', // 替换为你的Key
    version: '2.0',
    plugins: ['AMap.Marker', 'AMap.InfoWindow']
  }).then((AMap) => {
    const coords = detailsList.value.map(item => {
      const lon = Number(item.longitude);
      const lat = Number(item.latitude);
      return [lon, lat];
    });
    const map = new AMap.Map('map-container', {
      zoom: 8,
      center: coords[0] // 改为浙江中心
    });

    coords.forEach(coord => {
      new AMap.Marker({
        position: coord,
        map: map
      });
    });
    // new AMap.Marker({
    //   // position: [120.14, 30.24],
    //   map: map
    // });
    maploading.value = false
  }).catch((e) => {
    console.error('地图加载失败:', e);
  });
};
const changeOperationType = () => {
  console.log(formData.value.operationType, 'val')
  getfindInspectionUserList()
}
// const destroyMap = (id) => {
//   const map = mapInstances.value.get(id);
//   if (map) {
//     map.clearMap();
//     map.destroy();
//     mapInstances.value.delete(id);
//   }
// };
const submitForm = () => {


  formData.value.operationType = props.taskStatus === 4 ? '6' : formData.value.operationType
  formRef.value.validate((valid) => {
    if (valid) {

      emit('submitForm', formData.value)
      console.log(formData.value, 'val.operationType')

    } else {
      console.log('表单校验不通过');
    }
  });
};
const cancel = () => {
  emit('cancel')
};
const getfindInspectionUserList = () => {
  console.log(formData.value.operationType, props.taskStatus, 'formData.value.operationType')
  // 1待分配的情况 下 交接的时候是0 提交的时候是1
  // 验收的情况 交接3 确认完成3  退回2 
  let typeValue = '';
  if (props.taskStatus === 1) {
    typeValue = formData.value.operationType === '0' ? '0' : '1'
  } else if (props.taskStatus === 4) {
    typeValue = formData.value.operationType === '1' ? '2' : '3'
  }
  let obj = {
    type: typeValue
  }
  findInspectionUserList(obj).then((res) => {
    userList.value = res.data
  });
};
onMounted(() => {
  initMap();

  if (props.taskStatus === 1 || props.taskStatus === 2 || props.taskStatus === 3 || props.taskStatus === 4) {
    getfindInspectionUserList()
  }

});
</script>

<style lang="scss" scoped>
.containers-detail {
  width: 100%;
  // height: 550px;
  padding: 18px;
  box-sizing: border-box;
}

.map-table {
  position: absolute;
  // height: 50%;
  width: 50%;
  z-index: 9;
  opacity: 0.8;
  right: 3%;
  top: 2%;
  overflow-y: auto;
}

.map-container {
  width: 100%;
  height: 40vh;
  position: relative;
}

.err-num {
  color: rgba(59, 170, 245, 1);
}

.map-from {
  border: 1px solid rgba(0, 0, 0, 0.1);
  padding: 12px 0;
  margin-top: 10px;
}

.btn-group {
  text-align: center;
  padding: 0 20px;
}
</style>
