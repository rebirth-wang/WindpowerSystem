<template>
  <div class="root">
    <div class="container-shell">
      <div id="container" ref="containerRef"></div>
      <!-- 对讲 -->
      <div class="trank-wrap" v-if="type === 1">
        <el-tooltip :content="broadcastTooltip.content" placement="top" v-if="showBroadcastTooltip">
          <el-icon>
            <Mic @click="broadcastStatusClick()" :disabled="broadcastStatus === -2" />
          </el-icon>
        </el-tooltip>
      </div>
      <!-- 自定义倍速控制按钮 -->
      <el-dropdown class="playback-rate-control" trigger="click" @command="handleRateChange" v-if="type === 2">
        <el-button type="primary" link style="color: #fff">
          {{ currentRate }}x
          <i class="fa fa-caret-down"></i>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item
              v-for="rate in rateOptions"
              :key="rate"
              :command="rate"
              :class="{ selected: currentRate === rate }"
            >
              {{ rate }}x
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, getCurrentInstance, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import { ptzdirection } from '@/api/iot/sipdevice';
import { getPushUrl, startBroadcast, playbackSpeed } from '@/api/iot/media/player';
import { Mic } from '@element-plus/icons-vue';
const { t } = useI18n();
const { proxy } = getCurrentInstance() as any;

const props = defineProps<{
  playerinfo?: any;
  channelId?: string;
  deviceId?: string;
  streamId?: string;
  videoUrl?: string;
  type?: number;
}>();

let jessibucaPlayer: any = null;
let isDestroying = false;
let isDestroyed = false;
const uid = Math.random().toString(36).substring(2, 10);

const containerRef = ref<HTMLElement | null>(null);
const isPlaybackPause = ref(false);
const playbackStartTime = ref(0);
const useWebGPU = ref(false);
const isInit = ref(false);
const playinfo = ref<any>({});
const playtype = ref('play');
const operateBtns = ref({
  fullscreen: true,
  zoom: true,
  play: true,
  audio: true,
  ptz: false,
});
const broadcastRtc = ref<any>(null);
const broadcastStatus = ref(-1);
const currentRate = ref(1.0);
const rateOptions = [1, 2, 4];

const isMp4 = computed(() => {
  return props.videoUrl && props.videoUrl.toLowerCase().endsWith('.mp4');
});

const showBroadcastTooltip = computed(() => {
  return [-1, -2, 0, 1].includes(broadcastStatus.value);
});

const broadcastTooltip = computed<{ content: string }>(() => {
  const statusMap: Record<number, { content: string }> = {
    [-1]: { content: t('views.components.player.webrtc.08878-3') },
    [-2]: { content: t('views.components.player.webrtc.08878-2') },
    [0]: { content: t('views.components.player.webrtc.08878-4') },
    [1]: { content: t('views.components.player.webrtc.08878-5') },
  };
  return statusMap[broadcastStatus.value] || { content: '' };
});

function isMobile() {
  return /iphone|ipad|android.*mobile|windows.*phone|blackberry.*mobile/i.test(
    window.navigator.userAgent.toLowerCase()
  );
}

function isPad() {
  return /ipad|android(?!.*mobile)|tablet|kindle|silk/i.test(window.navigator.userAgent.toLowerCase());
}

function init() {
  const isSupportWebgpu = 'gpu' in navigator;
  useWebGPU.value = isSupportWebgpu;

  const useVconsole = isMobile() || isPad();
  if (useVconsole && (window as any).VConsole) {
    new (window as any).VConsole();
  }
  nextTick(() => {
    initplayer();
  });
}

function initconf() {
  if (playtype.value === 'play') {
    operateBtns.value.ptz = true;
  } else {
    operateBtns.value.ptz = false;
  }
}

function initplayer() {
  isPlaybackPause.value = false;
  initconf();

  isDestroying = false;
  isDestroyed = false;

  const playerConfig = {
    container: containerRef.value,
    decoder: '/js/jessibuca-pro/decoder-pro.js',
    videoBuffer: Number(0.2),
    isResize: false,
    useWCS: false,
    useMSE: isMp4.value ? true : false,
    useSIMD: true,
    wcsUseVideoRender: false,
    loadingText: '加载中',
    debug: false,
    debugLevel: 'debug',
    showBandwidth: !isMp4.value,
    showPlaybackOperate: true,
    operateBtns: operateBtns.value,
    forceNoOffscreen: true,
    isNotMute: true,
    showPerformance: false,
    playbackForwardMaxRateDecodeIFrame: 4,
    useWebGPU: useWebGPU.value,
    isLive: !isMp4.value,
    autoPlay: true,
    loop: false,
  };

  jessibucaPlayer = new (window as any).JessibucaPro(playerConfig);
  initcallback(jessibucaPlayer);
  isInit.value = true;
}

function initcallback(jessibuca: any) {
  jessibuca.on('error', (error: any) => {
    console.log('jessibuca error', error);
  });
  jessibuca.on('playFailedAndPaused', (reason: any, lastFrameInfo: any, msg: any) => {
    console.log('playFailedAndPaused', reason, msg);
  });
  jessibuca.on('visibilityChange', (value: boolean) => {
    console.log('visibilityChange', value);
  });
  jessibuca.on('pause', (pause: any) => {
    console.log('pause success!', pause);
  });
  jessibuca.on('play', (flag: any) => {
    console.log('play!', flag);
    if (playtype.value === 'playback') {
      jessibuca.setPlaybackStartTime(playbackStartTime.value);
    }
  });
  jessibuca.on('loading', (load: any) => {
    console.log('loading success!', load);
  });
  jessibuca.on('timeout', (error: any) => {
    console.log('timeout:', error);
  });
  jessibuca.on('playbackPreRateChange', (rate: any) => {
    jessibuca.forward(rate);
  });

  let pre = 0;
  jessibuca.on('timeUpdate', (ts: number) => {
    const cur = parseInt(String(ts / 60000));
    if (pre !== cur) {
      pre++;
    }
  });
  jessibuca.on((window as any).JessibucaPro?.EVENTS?.ptz || 'ptz', (arrow: string) => {
    console.log('ptz arrow', arrow);
    handlePtz(arrow);
  });
  jessibuca.on('crashLog', (data: any) => {
    console.log('crashLog is', data);
  });
}

async function handleRateChange(rate: number) {
  currentRate.value = rate;
  try {
    const query = { speed: rate };
    const res = (await playbackSpeed(props.deviceId, props.channelId, props.streamId, query)) as any;
    if (res.code !== 200) {
      proxy?.$modal?.msgError(res.msg);
    }
  } catch (error) {
    console.error('倍速设置失败:', error);
  }
}

function play(url?: string) {
  if (!url) return;
  if (jessibucaPlayer) {
    if (isMp4.value) {
      jessibucaPlayer.play(url, { isLive: false, autoPlay: true });
    } else {
      jessibucaPlayer.play(url);
    }
  }
}

function pause() {
  if (jessibucaPlayer) {
    jessibucaPlayer.pause();
  }
}

function replay(url?: string) {
  if (!url) return;
  if (jessibucaPlayer) {
    if (isDestroying || isDestroyed) return;
    isDestroying = true;
    jessibucaPlayer
      .destroy()
      .catch(() => {})
      .finally(() => {
        jessibucaPlayer = null;
        isDestroying = false;
        isDestroyed = true;
        initplayer();
        play(url);
      });
  } else {
    initplayer();
    play(url);
  }
}

function handlePtz(arrow: string) {
  let leftRight = 0;
  let upDown = 0;
  if (arrow === 'left') leftRight = 2;
  else if (arrow === 'right') leftRight = 1;
  else if (arrow === 'up') upDown = 1;
  else if (arrow === 'down') upDown = 2;

  const data = { leftRight, upDown, moveSpeed: 125 };
  if (playinfo.value && playinfo.value.playtype !== '') {
    ptzdirection(playinfo.value.deviceId, playinfo.value.channelId, data);
  }
}

function playback(url: string, playTimes: any) {
  if (jessibucaPlayer) {
    jessibucaPlayer.playback(url, {
      playList: playTimes,
      fps: 25,
      showControl: true,
      isUseFpsRender: true,
      isCacheBeforeDecodeForFpsRender: false,
      supportWheel: true,
    });
    isPlaybackPause.value = false;
  }
}

function playbackPause() {
  if (jessibucaPlayer) {
    jessibucaPlayer.playbackPause();
    isPlaybackPause.value = true;
  }
}

function replayback(url: string, playTimes: any) {
  if (jessibucaPlayer) {
    if (isDestroying || isDestroyed) return;
    isDestroying = true;
    jessibucaPlayer
      .destroy()
      .catch(() => {})
      .finally(() => {
        jessibucaPlayer = null;
        isDestroying = false;
        isDestroyed = true;
        initplayer();
        playback(url, playTimes);
      });
  } else {
    initplayer();
    playback(url, playTimes);
  }
}

function setPlaybackStartTime(curTime: number) {
  if (jessibucaPlayer) {
    jessibucaPlayer.setPlaybackStartTime(curTime);
    playbackStartTime.value = curTime;
  }
}

function destroy() {
  if (jessibucaPlayer) {
    if (isDestroying || isDestroyed) return;
    isDestroying = true;
    jessibucaPlayer
      .destroy()
      .catch(() => {})
      .finally(() => {
        jessibucaPlayer = null;
        isDestroying = false;
        isDestroyed = true;
        initplayer();
      });
  }
}

function close() {
  if (jessibucaPlayer) {
    jessibucaPlayer.close();
  }
}

function registercallback(events: string, func: Function) {
  if (jessibucaPlayer) {
    jessibucaPlayer.on(events, func);
  }
}

function broadcastStatusClick() {
  if (broadcastStatus.value === -1) {
    broadcastStatus.value = 0;
    getPushUrl(props.deviceId, props.channelId).then((res: any) => {
      if (res.code === 200) {
        const streamInfo = res.data;
        if (document.location.protocol.includes('https')) {
          startwebrtc(streamInfo.rtcs);
        } else {
          startwebrtc(streamInfo.rtc);
        }
      } else {
        proxy?.$modal?.msgError(res.msg);
      }
    });
  } else if (broadcastStatus.value === 1) {
    broadcastStatus.value = -1;
    broadcastRtc.value?.close();
  }
}

function startwebrtc(url: string) {
  const ZLMRTCClient = (window as any).ZLMRTCClient;
  if (!ZLMRTCClient) return;

  broadcastRtc.value = new ZLMRTCClient.Endpoint({
    debug: true,
    zlmsdpUrl: url,
    simulecast: false,
    useCamera: false,
    audioEnable: true,
    videoEnable: false,
    recvOnly: false,
  });

  broadcastRtc.value.on(ZLMRTCClient.Events.WEBRTC_NOT_SUPPORT, () => {
    proxy?.$modal?.msgError(t('views.components.player.webrtc.08878-6'));
    broadcastStatus.value = -1;
  });
  broadcastRtc.value.on(ZLMRTCClient.Events.WEBRTC_ICE_CANDIDATE_ERROR, () => {
    proxy?.$modal?.msgError(t('views.components.player.webrtc.08878-7'));
    broadcastStatus.value = -1;
  });
  broadcastRtc.value.on(ZLMRTCClient.Events.WEBRTC_OFFER_ANWSER_EXCHANGE_FAILED, (e: any) => {
    proxy?.$modal?.msgError(t('views.components.player.webrtc.08878-8') + e);
    broadcastStatus.value = -1;
  });
  broadcastRtc.value.on(ZLMRTCClient.Events.WEBRTC_ON_CONNECTION_STATE_CHANGE, (e: string) => {
    if (e === 'connecting') {
      broadcastStatus.value = 0;
    } else if (e === 'connected') {
      broadcastStatus.value = 1;
      startBroadcast(props.deviceId, props.channelId);
    } else if (e === 'disconnected' || e === 'failed') {
      broadcastStatus.value = -1;
    }
  });
  broadcastRtc.value.on(ZLMRTCClient.Events.CAPTURE_STREAM_FAILED, (e: any) => {
    proxy?.$modal?.msgError(t('views.components.player.webrtc.08878-9') + e);
    broadcastStatus.value = -1;
  });
}

watch(
  () => props.playerinfo,
  (newVal) => {
    if (newVal) {
      playinfo.value = newVal;
      if (playinfo.value.playtype) {
        playtype.value = playinfo.value.playtype;
      }
    }
  }
);

watch(
  () => props.videoUrl,
  (newVal) => {
    if (newVal) {
      replay(newVal);
    } else {
      close();
    }
  }
);

onMounted(() => {
  playinfo.value = props.playerinfo || {};
  if (playinfo.value.playtype) {
    playtype.value = playinfo.value.playtype;
  }
  init();
  if (props.videoUrl) {
    play(props.videoUrl);
  }
});

onBeforeUnmount(() => {
  if (jessibucaPlayer && !isDestroying && !isDestroyed) {
    isDestroying = true;
    jessibucaPlayer
      .destroy()
      .catch(() => {})
      .finally(() => {
        jessibucaPlayer = null;
        isDestroying = false;
        isDestroyed = true;
      });
  }
  broadcastRtc.value?.close();
});

defineExpose({
  play,
  pause,
  replay,
  playback,
  playbackPause,
  replayback,
  setPlaybackStartTime,
  destroy,
  close,
  registercallback,
});
</script>

<style scoped lang="scss">
.root {
  display: flex;
  margin-right: 3rem;
}

.container-shell {
  backdrop-filter: blur(5px);
  position: relative;
  border-radius: 10px;
}

.container-shell:before {
  position: absolute;
  color: darkgray;
  left: 10px;
}

#container {
  background: rgba(13, 14, 27, 0.7);
  width: 1200px;
  height: 630px;
  border-radius: 5px;
}

.err {
  position: absolute;
  top: 40px;
  left: 10px;
  color: red;
}

@media (max-width: 720px) {
  #container {
    width: 90vw;
    height: 52.7vw;
  }
}

.playback-rate-control {
  position: absolute;
  bottom: 6px;
  right: 210px;
  z-index: 100;
}

.selected {
  color: #409eff;
  font-weight: bold;
}

.trank-wrap {
  position: absolute;
  right: 180px;
  bottom: 8px;
  z-index: 10;
  color: #fff;
  cursor: pointer;
}

:deep(.jb-pro-controls-playback-time-inner) {
  width: 350px !important;
}
</style>
