<script setup lang="js">
import { StartDisAndChargingDialog, EndChargingDialog, PowerControlDialog } from "@/views/operationManagement/_components";

const emit = defineEmits(['close']);
const props = defineProps({
    title: {
        type: String,
        default: ''
    },
    opType: {
        type: String,
        default: ''
    },
    gunInfo: {
        type: Object,
        required: true
    }
});
const onClose = () => {
    emit("close");
}
</script>

<template>
    <StartDisAndChargingDialog v-if="['startDischarge', 'startCharge'].includes(opType)"
        :isVisible="['startDischarge', 'startCharge'].includes(opType)" :operateType="opType === 'startCharge' ? 1 : 2"
        :returnDataInfo="gunInfo" @close="onClose" />
    <EndChargingDialog v-else-if="opType === 'stopOperation'" :isVisible="opType === 'stopOperation'"
        :returnDataInfo="gunInfo" @close="onClose" />
    <PowerControlDialog v-else-if="opType === 'powerControl'" :isVisible="opType === 'powerControl'"
        :returnDataInfo="gunInfo" @close="onClose" />

</template>
<style lang="scss" scoped>
.selected {
    position: relative;
    background: linear-gradient(270deg, rgba(52, 232, 0, 0.2) 0%, rgba(52, 232, 0, 0.06) 100%);

    &::after {
        content: '\e6fa';
        width: 32px;
        height: 100%;
        position: absolute;
        right: 0;
        top: 0;
        color: white;
        background: #0071A4;
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 10;
    }
}

.dialog-btn {
    width: 84px;
    height: 32px;
    background: rgba(0, 45, 57, 0.7);
    box-shadow: 0px 3px 6px 1px rgba(0, 0, 0, 0.16);
    border-radius: 4px 4px 4px 4px;
    border: 1px solid #00CCFF;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;

    &:hover {
        box-shadow: 0px 3px 6px 1px rgba(0, 0, 0, 0.16), inset 0px 0px 10px 1px #03A5FF;
    }
}
</style>