<template>
  <div class="view-video-mp4-wrap" :id="detail.identifier">
    <video
      v-if="videoUrl && !errorMessage"
      ref="videoRef"
      class="view-video-mp4"
      controls
      autoplay
      muted
      :width="detail.style.position.w"
      :height="detail.style.position.h"
      :src="nativeVideoSrc"
    ></video>
    <div v-else class="video-placeholder">
      <div class="video-title">{{ detail.style?.title || '视频组件' }}</div>
      <div class="video-desc">{{ errorMessage || (editMode ? '编辑态预览' : '请配置直播视频地址') }}</div>
    </div>
  </div>
</template>

<script lang="ts">
import flvjs from 'flv.js';
import BaseView from '../View.vue';

export default {
  name: 'ViewVideoMp4',
  extends: BaseView,
  props: {},
  data() {
    return {
      videoUrl: '',
      flvPlayer: null as any,
      errorMessage: '',
    };
  },
  computed: {
    videoUrlInit() {
      this.videoUrl = this.detail.dataBind.videoUrl || '';
      return this.videoUrl;
    },
    nativeVideoSrc() {
      return this.isFlvUrl(this.videoUrl) ? '' : this.videoUrl;
    },
  },
  watch: {
    'detail.dataBind.videoUrl'() {
      this.initPlayer();
    },
  },
  mounted() {
    this.videoUrl = this.detail.dataBind.videoUrl || '';
    this.$nextTick(() => {
      this.initPlayer();
    });
  },
  beforeUnmount() {
    this.destroyFlvPlayer();
  },
  methods: {
    isFlvUrl(url: string) {
      return /\.flv($|\?)/i.test(url) || /^wss?:\/\//i.test(url);
    },
    isLiveFlvUrl(url: string) {
      return /^wss?:\/\//i.test(url) || /[?&]live=1(&|$)/i.test(url) || /\/live\//i.test(url);
    },
    getVideoElement() {
      return this.$refs.videoRef as HTMLVideoElement | undefined;
    },
    initPlayer() {
      this.destroyFlvPlayer();
      this.videoUrl = this.detail.dataBind.videoUrl || '';
      this.errorMessage = '';

      if (!this.videoUrl) return;

      this.$nextTick(() => {
        const videoElement = this.getVideoElement();
        if (!videoElement) return;

        if (this.isFlvUrl(this.videoUrl)) {
          if (!flvjs.isSupported()) {
            this.errorMessage = '当前浏览器不支持 FLV 直播播放';
            return;
          }

          this.flvPlayer = flvjs.createPlayer({
            type: 'flv',
            isLive: this.isLiveFlvUrl(this.videoUrl),
            hasAudio: false,
            cors: true,
            url: this.videoUrl,
          });
          this.flvPlayer.attachMediaElement(videoElement);
          this.flvPlayer.on(flvjs.Events.ERROR, (errorType: string, errorDetail: string) => {
            this.errorMessage = `FLV 播放失败：${errorType || ''}${errorDetail ? ` / ${errorDetail}` : ''}`;
          });
          this.flvPlayer.load();
          this.flvPlayer.play()?.catch?.(() => {});
          return;
        }

        videoElement.load();
        videoElement.play()?.catch?.(() => {});
      });
    },
    destroyFlvPlayer() {
      if (!this.flvPlayer) return;
      this.flvPlayer.pause?.();
      this.flvPlayer.unload?.();
      this.flvPlayer.detachMediaElement?.();
      this.flvPlayer.destroy?.();
      this.flvPlayer = null;
    },
    play() {
      const video = this.getVideoElement();
      video?.play?.();
    },
  },
};
</script>

<style lang="scss" scoped>
.view-video-mp4-wrap {
  width: 100%;
  height: 100%;
}

.view-video-mp4 {
  background-color: black;
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
