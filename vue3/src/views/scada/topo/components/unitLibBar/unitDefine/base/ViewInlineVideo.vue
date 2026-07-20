<template>
  <div class="inline-video" :id="detail.identifier + '_inline_video'">
    <el-image
      v-if="editMode"
      class="cover-image"
      :src="videoCover"
      alt="video cover"
      @dragstart.prevent
      @dragover.prevent
      @drop.prevent
    />
    <div v-else-if="loading || (statusMessage && channelList.length === 0)" class="video-placeholder">
      <div class="video-title">视频监控</div>
      <div class="video-desc">{{ loading ? '视频通道加载中...' : statusMessage }}</div>
    </div>
    <div class="video-wrap" v-else>
      <div v-if="statusMessage" class="runtime-message">{{ statusMessage }}</div>
      <el-tabs
        v-if="channelList.length > 1"
        class="tab-wrap"
        v-model="tabActive"
        type="card"
        @tab-click="handleTabClick"
      >
        <el-tab-pane
          v-for="(item, index) in channelList"
          :key="getChannelKey(item, index)"
          :label="getChannelName(item, index)"
          :name="getChannelKey(item, index)"
        >
          <div :style="getVideoBoxStyle(true)" :ref="`inlineVideo_${index}`"></div>
          <span v-if="getChannelStatusText(item)" class="status" :style="{ color: getChannelStatusColor(item) }">
            {{ getChannelStatusText(item) }}
          </span>
        </el-tab-pane>
      </el-tabs>
      <div v-else class="tab-wrap">
        <div :style="getVideoBoxStyle(false)" ref="inlineVideo_0"></div>
        <span v-if="getChannelStatusText(channelList[0])" class="status" :style="{ color: getChannelStatusColor(channelList[0]) }">
          {{ getChannelStatusText(channelList[0]) }}
        </span>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { useRoute } from 'vue-router';
import BaseView from '../View.vue';
import videoCover from '@/assets/images/scada/video_cover.png';
import { ptzdirection } from '@/api/iot/sipdevice';
import { startPlay, closeStream, startProxy, stopProxy } from '@/api/iot/channel';
import { listComRelDeviceOrScene } from '@/api/iot/media/channel';
import { getScadaRouteType, getRouteQueryString } from '@/utils/topo/topoUtil';

export default {
  name: 'ViewInlineVideo',
  extends: BaseView,
  setup() {
    const route = useRoute();
    return { route };
  },
  props: {},
  data() {
    return {
      videoCover,
      channelList: [] as any[],
      tabActive: '',
      jessibucaList: [] as any[],
      loading: false,
      statusMessage: '',
      activeIndex: -1,
    };
  },
  watch: {
    editMode(value: boolean) {
      if (value) {
        this.handleCloseJessibuca();
      } else {
        this.getVideoChannelList();
      }
    },
    'detail.dataBind.serialNumber'() {
      if (!this.editMode) {
        this.handleCloseJessibuca();
        this.getVideoChannelList();
      }
    },
  },
  mounted() {
    if (!this.editMode) {
      this.getVideoChannelList();
    }
  },
  methods: {
    getChannelKey(item: any, index: number) {
      return String(item?.channelSipId || item?.channelId || index);
    },
    getChannelName(item: any, index: number) {
      return item?.channelName || item?.name || `通道${index + 1}`;
    },
    getDeviceId(item: any) {
      return item?.deviceSipId || item?.deviceId || item?.sipDeviceId || '';
    },
    getChannelId(item: any) {
      return item?.channelId || item?.channelSipId || '';
    },
    getProxyChannelId(item: any) {
      return item?.channelId || item?.channelSipId || '';
    },
    getChannelStatusText(item: any) {
      const status = Number(item?.status);
      if (status === 1) return '未激活';
      if (status === 2) return '已禁用';
      if (status === 4) return '离线';
      return '';
    },
    getChannelStatusColor(item: any) {
      const status = Number(item?.status);
      if (status === 1) return '#ffba00';
      if (status === 2) return '#ff4949';
      if (status === 4) return '#909399';
      return '#909399';
    },
    getVideoBoxStyle(hasTabs: boolean) {
      const position = this.detail?.style?.position || {};
      const height = Math.max(Number(position.h || 0) - (hasTabs ? 22 : 0), 80);
      return {
        borderBottomRightRadius: `${this.detail?.style?.borderRadius || 0}px`,
        borderBottomLeftRadius: `${this.detail?.style?.borderRadius || 0}px`,
        opacity: this.detail?.style?.opacity ?? 1,
        width: `${position.w || 0}px`,
        height: `${height}px`,
        background: 'rgba(13, 14, 27, 0.7)',
      };
    },
    getContainerRef(index: number) {
      if (this.channelList.length === 1) {
        return this.$refs.inlineVideo_0 as HTMLElement | undefined;
      }
      const ref = this.$refs[`inlineVideo_${index}`] as HTMLElement[] | HTMLElement | undefined;
      return Array.isArray(ref) ? ref[0] : ref;
    },
    isChannelPlayable(item: any) {
      const status = Number(item?.status);
      return !status || status === 3;
    },
    getJessibucaCtor() {
      return (window as any).JessibucaPro;
    },
    initVideo(index = 0) {
      const JessibucaCtor = this.getJessibucaCtor();
      if (!JessibucaCtor) {
        this.statusMessage = 'JessibucaPro 未加载，无法播放视频监控';
        return;
      }
      const channel = this.channelList[index];
      if (channel && this.isChannelPlayable(channel)) {
        this.jessibucaList[index] = this.initJessibuca(index);
      }
    },
    initJessibuca(index: number) {
      const JessibucaCtor = this.getJessibucaCtor();
      const container = this.getContainerRef(index);
      if (!JessibucaCtor || !container) {
        this.statusMessage = '视频容器初始化失败';
        return null;
      }

      const isWebGPU = 'gpu' in navigator;
      const operateBtns = {
        fullscreen: true,
        zoom: true,
        ptz: true,
        play: true,
      };
      const jessibuca = new JessibucaCtor({
        container,
        decoder: '/js/jessibuca-pro/decoder-pro.js',
        videoBuffer: Number(0.2),
        isResize: false,
        useWCS: false,
        useMSE: false,
        useSIMD: true,
        wcsUseVideoRender: false,
        loadingText: '视频加载中...',
        debug: false,
        showBandwidth: true,
        showPlaybackOperate: true,
        operateBtns,
        forceNoOffscreen: true,
        isNotMute: false,
        showPerformance: false,
        playbackForwardMaxRateDecodeIFrame: 4,
        useWebGPU: isWebGPU,
      });
      this.onCallback(jessibuca, index);
      return jessibuca;
    },
    onCallback(jessibuca: any, index: number) {
      const JessibucaCtor = this.getJessibucaCtor();
      jessibuca.on('error', (err: any) => {
        console.error('jessibuca error', err);
        this.statusMessage = '视频播放异常，请检查通道状态或播放地址';
        this.destroyPlayer(index);
      });
      jessibuca.on('timeout', () => {
        this.statusMessage = '视频播放超时，请检查设备或网络';
      });
      jessibuca.on('playbackPreRateChange', (rate: number) => {
        jessibuca.forward(rate);
      });
      jessibuca.on(JessibucaCtor?.EVENTS?.ptz || 'ptz', (arrow: string) => {
        this.handlePtz(arrow, index);
      });
    },
    async destroyPlayer(index: number) {
      const player = this.jessibucaList[index];
      if (!player) return;
      try {
        player.close?.();
        await player.destroy?.();
      } catch (err) {
        console.warn('destroy jessibuca failed', err);
      } finally {
        this.jessibucaList[index] = null;
      }
    },
    handlePtz(arrow: string, index: number) {
      let leftRight = 0;
      let upDown = 0;
      if (arrow === 'left') {
        leftRight = 2;
      } else if (arrow === 'right') {
        leftRight = 1;
      } else if (arrow === 'up') {
        upDown = 1;
      } else if (arrow === 'down') {
        upDown = 2;
      }
      const data = {
        leftRight,
        upDown,
        moveSpeed: 125,
      };
      const channel = this.channelList[index];
      const deviceId = this.getDeviceId(channel);
      const channelId = this.getChannelId(channel);
      if (deviceId && channelId) {
        ptzdirection(deviceId, channelId, data).catch((err: any) => console.warn('ptz failed', err));
      }
    },
    getVideoQuery() {
      const type = getScadaRouteType(this.route.query);
      const routeSerialNumber = getRouteQueryString(this.route.query, 'serialNumber');
      const sceneModelId = getRouteQueryString(this.route.query, 'sceneModelId');
      const componentSerialNumber = this.detail?.dataBind?.serialNumber;
      if (type === 2) {
        return { serialNumber: null, sceneModelId };
      }
      return {
        serialNumber: type === 1 ? routeSerialNumber : componentSerialNumber,
        sceneModelId: null,
      };
    },
    async getVideoChannelList() {
      this.loading = true;
      this.statusMessage = '';
      this.channelList = [];
      this.jessibucaList = [];
      this.activeIndex = -1;

      const params = this.getVideoQuery();
      if (!params.sceneModelId && !params.serialNumber) {
        this.loading = false;
        this.statusMessage = '请先配置设备序列号';
        return;
      }

      try {
        const res = await listComRelDeviceOrScene(params);
        if (res?.code !== 200) {
          this.statusMessage = res?.msg || '视频通道查询失败';
          return;
        }
        const list = Array.isArray(res.data) ? res.data : [];
        if (list.length === 0) {
          this.statusMessage = '暂无绑定视频通道';
          return;
        }
        this.channelList = list;
        this.tabActive = this.getChannelKey(list[0], 0);
        this.loading = false;
        this.$nextTick(() => {
          setTimeout(() => {
            this.handlePlay({ ...this.channelList[0], index: 0 });
          }, 0);
        });
      } catch (err) {
        console.error('get video channel list failed', err);
        this.statusMessage = '视频通道查询异常';
      } finally {
        this.loading = false;
      }
    },
    async stopChannel(index: number) {
      const channel = this.channelList[index];
      if (!channel) return;
      const deviceId = this.getDeviceId(channel);
      const channelId = this.getChannelId(channel);
      const proxyChannelId = this.getProxyChannelId(channel);

      if (channel.streamId && deviceId && channelId) {
        closeStream(deviceId, channelId, channel.streamId).catch((err: any) => console.warn('close stream failed', err));
        channel.streamId = '';
      }
      if (channel.proxyUrl && deviceId && proxyChannelId) {
        stopProxy(deviceId, proxyChannelId).catch((err: any) => console.warn('stop proxy failed', err));
      }
      const player = this.jessibucaList[index];
      player?.close?.();
    },
    async handlePlay(option: any) {
      const index = Number(option?.index || 0);
      const channel = this.channelList[index];
      if (!channel) return;
      if (!this.isChannelPlayable(channel)) {
        this.statusMessage = `${this.getChannelName(channel, index)}当前状态不可播放`;
        return;
      }

      if (this.activeIndex !== -1 && this.activeIndex !== index) {
        await this.stopChannel(this.activeIndex);
      }

      const deviceId = this.getDeviceId(channel);
      const channelId = this.getChannelId(channel);
      const proxyChannelId = this.getProxyChannelId(channel);
      const player = this.jessibucaList[index] || this.initJessibuca(index);
      if (!player) return;

      try {
        let playurl = channel.playUrl || channel.playurl || '';
        if (!playurl) {
          if (!deviceId || !channelId) {
            this.statusMessage = `${this.getChannelName(channel, index)}缺少设备或通道标识`;
            console.warn('inline video missing device/channel id', channel);
            return;
          }
          const shouldProxy = String(channel.channelType) === '601' && channel.proxyUrl;
          const res = shouldProxy ? await startProxy(deviceId, proxyChannelId) : await startPlay(deviceId, channelId);
          if (res?.code !== 200) {
            this.statusMessage = res?.msg || `${this.getChannelName(channel, index)}播放失败`;
            return;
          }
          playurl = res?.data?.playurl || res?.data?.playUrl || '';
          Object.assign(channel, {
            streamId: res?.data?.streamId || channel.streamId,
            playurl,
          });
        }

        if (!playurl) {
          this.statusMessage = '接口未返回播放地址';
          return;
        }
        this.statusMessage = '';
        this.activeIndex = index;
        this.playWithJessibuca(player, playurl, index);
      } catch (err) {
        console.error(`channel ${index} play failed`, err);
        this.statusMessage = `${this.getChannelName(channel, index)}播放异常`;
      }
    },
    playWithJessibuca(player: any, playurl: string, index: number) {
      let played = false;
      const doPlay = () => {
        if (played) return;
        played = true;
        try {
          player.play(playurl)?.catch?.((err: any) => {
            console.error(`channel ${index} jessibuca play failed`, err);
            this.statusMessage = `${this.getChannelName(this.channelList[index], index)}播放失败`;
          });
        } catch (err) {
          console.error(`channel ${index} jessibuca play error`, err);
          this.statusMessage = `${this.getChannelName(this.channelList[index], index)}播放异常`;
        }
      };
      if (typeof player.hasLoaded === 'function' && !player.hasLoaded()) {
        player.on?.('load', doPlay);
        return;
      }
      doPlay();
    },
    handleTabClick(tab: any) {
      const index = Number(tab?.index || 0);
      const option = { ...this.channelList[index], index };
      this.$nextTick(() => {
        this.handlePlay(option);
      });
    },
    async handleCloseJessibuca() {
      const closeJobs = this.channelList.map((_item, index) => this.stopChannel(index));
      await Promise.all(closeJobs);
      const destroyJobs = this.jessibucaList.map((_item, index) => this.destroyPlayer(index));
      await Promise.all(destroyJobs);
      this.jessibucaList = [];
      this.activeIndex = -1;
    },
  },
  beforeUnmount() {
    this.handleCloseJessibuca();
  },
};
</script>

<style lang="scss" scoped>
.inline-video {
  width: 100%;
  height: 100%;

  .cover-image {
    display: block;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .video-wrap {
    position: relative;
    width: 100%;
    height: 100%;

    .tab-wrap {
      position: relative;

      :deep(.el-tabs__header) {
        margin: 0;

        .el-tabs__item {
          height: 20px;
          line-height: 25px;
          display: inline-block;
          width: 100px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }

      :deep(.el-tabs__active-bar) {
        background-color: transparent;
      }

      :deep(.el-tabs__nav) {
        margin-bottom: 0;
      }

      :deep(.el-tabs__item) {
        font-size: 12px;
        color: #303133;
      }

      :deep(.el-tabs__item.is-active) {
        font-size: 12px;
        color: #45a1ff;
        background-color: #fff;
      }

      :deep(.el-tabs__nav-next),
      .el-tabs__nav-prev {
        line-height: 25px;
      }

      :deep(.el-tabs__nav-prev) {
        line-height: 25px;
      }

      .status {
        position: absolute;
        top: 6px;
        left: 8px;
        font-size: 10px;
        z-index: 1;
      }
    }
  }
}

.runtime-message {
  position: absolute;
  left: 12px;
  right: 12px;
  bottom: 12px;
  z-index: 2;
  padding: 6px 10px;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.64);
  color: #fff;
  font-size: 12px;
  line-height: 18px;
  text-align: center;
  pointer-events: none;
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
