<template>
  <div
    ref="container"
    @dblclick="fullscreenSwitch"
    style="width: 100%; height: 100%; background-color: #000000; margin: 0 auto"
  ></div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount } from 'vue';
import { useRoute } from 'vue-router';

const props = defineProps<{
  videoUrl?: string;
  error?: string;
  hasAudio?: boolean;
  height?: string | number;
}>();

const route = useRoute();
const container = ref<HTMLElement | null>(null);
const isMounted = ref(false);
const pendingPlayUrl = ref('');

const playing = ref(false);
const isNotMute = ref(false);
const quieting = ref(false);
const fullscreen = ref(false);
const loaded = ref(false);
const speed = ref(0);
const performance = ref('');
const kBps = ref(0);
const btnDom = ref<HTMLElement | null>(null);
const videoInfo = ref<any>(null);
const volume = ref(1);
const rotate = ref(0);
const vod = ref(true);
const forceNoOffscreen = ref(false);

let jessibucaPlayer: any = null;
const uid = Math.random().toString(36).substring(2, 10);

function updatePlayerDomSize() {
  const dom = container.value;
  if (!dom || !dom.parentNode) return;
  let width = (dom.parentNode as HTMLElement).clientWidth;
  let height = (9 / 16) * width;
  const clientHeight = Math.min(document.body.clientHeight, document.documentElement.clientHeight);
  if (height > clientHeight) {
    height = clientHeight;
    width = (16 / 9) * height;
  }
  dom.style.width = width + 'px';
  dom.style.height = height + 'px';
}

function create() {
  jessibucaPlayer = new (window as any).JessibucaPro({
    container: container.value,
    autoWasm: true,
    background: '',
    controlAutoHide: false,
    debug: false,
    debugLevel: 'debug',
    decoder: '/js/jessibuca-pro/decoder-pro.js',
    forceNoOffscreen: true,
    hasAudio: typeof props.hasAudio === 'undefined' ? true : props.hasAudio,
    hasVideo: true,
    heartTimeout: 5,
    heartTimeoutReplay: true,
    heartTimeoutReplayTimes: 3,
    hiddenAutoPause: false,
    hotKey: false,
    isFlv: false,
    isFullResize: false,
    isNotMute: isNotMute.value,
    isResize: false,
    keepScreenOn: false,
    loadingText: '请稍等, 视频加载中......',
    loadingTimeout: 10,
    loadingTimeoutReplay: true,
    loadingTimeoutReplayTimes: 3,
    openWebglAlignment: false,
    operateBtns: {
      fullscreen: true,
      zoom: true,
      ptz: false,
      play: true,
    },
    recordType: 'webm',
    rotate: 0,
    showBandwidth: false,
    supportDblclickFullscreen: false,
    timeout: 10,
    useMSE: location.hostname !== 'localhost' && location.protocol !== 'https:',
    useOffscreen: false,
    useWCS: location.hostname === 'localhost' || location.protocol === 'https:',
    useWebFullScreen: false,
    videoBuffer: 0,
    wasmDecodeAudioSyncVideo: true,
    wasmDecodeErrorReplay: true,
    wcsUseVideoRender: true,
  });

  jessibucaPlayer.on('load', () => {
    console.log('on load init');
  });
  jessibucaPlayer.on('log', (msg: any) => {
    console.log('on log', msg);
  });
  jessibucaPlayer.on('record', (msg: any) => {
    console.log('on record:', msg);
  });
  jessibucaPlayer.on('pause', () => {
    playing.value = false;
    loaded.value = true;
  });
  jessibucaPlayer.on('play', () => {
    playing.value = true;
    loaded.value = true;
  });
  jessibucaPlayer.on('fullscreen', (msg: any) => {
    console.log('on fullscreen', msg);
    fullscreen.value = msg;
  });
  jessibucaPlayer.on('mute', (msg: any) => {
    console.log('on mute', msg);
    isNotMute.value = !msg;
  });
  jessibucaPlayer.on('audioInfo', (msg: any) => {
    console.log('audioInfo', msg);
  });
  let _ts = 0;
  jessibucaPlayer.on('timeUpdate', (ts: number) => {
    _ts = ts;
  });
  jessibucaPlayer.on('performance', (perf: number) => {
    let show = '卡顿';
    if (perf === 2) {
      show = '非常流畅';
    } else if (perf === 1) {
      show = '流畅';
    }
    performance.value = show;
  });
  jessibucaPlayer.on('kBps', (val: number) => {
    kBps.value = Math.round(val);
  });
}

function play(url?: string) {
  if (!url) return;
  if (!container.value) {
    pendingPlayUrl.value = url;
    return;
  }

  if (jessibucaPlayer) {
    destroy().then(() => {
      if (jessibucaPlayer && jessibucaPlayer.hasLoaded()) {
        jessibucaPlayer.play(url);
      } else {
        jessibucaPlayer.on('load', () => {
          console.log('load 播放');
          jessibucaPlayer.play(url);
        });
      }
    });
  } else {
    create();
    if (jessibucaPlayer && jessibucaPlayer.hasLoaded()) {
      jessibucaPlayer.play(url);
    } else {
      jessibucaPlayer.on('load', () => {
        console.log('load 播放');
        jessibucaPlayer.play(url);
      });
    }
  }
}

function pause() {
  if (jessibucaPlayer) {
    jessibucaPlayer.pause();
  }
  playing.value = false;
  performance.value = '';
}

function screenshot() {
  if (jessibucaPlayer) {
    jessibucaPlayer.screenshot();
  }
}

function mute() {
  if (jessibucaPlayer) {
    jessibucaPlayer.mute();
  }
}

function cancelMute() {
  if (jessibucaPlayer) {
    jessibucaPlayer.cancelMute();
  }
}

async function destroy() {
  if (jessibucaPlayer) {
    await jessibucaPlayer.destroy();
    jessibucaPlayer = null;
    playing.value = false;
    create();
  }
}

function fullscreenSwitch() {
  const isFull = isFullscreen();
  jessibucaPlayer?.setFullscreen(!isFull);
  fullscreen.value = !isFull;
}

function isFullscreen() {
  return (
    document.fullscreenElement ||
    (document as any).msFullscreenElement ||
    (document as any).mozFullScreenElement ||
    (document as any).webkitFullscreenElement ||
    false
  );
}

watch(
  () => props.videoUrl,
  (newVal) => {
    if (newVal && isMounted.value) {
      play(newVal);
    } else if (newVal) {
      pendingPlayUrl.value = newVal;
    }
  },
  { immediate: true }
);

onMounted(() => {
  isMounted.value = true;
  window.onerror = () => {};
  updatePlayerDomSize();
  window.onresize = () => {
    updatePlayerDomSize();
  };
  const paramUrl = route.params.url ? decodeURIComponent(route.params.url as string) : '';
  if (!props.videoUrl && paramUrl) {
    pendingPlayUrl.value = paramUrl;
  }
  btnDom.value = document.getElementById('buttonsBox');
  if (pendingPlayUrl.value) {
    const url = pendingPlayUrl.value;
    pendingPlayUrl.value = '';
    play(url);
  }
});

onBeforeUnmount(() => {
  if (jessibucaPlayer) {
    jessibucaPlayer.destroy();
  }
  playing.value = false;
  loaded.value = false;
  performance.value = '';
});

defineExpose({
  play,
  pause,
  screenshot,
  mute,
  cancelMute,
  destroy,
  fullscreenSwitch,
});
</script>

<style>
@import '../css/iconfont.css';
.buttons-box {
  width: 100%;
  height: 28px;
  background-color: rgba(43, 51, 63, 0.7);
  position: absolute;
  display: flex;
  left: 0;
  bottom: 0;
  user-select: none;
  z-index: 10;
}

.jessibuca-btn {
  width: 20px;
  color: rgb(255, 255, 255);
  line-height: 27px;
  margin: 0px 10px;
  padding: 0px 2px;
  cursor: pointer;
  text-align: center;
  font-size: 0.8rem !important;
}

.buttons-box-right {
  position: absolute;
  right: 0;
}
</style>
