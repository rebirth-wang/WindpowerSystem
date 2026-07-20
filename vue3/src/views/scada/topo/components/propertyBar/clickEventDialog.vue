<template>
  <div>
    <el-dialog
      class="click-event-dialog"
      :title="$t('scada.topo.components.propertyBar.clickEventDialog.318042-0')"
      v-model="visibleModel"
      width="600px"
      :before-close="handleClose"
      @opened="handleOpened"
    >
      <div class="event-form" v-loading="loading">
        <el-form label-width="100px">
          <el-form-item :label="$t('scada.topo.components.propertyBar.clickEventDialog.318042-1')">
            <div class="radio-group-wrap">
              <el-radio-group v-model="form.djType" @change="handleDjTypeChange">
                <el-radio :label="1">{{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-2') }}</el-radio>
                <el-radio :label="2">{{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-3') }}</el-radio>
                <el-radio v-if="scadaType !== 1" :label="3">
                  {{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-4') }}
                </el-radio>
                <el-radio v-if="form.comType === 'image-switch'" :label="4">
                  {{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-5') }}
                </el-radio>
                <el-radio v-if="scadaType !== 1" :label="5">
                  {{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-6') }}
                </el-radio>
              </el-radio-group>
            </div>
          </el-form-item>

          <el-form-item
            :label="$t('scada.topo.components.propertyBar.clickEventDialog.318042-7')"
            v-if="form.djType === 1 || form.djType === 4"
          >
            <el-input
              :placeholder="$t('scada.topo.components.propertyBar.clickEventDialog.318042-8')"
              v-model="form.modelName"
              readonly
              style="width: 400px"
            >
              <template #append>
                <el-button @click="handleVariateBind">{{ $t('select') }}</el-button>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item
            :label="$t('scada.topo.components.propertyBar.clickEventDialog.318042-28')"
            v-if="form.djType === 1"
          >
            <el-radio-group v-model="form.writeType" @change="handleWriteTypeChange">
              <el-radio :label="1">{{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-29') }}</el-radio>
              <el-radio :label="2">{{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-30') }}</el-radio>
              <el-radio :label="3">{{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-31') }}</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item
            :label="$t('scada.topo.components.propertyBar.clickEventDialog.318042-9')"
            v-if="form.djType === 1 && form.writeType === 1"
          >
            <el-input
              v-model="form.modelValue"
              :placeholder="$t('scada.topo.components.propertyBar.clickEventDialog.318042-10')"
              clearable
              style="width: 400px"
            />
          </el-form-item>

          <el-form-item
            :label="$t('scada.topo.components.propertyBar.clickEventDialog.318042-11')"
            v-if="form.djType === 1 && form.writeType === 2"
          >
            <el-input
              v-model="form.modelTip"
              :placeholder="$t('scada.topo.components.propertyBar.clickEventDialog.318042-12')"
              clearable
              style="width: 400px"
            />
          </el-form-item>

          <el-form-item class="fun-input" v-if="form.djType === 1 && form.writeType === 3">
            <div class="title">{{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-32') }}</div>
            <monaco-editor
              ref="editorRef"
              height="200px"
              :value="form.funValue || ''"
              :isMinimap="false"
              :isQuickSuggestions="false"
              style="width: 400px"
              @change="handleEditorChange"
            />
          </el-form-item>

          <el-form-item
            :label="$t('scada.topo.components.propertyBar.clickEventDialog.318042-13')"
            v-if="form.djType === 2"
          >
            <el-input
              v-model="form.externalLink"
              :placeholder="$t('scada.topo.components.propertyBar.clickEventDialog.318042-14')"
              clearable
              style="width: 400px"
            />
          </el-form-item>

          <el-form-item
            :label="$t('scada.topo.components.propertyBar.clickEventDialog.318042-4')"
            v-if="form.djType === 3"
          >
            <el-input
              :placeholder="$t('scada.topo.components.propertyBar.clickEventDialog.318042-15')"
              v-model="form.scadaName"
              readonly
              style="width: 400px"
            >
              <template #append>
                <el-button @click="openScadaListDialog">{{ $t('select') }}</el-button>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item
            :label="$t('scada.topo.components.propertyBar.clickEventDialog.318042-6')"
            v-if="form.djType === 5"
          >
            <el-select
              v-model="form.scadaIndex"
              :placeholder="$t('scada.topo.components.propertyBar.clickEventDialog.318042-27')"
              @change="handleScadaIndexSelectChange"
              style="width: 400px"
            >
              <el-option
                v-for="(item, index) in currentTopoList"
                :key="item.name + index"
                :label="item.name"
                :value="index"
              />
            </el-select>
          </el-form-item>

          <el-form-item
            :label="$t('scada.topo.components.propertyBar.clickEventDialog.318042-16')"
            v-if="form.djType === 3 || form.djType === 5"
          >
            <el-radio-group v-model="form.openModel">
              <el-radio :label="1">{{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-17') }}</el-radio>
              <el-radio :label="2">{{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-18') }}</el-radio>
              <el-radio :label="3">{{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-19') }}</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item
            :label="$t('scada.topo.components.propertyBar.clickEventDialog.318042-20')"
            v-if="(form.djType === 3 || form.djType === 5) && form.openModel === 3"
          >
            <el-input
              v-model.number="form.windowWidth"
              :placeholder="$t('scada.topo.components.propertyBar.clickEventDialog.318042-21')"
              clearable
              style="width: 400px"
            />
          </el-form-item>

          <el-form-item
            :label="$t('scada.topo.components.propertyBar.clickEventDialog.318042-22')"
            v-if="(form.djType === 3 || form.djType === 5) && form.openModel === 3"
          >
            <el-input
              v-model.number="form.windowHeight"
              :placeholder="$t('scada.topo.components.propertyBar.clickEventDialog.318042-23')"
              clearable
              style="width: 400px"
            />
          </el-form-item>

          <el-form-item
            :label="$t('scada.topo.components.propertyBar.clickEventDialog.318042-24')"
            v-if="form.djType === 4"
          >
            <el-radio-group v-model="form.switchControl">
              <el-radio :label="1">{{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-25') }}</el-radio>
              <el-radio :label="2">{{ $t('scada.topo.components.propertyBar.clickEventDialog.318042-26') }}</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item :label="$t('topo.components.propertyBar.index.038495-403')">
            <el-switch v-model="form.passwordEnabled" size="small" />
          </el-form-item>

          <el-form-item v-if="form.passwordEnabled" :label="$t('topo.components.propertyBar.index.038495-405')">
            <el-radio-group v-model="form.passwordType">
              <el-radio label="user">{{ $t('topo.components.propertyBar.index.038495-304') }}</el-radio>
              <el-radio label="custom">{{ $t('topo.components.propertyBar.index.038495-303') }}</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item
            v-if="form.passwordEnabled && form.passwordType === 'custom'"
            :label="$t('topo.components.propertyBar.index.038495-222')"
          >
            <el-input
              v-model="form.customPassword"
              :placeholder="$t('topo.components.propertyBar.index.038495-404')"
              clearable
              type="password"
              show-password
              style="width: 400px"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button type="primary" @click="handleConfirm">{{ $t('confirm') }}</el-button>
        <el-button @click="handleClose">{{ $t('cancel') }}</el-button>
      </template>
    </el-dialog>

    <scada-dialog
      v-model:visible="isScadaListDialog"
      :scada-type="scadaType"
      :selected-guid="form.scadaGuid"
      @confirm="handleScadaSelect"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, reactive, ref, watch, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import { storeToRefs } from 'pinia';
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import ScadaDialog from './scadaDialog.vue';
import { getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();
const topoEditorStore = useTopoEditorStore();
const { topoData, selected } = storeToRefs(topoEditorStore);

const props = defineProps<{
  visible: boolean;
  data: Record<string, any>;
}>();

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'confirm', value: any): void;
  (e: 'variateBind'): void;
}>();

const loading = ref(false);
const scadaType = ref(getScadaRouteType(route.query));
const form = reactive<any>({});
const isScadaListDialog = ref(false);
const editorRef = ref<any>(null);

const visibleModel = computed({
  get: () => props.visible,
  set: (v: boolean) => emit('update:visible', v),
});

const currentTopoList = computed(() => {
  const s = selected.value;
  return topoData.value?.[s] || [];
});

watch(
  () => props.data,
  (newVal) => {
    Object.keys(form).forEach((k) => delete form[k]);
    Object.assign(form, { ...(newVal || {}) });
  },
  { deep: true, immediate: true }
);

function handleVariateBind() {
  emit('variateBind');
}

function openScadaListDialog() {
  isScadaListDialog.value = true;
}

function handleScadaSelect(row: any) {
  const { guid, pageName, sceneModelId } = row || {};
  form.scadaGuid = guid;
  form.scadaName = pageName;
  form.sceneModelId = sceneModelId;
  isScadaListDialog.value = false;
}

function handleScadaIndexSelectChange(val: number) {
  const guid = getRouteQueryString(route.query, 'guid');
  const sceneModelId = getRouteQueryString(route.query, 'sceneModelId');
  const scada = currentTopoList.value?.[val];
  form.scadaGuid = guid;
  form.scadaIndex = val;
  form.scadaIndexName = scada?.name;
  form.sceneModelId = sceneModelId;
}

/** 将 form.funValue 同步到 Monaco（需在编辑器 v-if 挂载后调用） */
function syncFunValueEditor() {
  if (form.djType !== 1 || form.writeType !== 3) return;
  nextTick(() => {
    editorRef.value?.setValue(form.funValue ?? '');
  });
}

function handleOpened() {
  syncFunValueEditor();
}

function handleDjTypeChange(val: number) {
  if (val === 3) {
    form.scadaGuid = '';
    form.scadaName = '';
    form.sceneModelId = '';
  }
  if (val === 5) {
    form.scadaGuid = '';
    form.scadaIndex = '';
    form.scadaIndexName = '';
    form.sceneModelId = '';
  }
  syncFunValueEditor();
}

function handleWriteTypeChange(val: number) {
  if (val === 3) {
    syncFunValueEditor();
  }
}

function handleEditorChange(data: string) {
  form.funValue = data;
}

function handleConfirm() {
  emit('confirm', JSON.parse(JSON.stringify(form)));
}

function handleClose() {
  emit('update:visible', false);
}
</script>

<style lang="scss" scoped>
.click-event-dialog {
  width: 100%;

  .event-form {
    width: 100%;

    .radio-group-wrap {
      width: 100%;

      :deep(.el-radio) {
        padding: 10px 0;
      }
    }

    .fun-input {
      width: 100%;

      .title {
        height: 20px;
        width: 400px;
        line-height: 16px;
        border: 1px solid rgba(0, 0, 0, 0.298);
        font-size: 13px;
      }
    }
  }
}
</style>
