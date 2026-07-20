<template>
  <div class="product-extension-param">
    <el-button type="primary" plain :icon="Refresh" @click="getList" style="float: right">
      {{ $t('product.product-extension-param.343054-7') }}
    </el-button>

    <el-table :data="list" v-loading="loading" :border="false" row-key="rowKey">
      <el-table-column prop="paramName" :label="$t('product.product-extension-param.343054-0')" min-width="160" />
      <el-table-column prop="paramType" :label="$t('product.product-extension-param.343054-1')" min-width="120">
        <template #default="scope">{{ getTypeLabel(scope.row.paramType) }}</template>
      </el-table-column>
      <el-table-column prop="defaultValue" :label="$t('product.product-extension-param.343054-2')" min-width="160" />
      <el-table-column prop="isEnabled" width="120">
        <template #header>
          <span>
            {{ $t('product.product-extension-param.343054-3') }}
            <el-tooltip :content="$t('product.product-extension-param.343054-4')" placement="top">
              <el-icon class="enable-tip"><QuestionFilled /></el-icon>
            </el-tooltip>
          </span>
        </template>
        <template #default="scope">
          <el-switch
            v-model="scope.row.isEnabled"
            :active-value="1"
            :inactive-value="0"
            :disabled="productInfo.status != 1"
            @change="onQuickSwitchChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        prop="description"
        :label="$t('product.product-extension-param.343054-5')"
        min-width="220"
        show-overflow-tooltip
      />
      <el-table-column :label="$t('product.product-extension-param.343054-6')" width="150" fixed="right">
        <template #default="scope">
          <el-button link size="small" @click="openEditDialog(scope.row)" v-if="productInfo.status == 1">
            {{ $t('product.product-extension-param.343054-8') }}
          </el-button>
          <el-button link size="small" @click="deleteParam(scope.row)" v-if="productInfo.status == 1">
            {{ $t('product.product-extension-param.343054-9') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="bottom-add" v-if="productInfo.status == 1">
      <el-dropdown trigger="click" @command="onAddTypeSelect">
        <el-button type="primary" plain>
          {{ $t('product.product-extension-param.343054-10') }}
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-for="d in dictList" :key="d.value" :command="d.value">{{ d.label }}</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <el-dialog
      v-model="dialogOpen"
      :title="
        dialogForm.paramId
          ? $t('product.product-extension-param.343054-11')
          : $t('product.product-extension-param.343054-12')
      "
      width="580px"
      append-to-body
    >
      <el-form ref="dialogFormRef" :model="dialogForm" :rules="rules" label-width="100px">
        <el-form-item :label="$t('product.product-extension-param.343054-13')" prop="paramName">
          <el-input
            v-model="dialogForm.paramName"
            :placeholder="$t('product.product-extension-param.343054-14')"
            style="width: 400px"
          />
        </el-form-item>

        <template v-if="dialogForm.paramType === 'number'">
          <el-row :gutter="10" class="number-two-col">
            <el-col :span="12">
              <el-form-item :label="$t('product.product-extension-param.343054-15')">
                <el-input
                  v-model="dialogSpec.unit"
                  :placeholder="$t('product.product-extension-param.343054-14')"
                  style="width: 120px"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('product.product-extension-param.343054-2')">
                <el-input-number
                  v-model="dialogNumDefault"
                  :precision="dialogPrecision"
                  :placeholder="$t('product.product-extension-param.343054-14')"
                  style="width: 120px"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('product.product-extension-param.343054-17')">
                <el-input-number v-model="dialogSpec.min" :precision="dialogPrecision" style="width: 120px" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('product.product-extension-param.343054-18')">
                <el-input-number v-model="dialogSpec.max" :precision="dialogPrecision" style="width: 120px" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <template v-else-if="dialogForm.paramType === 'switch'">
          <el-form-item :label="$t('product.product-extension-param.343054-2')">
            <el-select v-model="dialogForm.defaultValue" style="width: 400px">
              <el-option label="0" value="0" />
              <el-option label="1" value="1" />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('product.product-extension-param.343054-19')">
            <div class="switch-text-row">
              <el-input
                v-model="dialogSpec.falseText"
                :placeholder="$t('product.product-extension-param.343054-21')"
                style="width: 400px"
              />
            </div>
          </el-form-item>
          <el-form-item :label="$t('product.product-extension-param.343054-20')">
            <div class="switch-text-row">
              <el-input
                v-model="dialogSpec.trueText"
                :placeholder="$t('product.product-extension-param.343054-22')"
                style="width: 400px"
              />
            </div>
          </el-form-item>
        </template>

        <template v-else-if="dialogForm.paramType === 'select'">
          <el-form-item :label="$t('product.product-extension-param.343054-2')">
            <el-select v-model="dialogForm.defaultValue" style="width: 400px">
              <el-option v-for="(op, i) in dialogSpec.option" :key="i" :label="op.label" :value="op.value" />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('product.product-extension-param.343054-23')">
            <div style="width: 400px">
              <div v-for="(op, i) in dialogSpec.option" :key="i" class="option-row">
                <el-input v-model="op.label" :placeholder="$t('product.product-extension-param.343054-24')" />
                <el-input v-model="op.value" :placeholder="$t('product.product-extension-param.343054-25')" />
                <el-button link @click="dialogSpec.option.splice(i, 1)">
                  {{ $t('product.product-extension-param.343054-9') }}
                </el-button>
              </div>
              <el-button size="small" @click="dialogSpec.option.push({ label: '', value: '' })">
                {{ $t('product.product-extension-param.343054-26') }}
              </el-button>
            </div>
          </el-form-item>
        </template>

        <template v-else-if="dialogForm.paramType === 'date'">
          <el-form-item :label="$t('product.product-extension-param.343054-2')">
            <el-date-picker
              v-model="dialogForm.defaultValue"
              type="date"
              value-format="YYYY-MM-DD"
              :placeholder="$t('product.product-extension-param.343054-27')"
              style="width: 400px"
            />
          </el-form-item>
        </template>

        <template v-else-if="dialogForm.paramType === 'time'">
          <el-form-item :label="$t('product.product-extension-param.343054-2')">
            <el-time-picker
              v-model="dialogForm.defaultValue"
              value-format="HH:mm:ss"
              :placeholder="$t('product.product-extension-param.343054-27')"
              style="width: 400px"
            />
          </el-form-item>
        </template>

        <template v-else>
          <el-form-item :label="$t('product.product-extension-param.343054-2')">
            <el-input
              v-model="dialogForm.defaultValue"
              :placeholder="$t('product.product-extension-param.343054-14')"
              style="width: 400px"
            />
          </el-form-item>
        </template>

        <el-form-item prop="isAppShow">
          <template #label>
            <span>
              {{ $t('product.product-extension-param.343054-28') }}
              <el-tooltip :content="$t('product.product-extension-param.343054-29')" placement="top">
                <el-icon class="enable-tip"><QuestionFilled /></el-icon>
              </el-tooltip>
            </span>
          </template>
          <el-switch v-model="dialogForm.isAppShow" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item :label="$t('product.product-extension-param.343054-3')">
          <el-switch v-model="dialogForm.isEnabled" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item :label="$t('product.product-extension-param.343054-5')">
          <el-input
            type="textarea"
            :rows="3"
            v-model="dialogForm.description"
            :placeholder="$t('product.product-extension-param.343054-30')"
            style="width: 400px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitDialog">
          {{ $t('product.product-extension-param.343054-31') }}
        </el-button>
        <el-button @click="dialogOpen = false">
          {{ $t('product.product-extension-param.343054-32') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, getCurrentInstance } from 'vue';
import { useI18n } from 'vue-i18n';
import {
  listProductExtendParams,
  addProductExtendParam,
  updateProductExtendParam,
  delProductExtendParam,
} from '@/api/iot/extendParam';
import { useDict } from '@/utils/dict/useDict';
import { Refresh, QuestionFilled } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance();
const { t } = useI18n();
const props = defineProps({ product: { type: Object, default: () => ({}) } });
const { product_ext_param_type } = useDict('product_ext_param_type');

const list = ref([]);
const loading = ref(false);
const productInfo = ref({});
const dialogOpen = ref(false);
const dialogFormRef = ref();
const dialogNumDefault = ref(0);
const dialogSpec = reactive({
  unit: '',
  min: 0,
  max: 100,
  mode: 'int',
  trueText: t('product.product-extension-param.343054-37'),
  falseText: t('product.product-extension-param.343054-38'),
  option: [],
});

const dialogForm = reactive({
  paramId: undefined,
  productId: undefined,
  paramName: '',
  paramType: 'text',
  defaultValue: '',
  isEnabled: 1,
  isAppShow: 0,
  description: '',
});

const rules = {
  paramName: [
    {
      required: true,
      message: t('product.product-extension-param.343054-14'),
      trigger: 'blur',
    },
  ],
};
const dictList = computed(() => (Array.isArray(product_ext_param_type?.value) ? product_ext_param_type.value : []));
const dialogPrecision = computed(() => (dialogSpec.mode === 'decimal' ? 2 : 0));

function toNumberOrNull(value) {
  if (value === null || value === undefined || value === '') return null;
  const numberValue = Number(value);
  return Number.isNaN(numberValue) ? null : numberValue;
}

function toNumberOrDefault(value, defaultValue = 0) {
  return toNumberOrNull(value) ?? defaultValue;
}

watch(
  () => props.product,
  (v) => {
    productInfo.value = v || {};
    if (v?.productId) getList();
  },
  { immediate: true }
);

function getTypeLabel(v) {
  return dictList.value.find((x) => String(x.value) === String(v))?.label || v;
}
function parseSpec(r) {
  try {
    return r.spec ? JSON.parse(r.spec) : {};
  } catch {
    return {};
  }
}

function getList() {
  loading.value = true;
  listProductExtendParams(props.product.productId)
    .then((res) => {
      list.value = (res.rows || res.data || []).map((x, i) => ({ ...x, rowKey: `old_${x.paramId || x.id || i}` }));
      loading.value = false;
    })
    .catch(() => (loading.value = false));
}

function resetDialog(type = 'text') {
  dialogForm.paramId = undefined;
  dialogForm.productId = props.product.productId;
  dialogForm.paramName = '';
  dialogForm.paramType = type;
  dialogForm.defaultValue = '';
  dialogForm.isEnabled = 1;
  dialogForm.isAppShow = 0;
  dialogForm.description = '';
  dialogNumDefault.value = 0;
  dialogSpec.unit = '';
  dialogSpec.min = 0;
  dialogSpec.max = 100;
  dialogSpec.mode = 'int';
  dialogSpec.trueText = t('product.product-extension-param.343054-37');
  dialogSpec.falseText = t('product.product-extension-param.343054-38');
  dialogSpec.option = [];
}

function onAddTypeSelect(type) {
  resetDialog(type);
  dialogOpen.value = true;
}

function openEditDialog(row) {
  resetDialog(row.paramType);
  dialogForm.paramId = row.paramId;
  dialogForm.paramName = row.paramName;
  dialogForm.paramType = row.paramType;
  dialogForm.defaultValue = row.defaultValue;
  dialogForm.isEnabled = row.isEnabled;
  dialogForm.isAppShow = row.isAppShow;
  dialogForm.description = row.description;
  const s = parseSpec(row);
  if (row.paramType === 'number') {
    dialogSpec.unit = s.unit || '';
    dialogSpec.min = toNumberOrDefault(s.min, 0);
    dialogSpec.max = toNumberOrDefault(s.max, 100);
    dialogSpec.mode = s.mode || 'int';
    dialogNumDefault.value = toNumberOrDefault(row.defaultValue, 0);
  } else if (row.paramType === 'switch') {
    dialogSpec.trueText = s.trueText || t('product.product-extension-param.343054-37');
    dialogSpec.falseText = s.falseText || t('product.product-extension-param.343054-38');
  } else if (row.paramType === 'select') {
    dialogSpec.option = Array.isArray(s.option) ? s.option : [];
  }
  dialogOpen.value = true;
}

function buildSpec() {
  if (dialogForm.paramType === 'number')
    return JSON.stringify({
      unit: dialogSpec.unit,
      min: toNumberOrDefault(dialogSpec.min, 0),
      max: toNumberOrDefault(dialogSpec.max, 100),
      mode: dialogSpec.mode,
    });
  if (dialogForm.paramType === 'switch')
    return JSON.stringify({ trueText: dialogSpec.trueText, falseText: dialogSpec.falseText });
  if (dialogForm.paramType === 'select') return JSON.stringify({ option: dialogSpec.option });
  return '';
}

function submitDialog() {
  dialogFormRef.value.validate((ok) => {
    if (!ok) return;
    const payload = {
      paramId: dialogForm.paramId,
      productId: dialogForm.productId,
      paramName: dialogForm.paramName,
      paramType: dialogForm.paramType,
      defaultValue: dialogForm.paramType === 'number' ? String(dialogNumDefault.value ?? 0) : dialogForm.defaultValue,
      isEnabled: dialogForm.isEnabled,
      isAppShow: dialogForm.isAppShow,
      description: dialogForm.description,
      spec: buildSpec(),
    };
    const req = payload.paramId ? updateProductExtendParam(payload) : addProductExtendParam(payload);
    req.then(() => {
      proxy.$modal.msgSuccess(t('product.product-extension-param.343054-33'));
      dialogOpen.value = false;
      getList();
    });
  });
}

function onQuickSwitchChange(row) {
  const payload = {
    paramId: row.paramId,
    productId: row.productId || props.product.productId,
    paramName: row.paramName,
    paramType: row.paramType,
    defaultValue: row.defaultValue,
    isEnabled: row.isEnabled,
    isAppShow: row.isAppShow,
    description: row.description,
    spec: row.spec || '',
  };
  updateProductExtendParam(payload)
    .then(() => {
      proxy.$modal.msgSuccess(t('product.product-extension-param.343054-34'));
    })
    .catch(() => {
      row.isEnabled = row.isEnabled === 1 ? 0 : 1;
    });
}

function deleteParam(row) {
  proxy.$modal.confirm(t('product.product-extension-param.343054-35')).then(() => {
    delProductExtendParam(row.paramId).then(() => {
      proxy.$modal.msgSuccess(t('product.product-extension-param.343054-36'));
      getList();
    });
  });
}

defineExpose({ getList });
</script>

<style scoped>
.bottom-add {
  margin-top: 12px;
}

.number-two-col {
  :deep(.el-form-item__content) {
    margin-left: 0 !important;
  }
  :deep(.el-input),
  :deep(.el-input-number) {
    width: 120px !important;
  }
}

.option-row {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 8px;
  margin-bottom: 8px;
}
.switch-text-row {
  display: grid;
  grid-template-columns: 220px 1fr;
  align-items: center;
  column-gap: 12px;
}
.switch-text-tip {
  color: #909399;
  font-size: 13px;
  white-space: nowrap;
}
</style>
