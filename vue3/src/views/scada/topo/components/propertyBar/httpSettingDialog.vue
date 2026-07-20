<template>
  <el-dialog
    :title="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-0')"
    v-model="visibleModel"
    width="900px"
    :close-on-click-modal="false"
    append-to-body
    :before-close="handleClose"
    @opened="handleOpened"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item :label="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-1')">
        <el-row>
          <el-col :span="16">
            <el-form-item prop="url">
              <el-input
                style="width: 100%"
                :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-2')"
                v-model="form.url"
              >
                <template #prepend>
                  <el-select style="width: 100px" v-model="form.type">
                    <el-option label="GET" value="get" />
                    <el-option label="POST" value="post" />
                    <el-option label="PUT" value="put" />
                  </el-select>
                </template>
              </el-input>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-input
              style="margin-left: 10px; width: 185px"
              type="number"
              :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-3')"
              v-model="form.time"
            >
              <template #append>
                <el-select style="width: 70px" v-model="form.unit">
                  <el-option :label="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-4')" value="s" />
                  <el-option :label="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-5')" value="m" />
                  <el-option :label="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-6')" value="h" />
                </el-select>
              </template>
            </el-input>
          </el-col>
        </el-row>
      </el-form-item>

      <el-form-item :label="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-7')">
        <div style="display: flex">
          <span>{{ $t('scada.topo.components.propertyBar.httpSettingDialog.038495-8') }}</span>
        </div>

        <el-tabs style="width: 100%" v-model="tabActive">
          <el-tab-pane style="height: 300px; overflow-y: auto" label="Params" name="1">
            <el-table :data="form.params" style="width: 100%" :border="false">
              <el-table-column type="index" width="50" />
              <el-table-column prop="key" label="Key">
                <template #default="scope">
                  <el-input
                    :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-9')"
                    v-model="scope.row.key"
                    clearable
                  />
                </template>
              </el-table-column>
              <el-table-column prop="value" label="Value">
                <template #default="scope">
                  <el-input
                    :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-9')"
                    v-model="scope.row.value"
                    clearable
                  />
                </template>
              </el-table-column>
              <el-table-column
                prop="address"
                :label="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-10')"
                width="120"
              >
                <template #default="scope">
                  <el-button type="primary" plain @click="handleAddHttpParams">+</el-button>
                  <el-button type="danger" plain @click="handleDeleteHttpParams(scope.$index)">-</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane style="height: 300px" label="Body" name="2">
            <el-radio-group v-model="form.body.type" @input="handleHttpBodyTypeInput">
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
                    :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-9')"
                    v-model="scope.row.key"
                    clearable
                  />
                </template>
              </el-table-column>
              <el-table-column prop="value" label="Value">
                <template #default="scope">
                  <el-input
                    :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-9')"
                    v-model="scope.row.value"
                    clearable
                  />
                </template>
              </el-table-column>
              <el-table-column
                prop="address"
                :label="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-10')"
                width="120"
              >
                <template #default="scope">
                  <el-button type="primary" plain @click="handleAddHttpBodyFormData">+</el-button>
                  <el-button type="danger" plain @click="handleDeleteHttpBodyFormData(scope.$index)">-</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div v-if="form.body.type === 3">
              <monaco-editor
                ref="jsonValueEditor"
                height="230px"
                :isMinimap="false"
                :isQuickSuggestions="false"
                @change="handleJsonValueEditorChange"
              />
            </div>
          </el-tab-pane>

          <el-tab-pane style="height: 300px; overflow-y: auto" label="Headers" name="3">
            <el-table :data="form.headers" style="width: 100%" :border="false">
              <el-table-column type="index" width="50" />
              <el-table-column prop="key" label="Key">
                <template #default="scope">
                  <el-input
                    :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-9')"
                    v-model="scope.row.key"
                    clearable
                  />
                </template>
              </el-table-column>
              <el-table-column prop="value" label="Value">
                <template #default="scope">
                  <el-input
                    :placeholder="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-9')"
                    v-model="scope.row.value"
                    clearable
                  />
                </template>
              </el-table-column>
              <el-table-column
                prop="address"
                :label="$t('scada.topo.components.propertyBar.httpSettingDialog.038495-10')"
                width="120"
              >
                <template #default="scope">
                  <el-button type="primary" plain @click="handleAddHttpHeader">+</el-button>
                  <el-button type="danger" plain @click="handleDeleteHttpHeader(scope.$index)">-</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button type="primary" @click="handleConfirm">{{ $t('confirm') }}</el-button>
      <el-button @click="handleClose">{{ $t('cancel') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, reactive, ref } from 'vue';

const { proxy } = getCurrentInstance() as any;

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
  set: (value: boolean) => emit('update:visible', value),
});

const formRef = ref<any>(null);
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

const tabActive = ref('1');

const rules = reactive({
  url: [
    {
      required: true,
      message: proxy.$t('scada.topo.components.propertyBar.httpSettingDialog.038495-12'),
      trigger: 'blur',
    },
  ],
});

function handleOpened() {
  proxy.resetForm('form');
  const { httpSetting } = props.data || {};
  const { params, body = {}, headers, ...res } = httpSetting || {};
  const { type, value } = body || {};

  Object.assign(form, {
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
  });

  if (type === 3) {
    jsonValueEditor.value?.setValue(value);
  }
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

function handleAddHttpParams() {
  form.params.push({});
}

function handleDeleteHttpParams(index: number) {
  if (form.params.length !== 1) {
    form.params.splice(index, 1);
  }
}

function handleHttpBodyTypeInput(val: number) {
  form.body.value = val === 2 ? [{}] : '';
}

function handleAddHttpBodyFormData() {
  form.body.value.push({});
}

function handleDeleteHttpBodyFormData(index: number) {
  if (form.body.value.length !== 1) {
    form.body.value.splice(index, 1);
  }
}

function handleJsonValueEditorChange(data: string) {
  form.body.value = data;
}

function handleAddHttpHeader() {
  form.headers.push({});
}

function handleDeleteHttpHeader(index: number) {
  if (form.headers.length !== 1) {
    form.headers.splice(index, 1);
  }
}

function parseBodyValues(body: any) {
  const { type, value } = body || {};
  if (type === 2) {
    return (value || []).reduce((obj: any, item: any) => {
      if (item.key) obj[item.key] = item.value;
      return obj;
    }, {});
  }
  return value;
}

function handleConfirm() {
  formRef.value?.validate((valid: boolean) => {
    if (!valid) return;

    const { id, params, body = {}, headers, ...res } = form;
    const results = {
      ...res,
      id: id ? id : Date.now().toString(36),
      params: (params || []).reduce((obj: any, item: any) => {
        if (item.key) obj[item.key] = item.value;
        return obj;
      }, {}),
      body: {
        type: body.type || 1,
        value: parseBodyValues(body),
      },
      headers: (headers || []).reduce((obj: any, item: any) => {
        if (item.key) obj[item.key] = item.value;
        return obj;
      }, {}),
    };

    emit('confirm', results);
  });
}

function handleClose() {
  emit('update:visible', false);
}
</script>
