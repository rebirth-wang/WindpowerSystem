<template>
  <el-color-picker
    v-model="theme"
    :predefine="['#486FF2', '#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#f5222d']"
    class="theme-picker"
    popper-class="theme-picker-dropdown"
  />
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue';
import { useSettingsStore } from '@/stores/modules/settings';

const settingsStore = useSettingsStore();
const chalk = ref(''); // content of theme-chalk css
const theme = ref('');
const ORIGINAL_THEME = '#486FF2'; // default color

const defaultTheme = computed(() => settingsStore.theme);

watch(
  () => defaultTheme.value,
  (val, oldVal) => {
    theme.value = val;
  },
  { immediate: true }
);

watch(theme, async (val) => {
  await setTheme(val);
});

onMounted(() => {
  if (defaultTheme.value !== ORIGINAL_THEME) {
    setTheme(defaultTheme.value);
  }
});

const setTheme = async (val: string) => {
  const oldVal = chalk.value ? theme.value : ORIGINAL_THEME;
  if (typeof val !== 'string') return;
  const themeCluster = getThemeCluster(val.replace('#', ''));
  const originalCluster = getThemeCluster(oldVal.replace('#', ''));

  const getHandler = (variable: string, id: string) => {
    return () => {
      const originalCluster = getThemeCluster(ORIGINAL_THEME.replace('#', ''));
      const newStyle = updateStyle((window as any)[variable], originalCluster, themeCluster);

      let styleTag = document.getElementById(id);
      if (!styleTag) {
        styleTag = document.createElement('style');
        styleTag.setAttribute('id', id);
        document.head.appendChild(styleTag);
      }
      styleTag.innerText = newStyle;
    };
  };

  if (!chalk.value) {
    // 获取 Element Plus 的 CSS 文件
    const url = 'https://unpkg.com/element-plus/dist/index.css';
    await getCSSString(url, 'chalk');
  }

  const chalkHandler = getHandler('chalk', 'chalk-style');
  if (chalkHandler) {
    chalkHandler();
  }

  const styles = Array.from(document.querySelectorAll('style')).filter((style: HTMLStyleElement) => {
    const text = style.innerText;
    return new RegExp(oldVal, 'i').test(text) && !/Chalk Variables/.test(text);
  });
  styles.forEach((style: HTMLStyleElement) => {
    const { innerText } = style;
    if (typeof innerText !== 'string') return;
    style.innerText = updateStyle(innerText, originalCluster, themeCluster);
  });

  // Update the store
  settingsStore.changeSetting({ key: 'theme', value: val });
};

const updateStyle = (style: string, oldCluster: string[], newCluster: string[]) => {
  let newStyle = style;
  oldCluster.forEach((color, index) => {
    newStyle = newStyle.replace(new RegExp(color, 'ig'), newCluster[index]);
  });
  return newStyle;
};

const getCSSString = (url: string, variable: string) => {
  return new Promise<void>((resolve) => {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
      if (xhr.readyState === 4 && xhr.status === 200) {
        (window as any)[variable] = xhr.responseText.replace(/@font-face{[^}]+}/, '');
        chalk.value = (window as any)[variable];
        resolve();
      }
    };
    xhr.open('GET', url);
    xhr.send();
  });
};

const getThemeCluster = (theme: string) => {
  const tintColor = (color: string, tint: number) => {
    let red = parseInt(color.slice(0, 2), 16);
    let green = parseInt(color.slice(2, 4), 16);
    let blue = parseInt(color.slice(4, 6), 16);

    if (tint === 0) {
      // when primary color is in its rgb space
      return [red, green, blue].join(',');
    } else {
      red = Math.round(red + tint * (255 - red));
      green = Math.round(green + tint * (255 - green));
      blue = Math.round(blue + tint * (255 - blue));

      const newRed = red.toString(16).padStart(2, '0');
      const newGreen = green.toString(16).padStart(2, '0');
      const newBlue = blue.toString(16).padStart(2, '0');

      return `#${newRed}${newGreen}${newBlue}`;
    }
  };

  const shadeColor = (color: string, shade: number) => {
    let red = parseInt(color.slice(0, 2), 16);
    let green = parseInt(color.slice(2, 4), 16);
    let blue = parseInt(color.slice(4, 6), 16);

    red = Math.round((1 - shade) * red);
    green = Math.round((1 - shade) * green);
    blue = Math.round((1 - shade) * blue);

    const newRed = red.toString(16).padStart(2, '0');
    const newGreen = green.toString(16).padStart(2, '0');
    const newBlue = blue.toString(16).padStart(2, '0');

    return `#${newRed}${newGreen}${newBlue}`;
  };

  const clusters = [theme];
  for (let i = 0; i <= 9; i++) {
    clusters.push(tintColor(theme, Number((i / 10).toFixed(2))));
  }
  clusters.push(shadeColor(theme, 0.1));
  return clusters;
};
</script>

<style>
.theme-message,
.theme-picker-dropdown {
  z-index: 99999 !important;
}

.theme-picker .el-color-picker__trigger {
  height: 26px !important;
  width: 26px !important;
  padding: 2px;
}

.theme-picker-dropdown .el-color-dropdown__link-btn {
  display: none;
}
</style>
