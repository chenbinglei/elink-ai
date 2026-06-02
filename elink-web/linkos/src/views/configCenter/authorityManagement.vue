<template>
  <div class="app-container">
    <HandleMenus title="产品列表" inputText="请输入模块名称" showAddButton :handleMenuArray="handleMenuArray" @handleMenuEvent="handleMenuEvent"/>
    <div class="app-container-right" v-resize="setTableMaxHeight">

      <div class="pageHeaderInfo" ref="headerFormRef">
        <div class="pageHeader_top">
          <div class="pageHeader_top_left">
            <span class="menuName">{{ $filters.moreData(productName) }}</span>
          </div>
          <div class="pageHeader_top_right" v-if="handleMenuArray.length">
            <el-button class="whiteFontButtons" @click="clickCompile('openDialog')">编辑产品</el-button>
            <el-button class="blackFontButtons" @click="clickDelete">删除产品</el-button>
            <el-button class="whiteFontButtons" :disabled="isAddButtonClick" :icon="CirclePlus" @click="addAuthority">新增权限</el-button>
          </div>
        </div>
      </div>

      <div class="tableContent" ref="tableCenterRef">
         <el-table :data="list" :max-height="tableMaxHeight" v-loading="listLoading" row-key="id">
            <el-table-column align="center" width="100px"></el-table-column>
            <el-table-column align="center" label="权限名称" show-overflow-tooltip>
              <template #default="{ row }">
                <div class="displayFlex">
                  <div class="iconImage">
                    <span class="yemian iconfont icon-quanxianpeizhi-yemian-01" v-if="row.permissionType === 1"></span>
                    <span class="kongjian iconfont icon-quanxianpeizhi-kongjian-01" v-else></span>
                  </div>
                  <div class="textTwo">{{ $filters.moreData(row.permissionName) }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" label="权限编码">
              <template #default="{ row }">{{ $filters.moreData(row.permissionCode)}}</template>
            </el-table-column>
            <el-table-column align="center" label="权限类型">
              <template #default="{ row }">{{ row.permissionType === 1 ? '页面' : '控件' }}</template>
            </el-table-column>
            <el-table-column align="center" label="URL" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.url)}}</template>
            </el-table-column>
            <el-table-column align="center" label="目录序号">
              <template #default="{ row }">{{ $filters.moreData(row.directoryDesc)}}</template>
            </el-table-column>
            <el-table-column align="center" label="图标" show-overflow-tooltip>
              <template #default="{ row }">{{ $filters.moreData(row.iconPath)}}</template>
            </el-table-column>
            <el-table-column align="center" label="说明" show-overflow-tooltip>
              <template #default="{ row }"><div class="textTwo">{{ $filters.moreData(row.explanation) }}</div></template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="150px">
              <template #default="{ row }">
                <el-link :underline="false" @click="infoClick(1, row)">编辑</el-link>
                <span style="margin:0 8px">|</span>
                <el-link type="danger" :underline="false" @click="infoClick(2, row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
      </div>
    </div>

    <!--    添加编辑产品-->
    <AlterFormDialog v-if="dialogVisible" v-model:isVisible="dialogVisible" :titleName="alterFormDialog?'编辑产品':'新增产品'" :formDialog="formDialog" @listArray="listArray" />

    <!--    新增修改权限-->
    <AuthorityCompileDialog v-if="authorityVisible" v-model:isVisible="authorityVisible" :titleName="authorityCompile?'修改权限':'新增权限'" :authorityCompile="authorityCompile"
                            :authorityFormDialog="authorityFormDialog" :tiledList="tiledList" @changeEvent="queryPermissionByModuleId" />
  </div>
</template>
<script>
import Draggable from 'vuedraggable'
import {ElMessage, ElMessageBox} from "element-plus";
import {CirclePlus, Delete} from '@element-plus/icons-vue';
import {computed, onMounted, reactive, ref, toRefs} from "vue";
import {nullToDelete, operateButtonIsClick, setTreeData} from '@/utils';
import {AlterFormDialog, AuthorityCompileDialog} from "@/views/configCenter/component";
import {queryProductList, deleteModuleById, findPermissionByModuleId, deletePermissionById} from '@/api/configCenter/authorityManagement'

export default {
  name: "authorityManagement",
  components: {Draggable, AlterFormDialog, AuthorityCompileDialog},
  props:{
    contentMaxHeight:{
      type:Number,
      default: 520
    }
  },
  setup(props) {

    const isAddButtonClick = computed(()=>{
      return operateButtonIsClick('/system/configureCenter/saveOrUpdatePermission')
    })


    const that = reactive({
      Delete,
      CirclePlus,

      list: [],
      tiledList: [],
      listLoading: false,
      tableMaxHeight: 300,
      //新增产品
      dialogVisible: false,
      alterFormDialog: false,
      formDialog: {},
      //新增权限
      authorityVisible: false,
      authorityCompile: false,
      authorityFormDialog: {},

      clientId: "", // 当前选中的  产品 clientId
      productId: "", // 当前选中的 产品id
      productName: "",// 当前选中的 产品name
      handleMenuArray: [],
    })

    // 产品列表
    const listArray = () => {
      queryProductList({  }).then(res => {
        let handleMenuArray = res.data ? res.data : [];
        handleMenuArray.forEach(val => {
          val.name = val.productName;
          val.iconName = "icon-moxingguanli";
        });
        that.handleMenuArray = res.data;
        if (!that.handleMenuArray.length) {
          that.productId = "";
          that.productName = "";
          that.list = [];
        }
      });
    }

    // 根据产品id查询权限数据
    const queryPermissionByModuleId = () => {
      that.listLoading = true;
      findPermissionByModuleId({ moduleId: that.productId }).then(res => {
        let list = res.data ? res.data : [];
        list.sort((a, b) => b.directoryDesc - a.directoryDesc);
        that.list = setTreeData(list);
        that.tiledList = JSON.parse(JSON.stringify(list));
        that.listLoading = false;
      }).catch(() => {
        that.list = [];
        that.tiledList = [];
        that.listLoading = false;
      });
    }

    // 表格中详情、删除
    const infoClick = (clickType, row) => {
      for(let key in row)row[key] = nullToDelete(row[key]);
      if (clickType === 1) {
        that.authorityFormDialog = row;
        that.authorityCompile = true;
        that.authorityVisible = true;
      }
      if (clickType === 2) {
        ElMessageBox.confirm(`您确定要删除（${row.permissionName}）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass",closeOnClickModal: false, dangerouslyUseHTMLString: true,
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deletePermissionById({id: row.id}).then(() => {
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
          queryPermissionByModuleId();
          ElMessage({type: "success", showClose: true, message: "删除成功！"});
        }).catch(() => {
          console.log("取消删除");
        });
      }
    }

    const handleMenuEvent = (menuButDate) => {
      // console.log(menuButDate);

      //添加产品
      if (menuButDate.menuType === "clickAddBut") {
        that.formDialog = {};
        that.dialogVisible = true;
        that.alterFormDialog = false;
      }

      //左侧菜单打开
      if (menuButDate.menuType === "clickTreeNode") {
        that.list = [];
        that.productId = menuButDate.id;
        that.productName = menuButDate.name;
        that.clientId = menuButDate.clientId;
        queryPermissionByModuleId();
      }
    }

    // 编辑产品
    const clickCompile = (operateType = "") => {
      if(operateType === "openDialog"){
        that.formDialog = {
          id: that.productId,
          clientId: that.clientId,
          productName: that.productName,
        }
        that.alterFormDialog = true;
        that.dialogVisible = true;
      }
    }

    // 删除产品
    const clickDelete = () => {
      if (that.list.length > 0) {
        ElMessage({type: "error", showClose: true, message: "请先删除该平台下的所有权限"});
        return
      }

      ElMessageBox.confirm(`您确定要删除平台（${that.productName}）吗？`, "删除提示", {
        customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
        cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
        beforeClose: (action, instance, done)=>{
          if (action === 'confirm') {
            instance.confirmButtonLoading = true;
            instance.confirmButtonText = '正在删除...';
            deleteModuleById({id: that.productId}).then(() => {
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
        listArray();
        ElMessage({type: "success", showClose: true, message: "删除成功！"});
      }).catch(() => {
        console.log("取消删除");
      });
    }

    // 新增权限
    const addAuthority = () => {
      if (!that.productId) {
        ElMessage({type: "error", showClose: true, message: "请先添加或选择产品"});
        return
      }

      that.authorityFormDialog = {
        permissionType: 1,
        isHidden: 0,
        isLayout: 1,
        moduleId: that.productId
      };
      that.authorityCompile = false;
      that.authorityVisible = true;
    }

    // 初始化表格高度
    const tableCenterRef = ref(null);
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight;
    }

    onMounted(() => {
      listArray();
    })

    return {...toRefs(that), listArray, setTableMaxHeight, tableCenterRef, queryPermissionByModuleId, infoClick, handleMenuEvent, clickCompile,headerFormRef, clickDelete,
      addAuthority, isAddButtonClick}
  }
}
</script>
<style scoped lang="scss">
.pageHeaderInfo{
  width: 100%;
  padding: 1px 24px 24px 24px;
  box-sizing: border-box;

  .pageHeader_top{
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    justify-content: space-between;

    .pageHeader_top_left {
      display: flex;
      align-items: center;
      margin-bottom: 5px;

      .menuName {
        display: flex;
        align-items: center;
        font-size: 22px;
        font-weight: bold;
        color: #242424;
        white-space: nowrap;
      }
    }

    .pageHeader_top_right{
      display: flex;
    }
  }
}

.displayFlex {
  flex: 1;
  display: flex;
  align-items: center;

  .iconImage{

    .kongjian {
      color: #FF8000;
    }

    .yemian {
      color: #1F74E2;
    }
  }
  .textTwo{
    flex: 1;
    -webkit-line-clamp: 1;
  }
}

.iconfont {
  display: block;

  &::before {
    margin-right: 5px;
  }
}

</style>
