<template>
	<div class="app-container">
    <div class="app-container-right">

      <TabBorderCard v-model:tabsCardIndex="componentName" :tabsCardArray="tabsArray">
        <template #content>
          <div class="content_body">
            <component :is="componentName" :key="componentName"></component>
          </div>
        </template>
      </TabBorderCard>

    </div>
	</div>
</template>

<script>
	import { reactive, toRefs } from "vue";
  import {Account,AmendPassword} from "./components";

	export default {
		name: "personalCenter",
    components: { Account, AmendPassword },
		setup() {
			const that = reactive({
        componentName: "Account",
        tabsArray: [
          {id: "Account", name: "个人资料"},
          {id: "AmendPassword", name: "密码修改"}
        ]
			})

			return { ...toRefs(that) };
		},
	};
</script>

<style scoped lang="scss">
.app-container-right{
  display: flex;
  align-items: center;

  :deep(.tabs_border_card){
    width: 50vw;
    min-height: 380px;

    .content_body{
      height: 100%;
      padding: 32px 16px;
      box-sizing: border-box;
    }
  }
}
</style>
