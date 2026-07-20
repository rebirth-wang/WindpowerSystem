<template>
  <div class="modbus-wrap">
    <div class="card-wrap">
      <div class="title-wrap">
        <div class="title">
          <div>{{ $t('product.product-modbus.562372-0') }}</div>
          <el-tooltip effect="dark" :content="$t('product.product-modbus.562372-1')" placement="top">
            <el-icon style="margin-left: 6px"><QuestionFilled /></el-icon>
          </el-tooltip>
        </div>
        <div class="btn-group">
          <el-button
            type="primary"
            plain
            :icon="Edit"
            @click="setSlave"
            v-if="!enableSetSlave && productInfo.status == 1"
          >
            {{ $t('product.product-modbus.562372-2') }}
          </el-button>
          <el-button
            type="primary"
            plain
            :icon="Close"
            @click="saveSlave"
            v-if="enableSetSlave"
            v-hasPermi="['modbus:config:edit']"
          >
            {{ $t('product.product-modbus.562372-3') }}
          </el-button>
          <el-button type="info" plain :icon="Edit" @click="cancelSlave" v-if="enableSetSlave">
            {{ $t('product.product-modbus.562372-4') }}
          </el-button>
        </div>
      </div>
      <el-form class="form-wrap" ref="formRef" :model="form" label-width="180px" :rules="rules">
        <el-row>
          <el-col :span="10">
            <el-form-item label="">
              <el-tooltip placement="top">
                <template #content>
                  <div class="tips_div">
                    {{ $t('product.product-modbus.562372-6') }}
                    <br />
                    {{ $t('product.product-modbus.562372-7') }}
                    <br />
                    {{ $t('product.product-modbus.562372-8') }}
                  </div>
                </template>
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
              <span style="margin: 0 15px 0 6px">{{ $t('product.product-modbus.562372-5') }}</span>
              <el-radio-group :disabled="!enableSetSlave || product.deviceType === 4" v-model="form.statusDeter">
                <el-radio v-for="dict in device_status_deter" :key="dict.value" :value="Number(dict.value)">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="14">
            <el-form-item v-if="form.statusDeter == '1'">
              <span>
                <el-tooltip placement="top">
                  <template #content>
                    <div class="tips_div">
                      {{ $t('product.product-modbus.562372-10') }},{{ $t('product.product-modbus.562372-12') }}
                    </div>
                  </template>
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
              </span>
              <span style="margin: 0 15px 0 6px">{{ $t('product.product-modbus.562372-9') }}</span>
              <el-select
                :disabled="!enableSetSlave"
                v-model="form.deterTimer"
                :placeholder="$t('product.product-modbus.562372-11')"
                style="width: 230px"
              >
                <el-option
                  v-for="dict in iot_modbus_poll_time"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="10" v-if="product.deviceType !== 4">
            <el-form-item label="">
              <span style="margin: 0 15px 0 6px">{{ $t('product.product-modbus.562372-13') }}</span>
              <el-radio-group :disabled="!enableSetSlave" v-model="form.pollType" @change="changePollType">
                <el-radio v-for="dict in data_collect_type" :key="dict.value" :value="Number(dict.value)">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="14" v-else>
            <el-form-item label="" prop="address">
              <span>
                <el-tooltip placement="top">
                  <template #content>
                    <div class="tips_div">{{ $t('product.product-modbus.562372-16') }}</div>
                  </template>
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
              </span>
              <span style="margin: 0 15px 0 6px">{{ $t('product.product-modbus.562372-14') }}</span>
              <el-input
                :disabled="!enableSetSlave"
                v-model="form.address"
                :label="$t('product.product-modbus.562372-15')"
                style="width: 230px"
                type="number"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <el-divider></el-divider>
    <!-- IO寄存器 -->
    <div class="card-wrap">
      <div class="title-wrap">
        <div class="title">
          <div>{{ $t('product.product-modbus.562372-17') }}</div>
          <el-tooltip effect="dark" :content="$t('product.product-modbus.562372-18')" placement="top">
            <el-icon style="margin-left: 6px"><QuestionFilled /></el-icon>
          </el-tooltip>
        </div>
        <div class="btn-group">
          <el-button
            type="primary"
            plain
            :icon="Edit"
            @click="editIOModbus"
            v-if="!enableEditIO && productInfo.status == 1"
          >
            {{ $t('product.product-modbus.562372-19') }}
          </el-button>
          <el-button
            type="primary"
            plain
            :icon="Check"
            @click="submitFormIO"
            v-if="enableEditIO"
            v-hasPermi="['modbus:config:edit']"
          >
            {{ $t('product.product-modbus.562372-22') }}
          </el-button>
          <el-button type="info" plain :icon="Close" @click="handleCancelIO" v-if="enableEditIO">
            {{ $t('product.product-modbus.562372-23') }}
          </el-button>
          <el-button
            plain
            :icon="Upload"
            v-if="productInfo.status == 1"
            @click.stop="batchImport('isSelectIo')"
            v-hasPermi="['modbus:config:import']"
          >
            {{ $t('product.product-modbus.562372-20') }}
          </el-button>
          <el-button
            plain
            :icon="Download"
            v-if="productInfo.status == 1"
            @click.stop="exportModbus('isSelectIo')"
            v-hasPermi="['modbus:config:export']"
          >
            {{ $t('product.product-modbus.562372-21') }}
          </el-button>
        </div>
      </div>
      <el-table
        class="table-wrap"
        v-loading="loadingIO"
        :data="configList"
        :key="configTableKey"
        data-key="id"
        ref="IOTableRef"
        :border="false"
      >
        <el-table-column
          :label="$t('product.product-modbus.562372-24')"
          align="center"
          prop="sort"
          width="60"
        ></el-table-column>
        <el-table-column :label="$t('product.product-modbus.562372-25')" align="center" prop="identifier" width="280px">
          <template #default="scope">
            <el-select
              style="width: 100%"
              filterable
              :disabled="!enableEditIO"
              v-model="scope.row.identifier"
              :placeholder="$t('product.product-modbus.562372-26')"
              :ref="
                (el: any) => {
                  selectIoRefs[scope.$index] = el;
                }
              "
              @change="
                (e: any) =>
                  updateSelectThingsModel({
                    newVal: e,
                    oldVal: selectIoRefs[scope.$index]?.value,
                    justiceSelect: 'isSelectIo',
                  })
              "
            >
              <el-option key="0" label="" value="" disabled style="width: 300px">
                <span style="float: left">{{ $t('product.product-modbus.562372-27') }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">
                  {{ $t('product.product-modbus.562372-28') }}
                </span>
              </el-option>
              <el-option
                v-for="item in thingsModelList"
                :key="item.identifier"
                :label="`${item.modelName} (${item.identifier})`"
                :value="item.identifier"
                style="width: 300px"
                :disabled="!item.isSelectIo"
              >
                <span style="float: left">{{ item.modelName }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ item.identifier }}</span>
              </el-option>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('product.product-modbus.562372-29')"
          align="center"
          prop="address"
          width="200px"
          v-if="product.deviceType === 1"
        >
          <template #default="scope">
            <el-input-number
              style="width: 100%"
              :disabled="!enableEditIO"
              v-model="scope.row.address"
              :min="0"
              :max="400000"
            ></el-input-number>
          </template>
        </el-table-column>
        <el-table-column :label="$t('product.product-modbus.562372-30')" align="center" prop="register" width="200px">
          <template #default="scope">
            <el-input-number
              style="width: 100%"
              :disabled="!enableEditIO"
              v-model="scope.row.register"
              :min="0"
              :max="400000"
            ></el-input-number>
          </template>
        </el-table-column>
        <el-table-column :label="$t('product.product-modbus.562372-31')" align="center" prop="isReadonly" width="260px">
          <template #default="scope">
            <el-radio-group :disabled="!enableEditIO" v-model="scope.row.isReadonly">
              <el-radio-button :value="1">{{ $t('product.product-modbus.562372-32') }}</el-radio-button>
              <el-radio-button :value="0">{{ $t('product.product-modbus.562372-33') }}</el-radio-button>
            </el-radio-group>
          </template>
        </el-table-column>
        <el-table-column :label="$t('product.product-modbus.562372-311')" align="center" prop="bitOrder" width="200px">
          <template #default="scope">
            <el-input-number
              style="width: 100%"
              :disabled="!enableEditIO"
              v-model="scope.row.bitOrder"
              :min="0"
              :max="15"
            ></el-input-number>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('product.product-modbus.562372-34')"
          align="center"
          class-name="small-padding fixed-width"
          fixed="right"
        >
          <template #default="scope">
            <el-button
              type="primary"
              link
              :icon="Delete"
              @click="handleDelete(scope.row, scope.$index, configList, 'isSelectIo')"
              v-hasPermi="['modbus:config:remove']"
            >
              {{ $t('product.product-modbus.562372-35') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParamsIO.pageNum"
        v-model:limit="queryParamsIO.pageSize"
        @pagination="getIOList"
      />
      <el-row :gutter="10" class="mb8" style="margin-top: 10px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAddIO" v-if="enableEditIO">
            {{ $t('product.product-modbus.562372-36') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAddBatch('isSelectIo')" v-if="enableEditIO">
            {{ $t('product.product-modbus.562372-37') }}
          </el-button>
        </el-col>
      </el-row>
    </div>
    <el-divider></el-divider>
    <!-- 数据寄存器 -->
    <div class="card-wrap">
      <div class="title-wrap">
        <div class="title">
          <div>{{ $t('product.product-modbus.562372-38') }}</div>
          <el-tooltip effect="dark" :content="$t('product.product-modbus.562372-39')" placement="top">
            <el-icon style="margin-left: 6px"><QuestionFilled /></el-icon>
          </el-tooltip>
        </div>
        <div class="btn-group">
          <el-button
            type="primary"
            plain
            :icon="Edit"
            @click="editDataModbus"
            v-if="!enableEditData && productInfo.status == 1"
          >
            {{ $t('product.product-modbus.562372-19') }}
          </el-button>
          <el-button
            type="primary"
            plain
            :icon="Check"
            @click="submitFormData"
            v-if="enableEditData"
            v-hasPermi="['modbus:config:edit']"
          >
            {{ $t('product.product-modbus.562372-22') }}
          </el-button>
          <el-button type="info" plain :icon="Close" @click="handleCancelData" v-if="enableEditData">
            {{ $t('product.product-modbus.562372-23') }}
          </el-button>
          <el-button
            plain
            :icon="Upload"
            v-if="productInfo.status == 1"
            @click.stop="batchImport('isSelectData')"
            v-hasPermi="['modbus:config:import']"
          >
            {{ $t('product.product-modbus.562372-20') }}
          </el-button>
          <el-button
            plain
            :icon="Download"
            v-if="productInfo.status == 1"
            @click.stop="exportModbus('isSelectData')"
            v-hasPermi="['modbus:config:export']"
          >
            {{ $t('product.product-modbus.562372-41') }}
          </el-button>
        </div>
      </div>
      <el-table
        class="table-wrap"
        v-loading="loadingData"
        :data="dataModbusList"
        :key="dataTableKey"
        ref="DataTableRef"
        :border="false"
      >
        <el-table-column
          :label="$t('product.product-modbus.562372-24')"
          align="center"
          prop="sort"
          width="60"
        ></el-table-column>
        <el-table-column :label="$t('product.product-modbus.562372-25')" align="center" prop="identifier" width="280px">
          <template #default="scope">
            <el-select
              style="width: 100%"
              filterable
              :disabled="!enableEditData"
              v-model="scope.row.identifier"
              :placeholder="$t('product.product-modbus.562372-26')"
              :ref="
                (el: any) => {
                  selectDataRefs[scope.$index] = el;
                }
              "
              @change="
                (e: any) =>
                  updateSelectThingsModel({
                    newVal: e,
                    oldVal: selectDataRefs[scope.$index]?.value,
                    justiceSelect: 'isSelectData',
                  })
              "
            >
              <el-option key="0" label="" value="" disabled style="width: 300px">
                <span style="float: left">{{ $t('product.product-modbus.562372-27') }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">
                  {{ $t('product.product-modbus.562372-28') }}
                </span>
              </el-option>
              <el-option
                v-for="item in thingsModelList"
                :key="item.identifier"
                :label="`${item.modelName} (${item.identifier})`"
                :value="item.identifier"
                style="width: 300px"
                :disabled="!item.isSelectData"
              >
                <span style="float: left">{{ item.modelName }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ item.identifier }}</span>
              </el-option>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('product.product-modbus.562372-29')"
          align="center"
          prop="address"
          width="200px"
          v-if="product.deviceType === 1"
        >
          <template #default="scope">
            <el-input-number
              style="width: 100%"
              :disabled="!enableEditData"
              v-model="scope.row.address"
              :min="0"
              :max="400000"
            ></el-input-number>
          </template>
        </el-table-column>
        <el-table-column :label="$t('product.product-modbus.562372-30')" align="center" prop="register" width="200px">
          <template #default="scope">
            <el-input-number
              style="width: 100%"
              :disabled="!enableEditData"
              v-model="scope.row.register"
              :min="0"
              :max="400000"
            ></el-input-number>
          </template>
        </el-table-column>
        <el-table-column :label="$t('product.product-modbus.562372-31')" align="center" prop="isReadonly" width="260px">
          <template #default="scope">
            <el-radio-group :disabled="!enableEditData" v-model="scope.row.isReadonly">
              <el-radio-button v-for="dict in modbus_read_config" :key="dict.value" :value="Number(dict.value)">
                {{ dict.label }}
              </el-radio-button>
            </el-radio-group>
          </template>
        </el-table-column>
        <el-table-column :label="$t('product.product-modbus.562372-43')" align="center" prop="quantity" width="200px">
          <template #default="scope">
            <el-input-number
              style="width: 100%"
              :disabled="!enableEditData"
              v-model="scope.row.quantity"
              :min="1"
              :max="256"
            ></el-input-number>
          </template>
        </el-table-column>
        <el-table-column :label="$t('product.product-modbus.562372-42')" align="center" prop="dataType" width="230px">
          <template #default="scope">
            <el-select
              :disabled="!enableEditData"
              v-model="scope.row.dataType"
              :placeholder="$t('product.product-modbus.562372-42')"
              style="display: inline-block; padding-right: 10px"
            >
              <el-option
                v-for="dict in iot_modbus_data_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              ></el-option>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('product.product-modbus.562372-34')"
          align="center"
          class-name="small-padding fixed-width"
          fixed="right"
        >
          <template #default="scope">
            <el-button
              v-if="enableEditData"
              type="primary"
              link
              :icon="Delete"
              @click="handleDelete(scope.row, scope.$index, dataModbusList, 'isSelectData')"
              v-hasPermi="['modbus:config:remove']"
            >
              {{ $t('product.product-modbus.562372-35') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="dataTotal > 0"
        :total="dataTotal"
        v-model:page="queryParamsData.pageNum"
        v-model:limit="queryParamsData.pageSize"
        @pagination="getDataList"
      />
      <el-row :gutter="10" class="mb8" style="margin-top: 10px">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAddData" v-if="enableEditData">
            {{ $t('product.product-modbus.562372-44') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAddBatch('isSelectData')" v-if="enableEditData">
            {{ $t('product.product-modbus.562372-45') }}
          </el-button>
        </el-col>
      </el-row>
    </div>
    <!-- 选择物模型 -->
    <things-list
      ref="thingsListRef"
      :productId="productIdVal"
      @productEvent="getThingsData($event)"
      :justiceSelect="justiceSelect"
    />
    <!-- 批量导入 -->
    <ImportBatch
      ref="importBatchRef"
      :productId="productIdVal"
      @data-imported="handleDataImport"
      :justiceSelect="justiceSelect"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, nextTick, getCurrentInstance } from 'vue';
import { Edit, Close, Check, Plus, Delete, Upload, Download, QuestionFilled } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { listConfig, addBatch } from '@/api/iot/modbusConfig';
import { getlListModbus } from '@/api/iot/model';
import thingsList from './things-model-list.vue';
import ImportBatch from './components/batchImportModbus.vue';
import Sortable from 'sortablejs';
import { addOrUpdate, getByProductId } from '@/api/iot/params';
import { listProductJob } from '@/api/iot/modbusJob';
import useDict from '@/utils/dict/useDict';

const {
  iot_modbus_data_type,
  iot_yes_no,
  data_collect_type,
  device_status_deter,
  iot_modbus_poll_time,
  modbus_read_config,
} = useDict(
  'iot_modbus_data_type',
  'iot_yes_no',
  'data_collect_type',
  'device_status_deter',
  'iot_modbus_poll_time',
  'modbus_read_config'
);
const { proxy } = getCurrentInstance() as any;

const emit = defineEmits(['sendPollType']);

const props = defineProps({
  product: { type: Object, default: null },
});

const formRef = ref();
const IOTableRef = ref();
const DataTableRef = ref();
const thingsListRef = ref();
const importBatchRef = ref();
const selectIoRefs = ref<any[]>([]);
const selectDataRefs = ref<any[]>([]);

const loadingIO = ref(false);
const loadingData = ref(false);
const total = ref(0);
const configList = ref<any[]>([]);
const enableEditIO = ref(false);
const enableSetSlave = ref(false);
const enableEditData = ref(false);
const dataModbusList = ref<any[]>([]);
const dataTotal = ref(0);
const thingsModelList = ref<any[]>([]);
const productIdVal = ref(0);
const productInfo = ref<any>({});
const configTableKey = ref(0);
const dataTableKey = ref(1000);
const delIoIds = ref<any[]>([]);
const delDataIds = ref<any[]>([]);
const justiceSelect = ref('isSelectData');
const jobTotal = ref(0);
let sortableIo: any = null;
let sortableData: any = null;

const queryParamsIO = reactive({
  pageNum: 1,
  pageSize: 10,
  identifier: null,
  address: null,
  isReadonly: null,
  dataType: null,
  quantity: null,
  type: 1,
  productId: 0,
});
const queryParamsData = reactive({
  pageNum: 1,
  pageSize: 10,
  identifier: null,
  address: null,
  isReadonly: null,
  dataType: null,
  quantity: null,
  type: 2,
  productId: 0,
});
const thingsModelParams = reactive({ pageNum: 1, pageSize: 1000, productId: 0 });
const form = reactive<any>({ statusDeter: 1, address: null, pollType: 0, deterTimer: '300', bitOrder: 0 });
const rules = reactive<any>({
  address: [{ required: true, message: proxy?.$t('product.product-modbus.562372-46'), trigger: 'blur' }],
});

watch(
  () => props.product,
  (newVal) => {
    productInfo.value = newVal;
    if (productInfo.value && productInfo.value.productId != 0) {
      thingsModelParams.productId = productInfo.value.productId;
      queryParamsIO.productId = productInfo.value.productId;
      queryParamsData.productId = productInfo.value.productId;
      productIdVal.value = productInfo.value.productId;
      getIOList();
      getDataList();
      getThingsModelList();
      getParams();
      sendPollType();
    }
  }
);

watch(enableEditIO, (n) => {
  if (sortableIo) sortableIo.option('disabled', !n);
  if (!n) {
    getIOList();
    getThingsModelList();
  }
  delIoIds.value = [];
  getThingsModelList();
});

watch(enableEditData, (n) => {
  if (sortableData) sortableData.option('disabled', !n);
  if (!n) {
    getDataList();
    getThingsModelList();
  }
  delDataIds.value = [];
});

onMounted(() => {
  const pid = props.product?.productId;
  productInfo.value = { status: props.product?.status };
  if (pid) {
    rowDropIo();
    rowDropData();
    sendPollType();
    thingsModelParams.productId = pid;
    queryParamsIO.productId = pid;
    queryParamsData.productId = pid;
    productIdVal.value = pid;
    getIOList();
    getDataList();
    getThingsModelList();
    getParams();
    sendPollType();
  }
});

function sendPollType() {
  emit('sendPollType', form.pollType);
}
function handleDataImport(data: any) {
  configList.value = data;
}

function getIOList() {
  loadingIO.value = true;
  listConfig(queryParamsIO).then((response: any) => {
    configList.value = response.rows;
    total.value = response.total;
    loadingIO.value = false;
  });
}

function getDataList() {
  loadingData.value = true;
  listConfig(queryParamsData).then((response: any) => {
    dataModbusList.value = response.rows;
    dataTotal.value = response.total;
    loadingData.value = false;
  });
  nextTick(() => {
    if (DataTableRef.value?.$el) DataTableRef.value.$el.querySelector('.el-table__body-wrapper')?.scrollTo(0, 0);
  });
}

function getThingsModelList() {
  getlListModbus(thingsModelParams).then((response: any) => {
    if (thingsListRef.value) thingsListRef.value.modelList = response.rows;
    thingsModelList.value = response.rows;
  });
}

function handleAddIO() {
  configList.value.push({
    identifier: '',
    slave: 1,
    address: 1,
    isReadonly: 1,
    type: 1,
    quantity: 1,
    bitOrder: 0,
    register: 0,
    sort: configList.value.length + 1,
  });
  setTimeout(() => {
    IOTableRef.value?.setCurrentRow(configList.value[configList.value.length - 1]);
  }, 10);
}

function handleAddData() {
  dataModbusList.value.push({
    identifier: '',
    slave: 1,
    address: 1,
    isReadonly: 1,
    dataType: 'ushort',
    quantity: 1,
    type: 2,
    register: 0,
    sort: dataModbusList.value.length + 1,
  });
  setTimeout(() => {
    DataTableRef.value?.setCurrentRow(dataModbusList.value[dataModbusList.value.length - 1]);
  }, 10);
}

function handleAddBatch(js: string) {
  justiceSelect.value = js;
  thingsListRef.value.open = true;
  thingsListRef.value.selectedList = js == 'isSelectData' ? dataModbusList.value : configList.value;
  thingsListRef.value.getList();
}

function editIOModbus() {
  enableEditIO.value = !enableEditIO.value;
}
function handleCancelIO() {
  enableEditIO.value = !enableEditIO.value;
}
function handleCancelData() {
  enableEditData.value = !enableEditData.value;
}
function editDataModbus() {
  enableEditData.value = !enableEditData.value;
}

function submitFormIO() {
  const list: any[] = [];
  configList.value.forEach((config: any) => {
    if (config.identifier) {
      config.sort = list.length + 1;
      list.push(config);
    }
  });
  loadingIO.value = true;
  addBatch({ productId: productIdVal.value, configList: list, delIds: delIoIds.value })
    .then(() => {
      proxy?.$modal.msgSuccess('保存成功');
      getIOList();
      loadingIO.value = false;
      enableEditIO.value = false;
    })
    .catch(() => {
      loadingIO.value = false;
    });
}

function submitFormData() {
  const list: any[] = [];
  dataModbusList.value.forEach((item: any) => {
    if (item.identifier) {
      item.sort = list.length + 1;
      list.push(item);
    }
  });
  loadingData.value = true;
  addBatch({ productId: productIdVal.value, configList: list, delIds: delDataIds.value })
    .then(() => {
      proxy?.$modal.msgSuccess(proxy?.$t('product.product-modbus.562372-52'));
      enableEditData.value = false;
      loadingData.value = false;
    })
    .catch(() => {
      loadingData.value = false;
    });
}

function handleDelete(row: any, index: number, list: any[], js: string) {
  const item = list.splice(index, 1)[0];
  if (js == 'isSelectData' && row.id) delDataIds.value.push(row.id);
  if (js == 'isSelectIo' && row.id) delIoIds.value.push(row.id);
  updateSelectThingsModel({ justiceSelect: js, oldVal: item.identifier });
}

function getThingsData(ids: any[]) {
  const list = justiceSelect.value == 'isSelectData' ? dataModbusList.value : configList.value;
  ids.forEach((id: any) => {
    if (list.findIndex((item: any) => item.identifier == id) == -1) {
      list.push({
        identifier: id,
        slave: 1,
        address: 1,
        isReadonly: 1,
        dataType: 'ushort',
        quantity: 1,
        register: 0,
        bitOrder: 0,
        type: justiceSelect.value == 'isSelectData' ? 2 : 1,
        sort: list.length + 1,
      });
      updateSelectThingsModel({ justiceSelect: justiceSelect.value, newVal: id });
    }
  });
}

function rowDropIo() {
  const tbody = IOTableRef.value?.$el?.querySelector('.el-table__body-wrapper tbody');
  if (!tbody) return;
  sortableIo = new Sortable(tbody, {
    disabled: true,
    onEnd: ({ newIndex, oldIndex }: any) => {
      dealDrop(configList.value, newIndex, oldIndex);
      configTableKey.value++;
      sortableIo?.destroy();
      nextTick(() => {
        rowDropIo();
        sortableIo?.option('disabled', false);
      });
    },
  });
}

function rowDropData() {
  const tbody = DataTableRef.value?.$el?.querySelector('.el-table__body-wrapper tbody');
  if (!tbody) return;
  sortableData = new Sortable(tbody, {
    disabled: true,
    onEnd: ({ newIndex, oldIndex }: any) => {
      dealDrop(dataModbusList.value, newIndex, oldIndex);
      dataTableKey.value++;
      sortableData?.destroy();
      nextTick(() => {
        rowDropData();
        sortableData?.option('disabled', false);
      });
    },
  });
}

function dealDrop(list: any[], newIndex: number, oldIndex: number) {
  if (oldIndex == newIndex) return;
  const curRow = list.splice(oldIndex, 1)[0];
  list.splice(newIndex, 0, curRow);
}

function updateSelectThingsModel({ oldVal, newVal, justiceSelect: js }: any) {
  const oldIndex = oldVal ? thingsModelList.value.findIndex((t: any) => t.identifier == oldVal) : -1;
  const newIndex = newVal ? thingsModelList.value.findIndex((t: any) => t.identifier == newVal) : -1;
  if (oldIndex != -1) thingsModelList.value[oldIndex][js] = true;
  if (newIndex != -1) thingsModelList.value[newIndex][js] = false;
}

function batchImport(js: string) {
  justiceSelect.value = js;
  importBatchRef.value.upload.importDeviceDialog = true;
}

function exportModbus(js: string) {
  const type = js == 'isSelectData' ? 2 : 1;
  const name =
    js == 'isSelectData'
      ? proxy?.$t('product.product-modbus.562372-38')
      : proxy?.$t('product.product-modbus.562372-17');
  proxy?.download(
    '/modbus/config/exportModbus?type=' + type + '&productId=' + productIdVal.value,
    {},
    `${name}_${new Date().getTime()}.xlsx`
  );
}

function getParams() {
  getByProductId({ productId: productIdVal.value }).then((response: any) => {
    if (response.data) {
      Object.assign(form, response.data);
      sendPollType();
    }
  });
}

function setSlave() {
  enableSetSlave.value = !enableSetSlave.value;
}

function saveSlave() {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      enableSetSlave.value = !enableSetSlave.value;
      form.productId = productIdVal.value;
      addOrUpdate(form).then(() => {
        proxy?.$modal.msgSuccess(proxy?.$t('product.product-modbus.562372-53'));
      });
    }
  });
  sendPollType();
}

function changePollType() {
  nextTick(() => {
    getTaskList(props.product.productId);
  });
  setTimeout(() => {
    if (form.pollType === 1 && jobTotal.value > 0) {
      ElMessageBox.confirm(
        proxy?.$t('product.product-modbus.562372-312'),
        proxy?.$t('product.product-modbus.562372-313'),
        {
          confirmButtonText: proxy?.$t('product.product-modbus.562372-314'),
          cancelButtonText: proxy?.$t('product.product-modbus.562372-315'),
          type: 'warning',
        }
      )
        .then(() => {})
        .catch(() => {
          form.pollType = 0;
          ElMessage({ type: 'info', message: proxy?.$t('product.product-modbus.562372-316') });
        });
    }
  }, 500);
}

function getTaskList(pid: any) {
  listProductJob({ pageNum: 1, pageSize: 10, productId: pid }).then((response: any) => {
    jobTotal.value = response.total;
  });
}

function cancelSlave() {
  enableSetSlave.value = !enableSetSlave.value;
}

/** 新增 IO 寄存器行操作(别名) */
function handleAdd() {
  handleAddIO();
}
</script>

<style lang="scss" scoped>
.modbus-wrap {
  padding: 0 5px;
  .card-wrap {
    margin-top: 10px;
    .title-wrap {
      display: flex;
      flex-direction: row;
      align-items: center;
      justify-content: space-between;
      .title {
        display: flex;
        flex-direction: row;
        align-items: center;
        font-size: 14px;
        font-weight: bold;
      }
    }
    .form-wrap {
      margin-top: 10px;
      margin-left: -150px;
    }
    .table-wrap {
      margin-top: 10px;
    }
  }
}
.tips_div {
  width: 300px;
  padding: 5px;
}
</style>
