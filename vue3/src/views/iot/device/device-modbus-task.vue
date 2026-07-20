<template>
  <div class="instruction-parsing">
    <el-form
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      v-show="showSearch"
      label-width="70px"
      v-if="device.protocolCode !== 'MODBUS-TCP'"
    >
      <el-form-item prop="jobName">
        <el-input
          v-model="queryParams.jobName"
          :placeholder="proxy?.$t('device.device-modbus-task.384302-1')"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item prop="status">
        <el-select
          v-model="queryParams.status"
          :placeholder="proxy?.$t('device.device-modbus-task.384302-3')"
          clearable
          @keyup.enter="handleQuery"
        >
          <el-option v-for="dict in sys_job_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">
          {{ proxy?.$t('device.device-modbus-task.384302-4') }}
        </el-button>
        <el-button :icon="Refresh" @click="resetQuery">{{ proxy?.$t('device.device-modbus-task.384302-5') }}</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" style="margin: 5px 0 10px 10px">
      <el-col :span="1.5" v-if="device.protocolCode !== 'MODBUS-TCP'">
        <el-button type="primary" plain :icon="Plus" size="small" @click="openEdit">
          {{ proxy?.$t('device.device-modbus.433390-1') }}
        </el-button>
      </el-col>
      <el-col :span="4.5" v-else>
        <div class="btn-group">
          <el-button
            type="primary"
            plain
            :icon="Edit"
            size="small"
            @click="setSlave"
            v-if="!enableSetSlave"
            v-hasPermi="['modbus:job:edit']"
          >
            {{ proxy?.$t('product.product-modbus.562372-2') }}
          </el-button>
          <el-button type="primary" plain :icon="Close" size="small" @click="saveSlave" v-if="enableSetSlave">
            {{ proxy?.$t('product.product-modbus.562372-3') }}
          </el-button>
          <el-button type="info" plain :icon="Edit" size="small" @click="cancelSlave" v-if="enableSetSlave">
            {{ proxy?.$t('product.product-modbus.562372-4') }}
          </el-button>
        </div>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="commandList"
      :border="false"
      v-if="(device.deviceType === 1 || device.deviceType === 2) && device.protocolCode === 'MODBUS-TCP'"
    >
      <el-table-column
        :label="proxy?.$t('product.product-modbus-task.894593-71')"
        align="center"
        prop="address"
        width="200px"
      >
        <template #default="scope">
          <el-input-number
            style="width: 100%"
            :disabled="!enableSetSlave"
            v-model="scope.row.address"
            :min="0"
            :max="400000"
            :label="proxy?.$t('product.product-modbus.562372-30')"
          ></el-input-number>
        </template>
      </el-table-column>
      <el-table-column
        :label="proxy?.$t('product.product-modbus-task.894593-72')"
        align="center"
        prop="register"
        width="200px"
      >
        <template #default="scope">
          <el-input-number
            style="width: 100%"
            :disabled="!enableSetSlave"
            v-model="scope.row.register"
            :min="0"
            :max="400000"
            :label="proxy?.$t('product.product-modbus.562372-30')"
          ></el-input-number>
        </template>
      </el-table-column>
      <el-table-column
        :label="proxy?.$t('product.product-modbus-task.894593-73')"
        align="center"
        prop="code"
        width="260px"
      >
        <template #default="scope">
          <el-select v-model="scope.row.code" style="width: 100%" :disabled="!enableSetSlave">
            <el-option
              v-for="dict in product_command_function_code"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column
        :label="proxy?.$t('product.product-modbus-task.894593-74')"
        align="center"
        prop="quantity"
        width="200px"
      >
        <template #default="scope">
          <el-input-number
            style="width: 100%"
            :disabled="!enableSetSlave"
            v-model="scope.row.quantity"
            :min="0"
          ></el-input-number>
        </template>
      </el-table-column>
      <el-table-column
        :label="proxy?.$t('product.product-modbus.562372-34')"
        align="center"
        class-name="small-padding fixed-width"
        fixed="right"
      >
        <template #default="scope">
          <el-button
            size="small"
            link
            :icon="Delete"
            v-if="enableSetSlave"
            @click="handleDelete(scope.row, scope.$index, commandList)"
            v-hasPermi="['modbus:job:remove']"
          >
            {{ proxy?.$t('product.product-modbus.562372-35') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 非直连设备和网关设备，协议也不是modbusTcp -->
    <el-table v-loading="loading" :data="jobList" :border="false" v-else>
      <el-table-column
        :label="proxy?.$t('product.product-modbus-task.894593-56')"
        align="center"
        prop="taskId"
        min-width="100"
      />
      <el-table-column
        :label="proxy?.$t('product.product-modbus-task.894593-7')"
        align="left"
        prop="jobName"
        min-width="180"
      />
      <el-table-column
        :label="proxy?.$t('product.product-modbus-task.894593-57')"
        align="left"
        prop="command"
        min-width="160"
      />
      <el-table-column
        :label="proxy?.$t('product.product-modbus-task.894593-58')"
        align="center"
        prop="status"
        min-width="100"
      >
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="0"
            :inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column
        :label="proxy?.$t('product.product-modbus-task.894593-59')"
        align="center"
        prop="remarkStr"
        min-width="110"
      ></el-table-column>
      <el-table-column
        :label="proxy?.$t('product.product-modbus-task.894593-60')"
        align="center"
        fixed="right"
        width="100"
      >
        <template #default="scope">
          <el-button
            size="small"
            link
            :icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['modbus:job:remove']"
          >
            {{ proxy?.$t('product.product-modbus-task.894593-61') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-if="device.protocolCode !== 'MODBUS-TCP'"
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
    <el-row :gutter="10" class="mb8" style="margin-top: 20px">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" size="small" @click="handleAddData" v-if="enableSetSlave">
          添加
        </el-button>
      </el-col>
    </el-row>

    <el-dialog
      :title="
        editName ? proxy?.$t('device.device-modbus-task.384302-12') : proxy?.$t('device.device-modbus-task.384302-13')
      "
      v-model="editDialog"
      :width="editName ? '800' : '900'"
    >
      <div class="dialog-content">
        <el-form :model="createForm" label-position="top">
          <el-form-item :label="proxy?.$t('device.device-modbus-task.384302-0')" prop="jobName">
            <el-input
              v-model="createForm.jobName"
              :placeholder="proxy?.$t('device.device-modbus-task.384302-1')"
              class="input-item"
            />
          </el-form-item>
          <el-row :gutter="40">
            <!-- 从机地址 -->
            <el-col :span="12">
              <el-form-item :label="proxy?.$t('device.device-modbus-task.384302-14')" prop="address" v-if="!selectPath">
                <el-input v-model="createForm.address" class="input-item" type="number"></el-input>
              </el-form-item>
              <el-form-item :label="proxy?.$t('device.device-modbus-task.384302-14')" prop="address" v-else>
                <el-select v-model="createForm.address" class="input-item">
                  <el-option
                    v-for="address in addressList"
                    :key="address"
                    :label="address"
                    :value="address"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <!-- 功能码 -->
            <el-col :span="12">
              <el-form-item :label="proxy?.$t('device.device-modbus-task.384302-15')" prop="code">
                <el-select v-model="createForm.code" @change="changeNum" class="input-item">
                  <el-option
                    :label="dict.label"
                    :value="dict.value"
                    v-for="dict in product_command_function_code"
                    :key="dict.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <!--起始寄存器地址-->
            <el-col :span="12">
              <el-form-item prop="startPath">
                <template #label>
                  <div class="form-item-label">
                    <div style="margin-right: auto">{{ proxy?.$t('device.device-modbus-task.384302-16') }}</div>
                    <el-tooltip :content="createForm.startPathSwitch" placement="top">
                      <el-switch
                        v-model="createForm.startPathSwitch"
                        size="small"
                        active-color="#13ce66"
                        inactive-color="#ff4949"
                        active-value="Dec"
                        inactive-value="Hex"
                      />
                    </el-tooltip>
                  </div>
                </template>
                <el-input
                  v-model="createForm.startPath"
                  type="number"
                  class="input-item"
                  v-show="createForm.startPathSwitch == 'Dec'"
                  :min="0"
                  @change="
                    () => {
                      createForm.startPath16 = int2hexFn(createForm.startPath);
                    }
                  "
                  @input="
                    () => {
                      createForm.startPath16 = int2hexFn(createForm.startPath);
                    }
                  "
                >
                  <template #append>0x{{ createForm.startPath16 }}</template>
                </el-input>
                <el-input
                  v-model="createForm.startPath16"
                  v-show="createForm.startPathSwitch != 'Dec'"
                  @input="
                    () => {
                      createForm.startPath = hex2intFn(createForm.startPath16);
                    }
                  "
                >
                  <template #append>{{ createForm.startPath }}</template>
                </el-input>
              </el-form-item>
            </el-col>
            <!-- 个数或写值 -->
            <el-col :span="12">
              <el-form-item
                :label="registerNumTitle"
                prop="registerNum"
                v-show="!['05', '06'].includes(createForm.code)"
              >
                <el-input-number
                  v-model="createForm.registerNum"
                  controls-position="right"
                  :min="0"
                  class="input-item"
                  @change="changeNum"
                />
              </el-form-item>
              <el-form-item prop="setValue" v-show="['05', '06'].includes(createForm.code)">
                <template #label>
                  <div class="form-item-label">
                    <div style="margin-right: auto">{{ registerNumTitle }}</div>
                    <el-tooltip :content="createForm.setValueSwitch" placement="top">
                      <el-switch
                        v-model="createForm.setValueSwitch"
                        size="small"
                        active-color="#13ce66"
                        inactive-color="#ff4949"
                        active-value="Dec"
                        inactive-value="Hex"
                      />
                    </el-tooltip>
                  </div>
                </template>
                <el-input
                  v-model="createForm.setValue"
                  type="number"
                  v-show="createForm.setValueSwitch == 'Dec'"
                  @change="
                    () => {
                      createForm.setValue16 = int2hexFn(createForm.setValue);
                    }
                  "
                  @input="
                    () => {
                      createForm.setValue16 = int2hexFn(createForm.setValue);
                    }
                  "
                >
                  <template #append>0x{{ createForm.setValue16 }}</template>
                </el-input>
                <el-input
                  v-model="createForm.setValue16"
                  v-show="createForm.setValueSwitch != 'Dec'"
                  @input="
                    () => {
                      createForm.setValue = hex2intFn(createForm.setValue16);
                    }
                  "
                >
                  <template #append>{{ createForm.setValue }}</template>
                </el-input>
              </el-form-item>
            </el-col>
            <!-- 批量写寄存器值 -->
            <el-col
              :span="12"
              v-for="(item, index) in registerValList"
              :key="'register' + index"
              v-show="createForm.code == '16'"
            >
              <el-form-item prop="registerValList">
                <template #label>
                  <div class="form-item-label">
                    <div style="margin-right: auto">
                      #{{ index }} {{ proxy?.$t('device.device-modbus-task.384302-17') }}
                    </div>
                    <el-tooltip :content="item.switch" placement="top">
                      <el-switch
                        v-model="item.switch"
                        size="small"
                        active-color="#13ce66"
                        @change="
                          () => {
                            refreshRegisterInpust(item, index);
                          }
                        "
                        inactive-color="#ff4949"
                        active-value="Dec"
                        inactive-value="Hex"
                      />
                    </el-tooltip>
                  </div>
                </template>
                <el-input
                  v-model="item.value"
                  type="number"
                  v-show="item.switch == 'Dec'"
                  :min="0"
                  @change="
                    () => {
                      item.value16 = int2hexFn(item.value);
                      refreshRegisterInpust(item, index);
                    }
                  "
                  @input="
                    () => {
                      item.value16 = int2hexFn(item.value);
                      refreshRegisterInpust(item, index);
                    }
                  "
                >
                  <template #append>0x{{ item.value16 }}</template>
                </el-input>
                <el-input
                  v-model="item.value16"
                  v-show="item.switch != 'Dec'"
                  @input="
                    () => {
                      item.value = hex2intFn(item.value16);
                      refreshRegisterInpust(item, index);
                    }
                  "
                >
                  <template #append>{{ item.value }}</template>
                </el-input>
              </el-form-item>
            </el-col>
            <!-- 批量写线圈值 -->
            <el-col :span="6" v-for="(item, index) in IOValList" :key="'IO' + index" v-show="createForm.code == '15'">
              <el-form-item prop="registerValList">
                <template #label>
                  <div class="form-item-label">
                    <div style="margin-right: auto">
                      #{{ index }} {{ proxy?.$t('device.device-modbus-task.384302-18') }}
                    </div>
                  </div>
                </template>
                <el-switch
                  v-model="item.value"
                  active-value="1"
                  inactive-value="0"
                  @change="
                    () => {
                      refreshIOInpust(item, index);
                    }
                  "
                />
              </el-form-item>
            </el-col>
          </el-row>
          <!-- 任务状态 -->
          <el-form-item :label="proxy?.$t('device.device-timer.433369-2')" prop="status">
            <el-radio-group v-model="createForm.status">
              <el-radio v-for="dict in sys_job_status" :key="dict.value" :value="Number(dict.value)">
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <!-- 时间周期 -->
          <el-form-item :label="proxy?.$t('device.device-modbus-task.384302-19')" prop="cycleType">
            <div class="timer-wrap">
              <el-radio-group v-model="createForm.cycleType" @change="handleCycleTypeInput">
                <el-radio :value="1" style="display: block">
                  {{ proxy?.$t('device.device-modbus-task.384302-20') }}
                  <el-tooltip placement="right">
                    <template #content>
                      {{ proxy?.$t('device.device-modbus-task.384302-21') }}
                      <br />
                      {{ proxy?.$t('device.device-modbus-task.384302-22') }}
                    </template>
                    <el-icon><QuestionFilled /></el-icon>
                  </el-tooltip>
                  <div class="timer-period">
                    <span>{{ proxy?.$t('device.device-modbus-task.384302-23') }}</span>
                    <el-select
                      style="width: 100px; margin-left: 10px"
                      v-model="cycles1[0].interval"
                      size="small"
                      :disabled="createForm.cycleType === 2"
                      @change="handleCycleInterval"
                    >
                      <el-option
                        v-for="dict in variable_operation_interval"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                      ></el-option>
                    </el-select>
                    <el-select
                      v-if="cycles1[0].interval === 'week'"
                      style="width: 100px; margin-left: 5px"
                      v-model="cycles1[0].week"
                      size="small"
                      :disabled="createForm.cycleType === 2"
                    >
                      <el-option
                        v-for="dict in variable_operation_week"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                      ></el-option>
                    </el-select>
                    <el-select
                      v-if="cycles1[0].interval === 'month'"
                      style="width: 100px; margin-left: 5px"
                      v-model="cycles1[0].day"
                      size="small"
                      :disabled="createForm.cycleType === 2"
                    >
                      <el-option
                        v-for="dict in variable_operation_day"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                      ></el-option>
                    </el-select>
                    <el-select
                      v-if="
                        cycles1[0].interval === 'day' ||
                        cycles1[0].interval === 'week' ||
                        cycles1[0].interval === 'month'
                      "
                      style="width: 100px; margin-left: 5px"
                      v-model="cycles1[0].time"
                      size="small"
                      :disabled="createForm.cycleType === 2"
                      @change="handleCycleTime"
                    >
                      <el-option
                        v-for="dict in variable_operation_time"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                      ></el-option>
                    </el-select>
                    <span style="margin-left: 10px">{{ proxy?.$t('device.device-modbus-task.384302-24') }}</span>
                  </div>
                </el-radio>
              </el-radio-group>
            </div>
          </el-form-item>
        </el-form>
        <div v-loading="createLoading">
          <div class="create-title">
            <el-button link @click.stop="encodeCmd">{{ proxy?.$t('device.device-modbus-task.384302-25') }}</el-button>
            <div class="title-right">
              <el-button type="primary" size="small" @click="copyTextFn(createCode)">
                {{ proxy?.$t('device.device-modbus-task.384302-26') }}
              </el-button>
            </div>
          </div>
          <div class="create-code">{{ createCode }}</div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-btn">
          <el-button @click="editDialog = false">{{ proxy?.$t('device.device-modbus-task.384302-27') }}</el-button>
          <el-button type="primary" @click="handleAdd">{{ proxy?.$t('confirm') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, getCurrentInstance } from 'vue';
import { hex2int, int2hex, copyText } from '@/utils/common';
import { encode } from '@/api/iot/mqttTest';
import { listJob, getJob, addJob, updateJob, getAddress, delJob } from '@/api/iot/modbusJob';
import { Search, Refresh, Plus, Delete, Edit, Close, QuestionFilled } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;

const {
  sys_job_group,
  sys_job_status,
  variable_operation_interval,
  variable_operation_time,
  variable_operation_week,
  variable_operation_day,
  variable_operation_type,
  product_command_function_code,
} = useDict(
  'sys_job_group',
  'sys_job_status',
  'variable_operation_interval',
  'variable_operation_time',
  'variable_operation_week',
  'variable_operation_day',
  'variable_operation_type',
  'product_command_function_code'
);

const props = defineProps({
  device: { type: Object, default: null },
});

const queryFormRef = ref<any>(null);
const format = ref('Hex');
const loading = ref(false);
const editDialog = ref(false);
const createForm = ref<any>({ cycleType: 1, status: 0 });
const commandList = ref<any[]>([]);
const ids = ref<any[]>([]);
const single = ref(true);
const multiple = ref(true);
const isEnableSwitch = ref(false);
const total = ref(0);
const jobList = ref<any[]>([]);
const showSearch = ref(true);
const createCode = ref('');
const registerValList = ref<any[]>([]);
const IOValList = ref<any[]>([]);
const editName = ref(false);
const editNameForm = ref<any>({});
const createLoading = ref(false);
const delDialog = ref(false);
const delItem = ref<any>({});
const deviceInfo = ref<any>({});
const deviceId = ref('');
const addressList = ref<any[]>([]);
const selectPath = ref(false);
const delDataIds = ref<any[]>([]);
const enableSetSlave = ref(false);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  jobName: null as any,
  deviceId: null as any,
  serialNumber: null as any,
  command: null as any,
  jobId: null as any,
  status: null as any,
  commandType: 1,
});
const cycles1 = ref([{ interval: '300', time: '', week: '', day: '' }]);
const cycles2 = ref([{ type: 'day', time: '00', week: '', day: '', toType: '1', toTime: '02', toWeek: '', toDay: '' }]);

const registerNumTitle = computed(() => {
  switch (createForm.value.code) {
    case '01':
    case '02':
    case '15':
      return proxy?.$t('device.device-modbus-task.384302-29');
    case '03':
    case '04':
    case '16':
      return proxy?.$t('device.device-modbus-task.384302-30');
    case '05':
      return proxy?.$t('device.device-modbus-task.384302-31');
    case '06':
      return proxy?.$t('device.device-modbus-task.384302-32');
    default:
      return '';
  }
});

watch(
  () => props.device,
  (newVal, oldVal) => {
    if (newVal?.deviceId && newVal.deviceId !== oldVal?.deviceId) {
      queryParams.deviceId = newVal.deviceId;
      deviceInfo.value = newVal;
      getList();
    }
  },
  { deep: true }
);

onMounted(() => {
  const { deviceId: dId } = props.device || {};
  if (dId) {
    queryParams.deviceId = dId;
    queryParams.commandType = 1;
    getList();
  }
  resetCreateForm();
});

function getList() {
  loading.value = true;
  listJob(queryParams).then((response: any) => {
    jobList.value = response.rows;
    if (response.total > 0) {
      if (response.rows[0].command.includes('{"register":')) {
        commandList.value = JSON.parse(response.rows[0].command);
      }
      createForm.value.taskId = response.rows[0].taskId;
    } else {
      createForm.value.taskId = '';
    }
    total.value = response.total;
    loading.value = false;
  });
}

function setSlave() {
  enableSetSlave.value = !enableSetSlave.value;
}

function saveSlave() {
  const data = {
    taskId: createForm.value.taskId,
    productId: props.device.productId,
    command: JSON.stringify(commandList.value),
    status: 0,
    commandType: 1,
    deviceId: props.device.deviceId,
    serialNumber: props.device.serialNumber,
  };
  updateJob(data).then(() => {
    proxy?.$modal.msgSuccess(proxy?.$t('product.product-modbus.562372-53'));
    getList();
    setSlave();
  });
}

function handleAddData() {
  commandList.value.push({ register: 0, address: 1, code: '03', quantity: 1 });
}

function cancelSlave() {
  enableSetSlave.value = !enableSetSlave.value;
}

function handleDelete(row: any, index?: number, list?: any[]) {
  if (props.device.protocolCode !== 'MODBUS-TCP') {
    const taskIds = row.taskId || ids.value;
    const params = { taskId: taskIds, jobId: row.jobId };
    proxy?.$modal
      .confirm(proxy?.$t('product.product-modbus-task.894593-64', [taskIds]))
      .then(() => delJob(params))
      .then(() => {
        getList();
        proxy?.$modal.msgSuccess(proxy?.$t('product.product-modbus-task.894593-65'));
      })
      .catch(() => {});
  } else {
    if (list && index !== undefined) {
      list.splice(index, 1);
      delDataIds.value.push(row.id);
    }
  }
}

function handleAdd() {
  let params: any = {
    address: createForm.value.address,
    register: createForm.value.startPath,
    code: parseInt(createForm.value.code),
    protocolCode: props.device.protocolCode,
    serialNumber: props.device.serialNumber,
    commandType: 1,
  };
  switch (createForm.value.code) {
    case '01':
    case '02':
    case '03':
    case '04':
      params.count = createForm.value.registerNum;
      break;
    case '05':
    case '06':
      params.writeData = createForm.value.setValue;
      break;
    case '15':
      params.count = createForm.value.registerNum;
      params.bitString = IOValList.value.map((item) => item.value).join('');
      break;
    case '16':
      params.count = createForm.value.registerNum;
      params.tenWriteData = registerValList.value.map((item) => item.value);
      break;
  }
  encode(params).then((response: any) => {
    createCode.value = response.msg;
    handlePush();
  });
}

function handlePush() {
  const c = cycles1.value.map((item) => {
    if (item.interval === 'hour') return { type: 'hour' };
    else if (item.interval === 'day') return { type: 'day', time: item.time };
    else if (item.interval === 'week') return { type: 'week', week: item.week, time: item.time };
    else if (item.interval === 'month') return { type: 'month', day: item.day, time: item.time };
    else return { interval: item.interval };
  });
  createForm.value.deviceId = props.device.deviceId;
  createForm.value.serialNumber = props.device.serialNumber;
  createForm.value.command = createCode.value;
  createForm.value.remark = JSON.stringify(c);
  submitForm();
  editDialog.value = false;
}

function submitForm() {
  createForm.value.commandType = 1;
  createForm.value.productId = props.device.productId;
  addJob(createForm.value).then(() => {
    proxy?.$modal.msgSuccess(proxy?.$t('device.device-modbus-task.384302-63'));
    getList();
  });
}

function openEdit() {
  resetCreateForm();
  getAddressList();
  editName.value = false;
}

function resetCreateForm() {
  createForm.value = {
    address: '1',
    code: '01',
    startPath: 0,
    startPath16: '0000',
    registerNum: 1,
    startPathSwitch: 'Dec',
    setValue: 0,
    setValue16: '0000',
    setValueSwitch: 'Dec',
    status: 0,
    cycleType: 1,
  };
  createCode.value = '';
}

function int2hexFn(str: any) {
  return int2hex(str);
}
function hex2intFn(str: any) {
  return hex2int(str);
}

function changeNum() {
  if (createForm.value.code == '16') {
    for (let index = 0; index < createForm.value.registerNum; index++) {
      if (!registerValList.value[index]) {
        registerValList.value[index] = { value: 0, value16: '0000', switch: 'Dec' };
      }
    }
    if (registerValList.value.length > createForm.value.registerNum) {
      registerValList.value.splice(
        createForm.value.registerNum,
        registerValList.value.length - createForm.value.registerNum
      );
    }
  }
  if (createForm.value.code == '15') {
    for (let index = 0; index < createForm.value.registerNum; index++) {
      if (!IOValList.value[index]) {
        IOValList.value[index] = { value: '0' };
      }
    }
    if (IOValList.value.length > createForm.value.registerNum) {
      IOValList.value.splice(createForm.value.registerNum, IOValList.value.length - createForm.value.registerNum);
    }
  }
}

function refreshRegisterInpust(item: any, index: number) {
  registerValList.value[index] = { ...item };
}

function refreshIOInpust(item: any, index: number) {
  IOValList.value[index] = { ...item };
}

function copyTextFn(code: string) {
  const res = copyText(code);
  proxy?.$modal.msgSuccess(res.message);
}

async function encodeCmd() {
  try {
    createLoading.value = true;
    let params: any = {
      address: parseInt(createForm.value.address),
      register: createForm.value.startPath,
      code: parseInt(createForm.value.code),
      protocolCode: props.device.protocolCode,
      serialNumber: props.device.serialNumber,
    };
    switch (createForm.value.code) {
      case '01':
      case '02':
      case '03':
      case '04':
        params.count = createForm.value.registerNum;
        break;
      case '05':
      case '06':
        params.writeData = createForm.value.setValue;
        break;
      case '15':
        params.count = createForm.value.registerNum;
        params.bitString = IOValList.value.map((item) => item.value).join('');
        break;
      case '16':
        params.count = createForm.value.registerNum;
        params.tenWriteData = registerValList.value.map((item) => item.value);
        break;
    }
    const res = await encode(params);
    createCode.value = (res as any).msg;
  } catch (err: any) {
    proxy?.$modal.msgError(err.message || proxy?.$t('device.device-modbus-task.384302-41'));
  } finally {
    createLoading.value = false;
  }
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy?.resetForm(queryFormRef.value);
  handleQuery();
}

function getAddressList() {
  getAddress(props.device.productId, props.device.serialNumber, props.device.deviceType).then((response: any) => {
    if (!response.data || response.data.length === 0) {
      createForm.value.address = '1';
    } else {
      selectPath.value = true;
      addressList.value = response.data;
    }
    editDialog.value = true;
  });
}

function handleStatusChange(row: any) {
  const text =
    row.status === 0
      ? proxy?.$t('device.device-modbus-task.384302-42')
      : proxy?.$t('device.device-modbus-task.384302-43');
  const data = { taskId: row.taskId, status: row.status };
  proxy?.$modal
    .confirm(
      proxy?.$t('device.device-modbus-task.384302-44', [
        text + proxy?.$t('device.device-modbus-task.384302-71') + row.jobId,
      ])
    )
    .then(() => updateJob(data))
    .then(() => {
      proxy?.$modal.msgSuccess(text + proxy?.$t('device.device-modbus-task.384302-45'));
    })
    .catch(() => {
      row.status = row.status === 0 ? 0 : 1;
    });
}

function handleCycleTypeInput(val: number) {
  if (val === 1) {
    cycles2.value = [{ type: 'day', time: '00', week: '', day: '', toType: '1', toTime: '02', toWeek: '', toDay: '' }];
  } else {
    cycles1.value = [{ interval: 'hour', time: '', week: '', day: '' }];
  }
}

function handleCycleInterval(val: string) {
  if (val === 'hour') cycles1.value[0] = { interval: val, time: '', week: '', day: '' };
  else if (val === 'day') cycles1.value[0] = { interval: val, time: '01', week: '', day: '' };
  else if (val === 'week') cycles1.value[0] = { interval: val, time: '01', week: '1', day: '' };
  else if (val === 'month') cycles1.value[0] = { interval: val, time: '01', week: '', day: '1' };
  else cycles1.value[0] = { interval: val, time: '', week: '', day: '' };
}

function handleCycleTime() {}
</script>

<style lang="scss" scoped>
$border-color: #dcdfe6;
$right-btn-color: #1890ff;

:deep(.el-dialog__body) {
  box-sizing: border-box;
  padding: 0;
}

.dialog-content {
  width: 100%;
  box-sizing: border-box;
  padding: 30px 20px;
  overflow: auto;

  .create-title {
    display: flex;
    line-height: 36px;
    margin-bottom: 16px;
    .title-right {
      margin-left: auto;
    }
  }
  .input-item {
    width: 250px;
  }
  .create-code {
    font-size: 18px;
    line-height: 36px;
    font-weight: 800;
  }
  .form-item-label {
    display: flex;
    align-items: center;
    width: 100%;
    :deep(.el-form-item__label) {
      width: 100%;
    }
  }
  .timer-wrap {
    .timer-period {
      display: inline-block;
      margin-left: 30px;
      color: #000000;
      font-size: 12px;
      font-weight: normal;
    }
    .timer-custom {
      display: block;
      margin-top: 12px;
      color: #000000;
      font-size: 12px;
      font-weight: normal;
    }
  }
}
</style>
