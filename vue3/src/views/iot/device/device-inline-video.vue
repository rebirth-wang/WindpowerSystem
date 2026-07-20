<template>
  <div class="device-inline-video" v-loading="loading" element-loading-background="#ffff">
    <el-row class="video-row" :gutter="16" v-show="total > 0">
      <el-col class="device-video-col" :span="6" v-for="(item, index) in sipList" :key="index">
        <div class="video">
          <player
            :ref="(el: any) => setPlayerRef(el, index)"
            :playerinfo="{ playtype: 'play', deviceId: item.deviceSipId, channelId: item.channelId }"
          ></player>
          <span v-if="item.status === 1" class="status" style="color: #ffba00">{{ $t('home.notActive') }}</span>
          <span v-if="item.status === 2" class="status" style="color: #ff4949">{{ $t('home.disabled') }}</span>
          <span v-if="item.status === 4" class="status" style="color: #909399">{{ $t('home.offline') }}</span>
          <el-icon v-if="item.status === 3" class="btn" @click="handlePlay($event, item, index)">
            <CaretRight />
          </el-icon>
        </div>
      </el-col>
    </el-row>
    <el-empty v-if="total === 0" :description="$t('device.inline-video.986754-0')"></el-empty>
    <div class="pagination-wrap" v-show="total > 0">
      <pagination
        :total="total"
        v-show="total > 0"
        v-model:page="pageNum"
        v-model:limit="pageSize"
        :pageSizes="[9, 18, 27, 36]"
        @pagination="getSipList"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, getCurrentInstance } from 'vue';
import { CaretRight } from '@element-plus/icons-vue';
import { useAppStore } from '@/stores/modules/app';
import player from '@/views/components/player/player.vue';
import { playCom, closeStream, startProxy, stopProxy } from '@/api/iot/media/player';

const { proxy } = getCurrentInstance()!;
const appStore = useAppStore();

const props = defineProps({
  sipRelationList: {
    type: Array as () => any[],
    default: () => [],
  },
});

const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(9);
const total = ref(0);
const tempSipList = ref<any[]>([]);
const playerRefs = ref<Record<number, any>>({});
const activeProxyKeys = ref(new Set<string>());

function setPlayerRef(el: any, index: number) {
  if (el) {
    playerRefs.value[index] = el;
  }
}

// 检测系统菜单栏状态
watch(
  () => appStore.sidebar?.opened,
  () => {
    calculatePlayerHeight();
  }
);

watch(
  () => props.sipRelationList,
  (newVal) => {
    if (newVal && newVal.length !== 0) {
      total.value = newVal.length;
      tempSipList.value = props.sipRelationList;
    }
  },
  { deep: true }
);

const sipList = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return props.sipRelationList.slice(start, end);
});

onMounted(() => {
  calculatePlayerHeight();
  window.addEventListener('resize', calculatePlayerHeight, true);
  tempSipList.value = props.sipRelationList;
  total.value = props.sipRelationList.length;
});

onBeforeUnmount(() => {
  handleClose();
  window.removeEventListener('resize', calculatePlayerHeight, true);
});

// 获取窗体高度
function calculatePlayerHeight() {
  loading.value = true;
  setTimeout(() => {
    sipList.value &&
      sipList.value.forEach((item: any, index: number) => {
        const playerRef = playerRefs.value[index];
        if (playerRef?.$refs?.container) {
          let videoContainer = playerRef.$refs.container;
          const colEl = document.querySelector('.device-video-col') as HTMLElement | null;
          const offsetWidth = colEl?.offsetWidth;
          videoContainer.style.width = offsetWidth ? offsetWidth - 16 + 'px' : '300px';
          videoContainer.style.height = '230px';
        }
      });
    loading.value = false;
  }, 100);
}

function getSipList(e: any) {
  pageNum.value = e.page;
  pageSize.value = e.limit;
}

// 播放
function getSipIndex(index: number) {
  return (pageNum.value - 1) * pageSize.value + index;
}

function getSipKey(item: any) {
  return `${item.deviceSipId || ''}_${item.channelId || ''}`;
}

function handlePlay(event: any, item: any, index: number) {
  const playerRef = playerRefs.value[index];
  const sipIndex = getSipIndex(index);
  if (item.channelType === '600' && item.playUrl) {
    const url = item.playUrl;
    playerRef?.play(url);
  } else if (item.channelType === '601' && item.proxyUrl) {
    startProxy(item.deviceSipId, item.channelId).then((res: any) => {
      activeProxyKeys.value.add(getSipKey(item));
      playerRef?.play(res.data.playurl);
    });
  } else {
    playCom(item.deviceSipId, item.channelId).then((res: any) => {
      if (res.code === 200) {
        if (!playerRef?.isInit) {
          playerRef?.init();
        }
        tempSipList.value[sipIndex].streamId = res.data.streamId;
        tempSipList.value[sipIndex].playurl = res.data.playurl;
        playerRef?.play(res.data.playurl);
        event.target.style.visibility = 'hidden';
      }
    });
  }
}

// 关闭播放器
function handleClose() {
  if (sipList.value && sipList.value.length !== 0) {
    sipList.value.forEach((item: any, index: number) => {
      const sipIndex = getSipIndex(index);
      const streamId = tempSipList.value[sipIndex]?.streamId;
      if (streamId) {
        tempSipList.value[sipIndex].streamId = '';
        tempSipList.value[sipIndex].playurl = '';
        closeStream(item.deviceSipId, item.channelId, streamId).catch(() => {});
      }
      // 停止代理拉流
      const sipKey = getSipKey(item);
      if (activeProxyKeys.value.has(sipKey)) {
        activeProxyKeys.value.delete(sipKey);
        stopProxy(item.deviceSipId, item.channelId).catch(() => {});
      }
      const playerRef = playerRefs.value[index];
      playerRef?.close();
    });
  }
}

defineExpose({ handleClose });
</script>

<style lang="scss" scoped>
.device-inline-video {
  position: relative;
  width: 100%;
  height: 100%;
  padding-bottom: 15px;

  .video-row {
    margin: 0 -8px 8px;
  }

  .device-video-col {
    margin-bottom: 16px;
    padding: 0 8px;
    box-sizing: border-box;
  }

  .pagination-wrap {
    position: relative;
    z-index: 2;
    clear: both;
    margin-top: 6px;
    padding: 14px 8px 4px;
    background: #fff;
  }

  .pagination-wrap :deep(.el-pagination) {
    position: static !important;
    top: auto !important;
    bottom: auto !important;
    margin: 0 !important;
    display: flex;
    justify-content: flex-end;
  }

  .video {
    position: relative;
    width: 100%;
    height: 230px;
    overflow: hidden;
    border-radius: 4px;

    .btn {
      position: absolute;
      bottom: 5px;
      left: 5px;
      border-radius: 50%;
      background: #fff;
      padding: 5px;
      cursor: pointer;
      color: #606266;
    }

    .status {
      position: absolute;
      top: 5px;
      left: 8px;
      font-size: 12px;
    }
  }
}
</style>
