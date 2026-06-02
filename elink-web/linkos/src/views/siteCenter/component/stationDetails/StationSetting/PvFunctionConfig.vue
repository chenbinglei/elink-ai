<template>
  <el-form :model="formInline" label-width="auto" :rules="rules" ref="formDialogRef">
    <el-form-item label="光伏累计发电量统计方式：" prop="pvQtSource">
      <el-select v-model="formInline.pvQtSource" clearable placeholder="默认" @change="selectionChangeFun">
        <el-option v-for="item in list" :key="item.id" :label="item.name" :value="item.id"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="二氧化碳减排量计算系数：" prop="reduceCoeff">
      <el-input-number v-model="formInline.reduceCoeff" controls-position="right" @change="selectionChangeFun"></el-input-number>
    </el-form-item>
    <el-form-item label="节约标煤量计算系数：" prop="tceCoeff">
      <el-input-number v-model="formInline.tceCoeff" controls-position="right" @change="selectionChangeFun"></el-input-number>
    </el-form-item>
    <el-form-item label="等效植树计算系数：" prop="treeCoeff">
      <el-input-number v-model="formInline.treeCoeff" controls-position="right" @change="selectionChangeFun"></el-input-number>
    </el-form-item>
    <el-form-item v-if="isClickEditBut">
      <el-button @click="clickCancelButFun">取消</el-button>
      <el-button type="primary" @click="clickSaveButFun">保存</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import {ElMessage, ElMessageBox} from "element-plus";
import {updateSiteSetUp} from "@/api/siteCenter/stationDetails";
import {defineComponent, getCurrentInstance, reactive, ref, toRefs, watch} from "vue";

export default defineComponent({
  name: "PvFunctionConfig",
  props: {
    returnDataInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  emits: ["changEvent"],
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateInputSl = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择"));
      } else {
        callback();
      }
    };

    const validateInputNum = (rule, value, callback) => {
      if (!value && value !== 0) {
        callback(new Error("请输入计算系数"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formInline: {},
      oldFormInline: {},
      isClickEditBut: false,
      list: [{id: 20, name: "逆变器"}, {id: 66, name: "并网点"}],
      rules:{
        pvQtSource: [{required: true, trigger: "change", validator: validateInputSl }],
        reduceCoeff: [{required: true, trigger: "change", validator: validateInputNum }],
        tceCoeff: [{required: true, trigger: "change", validator: validateInputNum }],
        treeCoeff: [{required: true, trigger: "change", validator: validateInputNum }],
      }
    })

    // 取消保存
    const clickCancelButFun = () => {
      that.isClickEditBut = false;
      that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
    }

    const formDialogRef = ref(null);
    const clickSaveButFun = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          ElMessageBox.confirm(`确定保存当前站点功能应用配置吗？`, "提示", {
            dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
            customClass: "deleteMsgBoxClass", showClose: false, closeOnClickModal: false,
            beforeClose: (action, instance, done) => {
              if (action === 'confirm') {
                instance.confirmButtonLoading = true;
                instance.confirmButtonText = '正在保存...';
                updateSiteSetUp({...that.formInline}).then(() => {
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
            emit("changEvent");
            that.isClickEditBut = false;
            ElMessage({type: "success", showClose: true, message: "更改成功！"});
          }).catch(() => {
            console.log("取消！");
          });
        }
      })
    }

    const selectionChangeFun = () => {
      that.isClickEditBut = true;
    }

    const watchReturnDataInfo = watch(() => props.returnDataInfo, (newReturnDataInfo) => {
      // console.log(newReturnDataInfo);
      let returnDataInfo = JSON.parse(JSON.stringify(newReturnDataInfo ?? {}));
      that.formInline = JSON.parse(JSON.stringify(returnDataInfo));
      that.oldFormInline = JSON.parse(JSON.stringify(returnDataInfo));
    }, {deep: true, immediate: true})

    return {...toRefs(that), watchReturnDataInfo, selectionChangeFun, clickCancelButFun, clickSaveButFun, formDialogRef}
  }
})

</script>

<style lang="scss" scoped>
.el-input-number{
  width: 214px;
}
</style>