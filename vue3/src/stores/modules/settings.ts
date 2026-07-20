import { defineStore } from 'pinia';
import { ref } from 'vue';
import defaultSettings from '@/settings';
import Cookies from 'js-cookie';

const { sideTheme, showSettings, topNav, tagsView, fixedHeader, sidebarLogo, dynamicTitle, language } = defaultSettings;

const storageSetting = JSON.parse(localStorage.getItem('layout-setting')) || '';

export const useSettingsStore = defineStore('settings', () => {
  // 状态
  const title = ref('');
  const theme = ref(storageSetting.theme || '#486FF2');
  const sideThemeRef = ref(storageSetting.sideTheme || sideTheme);
  const showSettingsRef = ref(showSettings);
  const topNavRef = ref(storageSetting.topNav === undefined ? topNav : storageSetting.topNav);
  const tagsViewRef = ref(storageSetting.tagsView === undefined ? tagsView : storageSetting.tagsView);
  const fixedHeaderRef = ref(storageSetting.fixedHeader === undefined ? fixedHeader : storageSetting.fixedHeader);
  const sidebarLogoRef = ref(storageSetting.sidebarLogo === undefined ? sidebarLogo : storageSetting.sidebarLogo);
  const dynamicTitleRef = ref(storageSetting.dynamicTitle === undefined ? dynamicTitle : storageSetting.dynamicTitle);
  const languageRef = ref(language);

  // 更改设置
  const changeSetting = (data: { key: string; value: any }) => {
    const { key, value } = data;
    const settingsMap: Record<string, any> = {
      theme,
      sideTheme: sideThemeRef,
      showSettings: showSettingsRef,
      topNav: topNavRef,
      tagsView: tagsViewRef,
      fixedHeader: fixedHeaderRef,
      sidebarLogo: sidebarLogoRef,
      dynamicTitle: dynamicTitleRef,
      language: languageRef,
    };
    
    if (settingsMap[key] !== undefined) {
      settingsMap[key].value = value;
    }
  };

  // 设置标题
  const setTitle = (titleValue: string) => {
    title.value = titleValue;
  };

  // 设置语言
  const setLang = (lang: string) => {
    languageRef.value = lang;
    Cookies.set('language', lang);
  };

  return {
    // 状态
    title,
    theme,
    sideTheme: sideThemeRef,
    showSettings: showSettingsRef,
    topNav: topNavRef,
    tagsView: tagsViewRef,
    fixedHeader: fixedHeaderRef,
    sidebarLogo: sidebarLogoRef,
    dynamicTitle: dynamicTitleRef,
    language: languageRef,
    
    // 方法
    changeSetting,
    setTitle,
    setLang,
  };
});