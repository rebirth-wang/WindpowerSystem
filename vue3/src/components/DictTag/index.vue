<template>
  <div class="dict-tag">
    <el-tag v-for="item in dictList" :key="item.value" :type="getTagType(item)" :effect="getValidEffect(effect)">
      {{ item.label }}
    </el-tag>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { PropType } from 'vue';

interface DictItem {
  value: string | number;
  label: string;
  listClass?: string | number;
}

const props = defineProps({
  options: {
    type: Array as PropType<DictItem[]>,
    default: () => [],
  },
  value: {
    type: [String, Number, Array],
    default: '',
  },
  effect: {
    type: String as PropType<'light' | 'dark' | 'plain'>,
    default: 'light',
  },
});

const dictList = computed(() => {
  if (!props.options || props.options.length === 0) return [];

  const values = Array.isArray(props.value) ? props.value : [props.value];
  // Use loose comparison (==) to handle string/number mismatch between dict values and data values
  return props.options.filter((item: DictItem) => values.some((v) => v == item.value));
});

const getTagType = (item: DictItem) => {
  const list = ['primary', 'success', 'info', 'warning', 'danger'] as const;
  type TagType = (typeof list)[number];

  // 如果 listClass 是字符串且是有效类型，直接返回
  if (typeof item.listClass === 'string' && list.includes(item.listClass as TagType)) {
    return item.listClass as TagType;
  }

  // 如果 listClass 是数字索引，转换为相应类型
  const index = Number(item.listClass);
  return !isNaN(index) && index >= 0 && index < list.length ? list[index] : 'primary';
};

const getValidEffect = (effect: string): 'light' | 'dark' | 'plain' => {
  if (['light', 'dark', 'plain'].includes(effect)) {
    return effect as 'light' | 'dark' | 'plain';
  }
  return 'light';
};
</script>

<style scoped>
.dict-tag {
  display: inline-block;
}
</style>
