<template>
  <div class="line-wrap">
    <el-form :model="curNode" label-width="68px" size="small">
      <el-form-item :label="$t('ruleengine.editor.index.807357-18')">
        <el-switch v-model="curNode.data.debug" :active-value="1" :inactive-value="0"></el-switch>
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
      <el-form-item :label="$t('ruleengine.editor.index.807357-21')">
        <el-input
          v-model="curNode.data.customId"
          size="small"
          :placeholder="$t('scene.index.670805-77')"
          style="margin-top: 3px"
        >
          <template #prepend>{{ $t('scene.index.670805-78') }}</template>
        </el-input>
        <monaco-editor
          ref="codeEditorRef"
          height="300px"
          :language="'json'"
          :value="editorValue"
          @change="handleEditorChange"
          width="100%"
          style="margin-top: 10px"
        />
      </el-form-item>
    </el-form>
    <!-- 调试日志组件 -->
    <debug-log :log-list="curNode.data.debugLog" ref="debugLogRef"></debug-log>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
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

const editorValue = computed({
  get() {
    return curNode.value.data.customValue || '';
  },
  set(value: string) {
    curNode.value.data.customValue = value;
  },
});

// 数据处理编辑器
const handleEditorChange = (data: any) => {
  editorValue.value = data;
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
.line-wrap {
  width: 100%;
  padding: 0 10px;
}
</style>
