<template>
  <Dialog v-model:isVisible="dialog_visible" disabledLoading :manualEnterClose="false" :listLoading="listLoading" :title="titleName" width="520" @confirm="saveDialog">
    <template v-slot:content>
      <div class="dialog-main">
        <el-form ref="formDialogRef" :model="formDialog" :rules="rules" label-width="110px">
          <el-form-item label="选择场站:" prop="siteIds">
            <el-tree-select class="leftArrowClass" v-model="formDialog.siteIds" :data="siteArray" :indent="0" multiple collapse-tags
                            :max-collapse-tags="1" :props="treeProps" node-key="id" placeholder="请选择" default-expand-all filterable
                            :render-after-expand="false" show-checkbox highlight-current>
            </el-tree-select>
          </el-form-item>
          <el-form-item label="权限：" prop="authority" v-if="sourceType === 1">
            <el-radio-group v-model="formDialog.authority">
              <el-radio v-for="(item,index) in authorityArray" :key="index" :label="item.id">{{ item.name }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts">
import {ElMessage} from "element-plus";
import {getDataListFun} from "@/utils";
import {getCurrentInstance, onMounted, reactive, toRefs, watch, defineComponent, ref} from "vue";
import {findOrganEmpowerSiteList, findTenantOrganSiteList, batchSaveOrganEmpower} from "@/api/tenantManagement/tenantTabulation";

export default defineComponent({
  name: "AssociatedSitesDialog",
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    titleName: {
      type: String,
      default: "新增站点"
    },
    tenantId: {
      type: [String, Number],
      default: ""
    },
    organId: {
      type: [String, Number],
      default: ""
    },
    // 1： 租户管理详情  2： 企业信息
    sourceType:{
      type: Number,
      default: 1
    }
  },
  setup(props) {

    const {emit} = getCurrentInstance();
    const validateSiteIds = (rule, value, callback) => {
      if(!value || !value.length){
        callback(new Error("请选择场站"));
      } else {
        callback();
      }
    };

    const that = reactive({
      siteArray: [],
      site_all_list: [],
      listLoading: false,
      formDialog: { authority: 1 },
      dialog_visible: props.isVisible,
      authorityArray: [{ id: 1, name: "只读"},{ id: 2, name: "读写"}],
      treeProps:  {value: "id", label: "label", children: "children"},

      rules: {
        siteIds: [{ required: true, trigger: "change", validator: validateSiteIds }],
      },
    })

    // 查询资产授权站点列表信息
    const queryOrganEmpowerSiteList = () => {
      findOrganEmpowerSiteList({ tenantId: props.tenantId }).then(res => {
        // let defaultSiteKeys = [];
        let siteArray = res.data ? res.data : [];
        // for (let i = 0; i < siteArray.length; i++) defaultSiteKeys.push(siteArray[i].id);
        that.siteArray = getDataListFun(siteArray,true,false,"siteName");
        // that.formDialog.siteIds = defaultSiteKeys;
      })
    }

    // 查询资产授权站点列表信息
    const queryTenantOrganSiteList = () => {
      findTenantOrganSiteList({ tenantId: props.tenantId,organId: props.organId }).then(res => {
        let siteArray = res.data ? res.data : [];
        that.site_all_list = JSON.parse(JSON.stringify(siteArray));
        that.siteArray = getDataListFun(siteArray,true,false,"siteName");
      })
    }

    const formDialogRef = ref(null);
    const saveDialog = () => {
      formDialogRef.value.validate((valid) => {
        if (valid) {
          that.listLoading = true;
          let organEmpowerInfoStr = [];
          let form_dialog = JSON.parse(JSON.stringify(that.formDialog));

          if(form_dialog.siteIds && form_dialog.siteIds.length){
            for(let i = 0;i < form_dialog.siteIds.length;i++){
              if( form_dialog.siteIds[i] !== "alllist"){
                let findItem = that.site_all_list.find(item => item.id === form_dialog.siteIds[i]);
                organEmpowerInfoStr.push({
                  organId: props.organId,
                  tenantId: props.tenantId,
                  siteId: form_dialog.siteIds[i],
                  authority: props.sourceType === 1 ? form_dialog.authority : findItem.authority,
                })
              }
            }
          }

          if(!organEmpowerInfoStr.length){
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
            that.dialog_visible = false;
            return
          }

          batchSaveOrganEmpower({ organEmpowerInfoStr: organEmpowerInfoStr }).then(()=>{
            emit("changeEvent");
            that.dialog_visible = false;
            ElMessage({ type: "success", showClose: true, message: "操作成功！" });
          }).catch(()=>{
            that.listLoading = false;
          })
        }
      })


    }

    const watchVisible = watch([() => props.isVisible], ([newVisible]) => {
      that.dialog_visible = newVisible;
    });

    const watchDialogVisible = watch([() => that.dialog_visible], ([newDialogVisible]) => {
      emit("update:isVisible", newDialogVisible);
    });

    onMounted(() => {
      props.sourceType === 1 ? queryOrganEmpowerSiteList() : queryTenantOrganSiteList();
    })

    return { ...toRefs(that),watchVisible,watchDialogVisible,saveDialog, queryOrganEmpowerSiteList, queryTenantOrganSiteList, formDialogRef }
  }
})
</script>

<style scoped>

</style>
