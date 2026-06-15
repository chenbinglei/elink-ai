<template>
  <Dialog v-model:isVisible="dialog_visible" :listLoading="listLoading" :manualEnterClose="false" :title="titleName" disabledLoading
          width="50vw" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main" v-loading="listLoading">
        <el-form ref="formdialogRef" :model="formdialog" :rules="rules" label-width="100px">
          <el-form-item label="事件名称：" prop="eventName">
            <el-input v-model="formdialog.eventName" maxlength="32" placeholder="请输入事件名称" show-word-limit type="text"></el-input>
          </el-form-item>
          <el-form-item label="事件级别：" prop="eventLevel">
            <el-select v-model="formdialog.eventLevel" placeholder="请选择">
              <el-option v-for="item in eventLevelArray" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
          </el-form-item>
          <el-form-item label="触发条件：" prop="calculateType">
            <el-select v-model="formdialog.calculateType" placeholder="请选择" @change="queryModelEventFunctionList()">
              <el-option v-for="item in calculateTypeArray" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
          </el-form-item>
          <template v-if="formdialog.calculateType">
            <el-form-item label="">
              <template v-if="formdialog.calculateType === 1">
                <el-table :data="formdialog.showData" max-height="180" border stripe>
                  <el-table-column align="center" label="运算符" width="110">
                    <template #default="{ row,$index }">
                      <el-select v-model="row.operator" placeholder="请选择" v-if="$index > 0">
                        <el-option v-for="item in operatorArray" :key="item.id" :label="item.name" :value="item.id"/>
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column align="center" label="功能点" width="130">
                    <template #default="{ row }">
                      <el-select v-model="row.functionLogo" placeholder="请选择">
                        <template v-for="item in functionArray" :key="item.functionLogo">
                          <el-option :disabled="disabledFunction(item.functionLogo)" :label="item.functionName" :value="item.functionLogo"/>
                        </template>
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column align="center" label="值" width="50">
                    <template #default="{ row }"><span>值</span></template>
                  </el-table-column>
                  <el-table-column align="center" label="比较符" width="120">
                    <template #default="{ row }">
                      <el-select v-model="row.symbol" placeholder="请选择">
                        <el-option v-for="item in symbolArray" :key="item.id" :label="item.name" :value="item.id"/>
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column align="center" label="比较值">
                    <template #default="{ row }">
                      <div class="flex-jc-ai-center">
                        <template v-if="row.symbol === 'InScope' || row.symbol === 'NoInScope'">
                          <el-input-number v-model="row.minValue" controls-position="right" placeholder="最小值"/>
                          <span style="margin: 0 2px">~</span>
                          <el-input-number v-model="row.maxValue" :min="row.minValue" :disabled="!row.minValue&&row.minValue!==0"
                                           controls-position="right" placeholder="最大值"/>
                        </template>
                        <template v-else>
                          <el-input-number v-model="row.minValue" controls-position="right" placeholder="请输入"/>
                        </template>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column align="center" width="50">
                    <template #header>
                      <span class="iconfont icon-treeAdd pointer" style="color:#1F74E2" @click="clickTableAddBut"></span>
                    </template>
                    <template #default="{ row,$index }">
                      <span class="iconfont icon-shanchuxiajimokuai pointer" style="color:#EC2020" @click="clickTableDeleteBut($index)"></span>
                    </template>
                  </el-table-column>
                </el-table>
              </template>
              <template v-if="formdialog.calculateType === 2">
                <el-col :span="6">
                  <el-form-item prop="functionLogo">
                    <el-select v-model="formdialog.showData.functionLogo" placeholder="请选择功能点">
                      <el-option v-for="item in functionArray" :key="item.functionLogo" :label="item.functionName" :value="item.functionLogo"/>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="1"></el-col>
                <el-col :span="5">
                  <el-form-item prop="point">
                    <el-input-number v-model="formdialog.showData.point" :step="1" step-strictly controls-position="right" placeholder="请输入点位"/>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="位运算结果=" prop="result" label-width="110px">
                    <el-select v-model="formdialog.showData.result" placeholder="请选择">
                      <el-option v-for="item in resultArray" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                  </el-form-item>
                </el-col>
              </template>
            </el-form-item>
          </template>
<!--          <el-form-item label="允许解除：">-->
<!--            <div class="item_content">-->
<!--              <el-switch v-model="formdialog.isAllow" :active-value="1" :inactive-value="2"></el-switch>-->
<!--              <div class="alterText">是否允许手动解除告警状态，使告警恢复正常。</div>-->
<!--            </div>-->
<!--          </el-form-item>-->
<!--          <el-form-item label="事件通知：">-->
<!--            &lt;!&ndash;            v-model="formdialog.eventInform"&ndash;&gt;-->
<!--            <el-input disabled placeholder="暂不规划，后续考虑支持短信、webhook 等多种推送方式" type="text"></el-input>-->
<!--          </el-form-item>-->
          <el-form-item label="事件描述：">
            <el-input v-model="formdialog.eventDesc" :rows="3" maxlength="100" placeholder="请输入描述" show-word-limit type="textarea"/>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {onlyNum, someCharmap} from "@/utils/validate";
import {event_level_array} from "@/utils/setVariate";
import {Delete, Plus} from "@element-plus/icons-vue";
import {getCurrentInstance, onMounted, reactive, ref, toRefs, watch} from "vue";
import {findModelEventListByModelId, getModelEventFunctionList, saveModelEvent} from "@/api/modelCenter/modelManagement";

export default {
  name: "AddModelEvent",
  components: {Plus, Delete},
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "创建场景"
    },
    activeEventId: {
      type: [Number, String],
      default: ""
    },
    activeModelId: {
      type: [Number, String],
      default: ""
    },
  },
  setup(props) {

    const {emit} = getCurrentInstance();

    const validateEventName = (rule, value, callback) => {
      if (!value || !someCharmap(value)) {
        callback(new Error("请输入正确的事件名称"));
      } else {
        callback();
      }
    };

    const validateEventLevel = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择事件级别"));
      } else {
        callback();
      }
    };

    const validateCalculateType = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择触发条件"));
      } else {
        callback();
      }
    };

    const validateFunctionLogo = (rule, value, callback) => {
      if (!that.formdialog.showData.functionLogo) {
        callback(new Error("请选择功能点"));
      } else {
        callback();
      }
    };

    const validateResult = (rule, value, callback) => {
      if (!that.formdialog.showData.result && that.formdialog.showData.result !== 0) {
        callback(new Error("请选择位运算结果"));
      } else {
        callback();
      }
    };

    const validatePoint = (rule, value, callback) => {
      if (!onlyNum(that.formdialog.showData.point)) {
        callback(new Error("请输入点位"));
      } else {
        callback();
      }
    };

    const that = reactive({
      formdialog: {},
      functionArray: [], // 可用功能点数据列表
      listLoading: false,
      dialog_visible: props.isVisible,
      eventLevelArray: event_level_array,
      resultArray: [{id: 0, name: 0}, {id: 1, name: 1}],
      operatorArray: [{id: "&&", name: "并且"}, {id: "||", name: "或者"}],
      calculateTypeArray: [{id: 1, name: "值运算"}, {id: 2, name: "位运算"}],
      symbolArray: [{id: ">", name: ">"},{id: "<", name: "<"},{id: ">=", name: "≥"},{id: "<=", name: "≤"},
        {id: "==", name: "="},{id: "!=", name: "≠"},
        {id: "InScope", name: "在范围内"},{id: "NoInScope", name: "不在范围内"}],

      rules: {
        eventName: [{required: true, trigger: "change", validator: validateEventName}],
        eventLevel: [{required: true, trigger: "change", validator: validateEventLevel}],
        calculateType: [{required: true, trigger: "change", validator: validateCalculateType}],

        point: [{required: true, trigger: "change", validator: validatePoint}],
        result: [{required: false, trigger: "change", validator: validateResult}],
        functionLogo: [{required: true, trigger: "change", validator: validateFunctionLogo}],
      }
    })

    const formdialogRef = ref(null);
    const saveDialog = () => {
      formdialogRef.value.validate((valid) => {
        if (valid) {
          // 添加模型事件
          let formdialog = JSON.parse(JSON.stringify(that.formdialog));

          if(formdialog.calculateType === 1){
            if(!formdialog.showData || !formdialog.showData.length){
              ElMessage({ type: "error", showClose: true, message: "请添加值运算数据！" });
              return
            }

            let storeData = "",functionLogos = [];
            for(let i = 0;i < formdialog.showData.length;i++){

              if(!formdialog.showData[i].operator && i > 0){
                ElMessage({ type: "error", showClose: true, message: `请选择（第${ i + 1}行）运算符！` });
                return
              }

              if(!formdialog.showData[i].functionLogo){
                ElMessage({ type: "error", showClose: true, message: `请选择（第${ i + 1}行）功能点！` });
                return
              }

              if(!formdialog.showData[i].symbol){
                ElMessage({ type: "error", showClose: true, message: `请选择（第${ i + 1}行）比较符！` });
                return
              }

              if(!formdialog.showData[i].minValue && formdialog.showData[i].minValue !== 0){
                ElMessage({ type: "error", showClose: true, message: `请填写（第${ i + 1}行）比较值！` });
                return
              }

              if(formdialog.showData[i].symbol === "InScope" || formdialog.showData[i].symbol === "NoInScope"){
                if(!formdialog.showData[i].maxValue && formdialog.showData[i].maxValue !== 0){
                  ElMessage({ type: "error", showClose: true, message: `请填写（第${ i + 1}行）比较值！` });
                  return
                }
              }

              functionLogos.push(formdialog.showData[i].functionLogo);
              // 拼接运算数据

              // 拼接比较符
              if(formdialog.showData[i].symbol === "InScope" || formdialog.showData[i].symbol === "NoInScope"){
                // 在范围内
                let formulaStr = "";
                if(formdialog.showData[i].symbol === "InScope"){
                  formulaStr = formdialog.showData[i].functionLogo + ">=" + formdialog.showData[i].minValue;
                  formulaStr = formulaStr + "&&";
                  formulaStr = formulaStr + formdialog.showData[i].functionLogo + "<=" + formdialog.showData[i].maxValue;
                }
                // 不在范围内
                if(formdialog.showData[i].symbol === "NoInScope"){
                  formulaStr = formdialog.showData[i].functionLogo + "<=" + formdialog.showData[i].minValue;
                  formulaStr = formulaStr + "&&";
                  formulaStr = formulaStr + formdialog.showData[i].functionLogo + ">=" + formdialog.showData[i].maxValue;
                }
                storeData = `${ storeData }(${ formulaStr })`;
              } else {
                storeData = storeData + formdialog.showData[i].functionLogo; // 先拼接 功能点id
                storeData = storeData + formdialog.showData[i].symbol; // 拼接比较符
                storeData = storeData + formdialog.showData[i].minValue; // 拼接比较值
              }
              // 因为最后一项不需要拼接运算符
              if((i + 1) < formdialog.showData.length)storeData = storeData + formdialog.showData[i].operator; // 拼接运算符
            }
            formdialog.storeData = storeData;
            formdialog.functionLogos = functionLogos;
          }

          // 位运算
          that.listLoading = true;
          if(formdialog.calculateType === 2){
            formdialog.functionLogos = [formdialog.showData.functionLogo];
            formdialog.storeData = JSON.parse(JSON.stringify(formdialog.showData));
          }

          saveModelEvent({ modelId:props.activeModelId,...formdialog }).then(res=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })
    }

    const clickTableAddBut = ()=>{
      that.formdialog.showData.push({ });
    }

    const clickTableDeleteBut = (index)=>{
      that.formdialog.showData.splice(index, 1);
    }

    // 那些功能点禁止选择（值运算）
    const disabledFunction = (functionLogo)=>{
      let disabled = false;
      let findItem = that.formdialog.showData.find(item=> item.functionLogo === functionLogo);
      if(findItem) disabled = true;
      return disabled
    }

    // 获取模型事件相关可用功能点数据列表
    const queryModelEventFunctionList = (isInitParame = false) => {
      if(!isInitParame) that.formdialog.showData = that.formdialog.calculateType === 1 ? [] : {};
      getModelEventFunctionList({calculateType: that.formdialog.calculateType, modelId: props.activeModelId}).then(res => {
        that.functionArray = res.data;
      })
    }

    // 初始化参数配置
    const initParamConfigFun = () => {
      if (props.activeEventId) {
        // 根据模型事件id查询模型事件详情
        that.listLoading = true;
        findModelEventListByModelId({id: props.activeEventId}).then(res => {
          let formdialog = res.data ? res.data : {};
          if(formdialog.showData)formdialog.showData = JSON.parse(formdialog.showData);
          that.formdialog = JSON.parse(JSON.stringify(formdialog));
          queryModelEventFunctionList(true);
          that.listLoading = false;
        }).catch(()=>{
          that.listLoading = false;
        })
      }
    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      initParamConfigFun();
    })

    return {...toRefs(that), watchVisible, watchDialogVisible, formdialogRef, saveDialog, initParamConfigFun, queryModelEventFunctionList,clickTableAddBut,
      clickTableDeleteBut, disabledFunction}
  }
}
</script>

<style lang="scss" scoped>
.alterText {
  color: #242424;
  font-size: 12px;
}
</style>
