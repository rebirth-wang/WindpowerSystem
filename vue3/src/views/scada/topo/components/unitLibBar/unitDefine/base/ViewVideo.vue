<template>
  <div class="view-video" :id="detail.identifier">
    <el-image
      v-if="editMode"
      class="video-cover"
      :style="playerStyle"
      :src="imageUrl"
      @dragstart.prevent
      @dragover.prevent
      @drop.prevent
    ></el-image>
    <template v-else>
      <div v-if="playerError" class="video-placeholder" :style="playerStyle">
        <div class="video-title">{{ detail.style?.title || '萤石云视频' }}</div>
        <div class="video-desc">{{ playerError }}</div>
      </div>
      <div v-else :id="playerContainerId" class="ezviz-player" :style="playerStyle"></div>
    </template>
  </div>
</template>

<script lang="ts">
import { EZUIKitPlayer } from 'ezuikit-js';
import BaseView from '../View.vue';
import videoImage from '@/assets/images/scada/video_monitor.png';

export default {
  name: 'ViewVideo',
  extends: BaseView,
  props: {},
  data() {
    return {
      imageUrl: videoImage,
      player: null as any,
      playerError: '',
      containerSeed: Math.random().toString(36).slice(2, 10),
      initTimer: undefined as number | undefined,
    };
  },
  computed: {
    dataBind(): any {
      return this.detail?.dataBind || {};
    },
    deviceSerial(): string {
      return String(this.dataBind.serialNumber || '').trim().toUpperCase();
    },
    channelNo(): number {
      const channel = Number(this.dataBind.ezvizChannel || 1);
      return Number.isFinite(channel) && channel > 0 ? channel : 1;
    },
    accessToken(): string {
      return String(this.dataBind.ezvizToken || '').trim();
    },
    playUrl(): string {
      return `ezopen://open.ys7.com/${this.deviceSerial}/${this.channelNo}.live`;
    },
    playerContainerId(): string {
      const identifier = String(this.detail?.identifier || this.containerSeed).replace(/[^A-Za-z0-9_-]/g, '_');
      return `ezviz-player-${identifier}`;
    },
    playerWidth(): number {
      return Number(this.detail?.style?.position?.w) || 433;
    },
    playerHeight(): number {
      return Number(this.detail?.style?.position?.h) || 225;
    },
    playerStyle(): Record<string, string> {
      return {
        width: `${this.playerWidth}px`,
        height: `${this.playerHeight}px`,
      };
    },
  },
  watch: {
    editMode() {
      this.reinitPlayer();
    },
    'detail.dataBind.serialNumber'() {
      this.reinitPlayer();
    },
    'detail.dataBind.ezvizChannel'() {
      this.reinitPlayer();
    },
    'detail.dataBind.ezvizToken'() {
      this.reinitPlayer();
    },
  },
  mounted() {
    this.initPlayer();
  },
  methods: {
    validatePlayerConfig() {
      if (!this.deviceSerial) {
        return '请先配置萤石云设备序列号';
      }
      if (!this.accessToken) {
        return '请先配置萤石云 accessToken';
      }
      return '';
    },
    initPlayer() {
      if (this.editMode) {
        this.destroyPlayer();
        this.playerError = '';
        return;
      }

      this.destroyPlayer();
      this.playerError = this.validatePlayerConfig();
      if (this.playerError) return;

      this.$nextTick(() => {
        const container = document.getElementById(this.playerContainerId);
        if (!container) {
          this.playerError = '萤石云播放器容器不存在，请重新打开预览';
          return;
        }

        try {
          this.player = new EZUIKitPlayer({
            id: this.playerContainerId,
            accessToken: this.accessToken,
            url: this.playUrl,
            width: this.playerWidth,
            height: this.playerHeight,
            scaleMode: 1,
            template: 'pcLive',
            handleError: (err: any) => {
              console.error('萤石云视频播放失败:', err);
              this.playerError = this.formatPlayerError(err);
              this.destroyPlayer();
            },
          });
        } catch (error) {
          console.error('萤石云播放器初始化失败:', error);
          this.playerError = this.formatPlayerError(error);
        }
      });
    },
    reinitPlayer() {
      if (this.initTimer) {
        window.clearTimeout(this.initTimer);
      }
      this.initTimer = window.setTimeout(() => {
        this.initTimer = undefined;
        this.initPlayer();
      }, 100);
    },
    formatPlayerError(error: any) {
      const code = error?.data?.nErrorCode || error?.code || error?.errorCode;
      const message = error?.message || error?.msg || error?.data?.message || error?.data?.msg;

      if (code) {
        return `萤石云视频播放失败，错误码：${code}${message ? `，${message}` : ''}`;
      }
      if (message) {
        return `萤石云视频播放失败：${message}`;
      }
      return '萤石云视频播放失败，请检查 accessToken、设备序列号、通道号和设备在线状态';
    },
    destroyPlayer() {
      if (!this.player) return;

      try {
        const result = this.player.destroy?.();
        if (result?.catch) {
          result.catch((error: any) => console.warn('萤石云播放器销毁失败:', error));
        }
      } catch (error) {
        console.warn('萤石云播放器销毁失败:', error);
      } finally {
        this.player = null;
      }
    },
  },
  beforeUnmount() {
    if (this.initTimer) {
      window.clearTimeout(this.initTimer);
      this.initTimer = undefined;
    }
    this.destroyPlayer();
  },
};
</script>

<style lang="scss">
.view-video {
  height: 100%;
  width: 100%;
}

.video-cover,
.ezviz-player {
  display: block;
}

.video-placeholder {
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
