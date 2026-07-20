<template>
  <div class="icon-body">
    <el-input
      v-model="name"
      style="position: relative"
      clearable
      placeholder="请输入图标名称"
      @clear="filterIcons"
      @input="filterIcons"
    >
      <template #suffix>
        <el-icon><Search /></el-icon>
      </template>
    </el-input>
    <div class="icon-list">
      <div v-for="(item, index) in iconList" :key="index" @click="selectedIcon(item)">
        <svg-icon :icon-class="item" style="height: 30px; width: 16px; margin-right: 6px" />
        <span>{{ item }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { Search } from '@element-plus/icons-vue';
import icons from './requireIcons';

const emit = defineEmits(['selected']);

const name = ref('');
const iconList = ref<string[]>([...icons]);

function filterIcons() {
  iconList.value = icons;
  if (name.value) {
    iconList.value = iconList.value.filter((item) => item.includes(name.value));
  }
}

function selectedIcon(iconName: string) {
  emit('selected', iconName);
  document.body.click();
}

function reset() {
  name.value = '';
  iconList.value = [...icons];
}

defineExpose({ reset });
</script>

<style lang="scss" scoped>
.icon-body {
  width: 100%;
  padding: 10px;

  .icon-list {
    height: 200px;
    overflow-y: scroll;

    div {
      height: 30px;
      line-height: 30px;
      margin-bottom: -5px;
      cursor: pointer;
      width: 33%;
      float: left;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      padding: 0 5px;
    }

    span {
      display: inline-block;
      vertical-align: -0.15em;
      fill: currentColor;
      overflow: hidden;
    }
  }
}
</style>
