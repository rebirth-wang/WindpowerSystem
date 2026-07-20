<template>
  <div :class="classObj" class="app-wrapper" :style="{ '--current-color': theme }">
    <div v-if="device === 'mobile' && sidebar.opened" class="drawer-bg" @click="handleClickOutside" />
    <Sidebar v-if="!sidebar.hide" class="sidebar-container" />
    <div :class="{ hasTagsView: needTagsView, sidebarHide: sidebar.hide }" class="main-container">
      <div :class="{ 'fixed-header': fixedHeader }">
        <Navbar />
        <TagsView v-if="needTagsView" />
      </div>
      <AppMain />
      <Settings />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useAppStore } from '@/stores/modules/app';
import { useSettingsStore } from '@/stores/modules/settings';
import { AppMain, Navbar, Settings, Sidebar, TagsView } from './components';

const route = useRoute();
const appStore = useAppStore();
const settingsStore = useSettingsStore();

const theme = computed(() => settingsStore.theme);
const sidebar = computed(() => appStore.sidebar);
const device = computed(() => appStore.device);
const needTagsView = computed(() => settingsStore.tagsView);
const fixedHeader = computed(() => settingsStore.fixedHeader);

const classObj = computed(() => ({
  hideSidebar: !sidebar.value.opened,
  openSidebar: sidebar.value.opened,
  withoutAnimation: sidebar.value.withoutAnimation,
  mobile: device.value === 'mobile',
}));

const { body } = document;
const WIDTH = 992;

watch(
  () => route.path,
  () => {
    if (device.value === 'mobile' && sidebar.value.opened) {
      appStore.closeSideBar(false);
    }
  },
  { immediate: true }
);

function $_isMobile() {
  const rect = body.getBoundingClientRect();
  return rect.width - 1 < WIDTH;
}

function $_resizeHandler() {
  if (!document.hidden) {
    const isMobile = $_isMobile();
    appStore.toggleDevice(isMobile ? 'mobile' : 'desktop');
    if (isMobile) {
      appStore.closeSideBar(true);
    }
  }
}

onMounted(() => {
  const isMobile = $_isMobile();
  if (isMobile) {
    appStore.toggleDevice('mobile');
    appStore.closeSideBar(true);
  }
  window.addEventListener('resize', $_resizeHandler);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', $_resizeHandler);
});

function handleClickOutside() {
  appStore.closeSideBar(false);
}
</script>

<style lang="scss" scoped>
@use '@/assets/styles/mixin.scss' as *;
@use '@/assets/styles/variables.scss' as *;

.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;

  &.mobile.openSidebar {
    position: fixed;
    top: 0;
  }
}

.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: calc(100% - #{$base-sidebar-width});
  transition: width 0.28s;
}

.hideSidebar .fixed-header {
  width: calc(100% - 54px);
}

.sidebarHide .fixed-header {
  width: 100%;
}

.mobile .fixed-header {
  width: 100%;
}
</style>
