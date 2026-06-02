<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" width="580" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="图形名称:" prop="graphName">
            <el-input v-model="formDialog.graphName" maxlength="32" placeholder="请输入图形名称" show-word-limit type="text"></el-input>
          </el-form-item>
          <el-form-item label="默认图形:">
            <el-radio-group v-model="formDialog.isDefault">
              <el-radio v-for="(item,index) in defaultArray" :key="index" :label="item.id">{{ item.name }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="图形分类:">
            <el-select v-model="formDialog.graphTypeId" clearable placeholder="请选择图形分类">
              <el-option v-for="item in graphTypeIdArray" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="图形URL:" prop="graphUrl">
            <el-input v-model="formDialog.graphUrl" :rows="3" placeholder="请输入图形URL" type="textarea"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {notCharmap, validateURL} from "@/utils/validate";
import {saveGraph} from "@/api/visualization/graphicAssociation";
import {findGraphTypeListByTypeId} from "@/api/visualization/graphicClassify";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch, defineComponent} from "vue";

export default defineComponent({
  name: "AddGraphicAssDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "添加关联"
    },
    activeEditInfo:{
      type: Object,
      default:()=>{
        return { }
      }
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const validateGraphName = (rule, value, callback) => {
      if (!value || !notCharmap(value)) {
        callback(new Error("请输入正确的图形名称"));
      } else {
        callback();
      }
    };

    const validateGraphUrl = (rule, value, callback) => {
      if (!validateURL(value)) {
        callback(new Error("请输入正确的图形URL"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      graphTypeIdArray: [],
      formDialog: { isDefault: 2 },
      dialog_visible: props.isVisible,
      defaultArray: [{id: 1, name: "默认"}, {id: 2, name: "否"}],
      rules: {
        graphName: [{required: true, trigger: "change", validator: validateGraphName }],
        graphUrl: [{required: true, trigger: "change", validator: validateGraphUrl }],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          saveGraph({ ...formDialog }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    // 根据资产分类id查询图形分类列表
    const queryGraphTypeListByTypeId = ()=>{
      findGraphTypeListByTypeId({ typeId: that.formDialog.typeId,timer: new Date() }).then(res=>{
        that.graphTypeIdArray = res.data ? res.data : [];
      })
    }

    const initParamConfigFun = () => {
      that.formDialog = Object.assign({},that.formDialog,props.activeEditInfo);
      queryGraphTypeListByTypeId();
      // console.log(that.formDialog);
    }

    const watchVisible = watch(() => props.isVisible, (newVisible) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch(() => that.dialog_visible, (newDialogVisible) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, formDialogRef, saveDialog, initParamConfigFun, queryGraphTypeListByTypeId}
  }
})
</script>

<style lang="scss" scoped>

</style>