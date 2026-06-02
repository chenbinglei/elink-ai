<template>
  <div class="app-container-right">

    <div ref="headerFormRef" class="header-form">
      <el-form :model="formInline" inline @submit.prevent>
        <el-form-item>
          <el-input v-model="formInline.keyword" placeholder="请输入关键词">
            <template #suffix>
              <div class="pointer flex-jc-ai-center" @click="listArray('resetPage')">
                <el-icon><Search /></el-icon>
              </div>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item style="float: right">
          <el-button :disabled="isAddButtonClick" :icon="CirclePlus" class="whiteFontButtons" @click="clickAddButFun">新增关联方</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="tableContent" v-resize="setTableMaxHeight">
      <el-table v-loading="listLoading" :data="list" :max-height="tableMaxHeight">
        <el-table-column label="序号" type="index" width="80" fixed="left"></el-table-column>
        <el-table-column label="关联方企业名称" show-overflow-tooltip fixed="left">
          <template #default="{ row }">{{ $filters.moreData(row.tenantName)}}</template>
        </el-table-column>
        <el-table-column label="关联方类型" show-overflow-tooltip>
          <template #default="{ row }">{{ $filters.moreData(findAffiliateTypesFun(row.affiliateTypes)) }}</template>
        </el-table-column>
<!--        <el-table-column label="资产权限" width="120">-->
<!--          <template #default="{ row }">{{ $filters.authorityType(row.authorityType)}}</template>-->
<!--        </el-table-column>-->
        <el-table-column label="联系人">
          <template #default="{ row }">{{ $filters.moreData(row.contactsName)}}</template>
        </el-table-column>
        <el-table-column label="联系电话">
          <template #default="{ row }">{{ $filters.moreData(row.contactsPhone)}}</template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="{ row }">
            <el-link :underline="false" @click="clickOperateButFun(1, row)">编辑</el-link>
            <template v-if="isTableDeleteButShowFun(row)">
              <span class="split_line">|</span>
              <el-link :underline="false" type="danger" @click="clickOperateButFun(2, row)">删除</el-link>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <AddRelatedPartiesDialog v-if="addRelatedPartiesVisible" v-model:isVisible="addRelatedPartiesVisible" :titleName="titleName" :activeEditInfo="activeEditInfo" @changeEvent="listArray('resetPage')" />
  </div>
</template>
<script>
import {operateButtonIsClick} from "@/utils";
import {ElMessage, ElMessageBox} from "element-plus";
import {CirclePlus, Search} from "@element-plus/icons-vue";
import {computed, defineComponent, onMounted, reactive, ref, toRefs} from "vue";
import AddRelatedPartiesDialog from "./RelatedPartiesManage/AddRelatedPartiesDialog.vue";
import {deleteAffiliatesInfoById, findAffiliatesListBySiteId} from "@/api/siteCenter/stationDetails";

export default defineComponent({
  name: "RelatedPartiesManage",
  props: {
    contentMaxHeight: {
      type: Number,
      default: 520
    },
    siteRecordsId: {
      type: [String, Number],
      default: ""
    }
  },
  components:{AddRelatedPartiesDialog, Search,},
  setup(props) {

    const isAddButtonClick = computed(() => {
      return operateButtonIsClick('/device/siteInfo/saveOrUpdateAffiliatesInfo')
    })

    const that = reactive({
      CirclePlus,
      formInline: { keywordType: 1},
      affiliateTypesArray: [{id: 1, name: "用能单位"}, {id: 2, name: "产权单位"}, {id: 3, name: "运营单位"}, {id: 4, name: "建设单位"}, {id: 5, name: "供电单位"}],

      list: [],
      listLoading: false,
      tableMaxHeight: 300,

      activeEditInfo: {},
      titleName: "添加关联方",
      addRelatedPartiesVisible: false,
    })

    const listArray = () => {
      that.listLoading = true;
      findAffiliatesListBySiteId({siteId: props.siteRecordsId, ...that.formInline}).then(res => {
        that.list = res.data ? res.data : [];
        that.listLoading = false;
      }).catch((error) => {
        that.listLoading = false;
        if (error && error.code === 88886)return
        that.list = [];
      })
    }

    const clickAddButFun = () => {
      that.titleName = "添加关联方";
      that.activeEditInfo = {siteId: props.siteRecordsId};
      that.addRelatedPartiesVisible = true;
    }

    const clickOperateButFun = (index, row) => {
      if(index === 1){
        that.titleName = "编辑关联方";
        let activeEditInfo = JSON.parse(JSON.stringify(row));
        if(activeEditInfo.affiliateTypes){
          activeEditInfo.affiliateTypes = activeEditInfo.affiliateTypes.split(',');
          for(let i = 0;i < activeEditInfo.affiliateTypes.length;i++){
            activeEditInfo.affiliateTypes[i] = Number(activeEditInfo.affiliateTypes[i]);
          }
        }
        that.activeEditInfo = JSON.parse(JSON.stringify(activeEditInfo));
        that.addRelatedPartiesVisible = true;
      }

      if(index === 2){
        ElMessageBox.confirm(`您确定要删除当前关联方（<span class="deleteName">${row.tenantName}</span>）吗？`, "删除提示", {
          customClass: "deleteMsgBoxClass", dangerouslyUseHTMLString: true, confirmButtonText: '确定',
          cancelButtonText: '取消', type: 'warning',closeOnClickModal: false,
          beforeClose: (action, instance, done)=>{
            if (action === 'confirm') {
              instance.confirmButtonLoading = true;
              instance.confirmButtonText = '正在删除...';
              deleteAffiliatesInfoById({ id: row.id }).then(()=> {
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
          listArray("resetPage");
          ElMessage({ type:"success",showClose: true,message:"操作成功！" });
        }).catch(() => {
          console.log("取消删除");
        });

      }
    }

    const findAffiliateTypesFun = (affiliateTypes)=>{
      let affiliateTypesText = "";
      if(affiliateTypes){
        let affiliateTypesArray = affiliateTypes.split(',');
        for(let i = 0;i < affiliateTypesArray.length;i++){
          let findItem = that.affiliateTypesArray.find(item => item.id === Number(affiliateTypesArray[i]));
          if (findItem) affiliateTypesArray[i] = findItem.name;
        }
        affiliateTypesText = affiliateTypesArray.join('，');
      }
      return affiliateTypesText
    }

    const isTableDeleteButShowFun = (row)=>{
      let tableDeleteButShow = true;
      if(row.affiliateTypes.indexOf(2) !== -1 || row.affiliateTypes.indexOf(3) !== -1) tableDeleteButShow = false;
      return tableDeleteButShow
    }

    // 初始化表格高度
    const headerFormRef = ref(null);
    const setTableMaxHeight = () => {
      let headerFormHeight = headerFormRef.value.offsetHeight;
      that.tableMaxHeight = props.contentMaxHeight - headerFormHeight;
    }

    onMounted(()=>{
      listArray();
    })

    return {...toRefs(that),headerFormRef,setTableMaxHeight,isAddButtonClick,listArray,clickOperateButFun,clickAddButFun, findAffiliateTypesFun, isTableDeleteButShowFun }
  }
})

</script>
<style lang="scss" scoped>
.app-container-right{
  padding-top: 12px;
  box-sizing: border-box;

  .header-form{
    padding: 0;
  }
}
</style>