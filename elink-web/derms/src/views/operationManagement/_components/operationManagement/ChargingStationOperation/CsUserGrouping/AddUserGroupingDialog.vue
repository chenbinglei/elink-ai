<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="620" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
          <el-form-item label="分组名称" prop="groupName">
            <el-input v-model="formDialog.groupName" placeholder="请输入分组名称" maxlength="32" show-word-limit/>
          </el-form-item>
          <el-form-item label="充电优惠策略" required>
            <el-col :span="24">
              <el-form-item prop="elecDiscount">
                <el-input v-model="formDialog.elecDiscount">
                  <template #prefix>
                    <span class="prefixText">电费折扣</span>
                  </template>
                  <template #suffix>%</template>
                </el-input>
              </el-form-item>
            </el-col>
            <div style="height: 18px;width: 100%"></div>
            <el-col :span="24">
              <el-form-item prop="serviceDiscount">
                <el-input v-model="formDialog.serviceDiscount">
                  <template #prefix>
                    <span class="prefixText">服务费折扣</span>
                  </template>
                  <template #suffix>%</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-form-item>
          <el-form-item label="应用站点" prop="applySiteIds">
              <el-select v-model="formDialog.applySiteIds" filterable multiple collapse-tags max-collapse-tags="1" placeholder="请选择应用站点">
                <el-option v-for="item in applySiteIdsArray" :key="item.id" :label="item.siteName" :value="item.id"></el-option>
              </el-select>
          </el-form-item>
          <el-form-item label="描述">
            <el-input type="textarea" v-model="formDialog.refer" :rows="3" placeholder="请输入描述" maxlength="200" show-word-limit/>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {num0to200, someCharmap} from "@/utils/validate";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted} from "vue";
import {queryAllSiteInfoList, saveOrUpdateUserGroup} from "@/api/operationManagement/CsUserGrouping";

export default defineComponent({
  name:"AddUserGroupingDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加用户组"
    },
    activeEditDataInfo: {
      type: Object,
      default: ()=>{
        return {};
      }
    },
  },
  setup(props){
    const {emit} = getCurrentInstance();

    const validateGroupName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的分组名称"));
      } else {
        callback();
      }
    };

    const validateApplySiteIds = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请选择应用站点"));
      } else {
        callback();
      }
    };

    const validateElecDiscount = (rule, value, callback) => {
      if(value !== undefined){
        if (!num0to200(value)) {
          callback(new Error("请输入整数数字（0-200）"));
        } else {
          callback();
        }
      } else {
        callback(new Error("请输入电费折扣"));
      }
    };

    const validateServiceDiscount = (rule, value, callback) => {
      if(value !== undefined){
        if (!num0to200(value)) {
          callback(new Error("请输入整数数字（0-200）"));
        } else {
          callback();
        }
      } else {
        callback(new Error("请输入服务费折扣"));
      }
    };

    const that = reactive({
      formDialog: {},
      listLoading: false,
      applySiteIdsArray: [],
      dialog_visible: props.isVisible,
      rules: {
        groupName: [{required: true, trigger: "change", validator: validateGroupName }],
        applySiteIds: [{required: true, trigger: "change", validator: validateApplySiteIds }],
        elecDiscount: [{required: true, trigger: "change", validator: validateElecDiscount }],
        serviceDiscount: [{required: true, trigger: "change", validator: validateServiceDiscount }],
      }
    });

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          if(formDialog.applySiteIds) formDialog.applySiteIds = formDialog.applySiteIds.join(',');
          saveOrUpdateUserGroup({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", message: "操作成功", showClose: true });
          }).catch(()=>{
            that.listLoading = false;
          });
        }
      });
    };

    const initParamConfigFun = ()=>{
      let activeEditDataInfo = JSON.parse(JSON.stringify(props.activeEditDataInfo));
      // console.log(activeEditDataInfo);
      if(activeEditDataInfo.siteDetailList && activeEditDataInfo.siteDetailList.length){
        activeEditDataInfo.applySiteIds = [];
        for(let i = 0;i < activeEditDataInfo.siteDetailList.length;i++){
          activeEditDataInfo.applySiteIds.push(activeEditDataInfo.siteDetailList[i].id);
        }
        delete activeEditDataInfo.siteDetailList;
      }
      that.formDialog = Object.assign({},that.formDialog,activeEditDataInfo);
      findAllSiteInfoList();
    };

    // 查询全部站点信息列表
    const findAllSiteInfoList = ()=>{
      queryAllSiteInfoList({ timer: new Date(),groupId: that.formDialog.id }).then(res=>{
        that.applySiteIdsArray = res.data;
      });
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

    return {...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, formDialogRef, initParamConfigFun, findAllSiteInfoList};
  }
});
</script>

<style scoped lang="scss">
.prefixText{
  padding-right: 10px;
  box-sizing: border-box;
}
</style>