<template>
  <div class="strategyParameterCom content_border">
    <template v-if="activeStrategyId">
      <div class="content_header">
        <div class="content_header_left">
          <img alt="" src="@/assets/image/celvecanshu.png"/>
          <span class="title">策略参数</span>
        </div>
        <div class="content_header_right">
          <el-button :icon="Refresh" @click="clickOperateBut(1)">刷新</el-button>
          <el-button :icon="DocumentCopy" @click="clickOperateBut(2)">复制</el-button>
          <el-button :icon="DocumentChecked" type="primary" @click="clickOperateBut(3)">保存</el-button>
          <el-button :icon="Delete" @click="clickOperateBut(4)">删除</el-button>
        </div>
      </div>
      <div class="content_body" v-loading="listLoading">
        <template v-if="handleMenuArray && handleMenuArray.length">
          <HandleMenus ref="handleMenusRef" :handleMenuArray="handleMenuArray" :treeProps="handleMenuTreeProps" customTreeClass="leftArrowClass"
                       dataRenewIsFun defaultExpandAll :isSearchInput="false" :isShowHeader="false" class="content_body_left"
                       :expandOnClickNode="false" @handleMenuEvent="handleMenuEvent"/>
          <div class="content_body_right" ref="contentBodyRightRef" v-if="!loadingFile">
            <StrategyParameterConfig ref="strategyParameterConfigRef" :configMenuArray="configMenuArray"></StrategyParameterConfig>
          </div>
        </template>
        <null-data v-else words="该文件无配置参数！"></null-data>
      </div>
    </template>
    <null-data v-else words="请先添加控制策略！"></null-data>
    <CopyStrategyDialog v-if="copyStrategyVisible" v-model:isVisible="copyStrategyVisible" :activeEditInfo="activeEditInfo" />
  </div>
</template>

<script>
import {useStore} from "vuex";
import {generateUUID} from "@/utils";
import {ElMessage, ElMessageBox} from "element-plus";
import CopyStrategyDialog from "./CopyStrategyDialog.vue";
import HandleMenus from "@/components/handleMenu/HandleMenus.vue";
import StrategyParameterConfig from "./StrategyParameterConfig.vue";
import {computed, defineComponent, getCurrentInstance, nextTick, reactive, ref, toRefs, watch} from "vue";
import {Refresh, DocumentCopy, Delete, DocumentChecked} from '@element-plus/icons-vue';
import {deleteStrategyById, findStrategyById, updateStrategy} from "@/api/centralMonitoring/energyManagement";

export default defineComponent({
  name: "StrategyParameterCom",
  components: {CopyStrategyDialog, HandleMenus,StrategyParameterConfig},
  props:{
    // 当前选中的策略id
    activeStrategyId: {
      type: [String, Number],
      default: ""
    },
    activeSiteId: {
      type: [String, Number],
      default: ""
    },
    // 当前设备信息
    activeDeviceInfo: {
      type: Object,
      default: () => {
        return {};
      }
    },
  },
  setup(props) {
    const store = useStore();
    const {emit} = getCurrentInstance();

    const strategyName = computed(() => {
      return store.state.energyManagement.strategyName;
    });

    const that = reactive({
      Delete,
      Refresh,
      DocumentCopy,
      DocumentChecked,
      configContent: {}, //  配置文件 保存的数据
      handleMenuArray: [],  // 左侧树形使用的数据
      configMenuArray: [], // 数据配置使用的数据
      originalConfigFileData: {}, // 保存初次获取的模板文件

      queryType: 1, // 1-保存数据 2-读取数据
      loadingFile: false,
      listLoading: false,
      activeEditInfo: {},
      copyStrategyVisible: false,
      handleMenuTreeProps:{value: "en_name", label: "ch_name", children: "body"},
    });

    // 根据策略id和类型查询策略配置
    const queryStrategyById = ()=>{
      that.listLoading = true;
      that.loadingFile = true;
      findStrategyById({id: props.activeStrategyId,type: that.queryType,timer: new Date() }).then(res=>{
        let returnDataInfo = res.data ? res.data : {};
        // console.log(returnDataInfo);
        store.dispatch("updateIssueStrategyTime",returnDataInfo.issueTime);
        that.configContent = returnDataInfo.configContent; // 配置文件 保存的数据

        let configFileData = JSON.parse(returnDataInfo.templateContent ?? "{}");
        that.originalConfigFileData = JSON.parse(JSON.stringify(configFileData)); // 保存初次获取的配置文件
        resetConfigMenuArrayDataFun();   // 设置文件配置数据
      }).catch((error)=>{
        if (error && error.code === 88886) return;
        that.configContent = "{}"; // 读取错误
        resetConfigMenuArrayDataFun();   // 设置文件配置数据
      });
    };

    // 设置文件配置数据
    const handleMenusRef = ref(null);
    const resetConfigMenuArrayDataFun = ()=>{
      // console.log(that.configContent);
      if(that.configContent){
        that.configContent = JSON.parse(that.configContent);
        // console.log(that.configContent);

        if(that.queryType === 1){
          let handleMenuArray = JSON.parse(JSON.stringify(that.configContent.policyCfg ?? [])); // 左侧树形使用的数据
          that.handleMenuArray = treeRecursionFun(JSON.parse(JSON.stringify(handleMenuArray)),true); // 左侧树形使用的数据
          that.configMenuArray = JSON.parse(JSON.stringify(that.configContent.policyCfg)); // 数据配置使用的数据
        }
        if(that.queryType === 2){
          let handleMenuArray = treeRecursionFun(JSON.parse(JSON.stringify(that.originalConfigFileData.policyCfg))); // 左侧树形使用的数据
          that.handleMenuArray = treeRecursionFun(JSON.parse(JSON.stringify(handleMenuArray)),true); // 左侧树形使用的数据
          let configMenuArray = JSON.parse(JSON.stringify(handleMenuArray));
          // 进行数据回填
          if(that.configContent.policyCfg) configMenuArray = resetConfigMenuArrayFun(configMenuArray,that.configContent.policyCfg);
          that.configMenuArray = JSON.parse(JSON.stringify(configMenuArray));
        }
      } else {
        // console.log(that.originalConfigFileData);
        that.handleMenuArray = treeRecursionFun(JSON.parse(JSON.stringify(that.originalConfigFileData.policyCfg))); // 左侧树形使用的数据
        that.configMenuArray = JSON.parse(JSON.stringify(that.handleMenuArray)); // 数据配置使用的数据
      }

      // console.log(that.configMenuArray);
      that.listLoading = false;
      that.loadingFile = false;
      nextTick(()=>handleMenusRef.value.treeBoxRef.filter("body"));
    };

    const contentBodyRightRef = ref(null);
    const handleMenuEvent = (data)=>{
      // console.log(data.id);
      let activeDom = document.getElementById(data.id ?? "");
      if(activeDom){
        let scrollTop = activeDom.offsetTop - contentBodyRightRef.value.offsetTop - 24;
        contentBodyRightRef.value.scrollTo({top: scrollTop, behavior: "smooth"});
      }
    };

    const strategyParameterConfigRef = ref(null);
    const clickOperateBut = (operateType)=>{

      if(operateType === 1){
        that.queryType = 1;
        queryStrategyById();
      }

      // 复制策略
      if(operateType === 2){
        that.activeEditInfo = {
          id: props.activeStrategyId,
          activeSiteId: props.activeSiteId,
          strategyType: props.activeDeviceInfo.strategyType
        };
        that.copyStrategyVisible = true;
      }

      if(operateType === 3){
        // console.log(that.configMenuArray);
        let configMenuArray = JSON.parse(JSON.stringify(that.configMenuArray));
        let configParam = JSON.parse(JSON.stringify(that.originalConfigFileData));

        let configValidateStatus = configFileDataValidateFun(configMenuArray);
        // console.log(configValidateStatus);
        if(!configValidateStatus) return false;
        if(configParam) configParam.policyCfg = configMenuArray;
        // console.log(configParam);

        that.listLoading = true;
        let formData = new FormData();
        let blob = new Blob([ JSON.stringify(configParam) ], {type: 'application/json'});
        let strategyParameFile = new File([blob], `策略参数配置.json`, { type: 'application/json' });
        formData.append("id", props.activeStrategyId);
        formData.append("configFile", strategyParameFile);
        formData.append("strategyName", strategyName.value);

        // 编辑策略数据
        updateStrategy(formData).then(()=>{
          that.queryType = 1;
          queryStrategyById();
          that.listLoading = false;
          ElMessage({ type: "success", showClose: true, message: "操作成功！" });
        }).catch(()=>{
          that.listLoading = false;
        });
      }

      if(operateType === 4){
        ElMessageBox.confirm(`确定删除当前策略吗？`, "提示", {
          dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
          customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteStrategyById({ id: props.activeStrategyId }).then(()=>{
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
          emit("changeEvent",{ operateType: "deleteStrategy" });
          ElMessage({ type: "success", showClose: true, message: "删除成功！" });
        }).catch(() => {
          console.log("取消删除！");
        });
      }
    };

    // 保存校验配置文件
    const configFileDataValidateFun = (configMenuArray = [],isVerification = true)=>{

      for(let i = 0;i < configMenuArray.length;i++){

        if(configMenuArray[i].type === "jsonObject"){
          // 需要校验必填
          // console.log(configMenuArray[i]);

          if(configMenuArray[i].body && configMenuArray[i].body.length){
            // 循环该项下的数值数组
            for(let j = 0;j < configMenuArray[i].body.length;j++){
              //如果该项 字段没有填写，有默认值，赋值默认值
              // console.log(configMenuArray[i].body[j]);
              if(!configMenuArray[i].body[j].value && configMenuArray[i].body[j].value !== 0){
                configMenuArray[i].body[j].value = configMenuArray[i].body[j].default;

                if(configMenuArray[i].body[j].Configuration){
                  ElMessage({ type: "error", showClose: true, message: `请填写${ configMenuArray[i].ch_name }中的${ configMenuArray[i].body[j].ch_name}` });
                  handleMenuEvent(configMenuArray[i].body[j]);
                  isVerification = false;
                  return false;
                }
              }
            }
          }
        }

        if(configMenuArray[i].type === "jsonArray"){

          // 是否校验数组类型必须填写
          if(configMenuArray[i].Configuration){
            if(!configMenuArray[i].body || !configMenuArray[i].body.length){
              ElMessage({ type: "error", showClose: true, message: `请填写${ configMenuArray[i].ch_name }的信息` });
              handleMenuEvent(configMenuArray[i]);
              isVerification = false;
              return false;
            }
          }

          if(configMenuArray[i].body && configMenuArray[i].body.length){
            // 循环该项下的数值数组
            for(let j = 0;j < configMenuArray[i].body.length;j++){
              for (let k = 0;k < configMenuArray[i].arrFormat.length;k++){
                let findItem = configMenuArray[i].arrFormat[k];
                //如果该项 字段没有填写，有默认值，赋值默认值
                if(!configMenuArray[i].body[j][findItem.en_name] && configMenuArray[i].body[j][findItem.en_name] !== 0){
                  configMenuArray[i].body[j][findItem.en_name] = findItem.default;

                  if(findItem.Configuration){
                    ElMessage({ type: "error", showClose: true, message: `请填写${ configMenuArray[i].ch_name }中第${ j + 1 }行的${ findItem.ch_name}` });
                    handleMenuEvent(configMenuArray[i]);
                    isVerification = false;
                    return false;
                  }
                }
              }
            }
          }
        }

        if(configMenuArray[i].body && configMenuArray[i].body.length) isVerification = configFileDataValidateFun(configMenuArray[i].body,isVerification);
      }

      // console.log(isVerification);
      return isVerification;
    };

    /*
    * 给每一项数据添加一个id
    * isJsonArrayBody:  是否删除 body数据
    * */
    const treeRecursionFun = (data,isJsonArrayBody = false)=>{
      for (let key in data) {
        if(!data['id']) data['id'] = generateUUID();
        if(data['type'] === "jsonArray" && isJsonArrayBody) delete data['body'];
        if (typeof data[key] === "object") treeRecursionFun(data[key],isJsonArrayBody);
      }
      return data;
    };

    // 读取数据 需要重新回填数据
    const resetConfigMenuArrayFun = (configMenuArray = [],configContent = {})=>{
      for(let i = 0;i < configMenuArray.length;i++){
        // console.log("configContent",configContent);
        let parent_obj = configContent[configMenuArray[i].en_name];
        if(configMenuArray[i].type === "jsonObject"){
          // console.log("parent_obj",parent_obj);
          // console.log("configMenuArray",configMenuArray[i]);
          if(configMenuArray[i].body && configMenuArray[i].body.length){
            for(let j = 0;j < configMenuArray[i].body.length;j++){
              try {
                if(!configMenuArray[i].body[j].body || !configMenuArray[i].body[j].body.length){
                  let active_value = parent_obj[configMenuArray[i].body[j].en_name];
                  configMenuArray[i].body[j].value = configMenuArray[i].body[j].type === "bool" ? String(active_value) : active_value;
                }
              } catch (e) {}
            }
          }
        }

        if(configMenuArray[i].type === "jsonArray"){
          // console.log("parent_obj",parent_obj);
          // console.log("configMenuArray",configMenuArray[i]);
          configMenuArray[i].body = JSON.parse(JSON.stringify(parent_obj));
        }

        if(configMenuArray[i].body && configMenuArray[i].body.length){
          resetConfigMenuArrayFun(configMenuArray[i].body,configContent[configMenuArray[i].en_name]);
        }
      }

      return configMenuArray;
    };

    const watchActiveStrategyId = watch(()=>props.activeStrategyId,(newActiveStrategyId)=>{
      that.queryType = 1; // 保存数据
      if(newActiveStrategyId) queryStrategyById();
    },{ deep: true });

    return {...toRefs(that), handleMenuEvent, treeRecursionFun, clickOperateBut, queryStrategyById, watchActiveStrategyId, strategyParameterConfigRef,
      configFileDataValidateFun, strategyName, resetConfigMenuArrayDataFun, contentBodyRightRef, resetConfigMenuArrayFun, handleMenusRef};
  }
});
</script>

<style lang="scss" scoped>
.strategyParameterCom {
  flex: 1;
  width: 2px;
  flex-basis: auto;
  display: flex;
  flex-direction: column;
  margin-left: 12px;

  .content_header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    box-sizing: border-box;
    padding: 14px 12px;

    .content_header_left {
      display: flex;
      align-items: center;

      img {
        width: 38px;
        height: 38px;
        margin-right: 14px;
      }

      .title {
        color: #FFFFFF;
        font-size: 16px;
        font-weight: 700;
      }
    }
  }

  .content_body {
    flex: 1;
    height: 2px;
    flex-basis: auto;
    display: flex;

    .content_body_left {
      width: 24%;
      min-width: 210px;
      max-width: 310px;
      height: 100%;
    }

    .content_body_right{
      flex: 1;
      height: 100%;
      overflow-y: auto;
      padding: 14px 12px;
      transition: all .25s;
      box-sizing: border-box;
    }
  }
}
</style>