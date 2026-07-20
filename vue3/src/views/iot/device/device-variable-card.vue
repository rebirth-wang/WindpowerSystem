<template>
  <div class="device-variable">
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item prop="type">
        <el-select
          v-model="queryParams.type"
          :placeholder="$t('device.variable-case.347856-1')"
          clearable
          @keyup.enter="handleQuery"
          style="width: 200px"
        >
          <el-option v-for="dict in iot_things_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item prop="modelName">
        <el-input v-model="queryParams.modelName" :placeholder="$t('device.variable-case.347856-3')" clearable />
      </el-form-item>
      <el-form-item prop="subDeviceId" v-if="device.deviceType === 2">
        <el-select
          v-model="subDeviceId"
          :placeholder="$t('device.variable-case.347856-17')"
          clearable
          @keyup.enter="handleQuery"
          style="width: 200px"
        >
          <el-option v-for="item in subDeviceList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">
          {{ $t('device.variable-case.347856-4') }}
        </el-button>
        <el-button :icon="Refresh" @click="handleResetQuery">{{ $t('device.variable-case.347856-5') }}</el-button>
        <el-button
          v-if="
            device.protocolCode === 'MODBUS-TCP-OVER-RTU' ||
            device.protocolCode === 'MODBUS-RTU' ||
            device.protocolCode === 'MODBUS-TCP'
          "
          :icon="Refresh"
          @click="activeCollectionAll"
        >
          {{ $t('device.variable-case.347856-16') }}
        </el-button>
      </el-form-item>
      <el-form-item style="line-height: 28px">
        <el-checkbox v-model="queryParams.isMonitor" :true-value="1" style="margin: 0px 10px" @change="handleQuery">
          <div style="color: #606266 !important; font-size: 14px">{{ $t('device.running-status.866086-33') }}</div>
        </el-checkbox>
        <el-tooltip :content="$t('device.running-status.866086-34')" placement="top">
          <el-icon style="color: #909399; font-size: 16px"><QuestionFilled /></el-icon>
        </el-tooltip>
      </el-form-item>
    </el-form>
    <el-row :gutter="20" v-if="queryParams.isMonitor !== 1">
      <el-col
        :xs="24"
        :sm="12"
        :md="12"
        :lg="6"
        :xl="4"
        v-for="(item, index) in variableList"
        :key="index"
        style="margin-bottom: 20px"
      >
        <el-card :body-style="{ padding: '20px' }" shadow="always" style="height: 130px">
          <el-row type="flex" :gutter="10" justify="space-between" align="middle">
            <el-col
              :span="8"
              style="text-align: left; font-size: 16px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis"
            >
              <el-tooltip :content="item.identifier + ' / ' + item.modelName" placement="top-start">
                <span>{{ item.modelName }}</span>
              </el-tooltip>
            </el-col>
            <el-col :span="16" style="text-align: right">
              <div class="icon_tag">
                <div>
                  <el-tooltip
                    class="item"
                    effect="dark"
                    :content="$t('device.variable-case.347856-13')"
                    placement="top-start"
                  >
                    <svg-icon
                      icon-class="gather"
                      style="color: #007aff; margin-right: 8px; cursor: pointer"
                      @click="activeCollection(item)"
                      v-if="
                        device.protocolCode === 'MODBUS-TCP-OVER-RTU' ||
                        device.protocolCode === 'MODBUS-RTU' ||
                        device.protocolCode === 'MODBUS-TCP'
                      "
                    ></svg-icon>
                  </el-tooltip>
                </div>
                <div>
                  <el-tooltip
                    class="item"
                    effect="dark"
                    :content="
                      item.isReadonly === 0 && item.type != 3
                        ? $t('device.running-status.866086-36')
                        : $t('device.running-status.866086-45')
                    "
                    placement="top"
                  >
                    <el-icon
                      style="color: #007aff; margin-right: 8px; cursor: pointer"
                      @click="editFunc(item)"
                      v-if="item.isReadonly === 0 && item.type != 3"
                    >
                      <Promotion />
                    </el-icon>
                    <el-icon style="color: #909399; margin-right: 8px; cursor: pointer" v-else><Promotion /></el-icon>
                  </el-tooltip>
                </div>
                <div>
                  <el-tooltip
                    class="item"
                    effect="dark"
                    :content="
                      item.isHistory === 1
                        ? $t('device.running-status.866086-37')
                        : $t('device.running-status.866086-46')
                    "
                    placement="top-start"
                  >
                    <svg-icon
                      style="cursor: pointer; color: #007aff"
                      aria-hidden="true"
                      iconClass="line_chart"
                      @click="handleHistory(item)"
                      v-if="item.isHistory === 1"
                    ></svg-icon>
                    <svg-icon
                      style="cursor: pointer; color: #909399"
                      aria-hidden="true"
                      iconClass="line_chart"
                      v-else
                    ></svg-icon>
                  </el-tooltip>
                </div>
                <dict-tag
                  style="float: right; margin-left: 8px; margin-top: -2px"
                  :options="iot_things_type"
                  :value="item.type"
                  size="small"
                />
              </div>
            </el-col>
          </el-row>
          <el-row
            type="flex"
            :gutter="10"
            justify="space-between"
            align="middle"
            style="margin-top: 10px"
            v-if="device.deviceType === 1 || device.deviceType === 2"
          >
            <el-col
              :span="8"
              style="text-align: left; font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis"
              v-if="
                [1, 2].includes(device.deviceType) &&
                ['MODBUS-TCP-OVER-RTU', 'MODBUS-RTU', 'MODBUS-TCP', 'JSON-GATEWAY', 'MODBUS-JSON-HP'].includes(
                  device.protocolCode
                )
              "
            >
              <el-tooltip :content="item.deviceName" placement="top-start">
                <span>{{ item.deviceName ? item.deviceName : '-' }}</span>
              </el-tooltip>
            </el-col>
            <el-col
              :span="16"
              style="text-align: right; font-size: 12px"
              v-if="
                [1, 2].includes(device.deviceType) &&
                ['MODBUS-TCP-OVER-RTU', 'MODBUS-RTU', 'MODBUS-TCP', 'JSON-GATEWAY', 'MODBUS-JSON-HP'].includes(
                  device.protocolCode
                )
              "
            >
              {{ $t('device.variable-case.347856-18') }}{{ item.address ? item.address : '-' }}
            </el-col>
          </el-row>
          <el-row type="flex" :gutter="10" justify="space-between">
            <el-col :span="12" style="font-feature-settings: 'tnum'; min-width: 0">
              <div class="data-container">
                <div v-if="item.datatype.type === 'integer' || item.datatype.type === 'decimal'" class="data-content">
                  <el-tooltip
                    :content="item.value ? item.value + '' : '--'"
                    placement="top-start"
                    v-if="item.value && item.value !== '-'"
                  >
                    <span class="truncate-text">{{ item.value ? item.value + '' : '--' }}</span>
                  </el-tooltip>
                  <span class="truncate-text" v-else>--</span>
                </div>
                <div v-if="item.datatype.type == 'bool'" class="data-content">
                  <div>{{ item.valueName ? item.valueName : '--' }}</div>
                </div>
                <div v-if="item.datatype.type == 'enum'" class="data-content">
                  <div v-if="item.showWay && item.showWay == 'button'">
                    <el-button
                      type=""
                      :plain="true"
                      style="margin-top: 20px"
                      size="small"
                      :disabled="shadowUnEnable || item.isReadonly == 1 || item.type == 3"
                      v-if="item.valueName !== null && item.valueName !== '' && item.valueName !== '-'"
                    >
                      {{ item.valueName }}
                    </el-button>
                  </div>
                  <div v-else class="truncate-text">{{ item.valueName ? item.valueName : '--' }}</div>
                </div>
                <div v-if="item.datatype.type === 'string'" class="data-content">
                  <el-tooltip
                    :content="item.value"
                    placement="top-start"
                    v-if="item.value !== null && item.value !== '' && item.value !== '-'"
                  >
                    <span class="truncate-text">{{ item.value ? item.value : '-' }}</span>
                  </el-tooltip>
                  <span class="truncate-text" v-else>{{ item.value ? item.value : '-' }}</span>
                </div>
                <div v-if="item.datatype.type !== 'bool' && item.datatype.type !== 'enum'" class="unit-text">
                  {{ item.datatype.unit ? item.datatype.unit : '' }}
                </div>
              </div>
            </el-col>
            <el-col :span="12" style="text-align: right">
              <div style="font-size: 12px; margin-top: 25px; display: inline-block">{{ item.ts ? item.ts : '-' }}</div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
    <!-- 图表 -->
    <el-row :gutter="20" v-if="queryParams.isMonitor === 1">
      <el-col :xs="24" :sm="12" :md="12" :lg="8" :xl="4" v-for="(item, index) in chartList" :key="index">
        <el-card shadow="hover" style="border-radius: 30px; margin-bottom: 20px">
          <div :ref="(el: any) => setMapRef(el, index)" style="height: 250px; width: 185px; margin: 0 auto"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty :description="$t('device.device-recycle.864193-8')" v-if="total == 0"></el-empty>
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      :pageSizes="[24, 48, 72, 96]"
      @pagination="getVariableList"
    />

    <!-- 服务下发 -->
    <el-dialog :title="$t('device.realTime-status.099127-26')" v-model="dialogValue" width="480px">
      <el-form>
        <el-form-item v-for="(item, index) in opationList" :label="`${item.label}：`" :key="index" label-width="120px">
          <el-input
            v-model="funVal[item.key]"
            :precision="0"
            :controls="false"
            @input="justNumber(item)"
            type="number"
            v-if="
              item.dataTypeName == 'integer' ||
              item.dataTypeName == 'decimal' ||
              (item.dataTypeName == 'array' && item.arrayType == 'integer') ||
              (item.dataTypeName == 'array' && item.arrayType == 'decimal')
            "
            style="width: 200px"
          ></el-input>
          <el-input
            v-model="funVal[item.key]"
            :precision="0"
            :controls="false"
            :placeholder="$t('device.running-status.866086-35')"
            type="text"
            v-if="item.dataTypeName == 'string' || (item.dataTypeName == 'array' && item.arrayType == 'string')"
            style="width: 230px"
            @input="justNumber(item)"
          ></el-input>
          <el-select v-if="item.dataTypeName == 'bool'" v-model="funVal[item.key]" style="width: 230px">
            <el-option
              v-for="option in item.options"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            ></el-option>
          </el-select>
          <div v-if="item.dataTypeName == 'enum'">
            <div v-if="item.showWay && item.showWay == 'button'">
              <el-button
                style="margin: 5px"
                size="small"
                @click="enumButtonClick(deviceInfoRef, item, subItem.value)"
                v-for="subItem in item.options"
                :key="subItem.value"
                v-model="funVal[item.key]"
                :disabled="shadowUnEnable || item.isReadonly == 1 || item.type == 3"
                :class="{ 'is-active-btn': subItem.value === (item.shadow || item.value) }"
              >
                {{ subItem.label }}
              </el-button>
            </div>
            <el-select v-else v-model="funVal[item.key]">
              <el-option
                v-for="option in item.options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              ></el-option>
            </el-select>
          </div>
          <span
            v-if="
              (item.dataTypeName == 'integer' ||
                item.dataTypeName == 'decimal' ||
                (item.dataTypeName == 'array' && item.arrayType == 'integer') ||
                (item.dataTypeName == 'array' && item.arrayType == 'decimal')) &&
              item.unit &&
              item.unit != 'un' &&
              item.unit != '/'
            "
          >
            ({{ item.unit }})
          </span>
          <span
            style="margin-left: 5px"
            v-if="
              item.dataTypeName == 'integer' ||
              item.dataTypeName == 'decimal' ||
              (item.dataTypeName == 'array' && item.arrayType == 'integer') ||
              (item.dataTypeName == 'array' && item.arrayType == 'decimal')
            "
          >
            （{{ item.min }} ~ {{ item.max }}）
          </span>
        </el-form-item>
        <el-form-item style="display: none">
          <el-input v-model="functionName"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogValue = false">{{ $t('cancel') }}</el-button>
        <el-button type="primary" @click="sendService" :loading="btnLoading" :disabled="!canSend">
          {{ $t('confirm') }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog :title="$t('device.variable-case.347856-15')" v-model="centerDialogVisible" width="380px">
      <span>{{ $t('device.variable-case.347856-14') }}</span>
      <template #footer>
        <el-button @click="centerDialogVisible = false">{{ $t('iot.group.device-list.849593-12') }}</el-button>
        <el-button type="primary" @click="confirmCollection">{{ $t('iot.group.device-list.849593-11') }}</el-button>
      </template>
    </el-dialog>
    <history-list ref="historyListRef" :model="modelParams"></history-list>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick, getCurrentInstance, onMounted, onBeforeUnmount } from 'vue';
import { Search, Refresh, QuestionFilled, Promotion } from '@element-plus/icons-vue';
import { listThingsModel } from '@/api/iot/device';
import { serviceInvokeReply, serviceInvoke } from '@/api/iot/runstatus';
import { propGet } from '@/api/iot/runstatus';
import { getOrderControl } from '@/api/iot/control';
import { listGateway } from '@/api/iot/gateway';
import { useDict } from '@/utils/dict/useDict';
import { useUserStore } from '@/stores/modules/user';
import busEvent from '@/utils/busEvent';
import historyList from './historyData.vue';

const { proxy } = getCurrentInstance() as any;
const userStore = useUserStore();
const { iot_things_type } = useDict('iot_things_type');

const props = defineProps({
  device: {
    type: Object,
    default: null,
  },
});

const loading = ref(false);
const dialogValue = ref(false);
const opationList = ref<any[]>([]);
const functionName = ref('');
const btnLoading = ref(false);
const canSend = ref(false);
const chooseFun = ref<any>({});
const variableList = ref<any[]>([]);
const chartList = ref<any[]>([]);
const monitorChart = ref<any[]>([]);
const total = ref(0);
const shadowUnEnable = ref(false);
const deviceInfoRef = ref<any>({});
const serialNumber = ref('');
const centerDialogVisible = ref(false);
const form = ref<any>({});
const funVal = ref<any>({});
const subDeviceId = ref<any>(null);
const subDeviceList = ref<any[]>([]);
const queryFormRef = ref<any>(null);
const historyListRef = ref<any>(null);
const mapRefs = ref<any[]>([]);
let offUpdateData: (() => void) | null = null;

const modelParams = reactive({
  identifier: '',
  modelId: '',
  modelName: '',
  dataType: '',
  serialNumber: '',
  productId: '',
  deviceName: '',
  type: 0,
  deviceId: null as any,
  datatype: '' as any,
});

const queryParams = reactive({
  deviceId: null as any,
  type: null as any,
  modelName: '',
  pageNum: 1,
  pageSize: 24,
  isMonitor: '' as any,
});

const querySubParams = reactive({
  pageNum: 1,
  pageSize: 10,
  parentClientId: '' as any,
});

function setMapRef(el: any, index: number) {
  if (el) {
    mapRefs.value[index] = el;
  }
}

watch(
  () => props.device,
  (newVal, oldVal) => {
    if (newVal?.deviceId && newVal.deviceId !== oldVal?.deviceId) {
      deviceInfoRef.value = newVal;
      queryParams.deviceId = newVal.deviceId;
      getVariableList();
    }
  },
  { deep: true }
);

onMounted(() => {
  const { deviceId, serialNumber: sn } = props.device || {};
  if (deviceId) {
    queryParams.deviceId = deviceId;
    serialNumber.value = sn;
    getVariableList();
    getSubDeviceList();
  }
  initData();
});

onBeforeUnmount(() => {
  if (offUpdateData) offUpdateData();
});

function initData() {
  if (offUpdateData) return;
  const handler = (params: any) => updateParam(params);
  busEvent.on('updateData', handler);
  offUpdateData = () => {
    busEvent.off('updateData', handler);
    offUpdateData = null;
  };
}

function getSubDeviceList() {
  querySubParams.parentClientId = props.device?.serialNumber;
  listGateway(querySubParams).then((response: any) => {
    subDeviceList.value = response.rows.map((item: any) => ({
      label: item.subDeviceName,
      value: item.subDeviceId,
    }));
  });
}

function getVariableList() {
  loading.value = true;
  queryParams.deviceId = subDeviceId.value ? subDeviceId.value : props.device?.deviceId;
  listThingsModel(queryParams).then((res: any) => {
    if (res.code === 200) {
      if (queryParams.isMonitor === 1) {
        chartList.value = res.rows;
        nextTick(() => {
          MonitorChart();
        });
      } else {
        variableList.value = res.rows.map((item: any) => ({
          ...item,
          valueName: getValueName(item) || '-',
        }));
        variableList.value = variableList.value.sort((a: any, b: any) => b.order - a.order);
        chartList.value = chartList.value.sort((a: any, b: any) => b.order - a.order);
      }
      total.value = res.total;
    }
    loading.value = false;
  });
}

function MonitorChart() {
  if (chartList.value && chartList.value.length > 0) {
    for (let i = 0; i < chartList.value.length; i++) {
      const el = mapRefs.value[i];
      if (!el) continue;
      monitorChart.value[i] = {
        chart: proxy?.$echarts.init(el),
        data: {
          id: chartList.value[i].identifier,
          name: chartList.value[i].modelName,
          value: chartList.value[i].shadow ? chartList.value[i].shadow : chartList.value[i].datatype.min,
        },
      };
      const option = {
        tooltip: { formatter: ' {b} <br/> {c}' + chartList.value[i].datatype.unit },
        series: [
          {
            name: chartList.value[i].datatype.type,
            type: 'gauge',
            min: chartList.value[i].datatype.min,
            max: chartList.value[i].datatype.max,
            colorBy: 'data',
            splitNumber: 10,
            radius: '100%',
            splitLine: { distance: 4 },
            axisLabel: { fontSize: 10, distance: 10 },
            axisTick: { distance: 4 },
            axisLine: {
              lineStyle: {
                width: 8,
                color: [
                  [0.2, '#486FF2'],
                  [0.8, '#12d09f'],
                  [1, '#F56C6C'],
                ],
                opacity: 0.3,
              },
            },
            pointer: { icon: 'triangle', length: '60%', width: 7 },
            progress: { show: true, width: 8 },
            detail: {
              valueAnimation: true,
              formatter: '{value}' + ' ' + chartList.value[i].datatype.unit,
              offsetCenter: [0, '80%'],
              fontSize: 20,
            },
            data: [
              {
                value: chartList.value[i].shadow ? chartList.value[i].shadow : chartList.value[i].datatype.min,
                name: chartList.value[i].modelName,
              },
            ],
            title: { offsetCenter: [0, '115%'], fontSize: 16 },
          },
        ],
      };
      monitorChart.value[i].chart.setOption(option);
    }
  }
}

function handleQuery() {
  queryParams.pageNum = 1;
  getVariableList();
}

function handleResetQuery() {
  proxy?.resetForm(queryFormRef.value);
  subDeviceId.value = null;
  handleQuery();
}

async function editFunc(item: any) {
  const userName = (userStore as any).dept?.userName;
  if (userName !== props.device?.createBy) {
    const params = { deviceId: props.device?.deviceId, modelId: item.modelId };
    const response: any = await getOrderControl(params);
    if (response.code != 200) {
      proxy?.$modal.msgWarning(response.msg);
      return;
    }
  }
  serialNumber.value = item.serialNumber;
  if (props.device?.status !== 3) {
    let title = '';
    if (props.device?.status === 1) title = proxy?.$t('device.device-variable.930930-0');
    else if (props.device?.status === 2) title = proxy?.$t('device.device-variable.930930-1');
    else title = proxy?.$t('device.device-variable.930930-2');
    proxy?.$modal.msgWarning(title);
    return;
  }
  dialogValue.value = true;
  canSend.value = true;
  funVal.value = {};
  chooseFun.value = item;
  getOpationList(item);
}

function handleHistory(item: any) {
  if (item.isHistory === 1) {
    modelParams.identifier = item.identifier;
    modelParams.type = item.type;
    modelParams.deviceId = props.device?.deviceId;
    modelParams.serialNumber = item.serialNumber;
    modelParams.datatype = item.datatype.type;
    historyListRef.value.drawer = true;
    historyListRef.value.getDevChartDatas();
  }
}

function getOpationList(item: any) {
  opationList.value = [];
  let options: any[] = [];
  funVal.value = {};
  const datatype = item.datatype;
  if (datatype.type == 'enum') {
    options = datatype.enumList?.map((option: any) => ({ label: option.text, value: option.value + '' })) || [];
  }
  if (datatype.type == 'bool') {
    options = [
      { label: datatype.falseText || '', value: '0' },
      { label: datatype.trueText || '', value: '1' },
    ];
  }
  opationList.value.push({
    dataTypeName: datatype.type,
    arrayType: datatype.arrayType,
    label: item.modelName,
    key: item.identifier,
    max: parseInt(datatype?.max || 100),
    min: parseInt(datatype.min),
    options: options,
    value: item.value,
  });
  opationList.value.forEach((op: any) => {
    let value = op.value == null ? '-' : op.value < op.min || op.value > op.max ? 0 : op.value;
    if (
      op.dataTypeName == 'integer' ||
      op.dataTypeName == 'decimal' ||
      (op.dataTypeName == 'array' && op.arrayType == 'integer') ||
      (op.dataTypeName == 'array' && op.arrayType == 'decimal')
    ) {
      value = parseInt(value);
    }
    funVal.value[op.key] = value;
  });
}

async function sendService() {
  try {
    let params = funVal.value;
    const pas = { serialNumber: serialNumber.value, identifier: chooseFun.value.identifier, remoteCommand: params };
    btnLoading.value = true;
    if (['MODBUS-TCP-OVER-RTU', 'MODBUS-RTU', 'MODBUS-TCP'].includes(deviceInfoRef.value.protocolCode)) {
      const response: any = await serviceInvokeReply(pas);
      if (response.code === 200) {
        proxy?.$modal.msgSuccess(proxy?.$t('device.running-status.866086-25'));
      } else {
        proxy?.$modal.msgError(response.msg);
      }
    } else {
      const response: any = await serviceInvoke(pas);
      if (response.code === 200) {
        proxy?.$modal.msgSuccess(proxy?.$t('device.running-status.866086-25'));
      } else {
        proxy?.$modal.msgError(response.msg);
      }
    }
    for (let i = 0; i < variableList.value.length; i++) {
      if (variableList.value[i].identifier == chooseFun.value.identifier) {
        const variable = Object.values(funVal.value)[0];
        variableList.value[i].value = variable;
        variableList.value[i].valueName = getValueName(variableList.value[i]);
        break;
      }
    }
  } finally {
    btnLoading.value = false;
    dialogValue.value = false;
  }
}

function justNumber(val: any) {
  canSend.value = true;
  opationList.value.some((item: any) => {
    if (item.max < funVal.value[item.key] || item.min > funVal.value[item.key]) {
      canSend.value = false;
      return true;
    }
  });
}

function enumButtonClick(device: any, item: any, value: any) {
  // enum button click handler
}

function updateParam(params: any) {
  let { serialNumber: sn, data } = params || {};
  if (!data || serialNumber.value !== sn) return;

  let messageList: any = data?.message ?? data;
  if (typeof messageList === 'string') {
    try {
      messageList = JSON.parse(messageList);
    } catch {
      messageList = [];
    }
  }
  if (!Array.isArray(messageList) || messageList.length === 0) return;

  messageList.forEach((msg: any) => {
    variableList.value.some((item: any, index: number) => {
      const msgId = msg.id ?? msg.identifier;
      const itemId = item.identifier;
      const matched = msg.address ? msg.address === item.address && msgId === itemId : msgId === itemId;
      if (!matched) return false;

      const variable = {
        ...variableList.value[index],
        ts: msg.ts || variableList.value[index].ts,
        value: msg.value,
      };
      variable.valueName = getValueName(variable);
      variableList.value[index] = variable;
      return true;
    });

    for (let k = 0; k < chartList.value.length; k++) {
      const chartItem = chartList.value[k];
      const msgId = msg.id ?? msg.identifier;
      const chartId = chartItem.identifier ?? chartItem.id;
      const matched = msg.address ? msg.address === chartItem.address && msgId === chartId : msgId === chartId;
      if (!matched) continue;

      chartItem.value = msg.value;
      chartItem.shadow = msg.value;
      for (let m = 0; m < monitorChart.value.length; m++) {
        const mc = monitorChart.value[m];
        if (!mc?.data || !mc?.chart) continue;
        if (msgId == mc.data.id || chartId == mc.data.id) {
          mc.chart.setOption({
            series: [
              {
                data: [
                  {
                    value: chartItem.value,
                    name: mc.data.name,
                  },
                ],
              },
            ],
          });
          break;
        }
      }
      break;
    }
  });
}

function getValueName(item: any) {
  let res = item.value || '-';
  if (item.datatype) {
    switch (item.datatype.type) {
      case 'bool':
        if (0 == item.value) res = item.datatype.falseText;
        if (1 == item.value) res = item.datatype.trueText;
        break;
      case 'enum':
        item.datatype.enumList?.some((enumOpt: any) => {
          if (enumOpt.value == item.value) {
            res = enumOpt.text;
            return true;
          }
        });
        break;
    }
  }
  return res;
}

function activeCollection(item: any) {
  if (props.device?.status !== 3) {
    let title = '';
    if (props.device?.status === 1) title = proxy?.$t('device.device-variable.930930-9');
    else if (props.device?.status === 2) title = proxy?.$t('device.device-variable.930930-10');
    else title = proxy?.$t('device.device-variable.930930-11');
    proxy?.$modal.msgWarning(title);
    return;
  }
  centerDialogVisible.value = true;
  form.value.serialNumber = item.serialNumber;
  form.value.type = 1;
  form.value.identifier = item.identifier;
  form.value.parentSerialNumber = item.parentSerialNumber;
}

function confirmCollection() {
  propGet(form.value).then((response: any) => {
    if (response.code == 200) {
      centerDialogVisible.value = false;
    }
  });
}

function activeCollectionAll() {
  if (props.device?.status !== 3) {
    let title = '';
    if (props.device?.status === 1) title = proxy?.$t('device.device-variable.930930-9');
    else if (props.device?.status === 2) title = proxy?.$t('device.device-variable.930930-10');
    else title = proxy?.$t('device.device-variable.930930-11');
    proxy?.$modal.msgWarning(title);
    return;
  }
  centerDialogVisible.value = true;
  form.value.serialNumber = serialNumber.value;
  form.value.type = 2;
}
</script>

<style lang="scss" scoped>
.device-variable {
  width: 100%;
}
.is-active-btn {
  color: #1890ff;
  border-color: #badeff;
  background-color: #e8f4ff;
}
.disabled {
  pointer-events: none;
  opacity: 0.5;
}
.icon_tag {
  display: flex;
  justify-content: flex-end;
  overflow: hidden;
}
.data-container {
  display: flex;
  align-items: center;
  width: 100%;
}
.truncate-text {
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100px;
  min-width: 0;
}
.data-content {
  flex: 1;
  min-width: 0;
  font-size: 14px;
  margin-top: 20px;
}
.unit-text {
  font-size: 15px;
  margin: 20px 0 0 5px;
  flex-shrink: 0;
}
</style>
