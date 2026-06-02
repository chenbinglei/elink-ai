<template>
  <Dialog v-model:isVisible="dialog_visible" :cancelVisible="false" :title="titleName" append-to-body width="820">
    <template v-slot:content>
      <div class="dialog-main app-container">
        <div class="header-form">
          <el-form :model="formInline" inline @submit.prevent>
            <el-form-item label="关键词：">
              <el-input v-model="formInline.name" placeholder="请输入关键词">
                <template #suffix>
                  <div class="pointer" @click="queryGraphSourceByGraphId">
                    <el-icon><Search/></el-icon>
                  </div>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item style="float: right">
              <el-button :icon="Plus" class="whiteFontButtons" @click="clickAssociateDataBut">关联数据源</el-button>
            </el-form-item>
          </el-form>
        </div>
        <div class="tableContent">
          <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
            <el-table-column label="序号" type="index" width="80"></el-table-column>
            <el-table-column label="名称" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.name) }}</template>
            </el-table-column>
            <el-table-column label="通信方式">
              <template #default="{ row }">{{ $filters.communicationType(row.type) }}</template>
            </el-table-column>
            <el-table-column label="URL" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.url) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <div class="table_operate_class">
                  <el-link :underline="false" @click="clickOperateBut(1, row)">
                    <span class="text">编辑</span>
                  </el-link>
                  <span class="split_line">|</span>
                  <el-link :underline="false" type="danger" @click="clickOperateBut(2, row)">
                    <span class="text">删除</span>
                  </el-link>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <AssociateDataSourcesDialog v-if="associateDataSourcesVisible" v-model:isVisible="associateDataSourcesVisible" :titleName="titleNameText" :activeEditInfo="activeEditInfo" @changeEvent="queryGraphSourceByGraphId" />

      </div>
    </template>
  </Dialog>
</template>

<script>
import {useRoute} from "vue-router";
import {Search, Plus} from '@element-plus/icons-vue';
import {ElMessage,ElMessageBox} from "element-plus";
import AssociateDataSourcesDialog from "./AssociateDataSourcesDialog.vue";
import {reactive, toRefs, watch, getCurrentInstance, defineComponent, onMounted} from 'vue';
import {findGraphSourceByGraphId,relationDataSource} from "@/api/2DVisualization/2d_artworkEditor";

export default defineComponent({
  name: "AddDataSourceDialog",
  components: {Search,AssociateDataSourcesDialog},
  props: {
    isVisible: {
      type: Boolean,
      default: false,
    },
  },
  setup(props) {
    const route = useRoute();
    const {emit} = getCurrentInstance();

    const that = reactive({
      Plus,
      listLoading: false,
      titleName: "数据源",
      activeGraphId: route.query.id, // 当前图模id
      dialog_visible: props.isVisible,

      list: [],
      formInline: {},
      tableMaxHeight: 320,

      activeEditInfo: {},
      titleNameText: "关联数据源",
      associateDataSourcesVisible: false,
    })

    const queryGraphSourceByGraphId = () => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      findGraphSourceByGraphId({ ...formInline,graphId: that.activeGraphId }).then(res => {
        that.list = res.data;
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886)return
        that.list = [];
      })
    }

    const clickAssociateDataBut = ()=>{
      that.titleNameText = "关联数据源";
      that.activeEditInfo = { graphId: that.activeGraphId,updateType: 1 };
      that.associateDataSourcesVisible = true;
    }

    const clickOperateBut = (operateType,row)=>{

      if(operateType === 1){
        that.titleName = "编辑数据源";
        that.activeEditInfo = JSON.parse(JSON.stringify({ updateType: 2,...row }));
        that.associateDataSourcesVisible = true;
      }


      if(operateType === 2){
        ElMessageBox.confirm(`确定删除数据源（<span class="deleteName">${ row.name }</span>）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              relationDataSource({ id: row.id,updateType: 3 }).then(()=>{
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
          queryGraphSourceByGraphId();
          ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    })

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    })

    onMounted(() => {
     queryGraphSourceByGraphId();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, clickAssociateDataBut, clickOperateBut, queryGraphSourceByGraphId}
  }
})

</script>

<style lang="scss" scoped>
.app-container {
  flex-direction: column;
}
</style>