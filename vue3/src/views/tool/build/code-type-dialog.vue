<template>
  <div>
    <el-dialog
      v-bind="$attrs"
      :model-value="visible"
      @update:modelValue="(val) => emit('update:visible', val)"
      width="500px"
      :close-on-click-modal="false"
      @open="onOpen"
      @close="onClose"
    >
      <el-row :gutter="15">
        <el-form ref="elFormRef" :model="formData" :rules="rules" size="default" label-width="100px">
          <el-col :span="24">
            <el-form-item :label="$t('build.index.2090840-12')" prop="type">
              <el-radio-group v-model="formData.type">
                <el-radio-button
                  v-for="(item, index) in typeOptions"
                  :key="index"
                  :label="item.value"
                  :disabled="item.disabled"
                >
                  {{ item.label }}
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="showFileName" :label="$t('build.index.2090840-13')" prop="fileName">
              <el-input v-model="formData.fileName" :placeholder="$t('build.index.2090840-14')" clearable />
            </el-form-item>
          </el-col>
        </el-form>
      </el-row>
      <template #footer>
        <el-button @click="close">{{ $t('cancel') }}</el-button>
        <el-button type="primary" @click="handleConfirm">{{ $t('confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps<{ visible?: boolean; showFileName?: boolean }>();
const emit = defineEmits(['update:visible', 'confirm']);

const elFormRef = ref();
const visible = computed(() => !!props.visible);
const formData = reactive<any>({
  fileName: undefined,
  type: 'file',
});
const rules = reactive<any>({
  fileName: [{ required: true, message: t('build.index.2090840-14'), trigger: 'blur' }],
  type: [{ required: true, message: t('build.index.2090840-15'), trigger: 'change' }],
});
const typeOptions = [
  { label: t('build.index.2090840-16'), value: 'file' },
  { label: t('build.index.2090840-17'), value: 'dialog' },
];

function onOpen() {
  if (props.showFileName) {
    formData.fileName = `${+new Date()}.vue`;
  }
}

function onClose() {}

function close() {
  emit('update:visible', false);
}

function handleConfirm() {
  elFormRef.value?.validate((valid: boolean) => {
    if (!valid) return;
    emit('confirm', { ...formData });
    close();
  });
}
</script>
