<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="890" @confirm="saveDialog">
    <template v-slot:content>
      <div v-loading="listLoading" class="dialog-main scrollbarStyle">
        <el-row :gutter="16">
          <template v-for="(item,index) in list" :key="index">
            <el-col :span="8">
              <div class="content_card_list pointer" :class="{disabledClass: item.showType !== 1}" @click="clickYanJingIconFun(item)">
                <div class="content_card_list_left">{{ $filters.moreData(item.functionName) }}</div>
                <div class="content_card_list_right">
                  <span v-if="item.showType === 2" class="iconfont icon-yanjing_yincang_o"></span>
                  <span v-else class="iconfont icon-yanjing_xianshi_o"></span>
                </div>
              </div>
            </el-col>
          </template>
        </el-row>
      </div>
    </template>
  </Dialog>
</template>

<script>
import {ElMessage} from "element-plus";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, onMounted} from "vue";
import {findDeviceFunctionListById, saveDeviceFunctionField} from "@/api/deviceCenter/deviceList";

export default defineComponent({
  name: "FunctionAttrFieldDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeDeviceId: {
      type: [Number, String],
      default: ""
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      list: [],
      listLoading: false,
      titleName: "功能点管理",
      dialog_visible: props.isVisible,
    })

    const saveDialog = () => {
      that.listLoading = true;
      let functionFields = [];
      for(let i = 0;i < that.list.length;i++) if(that.list[i].showType === 2) functionFields.push(that.list[i].functionId);
      saveDeviceFunctionField({deviceId: props.activeDeviceId,functionFields: functionFields}).then(()=>{
        ElMessage({ type: "success", showClose: true, message: `操作成功！` });
        emit("changeEvent");
        that.dialog_visible = false;
      }).catch(() => {
        that.listLoading = false;
      });
    }

    // 根据设备id查询设备功能属性列表数据
    const queryDeviceFunctionListById = () => {
      that.listLoading = true;
      findDeviceFunctionListById({deviceId: props.activeDeviceId,pageName: "FunctionAttrFieldDialog"}).then(res => {
        that.list = res.data ?? [];
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      });
    }

    const clickYanJingIconFun = (item)=>{
      item.showType = item.showType === 1 ? 2 : 1;
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(()=>{
      queryDeviceFunctionListById();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, saveDialog, queryDeviceFunctionListById, clickYanJingIconFun}
  }
})
</script>

<style lang="scss" scoped>
.dialog-main{
  width: 100%;
  max-height: 580px;
  overflow-y: auto;

  .content_card_list{
    padding: 12px 16px;
    border-radius: 4px;
    margin-bottom: 12px;
    box-sizing: border-box;
    border: 1px solid #DBDBDD;
    display: flex;
    align-items: center;

    .content_card_list_left{
      flex: 1;
      font-weight: 600;
    }

    .content_card_list_right{
      margin-right: 4px;
    }
  }

  .disabledClass{
    background-color: #DBDBDD;
  }
}
</style>