<template>
  <!-- 导入表 -->
  <el-dialog :title="$t('gen.import.832346-0')" v-model="visible" width="950px" top="5vh" append-to-body>
    <el-form :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item prop="dataName">
        <el-select
          v-model="queryParams.dataName"
          size="small"
          :placeholder="$t('views.iot.bridge.index.525282-51')"
          style="width: 100%"
          :clearable="true"
        >
          <el-option v-for="(item, index) in dataSources" :key="index" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item prop="tableName">
        <el-input
          v-model="queryParams.tableName"
          size="small"
          :placeholder="$t('gen.import.832346-2')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
        <el-button :icon="Refresh" size="small" @click="resetQuery">{{ $t('reset') }}</el-button>
      </el-form-item>
    </el-form>
    <el-row>
      <el-table
        @row-click="clickRow"
        ref="multipleTableRef"
        :data="dbTableList"
        @selection-change="handleSelectionChange"
        size="small"
        :border="false"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column
          prop="tableName"
          :label="$t('gen.import.832346-1')"
          :show-overflow-tooltip="true"
          min-width="150px"
        ></el-table-column>
        <el-table-column
          prop="tableComment"
          :label="$t('gen.import.832346-3')"
          :show-overflow-tooltip="true"
          min-width="150px"
        ></el-table-column>
        <el-table-column prop="createTime" :label="$t('creatTime')" align="center" min-width="120px"></el-table-column>
        <el-table-column
          prop="updateTime"
          :label="$t('gen.import.832346-5')"
          align="center"
          min-width="120px"
        ></el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        layout="prev, pager, next"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-row>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="handleImportTable">{{ $t('confirm') }}</el-button>
        <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, nextTick } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';
import { listDbTable, importTable, listDataSource } from '@/api/tool/gen';

const { proxy } = getCurrentInstance() as any;
const emit = defineEmits(['ok']);

const visible = ref(false);
const tables = ref<string[]>([]);
const total = ref(0);
const dbTableList = ref<any[]>([]);
const dataSources = ref<any[]>([]);
const multipleTableRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  tableName: undefined as any,
  tableComment: undefined as any,
  dataName: 'master',
});

function show() {
  proxy.resetForm('queryRef');
  getList();
  handleDataSource();
  visible.value = true;
  queryParams.pageNum = 1;
  nextTick(() => {
    multipleTableRef.value?.clearSelection();
  });
}

function clickRow(row: any) {
  multipleTableRef.value?.toggleRowSelection(row);
}

function handleSelectionChange(selection: any[]) {
  tables.value = selection.map((item) => item.tableName);
}

function getList() {
  listDbTable(queryParams).then((res: any) => {
    if (res.code === 200) {
      dbTableList.value = res.rows;
      total.value = res.total;
    }
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
function resetQuery() {
  proxy.resetForm('queryRef');
  handleQuery();
}

function handleDataSource() {
  listDataSource().then((response: any) => {
    dataSources.value = response.data;
  });
}

function handleImportTable() {
  const tableNames = tables.value.join(',');
  const params = { tables: tableNames, dataName: queryParams.dataName };
  if (tableNames == '') {
    proxy.$modal.msgError(proxy.$t('gen.import.832346-6'));
    return;
  }
  importTable(params).then((res: any) => {
    proxy.$modal.msgSuccess(res.msg);
    if (res.code === 200) {
      visible.value = false;
      emit('ok');
    }
  });
}

function getRowKeys(row: any) {
  return row.tableName;
}

defineExpose({ show });
</script>
