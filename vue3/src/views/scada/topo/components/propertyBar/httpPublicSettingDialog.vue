<template>
  <el-dialog
    :title="$t('scada.topo.components.propertyBar.httpPublicSettingDialog.038495-0')"
    v-model="visibleModel"
    width="1100px"
    append-to-body
    :close-on-click-modal="false"
    :before-close="handleClose"
    @opened="handleOpened"
  >
    <el-row :gutter="20">
      <el-col :span="15">
        <el-form label-width="70px">
          <div style="margin-bottom: 20px">
            <el-button type="primary" :icon="Plus" @click="handleAddHttp">
              {{ $t('add') }}
            </el-button>
            <el-button :icon="Edit" :disabled="!httpSettingId" @click="handleEditHttp">
              {{ $t('update') }}
            </el-button>
          </div>

          <el-form-item :label="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-1')">
            <el-row>
              <el-col :span="17">
                <el-input
                  style="width: 100%"
                  v-model="form.url"
                  :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-2')"
                  disabled
                >
                  <template #prepend>
                    <el-select disabled style="width: 95px" v-model="form.type">
                      <el-option label="GET" value="get" />
                      <el-option label="POST" value="post" />
                      <el-option label="PUT" value="put" />
                    </el-select>
                  </template>
                </el-input>
              </el-col>
              <el-col :span="7">
                <el-input
                  disabled
                  style="margin-left: 10px; width: 159px"
                  type="number"
                  :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-3')"
                  v-model="form.time"
                >
                  <template #append>
                    <el-select disabled style="width: 70px" v-model="form.unit">
                      <el-option
                        :label="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-4')"
                        value="s"
                      />
                      <el-option
                        :label="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-5')"
                        value="m"
                      />
                      <el-option
                        :label="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-6')"
                        value="h"
                      />
                    </el-select>
                  </template>
                </el-input>
              </el-col>
            </el-row>
          </el-form-item>

          <el-form-item :label="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-7')">
            <div>{{ $t('scada.topo.components.propertyBar.httpSettingDialog.038495-8') }}</div>
            <el-tabs style="width: 100%" v-model="tabActive">
              <el-tab-pane style="height: 300px; overflow-y: auto" label="Params" name="1">
                <el-table :data="form.params" style="width: 100%" :border="false">
                  <el-table-column type="index" width="50" />
                  <el-table-column prop="key" label="Key">
                    <template #default="scope">
                      <el-input
                        disabled
                        :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-9')"
                        v-model="scope.row.key"
                        clearable
                      />
                    </template>
                  </el-table-column>
                  <el-table-column prop="value" label="Value">
                    <template #default="scope">
                      <el-input
                        disabled
                        :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-9')"
                        v-model="scope.row.value"
                        clearable
                      />
                    </template>
                  </el-table-column>
                </el-table>
              </el-tab-pane>

              <el-tab-pane style="height: 300px" label="Body" name="2">
                <el-radio-group v-model="form.body.type" @input="handleHttpBodyRadioInput">
                  <el-radio :label="1">none</el-radio>
                  <el-radio :label="2">x-www-form-urlencoded</el-radio>
                  <el-radio :label="3">json</el-radio>
                </el-radio-group>

                <el-empty
                  v-if="form.body.type === 1"
                  :description="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-11')"
                />

                <el-table
                  v-if="form.body.type === 2"
                  :data="form.body.value"
                  style="width: 100%; height: 264px; overflow-y: auto"
                  :border="false"
                >
                  <el-table-column type="index" width="50" />
                  <el-table-column prop="key" label="Key">
                    <template #default="scope">
                      <el-input
                        disabled
                        :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-9')"
                        v-model="scope.row.key"
                        clearable
                      />
                    </template>
                  </el-table-column>
                  <el-table-column prop="value" label="Value">
                    <template #default="scope">
                      <el-input
                        disabled
                        :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-9')"
                        v-model="scope.row.value"
                        clearable
                      />
                    </template>
                  </el-table-column>
                </el-table>

                <div v-if="form.body.type === 3">
                  <monaco-editor
                    ref="jsonValueEditor"
                    height="230px"
                    :isMinimap="false"
                    :isQuickSuggestions="false"
                    :readOnly="true"
                  />
                </div>
              </el-tab-pane>

              <el-tab-pane style="height: 300px; overflow-y: auto" label="Headers" name="headers">
                <el-table :data="form.headers" style="width: 100%" :border="false">
                  <el-table-column type="index" width="50" />
                  <el-table-column prop="key" label="Key">
                    <template #default="scope">
                      <el-input
                        disabled
                        :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-9')"
                        v-model="scope.row.key"
                        clearable
                      />
                    </template>
                  </el-table-column>
                  <el-table-column prop="value" label="Value">
                    <template #default="scope">
                      <el-input
                        disabled
                        :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-9')"
                        v-model="scope.row.value"
                        clearable
                      />
                    </template>
                  </el-table-column>
                </el-table>
              </el-tab-pane>
            </el-tabs>
          </el-form-item>
        </el-form>
      </el-col>

      <el-col :span="9">
        <div>{{ $t('scada.topo.components.propertyBar.httpPublicSettingDialog.038495-1') }}</div>
        <el-table
          ref="publicTable"
          :data="httpPublicList"
          max-height="482px"
          style="width: 100%"
          :border="false"
          @row-click="handleSelectItemHttpSetting"
        >
          <el-table-column :label="$t('select')" width="60" align="center">
            <template #default="{ row }">
              <el-radio
                class="http-public-row-radio"
                v-model="httpSettingId"
                :value="row.id"
                @change="handleSelectItemHttpSetting(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="URL" prop="url" :show-overflow-tooltip="true" />
          <el-table-column :label="$t('opation')" width="60" align="center">
            <template #default="{ row }">
              <i
                class="el-icon-delete"
                @click.stop="handleDeleteItemHttpSetting(row.id)"
                style="cursor: pointer; color: #f56c6c"
              ></i>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>

    <template #footer>
      <el-button type="primary" @click="handleConfirm">{{ $t('confirm') }}</el-button>
      <el-button @click="handleClose">{{ $t('cancel') }}</el-button>
    </template>

    <http-setting-dialog
      v-model:visible="isHttpSettingDialog"
      :data="httpSettingData"
      @confirm="handleHttpSettingConfirm"
    />
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, nextTick, reactive, ref } from 'vue';
import { Edit, Plus } from '@element-plus/icons-vue';
import { storeToRefs } from 'pinia';
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import HttpSettingDialog from './httpSettingDialog.vue';

const topoEditorStore = useTopoEditorStore();
const { topoData, selected, pageIndex } = storeToRefs(topoEditorStore);

const props = defineProps<{
  visible: boolean;
  data: Record<string, any>;
}>();

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'confirm', value: { httpSettingId: string }): void;
}>();

const visibleModel = computed({
  get: () => props.visible,
  set: (v: boolean) => emit('update:visible', v),
});

const jsonValueEditor = ref<any>(null);

const form = reactive<any>({
  type: 'get',
  time: 30,
  unit: 's',
  params: [{}],
  body: {
    type: 1,
    value: '',
  },
  headers: [{}],
});

const httpPublicList = ref<any[]>([]);
const isHttpSettingDialog = ref(false);
const httpSettingData = ref<any>({});
const httpSettingId = ref('');
const tabActive = ref('1');

function handleOpened() {
  resetData();
  const list = topoData.value?.[selected.value]?.[pageIndex.value]?.httpPublicSetting || [];
  httpPublicList.value = JSON.parse(JSON.stringify(list));

  if (httpPublicList.value.length !== 0) {
    httpPublicList.value.forEach((item) => (item.isSelect = false));
    const selectedId = props.data?.httpSettingId;
    if (selectedId) {
      setHttpItemSelect(selectedId);
      const row = httpPublicList.value.find((item) => item.id === selectedId);
      if (row) handleSelectItemHttpSetting(row);
      httpSettingId.value = selectedId;
    }
  }
}

function handleAddHttp() {
  httpSettingData.value = {
    type: 'get',
    time: 30,
    unit: 's',
    params: {},
    body: {
      type: 1,
      value: '',
    },
    headers: {},
  };
  isHttpSettingDialog.value = true;
}

function handleHttpSettingConfirm(data: any) {
  const old = httpPublicList.value.find((item) => item.id === data.id);
  if (old) Object.assign(old, data);
  else httpPublicList.value.push(data);
  isHttpSettingDialog.value = false;
}

function handleSelectItemHttpSetting(row: any) {
  const { id } = row;
  const httpSetting = httpPublicList.value.find((item) => item.id === id);
  const { params, body = {}, headers, ...res } = httpSetting || {};
  const { type, value } = body || {};

  const pa = {
    ...res,
    params:
      params && Object.keys(params).length !== 0
        ? Object.keys(params).map((key) => ({ key, value: params[key] }))
        : [{}],
    body: {
      type: type || 1,
      value: getBodyValues(body),
    },
    headers:
      headers && Object.keys(headers).length !== 0
        ? Object.keys(headers).map((key) => ({ key, value: headers[key] }))
        : [{}],
  };

  Object.assign(form, JSON.parse(JSON.stringify(pa)));

  if (type === 3) {
    nextTick(() => {
      jsonValueEditor.value?.setValue(value);
    });
  }

  setHttpItemSelect(id);
  httpSettingId.value = id;
}

function getBodyValues(body: any) {
  const { type, value } = body || {};
  if (type === 2) {
    return value && Object.keys(value).length !== 0
      ? Object.keys(value).map((key) => ({ key, value: value[key] }))
      : [{}];
  }
  return value;
}

function setHttpItemSelect(id: string) {
  httpPublicList.value.forEach((item) => {
    item.isSelect = item.id === id;
  });
  httpPublicList.value = JSON.parse(JSON.stringify(httpPublicList.value));
}

function handleEditHttp() {
  const row = httpPublicList.value.find((item) => item.id === httpSettingId.value);
  httpSettingData.value = { httpSetting: row };
  isHttpSettingDialog.value = true;
}

function handleDeleteItemHttpSetting(id: string) {
  httpPublicList.value = httpPublicList.value.filter((item) => item.id !== id);
  if (httpSettingId.value === id) {
    setTimeout(() => {
      httpSettingId.value = '';
    }, 100);
  }
}

function handleHttpBodyRadioInput(val: number) {
  if (val === 2) form.body.value = [{}];
  else form.body.value = '';
}

function resetData() {
  Object.assign(form, {
    type: 'get',
    time: 30,
    unit: 's',
    params: [{}],
    body: {
      type: 1,
      value: '',
    },
    headers: [{}],
  });
  httpPublicList.value = [];
  isHttpSettingDialog.value = false;
  httpSettingData.value = {};
  httpSettingId.value = '';
  tabActive.value = '1';
}

function handleConfirm() {
  topoEditorStore.setHttpPublicSetting(httpPublicList.value);
  emit('confirm', { httpSettingId: httpSettingId.value });
}

function handleClose() {
  emit('update:visible', false);
}
</script>

<style scoped>
.http-public-row-radio :deep(.el-radio__label) {
  display: none;
  padding-left: 0;
}
</style>
