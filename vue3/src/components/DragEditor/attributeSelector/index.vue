<template>
  <el-dialog
    :title="$t('components.DragEditor.attributeSelector.756540-0')"
    v-model="visible"
    width="800px"
    @open="handleOpen"
    @opened="handleOpened"
  >
    <div class="content">
      <el-table
        style="width: 100%"
        ref="multipleTable"
        v-loading="loading"
        :border="false"
        :data="tableData"
        tooltip-effect="dark"
        size="small"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column :label="$t('components.DragEditor.attributeSelector.756540-1')" min-width="200">
          <template #default="scope">{{ scope.row.templateName }}</template>
        </el-table-column>
        <el-table-column
          prop="identifier"
          :label="$t('components.DragEditor.attributeSelector.756540-2')"
          min-width="150"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          :label="$t('components.DragEditor.attributeSelector.756540-3')"
          align="center"
          prop="datatype"
          width="90"
        >
          <template #default="scope">
            <dict-tag :options="dict.type.iot_data_type" :value="scope.row.datatype" />
          </template>
        </el-table-column>
      </el-table>
    </div>
    <template #footer>
      <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
      <el-button type="primary" @click="handleSave">{{ $t('confirm') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import type { ElTable } from 'element-plus';
import { listModel } from '@/api/iot/model';
import { useDragEditorStore } from '@/stores/modules/dragEditor';
import { useDict } from '@/utils/dict';

defineOptions({ name: 'FeatureSelection' });

const props = withDefaults(
  defineProps<{
    data: { show: boolean };
    type?: string;
  }>(),
  { type: 'textDisplay1Style' }
);

const route = useRoute();
const dragStore = useDragEditorStore();
const { dict } = useDict('iot_data_type');
const multipleTable = ref<InstanceType<typeof ElTable>>();
const loading = ref(false);
const tableData = ref<any[]>([]);
const mulSelectModel = ref<any[]>([]);
const currentproperties = computed(() => dragStore.currentproperties);
const visible = computed({
  get: () => !!props.data?.show,
  set: (val: boolean) => {
    if (props.data) props.data.show = val;
  },
});

async function getList() {
  loading.value = true;
  tableData.value = [];
  const params = {
    productId: route.query.id || dragStore.id,
    pageNum: 1,
    pageSize: 999,
  };

  try {
    const res: any = await listModel(params);
    if (res.code !== 200) return;
    const rows = res.rows || [];
    tableData.value = rows
      .map(formatModelRow)
      .filter(Boolean)
      .filter((item) => {
        switch (props.type) {
          case 'textDisplay1Style':
            return item.datatype === 'string';
          case 'btnControl1Style':
            return item.datatype === 'bool';
          case 'numControl1Style':
            return isNumberDatatype(item.datatype) && item.isReadonly === 0 && item.isChart === 0;
          case 'historicalDataGaugeStyle':
            return item.isChart === 1;
          case 'mulStatusControl1Style':
            return item.datatype === 'enum';
          case 'numDisplay1Style':
            return isNumberDatatype(item.datatype) && item.isReadonly === 1 && item.isChart === 0;
          default:
            return true;
        }
      });
  } finally {
    loading.value = false;
  }
}

function formatModelRow(item: any) {
  const specs = parseSpecs(item.specs);
  const datatype = item.datatype || specs.type;
  const existingAttribute = getExistingAttribute(item.modelId);
  const baseData = {
    templateId: item.modelId,
    templateName: existingAttribute?.templateName || item.modelName,
    isChart: normalizeFlag(item.isChart),
    isReadonly: normalizeFlag(item.isReadonly),
    identifier: item.identifier,
    datatype,
    icon: existingAttribute?.icon || 'Edit',
  };

  switch (datatype) {
    case 'string':
      return { ...baseData, maxLength: specs.maxLength };
    case 'enum':
      return {
        ...baseData,
        enumList: specs.enumList?.length ? specs.enumList : [{ value: '', text: '' }],
      };
    case 'integer':
    case 'decimal':
      return { ...baseData, min: specs.min, max: specs.max, unit: specs.unit };
    case 'bool':
      return { ...baseData, trueText: specs.trueText, falseText: specs.falseText };
    default:
      return baseData;
  }
}

function getExistingAttribute(templateId: string | number) {
  const attributes = currentproperties.value?.setConfig?.attributes || [];
  return attributes.find((item: any) => String(item.templateId) === String(templateId));
}

function parseSpecs(specs: any) {
  if (!specs) return {};
  if (typeof specs === 'object') return specs;
  try {
    return JSON.parse(specs);
  } catch {
    return {};
  }
}

function normalizeFlag(value: any) {
  return Number(value ?? 0);
}

function isNumberDatatype(datatype: string) {
  return datatype === 'integer' || datatype === 'decimal';
}

function handleOpen() {
  nextTick(() => {
    multipleTable.value?.clearSelection();
    mulSelectModel.value = [];
  });
}

function handleOpened() {
  restoreSelection();
}

function restoreSelection() {
  const setConfig = currentproperties.value?.setConfig;
  if (!setConfig?.attributes?.length) return;
  tableData.value.forEach((row) => {
    const selected = setConfig.attributes.some((item: any) => String(item.templateId) === String(row.templateId));
    if (selected) {
      nextTick(() => multipleTable.value?.toggleRowSelection(row, true));
    }
  });
}

function handleSelectionChange(val: any[]) {
  mulSelectModel.value = val;
}

function handleSave() {
  const setConfig = currentproperties.value?.setConfig;
  if (setConfig) {
    setConfig.attributes = mulSelectModel.value;
  }
  nextTick(() => {
    visible.value = false;
  });
}

watch(
  () => props.data?.show,
  async (show) => {
    if (show) {
      await getList();
      await nextTick();
      restoreSelection();
    }
  }
);
</script>

<style scoped lang="scss">
.content {
  max-height: 500px;
  overflow: auto;
}
</style>
