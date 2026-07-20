<template>
  <el-dialog
    :title="$t('scada.topo.components.propertyBar.interactionEventDialog.764059-0')"
    v-model="visibleModel"
    width="700px"
    :before-close="handleClose"
    append-to-body
  >
    <div class="interaction-event-dialog">
      <el-button type="primary" @click="handleAddForm" :icon="Plus">{{ $t('add') }}</el-button>
      <div class="form-card-wrap">
        <div v-if="formData.interList.length === 0" class="empty-state">
          <el-icon style="font-size: 24px; margin-bottom: 10px">
            <InfoFilled />
          </el-icon>
          <p>{{ $t('scada.topo.components.propertyBar.interactionEventDialog.764059-1') }}</p>
        </div>

        <div v-else class="form-card" v-for="(form, index) in formData.interList" :key="index">
          <div class="header">
            <span class="form-title">
              {{ $t('scada.topo.components.propertyBar.interactionEventDialog.764059-2') }} - {{ index + 1 }}
            </span>
            <el-icon class="delete-btn" @click="handleRemoveForm(index)">
              <RemoveFilled />
            </el-icon>
          </div>

          <el-form :model="form" label-width="80px">
            <div class="section-title">
              {{ $t('scada.topo.components.propertyBar.interactionEventDialog.764059-21') }}
            </div>

            <div class="flex-container">
              <el-form-item
                :label="$t('scada.topo.components.propertyBar.interactionEventDialog.764059-4')"
                prop="triggerType"
              >
                <el-select
                  style="width: 100%"
                  v-model="form.triggerType"
                  :placeholder="$t('scada.topo.components.propertyBar.interactionEventDialog.764059-5')"
                >
                  <el-option
                    v-for="item in formData.interInfo.triggerTypes"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>

              <el-form-item :label="$t('scada.topo.components.propertyBar.interactionEventDialog.764059-7')">
                <template #label>
                  <span style="display: inline-flex; align-items: center; gap: 4px">
                    {{ $t('scada.topo.components.propertyBar.interactionEventDialog.764059-7') }}
                    <el-tooltip
                      :content="$t('scada.topo.components.propertyBar.interactionEventDialog.764059-8')"
                      placement="top"
                    >
                      <el-icon style="display: inline-flex; align-items: center">
                        <QuestionFilled />
                      </el-icon>
                    </el-tooltip>
                  </span>
                </template>

                <el-select
                  style="width: 100%"
                  v-model="form.bindCompId"
                  :placeholder="$t('pleaseSelect')"
                  @change="handleBindCompChange($event, index)"
                >
                  <el-option
                    v-for="item in bindCompList"
                    :key="item.identifier"
                    :label="item.name"
                    :value="item.identifier"
                    :disabled="item.disabled"
                  />
                </el-select>
              </el-form-item>
            </div>

            <el-form-item
              :label="$t('scada.topo.components.propertyBar.interactionEventDialog.764059-9')"
              v-if="form.triggerType"
            >
              <el-table :data="formData.interInfo.queryResults" style="width: 100%" size="small" border>
                <el-table-column
                  prop="id"
                  :label="$t('scada.topo.components.propertyBar.interactionEventDialog.764059-10')"
                  align="center"
                />
                <el-table-column
                  prop="name"
                  :label="$t('scada.topo.components.propertyBar.interactionEventDialog.764059-11')"
                  align="center"
                />
              </el-table>
            </el-form-item>

            <div class="section-title">
              {{ $t('scada.topo.components.propertyBar.interactionEventDialog.764059-12') }}
            </div>

            <div class="flex-container">
              <el-form-item label="Params">
                <div v-if="form.params.length === 0">
                  {{ $t('scada.topo.components.propertyBar.interactionEventDialog.764059-13') }}
                </div>
                <div style="display: flex; flex-wrap: wrap; gap: 10px" v-else>
                  <div style="width: calc(50% - 5px)" v-for="(param, idx) in form.params" :key="idx">
                    <el-select
                      v-model="param.value"
                      :placeholder="$t('scada.topo.components.propertyBar.interactionEventDialog.764059-14')"
                    >
                      <el-option
                        v-for="item in formData.interInfo.queryResults"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id"
                      />
                    </el-select>
                    {{ param.id }}
                  </div>
                </div>
              </el-form-item>

              <el-form-item label="Header">
                <div v-if="form.headers.length === 0">
                  {{ $t('scada.topo.components.propertyBar.interactionEventDialog.764059-13') }}
                </div>
                <div style="display: flex; flex-wrap: wrap; gap: 10px" v-else>
                  <div style="width: calc(50% - 5px)" v-for="(header, idx) in form.headers" :key="idx">
                    <el-select
                      v-model="header.value"
                      :placeholder="$t('scada.topo.components.propertyBar.interactionEventDialog.764059-14')"
                    >
                      <el-option
                        v-for="item in formData.interInfo.queryResults"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id"
                      />
                    </el-select>
                    {{ header.id }}
                  </div>
                </div>
              </el-form-item>
            </div>
          </el-form>
        </div>
      </div>

      <div class="button-group">
        <el-button type="primary" @click="handleConfirm">{{ $t('confirm') }}</el-button>
        <el-button @click="handleClose">{{ $t('cancel') }}</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, reactive, watch } from 'vue';
import { storeToRefs } from 'pinia';
import { ElMessage, ElMessageBox } from 'element-plus';
import { InfoFilled, Plus, QuestionFilled, RemoveFilled } from '@element-plus/icons-vue';
import { useTopoEditorStore } from '@/stores/modules/topoEditor';

const { proxy } = getCurrentInstance() as any;
const topoEditorStore = useTopoEditorStore();
const { topoData, selected, pageIndex } = storeToRefs(topoEditorStore);

type InterParam = { id: string; value: string };
type InterItem = {
  triggerType: number | string | null;
  bindCompId: string;
  params: InterParam[];
  headers: InterParam[];
};
type InterInfo = {
  triggerTypes: Array<{ id: number | string; name: string }>;
  queryResults: Array<{ id: string; name: string }>;
};
type FormDataType = {
  interInfo: InterInfo;
  interList: InterItem[];
};

const props = defineProps<{
  visible: boolean;
  data: FormDataType;
}>();

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'confirm', value: FormDataType): void;
}>();

const formData = reactive<FormDataType>({
  interInfo: { triggerTypes: [], queryResults: [] },
  interList: [],
});

const visibleModel = computed({
  get: () => props.visible,
  set: (value: boolean) => emit('update:visible', value),
});

const bindCompList = computed(() => {
  const page = topoData.value?.[selected.value]?.[pageIndex.value];
  const components = page?.components || [];
  const httpPublicSetting = page?.httpPublicSetting || [];

  return components
    .filter((item: any) => item?.dataBind?.dataType === 4 || item?.dataBind?.dataType === 5)
    .map((item: any) => {
      const { identifier, name, dataBind } = item;
      const { dataType, httpSetting, httpSettingId } = dataBind || {};
      if (dataType === 4) {
        return { identifier, name, httpSetting, disabled: false };
      }
      const row = httpPublicSetting.find((it: any) => it.id === httpSettingId);
      return { identifier, name, httpSetting: row, disabled: false };
    });
});

watch(
  () => props.data,
  (newVal) => {
    const cloned = JSON.parse(JSON.stringify(newVal || { interInfo: {}, interList: [] }));
    formData.interInfo = cloned.interInfo || { triggerTypes: [], queryResults: [] };
    formData.interList = cloned.interList || [];
    initData();
  },
  { deep: true, immediate: true }
);

function initData() {
  bindCompList.value.forEach((item) => (item.disabled = false));

  const interList = formData.interList;
  if (interList.length !== 0) {
    interList.forEach((inter, index) => {
      bindCompList.value.forEach((row) => {
        const isHas = interList.some((it) => it.bindCompId === row.identifier);
        row.disabled = isHas;
      });

      const bindComp = bindCompList.value.find((it) => it.identifier === inter.bindCompId);
      const httpSetting = bindComp?.httpSetting || {};
      const params = httpSetting?.params || {};
      const headers = httpSetting?.headers || {};

      if (interList[index].params && interList[index].params.length !== 0) {
        interList[index].params = interList[index].params.filter((item) =>
          Object.keys(params).length !== 0 ? Object.keys(params).some((p) => p === item.id) : false
        );
      }

      if (interList[index].headers && interList[index].headers.length !== 0) {
        interList[index].headers = interList[index].headers.filter((item) =>
          Object.keys(headers).length !== 0 ? Object.keys(headers).some((h) => h === item.id) : false
        );
      }
    });
  }
}

function handleAddForm() {
  const page = topoData.value?.[selected.value]?.[pageIndex.value];
  const canAdd = (page?.components || []).some((item: any) => [4, 5].includes(item?.dataBind?.dataType));

  if (canAdd) {
    formData.interList.push({
      triggerType: null,
      bindCompId: '',
      params: [],
      headers: [],
    });
  } else {
    ElMessage.error(proxy.$t('scada.topo.components.propertyBar.interactionEventDialog.764059-16'));
  }
}

function handleBindCompChange(val: string, index: number) {
  const interList = formData.interList;

  bindCompList.value.forEach((row) => {
    const isHas = interList.some((item) => item.bindCompId === row.identifier);
    row.disabled = isHas;
  });

  const bindComp = bindCompList.value.find((item) => item.identifier === val);
  const httpSetting = bindComp?.httpSetting || {};
  const params = httpSetting?.params || {};
  const headers = httpSetting?.headers || {};

  interList[index].params =
    Object.keys(params).length !== 0 ? Object.keys(params).map((item) => ({ id: item, value: '' })) : [];

  interList[index].headers =
    Object.keys(headers).length !== 0 ? Object.keys(headers).map((item) => ({ id: item, value: '' })) : [];
}

function handleRemoveForm(index: number) {
  ElMessageBox.confirm(
    proxy.$t('scada.topo.components.propertyBar.interactionEventDialog.764059-17'),
    proxy.$t('template.index.891112-171')
  )
    .then(() => {
      const interList = formData.interList;
      bindCompList.value.forEach((item) => {
        if (item.identifier === interList[index].bindCompId && item.disabled) {
          item.disabled = false;
        }
      });
      interList.splice(index, 1);
    })
    .catch(() => {});
}

function handleConfirm() {
  const interList = formData.interList;
  let isError = false;

  interList.forEach((item) => {
    if (!item.triggerType) isError = true;
    if (!item.bindCompId) isError = true;
    if ((item.params || []).some((p) => p.value === '')) isError = true;
    if ((item.headers || []).some((h) => h.value === '')) isError = true;
  });

  if (interList.length === 0 || isError) {
    ElMessage.error(proxy.$t('scada.topo.components.propertyBar.interactionEventDialog.764059-19'));
    return;
  }

  ElMessage.success(proxy.$t('scada.topo.components.propertyBar.interactionEventDialog.764059-20'));
  emit('confirm', JSON.parse(JSON.stringify(formData)));
}

function handleClose() {
  emit('update:visible', false);
}
</script>

<style lang="scss" scoped>
.interaction-event-dialog {
  width: 100%;

  .form-card-wrap {
    max-height: 470px;
    overflow-y: auto;
    margin-top: 20px;

    .empty-state {
      height: 290px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      color: #909399;
      background-color: #f9f9f9;
      border-radius: 4px;
    }

    .form-card {
      background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
      border-radius: 4px;
      padding: 15px;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
      margin-top: 20px;

      &:first-child {
        margin-top: 0;
      }

      .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
        padding-bottom: 10px;
        border-bottom: 1px dashed #dcdfe6;

        .form-title {
          font-weight: bold;
          color: #486ff2;
        }
      }

      .section-title {
        font-weight: bold;
        color: #606266;
        margin-bottom: 10px;
      }

      .flex-container {
        display: flex;
        gap: 15px;
      }

      .flex-container .el-form-item {
        flex: 1;
      }

      .delete-btn {
        color: #f56c6c;
        cursor: pointer;
        font-size: 18px;
      }
    }
  }

  .button-group {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}
</style>
