<template>
  <div class="setting-bar">
    <component
      :is="dragComponentRegistry[dragStore.rightcom] || dragStore.rightcom"
      :datas="dragStore.currentproperties"
      @componenmanagement="handleComponenmanagement"
    />
  </div>
</template>

<script setup lang="ts">
import { watch } from 'vue';
import { useDragEditorStore } from '@/stores/modules/dragEditor';
import utils from '@/utils/index';
import { dragComponentRegistry } from './componentRegistry';

const dragStore = useDragEditorStore();

watch(
  () => dragStore.rightcom,
  (newval) => {
    if (newval === 'decorate') {
      utils.forEach(dragStore.pageComponents, (res: any) => {
        if (res.active === true) res.active = false;
      });
      dragStore.setCurrentproperties(dragStore.pageSetup);
      return;
    }
    if (newval === 'componenmanagement') {
      utils.forEach(dragStore.pageComponents, (res: any) => {
        if (res.active === true) res.active = false;
      });
      dragStore.setCurrentproperties(dragStore.pageComponents);
    }
  }
);

function handleComponenmanagement(res: any) {
  dragStore.setPageComponents(res);
}
</script>

<style lang="scss" scoped>
.setting-bar {
  width: 376px;
  height: 100%;
  overflow-y: scroll;
  overflow-x: hidden;
  position: relative;
  padding: 0 12px;
  background: #fff;
  border-left: 1px solid #f2f4f6;

  &::-webkit-scrollbar-thumb {
    background-color: #e1e1e1;
  }
  &::-webkit-scrollbar-thumb:hover {
    background-color: #a5a2a2;
  }
  &::-webkit-scrollbar {
    width: 3px;
    height: 3px;
    position: absolute;
  }
  &::-webkit-scrollbar-track {
    background-color: #fff;
  }
}
</style>
