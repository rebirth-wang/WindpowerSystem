<template>
  <div class="data-panel">
    <div class="content-wrap" v-if="data">
      <template v-for="(item, index) in data" :key="index">
        <div
          class="data-item"
          draggable="true"
          v-if="!item.isHide || item.isHide == false"
          @dragstart="handleDragstart($event, item)"
        >
          <!-- svg图片 -->
          <template v-if="item.type === 'svg'">
            <svg-icon class="icon" :icon-class="item.icon" />
            <div class="text">{{ item.text }}</div>
          </template>
          <!-- 阿里巴巴矢量图标 -->
          <template v-else-if="item.isFontIcon === 1">
            <div class="icon">
              <i class="icon iconfont" :class="item.icon"></i>
            </div>
            <div class="text">
              {{ item.text }}
            </div>
          </template>
          <!-- 本地图片 -->
          <template v-else-if="item.type === 'static' && item.icon">
            <img class="icon" :src="getUnitIconUrl(item.icon)" />
            <span class="text">{{ item.text }}</span>
          </template>
          <!-- 网络图片 -->
          <template v-else-if="item.type === 'service'">
            <img
              class="icon"
              :src="baseApi + item.icon"
              @mouseover="handleImgMouseover($event, item)"
              @mouseleave="isShow = false"
            />
            <span class="text">{{ item.text }}</span>
          </template>
          <template v-else>
            <svg-icon class="icon" :icon-class="item" />
          </template>
        </div>
      </template>
      <div v-if="isMore && data.length !== total" class="more-item" @click="handleMore">{{ $t('more') }}</div>
    </div>
    <el-empty v-else :image-size="50">
      <template #description>
        <span style="color: #82848a; font-size: 12px">{{ $t('noData') }}</span>
      </template>
    </el-empty>
    <div :style="{ top: imageTop + 'px', left: `${sidebarStatus ? 300 : 180}` + 'px' }" class="img-hover" v-if="isShow">
      <img class="img-wrap" :src="baseApi + imageHover.icon" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useAppStore } from '@/stores/modules/app';

const appStore = useAppStore();

const props = defineProps<{
  isMore?: boolean;
  data?: any[];
  total?: number;
}>();

const emit = defineEmits<{
  (e: 'moreClick', value: any): void;
}>();

const baseApi = import.meta.env.VITE_APP_BASE_API;
const unitIconModules = import.meta.glob('/src/assets/images/scada/unitIcon/**/*', {
  eager: true,
  query: '?url',
  import: 'default',
}) as Record<string, string>;
const sidebarStatus = ref(appStore.sidebar.opened);
const imageHover = ref({ icon: '', text: '' });
const imageTop = ref(0);
const isShow = ref(false);

function getUnitIconUrl(icon: string) {
  if (!icon) return '';
  if (/^(https?:)?\/\//.test(icon) || icon.startsWith('data:') || icon.startsWith('/')) {
    return icon;
  }
  const normalizedIcon = icon
    .replace(/\\/g, '/')
    .replace(/^assets\/images\/scada\/unitIcon\//, '')
    .replace(/^src\/assets\/images\/scada\/unitIcon\//, '');
  return unitIconModules[`/src/assets/images/scada/unitIcon/${normalizedIcon}`] || '';
}

function handleDragstart(event: DragEvent, item: any) {
  const { type, icon, info } = item;
  const img = { icon, type };
  const data = { img, ...info };
  const infoJson = JSON.stringify(data);
  console.log('左边栏开始拖拽', data);
  event.dataTransfer?.setData('my-info', infoJson);
}

function handleImgMouseover(event: MouseEvent, value: any) {
  imageHover.value = value;
  imageTop.value = event.pageY - 10;
  isShow.value = true;
}

function handleMore() {
  emit('moreClick', null);
}
</script>

<style lang="scss" scoped>
.data-panel {
  height: 100%;
  width: 100%;

  .content-wrap {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    align-items: flex-start;

    .data-item {
      width: 33.3%;
      height: 78px;
      padding: 5px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      border: 2px solid transparent;

      .icon {
        width: 28px;
        height: 28px;
      }

      .text {
        width: 100%;
        font-size: 13px;
        line-height: normal;
        text-align: center;
        margin-top: 8px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .data-item:hover {
      border: 2px solid #486ff2;
      border-radius: 2px;
      cursor: pointer;
      color: #486ff2;
    }

    .more-item {
      width: 33.3%;
      height: 78px;
      padding: 5px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      border: 2px solid transparent;
      background-color: #f2f3f452;
      border-radius: 5px;
      font-size: 13px;
      cursor: pointer;
    }
  }

  .img-hover {
    position: fixed;
    z-index: 9999;
    text-align: center;
    background-color: #ffff;
    padding: 10px;
    border-radius: 5px;

    .img-wrap {
      height: 150px;
      width: 150px;
    }
  }
}
</style>
