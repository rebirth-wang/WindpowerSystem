<template>
  <div class="line-wrap">
    <el-form :model="curLine" label-width="60px" size="small">
      <el-form-item label="">
        <template #label>
          <span>
            <el-tooltip :content="$t('ruleengine.editor.components.form.flow-line.807357-8')" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </span>
          {{ $t('ruleengine.editor.components.form.flow-line.807357-0') }}
        </template>

        <el-select
          style="width: 100%"
          v-model="curLine.type"
          :placeholder="$t('pleaseSelect')"
          @change="changeType"
          clearable
        >
          <el-option
            v-for="item in processedTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
            :disabled="item.disabled"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item
        :label="$t('ruleengine.editor.components.form.flow-line.807357-1')"
        v-if="curLine.type === 'to'"
        :rules="numberRules"
      >
        <el-input v-model.number="curLine.value" type="number" clearable @input="handleNumberInput"></el-input>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRuleEditorStore } from '@/stores/modules/ruleEditor';
import { storeToRefs } from 'pinia';
import { QuestionFilled } from '@element-plus/icons-vue';

const { t } = useI18n();
const ruleEditorStore = useRuleEditorStore();
const { curLine } = storeToRefs(ruleEditorStore);

const typeOptions = [
  { value: 'common', label: t('ruleengine.editor.components.form.flow-line.807357-2') },
  { value: 'to', label: t('ruleengine.editor.components.form.flow-line.807357-3') },
  { value: 'true', label: t('ruleengine.editor.components.form.flow-line.807357-4') },
  { value: 'false', label: t('ruleengine.editor.components.form.flow-line.807357-5') },
];

const numberRules = [
  { required: true, message: t('ruleengine.editor.components.form.flow-line.807357-6'), trigger: 'blur' },
  {
    type: 'number',
    min: 1,
    max: 9999,
    message: t('ruleengine.editor.components.form.flow-line.807357-7'),
    trigger: 'blur',
  },
];

const isFromValid = computed(() => {
  return curLine.value.from && curLine.value.from.includes('condition_');
});

// 处理选项的禁用状态
const processedTypeOptions = computed(() => {
  return typeOptions.map((item) => {
    if (isFromValid.value) {
      return { ...item, disabled: item.value === 'common' };
    } else {
      return { ...item, disabled: item.value !== 'common' };
    }
  });
});

const changeType = (val: string) => {
  curLine.value.value = null;
  if (val === 'true' || val === 'false') {
    curLine.value.label = val;
  }
};

const handleNumberInput = (value: any) => {
  if (value === null || value === '') return;

  let str = value.toString();
  str = str.replace(/[^\d]/g, '');

  if (str.length > 4) {
    str = str.slice(0, 4);
  }
  curLine.value.label = str;

  let num = parseInt(str, 10) || 1;
  num = Math.min(Math.max(num, 1), 9999);

  nextTick(() => {
    curLine.value.value = num;
  });
};
</script>

<style lang="scss" scoped>
.line-wrap {
  width: 100%;
}
</style>
