<template>

</template>

<script>
import CreateNodeTask from "./CreateNodeTask";
import {reactive, toRefs, onMounted, ref, watch} from "vue";
import {Search, CirclePlus} from '@element-plus/icons-vue';
import {findNodeAddRecordListByPage} from "@/api/dataManagement/nodeAddRecording";

export default {
  name: "DataAddRecordingTable",
  components: {CreateNodeTask},
  props:{
    activeRecordId:{
      type: [Number,String],
      default:""
    }
  },
  setup(props) {

    const that = reactive({
      Search,
      CirclePlus,
      formInline: {},
      keyTypeArray: [{id: 1, name: "名称"}, {id: 2, name: "编码"}],

      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,
      computeNodeVisible: false,
    })

    const listArray = (operateType) => {
      that.listLoading = true;
      if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      findNodeAddRecordListByPage({ ...that.formInline,deviceId:props.activeRecordId,page: that.currentPage, size: that.pageNum }).then(res => {
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

    const resetForm = () => {
      that.formInline = {};
      listArray("resetPage");
    }

    // 初始化表格高度
    const tableCenterRef = ref(null);
    const setTableMaxHeight = () => {
      that.tableMaxHeight = tableCenterRef.value.offsetHeight;
    }

    const watchActiveRecordId = watch(()=>props.activeRecordId,(newActiveRecordId)=>{
      listArray('refresh');
    })

    return {...toRefs(that), watchActiveRecordId, tableCenterRef, setTableMaxHeight, clickAddBut, listArray, resetForm}

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
