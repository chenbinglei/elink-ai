<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="680" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="content_body">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="160px">
          <el-form-item label="运营商 ID：" prop="operatorId">
            <el-input v-model="formDialog.operatorId" maxlength="9" placeholder="请输入运营商 ID" show-word-limit type="text"></el-input>
          </el-form-item>
          <el-form-item label="运营商名称：" prop="operatorName">
            <el-input v-model="formDialog.operatorName" maxlength="64" placeholder="请输入运营商名称" show-word-limit type="text"></el-input>
          </el-form-item>
          <el-form-item label="运营商简称：">
            <el-input v-model="formDialog.operatorShortName" maxlength="32" placeholder="请输入运营商简称" show-word-limit type="text"></el-input>
          </el-form-item>
          <el-form-item label="统一社会信用代码：" prop="operatorCreditCode">
            <el-input v-model="formDialog.operatorCreditCode" maxlength="18" placeholder="请输入统一社会信用代码" show-word-limit type="text"></el-input>
          </el-form-item>
          <el-form-item label="运营商注册地址：">
            <el-input v-model="formDialog.operatorRegAddress" placeholder="请输入" type="text"></el-input>
          </el-form-item>
          <el-form-item label="联系人：" prop="operatorContact">
            <el-input v-model="formDialog.operatorContact" maxlength="16" placeholder="请输入联系人" show-word-limit type="text"></el-input>
          </el-form-item>
          <el-form-item label="运营商电话1：" prop="operatorTel1">
            <el-input v-model="formDialog.operatorTel1" maxlength="11" placeholder="请输入运营商电话1" show-word-limit type="text"></el-input>
          </el-form-item>
          <el-form-item label="运营商电话2：" prop="operatorTel2">
            <el-input v-model="formDialog.operatorTel2" maxlength="11" placeholder="请输入运营商电话2" show-word-limit type="text"></el-input>
          </el-form-item>

          <el-form-item label="备注：">
            <el-input v-model="formDialog.operatorNote" maxlength="255" :rows="3" placeholder="请输入备注" show-word-limit type="textarea"></el-input>
          </el-form-item>

<!--          <div class="title_text">加密设置</div>-->
<!--          <el-form-item label="运营商密钥：">-->
<!--            <el-input v-model="formDialog.operatorTel1" placeholder="请输入运营商密钥" type="text"></el-input>-->
<!--          </el-form-item>-->
<!--          <el-form-item label="消息密钥：">-->
<!--            <el-input v-model="formDialog.operatorTel1" placeholder="请输入消息密钥" type="text"></el-input>-->
<!--          </el-form-item>-->
<!--          <el-form-item label="消息密钥初始化向量：">-->
<!--            <el-input v-model="formDialog.operatorTel1" placeholder="请输入消息密钥初始化向量" type="text"></el-input>-->
<!--          </el-form-item>-->
<!--          <el-form-item label="签名密钥：">-->
<!--            <el-input v-model="formDialog.operatorTel1" placeholder="请输入签名密钥" type="text"></el-input>-->
<!--          </el-form-item>-->
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {isStrLength, mobile, someCharmap} from "@/utils/validate";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";
import {findOperatorDetailsById, saveOrUpdateOperatorInfo} from "@/api/configCenter/operatorManagement";

export default defineComponent({
  name: "AddOperatorDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "新增运营商"
    },
    operatorId: {
      type: [String,Number],
      default: ""
    },
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateOperatorId = (rule, value, callback) => {
      if (!value || !isStrLength(value,9)) {
        callback(new Error("请输入正确的运营商 ID"));
      } else {
        callback();
      }
    };

    const validateOperatorContact = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入运营商联系人"));
      } else {
        callback();
      }
    };

    const validateOperatorName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的运营商名称"));
      } else {
        callback();
      }
    };

    const validateOperatorTel1 = (rule, value, callback) => {
      if (!value || !mobile(value)) {
        callback(new Error("请输入正确的运营商电话1"));
      } else {
        callback();
      }
    };

    const validateOperatorTel2 = (rule, value, callback) => {
      if (value && !mobile(value)) {
        callback(new Error("请输入正确的运营商电话2"));
      } else {
        callback();
      }
    };

    const validateOperatorCreditCode = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入正确的统一社会信用代码"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      listLoading: false,
      dialog_visible: props.isVisible,

      rules:{
        operatorId:[{required: true, trigger: "change", validator: validateOperatorId }],
        operatorName:[{required: true, trigger: "change", validator: validateOperatorName }],
        operatorTel1:[{required: true, trigger: "change", validator: validateOperatorTel1 }],
        operatorTel2:[{required: false, trigger: "change", validator: validateOperatorTel2 }],
        operatorContact:[{required: true, trigger: "change", validator: validateOperatorContact }],
        operatorCreditCode:[{required: true, trigger: "change", validator: validateOperatorCreditCode }]
      }
    })

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          saveOrUpdateOperatorInfo({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    // 根据id查询运营商信息
    const queryOperatorDetailsById = ()=>{
      findOperatorDetailsById({ id: props.operatorId }).then(res=>{
        let formDialog = res.data ? JSON.parse(JSON.stringify(res.data)) : { };
        for(let key in formDialog)if(!formDialog[key])formDialog[key] = "";
        that.formDialog = JSON.parse(JSON.stringify(formDialog));
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      if (props.operatorId)queryOperatorDetailsById();
    })

    return { ...toRefs(that),watchVisible,watchDialogVisible,formDialogRef,clickConfirmBut,queryOperatorDetailsById}
  }
})
</script>

<style scoped lang="scss">
.title_text{
  padding-left: 12px;
  font-weight: bold;
  font-size: 18px;
}
</style>
