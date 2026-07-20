<template>
  <!-- 规则引擎表单子组件聚合入口，仅作导出使用 -->
</template>

<script lang="ts">
import { defineComponent } from 'vue';

/**
 * 规则引擎表单子组件聚合入口
 * 在 vue3 中，各子组件已直接在 nodeForm.vue 中通过 import 按需引入，
 * 本文件保留作为兼容占位，不再负责全局注册。
 */
export { default as FlowDevTrigger } from './flow-dev-trigger.vue';
export { default as FlowDevExecute } from './flow-dev-execute.vue';
export { default as FlowCondition } from './flow-condition.vue';
export { default as FlowDelay } from './flow-delay.vue';
export { default as FlowAlarm } from './flow-alarm.vue';
export { default as FlowLine } from './flow-line.vue';
export { default as FlowComment } from './flow-comment.vue';
export { default as FlowProductTrigger } from './flow-product-trigger.vue';
export { default as FlowProductExecute } from './flow-product-execute.vue';
export { default as FlowScheduledTrigger } from './flow-scheduled-trigger.vue';
export { default as FlowCustomTrigger } from './flow-custom-trigger.vue';
export { default as FlowBridge } from './flow-bridge.vue';
export { default as FlowScript } from './flow-script.vue';

export default defineComponent({
  name: 'RuleEngineForm',
});
</script>
