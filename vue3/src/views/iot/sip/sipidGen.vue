<template>
  <el-dialog :title="title" v-model="open" width="550px" append-to-body>
    <el-form ref="createFormRef" :model="createForm" label-width="100px">
      <el-form-item :label="$t('sip.sipidGen.998538-0')">
        <el-cascader
          :options="cityOptions"
          :props="{ checkStrictly: true }"
          v-model="createForm.city"
          @change="changeProvince"
          style="width: 350px"
        />
      </el-form-item>
      <el-form-item :label="$t('sip.index.998533-9')" prop="deviceType">
        <el-select
          clearable
          v-model="createForm.deviceType"
          :placeholder="$t('sip.index.998533-14')"
          filterable
          style="width: 350px"
        >
          <el-option
            v-for="dict in video_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
            :disabled="dict.value === '600'"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('sip.index.998533-15')" prop="channelType">
        <el-select
          clearable
          v-model="createForm.channelType"
          :placeholder="$t('sip.index.998533-16')"
          filterable
          :disabled="createForm.deviceType === ''"
          style="width: 350px"
        >
          <el-option
            v-for="dict in channel_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
            :disabled="dict.value === '600' || dict.value === '601'"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('sip.index.998533-20')" prop="createNum">
        <el-input-number
          controls-position="right"
          v-model="createForm.createNum"
          :placeholder="$t('sip.index.998533-19')"
          style="width: 350px"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitForm">{{ $t('sip.index.998533-21') }}</el-button>
        <el-button @click="closeDialog">{{ $t('cancel') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, getCurrentInstance } from 'vue';
import { regionData, codeToText } from 'element-china-area-data';
import { addComChannel } from '@/api/iot/media/channel';
import { useDict } from '@/utils/dict';

const { proxy } = getCurrentInstance() as any;
const { video_type, channel_type } = useDict('video_type', 'channel_type');

const props = defineProps({
  product: {
    type: Object,
    default: null,
  },
});

const emit = defineEmits(['addGenEvent']);

const title = ref(proxy.$t('sip.sipidGen.998538-1'));
const open = ref(false);
const createFormRef = ref<any>(null);
const createForm = ref<any>({
  city: [],
  deviceType: '',
  channelType: '',
  createNum: 1,
  citycode: '',
});

function processCityOptions(data: any[]) {
  const processedData = JSON.parse(JSON.stringify(data));
  const guangdong = processedData.find((item: any) => item.value === '440000');
  if (guangdong && guangdong.children) {
    const dongguan = guangdong.children.find((city: any) => city.value === '441900');
    const zhongshan = guangdong.children.find((city: any) => city.value === '442000');
    if (dongguan) dongguan.children = undefined;
    if (zhongshan) zhongshan.children = undefined;
  }
  return processedData;
}

const cityOptions = processCityOptions(regionData as any[]);

function openDialog() {
  createForm.value = { city: [], deviceType: '', channelType: '', createNum: 1, citycode: '' };
  open.value = true;
}

function changeProvince(data: any[]) {
  if (!data || data.length === 0) {
    createForm.value.citycode = '';
    return;
  }
  const province = codeToText[data[0]];
  if (data.length >= 2) {
    const cityCode = data[1];
    if (cityCode === '441900' || cityCode === '442000') {
      createForm.value.city = data.slice(0, 2);
      const cityName = codeToText[cityCode];
      createForm.value.citycode = province + '/' + cityName;
      return;
    }
  }
  if (data.length === 3) {
    const city = codeToText[data[1]];
    const district = codeToText[data[2]];
    createForm.value.citycode = province + '/' + city + '/' + district;
  }
}

function submitForm() {
  if (createForm.value.createNum < 1) {
    proxy.$modal.alertError(proxy.$t('sip.index.998533-42'));
    return;
  }

  createForm.value.productId = props.product?.productId;
  createForm.value.productName = props.product?.productName;
  createForm.value.tenantId = props.product?.tenantId;
  createForm.value.tenantName = props.product?.tenantName;

  const isSpecialCity =
    createForm.value.city.length === 2 &&
    (createForm.value.city[1] === '441900' || createForm.value.city[1] === '442000');

  if (isSpecialCity) {
    createForm.value.deviceId = createForm.value.city[1] + '0000' + createForm.value.deviceType + '0';
    createForm.value.channelId = createForm.value.city[1] + '0000' + createForm.value.channelType + '0';
  } else {
    createForm.value.deviceId = createForm.value.city[2] + '0000' + createForm.value.deviceType + '0';
    createForm.value.channelId = createForm.value.city[2] + '0000' + createForm.value.channelType + '0';
  }

  const isValidCity = createForm.value.city.length === 3 || isSpecialCity;
  if (createForm.value.deviceType !== '' && createForm.value.channelType !== '' && isValidCity) {
    addComChannel(createForm.value.createNum, createForm.value).then((res: any) => {
      if (res.code === 200) {
        emit('addGenEvent', res.data);
        proxy.$modal.msgSuccess(proxy.$t('sip.sipidGen.998538-2'));
      }
      open.value = false;
    });
  } else {
    proxy.$modal.alertError(proxy.$t('sip.sipidGen.998538-3'));
  }
}

function closeDialog() {
  open.value = false;
}

defineExpose({ openDialog });
</script>
