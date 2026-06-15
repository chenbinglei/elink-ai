<template>
  <el-dialog v-model="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName"
    width="50%" @close="closeDialog">
    <div class="dialog-main">
      <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="130px">
        <el-form-item label="策略名称：" prop="strategyName">
          <el-input style="max-width: 380px" v-model="formDialog.strategyName" placeholder="请输入策略名称" type="text"
            max="32" />
        </el-form-item>
        <el-form-item label="生效时间：" prop="takeTime">
          <el-date-picker v-model="formDialog.takeTime" type="daterange" style="max-width: 365px" range-separator="-"
            start-placeholder="开始时间" end-placeholder="结束时间" :format="'YYYY-MM-DD'" :value-format="'YYYY-MM-DD'" />
        </el-form-item>
        <el-form-item label="定价方式：" prop="priceType">
          <buttons-tabs customClass="connectClass" :tabsArray="priceTypeArray"
            v-model:tabsIndex="formDialog.priceType"></buttons-tabs>
        </el-form-item>


        <template v-if="formDialog.priceType === 1">
          <el-form-item label="配置价格：" required>
            <el-form-item prop="electMoney">
              <el-input v-model="formDialog.electMoney" placeholder="请输入电费">
                <template #suffix>
                  <span class="unit_text">元/度</span>
                </template>
              </el-input>
            </el-form-item>
          </el-form-item>
        </template>
        <template v-if="formDialog.priceType === 2">
          <el-form-item label="配置价格：" prop="periodTimeList">
            <div class="content_table">
              <div class="content_table_top">
                <DayPartingSelect v-model:periodTimeList="formDialog.periodTimeList" ref="dayPartingSelectRef">
                </DayPartingSelect>
              </div>
              <div class="content_table_bottom">
                <el-table :data="formDialog.periodTimeList" :max-height="tableMaxHeight">
                  <el-table-column label="时段" width="110">
                    <template #default="{ row }">
                      <div class="period_type" :class="'period_type' + row.periodType">
                        <span>{{ $filters.periodType(row.periodType) }}</span>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column label="时间">
                    <template #default="{ row }">
                      <template v-if="row.children && row.children.length">
                        <template v-for="(item, index) in row.children" :key="index">
                          <span>{{ item.startTime }}</span>
                          <span>~</span>
                          <span>{{ item.actualEndTime }}</span>
                          <span v-if="index + 1 < row.children.length">、</span>
                        </template>
                      </template>
                      <template v-else>--</template>
                    </template>
                  </el-table-column>
                  <el-table-column label="电费" width="380">
                    <template #default="{ row }">
                      <div class="content_config">
                        <el-col :span="11">
                          <el-input v-model="row.electMoney" placeholder="请输入电费" />
                        </el-col>
                        <span class="unit_text">元/度</span>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </el-form-item>
        </template>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer mt-12px justify-end">
        <el-button @click="closeDialog()">取消</el-button>
        <!-- <el-button> {{ formDialog}}复制时段</el-button> -->
        <el-button type="primary" @click="clickConfirmBut()">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script lang="ts">
import { ElMessage } from "element-plus";
import { num40to9999999dot } from "@/utils/validate";
import DayPartingSelect from "./SetPriceDialog/DayPartingSelect.vue";
import { findElectConfigById, saveElectConfig } from "@/api/assetManagement/electricityPrice";
import { getCurrentInstance, reactive, toRefs, watch, defineComponent, ref, onMounted, nextTick } from "vue";

export default defineComponent({
  name: "SetPriceDialog",
  components: { DayPartingSelect },
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    siteId: {
      type: [Number, String],
      default: ""
    },
    // 复制时段使用
    activePricingId: {
      type: [Number, String],
      default: ""
    },
    // 电机模块类型
    moduleType: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: ''
    }




  },
  setup (props) {

    const { emit } = getCurrentInstance();
    console.log(props);

    const validatestrategyName = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请输入策略名称"));
      } else {
        callback();
      }
    };
    const validateTakeTime = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择生效时间"));
      } else {
        callback();
      }
    }; const validatepriceType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择定价方式"));
      } else {
        callback();
      }
    };


    const validateElectMoney = (rule, value, callback) => {
      if (!num40to9999999dot(value)) {
        callback(new Error("请输入正确的电费"));
      } else {
        callback();
      }
    }; const validatePeriodTimeList = (rule, value, callback) => {
      if (!value || !value.length) {
        callback(new Error("请添加分时段配置"));
      } else {
        callback();
      }
    };

    const that = reactive({
      listLoading: false,
      titleName: "电价策略",
      dialog_visible: props.isVisible,
      formDialog: { priceType: 1 },
      priceTypeArray: [{ id: 1, name: "全天同价" }, { id: 2, name: "分时段定价" }],
      title: "",
      tableMaxHeight: 320,
      rules: {
        strategyName: [{ required: true, trigger: "blur", validator: validatestrategyName }],
        takeTime: [{ required: true, trigger: "change", validator: validateTakeTime }],
        priceType: [{ required: true, trigger: "change", validator: validatepriceType }],
        electMoney: [{ required: true, trigger: "change", validator: validateElectMoney }],
        periodTimeList: [{ required: true, trigger: "change", validator: validatePeriodTimeList }],
      }
    });

    //
    const formDialogRef = ref(null);
    const clickConfirmBut = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let chargerPriceDtos = [];
          let form_dialog = JSON.parse(JSON.stringify(that.formDialog));
          let startDate = form_dialog.takeTime[0];
          let endDate = form_dialog.takeTime[1];
          delete form_dialog.takeTime;
          // 全天同价
          if (form_dialog.priceType === 1) {
            let chargerPriceConfig = {
              endTime: "23:59",
              startTime: "00:00",
              periodType: 6,
              electMoney: Number(form_dialog.electMoney),
            };
            chargerPriceDtos.push(chargerPriceConfig);
          }


          // 分时段定价
          if (form_dialog.priceType === 2) {
            for (let i = 0; i < form_dialog.periodTimeList.length; i++) {

              if (!num40to9999999dot(form_dialog.periodTimeList[i].electMoney)) {
                ElMessage({ type: "error", showClose: true, message: `请输入正确的电费！（第${i + 1}行）` });
                that.listLoading = false;
                return;
              }

              for (let j = 0; j < form_dialog.periodTimeList[i].children.length; j++) {
                chargerPriceDtos.push({
                  periodType: form_dialog.periodTimeList[i].periodType,
                  electMoney: form_dialog.periodTimeList[i].electMoney,
                  startTime: form_dialog.periodTimeList[i].children[j].startTime,
                  endTime: form_dialog.periodTimeList[i].children[j].actualEndTime,
                });
              }
            }
          }

          delete form_dialog.periodTimeList;
          delete form_dialog.electMoney;


          let obj = { ...form_dialog, electTimeFrames: chargerPriceDtos, siteId: props.siteId, endDate: endDate, startDate: startDate, moduleType: props.moduleType, };
          console.log(obj, that.title);
          if (props.title !== '编辑') {
            delete obj.id;
          }
          saveElectConfig(obj).then(() => {

            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
            closeDialog();
          }).catch(() => {
            that.listLoading = false;
          });
        }
      });
    };

    const dayPartingSelectRef = ref(null);
    const initParamConfigFun = () => {
      // 获取时段计费信息配置
      if (props.activePricingId) {
        that.listLoading = true;
        findElectConfigById({ id: props.activePricingId }).then(res => {
          let returnDataInfo = res.data ? res.data : {};
          that.formDialog = returnDataInfo
          that.formDialog.takeTime = [returnDataInfo.startDate, returnDataInfo.endDate];
          nextTick(() => {
            // 全天同价
            if (that.formDialog.priceType === 1) {
              if (returnDataInfo.electTimeFrameList && returnDataInfo.electTimeFrameList.length) {
                that.formDialog.electMoney = returnDataInfo.electTimeFrameList[0].electMoney;
              }
            }

            // 分时段定价
            if (that.formDialog.priceType === 2) {
              if (returnDataInfo.electTimeFrameList && returnDataInfo.electTimeFrameList.length) {
                dayPartingSelectRef.value.echoPeriodTimeConfigFun(returnDataInfo.electTimeFrameList);
              }
            }
            that.listLoading = false;
          });

        }).catch(() => {
          that.listLoading = false;
        });
      }
    };

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
      that.title = props.title;

    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      if (newDialogVisible) {
        if (props.activePricingId) {
          initParamConfigFun();
        } else {
          formDialogRef.value?.resetFields();
          that.formDialog.priceType = 1; // ✅ 正确方式
        }
      }
      emit("update:isVisible", newDialogVisible);
    });
    const closeDialog = () => {
      // 方法一：使用 Element Plus 的 resetFields
      if (formDialogRef.value) {
        formDialogRef.value.resetFields();
      }
      emit("close", props.activePricingId);
    }

    onMounted(() => {
      console.log("formDialog.priceType:", that.formDialog.priceType);
    });

    return { ...toRefs(that), watchDialogVisible, watchVisible, clickConfirmBut, formDialogRef, initParamConfigFun, dayPartingSelectRef, closeDialog };
  }
});
</script>

<style lang="scss" scoped>
.content_config {
  display: flex;
  align-items: center;
  border-radius: 8px;

  .el-input {
    --el-border-color: none !important;
  }
}

.unit_text {
  color: #FFFFFF;
  font-size: 14px;
  margin-left: 10px;
}

.dialog-main {
  margin-top: 10px;
}

.content_table {
  width: 100%;
  padding: 16px;
  border-radius: 8px;
  box-sizing: border-box;
  background: rgba(0, 67, 125, 0.15);

  .period_type {
    width: fit-content;
    height: 32px;
    border-radius: 8px;
    background: #36CFC2;
    padding: 0 16px;
    line-height: 32px;
    font-size: 14px;
    color: #FFFFFF;
  }

  .period_type1 {
    background: #FB6868;
  }

  .period_type2 {
    background: #FD9449;
  }

  .period_type3 {
    background: #56ADF7;
  }

  .period_type4 {
    background: #6DCF36;
  }
}
</style>