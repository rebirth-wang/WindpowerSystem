<template>
  <div class="tool-gen">
    <el-card v-show="showSearch" class="search-card">
      <div class="form-wrap">
        <el-form @submit.prevent :model="queryParams" ref="queryRef" :inline="true" class="search-form">
          <el-form-item prop="tableName">
            <el-input
              v-model="queryParams.tableName"
              :placeholder="$t('gen.import.832346-2')"
              clearable
              style="width: 192px"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item prop="dataName">
            <el-select
              v-model="queryParams.dataName"
              :placeholder="$t('views.iot.bridge.index.525282-51')"
              style="width: 192px"
              :clearable="true"
            >
              <el-option v-for="(item, index) in dataSources" :key="index" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-date-picker
              v-model="dateRange"
              style="width: 240px"
              value-format="YYYY-MM-DD"
              type="daterange"
              range-separator="-"
              :start-placeholder="$t('notify.log.333543-9')"
              :end-placeholder="$t('notify.log.333543-10')"
            ></el-date-picker>
          </el-form-item>
          <template v-if="searchShow">
            <el-form-item prop="tableComment">
              <el-input
                v-model="queryParams.tableComment"
                :placeholder="$t('gen.import.832346-4')"
                clearable
                style="width: 192px"
                @keyup.enter="handleQuery"
              />
            </el-form-item>
          </template>
        </el-form>
        <div class="search-btn-group">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
          <el-button type="primary" link @click="searchChange">
            <span style="color: #486ff2; margin-left: 14px">
              {{ searchShow ? $t('template.index.891112-113') : $t('template.index.891112-112') }}
            </span>
            <el-icon style="color: #486ff2; margin-left: 10px">
              <ArrowUp v-if="searchShow" />
              <ArrowDown v-else />
            </el-icon>
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Upload" @click="openImportTable" v-hasPermi="['tool:gen:import']">
            {{ $t('import') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleEditTable" v-hasPermi="['tool:gen:edit']">
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['tool:gen:remove']">
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" :disabled="multiple" @click="handleGenTable" v-hasPermi="['tool:gen:code']">
            {{ $t('gen.index.467583-0') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table
        v-loading="loading"
        :data="tableList"
        @selection-change="handleSelectionChange"
        :border="false"
        :row-key="getRowKeys"
        ref="multipleTableRef"
      >
        <el-table-column type="selection" align="center" width="55" :reserve-selection="true" />
        <el-table-column :label="$t('gen.editTable.650980-2')" type="index" width="60" align="left">
          <template #default="scope">
            <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('gen.import.832346-1')"
          align="left"
          prop="tableName"
          :show-overflow-tooltip="true"
          min-width="160"
        />
        <el-table-column
          :label="$t('views.iot.bridge.index.525282-50')"
          prop="dataName"
          align="center"
          :show-overflow-tooltip="true"
          min-width="100"
        ></el-table-column>
        <el-table-column
          :label="$t('gen.import.832346-3')"
          align="left"
          prop="tableComment"
          :show-overflow-tooltip="true"
          min-width="180"
        />
        <el-table-column
          :label="$t('gen.index.467583-1')"
          align="left"
          prop="className"
          :show-overflow-tooltip="true"
          min-width="180"
        />
        <el-table-column :label="$t('creatTime')" align="center" prop="createTime" width="160" />
        <el-table-column :label="$t('gen.import.832346-5')" align="center" prop="updateTime" width="160" />
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="300"
        >
          <template #default="scope">
            <el-button
              link
              :icon="View"
              size="small"
              @click="handlePreview(scope.row)"
              v-hasPermi="['tool:gen:preview']"
            >
              {{ $t('gen.index.467583-2') }}
            </el-button>
            <el-button
              link
              :icon="Edit"
              size="small"
              @click="handleEditTable(scope.row)"
              v-hasPermi="['tool:gen:edit']"
            >
              {{ $t('edit') }}
            </el-button>
            <el-button
              :icon="Delete"
              link
              size="small"
              @click="handleDelete(scope.row)"
              v-hasPermi="['tool:gen:remove']"
            >
              {{ $t('del') }}
            </el-button>
            <el-button
              link
              :icon="Refresh"
              size="small"
              @click="handleSynchDb(scope.row)"
              v-hasPermi="['tool:gen:edit']"
            >
              {{ $t('gen.index.467583-3') }}
            </el-button>
            <el-button
              :icon="Download"
              link
              size="small"
              @click="handleGenTable(scope.row)"
              v-hasPermi="['tool:gen:code']"
            >
              {{ $t('gen.index.467583-0') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        style="margin-bottom: 20px"
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 预览界面 -->
    <el-dialog :title="preview.title" v-model="preview.open" width="70%" append-to-body class="scrollbar">
      <el-tabs v-model="preview.activeName">
        <el-tab-pane
          v-for="(value, key) in preview.data"
          :label="String(key).substring(String(key).lastIndexOf('/') + 1, String(key).indexOf('.vm'))"
          :name="String(key).substring(String(key).lastIndexOf('/') + 1, String(key).indexOf('.vm'))"
          :key="key"
        >
          <el-link
            underline="never"
            :icon="DocumentCopy"
            v-clipboard:copy="value"
            v-clipboard:success="clipboardSuccess"
            style="float: right"
          >
            复制
          </el-link>
          <pre><code>{{ value }}</code></pre>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
    <import-table ref="importRef" @ok="handleQuery" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted, onActivated } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import {
  Search,
  Refresh,
  Upload,
  Edit,
  Delete,
  Download,
  ArrowUp,
  ArrowDown,
  DocumentCopy,
  View,
} from '@element-plus/icons-vue';
import { listTable, previewTable, delTable, genCode, synchDb, listDataSource } from '@/api/tool/gen';
import importTable from './importTable.vue';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();
const route = useRoute();

const searchShow = ref(false);
const loading = ref(true);
const uniqueId = ref('');
const ids = ref<any[]>([]);
const tableNames = ref<string[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const tableList = ref<any[]>([]);
const dateRange = ref<any>('');
const dataSources = ref<any[]>([]);
const multipleTableRef = ref<any>(null);
const importRef = ref<any>(null);
const queryRef = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  tableName: undefined as any,
  tableComment: undefined as any,
  dataName: '',
});

const preview = reactive<any>({
  open: false,
  title: t('gen.index.467583-5'),
  data: {},
  activeName: 'domain.java',
});

function getList() {
  loading.value = true;
  listTable(proxy.addDateRange(queryParams, dateRange.value)).then((response: any) => {
    tableList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleDataSource() {
  listDataSource().then((response: any) => {
    dataSources.value = response.data;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
function searchChange() {
  searchShow.value = !searchShow.value;
}

function handleGenTable(row: any) {
  const names = row.tableName || tableNames.value;
  if (names == '') {
    proxy.$modal.msgError(t('gen.index.467583-6'));
    return;
  }
  if (row.genType === '1') {
    genCode(row.tableName).then(() => {
      proxy.$modal.msgSuccess(t('gen.index.467583-7') + row.genPath);
    });
  } else {
    proxy.$download.zip('/tool/gen/batchGenCode?tables=' + names, 'ruoyi.zip');
  }
}

function handleSynchDb(row: any) {
  const tableName = row.tableName;
  proxy.$modal
    .confirm(t('gen.index.467583-8') + tableName + t('gen.index.467583-9'))
    .then(() => {
      return synchDb(tableName);
    })
    .then(() => {
      proxy.$modal.msgSuccess(t('gen.index.467583-10'));
    })
    .catch(() => {});
}

function openImportTable() {
  importRef.value?.show();
}

function resetQuery() {
  dateRange.value = [];
  queryRef.value?.resetFields();
  handleQuery();
}

function handlePreview(row: any) {
  previewTable(row.tableId).then((res: any) => {
    if (res.code === 200) {
      preview.data = res.data;
      preview.open = true;
      preview.activeName = 'domain.java';
    }
  });
}

function clipboardSuccess() {
  proxy.$modal.msgSuccess(t('gen.index.467583-11'));
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.tableId);
  tableNames.value = selection.map((item) => item.tableName);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleEditTable(row?: any) {
  const tableId = row?.tableId || ids.value[0];
  const tableName = row?.tableName || tableNames.value[0];
  const params = { pageNum: queryParams.pageNum };
  (proxy as any).$tab.openPage('修改[' + tableName + ']生成配置', '/tool/genEdit/index/' + tableId, params);
}

function handleDelete(row?: any) {
  const tableIds = row?.tableId || ids.value;
  proxy.$modal
    .confirm(t('gen.index.467583-12', [tableIds]))
    .then(() => {
      return delTable(tableIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function getRowKeys(row: any) {
  return row.tableId;
}

onMounted(() => {
  getList();
  handleDataSource();
});

onActivated(() => {
  const time = route.query.t;
  if (time != null && time != uniqueId.value) {
    uniqueId.value = time as string;
    queryParams.pageNum = Number(route.query.pageNum);
    getList();
  }
});
</script>

<style lang="scss" scoped>
.tool-gen {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;

    .form-wrap {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: flex-end;
      margin-bottom: -22.5px;

      .search-btn-group {
        display: flex;
        flex-direction: row;
        margin-bottom: 22px;
      }
    }
  }
}
</style>
