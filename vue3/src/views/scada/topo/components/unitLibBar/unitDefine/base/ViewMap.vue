<template>
  <div class="view-map" :id="detail.identifier">
    <baidu-map
      v-if="baiduMapAk"
      :ak="baiduMapAk"
      v-loading="loading"
      element-loading-background="transparent"
      :id="detail.identifier + '_map'"
      :style="mapStyle"
      :center="center"
      :zoom="zoom"
      :scroll-wheel-zoom="true"
      @ready="ready"
    >
      <bm-city-list anchor="BMAP_ANCHOR_TOP_LEFT" v-if="false" />
      <bm-marker
        v-if="map"
        v-for="item of points"
        :key="item.deviceId || item.serialNumber"
        :position="{ lng: item.longitude, lat: item.latitude }"
        :dragging="false"
        :icon="{ url: judge(item.status), size: { width: 40, height: 40 } }"
        @click="clickHandler(item)"
      />
    </baidu-map>
    <div v-else class="map-placeholder">
      <div class="placeholder-title">地图</div>
      <div class="placeholder-desc">{{ statusMessage || '地图初始化中...' }}</div>
    </div>
    <div v-if="popupVisible && currentDevice" class="device-popup" :style="popupStyle">
      <div class="close" @click="infoWindowClose">x</div>
      <div class="pro-size">
        <span>{{ $t('home.deviceNum') }}</span>
        <span>{{ currentDevice.serialNumber }}</span>
      </div>
      <div class="pro-size">
        <span>{{ $t('home.deviceName') }}</span>
        <span>{{ currentDevice.deviceName }}</span>
      </div>
      <div class="pro-size">
        <span>{{ $t('home.location') }}</span>
        <span>{{ currentDevice.networkAddress }}</span>
      </div>
      <div class="pro-size">
        <span>{{ $t('home.deviceStatus') }}</span>
        <el-tag type="warning" v-if="currentDevice.status == 1">{{ $t('home.notActive') }}</el-tag>
        <el-tag type="danger" v-if="currentDevice.status == 2">{{ $t('home.disabled') }}</el-tag>
        <el-tag type="success" v-if="currentDevice.status == 3">{{ $t('home.onLine') }}</el-tag>
        <el-tag type="info" v-if="currentDevice.status == 4">{{ $t('home.offline') }}</el-tag>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import BaseView from '../View.vue';
import { BaiduMap, BmCityList, BmMarker } from 'vue-baidu-map-3x';
import { useUserStore } from '@/stores/modules/user';
import onlineImg from '@/assets/images/scada/marker_online.png';
import offlineImg from '@/assets/images/scada/marker_offline.png';
import inactiveImg from '@/assets/images/scada/marker_inactive.png';
import forbiddenImg from '@/assets/images/scada/marker_forbidden.png';
import { listAllDeviceShort } from '@/api/iot/device';
import { getConfigFull } from '@/api/system/config';

export default {
  name: 'ViewMap',
  extends: BaseView,
  components: {
    BaiduMap,
    BmCityList,
    BmMarker,
  },
  setup() {
    const userStore = useUserStore();
    return { userStore };
  },
  data() {
    return {
      loading: false,
      center: { lng: 116.40605, lat: 39.915879 },
      zoom: 10,
      points: [] as any[],
      timer: null as any,
      map: null as any,
      baiduMapAk: '',
      statusMessage: '',
      popupVisible: false,
      popupStyle: {
        left: '0px',
        top: '0px',
      },
      currentDevice: null as any,
    };
  },
  computed: {
    mapStyle() {
      return {
        width: `${this.detail?.style?.position?.w || 0}px`,
        height: `${this.detail?.style?.position?.h || 0}px`,
      };
    },
  },
  watch: {
    'detail.style.mapModel'(value: string) {
      if (this.map && value) {
        this.setMapTheme(value);
      }
    },
  },
  async mounted() {
    await this.initMapAk();
    this.getList();
    this.timer = setInterval(() => {
      this.getList();
    }, 60000);
  },
  beforeUnmount() {
    clearInterval(this.timer);
    this.timer = null;
  },
  methods: {
    async initMapAk() {
      try {
        const res = await getConfigFull('sys.env.baidumap.key');
        const { configValue, isEncryption } = res.data || {};
        if (!configValue) {
          this.statusMessage = '请先配置百度地图 AK';
          return;
        }
        this.baiduMapAk = isEncryption === 1 ? window.atob(configValue) : configValue;
      } catch (error) {
        console.error('获取百度地图AK失败:', error);
        this.statusMessage = '百度地图 AK 获取失败';
      }
    },
    ready({ map }: any) {
      this.map = map;
      this.$nextTick(() => {
        setTimeout(() => {
          map.checkResize?.();
          map.centerAndZoom(this.center, this.zoom);
          this.setMapTheme(this.detail?.style?.mapModel);
        }, 500);
      });
    },
    setMapTheme(mapModel: string) {
      if (!this.map || !mapModel || mapModel === 'normal') return;
      if (typeof this.map.setMapStyleV2 === 'function') {
        this.map.setMapStyleV2({ styleId: mapModel });
        return;
      }
      this.map.setMapStyle?.({ style: mapModel });
    },
    getList() {
      const params = { deptId: this.userStore?.dept?.deptId };
      this.loading = true;
      listAllDeviceShort(params)
        .then((res: any) => {
          if (res.code !== 200) {
            this.statusMessage = res.msg || '设备点位查询失败';
            return;
          }
          const rows = Array.isArray(res.rows) ? res.rows : [];
          this.setZoom(rows);
          this.points = rows;
          this.statusMessage = rows.length ? '' : '暂无设备点位';
        })
        .catch((error: any) => {
          console.error('获取地图设备点位失败:', error);
          this.statusMessage = '设备点位查询异常';
        })
        .finally(() => {
          this.loading = false;
        });
    },
    judge(status: number | string) {
      if (status == 3) return onlineImg;
      if (status == 4) return offlineImg;
      if (status == 1) return inactiveImg;
      return forbiddenImg;
    },
    setZoom(list: any[]) {
      if (!list || list.length === 0) return;
      const validPoints = list.filter((item) => item.longitude && item.latitude);
      if (validPoints.length === 0) {
        this.locateByBrowser();
        return;
      }

      let maxLng = Number(validPoints[0].longitude);
      let minLng = Number(validPoints[0].longitude);
      let maxLat = Number(validPoints[0].latitude);
      let minLat = Number(validPoints[0].latitude);

      validPoints.forEach((item) => {
        const lng = Number(item.longitude);
        const lat = Number(item.latitude);
        maxLng = Math.max(maxLng, lng);
        minLng = Math.min(minLng, lng);
        maxLat = Math.max(maxLat, lat);
        minLat = Math.min(minLat, lat);
      });

      this.center.lng = (maxLng + minLng) / 2;
      this.center.lat = (maxLat + minLat) / 2;
      this.zoom = this.calcZoom(maxLng, minLng, maxLat, minLat);
    },
    locateByBrowser() {
      const BMap = (window as any).BMap;
      if (!BMap?.Geolocation) return;
      const geolocation = new BMap.Geolocation();
      geolocation.getCurrentPosition(
        (r: any) => {
          const { latitude, longitude } = r || {};
          if (latitude && longitude) {
            this.center.lng = longitude;
            this.center.lat = latitude;
            this.zoom = 8;
          }
        },
        (err: any) => {
          console.log('bai-loc-err', err);
        }
      );
    },
    calcZoom(maxLng: number, minLng: number, maxLat: number, minLat: number) {
      const zoomLevels = [
        50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 25000, 50000, 100000, 200000, 500000, 1000000, 2000000,
      ];
      const averLng = this.rad(maxLng) - this.rad(minLng);
      const averLat = this.rad(maxLat) - this.rad(minLat);
      let distance =
        2 *
        Math.asin(
          Math.sqrt(
            Math.pow(Math.sin(averLat / 2), 2) +
              Math.cos(this.rad(maxLat)) * Math.cos(this.rad(minLat)) * Math.pow(Math.sin(averLng / 2), 2)
          )
        );
      distance = Math.round(distance * 6378.137 * 10000) / 10000;
      const distanceKm = Number(distance.toFixed(2));
      const index = zoomLevels.findIndex((item) => item - distanceKm > 0);
      return index === -1 ? 3 : 8 - index + 3;
    },
    rad(value: number) {
      return (value * Math.PI) / 180.0;
    },
    clickHandler(item: any) {
      const BMap = (window as any).BMap;
      if (!this.map || !item || !BMap?.Point) return;
      this.currentDevice = item;
      const pixel = this.map.pointToPixel(new BMap.Point(item.longitude, item.latitude));
      this.popupStyle = {
        left: `${pixel.x + 12}px`,
        top: `${pixel.y - 200}px`,
      };
      this.popupVisible = true;
    },
    infoWindowClose() {
      this.popupVisible = false;
      this.currentDevice = null;
    },
  },
};
</script>

<style lang="scss" scoped>
.view-map {
  position: relative;
  width: 100%;
  height: 100%;

  .map-placeholder {
    width: 100%;
    height: 100%;
    min-height: 160px;
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

  .placeholder-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 8px;
  }

  .placeholder-desc {
    font-size: 12px;
    text-align: center;
    padding: 0 12px;
  }

  .device-popup {
    position: absolute;
    min-width: 280px;
    max-width: 360px;
    background: #fff;
    border: 1px solid #dcdfe6;
    border-radius: 2px;
    box-shadow: 0 8px 18px rgba(0, 0, 0, 0.2);
    padding: 14px 16px;
    z-index: 999;

    &::after {
      content: '';
      position: absolute;
      left: 50%;
      bottom: -12px;
      margin-left: -10px;
      width: 0;
      height: 0;
      border-left: 10px solid transparent;
      border-right: 10px solid transparent;
      border-top: 12px solid #fff;
    }

    .close {
      position: absolute;
      right: 10px;
      top: 6px;
      font-size: 18px;
      line-height: 1;
      color: #909399;
      cursor: pointer;
      user-select: none;
    }
  }

  .pro-size {
    font-size: 13px;
    margin: 8px 0;
    color: #303133;
    line-height: 1.8;
    word-break: break-all;
  }
}
</style>
