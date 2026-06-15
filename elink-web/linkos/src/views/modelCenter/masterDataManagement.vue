<template>
  <div class="app-container">
    <HandleMenus ref="handleMenusRef" :handleMenuArray="handleMenuArray" isCollapseBut title="资产分类" @handleMenuEvent="handleMenuEvent"/>
    <div class="app-container-right" v-resize="setTableMaxHeight">
      <template v-if="tabsArray && tabsArray.length">
        <Tabs v-model:tabsIndex="componentName" :tabsArray="tabsArray" isRightSlot label="label">
          <template #rightButtonSlot>
            <div class="content-right">
              <el-form ref="headerFormRef" :model="formInline" inline @submit.prevent="">
                <el-form-item>
                  <el-input v-model="formInline.keyword" class="input-with-select" placeholder="请输入关键词">
                    <template #append>
                      <el-button :icon="Search" @click="clickItemButFun(1)"></el-button>
                    </template>
                  </el-input>
                </el-form-item>
                <el-form-item>
                  <el-button :disabled="(isAddButClick1 && componentName === 'StandardFunction') || (isAddButClick2 && componentName === 'ExtendedAttribute')"
                             :icon="CirclePlus" class="whiteFontButtons" @click="clickItemButFun(2)">添加</el-button>
                </el-form-item>
              </el-form>
            </div>
          </template>
        </Tabs>
        <component :is="componentName" ref="componentRef" :contentHeight="contentHeight" :activeDeviceTypeId="activeDeviceTypeId" :formInline="formInline"></component>
      </template>
      <null-data v-else words="请联系管理员开放权限"></null-data>
    </div>
  </div>
</template>

<script lang="ts">
import {Search, CirclePlus} from "@element-plus/icons-vue";
import {getAssetTypeList} from "@/api/modelCenter/modelManagement";
import {getLeftTreeDataFun, operateButtonIsClick, setTreeData} from "@/utils";
import {computed, onMounted, reactive, toRefs, defineComponent, ref} from "vue";
import {StandardFunction,ExtendedAttribute} from "@/views/modelCenter/component";

export default defineComponent({
  name: "masterDataManagement",
  components: {StandardFunction,ExtendedAttribute},
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    }
  },
  setup(props) {

    // 标准功能点 添加按钮是否可点击
    const isAddButClick1 = computed(() => {
      return operateButtonIsClick('/device/function/saveFunction')
    })

    // 扩展属性 添加按钮是否可点击
    const isAddButClick2 = computed(() => {
      return operateButtonIsClick('/device/rea/saveSea')
    })

    const that = reactive({
      Search,
      CirclePlus,
      tabsArray: [],
      formInline: {},
      oldFormInline: {},
      componentName: "",
      contentHeight: 280,
      handleMenuArray: [],
      activeDeviceTypeId: "",
      allHandleMenuArray: []
    })

    const handleMenuEvent = (menuButDate) => {
      // console.log(menuButDate);
      if (menuButDate.menuType === "clickTreeNode") {
        if (menuButDate.type === 2) {
          that.formInline = JSON.parse(JSON.stringify(that.oldFormInline));
          that.activeDeviceTypeId = menuButDate.id;
        }
      }
    }

    const componentRef = ref(null);
    const clickItemButFun = (operationType)=>{
      if(operationType === 1)componentRef.value.listArray("resetPage");
      if(operationType === 2)componentRef.value.clickAddButtonFun();
    }

    // 查询当前页面的子级权限
    const queryTabsArrayFun = () => {
      that.tabsArray = getLeftTreeDataFun("/modelCenter/masterDataManagement", 0, 0);
    }

    // 获取设备类型列表
    const queryAssetTypeList = () => {
      getAssetTypeList({timer: new Date(), pageName: "masterDataManagement"}).then(res => {
        let assetTypeList = res.data ? res.data : [];
        for (let i = 0; i < assetTypeList.length; i++) {
          if(assetTypeList[i].type === 1){
            assetTypeList[i].disabled = true;
            assetTypeList[i].iconName = "icon-wenjianjia";
          }
          assetTypeList[i].name = assetTypeList[i].typeName;
        }
        that.handleMenuArray = setTreeData(assetTypeList);
        that.allHandleMenuArray = JSON.parse(JSON.stringify(assetTypeList));
        // console.log(that.handleMenuArray);
      })
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value?.offsetHeight ?? 0;
      that.contentHeight = props.contentMaxHeight - headerFormHeight - 150;
    }

    onMounted(() => {
      queryTabsArrayFun();
      queryAssetTypeList();
      that.oldFormInline = JSON.parse(JSON.stringify(that.formInline));
    })

    return {...toRefs(that), queryTabsArrayFun, queryAssetTypeList, handleMenuEvent, isAddButClick1, isAddButClick2, headerFormRef, setTableMaxHeight,
      clickItemButFun, componentRef}
  }
})
</script>

<style lang="scss" scoped>
.el-form-item {
  margin-bottom: 0;
}
</style>