<template>
  <div :class="{ show: show }" class="right-panel-container">
    <div class="right-panel-background" />
    <div class="right-panel">
      <div
        class="right-panel__button"
        :style="{ top: buttonTop + 'px', 'background-color': theme }"
        @click="show = !show"
      >
        <el-icon>
          <Close v-if="show" />
          <Setting v-else />
        </el-icon>
      </div>
      <div class="right-panel-items">
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { Close, Setting } from '@element-plus/icons-vue';
import { useSettingsStore } from '@/stores/modules/settings';

const props = defineProps({
  clickNotClose: {
    type: Boolean,
    default: false,
  },
  buttonTop: {
    type: Number,
    default: 250,
  },
});

const settingsStore = useSettingsStore();
const show = ref(false);
const theme = computed(() => settingsStore.theme);

watch(show, (value) => {
  if (value && !props.clickNotClose) {
    addEventClick();
  }
});

function addEventClick() {
  window.addEventListener('click', closeSidebar);
}

function closeSidebar(evt: Event) {
  const parent = (evt.target as HTMLElement).closest('.right-panel');
  if (!parent) {
    show.value = false;
    window.removeEventListener('click', closeSidebar);
  }
}
</script>

<style>
.showRightPanel {
  overflow: hidden;
  position: relative;
  width: calc(100% - 15px);
}
</style>

<style lang="scss" scoped>
.right-panel-background {
  position: fixed;
  top: 0;
  left: 0;
  opacity: 0;
  transition: opacity 0.3s cubic-bezier(0.7, 0.3, 0.1, 1);
  background: rgba(0, 0, 0, 0.2);
  z-index: -1;
}

.right-panel {
  width: 100%;
  max-width: 260px;
  height: 100vh;
  position: fixed;
  top: 0;
  right: 0;
  box-shadow: 0px 0px 15px 0px rgba(0, 0, 0, 0.05);
  transition: all 0.25s cubic-bezier(0.7, 0.3, 0.1, 1);
  transform: translate(100%);
  background: #fff;
  z-index: 40000;
}

.show {
  transition: all 0.3s cubic-bezier(0.7, 0.3, 0.1, 1);

  .right-panel-background {
    z-index: 20000;
    opacity: 1;
    width: 100%;
    height: 100%;
  }

  .right-panel {
    transform: translate(0);
  }
}

.right-panel__button {
  width: 36px;
  height: 36px;
  position: absolute;
  left: -36px;
  text-align: center;
  font-size: 24px;
  border-radius: 6px 0 0 6px !important;
  z-index: 0;
  pointer-events: auto;
  cursor: pointer;
  color: #fff;
  line-height: 36px;
}
</style>
