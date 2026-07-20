<template>
  <div class="view-weather" :id="detail.identifier">
    <div class="weather-card" :style="cardStyle">
      <div class="weather-head">
        <div class="weather-place" :style="{ fontSize: smallTextSize }">{{ displayLocation }}</div>
        <div class="weather-state" :style="{ fontSize: smallTextSize }">{{ statusMessage || nowData.text || '--' }}</div>
      </div>

      <div v-if="statusMessage" class="weather-placeholder">
        {{ statusMessage }}
      </div>
      <div v-else class="weather-main">
        <img class="weather-icon" :src="weatherIcon" :style="iconStyle" draggable="false" />
        <div class="weather-temp-wrap">
          <div class="weather-temp" :style="{ fontSize: tempTextSize }">
            {{ nowData.temperature || '--' }}<span>°C</span>
          </div>
          <div class="weather-sub" :style="{ fontSize: smallTextSize }">
            {{ nowData.city ? '实时天气' : '等待天气数据' }}
          </div>
        </div>
      </div>

      <div class="weather-foot" :style="{ fontSize: footerTextSize }">
        {{ updateText }}
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import axios from 'axios';
import BaseView from '../View.vue';
import { getConfigFull } from '@/api/system/config';

const weatherIconModules = import.meta.glob('@/assets/images/scada/weather/*.svg', {
  eager: true,
  import: 'default',
}) as Record<string, string>;

export default {
  name: 'ViewWeather',
  extends: BaseView,
  props: {},
  data() {
    return {
      nowData: {
        code: '0',
        temperature: '',
        text: '',
        city: '',
      },
      timer: null as any,
      statusMessage: '',
      loading: false,
      updateTime: '',
    };
  },
  computed: {
    cardStyle() {
      const style = this.detail.style || {};
      return {
        fontFamily: style.fontFamily,
        color: style.foreColor,
        width: `${style.position.w}px`,
        height: `${style.position.h}px`,
        border: `${style.waterBorderWidth}px solid`,
        borderRadius: `${style.borderRadius}px`,
        borderColor: style.waterBorderColor,
        background: style.backColor,
        boxShadow: `${style.boxShadowWidth || 0}px ${style.boxShadowWidth || 0}px ${(style.boxShadowWidth || 0) * 2}px ${style.boxShadowColor || 'transparent'}`,
      };
    },
    displayLocation() {
      const dataBind = this.detail.dataBind || {};
      const cityName = dataBind.cityName || '';
      const districtName = dataBind.districtName || '';
      if (cityName && districtName && cityName !== districtName) return `${cityName} · ${districtName}`;
      if (districtName) return districtName;
      if (cityName) return cityName;
      return this.nowData.city || '请选择地区';
    },
    weatherIcon() {
      return weatherIconModules[`/src/assets/images/scada/weather/${this.nowData.code || '0'}.svg`] || weatherIconModules['/src/assets/images/scada/weather/0.svg'];
    },
    smallTextSize() {
      return `${Math.max(this.detail.style.position.h / 14, 12)}px`;
    },
    footerTextSize() {
      return `${Math.max(this.detail.style.position.h / 18, 11)}px`;
    },
    tempTextSize() {
      return `${Math.max(this.detail.style.position.h / 3.6, 32)}px`;
    },
    iconStyle() {
      const size = Math.max(Math.min(this.detail.style.position.h * 0.45, this.detail.style.position.w * 0.26), 56);
      return {
        width: `${size}px`,
        height: `${size}px`,
      };
    },
    updateText() {
      if (this.loading) return '更新中...';
      if (this.updateTime) return `更新于 ${this.updateTime}`;
      return '心知天气';
    },
  },
  watch: {
    'detail.dataBind.districtCode'() {
      this.restartWeatherTask();
    },
    'detail.dataBind.latitude'() {
      this.restartWeatherTask();
    },
    'detail.dataBind.longitude'() {
      this.restartWeatherTask();
    },
  },
  mounted() {
    this.restartWeatherTask();
  },
  beforeUnmount() {
    this.clearTimer();
  },
  methods: {
    clearTimer() {
      clearInterval(this.timer);
      this.timer = null;
    },
    restartWeatherTask() {
      this.clearTimer();
      const { districtCode, latitude, longitude } = this.detail.dataBind || {};
      if (!districtCode || !latitude || !longitude) {
        this.statusMessage = '请先配置天气地区';
        return;
      }
      this.getWeatherData();
      this.timer = setInterval(() => {
        this.getWeatherData();
      }, 60000 * 60);
    },
    async getWeatherData() {
      const { latitude, longitude } = this.detail.dataBind || {};
      this.loading = true;
      this.statusMessage = '';
      try {
        const key = await this.getSafeKey('sys.env.weather.key');
        if (!key) {
          this.statusMessage = '请先配置天气 API Key';
          return;
        }
        const res = await axios({
          url: 'https://api.seniverse.com/v3/weather/now.json',
          method: 'get',
          params: {
            key,
            location: `${latitude}:${longitude}`,
            language: 'zh-Hans',
            unit: 'c',
          },
        });
        const { results } = res.data || {};
        const current = Array.isArray(results) ? results[0] : null;
        if (!current?.location || !current?.now) {
          this.statusMessage = '天气数据为空';
          return;
        }
        this.nowData = { ...current.now, city: current.location.name };
        this.updateTime = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
      } catch (error) {
        console.error('获取天气数据失败:', error);
        this.statusMessage = this.getWeatherErrorMessage(error);
      } finally {
        this.loading = false;
      }
    },
    async getSafeKey(configKey: string) {
      const res = await getConfigFull(configKey);
      const { configValue, isEncryption } = res.data || {};
      if (!configValue) return '';
      return isEncryption === 1 ? window.atob(configValue) : configValue;
    },
    getWeatherErrorMessage(error: any) {
      const status = error?.response?.status;
      const serverMessage = error?.response?.data?.status || error?.response?.data?.message || error?.response?.data?.error;
      if (status === 403) {
        return serverMessage || '天气接口拒绝访问，请检查天气 API Key 权限';
      }
      if (status === 401) {
        return serverMessage || '天气 API Key 无效';
      }
      if (status) {
        return serverMessage || `天气接口请求失败(${status})`;
      }
      return '天气数据获取失败';
    },
  },
};
</script>

<style lang="scss" scoped>
.view-weather {
  width: 100%;
  height: 100%;
}

.weather-card {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 14px 18px;
}

.weather-head,
.weather-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  min-height: 20px;
  line-height: 1.2;
}

.weather-place {
  font-weight: 600;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.weather-state,
.weather-foot,
.weather-sub {
  opacity: 0.76;
  white-space: nowrap;
}

.weather-main {
  flex: 1;
  min-height: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 18px;
}

.weather-icon {
  flex: 0 0 auto;
  object-fit: contain;
}

.weather-temp-wrap {
  min-width: 0;
}

.weather-temp {
  font-weight: 700;
  line-height: 1;
  white-space: nowrap;

  span {
    margin-left: 4px;
    font-size: 0.38em;
    font-weight: 600;
    vertical-align: super;
  }
}

.weather-placeholder {
  flex: 1;
  min-height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: inherit;
  opacity: 0.78;
  font-size: 13px;
  text-align: center;
}
</style>
