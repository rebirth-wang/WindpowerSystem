<template>
  <div class="flow-script-wrap">
    <el-form :model="curNode.data" label-width="68px" size="small">
      <el-form-item :label="$t('ruleengine.editor.index.807357-18')">
        <el-switch v-model="curNode.data.debug" :active-value="1" :inactive-value="0" />
        <el-button
          type="primary"
          link
          size="small"
          @click="showDebugLog"
          style="color: #486ff2; margin-left: 20px; height: 26px; font-size: 13px"
        >
          <el-icon><View /></el-icon>
          {{ $t('look') }}
        </el-button>
      </el-form-item>
      <el-form-item :label="$t('script.349087-4')" prop="scriptName">
        <el-input v-model="curNode.data.scriptName" :placeholder="$t('script.349087-3')" style="width: 100%" />
      </el-form-item>

      <el-form-item :label="$t('script.349087-13')" prop="enable">
        <el-switch v-model="curNode.data.enable" :active-value="1" :inactive-value="0" />
      </el-form-item>
      <el-form-item
        :label="$t('ruleengine.editor.components.form.flow-script.807357-0')"
        prop="scriptData"
      ></el-form-item>
      <div style="padding: 0px 10px">
        <monaco-editor
          ref="codeEditorRef"
          height="300px"
          :language="'groovy'"
          :value="curNode.data.scriptData"
          @change="handleEditorChange"
          width="100%"
          style="margin-top: 10px"
        />
      </div>
    </el-form>
    <!-- 调试日志组件 -->
    <debug-log :log-list="curNode.data.debugLog" ref="debugLogRef"></debug-log>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { useRuleEditorStore } from '@/stores/modules/ruleEditor';
import { storeToRefs } from 'pinia';
import { View } from '@element-plus/icons-vue';
import debugLog from '../debug-log.vue';

const { t } = useI18n();
const route = useRoute();
const ruleEditorStore = useRuleEditorStore();
const { curNode } = storeToRefs(ruleEditorStore);

const codeEditorRef = ref();
const debugLogRef = ref();

onMounted(() => {
  curNode.value.data.scriptId = curNode.value.id;
});

// 数据处理编辑器
const handleEditorChange = (data: any) => {
  curNode.value.data.scriptData = data;
};

// 查看日志
const showDebugLog = () => {
  debugLogRef.value.openLog = true;
  const id = Number(route.query.id);
  const curNodeId = curNode.value.id;
  debugLogRef.value.handleViewLog(id, curNodeId);
};
</script>

<style lang="scss" scoped>
.flow-script-wrap {
  width: 100%;
  padding: 0 10px;
}
</style>
