<template>
  <div class="monitor-job">
    <el-card v-show="showSearch" class="search-card">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
        class="search-form"
      >
        <el-form-item prop="jobName">
          <el-input
            v-model="queryParams.jobName"
            :placeholder="$t('system.job.356378-1')"
            clearable
            style="width: 192px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item prop="jobGroup">
          <el-select
            v-model="queryParams.jobGroup"
            :placeholder="$t('system.job.356378-3')"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="item in dict.type.sys_job_group"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="status">
          <el-select
            v-model="queryParams.status"
            :placeholder="$t('system.job.356378-5')"
            clearable
            style="width: 192px"
          >
            <el-option
              v-for="item in dict.type.sys_job_status"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card>
      <el-row :gutter="10" style="margin-bottom: 15px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['monitor:job:add']">
            {{ $t('add') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['monitor:job:edit']">
            {{ $t('update') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple"
            @click="handleDelete()"
            v-hasPermi="['monitor:job:remove']"
          >
            {{ $t('del') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Download" @click="handleExport" v-hasPermi="['monitor:job:export']">
            {{ $t('export') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain :icon="Operation" @click="handleJobLog()" v-hasPermi="['monitor:job:query']">
            {{ $t('system.job.356378-6') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="jobList"
        :border="false"
        @selection-change="handleSelectionChange"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column :reserve-selection="true" type="selection" width="55" align="center" />
        <el-table-column :label="$t('system.job.356378-7')" width="100" align="center" prop="jobId" />
        <el-table-column
          :label="$t('system.job.356378-0')"
          align="left"
          prop="jobName"
          :show-overflow-tooltip="true"
          min-width="200"
        />
        <el-table-column :label="$t('system.job.356378-2')" align="center" prop="jobGroup" min-width="180">
          <template #default="scope">
            <dict-tag :options="dict.type.sys_job_group" :value="scope.row.jobGroup" />
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('system.job.356378-8')"
          align="left"
          prop="invokeTarget"
          :show-overflow-tooltip="true"
          min-width="200"
        />
        <el-table-column
          :label="$t('system.job.356378-9')"
          align="left"
          prop="cronExpression"
          :show-overflow-tooltip="true"
          min-width="180"
        />
        <el-table-column :label="$t('status')" align="center" width="90">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="0"
              :inactive-value="1"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="300"
        >
          <template #default="scope">
            <div class="op-actions">
              <el-button
                size="small"
                link
                :icon="Edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['monitor:job:edit']"
              >
                {{ $t('update') }}
              </el-button>
              <el-button
                size="small"
                link
                :icon="Delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['monitor:job:remove']"
              >
                {{ $t('del') }}
              </el-button>
              <el-dropdown
                class="op-more"
                @command="(command) => handleCommand(command, scope.row)"
                v-hasPermi="['monitor:job:changeStatus', 'monitor:job:query']"
              >
                <el-button size="small" link :icon="DArrowRight">
                  {{ $t('user.index.098976-14') }}
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-if="checkPermi(['monitor:job:run'])" command="handleRun" :icon="CaretRight">
                      {{ $t('system.job.356378-10') }}
                    </el-dropdown-item>
                    <el-dropdown-item v-if="checkPermi(['monitor:job:query'])" command="handleView" :icon="View">
                      {{ $t('system.job.356378-11') }}
                    </el-dropdown-item>
                    <el-dropdown-item v-if="checkPermi(['monitor:job:query'])" command="handleJobLog" :icon="Operation">
                      {{ $t('system.job.356378-12') }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
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

    <!-- 添加或修改定时任务对话框 -->
    <el-dialog :title="title" v-model="open" width="810px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="125px">
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('system.job.356378-0')" prop="jobName">
              <el-input v-model="form.jobName" :placeholder="$t('system.job.356378-1')" style="width: 250px" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('system.job.356378-13')" prop="jobGroup">
              <el-select v-model="form.jobGroup" :placeholder="$t('system.job.356378-14')" style="width: 220px">
                <el-option
                  v-for="item in dict.type.sys_job_group"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="invokeTarget">
              <template #label>
                {{ $t('system.job.356378-15') }}
                <el-tooltip placement="top">
                  <template #content>
                    {{ $t('system.job.356378-16') }}
                    <br />
                    {{ $t('system.job.356378-17') }}
                    <br />
                    {{ $t('system.job.356378-18') }}
                  </template>
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
              </template>
              <el-input v-model="form.invokeTarget" :placeholder="$t('system.job.356378-8')" style="width: 604px" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="$t('system.job.356378-19')" prop="cronExpression">
              <el-input v-model="form.cronExpression" :placeholder="$t('system.job.356378-20')" style="width: 604px">
                <template #append>
                  <el-button type="primary" @click="handleShowCron">
                    {{ $t('system.job.356378-21') }}
                    <el-icon class="el-icon--right"><Timer /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('system.job.356378-22')" prop="misfirePolicy">
              <el-radio-group v-model="form.misfirePolicy" size="small">
                <el-radio-button v-for="item in dict.type.execution_strategy" :key="item.value" :value="item.value">
                  {{ item.label }}
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('status')">
              <el-radio-group v-model="form.status">
                <el-radio v-for="item in dict.type.sys_job_status" :key="item.value" :value="Number(item.value)">
                  {{ item.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="$t('system.job.356378-25')" prop="concurrent">
              <el-radio-group v-model="form.concurrent" size="small">
                <el-radio-button v-for="item in dict.type.is_concurrency" :key="item.value" :value="item.value">
                  {{ item.label }}
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">{{ $t('confirm') }}</el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog :title="$t('system.job.356378-28')" v-model="openCron" append-to-body destroy-on-close class="scrollbar">
      <crontab @hide="openCron = false" @fill="crontabFill" :expression="expression" />
    </el-dialog>

    <!-- 任务日志详细 -->
    <el-dialog :title="$t('system.job.356378-11')" v-model="openView" width="710px" append-to-body>
      <el-form ref="viewFormRef" :model="form" label-width="135px" size="small">
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('system.job.356378-29')">{{ form.jobId }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('system.job.356378-31')">{{ jobGroupFormat(form) }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('system.job.356378-30')">{{ form.jobName }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('system.job.356378-32')">{{ form.createTime }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('system.job.356378-33')">{{ form.cronExpression }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('system.job.356378-34')">{{ parseTime(form.nextValidTime) }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item :label="$t('system.job.356378-35')">{{ form.invokeTarget }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('system.job.356378-36')">
              <div>
                {{ (dict.type.execution_status.find((item: any) => Number(item.value) === form.status) || {}).label }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('system.job.356378-25')">
              <div>
                {{ (dict.type.is_concurrency.find((item: any) => Number(item.value) === form.concurrent) || {}).label }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('system.job.356378-39')">
              <div>
                {{
                  (dict.type.execution_strategy.find((item: any) => Number(item.value) === form.misfirePolicy) || {})
                    .label
                }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="openView = false">{{ $t('close') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import {
  Search,
  Refresh,
  Plus,
  Edit,
  Delete,
  Download,
  Operation,
  DArrowRight,
  CaretRight,
  View,
  QuestionFilled,
  Timer,
} from '@element-plus/icons-vue';
import { listJob, getJob, delJob, addJob, updateJob, runJob, changeJobStatus } from '@/api/monitor/job';
import Crontab from '@/components/Crontab/index.vue';
import { useDict } from '@/utils/dict/useDict';
import { checkPermi } from '@/utils/permission';
import { parseTime, selectDictLabel } from '@/utils/ruoyi';

const { t } = useI18n();
const { proxy } = getCurrentInstance()!;
const router = useRouter();
const { dict } = useDict('sys_job_group', 'sys_job_status', 'execution_strategy', 'is_concurrency', 'execution_status');

const loading = ref(true);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const jobList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const openView = ref(false);
const openCron = ref(false);
const expression = ref('');
const queryFormRef = ref();
const formRef = ref();
const multipleTableRef = ref();

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  jobName: undefined as string | undefined,
  jobGroup: undefined as string | undefined,
  status: undefined as string | undefined,
});
const form = ref<any>({});
const rules = reactive({
  jobName: [{ required: true, message: t('system.job.356378-41'), trigger: 'blur' }],
  invokeTarget: [{ required: true, message: t('system.job.356378-42'), trigger: 'blur' }],
  cronExpression: [{ required: true, message: t('system.job.356378-43'), trigger: 'blur' }],
});

function getList() {
  loading.value = true;
  listJob(queryParams).then((response: any) => {
    jobList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function jobGroupFormat(row: any) {
  return selectDictLabel(dict.type.sys_job_group, row.jobGroup);
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    jobId: undefined,
    jobName: undefined,
    jobGroup: undefined,
    invokeTarget: undefined,
    cronExpression: undefined,
    misfirePolicy: 1,
    concurrent: 1,
    status: 0,
  };
  formRef.value?.resetFields();
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  queryFormRef.value?.resetFields();
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.jobId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleCommand(command: string, row: any) {
  switch (command) {
    case 'handleRun':
      handleRun(row);
      break;
    case 'handleView':
      handleView(row);
      break;
    case 'handleJobLog':
      handleJobLog(row);
      break;
  }
}

function handleStatusChange(row: any) {
  const text = row.status === 0 ? t('simulate.index.111543-54') : t('simulate.index.111543-55');
  (proxy as any).$modal
    .confirm(t('system.job.356378-44') + text + '""' + row.jobName + t('system.job.356378-45'))
    .then(() => {
      return changeJobStatus(row.jobId, row.status);
    })
    .then(() => {
      (proxy as any).$modal.msgSuccess(text + t('success'));
    })
    .catch(() => {
      row.status = row.status === 0 ? 1 : 0;
    });
}

function handleRun(row: any) {
  (proxy as any).$modal
    .confirm(t('system.job.356378-46') + row.jobName + t('system.job.356378-45'))
    .then(() => {
      return runJob(row.jobId, row.jobGroup);
    })
    .then(() => {
      (proxy as any).$modal.msgSuccess(t('system.job.356378-47'));
    })
    .catch(() => {});
}

function handleView(row: any) {
  getJob(row.jobId).then((response: any) => {
    form.value = response.data;
    openView.value = true;
  });
}

function handleShowCron() {
  expression.value = form.value.cronExpression;
  openCron.value = true;
}

function crontabFill(value: string) {
  form.value.cronExpression = value;
}

function handleJobLog(row?: any) {
  const jobId = row?.jobId || 0;
  router.push('/monitor/jobLog/index/' + jobId);
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = t('system.job.356378-48');
}

function handleUpdate(row?: any) {
  reset();
  const jobId = row?.jobId || ids.value;
  getJob(jobId).then((response: any) => {
    form.value = response.data;
    open.value = true;
    title.value = t('system.job.356378-49');
  });
}

function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (form.value.jobId != undefined) {
        updateJob(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('updateSuccess'));
          open.value = false;
          getList();
        });
      } else {
        addJob(form.value).then(() => {
          (proxy as any).$modal.msgSuccess(t('addSuccess'));
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row?: any) {
  const jobIds = row?.jobId || ids.value;
  (proxy as any).$modal
    .confirm(t('system.job.356378-50', [jobIds]))
    .then(() => {
      return delJob(jobIds);
    })
    .then(() => {
      getList();
      (proxy as any).$modal.msgSuccess(t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function handleExport() {
  (proxy as any).download('monitor/job/export', { ...queryParams }, `job_${new Date().getTime()}.xlsx`);
}

function getRowKeys(row: any) {
  return row.jobId;
}

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.monitor-job {
  padding: 20px;

  .search-card {
    margin-bottom: 15px;
    padding: 3px 0;
  }

  .search-form {
    margin-bottom: -22.5px;
  }
  :deep(.el-button.is-link) {
    padding: 0 !important;
    min-height: 22px;
  }

  .op-actions {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    line-height: 1;

    :deep(.el-button) {
      margin: 0 !important;
      vertical-align: middle;
    }

    .op-more {
      display: inline-flex;
      align-items: center;

      :deep(.el-dropdown-link),
      :deep(.el-button) {
        display: inline-flex;
        align-items: center;
      }
    }
  }
}
</style>
