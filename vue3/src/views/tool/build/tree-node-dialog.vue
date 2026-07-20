<template>
  <div>
    <el-dialog v-bind="$attrs" :close-on-click-modal="false" @open="onOpen" @close="onClose">
      <el-row :gutter="0">
        <el-form ref="elFormRef" :model="formData" :rules="rules" size="small" label-width="100px">
          <el-col :span="24">
            <el-form-item :label="$t('build.index.2090840-101')" prop="label">
              <el-input v-model="formData.label" :placeholder="$t('build.tree.897735-0')" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="$t('build.index.2090840-102')" prop="value">
              <el-input v-model="formData.value" :placeholder="$t('build.tree.897735-1')" clearable>
                <template #append>
                  <el-select v-model="dataType" :style="{ width: '100px' }">
                    <el-option
                      v-for="(item, index) in dataTypeOptions"
                      :key="index"
                      :label="item.label"
                      :value="item.value"
                      :disabled="item.disabled"
                    />
                  </el-select>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-form>
      </el-row>
      <template #footer>
        <el-button type="primary" @click="handleConfirm">{{ $t('confirm') }}</el-button>
        <el-button @click="close">{{ $t('cancel') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { isNumberStr } from '@/utils/index';

const { t } = useI18n();
const emit = defineEmits(['update:visible', 'commit']);

const elFormRef = ref();
let id = 100;
const formData = reactive<any>({
  label: undefined,
  value: undefined,
});
const rules = reactive<any>({
  label: [{ required: true, message: t('build.tree.897735-0'), trigger: 'blur' }],
  value: [{ required: true, message: t('build.tree.897735-1'), trigger: 'blur' }],
});
const dataType = ref('string');
const dataTypeOptions = [
  { label: t('build.tree.897735-2'), value: 'string' },
  { label: t('build.tree.897735-3'), value: 'number' },
];

watch(
  () => formData.value,
  (val) => {
    dataType.value = isNumberStr(val) ? 'number' : 'string';
  }
);

function onOpen() {
  formData.label = undefined;
  formData.value = undefined;
}

function onClose() {}

function close() {
  emit('update:visible', false);
}

function handleConfirm() {
  elFormRef.value?.validate((valid: boolean) => {
    if (!valid) return;
    if (dataType.value === 'number') {
      formData.value = parseFloat(formData.value);
    }
    formData.id = id++;
    emit('commit', { ...formData });
    close();
  });
}
</script>
