<template>
  <div
    class="sidebar-logo-container"
    :class="{ collapse: collapse }"
    :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }"
  >
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/index">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
        <h1
          v-else
          class="sidebar-title"
          :style="{ color: sideTheme === 'theme-dark' ? variables.logoTitleColor : variables.logoLightTitleColor }"
          style="padding-left: 10px"
        >
          {{ title }}
        </h1>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/index">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
        <span
          class="sidebar-title"
          :style="{ color: sideTheme === 'theme-dark' ? variables.logoTitleColor : variables.logoLightTitleColor }"
          style="padding-left: 10px"
        >
          {{ title }}
        </span>
      </router-link>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useSettingsStore } from '@/stores/modules/settings';
import { useUserStore } from '@/stores/modules/user';
import { getDept } from '@/api/system/dept';
import logoImg from '@/assets/images/logo.gif';
import variables from '@/assets/styles/variables.module.scss';

const props = defineProps({
  collapse: {
    type: Boolean,
    required: true,
  },
});

const settingsStore = useSettingsStore();
const userStore = useUserStore();

const sideTheme = computed(() => settingsStore.sideTheme);

const title = ref('FastBee');
const logo = ref('');
const baseUrl = import.meta.env.VITE_APP_BASE_API;

onMounted(() => {
  getDeptDetail();
});

function getDeptDetail() {
  const dept = userStore.dept;
  if (!dept || !dept.deptId) {
    logo.value = logoImg;
    return;
  }
  getDept(dept.deptId)
    .then((response: any) => {
      const data = response.data;
      if (data.logoName) {
        title.value = data.logoName.length > 8 ? data.logoName.slice(0, 8) : data.logoName;
      } else {
        title.value = 'FastBee';
      }
      if (data.deptLogo) {
        logo.value = baseUrl + data.deptLogo;
      } else {
        logo.value = logoImg;
      }
    })
    .catch(() => {
      logo.value = logoImg;
    });
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter-from,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  height: 50px;
  line-height: 50px;
  background: #2b2f3a;
  overflow: hidden;
  margin-left: 12px;
  margin-top: 15px;
  width: 400px;
  white-space: nowrap;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;

    & .sidebar-logo {
      width: 30px;
      height: 30px;
      vertical-align: middle;
    }

    & .sidebar-title {
      display: inline-block;
      margin: 0;
      color: #fff;
      font-weight: 400;
      line-height: 50px;
      font-size: 18px;
      font-family: '微软雅黑';
      vertical-align: middle;
      white-space: nowrap;
      overflow: hidden;
      width: calc(100% - 38px);
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
    }
  }
}
</style>
