<template>
  <div class="scenario-list-video">
    <el-row style="margin-left: -10px; margin-right: -10px; margin-top: -10px">
      <el-col
        id="sceneVideoCol"
        :span="8"
        style="padding-left: 10px; padding-right: 10px; padding-top: 15px"
        v-for="(item, index) in sipList"
        :key="index"
      >
        <div class="video">
          <player
            :ref="(el: any) => setPlayerRef(el, index)"
            :playerinfo="{ playtype: 'play', deviceId: item.deviceId, channelId: item.channelId }"
          ></player>
          <span v-if="item.status === 1" class="status" style="color: #ffba00">{{ $t('home.notActive') }}</span>
          <span v-if="item.status === 2" class="status" style="color: #ff4949">{{ $t('home.disabled') }}</span>
          <span v-if="item.status === 4" class="status" style="color: #909399">{{ $t('home.offline') }}</span>
          <i v-if="item.status === 3" class="el-icon-caret-right btn" @click="handlePlay($event, item, index)"></i>
        </div>
      </el-col>
    </el-row>
    <el-empty v-if="total === 0" :description="$t('scene.scada.433893-1')"></el-empty>
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="pageNum"
      v-model:limit="pageSize"
      :pageSizes="[9, 18, 27, 36]"
      @pagination="getSipList"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import player from '@/views/components/player/player.vue';
import { playCom } from '@/api/iot/media/player';
import { closeStream, startProxy, stopProxy } from '@/api/iot/media/player';

const props = defineProps<{ sceneModels: any }>();

const pageNum = ref(1);
const pageSize = ref(9);
const total = ref(0);
const tempSipList = ref<any[]>([]);
const playerRefs = ref<Record<number, any>>({});

function setPlayerRef(el: any, index: number) {
  if (el) {
    playerRefs.value[index] = el;
  }
}

const sipList = computed(() => {
  const { sipRelationVOList } = props.sceneModels;
  if (!sipRelationVOList) return [];
  const start = (pageNum.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return sipRelationVOList.slice(start, end);
});

watch(sipList, () => {
  calculatePlayerHeight();
});

/** 获取窗体高度 */
function calculatePlayerHeight() {
  setTimeout(() => {
    sipList.value &&
      sipList.value.forEach((_item: any, index: number) => {
        const playerComp = playerRefs.value[index];
        if (playerComp?.$refs?.container) {
          let videoContainer = playerComp.$refs.container;
          const colEl = document.getElementById('sceneVideoCol');
          const offsetWidth = colEl?.offsetWidth;
          videoContainer.style.width = offsetWidth ? offsetWidth - 10 + 'px' : '300px';
          videoContainer.style.height = '230px';
        }
      });
  }, 100);
}

function getSipList(e: any) {
  pageNum.value = e.page;
  pageSize.value = e.limit;
}

/** 播放 */
function handlePlay(event: any, item: any, index: number) {
  const playerComp = playerRefs.value[index];
  if (!playerComp) return;

  if (item.channelType === '600' && item.playUrl) {
    playerComp.play(item.playUrl);
  } else if (item.channelType === '601' && item.proxyUrl) {
    startProxy(item.deviceId, item.channelId).then((res: any) => {
      playerComp.play(res.data.playurl);
    });
  } else {
    playCom(item.deviceId, item.channelId).then((res: any) => {
      if (res.code === 200) {
        if (!playerComp.isInit) {
          playerComp.init();
        }
        tempSipList.value[index].streamId = res.data.streamId;
        tempSipList.value[index].playurl = res.data.playurl;
        playerComp.play(res.data.playurl);
        event.target.style.visibility = 'hidden';
      }
    });
  }
}

/** 关闭播放器 */
function handleClose() {
  if (sipList.value && sipList.value.length !== 0) {
    sipList.value.forEach((item: any, index: number) => {
      if (tempSipList.value[index]?.streamId) {
        closeStream(item.deviceId, item.channelId, tempSipList.value[index].streamId).then(() => {});
      }
      if (item.proxyUrl) {
        stopProxy(item.deviceId, item.channelId).then((res: any) => {
          console.log(res);
        });
      }
      playerRefs.value[index]?.close();
    });
  }
}

onMounted(() => {
  const { sipRelationVOList } = props.sceneModels;
  calculatePlayerHeight();
  window.addEventListener('resize', calculatePlayerHeight, true);
  tempSipList.value = sipRelationVOList || [];
  total.value = sipRelationVOList?.length || 0;
});

onUnmounted(() => {
  handleClose();
  window.removeEventListener('resize', calculatePlayerHeight, true);
});

defineExpose({ handleClose });
</script>

<style lang="scss" scoped>
.scenario-list-video {
  position: relative;
  width: 100%;
  height: 100%;
  padding-bottom: 15px;

  .video {
    position: relative;
    width: 370px;
    height: 230px;

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
:deep(.pagination-container) {
  background: rgba(255, 255, 255, 0);
}
</style>
