<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="520" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px" @submit.native.prevent>
          <el-form-item label="文件名称：">
            <el-input v-model="formDialog.name" placeholder="请输入" disabled type="text"></el-input>
          </el-form-item>
          <el-form-item label="关联站点：" prop="siteId">
            <el-select v-model="formDialog.siteId" filterable placeholder="请选择">
              <el-option v-for="item in siteIdArray" :key="item.siteId" :label="item.siteName" :value="item.siteId"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {findSiteListByUserId, saveGraph} from "@/api/2DVisualization/2d_drawManagement";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "FileAssociationSiteDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeEditInfo: {
      type: Object,
      default: ()=>{
        return {}
      }
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateSiteId = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择关联站点"));
      } else {
        callback();
      }
    }

    const that = reactive({
      listLoading: false,
      titleName: "关联站点",
      dialog_visible: props.isVisible,
      formDialog: { type: 2,updateType: 7 },
      siteIdArray: [],
      rules: {
        siteId: [{ required: true, trigger: "change", validator: validateSiteId }],
      }
    })

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          // console.log(formDialog);
          // 新建或编辑图模数据
          saveGraph({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", message: "操作成功", showClose: true });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const initParamConfigFun = () => {
      that.formDialog = Object.assign({}, that.formDialog, props.activeEditInfo);
      // console.log(that.formDialog);
    }

    // 根据用户id查询站点列表
    const querySiteListByUserId = ()=>{
      findSiteListByUserId({timer: new Date()}).then(res=>{
        that.siteIdArray = res.data;
      })
    }

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch(() => that.dialog_visible, (newDialogVisible) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
      querySiteListByUserId();
    })

    return {...toRefs(that), watchDialogVisible, watchVisible, initParamConfigFun, clickConfirmBut, formDialogRef, querySiteListByUserId}
  }
})
</script>

<style lang="scss" scoped>

</style>