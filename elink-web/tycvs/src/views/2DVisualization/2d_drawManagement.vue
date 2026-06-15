<template>
  <div class="app-container fd-column">

    <div class="app-container-right">
      <div class="header-form">
        <el-form :model="formInline" inline @submit.prevent="listArray">
          <el-row :gutter="16">
            <el-col :md="8" :sm="12" :xl="6" :xs="24">
              <el-form-item label="关键词：">
                <el-input v-model="formInline.name" clearable placeholder="搜索文件/文件夹名称" />
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

      <div class="tableContent bg_color_class">
        <TableHeaderTitle title="2D可视化">
          <template #content>
            <el-button class="whiteFontButtons" @click="clickAddButFun(2)">
              <span class="iconfont icon-wenjiantianjia"></span>
              <span class="but_text">新建图模</span>
            </el-button>
            <el-button class="whiteFontButtons" @click="clickAddButFun(1)">
              <span class="iconfont icon-wenjiantianjia"></span>
              <span class="but_text">新建文件夹</span>
            </el-button>
            <el-button class="whiteFontButtons" @click="clickAddButFun(4)">
              <span class="iconfont icon-wenjiantianjia"></span>
              <span class="but_text">导入图模</span>
            </el-button>
          </template>
        </TableHeaderTitle>
        <el-table>
          <el-table-column label="名称"></el-table-column>
          <el-table-column label="状态"></el-table-column>
          <el-table-column label="关联"></el-table-column>
          <el-table-column label="类型"></el-table-column>
          <el-table-column label="更新时间"></el-table-column>
          <el-table-column label="操作" width="120px"></el-table-column>
        </el-table>
        <div class="tableCenter" v-loading="listLoading" style="overflow-y: auto">
          <el-tree :data="list" node-key="id" default-expand-all draggable :props="{children: 'children',label: 'name'}" :indent="12"
                   :allow-drop="allowDropFun" :expand-on-click-node="false" @node-drop="handleDrop">
            <template #default="{ data }">
              <span v-if="data.type === 2" class="sort-icon iconfont icon-tuozhuai"></span>
              <div class="custom-tree-node">
                <div class="el-table-column">
                  <span v-if="data.type === 1" class="iconfont icon-zhucaidan"></span>
                  <span v-if="data.type === 2" class="iconfont icon-wenjianshangchuanhou"></span>
                  <div class="fileName textTwo"><span>{{ $filters.moreData(data.name) }}</span></div>
                </div>
                <div class="el-table-column">
                  <template v-if="data.type === 2">
                    <span class="graphStatus" :class="'graphStatus' + data.status">
                      {{ $filters.graphStatus(data.status) }}
                    </span>
                  </template>
                </div>
                <div class="el-table-column">
                  <div v-if="data.type === 2" class="siteName textTwo" @click.stop="clickAddButFun(3,data)">
                    <span v-if="data.siteName">{{ $filters.moreData(data.siteName) }}</span>
                    <span v-else>未关联</span>
                  </div>
                </div>
                <div class="el-table-column">{{ $filters.graphType(data.type) }}</div>
                <div class="el-table-column">{{ $filters.moreData(data.updateTime) }}</div>
                <div class="el-table-column" style="max-width: 120px">
                  <TableOperateCom :dataInfo="data" @changeEvent="listArray"></TableOperateCom>
                </div>
              </div>
            </template>
          </el-tree>
        </div>
      </div>
    </div>

    <FileAssociationSiteDialog v-if="fileAssociationSiteVisible" v-model:isVisible="fileAssociationSiteVisible" :activeEditInfo="activeEditInfo" @changeEvent="listArray" />
    <AddFileAndFolderDialog v-if="addFileAndFolderVisible" v-model:isVisible="addFileAndFolderVisible" :titleName="titleName" @changeEvent="listArray" />
    <uploadFileDialog v-if="uploadFileVisible" v-model:isVisible="uploadFileVisible" :titleName="titleName" @changeEvent="listArray" />
  </div>
</template>

<script lang="ts">
import {useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import {Search} from "@element-plus/icons-vue";
import {reactive, toRefs, onMounted} from "vue";
import {queryUserAuthorityIsHaveFun, setTreeData} from "@/utils";
import {queryGraphList, saveGraph} from "@/api/2DVisualization/2d_drawManagement";
import {AddFileAndFolderDialog, TableOperateCom, FileAssociationSiteDialog, uploadFileDialog} from "@/views/2DVisualization/components";

export default {
  name: "2d_drawManagement",
  components: {AddFileAndFolderDialog, TableOperateCom, FileAssociationSiteDialog,uploadFileDialog},
  setup() {
    const vueRouter = useRouter();
    const that = reactive({
      Search,
      formInline: {},
      oldFormInline: {},

      list: [],
      activeEditInfo: {},
      listLoading: false,
      tableMaxHeight: 300,
      titleName: "新建文件",
      addFileAndFolderVisible: false,
      fileAssociationSiteVisible: false,
      uploadFileVisible: false,
    })

    // 查询图元管理列表
    const listArray = () => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));
      queryGraphList({...formInline}).then(res => {
        let list = res.data ? res.data : [];
        that.list = setTreeData(list);
        that.listLoading = false;
      }).catch((err) => {
        that.listLoading = false;
        if (err && err.code === 88886) return
        that.list = [];
      });
    }

    const clickAddButFun = (operateType,data) => {

      if (operateType === 1) {
        that.titleName = "新建文件夹";
        that.addFileAndFolderVisible = true;
      }

      if (operateType === 2) {
        const routeName = "/2DVisualization/2d_artworkEditor";
        const isAuthority = queryUserAuthorityIsHaveFun(routeName);
        if (!isAuthority) {
          ElMessage({type: "warning", showClose: true, message: "请联系管理员打开对应权限！"});
          return
        }

        // 新建或编辑图模数据
        saveGraph({type: 2, name: "新建图纸", updateType: 1}).then(res => {
          vueRouter.push({
            query: {id: res.data},
            path: "/2DVisualization/2d_artworkEditor",
          });
        })
      }

      if (operateType === 3) {
        that.activeEditInfo = JSON.parse(JSON.stringify(data));
        that.fileAssociationSiteVisible = true;
      }
      if (operateType === 4) {
        that.titleName = "导入图模";
        that.uploadFileVisible = true;
        console.log("导入图模");
      }

    }

    // 拖拽成功完成时触发的事件
    const handleDrop = (dropNode,innerNode)=>{
      that.listLoading = true;
      saveGraph({ id: dropNode.data.id,parentId: innerNode.data.id,updateType: 2 }).then(()=>{
        that.listLoading = false;
        ElMessage({ type: "success", message: "操作成功", showClose: true });
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const allowDropFun = (draggingNode, dropNode)=>{
      return dropNode.data?.type === 1
    }

    onMounted(() => {
      listArray();
    })

    return {...toRefs(that), clickAddButFun, listArray, handleDrop, allowDropFun}
  }
}
</script>

<style lang="scss" scoped>

.but_text{
  margin-left: 4px;
}

:deep(.el-tree){
  --el-tree-node-content-height: 48px;

  .el-tree-node__content{
    position: relative;

    .sort-icon{
      display: none;
      position: absolute;
      left: 4px;
    }

    &:hover{
      .sort-icon{
        cursor: move;
        display: block !important;
      }
    }
  }

  .custom-tree-node{
    flex: 1;
    width: 2px;
    display: flex;

    .el-table-column{
      flex: 1;
      display: flex;
      align-items: center;

      .fileName{
        flex: 1;
        width: 2px;
        padding-bottom: 1px;
        box-sizing: border-box;
      }

      .siteName{
        color: #1F74E2;
        cursor: pointer;
      }
    }

    .icon-zhucaidan{
      color: #F9A230;
      margin-right: 5px;
    }

    .icon-wenjianshangchuanhou{
      color: #1F74E2;
      margin-right: 5px;
    }

    .graphStatus1{
      color: #FF2626;
    }

    .graphStatus2{
      color: #049735;
    }
  }
}

:deep(.el-table__body-wrapper){
  display: none;
}
</style>