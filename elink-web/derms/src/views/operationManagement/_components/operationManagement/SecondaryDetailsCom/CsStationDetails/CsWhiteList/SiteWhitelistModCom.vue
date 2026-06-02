<template>
  <div class="siteWhitelistModCom">
    <el-form :model="formInline" :disabled="authority === 1">
      <el-form-item label="白名单模式：">
        <el-radio-group v-model="formInline.rosterMode" @change="rosterModeChangeFun">
          <template v-for="(item,index) in modeTypeArray" :key="index">
            <el-radio :value="item.id" size="small">{{ item.name }}</el-radio>
          </template>
        </el-radio-group>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {ElMessage, ElMessageBox} from "element-plus";
import {reactive, toRefs, ref, watch, onMounted, defineComponent} from "vue";
import {findRosterModeBySiteId, saveOrUpdateRosterMode} from "@/api/operationManagement/CsStationDetails";

export default defineComponent({
  name: "SiteWhitelistModCom",
  props: {
    siteId: {
      type: [Number, String],
      default: ""
    },
    //权限 1-只读 2-读写
    authority: {
      type: Number,
      default: 1
    },
  },
  setup(props) {
    const that = reactive({
      formInline: {},
      modeTypeArray: [{id: 1, name: "仅白名单用户可用"}, {id: 2, name: "白名单用户免费充电"}],
    });

    // 查询站点白名单模式
    const queryRosterModeBySiteId = () => {
      findRosterModeBySiteId({siteId: props.siteId}).then(res => {
        that.formInline = res.data ? res.data : {};
      });
    };

    const rosterModeChangeFun = (val)=>{
      let highlightText = val === 2 ?  "白名单用户免费充电" : "仅白名单用户可用";
      ElMessageBox.confirm(`确定更改模式为（<span class="deleteName">${ highlightText }</span>）吗？`, "提示", {
        dangerouslyUseHTMLString: true,confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
        customClass: "deleteMsgBoxClass", showClose:false, closeOnClickModal: false,
        beforeClose: (action, instance, done)=>{
          if (action === 'confirm') {
            instance.confirmButtonLoading = true;
            instance.confirmButtonText = '正在更改...';
            saveOrUpdateRosterMode({ ...that.formInline, siteId: props.siteId }).then(()=>{
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
        queryRosterModeBySiteId();
        ElMessage({ type: "success", showClose: true, message: "更改成功！" });
      }).catch(() => {
        queryRosterModeBySiteId();
        console.log("取消删除！");
      });
    };

    onMounted(()=>{
      queryRosterModeBySiteId();
    });

    return {...toRefs(that), queryRosterModeBySiteId, rosterModeChangeFun};
  }
});
</script>

<style lang="scss" scoped>

</style>