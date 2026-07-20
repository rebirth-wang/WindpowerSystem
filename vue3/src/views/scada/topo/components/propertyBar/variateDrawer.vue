<template>
  <div class="property-bar-variate-drawer">
    <el-drawer
      class="variate-drawer"
      :title="$t('scada.topo.components.propertyBar.variateDrawer.842076-0')"
      v-model="visibleModel"
      direction="rtl"
      size="336px"
      :before-close="handleClose"
      @opened="handleOpened"
    >
      <template #title>
        <div class="title">{{ $t('scada.topo.components.propertyBar.variateDrawer.842076-0') }}</div>
      </template>

      <div class="main-wrap">
        <el-form ref="formRef" :model="form" label-width="70px">
          <el-form-item :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-1')">
            <el-select
              v-model="form.dataType"
              :placeholder="$t('scada.topo.components.propertyBar.variateDrawer.842076-2')"
              @change="handleDataTypeChange"
              style="width: 100%"
            >
              <el-option
                :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-3')"
                :value="1"
                v-if="!isShowDeviceDataType(form.type)"
              >
                <span style="font-size: 12px">
                  {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-3') }}
                </span>
              </el-option>
              <el-option
                :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-4')"
                :value="2"
                v-if="!isShowDataMockType(form.type)"
              >
                <span style="font-size: 12px">
                  {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-4') }}
                </span>
              </el-option>
              <el-option
                :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-9')"
                :value="3"
                v-if="isShowStaticDataType(form.type)"
              >
                <span style="font-size: 12px">
                  {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-9') }}
                </span>
              </el-option>
              <el-option
                :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-10')"
                :value="4"
                v-if="isShowDynamicDataType(form.type)"
              >
                <span style="font-size: 12px">
                  {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-10') }}
                </span>
              </el-option>
              <el-option
                :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-38')"
                :value="5"
                v-if="isShowPublicInterfaceType(form.type)"
              >
                <span style="font-size: 12px">
                  {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-38') }}
                </span>
              </el-option>
            </el-select>
          </el-form-item>

          <template v-if="form.dataType === 1">
            <el-form-item :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-5')">
              <el-button class="btn" type="primary" @click="handleSelectVariate">
                {{ form.modelName || $t('scada.topo.components.propertyBar.variateDrawer.842076-6') }}
              </el-button>
            </el-form-item>
            <el-form-item :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-7')">
              <el-switch v-model="form.filter" :active-value="1" :inactive-value="0" />
            </el-form-item>
            <div class="fun-input" v-show="form.filter === 1">
              <div class="title">{{ $t('scada.topo.components.propertyBar.variateDrawer.842076-8') }}</div>
              <monaco-editor
                ref="funValueEditorRef"
                height="200px"
                :isMinimap="false"
                :isQuickSuggestions="false"
                @change="handleFunValueEditorChange"
              />
            </div>
            <!-- 添加数据映射按钮 -->
            <el-form-item
              :style="{ marginTop: form.filter === 1 ? '20px' : '0' }"
              :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-23')"
            >
              <div
                style="
                  width: 100%;
                  display: flex;
                  justify-content: space-between;
                  align-items: center;
                  margin-bottom: 6px;
                "
              >
                <el-button class="btn" type="primary" @click="openDataMappingDialog">
                  {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-24') }}
                </el-button>
              </div>
              <div v-if="form.modelMaps?.length > 0 && form.modelMaps[0].comparisonValue !== ''">
                <div
                  v-for="(item, index) in form.modelMaps"
                  :key="index"
                  style="display: flex; align-items: center; gap: 10px"
                >
                  <div class="mapping-row">
                    <div>{{ getVarName(item.identifier) }}</div>
                    <div>{{ item.operator }}</div>
                    <div class="show-item">{{ item.comparisonValue }}</div>
                    <div>→</div>
                    <div class="show-item">{{ item.actionValue }}</div>
                  </div>
                  <el-button
                    v-if="form.modelMaps?.length > 0 && form.modelMaps[0].comparisonValue !== ''"
                    type="text"
                    :icon="Delete"
                    @click="handleDeleteMapping(index)"
                  />
                </div>
              </div>
            </el-form-item>
          </template>

          <template v-if="form.dataType === 2">
            <el-form-item :label="$t('scada.topo.components.propertyBar.variateDialog.764059-10')">
              <el-input
                v-model="form.simValue"
                :placeholder="$t('scada.topo.components.propertyBar.variateDialog.764059-11')"
                clearable
              />
            </el-form-item>
            <el-form-item :label="$t('scada.topo.components.propertyBar.variateDialog.764059-12')">
              <el-input
                v-model="form.simInterval"
                type="number"
                :placeholder="$t('scada.topo.components.propertyBar.variateDialog.764059-13')"
                clearable
              />
            </el-form-item>
            <div class="tip">
              <p>{{ $t('scada.topo.components.propertyBar.variateDialog.764059-14') }}</p>
              <p>{{ $t('scada.topo.components.propertyBar.variateDialog.764059-15') }}</p>
              <p>{{ $t('scada.topo.components.propertyBar.variateDialog.764059-16') }}</p>
              <p>{{ $t('scada.topo.components.propertyBar.variateDialog.764059-17') }}</p>
            </div>
          </template>

          <template v-if="form.dataType === 3">
            <el-form-item :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-11')">
              <div style="font-size: 13px; color: #909399">
                {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-12') }}
              </div>
            </el-form-item>
            <div class="fun-input">
              <monaco-editor
                ref="staticValueEditorRef"
                height="400px"
                :isMinimap="false"
                :isQuickSuggestions="false"
                @change="handleStaticValueEditorChange"
              />
            </div>
          </template>

          <template v-if="form.dataType === 4 || form.dataType === 5">
            <div class="http-setting" @mouseenter="isHttpSettingBtn = true" @mouseleave="isHttpSettingBtn = false">
              <div class="edit-wrap" v-if="isHttpSettingBtn">
                <el-button v-if="form.dataType === 4" type="info" @click="handleHttpSettingClick">
                  {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-13') }}
                </el-button>
                <el-button v-if="form.dataType === 5" type="info" @click="handleHttpPublicSettingClick">
                  {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-13') }}
                </el-button>
              </div>

              <div class="request-content">
                <el-form-item :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-14')">
                  <el-input readonly :value="getHttpType(httpSetting.type)" />
                </el-form-item>
                <el-form-item :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-15')">
                  <el-input readonly :value="$t('scada.topo.components.propertyBar.variateDrawer.842076-22')" />
                </el-form-item>
                <el-form-item :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-16')">
                  <el-input readonly :value="httpSetting.time">
                    <template #append>{{ getHttpUnit(httpSetting.unit) }}</template>
                  </el-input>
                </el-form-item>
                <el-form-item :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-17')">
                  <el-input readonly v-model="httpSetting.url" type="textarea" />
                </el-form-item>
              </div>
            </div>

            <el-form-item
              :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-18')"
              style="margin-top: 20px"
            >
              <el-button class="btn" @click="handleSendHttpRequest">
                {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-19') }}
              </el-button>
            </el-form-item>

            <el-form-item
              v-if="httpSetting.url"
              :label="$t('scada.topo.components.propertyBar.variateDrawer.842076-20')"
            >
              <el-button class="btn" @click="handleAddHttpFilter">
                {{ $t('scada.topo.components.propertyBar.variateDrawer.842076-21') }}
              </el-button>
            </el-form-item>
          </template>
        </el-form>
      </div>

      <div class="footer-wrap">
        <el-button type="primary" @click="handleConfirm">{{ $t('confirm') }}</el-button>
        <el-button @click="handleClose">{{ $t('cancel') }}</el-button>
      </div>
    </el-drawer>

    <variate-dialog
      ref="variateDialogRef"
      v-model:visible="isVariateDialog"
      :multiple="false"
      @confirm="handleVariateConfirm"
    />
    <http-setting-dialog
      v-model:visible="isHttpSettingDialog"
      :data="httpSettingData"
      @confirm="handleHttpSettingConfirm"
    />
    <http-filter-dialog
      v-model:visible="isHttpFilterDialog"
      :data="httpFilterData"
      @confirm="handleHttpFilterConfirm"
    />
    <http-public-setting-dialog
      v-model:visible="isHttpPublicSettingDialog"
      :data="httpPublicSettingData"
      @confirm="handleHttpPublicSettingConfirm"
    />
    <data-mapping-dialog
      v-model:visible="dataMappingVisible"
      :modelName="form.modelName"
      :valueType="form.valueType"
      :modelMaps="form.modelMaps"
      :variables="mappingVariables"
      @save="saveDataMappings"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, reactive, ref, watch, nextTick } from 'vue';
import { Delete } from '@element-plus/icons-vue';
import { storeToRefs } from 'pinia';
import { useTopoEditorStore } from '@/stores/modules/topoEditor';

import VariateDialog from './variateDialog.vue';
import HttpSettingDialog from './httpSettingDialog.vue';
import HttpFilterDialog from './httpFilterDialog.vue';
import HttpPublicSettingDialog from './httpPublicSettingDialog.vue';
import DataMappingDialog from './dataMappingDialog.vue';

const { proxy } = getCurrentInstance() as any;
const topoEditorStore = useTopoEditorStore();
const { topoData, selected, pageIndex } = storeToRefs(topoEditorStore);

const props = defineProps<{
  visible: boolean;
  data: Record<string, any>;
}>();

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'confirm', value: any): void;
}>();

const visibleModel = computed({
  get: () => props.visible,
  set: (v: boolean) => emit('update:visible', v),
});

const formRef = ref<any>(null);
const variateDialogRef = ref<any>(null);
const funValueEditorRef = ref<any>(null);
const staticValueEditorRef = ref<any>(null);

const form = reactive<any>({});
const isVariateDialog = ref(false);

const httpSetting = reactive<any>({
  type: 'get',
  time: 30,
  unit: 's',
  params: {},
  body: { type: 1, value: '' },
  headers: {},
});
const isHttpSettingBtn = ref(false);
const isHttpSettingDialog = ref(false);
const httpSettingData = ref<any>({});
const isHttpFilterDialog = ref(false);
const httpFilterData = ref<any>({});
const isHttpPublicSettingDialog = ref(false);
const httpPublicSettingData = ref<any>({});

const dataMappingVisible = ref(false);
const mappingVariables = ref<any[]>([]);

watch(
  () => props.data,
  (newVal) => {
    const val = newVal || {};
    Object.keys(form).forEach((k) => delete form[k]);
    Object.assign(form, JSON.parse(JSON.stringify(val)));
    form.modelMaps = val.modelMaps || [];

    const vars: any[] = [];
    if (val?.boundRows?.length) {
      val.boundRows.forEach((r: any) => {
        vars.push({ identifier: r.identifier, modelName: r.modelName, valueType: r.valueType });
      });
    } else if (Array.isArray(val?.identifiers)) {
      const names = val.modelNames || [];
      val.identifiers.forEach((id: string, idx: number) => {
        vars.push({ identifier: id, modelName: names[idx] || id });
      });
    } else if (val?.identifier) {
      vars.push({ identifier: val.identifier, modelName: val.modelName, valueType: val.valueType });
    }
    mappingVariables.value = vars;
  },
  { deep: true, immediate: true }
);

function handleOpened() {
  nextTick(() => {
    funValueEditorRef.value?.setValue(form.funValue);
    staticValueEditorRef.value?.setValue(form.staticValue);
    const { dataType, httpSetting: h, httpSettingId } = form;
    resetHttpSetting();
    if (dataType === 4 && h && Object.keys(h).length !== 0) Object.assign(httpSetting, h);
    if (dataType === 5 && httpSettingId) {
      const list = topoData.value?.[selected.value]?.[pageIndex.value]?.httpPublicSetting || [];
      const row = list.find((item: any) => item.id === httpSettingId);
      if (row) Object.assign(httpSetting, row);
    }
  });
}

function handleDataTypeChange(val: number) {
  nextTick(() => {
    const { funValue, staticValue, httpSetting: h, httpSettingId } = props.data || {};
    const temp = {
      type: 'get',
      time: 30,
      unit: 's',
      params: {},
      body: { type: 1, value: '' },
      headers: {},
    };
    if (val === 1) funValueEditorRef.value?.setValue(funValue);
    if (val === 3) staticValueEditorRef.value?.setValue(staticValue);
    if (val === 4) Object.assign(httpSetting, h && Object.keys(h).length ? h : temp);
    if (val === 5) {
      if (httpSettingId) {
        const list = topoData.value?.[selected.value]?.[pageIndex.value]?.httpPublicSetting || [];
        const row = list.find((item: any) => item.id === httpSettingId);
        Object.assign(httpSetting, row || temp);
      } else {
        Object.assign(httpSetting, temp);
      }
    }
  });
}

function handleSelectVariate() {
  const { serialNumber, sceneModelDeviceId, identifier } = { ...props.data, ...form };
  variateDialogRef.value?.setCurSinIdentifier(identifier ?? null);
  variateDialogRef.value?.setQueryDeviceContext?.(serialNumber, sceneModelDeviceId);
  variateDialogRef.value?.open();
  isVariateDialog.value = true;
}

function handleVariateConfirm(row: any) {
  const { modelName, identifier, sceneModelId, sceneModelDeviceId, variableType, productId, serialNumber, valueType } =
    row || {};
  Object.assign(form, {
    ...form,
    modelName,
    identifier,
    sceneModelId,
    sceneModelDeviceId,
    variableType,
    productId,
    serialNumber,
    valueType,
  });
  mappingVariables.value = [{ identifier, modelName, valueType }];
  isVariateDialog.value = false;
}

function handleFunValueEditorChange(data: string) {
  form.funValue = data;
}
function handleStaticValueEditorChange(data: string) {
  form.staticValue = data;
}

function resetHttpSetting() {
  Object.assign(httpSetting, {
    type: 'get',
    time: 30,
    unit: 's',
    params: {},
    body: { type: 1, value: '' },
    headers: {},
  });
}

function handleHttpSettingClick() {
  httpSettingData.value = { httpSetting: { ...httpSetting } };
  isHttpSettingDialog.value = true;
}
function handleHttpSettingConfirm(data: any) {
  form.httpSetting = data;
  Object.assign(httpSetting, data);
  isHttpSettingDialog.value = false;
}

function getBodyValues(body: any) {
  const { type, value } = body || {};
  if (type === 3 && value) return eval('(' + value + ')');
  return value;
}
function getBodyheaders(body: any, headers: any) {
  const { type } = body || {};
  if (type === 2) return { 'Content-Type': 'application/x-www-form-urlencoded', ...(headers || {}) };
  return { 'Content-Type': 'application/json;charset=utf-8', ...(headers || {}) };
}

function handleSendHttpRequest() {
  const { url, type, params, body, headers } = httpSetting || {};
  proxy.$modal.loading(proxy.$t('scene.detail.index.209809-3'));
  proxy
    .$request({
      url,
      method: type,
      headers: getBodyheaders(body, headers),
      params,
      data: getBodyValues(body),
    })
    .then(() => proxy.$modal.msgSuccess(proxy.$t('scada.topo.components.propertyBar.variateDrawer.842076-43')))
    .finally(() => proxy.$modal.closeLoading());
}

function handleAddHttpFilter() {
  httpFilterData.value = { httpSetting: { ...httpSetting }, httpFilter: form.httpFilter };
  isHttpFilterDialog.value = true;
}
function handleHttpFilterConfirm(data: any) {
  if (typeof data === 'object' && data.filter !== undefined) {
    form.httpFilter = data.filter;
    form.echartData = JSON.stringify(data.response);
  } else {
    form.httpFilter = data;
  }
  isHttpFilterDialog.value = false;
}

function handleHttpPublicSettingClick() {
  httpPublicSettingData.value = { httpSettingId: form.httpSettingId };
  isHttpPublicSettingDialog.value = true;
}
function handleHttpPublicSettingConfirm(data: any) {
  const { httpSettingId } = data || {};
  form.httpSettingId = httpSettingId;
  const list = topoData.value?.[selected.value]?.[pageIndex.value]?.httpPublicSetting || [];
  const h = list.find((item: any) => item.id === httpSettingId);
  if (h) Object.assign(httpSetting, h);
  isHttpPublicSettingDialog.value = false;
}

function handleConfirm() {
  emit('confirm', JSON.parse(JSON.stringify(form)));
}
function handleClose() {
  emit('update:visible', false);
}

function getHttpType(type: string) {
  if (type === 'get') return 'GET';
  if (type === 'post') return 'POST';
  if (type === 'put') return 'PUT';
  return '';
}
function getHttpUnit(unit: string) {
  if (unit === 's') return proxy.$t('scada.topo.components.propertyBar.variateDialog.764059-18');
  if (unit === 'm') return proxy.$t('scada.topo.components.propertyBar.variateDialog.764059-19');
  if (unit === 'h') return proxy.$t('scada.topo.components.propertyBar.variateDialog.764059-20');
  return '';
}

function openDataMappingDialog() {
  dataMappingVisible.value = true;
}
function saveDataMappings(newMappings: any[]) {
  form.modelMaps = newMappings;
}
function handleDeleteMapping(index: number) {
  form.modelMaps.splice(index, 1);
}
function getVarName(identifier: string) {
  const v = mappingVariables.value.find((x) => x.identifier === identifier);
  return v ? v.modelName || identifier : identifier;
}

function isShowDeviceDataType(type: string) {
  return ['select', 'chart-wrapper', 'chart-map'].includes(type);
}
function isShowDataMockType(type: string) {
  return ['select', 'chart-wrapper', 'chart-map'].includes(type);
}
function isShowStaticDataType(type: string) {
  return ['select'].includes(type);
}
function isShowDynamicDataType(type: string) {
  return [
    'text',
    'num',
    'image-switch',
    'flow-bar-dynamic',
    'chart-water',
    'triangle',
    'rect',
    'circular',
    'straight-line',
    'line-segment',
    'bizier-curve',
    'vertical-line',
    'chart-gauge',
    'chart-temp',
    'image',
    'chart-wrapper',
    'chart-map',
  ].includes(type);
}
function isShowPublicInterfaceType(type: string) {
  return isShowDynamicDataType(type);
}
</script>

<style lang="scss" scoped>
.property-bar-variate-drawer {
  width: 100%;

  .variate-drawer {
    width: 100%;

    .title {
      font-size: 15px;
    }

    .main-wrap {
      width: 100%;

      .btn {
        border: 1px solid #6d8cf58f !important;
        background-color: #ffffff !important;
        color: #6d8cf5 !important;
        width: 100%;
      }

      .fun-input {
        width: 293px;
        margin-left: 3px;

        .title {
          height: 20px;
          line-height: 16px;
          border: 1px solid rgba(0, 0, 0, 0.298);
          font-size: 13px;
        }
      }

      .tip {
        margin-left: 16px;

        p {
          font-size: 13px;
          line-height: 23px;
          margin-top: 10px;
        }
      }

      .http-setting {
        position: relative;
        padding: 10px 5px;
        margin: -10px -5px;

        .edit-wrap {
          position: absolute;
          inset: 0;
          background-color: rgba(0, 0, 0, 0.6);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 10;
          cursor: pointer;
          color: #ffffff;
          border-radius: 4px;
        }

        .request-content {
          position: relative;
          z-index: 5;
        }
      }
    }

    .footer-wrap {
      position: absolute;
      bottom: 15px;
      right: 20px;
    }
  }
}

.mapping-row {
  display: flex;
  gap: 5px;
  max-width: 200px;
}

.show-item {
  max-width: 100px;
  overflow-x: auto;
  white-space: nowrap;
}
</style>
