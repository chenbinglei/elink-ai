<template>
  <div class="content_body" v-resize="setTableMaxHeight">
    <HandleMenus :isShowHeader="false" :handleMenuArray="handleMenuArray" @handleMenuEvent="handleMenuEvent" />
    <div class="content_table_right" ref="tableCenterRef">
      <el-table :data="list" :max-height="tableMaxHeight" v-loading="listLoading" row-key="id"
        :default-sort="{ prop: 'directoryDesc', order: 'descending' }">
        <el-table-column align="center" width="100px"></el-table-column>
        <el-table-column align="center" label="权限名称" prop="permissionName" show-overflow-tooltip></el-table-column>
        <el-table-column align="center" label="类型" prop="permissionType">
          <template #default="{ row }">{{ row.permissionType === 1 ? '页面' : '控件' }}</template>
        </el-table-column>
        <el-table-column width="150">
          <template #header>
            <el-checkbox v-model="isSelectAll" :disabled="!isEditCompany" @change="selectAllChangeFun">全选</el-checkbox>
          </template>
          <template #default="{ row }">
            <el-checkbox v-model="row.operate" :true-label="1" :false-label="2" :disabled="!isEditCompany"
              @change="selectChildrenChangeFun(row)"></el-checkbox>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { useStore } from "vuex";
import { setTreeData, treeToArray } from "@/utils";
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, nextTick, onMounted, reactive, ref, toRefs, defineComponent } from "vue";
import { findPermissionByModuleId, queryProductList } from "@/api/configCenter/authorityManagement";
import { findTenantApplyEmpowerInfoById, saveTenantApplyEmpowerInfo } from "@/api/tenantManagement/tenantTabulation";

export default defineComponent({
  name: "ApplicationAuth",
  props: {
    tenantId: {
      type: [String, Number],
      default: ""
    },
    isEditCompany: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const store = useStore();

    const userInfo = computed(() => {
      return store.state.app.userInfo;
    });

    const contentMainMaxHeight = computed(() => {
      return store.state.app.contentMainMaxHeight;
    });

    const that = reactive({
      tenant_id: props.tenantId,
      list: [],
      allList: [], // 权限列表
      listLoading: false,
      tableMaxHeight: 300,
      handleMenuArray: [],
      allHandleMenuArray: [],
      addOrganizeVisible: false,
      productId: "", // 当前选中的 产品id
      isSelectAll: false, // 是否全选
    })

    // 产品 模块列表
    const listArray = () => {
      queryProductList({ timer: new Date(), pageName: "Application" }).then(res => {
        let handleMenuArray = res.data ? res.data : [];
        handleMenuArray.forEach(val => {
          val.name = val.productName;
          val.iconName = "icon-moxingguanli";
        });
        that.handleMenuArray = res.data;
        if (!that.handleMenuArray.length) {
          that.productId = "";
          that.list = [];
        }
      });
    }

    const handleMenuEvent = (menuButDate) => {
      // console.log(menuButDate);
      //左侧菜单打开
      if (menuButDate.menuType === "clickTreeNode") {
        that.productId = menuButDate.id;
        userInfo.value.userRole === 0 ? getPermissionArray() : getTenantApplyEmpowerInfo();
      }
    }

    // 根据模块id查询权限数据 (查所有的权限)
    const getPermissionArray = () => {
      that.listLoading = true;
      findPermissionByModuleId({ moduleId: that.productId, timer: new Date() }).then(res => {
        that.allList = res.data && res.data.length ? res.data : [];
        getTenantApplyEmpowerInfo();
      }).catch(() => {
        that.list = [];
        that.allList = [];
        getTenantApplyEmpowerInfo();
      });
    }

    // 查询指定租户指定模块下配置的应用授权数据（某个模块   操作为开启的  数据）
    const getTenantApplyEmpowerInfo = () => {
      findTenantApplyEmpowerInfoById({ tenantId: that.tenant_id, moduleId: that.productId, timer: new Date() }).then(res => {

        let allList = [];
        let resData = res.data && res.data.length ? res.data : [];
        allList = JSON.parse(JSON.stringify(resData));
        if (userInfo.value.userRole === 0) {
          for (let i = 0; i < resData.length; i++) {
            let findIndex = allList.findIndex(item => item.id === resData[i].id);
            if (findIndex !== -1) allList[findIndex].operate = resData[i].operate;
          }
        }

        // 数据库索引排序
        allList = allList.sort((a, b) => {
          return a.directoryDesc > b.directoryDesc ? -1 : a.directoryDesc < b.directoryDesc ? 1 : 0;
        });
        that.list = setTreeData(allList);
        that.listLoading = false;
        that.allList = allList;
        setIsSelectAllFun();
      }).catch(() => {
        that.listLoading = false;
      })
    }

    // 设置全选是否选中
    const setIsSelectAllFun = () => {
      let newApplyEmpowerVos = [];
      let newList = treeToArray(that.list);
      newList.forEach(item => {
        if (item.operate === 1) {
          newApplyEmpowerVos.push({ tenantId: that.tenant_id, moduleId: item.moduleId, permissionId: item.id, operate: item.operate })
        }
      })
      that.isSelectAll = newApplyEmpowerVos.length === newList.length;
      return newApplyEmpowerVos
    }

    // 按钮操作
    const clickButtonFun = (type) => {
      if (type === "allSelect" || type === "allDelete") {
        that.isSelectAll = !that.isSelectAll;
        selectAllChangeFun();
      }

      if (type === "allSave") {
        ElMessageBox.confirm(`确定保存设置吗？`, "提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "elMessageBoxWarning", showClose: false, type: 'warning',
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在保存...';
              let tenantApplyEmpowerVos = setIsSelectAllFun();
              saveTenantApplyEmpowerInfo({ tenantApplyEmpowerVos: tenantApplyEmpowerVos }).then(() => {
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
          that.listLoading = true;
          getTenantApplyEmpowerInfo();
          ElMessage({ type: "success", showClose: true, message: "设置成功！" });
        }).catch(() => {
          console.log("取消保存");
        });
      }
    }

    // 全选 反选
    const selectAllChangeFun = () => {
      let list = JSON.parse(JSON.stringify(that.allList));
      for (let i = 0; i < list.length; i++) {
        list[i].operate = that.isSelectAll ? 1 : 2;
      }
      that.list = setTreeData(list);
      setIsSelectAllFun();
    }

    const selectChildrenChangeFun = (row) => {
      let operate = row.operate;
      let selectableArray = JSON.parse(JSON.stringify(treeToArray(that.list))); // 页面所有平级权限
      let activeAllChildren = JSON.parse(JSON.stringify(treeToArray(row.children ? row.children : []))); // 获取当前操作的所有子级

      let activeAllParentArray = returnActiveAllParentIds(row.id, operate); // 当前操作的 所有父级
      // console.log(activeAllParentArray);

      nextTick(() => {
        activeAllChildren = [...activeAllChildren, ...activeAllParentArray];
        // 设置当前操作的所有子级(包含父级)
        activeAllChildren.forEach(item => {
          let findIndex = selectableArray.findIndex(itam => itam.id === item.id);
          selectableArray[findIndex].operate = operate;
        });

        that.list = setTreeData(selectableArray);
        setIsSelectAllFun();
      })
    }

    // 返回当前操作的 所有相关父级
    const returnActiveAllParentIds = (activeId, activeOperate) => {
      let activeAllParentIds = [];
      let selectableArray = JSON.parse(JSON.stringify(treeToArray(that.list))); // 页面所有平级权限

      let findItem = selectableArray.find(item => item.id === activeId); // 当前操作的
      if (findItem) {
        let findParentIndex = selectableArray.findIndex(item => item.id === findItem.parentId); // 当前操作的 父级

        if (findParentIndex !== -1 && selectableArray[findParentIndex].operate !== activeOperate) {

          // 如果为勾选，则当前所有父级进行勾选
          if (activeOperate === 1) {
            activeAllParentIds.push(selectableArray[findParentIndex]);
            activeAllParentIds = [...activeAllParentIds, ...returnActiveAllParentIds(selectableArray[findParentIndex].id, activeOperate)];
          } else {
            // 设置取消勾选的话 (需要先查看同级是否有勾选的，才可操作)
            let visAvis = true; // 默认可设置
            // 查找当前父级下的 所有子级
            for (let i = 0; i < selectableArray.length; i++) {
              if (selectableArray[i].parentId === selectableArray[findParentIndex].id) {
                if (selectableArray[i].operate !== activeOperate) {
                  visAvis = false;
                  break
                }
              }
            }

            // console.log(visAvis);

            if (visAvis) {
              activeAllParentIds.push(selectableArray[findParentIndex]);
              activeAllParentIds = [...activeAllParentIds, ...returnActiveAllParentIds(selectableArray[findParentIndex].id, activeOperate)];
            }

          }
        }
      }

      return activeAllParentIds
    }

    // 初始化表格高度
    const tableCenterRef = ref(null);
    const setTableMaxHeight = () => {
      that.tableMaxHeight = contentMainMaxHeight.value - 120;
    }

    onMounted(() => {
      listArray();
    })

    return {
      ...toRefs(that), clickButtonFun, handleMenuEvent, setTableMaxHeight, getTenantApplyEmpowerInfo, getPermissionArray, selectAllChangeFun, tableCenterRef,
      selectChildrenChangeFun, returnActiveAllParentIds, setIsSelectAllFun, contentMainMaxHeight
    }
  }
})
</script>

<style scoped lang="scss">
.content_body {
  height: 100%;
  padding: 16px 12px 0 12px;
  box-sizing: border-box;
  display: flex;

  .content_table_right {
    flex: 1;
    width: 2px;
    height: 100%;
    display: flex;
    flex-direction: column;

    .tableContent {
      padding: 0 !important;
    }
  }
}
</style>
