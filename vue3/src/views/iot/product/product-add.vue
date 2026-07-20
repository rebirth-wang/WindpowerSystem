<template>
  <el-dialog :title="$t('product.product-edit.473153-88')" v-model="open" width="900px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-row>
        <el-col :span="12">
          <el-form-item :label="$t('product.product-edit.473153-1')" prop="productName">
            <el-input
              v-model="form.productName"
              :placeholder="$t('product.product-edit.473153-2')"
              style="width: 300px"
            />
          </el-form-item>
          <el-form-item :label="$t('product.product-edit.473153-3')" prop="categoryId">
            <el-select
              v-model="form.categoryId"
              :placeholder="$t('product.product-edit.473153-4')"
              @change="selectCategory"
              filterable
              clearable
              style="width: 300px"
            >
              <el-option
                v-for="category in categoryShortList"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('product.product-edit.473153-78')" prop="deviceType">
            <el-select
              v-model="form.deviceType"
              :placeholder="$t('product.product-edit.473153-13')"
              filterable
              clearable
              @change="handleDeviceTypeChange"
              style="width: 300px"
            >
              <el-option
                v-for="dict in iot_device_type"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('product.product-edit.473153-91')" prop="firmwareType">
            <el-select
              v-model="form.firmwareType"
              :placeholder="$t('product.product-edit.473153-92')"
              filterable
              clearable
              style="width: 300px"
            >
              <el-option
                v-for="dict in iot_firmware_type"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('product.product-edit.473153-16')" prop="networkMethod">
            <el-select
              v-model="form.networkMethod"
              :placeholder="$t('product.product-edit.473153-17')"
              clearable
              style="width: 300px"
            >
              <el-option
                v-for="dict in networkOptions"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('product.product-edit.473153-18')">
            <template #label>
              <span style="display: inline-flex; align-items: center">
                <el-tooltip :content="$t('product.product-edit.473153-19')" placement="top">
                  <el-icon style="margin-right: 4px"><QuestionFilled /></el-icon>
                </el-tooltip>
                {{ $t('product.product-edit.473153-18') }}
              </span>
            </template>
            <el-radio-group v-model="form.isSys">
              <el-radio v-for="dict in iot_yes_no" :key="dict.value" :value="Number(dict.value)">
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item v-if="form.deviceType !== 3" :label="$t('product.product-edit.473153-81')" prop="protocolCode">
            <el-select
              v-model="form.protocolCode"
              :placeholder="$t('product.product-edit.473153-82')"
              @change="handleProductCodeChange"
              filterable
              clearable
              style="width: 300px"
            >
              <el-option
                v-for="p in protocolList"
                :key="p.protocolCode"
                :label="p.protocolName"
                :value="p.protocolCode"
              />
            </el-select>
          </el-form-item>
          <el-form-item v-if="form.deviceType !== 4" :label="$t('product.product-edit.473153-14')" prop="transport">
            <el-select
              v-model="form.transport"
              :placeholder="$t('product.product-edit.473153-15')"
              clearable
              style="width: 300px"
              :disabled="form.deviceType !== 3 && ['MODBUS-TCP', 'MODBUS-TCP-OVER-RTU'].includes(form.protocolCode)"
            >
              <el-option
                v-for="dict in iot_transport_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
                :disabled="isTransportDisabled(dict.value)"
              />
            </el-select>
          </el-form-item>
          <el-form-item
            :label="$t('product.product-edit.473153-21')"
            prop="vertificateMethod"
            v-if="form.transport === 'MQTT' || form.transport === 'COAP' || form.transport === 'HTTP'"
          >
            <el-select
              v-model="form.vertificateMethod"
              :placeholder="$t('product.product-edit.473153-22')"
              clearable
              style="width: 300px"
            >
              <el-option
                v-for="dict in iot_vertificate_method"
                :key="dict.value"
                :label="dict.label"
                :value="Number(dict.value)"
              />
            </el-select>
          </el-form-item>
          <el-form-item v-if="form.deviceType != 4" :label="$t('product.product-edit.473153-23')" prop="locationWay">
            <el-select
              v-model="form.locationWay"
              :placeholder="$t('product.product-edit.473153-24')"
              clearable
              style="width: 300px"
            >
              <el-option
                v-for="dict in iot_location_way"
                :key="dict.value"
                :label="dict.label"
                :value="Number(dict.value)"
              />
            </el-select>
          </el-form-item>
          <el-form-item
            v-if="['MQTT', 'HTTP', 'COAP'].includes(form.transport)"
            :label="$t('product.product-edit.473153-20')"
          >
            <el-switch v-model="form.isAuthorize" :active-value="1" :inactive-value="0" />
          </el-form-item>
          <el-row>
            <el-col :span="9">
              <el-form-item
                v-if="
                  form.deviceType === 2 &&
                  ['MODBUS-RTU', 'MODBUS-TCP-OVER-RTU', 'MODBUS-TCP', 'MODBUS-JSON-HP', 'JSON-GATEWAY'].includes(
                    form.protocolCode
                  )
                "
              >
                <template #label>
                  <span style="display: inline-flex; align-items: center">
                    <el-tooltip :content="$t('product.product-add.234678-19')" placement="top">
                      <el-icon style="margin-right: 4px"><QuestionFilled /></el-icon>
                    </el-tooltip>
                    {{ $t('product.product-add.234678-0') }}
                  </span>
                </template>
                <el-switch
                  v-model="form.isSubProduct"
                  :active-value="1"
                  :inactive-value="0"
                  @change="handleSubProductChange"
                />
              </el-form-item>
            </el-col>
            <el-col :span="15">
              <el-input
                v-if="form.deviceType === 2 && form.isSubProduct"
                v-model="form.subProductCount"
                type="number"
                style="width: 236px"
                @input="handleSubProductCountChange"
                :placeholder="$t('product.product-add.234678-18')"
              >
                <template #append>{{ $t('product.product-add.234678-1') }}</template>
              </el-input>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
      <!-- 子产品折叠面板区域 -->
      <div
        v-if="form.isSubProduct && subProductList.length > 0 && form.subProductCount > 0"
        class="sub-product-container"
      >
        <h3 class="list-title">
          <svg-icon class-name="product-icon" icon-class="product" />
          <span>{{ $t('product.product-add.234678-2') }}</span>
        </h3>
        <el-collapse v-model="activePanels" accordion class="custom-collapse">
          <el-collapse-item
            v-for="(product, index) in subProductList"
            :key="`subproduct-${index}`"
            :name="index"
            class="collapse-item"
          >
            <template #title>
              <div class="panel-title">
                <span class="subproduct-index">{{ $t('product.product-add.234678-3') }} {{ index + 1 }}</span>
              </div>
            </template>
            <div class="panel-content">
              <div class="section">
                <div class="section-header">
                  <svg-icon icon-class="system" class="section-icon" />
                  <span>{{ $t('product.product-add.234678-4') }}</span>
                </div>
                <el-row :gutter="20">
                  <el-col :xs="24" :sm="12" :md="12">
                    <el-form-item :label="$t('product.product-add.234678-5')" label-width="100px">
                      <el-input
                        v-model="product.subProductName"
                        :placeholder="$t('product.product-add.234678-6')"
                        clearable
                        class="form-input"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="12" :md="12">
                    <el-form-item :label="$t('product.product-add.234678-7')" label-width="100px">
                      <el-input
                        v-model="product.address"
                        :placeholder="$t('product.product-add.234678-8')"
                        clearable
                        class="form-input"
                        type="number"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </div>
              <div class="section">
                <div class="section-header">
                  <el-icon class="section-icon"><Setting /></el-icon>
                  <span>{{ $t('product.product-add.234678-9') }}</span>
                </div>
                <el-row :gutter="20">
                  <el-col :xs="24" :sm="12" :md="12">
                    <div class="upload-group">
                      <el-form-item :label="$t('product.product-add.234678-10')" label-width="120px">
                        <fileUpload
                          ref="uploadRef"
                          :value="form.filePath"
                          :limit="1"
                          :fileSize="5"
                          :uploadFileUrl="dynamicUploadUrl"
                          :fileType="['xlsx', 'xls']"
                          @input="getFilePath(index, $event, 'thingsModelFilePath')"
                        />
                        <el-button
                          style="margin-left: 10px; color: #409eff"
                          type="primary"
                          link
                          size="small"
                          @click.stop="downloadTemplate"
                        >
                          <el-icon style="color: #409eff"><Download /></el-icon>
                          {{ $t('product.product-add.234678-11') }}
                        </el-button>
                      </el-form-item>
                    </div>
                  </el-col>
                </el-row>
              </div>
              <div class="section">
                <div class="section-header">
                  <el-icon class="section-icon"><Cpu /></el-icon>
                  <span>{{ $t('product.product-add.234678-12') }}</span>
                </div>
                <el-row :gutter="20">
                  <el-col :xs="24" :sm="12" :md="12">
                    <div class="upload-group">
                      <el-form-item :label="$t('product.product-add.234678-13')" label-width="120px">
                        <fileUpload
                          ref="uploadRef"
                          :value="form.filePath"
                          :limit="1"
                          :fileSize="5"
                          :uploadFileUrl="dynamicUploadUrl"
                          :data="extraData"
                          :fileType="['xlsx', 'xls']"
                          @input="getFilePath(index, $event, 'ioRegisterFilePath')"
                        />
                        <el-button
                          style="margin-left: 10px; color: #409eff"
                          type="primary"
                          link
                          size="small"
                          @click.stop="downloadRegisterTemplate('isSelectIo')"
                        >
                          <el-icon style="color: #409eff"><Download /></el-icon>
                          {{ $t('product.product-add.234678-14') }}
                        </el-button>
                      </el-form-item>
                    </div>
                  </el-col>
                  <el-col :xs="24" :sm="12" :md="12">
                    <div class="upload-group">
                      <el-form-item :label="$t('product.product-add.234678-15')" label-width="120px">
                        <fileUpload
                          ref="uploadRef"
                          :value="form.filePath"
                          :limit="1"
                          :fileSize="5"
                          :uploadFileUrl="dynamicUploadUrl"
                          :fileType="['xlsx', 'xls']"
                          @input="getFilePath(index, $event, 'dataRegisterFilePath')"
                        />
                        <el-button
                          style="margin-left: 10px; color: #409eff"
                          type="primary"
                          link
                          size="small"
                          @click.stop="downloadRegisterTemplate('isSelectData')"
                        >
                          <el-icon style="color: #409eff"><Download /></el-icon>
                          {{ $t('product.product-add.234678-16') }}
                        </el-button>
                      </el-form-item>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="submitForm" type="primary">{{ $t('device.product-list.058448-14') }}</el-button>
        <el-button @click="closeDialog">{{ $t('device.product-list.058448-15') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, getCurrentInstance, onMounted } from 'vue';
import { QuestionFilled, Setting, Download, Cpu } from '@element-plus/icons-vue';
import { listProtocol } from '@/api/iot/protocol';
import { listShortCategory } from '@/api/iot/category';
import { addProduct } from '@/api/iot/product';
import { useDict } from '@/utils/dict/useDict';

const { proxy } = getCurrentInstance() as any;
const {
  iot_device_type,
  iot_network_method,
  iot_vertificate_method,
  iot_transport_type,
  iot_location_way,
  sub_gateway_type,
  iot_firmware_type,
  iot_yes_no,
} = useDict(
  'iot_device_type',
  'iot_network_method',
  'iot_vertificate_method',
  'iot_transport_type',
  'iot_location_way',
  'sub_gateway_type',
  'iot_firmware_type',
  'iot_yes_no'
);

const emit = defineEmits(['success']);
const VIDEO_TRANSPORT_TYPES = [
  'GB28181',
  'STREAM_PUSH',
  'STREAM_PROXY',
  'ONVIF',
  'JT_1078',
  'ISUP',
  'EZVIZ',
  'ICC_DAHUA',
];

const open = ref(false);
const formRef = ref<any>(null);
const protocolList = ref<any[]>([]);
const categoryShortList = ref<any[]>([]);
const activePanels = ref<any[]>([]);
const subProductList = ref<any[]>([]);
const extraData = { path: 'productImport' };

const form = reactive<any>({
  productId: null,
  productName: '',
  categoryId: null,
  categoryName: '',
  status: 1,
  tslJson: null,
  isAuthorize: 0,
  deviceType: 1,
  transport: 'MQTT',
  networkMethod: 1,
  vertificateMethod: 3,
  remark: null,
  imgUrl: '',
  locationWay: 1,
  isSys: 0,
  isSubProduct: 0,
  subProductCount: 0,
  firmwareType: 2,
  filePath: '',
  protocolCode: '',
});

const rules = reactive<any>({
  productName: [{ required: true, message: proxy.$t('product.product-edit.473153-58'), trigger: 'blur' }],
  categoryId: [{ required: true, message: proxy.$t('product.product-edit.473153-59'), trigger: 'blur' }],
  deviceType: [{ required: true, message: proxy.$t('product.product-edit.473153-13'), trigger: 'blur' }],
  firmwareType: [{ required: true, message: proxy.$t('product.product-edit.473153-92'), trigger: 'blur' }],
  protocolCode: [{ required: true, message: proxy.$t('product.product-edit.473153-60'), trigger: 'blur' }],
  transport: [{ required: true, message: proxy.$t('product.product-edit.473153-61'), trigger: 'blur' }],
  isSys: [{ required: true, message: proxy.$t('product.product-edit.473153-61'), trigger: 'blur' }],
});

const networkOptions = computed(() => (form.deviceType == 4 ? sub_gateway_type.value : iot_network_method.value));
const dynamicUploadUrl = computed(() => {
  const base = import.meta.env.VITE_APP_BASE_API + '/common/upload';
  return `${base}?path=productImport`;
});

function openDialog() {
  reset();
  open.value = true;
}

function getShortCategory() {
  listShortCategory({ pageSize: 999 }).then((response: any) => {
    categoryShortList.value = response.data;
  });
}

function getProtocol() {
  listProtocol({ protocolStatus: 1, pageSize: 99, display: 1 }).then((res: any) => {
    protocolList.value = res.rows;
  });
}

function getFilePath(index: number, data: any, fileType: string) {
  subProductList.value[index][fileType] = data;
}

function isVideoTransport(value: string) {
  return VIDEO_TRANSPORT_TYPES.includes(value);
}

function isTransportDisabled(value: string) {
  return (
    (form.deviceType === 3 && !isVideoTransport(value)) ||
    (form.deviceType !== 3 && form.protocolCode === 'MODBUS-TCP' && value !== 'TCP')
  );
}

function handleProductCodeChange(val: string) {
  form.isSubProduct = 0;
  if (form.deviceType === 3) {
    if (!isVideoTransport(form.transport)) {
      form.transport = 'GB28181';
    }
    return;
  }
  if (val == 'MODBUS-TCP' || val == 'MODBUS-TCP-OVER-RTU') {
    form.transport = 'TCP';
  } else {
    form.transport = 'MQTT';
  }
}

function handleDeviceTypeChange(val: number) {
  form.isSubProduct = 0;
  if (val === 3) {
    if (!isVideoTransport(form.transport)) {
      form.transport = 'GB28181';
    }
    form.locationWay = 3;
  } else if (val === 4) {
    form.transport = '';
  } else {
    form.transport = 'MQTT';
  }
}

function reset() {
  Object.assign(form, {
    productId: null,
    productName: '',
    categoryId: null,
    categoryName: '',
    status: 1,
    tslJson: null,
    isAuthorize: 0,
    deviceType: 1,
    transport: 'MQTT',
    networkMethod: 1,
    vertificateMethod: 3,
    remark: null,
    imgUrl: '',
    locationWay: 1,
    isSys: 0,
    isSubProduct: 0,
    subProductCount: 0,
    firmwareType: 2,
    protocolCode: '',
  });
  subProductList.value = [];
  proxy.resetForm(formRef.value);
}

function selectCategory(val: any) {
  for (const cat of categoryShortList.value) {
    if (cat.id == val) {
      form.categoryName = cat.name;
      return;
    }
  }
}

function submitForm() {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      const params = { ...form, subProductList: subProductList.value };
      addProduct(params).then((res: any) => {
        if (res.code === 200) {
          proxy.$modal.msgSuccess(proxy.$t('product.product-edit.473153-64'));
          emit('success');
        }
        open.value = false;
      });
    }
  });
}

function closeDialog() {
  open.value = false;
}

function handleSubProductCountChange(newCount: any) {
  newCount = Math.max(0, Math.min(10, newCount));
  if (newCount > subProductList.value.length) {
    const addCount = newCount - subProductList.value.length;
    for (let i = 0; i < addCount; i++) {
      subProductList.value.push({
        subProductName: proxy.$t('product.product-add.234678-17') + `${subProductList.value.length + 1}`,
        address: subProductList.value.length + 1,
        thingsModelFilePath: '',
        modbusJobFilePath: '',
        ioRegisterFilePath: '',
        dataRegisterFilePath: '',
      });
    }
  } else {
    subProductList.value.splice(newCount);
  }
}

function handleSubProductChange(val: any) {
  form.subProductCount = 0;
  if (!val) subProductList.value = [];
}

function downloadTemplate() {
  proxy.download('/iot/model/temp', {}, `${new Date().getTime()}.xlsx`);
}

function downloadRegisterTemplate(justiceSelect: string) {
  const type = justiceSelect == 'isSelectData' ? 2 : 1;
  const name =
    justiceSelect == 'isSelectData'
      ? proxy.$t('product.components.batchImportModbus.745343-1')
      : proxy.$t('product.components.batchImportModbus.745343-0');
  proxy.download('/modbus/config/modbusTemplate?type=' + type, {}, `${name}_${new Date().getTime()}.xlsx`);
}

defineExpose({ open, openDialog, reset });

onMounted(() => {
  getProtocol();
  getShortCategory();
});
</script>

<style lang="scss" scoped>
.sub-product-container {
  margin-top: 25px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
  background: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}
.list-title {
  display: flex;
  align-items: center;
  color: #486ff2;
  font-size: 18px;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}
.list-title .product-icon {
  font-size: 22px;
  margin-right: 10px;
}
.custom-collapse {
  border: none;
  border-radius: 6px;
}
.collapse-item {
  margin-bottom: 12px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}
.panel-title {
  font-weight: 600;
  display: flex;
  align-items: center;
  padding: 0 10px;
}
.subproduct-index {
  margin-right: 15px;
}
.panel-content {
  padding: 20px;
  background: #fafafa;
  border-top: 1px solid #ebeef5;
}
.section {
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #e6e9ed;
}
.section:last-child {
  margin-bottom: 0;
  border-bottom: none;
}
.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 18px;
  font-weight: 600;
  color: #444;
}
.section-icon {
  font-size: 16px;
  margin-right: 8px;
  color: #486ff2;
}
.upload-group {
  display: flex;
  flex-direction: row;
}
.form-input {
  width: 100%;
  max-width: 300px;
}
</style>
