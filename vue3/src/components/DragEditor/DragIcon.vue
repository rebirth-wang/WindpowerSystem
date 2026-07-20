<template>
  <el-icon :size="size" :color="color">
    <component :is="iconComponent" />
  </el-icon>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import * as ElementPlusIcons from '@element-plus/icons-vue';

const props = withDefaults(
  defineProps<{
    name?: string;
    size?: number | string;
    color?: string;
  }>(),
  {
    name: 'Edit',
    size: 16,
    color: undefined,
  }
);

const legacyIconMap: Record<string, string> = {
  'el-icon-arrow-left': 'ArrowLeft',
  'el-icon-bank-card': 'CreditCard',
  'el-icon-caret-left': 'CaretLeft',
  'el-icon-data-line': 'DataLine',
  'el-icon-document': 'Document',
  'el-icon-edit': 'Edit',
  'el-icon-menu': 'Menu',
  'el-icon-open': 'Open',
  'el-icon-postcard': 'Postcard',
  'el-icon-price-tag': 'PriceTag',
  'el-icon-s-operation': 'Operation',
  'el-icon-s-tools': 'Tools',
  'el-icon-setting': 'Setting',
  'el-icon-view': 'View',
};

const iconComponent = computed(() => {
  const iconName = legacyIconMap[props.name] || props.name || 'Edit';
  return (ElementPlusIcons as any)[iconName] || ElementPlusIcons.Edit;
});
</script>
