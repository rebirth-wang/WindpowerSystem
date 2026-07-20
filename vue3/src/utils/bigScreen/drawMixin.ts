/*
 * 屏幕适配 composable 函数（Vue3版本，对应vue2的drawMixin.js）
 */

import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useSettingsStore } from '@/stores/modules/settings';

// * 设计稿尺寸（px）
const baseWidth = 1920;
const baseHeight = 1080;

// * 需保持的比例（默认1.77778）
const baseProportion = parseFloat((baseWidth / baseHeight).toFixed(5));

export function useDrawMixin(appRef: any) {
  const settingsStore = useSettingsStore();
  const drawTiming = ref<ReturnType<typeof setTimeout> | null>(null);

  function calcRate() {
    const el = appRef?.value ?? appRef;
    if (!el) return;
    const currentRate = parseFloat((window.innerWidth / window.innerHeight).toFixed(5));
    const scale = { width: '1', height: '1' };
    if (currentRate > baseProportion) {
      // 表示更宽
      scale.width = ((window.innerHeight * baseProportion) / baseWidth).toFixed(5);
      scale.height = (window.innerHeight / baseHeight).toFixed(5);
    } else {
      // 表示更高
      scale.height = (window.innerWidth / baseProportion / baseHeight).toFixed(5);
      scale.width = (window.innerWidth / baseWidth).toFixed(5);
    }
    el.style.transform = `scale(${scale.width}, ${scale.height}) translate(-50%, -50%)`;
  }

  function resize() {
    if (!settingsStore.isScale) return;
    if (drawTiming.value) clearTimeout(drawTiming.value);
    drawTiming.value = setTimeout(() => {
      calcRate();
    }, 200);
  }

  onMounted(() => {
    if (!settingsStore.isScale) return;
    calcRate();
    window.addEventListener('resize', resize);
  });

  onBeforeUnmount(() => {
    window.removeEventListener('resize', resize);
  });

  return { calcRate, resize };
}
