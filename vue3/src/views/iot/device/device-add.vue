<template>
  <div class="device-add-wrap">
    <el-dialog :title="$t('device.device-edit.148398-84')" v-model="open" width="900px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
            <el-form-item :label="$t('device.device-edit.148398-1')" prop="deviceName">
              <el-input
                style="width: 300px"
                v-model="form.deviceName"
                :placeholder="$t('device.device-edit.148398-2')"
              ></el-input>
            </el-form-item>
            <el-form-item label="" prop="productName">
              <template #label>
                <span style="color: red">*</span>
                {{ $t('device.device-edit.148398-4') }}
              </template>
              <el-input
                style="width: 300px"
                readonly
                v-model="form.productName"
                :placeholder="$t('device.device-edit.148398-5')"
              >
                <template #append>
                  <el-button @click="selectProduct()">{{ $t('device.device-edit.148398-6') }}</el-button>
                </template>
              </el-input>
            </el-form-item>
            <!-- 设备编号 -->
            <el-form-item :label="$t('device.device-edit.148398-7')" prop="serialNumber">
              <el-input
                style="width: 300px"
                v-model="form.serialNumber"
                @input="handleSerialNumberInput"
                :placeholder="$t('device.device-edit.148398-8')"
                maxlength="64"
                minlength="9"
                :readonly="isGb28181Device"
              >
                <template #append v-if="!isGb28181Device">
                  <el-button @click="generateNum" :loading="genDisabled" v-hasPermi="['iot:device:add']">
                    {{ $t('device.device-edit.148398-9') }}
                  </el-button>
                </template>
                <template #append v-if="isGb28181Device">
                  <el-button @click="genSipID()" v-hasPermi="['iot:device:add']">
                    {{ $t('device.device-edit.148398-9') }}
                  </el-button>
                </template>
              </el-input>
            </el-form-item>
            <!-- modbus主机 -->
            <el-form-item
              :label="$t('device.device-edit.148398-103')"
              prop="deviceIp"
              v-if="form.protocolCode === 'MODBUS-TCP' && form.deviceType !== 4"
            >
              <el-input style="width: 300px" v-model="form.deviceIp" placeholder="127.0.0.1" maxlength="32"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
            <el-form-item :label="$t('device.device-edit.148398-12')" prop="firmwareVersion">
              <el-input
                style="width: 300px"
                v-model="form.firmwareVersion"
                :placeholder="$t('device.device-edit.148398-13')"
                :disabled="form.deviceType === 3"
              >
                <template #prepend>Version</template>
                <template #append>
                  {{ form.firmwareType === 1 ? $t('firmware.index.222541-52') : $t('firmware.index.222541-53') }}
                </template>
              </el-input>
            </el-form-item>
            <el-form-item :label="$t('device.device-edit.148398-19')" prop="locationWay">
              <el-select
                style="width: 300px"
                v-model="form.locationWay"
                :placeholder="$t('device.device-edit.148398-20')"
              >
                <el-option
                  v-for="dict in iot_location_way"
                  :key="dict.value"
                  :label="dict.label"
                  :value="Number(dict.value)"
                />
              </el-select>
            </el-form-item>
            <!-- 设备影子 -->
            <el-form-item v-if="form.deviceType !== 3" :label="$t('device.device-edit.148398-15')" prop="isShadow">
              <el-radio-group v-model="form.isShadow">
                <el-radio v-for="dict in device_shadow" :key="dict.value" :value="Number(dict.value)">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <!-- modbus端口 -->
            <el-form-item
              :label="$t('device.device-edit.148398-104')"
              prop="devicePort"
              v-if="form.protocolCode === 'MODBUS-TCP' && (form.deviceType === 1 || form.deviceType === 2)"
            >
              <el-input
                style="width: 300px"
                v-model="form.devicePort"
                :placeholder="$t('device.device-edit.148398-96')"
                maxlength="32"
                :readonly="form.deviceType === 3"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="sub-product-container" v-if="gatewayList.length > 0">
        <!-- 子产品列表导航 -->
        <div class="sub-product-header">
          <h3 class="list-title">
            <svg-icon class-name="title-icon" icon-class="product" />
            {{ $t('device.device-add.222343-0') }}
          </h3>
          <div class="header-divider"></div>
        </div>

        <!-- 子产品折叠面板 -->
        <el-collapse class="sub-product-collapse" v-if="gatewayList.length > 0" accordion>
          <el-collapse-item v-for="(subProduct, index) in gatewayList" :key="index" class="sub-product-item">
            <template #title>
              <div class="sub-product-title">
                <svg-icon class-name="product-icon" icon-class="product" />
                <span class="product-name">{{ subProduct.subProductName }}</span>
                <span class="product-status" :class="subProduct.enable ? 'status-active' : 'status-inactive'">
                  {{ subProduct.enable ? $t('device.device-add.222343-1') : $t('device.device-add.222343-2') }}
                </span>
              </div>
            </template>

            <el-form label-width="100px" class="sub-product-form">
              <div class="form-section">
                <el-row>
                  <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
                    <el-form-item :label="$t('device.device-add.222343-3')" class="form-item">
                      <el-switch
                        v-model="subProduct.enable"
                        @change="changeSwitch(subProduct)"
                        active-color="#486ff2"
                      ></el-switch>
                    </el-form-item>
                  </el-col>

                  <el-col
                    :xs="24"
                    :sm="24"
                    :md="24"
                    :lg="12"
                    :xl="12"
                    v-if="subProduct.enable"
                    class="device-count-col"
                  >
                    <el-form-item :label="$t('device.device-add.222343-5')" class="form-item">
                      <el-input
                        v-model="subProduct.deviceCount"
                        :min="1"
                        :max="20"
                        type="number"
                        @input="updateDeviceList(subProduct)"
                        :placeholder="$t('device.device-add.222343-5')"
                        class="device-count-input"
                      ></el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
              </div>

              <!-- 子产品列表配置 -->
              <div v-if="subProduct.enable && subProduct.deviceCount > 0" class="devices-section">
                <div class="devices-header">
                  <svg-icon class-name="device-icon" icon-class="device" />
                  <h4 class="devices-title">{{ $t('device.device-add.222343-6') }}</h4>
                  <div class="devices-divider"></div>
                </div>

                <el-collapse class="devices-collapse" style="margin-top: 10px" accordion>
                  <el-collapse-item
                    v-for="(device, deviceIndex) in subProduct.devices"
                    :key="deviceIndex"
                    :title="$t('device.device-add.222343-7') + (Number(deviceIndex) + 1)"
                    class="device-item"
                  >
                    <div class="device-form">
                      <el-row>
                        <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12" class="device-form-col">
                          <el-form-item :label="$t('device.device-add.222343-8')" class="device-form-item">
                            <el-input
                              v-model="device.name"
                              :placeholder="$t('device.device-add.222343-8')"
                              class="device-input"
                            ></el-input>
                          </el-form-item>
                        </el-col>
                        <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12" class="device-form-col">
                          <el-form-item :label="$t('device.device-add.222343-10')" class="device-form-item">
                            <el-input
                              v-model="device.address"
                              :placeholder="$t('device.device-add.222343-11')"
                              class="device-input"
                              type="number"
                            ></el-input>
                          </el-form-item>
                        </el-col>
                      </el-row>
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
            </el-form>
          </el-collapse-item>
        </el-collapse>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleSubmitForm" type="primary" :loading="submitLoading" :disabled="submitLoading">
            {{ $t('device.product-list.058448-14') }}
          </el-button>
          <el-button @click="open = false">{{ $t('device.product-list.058448-15') }}</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 选择产品 -->
    <product-list ref="productListRef" :productId="form.productId" @productEvent="getProductData($event)" />
    <!-- 监控设备生成通道id -->
    <sipid ref="sipidGenRef" :product="form" @addGenEvent="getSipIDData($event)" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, getCurrentInstance } from 'vue';
import productList from './product-list.vue';
import sipid from '../sip/sipidGen.vue';
import { addDevice, generatorDeviceNum, modbusTcpHostList } from '@/api/iot/device';
import { gatewayProductList } from '@/api/iot/gateway';
import useDict from '@/utils/dict/useDict';

const { iot_location_way, device_shadow } = useDict('iot_location_way', 'device_shadow');

const { proxy } = getCurrentInstance() as any;
const emit = defineEmits(['success']);

// 响应式数据
const open = ref(false);
const submitLoading = ref(false); // 防止重复提交
const genDisabled = ref(false); // 生成设备编码是否禁用
const loading = ref(true); // 遮罩层
const deviceId = ref('');
const openTip = ref(false);
const openServerTip = ref(false);
const serverType = ref(1);
const optionList = ref<any[]>([]);
const gwSerialNumber = ref('');
const gatewayList = ref<any[]>([]); // 子产品数组
const activeCollapse = ref([]); // 当前展开的面板
const subProductList = ref<any[]>([]); // 子产品列表
const isSubmitting = ref(false);

// 表单数据
const form = ref<any>({
  deviceId: 0,
  deviceName: null,
  productId: null,
  productName: null,
  userId: null,
  userName: null,
  tenantId: null,
  tenantName: null,
  serialNumber: '',
  firmwareType: 1,
  firmwareVersion: 1,
  status: 1,
  rssi: null,
  networkAddress: null,
  networkIp: null,
  longitude: null,
  latitude: null,
  activeTime: null,
  createBy: null,
  createTime: null,
  updateBy: null,
  updateTime: null,
  remark: null,
  locationWay: 1,
  clientId: 0,
  isShadow: 0,
  gwDeviceId: '',
  deviceIp: '',
  devicePort: '502',
  transport: null,
});

// 表单校验规则
const rules = reactive({
  deviceName: [
    {
      required: true,
      message: proxy?.$t('device.device-edit.148398-60'),
      trigger: 'blur',
    },
    {
      min: 2,
      max: 32,
      message: proxy?.$t('device.device-edit.148398-61'),
      trigger: 'blur',
    },
  ],
  firmwareVersion: [
    {
      required: true,
      message: proxy?.$t('device.device-edit.148398-62'),
      trigger: 'blur',
    },
    {
      validator: (rule: any, value: any, callback: any) => {
        const regex = /^[a-zA-Z0-9][a-zA-Z0-9]*(?:\.[a-zA-Z0-9]+)*$/;
        if (!regex.test(value)) {
          callback(new Error(proxy?.$t('device.device-edit.148398-108')));
        } else {
          callback();
        }
      },
      trigger: 'blur',
    },
  ],
  devicePort: [
    {
      required: true,
      message: proxy?.$t('device.device-edit.148398-97'),
      trigger: 'blur',
    },
  ],
  serialNumber: [
    {
      required: true,
      message: proxy?.$t('device.device-edit.148398-65'),
      trigger: 'blur',
    },
    {
      min: 9,
      max: 64,
      message: proxy?.$t('device.device-edit.148398-109'),
      trigger: 'blur',
    },
    { validator: validateSerialNumber, trigger: 'blur' },
  ],
  deviceIp: [
    {
      required: true,
      message: proxy?.$t('device.device-edit.148398-102'),
      trigger: 'blur',
    },
  ],
});

// 计算属性
const deviceStatus = computed({
  get() {
    if (form.value.status == 2) {
      return 1;
    }
    return 0;
  },
  set(val: number) {
    if (val == 1) {
      // 1-未激活，2-禁用，3-在线，4-离线
      form.value.status = 2;
    } else if (val == 0) {
      form.value.status = 1;
    }
  },
});

const isGb28181Device = computed(() => form.value.deviceType === 3 && form.value.transport === 'GB28181');

// Refs
const formRef = ref();
const productListRef = ref();
const sipidGenRef = ref();

// 选择产品
function selectProduct() {
  form.value.serialNumber = '';
  form.value.deviceIp = '';
  productListRef.value.open = true;
  productListRef.value.getList();
}

// 获取选中的产品
function getProductData(product: any) {
  form.value.productId = product.productId;
  form.value.productName = product.productName;
  form.value.deviceType = product.deviceType;
  form.value.protocolCode = product.protocolCode;
  form.value.transport = product.transport;
  form.value.tenantId = product.tenantId;
  form.value.tenantName = product.tenantName;
  form.value.firmwareType = product.firmwareType;
  form.value.slaveId = product.slaveId;
  gatewayList.value = []; //重置
  if (product.deviceType === 2) {
    const params = {
      pageNum: 1,
      pageSize: 999,
      gwProductId: product.productId,
    };
    getSubProductList(params);
  }
  if (product.deviceType == 3) {
    form.value.locationWay = 3;
  } else {
    form.value.locationWay = 1;
  }
  if (product.transport === 'TCP') {
    openServerTip.value = true;
    serverType.value = 3;
  } else {
    openServerTip.value = false;
    serverType.value = 1;
  }
}

// 校验设备编号
function validateSerialNumber(rule: any, value: any, callback: any) {
  const reg = /^[0-9a-zA-Z]+$/;
  if (!reg.test(value)) {
    callback(new Error(proxy?.$t('device.device-edit.148398-66')));
  } else {
    callback();
  }
}

// 查询网关设备列表
function getHostList() {
  optionList.value = [];
  modbusTcpHostList().then((response: any) => {
    optionList.value = response.rows.map((item: any) => {
      return { value: item.deviceId, label: item.deviceName, deviceIp: item.deviceIp };
    });
  });
}

// 生成随机字母和数字
function generateNum() {
  if (!form.value.productId || form.value.productId == 0) {
    proxy?.$modal.alertError(proxy?.$t('device.device-edit.148398-72'));
    return;
  }
  genDisabled.value = true;
  const params = { type: serverType.value };
  generatorDeviceNum(params).then((response: any) => {
    form.value.serialNumber = response.data;
    genDisabled.value = false;
  });
}

// 限制序列号只能输入英文字符、数字和下划线
function handleSerialNumberInput() {
  const reg = /^[a-zA-Z0-9_]*$/;
  if (!reg.test(form.value.serialNumber)) {
    // 移除非法字符
    form.value.serialNumber = form.value.serialNumber.replace(/[^a-zA-Z0-9_]/g, '');
  }
}

// 生成监控设备ID
function genSipID() {
  if (!isGb28181Device.value) {
    return;
  }
  sipidGenRef.value?.openDialog?.();
}

// 生成通道id
function getSipIDData(devsipid: any) {
  form.value.serialNumber = devsipid;
}

// 表单重置
function reset() {
  form.value = {
    deviceId: 0,
    deviceName: null,
    productId: null,
    productName: null,
    userId: null,
    userName: null,
    tenantId: null,
    tenantName: null,
    serialNumber: '',
    firmwareType: 1,
    firmwareVersion: 1,
    status: 1,
    rssi: null,
    networkAddress: null,
    networkIp: null,
    longitude: null,
    latitude: null,
    activeTime: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null,
    locationWay: 1,
    clientId: 0,
    isShadow: 0,
    gwDeviceId: '',
    deviceIp: '',
    devicePort: '502',
  };
  deviceStatus.value = 0;
  gatewayList.value = [];
  submitLoading.value = false;
  proxy?.resetForm(formRef.value);
}

// 提交按钮
function handleSubmitForm() {
  // 防止重复点击
  if (submitLoading.value) return;
  if (form.value.protocolCode === 'MODBUS-TCP' && form.value.deviceType !== 4) {
    form.value.devicePort = form.value.devicePort || 502;
    let ipReg =
      /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
    if (!ipReg.test(form.value.deviceIp)) {
      proxy?.$modal.alertError(proxy?.$t('device.device-edit.148398-98'));
      return;
    }
  }
  if (form.value.productId == null || form.value.productId == 0) {
    proxy?.$modal.alertError(proxy?.$t('device.device-edit.148398-67'));
    return;
  }
  subProductList.value = gatewayList.value.map((item) => {
    return {
      subProductId: item.subProductId,
      subDeviceList: item.devices.map((device: any) => {
        return {
          deviceName: device.name,
          address: device.address.toString(),
        };
      }),
    };
  });
  // 标记为提交中，validate 不通过时会在回调中复位
  submitLoading.value = true;
  proxy?.$refs['formRef'].validate((valid: boolean) => {
    if (valid) {
      const params = {
        ...form.value,
        subProductList: subProductList.value,
      };
      addDevice(params)
        .then((response: any) => {
          if (response.code == 200) {
            proxy?.$modal.msgSuccess(proxy?.$t('device.device-edit.148398-105'));
            if (form.value.status == 2) {
              deviceStatus.value = 1;
            }
            emit('success');
            open.value = false;
          } else {
            proxy?.$modal.msgError(response.msg);
          }
        })
        .catch((error: any) => {
          // 捕获请求异常（如网络错误）
          proxy?.$modal.msgError(proxy?.$t('device.device-add.222343-12'));
          console.error('提交异常：', error);
        })
        .finally(() => {
          // 无论成功/失败，最终都重置加载状态
          submitLoading.value = false;
        });
    } else {
      // 表单校验失败，立即重置加载状态
      submitLoading.value = false;
      return false; // 阻止表单提交
    }
  });
}

function updateDeviceList(subProduct: any) {
  // 确保deviceCount是非负整数
  if (subProduct.deviceCount < 0) {
    subProduct.deviceCount = 0;
  }
  let newLen = Math.max(0, Math.floor(subProduct.deviceCount));
  let oldLen = subProduct.devices.length;
  if (newLen > oldLen) {
    for (let i = oldLen; i < newLen; i++) {
      subProduct.devices.push({ name: '', address: '' });
    }
  } else {
    subProduct.devices = subProduct.devices.slice(0, newLen);
  }
}

/** 查询网关与子设备关联列表 */
function getSubProductList(params: any) {
  loading.value = true;
  gatewayList.value = [];
  gatewayProductList(params).then((response: any) => {
    if (response.code === 200) {
      gatewayList.value = response.rows.map((item: any) => {
        return {
          ...item,
          enable: false,
          deviceCount: 1,
          devices: [],
        };
      });
    }
    loading.value = false;
  });
}

function changeSwitch(subProduct: any) {
  subProduct.deviceCount = 1;
  if (!subProduct.enable) {
    subProduct.devices = [];
  } else {
    updateDeviceList(subProduct);
  }
}

// 暴露公共属性和方法
defineExpose({
  open,
  reset,
});
</script>

<style lang="scss" scoped>
.device-add-wrap {
  .alert-wrap {
    height: 35px;
    margin-top: 10px;

    :deep(.el-alert__icon) {
      font-size: 16px;
      width: 16px;
    }

    :deep(.el-alert__description) {
      font-size: 12px;
      margin: 0;
    }
  }
}

:deep(.el-collapse-item__header) {
  border-bottom: none;
  border-top: none;
}

:deep(.el-collapse-item__content) {
  padding: 0;
}

:deep(.devices-section) {
  border-top: none;
}

.sub-product-container {
  padding: 20px;
  background-color: #f9fafc;
  border-radius: 8px;
  min-height: 200px;
  margin-top: -10px;
}

/* 子产品头部样式 */
.sub-product-header {
  margin-bottom: 20px;
}

.list-title {
  color: #486ff2;
  font-size: 18px;
  font-weight: 500;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
}

.title-icon {
  margin-right: 8px;
  font-size: 20px;
}

.header-divider {
  height: 2px;
  background-color: #e8e8e8;
  margin-bottom: 10px;
}

/* 子产品折叠面板样式 */
.sub-product-collapse {
  border: none;
}

.sub-product-item {
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  margin-bottom: 15px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;
}

.sub-product-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.sub-product-title {
  display: flex;
  align-items: center;
  padding: 5px 0;
}

.product-icon {
  margin-right: 8px;
  color: #486ff2;
  margin-left: 10px;
}

.product-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.product-status {
  margin-left: auto;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  height: 30px;
  /* 垂直居中关键样式 */
  line-height: 30px; /* 与height保持一致，实现垂直居中 */
  display: inline-flex; /* 启用弹性盒模型 */
  align-items: center; /* 弹性盒内垂直居中 */
  justify-content: center; /* 弹性盒内水平居中（可选，增强效果） */
}

.status-active {
  background-color: #e6f7ee;
  color: #00b42a;
}

.status-inactive {
  background-color: #fef0f0;
  color: #f53f3f;
}

/* 表单样式 */
.sub-product-form {
  padding: 15px 20px;
  background-color: #fff;
}

.form-section {
  padding: 10px 0;
  border-bottom: 1px dashed #e8e8e8;
  margin-bottom: 15px;
}

.form-item {
  margin-bottom: 15px !important;
}

.device-count-col {
  transition: all 0.3s ease;
}

.device-count-input {
  width: 200px !important;
}

/* 子设备区域样式 */
.devices-section {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #e8e8e8;
}

.devices-header {
  margin-bottom: 15px;
}

.devices-title {
  color: #333;
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
}

.device-icon {
  margin-right: 8px;
  color: #722ed1;
}

.devices-divider {
  height: 1px;
  background-color: #e8e8e8;
  margin-bottom: 10px;
}

.devices-collapse {
  border: 1px solid #f0f0f0;
  border-radius: 4px;
}

.device-item {
  border-bottom: 1px solid #f0f0f0 !important;
  margin-left: 10px;
}

.device-item:last-child {
  border-bottom: none !important;
}

.device-form {
  padding: 10px 0 5px 15px;
  background-color: #fafafa;
  border-radius: 4px;
}

.device-form-col {
  padding: 5px 0;
}

.device-form-item {
  margin-bottom: 10px !important;
}

.device-input {
  width: 100% !important;
  max-width: 300px;
}

/* 空状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #8c8c8c;
  background-color: #fff;
  border-radius: 8px;
  border: 1px dashed #e8e8e8;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 14px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .sub-product-container {
    padding: 10px;
  }

  .device-input {
    max-width: 100%;
  }
}
</style>
