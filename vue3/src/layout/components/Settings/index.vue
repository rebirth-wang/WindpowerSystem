<template>
  <el-drawer size="280px" v-model="visible" :with-header="false" :append-to-body="true" :show-close="false">
    <div class="drawer-container">
      <div>
        <div class="setting-drawer-content">
          <div class="setting-drawer-title">
            <h3 class="drawer-title">{{ $t('layout.components.settings.index.909453-0') }}</h3>
          </div>
          <div class="setting-drawer-block-checbox">
            <div class="setting-drawer-block-checbox-item" @click="handleTheme('theme-dark')">
              <img src="@/assets/images/dark.svg" alt="dark" />
              <div
                v-if="sideTheme === 'theme-dark'"
                class="setting-drawer-block-checbox-selectIcon"
                style="display: block"
              >
                <i aria-label="icon: check" class="anticon anticon-check">
                  <svg
                    viewBox="64 64 896 896"
                    data-icon="check"
                    width="1em"
                    height="1em"
                    :fill="theme"
                    aria-hidden="true"
                    focusable="false"
                    class=""
                  >
                    <path
                      d="M912 190h-69.9c-9.8 0-19.1 4.5-25.1 12.2L404.7 724.5 207 474a32 32 0 0 0-25.1-12.2H112c-6.7 0-10.4 7.7-6.3 12.9l273.9 347c12.8 16.2 37.4 16.2 50.3 0l488.4-618.9c4.1-5.1.4-12.8-6.3-12.8z"
                    />
                  </svg>
                </i>
              </div>
            </div>
            <div class="setting-drawer-block-checbox-item" @click="handleTheme('theme-light')">
              <img src="@/assets/images/light.svg" alt="light" />
              <div
                v-if="sideTheme === 'theme-light'"
                class="setting-drawer-block-checbox-selectIcon"
                style="display: block"
              >
                <i aria-label="icon: check" class="anticon anticon-check">
                  <svg
                    viewBox="64 64 896 896"
                    data-icon="check"
                    width="1em"
                    height="1em"
                    :fill="theme"
                    aria-hidden="true"
                    focusable="false"
                    class=""
                  >
                    <path
                      d="M912 190h-69.9c-9.8 0-19.1 4.5-25.1 12.2L404.7 724.5 207 474a32 32 0 0 0-25.1-12.2H112c-6.7 0-10.4 7.7-6.3 12.9l273.9 347c12.8 16.2 37.4 16.2 50.3 0l488.4-618.9c4.1-5.1.4-12.8-6.3-12.8z"
                    />
                  </svg>
                </i>
              </div>
            </div>
          </div>

          <div class="drawer-item">
            <span>{{ $t('layout.components.settings.index.909453-1') }}</span>
            <theme-picker style="float: right; height: 26px; margin: -3px 8px 0 0" @change="themeChange" />
          </div>
        </div>

        <el-divider />

        <h3 class="drawer-title">{{ $t('layout.components.settings.index.909453-2') }}</h3>

        <div class="drawer-item">
          <span>{{ $t('layout.components.settings.index.909453-3') }}</span>
          <el-switch v-model="topNav" class="drawer-switch" />
        </div>

        <div class="drawer-item">
          <span>{{ $t('layout.components.settings.index.909453-4') }}</span>
          <el-switch v-model="tagsView" class="drawer-switch" />
        </div>

        <div class="drawer-item">
          <span>{{ $t('layout.components.settings.index.909453-5') }}</span>
          <el-switch v-model="fixedHeader" class="drawer-switch" />
        </div>

        <div class="drawer-item">
          <span>{{ $t('layout.components.settings.index.909453-6') }}</span>
          <el-switch v-model="sidebarLogo" class="drawer-switch" />
        </div>

        <div class="drawer-item">
          <span>{{ $t('layout.components.settings.index.909453-7') }}</span>
          <el-switch v-model="dynamicTitle" class="drawer-switch" />
        </div>

        <el-divider />

        <el-button type="primary" plain :icon="DocumentAdd" @click="saveSetting">
          {{ $t('layout.components.settings.index.909453-8') }}
        </el-button>
        <el-button plain :icon="Refresh" @click="resetSetting">
          {{ $t('layout.components.settings.index.909453-9') }}
        </el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElLoading } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { DocumentAdd, Refresh } from '@element-plus/icons-vue';
import { useSettingsStore } from '@/stores/modules/settings';
import { useAppStore } from '@/stores/modules/app';
import ThemePicker from '@/components/ThemePicker/index.vue';

const { t } = useI18n();
const settingsStore = useSettingsStore();
const appStore = useAppStore();

const theme = ref(settingsStore.theme);
const sideTheme = ref(settingsStore.sideTheme);

const visible = computed({
  get: () => settingsStore.showSettings,
  set: (val: boolean) => settingsStore.changeSetting({ key: 'showSettings', value: val }),
});

const fixedHeader = computed({
  get: () => settingsStore.fixedHeader,
  set: (val: boolean) => settingsStore.changeSetting({ key: 'fixedHeader', value: val }),
});

const topNav = computed({
  get: () => settingsStore.topNav,
  set: (val: boolean) => {
    settingsStore.changeSetting({ key: 'topNav', value: val });
    if (!val) {
      appStore.toggleSideBarHide(false);
    }
  },
});

const tagsView = computed({
  get: () => settingsStore.tagsView,
  set: (val: boolean) => settingsStore.changeSetting({ key: 'tagsView', value: val }),
});

const sidebarLogo = computed({
  get: () => settingsStore.sidebarLogo,
  set: (val: boolean) => settingsStore.changeSetting({ key: 'sidebarLogo', value: val }),
});

const dynamicTitle = computed({
  get: () => settingsStore.dynamicTitle,
  set: (val: boolean) => settingsStore.changeSetting({ key: 'dynamicTitle', value: val }),
});

function themeChange(val: string) {
  settingsStore.changeSetting({ key: 'theme', value: val });
  theme.value = val;
}

function handleTheme(val: string) {
  settingsStore.changeSetting({ key: 'sideTheme', value: val });
  sideTheme.value = val;
}

function saveSetting() {
  const loading = ElLoading.service({
    lock: true,
    text: t('layout.components.settings.index.909453-10'),
  });
  const cache = {
    topNav: topNav.value,
    tagsView: tagsView.value,
    fixedHeader: fixedHeader.value,
    sidebarLogo: sidebarLogo.value,
    dynamicTitle: dynamicTitle.value,
    sideTheme: sideTheme.value,
    theme: theme.value,
  };
  localStorage.setItem('layout-setting', JSON.stringify(cache));
  setTimeout(() => {
    loading.close();
  }, 1000);
}

function resetSetting() {
  ElLoading.service({
    lock: true,
    text: t('layout.components.settings.index.909453-11'),
  });
  localStorage.removeItem('layout-setting');
  setTimeout(() => window.location.reload(), 1000);
}
</script>

<style lang="scss" scoped>
.setting-drawer-content {
  .setting-drawer-title {
    margin-bottom: 12px;
    color: rgba(0, 0, 0, 0.85);
    font-size: 14px;
    line-height: 22px;
    font-weight: bold;
  }

  .setting-drawer-block-checbox {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    margin-top: 10px;
    margin-bottom: 20px;

    .setting-drawer-block-checbox-item {
      position: relative;
      margin-right: 16px;
      border-radius: 2px;
      cursor: pointer;

      img {
        width: 48px;
        height: 48px;
      }

      .setting-drawer-block-checbox-selectIcon {
        position: absolute;
        top: 4px;
        right: 0;
        width: 100%;
        height: 100%;
        padding-top: 15px;
        padding-left: 24px;
        color: #1890ff;
        font-weight: 700;
        font-size: 14px;
      }
    }
  }
}

.drawer-container {
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;
  .drawer-title {
    margin-bottom: 12px;
    color: rgba(0, 0, 0, 0.85);
    font-size: 14px;
    line-height: 22px;
  }
  .drawer-item {
    color: rgba(0, 0, 0, 0.65);
    font-size: 14px;
    padding: 12px 0;
  }
  .drawer-switch {
    float: right;
  }
}
</style>
