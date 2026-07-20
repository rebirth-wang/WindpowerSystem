<template>
  <div class="navbar">
    <hamburger
      id="hamburger-container"
      :is-active="sidebar.opened"
      class="hamburger-container"
      @toggleClick="toggleSideBar"
    />
    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" v-if="!topNav" />
    <top-nav id="topmenu-container" class="topmenu-container" v-if="topNav" />

    <div class="right-menu">
      <template v-if="device !== 'mobile'">
        <lang-select id="lang-select" class="right-menu-item" />
        <size-select class="right-menu-item" />
      </template>

      <el-dropdown class="avatar-container right-menu-item">
        <div class="avatar-wrapper">
          <img class="avatar" :src="avatar" />
          <span class="name">{{ name }}</span>
          <el-icon class="icon"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <router-link to="/user/profile">
              <el-dropdown-item>{{ $t('navbar.personalCenter') }}</el-dropdown-item>
            </router-link>
            <el-dropdown-item @click="setting = true">
              <span>{{ $t('navbar.layoutSetting') }}</span>
            </el-dropdown-item>
            <el-dropdown-item divided @click="logout">
              <span>{{ $t('navbar.logout') }}</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessageBox } from 'element-plus';
import { ArrowDown } from '@element-plus/icons-vue';
import { useAppStore } from '@/stores/modules/app';
import { useUserStore } from '@/stores/modules/user';
import { useSettingsStore } from '@/stores/modules/settings';
import Breadcrumb from '@/components/Breadcrumb/index.vue';
import TopNav from '@/components/TopNav/index.vue';
import Hamburger from '@/components/Hamburger/index.vue';
import SizeSelect from '@/components/SizeSelect/index.vue';
import LangSelect from '@/layout/components/LangSelect.vue';

const { t } = useI18n();
const appStore = useAppStore();
const userStore = useUserStore();
const settingsStore = useSettingsStore();

const sidebar = computed(() => appStore.sidebar);
const device = computed(() => appStore.device);
const avatar = computed(() => userStore.avatar);
const name = computed(() => userStore.name);
const topNav = computed(() => settingsStore.topNav);

const setting = computed({
  get: () => settingsStore.showSettings,
  set: (val: boolean) => {
    settingsStore.changeSetting({ key: 'showSettings', value: val });
  },
});

function toggleSideBar() {
  appStore.toggleSideBar();
}

async function logout() {
  ElMessageBox.confirm(t('login.989807-31'), t('login.989807-32'), {
    confirmButtonText: t('confirm'),
    cancelButtonText: t('cancel'),
    type: 'warning',
  })
    .then(() => {
      userStore.LogOut().then(() => {
        location.href = '/';
      });
    })
    .catch(() => {});
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: visible;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;
    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .right-menu {
    float: right;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      font-size: 14px;
      color: #606266;
      cursor: pointer;
    }

    .avatar-container {
      margin-right: 20px;

      .avatar-wrapper {
        font-size: 14px;
        cursor: pointer;
        display: flex;
        flex-direction: row;
        align-items: center;

        .avatar {
          width: 30px;
          height: 30px;
          border-radius: 50%;
        }

        .name {
          margin-left: 6px;
          font-size: 14px;
          color: #606266;
        }

        .icon {
          margin-left: 4px;
        }
      }
    }
  }
}
</style>
