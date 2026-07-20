<template>
  <div class="running-status beautify-scroll-def" v-loading="loading">
    <el-main style="position: relative" class="H100">
      <el-row :gutter="12" class="row-list" v-if="!loading && runningData.length > 0">
        <el-col :span="6" v-for="(item, index) in runningData" :key="index" style="margin-bottom: 10px; height: 110px">
          <el-card shadow="hover" class="elcard">
            <div class="head">
              <div class="title">
                {{ item.name || '--' }}
                <el-tooltip
                  :content="$t('device.realTime-status.845353-0')"
                  v-if="item.isReadonly == 0"
                  class="title_send"
                >
                  <el-icon class="send-icon" @click.stop="editFunc(item)">
                    <Promotion />
                    <span class="send_title">{{ $t('device.realTime-status.845353-1') }}</span>
                  </el-icon>
                </el-tooltip>
              </div>
              <div class="name">
                <span class="value_class">{{ item.valueName || '-' }}</span>
                <span v-if="item.datatype?.unit && item.datatype.unit != 'un'">
                  {{ item.datatype.unit || item.datatype.unitName }}
                </span>
              </div>
            </div>
            <div class="card-bottom">{{ $t('device.realTime-status.845353-2') }}{{ item.ts || '--' }}</div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty :image-size="200" v-if="!loading && runningData.length === 0" />
    </el-main>

    <!-- 指令下发弹窗 -->
    <el-dialog :title="$t('device.realTime-status.845353-3')" v-model="dialogValue" width="30%">
      <el-form style="height: 100%; padding: 0 20px">
        <el-form-item v-for="(item, index) in opationList" :label="`${item.label}：`" :key="index" label-width="180px">
          <el-input
            v-model="funVal[item.key]"
            @input="justicNumber(item)"
            type="number"
            v-if="item.dataTypeName == 'integer' || item.dataTypeName == 'decimal'"
            style="width: 50%"
          />
          <el-select
            v-if="item.dataTypeName == 'enum' || item.dataTypeName == 'singleBoolean' || item.dataTypeName == 'bool'"
            v-model="funVal[item.key]"
          >
            <el-option v-for="option in item.options" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
          <span
            v-if="
              (item.dataTypeName == 'integer' || item.dataTypeName == 'decimal') &&
              item.unit &&
              item.unit != 'un' &&
              item.unit != '/'
            "
          >
            （{{ item.unit }}）
          </span>
          <span class="range" v-if="item.dataTypeName == 'integer' || item.dataTypeName == 'decimal'">
            ({{ item.min }} ~ {{ item.max }})
          </span>
        </el-form-item>
        <el-form-item style="display: none">
          <el-input v-model="functionName" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogValue = false">{{ $t('device.realTime-status.845353-4') }}</el-button>
        <el-button type="primary" @click="sendService" :loading="btnLoading" :disabled="!canSend">
          {{ $t('device.realTime-status.845353-5') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted, getCurrentInstance } from 'vue';
import { Promotion } from '@element-plus/icons-vue';
import { serviceInvoke, runningStatus } from '@/api/iot/runstatus';
import busEvent from '@/utils/busEvent';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';

const { proxy } = getCurrentInstance() as any;
const { t } = useI18n();

const props = defineProps({
  device: {
    type: Object,
    default: () => ({}),
  },
});

const emit = defineEmits(['statusEvent']);

const loading = ref(false);
const runningData = ref<any[]>([]);
const dialogValue = ref(false);
const canSend = ref(false);
const btnLoading = ref(false);
const funVal = ref<Record<string, any>>({});
const opationList = ref<any[]>([]);
const functionName = ref('');
const from = ref<any>({});

// 内部状态
const params = ref<any>({});
const serialNumber = ref('');
const deviceInfo = ref<any>({});
const slaveList = ref<any[]>([]);

/** 监听 device prop 变化 */
watch(
  () => props.device,
  (newVal: any) => {
    if (newVal && newVal.serialNumber) {
      params.value.serialNumber = newVal.serialNumber;
      serialNumber.value = newVal.serialNumber;
      params.value.productId = newVal.productId;
      params.value.slaveId = newVal.slaveId;
      params.value.deviceId = newVal.deviceId;
      deviceInfo.value = newVal;
      updateDeviceStatus(deviceInfo.value);
      slaveList.value = newVal.subDeviceList || [];
      getSlaveList();
    }
  }
);

const updateDataHandler = (params: any) => {
  if (params.data && params.data[0]?.remark) {
    params.data[0].ts = params.data[0].remark;
  }
  updateData(params);
};

onMounted(() => {
  busEvent.on('updateData', updateDataHandler);
});

onUnmounted(() => {
  busEvent.off('updateData', updateDataHandler);
});

/** 获取运行状态 */
const getRuntimeStatus = () => {
  runningStatus(params.value).then((response: any) => {
    if (response.data?.thingsModels) {
      runningData.value = response.data.thingsModels.map((item: any) => {
        const result = { ...item, valueName: '' };
        result.valueName = resolveValueName(item);
        return result;
      });
    }
  });
};

/** 根据从机列表获取数据 */
const getSlaveList = () => {
  getRuntimeStatus();
};

/** 指令下发入口 */
const editFunc = (item: any) => {
  dialogValue.value = true;
  canSend.value = true;
  funVal.value = {};
  from.value = item;
  getValueName(item);
};

/** 更新设备状态标识 */
const updateDeviceStatus = (device: any) => {
  emit('statusEvent', device.status);
};

/** 解析显示值 */
const resolveValueName = (item: any): string => {
  const datatype = item.datatype || {};
  if (datatype.type == 'enum') {
    const found = (datatype.enumList || []).find((val: any) => val.value == item.value);
    return found ? found.text : item.value;
  } else if (datatype.type == 'bool') {
    return item.value == 0 ? datatype.falseText : datatype.trueText;
  }
  return item.value || '-';
};

/** 初始化下发值 */
const getValueName = (item: any) => {
  funVal.value[item.id] = item.value;
};

/** 发送指令 */
const sendService = () => {
  try {
    funVal.value[from.value.id] = from.value.shadow;
    const data = {
      serialNumber: serialNumber.value,
      productId: params.value.productId,
      remoteCommand: funVal.value,
      identifier: from.value.id,
      slaveId: params.value.slaveId,
      modelName: from.value.name,
      isShadow: (props.device as any).status != 3,
      type: from.value.type,
    };
    serviceInvoke(data).then((response: any) => {
      if (response.code == 200) {
        ElMessage({ type: 'success', message: t('device.running-status.866086-25') });
      }
    });
  } finally {
    dialogValue.value = false;
  }
};

/** 判断输入范围 */
const justicNumber = (item: any) => {
  canSend.value = true;
  if (
    from.value.datatype?.max < funVal.value[from.value.identity] ||
    from.value.datatype?.min > funVal.value[from.value.identity]
  ) {
    canSend.value = false;
  }
};

/** 实时数据更新 */
const updateData = (msg: any) => {
  if (msg.data && msg.data.length !== 0) {
    msg.data.forEach((d: any) => {
      runningData.value.some((old: any, index: number) => {
        if (d.slaveId === old.slaveId && d.id == old.id) {
          const template = { ...runningData.value[index] };
          template.ts = d.ts;
          template.value = d.value;
          if (old.datatype?.type == 'enum') {
            const found = (old.datatype.enumList || []).find((val: any) => val.value == template.value);
            if (found) template.value = found.text;
          } else if (old.datatype?.type == 'bool') {
            template.value = template.value == 0 ? old.datatype.falseText : old.datatype.trueText;
          }
          template.valueName = resolveValueName(template);
          runningData.value[index] = template;
          return true;
        }
      });
    });
  }
};
</script>

<style lang="scss" scoped>
.H100 {
  margin-left: 10px;
}

.row-list {
  height: 700px;
  overflow: auto;
  margin: -20px -20px -20px -30px !important;
  font-size: 12px;
  line-height: 20px;
}

.running-status {
  .elcard {
    height: 100%;
    .head {
      .title {
        font-size: 13px;
        color: #606266;
        display: flex;
        align-items: center;
        justify-content: space-between;
        .title_send {
          font-size: 12px;
          color: #409eff;
        }
        .send-icon {
          cursor: pointer;
          color: #1890ff;
        }
      }
      .name {
        margin-top: 8px;
        .value_class {
          font-size: 18px;
          font-weight: bold;
          color: #303133;
        }
      }
    }
    .card-bottom {
      margin-top: 8px;
      font-size: 11px;
      color: #909399;
    }
  }
}

.range {
  color: #909399;
  font-size: 12px;
  margin-left: 5px;
}
</style>
