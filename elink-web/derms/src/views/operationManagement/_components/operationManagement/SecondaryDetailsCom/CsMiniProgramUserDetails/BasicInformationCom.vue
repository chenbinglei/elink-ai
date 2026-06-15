<template>
  <TitleView title="基本信息">
    <template #headerRight>
      <el-button :icon="Edit" @click="clickEditButtonFun">编辑</el-button>
    </template>
    <template #content>
      <div v-loading="listLoading" class="basicInformationCom">
        <el-row :gutter="12" class="content_list">
          <template v-for="(child,i) in list" :key="i">
            <el-col :lg="8" :sm="12" class="info_li">
              <div class="flex_li_left">{{ child.reaName }}：</div>
              <div class="flex_li_right textTwo">
                <span v-if="child.filterName">{{ $filters[child.filterName](returnDataInfo[child.fieldName]) }}</span>
                <span v-else>{{ $filters.moreData(returnDataInfo[child.fieldName]) }}</span>
                <span v-if="child.unit" class="unit">{{ child.unit }}</span>
              </div>
            </el-col>
          </template>
        </el-row>
      </div>
      <AddUserInfoDialog v-if="addUserInfoVisible" v-model:isVisible="addUserInfoVisible" :titleName="titleName" :activeEditDataInfo="returnDataInfo" @changeEvent="findAppletUserDetailById" />
    </template>
  </TitleView>
</template>

<script lang="ts">
import {Edit} from '@element-plus/icons-vue';
import {defineComponent, onMounted, reactive, toRefs} from "vue";
import {queryAppletUserDetailById} from "@/api/operationManagement/CsMiniProgramUserDetails";
import {AddUserInfoDialog} from "@/views/operationManagement/_components/operationManagement/ChargingStationOperation/index";

export default defineComponent({
  name: "BasicInformationCom",
  components: {AddUserInfoDialog},
  props: {
    routeInfo: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  setup(props) {

    const that = reactive({
      Edit,
      listLoading: false,
      returnDataInfo: {},
      list: [
        {reaName: "用户ID", fieldName: "id"},
        {reaName: "用户昵称", fieldName: "nickName"},
        {reaName: "手机号", fieldName: "phoneNum"},
        {reaName: "用户分组", fieldName: "groupName"},
        {reaName: "用户邮箱", fieldName: "mailbox"},
        {reaName: "平台类型", fieldName: "platformType", filterName: "platformType"},
        {reaName: "注册时间", fieldName: "createTime"},
        {reaName: "描述", fieldName: "refer"},
      ],

      titleName: "编辑用户",
      addUserInfoVisible: false,
    });

    // 根据小程序用户id查询用户详情数据
    const findAppletUserDetailById = () => {
      that.listLoading = true;
      queryAppletUserDetailById({timer: new Date(), ...props.routeInfo}).then(res => {
        that.returnDataInfo = res.data ? res.data : {};
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      });
    };

    const clickEditButtonFun = ()=>{
      that.addUserInfoVisible = true;
    };

    onMounted(()=>{
      findAppletUserDetailById();
    });

    return {...toRefs(that), findAppletUserDetailById, clickEditButtonFun};
  }
});
</script>

<style lang="scss" scoped>
.info_li {
  display: flex;
  align-items: center;
  margin-bottom: 24px;

  .flex_li_left {
    color: #D3ECFB;
    font-size: 14px;
    white-space: nowrap;
  }

  .flex_li_right {
    color: #D3ECFB;
    font-size: 14px;
    display: flex;
    align-items: center;

    .unit {
      margin-left: 2px;
    }
  }
}
</style>