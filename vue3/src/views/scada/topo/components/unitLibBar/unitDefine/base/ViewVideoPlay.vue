<template>
  <div
    class="view-video-play"
    :id="detail.identifier"
    :style="{ width: componentWidth, height: componentHeight }"
  >
    <video-player
      v-if="hasVideoUrl"
      :key="playerKey"
      class="vjs-custom-skin"
      :style="{ width: componentWidth, height: componentHeight }"
      :playsinline="true"
      :fill="true"
      :options="playerOptions"
    />
    <div v-else class="video-placeholder">
      <div class="video-title">通用视频</div>
      <div class="video-desc">{{ editMode ? '编辑态预览' : '请先配置视频地址' }}</div>
    </div>
  </div>
</template>

<script lang="ts">
import BaseView from '../View.vue';

export default {
  name: 'ViewVideoPlay',
  extends: BaseView,
  props: {},
  computed: {
    videoUrl() {
      return String(this.detail?.dataBind?.videoUrl || '').trim();
    },
    imageUrl() {
      return String(this.detail?.dataBind?.imageUrl || '').trim();
    },
    hasVideoUrl() {
      return Boolean(this.videoUrl);
    },
    componentWidth() {
      return `${this.detail?.style?.position?.w || 0}px`;
    },
    componentHeight() {
      return `${this.detail?.style?.position?.h || 0}px`;
    },
    playerKey() {
      return `${this.detail.identifier}_${this.videoUrl}_${this.imageUrl}`;
    },
    playerOptions() {
      return {
        controls: true,
        playbackRates: [0.5, 1.0, 1.5, 2.0],
        autoplay: false,
        muted: false,
        loop: false,
        preload: 'auto',
        language: 'zh-CN',
        fluid: false,
        fill: true,
        width: this.detail?.style?.position?.w || undefined,
        height: this.detail?.style?.position?.h || undefined,
        sources: [
          {
            type: 'video/mp4',
            src: this.videoUrl,
          },
        ],
        poster: this.imageUrl || undefined,
        notSupportedMessage: '当前视频地址无法播放，请检查地址或格式',
        controlBar: {
          timeDivider: true,
          durationDisplay: true,
          remainingTimeDisplay: false,
          fullscreenToggle: true,
        },
      };
    },
  },
};
</script>

<style lang="scss" scoped>
.view-video-play {
  width: 100%;
  height: 100%;
  background: #000;
  overflow: hidden;

  :deep(.video-js),
  :deep(.vjs-tech) {
    width: 100%;
    height: 100%;
  }

  :deep(.vjs-custom-skin .vjs-big-play-button) {
    font-size: 3em;
  }
}

.video-placeholder {
  width: 100%;
  height: 100%;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.04);
  color: #909399;
  box-sizing: border-box;
}

.video-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.video-desc {
  font-size: 12px;
  text-align: center;
  padding: 0 12px;
}
</style>
