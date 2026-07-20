<template>
  <el-dialog :title="$t('ruleengine.editor.components.case.807357-0')" v-model="visible" width="45%">
    <el-table
      :data="tableData"
      style="width: 100%"
      :show-header="false"
      :border="false"
      height="400"
      @cell-click="handleCellClick"
    >
      <el-table-column prop="name" :label="$t('ruleengine.editor.components.case.807357-1')">
        <template #default="scope">
          <el-icon><Document /></el-icon>
          <span style="margin-left: 10px">{{ scope.row.name }}</span>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Document } from '@element-plus/icons-vue';

const { t } = useI18n();

const emit = defineEmits(['cell-click']);

const visible = ref(false);
const tableData = ref([
  { id: 'Switch', name: t('ruleengine.editor.components.case.807357-7') },
  { id: 'IfSingle', name: t('ruleengine.editor.components.case.807357-8') },
  { id: 'If', name: t('ruleengine.editor.components.case.807357-9') },
  { id: 'Timer', name: t('ruleengine.editor.components.case.807357-10') },
  { id: 'Custom', name: t('ruleengine.editor.components.case.807357-11') },
  { id: 'Multiple', name: t('ruleengine.editor.components.case.807357-12') },
]);

const init = () => {
  visible.value = true;
};

const handleCellClick = (row: any) => {
  emit('cell-click', row);
};

defineExpose({ init });
</script>
