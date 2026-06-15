<template>
  <div class="companyInfo">
    <CompanyPublicComponent :tenantId="userInfo.tenantId" :isEditCompany="isEditCompany" :sourceType="sourceType"></CompanyPublicComponent>
  </div>
</template>

<script lang="ts">
import { useAppStore } from '@/stores/index';

import {computed, reactive, toRefs} from "vue";
import {CompanyPublicComponent} from "@/views/tenantManagement/component"

export default {
  name: "companyInfo",
  components: {CompanyPublicComponent},
  setup() {

    const appStore = useAppStore();
    const userInfo = computed(() => {
      return appStore.userInfo;
    });

    const that = reactive({
      sourceType: 2,
      isEditCompany: userInfo.value.userRole === 0 || userInfo.value.userRole === 1,
    });

    return {...toRefs(that), userInfo }
  },
}
</script>

<style lang="scss" scoped>
.companyInfo {
  width: 100%;
  height: 100%;
}
</style>
