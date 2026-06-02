<template>
  <el-popover v-model:visible="popoverVisible" :effect="effect" :width="180" placement="left-start" trigger="click">
    <template #reference>
      <div :class="{ active_class: popoverVisible }" class="click_class flex-jc-ai-center">
        <span class="iconfont icon-shenglvehao"></span>
      </div>
    </template>
    <div class="tableOperateCom">
      <template v-for="(item,index) in list" :key="index">
        <template v-if="item.fieldName">
          <div :class="{ notAllowed: (table_info.lockStatus && (item.fieldName === 'open' || item.fieldName === 'rename' || item.fieldName === 'move' || item.fieldName === 'delete')) ||
               ((table_info.status !== 1 || !table_info.domainId) && item.fieldName === 'publish') ||
               (!table_info.domainId && item.fieldName === 'preview') }" class="content_list" @click.stop="clickItemFun(item.fieldName)">
            <div class="content_list_left">{{ item.name }}</div>
            <div class="content_list_right">
              <template v-if="item.fieldName === 'lock'">
                <el-switch v-model="table_info.lockStatus" :active-value="1" :inactive-value="0" size="small" @change="lockChangeFun"/>
              </template>
            </div>
          </div>
        </template>
        <div v-else class="content_line"></div>
      </template>
    </div>

    <MoveFileDialog v-if="moveFileVisible" v-model:isVisible="moveFileVisible" :activeGraphId="activeGraphId" :parentId="parentId" @changeEvent="changeEvent"></MoveFileDialog>
    <AddFileAndFolderDialog v-if="addFileAndFolderVisible" v-model:isVisible="addFileAndFolderVisible" :activeEditInfo="activeEditInfo" :titleName="titleName" @changeEvent="changeEvent"></AddFileAndFolderDialog>
  </el-popover>
</template>

<script>
import {useRouter} from "vue-router";
import {readOSSFile} from "@/common/readOSSFile";
import MoveFileDialog from "./MoveFileDialog.vue";
import {ElMessage, ElMessageBox, ElLoading} from "element-plus";
import AddFileAndFolderDialog from "./AddFileAndFolderDialog.vue";
import {getFileNameFromPath, queryUserAuthorityIsHaveFun} from "@/utils";
import {reactive, toRefs, defineComponent, watch, getCurrentInstance} from "vue";
import {cloneGraph, deleteGraphByIds, saveGraph} from "@/api/2DVisualization/2d_drawManagement";

export default defineComponent({
  name: "TableOperateCom",
  components: {AddFileAndFolderDialog, MoveFileDialog},
  props: {
    dataInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {

    const vueRouter = useRouter();
    const {emit} = getCurrentInstance();

    const that = reactive({
      effect: 'light',
      popoverVisible: false,
      table_info: props.dataInfo,

      list: [],
      parentId: "",
      activeGraphId: "",
      activeEditInfo: {},
      moveFileVisible: false,
      titleName: "编辑图模/文件",
      addFileAndFolderVisible: false,

      file_list: [
        {name: "重命名", fieldName: "rename"},
        {name: "移动", fieldName: "move"},
        {name: "删除", fieldName: "delete"},
      ],
      artwork_list: [
        {name: "打开", fieldName: "open"},
        {name: "预览", fieldName: "preview"},
        {name: "发布", fieldName: "publish"},
        {},
        {name: "锁定", fieldName: "lock"},
        {},
        {name: "重命名", fieldName: "rename"},
        {name: "移动", fieldName: "move"},
        {name: "创建副本", fieldName: "createCopy"},
        {},
        {name: "删除", fieldName: "delete"},
      ],
    })

    const clickItemFun = (fieldName) => {

      if (that.table_info.lockStatus) {
        if (fieldName === 'open' || fieldName === 'rename' || fieldName === 'move' || fieldName === 'delete') {
          return;
        }
      }

      // 只有有变化的图纸才能进行发布操作
      if ((that.table_info.status !== 1 || !that.table_info.domainId) && fieldName === 'publish') return;

      // 预览必须要有 域名id
      if (!that.table_info.domainId && fieldName === 'preview') return;

      if (fieldName === 'open') openCanvasMeta2dFun({fieldName: fieldName,id: that.table_info.id });

      // 克隆图模
      if (fieldName === 'createCopy') {
        const loading = ElLoading.service({lock: true, text: '正在复制...', background: 'rgba(0, 0, 0, 0.7)'});
        const fileName = getFileNameFromPath(that.table_info.filePath);
        readOSSFile({ fileName: fileName }).then(result_file =>{
          let formData = new FormData();
          let meta2dBlob = new Blob([result_file.data], {type: 'application/json'});
          let meta2dFile = new File([meta2dBlob], `${ that.table_info.name }_副本.json`, {type: 'application/json'});
          formData.append("id", that.table_info.id);
          formData.append("file", meta2dFile);
          cloneGraph(formData).then(res=>{
            ElMessage({type: 'success', showClose: true, message: '复制成功！'});
            openCanvasMeta2dFun({fieldName: fieldName,id: res.data });
            emit("changeEvent");
            loading.close();
          }).catch(()=>{
            loading.close();
          })
        }).catch(()=>{
          loading.close();
        })
      }

      if (fieldName === "preview") {
        vueRouter.push({path: `/canvasPreview/${that.table_info.domainId}`});
      }

      if (fieldName === 'rename') {
        that.activeEditInfo = {
          updateType: 3,
          id: that.table_info.id,
          type: that.table_info.type,
          name: that.table_info.name,
        };

        that.titleName = that.table_info.type === 1 ? "编辑文件名称" : "编辑图模名称";
        that.addFileAndFolderVisible = true;
      }

      if (fieldName === 'move') {
        that.activeGraphId = that.table_info.id;
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
              deleteGraphByIds({ids: [that.table_info.id]}).then(() => {
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

      if (fieldName === 'publish') {
        ElMessageBox.confirm(`确定发布该图纸（<span class="highlightText">${that.table_info.name}</span>）吗？`, "提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose: false, closeOnClickModal: false,
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              saveGraph({id: that.table_info.id,domainId: that.table_info.domainId,updateType: 6}).then(() => {
                done();
                instance.confirmButtonLoading = false;
              }).catch(() => {
                instance.confirmButtonLoading = false;
              });
            } else {
              done();
            }
          }
        }).then(() => {
          emit("changeEvent");
          ElMessage({type: "success", showClose: true, message: "操作成功！"});
        }).catch(() => {
          console.log("取消！");
        });
      }
    }


    // 打开画布
    const openCanvasMeta2dFun = (data)=>{
      const routeName = "/2DVisualization/2d_artworkEditor";
      const isAuthority = queryUserAuthorityIsHaveFun(routeName);
      if (!isAuthority) {
        ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
        return
      }

      vueRouter.push({query: {id: data.id}, path: "/2DVisualization/2d_artworkEditor"});
    }

    // 锁定
    const lockChangeFun = () => {
      let titleText = that.table_info.lockStatus ? '锁定' : '解锁';
      ElMessageBox.confirm(`确定${titleText}（<span class="deleteName">${that.table_info.name}</span>）吗？`, "提示", {
        dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
        customClass: "deleteMsgBoxClass", showClose: false, closeOnClickModal: false,
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true;
            saveGraph({id: that.table_info.id, lockStatus: that.table_info.lockStatus, updateType: 4}).then(() => {
              done();
              instance.confirmButtonLoading = false;
            }).catch(() => {
              instance.confirmButtonLoading = false;
              that.table_info.lockStatus = that.table_info.lockStatus ? 0 : 1;
            });
          } else {
            done();
          }
        }
      }).then(() => {
        emit("changeEvent");
        ElMessage({type: "success", showClose: true, message: "操作成功！"});
      }).catch(() => {
        console.log("取消！");
        that.table_info.lockStatus = that.table_info.lockStatus ? 0 : 1;
      });
    }

    const changeEvent = (data) => {
      emit("changeEvent", data);
    }

    const watchDataInfo = watch(() => props.dataInfo, (newDataInfo) => {
      that.table_info = JSON.parse(JSON.stringify(newDataInfo));
      that.list = newDataInfo.type === 2 ? that.artwork_list : that.file_list;
    }, {deep: true, immediate: true})

    return {...toRefs(that), watchDataInfo, lockChangeFun, clickItemFun, changeEvent, openCanvasMeta2dFun}
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

  .notAllowed {
    color: #D8D8D8;
  }

  .content_line {
    height: 1px;
    background-color: #EAEEF1;
  }
}
</style>