<template>
  <div>
    <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="48px">
      <el-form-item prop="templateName">
        <el-input
          v-model="queryParams.templateName"
          :placeholder="$t('product.product-select-template.318012-1')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item prop="type">
        <el-select
          v-model="queryParams.type"
          :placeholder="$t('product.product-select-template.318012-3')"
          clearable
          style="width: 180px"
        >
          <el-option v-for="dict in iot_things_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item prop="isSys">
        <el-select
          v-model="queryParams.isSys"
          :placeholder="$t('template.index.891112-124')"
          clearable
          style="width: 180px"
          @keyup.enter="handleQuery"
        >
          <el-option v-for="dict in system_type_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">
          {{ $t('product.product-select-template.318012-4') }}
        </el-button>
        <el-button :icon="Refresh" @click="resetQuery">
          {{ $t('product.product-select-template.318012-5') }}
        </el-button>
      </el-form-item>
    </el-form>
    <el-table
      v-loading="loading"
      :data="templateList"
      @selection-change="handleSelectionChange"
      ref="selectTemplateTableRef"
      size="small"
      :row-key="getRowKeys"
      :border="false"
    >
      <el-table-column type="selection" width="55" align="center" :reserve-selection="true" />
      <el-table-column
        :label="$t('product.product-select-template.318012-0')"
        align="left"
        prop="templateName"
        min-width="160"
      />
      <el-table-column
        :label="$t('product.product-select-template.318012-6')"
        align="left"
        prop="identifier"
        min-width="120"
      />
      <el-table-column
        :label="$t('product.product-select-template.318012-7')"
        align="center"
        prop="type"
        min-width="100"
      >
        <template #default="scope">
          <dict-tag :options="iot_things_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-things-model.142341-12')"
        align="center"
        prop="isChart"
        min-width="90"
      >
        <template #default="scope">
          <dict-tag :options="iot_yes_no" :value="scope.row.isChart" />
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-select-template.318012-9')"
        align="center"
        prop="isMonitor"
        min-width="90"
      >
        <template #default="scope">
          <dict-tag :options="iot_yes_no" :value="scope.row.isMonitor" />
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-select-template.318012-10')"
        align="center"
        prop="isReadonly"
        min-width="50"
      >
        <template #default="scope">
          <dict-tag :options="iot_yes_no" :value="scope.row.isReadonly" />
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-select-template.318012-11')"
        align="center"
        prop="isHistory"
        min-width="90"
      >
        <template #default="scope">
          <dict-tag :options="iot_yes_no" :value="scope.row.isHistory" />
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('product.product-select-template.318012-12')"
        align="center"
        prop="datatype"
        min-width="90"
      >
        <template #default="scope">
          <dict-tag :options="iot_data_type" :value="scope.row.datatype" />
        </template>
      </el-table-column>
      <el-table-column :label="$t('template.index.891112-124')" align="center" prop="isSys" min-width="90">
        <template #default="scope">
          <dict-tag :options="system_type_status" :value="scope.row.isSys" />
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total > 0"
      layout="prev, pager, next"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listTemplate } from '@/api/iot/template';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;
const { iot_things_type, iot_data_type, iot_yes_no, system_type_status } = useDict(
  'iot_things_type',
  'iot_data_type',
  'iot_yes_no',
  'system_type_status'
);
const emit = defineEmits(['idsToParentEvent']);

const loading = ref(false);
const ids = ref<any[]>([]);
const total = ref(0);
const templateList = ref<any[]>([]);
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  templateName: null as string | null,
  type: null as string | null,
  isSys: null as string | null,
});
const selectTemplateTableRef = ref<any>(null);

function getList() {
  loading.value = true;
  listTemplate(queryParams.value).then((response: any) => {
    templateList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  queryParams.value.templateName = null;
  queryParams.value.type = null;
  queryParams.value.isSys = null;
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.templateId);
  emit('idsToParentEvent', ids.value);
}

function getRowKeys(row: any) {
  return row.templateId;
}

onMounted(() => {
  getList();
  ids.value = [];
});

defineExpose({ selectTemplateTableRef });
</script>
