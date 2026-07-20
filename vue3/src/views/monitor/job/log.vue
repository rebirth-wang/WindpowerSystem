<template>
  <div class="log-wrap">
    <el-card v-show="showSearch" class="search-card">
      <div class="form-wrap">
        <el-form @submit.prevent :model="queryParams" ref="queryFormRef" :inline="true" label-width="88px">
          <el-form-item prop="jobName">
            <el-input
              v-model="queryParams.jobName"
              :placeholder="$t('system.job.log.085689-0')"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item prop="jobGroup">
            <el-select v-model="queryParams.jobGroup" :placeholder="$t('system.job.log.085689-2')" clearable>
              <el-option
                v-for="item in dict.type.sys_job_group"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item prop="status">
            <el-select v-model="queryParams.status" :placeholder="$t('system.job.log.085689-4')" clearable>
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
                :start-placeholder="$t('device.device-functionlog.399522-8')"
                :end-placeholder="$t('device.device-functionlog.399522-9')"
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

    <el-card class="card-wrap">
      <el-row :gutter="10" style="margin-bottom: 15px">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            :icon="Download"
            size="small"
            @click="handleExport"
            v-hasPermi="['monitor:job:export']"
          >
            {{ $t('export') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            size="small"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['monitor:job:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Delete" size="small" @click="handleClean" v-hasPermi="['monitor:job:remove']">
            {{ $t('system.job.log.085689-6') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Close" size="small" @click="handleClose">{{ $t('close') }}</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
      <el-table v-loading="loading" :data="jobLogList" @selection-change="handleSelectionChange" :border="false">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column :label="$t('system.job.log.085689-7')" align="center" prop="jobLogId" min-width="100" />
        <el-table-column
          :label="$t('system.job.356378-0')"
          align="left"
          prop="jobName"
          :show-overflow-tooltip="true"
          min-width="130"
        />
        <el-table-column
          :label="$t('system.job.356378-2')"
          align="center"
          prop="jobGroup"
          :show-overflow-tooltip="true"
          min-width="80"
        >
          <template #default="scope">
            <dict-tag :options="dict.type.sys_job_group" :value="scope.row.jobGroup" />
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('system.job.log.085689-8')"
          align="center"
          prop="invokeTarget"
          :show-overflow-tooltip="true"
          min-width="200"
        />
        <el-table-column
          :label="$t('system.job.log.085689-9')"
          align="center"
          prop="jobMessage"
          :show-overflow-tooltip="true"
          min-width="180"
        />
        <el-table-column :label="$t('system.job.log.085689-10')" align="center" prop="status" min-width="80">
          <template #default="scope">
            <dict-tag :options="dict.type.sys_common_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('system.job.log.085689-11')" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" :label="$t('opation')" align="center" width="100">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              link
              :icon="View"
              @click="handleView(scope.row)"
              v-hasPermi="['monitor:job:query']"
            >
              {{ $t('system.job.log.085689-12') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />

      <!-- 调度日志详细 -->
      <el-dialog :title="$t('system.job.log.085689-13')" v-model="open" width="700px" append-to-body>
        <div style="margin-top: -55px">
          <el-divider style="margin-top: -30px" />
          <el-form ref="detailFormRef" :model="form" label-width="100px" size="small">
            <el-row>
              <el-col :span="12">
                <el-form-item :label="$t('system.job.log.085689-14')">{{ form.jobLogId }}</el-form-item>
                <el-form-item :label="$t('system.job.log.085689-15')">{{ form.jobName }}</el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item :label="$t('system.job.log.085689-16')">{{ form.jobGroup }}</el-form-item>
                <el-form-item :label="$t('system.job.log.085689-17')">{{ form.createTime }}</el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item :label="$t('system.job.log.085689-18')">{{ form.invokeTarget }}</el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item :label="$t('system.job.log.085689-19')">{{ form.jobMessage }}</el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item :label="$t('system.job.log.085689-20')">
                  <div v-if="form.status == 0">{{ $t('system.job.356378-37') }}</div>
                  <div v-else-if="form.status == 1">{{ $t('system.job.356378-38') }}</div>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item :label="$t('system.job.log.085689')" v-if="form.status == 1">
                  {{ form.exceptionInfo }}
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="open = false">{{ $t('close') }}</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Search, Refresh, Delete, Download, Close, View, ArrowDown, ArrowUp } from '@element-plus/icons-vue';
import { getJob } from '@/api/monitor/job';
import { listJobLog, delJobLog, cleanJobLog } from '@/api/monitor/jobLog';
import { useDict } from '@/utils/dict/useDict';
import { parseTime, addDateRange } from '@/utils/ruoyi';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const route = useRoute();
const { dict } = useDict('sys_common_status', 'sys_job_group');

const loading = ref(true);
const ids = ref<any[]>([]);
const multiple = ref(true);
const showSearch = ref(true);
const searchShow = ref(false);
const total = ref(0);
const jobLogList = ref<any[]>([]);
const open = ref(false);
const dateRange = ref<any[]>([]);
const form = ref<any>({});
const queryFormRef = ref();

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  jobName: undefined as string | undefined,
  jobGroup: undefined as string | undefined,
  status: undefined as string | undefined,
});

function getList() {
  loading.value = true;
  listJobLog(addDateRange(queryParams, dateRange.value)).then((response: any) => {
    jobLogList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleClose() {
  const obj = { path: '/monitor/job' };
  (proxy as any).$tab.closeOpenPage(obj);
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  dateRange.value = [];
  queryFormRef.value?.resetFields();
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.jobLogId);
  multiple.value = !selection.length;
}

function handleView(row: any) {
  open.value = true;
  form.value = row;
}

function handleDelete() {
  const jobLogIds = ids.value;
  (proxy as any).$modal
    .confirm(t('system.job.log.085689-22', [jobLogIds]))
    .then(() => {
      return delJobLog(jobLogIds);
    })
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('delSuccess'));
    })
    .catch(() => {});
}

function handleClean() {
  (proxy as any).$modal
    .confirm(t('system.job.log.085689-23'))
    .then(() => {
      return cleanJobLog();
    })
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('system.job.log.085689-24'));
    })
    .catch(() => {});
}

function handleExport() {
  (proxy as any).download('/monitor/jobLog/export', { ...queryParams }, `log_${new Date().getTime()}.xlsx`);
}

function searchChange() {
  searchShow.value = !searchShow.value;
}

onMounted(() => {
  const jobId = route.params && route.params.jobId;
  if (jobId !== undefined && jobId != '0') {
    getJob(jobId as any).then((response: any) => {
      queryParams.jobName = response.data.jobName;
      queryParams.jobGroup = response.data.jobGroup;
      getList();
    });
  } else {
    getList();
  }
});
</script>

<style lang="scss" scoped>
.log-wrap {
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

.top-card-wrap {
  margin-bottom: 5px;
}

.card-wrap {
  padding-bottom: 100px;
}
</style>
