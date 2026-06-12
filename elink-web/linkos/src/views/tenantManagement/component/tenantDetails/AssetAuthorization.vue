<template>
  <div class="content_body">
    <HandleMenus v-if="sourceType === 2" :isShowHeader="false" :handleMenuArray="handleMenuArray" @handleMenuEvent="handleMenuEvent"/>

    <div class="content_table_right" v-resize="setTableMaxHeight" v-loading="listLoading">
      <div class="tableCenter">
        <el-table :data="list" :max-height="tableMaxHeight">
          <el-table-column align="center" label="站点名称">
            <template #default="{ row }">{{ $filters.moreData(row.siteName) }}</template>
          </el-table-column>
          <el-table-column align="center" label="权限">
            <template #default="{ row }">
              <el-radio-group v-model="row.authority" @change="authorityChangeFun(row)">
                <template v-for="(item,index) in authorityArray" :key="index">
                  <el-radio :disabled="!isEditCompany || !isOperateStatus" :label="item.id">{{ item.name }}</el-radio>
                </template>
              </el-radio-group>
            </template>
          </el-table-column>
          <template v-if="isEditCompany">
            <el-table-column align="center" label="操作" width="150">
              <template #default="{ row }">
                <template v-if="isOperateStatus">
                  <el-link type="danger" :underline="false" @click="infoClick(row)">删除</el-link>
                </template>
              </template>
            </el-table-column>
          </template>
        </el-table>
      </div>
      <div class="tablePagination" ref="tablePaginationRef">
        <Pagination v-model:pageSize="pageNum" :totalNumber="totalNumber" v-model:currentPage="currentPage" @pageChange="listArray"/>
      </div>
    </div>

    <!--    新增站点-->
    <AssociatedSitesDialog v-if="associatedSitesVisible" v-model:isVisible="associatedSitesVisible" :tenantId="tenantId" :organId="organId" :sourceType="sourceType" @changeEvent="listArray('resetPage')" />
  </div>
</template>

<script>
import { useAppStore } from '@/stores/index';

import {setTreeData, treeToArray} from "@/utils";
import {ElMessage, ElMessageBox} from "element-plus";
import AssociatedSitesDialog from "./AssetAuthorization/AssociatedSitesDialog.vue";
import {computed, onMounted, reactive, toRefs, defineComponent, getCurrentInstance, watch} from "vue";
import {deleteEmpowerInfoById, findEmpowerListByPage, updateEmpowerAuthorityById, findTenantDetailsById, findOrganStructureByTenantId} from "@/api/tenantManagement/tenantTabulation";

export default defineComponent({
  name: "AssetAuthorization",
  components: {AssociatedSitesDialog },
  props: {
    tenantId: {
      type: [String, Number],
      default: ""
    },
    isEditCompany:{
      type: Boolean,
      default: false
    },
    // 1： 租户管理详情  2： 企业信息
    sourceType:{
      type: Number,
      default: 1
    }
  },
  setup(props){

    const appStore = useAppStore();
    const {emit} = getCurrentInstance();
    const contentMainMaxHeight = computed(() => {
      return appStore.contentMainMaxHeight;
    });

    const that = reactive({
      list: [],
      pageNum: 20,
      currentPage: 1,
      totalNumber: 0,
      listLoading: false,
      tableMaxHeight: 300,

      organId: "", // 组织结构id
      isOperateStatus: true, // 页面是否可操作
      authorityArray: [{ id: 1, name: "只读"},{ id: 2, name: "读写"}],

      handleMenuArray: [],
      allHandleMenuArray: [],
      associatedSitesVisible: false,
    })

    // 根据id查询租户详情信息
    const queryTenantDetailsById = ()=>{
      that.listLoading = true;
      findTenantDetailsById({ id: props.tenantId,timer: new Date() }).then(res=>{
        that.isOperateStatus = true;
        that.organId = res.data.organStructureId;
        emit("changEvent", { type: "AssetAuthorization",isOperateStatus: that.isOperateStatus});
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    // 根据租户id查询租户下组织架构信息
    const queryOrganStructure = () => {
      findOrganStructureByTenantId({ tenantId: props.tenantId,timer: new Date() }).then( res=> {
        let list = treeToArray(res.data ? res.data : []);
        list.forEach(item=>{
          item.name = item.organName;
          item.iconName = !item.parentId ? "icon-company" : "icon-organization";
        })

        that.handleMenuArray = setTreeData(list);
        that.allHandleMenuArray = list;
      }).catch(()=>{
      })
    }

    const handleMenuEvent = (menuButDate) => {
      // console.log(menuButDate);
      //左侧菜单打开
      if (menuButDate.menuType === "clickTreeNode") {
        that.organId = menuButDate.id;
        that.isOperateStatus = !!menuButDate.parentId;
        emit("changEvent", { type: "AssetAuthorization",isOperateStatus: that.isOperateStatus});
      }
    }

    // 根据组织机构id分页查询资产授权列表
    const listArray = (reset) => {
      that.listLoading = true;
      if (reset === "resetPage") that.currentPage = 1;
      findEmpowerListByPage({ page: that.currentPage, size: that.pageNum, organId: that.organId }).then(res => {
        let list = res.data.items;
        list.forEach(item => item.oldAuthority = item.authority);
        that.list = JSON.parse(JSON.stringify(list));
        that.totalNumber = res.data.totalSize;
        that.listLoading = false;
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    // 权限操作
    const authorityChangeFun = (row) => {
      let oldAuthority = row.oldAuthority ? row.authority === 1 ? 2 : 1 : '';
      ElMessageBox.confirm(`确定修改权限吗？`, "提示", {
        dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
        customClass: "elMessageBoxWarning", showClose:false,type: 'warning',
        beforeClose: (action, instance, done)=>{
          if (action === 'confirm') {
            instance.confirmButtonLoading = true;
            updateEmpowerAuthorityById({ authority: row.authority, empowerId: row.id,empowerType: props.sourceType }).then(res => {
              done();
              instance.confirmButtonLoading = false;
            }).catch(() => {
              row.authority = oldAuthority;
              instance.confirmButtonLoading = false;
            });
          } else {
            done();
          }
        }
      }).then(() => {
        listArray();
        ElMessage({ type:"success",showClose: true,message:"修改成功" });
      }).catch(() => {
        row.authority = oldAuthority;
      });
    }

    // 操作
    const infoClick = (row) => {
      ElMessageBox.confirm(`确定删除该授权信息项吗？`, "删除提示", {
        dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消',
        customClass: "elMessageBoxWarning", showClose:false,type: 'warning',
        beforeClose: (action, instance, done)=>{
          if (action === 'confirm') {
            instance.confirmButtonLoading = true;
            instance.confirmButtonText = '正在删除...';
            deleteEmpowerInfoById({ empowerId: row.id,empowerType: props.sourceType }).then(()=>{
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
        ElMessage({ type: "success", showClose: true, message: "删除成功！" });
      }).catch((error) => {
        console.log(error);
      });
    }

    const clickButtonFun = ()=>{
      that.associatedSitesVisible = true;
    }

    // 初始化表格高度
    const setTableMaxHeight = () => {
      that.tableMaxHeight = contentMainMaxHeight.value - 210;
    }

    const watchOrganId = watch(() => that.organId, (newOrganId)=> {
      listArray("resetPage");
    },{ deep:true })

    onMounted(() =>{
      // console.log(props.sourceType);
      props.sourceType === 1 ? queryTenantDetailsById() : queryOrganStructure();
    })

    return { ...toRefs(that), clickButtonFun, infoClick, setTableMaxHeight, listArray, authorityChangeFun, queryTenantDetailsById, queryOrganStructure,
      handleMenuEvent,watchOrganId}
  }
})
</script>

<style lang="scss" scoped>
.content_body{
  height: 100%;
  padding: 12px 0 0 12px;
  box-sizing: border-box;
  display: flex;

  .content_table_right{
    flex: 1;
    width: 2px;
    height: 100%;
    display: flex;
    flex-direction: column;
    padding-left: 12px;
    box-sizing: border-box;
  }
}
</style>
