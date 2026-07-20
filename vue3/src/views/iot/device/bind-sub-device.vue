<template>
  <el-dialog
    class="sub-device-dialog"
    :title="title"
    v-model="open"
    width="850px"
    append-to-body
    :close-on-click-modal="false"
    :close-on-press-escape="false"
  >
    <el-form ref="formRef" label-width="90px">
      <div class="device-form">
        <div class="sub-device-item" v-for="(subDevice, index) in subDevices" :key="index">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="10">
              <el-form-item
                :label="$t('device-add-sub-device.123456-0')"
                :prop="'subDevices.' + index + '.subDeviceName'"
              >
                <el-select
                  v-model="subDevice.subClientId"
                  :placeholder="$t('device-add-sub-device.123456-1')"
                  @change="handleSubDeviceChange(index, $event)"
                  clearable
                  filterable
                >
                  <el-option
                    v-for="item in gatewayList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :xs="24" :sm="12" :md="8">
              <el-form-item :label="$t('device-add-sub-device.123456-2')" :prop="'subDevices.' + index + '.address'">
                <el-input
                  v-model="subDevice.address"
                  :placeholder="$t('device-add-sub-device.123456-3')"
                  style="width: 200px"
                  type="number"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <div style="margin-left: 30px; margin-top: 10px">
                <el-button
                  size="small"
                  v-if="index != 0"
                  plain
                  type="danger"
                  style="padding: 5px"
                  :icon="Delete"
                  @click="handleDelete(index)"
                >
                  {{ $t('del') }}
                </el-button>
              </div>
            </el-col>
          </el-row>
        </div>
        <div>
          +
          <a style="color: #486ff2" @click="addSubDevice">{{ $t('device-add-sub-device.123456-5') }}</a>
        </div>
      </div>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="handleSubmitForm">{{ $t('device-add-sub-device.123456-5') }}</el-button>
        <el-button @click="cancel">{{ $t('cancel') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { Delete } from '@element-plus/icons-vue';
import { listSubGateway, addGatewayBatch } from '@/api/iot/gateway';

const { proxy } = getCurrentInstance()!;

const props = defineProps({
  gateway: {
    type: Object,
    default: null,
  },
});

const emit = defineEmits(['addSuccess']);

// 子设备数组
const subDevices = ref<any[]>([
  {
    subProductId: '',
    address: '',
    subClientId: '',
  },
]);
// 弹出层标题
const title = ref(proxy?.$t('device-add-sub-device.123456-6'));
// 是否显示弹出层
const open = ref(false);
// 网关与子设备关联表格数据
const gatewayList = ref<any[]>([]);
const loading = ref(false);
const total = ref(0);
const queryParams = reactive({});
const formRef = ref();

/** 查询网关与子设备关联列表 */
function getList() {
  loading.value = true;
  listSubGateway(queryParams).then((response: any) => {
    gatewayList.value = response.rows.map((item: any) => {
      return {
        value: item.serialNumber,
        label: item.deviceName,
        productId: item.productId,
      };
    });
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

function reset() {
  subDevices.value = [
    {
      subProductId: '',
      subClientId: '',
      address: '',
    },
  ];
}

// 删除
function handleDelete(index: number) {
  subDevices.value.splice(index, 1);
}

function addSubDevice() {
  subDevices.value.push({
    subProductId: '',
    subClientId: '',
    address: '',
  });
}

// 子设备名称选择
function handleSubDeviceChange(index: number, selectedValue: string) {
  const selectedDevice = gatewayList.value.find((item) => item.value === selectedValue);
  if (selectedDevice) {
    subDevices.value[index] = {
      ...subDevices.value[index],
      subProductId: selectedDevice.productId,
    };
  }
}

function handleSubmitForm() {
  const params = {
    parentClientId: props.gateway.parentClientId,
    parentDeviceId: props.gateway.deviceId,
    parentProductId: props.gateway.productId,
    subDeviceAddInfoVOList: subDevices.value,
  };
  addGatewayBatch(params).then(() => {
    open.value = false;
    reset();
    proxy?.$modal.msgSuccess(proxy?.$t('device-add-sub-device.123456-7'));
    emit('addSuccess');
  });
}

onMounted(() => {
  getList();
  reset();
});

defineExpose({ open, reset, getList });
</script>
