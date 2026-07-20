<template>
  <el-tabs class="cmpt-bar" v-model="currtabsName" tab-position="left">
    <el-tab-pane v-for="item in options" :key="item.id" :name="item.id">
      <template #label>
        <div class="tab-item">
          <drag-icon :name="item.icon" :size="26" />
          <span class="name" :class="{ active: currtabsName === item.id }">{{ item.name }}</span>
        </div>
      </template>
      <panel :pointer="pointer" :datas="getDatas(item.json)" />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import DragIcon from '@/components/DragEditor/DragIcon.vue';
import Panel from './panel.vue';
import menus from './menus';

import textDisplayJson from './unitMenu/textDisplay.json';
import numDisplayJson from './unitMenu/numDisplay.json';
import numControlJson from './unitMenu/numControl.json';
import mulStatusControlJson from './unitMenu/mulStatusControl.json';
import btnControlJson from './unitMenu/btnControl.json';
import historicalDataJson from './unitMenu/historicalData.json';
import otherJson from './unitMenu/other.json';

const props = defineProps<{ pointer: any }>();

const currtabsName = ref(menus[0].id);
const options = menus;

const jsonMap: Record<string, any> = {
  textDisplayJson,
  numDisplayJson,
  numControlJson,
  mulStatusControlJson,
  btnControlJson,
  historicalDataJson,
  otherJson,
};

function getDatas(name: string) {
  return jsonMap[name];
}
</script>

<style lang="scss" scoped>
.cmpt-bar {
  height: 100%;
  background: #ffffff;
  border-right: 1px solid #f2f4f6;

  :deep(.el-tabs__header) {
    margin: 0;
    background: #f7f8fa;

    .el-tabs__item {
      padding: 8px;
      color: #323232;
      height: 100%;
      width: 78px;

      .tab-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        font-size: 14px;
        .name {
          font-size: 12px;
          font-weight: 500;
          line-height: 24px;
          text-align: center;
          white-space: normal;
        }
      }

      &.is-active {
        color: #486ff2;
        background: #ffffff;
      }
    }

    .el-tabs__active-bar {
      background: transparent;
    }
    .el-tabs__nav-wrap::after {
      background-color: transparent;
    }
  }

  .active {
    color: #486ff2 !important;
  }

  :deep(.el-tabs__content) {
    height: 100%;
    .el-tab-pane {
      height: 100%;
    }
    .el-sub-menu__title {
      height: 46px;
      line-height: 46px;
      font-size: 13px;
    }
  }
}
</style>
