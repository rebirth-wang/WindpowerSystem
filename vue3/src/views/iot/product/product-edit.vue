<template>
  <div class="product-edit">
    <el-card class="top-card" :body-style="{ padding: '26px 20px' }">
      <div class="title-wrap">
        <el-button class="top-button" type="info" size="small" @click="goBack()">
          <el-icon><ArrowLeft /></el-icon>
          {{ $t('product.product-edit.473153-44') }}
        </el-button>
        <span class="info-item">{{ $t('product.product-edit.473153-1') }}：{{ form.productName }}</span>
        <span class="info-item">
          {{ $t('product.product-edit.473153-85') }}：
          <el-button
            :disabled="form.isOwner === 0"
            v-if="form.status == 1"
            @click="changeProductStatusFn(1)"
            type="primary"
            size="small"
            plain
            style="height: 24px; padding: 0 10px"
          >
            {{ $t('product.product-edit.473153-86') }}
          </el-button>
          <el-button
            :disabled="form.isOwner === 0"
            v-if="form.status == 2"
            @click="changeProductStatusFn(2)"
            type="danger"
            size="small"
            plain
            style="height: 24px; padding: 0 10px"
          >
            {{ $t('product.product-edit.473153-87') }}
          </el-button>
        </span>
      </div>
    </el-card>
    <el-card style="padding-bottom: 100px">
      <el-tabs
        class="custom-tabs"
        v-model="activeName"
        tab-position="top"
        style="min-height: 400px"
        @tab-click="tabChange"
      >
        <el-tab-pane name="basic" :label="$t('product.product-edit.473153-0')">
          <el-form class="basic-span" ref="formRef" :model="form" :rules="rules" label-width="100px">
            <el-row :gutter="100">
              <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="8">
                <el-form-item :label="$t('product.product-edit.473153-1')" prop="productName">
                  <el-input
                    v-model="form.productName"
                    :placeholder="$t('product.product-edit.473153-2')"
                    :readonly="form.status == 2 || form.isOwner == 0"
                  />
                </el-form-item>
                <el-form-item :label="$t('product.product-edit.473153-3')" prop="categoryId">
                  <el-select
                    v-model="form.categoryId"
                    :placeholder="$t('product.product-edit.473153-4')"
                    @change="selectCategory"
                    style="width: 100%"
                    :disabled="form.status == 2 || form.isOwner == 0"
                    filterable
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
                    style="width: 100%"
                    v-model="form.deviceType"
                    :placeholder="$t('product.product-edit.473153-13')"
                    :disabled="form.status == 2 || form.isOwner == 0 || !!form.productId"
                    filterable
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
                    style="width: 100%"
                    v-model="form.firmwareType"
                    :placeholder="$t('product.product-edit.473153-92')"
                    :disabled="form.status == 2 || form.isOwner == 0"
                    filterable
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
                    style="width: 100%"
                    :disabled="form.status == 2 || form.isOwner == 0"
                  >
                    <el-option
                      v-for="dict in networkOptions"
                      :key="dict.value"
                      :label="dict.label"
                      :value="parseInt(dict.value)"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item :label="$t('product.product-edit.473153-25')" prop="productId">
                  <el-input v-model="form.productId" :placeholder="$t('product.product-edit.473153-26')" readonly />
                </el-form-item>
                <el-form-item :label="$t('product.product-edit.473153-18')">
                  <template #label>
                    <span>
                      {{ $t('product.product-edit.473153-18') }}
                      <el-tooltip style="cursor: pointer; margin-left: 5px" effect="light" placement="bottom">
                        <template #content>
                          {{ $t('product.product-edit.473153-19') }}
                          <br />
                        </template>
                        <el-icon><QuestionFilled /></el-icon>
                      </el-tooltip>
                    </span>
                  </template>
                  <el-radio-group v-model="form.isSys" :disabled="form.status == 2 || form.isOwner == 0">
                    <el-radio v-for="dict in iot_yes_no" :key="dict.value" :value="Number(dict.value)">
                      {{ dict.label }}
                    </el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item :label="$t('product.product-edit.473153-32')" prop="remark">
                  <el-input
                    v-model="form.remark"
                    type="textarea"
                    :autosize="{ minRows: 3, maxRows: 5 }"
                    :placeholder="$t('product.product-edit.473153-33')"
                    :disabled="form.status == 2 || form.isOwner == 0"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="8">
                <el-form-item
                  v-if="form.deviceType !== 3"
                  :label="$t('product.product-edit.473153-81')"
                  prop="protocolCode"
                >
                  <el-select
                    v-model="form.protocolCode"
                    :placeholder="$t('product.product-edit.473153-82')"
                    style="width: 100%"
                    :disabled="form.status == 2 || form.isOwner == 0 || !!form.productId"
                    @change="handleProductCodeChange"
                    filterable
                  >
                    <el-option
                      v-for="p in protocolList"
                      :key="p.protocolCode"
                      :label="p.protocolName"
                      :value="p.protocolCode"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item
                  v-if="form.deviceType !== 4"
                  :label="$t('product.product-edit.473153-14')"
                  prop="transport"
                >
                  <el-select
                    v-model="form.transport"
                    :placeholder="$t('product.product-edit.473153-15')"
                    style="width: 100%"
                    :disabled="form.status === 2 || form.isOwner === 0"
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
                    style="width: 100%"
                  >
                    <el-option
                      v-for="dict in iot_vertificate_method"
                      :key="dict.value"
                      :label="dict.label"
                      :value="Number(dict.value)"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item
                  v-if="form.deviceType !== 4"
                  :label="$t('product.product-edit.473153-23')"
                  prop="locationWay"
                >
                  <el-select
                    style="width: 100%"
                    v-model="form.locationWay"
                    :placeholder="$t('product.product-edit.473153-24')"
                    clearable
                    :disabled="form.status == 2 || form.isOwner == 0"
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
                  :label="$t('product.product-edit.473153-93')"
                  prop="account"
                >
                  <el-input
                    style="width: 100%"
                    v-model="form.account"
                    :placeholder="$t('product.product-edit.473153-28')"
                    :type="accountInputType"
                    :readonly="form.status === 2 || form.isOwner == 0"
                  >
                    <template #append>
                      <el-button :icon="View" style="font-size: 18px" @click="changeInputType('account')" />
                    </template>
                  </el-input>
                </el-form-item>
                <el-form-item
                  v-if="['MQTT', 'HTTP', 'COAP'].includes(form.transport)"
                  :label="$t('product.product-edit.473153-94')"
                  prop="authPassword"
                >
                  <el-input
                    style="width: 100%"
                    v-model="form.authPassword"
                    :placeholder="$t('product.product-edit.473153-30')"
                    :type="passwordInputType"
                    :readonly="form.status === 2 || form.isOwner == 0"
                  >
                    <template #append>
                      <el-button :icon="View" style="font-size: 18px" @click="changeInputType('password')" />
                    </template>
                  </el-input>
                </el-form-item>
                <el-form-item
                  v-if="['MQTT', 'HTTP', 'COAP'].includes(form.transport) && form.vertificateMethod !== 1"
                  :label="$t('product.product-edit.473153-95')"
                  prop="secret"
                >
                  <el-input
                    style="width: 100%"
                    v-model="form.secret"
                    :placeholder="$t('product.product-edit.473153-26')"
                    :type="keyInputType"
                    readonly
                  >
                    <template #append>
                      <el-button :icon="View" style="font-size: 18px" @click="changeInputType('key')" />
                    </template>
                  </el-input>
                </el-form-item>
                <el-form-item
                  v-if="['MQTT', 'HTTP', 'COAP'].includes(form.transport)"
                  :label="$t('product.product-edit.473153-20')"
                >
                  <el-switch
                    v-model="form.isAuthorize"
                    @change="changeIsAuthorize(form.isAuthorize)"
                    :active-value="1"
                    :inactive-value="0"
                    :disabled="form.status == 2 || form.isOwner == 0"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="8">
                <el-form-item :label="$t('product.product-edit.473153-34')">
                  <div v-if="form.status === 2 && !form.imgUrl && form.isOwner != 0">
                    <el-image
                      style="height: 145px; border-radius: 10px"
                      :preview-src-list="[gatewayImg]"
                      :src="gatewayImg"
                      fit="cover"
                      v-if="form.deviceType == 2"
                    />
                    <el-image
                      style="height: 145px; border-radius: 10px"
                      :preview-src-list="[videoImg]"
                      :src="videoImg"
                      fit="cover"
                      v-else-if="form.deviceType == 3"
                    />
                    <el-image
                      style="height: 145px; border-radius: 10px"
                      :preview-src-list="[productImgSrc]"
                      :src="productImgSrc"
                      fit="cover"
                      v-else
                    />
                  </div>
                  <div v-else>
                    <imageUpload
                      ref="imageUploadRef"
                      :disabled="form.status === 2"
                      :value="form.imgUrl"
                      :limit="1"
                      :fileSize="1"
                      @input="getImagePath($event)"
                    />
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
            <el-col :span="20">
              <el-form-item style="text-align: center; margin: 40px 0px">
                <el-button
                  v-show="!!form.productId && form.status != 2 && form.isOwner != 0"
                  type="primary"
                  @click="handleSubmitForm"
                  v-hasPermi="['iot:product:edit']"
                >
                  {{ $t('update') }}
                </el-button>
                <el-button
                  v-show="!form.productId && form.status != 2"
                  type="primary"
                  @click="handleSubmitForm"
                  v-hasPermi="['iot:product:add']"
                >
                  {{ $t('add') }}
                </el-button>
              </el-form-item>
            </el-col>
          </el-form>
        </el-tab-pane>
        <el-tab-pane name="things" :label="$t('product.product-edit.473153-38')">
          <product-things-model ref="productThingsModelRef" :product="form" @updateModel="updateModel" />
        </el-tab-pane>
        <el-tab-pane
          name="productSub"
          v-if="
            form.deviceType === 2 &&
            [
              'MODBUS-RTU',
              'MODBUS-TCP-OVER-RTU',
              'MODBUS-JSON-HP',
              'MODBUS-JSON-ZQWL',
              'MODBUS-TCP',
              'JSON-GATEWAY',
            ].includes(form.protocolCode)
          "
          lazy
          :label="$t('product.product-edit.473153-83')"
        >
          <product-sub ref="productSubRef" :product="form" />
        </el-tab-pane>
        <el-tab-pane
          name="productModbus"
          v-if="
            form.deviceType !== 3 &&
            ['MODBUS-RTU', 'MODBUS-TCP', 'MODBUS-JSON-HP', 'MODBUS-JSON-ZQWL', 'MODBUS-TCP-OVER-RTU'].includes(
              form.protocolCode
            )
          "
          :label="$t('product.product-edit.473153-80')"
        >
          <product-modbus ref="productModbusRef" :product="form" @sendPollType="sendPollType" />
        </el-tab-pane>
        <el-tab-pane
          name="productModbusTask"
          v-if="
            (form.deviceType === 1 || form.deviceType === 2) &&
            polltype === 0 &&
            ['MODBUS-RTU', 'MODBUS-TCP-OVER-RTU', 'MODBUS-TCP'].includes(form.protocolCode)
          "
          lazy
          :label="$t('product.product-edit.473153-84')"
        >
          <product-modbus-task ref="productModbusTaskRef" :product="form" @getSendData="getData($event)" />
        </el-tab-pane>
        <el-tab-pane
          name="productInstructionsConfig"
          v-if="(form.deviceType === 1 || form.deviceType === 2) && form.protocolCode == 'MODBUS-TCP'"
          lazy
          :label="$t('device.device-edit.148398-106')"
        >
          <product-instruction-config ref="productInstructionsConfigRef" :product="form" />
        </el-tab-pane>
        <el-tab-pane
          name="productAuthorize"
          v-if="
            ((form.deviceType !== 3 && form.transport === 'MQTT') ||
              form.transport === 'COAP' ||
              form.transport === 'HTTP') &&
            isAuthorizeQuery &&
            form.isAuthorize === 1
          "
          :label="$t('product.product-edit.473153-40')"
        >
          <product-authorize ref="productAuthorizeRef" :product="form" />
        </el-tab-pane>
        <el-tab-pane name="sipConfig" v-if="form.deviceType === 3" :label="$t('product.product-edit.473153-41')">
          <config-sip ref="configSipRef" :product="form" />
        </el-tab-pane>
        <el-tab-pane
          name="scada"
          v-if="form.deviceType !== 3 && isShowScada"
          lazy
          :label="$t('device.device-edit.148398-73')"
        >
          <product-scada ref="productScadaRef" :product="form" />
        </el-tab-pane>
        <el-tab-pane
          name="extensionParam"
          :disabled="!form.productId"
          lazy
          :label="$t('product.product-edit.473153-96')"
        >
          <product-extension-param ref="productExtensionParamRef" :product="form" />
        </el-tab-pane>
        <el-tab-pane
          name="applicationConfiguration"
          v-if="form.deviceType !== 3"
          lazy
          :label="$t('product.drag.854578-0')"
        >
          <application-configuration ref="applicationConfigurationRef" :product="form" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, getCurrentInstance, onActivated, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ArrowLeft, QuestionFilled, View } from '@element-plus/icons-vue';
import defaultSettings from '@/settings';
import imageUpload from '@/components/ImageUpload/index.vue';
import productThingsModel from './product-things-model.vue';
import productAuthorize from './product-authorize.vue';
import applicationConfiguration from './application-configuration.vue';
import configSip from '../sip/sipconfig.vue';
import productScada from './product-scada.vue';
import productModbus from './product-modbus.vue';
import productSub from './product-sub.vue';
import productInstructionConfig from './product-instruction-config.vue';
import productModbusTask from './product-modbus-task.vue';
import productExtensionParam from './product-extension-param.vue';
import { listProtocol } from '@/api/iot/protocol';
import { listShortCategory } from '@/api/iot/category';
import { getProduct, addProduct, updateProduct, changeProductStatus, deviceCount } from '@/api/iot/product';
import { checkPermi } from '@/utils/permission';
import { mergeObjects } from '@/utils/index';
import { useDict } from '@/utils/dict/useDict';

import gatewayImg from '@/assets/images/gateway.png';
import videoImg from '@/assets/images/video.png';
import productImgSrc from '@/assets/images/product.png';

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
const router = useRouter();
const route = useRoute();
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

const keyInputType = ref('password');
const accountInputType = ref('password');
const passwordInputType = ref('password');
const activeName = ref('basic');
const categoryShortList = ref<any[]>([]);
const protocolList = ref<any[]>([]);
const isShowScada = defaultSettings.isShowScada;
const formRef = ref<any>(null);
const productThingsModelRef = ref<any>(null);

const formParams = {
  productName: '',
  categoryId: null,
  categoryName: '',
  deviceType: 1,
  firmwareType: 2,
  networkMethod: 1,
  isSys: 0,
  remark: '',
  protocolCode: '',
  transport: 'MQTT',
  vertificateMethod: 3,
  locationWay: 1,
  account: '',
  authPassword: '',
  secret: null,
  isAuthorize: 0,
  imgUrl: '',
  status: 0,
};
const form = reactive<any>({});
const isEditing = ref(false);
const polltype = ref<any>('');
const isAuthorizeQuery = ref(false);
let uniqueId: any = null;

const rules = reactive<any>({
  productName: [{ required: true, message: proxy.$t('product.product-edit.473153-58'), trigger: 'blur' }],
  categoryId: [{ required: true, message: proxy.$t('product.product-edit.473153-59'), trigger: 'blur' }],
  deviceType: [{ required: true, message: proxy.$t('product.product-edit.473153-13'), trigger: 'change' }],
  firmwareType: [{ required: true, message: proxy.$t('product.product-edit.473153-92'), trigger: 'change' }],
  protocolCode: [{ required: true, message: proxy.$t('product.product-edit.473153-60'), trigger: 'change' }],
  transport: [{ required: true, message: proxy.$t('product.product-edit.473153-61'), trigger: 'change' }],
});

const networkOptions = computed(() => (form.deviceType == 4 ? sub_gateway_type.value : iot_network_method.value));

function getShortCategory() {
  listShortCategory({ pageSize: 999 }).then((response: any) => {
    categoryShortList.value = response.data;
  });
}
function sendPollType(value: any) {
  polltype.value = value;
}
function getData(data: any) {
  activeName.value = data;
}
function goBack() {
  router.go(-1);
}

function getProductData() {
  getProduct(form.productId).then((res: any) => {
    if (res.code === 200) Object.assign(form, res.data);
  });
}

function handleSubmitForm() {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      const { productId, ...fo } = form;
      let data = mergeObjects(formParams, fo);
      if (productId === undefined || productId === null || productId === '') {
        addProduct(data).then((res: any) => {
          if (res.code === 200) {
            Object.assign(form, res.data);
            proxy.$modal.alertSuccess(proxy.$t('product.product-edit.473153-64'));
          }
        });
      } else {
        const { tenantId, createBy } = form;
        data = { ...data, productId, tenantId, createBy };
        updateProduct(data).then((res: any) => {
          if (res.code === 200) proxy.$modal.alertSuccess(proxy.$t('product.product-edit.473153-62'));
        });
      }
    }
  });
}

async function changeProductStatusFn(status: number) {
  let message = proxy.$t('product.product-edit.473153-66');
  if (status == 1) {
    message = proxy.$t('product.product-edit.473153-67');
  } else if (status == 2) {
    let hasPermission = checkPermi(['iot:product:edit']);
    if (!hasPermission) {
      proxy.$modal.alertError(proxy.$t('product.index.091251-31'));
      return;
    }
    let result: any = await deviceCount(form.productId);
    if (result.data > 0) message = proxy.$t('product.product-edit.473153-68', [result.data]);
  }
  proxy.$modal
    .confirm(message)
    .then(() => {
      const params = { productId: form.productId, status: status === 1 ? 2 : 1, deviceType: form.deviceType };
      changeProductStatus(params).then((res: any) => {
        if (res.code === 200) {
          proxy.$modal.alertSuccess(res.msg);
          getProductData();
        }
      });
    })
    .catch(() => {
      activeName.value = 'basic';
    });
}

function selectCategory(val: any) {
  for (const cat of categoryShortList.value) {
    if (cat.id == val) {
      form.categoryName = cat.name;
      return;
    }
  }
}
function getImagePath(data: any) {
  form.imgUrl = data;
}
function changeInputType(name: string) {
  if (name == 'key') keyInputType.value = keyInputType.value == 'password' ? 'text' : 'password';
  else if (name == 'account') accountInputType.value = accountInputType.value == 'password' ? 'text' : 'password';
  else if (name == 'password') passwordInputType.value = passwordInputType.value == 'password' ? 'text' : 'password';
}
function changeIsAuthorize(val: any) {
  form.isAuthorize = val === 0 ? 1 : 0;
  let text = val === 1 ? proxy.$t('product.product-edit.473153-72') : proxy.$t('product.product-edit.473153-74');
  proxy.$modal.confirm(proxy.$t('product.product-edit.473153-75', [text])).then(() => {
    if (!!form.productId) {
      val === 0 ? (form.isAuthorize = 0) : (form.isAuthorize = 1);
      updateProduct(form).then((res: any) => {
        if (res.code === 200) proxy.$modal.msgSuccess(proxy.$t('product.product-edit.473153-77') + text);
      });
    }
  });
}
function getProtocol() {
  listProtocol({ protocolStatus: 1, pageSize: 99, display: 1 }).then((res: any) => {
    protocolList.value = res.rows;
  });
}
function isVideoTransport(value: string) {
  return VIDEO_TRANSPORT_TYPES.includes(value);
}
function isTransportDisabled(value: string) {
  return (
    (form.deviceType === 3 && !isVideoTransport(value)) ||
    (form.deviceType !== 3 && form.protocolCode === 'MODBUS-TCP-OVER-RTU' && value !== 'TCP')
  );
}
function handleProductCodeChange(val: string) {
  if (val == 'MODBUS-TCP-OVER-RTU') form.transport = 'TCP';
}
function tabChange(tabItem: any) {
  if (tabItem.paneName == 'alert') {
    /* reserved */
  }
}
function updateModel() {
  productThingsModelRef.value?.getList();
}

// Init
const productId = route.query?.productId;
form.productId = productId;
if (!!form.productId) {
  getProductData();
} else {
  form.locationWay = 3;
  isEditing.value = true;
}
const tabPanelName = route.query?.tabPanelName as string;
if (tabPanelName) activeName.value = tabPanelName;
getShortCategory();
if (!form.productId || form.productId == 0) {
  accountInputType.value = 'text';
  passwordInputType.value = 'text';
}
getProtocol();
isAuthorizeQuery.value = checkPermi(['iot:authorize:query']);

onActivated(() => {
  const time = route.query.t;
  if (time != null && time != uniqueId) {
    uniqueId = time;
  }
  let pid = route.query.productId;
  if (pid) {
    form.productId = Number(pid);
    getProductData();
    getShortCategory();
  }
  const tab = route.query.tabPanelName as string;
  if (tab) activeName.value = tab;
});
</script>

<style lang="scss" scoped>
.product-edit {
  padding: 20px;
  .top-card {
    margin-bottom: 10px;
    .title-wrap {
      display: flex;
      flex-direction: row;
      align-items: center;
      .top-button {
        height: 22px;
        color: #909399;
        background: #f4f5f7;
        padding: 0px 8px;
        border: none;
      }
      .info-item {
        font-weight: normal;
        font-size: 14px;
        color: #333333;
        line-height: 20px;
        margin-left: 36px;
      }
    }
  }
  .custom-tabs {
    .basic-span {
      margin-top: 16px;
    }
    :deep(.el-card__body) {
      padding: 0 20px;
    }
    :deep(.el-tabs__active-bar) {
      background-color: transparent;
    }
    :deep(.el-tabs__nav) {
      margin-bottom: 12px;
    }
    :deep(.el-tabs__item) {
      padding: 0px 25px !important;
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }
    :deep(.el-tabs__item.is-active) {
      color: #fff;
      background-color: #486ff2;
      border-radius: 4px;
      height: 32px;
      line-height: 34px;
    }
  }
}
</style>
