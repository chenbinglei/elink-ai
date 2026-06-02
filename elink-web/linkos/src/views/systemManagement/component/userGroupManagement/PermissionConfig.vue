<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listDialogLoading" :title="titleName" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <HandleMenus :isShowHeader="false" :handleMenuArray="handleMenuArray" @handleMenuEvent="handleMenuEvent"/>
        <div class="dialog-right">
          <el-table :data="list" :max-height="tableMaxHeight" v-loading="listLoading" row-key="id" :default-sort="{ prop: 'directoryDesc', order: 'descending' }">
            <el-table-column align="center" width="100px"></el-table-column>
            <el-table-column align="center" label="权限名称" prop="permissionName" show-overflow-tooltip></el-table-column>
            <el-table-column align="center" label="类型">
              <template #default="{ row }">{{ row.permissionType === 1 ? '页面' : '控件' }}</template>
            </el-table-column>
            <el-table-column width="150">
              <template #header>
                <el-checkbox v-model="isSelectAll" @change="selectAllChangeFun"><span>操作</span></el-checkbox>
              </template>
              <template #default="{ row }">
                <el-checkbox v-model="row.operate" :true-label="1" :false-label="2" @change="selectChildrenChangeFun(row)"></el-checkbox>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {setTreeData, treeToArray} from "@/utils";
import {Delete, Plus} from "@element-plus/icons-vue";
import {queryProductList} from "@/api/configCenter/authorityManagement";
import {findTenantApplyEmpowerInfoById} from "@/api/tenantManagement/tenantTabulation";
import {getCurrentInstance, nextTick, onMounted, reactive, toRefs, watch, defineComponent} from "vue";
import {findGroupApplyEmpowerInfoById, saveGroupApplyEmpowerInfo} from "@/api/systemManagement/userGroupManagement";

export default defineComponent({
  name: "PermissionConfig",
  components:{Plus,Delete},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "权限配置"
    },
    groupId: {
      type: [String,Number],
      default: ""
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      dialog_visible: props.isVisible,

      productId: "", // 当前选中的 模块 id
      handleMenuArray: [],
      allHandleMenuArray: [],

      list: [],
      allList: [],
      listLoading: false,
      tableMaxHeight: 480,
      isSelectAll: false, // 是否全选
      listDialogLoading: false,
    })

    // 产品 模块列表
    const listArray = (keyWords = "") => {
      queryProductList({ keyWords: keyWords }).then(res => {
        let handleMenuArray = res.data ? res.data : [];

        handleMenuArray.forEach(val => {
          val.name = val.productName;
          val.iconName = "icon-moxingguanli";
        });

        that.allHandleMenuArray = handleMenuArray;
        that.handleMenuArray = setTreeData(handleMenuArray);

        if (!that.handleMenuArray.length) {
          that.productId = "";
          that.list = [];
        }
      });
    }

    const handleMenuEvent = (menuButDate) => {
      //左侧菜单打开
      if (menuButDate.menuType === "clickTreeNode") {
        that.productId = menuButDate.id;
        getTenantApplyEmpowerInfo();
      }
    }

    // 查询指定租户指定模块下配置的应用授权数据（某个模块   操作为开启的  数据）
    const getTenantApplyEmpowerInfo = () => {
      that.listLoading = true;
      findTenantApplyEmpowerInfoById({ moduleId: that.productId,timer: new Date() }).then(res => {
        let list = res.data && res.data.length ? res.data : [];
        that.allList = JSON.parse(JSON.stringify(list));
        getGroupApplyEmpowerInfo();
      }).catch(() => {
        that.allList = [];
      });
    }

    // 查询指定用户组指定模块下配置的应用授权数据
    const getGroupApplyEmpowerInfo = () => {
      findGroupApplyEmpowerInfoById({ groupId: props.groupId,moduleId: that.productId,timer: new Date() }).then(res => {
        let allList = JSON.parse(JSON.stringify(that.allList));
        let list = res.data && res.data.length ? res.data : [];

        for(let i = 0; i < allList.length; i++){
          let findItem = list.find(item => item.id === allList[i].id);
          allList[i].operate = findItem && findItem.operate ? findItem.operate : 2;
        }

        that.list = setTreeData(allList);
        that.listLoading = false;
        setSelectAllCheckFun();
      }).catch(() => {
        that.listLoading = false;
        that.list = [];
      });
    }

    // 全选 反选
    const selectAllChangeFun = ()=>{
      let list = JSON.parse(JSON.stringify(that.allList));
      for(let i = 0;i < list.length;i++){
        list[i].operate = that.isSelectAll ? 1 : 2;
      }
      that.list = setTreeData(list);
    }

    const selectChildrenChangeFun = (row)=>{
      let operate = row.operate;
      let selectableArray = JSON.parse(JSON.stringify(treeToArray(that.list))); // 页面所有平级权限
      let activeAllChildren = JSON.parse(JSON.stringify(treeToArray(row.children ? row.children : []))); // 获取当前操作的所有子级

      let activeAllParentArray = returnActiveAllParentIds(row.id,operate); // 当前操作的 所有父级
      // console.log(activeAllParentArray);

      nextTick(()=>{
        activeAllChildren = [...activeAllChildren,...activeAllParentArray];
        // 设置当前操作的所有子级(包含父级)
        activeAllChildren.forEach(item =>{
          let findIndex = selectableArray.findIndex(itam => itam.id === item.id);
          selectableArray[findIndex].operate = operate;
        });

        that.list = setTreeData(selectableArray);
        setSelectAllCheckFun();
      })
    }

    // 返回当前操作的 所有相关父级
    const returnActiveAllParentIds = (activeId,activeOperate)=>{
      let activeAllParentIds = [];
      let selectableArray = JSON.parse(JSON.stringify(treeToArray(that.list))); // 页面所有平级权限

      let findItem = selectableArray.find(item => item.id === activeId); // 当前操作的
      if(findItem){
        let findParentIndex = selectableArray.findIndex(item => item.id === findItem.parentId); // 当前操作的 父级

        if(findParentIndex !== -1 && selectableArray[findParentIndex].operate !== activeOperate){

          // 如果为勾选，则当前所有父级进行勾选
          if(activeOperate === 1){
            activeAllParentIds.push(selectableArray[findParentIndex]);
            activeAllParentIds = [...activeAllParentIds,...returnActiveAllParentIds(selectableArray[findParentIndex].id,activeOperate)];
          } else {
            // 设置取消勾选的话 (需要先查看同级是否有勾选的，才可操作)
            let visAvis = true; // 默认可设置
            // 查找当前父级下的 所有子级
            for(let i = 0; i < selectableArray.length;i++){
              if(selectableArray[i].parentId === selectableArray[findParentIndex].id){
                if(selectableArray[i].operate !== activeOperate){
                  visAvis = false;
                  break
                }
              }
            }

            // console.log(visAvis);

            if(visAvis){
              activeAllParentIds.push(selectableArray[findParentIndex]);
              activeAllParentIds = [...activeAllParentIds,...returnActiveAllParentIds(selectableArray[findParentIndex].id,activeOperate)];
            }

          }
        }
      }

      return activeAllParentIds
    }

    // 设置全选 是否勾选
    const setSelectAllCheckFun = ()=>{
      let groupApplyEmpowerVos = [],permissionIds = [];
      let selectableArray = JSON.parse(JSON.stringify(treeToArray(that.list))); // 页面所有平级权限
      // console.log(selectableArray);

      selectableArray.forEach(item =>{
        if(item.operate === 1){
          groupApplyEmpowerVos.push({ groupId: props.groupId, moduleId: item.moduleId, permissionId: item.id, operate: item.operate });
        } else {
          permissionIds.push(item.id);
        }
      })

      that.isSelectAll = groupApplyEmpowerVos.length === selectableArray.length;

      return {groupApplyEmpowerVos, permissionIds}
    }

    const saveDialog = () => {
      that.listDialogLoading = true;
      let dataInfo = setSelectAllCheckFun();
      // 保存用户组应用授权信息

      let groupApplyEmpowerVos = "";
      if(dataInfo.groupApplyEmpowerVos && dataInfo.groupApplyEmpowerVos.length){
        groupApplyEmpowerVos = JSON.parse(JSON.stringify(dataInfo.groupApplyEmpowerVos));
      }

      // 保存用户组应用授权信息
      saveGroupApplyEmpowerInfo({ groupApplyEmpowerVos: groupApplyEmpowerVos }).then(()=>{
        emit("changeEvent");
        that.dialog_visible = false;
        ElMessage({ type: "success", showClose: true, message: "操作成功！" });
        // // 删除指定用户组下关联的指定权限数据
        // deleteGroupApplyEmpowerByGroupId({ groupId: props.groupId, permissionIds: dataInfo.permissionIds.join(',') }).then(()=>{
        //   emit("changeEvent");
        //   that.dialog_visible = false;
        //   ElMessage({ type: "success", showClose: true, message: "操作成功！" });
        // }).catch(()=>{
        //   that.listDialogLoading = false;
        // })
      }).catch(()=>{
        that.listDialogLoading = false;
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      listArray();
    })

    return {...toRefs(that),watchVisible,watchDialogVisible, listArray, handleMenuEvent, getGroupApplyEmpowerInfo, saveDialog, selectAllChangeFun,
      selectChildrenChangeFun,setSelectAllCheckFun,returnActiveAllParentIds, getTenantApplyEmpowerInfo}
  }
})
</script>

<style scoped lang="scss">
.dialog-main {
  display: flex;
  border: 1px solid #EFEFEF;

  .dialog-right{
    flex: 1;
  }
}
</style>

