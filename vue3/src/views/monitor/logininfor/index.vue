<template>
  <div class="monitor-logininfor">
    <el-card v-show="showSearch" class="search-card">
      <div class="form-wrap">
        <el-form @submit.prevent :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item prop="ipaddr">
            <el-input
              v-model="queryParams.ipaddr"
              :placeholder="$t('online.093480-1')"
              clearable
              style="width: 192px"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item prop="userName">
            <el-input
              v-model="queryParams.userName"
              :placeholder="$t('online.093480-2')"
              clearable
              style="width: 192px"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item prop="status">
            <el-select
              v-model="queryParams.status"
              :placeholder="$t('system.logininfor.890875-0')"
              clearable
              style="width: 192px"
            >
              <el-option
                v-for="item in dict.type.sys_common_status"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <template v-if="searchShow">
            <el-form-item>
              <el-date-picker
                v-model="dateRange"
                style="width: 240px"
                value-format="YYYY-MM-DD"
                type="daterange"
                range-separator="-"
                :start-placeholder="$t('system.dict.index.880996-3')"
                :end-placeholder="$t('system.dict.index.880996-4')"
              />
            </el-form-item>
          </template>
        </el-form>
        <div class="search-btn-group">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
          <el-button type="primary" link @click="searchChange">
            <span style="color: #486ff2; margin-left: 14px">
              {{ searchShow ? t('template.index.891112-113') : t('template.index.891112-112') }}
            </span>
            <el-icon style="color: #486ff2; margin-left: 10px">
              <ArrowDown v-if="!searchShow" />
              <ArrowUp v-else />
            </el-icon>
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 15px">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            :icon="Unlock"
            :disabled="single"
            @click="handleUnlock"
            v-hasPermi="['monitor:logininfor:unlock']"
          >
            {{ $t('system.logininfor.890875-1') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" @click="handleClean" v-hasPermi="['monitor:logininfor:remove']">
            {{ $t('operlog.874509-7') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete()"
            v-hasPermi="['monitor:logininfor:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['monitor:logininfor:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        ref="tablesRef"
        v-loading="loading"
        :data="listData"
        :border="false"
        @selection-change="handleSelectionChange"
        :default-sort="defaultSort"
        @sort-change="handleSortChange"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('system.logininfor.890875-2')" align="center" prop="infoId" width="90" />
        <el-table-column
          :label="$t('user.profile.index.894502-1')"
          align="left"
          prop="userName"
          :show-overflow-tooltip="true"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
          min-width="200"
        />
        <el-table-column
          :label="$t('online.093480-7')"
          align="center"
          prop="ipaddr"
          :show-overflow-tooltip="true"
          width="150"
        />
        <el-table-column
          :label="$t('online.093480-8')"
          align="center"
          prop="loginLocation"
          :show-overflow-tooltip="true"
          min-width="200"
        />
        <el-table-column
          :label="$t('online.093480-9')"
          align="center"
          prop="browser"
          :show-overflow-tooltip="true"
          min-width="130"
        />
        <el-table-column :label="$t('online.093480-10')" align="center" prop="os" min-width="150" />
        <el-table-column :label="$t('system.logininfor.890875-9')" align="center" prop="status" width="100">
          <template #default="scope">
            <dict-tag :options="dict.type.sys_common_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('system.logininfor.890875-3')" align="left" prop="msg" min-width="180" />
        <el-table-column
          :label="$t('online.093480-11')"
          align="center"
          prop="loginTime"
          sortable="custom"
          :sort-orders="['descending', 'ascending']"
          width="180"
        >
          <template #default="scope">
            <span>{{ parseTime(scope.row.loginTime) }}</span>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, Refresh, Delete, Download, ArrowDown, ArrowUp, Unlock } from '@element-plus/icons-vue';
import { list, delLogininfor, cleanLogininfor, unlockLogininfor } from '@/api/monitor/logininfor';
import { useDict } from '@/utils/dict/useDict';
import { parseTime, addDateRange } from '@/utils/ruoyi';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const { dict } = useDict('sys_common_status');

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const selectName = ref('');
const showSearch = ref(true);
const searchShow = ref(false);
const total = ref(0);
const listData = ref<any[]>([]);
const dateRange = ref<any[]>([]);
const defaultSort = reactive({ prop: 'loginTime', order: 'descending' as const });
const queryFormRef = ref();
const tablesRef = ref();

const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  ipaddr: undefined,
  userName: undefined,
  status: undefined,
});

function getList() {
  loading.value = true;
  list(addDateRange(queryParams, dateRange.value)).then((response: any) => {
    listData.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  dateRange.value = [];
  queryFormRef.value?.resetFields();
  queryParams.pageNum = 1;
  tablesRef.value?.sort(defaultSort.prop, defaultSort.order);
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.infoId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
  selectName.value = selection.map((item) => item.userName).join(',');
}

function handleSortChange(column: any) {
  queryParams.orderByColumn = column.prop;
  queryParams.isAsc = column.order;
  getList();
}

function handleDelete() {
  const infoIds = ids.value;
  (proxy as any).$modal
    .confirm(t('system.logininfor.890875-4', [infoIds]))
    .then(() => {
      return delLogininfor(infoIds);
    })
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('delSuccess'));
    })
    .catch(() => {});
}

function handleClean() {
  (proxy as any).$modal
    .confirm(t('system.logininfor.890875-5'))
    .then(() => {
      return cleanLogininfor();
    })
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('operlog.874509-29'));
    })
    .catch(() => {});
}

function handleUnlock() {
  const username = selectName.value;
  (proxy as any).$modal
    .confirm(t('system.logininfor.890875-6', [username]))
    .then(() => {
      return unlockLogininfor(username);
    })
    .then(() => {
      (proxy as any).$modal.msgSuccess(t('system.logininfor.890875-8') + username + t('system.logininfor.890875-7'));
    })
    .catch(() => {});
}

function handleExport() {
  (proxy as any).download('monitor/logininfor/export', { ...queryParams }, `logininfor_${new Date().getTime()}.xlsx`);
}

function searchChange() {
  searchShow.value = !searchShow.value;
}

function getRowKeys(row: any) {
  return row.infoId;
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.monitor-logininfor {
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
