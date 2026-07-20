<template>
  <div class="firmware-task">
    <el-card>
      <el-descriptions :title="$t('firmware.task.222543-0')">
        <el-descriptions-item
          v-for="(item, index) in firmwareInfo"
          :key="index"
          :label="item.name"
          label-class-name="feint"
        >
          {{ item.value }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    <el-card>
      <div class="firmwareDeviceHeader">
        <h1>{{ $t('firmware.task.222543-1') }}</h1>
        <el-icon class="icon" @click="updateFirmwareDevice()"><Refresh /></el-icon>
      </div>
      <div class="firmwareDevice">
        <div class="box" v-for="(item, index) in firmwareDevice" :key="index">
          <span class="title">{{ item.title }}</span>
          <span class="num">{{ item.num }}</span>
        </div>
      </div>
    </el-card>
    <el-card>
      <div class="firmwareDeviceHeader">
        <h1>{{ $t('firmware.task.222543-2') }}</h1>
      </div>
      <div class="task">
        <div class="taskHeader">
          <div class="taskTitle">
            <el-button @click="otaUpGrade()" type="primary" :icon="Plus" plain v-hasPermi="['iot:task:add']">
              {{ $t('firmware.task.222543-50') }}
            </el-button>
          </div>
          <div class="taskInput">
            <el-input
              class="searchInput"
              v-model="taskName"
              :placeholder="$t('firmware.task.222543-51')"
              clearable
              @keyup.enter="taskQuery()"
            />
            <el-button @click="taskQuery" style="height: 32px; width: 32px; padding: 0; margin-left: 10px">
              <el-icon><Search /></el-icon>
            </el-button>
          </div>
        </div>
        <div class="taskBody">
          <el-table v-loading="loading" :data="firmwareTaskList" :border="false">
            <el-table-column
              :label="$t('firmware.task.222543-8')"
              align="center"
              prop="id"
              width="80"
            ></el-table-column>
            <el-table-column
              :label="$t('firmware.task.222543-9')"
              align="left"
              prop="taskName"
              min-width="180"
            ></el-table-column>
            <el-table-column :label="$t('firmware.task.222543-10')" align="center" prop="upgradeType" min-width="140">
              <template #default="scope">
                <el-tag type="warning" v-if="scope.row.upgradeType == 1">{{ $t('firmware.task.222543-11') }}</el-tag>
                <el-tag v-else>{{ $t('firmware.task.222543-12') }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('firmware.task.222543-13')"
              align="center"
              prop="deviceAmount"
              min-width="80"
            ></el-table-column>
            <el-table-column
              :label="$t('firmware.task.222543-14')"
              align="center"
              prop="bookTime"
              width="160"
            ></el-table-column>
            <el-table-column
              :label="$t('firmware.task.222543-52')"
              align="center"
              prop="createTime"
              width="160"
            ></el-table-column>
            <el-table-column
              :label="$t('firmware.task.222543-15')"
              align="left"
              prop="taskDesc"
              min-width="180"
            ></el-table-column>
            <el-table-column :label="$t('opation')" align="center" fixed="right" width="225">
              <template #default="scope">
                <el-button
                  @click="taskDetailClick(scope.row)"
                  link
                  size="small"
                  :icon="View"
                  v-hasPermi="['iot:task:query']"
                >
                  {{ $t('firmware.task.222543-17') }}
                </el-button>
                <el-button
                  @click="handleDelete(scope.row)"
                  link
                  size="small"
                  :icon="Delete"
                  v-hasPermi="['iot:task:remove']"
                >
                  {{ $t('firmware.task.222543-53') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <pagination
            v-show="taskTotal > 0"
            :total="taskTotal"
            v-model:page="taskParams.pageNum"
            v-model:limit="taskParams.pageSize"
            @pagination="getTaskList"
          />
        </div>
      </div>
    </el-card>

    <!-- 任务详情弹框 -->
    <el-dialog
      class="task-detail-dialog"
      :title="$t('firmware.task.222543-31')"
      width="960px"
      v-model="taskDialogVisible"
      append-to-body
    >
      <div class="dialogBox">
        <el-card class="dialog-card">
          <el-descriptions :title="$t('firmware.task.222543-32')" :column="1">
            <el-descriptions-item
              v-for="(item, index) in taskDialogData"
              :key="index"
              :label="item.name"
              label-class-name="feint"
            >
              {{ item.value }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
        <el-card class="dialog-card">
          <el-descriptions :title="$t('firmware.task.222543-33')">
            <template #extra>
              <el-button size="small" type="primary" link :icon="Refresh" @click="chartRefresh"></el-button>
            </template>
          </el-descriptions>
          <div v-show="chartData.length !== 0" class="chart" ref="taskChartRef"></div>
          <el-empty
            v-show="chartData.length === 0"
            class="chart"
            :image-size="120"
            :description="$t('noData')"
          ></el-empty>
        </el-card>
      </div>
      <el-card class="dialog-table-card">
        <div class="firmwareDeviceHeader">
          <h1>{{ $t('firmware.task.222543-32') }}</h1>
        </div>
        <div class="deviceDialogSearch">
          <div class="left" id="left">
            <span :class="{ active: deviceStatus === 'all' }" @click="deviceStatusClick('all')">
              {{ $t('firmware.task.222543-35') }}（{{ deviceDialogSearchStatus[0] }}）
            </span>
            <span :class="{ active: deviceStatus === 0 }" @click="deviceStatusClick(0)">
              {{ $t('firmware.task.222543-37') }}（{{ deviceDialogSearchStatus[2] }}）
            </span>
            <span :class="{ active: deviceStatus === 2 }" @click="deviceStatusClick(2)">
              {{ $t('firmware.task.222543-38') }}（{{ deviceDialogSearchStatus[4] }}）
            </span>
            <span :class="{ active: deviceStatus === 3 }" @click="deviceStatusClick(3)">
              {{ $t('firmware.task.222543-36') }}（{{ deviceDialogSearchStatus[1] }}）
            </span>
            <span :class="{ active: deviceStatus === 4 }" @click="deviceStatusClick(4)">
              {{ $t('firmware.task.222543-39') }}（{{ deviceDialogSearchStatus[5] }}）
            </span>
            <span :class="{ active: deviceStatus === 5 }" @click="deviceStatusClick(5)">
              {{ $t('firmware.task.222543-40') }}（{{ deviceDialogSearchStatus[6] }}）
            </span>
          </div>
          <div class="right">
            <el-input
              class="input3"
              v-model="deviceSerialNumber"
              :placeholder="$t('firmware.task.222543-54')"
              clearable
              @keyup.enter="handleTaskList"
            />
            <el-button @click="handleTaskList" style="height: 32px; width: 32px; padding: 0; margin-left: 10px">
              <el-icon><Search /></el-icon>
            </el-button>
            <el-button @click="deviceInfoQuery" style="height: 32px; width: 32px; padding: 0">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </div>
        </div>
        <el-table v-loading="loading" :data="deviceDialogList" :border="false">
          <el-table-column
            :label="$t('firmware.deviceList.index.222542-1')"
            align="left"
            prop="deviceName"
            min-width="160"
          ></el-table-column>
          <el-table-column
            :label="$t('firmware.deviceList.index.222542-5')"
            align="left"
            prop="serialNumber"
            min-width="140"
          ></el-table-column>
          <el-table-column
            :label="$t('firmware.task.222543-8')"
            align="center"
            prop="taskId"
            width="80"
          ></el-table-column>
          <el-table-column
            :label="$t('firmware.task.222543-9')"
            align="center"
            prop="taskName"
            min-width="150"
          ></el-table-column>
          <el-table-column :label="$t('firmware.task.222543-19')" align="center" prop="version" width="120">
            <template #default="scope">
              <span>Version {{ scope.row.version }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('firmware.task.222543-21')" align="center" prop="upgradeStatus" width="100">
            <template #default="scope">
              <dict-tag :options="firmware_upgrade_status" :value="scope.row.upgradeStatus" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('firmware.task.222543-55')" align="center" prop="progress" width="210">
            <template #default="scope">
              <el-progress :percentage="parseInt(scope.row.progress)"></el-progress>
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('firmware.task.222543-29')"
            align="center"
            prop="detailMsg"
            width="100"
          ></el-table-column>
          <el-table-column
            :label="$t('firmware.task.222543-30')"
            align="center"
            prop="updateTime"
            width="160"
          ></el-table-column>
          <el-table-column fixed="right" :label="$t('firmware.task.222543-56')" align="center" width="95">
            <template #default="scope">
              <el-button
                type="primary"
                link
                size="small"
                :disabled="![4, 5].includes(scope.row.upgradeStatus)"
                @click="upgradeTaskHandle(scope.row)"
                v-hasPermi="['iot:task:upgrade']"
              >
                {{ $t('firmware.task.222543-57') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination
          v-show="deviceInfoTotal > 0"
          layout="prev, pager, next"
          :total="deviceInfoTotal"
          v-model:page="deviceInfoParams.pageNum"
          v-model:limit="deviceInfoParams.pageSize"
          @pagination="getDeviceInfoList"
        />
      </el-card>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="handleDialogCancel">{{ $t('confirm') }}</el-button>
          <el-button @click="handleDialogCancel">{{ $t('cancel') }}</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增-固件升级 -->
    <el-dialog :title="upgradeTitle" v-model="openUpGrade" width="650px" append-to-body>
      <el-form ref="formUpGradeRef" :model="formUpGrade" :rules="rulesUpGrade" label-width="135px">
        <el-form-item :label="$t('firmware.index.222541-0')" prop="firmwareName">{{ form.firmwareName }}</el-form-item>
        <el-form-item :label="$t('firmware.index.222541-2')" prop="productId">{{ form.productName }}</el-form-item>
        <el-form-item :label="$t('firmware.index.222541-15')" prop="version">Version {{ form.version }}</el-form-item>
        <el-form-item :label="$t('firmware.index.222541-20')" prop="taskName">
          <el-input
            v-model="formUpGrade.taskName"
            :placeholder="$t('firmware.index.222541-22')"
            maxlength="20"
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('firmware.task.222543-58')" prop="upgradeType">
          <el-select
            v-model="formUpGrade.upgradeType"
            :placeholder="$t('firmware.task.222543-59')"
            clearable
            @change="changeUpType"
            style="width: 400px"
          >
            <el-option
              v-for="type in oat_update_limit"
              :key="type.value"
              :label="type.label"
              :value="type.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('firmware.task.222543-60')" v-if="formUpGrade.upgradeType != '1'">
          <el-badge :value="formUpGrade.deviceAmount" class="item">
            <el-button type="primary" size="small" plain class="mr5" @click="selectDeviceList">
              {{ $t('firmware.task.222543-61') }}
            </el-button>
          </el-badge>
          <el-tag
            style="margin-left: 10px"
            :key="tag"
            v-for="tag in formUpGrade.deviceList"
            closable
            size="small"
            :disable-transitions="false"
            @close="handleClose(tag)"
          >
            {{ tag }}
          </el-tag>
        </el-form-item>
        <el-form-item :label="$t('firmware.index.222541-32')" prop="bookTime">
          <el-date-picker
            v-model="formUpGrade.bookTime"
            type="datetime"
            :placeholder="$t('firmware.index.222541-33')"
            style="width: 400px"
          ></el-date-picker>
        </el-form-item>
        <el-form-item :label="$t('firmware.index.222541-34')" prop="taskDesc">
          <el-input
            v-model="formUpGrade.taskDesc"
            type="textarea"
            :placeholder="$t('firmware.index.222541-35')"
            :autosize="{ minRows: 3, maxRows: 5 }"
            style="width: 400px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="!istrue" type="primary" @click="submitFormUpGrade">{{ $t('save') }}</el-button>
          <el-button v-else type="primary" size="small" :loading="true" disabled>
            {{ $t('firmware.task.222543-24') }}
          </el-button>
          <el-button @click="canceUpGrade">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
    <SelectDeviceListDialog ref="deviceListRef" :upGrade="formUpGrade"></SelectDeviceListDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick, onMounted, onUnmounted, getCurrentInstance } from 'vue';
import { Search, Refresh, Plus, Delete, View } from '@element-plus/icons-vue';
import {
  listTask,
  deviceList as deviceListApi,
  deviceStatistic,
  upgradeTask,
  delTask,
  getTask,
} from '@/api/iot/firmwareTask';
import { formatDate } from '@/utils/common';
import * as echarts from 'echarts';
import { getFirmware } from '@/api/iot/firmware';
import SelectDeviceListDialog from './device-list.vue';
import { listDeviceByGroup } from '@/api/iot/device';
import { addTask } from '@/api/iot/firmwareTask';
import { useDict } from '@/utils/dict';

const { iot_yes_no, oat_update_limit, firmware_upgrade_status } = useDict(
  'iot_yes_no',
  'oat_update_limit',
  'firmware_upgrade_status'
);
const proxy = getCurrentInstance()?.proxy as any;

const taskChartRef = ref<any>(null);
const deviceListRef = ref<any>(null);
const formUpGradeRef = ref<any>(null);
const firmwareId = ref('');
const loading = ref(false);
const istrue = ref(false);
const taskName = ref('');
const deviceSerialNumber = ref('');
const taskDialogVisible = ref(false);
const openUpGrade = ref(false);
const upgradeTitle = ref('');
const deviceStatus = ref<any>('all');
let myChart: any = null;

const firmwareInfo = ref<any[]>([]);
const firmwareDevice = ref<any[]>([]);
const taskDialogData = ref<any[]>([]);
const chartData = ref<any[]>([]);
const chartParam = ref<any>({});
const deviceDialogList = ref<any[]>([]);
const firmwareTaskList = ref<any[]>([]);
const deviceDialogSearchStatus = ref([0, 0, 0, 0, 0, 0, 0]);
const deviceInfoTotal = ref(0);
const taskTotal = ref(0);

const taskParams = reactive<any>({ pageNum: 1, pageSize: 10, firmwareId: '', taskName: '' });
const deviceInfoParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  firmwareId: '',
  taskId: '',
  upgradeStatus: '',
  serialNumber: '',
});
const formUpGrade = reactive<any>({
  taskName: null,
  firmwareId: 0,
  deviceAmount: 0,
  bookTime: null,
  upgradeType: null,
  upType: null,
  deviceList: [],
  version: null,
  flag: false,
});
const form = ref<any>({ version: 1.0 });

const rulesUpGrade = reactive<any>({
  taskName: [{ required: true, message: proxy?.$t('firmware.index.222541-41'), trigger: 'blur' }],
  upgradeType: [{ required: true, message: proxy?.$t('firmware.task.222543-22'), trigger: 'blur' }],
});

onMounted(() => {
  const data = JSON.parse(sessionStorage.getItem('firmwareTaskInfo') || '{}');
  firmwareId.value = data.firmwareId;
  taskParams.firmwareId = data.firmwareId;
  deviceInfoParams.firmwareId = data.firmwareId;
  formUpGrade.firmwareId = data.firmwareId;

  firmwareInfo.value = [
    { name: proxy?.$t('firmware.index.222541-0'), value: data.firmwareName },
    { name: proxy?.$t('firmware.index.222541-2'), value: data.productName },
    {
      name: proxy?.$t('firmware.task.222543-42'),
      value: data.isLatest == 1 ? proxy?.$t('firmware.task.222543-63') : proxy?.$t('firmware.task.222543-64'),
    },
    {
      name: proxy?.$t('firmware.task.222543-62'),
      value: data.firmwareType == 1 ? proxy?.$t('firmware.task.222543-65') : 'http',
    },
    { name: proxy?.$t('firmware.index.222541-15'), value: 'Version ' + data.version },
    { name: proxy?.$t('firmware.task.222543-16'), value: data.createTime },
    { name: proxy?.$t('firmware.task.222543-44'), value: data.remark },
  ];
  firmwareDevice.value = [
    { title: proxy?.$t('firmware.task.222543-45'), num: 0 },
    { title: proxy?.$t('firmware.task.222543-36'), num: 0 },
    { title: proxy?.$t('firmware.task.222543-46'), num: 0 },
    { title: proxy?.$t('firmware.task.222543-39'), num: 0 },
  ];
  taskDialogData.value = [
    { name: proxy?.$t('firmware.task.222543-8'), value: '' },
    { name: proxy?.$t('firmware.task.222543-9'), value: '' },
    { name: proxy?.$t('firmware.task.222543-10'), value: '' },
    { name: proxy?.$t('firmware.task.222543-13'), value: '' },
    { name: proxy?.$t('firmware.task.222543-14'), value: '' },
    { name: proxy?.$t('firmware.task.222543-15'), value: '' },
    { name: proxy?.$t('firmware.task.222543-16'), value: '' },
  ];
  getDeviceStatistic();
  getTaskList();
  connectMqtt();
});

onUnmounted(() => {
  mqttUnSubscribe(taskDialogData.value[0]?.value);
});

watch(
  chartData,
  (newVal) => {
    if (newVal.length > 0) {
      nextTick(() => {
        if (!taskChartRef.value) return;
        myChart = echarts.init(taskChartRef.value);
        myChart.setOption({
          grid: { top: 0, bottom: 0, containLabel: true },
          tooltip: { trigger: 'item', formatter: '{b}:{c}' },
          series: [
            {
              name: proxy?.$t('firmware.task.222543-33'),
              type: 'pie',
              radius: ['45%', '70%'],
              label: { show: true, formatter: '{b}:{c}\n占比:{d}%' },
              data: newVal,
            },
          ],
        });
      });
    }
  },
  { immediate: true, deep: true }
);

function getDeviceStatistic() {
  deviceStatistic({ firmwareId: firmwareId.value }).then((res: any) => {
    if (res.code == 200) {
      const data = res.data;
      let now = 0,
        success = 0,
        fail = 0,
        total = 0;
      data.forEach((item: any) => {
        total += item.deviceCount;
        if ([0, 1, 2].includes(item.upgradeStatus)) now += item.deviceCount;
        else if ([4, 5].includes(item.upgradeStatus)) fail += item.deviceCount;
        else success += item.deviceCount;
      });
      firmwareDevice.value[0].num = total;
      firmwareDevice.value[1].num = success;
      firmwareDevice.value[2].num = now;
      firmwareDevice.value[3].num = fail;
    }
  });
}

function delay() {
  let n = 5;
  const time = setInterval(() => {
    istrue.value = true;
    n--;
    if (n < 0) {
      istrue.value = false;
      clearInterval(time);
    }
  }, 2000);
}

function handleDelete(row: any) {
  const taskIds = row.id;
  proxy?.$modal
    .confirm(proxy?.$t('firmware.task.222543-66', [taskIds]))
    .then(() => {
      return delTask(taskIds);
    })
    .then(() => {
      getTaskList();
      getDeviceStatistic();
      proxy?.$modal.msgSuccess(proxy?.$t('firmware.task.222543-67'));
    })
    .catch(() => {});
}

function canceUpGrade() {
  openUpGrade.value = false;
  resetForm();
}

function submitFormUpGrade() {
  formUpGradeRef.value?.validate((valid: boolean) => {
    if (valid) {
      delay();
      if (formUpGrade.deviceAmount > 0) {
        addTask(formUpGrade).then((response: any) => {
          if (response.code == 200) {
            proxy?.$modal.msgSuccess(proxy?.$t('addSuccess'));
            openUpGrade.value = false;
            getTaskList();
            getDeviceStatistic();
          }
        });
      } else {
        proxy?.$modal.msgError(proxy?.$t('firmware.index.222541-47'));
      }
    }
  });
}

function selectDeviceList() {
  formUpGrade.flag = true;
  deviceListRef.value.openDeviceList = true;
}

function resetForm() {
  form.value = { firmwareId: 0, firmwareName: null, productId: null, productName: null, version: 1.0 };
}

function otaUpGrade() {
  formUpGrade.deviceList = [];
  formUpGrade.deviceAmount = 0;
  proxy?.resetForm('formUpGradeRef');
  const fwId = taskParams.firmwareId;
  getFirmware(fwId).then((response: any) => {
    form.value = response.data;
    openUpGrade.value = true;
    upgradeTitle.value = proxy?.$t('firmware.task.222543-23');
    formUpGrade.productId = form.value.productId;
    formUpGrade.firmwareId = fwId;
  });
}

function updateFirmwareDevice() {
  getDeviceStatistic();
}

function getTaskList() {
  listTask(taskParams).then((res: any) => {
    firmwareTaskList.value = res.rows;
    taskTotal.value = res.total;
  });
}

function taskQuery() {
  taskParams.taskName = taskName.value;
  getTaskList();
}

function taskDetailClick(data: any) {
  taskDialogData.value[0].value = data.id;
  taskDialogData.value[1].value = data.taskName;
  taskDialogData.value[2].value =
    data.upgradeType == 1 ? proxy?.$t('firmware.task.222543-68') : proxy?.$t('firmware.task.222543-69');
  taskDialogData.value[3].value = data.deviceAmount;
  taskDialogData.value[4].value = data.bookTime;
  taskDialogData.value[5].value = data.taskDesc;
  taskDialogData.value[6].value = data.createTime;
  deviceDialogSearchStatus.value[0] = data.deviceAmount;
  taskDialogVisible.value = true;
  chartParam.value = { taskId: data.id, firmwareId: data.firmwareId };
  deviceInfoParams.taskId = data.id;
  deviceInfoParams.serialNumber = '';
  getChartData();
  getDeviceInfoList();
  mqttSubscribe(data.id);
}

function getChartData() {
  deviceStatistic(chartParam.value).then((res: any) => {
    if (res.code === 200) {
      const data = res.data;
      let cd: any[] = [];
      let ts = [deviceDialogSearchStatus.value[0], 0, 0, 0, 0, 0, 0];
      data.forEach((item: any) => {
        switch (item.upgradeStatus) {
          case 0:
            cd.push({ name: proxy?.$t('firmware.task.222543-48'), value: item.deviceCount });
            ts[2] = item.deviceCount;
            break;
          case 2:
            cd.push({ name: proxy?.$t('firmware.task.222543-38'), value: item.deviceCount });
            ts[4] = item.deviceCount;
            break;
          case 3:
            cd.push({ name: proxy?.$t('firmware.task.222543-36'), value: item.deviceCount });
            ts[1] = item.deviceCount;
            break;
          case 4:
            cd.push({ name: proxy?.$t('firmware.task.222543-39'), value: item.deviceCount });
            ts[5] = item.deviceCount;
            break;
          case 5:
            cd.push({ name: proxy?.$t('firmware.task.222543-40'), value: item.deviceCount });
            ts[6] = item.deviceCount;
            break;
        }
      });
      chartData.value = cd;
      deviceDialogSearchStatus.value = ts;
    }
  });
}

function chartRefresh() {
  getChartData();
}

function getDeviceInfoList() {
  deviceListApi(deviceInfoParams).then((res: any) => {
    deviceDialogList.value = res.rows;
    deviceInfoTotal.value = res.total;
  });
}

function deviceInfoQuery() {
  deviceSerialNumber.value = '';
  const id = taskDialogData.value[0]?.value || null;
  getTask(id).then((res: any) => {
    if (res.code === 200) {
      mqttUnSubscribe(id);
      taskDetailClick(res.data);
    }
  });
}

function handleTaskList() {
  deviceInfoParams.serialNumber = deviceSerialNumber.value;
  getDeviceInfoList();
}

function deviceStatusClick(id: any) {
  deviceStatus.value = id;
  deviceInfoParams.upgradeStatus = id === 'all' ? '' : id;
  getDeviceInfoList();
}

function handleDialogCancel() {
  taskDialogVisible.value = false;
  deviceStatus.value = 'all';
  deviceSerialNumber.value = '';
  deviceInfoParams.upgradeStatus = '';
  deviceInfoParams.serialNumber = '';
  mqttUnSubscribe(taskDialogData.value[0]?.value);
}

async function connectMqtt() {
  if ((proxy as any)?.$mqttTool?.client == null) {
    await (proxy as any)?.$mqttTool?.connect((proxy as any)?.vuex_token);
  } else {
    (proxy as any)?.$mqttTool?.client?.removeAllListeners('message');
  }
  mqttCallback();
}

function mqttCallback() {
  (proxy as any)?.$mqttTool?.client?.on('message', (topic: string, message: any) => {
    message = JSON.parse(message.toString('utf8'));
    if (!message) return;
    setMqttData(message);
  });
}

function mqttSubscribe(taskId: any) {
  if (!taskId) return;
  (proxy as any)?.$mqttTool?.subscribe(['/' + taskId + '/ws/ota/status']);
}

function mqttUnSubscribe(taskId: any) {
  if (!taskId) return;
  (proxy as any)?.$mqttTool?.unsubscribe(['/' + taskId + '/ws/ota/status']);
}

function setMqttData(data: any) {
  for (let i = 0; i < deviceDialogList.value.length; i++) {
    const item = deviceDialogList.value[i];
    if (item.serialNumber === data.serialNumber) {
      item.upgradeStatus = data.status;
      item.updateTime = formatDate(data.timestamp, 'Y-M-D h:m:s');
      item.detailMsg = getDetailMsg(data.status);
      if (data.progress) item.progress = data.progress;
      break;
    }
  }
}

function getDetailMsg(status: number) {
  const statusObj: any = {
    0: proxy?.$t('firmware.task.222543-70'),
    1: proxy?.$t('firmware.task.222543-71'),
    2: proxy?.$t('firmware.task.222543-72'),
    3: proxy?.$t('firmware.task.222543-73'),
    4: proxy?.$t('firmware.task.222543-74'),
    5: proxy?.$t('firmware.task.222543-75'),
    6: proxy?.$t('firmware.task.222543-76'),
  };
  return statusObj[status] || '';
}

function changeUpType(val: string) {
  resetDeviceList();
  if (val == '1') {
    listDeviceByGroup({ productId: formUpGrade.productId, firmwareVersion: formUpGrade.version }).then(
      (response: any) => {
        response.rows.forEach((row: any) => {
          formUpGrade.deviceList.push(row.serialNumber);
        });
        formUpGrade.deviceAmount = response.total;
      }
    );
  }
}

function resetDeviceList() {
  formUpGrade.deviceList = [];
  formUpGrade.deviceAmount = 0;
}

function handleClose(tag: any) {
  formUpGrade.deviceList.splice(formUpGrade.deviceList.indexOf(tag), 1);
  formUpGrade.deviceAmount = formUpGrade.deviceList.length;
}

function upgradeTaskHandle(row: any) {
  upgradeTask({ firmwareId: row.firmwareId, taskId: row.taskId, devices: [row.serialNumber], upgradeType: 2 }).then(
    (res: any) => {
      if (res.code === 200) {
        proxy?.$modal.msgSuccess(res.msg || proxy?.$t('firmware.task.222543-77'));
      } else {
        proxy?.$modal.msgError(res.msg || proxy?.$t('firmware.task.222543-78'));
      }
    }
  );
}
</script>

<style scoped>
.firmware-task {
  padding: 20px;
}
.firmware-task :deep(.feint) {
  color: #999;
}
.firmware-task :deep(.el-card) {
  margin-bottom: 10px;
}
.firmwareDeviceHeader {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}
h1 {
  margin: 0;
  height: 28px;
  line-height: 28px;
  font-size: 16px;
  font-weight: bold;
}
.icon {
  width: 20px;
  line-height: 28px;
}
.icon:hover {
  color: #5cb6ff;
  cursor: pointer;
}
.firmware-task :deep(.searchInput) {
  width: 200px;
}
.firmwareDevice {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.box {
  flex: 1;
  padding: 0 10px;
  height: 60px;
  border-right: 1px solid #999;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}
.box:last-child {
  border-right: none;
}
.title {
  font-size: 14px;
  color: #999;
  margin-bottom: 10px;
  text-align: left;
}
.num {
  font-weight: bold;
}
.box:nth-child(1) .num {
  color: #5cb6ff;
}
.box:nth-child(2) .num {
  color: #67c23a;
}
.box:nth-child(3) .num {
  color: #e6a23c;
}
.box:nth-child(4) .num {
  color: #f56c6c;
}
.taskHeader {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
.dialogBox {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}
.dialog-card {
  flex: 1;
  margin-bottom: 0 !important;
}
.dialog-table-card {
  margin-bottom: 0 !important;
}
.chart {
  height: 218px;
}
.deviceDialogSearch {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.left {
  display: inline-flex;
  flex-wrap: wrap;
}
.left span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 98px;
  padding: 0 10px;
  text-align: center;
  height: 30px;
  line-height: 28px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  font-size: 12px;
  background: #fff;
  color: #606266;
}
.left span:not(:first-child) {
  border-left: none;
}
#left .active {
  color: #409eff;
  border-color: #409eff;
  position: relative;
  z-index: 1;
}
.right {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}
.input3 {
  margin-left: 10px;
  width: 170px;
}

.task-detail-dialog :deep(.el-dialog__body) {
  padding-top: 10px;
}
.task-detail-dialog :deep(.el-descriptions__title) {
  font-size: 30px;
  line-height: 42px;
  font-weight: 700;
  color: #303133;
}
</style>
