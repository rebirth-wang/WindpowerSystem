<template>
  <component :is="type" v-bind="linkProps(to)">
    <slot />
  </component>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { isExternal } from '@/utils/validate';

const props = defineProps({
  to: {
    type: [String, Object],
    required: true,
  },
  target: {
    type: [String, Number],
    default: 0,
  },
});

const isExternalLink = computed(() => {
  return typeof props.to === 'string' && isExternal(props.to);
});

const type = computed(() => {
  if (isExternalLink.value) {
    return 'a';
  }
  return 'router-link';
});

function linkProps(to: any) {
  if (isExternalLink.value) {
    return {
      href: to,
      target: props.target ? '_blank' : '_self',
      rel: 'noopener',
    };
  }
  return { to: to };
}
</script>
