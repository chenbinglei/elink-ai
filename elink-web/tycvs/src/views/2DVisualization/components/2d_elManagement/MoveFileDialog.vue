<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" append-to-body disabledLoading width="580" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <lx-move-file-com ref="lxMoveFileComRef" v-model:activeFileRouteId="activeFileRouteId" :fileMenuArray="fileMenuArray"></lx-move-file-com>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {setTreeData} from "@/utils";
import {ElMessage} from 'element-plus';
import {LxMoveFileCom} from "@/components/LxComponents";
import {queryPelList,saveGraphPel} from "@/api/2DVisualization/2d_elManagement";
import {getCurrentInstance, onMounted, reactive, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: 'moveFileDialog',
  components:{LxMoveFileCom},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activePelId: {
      type: [String,Number],
      default: ""
    },
    parentId: {
      type: [String,Number],
      default: ""
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      updateType: 4, //编辑类型 1-新增 2-正常编辑 3-重命名 4-移动
      fileMenuArray: [],
      listLoading: false,
      titleName: "移动文件到",
      dialog_visible: props.isVisible,
      activeFileRouteId: props.parentId,
    })

    const clickConfirmBut = () => {

      if(!that.activeFileRouteId){
        ElMessage({ type: "error", message: "请选择要移动到的文件夹", showClose: true });
        return
      }

      that.listLoading = true;
      saveGraphPel({id: props.activePelId,parentId: that.activeFileRouteId,updateType: that.updateType}).then(()=>{
        emit("changeEvent");
        that.dialog_visible = false;
        ElMessage({ type: "success", message: "操作成功", showClose: true });
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const initParamConfigFun = () => {
      that.listLoading = true;
      queryPelList({ timer: new Date() }).then(res=>{
        let fileMenuArray = [];
        let list = res.data ? res.data : [];
        list.forEach(item=>{
          if(item.type === 1){
            if(item.id === props.activePelId) item.disabled = true;
            fileMenuArray.push(item);
          }
        });
        // console.log(fileMenuArray)
        that.fileMenuArray = setTreeData(JSON.parse(JSON.stringify(fileMenuArray)));
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch(() => that.dialog_visible, (newDialogVisible) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    })

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut}
  }
})
</script>

<style lang="scss" scoped>
.dialog-main {
  width: 100%;
}
</style>