<template>
  <div class="device-timer-wrap">
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch" label-width="70px">
      <el-form-item prop="jobName">
        <el-input
          v-model="queryParams.jobName"
          :placeholder="proxy?.$t('device.device-timer.433369-1')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item prop="status">
        <el-select
          v-model="queryParams.status"
          :placeholder="proxy?.$t('device.device-timer.433369-3')"
          clearable
          style="width: 200px"
        >
          <el-option v-for="dict in sys_job_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">
          {{ proxy?.$t('device.device-timer.433369-4') }}
        </el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ proxy?.$t('device.device-timer.433369-5') }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" style="margin-bottom: 16px">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          :icon="Plus"
          size="small"
          @click="handleAdd"
          v-hasPermi="['iot:device:timer:add']"
        >
          {{ proxy?.$t('add') }}
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          plain
          :icon="Delete"
          size="small"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['iot:device:timer:remove']"
        >
          {{ proxy?.$t('del') }}
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jobList" @selection-change="handleSelectionChange" :border="false">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column
        :label="proxy?.$t('device.device-timer.433369-7')"
        align="left"
        prop="jobName"
        :show-overflow-tooltip="true"
        min-width="160"
      />
      <el-table-column :label="proxy?.$t('device.device-timer.433369-8')" align="left" prop="cronText" min-width="160">
        <template #default="scope">
          <div v-html="formatCronDisplay(scope.row)"></div>
        </template>
      </el-table-column>
      <el-table-column
        :label="proxy?.$t('device.device-timer.433369-9')"
        align="left"
        prop="cronExpression"
        :show-overflow-tooltip="true"
        min-width="150"
      />
      <el-table-column
        :label="proxy?.$t('device.device-timer.433369-10')"
        align="left"
        prop="actions"
        :show-overflow-tooltip="true"
        min-width="200"
      >
        <template #default="scope">
          <div v-html="formatActionsDisplay(scope.row.actions)" style="overflow: hidden; white-space: nowrap"></div>
        </template>
      </el-table-column>
      <el-table-column :label="proxy?.$t('device.device-timer.433369-11')" align="center" min-width="100">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="0"
            :inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column fixed="right" :label="proxy?.$t('opation')" align="center" width="280">
        <template #default="scope">
          <el-button
            size="small"
            link
            :icon="View"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['iot:device:timer:query']"
          >
            {{ proxy?.$t('device.device-timer.433369-14') }}
          </el-button>
          <el-button
            size="small"
            link
            :icon="CaretRight"
            @click="handleView(scope.row)"
            v-hasPermi="['iot:device:timer:query']"
          >
            {{ proxy?.$t('device.device-timer.433369-15') }}
          </el-button>
          <el-button
            size="small"
            link
            :icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['iot:device:timer:remove']"
          >
            {{ proxy?.$t('device.device-timer.433369-16') }}
          </el-button>
          <el-button
            size="small"
            link
            :icon="CaretRight"
            @click="handleRun(scope.row)"
            v-hasPermi="['iot:device:timer:execute']"
          >
            {{ proxy?.$t('device.device-timer.433369-17') }}
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

    <!-- 添加或修改定时定时对话框 -->
    <el-dialog
      class="device-timer-config-dialog"
      :title="title"
      v-model="open"
      width="800px"
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item :label="proxy?.$t('device.device-timer.433369-0')" prop="jobName">
          <el-input
            v-model="form.jobName"
            :placeholder="proxy?.$t('device.device-timer.433369-1')"
            style="width: 280px"
          />
        </el-form-item>
        <el-form-item :label="proxy?.$t('device.device-timer.433369-18')" prop="timerTimeValue">
          <el-time-picker
            v-model="timerTimeValue"
            value-format="HH:mm"
            format="HH:mm"
            :placeholder="proxy?.$t('device.device-timer.433369-19')"
            style="width: 280px"
            @change="timeChange"
            :disabled="form.isAdvance == 1"
          ></el-time-picker>
        </el-form-item>
        <el-form-item :label="proxy?.$t('device.device-timer.433369-20')" prop="timerWeek">
          <el-row>
            <el-col :span="18">
              <el-select
                v-model="timerWeekValue"
                :placeholder="proxy?.$t('device.device-timer.433369-21')"
                multiple
                style="width: 100%"
                @change="weekChange"
                :disabled="form.isAdvance == 1"
              >
                <el-option
                  v-for="dict in variable_operation_week"
                  :key="dict.value"
                  :label="dict.label"
                  :value="Number(dict.value)"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item :label="proxy?.$t('device.device-timer.433369-22')" prop="cron">
          <el-row>
            <el-col :span="18">
              <el-input
                v-model="form.cronExpression"
                :placeholder="proxy?.$t('device.device-timer.433369-23')"
                :disabled="form.isAdvance == 0"
              >
                <template #append>
                  <el-button type="primary" @click="handleShowCron" :disabled="form.isAdvance == 0">
                    {{ proxy?.$t('device.device-timer.433369-24') }}
                    <el-icon><Timer /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </el-col>
            <el-col :span="4" :offset="1">
              <el-checkbox v-model="form.isAdvance" :true-value="1" :false-value="0" @change="customerCronChange">
                {{ proxy?.$t('device.device-timer.433369-25') }}
              </el-checkbox>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item :label="proxy?.$t('device.device-timer.433369-2')" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in sys_job_status" :key="dict.value" :value="Number(dict.value)">
              {{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <div style="height: 1px; background-color: #ddd; margin: 0 0 20px 0"></div>
        <el-form-item class="action-wrap" :label="proxy?.$t('device.device-timer.433369-26')" prop="actions">
          <div class="item-wrap" v-for="(actionItem, index) in actionList" :key="index + 'action'">
            <el-row :gutter="16">
              <el-col :span="5">
                <el-select
                  class="action-select"
                  v-model="actionItem.type"
                  :placeholder="proxy?.$t('device.device-timer.433369-27')"
                  @change="handleActionTypeChange($event, index)"
                >
                  <el-option
                    v-for="(subItem, subIndex) in modelTypes"
                    :key="subIndex + 'type'"
                    :label="subItem.label"
                    :value="subItem.value"
                  ></el-option>
                </el-select>
              </el-col>
              <el-col :span="10">
                <el-select
                  class="action-select"
                  v-model="actionItem.parentId"
                  :placeholder="proxy?.$t('device.device-timer.433369-28')"
                  v-if="actionItem.type == 1"
                  @change="handleActionParentModelChange($event, index)"
                >
                  <el-option
                    v-for="(subItem, subIndex) in thingsModel.properties"
                    :key="subIndex + 'property'"
                    :label="subItem.name"
                    :value="subItem.id"
                  ></el-option>
                </el-select>
                <el-select
                  class="action-select"
                  v-model="actionItem.parentId"
                  :placeholder="proxy?.$t('device.device-timer.433369-28')"
                  v-else-if="actionItem.type == 2"
                  @change="handleActionParentModelChange($event, index)"
                >
                  <el-option
                    v-for="(subItem, subIndex) in thingsModel.functions"
                    :key="subIndex + 'func'"
                    :label="subItem.name"
                    :value="subItem.id"
                  ></el-option>
                </el-select>
              </el-col>
              <div class="delete-wrap">
                <el-button
                  v-if="index !== 0"
                  size="small"
                  plain
                  type="danger"
                  style="padding: 5px"
                  :icon="Delete"
                  @click="handleRemoveActionItem(index)"
                >
                  {{ proxy?.$t('device.device-timer.433369-16') }}
                </el-button>
              </div>
            </el-row>

            <!--数组索引/物模型/值-->
            <el-row :gutter="16">
              <el-col :span="5" v-if="actionItem.parentModel && actionItem.parentModel.datatype.type === 'array'">
                <el-select
                  v-model="actionItem.arrayIndex"
                  :placeholder="proxy?.$t('device.device-timer.433369-21')"
                  style="width: 180px"
                  @change="handleActionIndexChange($event, index)"
                >
                  <el-option
                    v-for="subItem in actionItem.parentModel.datatype.arrayModel"
                    :key="subItem.id"
                    :label="subItem.name"
                    :value="subItem.id"
                  ></el-option>
                </el-select>
              </el-col>
              <el-col
                :span="5"
                v-if="
                  actionItem.parentModel &&
                  actionItem.parentModel.datatype.type === 'array' &&
                  actionItem.parentModel.datatype.arrayType === 'object'
                "
              >
                <el-select
                  v-model="actionItem.id"
                  :placeholder="proxy?.$t('device.device-timer.433369-21')"
                  style="width: 180px"
                  @change="handleActionModelChange($event, index)"
                >
                  <el-option
                    v-for="(subItem, subIndex) in actionItem.parentModel.datatype.params"
                    :key="subIndex"
                    :label="subItem.name"
                    :value="subItem.id"
                  ></el-option>
                </el-select>
              </el-col>
              <el-col :span="5" v-if="actionItem.parentModel && actionItem.parentModel.datatype.type === 'object'">
                <el-select
                  v-model="actionItem.id"
                  :placeholder="proxy?.$t('device.device-timer.433369-21')"
                  style="width: 180px"
                  @change="handleActionModelChange($event, index)"
                >
                  <el-option
                    v-for="(subItem, subIndex) in actionItem.parentModel.datatype.params"
                    :key="subIndex"
                    :label="subItem.name"
                    :value="subItem.id"
                  ></el-option>
                </el-select>
              </el-col>
              <el-col :span="10" v-if="actionItem.model">
                <div v-if="actionItem.model.datatype.type == 'integer' || actionItem.model.datatype.type == 'decimal'">
                  <el-input
                    style="vertical-align: baseline"
                    v-model="actionItem.value"
                    :placeholder="proxy?.$t('device.device-timer.433369-29')"
                    :max="actionItem.model.datatype.max"
                    :min="actionItem.model.datatype.min"
                    type="number"
                  >
                    <template #append>{{ actionItem.model.datatype.unit }}</template>
                  </el-input>
                </div>
                <div v-else-if="actionItem.model.datatype.type == 'bool'">
                  <el-switch
                    style="vertical-align: baseline"
                    v-model="actionItem.value"
                    :active-text="actionItem.model.datatype.trueText"
                    :inactive-text="actionItem.model.datatype.falseText"
                    :active-value="1"
                    :inactive-value="0"
                  ></el-switch>
                </div>
                <div v-else-if="actionItem.model.datatype.type == 'enum'">
                  <el-select
                    v-model="actionItem.value"
                    :placeholder="proxy?.$t('device.device-timer.433369-21')"
                    style="width: 180px"
                  >
                    <el-option
                      v-for="(subItem, subIndex) in actionItem.model.datatype.enumList"
                      :key="subIndex + 'things'"
                      :label="subItem.text"
                      :value="subItem.value"
                    ></el-option>
                  </el-select>
                </div>
                <div v-else-if="actionItem.model.datatype.type == 'string'">
                  <el-input
                    v-model="actionItem.value"
                    :placeholder="proxy?.$t('device.device-timer.433369-30')"
                    :max="actionItem.model.datatype.maxLength"
                    style="width: 180px"
                  />
                </div>
              </el-col>
            </el-row>
          </div>
          <div>
            +
            <a style="color: #486ff2" @click="handleAddActionItem()">
              {{ proxy?.$t('device.device-timer.433369-31') }}
            </a>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="handleSubmitForm"
            :loading="submitButtonLoading"
            v-hasPermi="['iot:device:timer:add']"
            v-show="!form.jobId"
          >
            {{ proxy?.$t('device.device-timer.433369-32') }}
          </el-button>
          <el-button
            type="primary"
            @click="handleSubmitForm"
            :loading="submitButtonLoading"
            v-hasPermi="['iot:device:timer:edit']"
            v-show="form.jobId"
          >
            {{ proxy?.$t('device.device-timer.433369-33') }}
          </el-button>
          <el-button @click="handleCancel">{{ proxy?.$t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      :title="proxy?.$t('device.device-timer.433369-35')"
      v-model="openCron"
      append-to-body
      destroy-on-close
      class="scrollbar"
    >
      <crontab
        @hide="openCron = false"
        @fill="crontabFill"
        :expression="expression"
        style="padding-bottom: 80px"
      ></crontab>
    </el-dialog>

    <!-- 定时日志详细 -->
    <el-dialog :title="proxy?.$t('device.device-timer.433369-15')" v-model="openView" width="700px" append-to-body>
      <el-form ref="viewFormRef" :model="form" label-width="120px" size="small">
        <el-row>
          <el-col :span="12">
            <el-form-item :label="proxy?.$t('device.device-timer.433369-36')">{{ form.jobId }}</el-form-item>
            <el-form-item :label="proxy?.$t('device.device-timer.433369-37')">{{ form.jobName }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="proxy?.$t('device.device-timer.433369-38')">{{ jobGroupFormat(form) }}</el-form-item>
            <el-form-item :label="proxy?.$t('device.device-timer.433369-39')">{{ form.createTime }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="proxy?.$t('device.device-timer.433369-40')">
              <div v-if="form.concurrent == 0">{{ proxy?.$t('device.device-timer.433369-41') }}</div>
              <div v-else-if="form.concurrent == 1">{{ proxy?.$t('device.device-timer.433369-42') }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="proxy?.$t('device.device-timer.433369-43')">{{ form.cronExpression }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="proxy?.$t('device.device-timer.433369-44')">
              <div v-if="form.misfirePolicy == 0">{{ proxy?.$t('device.device-timer.433369-45') }}</div>
              <div v-else-if="form.misfirePolicy == 1">{{ proxy?.$t('device.device-timer.433369-46') }}</div>
              <div v-else-if="form.misfirePolicy == 2">{{ proxy?.$t('device.device-timer.433369-17') }}</div>
              <div v-else-if="form.misfirePolicy == 3">{{ proxy?.$t('device.device-timer.433369-47') }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="proxy?.$t('device.device-timer.433369-48')">
              {{ proxy?.parseTime(form.nextValidTime) }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="proxy?.$t('device.device-timer.433369-49')">
              <div v-if="form.status == 0">{{ proxy?.$t('device.device-timer.433369-50') }}</div>
              <div v-else-if="form.status == 1">{{ proxy?.$t('device.device-timer.433369-51') }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="proxy?.$t('device.device-timer.433369-52')">
              <div
                v-html="formatActionsDisplay(form.actions)"
                style="border: 1px solid #ddd; padding: 10px; border-radius: 5px; width: 465px"
              ></div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="openView = false">{{ proxy?.$t('device.device-timer.433369-53') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, getCurrentInstance, onMounted } from 'vue';
import { listJob, getJob, delJob, addJob, updateJob, runJob, changeJobStatus } from '@/api/iot/deviceJob';
import { Search, Refresh, Plus, Delete, View, CaretRight, Timer } from '@element-plus/icons-vue';
import { selectDictLabel } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;

const { sys_job_group, sys_job_status, variable_operation_week } = useDict(
  'sys_job_group',
  'sys_job_status',
  'variable_operation_week'
);

const props = defineProps({
  device: { type: Object, default: null },
});

const deviceInfo = ref<any>({});
const actionList = ref<any[]>([]);
const thingsModel = ref<any>({});
const loading = ref(false);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const jobList = ref<any[]>([]);
const timerWeekValue = ref<number[]>([]);
const timerTimeValue = ref('');
const title = ref('');
const open = ref(false);
const openView = ref(false);
const openCron = ref(false);
const expression = ref('');
const submitButtonLoading = ref(false);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  deviceId: 0,
  jobName: undefined as any,
  jobGroup: undefined as any,
  status: undefined as any,
});
const modelTypes = ref([
  { value: 1, label: proxy?.$t('device.device-timer.433369-61') },
  { value: 2, label: proxy?.$t('device.device-timer.433369-62') },
]);
const form = ref<any>({});
const rules = reactive({
  jobName: [{ required: true, message: proxy?.$t('device.device-timer.433369-63'), trigger: 'blur' }],
});

const queryFormRef = ref<any>(null);
const formRef = ref<any>(null);

watch(
  () => props.device,
  (newVal) => {
    if (newVal) {
      deviceInfo.value = newVal;
      initThingsModel();
    }
  }
);

onMounted(() => {
  getList();
  deviceInfo.value = props.device;
  initThingsModel();
});

function initThingsModel() {
  if (deviceInfo.value && deviceInfo.value.deviceId !== 0) {
    thingsModel.value = formatArrayIndex(deviceInfo.value.cacheThingsModel);
    if (thingsModel.value.properties && thingsModel.value.properties.length !== 0) {
      thingsModel.value.properties = thingsModel.value.properties.filter((item: any) => {
        if (item.datatype.params && item.datatype.params.length !== 0) {
          item.datatype.params = item.datatype.params.filter((p: any) => p.isMonitor == 0 && p.isReadonly == 0);
        }
        return item.isMonitor == 0 && item.isReadonly == 0;
      });
    }
    if (thingsModel.value.functions && thingsModel.value.functions.length !== 0) {
      thingsModel.value.functions = thingsModel.value.functions.filter((item: any) => {
        if (item.datatype.params && item.datatype.params.length !== 0) {
          item.datatype.params = item.datatype.params.filter((p: any) => p.isMonitor == 0 && p.isReadonly == 0);
        }
        return item.isMonitor == 0 && item.isReadonly == 0;
      });
    }
    queryParams.deviceId = deviceInfo.value.deviceId;
  }
}

function getList() {
  loading.value = true;
  listJob(queryParams).then((response: any) => {
    jobList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function jobGroupFormat(row: any) {
  return selectDictLabel(sys_job_group.value, row.jobGroup);
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy?.resetForm(queryFormRef.value);
  handleQuery();
}

/** 导出按钮操作 */
function handleExport() {
  proxy?.download('iot/job/export', { ...queryParams }, `job_${new Date().getTime()}.xlsx`);
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.jobId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleStatusChange(row: any) {
  const text =
    row.status === 0 ? proxy?.$t('device.device-timer.433369-12') : proxy?.$t('device.device-timer.433369-64');
  proxy?.$modal
    .confirm(proxy?.$t('device.device-timer.433369-65', [text + '""' + row.jobName]))
    .then(() => changeJobStatus(row.jobId, row.status))
    .then(() => {
      proxy?.$modal.msgSuccess(text + proxy?.$t('device.device-timer.433369-67'));
    })
    .catch(() => {
      row.status = row.status === 0 ? 1 : 0;
    });
}

function handleRun(row: any) {
  proxy?.$modal
    .confirm(proxy?.$t('device.device-timer.433369-68', [row.jobName]))
    .then(() => runJob(row.jobId, row.jobGroup))
    .then(() => {
      proxy?.$modal.msgSuccess(proxy?.$t('device.device-timer.433369-69'));
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

function handleAdd() {
  reset();
  open.value = true;
  title.value = proxy?.$t('device.device-timer.433369-70');
}

function handleCancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    jobId: undefined,
    jobName: undefined,
    cronExpression: undefined,
    status: 0,
    jobGroup: 'DEFAULT',
    misfirePolicy: 2,
    concurrent: 1,
    isAdvance: 0,
    jobType: 1,
    productId: 0,
    productName: '',
    sceneId: 0,
    alertId: 0,
    actions: '',
  };
  submitButtonLoading.value = false;
  timerWeekValue.value = [1, 2, 3, 4, 5, 6, 7];
  timerTimeValue.value = '';
  actionList.value = [
    {
      id: '',
      name: '',
      value: '',
      valueName: '',
      type: 1,
      parentId: '',
      parentName: '',
      arrayIndex: '',
      arrayIndexName: '',
      model: null,
    },
  ];
  proxy?.resetForm(formRef.value);
}

function handleUpdate(row: any) {
  reset();
  const jobId = row.jobId || ids.value;
  getJob(jobId).then((response: any) => {
    form.value = response.data;
    actionList.value = JSON.parse(form.value.actions);
    for (let i = 0; i < actionList.value.length; i++) {
      if (actionList.value[i].type == 1) {
        setParentAndModelData(actionList.value[i], thingsModel.value.properties);
      } else if (actionList.value[i].type == 2) {
        setParentAndModelData(actionList.value[i], thingsModel.value.functions);
      }
    }
    if (form.value.isAdvance == 0) {
      let arrayValue = form.value.cronExpression.substring(12).split(',').map(Number);
      timerWeekValue.value = arrayValue.map((num: number) => (num === 1 ? 7 : num - 1));
      timerTimeValue.value =
        form.value.cronExpression.substring(5, 7) + ':' + form.value.cronExpression.substring(2, 4);
    }
    open.value = true;
    title.value = proxy?.$t('device.device-timer.433369-71');
  });
}

function setParentAndModelData(sceneScript: any, sceneScripts: any[]) {
  for (let i = 0; i < sceneScripts.length; i++) {
    if (sceneScript.parentId == sceneScripts[i].id) {
      sceneScript.parentModel = sceneScripts[i];
      if (sceneScript.parentModel.datatype.type === 'object') {
        for (let j = 0; j < sceneScript.parentModel.datatype.params.length; j++) {
          if (sceneScript.id == sceneScript.parentModel.datatype.params[j].id) {
            sceneScript.model = sceneScript.parentModel.datatype.params[j];
          }
        }
      } else if (
        sceneScript.parentModel.datatype.arrayType === 'object' &&
        sceneScript.parentModel.datatype.type === 'array'
      ) {
        if (sceneScript.id.indexOf('array_') != -1) {
          sceneScript.id = sceneScript.id.substring(9);
        }
        for (let j = 0; j < sceneScript.parentModel.datatype.params.length; j++) {
          if (sceneScript.id == sceneScript.parentModel.datatype.params[j].id) {
            sceneScript.model = sceneScript.parentModel.datatype.params[j];
          }
        }
      } else if (
        sceneScript.parentModel.datatype.arrayType !== 'object' &&
        sceneScript.parentModel.datatype.type === 'array'
      ) {
        if (sceneScript.id.indexOf('array_') != -1) {
          sceneScript.id = sceneScript.id.substring(9);
        }
        sceneScript.model = {
          datatype: {
            type: sceneScript.parentModel.datatype.arrayType,
            maxLength: -1,
            min: -1,
            max: -1,
            unit: proxy?.$t('device.device-timer.433369-72'),
          },
        };
      } else {
        sceneScript.model = sceneScript.parentModel;
      }
      break;
    }
  }
}

function handleDelete(row?: any) {
  const jobIds = row?.jobId || ids.value;
  proxy?.$modal
    .confirm(proxy?.$t('device.device-timer.433369-73', [jobIds]))
    .then(() => delJob(jobIds))
    .then(() => {
      getList();
      proxy?.$modal.msgSuccess(proxy?.$t('device.device-timer.433369-75'));
    })
    .catch(() => {});
}

function weekChange() {
  gentCronExpression();
}

function timeChange() {
  gentCronExpression();
}

function customerCronChange(data: any) {
  if (data == 0) {
    gentCronExpression();
  }
}

function gentCronExpression() {
  let hour = '00';
  let minute = '00';
  if (timerTimeValue.value != null && timerTimeValue.value != '') {
    hour = timerTimeValue.value.substring(0, 2);
    minute = timerTimeValue.value.substring(3);
  }
  let week: any = '*';
  if (timerWeekValue.value.length > 0) {
    let order = timerWeekValue.value.slice().sort();
    for (let i = 0; i < order.length; i++) {
      if (order[i] == 7) {
        order[i] = 1;
      } else {
        order[i] = order[i] + 1;
      }
    }
    week = order;
  }
  form.value.cronExpression = '0 ' + minute + ' ' + hour + ' ? * ' + week;
}

function formatCronDisplay(item: any) {
  let result = '';
  if (item.isAdvance == 0) {
    let time =
      '<br /><span style="color:#F56C6C">时间 ' +
      item.cronExpression.substring(5, 7) +
      ':' +
      item.cronExpression.substring(2, 4) +
      '</span>';
    let week = item.cronExpression.substring(12);
    if (week == '1,2,3,4,5,6,7') {
      result = '每天 ' + time;
    } else {
      let weekArray = week.split(',');
      for (let i = 0; i < weekArray.length; i++) {
        if (weekArray[i] == '2') result += proxy?.$t('device.device-timer.433369-78');
        else if (weekArray[i] == '3') result += proxy?.$t('device.device-timer.433369-79');
        else if (weekArray[i] == '4') result += proxy?.$t('device.device-timer.433369-80');
        else if (weekArray[i] == '5') result += proxy?.$t('device.device-timer.433369-81');
        else if (weekArray[i] == '6') result += proxy?.$t('device.device-timer.433369-82');
        else if (weekArray[i] == '7') result += proxy?.$t('device.device-timer.433369-83');
        else if (weekArray[i] == '1') result += proxy?.$t('device.device-timer.433369-84');
      }
      result = result.substring(0, result.length - 1) + ' ' + time;
    }
  } else {
    result = proxy?.$t('device.device-timer.433369-85');
  }
  return result;
}

function formatActionsDisplay(json: string) {
  if (json == null || json == '') return;
  let actions = JSON.parse(json);
  let result = '';
  for (let i = 0; i < actions.length; i++) {
    if (actions[i].arrayIndexName) {
      result += `${actions[i].parentName} >> ${actions[i].arrayIndexName} >> ${actions[i].name} <span style="color:#F56C6C"> ${actions[i].valueName ? actions[i].valueName : actions[i].value}</span><br />`;
    } else {
      if (actions[i].parentName !== actions[i].name) {
        result += `${actions[i].parentName} >> ${actions[i].name} <span style="color:#F56C6C">${actions[i].valueName ? actions[i].valueName : actions[i].value}</span><br />`;
      } else {
        result += `${actions[i].name} <span style="color:#F56C6C">${actions[i].valueName ? actions[i].valueName : actions[i].value}</span><br />`;
      }
    }
  }
  return result == '' ? proxy?.$t('device.device-timer.433369-86') : result;
}

function formatArrayIndex(data: any) {
  let obj = { ...data };
  for (let o in obj) {
    obj[o] = obj[o].map((item: any) => {
      if (item.datatype.type === 'array') {
        let arrayModel = [];
        for (let k = 0; k < item.datatype.arrayCount; k++) {
          let index = k > 9 ? String(k) : '0' + k;
          arrayModel.push({ id: index, name: item.name + ' ' + (k + 1) });
        }
        item.datatype.arrayModel = arrayModel;
      }
      return item;
    });
  }
  return obj;
}

function handleAddActionItem() {
  actionList.value.push({
    id: '',
    name: '',
    value: '',
    valueName: '',
    type: 1,
    parentId: '',
    parentName: '',
    arrayIndex: '',
    arrayIndexName: '',
    model: null,
  });
}

function handleRemoveActionItem(index: number) {
  actionList.value.splice(index, 1);
}

function handleActionTypeChange(data: any, index: number) {
  actionList.value[index] = {
    ...actionList.value[index],
    id: '',
    name: '',
    value: '',
    valueName: '',
    parentId: '',
    parentName: '',
    arrayIndex: '',
    arrayIndexName: '',
    parentModel: null,
    model: null,
    type: data,
  };
}

function handleActionIndexChange(id: string, index: number) {
  actionList.value[index].arrayIndexName = actionList.value[index].parentModel.datatype.arrayModel.find(
    (x: any) => x.id == id
  ).name;
  actionList.value[index].value = '';
  if (actionList.value[index].parentModel.datatype.arrayType === 'object') {
    actionList.value[index].id = '';
    actionList.value[index].name = '';
  }
}

function handleActionParentModelChange(identifier: string, index: number) {
  actionList.value[index].model = null;
  actionList.value[index].value = '';
  actionList.value[index].arrayIndex = '';
  actionList.value[index].arrayIndexName = '';

  let scripts: any[] = [];
  if (actionList.value[index].type == 1) {
    scripts = thingsModel.value.properties;
  } else if (actionList.value[index].type == 2) {
    scripts = thingsModel.value.functions;
  }
  for (let i = 0; i < scripts.length; i++) {
    if (scripts[i].id == identifier) {
      actionList.value[index].parentName = scripts[i].name;
      actionList.value[index].parentModel = scripts[i];
      if (scripts[i].datatype.type === 'object') {
        actionList.value[index].id = '';
        actionList.value[index].name = '';
      } else if (scripts[i].datatype.type === 'array' && scripts[i].datatype.arrayType === 'object') {
        actionList.value[index].id = '';
        actionList.value[index].name = '';
      } else if (scripts[i].datatype.type === 'array' && scripts[i].datatype.arrayType !== 'object') {
        actionList.value[index].id = scripts[i].id;
        actionList.value[index].name = scripts[i].name;
        actionList.value[index].model = {
          datatype: {
            type: scripts[i].datatype.arrayType,
            maxLength: -1,
            min: -1,
            max: -1,
            unit: proxy?.$t('device.device-timer.433369-72'),
          },
        };
      } else {
        actionList.value[index].id = scripts[i].id;
        actionList.value[index].name = scripts[i].name;
        actionList.value[index].model = scripts[i];
      }
      break;
    }
  }
}

function handleActionModelChange(id: string, index: number) {
  actionList.value[index].value = '';
  if (
    actionList.value[index].parentModel.datatype.type === 'array' ||
    actionList.value[index].parentModel.datatype.type === 'object'
  ) {
    const model = actionList.value[index].parentModel.datatype.params.find((item: any) => item.id == id);
    actionList.value[index].name = model.name;
    actionList.value[index].model = model;
  }
}

function handleSubmitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      let actions: any[] = [];
      if (form.value.isAdvance == 0) {
        if (timerTimeValue.value == '' || timerTimeValue.value == null) {
          proxy?.$modal.alertError(proxy?.$t('device.device-timer.433369-87'));
          return;
        }
        if (timerWeekValue.value == null || timerWeekValue.value.length === 0) {
          proxy?.$modal.alertError(proxy?.$t('device.device-timer.433369-88'));
          return;
        }
      } else if (form.value.isAdvance == 1) {
        if (form.value.cronExpression == '') {
          proxy?.$modal.alertError(proxy?.$t('device.device-timer.433369-89'));
          return;
        }
      }
      for (let i = 0; i < actionList.value.length; i++) {
        if (actionList.value[i].value === '') {
          proxy?.$modal.alertError(proxy?.$t('device.device-timer.433369-90'));
          return;
        }
        const item = actionList.value[i];
        let id = item.arrayIndex != '' ? 'array_' + item.arrayIndex + '_' + item.id : item.id;
        let valueName = '';
        if (item.model.datatype.type === 'bool') {
          valueName = item.value === 1 ? item.model.datatype.trueText : item.model.datatype.falseText;
        } else if (item.model.datatype.type === 'enum') {
          valueName = item.model.datatype.enumList.find((subItem: any) => subItem.value === item.value)?.text || '';
        }
        actions[i] = {
          type: item.type,
          id: item.id,
          name: item.name,
          value: item.value,
          valueName: valueName,
          parentId: item.parentId,
          parentName: item.parentName,
          arrayIndex: item.arrayIndex,
          arrayIndexName: item.arrayIndexName,
          deviceId: deviceInfo.value.deviceId,
          deviceName: deviceInfo.value.deviceName,
        };
      }
      form.value.actions = JSON.stringify(actions);
      form.value.deviceId = deviceInfo.value.deviceId;
      form.value.deviceName = deviceInfo.value.deviceName;
      form.value.serialNumber = deviceInfo.value.serialNumber;
      form.value.productId = deviceInfo.value.productId;
      form.value.productName = deviceInfo.value.productName;
      form.value.protocolCode = deviceInfo.value.protocolCode;
      submitButtonLoading.value = true;
      if (form.value.jobId != undefined) {
        updateJob(form.value).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('device.device-timer.433369-91'));
          submitButtonLoading.value = false;
          open.value = false;
          getList();
        });
      } else {
        addJob(form.value).then(() => {
          proxy?.$modal.msgSuccess(proxy?.$t('device.device-timer.433369-92'));
          submitButtonLoading.value = false;
          open.value = false;
          getList();
        });
      }
    }
  });
}

defineExpose({ getList });
</script>

<style lang="scss" scoped>
.device-timer-wrap {
  padding-bottom: 20px;
}

.device-timer-config-dialog {
  .action-wrap {
    position: relative;

    :deep(.el-form-item__content) {
      display: block;
      width: 100%;
    }

    .item-wrap {
      margin-bottom: 12px;
      padding: 10px 12px;
      background-color: #d9e5f6;
      border-radius: 6px;

      :deep(.el-row) {
        margin-left: 0 !important;
        margin-right: 0 !important;
      }

      :deep(.el-select) {
        width: 100% !important;
      }

      .delete-wrap {
        position: absolute;
        right: 12px;
        top: 6px;
      }
    }

    > div:last-child {
      margin-top: 10px;
      margin-left: 2px;
    }
  }
}
</style>
