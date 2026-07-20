<template>
  <div class="device-variable">
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item prop="type">
        <el-select v-model="queryParams.type" :placeholder="$t('device.variable-case.347856-1')" clearable>
          <el-option v-for="dict in iot_things_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item prop="modelName">
        <el-input v-model="queryParams.modelName" :placeholder="$t('device.variable-case.347856-3')" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">
          {{ $t('device.variable-case.347856-4') }}
        </el-button>
        <el-button :icon="Refresh" @click="handleResetQuery">
          {{ $t('device.variable-case.347856-5') }}
        </el-button>
        <el-button :icon="Refresh" @click="activeCollectionAll">
          {{ $t('device.variable-case.347856-16') }}
        </el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="variableList" style="width: 100%; margin-top: 5px" :border="false">
      <el-table-column prop="identifier" :label="$t('device.variable-case.347856-6')" width="130" />
      <el-table-column prop="type" :label="$t('device.variable-case.347856-7')" width="100">
        <template #default="scope">
          <dict-tag :options="iot_things_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column prop="modelName" :label="$t('device.variable-case.347856-8')" />
      <el-table-column prop="ts" :label="$t('device.variable-case.347856-9')" />
      <el-table-column prop="value" :label="$t('device.variable-case.347856-10')">
        <template #default="scope">
          <span>
            {{ scope.row.valueName === '' || scope.row.valueName === null ? '-' : scope.row.valueName }}
            {{ scope.row.unit }}
            <el-icon
              v-if="scope.row.isReadonly === 0 && scope.row.type != 3"
              style="cursor: pointer; color: #1890ff"
              @click="editFunc(scope.row)"
            >
              <Edit />
            </el-icon>
          </span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('device.variable-case.347856-11')"
        align="center"
        class-name="small-padding fixed-width"
        width="200"
      >
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            link
            @click="activeCollection(scope.row)"
            v-if="props.device.protocolCode === 'MODBUS-TCP-OVER-RTU' || props.device.protocolCode === 'MODBUS-RTU'"
          >
            {{ $t('device.variable-case.347856-13') }}
          </el-button>
          <el-button size="small" type="primary" link @click="handleQueryHistory(scope.row)">
            {{ $t('device.variable-case.347856-12') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getVariableList"
    />

    <!-- 主动采集确认弹窗 -->
    <el-dialog :title="$t('device.variable-case.347856-15')" v-model="centerDialogVisible" width="30%" center>
      <span>{{ $t('device.variable-case.347856-14') }}</span>
      <template #footer>
        <el-button @click="centerDialogVisible = false">{{ $t('iot.group.device-list.849593-12') }}</el-button>
        <el-button type="primary" @click="confirmCollection">{{ $t('iot.group.device-list.849593-11') }}</el-button>
      </template>
    </el-dialog>

    <!-- 指令下发弹窗 -->
    <el-dialog :title="$t('device.realTime-status.099127-26')" v-model="dialogValue" width="30%">
      <el-form style="height: 100%; padding: 0 20px">
        <el-form-item v-for="(item, index) in opationList" :label="`${item.label}：`" :key="index" label-width="180px">
          <el-input
            v-model="funVal[item.key]"
            @input="justNumber(item)"
            type="number"
            v-if="
              item.dataTypeName == 'integer' ||
              item.dataTypeName == 'decimal' ||
              (item.dataTypeName == 'array' && item.arrayType == 'integer') ||
              (item.dataTypeName == 'array' && item.arrayType == 'decimal')
            "
            style="width: 50%"
          />
          <el-input
            v-model="funVal[item.key]"
            :placeholder="$t('plzInput')"
            type="text"
            v-if="item.dataTypeName == 'string' || (item.dataTypeName == 'array' && item.arrayType == 'string')"
            style="width: 50%"
            @input="justNumber(item)"
          />
          <el-select v-if="item.dataTypeName == 'enum' || item.dataTypeName == 'bool'" v-model="funVal[item.key]">
            <el-option v-for="option in item.options" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
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
            class="range"
            v-if="
              item.dataTypeName == 'integer' ||
              item.dataTypeName == 'decimal' ||
              (item.dataTypeName == 'array' && item.arrayType == 'integer') ||
              (item.dataTypeName == 'array' && item.arrayType == 'decimal')
            "
          >
            ({{ item.min }} ~ {{ item.max }})
          </span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogValue = false">{{ $t('cancel') }}</el-button>
        <el-button type="primary" @click="sendService" :loading="btnLoading" :disabled="!canSend">
          {{ $t('confirm') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, onUnmounted, getCurrentInstance } from 'vue';
import { Search, Refresh, Edit } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict';
import { useRouter } from 'vue-router';
import { listThingsModel } from '@/api/iot/device';
import { serviceInvokeReply, serviceInvoke, propGet } from '@/api/iot/runstatus';
import { getOrderControl } from '@/api/iot/control';
import { useUserStore } from '@/stores/modules/user';
import busEvent from '@/utils/busEvent';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();
const { iot_things_type } = useDict('iot_things_type');
const router = useRouter();
const userStore = useUserStore();

const props = defineProps({
  device: {
    type: Object,
    default: () => ({}),
  },
});

const loading = ref(false);
const total = ref(0);
const variableList = ref<any[]>([]);
const dialogValue = ref(false);
const centerDialogVisible = ref(false);
const canSend = ref(false);
const btnLoading = ref(false);
const funVal = ref<Record<string, any>>({});
const chooseFun = ref<any>({});
const deviceInfo = ref<any>({});
const serialNumber = ref('');
const opationList = ref<any[]>([]);
const form = ref<any>({});

const queryFormRef = ref<any>(null);

const queryParams = reactive({
  deviceId: null as any,
  type: null as any,
  modelName: '',
  pageNum: 1,
  pageSize: 10,
});

/** 监听 device 变化 */
watch(
  () => props.device,
  (newVal: any, oldVal: any) => {
    if (newVal?.deviceId && newVal.deviceId !== oldVal?.deviceId) {
      deviceInfo.value = newVal;
      queryParams.deviceId = newVal.deviceId;
      getVariableList();
    }
  },
  { deep: true }
);

const updateParamHandler = (params: any) => {
  updateParam(params);
};

onMounted(() => {
  const { deviceId, serialNumber: sn } = props.device as any;
  if (deviceId) {
    queryParams.deviceId = deviceId;
    serialNumber.value = sn;
    getVariableList();
  }
  deviceInfo.value = props.device;
  // 监听实时数据更新
  busEvent.on('updateData', updateParamHandler);
});

onUnmounted(() => {
  busEvent.off('updateData', updateParamHandler);
});

/** 获取变量列表 */
const getVariableList = () => {
  loading.value = true;
  listThingsModel(queryParams).then((res: any) => {
    if (res.code === 200) {
      variableList.value = res.rows.map((item: any) => ({
        ...item,
        valueName: getValueName(item),
        dataTypeName: item.datatype?.type || '',
      }));
      total.value = res.total;
    }
    loading.value = false;
  });
};

/** 搜索 */
const handleQuery = () => {
  queryParams.pageNum = 1;
  getVariableList();
};

/** 重置搜索 */
const handleResetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

/** 指令下发 */
const editFunc = async (item: any) => {
  const userName = userStore.dept?.userName;
  if (userName !== (props.device as any).createBy) {
    const params = {
      deviceId: (props.device as any).deviceId,
      modelId: item.modelId,
    };
    const response: any = await getOrderControl(params);
    if (response.code != 200) {
      ElMessage({ type: 'warning', message: response.msg });
      return;
    }
  }
  serialNumber.value = item.serialNumber;
  const device = props.device as any;
  if (device.status !== 3) {
    let title = '';
    if (device.status === 1) {
      title = proxy?.$t('device.device-variable.930930-0');
    } else if (device.status === 2) {
      title = proxy?.$t('device.device-variable.930930-1');
    } else {
      title = proxy?.$t('device.device-variable.930930-2');
    }
    ElMessage({ type: 'warning', message: title });
    return;
  }
  dialogValue.value = true;
  canSend.value = true;
  funVal.value = {};
  chooseFun.value = item;
  getOpationList(item);
};

/** 封装操作列表 */
const getOpationList = (item: any) => {
  opationList.value = [];
  funVal.value = {};
  const datatype = item.datatype;
  let options: any[] = [];
  if (datatype.type == 'enum') {
    options = (datatype.enumList || []).map((option: any) => ({
      label: option.text,
      value: option.value + '',
    }));
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
    min: parseInt(datatype?.min || -100),
    options,
    value: item.value,
    unit: datatype.unit,
  });
  opationList.value.forEach((op) => {
    let value = op.value;
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
};

/** 发送指令 */
const sendService = async () => {
  try {
    const pas = {
      serialNumber: serialNumber.value,
      identifier: chooseFun.value.identifier,
      remoteCommand: funVal.value,
    };
    btnLoading.value = true;
    const device = props.device as any;
    if (['MODBUS-TCP-OVER-RTU', 'MODBUS-RTU'].includes(device.protocolCode)) {
      const response: any = await serviceInvokeReply(pas);
      if (response.code === 200) {
        ElMessage({ type: 'success', message: t('device.running-status.866086-25') });
      } else {
        ElMessage.error(response.msg);
      }
    } else {
      const response: any = await serviceInvoke(pas);
      if (response.code === 200) {
        ElMessage({ type: 'success', message: t('device.running-status.866086-25') });
      } else {
        ElMessage.error(response.msg);
      }
    }
    for (let i = 0; i < variableList.value.length; i++) {
      if (variableList.value[i].identifier == chooseFun.value.identifier) {
        const variable = Object.values(funVal.value)[0];
        if (device.status == 3) {
          variableList.value[i].value = variable;
          variableList.value[i].valueName = getValueName(variableList.value[i]);
        }
        break;
      }
    }
  } finally {
    btnLoading.value = false;
    dialogValue.value = false;
  }
};

/** 判断输入是否超过范围 */
const justNumber = (val: any) => {
  canSend.value = true;
  opationList.value.some((item) => {
    if (item.max < funVal.value[item.key] || item.min > funVal.value[item.key]) {
      canSend.value = false;
      return true;
    }
  });
};

/** 查询历史数据 */
const handleQueryHistory = (item: any) => {
  router.push({
    path: '/dataCenter/history',
    query: {
      deviceId: (props.device as any).deviceId,
      identifier: item.identifier,
      activeName: 'device',
    },
  });
};

/** 更新参数值 */
const updateParam = (params: any) => {
  let { data } = params;
  if (data) {
    data = data.message;
    (data || []).forEach((msg: any) => {
      variableList.value.some((item, index) => {
        if (msg.id === item.identifier) {
          variableList.value[index] = {
            ...variableList.value[index],
            ts: msg.ts,
            value: msg.value,
            valueName: getValueName({ ...variableList.value[index], value: msg.value }),
          };
          return true;
        }
      });
    });
  }
};

/** 获取显示值 */
const getValueName = (item: any): string => {
  let res = item.value || '-';
  if (item.datatype) {
    switch (item.datatype.type) {
      case 'bool':
        if (item.value == 0) res = item.datatype.falseText;
        if (item.value == 1) res = item.datatype.trueText;
        break;
      case 'enum':
        (item.datatype.enumList || []).some((enumOpt: any) => {
          if (enumOpt.value == item.value) {
            res = enumOpt.text;
            return true;
          }
        });
        break;
    }
  }
  return res;
};

/** 主动采集单个 */
const activeCollection = (item: any) => {
  centerDialogVisible.value = true;
  form.value.serialNumber = item.serialNumber;
  form.value.type = 1;
  form.value.identifier = item.identifier;
  form.value.parentSerialNumber = item.parentSerialNumber;
};

/** 确认采集 */
const confirmCollection = () => {
  propGet(form.value).then((response: any) => {
    if (response.code == 200) {
      centerDialogVisible.value = false;
    }
  });
};

/** 采集所有 */
const activeCollectionAll = () => {
  centerDialogVisible.value = true;
  form.value.serialNumber = serialNumber.value;
  form.value.type = 2;
};
</script>

<style lang="scss" scoped>
.device-variable {
  width: 100%;
  padding: 5px 5px 10px 20px;
}
</style>
