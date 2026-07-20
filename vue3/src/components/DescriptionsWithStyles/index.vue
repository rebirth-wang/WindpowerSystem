<template>
  <div ref="wrapRef" class="descriptions-with-styles">
    <el-descriptions v-bind="$attrs">
      <template v-for="(_slot, name) in $slots" :key="name" #[name]="slotScope">
        <slot :name="name" v-bind="slotScope ?? {}" />
      </template>
    </el-descriptions>
  </div>
</template>

<script setup>
/**
 * Element Plus 的 el-descriptions 不再提供 label-style / content-style，
 * 此组件对齐 Element UI 用法，将样式应用到 .el-descriptions__label / .el-descriptions__content。
 */
import { ref, watch, onMounted, onUpdated, nextTick } from 'vue';

defineOptions({
  inheritAttrs: false,
  name: 'DescriptionsWithStyles',
});

const props = defineProps({
  /** 与 Element UI label-style 一致：作用于标签单元 */
  labelStyle: {
    type: Object,
    default: null,
  },
  /** 与 Element UI content-style 一致：作用于内容单元 */
  contentStyle: {
    type: Object,
    default: null,
  },
});

const wrapRef = ref(null);

/** 数字默认补 px（与 Vue style 绑定常见规则一致）；下列属性不补单位 */
const NO_UNIT_KEYS = new Set([
  'opacity',
  'zIndex',
  'fontWeight',
  'lineHeight',
  'flex',
  'flexGrow',
  'flexShrink',
  'order',
  'fillOpacity',
  'strokeOpacity',
]);

function normalizeCSSValue(key, val) {
  if (val == null) return null;
  if (typeof val === 'number') {
    if (NO_UNIT_KEYS.has(key)) return String(val);
    return `${val}px`;
  }
  return String(val);
}

function assignDomStyle(el, styleObj) {
  if (!styleObj) return;
  for (const key of Object.keys(styleObj)) {
    const v = normalizeCSSValue(key, styleObj[key]);
    if (v != null) el.style[key] = v;
  }
}

function applyCellStyles() {
  const root = wrapRef.value;
  if (!root) return;
  if (props.labelStyle) {
    root.querySelectorAll('.el-descriptions__label').forEach((el) => assignDomStyle(el, props.labelStyle));
  }
  if (props.contentStyle) {
    root.querySelectorAll('.el-descriptions__content').forEach((el) => assignDomStyle(el, props.contentStyle));
  }
}

function scheduleApply() {
  nextTick(() => applyCellStyles());
}

watch(
  () => [props.labelStyle, props.contentStyle],
  () => scheduleApply(),
  { deep: true }
);

onMounted(scheduleApply);
onUpdated(scheduleApply);
</script>
