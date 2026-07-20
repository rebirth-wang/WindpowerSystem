<template>
  <div class="lang-select">
    <el-dropdown @command="chooseLang">
      <div class="link-wrap">
        <svg-icon icon-class="globe" />
        <span class="name">{{ language }}</span>
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-for="(item, key, i) in langs" :key="i" :command="key">
            <span style="font-size: 14px">{{ item.language }}</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { langs } from '@/lang';
import { addOrUpdate } from '@/api/system/language';
import { getToken } from '@/utils/auth';
import defaultSettings from '@/settings';
import { useSettingsStore } from '@/stores/modules/settings';

const { locale } = useI18n();
const settingsStore = useSettingsStore();
const language = ref('');

function setLanguage() {
  const lang = locale.value || defaultSettings.language;
  language.value = (langs as any)[lang]?.language || '';
  if (getToken()) addOrUpdate({ language: lang });
  settingsStore.setLang(lang);
}

function chooseLang(lang: string) {
  locale.value = lang;
  setLanguage();
  window.location.reload();
}

onMounted(() => {
  setLanguage();
});
</script>

<style lang="scss" scoped>
.lang-select {
  cursor: pointer;
  line-height: 14px;
  font-size: 14px;

  .link-wrap {
    display: flex;
    align-items: center;

    .name {
      margin-left: 6px;
    }
  }

  :deep(.el-dropdown-menu__item) {
    font-weight: 400;
    font-size: 14px;
    color: #909399;
  }
}
</style>
