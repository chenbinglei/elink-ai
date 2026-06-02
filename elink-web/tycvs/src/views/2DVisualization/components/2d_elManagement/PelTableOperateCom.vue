<template>
  <el-popover v-model:visible="popoverVisible" :effect="effect" :width="180" placement="left-start" trigger="click">
    <template #reference>
      <div :class="{ active_class: popoverVisible }" class="click_class flex-jc-ai-center">
        <span class="iconfont icon-shenglvehao"></span>
      </div>
    </template>
    <div class="tableOperateCom">
      <template v-for="(item,index) in list" :key="index">
        <div v-if="item.fieldName" class="content_list" @click.stop="clickItemFun(item.fieldName)">
          <div class="content_list_left">{{ item.name }}</div>
          <div class="content_list_right"></div>
        </div>
        <div v-else class="content_line"></div>
      </template>
    </div>

    <MoveFileDialog v-if="moveFileVisible" v-model:isVisible="moveFileVisible" :activePelId="activePelId" :parentId="parentId" @changeEvent="changeEvent"></MoveFileDialog>
    <AddElementDialog v-if="addElementVisible" v-model:isVisible="addElementVisible" :titleName="titleName" :activePelId="activePelId" :updateType="updateType" @changeEvent="changeEvent"></AddElementDialog>
  </el-popover>
</template>

<script>
import {useRouter} from "vue-router";
import MoveFileDialog from "./MoveFileDialog.vue";
import {ElMessage, ElMessageBox} from "element-plus";
import AddElementDialog from "./AddElementDialog.vue"
import {deletePelByIds} from "@/api/2DVisualization/2d_elManagement";
import {reactive, toRefs, defineComponent, watch, getCurrentInstance} from "vue";

export default defineComponent({
  name: "PelTableOperateCom",
  components: {MoveFileDialog, AddElementDialog},
  props: {
    dataInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {

    const {emit} = getCurrentInstance();

    const that = reactive({
      effect: 'light',
      popoverVisible: false,
      table_info: props.dataInfo,

      list: [],
      parentId: "",
      activePelId: "",
      updateType: 2, // 编辑类型 1-新增 2-正常编辑 3-重命名 4-移动
      moveFileVisible: false,
      titleName: "编辑图模/文件",
      addElementVisible: false,

      file_list: [
        {name: "重命名", fieldName: "rename"},
        {name: "移动", fieldName: "move"},
        {name: "删除", fieldName: "delete"},
      ],
      artwork_list: [
        {name: "编辑", fieldName: "edit"},
        {name: "重命名", fieldName: "rename"},
        {name: "移动", fieldName: "move"},
        {name: "删除", fieldName: "delete"},
      ],
    })

    const clickItemFun = (fieldName) => {

      if (fieldName === 'edit') {
        that.updateType = 2;
        that.titleName = "编辑图模/文件";
        that.activePelId = that.table_info.id;
        that.addElementVisible = true;
      }

      if (fieldName === 'rename') {
        that.updateType = 3;
        that.titleName = "编辑图模/文件";
        that.activePelId = that.table_info.id;
        that.addElementVisible = true;
      }

      if (fieldName === 'move') {
        that.updateType = 4;
        that.activePelId = that.table_info.id;
        that.parentId = that.table_info.parentId;
        that.moveFileVisible = true;
      }

      if (fieldName === 'delete') {
        ElMessageBox.confirm(`确定删除（<span class="deleteName">${that.table_info.name}</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose: false, closeOnClickModal: false,
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deletePelByIds({ ids: [that.table_info.id] }).then(()=>{
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
          emit("changeEvent");
          ElMessage({type: "success", showClose: true, message: "删除成功！"});
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    const changeEvent = (data)=>{
      emit("changeEvent",data);
    }

    const watchDataInfo = watch(() => props.dataInfo, (newDataInfo) => {
      that.table_info = JSON.parse(JSON.stringify(newDataInfo));
      that.list = newDataInfo.type === 1 ? that.file_list : that.artwork_list;
    }, {deep: true, immediate: true})

    return {...toRefs(that), watchDataInfo, clickItemFun, changeEvent}
  }
})
</script>

<style lang="scss" scoped>
.click_class {
  width: 32px;
  height: 32px;
  background: #F1F3FA;
  border-radius: 8px;

  .iconfont {
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
  }

  &:hover {
    color: #007FEB;
    background: rgba(0, 127, 235, .1);
  }
}

.active_class {
  color: #007FEB;
  background: rgba(0, 127, 235, .1);
}

.tableOperateCom {
  width: 100%;

  .content_list {
    height: 35px;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .content_list_left {
      padding-left: 12px;
    }

    .content_list_right {
      padding-right: 10px;
    }

    &:hover {
      cursor: pointer;
      background: var(--el-menu-hover-bg-color);
    }
  }

  .content_line {
    height: 1px;
    background-color: #EAEEF1;
  }
}
</style>
