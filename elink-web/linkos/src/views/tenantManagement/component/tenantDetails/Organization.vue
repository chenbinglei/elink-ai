<template>
  <div class="content_body">
    <HandleMenus :isShowHeader="false" :handleMenuArray="handleMenuArray" @handleMenuEvent="handleMenuEvent"/>

    <div class="content_table_right" v-loading="listLoading">
      <template v-if="handleMenuArray && handleMenuArray.length">
        <el-form ref="formDialogRef" class="form_top" :model="formDialog" :disabled="!isEditCompany" :rules="rules" label-width="110px">
          <el-form-item label="组织名称:" prop="organName">
            <el-input type="text" v-model="formDialog.organName" maxlength="20" show-word-limit placeholder="请输入组织名称"></el-input>
          </el-form-item>
          <el-form-item label="父级组织:" prop="parentId">
            <el-select v-model="formDialog.parentId" clearable placeholder="请选择父级组织" :disabled="!parentId">
              <template v-for="item in allHandleMenuArray" :key="item.id">
                <el-option v-if="item.id !== organId" :label="item.organName" :value="item.id"></el-option>
              </template>
            </el-select>
          </el-form-item>
          <el-form-item label="排序:" prop="sortNumber">
            <el-input-number v-model="formDialog.sortNumber" :disabled="!parentId" placeholder="请输入排序" controls-position="right" style="width: 100%;"></el-input-number>
            <div style="font-size: 12px;color: #666666">排序数字越大越靠前</div>
          </el-form-item>
        </el-form>
        <div class="button_btm" v-if="isEditCompany">
          <el-button type="danger" :disabled="!parentId" @click="clickCompile('delete')">删除组织</el-button>
          <el-button class="whiteFontButtons" @click="clickCompile('save')">保存设置</el-button>
        </div>
      </template>
      <null-data v-else words="暂无组织信息"></null-data>
    </div>

    <!--    新增组织-->
    <AddOrganizeDialog v-if="addOrganizeVisible" v-model:isVisible="addOrganizeVisible" :fatherArr="allHandleMenuArray" :tenantId="tenantId" :parentId="parentId" @changeEvent="getOrganStructure" />
  </div>
</template>

<script>
import {setTreeData, treeToArray} from "@/utils";
import {commonCharName} from "@/utils/validate";
import {ElMessage, ElMessageBox} from "element-plus";
import AddOrganizeDialog from "./Organization/AddOrganizeDialog.vue";
import {onMounted, reactive, ref, toRefs, defineComponent} from "vue";
import {deleteOrganStructureById, findOrganStructureByTenantId, saveOrUpdateOrganStructure} from "@/api/tenantManagement/tenantTabulation";

export default defineComponent({
  name: "Organization",
  components: {AddOrganizeDialog},
  props: {
    // 租户、企业id
    tenantId: {
      type: [String, Number],
      default: ""
    },
    isEditCompany:{
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const formDialogRef = ref(null);

    const validateOrganName = (rule, value, callback) => {
      if (!value || !commonCharName(value)) {
        callback(new Error("请输入正确的组织名称"));
      } else {
        callback();
      }
    };

    const validateParentId = (rule, value, callback) => {
      if(!value){
        callback(new Error("请选择父级组织"));
      } else {
        callback();
      }
    };

    const validateSortNumber = (rule, value, callback) => {
      if(!value && value !== 0){
        callback(new Error("请输入排序"));
      } else {
        callback();
      }
    };

    const that = reactive({
      organId: "", // 组织结构id
      parentId: "",
      formDialog: {},
      listLoading: false,
      handleMenuArray: [],
      allHandleMenuArray: [],
      addOrganizeVisible: false,
      rules: {
        parentId: [{ required: true, trigger: "blur", validator: validateParentId }],
        organName: [{required: true, trigger: "change", validator: validateOrganName}],
        sortNumber: [{ required: true, trigger: "change", validator: validateSortNumber }],
      },
    })

    // 根据租户id查询租户下组织架构信息
    const getOrganStructure = () => {
      that.listLoading = true;
      findOrganStructureByTenantId({ tenantId: props.tenantId }).then(res => {

        let list = treeToArray(res.data ? res.data : []);
        list.forEach(item=>{
          item.name = item.organName;
          item.iconName = !item.parentId ? "icon-company" : "icon-organization";
        })

        list.sort((a, b) => {
          return b.sortNumber - a.sortNumber
        });

        that.handleMenuArray = setTreeData(list);
        that.allHandleMenuArray = JSON.parse(JSON.stringify(list));
        that.listLoading = false;
      }).catch(() => {
        that.listLoading = false;
      })
    }


    const handleMenuEvent = (menuButDate) => {
      // console.log(menuButDate);

      //左侧菜单打开
      if (menuButDate.menuType === "clickTreeNode") {
        that.organId = menuButDate.id;
        that.parentId = menuButDate.parentId;
        let formDialog = {parentId: menuButDate?.parentId, organName: menuButDate.organName, sortNumber: menuButDate.sortNumber}
        that.formDialog = JSON.parse(JSON.stringify(formDialog));
      }
    }

    // 处理组织
    const clickCompile = (companyType) => {
      if (companyType === 'delete') {
        ElMessageBox.confirm(`确定删除（${that.formDialog.organName}）吗？`, "删除提示", {
          dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消',
          customClass: "elMessageBoxWarning", showClose: false, type: 'warning',
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteOrganStructureById({ organId: that.organId }).then(() => {
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
          getOrganStructure();
          ElMessage({type: "success", showClose: true, message: "删除成功！"});
        }).catch(() => {
          console.log("取消删除！");
        });
      }

      if (companyType === 'save') {
        formDialogRef.value.validate((valid) => {
          if (valid) {
            saveOrUpdateOrganStructure({ ...that.formDialog, tenantId: props.tenantId, id: that.organId }).then(() => {
              getOrganStructure();
              ElMessage({type: "success", showClose: true, message: "操作成功！"});
            }).catch(() => {
              that.listLoading = false;
            })
          }
        })
      }
    }

    const clickButtonFun = () => {
      that.addOrganizeVisible = true;
    }

    onMounted(() => {
      getOrganStructure();
    })

    return {...toRefs(that), getOrganStructure, clickButtonFun, handleMenuEvent, clickCompile, formDialogRef}
  }
})
</script>

<style scoped lang="scss">
.content_body{
  height: 100%;
  padding: 16px 12px 0 12px;
  box-sizing: border-box;
  display: flex;

  .content_table_right{
    flex: 1;
    width: 2px;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 12px 16px;
    box-sizing: border-box;

    .el-select{
      width: 100%;
    }

    .button_btm {
      margin-top: 16px;
    }
  }
}
</style>
