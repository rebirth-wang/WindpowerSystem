<template>
  <div class="flow-delay">
    <el-form :model="curNode.data" label-width="68px" size="small">
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
      <el-form-item :label="$t('ruleengine.editor.components.form.flow-delay.807357-0')">
        <div class="cell">
          <el-input
            style="width: 180px"
            v-model="curNode.data.delay"
            :placeholder="$t('ruleengine.editor.components.form.flow-delay.807357-1')"
            type="number"
          ></el-input>
          <el-select style="width: 100%" v-model="curNode.data.delayType" :placeholder="$t('pleaseSelect')">
            <el-option
              v-for="dict in rule_visualization_time_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </div>
      </el-form-item>
    </el-form>
    <!-- 调试日志组件 -->
    <debug-log :log-list="curNode.data.debugLog" ref="debugLogRef"></debug-log>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { useRuleEditorStore } from '@/stores/modules/ruleEditor';
import { storeToRefs } from 'pinia';
import { useDict } from '@/utils/dict/useDict';
import { View } from '@element-plus/icons-vue';
import debugLog from '../debug-log.vue';

const { t } = useI18n();
const route = useRoute();
const ruleEditorStore = useRuleEditorStore();
const { curNode } = storeToRefs(ruleEditorStore);
const { rule_visualization_time_type } = useDict('rule_visualization_time_type');

const debugLogRef = ref();

// 查看日志
const showDebugLog = () => {
  debugLogRef.value.openLog = true;
  const id = Number(route.query.id);
  const curNodeId = curNode.value.id;
  debugLogRef.value.handleViewLog(id, curNodeId);
};
</script>

<style lang="scss" scoped>
.flow-delay {
  width: 100%;
  padding: 0 10px;

  .cell {
    display: flex;
    flex-direction: row;
    gap: 5px;
  }
}
</style>
