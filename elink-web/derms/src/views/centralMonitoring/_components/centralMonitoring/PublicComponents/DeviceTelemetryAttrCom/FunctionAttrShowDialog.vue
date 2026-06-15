<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading width="890" @confirm="clickConfirmBut">
    <template v-slot:content>
      <div v-loading="listLoading" class="dialog-main scrollbarStyle">
        <el-row :gutter="16" v-if="list && list.length">
          <template v-for="(item,index) in list" :key="index">
            <el-col :span="8">
              <div class="content_card_list pointer" :class="{disabledClass: item.showType !== 1}" @click="clickYanJingIconFun(item)">
                <div class="content_card_list_line"></div>
                <div class="content_card_list_left">{{ $filters.moreData(item.functionName) }}</div>
                <div class="content_card_list_right">
                  <span v-if="item.showType === 2" class="iconfont icon-yanjing_yincang_o"></span>
                  <span v-else class="iconfont icon-yanjing_xianshi_o"></span>
                </div>
              </div>
            </el-col>
          </template>
        </el-row>
        <null-data v-else words="暂无功能点属性"></null-data>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {findDeviceTelemetryList, saveDeviceDeviceFieldSet} from "@/api/centralMonitoring/centralMonitoring";
import {getCurrentInstance, reactive, toRefs, watch, defineComponent, onMounted} from "vue";

export default defineComponent({
  name: "FunctionAttrShowDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    activeDeviceId: {
      type: String,
      default: ""
    }
  },
  setup(props) {
    const {emit} = getCurrentInstance();

    const that = reactive({
      list: [],
      titleName: "遥控",
      listLoading: false,
      dialog_visible: props.isVisible,
    });

    const clickItemFun = (operateType)=>{
      that.activeOperateType = operateType;
    };

    const clickConfirmBut = () => {
      let functionFields = [];
      for(let i = 0;i < that.list.length;i++) if(that.list[i].showType === 2) functionFields.push(that.list[i].functionId);
      saveDeviceDeviceFieldSet({deviceId: props.activeDeviceId,functionFields: functionFields}).then(()=>{
        emit("changeEvent");
        that.dialog_visible = false;
        ElMessage({ type: "success", showClose: true, message: `操作成功！` });
      }).catch(() => {
        that.listLoading = false;
      });
    };

    const initParamConfigFun = () => {
      that.listLoading = true;
      findDeviceTelemetryList({deviceId: props.activeDeviceId, timer: new Date()}).then(res => {
        that.list = res.data ? res.data : [];
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      });
    };

    const clickYanJingIconFun = (item)=>{
      item.showType = item.showType === 1 ? 2 : 1;
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    });

    return {...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, initParamConfigFun, clickItemFun, clickYanJingIconFun};
  }
});
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
    background: #a7bcce26;
    border: 1px solid #106ec426;
    position: relative;
    display: flex;
    align-items: center;

    .content_card_list_left{
      flex: 1;
      font-weight: 600;
    }

    .content_card_list_right{
      margin-right: 4px;
    }

    .content_card_list_line{
      width: 3px;
      height: 18px;
      position: absolute;
      left: 0;
      border-radius: 0 12px 12px 0;
      box-shadow: 0 4px 4px #00000059;
      background: linear-gradient(180deg, #48c5ff 0%, #057cb3 106.06%);
    }
  }

  .disabledClass{
    background: #0071a199;
  }
}
</style>
