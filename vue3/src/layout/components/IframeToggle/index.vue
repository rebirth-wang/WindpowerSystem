<template>
  <transition-group name="fade-transform">
    <inner-link
      v-for="(item, index) in iframeViews"
      :key="item.path"
      :iframeId="'iframe' + index"
      v-show="$route.path === item.path"
      :src="item.meta.link"
    ></inner-link>
  </transition-group>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useTagsViewStore } from '@/stores/modules/tagsView';
import InnerLink from '../InnerLink/index.vue';

const tagsViewStore = useTagsViewStore();
const iframeViews = computed(() => tagsViewStore.iframeViews);
</script>
