<template>
  <div class="things-model-input">
    <!-- 布尔类型 -->
    <el-switch
      v-if="isBoolean"
      v-model="modelValue"
      :active-value="1"
      :inactive-value="0"
      :size="size"
    />

    <!-- 枚举类型 -->
    <el-select
      v-else-if="isEnum"
      v-model="modelValue"
      :placeholder="$t('select')"
      :size="size"
      style="width: 100%"
    >
      <el-option
        v-for="opt in options"
        :key="opt.value"
        :label="opt.label"
        :value="opt.value"
      />
    </el-select>

    <!-- 范围操作符 - 显示两个输入框 -->
    <template v-else-if="isRangeOperator">
      <el-row :gutter="8">
        <el-col :span="11">
          <el-input
            v-model="valueARef"
            :placeholder="$t('min')"
            :size="size"
            @change="handleRangeChange"
          />
        </el-col>
        <el-col :span="2" style="text-align: center">~</el-col>
        <el-col :span="11">
          <el-input
            v-model="valueBRef"
            :placeholder="$t('max')"
            :size="size"
            @change="handleRangeChange"
          />
        </el-col>
      </el-row>
    </template>

    <!-- 数值类型 -->
    <el-input-number
      v-else-if="isNumber"
      v-model="modelValue"
      :size="size"
      :precision="precision"
      :step="step"
      style="width: 100%"
      controls-position="right"
    />

    <!-- 默认文本输入 -->
    <el-input
      v-else
      v-model="modelValue"
      :placeholder="$t('input')"
      :size="size"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';

interface DataType {
  type: string;
  specs?: {
    min?: number;
    max?: number;
    step?: number;
    enumList?: { value: string; label: string }[];
  };
}

interface Model {
  datatype: DataType;
}

const props = defineProps<{
  modelValue?: any;
  valueA?: any;
  valueB?: any;
  model: Model;
  operator?: string;
  size?: 'large' | 'default' | 'small';
}>();

const emit = defineEmits<{
  'update:modelValue': [val: any];
  'update:valueA': [val: any];
  'update:valueB': [val: any];
  'range-change': [val: { valueA: any; valueB: any }];
}>();

const size = computed(() => props.size || 'default');

const dataType = computed(() => props.model?.datatype?.type || 'string');

const isBoolean = computed(() => dataType.value === 'bool');

const isEnum = computed(() => dataType.value === 'enum');

const isNumber = computed(() => ['integer', 'decimal', 'int', 'float', 'double'].includes(dataType.value));

const options = computed(() => {
  return props.model?.datatype?.specs?.enumList || [];
});

const precision = computed(() => {
  return dataType.value === 'integer' || dataType.value === 'int' ? 0 : 2;
});

const step = computed(() => {
  return precision.value === 0 ? 1 : 0.1;
});

// 范围操作符：between, notBetween
const isRangeOperator = computed(() => {
  return ['between', 'notBetween'].includes(props.operator || '');
});

const modelValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

const valueARef = ref(props.valueA);
const valueBRef = ref(props.valueB);

watch(() => props.valueA, (val) => {
  valueARef.value = val;
});

watch(() => props.valueB, (val) => {
  valueBRef.value = val;
});

function handleRangeChange() {
  emit('update:valueA', valueARef.value);
  emit('update:valueB', valueBRef.value);
  emit('range-change', { valueA: valueARef.value, valueB: valueBRef.value });
}
</script>

<style lang="scss" scoped>
.things-model-input {
  width: 100%;
}
</style>
