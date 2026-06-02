<template>
  <div class="strategyParameterConfig">
    <template v-for="(item, index) in configMenuArray" :key="index">
      <title-view :title="item.ch_name" :id="item.id">
        <template #headerRight>
          <!--          <div class="pointer" style="width: 100%;" v-if="item.desc">-->
          <!--            <el-icon size="18" color="#FFFFFF80" @click.stop="clickLookDescFun(item)"><Warning /></el-icon>-->
          <!--          </div>-->
          <div class="" v-if="item.desc" style="width: 100%;">
            <el-tooltip effect="dark" :content="item.desc" placement="top-start">
              <el-icon class="pointer" size="16" color="#FFFFFF80">
                <Warning />
              </el-icon>
            </el-tooltip>
          </div>
        </template>
        <template #content>
          <div class="content_body_form">
            <StrategyParameterConfig v-if="isContinueTheCycleFun(item)" :configMenuArray="item.body" />
            <template v-else>
              <template v-if="item.type === 'jsonObject'">
                <el-form ref="formDialogRef" label-width="auto" label-position="right" size="large">
                  <template v-for="(formItem, formIndex) in item.body" :key="formIndex">
                    <el-form-item :required="formItem.Configuration">
                      <template #label>
                        <div class="flex-ai-center" :id="formItem.id">
                          <template v-if="formItem.desc">
                            <!--                            <div class="pointer flex-ai-center" style="margin-right: 6px;" @click.stop.prevent="clickLookDescFun(formItem)">-->
                            <!--                              <el-icon size="16" color="#FFFFFF80"><Warning /></el-icon>-->
                            <!--                            </div>-->
                            <el-tooltip effect="dark" :content="formItem.desc" placement="top-start">
                              <el-icon class="pointer" style="margin-right: 2px;" size="16" color="#FFFFFF80">
                                <Warning />
                              </el-icon>
                            </el-tooltip>
                          </template>
                          <span class="form_item_label">{{ $filters.moreData(formItem.ch_name) }}</span>
                          <span>：</span>
                        </div>
                      </template>
                      <template v-if="formItem.type === 'bool'">
                        <!--                        <el-select v-model="formItem.value" placeholder="请选择" filterable>-->
                        <!--                          <el-option v-for="selectItem in boolArray" :key="selectItem.id" :label="selectItem.name" :value="selectItem.id"></el-option>-->
                        <!--                        </el-select>-->
                        <el-switch v-model="formItem.value"
                          style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949" inline-prompt
                          active-value="true" inactive-value="false" active-text="开" inactive-text="关" />
                      </template>
                      <template
                        v-else-if="formItem.type === 'int' || formItem.type === 'float' || formItem.type === 'double'">
                        <el-input-number v-model="formItem.value" placeholder="请输入" controls-position="right"
                          style="width: 100%;" />
                      </template>
                      <el-input v-else v-model="formItem.value" placeholder="请输入" />
                    </el-form-item>
                  </template>
                </el-form>
              </template>
              <template v-if="item.type === 'jsonArray'">
                <el-table :data="item.body" :max-height="tableMaxHeight" border style="width: 100%">
                  <el-table-column fixed label="序号" type="index" width="60"></el-table-column>
                  <template v-if="item.arrFormat && item.arrFormat.length">
                    <template v-for="(tableItem, tableIndex) in item.arrFormat" :key="tableIndex">
                      <el-table-column align="center" min-width="210">
                        <template #header>
                          <div class="headerCustom flex flex-ai-center">
                            <div class="header_label">{{ tableItem.ch_name }}</div>
                            <template v-if="tableItem.desc">
                              <div style="width: 4px"></div>
                              <el-tooltip effect="dark" :content="tableItem.desc" placement="top-start">
                                <el-icon size="16" color="#FFFFFF80">
                                  <Warning />
                                </el-icon>
                              </el-tooltip>
                            </template>
                          </div>
                        </template>
                        <template #default="{ row }">
                          <template v-if="tableItem.type === 'bool'">
                            <!--                            <el-select v-model="row[tableItem.en_name]" :placeholder="'请选择' + tableItem.ch_name" filterable >-->
                            <!--                              <el-option v-for="seletItem in boolArray" :key="seletItem.id" :label="seletItem.name" :value="seletItem.id"></el-option>-->
                            <!--                            </el-select>-->
                            <el-switch v-model="row[tableItem.en_name]"
                              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949" inline-prompt
                              active-value="true" inactive-value="false" active-text="是" inactive-text="否" />
                          </template>
                          <template v-else-if="tableItem.type === 'time'">
                            <el-time-picker v-model="row[tableItem.en_name]" style="width: 100%;"
                              value-format="HH:mm:ss" :placeholder="'请选择' + tableItem.ch_name" />
                          </template>
                          <template v-else-if="tableItem.type === 'date'">
                            <el-date-picker v-model="row[tableItem.en_name]" style="width: 100%;" type="date"
                              value-format="YYYY-MM-DD" :placeholder="'请选择' + tableItem.ch_name" />
                          </template>
                          <template v-else-if="tableItem.type === 'datetime'">
                            <el-date-picker v-model="row[tableItem.en_name]" type="datetime" style="width: 100%;"
                              value-format="YYYY-MM-DD HH:mm:ss" :placeholder="'请选择' + tableItem.ch_name" />
                          </template>

                          <template
                            v-else-if="tableItem.type === 'int' || tableItem.type === 'float' || tableItem.type === 'double'">
                            <el-input-number v-model="row[tableItem.en_name]" :placeholder="'请选择' + tableItem.ch_name"
                              controls-position="right" style="width: 100%;" />
                          </template>

                          <el-input v-else v-model="row[tableItem.en_name]" :placeholder="'请输入' + tableItem.ch_name" />
                        </template>
                      </el-table-column>
                    </template>
                  </template>
                  <el-table-column align="center" fixed="right" width="60">
                    <template #header>
                      <div class="flex-all flex jc-center ai-center pointer" @click.stop="clickAddPlusFilledFun(item)">
                        <el-icon size="24" color="#079CEB">
                          <CirclePlusFilled />
                        </el-icon>
                      </div>
                    </template>
                    <template #default="{ row, $index }">
                      <div class="pointer" @click.stop="clickRemoveFilledFun(item, $index)">
                        <el-icon size="24" color="#FB393A">
                          <RemoveFilled />
                        </el-icon>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </template>

            </template>
          </div>
        </template>
      </title-view>
    </template>

    <ConfigDescDialog v-if="configDescVisible" v-model:isVisible="configDescVisible" :titleName="titleName"
      :describeContent="describeContent" />
  </div>
</template>

<script>
import { ElMessageBox } from "element-plus";
import ConfigDescDialog from "./ConfigDescDialog.vue";
import { defineComponent, reactive, toRefs } from "vue";
import { CirclePlusFilled, RemoveFilled, Warning } from "@element-plus/icons-vue";

export default defineComponent({
  name: "StrategyParameterConfig",
  components: { ConfigDescDialog, CirclePlusFilled, RemoveFilled, Warning },
  props: {
    configMenuArray: {
      type: Array,
      default: () => []
    }
  },
  setup (props) {
    const that = reactive({
      titleName: "说明",
      tableMaxHeight: 380,
      describeContent: "",
      configDescVisible: false,
      boolArray: [{ id: 'true', name: "是" }, { id: 'false', name: "否" }]
    });

    // 配置文件  jsonArray 添加数据
    const clickAddPlusFilledFun = (item) => {
      // console.log(item);
      let data = {};
      try {
        if (item.arrFormat && item.arrFormat.length) {
          for (let i = 0; i < item.arrFormat.length; i++) {
            if (item.arrFormat[i].en_name && (item.arrFormat[i].default || item.arrFormat[i].default === 0)) {
              data[item.arrFormat[i].en_name] = item.arrFormat[i].default;
            }
          }
        }
      } catch (e) { }

      if (!item.body) item.body = [];
      item.body.push(data);
    };

    // 配置文件  jsonArray 删除数据
    const clickRemoveFilledFun = (item, index) => {
      ElMessageBox.confirm(`确定删除<span class="deleteName">${item.ch_name}</span>的第${index + 1}条数据吗？`, "提示", {
        dangerouslyUseHTMLString: true, confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
        customClass: "deleteMsgBoxClass", showClose: false, closeOnClickModal: false,
      }).then(() => {
        item.body.splice(index, 1);
      }).catch(() => {
        console.log("取消操作！");
      });
    };

    const clickLookDescFun = (data) => {
      that.describeContent = data.desc;
      that.titleName = data.ch_name + "-说明";
      that.configDescVisible = true;
    };

    // 查看配置文件那些显示控制
    const isContinueTheCycleFun = (data) => {
      let isContinueTheCycle = false;
      if (data.type === 'jsonObject') {
        if (data.body && data.body.length) {
          isContinueTheCycle = true;
          let childrenTheCycle = false;
          for (let i = 0; i < data.body.length; i++) {
            if (data.body[i].body && data.body[i].body.length) {
              childrenTheCycle = true;
              break;
            }
          }
          isContinueTheCycle = childrenTheCycle;
        }
      }
      return isContinueTheCycle;
    };

    return { ...toRefs(that), isContinueTheCycleFun, clickAddPlusFilledFun, clickRemoveFilledFun, clickLookDescFun };
  }
});
</script>

<style lang="scss" scoped>
.strategyParameterConfig {
  width: 100%;

  .content_body_form {
    width: 100%;
    padding: 4px 10px;
    box-sizing: border-box;
  }
}
</style>