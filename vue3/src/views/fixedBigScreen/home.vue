<template>
  <div id="index" ref="appRef" class="index_home" :class="{ pageisScale: true }">
    <div class="bg">
      <dv-loading v-if="loading">Loading...</dv-loading>
      <div v-else class="host-body">
        <!-- 头部 s -->
        <div class="d-flex jc-center title_wrap">
          <div class="zuojuxing"></div>
          <div class="youjuxing"></div>
          <div class="guang"></div>
          <div class="d-flex jc-center">
            <div class="title">
              <span class="title-text">FastBee物联网平台</span>
            </div>
          </div>
          <div class="timers">
            {{ dateYear }} {{ dateWeek }} {{ dateDay }}
            <i class="blq-icon-shezhi02" style="margin-left: 10px" @click="showSetting"></i>
          </div>
        </div>
        <!-- 头部 e-->
        <!-- 内容  s-->
        <IndexContent />
        <!-- 内容 e -->
      </div>
    </div>
    <Setting ref="settingRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { formatTime } from '@/utils/bigScreen/index.js';
import Setting from './setting.vue';
import IndexContent from './indexs/index.vue';

const appRef = ref<HTMLElement | null>(null);
const settingRef = ref<InstanceType<typeof Setting> | null>(null);

const loading = ref(true);
const dateDay = ref('');
const dateYear = ref('');
const dateWeek = ref('');
const weekday = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
let timing: ReturnType<typeof setInterval> | null = null;

onMounted(() => {
  timeFn();
  cancelLoading();
  window.addEventListener('resize', applyScale);
  applyScale();
});

onBeforeUnmount(() => {
  if (timing) {
    clearInterval(timing);
  }
  window.removeEventListener('resize', applyScale);
});

function showSetting() {
  settingRef.value?.init();
}

function timeFn() {
  timing = setInterval(() => {
    dateDay.value = formatTime(new Date(), 'HH: mm: ss');
    dateYear.value = formatTime(new Date(), 'yyyy-MM-dd');
    dateWeek.value = weekday[new Date().getDay()];
  }, 1000);
}

function cancelLoading() {
  setTimeout(() => {
    loading.value = false;
  }, 500);
}

function calculateScale() {
  const baseWidth = 1920;
  const screenWidth = window.innerWidth;
  return screenWidth / baseWidth;
}

function applyScale() {
  const scale = calculateScale();
  const element = document.getElementById('index');
  if (element) {
    element.style.transform = `scale(${scale})`;
  }
}
</script>

<style lang="scss">
@use '@/assets/styles/bigScreen/theme/index.css' as themeIndex;
@use '@/assets/styles/bigScreen/home.scss' as homeStyle;
@use '@/assets/styles/bigScreen/index.scss' as bigScreenIndex;
</style>
