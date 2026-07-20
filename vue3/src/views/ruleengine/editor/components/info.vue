<template>
  <el-dialog :title="$t('ruleengine.editor.components.info.807357-0')" v-model="dialogVisible" width="50%">
    <el-alert
      :title="$t('ruleengine.editor.components.info.807357-1')"
      type="warning"
      :description="$t('ruleengine.editor.components.info.807357-2')"
      show-icon
      :close-text="$t('ruleengine.editor.components.info.807357-3')"
    ></el-alert>
    <MonacoEditor style="margin-top: 5px; height: 500px" :value="flowJsonData" :language="'json'" :readOnly="true" />
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const props = defineProps<{
  data: any;
}>();

const dialogVisible = ref(false);
const flowJsonData = ref('');

const init = () => {
  dialogVisible.value = true;
  flowJsonData.value = JSON.stringify(props.data, null, 4);
};

defineExpose({ init });
</script>
