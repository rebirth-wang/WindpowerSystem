<template>
  <div class="iot-sip">
    <el-card v-show="showSearch" style="margin-bottom: 15px; border-radius: 8px; width: 100%">
      <el-form
        @submit.prevent
        :model="queryParams"
        ref="queryRef"
        :inline="true"
        label-width="68px"
        style="margin-bottom: -18px; padding: 3px 0 0 0"
      >
        <el-form-item prop="deviceId">
          <el-input
            v-model="queryParams.deviceId"
            :placeholder="$t('sip.index.998533-1')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          />
        </el-form-item>
        <el-form-item prop="channelId">
          <el-input
            v-model="queryParams.channelId"
            :placeholder="$t('sip.index.998533-3')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          />
        </el-form-item>
        <el-form-item prop="status">
          <el-select
            v-model="queryParams.status"
            :placeholder="$t('sip.index.998533-5')"
            clearable
            @keyup.enter="handleQuery"
            style="width: 192px"
          >
            <el-option v-for="dict in sip_gen_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="handleQuery">{{ $t('search') }}</el-button>
          <el-button :icon="Refresh" @click="resetQuery">{{ $t('reset') }}</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card style="border-radius: 8px">
      <el-row :gutter="10" style="margin-bottom: 16px">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            :icon="Plus"
            @click="handleAdd"
            v-hasPermi="['iot:video:add']"
            :disabled="isGeneralUser"
          >
            {{ $t('sip.index.998533-6') }}
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            plain
            :icon="Delete"
            :disabled="multiple || isGeneralUser"
            @click="handleDelete"
            v-hasPermi="['iot:video:remove']"
          >
            {{ $t('sip.index.998533-7') }}
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table
        v-loading="loading"
        :data="sipidList"
        :border="false"
        @selection-change="handleSelectionChange"
        @cell-dblclick="celldblclick"
        ref="multipleTableRef"
        :row-key="getRowKeys"
      >
        <el-table-column
          :reserve-selection="true"
          type="selection"
          width="55"
          align="center"
          :selectable="selectable"
        />
        <el-table-column :label="$t('device.device-edit.148398-7')" align="center" prop="deviceId" min-width="195">
          <template #default="scope">
          <el-link underline="never" type="primary" @click="handleViewDevice(scope.row.deviceId)">
              {{ scope.row.deviceId }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column :label="$t('sip.index.998533-2')" align="center" prop="channelId" min-width="195" />
        <el-table-column :label="$t('sip.index.998533-4')" align="center" prop="status" width="80">
          <template #default="scope">
            <dict-tag :options="sip_gen_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column :label="$t('sip.index.998533-8')" align="left" prop="productName" min-width="180" />
        <el-table-column :label="$t('sip.index.998533-9')" align="center" prop="deviceType" min-width="110">
          <template #default="scope">
            <dict-tag v-if="scope.row.deviceType" :options="video_type" :value="scope.row.deviceType" />
            <span v-else>---</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('sip.index.998533-15')" align="center" prop="channelType" min-width="110">
          <template #default="scope">
            <dict-tag v-if="scope.row.channelType" :options="channel_type" :value="scope.row.channelType" />
            <span v-else>---</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-117')" align="center" prop="tenantName" width="150">
          <template #default="scope">
            <span v-if="scope.row.tenantName">{{ scope.row.tenantName }}</span>
            <span v-else>---</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('template.index.891112-118')" align="center" prop="createBy" width="150">
          <template #default="scope">
            <span v-if="scope.row.createBy">{{ scope.row.createBy }}</span>
            <span v-else>---</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('sip.index.998533-10')" align="center" prop="cityCode" min-width="185">
          <template #default="scope">
            <span v-if="scope.row.cityCode">{{ scope.row.cityCode }}</span>
            <span v-else>---</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('sip.index.998533-11')" align="center" prop="registerTime" width="160">
          <template #default="scope">
            <span v-if="scope.row.registerTime">
              {{ parseTime(scope.row.registerTime, '{y}-{m}-{d} {h}:{m}:{s}') }}
            </span>
            <span v-else>---</span>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          :label="$t('opation')"
          align="center"
          class-name="small-padding fixed-width"
          width="130"
        >
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              link
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-if="['600', '601', '602'].includes(scope.row.channelType)"
            >
              {{ $t('update') }}
            </el-button>
            <el-button size="small" link :icon="Connection" @click="handleBinding(scope.row)">
              {{ $t('sip.index.998533-12') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        style="margin-bottom: 20px"
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 新增/编辑 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form :model="createForm" label-width="110px" ref="createFormRef">
        <el-form-item :label="$t('sip.index.998533-13')" v-if="!createForm.id">
          <el-cascader
            :options="cityOptions"
            v-model="createForm.city"
            @change="changeProvince"
            change-on-select
            style="width: 400px"
          />
        </el-form-item>
        <el-form-item :label="$t('sip.index.998533-9')" prop="deviceType" v-if="!createForm.id">
          <el-select v-model="createForm.deviceType" :placeholder="$t('sip.index.998533-14')" style="width: 400px">
            <el-option v-for="dict in video_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('sip.index.998533-15')" prop="channelType" v-if="!createForm.id">
          <el-select v-model="createForm.channelType" :placeholder="$t('sip.index.998533-16')" style="width: 400px">
            <el-option
              v-for="dict in channel_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
              :disabled="
                createForm.deviceType === '600' && dict.value !== '600' && dict.value !== '601' && dict.value !== '602'
              "
            />
          </el-select>
        </el-form-item>
        <el-form-item
          :label="$t('sip.index.998533-8')"
          prop="productName"
          v-if="
            createForm.channelType !== '600' && createForm.channelType !== '601' && createForm.channelType !== '602'
          "
        >
          <el-input
            readonly
            v-model="createForm.productName"
            :placeholder="$t('sip.index.998533-17')"
            style="width: 400px"
          >
            <template #append>
              <el-button @click="selectProduct">{{ $t('sip.index.998533-18') }}</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('sip.index.998533-48')" prop="playUrl" v-if="createForm.channelType === '600'">
          <template #label>
            <el-tooltip :content="$t('sip.index.998533-49')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
            <span>{{ $t('sip.index.998533-48') }}</span>
          </template>
          <el-input
            v-model="createForm.playUrl"
            :placeholder="$t('sip.index.998533-50')"
            style="width: 400px"
            type="textarea"
          />
        </el-form-item>
        <el-form-item :label="$t('sip.index.998533-51')" prop="proxyUrl" v-if="createForm.channelType === '601'">
          <template #label>
            <el-tooltip :content="$t('sip.index.998533-52')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
            <span>{{ $t('sip.index.998533-51') }}</span>
          </template>
          <el-input
            v-model="createForm.proxyUrl"
            :placeholder="$t('sip.index.998533-53')"
            style="width: 400px"
            type="textarea"
          />
        </el-form-item>
        <el-form-item
          :label="$t('sip.index.998533-20')"
          prop="createNum"
          v-if="!['600', '601', '602'].includes(createForm.channelType)"
        >
          <el-input-number
            controls-position="right"
            v-model="createForm.createNum"
            :max="10"
            :placeholder="$t('sip.index.998533-19')"
            style="width: 400px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm" v-if="!createForm.id">
            {{ $t('sip.index.998533-21') }}
          </el-button>
          <el-button type="primary" @click="submitForm" v-else>{{ $t('update') }}</el-button>
          <el-button @click="cancel">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 设备绑定 -->
    <el-dialog :title="title" v-model="bindingOpen" width="600px" append-to-body>
      <el-form :model="deviceBindForm" label-width="100px" ref="deviceBindFormRef" :rules="deviceBindRules">
        <el-form-item :label="$t('sip.index.998533-22')" prop="deviceId">
          <el-select
            style="width: 400px"
            v-model="deviceBindForm.reDeviceId"
            :placeholder="$t('sip.index.998533-23')"
            filterable
            clearable
          >
            <el-option
              v-for="(item, index) in deviceList"
              :key="index"
              :label="item.deviceName"
              :value="item.deviceId"
              :disabled="item.deviceType === 3"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('sip.index.998533-24')" prop="sceneId">
          <el-select
            style="width: 400px"
            v-model="deviceBindForm.reSceneModelId"
            :placeholder="$t('sip.index.998533-25')"
            filterable
            clearable
          >
            <el-option
              v-for="(item, index) in sceneList"
              :key="index"
              :label="item.sceneModelName"
              :value="item.sceneModelId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmitDeviceBindForm" v-hasPermi="['iot:relation:add']">
            {{ $t('confirm') }}
          </el-button>
          <el-button @click="cancelBinding">{{ $t('cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 选择产品 -->
    <product-list ref="productListRef" @productEvent="getProductData" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, nextTick, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
import { Search, Refresh, Plus, Delete, Edit, Connection, QuestionFilled } from '@element-plus/icons-vue';
import { regionData, codeToText } from 'element-china-area-data';
import { getDeviceBySerialNumber, listDeviceShort } from '@/api/iot/device';
import {
  listComChannel,
  getComChannel,
  delComChannel,
  addComChannel,
  updateComChannel,
  Combinding,
} from '@/api/iot/media/channel';
import { getSceneModelList } from '@/api/scene/list';
import { parseTime } from '@/utils/ruoyi';
import { useDict } from '@/utils/dict';
import { useUserStore } from '@/stores/modules/user';
import productList from './product-list.vue';

const { proxy } = getCurrentInstance() as any;
const router = useRouter();
const userStore = useUserStore();
const { video_type, channel_type, sip_gen_status } = useDict('video_type', 'channel_type', 'sip_gen_status');

const props = defineProps({
  product: { type: Object, default: null },
});

const isGeneralUser = ref(true);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<any[]>([]);
const multiple = ref(true);
const total = ref(0);
const sipidList = ref<any[]>([]);
const title = ref('');
const open = ref(false);
const deviceList = ref<any[]>([]);
const sceneList = ref<any[]>([]);
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  deviceId: null as string | null,
  deviceChannelId: null as string | null,
  channelId: null as string | null,
  status: null as string | null,
  productId: null as number | null,
});
const createForm = ref<any>({});
const productInfo = ref<any>({});
const cityOptions = regionData as any[];
const bindingOpen = ref(false);
const deviceBindForm = ref<any>({});

const queryRef = ref<any>(null);
const createFormRef = ref<any>(null);
const deviceBindFormRef = ref<any>(null);
const multipleTableRef = ref<any>(null);
const productListRef = ref<any>(null);

// 交叉校验
let isValidate = false;
const validatePass = (rule: any, value: any, callback: any) => {
  const { reDeviceId, reSceneModelId } = deviceBindForm.value;
  if (!(reDeviceId || reSceneModelId)) {
    callback(new Error(proxy.$t('sip.index.998533-54')));
  } else {
    const validate = ['deviceId', 'sceneId'];
    if (!isValidate) {
      isValidate = true;
      const index = validate.indexOf(rule.field);
      validate.splice(index, 1);
      deviceBindFormRef.value?.validateField(validate, () => {
        setTimeout(() => {
          isValidate = false;
        }, 10);
      });
    }
    callback();
  }
};

const deviceBindRules = reactive<any>({
  deviceId: [{ validator: validatePass, trigger: 'change' }],
  sceneId: [{ validator: validatePass, trigger: 'change' }],
});

watch(
  () => props.product,
  (newVal) => {
    productInfo.value = newVal;
    if (productInfo.value && productInfo.value.productId != 0) {
      queryParams.value.productId = productInfo.value.productId;
      getList();
    }
  }
);

function handleViewDevice(serialNumber: string) {
  router.push({ path: '/iot/device/list', query: { t: Date.now().toString(), sn: serialNumber } });
}

function selectProduct() {
  open.value = false;
  productListRef.value.open = true;
  productListRef.value.getList();
}

function getProductData(product: any) {
  open.value = true;
  createForm.value.productId = product.productId;
  createForm.value.productName = product.productName;
  createForm.value.tenantId = product.tenantId;
  createForm.value.tenantName = product.tenantName;
}

function changeProvince(data: any) {
  if (data && data[0] != null && data[1] != null && data[2] != null) {
    createForm.value.citycode = codeToText[data[0]] + '/' + codeToText[data[1]] + '/' + codeToText[data[2]];
  }
}

function getList() {
  loading.value = true;
  listComChannel(queryParams.value).then((response: any) => {
    sipidList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function cancel() {
  open.value = false;
}

function handleUpdate(row: any) {
  getComChannel(row.id).then((response: any) => {
    createForm.value = response.data;
    open.value = true;
    title.value = proxy.$t('sip.index.998533-55');
  });
}

function reset() {
  createForm.value = {
    id: null,
    deviceId: null,
    channelId: null,
    status: 1,
    registertime: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    createNum: 1,
    channelType: '',
    deviceType: '',
    city: [],
    area: '',
    productName: '',
    tenantName: '',
    productId: null,
    cityCode: '',
    registerTime: '',
    proxyUrl: '',
    playUrl: '',
  };
  createFormRef.value?.clearValidate();
}

function handleQuery() {
  loading.value = true;
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  queryRef.value.resetFields();
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = proxy.$t('sip.index.998533-38');
}

function handleBinding(row: any) {
  deviceBindForm.value = row;
  bindingOpen.value = true;
  title.value = proxy.$t('sip.index.998533-40');
  nextTick(() => {
    deviceBindFormRef.value?.clearValidate();
  });
}

function getDeviceList() {
  listDeviceShort({ showChild: true, pageNum: 1, pageSize: 9999 }).then((res: any) => {
    if (res.code === 200) {
      deviceList.value = res.rows;
    }
  });
}

function getSceneListDatas() {
  getSceneModelList({ pageNum: 1, pageSize: 9999 }).then((res: any) => {
    if (res.code === 200) {
      sceneList.value = res.rows;
    }
  });
}

function cancelBinding() {
  bindingOpen.value = false;
}

function handleSubmitDeviceBindForm() {
  deviceBindFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      const { channelId, reDeviceId, reSceneModelId } = deviceBindForm.value;
      Combinding({ channelId: channelId, reDeviceId, reSceneModelId }).then((res: any) => {
        if (res.code === 200) {
          bindingOpen.value = false;
          proxy.$message.success(proxy.$t('sip.index.998533-41'));
        } else {
          proxy.$message.error(res.msg);
        }
      });
    }
  });
}

function submitForm() {
  if (createForm.value.createNum < 1) {
    proxy.$modal.alertError(proxy.$t('sip.index.998533-42'));
    return;
  }
  const allowedChannelTypes = ['600', '601', '602'];
  if (!allowedChannelTypes.includes(createForm.value.channelType)) {
    if (!createForm.value.productId || createForm.value.productId == 0) {
      proxy.$modal.alertError(proxy.$t('sip.index.998533-43'));
      return;
    }
  }
  if (['600', '601', '602'].includes(createForm.value.channelType)) {
    createForm.value.status = 3;
  }
  if (!createForm.value.id) {
    createForm.value.deviceId = createForm.value.city[2] + '0000' + createForm.value.deviceType + '0';
    createForm.value.channelId = createForm.value.city[2] + '0000' + createForm.value.channelType + '0';
    if (
      createForm.value.deviceType !== '' &&
      createForm.value.channelType !== '' &&
      createForm.value.city.length === 3
    ) {
      addComChannel(createForm.value.createNum, createForm.value).then(() => {
        proxy.$modal.msgSuccess(proxy.$t('addSuccess'));
        open.value = false;
        getList();
      });
    } else {
      proxy.$message.error(proxy.$t('sip.index.998533-44'));
    }
  } else {
    updateComChannel(createForm.value).then(() => {
      proxy.$modal.msgSuccess(proxy.$t('updateSuccess'));
      open.value = false;
      getList();
    });
  }
}

function handleDelete(row?: any) {
  const sipIds = row?.id || ids.value;
  proxy.$modal
    .confirm(proxy.$t('sip.index.998533-45', [sipIds]))
    .then(() => {
      return delComChannel(sipIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess(proxy.$t('delSuccess'));
      multipleTableRef.value?.clearSelection();
    })
    .catch(() => {});
}

function selectable(row: any) {
  return !(row.status == 2 || isGeneralUser.value);
}

function celldblclick(row: any, column: any) {
  navigator.clipboard
    .writeText(row[column.property] || '')
    .then(() => {
      proxy.$notify({
        title: proxy.$t('success'),
        message: proxy.$t('sip.index.998533-46'),
        type: 'success',
        offset: 50,
        duration: 2000,
      });
    })
    .catch(() => {
      proxy.$notify({
        title: proxy.$t('fail'),
        message: proxy.$t('sip.index.998533-47'),
        type: 'error',
        offset: 50,
        duration: 2000,
      });
    });
}

function getRowKeys(row: any) {
  return row.id;
}

onMounted(() => {
  if (!userStore.roles.includes('general')) {
    isGeneralUser.value = false;
  }
  getList();
  getDeviceList();
  getSceneListDatas();
});
</script>

<style lang="scss" scoped>
.iot-sip {
  padding: 20px;

  .specsColor {
    background-color: #fcfcfc;
  }
}
</style>
