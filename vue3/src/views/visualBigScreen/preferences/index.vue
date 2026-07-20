<template>
  <div class="view-param-setting">
    <el-card class="mb5">
      <div class="title-wrap">{{ $t('iot.view.setting-23209-0') }}</div>
      <div class="des-wrap mt10">{{ $t('iot.view.setting-23209-1') }}</div>
    </el-card>
    <el-card class="mb5">
      <div class="title-wrap">{{ $t('iot.view.setting-23209-2') }}</div>

      <el-form class="mt10" ref="queryForm" :model="queryParams" :rules="rules" label-width="100px">
        <!-- type : 1. 轮播列表  2.滚动排名列表  3.折线图/柱状图/饼状图-非环形（单个属性）  4.折线图/柱状图（两个属性）5.视频（MP4） -->
        <el-form-item :label="$t('iot.view.setting-23209-9')" prop="type" required>
          <el-select
            style="width: 350px"
            v-model="queryParams.type"
            :placeholder="$t('iot.view.setting-23209-10')"
            filterable
            @change="handleTypeChange"
            clearable
          >
            <el-option v-for="dict in databoard_card_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('iot.view.setting-23209-3')" prop="deviceId" required>
          <el-select
            style="width: 350px"
            v-model="queryParams.deviceId"
            :placeholder="$t('iot.view.setting-23209-4')"
            :loading="deviceLoading"
            filterable
            @change="handleDeviceItem"
          >
            <el-option
              v-for="item in deviceList"
              :key="item.serialNumber"
              :label="item.deviceName"
              :value="item.deviceId || ''"
              :disabled="queryParams.type === '5' && item.deviceType !== 3"
            >
              <span>{{ item.deviceName }}</span>
              <span>{{ `（${item.serialNumber}）` }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item :label="$t('iot.view.setting-23209-7')" prop="identifier" v-if="queryParams.deviceType !== 3">
          <el-select
            style="width: 350px"
            v-model="queryParams.identifier"
            multiple
            :placeholder="$t('iot.view.setting-23209-8')"
            :loading="statusLoading"
            :multiple-limit="multipleLimit"
            filterable
            clearable
            :disabled="!queryParams.deviceId"
          >
            <el-option v-for="item in statusList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('sip.channel.998532-3')" prop="channelId" required v-if="queryParams.deviceType === 3">
          <el-select
            v-model="queryParams.channelId"
            :placeholder="$t('record.record-oss.80878-3')"
            style="width: 350px"
            size="small"
          >
            <el-option
              v-for="option in channelList"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item style="float: right">
          <el-button type="primary" size="small" :loading="generateLoading" @click="handleGenerate">
            {{ $t('iot.view.setting-23209-11') }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="mb5">
      <div class="title-wrap">{{ $t('iot.view.setting-23209-12') }}</div>
      <el-form class="mt10" label-width="100px">
        <el-form-item label="url">
          <el-input v-model="actions.url" clearable>
            <template #append>
              <el-button @click="handleCopyUrl">{{ $t('iot.view.setting-23209-13') }}</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('iot.view.setting-23209-14')">
          <div class="response-box">
            <json-viewer :value="actions.data" :expand-depth="10" copyable>
              <template #copy>
                <span class="json-copy-slot">{{ $t('iot.view.setting-23209-13') }}</span>
              </template>
            </json-viewer>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick, getCurrentInstance } from 'vue';
import { ElMessage } from 'element-plus';
import { listDeviceShort, getDevice, getDeviceRunningStatus } from '@/api/iot/device';
import { getBashBoardMatchsData, getVideoUrl } from '@/api/iot/bashBoard';
import { listComChannel } from '@/api/iot/media/channel';
import { JsonViewer } from 'vue3-json-viewer';
import useDict from '@/utils/dict/useDict';

const { databoard_card_type } = useDict('databoard_card_type');
const proxy = getCurrentInstance()?.proxy as any;

const queryForm = ref<any>(null);
const generateLoading = ref(false);
const deviceLoading = ref(false);
const statusLoading = ref(false);
const channelList = ref<any[]>([]);
const deviceList = ref<any[]>([]);
const statusList = ref<any[]>([]);

const deviceDetail = ref<any>({
  subDeviceList: [],
});

const actions = reactive<any>({
  url: '',
  data: {},
});

const queryParams = reactive<any>({
  deviceId: '',
  identifier: [],
  type: '',
  deviceType: undefined,
  serialNumber: '',
  channelId: '',
});

const rules = reactive<any>({
  deviceId: [
    {
      required: true,
      message: proxy?.$t('iot.view.setting-23209-15'),
      trigger: 'change',
    },
  ],
  identifier: [
    {
      required: true,
      message: proxy?.$t('iot.view.setting-23209-17'),
      trigger: 'change',
    },
  ],
  type: [
    {
      required: true,
      message: proxy?.$t('iot.view.setting-23209-18'),
      trigger: 'change',
    },
  ],
  channelId: [
    {
      required: true,
      message: proxy?.$t('record.record-oss.80878-3'),
      trigger: 'change',
    },
  ],
});

const multipleLimit = computed(() => {
  if (queryParams.type === '3') {
    return 1;
  }
  if (queryParams.type === '4') {
    return 2;
  }
  return 0;
});

function getDeviceList() {
  const params = { pageNum: 1, pageSize: 99999 };
  deviceLoading.value = true;
  listDeviceShort(params)
    .then((res: any) => {
      deviceList.value = res.rows || [];
    })
    .finally(() => {
      deviceLoading.value = false;
    });
}

async function handleDeviceItem() {
  deviceDetail.value = await getDeviceDetail();
  if (deviceDetail.value?.subDeviceList === null) {
    getDeviceThingsModel();
  }
  queryParams.identifier = [];
  nextTick(() => {
    queryForm.value?.clearValidate();
  });
}

function getDeviceDetail() {
  const { deviceId } = queryParams;
  return getDevice(deviceId).then((res: any) => {
    queryParams.deviceType = res.data.deviceType;
    queryParams.serialNumber = res.data.serialNumber;
    queryParams.channelId = '';
    if (queryParams.deviceType === 3) {
      getChannelList();
    } else {
      channelList.value = [];
    }
    return res.data;
  });
}

function getDeviceThingsModel() {
  statusLoading.value = true;
  const params = { deviceId: queryParams.deviceId };
  getDeviceRunningStatus(params)
    .then((res: any) => {
      const thingsModel = res.data?.thingsModels || [];
      statusList.value = formatThingsModel(thingsModel);
    })
    .finally(() => {
      statusLoading.value = false;
    });
}

function formatThingsModel(data: any[]) {
  const source = JSON.parse(JSON.stringify(data || []));
  const list: any[] = [];
  for (let i = 0; i < source.length; i++) {
    if (source[i].datatype.type == 'array') {
      if (source[i].datatype.arrayType == 'object') {
        for (let k = 0; k < source[i].datatype.arrayParams.length; k++) {
          for (let j = 0; j < source[i].datatype.arrayParams[k].length; j++) {
            const index = k > 9 ? String(k) : '0' + k;
            const prefix = 'array_' + index + '_';
            source[i].datatype.arrayParams[k][j].id = prefix + source[i].datatype.arrayParams[k][j].id;
            source[i].datatype.arrayParams[k][j].name =
              '[' + source[i].name + (k + 1) + '] ' + source[i].datatype.arrayParams[k][j].name;
            source[i].datatype.arrayParams[k][j].datatype.arrayType = 'object';
            list.push(source[i].datatype.arrayParams[k][j]);
            source[i].datatype.arrayParams[k].splice(j--, 1);
          }
        }
      }
    } else if (source[i].datatype.type == 'object') {
      for (let j = 0; j < source[i].datatype.params.length; j++) {
        source[i].datatype.params[j].name = '[' + source[i].name + '] ' + source[i].datatype.params[j].name;
        list.push(source[i].datatype.params[j]);
        source[i].datatype.params.splice(j--, 1);
      }
    } else {
      list.push(source[i]);
      source.splice(i--, 1);
    }
  }
  return list;
}

function handleTypeChange() {
  queryParams.identifier = [];
  queryParams.deviceId = '';
  queryParams.deviceType = undefined;
  queryParams.serialNumber = '';
  queryParams.channelId = '';
  statusList.value = [];
  channelList.value = [];
}

function getChannelList() {
  const params = {
    pageNum: 1,
    pageSize: 99999,
    deviceId: queryParams.serialNumber,
  };
  listComChannel(params).then((response: any) => {
    channelList.value = (response.rows || []).map((item: any) => {
      return { value: item.channelId, label: item.channelName };
    });
  });
}

function handleGenerate() {
  queryForm.value?.validate((valid: boolean) => {
    if (!valid) return;
    generateLoading.value = true;
    actions.url = '';
    actions.data = {};
    if (queryParams.deviceType == 3) {
      getVideoUrlAction();
    } else {
      getBashBoardMatchsDataAction();
    }
  });
}

function getBashBoardMatchsDataAction() {
  const { deviceId, identifier, type } = queryParams;
  const params: any = { deviceId, type, identifier };
  getBashBoardMatchsData(params)
    .then((res: any) => {
      if (res.code === 200) {
        const arr: string[] = [];
        for (const key in params) {
          if (Object.prototype.hasOwnProperty.call(params, key)) {
            arr.push(`${key}=${params[key]}`);
          }
        }
        actions.url = `${window.location.origin}${import.meta.env.VITE_APP_BASE_API}/bashBoard/matchsData?${arr.join('&')}`;
        actions.data = res.data;
        ElMessage.success(proxy?.$t('iot.view.setting-23209-19'));
      } else {
        ElMessage.success(proxy?.$t('iot.view.setting-23209-20'));
      }
    })
    .finally(() => {
      generateLoading.value = false;
    });
}

function getVideoUrlAction() {
  const deviceId = queryParams.serialNumber;
  const channelId = queryParams.channelId;
  getVideoUrl(deviceId, channelId)
    .then((res: any) => {
      if (res.code === 200) {
        actions.url = res.msg;
        ElMessage.success(proxy?.$t('iot.view.setting-23209-19'));
      } else {
        ElMessage.success(proxy?.$t('iot.view.setting-23209-20'));
      }
    })
    .finally(() => {
      generateLoading.value = false;
    });
}

async function handleCopyUrl() {
  if (!actions.url) return;
  if (navigator.clipboard && window.isSecureContext) {
    await navigator.clipboard.writeText(actions.url);
  } else {
    const input = document.createElement('input');
    input.value = actions.url;
    document.body.appendChild(input);
    input.select();
    document.execCommand('Copy');
    document.body.removeChild(input);
  }
  ElMessage.success(proxy?.$t('device.device-edit.148398-71'));
}

onMounted(() => {
  getDeviceList();
});
</script>

<style lang="scss" scoped>
.view-param-setting {
  padding: 20px;
  font-weight: 700;

  .mb5 {
    margin-bottom: 15px;
  }

  :deep(.el-card__body) {
    padding: 14px 20px;
  }

  :deep(.el-form-item) {
    margin-bottom: 14px;
  }

  :deep(.el-form-item__label) {
    font-size: 13px;
    line-height: 30px;
    color: #606266;
  }

  :deep(.el-input__wrapper),
  :deep(.el-select .el-input__wrapper) {
    min-height: 32px;
  }

  .title-wrap {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    line-height: 20px;
  }

  .des-wrap {
    color: #606266;
    font-size: 12px;
    line-height: 18px;
  }

  .response-box {
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    height: 300px;
    width: 100%;
    overflow: auto;
    padding: 8px 10px;
    background: #fff;
  }

  :deep(.jv-container) {
    position: relative;
    background: #fff;
  }

  :deep(.jv-code),
  :deep(.jv-code-box) {
    position: relative;
  }

  :deep(.jv-copy),
  :deep(.jv-button) {
    position: absolute;
    right: 10px;
    top: 6px;
    color: #79bbff !important;
    font-size: 12px;
    cursor: pointer;
  }

  .json-copy-slot {
    color: #79bbff;
    font-size: 12px;
    font-weight: 400;
    display: inline-block;
  }
}
</style>
