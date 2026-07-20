<template>
  <el-dialog
    class="scada-dialog"
    :title="$t('scada.topo.components.propertyBar.scadaDialog.764059-1')"
    v-model="visibleModel"
    width="800px"
    :before-close="handleClose"
  >
    <div class="list-content" v-loading="loading">
      <el-form :model="queryParams" ref="queryForm" :inline="true" @submit.prevent>
        <el-form-item>
          <el-input
            v-model="queryParams.pageName"
            :placeholder="$t('scada.topo.components.propertyBar.scadaDialog.764059-2')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 168px"
          />
        </el-form-item>
        <el-form-item style="margin-bottom: 18px">
          <el-button type="primary" :icon="Search" @click="handleQuery">
            {{ $t('search') }}
          </el-button>
          <el-button :icon="Refresh" @click="resetQuery" style="margin-left: 8px">
            {{ $t('reset') }}
          </el-button>
        </el-form-item>
      </el-form>

      <el-table
        :data="scadaList"
        highlight-current-row
        @current-change="handleCurrentChange"
        style="width: 100%"
        ref="scadaTable"
        :border="false"
      >
        <el-table-column
          width="60"
          :label="$t('scada.topo.components.propertyBar.scadaDialog.764059-4')"
          align="center"
        >
          <template #default="scope">
            <el-radio
              v-model="currentSelectedGuid"
              :label="scope.row.guid"
              @change="handleRadioChange(scope.row)"
              class="hidden-radio-text"
            >
              {{ scope.row.guid }}
            </el-radio>
          </template>
        </el-table-column>
        <el-table-column
          prop="pageName"
          :label="$t('scada.topo.components.propertyBar.scadaDialog.764059-3')"
          min-width="220"
        />
        <el-table-column
          prop="pageResolution"
          :label="$t('scada.topo.components.propertyBar.scadaDialog.764059-5')"
          width="160"
          align="center"
        />
      </el-table>

      <pagination
        v-show="total > 0"
        layout="prev, pager, next"
        :total="total"
        :page="queryParams.pageNum"
        @update:page="queryParams.pageNum = $event"
        :limit="queryParams.pageSize"
        @update:limit="queryParams.pageSize = $event"
        @pagination="getScadaList"
      />
    </div>

    <template #footer>
      <el-button type="primary" @click="handleConfirm" :disabled="!selectedRow">{{ $t('confirm') }}</el-button>
      <el-button @click="handleClose">{{ $t('cancel') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, nextTick, reactive, ref, watch } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listCenter } from '@/api/scada/center';
import Pagination from '@/components/Pagination/index.vue';

const props = defineProps<{
  visible: boolean;
  scadaType: number;
  selectedGuid: string;
}>();

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'confirm', value: Record<string, any> | null): void;
}>();

const loading = ref(false);
const scadaList = ref<any[]>([]);
const total = ref(0);
const currentSelectedGuid = ref('');
const selectedRow = ref<Record<string, any> | null>(null);
const scadaTable = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  type: props.scadaType,
  pageName: null as string | null,
});

const visibleModel = computed({
  get: () => props.visible,
  set: (value: boolean) => emit('update:visible', value),
});

watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      queryParams.pageNum = 1;
      currentSelectedGuid.value = props.selectedGuid || '';
      selectedRow.value = null;
      getScadaList();
    }
  }
);

watch(
  () => props.scadaType,
  (newVal) => {
    queryParams.type = newVal;
  }
);

watch(currentSelectedGuid, (newGuid) => {
  if (!newGuid) {
    selectedRow.value = null;
    return;
  }
  const row = (scadaList.value || []).find((r) => r.guid === newGuid) || null;
  if (row) {
    selectedRow.value = row;
    nextTick(() => {
      if (scadaTable.value && scadaTable.value.setCurrentRow) {
        scadaTable.value.setCurrentRow(row);
      }
    });
  }
});

function getScadaList() {
  loading.value = true;
  listCenter(queryParams)
    .then((res: any) => {
      if (res.code === 200) {
        scadaList.value = res.rows || [];
        total.value = res.total || 0;
        if (currentSelectedGuid.value) {
          const sel = scadaList.value.find((r) => r.guid === currentSelectedGuid.value) || null;
          if (sel) {
            selectedRow.value = sel;
            nextTick(() => {
              if (scadaTable.value && scadaTable.value.setCurrentRow) {
                scadaTable.value.setCurrentRow(sel);
              }
            });
          }
        }
      }
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
}

function handleRadioChange(row: Record<string, any>) {
  selectedRow.value = row;
  currentSelectedGuid.value = row.guid;
}

function handleCurrentChange(row: Record<string, any> | null) {
  if (!row || !row.guid) return;
  currentSelectedGuid.value = row.guid;
  selectedRow.value = row;
}

function handleConfirm() {
  if (!selectedRow.value && currentSelectedGuid.value) {
    selectedRow.value = scadaList.value.find((r) => r.guid === currentSelectedGuid.value) || null;
  }
  emit('confirm', selectedRow.value);
}

function handleClose() {
  emit('update:visible', false);
}

function handleQuery() {
  queryParams.pageNum = 1;
  getScadaList();
}

function resetQuery() {
  queryParams.pageName = null;
  handleQuery();
}
</script>

<style lang="scss" scoped>
.scada-dialog {
  .list-content {
    padding: 0;
  }

  .hidden-radio-text {
    height: 23px;

    :deep(.el-radio__label) {
      display: none;
    }

    :deep(.el-radio__input) {
      margin-right: 0;
    }
  }
}
</style>
