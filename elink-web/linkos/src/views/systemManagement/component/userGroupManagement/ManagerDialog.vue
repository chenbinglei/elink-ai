<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading"
          :title="titleName" width="680px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">

        <HandleMenus ref="handleMenusRef" :isShowHeader="false" :handleMenuArray="handleMenuArray" showCheckbox
                     :isBottomBut="false" defaultExpandAll @handleMenuEvent="handleMenuEvent"/>

        <div class="dialog_right">
          <div class="tag_top">
            <div class="">已选：{{ tagArr.length }}人</div>
            <div class="clear pointer" @click="clearTagsFun">全部清除</div>
          </div>
          <div class="tag_btm">
            <template v-for="(item,index) in tagArr" :key="index">
              <el-tag closable @close="tagCloseFun(item)">
                {{ item.name }}
              </el-tag>
            </template>
          </div>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {setTreeData} from "@/utils";
import {ElMessage} from "element-plus";
import {Delete, Plus} from "@element-plus/icons-vue";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch} from "vue";
import {
  findOrganStructureListByTenantId, findUserListByTenantId,
  groupManageUser
} from "@/api/systemManagement/userGroupManagement";

export default {
  name: "ManagerDialog",
  components:{Plus,Delete},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "管理人员"
    },
    groupId: {
      type: [String,Number],
      default: ""
    },
    // 用户数组
    userDtoList: {
      type: Array,
      default: []
    },
    formDialog: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();
    const handleMenusRef = ref(null);
    const that = reactive({
      dialog_visible: props.isVisible,
      listLoading: false,
      handleMenuArray: [],
      allHandleMenuArray: [],
      resData: {},
      tagArr: [],
      oldTagArr: [],
      addUserIds: [], // 新增用户id
      deleteUserIds: [], // 移除用户id
      checkKeysArr: [],
      userList: [], // 用户列表
      organList: [], // 组织架构列表
      otherNumber: 0,
      first: true
    })

    // 根据租户id查询组织架构信息列表
    const getOrganStructureList = () => {
      findOrganStructureListByTenantId({}).then( res=> {
        let resData = res.data;
        resData.forEach(item => {
          item.name = item.organName ? item.organName : "";
        })
        that.organList = JSON.parse(JSON.stringify(resData));
      }).catch((err)=>{
        console.log(err)
      })
    }

    // 根据租户id查询用户列表
    const getUserList = () => {
      findUserListByTenantId({}).then( res=> {
        let resData = res.data;
        resData.forEach(item => {
          item.parentId = item.organId ? item.organId : "";
          item.name = item.fullName ? item.fullName : "";

        })
        that.userList = JSON.parse(JSON.stringify(resData));
      }).catch((err)=>{
        console.log(err)
      })
    }

    // 处理左侧组织架构用户信息
    const getOrganStructure = () => {
      getOrganStructureList();
      getUserList();

      setTimeout(() => {
        let resData = that.organList.concat(that.userList),checkKeysArr = [];

        resData.forEach(item => {
          if(!item.parentId && item.fullName){
            item.parentId = "other";
            that.otherNumber += 1;
          }
          if(!item.parentId)item.parentId = "alllist";

          // 只展示 userRole为2 的 userRole（0：平台管理员  1：管理员  2：普通用户）
          // let findIndex = resData.findIndex(item => item.userRole !== 2);
          // if(findIndex !== -1)resData.splice(findIndex,1);
        })
        that.resData = JSON.parse(JSON.stringify(resData));
        for(let i = 0; i < props.userDtoList.length; i++){
          checkKeysArr.push(props.userDtoList[i].id);
        }

        // 树形选中id数组
        that.checkKeysArr = checkKeysArr;

        let organTreeData = setOrganTreeFun(that.resData, true);
        setChildrenNumberFun(organTreeData)
        that.handleMenuArray = JSON.parse(JSON.stringify(organTreeData));
        that.allHandleMenuArray = that.resData;
      },200)
    }

    // 设置组织架构人员树形结构
    const setOrganTreeFun = (data = [], pushAlllist = false) => {
      let alllist = [
        {id: "alllist", name: "全部", parentId: 0, number: data.length ,children: []},
        {id: "other", name: "其他", parentId: 0, number: that.otherNumber ,children: []}
      ];
      let allCarList = [...data];
      if (pushAlllist) allCarList = [...data, ...alllist];
      // console.log(setTreeData(allCarList))
      return setTreeData(allCarList);
    }

    // 递归设置  转变字段,名称拼接根部数量
    const setChildrenNumberFun = (data) => {
      if (data && data.length) {
        data.forEach(item => {
          // 拼接显示组织根部数量
          if(item.children && item.children.length){
            item.name = item.name + "(" + item.children.length + ")";
          }

          if(item.children)setChildrenNumberFun(item.children);
        })
      }
    };

    const handleMenuEvent = (menuButDate) => {
      // console.log(menuButDate);
      //左侧菜单打开
      if (menuButDate.type === "clickTreeNode") {
        let tagArr = [];
        for(let i = 0; i < that.resData.length; i++){
          menuButDate.allSelectIdArray.forEach(item => {
            if(item === that.resData[i].id && that.resData[i].fullName){
              tagArr.push({ name: that.resData[i].fullName,id: that.resData[i].id });
            }
          })
        }
        that.tagArr = JSON.parse(JSON.stringify(tagArr));
        if(that.first){
          that.oldTagArr = JSON.parse(JSON.stringify(tagArr));
          that.first = false;
        }
      }

      if(menuButDate.type === "initHandleMenu"){
        handleMenusRef.value.setCheckedKeysFun(that.checkKeysArr); // 设置选中
      }
    }

    const saveDialog = () => {
      that.listLoading = true;
      // 处理新加用户ID
      that.tagArr.forEach(item => {
        let findIndex = that.oldTagArr.findIndex(itam => itam.id === item.id);
        if(findIndex === -1)that.addUserIds.push(item.id);
      })

      // 处理左侧删除用户ID
      that.oldTagArr.forEach(item => {
        let findIndex = that.tagArr.findIndex(itam => itam.id === item.id);
        if(findIndex === -1)that.deleteUserIds.push(item.id);
      })

      let data = {
        addUserIds: that.addUserIds.toString(),
        deleteUserIds: that.deleteUserIds.toString(),
        groupId: props.groupId
      }
      groupManageUser(data).then(()=>{
        emit("changeEvent");
        that.dialog_visible = false;
        that.listLoading = false;
        ElMessage({ type: "success", showClose: true, message: "操作成功！" });
      }).catch(()=>{
        that.listLoading = false;
      })
    }

    // 删除标签
    const tagCloseFun = (data) => {
      let checkKeysArr = [];
      let finIndex = that.tagArr.findIndex(item => item.id === data.id);
      if(finIndex !== -1)that.tagArr.splice(finIndex,1);
      // 删除的id数组
      if(JSON.stringify(that.deleteUserIds).indexOf(data.id) === -1)that.deleteUserIds.push(data.id);
      // 剩余的标签id数组
      that.tagArr.forEach(item => {
        checkKeysArr.push(item.id);
      })
      handleMenusRef.value.setCheckedKeysFun(checkKeysArr);
    }

    // 全部清除标签
    const clearTagsFun = () => {
      that.tagArr = [];
      handleMenusRef.value.setCheckedKeysFun();
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      getOrganStructure();
    })

    return { ...toRefs(that),watchVisible,watchDialogVisible,saveDialog,getOrganStructureList,getUserList,
      handleMenuEvent, setOrganTreeFun, tagCloseFun, clearTagsFun, handleMenusRef
    }
  }
}
</script>

<style scoped lang="scss">
.dialog-main {
  width: 100%;
  height: 50vh;
  display: flex;
  padding: 0 24px;
  box-sizing: border-box;
  border: 1px solid #EFEFEF;

  .dialog_right {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 16px;
    box-sizing: border-box;

    .tag_top {
      display: flex;
      justify-content: space-between;

      .clear{
        color: #1F74E2;
      }
    }

    .tag_btm {
      flex: 1;
      padding-top: 16px;
      box-sizing: border-box;
      overflow-y: auto;

      .el-tag {
        width: 100%;
        display: flex;
        justify-content: space-between;
        margin-bottom: 12px;
        font-size: 16px;

        &:last-child {
          margin-bottom: 0;
        }

        .el-tag__content {
          flex: 1;
        }
      }
    }
  }
}
</style>
