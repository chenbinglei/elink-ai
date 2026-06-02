<template>
  <div class="tenant_details">
    <CompanyPublicComponent :tenantId="tenantId" isEditCompany></CompanyPublicComponent>
  </div>
</template>

<script>
import {useRoute} from "vue-router";
import {reactive, toRefs} from "vue";
import {CompanyPublicComponent} from "@/views/tenantManagement/component"

export default {
  name: "tenantDetails",
  components: {CompanyPublicComponent},
  setup() {

    const route = useRoute();
    const that = reactive({
      tenantId: route.query.id,
    });

    return {...toRefs(that)}
  },
}
</script>

<style lang="scss" scoped>
.tenant_details{
  width: 100%;
  height: 100%;
}
</style>
