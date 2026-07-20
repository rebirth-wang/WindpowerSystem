<template>
  <el-dialog
    :title="$t('scada.topo.components.propertyBar.httpFilterDialog.038495-0')"
    v-model="visibleModel"
    width="1000px"
    :close-on-click-modal="false"
    append-to-body
    :before-close="handleClose"
    @opened="handleOpened"
  >
    <el-row :gutter="15" class="http-filter-dialog">
      <el-col :span="12" class="left-wrap">
        <div class="filter-wrap">
          <div class="title-wrap">
            <span style="color: #b478cf">function&nbsp;</span>
            <span style="color: #70c0e8">filter(res) &lbrace;</span>
          </div>
          <monaco-editor ref="filterEditor" height="459px" @change="handleFilterEditorChange" />
          <div class="end-wrap">&rbrace;</div>
        </div>
      </el-col>

      <el-col :span="12" class="right-wrap">
        <div class="res-wrap">
          {{ $t('scada.topo.components.propertyBar.httpFilterDialog.038495-1') }}{{ response || $t('none') }}
        </div>
        <div class="result-wrap">
          {{ $t('scada.topo.components.propertyBar.httpFilterDialog.038495-2') }}{{ results || $t('none') }}
        </div>
      </el-col>
    </el-row>

    <template #footer>
      <el-button type="primary" @click="handleConfirm">{{ $t('confirm') }}</el-button>
      <el-button @click="handleClose">{{ $t('cancel') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, ref, watch } from 'vue';

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
  set: (v: boolean) => emit('update:visible', v),
});

const filterEditor = ref<any>(null);
const filter = ref('');
const response = ref<any>(null);
const results = ref<any>(null);

watch(
  () => props.data,
  (newVal) => {
    filter.value = newVal?.httpFilter || '';
  },
  { deep: true, immediate: true }
);

function handleOpened() {
  dataReset();
  const { httpSetting } = props.data || {};
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
    .then((res: any) => {
      response.value = res;
    })
    .finally(() => {
      proxy.$modal.closeLoading();
      filterEditor.value?.setValue(filter.value || 'return res');
    });
}

function getBodyValues(body: any) {
  const { type, value } = body || {};
  if (type === 3 && value) {
    return eval('(' + value + ')');
  }
  return value;
}

function getBodyheaders(body: any, headers: any) {
  const { type } = body || {};
  if (type === 2) {
    return { 'Content-Type': 'application/x-www-form-urlencoded', ...(headers || {}) };
  }
  return { 'Content-Type': 'application/json;charset=utf-8', ...(headers || {}) };
}

function handleFilterEditorChange(data: string) {
  filter.value = data;
  const resp = response.value || '';
  if (!resp) return;

  const funStr = 'function () {\n' + 'const res = resp;' + '\n' + data + '\n' + '}';
  try {
    const fun = eval('(' + funStr + ')');
    results.value = fun();
  } catch (e) {
    results.value = e;
  }
}

function dataReset() {
  response.value = null;
  results.value = null;
}

function handleConfirm() {
  emit('confirm', {
    filter: filter.value,
    result: results.value,
    response: response.value,
  });
}

function handleClose() {
  emit('update:visible', false);
}
</script>

<style lang="scss" scoped>
.http-filter-dialog {
  width: 100%;

  .left-wrap {
    height: 100%;

    .filter-wrap {
      background-color: #1e1e1e;
      border-radius: 5px;

      .title-wrap {
        padding: 10px;
      }

      .end-wrap {
        padding: 10px;
        color: #70c0e8;
      }
    }
  }

  .right-wrap {
    height: 531px;
    overflow-y: auto;

    .res-wrap {
      min-height: 258px;
      background-color: #1e1e1e;
      padding: 20px;
      border-radius: 5px;
      color: #fff;
    }

    .result-wrap {
      min-height: 258px;
      margin-top: 15px;
      background-color: #1e1e1e;
      padding: 20px;
      border-radius: 5px;
      color: #fff;
    }
  }
}
</style>