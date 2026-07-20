<template>
  <el-dialog
    :title="$t('scada.topo.components.propertyBar.variateDrawer.842076-25')"
    v-model="visibleModel"
    width="800px"
    append-to-body
    :before-close="handleClose"
  >
    <el-alert
      :title="
        variables && variables.length
          ? `${$t('scada.topo.components.propertyBar.variateDrawer.842076-26')} ${variables
              .map((v) => v.modelName || v.identifier)
              .join('，')} ${valueType || ''}`
          : $t('scada.topo.components.propertyBar.variateDrawer.842076-27')
      "
      :type="variables && variables.length ? 'info' : 'warning'"
      show-icon
      :closable="false"
      class="mb-10"
    />

    <div style="text-align: right; margin-bottom: 10px">
      <el-button type="primary" :icon="Plus" @click="addDataMapping" style="margin-right: 5px">
        {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-28') }}
      </el-button>
      <el-button :icon="Refresh" @click="resetDataMappings">
        {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-37') }}
      </el-button>
    </div>

    <el-table :data="localModelMaps" max-height="400" :border="false">
      <el-table-column
        v-if="hasMultipleVars"
        :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-44')"
        min-width="120"
      >
        <template #default="{ row }">
          <el-select v-model="row.identifier" :placeholder="$t('topo.components.propertyBar.index.038495-195')">
            <el-option
              v-for="v in variables"
              :key="v.identifier"
              :label="v.modelName || v.identifier"
              :value="v.identifier"
            />
          </el-select>
        </template>
      </el-table-column>

      <el-table-column :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-29')" min-width="90">
        <template #default="{ row }">
          <el-select v-model="row.operator">
            <el-option v-for="op in operators" :key="op" :label="op" :value="op" />
          </el-select>
        </template>
      </el-table-column>

      <el-table-column :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-30')" min-width="120">
        <template #default="{ row }">
          <el-input
            v-model="row.comparisonValue"
            :type="getInputTypeByValueType(getValueTypeForRow(row))"
            :placeholder="$t('scada.topo.components.propertyBar.variateDrawer.842076-31')"
          />
        </template>
      </el-table-column>

      <el-table-column :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-32')" min-width="120">
        <template #default="{ row }">
          <el-input
            v-model="row.actionValue"
            :placeholder="$t('scada.topo.components.propertyBar.variateDrawer.842076-33')"
          />
        </template>
      </el-table-column>

      <el-table-column :label="$t('topo.components.propertyBar.index.038495-360')" width="100" align="center">
        <template #default="{ $index }">
          <el-button style="color: #f56c6c" type="text" icon="el-icon-delete" @click="removeDataMapping($index)">
            {{ $t('topo.topoToolbox.250932-9') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button type="primary" @click="confirm">
        {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-35') }}
      </el-button>
      <el-button @click="handleClose">
        {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-36') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { Plus, Refresh } from '@element-plus/icons-vue';

type VarItem = {
  identifier: string;
  modelName?: string;
  valueType?: string;
};

type MapRow = {
  identifier: string;
  operator: string;
  comparisonValue: string | number;
  actionValue: string | number;
};

const props = defineProps<{
  visible: boolean;
  modelName?: string;
  valueType?: string;
  modelMaps?: MapRow[];
  variables?: VarItem[];
}>();

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'save', value: MapRow[]): void;
}>();

const operators = ['=', '>', '<', '>=', '<=', '!='];
const localModelMaps = ref<MapRow[]>([]);

const variables = computed(() => props.variables || []);
const hasMultipleVars = computed(() => variables.value.length > 1);

const visibleModel = computed({
  get: () => props.visible,
  set: (value: boolean) => emit('update:visible', value),
});

watch(
  () => props.modelMaps,
  (val) => {
    const defaultIdentifier = variables.value.length ? variables.value[0].identifier : '';
    let maps: MapRow[] =
      val && val.length > 0
        ? JSON.parse(JSON.stringify(val))
        : [{ identifier: defaultIdentifier, operator: '=', comparisonValue: '', actionValue: '' }];

    if (maps.length) {
      maps.forEach((row) => {
        if (!Object.prototype.hasOwnProperty.call(row, 'identifier') || !row.identifier) {
          row.identifier = defaultIdentifier;
        }
      });
    }
    localModelMaps.value = maps;
  },
  { immediate: true, deep: true }
);

watch(
  () => props.variables,
  (val) => {
    const defaultIdentifier = val && val.length ? val[0].identifier : '';
    const vars = Array.isArray(val) ? val : [];
    if (localModelMaps.value && localModelMaps.value.length) {
      localModelMaps.value.forEach((row) => {
        if (!Object.prototype.hasOwnProperty.call(row, 'identifier')) {
          row.identifier = defaultIdentifier;
          return;
        }
        if (!row.identifier || !vars.some((v) => v.identifier === row.identifier)) {
          row.identifier = defaultIdentifier;
        }
      });
    }
  },
  { immediate: true }
);

function handleClose() {
  emit('update:visible', false);
}

function addDataMapping() {
  localModelMaps.value.push({
    identifier: variables.value.length ? variables.value[0].identifier : '',
    operator: '=',
    comparisonValue: '',
    actionValue: '',
  });
}

function resetDataMappings() {
  localModelMaps.value = [
    {
      identifier: variables.value.length ? variables.value[0].identifier : '',
      operator: '=',
      comparisonValue: '',
      actionValue: '',
    },
  ];
}

function removeDataMapping(index: number) {
  localModelMaps.value.splice(index, 1);
  if (localModelMaps.value.length === 0) {
    addDataMapping();
  }
}

function getValueTypeForRow(row: MapRow) {
  if (hasMultipleVars.value) {
    const v = variables.value.find((x) => x.identifier === row.identifier);
    return v ? v.valueType : props.valueType;
  }
  return props.valueType;
}

function confirm() {
  emit('save', JSON.parse(JSON.stringify(localModelMaps.value)));
  handleClose();
}

function getInputTypeByValueType(type?: string) {
  if (!type) return 'text';
  const num = ['integer', 'long', 'double', 'float', 'decimal', 'number'];
  return num.includes(type.toLowerCase()) ? 'number' : 'text';
}
</script>

<style lang="scss" scoped>
.mb-10 {
  margin-bottom: 10px;
}
</style>
