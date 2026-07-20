<template>
  <div>
    <flow-http-bridge v-if="curNode.data.type == 3"></flow-http-bridge>
    <flow-mqtt-bridge v-if="curNode.data.type == 4"></flow-mqtt-bridge>
    <flow-data-bridge v-if="curNode.data.type == 5"></flow-data-bridge>
  </div>
</template>

<script setup lang="ts">
import { useRuleEditorStore } from '@/stores/modules/ruleEditor';
import { storeToRefs } from 'pinia';
import FlowMqttBridge from './flow-mqtt-bridge.vue';
import FlowHttpBridge from './flow-http-bridge.vue';
import FlowDataBridge from './flow-data-bridge.vue';

const ruleEditorStore = useRuleEditorStore();
const { curNode } = storeToRefs(ruleEditorStore);
</script>
