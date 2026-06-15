<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="720" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main" v-loading="listLoading">
        <div class="content_body_left">
          <div class="content_body_top">
            <el-input v-model="phoneNum" clearable placeholder="请输入手机号码" @keyup.enter="findAppletUserList">
              <template #suffix>
                <div class="search pointer" @click="findAppletUserList">
                  <el-icon><Search /></el-icon>
                </div>
              </template>
            </el-input>
          </div>
          <div class="content_body_bottom">
            <template v-if="appletUserList && appletUserList.length">
              <template v-for="(item,index) in appletUserList" :key="index">
                <div class="user_list_info">
                  <div class="user_list_info_left">
                    <div class="phoneNum">{{ $filters.moreData(item.phoneNum) }}</div>
                    <div class="nickName textTwo">{{ $filters.moreData(item.nickName) }}</div>
                  </div>
                  <template v-if="findCheckedUserListFun(item)">
                    <div class="user_list_info_right pointer" @click="clickOperateBut(1, item)">
                      <el-icon size="21" color="#4B9EFD"><CirclePlusFilled /></el-icon>
                    </div>
                  </template>
                  <template v-else>
                    <span class="selected_class">已选择</span>
                  </template>
                </div>
              </template>
            </template>
            <div v-else class="flex-jc-ai-center null_data"><null-data></null-data></div>
          </div>
        </div>
        <div class="content_body_right">
          <div class="content_body_top">
            <div class="title">
              <span>已选用户（</span>
              <span class="number">{{ checkedUserList.length }}</span>
              <span>人）</span>
            </div>
            <div class="pointer clean_all" @click="clickCleanAllButFun">全部清除</div>
          </div>
          <div class="content_body_bottom">
            <template v-if="checkedUserList && checkedUserList.length">
              <template v-for="(item,index) in checkedUserList" :key="index">
                <div class="user_list_info">
                  <div class="user_list_info_left">
                    <div class="phoneNum">{{ $filters.moreData(item.phoneNum) }}</div>
                    <div class="nickName textTwo">{{ $filters.moreData(item.nickName) }}</div>
                  </div>
                  <div class="user_list_info_right pointer" @click="clickOperateBut(2, item)">
                    <el-icon size="21" color="#FD393A"><RemoveFilled /></el-icon>
                  </div>
                </div>
              </template>
            </template>
            <div v-else class="flex-jc-ai-center null_data"><null-data></null-data></div>
          </div>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {Search, CirclePlusFilled, RemoveFilled} from '@element-plus/icons-vue';
import {queryAppletUserList} from "@/api/operationManagement/CsMiniProgramUsers";
import {manageGroupUserByGroupId, queryAppletUserListByGroupId} from "@/api/operationManagement/CsUserGrouping";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, onMounted} from "vue";

export default defineComponent({
  name:"ManagingUsersDialog",
  components:{Search,CirclePlusFilled,RemoveFilled},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeEditId: {
      type: String,
      default: ""
    }
  },
  setup(props){
    const {emit} = getCurrentInstance();

    const that = reactive({
      phoneNum: "",
      listLoading: false,
      titleName: "管理成员",
      allAppletUserList: [], // 全部用户数据列表
      dialog_visible: props.isVisible,

      appletUserList: [],
      checkedUserList: [],
      listLeftLoading: false,
      listRightLoading: false,
      removeCheckedUserList: [], // 已选择 删除的用户
    });

    const clickConfirmBut = ()=>{
      // console.log(that.checkedUserList);
      // console.log(that.removeCheckedUserList);
      that.listLoading = true;
      let checkedUserList = [];
      let removeCheckedUserList = [];
      for(let i = 0;i < that.checkedUserList.length;i++){
        if (that.checkedUserList[i].operateType) checkedUserList.push(that.checkedUserList[i].id);
      }
      for(let i = 0;i < that.removeCheckedUserList.length;i++) removeCheckedUserList.push(that.removeCheckedUserList[i].id);
      manageGroupUserByGroupId({groupId: props.activeEditId,addUserIds: checkedUserList.join(','),removeUserIds: removeCheckedUserList.join(',') }).then(res=>{
        emit("changeEvent");
        that.dialog_visible = false;
        ElMessage({ type: "success", message: "操作成功", showClose: true });
      }).catch(()=>{
        that.listLoading = false;
      });
    };

    const clickOperateBut = (operateType, data) => {
      if(operateType === 1){
        let findRemoveIndex = that.removeCheckedUserList.findIndex(item => item.id === data.id);
        that.checkedUserList.push({ operateType: findRemoveIndex === -1 ? 1 : 0,...data });
        that.removeCheckedUserList.splice(findRemoveIndex,1);
      }

      if(operateType === 2){
        // 代表当前用户 在系统中属于当前 用户组
        if(!data.operateType){
          let findItem = that.removeCheckedUserList.find(item => item.id === data.id);
          if(!findItem) that.removeCheckedUserList.push(data);
        }
        let findIndex = that.checkedUserList.findIndex(item => item.id === data.id);
        if(findIndex !== -1) that.checkedUserList.splice(findIndex,1);
      }
    };

    // 全部清除
    const clickCleanAllButFun = ()=>{
      for(let i = 0;i < that.checkedUserList.length;i++){
        // 代表当前用户 在系统中属于当前 用户组
        if(!that.checkedUserList[i].operateType){
          let findItem = that.removeCheckedUserList.find(item => item.id === that.checkedUserList[i].id);
          if(!findItem) that.removeCheckedUserList.push(that.checkedUserList[i]);
        }
      }
      that.checkedUserList = [];
    };

    const findAppletUserList = (operateType) => {
      that.listLeftLoading = true;
      // if (operateType === "resetPage" || operateType === "refresh") that.currentPage = 1;
      queryAppletUserList({page: 1, size: 0, phoneNum: that.phoneNum }).then(res => {
        that.listLeftLoading = false;
        that.appletUserList = res.data.items;
        that.allAppletUserList = res.data.items;
        that.totalNumber = res.data.totalSize;
        // if (operateType === "resetPage") ElMessage({type: "success", message: "重置成功", showClose: true});
      }).catch((error) => {
        that.listLeftLoading = false;
        if (error && error.code === 88886) return;
        that.totalNumber = 0;
        that.appletUserList = [];
      });
    };

    // 根据分组id查询关联小程序用户列表
    const findAppletUserListByGroupId = ()=>{
      that.listRightLoading = true;
      queryAppletUserListByGroupId({groupId: props.activeEditId,timer: new Date() }).then(res=>{
        that.checkedUserList = res.data ? res.data : [];
        that.listRightLoading = false;
      }).catch(() => {
        that.listRightLoading = false;
      });
    };


    const findCheckedUserListFun = (data)=>{
      let isChecked = true;
      let findItem = that.checkedUserList.find(item=> item.id === data.id);
      if(findItem) isChecked = false;
      return isChecked;
    };

    const initParamConfigFun = ()=>{
      findAppletUserList();
      findAppletUserListByGroupId();
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, initParamConfigFun, findAppletUserList, findAppletUserListByGroupId,
      findCheckedUserListFun,clickCleanAllButFun,clickOperateBut};
  }
});
</script>

<style scoped lang="scss">
.dialog-main{
  display: flex;
  align-items: center;

  .content_body_left,.content_body_right{
    flex: 1;
    width: 2px;
    flex-basis: auto;
    height: 460px;
    display: flex;
    flex-direction: column;
    box-sizing: border-box;

    .content_body_top{
      min-height: 32px;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .clean_all{
        color: #106ec4;
      }

      .number{
        color: #106ec4;
        font-weight: bold;
      }
    }

    .content_body_bottom{
      flex: 1;
      height: 2px;
      flex-basis: auto;
      overflow-y: auto;

      .user_list_info{
        width: 100%;
        display: flex;
        align-items: center;
        margin-bottom: 10px;
        border-radius: 6px;
        padding: 10px 12px;
        box-sizing: border-box;
        background-color: rgba(7, 156, 235,0.2);

        .user_list_info_left{
          flex: 1;
          width: 2px;
          flex-basis: auto;

          .phoneNum{
            margin-bottom: 10px;
          }
        }

        .user_list_info_right{
          margin-left: 12px;
        }

        .selected_class{
          font-size: 12px;
          color: rgba(255,255,255,.6);
        }

        &:last-child{
          margin-bottom: 0;
        }

        &:hover{
          background-color: rgba(7, 156, 235,0.25);
        }
      }

      .null_data{
        height: 100%;
      }
    }
  }

  .content_body_left{
    padding-right: 12px;
    border-right: 1px solid #106ec499;
  }

  .content_body_right{
    padding-left: 12px;
  }

}
</style>