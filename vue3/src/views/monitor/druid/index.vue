<template>
  <div class="app-container">
    <iFrame :src="url" />
  </div>
</template>
<script setup lang="ts">
import iFrame from '@/components/iFrame/index.vue';

const url = import.meta.env.VITE_APP_BASE_API + '/druid/login.html';
</script>
