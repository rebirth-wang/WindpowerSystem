<template>
  <div class="view-vr" :style="vrWrapStyle" @contextmenu.prevent="onContextmenu">
    <div v-if="!hasPanoramaUrl" class="vr-placeholder">
      <div class="vr-placeholder-title">VR场景</div>
      <div class="vr-placeholder-desc">{{ vrStatusMessage }}</div>
    </div>
    <div v-else ref="psvdbg" class="psv-mount"></div>
  </div>
</template>

<script lang="ts">
import { h } from 'vue';
import type { Component } from 'vue';
import { Viewer } from '@photo-sphere-viewer/core';
import { MarkersPlugin } from '@photo-sphere-viewer/markers-plugin';
import '@photo-sphere-viewer/core/index.css';
import '@photo-sphere-viewer/markers-plugin/index.css';
import { ElIcon } from 'element-plus';
import { Location, Delete, CircleClose } from '@element-plus/icons-vue';

import BaseView from '../View.vue';
import vrMarker from '@/assets/images/scada/vr_marker.png';
import uid from '@/utils/uid';
import { getToken } from '@/utils/auth';

function ctxMenuIcon(comp: Component) {
  return h(ElIcon, { size: 16 }, { default: () => h(comp) });
}

function panoramaLikeVue2(baseApi: string, raw: string | undefined): string {
  const u = String(raw ?? '').trim();
  if (/^https?:\/\//i.test(u) || u.startsWith('data:') || u.startsWith('blob:')) return u;
  const b = String(baseApi ?? '').replace(/\/+$/, '');
  const path = u.replace(/^\/+/, '');
  if (!path) return b ? `${b}/` : '/';
  return b ? `${b}/${path}` : `/${path}`;
}

type VrMarkerData = {
  id?: string;
  latitude?: number;
  longitude?: number;
  position?: {
    pitch?: number;
    yaw?: number;
  };
};

export default {
  name: 'ViewVr',
  extends: BaseView,
  data() {
    return {
      baseApi: import.meta.env.VITE_APP_BASE_API as string,
      viewer: null as InstanceType<typeof Viewer> | null,
      markersPlugin: null as any,
      vrImg: vrMarker,
      location: {} as Record<string, unknown>,
      selectMarkerId: null as string | null,
      vrStatusMessage: '请先配置VR全景图',
    };
  },
  computed: {
    VRChange() {
      return this.detail.style.url;
    },
    vrBoxKey() {
      const p = this.detail?.style?.position;
      const w = Math.max(1, Number(p?.w) || 400);
      const h = Math.max(1, Number(p?.h) || 300);
      return `${w}x${h}`;
    },
    hasPanoramaUrl() {
      return Boolean(String(this.detail?.style?.url || '').trim());
    },
    vrWrapStyle() {
      const p = this.detail?.style?.position;
      const w = Math.max(1, Number(p?.w) || 400);
      const h = Math.max(1, Number(p?.h) || 300);
      return {
        width: `${w}px`,
        height: `${h}px`,
        maxWidth: '100%' as const,
        maxHeight: '100%' as const,
        overflow: 'hidden' as const,
        boxSizing: 'border-box' as const,
        position: 'relative' as const,
      };
    },
  },
  watch: {
    VRChange() {
      this.destoryVR();
      if (this.hasPanoramaUrl) {
        this.$nextTick(() => this.init(this.detail.style.url, this.detail.style.markers));
      } else {
        this.vrStatusMessage = '请先配置VR全景图';
      }
    },
    vrBoxKey() {
      this.resizeViewerToBox();
    },
  },
  mounted() {
    if (this.hasPanoramaUrl) {
      this.$nextTick(() => this.init(this.detail.style.url, this.detail.style.markers));
    }
    document.addEventListener('contextmenu', this.hideContextMenu);
  },
  beforeUnmount() {
    document.removeEventListener('contextmenu', this.hideContextMenu);
    this.destoryVR();
  },
  methods: {
    ensureMarkersArray() {
      if (!Array.isArray(this.detail.style.markers)) {
        this.detail.style.markers = [];
      }
    },
    authHeadersForUrl(url: string): Record<string, string> | undefined {
      const token = getToken();
      if (!token) return undefined;
      try {
        const u = new URL(url, window.location.origin);
        const baseStr = (this.baseApi || '').trim();
        if (!baseStr) return undefined;
        const apiBase = new URL(
          baseStr.startsWith('http')
            ? baseStr
            : `${window.location.origin}${baseStr.startsWith('/') ? '' : '/'}${baseStr}`,
          window.location.origin
        );
        const basePath = apiBase.pathname.replace(/\/+$/, '') || '/';
        if (u.origin === apiBase.origin && u.pathname.startsWith(basePath)) {
          return { Authorization: `Bearer ${token}` };
        }
      } catch {
        /* ignore */
      }
      return undefined;
    },
    addMarker() {
      this.ensureMarkersArray();
      if (this.markersPlugin) {
        const marker = this.getMarkDeatil(this.location);
        this.markersPlugin.addMarker(marker);
        this.detail.style.markers.push(marker);
      }
    },
    deleteMarker() {
      this.ensureMarkersArray();
      if (this.selectMarkerId && this.markersPlugin) {
        this.markersPlugin.removeMarker(this.selectMarkerId);
        this.detail.style.markers = this.detail.style.markers.filter((item: any) => item.id !== this.selectMarkerId);
      }
    },
    onContextmenu(event: MouseEvent) {
      if (!this.editMode || !this.hasPanoramaUrl) return false;
      event.stopPropagation();
      this.$contextmenu({
        items: [
          {
            label: this.$t('scada.topo.unit.viewVr.addMarker'),
            icon: ctxMenuIcon(Location),
            onClick: () => {
              this.addMarker();
            },
          },
          {
            label: this.$t('scada.topo.unit.viewVr.deleteMarker'),
            divided: true,
            icon: ctxMenuIcon(Delete),
            onClick: () => {
              this.deleteMarker();
            },
          },
          {
            label: this.$t('cancel'),
            icon: ctxMenuIcon(CircleClose),
            onClick: () => {},
          },
        ],
        x: event.clientX,
        y: event.clientY,
        customClass: 'custom-class',
        zIndex: 9999,
        minWidth: 230,
      });
      return true;
    },
    destoryVR() {
      if (!this.viewer) return;
      try {
        this.viewer.destroy();
      } catch (e) {
        console.warn(e);
        const el = this.$refs.psvdbg as HTMLElement | undefined;
        if (el?.firstChild) el.removeChild(el.firstChild);
      }
      this.viewer = null;
      this.markersPlugin = null;
    },
    hideContextMenu() {
      /* reserved */
    },
    resizeViewerToBox() {
      if (!this.viewer) return;
      const p = this.detail?.style?.position;
      const w = Math.max(1, Number(p?.w) || 400);
      const h = Math.max(1, Number(p?.h) || 300);
      try {
        this.viewer.resize({ width: `${w}px`, height: `${h}px` });
      } catch (e) {
        console.warn('PSV resize failed', e);
      }
    },
    init(url: string | undefined, markers: unknown) {
      if (!String(url || '').trim()) {
        this.vrStatusMessage = '请先配置VR全景图';
        return;
      }
      this.vrStatusMessage = '';
      this.ensureMarkersArray();
      const initMarks: any[] = [];
      (Array.isArray(markers) ? markers : []).forEach((item: VrMarkerData) => {
        initMarks.push(this.getMarkDeatil(item));
      });

      const panoramaUrl = panoramaLikeVue2(this.baseApi, url);
      const headers = this.authHeadersForUrl(panoramaUrl);

      const boxW = Math.max(1, Number(this.detail?.style?.position?.w) || 400);
      const boxH = Math.max(1, Number(this.detail?.style?.position?.h) || 300);

      const el = this.$refs.psvdbg as HTMLElement | undefined;
      if (!el) {
        if (this.hasPanoramaUrl) {
          this.$nextTick(() => this.init(url, markers));
        }
        return;
      }

      const opts: Record<string, unknown> = {
        panorama: panoramaUrl,
        container: el,
        caption: 'FastBee',
        navbar: ['zoom', 'move', 'fullscreen'],
        /** 无全景时画布清屏透明，便于透出组态网格 */
        canvasBackground: 'transparent',
        size: { width: boxW, height: boxH },
        plugins: [
          MarkersPlugin.withConfig({
            markers: initMarks,
          }),
        ],
        maxFov: 100,
      };
      if (headers) opts.requestHeaders = headers;

      this.viewer = new Viewer(opts as any);
      this.markersPlugin = this.viewer.getPlugin(MarkersPlugin);

      this.viewer.addEventListener(
        'ready',
        () => {
          try {
            this.viewer!.resize({ width: '100%', height: '100%' });
          } catch {
            /* ignore */
          }
          console.log('viewer is ready');
        },
        { once: true }
      );

      if (this.editMode) {
        this.markersPlugin.addEventListener('enter-marker', ({ marker }: any) => {
          console.log(`Cursor is over marker ${marker.id}`);
        });
        this.markersPlugin.addEventListener('select-marker', ({ marker }: any) => {
          console.log(`Cursor is select marker ${marker.id}`);
          this.selectMarkerId = marker.id;
        });
        this.viewer.addEventListener('click', ({ data }: any) => {
          console.log(`${data.rightclick ? 'right ' : ''}clicked at yaw: ${data.yaw} pitch: ${data.pitch}`);
          this.location = {
            longitude: data.yaw,
            latitude: data.pitch,
          };
        });
      } else {
        this.markersPlugin.addEventListener('select-marker', ({ marker }: any) => {
          console.log(`Cursor is select marker ${marker.id}`);
        });
      }
    },
    getMarkDeatil(data: VrMarkerData) {
      const num = 1 + Math.round(Math.random() * 2);
      const longitude = Number(data.longitude ?? data.position?.yaw ?? 0);
      const latitude = Number(data.latitude ?? data.position?.pitch ?? 0);
      return {
        id: data.id || uid(),
        status: 1,
        anchor: 'center center',
        image: this.vrImg,
        size: { width: 50, height: 50 },
        position: {
          yaw: longitude,
          pitch: latitude,
        },
        latitude,
        longitude,
        tooltip: {
          content: `<span>${this.$t('scada.topo.unit.viewVr.markerTooltip', [num])}</span>`,
          position: 'center top',
          trigger: 'hover',
        },
        type: 'marker',
      };
    },
  },
};
</script>

<style scoped>
/* 外层随组态 backColor；默认透明 */
.view-vr {
  height: 100%;
  width: 100%;
  background: transparent;
}

.psv-mount {
  width: 100%;
  height: 100%;
  min-height: 1px;
}

.vr-placeholder {
  width: 100%;
  height: 100%;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #6b7280;
  background: rgba(248, 250, 252, 0.78);
  border: 1px dashed rgba(148, 163, 184, 0.8);
  box-sizing: border-box;
  text-align: center;
  padding: 16px;
}

.vr-placeholder-title {
  font-size: 16px;
  font-weight: 600;
  color: #334155;
}

.vr-placeholder-desc {
  font-size: 13px;
  line-height: 1.5;
}

/*
 * 差距来源（与图一不一致时）：
 * 1) 官方 .psv-overlay 是「径向灰阶渐变 + opacity:0.8」，我们曾改成 linear，晕影没了。
 * 2) 官方 .psv-overlay-image svg 为 width:50vw，组态里组件很窄时图标/排版会炸、像裁切。
 * 下面保留官方同款径向色标，仅把色值换成带 alpha 以透出网格；并限制 overlay 内图标与字号。
 */
.view-vr :deep(.psv-container) {
  background: transparent;
}

.view-vr :deep(.psv-overlay) {
  background: radial-gradient(
    rgba(255, 255, 255, 0.78) 0%,
    rgba(253, 253, 253, 0.72) 16%,
    rgba(251, 251, 251, 0.66) 33%,
    rgba(248, 248, 248, 0.58) 49%,
    rgba(239, 239, 239, 0.5) 66%,
    rgba(223, 223, 223, 0.44) 82%,
    rgba(191, 191, 191, 0.38) 100%
  );
  color: #111;
  opacity: 1;
}

.view-vr :deep(.psv-overlay-image) {
  margin-bottom: 3vh;
}

.view-vr :deep(.psv-overlay-image svg) {
  width: clamp(64px, 32%, 180px) !important;
  height: auto !important;
  max-width: 100%;
}

.view-vr :deep(.psv-overlay-text) {
  font-size: clamp(12px, 3.2cqw, 20px);
  line-height: 1.35;
  max-width: 92%;
  padding: 0 8px;
  box-sizing: border-box;
}

@supports not (font-size: 1cqw) {
  .view-vr :deep(.psv-overlay-text) {
    font-size: clamp(12px, 2.6vw, 20px);
  }
}

.view-vr :deep(.psv-overlay-subtext) {
  font-size: clamp(11px, 2.5cqw, 16px);
}

@supports not (font-size: 1cqw) {
  .view-vr :deep(.psv-overlay-subtext) {
    font-size: clamp(11px, 2vw, 16px);
  }
}

.view-vr :deep(.psv-loader-container) {
  background: rgba(255, 255, 255, 0.2);
}
</style>
