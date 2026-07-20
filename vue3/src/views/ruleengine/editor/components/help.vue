<template>
  <el-dialog
    :title="$t('ruleengine.editor.components.help.807357-0')"
    v-model="dialogVisible"
    width="70%"
    class="flowHelp"
  >
    <el-tabs tab-position="left">
      <el-tab-pane :label="$t('ruleengine.editor.components.help.807357-1')">
        <el-divider content-position="left">{{ $t('ruleengine.editor.components.help.807357-1') }}</el-divider>
        <div>{{ $t('ruleengine.editor.components.help.807357-2') }}</div>
      </el-tab-pane>
      <el-tab-pane :label="$t('ruleengine.editor.components.help.807357-3')">
        <el-divider content-position="left">{{ $t('ruleengine.editor.components.help.807357-4') }}</el-divider>
        <div>{{ $t('ruleengine.editor.components.help.807357-5') }}</div>
        <el-divider content-position="left">{{ $t('ruleengine.editor.components.help.807357-6') }}</el-divider>
        <pre>{{ $t('ruleengine.editor.components.help.807357-7') }}</pre>
      </el-tab-pane>
      <el-tab-pane :label="$t('ruleengine.editor.components.help.807357-8')">
        <el-divider content-position="left">{{ $t('ruleengine.editor.components.help.807357-8') }}</el-divider>
        <div>{{ $t('ruleengine.editor.components.help.807357-9') }}</div>
      </el-tab-pane>
      <el-tab-pane :label="$t('ruleengine.editor.components.help.807357-10')">
        <el-divider content-position="left">{{ $t('ruleengine.editor.components.help.807357-10') }}</el-divider>
        <div>{{ $t('ruleengine.editor.components.help.807357-11') }}</div>
      </el-tab-pane>
      <el-tab-pane :label="$t('ruleengine.editor.components.help.807357-12')">
        <el-divider content-position="left">{{ $t('ruleengine.editor.components.help.807357-12') }}</el-divider>
        <div>{{ $t('ruleengine.editor.components.help.807357-13') }}</div>
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const dialogVisible = ref(false);

const init = () => {
  dialogVisible.value = true;
};

defineExpose({ init });
</script>

<style lang="scss">
.flowHelp {
  height: 80%;
}
</style>
