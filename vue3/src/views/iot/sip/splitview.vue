<template>
  <div class="iot-sip-split-view">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card style="height: 729px; overflow: hidden">
          <DeviceTree :clickEvent="clickEvent"></DeviceTree>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card>
          <el-row :gutter="10" style="margin-bottom: 16px">
            <el-col :span="1.5">
              <el-button :class="{ active: spilt == 1 }" @click="spilt = 1" plain size="small">
                <svg-icon class-name="search-icon" icon-class="single_screen" />
                {{ $t('sip.splitview.998531-2') }}
              </el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button :class="{ active: spilt == 4 }" @click="spilt = 4" plain :icon="Menu" size="small">
                {{ $t('sip.splitview.998531-3') }}
              </el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button :class="{ active: spilt == 9 }" @click="spilt = 9" plain :icon="Grid" size="small">
                {{ $t('sip.splitview.998531-4') }}
              </el-button>
            </el-col>
            <!-- 全屏 -->
            <el-col :span="1.5">
              <el-button
                :class="{ active: isFullscreen }"
                @click="toggleFullscreen"
                plain
                :icon="FullScreen"
                size="small"
              >
                {{ $t('navbar.full') }}
              </el-button>
            </el-col>
          </el-row>
          <div ref="playContainer" style="height: 639px; display: flex; flex-wrap: wrap; gap: 1px">
            <!-- 为播放窗口添加选中高亮效果 -->
            <div
              v-for="i in spilt"
              :key="i"
              class="play-box"
              :style="liveStyle"
              @click="handlePlayerSelect(i - 1)"
              :class="{ 'play-box-selected': spilt > 1 && playerIdx === i - 1 }"
            >
              <div v-if="!videoUrl[i - 1]" style="color: #ffffff; font-size: 30px; font-weight: bold">{{ i }}</div>
              <player
                ref="playerRefs"
                v-else
                :videoUrl="videoUrl[i - 1]"
                fluent
                autoplay
                @screenshot="shot"
                @destroy="destroy"
                class="player-wrap"
              />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Menu, Grid, FullScreen } from '@element-plus/icons-vue';
import player from '@/views/components/player/jessibuca.vue';
import DeviceTree from '@/views/components/player/DeviceTree.vue';
import { startPlay } from '@/api/iot/channel';

const route = useRoute();
const router = useRouter();

const videoUrl = ref(['']);
const spilt = ref(1); //分屏
const playerIdx = ref(0); //激活播放器索引
const updateLooper = ref(0); //数据刷新轮训标志
const count = ref(15);
const total = ref(0);
const loading = ref(false);
const isFullscreen = ref(false); // 标记是否全屏状态
const playContainer = ref(null);
const playerRefs = ref([]);

// 点击事件
const clickEvent = (data) => {
  if (data.channelId) {
    sendDevicePush(data);
  }
};

// 全屏切换方法
const toggleFullscreen = () => {
  const container = playContainer.value;
  if (!container) return;

  if (isFullscreen.value) {
    // 退出全屏
    if (document.exitFullscreen) {
      document.exitFullscreen();
    } else if (document.webkitExitFullscreen) {
      document.webkitExitFullscreen();
    } else if (document.mozCancelFullScreen) {
      document.mozCancelFullScreen();
    } else if (document.msExitFullscreen) {
      document.msExitFullscreen();
    }
  } else {
    // 进入全屏
    if (container.requestFullscreen) {
      container.requestFullscreen();
    } else if (container.webkitRequestFullscreen) {
      container.webkitRequestFullscreen();
    } else if (container.mozRequestFullScreen) {
      container.mozRequestFullScreen();
    } else if (container.msRequestFullscreen) {
      container.msRequestFullscreen();
    }
  }
};

// 处理播放器选中
const handlePlayerSelect = (idx) => {
  playerIdx.value = idx;
};

// 通知设备上传媒体流
const sendDevicePush = (itemData) => {
  save(itemData);
  let deviceId = itemData.deviceId;
  let channelId = itemData.channelId;
  console.log('通知设备推流 1: ' + deviceId + ' : ' + channelId);
  let idxTmp = playerIdx.value;
  loading.value = true;

  startPlay(deviceId, channelId)
    .then((res) => {
      if (res.code === 200) {
        const data = res.data || {};
        console.log('开始播放：' + deviceId + ' : ' + channelId);
        console.log('流媒体信息：' + data);
        console.log('playurl: ' + data.playurl);
        console.log('protocol: ' + window.location.protocol);
        if (window.location.protocol === 'http:') {
          itemData.playUrl = data.ws_flv;
        } else {
          itemData.playUrl = data.wss_flv;
        }
        itemData.streamId = data.streamId;
        setPlayUrl(itemData.playUrl, idxTmp);
      }
    })
    .finally(() => {
      loading.value = false;
    });
};

const setPlayUrl = (url, idx) => {
  videoUrl.value[idx] = url;
  setTimeout(() => {
    localStorage.setItem('videoUrl', JSON.stringify(videoUrl.value));
  }, 100);
};

const checkPlayByParam = () => {
  let { deviceId, channelId } = route.query;
  if (deviceId && channelId) {
    sendDevicePush({ deviceId, channelId });
  }
};

const shot = (e) => {
  var base64ToBlob = function (code) {
    let parts = code.split(';base64,');
    let contentType = parts[0].split(':')[1];
    let raw = window.atob(parts[1]);
    let rawLength = raw.length;
    let uInt8Array = new Uint8Array(rawLength);
    for (let i = 0; i < rawLength; ++i) {
      uInt8Array[i] = raw.charCodeAt(i);
    }
    return new Blob([uInt8Array], {
      type: contentType,
    });
  };
  let aLink = document.createElement('a');
  let blob = base64ToBlob(e);
  let evt = document.createEvent('HTMLEvents');
  evt.initEvent('click', true, true);
  aLink.download = '截图';
  aLink.href = URL.createObjectURL(blob);
  aLink.click();
};

const save = (item) => {
  let dataStr = localStorage.getItem('playData') || '[]';
  let data = JSON.parse(dataStr);
  data[playerIdx.value] = item;
  localStorage.setItem('playData', JSON.stringify(data));
};

const clear = (idx) => {
  let dataStr = localStorage.getItem('playData') || '[]';
  let data = JSON.parse(dataStr);
  data[idx - 1] = null;
  console.log(data);
  localStorage.setItem('playData', JSON.stringify(data));
};

const destroy = (idx) => {
  console.log(idx);
  clear(idx.substring(idx.length - 1));
};

// 计算属性
const liveStyle = computed(() => {
  let style = { width: '100%', height: '100%' };
  switch (spilt.value) {
    case 4:
      style = { width: 'calc((100% - 1px) / 2)', height: 'calc((100% - 1px) / 2)' };
      break;
    case 9:
      style = { width: 'calc((100% - 2px) / 3)', height: 'calc((100% - 2px) / 3)' };
      break;
  }
  return style;
});

const resizeAllPlayers = () => {
  if (playerRefs.value && playerRefs.value.length > 0) {
    playerRefs.value.forEach((player) => {
      if (player?.resize) {
        player.resize();
      } else if (player?.updatePlayerDomSize) {
        player.updatePlayerDomSize();
      }
    });
  }
};

// 监听器
watch(spilt, (newValue) => {
  console.log('切换画幅;' + newValue);
  nextTick(() => {
    resizeAllPlayers();
    // 分屏切换后再次执行一次，避免播放器内部尺寸计算滞后导致偏移
    setTimeout(() => {
      resizeAllPlayers();
    }, 60);
  });
  localStorage.setItem('split', String(newValue));
});

watch(
  () => route.fullPath,
  () => {
    checkPlayByParam();
  }
);

watch(isFullscreen, () => {
  nextTick(() => {
    resizeAllPlayers();
    setTimeout(() => {
      resizeAllPlayers();
    }, 60);
  });
});

// 生命周期
onMounted(() => {
  // 监听全屏状态变化（ESC/按钮退出都会触发）
  document.addEventListener('fullscreenchange', handleFullscreenChange);

  // created 中的逻辑移到 mounted 中执行
  checkPlayByParam();
});

const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement;
};

onBeforeUnmount(() => {
  document.removeEventListener('fullscreenchange', handleFullscreenChange);

  // destroyed 中的清理逻辑移到 beforeUnmount 中执行
  clearTimeout(updateLooper.value);
});
</script>

<style lang="scss" scoped>
.iot-sip-split-view {
  padding: 20px;

  .play-box {
    background-color: #55565f;
    border: 1px solid #505050;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
  }

  // 播放窗口选中高亮效果
  .play-box-selected {
    border-color: #486ff2;
    box-shadow:
      0 0 0 2px #486ff2,
      0 0 10px rgba(72, 111, 242, 0.3);
  }

  .player-wrap {
    position: absolute;
    top: 0;
    left: 0;
    width: 100% !important;
    height: 100% !important;
    overflow: hidden;
    // transition: all 0.2s ease; // 添加过渡动画
  }

  // 播放器选中高亮效果
  .player-selected {
    box-shadow: 0 0 15px rgba(72, 111, 242, 0.5) inset;
  }

  .search-icon {
    margin-right: 4px;
    vertical-align: -1px;
  }

  .active {
    background: #ffffff;
    border-color: #486ff2;
    color: #486ff2;
  }

  :deep(.el-card__body) {
    overflow: hidden;
  }
}
</style>
