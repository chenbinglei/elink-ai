<template>
  <Dialog v-model:isVisible="dialog_visible" title="清除节点数据" :listLoading="listLoading" :manualEnterClose="false" disabledLoading width="620px" @confirm="saveDialog">
    <template v-slot:content>
      <div class="content_body">
        <el-form ref="formDialogRef" :inline="true" :model="formDialog" :rules="rules">
          <el-form-item label="时间范围：" prop="pickerDate">
            <el-date-picker v-model="formDialog.pickerDate" type="datetimerange" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm"
                            :shortcuts="pickerOptions.shortcuts" @calendar-change="pickerOptions.calendarChange" :disabled-date="pickerOptions.disabledDate"
                            :disabled-hours="pickerOptions.disabledHours" :disabled-minutes="pickerOptions.disabledMinutes"
                            range-separator="~" start-placeholder="开始时间" end-placeholder="结束时间"/>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {getCurrentInstance, reactive, toRefs, watch, ref} from "vue";
import {getNowDateAll, pickerOptionsGthanAcTime} from "@/utils/dateTime";

export default {
  name: "ClearNodeHistoryData",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    // 当前编辑的节点id
    computeNodeId: {
      type: [String, Number],
      default: ""
    },
  },
  setup(props){
    const {emit} = getCurrentInstance();

    const validatePickerDate = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择时间范围"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formDialog: {},
      listLoading: false,
      dialog_visible: props.isVisible,
      pickerOptions: pickerOptionsGthanAcTime(),
      rules:{
        pickerDate: [{required: true, trigger: "change", validator: validatePickerDate}],
      }
    })

    const formDialogRef = ref(null);
    const saveDialog = ()=>{
      formDialogRef.value.validate((valid) => {
        if (valid) {
          // that.listLoading = true;
          // let formDialog = JSON.parse(JSON.stringify(that.formDialog));
          // formDialog.endTime = getNowDateAll(formDialog.pickerDate[1]);
          // formDialog.startTime = getNowDateAll(formDialog.pickerDate[0]);
          // deleteNodeHistoryDataByDate({ nodeId:props.computeNodeId,...formDialog }).then(()=>{
          //   that.listLoading = false;
          //   that.dialog_visible = false;
          //   emit("changEvent",{ type: "listArray" });
          //   ElMessage({type: "success", message: "清除成功！", showClose: true});
          // }).catch(()=>{
          //   that.listLoading = false;
          // })
        }
      })
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    return {...toRefs(that), watchVisible, watchDialogVisible, saveDialog, formDialogRef }
  }
}
</script>

<style scoped lang="scss">
.el-form-item{
  margin-bottom: 0;
}
</style>
