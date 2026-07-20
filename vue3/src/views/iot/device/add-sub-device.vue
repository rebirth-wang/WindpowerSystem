<template>
  <el-dialog :title="title" v-model="open" width="800px" append-to-body>
    <div class="sub-product-container">
      <!-- 子产品列表导航 -->
      <div v-if="gatewayList.length > 0" class="sub-product-header">
        <h3 class="list-title">
          <svg-icon class-name="title-icon" icon-class="product" />
          {{ $t('device-bind-sub-device.123456-0') }}
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
                {{ subProduct.enable ? $t('device-bind-sub-device.123456-1') : $t('device-bind-sub-device.123456-2') }}
              </span>
            </div>
          </template>

          <el-form ref="formRef" :model="product" label-width="100px" class="sub-product-form">
            <div class="form-section">
              <el-row>
                <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
                  <el-form-item :label="$t('device-bind-sub-device.123456-3')" class="form-item">
                    <el-switch
                      v-model="subProduct.enable"
                      @change="changeSwitch(subProduct)"
                      active-color="#486ff2"
                    ></el-switch>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12" v-if="subProduct.enable" class="device-count-col">
                  <el-form-item :label="$t('device-bind-sub-device.123456-4')" class="form-item">
                    <el-input
                      v-model="subProduct.deviceCount"
                      :min="1"
                      :max="20"
                      type="number"
                      @input="updateDeviceList(subProduct)"
                      @change="handleDeviceCountChange(subProduct)"
                      :placeholder="$t('device-bind-sub-device.123456-5')"
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
                <h4 class="devices-title">{{ $t('device-bind-sub-device.123456-6') }}</h4>
                <div class="devices-divider"></div>
              </div>

              <el-collapse class="devices-collapse" style="margin-top: 10px" accordion>
                <el-collapse-item
                  v-for="(device, deviceIndex) in subProduct.devices"
                  :key="deviceIndex"
                  :title="$t('device-bind-sub-device.123456-7') + (deviceIndex + 1)"
                  class="device-item"
                >
                  <div class="device-form">
                    <el-row>
                      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12" class="device-form-col">
                        <el-form-item :label="$t('device-bind-sub-device.123456-8')" class="device-form-item">
                          <el-input
                            v-model="device.name"
                            :placeholder="$t('device-bind-sub-device.123456-9')"
                            class="device-input"
                          ></el-input>
                        </el-form-item>
                      </el-col>
                      <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12" class="device-form-col">
                        <el-form-item :label="$t('device-bind-sub-device.123456-10')" class="device-form-item">
                          <el-input
                            v-model="device.address"
                            :placeholder="$t('device-bind-sub-device.123456-11')"
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
      <div class="dialog-btn">
        <el-button type="primary" @click="handleSave">{{ $t('confirm') }}</el-button>
        <el-button @click="open = false">{{ $t('product.product-modbus-task.894593-27') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance } from 'vue';
import { saveSubDeviceConfig, gatewayProductList } from '@/api/iot/gateway';

const { proxy } = getCurrentInstance() as any;

const emit = defineEmits(['addSuccess']);

const props = defineProps({
  gateway: {
    type: Object,
    default: null,
  },
});

const loading = ref(true);
const title = ref(proxy?.$t('device-bind-sub-device.123456-12'));
const open = ref(false);
const gatewayList = ref<any[]>([]);
const product = reactive({
  subProductName: '',
  enable: false,
  deviceCount: 0,
  devices: [] as any[],
});
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  gwProductId: 0,
});

function getList() {
  loading.value = true;
  queryParams.gwProductId = props.gateway.productId;
  gatewayProductList(queryParams).then((response: any) => {
    if (!response.rows || response.rows.length == 0) {
      proxy?.$alert(proxy?.$t('device-bind-sub-device.123456-13'), proxy?.$t('device-bind-sub-device.123456-14'), {
        confirmButtonText: proxy?.$t('device-bind-sub-device.123456-15'),
        callback: () => {
          open.value = false;
        },
      });
    } else {
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
  updateDeviceList(subProduct);
}

function handleDeviceCountChange(subProduct: any) {
  updateDeviceList(subProduct);
}

function updateDeviceList(subProduct: any) {
  if (subProduct.deviceCount < 0) {
    subProduct.deviceCount = 1;
  }
  if (subProduct.deviceCount > 20) {
    subProduct.deviceCount = 20;
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

function handleSave() {
  const subProductList = gatewayList.value.map((item: any) => {
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
  const params = {
    parentProductId: props.gateway.productId,
    parentClientId: props.gateway.serialNumber,
    subProductList: subProductList,
  };
  saveSubDeviceConfig(params).then((response: any) => {
    proxy?.$modal.msgSuccess(response.msg);
    open.value = false;
    emit('addSuccess');
  });
}

defineExpose({ open, getList });
</script>

<style scoped>
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
  min-height: 300px;
  margin-top: -20px;
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
  line-height: 30px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
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
