<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" append-to-body disabledLoading width="780" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px" @submit.native.prevent>
          <el-form-item label="图模名称：">
            <div class="flex-ai-center canvasName">
              <span class="iconfont icon-wenjianshangchuanhou"></span>
              <span>{{ canvasMeta2dData.name }}</span>
            </div>
          </el-form-item>
          <el-form-item label="URL：" required>
            <el-col :span="11">
              <div class="domain_url">{{ formDialog.locationOrigin }}/{{ formDialog.previewName }}</div>
            </el-col>
            <el-col :span="2">
              <div class="flex-jc-ai-center pointer">
                <el-icon size="24"><Refresh /></el-icon>
              </div>
            </el-col>
            <el-col :span="11">
              <el-form-item prop="domainId">
                <el-input v-model="formDialog.domainId" placeholder="请输入" maxlength="10"></el-input>
              </el-form-item>
            </el-col>
          </el-form-item>
          <el-form-item label="访问域名：">
            <div class="flex-ai-center jc-space-between" style="width: 100%;padding-right: 12px;box-sizing: border-box">
              <div class="domain_url">{{ formDialog.locationOrigin }}/{{ formDialog.previewName }}/{{ formDialog.domainId }}</div>
              <div class="flex-ai-center">
                <el-link type="primary" @click="clickItemButFun(1)">点击访问</el-link>
                <div style="width: 16px"></div>
                <el-link type="primary" :underline="false" @click="clickItemButFun(2)">一键复制</el-link>
              </div>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {useStore} from "vuex";
import {ElMessage} from "element-plus";
import {clickCopyValue} from "@/utils";
import {useRoute, useRouter} from "vue-router";
import {letterNumLine} from "@/utils/validate";
import {Refresh} from '@element-plus/icons-vue';
import {saveGraph} from "@/api/2DVisualization/2d_drawManagement";
import {handleCanvasMeta2dDataFun} from "./handleCanvasMeta2dData";
import {reactive, toRefs, watch, getCurrentInstance, defineComponent, onMounted, computed, ref} from 'vue';

export default defineComponent({
  name: 'CanvasMeta2dPublishDialog',
  components:{Refresh},
  props: {
    isVisible: {
      type: Boolean,
      default: false,
    },
  },
  setup(props) {
    const store = useStore();
    const route = useRoute();
    const vueRouter = useRouter();
    const {emit} = getCurrentInstance();

    const canvasMeta2d = computed(() => {
      return store.state.meta2d.canvasMeta2d;
    });

    const canvasMeta2dData = computed(() => {
      return store.state.meta2d.canvasMeta2dData;
    });

    const validateDomainId = (rule, value, callback) => {
      if (!value || !letterNumLine(value)) {
        callback(new Error("请输入正确的参数"));
      } else {
        callback();
      }
    }

    const that = reactive({
      formDialog: {},
      titleName: "发布",
      listLoading: false,
      activeRoutePath: route.path, // 当前页面路径
      dialog_visible: props.isVisible,

      rules:{
        domainId: [{required: true, trigger: "change", validator: validateDomainId }],
      }
    })

    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let formData = new FormData();
          let returnDataInfo = handleCanvasMeta2dDataFun();
          let formDialog = JSON.parse(JSON.stringify(that.formDialog));

          formDialog.updateType = 6; // 编辑类型 1-新增 2-移动 3-重命名 4-锁定状态 5-保存 6-发布
          formDialog.id = canvasMeta2dData.value.id;
          formDialog.name = canvasMeta2dData.value.name;
          for(let key in formDialog) if(formDialog[key])formData.append(key, formDialog[key]);

          let meta2dBlob = new Blob([JSON.stringify(returnDataInfo.canvasMeta2dFileData)], {type: 'application/json'});
          let meta2dFile = new File([meta2dBlob], `${ formDialog.name }.json`, {type: 'application/json'});
          formData.append("deviceVariables", JSON.stringify(returnDataInfo.deviceVariables));
          formData.append("file", meta2dFile);

          saveGraph(formData).then(()=>{
            clickBackButFun();
            that.listLoading = false;
            ElMessage({type: "success", showClose: true, message: "发布成功！"});
          }).catch(() => {
            that.listLoading = false;
          });
        }
      })
    }

    // 后退一页  回去
    const clickBackButFun = () => {
      vueRouter.go(-1);
      setTimeout(() => {
        let activeRoutePath = route.path;
        // 检测当前页的路由是否还是原来的，如果是原来的则重新回退
        if (activeRoutePath === that.activeRoutePath) {
          clickBackButFun();
        }
      }, 20)
    }

    // 点击访问  一键复制
    const clickItemButFun = (operateType)=>{
      formDialogRef.value.validate((valid) => {
        if (valid) {
          let canvas_link = `${ that.formDialog.locationOrigin }/${ that.formDialog.previewName }/${ that.formDialog.domainId }`;
          if(operateType === 1) window.open(canvas_link, '_blank');
          if(operateType === 2) clickCopyValue(canvas_link);
        }
      })
    }

    const initParamConfigFun = ()=>{
      that.formDialog.previewName = 'canvasPreview';
      that.formDialog.locationOrigin = location.origin;
      that.formDialog.domainId = canvasMeta2dData.value.domainId;
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    })

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    })

    onMounted(() => {
      initParamConfigFun();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, clickConfirmBut, initParamConfigFun, canvasMeta2dData, formDialogRef, canvasMeta2d, clickItemButFun, clickBackButFun}
  }
})
</script>

<style lang="scss" scoped>
.domain_url{
  color: #666666;
  font-size: 12px;
}

.canvasName{

  .iconfont{
    color: #007FEB;
    margin-right: 12px;
  }
}
</style>