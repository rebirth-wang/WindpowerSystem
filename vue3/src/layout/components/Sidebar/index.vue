<template>
  <div
    :class="{ 'has-logo': showLogo }"
    :style="{
      backgroundColor:
        settingsStore.sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground,
    }"
  >
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar :class="settingsStore.sideTheme" wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="
          settingsStore.sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground
        "
        :text-color="settingsStore.sideTheme === 'theme-dark' ? variables.menuColor : variables.menuLightColor"
        :unique-opened="true"
        :active-text-color="settingsStore.theme"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item
          v-for="(route, index) in sidebarRouters"
          :key="route.path + index"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
    <div
      class="version"
      :style="{
        color: settingsStore.sideTheme === 'theme-dark' ? variables.logoTitleColor : variables.logoLightTitleColor,
      }"
    >
      <div class="collapse" v-if="isCollapse">
        <svg-icon style="font-size: 16px; margin-right: 0px" icon-class="version" />
      </div>
      <div class="expand" v-else>
        <svg-icon style="font-size: 16px; margin-right: 6px" icon-class="version" />
        <span>2.9.0</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { useAppStore } from '@/stores/modules/app';
import { useSettingsStore } from '@/stores/modules/settings';
import { usePermissionStore } from '@/stores/modules/permission';
import Logo from './Logo.vue';
import SidebarItem from './SidebarItem.vue';
import variables from '@/assets/styles/variables.module.scss';

const route = useRoute();
const appStore = useAppStore();
const settingsStore = useSettingsStore();
const permissionStore = usePermissionStore();

const sidebarRouters = computed(() => permissionStore.sidebarRouters);
const sidebar = computed(() => appStore.sidebar);

const activeMenu = computed(() => {
  const { meta, path } = route;
  if (meta.activeMenu) {
    return meta.activeMenu as string;
  }
  return path;
});

const showLogo = computed(() => settingsStore.sidebarLogo);

const isCollapse = computed(() => !sidebar.value.opened);
</script>

<style lang="scss" scoped>
.version {
  font-size: 14px;
  height: 35px;
  line-height: 35px;
  .collapse {
    text-align: center;
  }
  .expand {
    display: flex;
    flex-direction: row;
    align-items: center;
    padding-left: 18px;
  }
}
</style>
