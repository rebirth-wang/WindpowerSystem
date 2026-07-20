<template>
  <div class="sidebar-item-wrap">
    <svg-icon v-if="icon" class-name="sidebar-item-icon" :icon-class="icon" />
    <span v-if="title" class="sidebar-item-title" :title="title">{{ title }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps({
  icon: {
    type: String,
    default: '',
  },
  title: {
    type: String,
    default: '',
  },
});
</script>
