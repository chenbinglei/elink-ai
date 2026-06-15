<template>
  <div class="app-container fd-column">

    <div class="app-container-right">
      <div class="header-form">
        <el-form :model="formInline" inline class="flex-all" @submit.prevent="listArray">
          <el-row :gutter="16">
            <el-col :md="8" :sm="12" :xl="6" :xs="24">
              <el-form-item label="关键词：">
                <el-input v-model="formInline.name" clearable placeholder="搜索文件/图元名称"/>
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
        <TableHeaderTitle title="图元管理">
          <template #content>
            <el-button class="whiteFontButtons" @click="clickAddButFun(2)">
              <span class="iconfont icon-wenjiantianjia"></span>
              <span class="but_text">新建图元</span>
            </el-button>
            <el-button class="whiteFontButtons" @click="clickAddButFun(1)">
              <span class="iconfont icon-wenjiantianjia"></span>
              <span class="but_text">新建文件夹</span>
            </el-button>
          </template>
        </TableHeaderTitle>
        <el-table>
          <el-table-column label="图元名称">
            <template #default="{ row }">
              <span v-if="row.type === 1" class="iconfont icon-zhucaidan"></span>
              <!--                <span v-if="row.type === 2" class="iconfont icon-wenjianshangchuanhou"></span>-->
              <span class="name">{{ $filters.moreData(row.name) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="缩略图">
            <template #default="{ row }">
              <div class="image_class flex-jc-ai-center" v-if="row.filePath">
                <img class="img_class" :src="row.filePath" alt="" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="类型">
            <template #default="{ row }">
              <template v-if="row.type === 2">{{ $filters.pelType(row.pelType) }}</template>
              <span v-else>{{ $filters.pelFileType(row.type) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="更新时间">
            <template #default="{ row }">{{ $filters.moreData(row.updateTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120px">
            <template #default="{ row }">
              <PelTableOperateCom :dataInfo="row" @changeEvent="listArray"></PelTableOperateCom>
            </template>
          </el-table-column>
        </el-table>

        <div class="tableCenter" v-loading="listLoading" style="overflow-y: auto">
          <el-tree :data="list" node-key="id" default-expand-all draggable :props="{children: 'children',label: 'name'}" :allow-drop="allowDropFun"
                   :expand-on-click-node="false" @node-drop="handleDrop">
            <template #default="{ data }">
              <span v-if="data.type === 2" class="sort-icon iconfont icon-tuozhuai"></span>
              <div class="custom-tree-node">
                <div class="el-table-column">
                  <span v-if="data.type === 1" class="iconfont icon-zhucaidan"></span>
                  <div class="fileName textTwo">{{ $filters.moreData(data.name) }}</div>
                </div>
                <div class="el-table-column">
                  <div class="image_class flex-jc-ai-center" v-if="data.filePath">
                    <img class="img_class" :src="data.filePath" alt="" />
                  </div>
                </div>
                <div class="el-table-column">{{ $filters.graphType(data.type) }}</div>
                <div class="el-table-column">{{ $filters.moreData(data.updateTime) }}</div>
                <div class="el-table-column" style="max-width: 120px">
                  <PelTableOperateCom :dataInfo="data" @changeEvent="listArray"></PelTableOperateCom>
                </div>
              </div>
            </template>
          </el-tree>
        </div>
      </div>
    </div>

    <AddElementDialog v-if="addElementVisible" v-model:isVisible="addElementVisible" :titleName="titleName" :operateType="operateType" @changeEvent="listArray" />
  </div>
</template>

<script lang="ts">
import {setTreeData} from "@/utils";
import {ElMessage} from "element-plus";
import {Search} from "@element-plus/icons-vue";
import {reactive, toRefs, onMounted} from "vue";
import {queryPelList, saveGraphPel} from "@/api/2DVisualization/2d_elManagement";
import {AddElementDialog, PelTableOperateCom} from "@/views/2DVisualization/components";

export default {
  name: "2d_elManagement",
  components:{PelTableOperateCom, AddElementDialog},
  setup() {
    const that = reactive({
      Search,
      formInline: {},
      oldFormInline: {},

      list: [],
      operateType: 1,
      listLoading: false,
      tableMaxHeight: 300,
      titleName: "新建图元/文件",
      addElementVisible: false,
    })

    // 查询图元管理列表
    const listArray = () => {
      that.listLoading = true;
      let formInline = JSON.parse(JSON.stringify(that.formInline));

      queryPelList({ ...formInline }).then(res => {
        let list = res.data ? res.data : [];
        that.list = setTreeData(list);
        that.listLoading = false;
      }).catch((err) => {
        that.listLoading = false;
        if (err && err.code === 88886) return
        that.list = [];
      });
    }

    const clickAddButFun = (operateType)=>{
      that.operateType = operateType;
      that.titleName = "新建图元/文件";
      that.addElementVisible = true;
    }

    // 拖拽成功完成时触发的事件
    const handleDrop = (dropNode,innerNode)=>{
      that.listLoading = true;
      saveGraphPel({ id: dropNode.data.id,parentId: innerNode.data.id,updateType: 4 }).then(()=>{
        that.listLoading = false;
        ElMessage({ type: "success", message: "操作成功", showClose: true });
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    const allowDropFun = (draggingNode, dropNode)=>{
      return dropNode.data?.type === 1
    }

    onMounted(()=>{
      listArray();
    })

    return {...toRefs(that), allowDropFun, handleDrop, clickAddButFun, listArray}
  }
}
</script>

<style lang="scss" scoped>
.but_text{
  margin-left: 4px;
}

:deep(.el-tree){
  --el-tree-node-content-height: 58px;

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
      }

      .image_class{
        width: 60px;
        height: 60px;
        padding: 1px;
        box-sizing: border-box;

        .img_class{
          max-width: 58px;
          max-height: 58px;
        }
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
  }
}

:deep(.el-table__body-wrapper){
  display: none;
}
</style>