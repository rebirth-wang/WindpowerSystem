<template>
  <div class="home-page-container">
    <div class="home-tab-bar">
      <el-radio-group v-model="activeHomeTab" size="small">
        <el-radio-button v-if="hasAiChatPermission" :value="HOME_TAB_AI">{{ $t('home.ai') }}</el-radio-button>
        <el-radio-button :value="HOME_TAB_DASHBOARD">{{ $t('home.home') }}</el-radio-button>
      </el-radio-group>
    </div>

    <AiChatWorkspace v-if="hasAiChatPermission" v-show="activeHomeTab === HOME_TAB_AI" />

    <div v-show="activeHomeTab === HOME_TAB_DASHBOARD" class="home-dashboard-content">
      <!-- 设备统计信息以及天气 -->
      <el-row :gutter="20" class="statistics-container">
        <!-- 设备统计 -->
        <el-col :xs="24" :sm="24" :md="24" :lg="16" :xl="16">
          <el-row :gutter="20">
            <el-col :span="8" class="statistics-item">
              <!-- 设备数量 -->
              <div class="card-panel" @click="handleOpenPage(1)">
                <div class="card-content device">
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <div class="card-icon">
                        <svg-icon icon-class="device" class-name="card-panel-icon" />
                      </div>
                    </el-col>
                    <el-col :span="12">
                      <div class="card-data">
                        <div class="card-title">{{ $t('home.number') }}</div>
                        <count-to
                          class="card-panel-num"
                          :startVal="0"
                          :endVal="deviceCountStatic.deviceCount || 0"
                          :duration="3000"
                        />
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-col>
            <el-col :span="8" class="statistics-item">
              <!-- 产品数量 -->
              <div class="card-panel" @click="handleOpenPage(2)">
                <div class="card-content product">
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <div class="card-icon">
                        <svg-icon icon-class="model" class-name="card-panel-icon" />
                      </div>
                    </el-col>
                    <el-col :span="12">
                      <div class="card-data">
                        <div class="card-title">{{ $t('home.product') }}</div>
                        <count-to
                          class="card-panel-num"
                          :startVal="0"
                          :endVal="deviceCountStatic.productCount || 0"
                          :duration="3000"
                        />
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-col>
            <el-col :span="8" class="statistics-item">
              <!-- 操作记录 -->
              <div class="card-panel" @click="handleOpenPage(3)">
                <div class="card-content function">
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <div class="card-icon">
                        <svg-icon icon-class="log" class-name="card-panel-icon" />
                      </div>
                    </el-col>
                    <el-col :span="12">
                      <div class="card-data">
                        <div class="card-title">{{ $t('home.records') }}</div>
                        <count-to
                          class="card-panel-num"
                          :startVal="0"
                          :endVal="functionStatistic || 0"
                          :duration="3000"
                        />
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <!-- 监测数据 -->
              <div class="card-panel" @click="handleOpenPage(4)">
                <div class="card-content monitor">
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <div class="card-icon">
                        <svg-icon icon-class="monitor_solid" class-name="card-panel-icon" />
                      </div>
                    </el-col>
                    <el-col :span="12">
                      <div class="card-data">
                        <div class="card-title">{{ $t('home.monitoring') }}</div>
                        <count-to
                          class="card-panel-num"
                          :startVal="0"
                          :endVal="deviceStatistic.monitorCount || 0"
                          :duration="3000"
                        />
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <!-- 告警数量 -->
              <div class="card-panel" @click="handleOpenPage(5)">
                <div class="card-content alert">
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <div class="card-icon">
                        <svg-icon icon-class="alert" class-name="card-panel-icon" />
                      </div>
                    </el-col>
                    <el-col :span="12">
                      <div class="card-data">
                        <div class="card-title">{{ $t('home.alarm') }}</div>
                        <count-to
                          class="card-panel-num"
                          :startVal="0"
                          :endVal="deviceCountStatic.alertCount || 0"
                          :duration="3000"
                        />
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <!-- 上报事件 -->
              <div class="card-panel" @click="handleOpenPage(6)">
                <div class="card-content reports">
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <div class="card-icon">
                        <svg-icon icon-class="event" class-name="card-panel-icon" />
                      </div>
                    </el-col>
                    <el-col :span="12">
                      <div class="card-data">
                        <div class="card-title">{{ $t('home.reports') }}</div>
                        <count-to
                          class="card-panel-num"
                          :startVal="0"
                          :endVal="deviceStatistic.eventCount || 0"
                          :duration="3000"
                        />
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-col>
        <el-col :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
          <!-- 天气 -->
          <div @click="getLocation" title="点击获取天气">
            <el-card
              class="weather-card"
              shadow="hover"
              :style="{
                '--background-start': getBackgroundColor(weatherData.data.type).start,
                '--background-end': getBackgroundColor(weatherData.data.type).end,
              }"
            >
              <el-row :gutter="20">
                <el-col :span="10">
                  <div class="weather-main">
                    <img :src="weatherData.data.typeIcon" alt="天气图标" class="weather-icon" />
                  </div>
                </el-col>
                <el-col :span="14">
                  <!-- 头部：城市名称、日期与星期 -->
                  <div class="weather-header">
                    <h2>{{ weatherData.city }}</h2>
                    <div class="date-week">
                      <span>{{ weatherData.data.date }}</span>
                      &nbsp;
                      <span>{{ weatherData.data.week }}</span>
                    </div>
                  </div>
                  <el-row :gutter="10" style="margin-top: 10px">
                    <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
                      <div class="low-temperature">{{ weatherData.data.low }}</div>
                    </el-col>
                    <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
                      <!-- 温度 -->
                      <div class="high-temperature">/ {{ weatherData.data.high }}</div>
                      <p class="weather-description">{{ weatherData.data.type }}</p>
                    </el-col>
                  </el-row>
                  <!-- 详情信息：风向、风力 -->
                  <el-row :gutter="10">
                    <div class="weather-details">
                      <el-col :span="12">
                        <div class="detail-item">
                          <svg-icon icon-class="wind_direction" />
                          <span class="detail-text">{{ weatherData.data.fengxiang }}</span>
                        </div>
                      </el-col>
                      <el-col :span="12">
                        <div class="detail-item">
                          <svg-icon icon-class="wind_speed" />
                          <span class="detail-text">{{ weatherData.data.fengli }}</span>
                        </div>
                      </el-col>
                    </div>
                  </el-row>
                </el-col>
              </el-row>
            </el-card>
          </div>
        </el-col>
      </el-row>

      <!-- 地图 -->
      <el-row :gutter="20" class="statistics-container">
        <!-- 地图 -->
        <el-col :xs="24" :sm="24" :md="24" :lg="16" :xl="16">
          <div class="map-flex">
            <div class="map-card" shadow="hover">
              <div
                id="tdtMapWrapper"
                style="width: 100%; height: 100%; position: relative; z-index: 10; border-radius: 10px"
                v-show="currentChange === 0"
              >
                <!-- 天地图容器 -->
                <div class="tdt-map-title" ref="mapTitleRef">
                  {{ mapTitle }}
                  <div class="tdt-map-subtitle">Fastbee open source iot platform</div>
                </div>
                <div
                  id="tdtMap"
                  style="width: 100%; height: 100%; position: absolute; top: 0; left: 0; z-index: 1"
                ></div>
              </div>
              <div class="map-container" v-show="currentChange === 1 || currentChange === 2">
                <div ref="mapRef" class="map" id="map"></div>
              </div>

              <div class="map-toolbar" style="position: absolute; z-index: 1000">
                <el-dropdown placement="top-end" popper-class="map-dropdown" @command="handleMapDropdownCommand">
                  <div
                    style="
                      width: 36px;
                      height: 36px;
                      background: #e3edff;
                      border-radius: 6px 6px 6px 6px;
                      display: flex;
                      flex-direction: row;
                      justify-content: center;
                      align-items: center;
                      z-index: 100;
                    "
                  >
                    <img style="width: 14px; height: 14px" :src="mapSwitchImg" />
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item
                        v-for="item in mapSwitchDatas"
                        :key="item.id"
                        :command="item.id"
                        :disabled="currentChange === item.id"
                      >
                        {{ item.name }}
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
            <!-- cpu使用率 -->
            <el-card class="rate-card" shadow="hover">
              <div v-if="isAdmin && hasSever">
                <div>
                  <div class="chart-title">{{ $t('home.usage') }}</div>
                  <el-row>
                    <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
                      <div ref="pieCpu" class="pieCpu"></div>
                    </el-col>
                  </el-row>
                </div>
                <el-row :gutter="20">
                  <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
                    <div>
                      <div class="chart-title">{{ $t('home.memoryRate') }}</div>
                      <div ref="pieMemery" class="pieMemery"></div>
                    </div>
                  </el-col>
                  <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
                    <div>
                      <div class="chart-title">{{ $t('home.disk') }}</div>
                      <div ref="pieDisk" class="pieDisk"></div>
                    </div>
                  </el-col>
                </el-row>
              </div>
              <div v-else>
                <el-empty style="height: 500px" :description="$t('dataCenter.analysis.349202-27')"></el-empty>
              </div>
            </el-card>
          </div>
        </el-col>
        <!-- 折线图 -->
        <el-col :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
          <!-- 信息栏 -->
          <el-card shadow="hover" class="message-card">
            <div class="message-title">{{ $t('home.information') }}</div>
            <div class="notice-bar" :class="{ animating: animate }" v-if="noticeList.length > 0 && hasNotice">
              <div class="item-wrap" @click="openDetail(item.noticeId)" v-for="item in noticeList" :key="item.noticeId">
                <div class="left-wrap">
                  <el-tag size="small" effect="dark" type="warning" v-if="item.noticeType == 2">
                    {{ $t('home.announcement') }}
                  </el-tag>
                  <el-tag size="small" effect="dark" v-else>{{ $t('home.message') }}</el-tag>
                  <span style="margin-left: 8px">{{ item.noticeTitle }}</span>
                </div>
                <div class="right-wrap">
                  {{ parseTime(item.createTime, '{y}-{m}-{d}') }}
                </div>
              </div>
            </div>
            <div v-if="noticeList.length === 0 && hasNotice">
              <el-empty style="height: 200px" :description="$t('dataCenter.analysis.349202-7')"></el-empty>
            </div>
            <div v-if="!hasNotice">
              <el-empty style="height: 200px" :description="$t('dataCenter.analysis.349202-27')"></el-empty>
            </div>
          </el-card>
          <div>
            <!-- 登录用户数量 -->
            <el-card class="line-card" shadow="hover">
              <div
                ref="lineChart"
                style="height: 270px; width: 100%"
                v-if="isAdmin && linechart.counts && hasLineChart"
              ></div>
              <div v-else class="message-title" style="margin: 0px 0 10px">
                {{ $t('views.index.394840-16') }}
                <el-empty style="height: 250px" :description="$t('dataCenter.analysis.349202-27')"></el-empty>
              </div>
            </el-card>
          </div>
          <div>
            <!-- mqtt状态数据 -->
            <el-card class="card-container" shadow="hover">
              <div ref="statsChart" style="height: 310px"></div>
            </el-card>
          </div>
        </el-col>
      </el-row>

      <!-- h5手机显示页面 -->
      <el-card shadow="hover" style="margin: -20px 10px 20px 10px" class="phone-card" v-if="systemForm.isShowPhone">
        <el-row :gutter="40">
          <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12" style="padding: 10px">
            <div style="padding: 30px; margin: 20px 0; font-size: 14px">
              <div
                style="
                  margin-bottom: 20px;
                  font-family:
                    PingFangSC,
                    PingFang SC;
                  font-weight: 600;
                  font-size: 35px;
                  color: #303133;
                  line-height: 49px;
                  text-align: left;
                  font-style: normal;
                "
              >
                {{ $t('views.index.394840-0') }}
              </div>
              <div style="display: table; font-size: 14px; margin-bottom: 10px">
                <div style="display: table-cell; line-height: 22px">
                  <b
                    style="
                      margin-right: 10px;
                      font-family:
                        PingFangSC,
                        PingFang SC;
                      font-weight: 500;
                      font-size: 13px;
                      color: #67c23a;
                      line-height: 18px;
                      text-align: left;
                      font-style: normal;
                    "
                  >
                    {{ $t('views.index.394840-1') }}
                  </b>
                </div>
              </div>
              <div style="margin-bottom: 10px">
                <div style="width: 70px; font-weight: bold; display: table-cell; padding: 5px 0">
                  {{ $t('views.index.394840-2') }}
                </div>
                <div style="line-height: 22px">{{ $t('views.index.394840-3') }}</div>
              </div>
              <div style="margin: 10px 0">
                <div style="width: 70px; font-weight: bold; display: table-cell; padding: 5px 0">
                  {{ $t('views.index.394840-4') }}
                </div>
                <div style="line-height: 22px">
                  {{ $t('views.index.394840-5') }}
                  <br />
                  <el-link target="_blank" href="https://fastbee.cn/doc/pages/sponsor">
                    {{ $t('views.index.394840-6') }}
                  </el-link>
                </div>
              </div>
            </div>
            <div style="padding: 70px 30px 0 20px; font-size: 14px">
              <div style="float: left; width: 200px">
                <el-image style="width: 180px" :src="codeImg"></el-image>
              </div>
              <div style="float: left">
                <div class="mini-program">{{ $t('views.index.394840-7') }}</div>
                <div style="display: table; margin-bottom: 5px">
                  <div class="web-site">{{ $t('views.index.394840-9') }}</div>
                  <div class="other-site">
                    <el-link target="_blank" href="https://fastbee.cn/">www.fastbee.cn</el-link>
                  </div>
                </div>
                <div style="display: table; margin-bottom: 5px">
                  <div class="web-site">{{ $t('views.index.394840-10') }}</div>
                  <div class="other-site">
                    <el-link target="_blank" href="https://fastbee.cn/doc">www.fastbee.cn/doc</el-link>
                  </div>
                </div>
                <div style="display: table; margin: 5px 0">
                  <div class="web-site">{{ $t('views.index.394840-11') }}</div>
                  <div class="other-site">
                    <span>164770707@qq.com</span>
                  </div>
                </div>
                <div style="display: table; margin-bottom: 10px">
                  <div class="web-site">{{ $t('views.index.394840-12') }}</div>
                  <div class="other-site">
                    <el-link target="_blank" href="https://gitee.com/kerwincui/wumei-smart" style="font-size: 12px">
                      {{ $t('views.index.394840-13') }}
                    </el-link>
                    <el-link
                      target="_blank"
                      href="https://github.com/kerwincui/fastbee"
                      style="margin-left: 20px; font-size: 12px"
                    >
                      {{ $t('views.index.394840-14') }}
                    </el-link>
                  </div>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="12" :xl="12" style="padding: 30px">
            <div class="phone">
              <div class="phone-container">
                <iframe
                  class="phone-iframe"
                  src="https://iot.fastbee.cn/h5"
                  id="iframe"
                  frameborder="0"
                  scrolling="no"
                  loading="lazy"
                  allow="geolocation; fullscreen"
                ></iframe>
              </div>
              <div class="frame-remark">{{ $t('views.index.394840-8') }}</div>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <!--通知公告详情 -->
      <el-dialog :title="notice.noticeTitle" v-model="open" width="800px" append-to-body>
        <div style="margin-top: -20px; margin-bottom: 10px">
          <el-tag size="small" effect="dark" type="warning" v-if="notice.noticeType == 2">
            {{ $t('home.announcement') }}
          </el-tag>
          <el-tag size="small" effect="dark" v-else>{{ $t('home.message') }}</el-tag>
          <span style="margin-left: 20px">{{ notice.createTime }}</span>
        </div>
        <div v-loading="dialogLoading" class="content">
          <div v-html="notice.noticeContent"></div>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="closeDetail">{{ $t('home.close') }}</el-button>
          </div>
        </template>
      </el-dialog>

      <div class="footer-container">
        <span v-if="systemForm.copyRight">
          Copyright © 2021-2025
          <a href="https://fastbee.cn" target="_blank">FastBee</a>
          |
          <a href="https://fastbee.cn" target="_blank">{{ $t('views.index.394840-15') }}</a>
          |
          <a href="https://fastbee.cn/doc/sponsor" target="_blank">Apache License</a>
        </span>
        <br />
        <span v-if="systemForm.isDoc">
          {{ $t('views.index.394840-17') }}
          <a href="https://fastbee.cn/doc" target="_blank">https://fastbee.cn/doc/</a>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, onActivated, nextTick, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import * as echarts from 'echarts';
import 'echarts-liquidfill';
import axios from 'axios';
import dayjs from 'dayjs';
import Cookies from 'js-cookie';
import { CountTo } from 'vue3-count-to';
import { getEventStatistic, getDeviceCountStatistic, getFunctionStatistic, listAllDeviceShort } from '@/api/iot/device';
import { listNotice, getNotice } from '@/api/system/notice';
import { getServer } from '@/api/monitor/server';
import { searchUserCount } from '@/api/monitor/jobLog';
import { getNettyMqttStats, statisticNettyMqtt } from '@/api/iot/netty';
import { getConfigKey, getConfigFull } from '@/api/system/config';
import { parseTime } from '@/utils/ruoyi';
import { checkPermi } from '@/utils/permission';
import { loadBMap, loadGMap, loadTianDiTu } from '@/utils/map.js';
import { useUserStore } from '@/stores/modules/user';
import AiChatWorkspace from '@/views/ai/chat/components/AiChatWorkspace.vue';
import mapSwitchImg from '@/assets/images/map_switch.png';
import codeImg from '@/assets/images/code.jpg';
import weatherQing from '@/assets/images/weather/qing.png';
import weatherDuoyun from '@/assets/images/weather/duoyun.png';
import weatherYin from '@/assets/images/weather/yin.png';
import weatherXiaoyu from '@/assets/images/weather/xiaoyu.png';
import weatherZhongyu from '@/assets/images/weather/zhongyu.png';
import weatherDayu from '@/assets/images/weather/dayu.png';
import weatherLeizhenyu from '@/assets/images/weather/leizhenyu.png';

const router = useRouter();
const { t } = useI18n();
const userStore = useUserStore();
const HOME_TAB_AI = 'ai';
const HOME_TAB_DASHBOARD = 'dashboard';
const hasAiChatPermission = computed(() => checkPermi(['ai:chat:list']));
const activeHomeTab = ref(hasAiChatPermission.value ? HOME_TAB_AI : HOME_TAB_DASHBOARD);
const dashboardInitialized = ref(false);

// Template refs
const pieCpu = ref<HTMLElement | null>(null);
const pieMemery = ref<HTMLElement | null>(null);
const pieDisk = ref<HTMLElement | null>(null);
const lineChart = ref<HTMLElement | null>(null);
const statsChart = ref<HTMLElement | null>(null);
const mapRef = ref<HTMLElement | null>(null);
const mapTitleRef = ref<HTMLElement | null>(null);

// 天气信息
const weatherData = reactive({
  success: true,
  city: '---',
  data: {
    date: '---',
    week: '--',
    type: '-',
    typeIcon: weatherQing,
    low: '-°C',
    high: '-°C',
    fengxiang: '---',
    fengli: '--',
  } as any,
  air: { aqi: 85, aqi_level: 2, aqi_name: '良' },
});
const radius = ref(['55%', '75%']);
const labelFontSize = ref('24');
const stats = ref<any>({});
const staticData = ref<any>({});
const linechart = reactive({ date: [] as string[], counts: [] as number[] });
const hasLineChart = ref(true);
const dialogLoading = ref(true);
const open = ref(false);
const noticeList = ref<any[]>([]);
const hasNotice = ref(true);
const notice = ref<any>({});
const isAdmin = ref(false);
const deviceList = ref<any[]>([]);
const deviceStatistic = ref<any>({});
const deviceCountStatic = ref<any>({});
const functionStatistic = ref(0);
const deviceCount = ref(0);
const animate = ref(true);
const interval = ref(2000);
const bMapChart = ref<any>(null);
const gMapChart = ref<any>(null);
const googleMap = ref<any>(null);
const googleMarkers = ref<any[]>([]);
const tMapChart = ref<any>(null);
const tMapState = reactive({ center: { lng: 105, lat: 30 }, zoom: 5 });
const mqttChart = ref<any>(null);
const pieCpuChart = ref<any>(null);
const rateChart = ref<any>(null);
const sysChart = ref<any>(null);
const loginUserChart = ref<any>(null);
const echartsMapInstance = ref<any>(null);
const deviceMarkers = ref<any[]>([]);
const mapTitle = ref('');
const server = reactive<any>({
  jvm: { name: '', version: '', startTime: '', runTime: '', used: '', total: 100 },
  sys: { computerName: '', osName: '', computerIp: '', osArch: '' },
  cpu: { cpuNum: 1, used: 0, sys: 0, free: 100 },
  mem: { total: 2, used: 0, free: 2 },
  sysFiles: [{ used: '0GB', free: '0GB' }],
});
const hasSever = ref(true);
const tableData = ref<any[]>([]);
const systemForm = reactive({
  copyRight: true,
  logo: undefined as any,
  imgUrl: undefined as any,
  accountTip: true,
  document: true,
  website: true,
  isShowPhone: true,
  isDoc: true,
});
const mapSwitchDatas = computed(() => [
  { id: 0, name: t('home.tianDiTu') },
  { id: 1, name: t('home.baiduMap') },
  { id: 2, name: t('home.googleMap') },
]);
const currentChange = ref(0);
const statusColors: Record<number, string> = { 1: '#E6A23C', 2: '#F56C6C', 3: '#67C23A', 4: '#909399', 5: '#e80705' };
let intervalId: ReturnType<typeof setInterval> | null = null;
let mapResizeObserver: ResizeObserver | null = null;
let chartResizeObserver: ResizeObserver | null = null;
const intervalIds = new Set<ReturnType<typeof setInterval>>();
const chartInstances = new Map<string, any>();

// === METHODS START ===
function handleOpenPage(val: number) {
  const routes: Record<number, string> = {
    1: '/iot/device/list',
    2: '/iot/product/list',
    3: '/netty/mqtt',
    4: '/netty/mqtt',
    5: '/ruleengine/alertLog',
    6: '/netty/mqtt',
  };
  if (routes[val]) router.push({ path: routes[val] });
}
function startScroll() {
  if (intervalId) clearInterval(intervalId);
  intervalId = setInterval(() => {
    const first = noticeList.value.shift();
    if (first) noticeList.value.push(first);
  }, interval.value);
  intervalIds.add(intervalId);
}
function stopScroll() {
  if (intervalId) {
    clearInterval(intervalId);
    intervalIds.delete(intervalId);
    intervalId = null;
  }
}
function getChartData() {
  searchUserCount()
    .then((response: any) => {
      response.data.reverse().forEach((item: any) => {
        linechart.date.push(item.datetime);
        linechart.counts.push(item.user_count);
      });
      drawLine();
    })
    .catch((error: any) => {
      if (error?.code === 403) hasLineChart.value = false;
    });
}
function updateRadius() {
  const isMobile = window.matchMedia('(max-width: 430px)').matches;
  radius.value = isMobile ? ['35%', '45%'] : ['55%', '75%'];
  labelFontSize.value = isMobile ? '16' : '28';
  drawPieCpu();
}
function statisticMqtt() {
  statisticNettyMqtt()
    .then((r: any) => {
      staticData.value = r.data;
    })
    .catch(() => {});
}
function getMqttStats() {
  getNettyMqttStats()
    .then((r: any) => {
      stats.value = r.data;
      drawStats();
    })
    .catch(() => {});
}
function drawStats() {
  if (!statsChart.value) return;
  if (!mqttChart.value) mqttChart.value = echarts.init(statsChart.value);
  const option = {
    title: {
      text: t('views.index.394840-19'),
      fontFamily: 'PingFangSC, PingFang SC',
      lineHeight: 22,
      fontWeight: 600,
      fontSize: 16,
      color: '#303133',
      fontStyle: 'normal',
      textAlign: 'left',
    },
    grid: {
      left: '2%', // 左侧距离
      right: '4%', // 右侧距离
      top: '18%', // 顶部距离（留出标题空间）
      bottom: '2%', // X 轴距离底部 15%
      containLabel: true, // 确保坐标轴标签完整显示
    },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: {
      data: [t('netty.mqtt.564432-18'), t('netty.mqtt.564432-19')],
      right: '15',
      icon: 'rect',
      itemWidth: 10,
      itemHeight: 10,
      borderRadius: 20,
      color: 'rgba(0,0,0,0.65)',
    },
    xAxis: {
      type: 'value',
      boundaryGap: [0, 0.01],
      splitLine: { show: true, lineStyle: { type: 'dashed' } },
      splitNumber: 3,
    },
    yAxis: {
      type: 'category',
      data: [
        t('netty.mqtt.564432-13'),
        t('netty.mqtt.564432-14'),
        t('netty.mqtt.564432-15'),
        t('netty.mqtt.564432-16'),
        t('netty.mqtt.564432-17'),
      ],
    },
    series: [
      {
        name: t('netty.mqtt.564432-18'),
        color: '#0bb9ff',
        type: 'bar',
        data: [
          stats.value['connection_count'],
          stats.value['session_count'],
          stats.value['subscription_count'],
          stats.value['retain_count'],
          stats.value['retain_count'],
        ],
      },
      {
        name: t('netty.mqtt.564432-19'),
        color: '#4a6ff8',
        type: 'bar',
        data: [
          stats.value['connection_total'],
          stats.value['session_total'],
          stats.value['subscription_total'],
          stats.value['retain_total'],
          stats.value['retain_total'],
        ],
      },
    ],
  };
  mqttChart.value.setOption(option);
}
function isGeolocationAllowed() {
  const policy = (document as any).permissionsPolicy || (document as any).featurePolicy;
  return !policy || policy.allowsFeature?.('geolocation') !== false;
}

async function getLocation() {
  if (!isGeolocationAllowed()) {
    fetchWeather(39.9042, 116.4074);
    return;
  }
  if (!navigator.geolocation) {
    try {
      const T = await loadTianDiTu();
      const geolocation = new (T as any).Geolocation({
        provider: 'w3c',
        highAccuracy: true,
        timeout: 5000,
        maximumAge: 0,
      });
      geolocation.getCurrentPosition(
        (position: any) => {
          const { lat, lng } = position.lnglat;
          fetchWeather(lat, lng);
        },
        () => {
          fetchWeather(39.9042, 116.4074);
        }
      );
    } catch {
      fetchWeather(39.9042, 116.4074);
    }
  } else {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        fetchWeather(position.coords.latitude, position.coords.longitude);
      },
      () => {
        fetchWeather(39.9042, 116.4074);
      },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
    );
  }
}
async function getSafeKey(configKey: string) {
  const res: any = await getConfigFull(configKey);
  const { configValue, isEncryption } = res.data || {};
  if (isEncryption !== 1) return configValue;
  if (!configValue) throw new Error(`${configKey} 未获取到 key`);
  return window.atob(configValue);
}
async function fetchWeather(lat: number, lon: number) {
  try {
    const key = await getSafeKey('sys.env.weather.key');
    const url = `https://api.seniverse.com/v3/weather/daily.json?key=${key}&location=${lat}:${lon}&language=zh-Hans&unit=c`;
    const response = await axios.get(url);
    const weather = response.data.results[0];
    if (response.data) {
      weatherData.success = true;
      weatherData.city = weather.location.name;
      weatherData.data = {
        date: weather.daily[0].date,
        week: '',
        type: weather.daily[0].text_day,
        typeIcon: getTypeIcon(weather.daily[0].text_day),
        low: weather.daily[0].low + '°C',
        high: weather.daily[0].high + '°C',
        fengxiang: weather.daily[0].wind_direction,
        fengli: weather.daily[0].wind_speed + ' 级',
        night: {
          type: weather.daily[0].text_night,
          fengxiang: weather.daily[0].wind_direction,
          fengli: weather.daily[0].wind_speed,
        },
      };
      const bg = getBackgroundColor(weather.daily[0].text_day);
      document.documentElement.style.setProperty('--background-start', bg.start);
      document.documentElement.style.setProperty('--background-end', bg.end);
    }
  } catch (err) {
    console.log(err);
  }
}
function getBackgroundColor(type: string) {
  const bgs: Record<string, { start: string; end: string }> = {
    晴: { start: '#FFF0C1', end: '#FFD1D1' },
    多云: { start: '#F3F6FF', end: '#DCE6FA' },
    阴: { start: '#F5F5F5', end: '#D3D3D3' },
    小雨: { start: '#D8F0FF', end: '#BCE0FF' },
    中雨: { start: '#C4DFFF', end: '#A1C4F8' },
    大雨: { start: '#A8E3FF', end: '#8CCFFF' },
    雷阵雨: { start: '#F2E7FF', end: '#C2C2C2' },
    暴雨: { start: '#D8F0FF', end: '#C2C2C2' },
    阵雨: { start: '#D8F0FF', end: '#C2C2C2' },
  };
  return bgs[type] || { start: '#FFF0C1', end: '#FBFBFD' };
}
function getTypeIcon(type: string) {
  const icons: Record<string, string> = {
    晴: weatherQing,
    多云: weatherDuoyun,
    阴: weatherYin,
    小雨: weatherXiaoyu,
    中雨: weatherZhongyu,
    大雨: weatherDayu,
    雷阵雨: weatherLeizhenyu,
    暴雨: weatherDayu,
    阵雨: weatherXiaoyu,
  };
  return icons[type] || weatherQing;
}
async function init() {
  if (
    userStore.roles.includes('admin') ||
    userStore.roles.includes('manager') ||
    (userStore.dept as any)?.userName === 'fastbee'
  ) {
    isAdmin.value = true;
    getServerInfo();
    getChartData();
  }
}

function getEventStatisticData() {
  getEventStatistic()
    .then((r: any) => {
      deviceStatistic.value = r.data;
    })
    .catch(() => {});
}
function getDeviceCountStatisticData() {
  getDeviceCountStatistic()
    .then((r: any) => {
      deviceCountStatic.value = r.data;
    })
    .catch(() => {});
}
function getFunctionStatisticData() {
  getFunctionStatistic()
    .then((r: any) => {
      functionStatistic.value = r.data;
    })
    .catch(() => {});
}
function getNoticeList() {
  listNotice({ pageNum: 1, pageSize: 6 })
    .then((r: any) => {
      if (r.code == 200) noticeList.value = r.rows.splice(0, 6);
    })
    .catch((error: any) => {
      if (error?.code === 403) hasNotice.value = false;
    });
}
function openDetail(id: number) {
  open.value = true;
  dialogLoading.value = true;
  getNotice(id)
    .then((r: any) => {
      notice.value = r.data;
      open.value = true;
      dialogLoading.value = false;
    })
    .catch(() => {});
}
function closeDetail() {
  open.value = false;
}
function getAllDevice() {
  listAllDeviceShort({})
    .then((res: any) => {
      if (res.code === 200) {
        deviceList.value = res.rows;
        deviceCount.value = res.total;
        loadMap();
      }
    })
    .catch(() => {});
}
function loadMap() {
  nextTick(() => {
    const lan = Cookies.get('language');
    if (lan === 'zh-CN') {
      initTianDiTu();
    } else {
      loadGMap().then(() => {
        getGoogleMap();
      });
    }
  });
}
function getServerInfo() {
  getServer()
    .then((response: any) => {
      Object.assign(server, response.data);
      tableData.value = [
        {
          server: t('home.serverName'),
          serverContent: server.sys.computerName,
          java: t('home.javaName'),
          javaContent: server.jvm.name,
        },
        {
          server: t('home.serverIp'),
          serverContent: server.sys.computerIp,
          java: t('home.startTime'),
          javaContent: server.jvm.startTime,
        },
        {
          server: t('home.system'),
          serverContent: server.sys.osName,
          java: t('home.javaVer'),
          javaContent: server.jvm.version,
        },
        {
          server: t('home.architecture'),
          serverContent: server.sys.osArch,
          java: t('home.runtime'),
          javaContent: server.jvm.runTime,
        },
        {
          server: t('home.core'),
          serverContent: server.cpu.cpuNum,
          java: t('home.memory'),
          javaContent: server.jvm.used,
        },
        { server: t('home.size'), serverContent: server.mem.total, java: t('home.JVM'), javaContent: server.jvm.total },
      ];
      nextTick(() => {
        drawPieCpu();
        drawPieMemery();
        drawPieDisk();
      });
    })
    .catch((error: any) => {
      if (error?.code === 403) hasSever.value = false;
    });
}
// Map methods
function getBaiduMap() {
  if (!mapRef.value) {
    console.error('百度地图 DOM 容器不存在');
    return;
  }
  if (bMapChart.value) {
    try {
      bMapChart.value.getOption();
    } catch (error) {
      console.warn('百度地图 ECharts 实例已失效，重新初始化');
      bMapChart.value.dispose();
      bMapChart.value = null;
    }
  }
  if (!bMapChart.value) {
    bMapChart.value = echarts.init(mapRef.value);
    window.addEventListener('resize', () => bMapChart.value?.resize());
  }
  setMapOption(bMapChart.value);
}
function getGoogleMap() {
  const mapEl = document.getElementById('map');
  if (!mapEl || !(window as any).google?.maps) return;

  if (gMapChart.value) {
    gMapChart.value.dispose?.();
    gMapChart.value = null;
  }
  googleMap.value = new (window as any).google.maps.Map(mapEl, {
    center: { lat: 38, lng: 105 },
    zoom: 5,
  });
  createGoogleMarkers();
}

function clearGoogleMarkers() {
  googleMarkers.value.forEach((marker) => {
    marker.map = null;
  });
  googleMarkers.value = [];
}

function createGoogleMarkers() {
  if (!googleMap.value || !(window as any).google?.maps) return;
  clearGoogleMarkers();

  const google = (window as any).google;
  const infoWindow = new google.maps.InfoWindow();
  deviceList.value.forEach((device: any) => {
    const lng = Number(device.longitude);
    const lat = Number(device.latitude);
    if (Number.isNaN(lng) || Number.isNaN(lat)) return;

    const markerEl = document.createElement('div');
    markerEl.style.cssText = `
      width:${device.status === 3 || device.hasAlert ? 16 : 12}px;
      height:${device.status === 3 || device.hasAlert ? 16 : 12}px;
      border-radius:50%;
      background:${device.hasAlert ? statusColors[5] : statusColors[device.status] || '#909399'};
      border:1px solid #fff;
      box-shadow:0 1px 4px rgba(0,0,0,0.35);
      cursor:pointer;
    `;

    const marker = new google.maps.marker.AdvancedMarkerElement({
      position: { lat, lng },
      map: googleMap.value,
      title: device.deviceName,
      content: markerEl,
    });

    markerEl.addEventListener('mouseenter', () => {
      infoWindow.setContent(
        `<div style="line-height:24px;font-size:12px;">
          设备名称：<span style="color:#486FF2">${device.deviceName || ''}</span><br/>
          设备编号：${device.serialNumber || ''}<br/>
          产品名称：${device.productName || ''}<br/>
          所在地址：${device.networkAddress || ''}
        </div>`
      );
      infoWindow.open(googleMap.value, marker);
    });
    markerEl.addEventListener('mouseleave', () => infoWindow.close());
    marker.addEventListener('gmp-click', () => {
      router.push({ path: '/iot/device/edit', query: { t: Date.now(), deviceId: device.deviceId } });
    });

    googleMarkers.value.push(marker);
  });
}
function rebuildChartIfContainerChanged(chartRef: any, elRef: any) {
  const chart = chartRef.value;
  const el = elRef.value;
  if (!chart || !el) return;
  const currentDom = chart.getDom?.();
  if (currentDom && currentDom !== el) {
    chart.dispose();
    chartRef.value = null;
  }
}

function observeChartContainers() {
  if (chartResizeObserver) {
    chartResizeObserver.disconnect();
    chartResizeObserver = null;
  }

  let resizeTimer: ReturnType<typeof setTimeout> | null = null;
  chartResizeObserver = new ResizeObserver(() => {
    if (resizeTimer) clearTimeout(resizeTimer);
    resizeTimer = setTimeout(() => {
      refreshChartsAfterScreenChange();
    }, 120);
  });

  [pieMemery.value, pieDisk.value, pieCpu.value, lineChart.value, statsChart.value]
    .filter(Boolean)
    .forEach((el) => chartResizeObserver?.observe(el as Element));
}

function refreshChartsAfterScreenChange() {
  nextTick(() => {
    rebuildChartIfContainerChanged(pieCpuChart, pieCpu);
    rebuildChartIfContainerChanged(rateChart, pieMemery);
    rebuildChartIfContainerChanged(sysChart, pieDisk);
    rebuildChartIfContainerChanged(loginUserChart, lineChart);
    rebuildChartIfContainerChanged(mqttChart, statsChart);

    drawPieCpu();
    drawPieMemery();
    drawPieDisk();
    drawLine();
    drawStats();

    setTimeout(() => {
      handleResize();
    }, 120);
  });
}

function handleVisibilityChange() {
  if (document.visibilityState === 'visible') {
    refreshChartsAfterScreenChange();
    if (tMapChart.value) {
      setTimeout(() => {
        tMapChart.value.checkResize?.();
        tMapChart.value.enableDrag(true);
        tMapChart.value.enableScrollWheelZoom(true);
      }, 100);
    }
  }
}
function handlePageShow(event: PageTransitionEvent) {
  if (event.persisted) {
    refreshChartsAfterScreenChange();
    if (tMapChart.value) {
      setTimeout(() => {
        tMapChart.value.checkResize?.();
        tMapChart.value.enableDrag(true);
        tMapChart.value.enableScrollWheelZoom(true);
      }, 100);
    }
  }
}
async function initTianDiTu() {
  try {
    await nextTick();
    const tdtContainer = document.getElementById('tdtMap');
    if (!tdtContainer) throw new Error('天地图容器未找到');
    if (tdtContainer.offsetWidth === 0 || tdtContainer.offsetHeight === 0)
      throw new Error('天地图容器无有效尺寸，请检查样式');
    await loadTianDiTu();
    setMapTitleText();
    tMapChart.value = new (window as any).T.Map(tdtContainer);
    const center = new (window as any).T.LngLat(105, 30);
    tMapChart.value.centerAndZoom(center, 5);
    tMapChart.value.addControl(new (window as any).T.Control.Zoom());
    tMapChart.value.enableScrollWheelZoom(true);
    tMapChart.value.enableDrag(true);
    createDeviceMarkers();
    bindMapEvents();
    return true;
  } catch (error: any) {
    console.error('天地图初始化失败：', error.message);
    return false;
  }
}
function setMapTitleText() {
  if (!deviceList.value || !Array.isArray(deviceList.value)) {
    mapTitle.value = `${t('home.onlineDevice')}（0）`;
    return;
  }
  const onlineCount = deviceList.value.reduce((count: number, device: any) => {
    return count + (device.status === 3 ? 1 : 0);
  }, 0);
  mapTitle.value = `${t('home.onlineDevice')}（${onlineCount}）`;
}
function bindMapEvents() {
  if (!tMapChart.value) return;
  tMapChart.value.on('zoomend', () => {
    tMapState.zoom = tMapChart.value.getZoom();
  });
  tMapChart.value.on('moveend', () => {
    const center = tMapChart.value.getCenter();
    tMapState.center = { lng: center.lng, lat: center.lat };
  });
  window.addEventListener('resize', handleResize);
}
function createEChartsIcon(status: number, hasAlert = false) {
  const size = 20;
  const actualStatus = hasAlert ? 5 : status;
  const color = statusColors[actualStatus] || '#909399';
  let svg = '';
  if (actualStatus === 3 || actualStatus === 5) {
    svg = `<svg width="${size * 10}" height="${size * 10}" xmlns="http://www.w3.org/2000/svg">
            <circle cx="${size * 5}" cy="${size * 5}" r="0" fill="none" stroke="${color}" stroke-width="2" opacity="1">
                <animate attributeName="r" values="0;${size * 4};0" dur="6s" repeatCount="indefinite"/>
                <animate attributeName="opacity" values="1;0;1" dur="6s" repeatCount="indefinite"/>
            </circle>
            <circle cx="${size * 5}" cy="${size * 5}" r="0" fill="none" stroke="${color}" stroke-width="1.5" opacity="1">
                <animate attributeName="r" values="0;${size * 3};0" dur="4s" repeatCount="indefinite" begin="1.2s"/>
                <animate attributeName="opacity" values="1;0;1" dur="4s" repeatCount="indefinite" begin="1.2s"/>
            </circle>
            <circle cx="${size * 5}" cy="${size * 5}" r="${size * 0.8}" fill="${color}"/>
            ${
              actualStatus === 5
                ? `<circle cx="${size * 5}" cy="${size * 5}" r="0" fill="none" stroke="#ff0000" stroke-width="2" opacity="1">
                <animate attributeName="r" values="0;${size * 1.2};0" dur="2s" repeatCount="indefinite"/>
                <animate attributeName="opacity" values="1;0.3;1" dur="2s" repeatCount="indefinite"/>
            </circle>`
                : ''
            }
        </svg>`;
  } else {
    svg = `<svg width="${size * 4}" height="${size * 4}" xmlns="http://www.w3.org/2000/svg">
            <circle cx="${size * 2}" cy="${size * 2}" r="${size * 0.6}" fill="${color}" opacity="0.8"/>
        </svg>`;
  }
  const base64SVG = `data:image/svg+xml;base64,${btoa(unescape(encodeURIComponent(svg)))}`;
  const isAnimated = actualStatus === 3 || actualStatus === 5;
  return new (window as any).T.Icon({
    iconUrl: base64SVG,
    iconSize: isAnimated ? [size * 5, size * 5] : [size * 2, size * 2],
    iconAnchor: isAnimated ? [size * 2.5, size * 2.5] : [size, size],
    shadowUrl: '',
    shadowSize: [0, 0],
    shadowAnchor: [0, 0],
  });
}
function createDeviceMarkers() {
  clearDeviceMarkers();
  if (!tMapChart.value || !deviceList.value.length) return;
  deviceList.value.forEach((device: any) => {
    try {
      const {
        deviceId = `marker_${Date.now()}_${Math.random().toString(36).slice(2)}`,
        deviceName,
        longitude,
        latitude,
        status = false,
        hasAlert = false,
        isShadow,
        firmwareVersion,
        networkAddress,
        productName,
        activeTime,
        serialNumber,
        locationWay,
      } = device;
      const lon = parseFloat(longitude),
        lat = parseFloat(latitude);
      if (isNaN(lon) || lon < -180 || lon > 180 || isNaN(lat) || lat < -90 || lat > 90) return;
      const lngLat = new (window as any).T.LngLat(lon, lat);
      const icon = createEChartsIcon(status, hasAlert);
      const marker = new (window as any).T.Marker(lngLat, { icon });
      marker.deviceData = {
        deviceId,
        deviceName,
        status,
        isShadow,
        firmwareVersion,
        networkAddress,
        productName,
        activeTime,
        serialNumber,
        locationWay,
        hasAlert,
      };
      let currentInfoWindow: any = null;
      marker.on('mouseover', () => {
        showDeviceInfoWindow(marker, lngLat, (iw: any) => {
          currentInfoWindow = iw;
        });
      });
      marker.on('mouseout', () => {
        if (currentInfoWindow && tMapChart.value) tMapChart.value.closeInfoWindow(currentInfoWindow);
      });
      marker.on('click', () => {
        const old = document.getElementById('custom-device-info');
        if (old) old.remove();
        router.push({ path: '/iot/device/edit', query: { t: Date.now(), deviceId } });
      });
      marker.addTo(tMapChart.value);
      deviceMarkers.value.push(marker);
    } catch (e: any) {
      console.error(`创建设备【${device.deviceName || '未知'}】Marker失败：`, e.message);
    }
  });
}
function showDeviceInfoWindow(marker: any, position: any, callback?: (iw: any) => void) {
  const data = marker.deviceData;
  if (!data) return null;
  const old = document.getElementById('custom-device-info');
  if (old) old.remove();
  const dom = document.createElement('div');
  dom.id = 'custom-device-info';
  dom.style.cssText =
    'position:absolute;z-index:9999!important;padding:8px 10px;line-height:26px;background:#fff;border:1px solid #ccc;border-radius:2px;box-shadow:0 1px 5px rgba(0,0,0,0.2);font-size:12px;color:#333;min-width:220px;max-width:300px;';
  let h = `设备名称： <span style='color:#486FF2'>${data.deviceName}</span><br/>设备编号： ${data.serialNumber}<br/>设备状态： `;
  if (data.status == 1) h += "<span style='color:#E6A23C'>未激活</span><br/>";
  else if (data.status == 2) h += "<span style='color:#F56C6C'>禁用</span><br/>";
  else if (data.status == 3) h += "<span style='color:#67C23A'>在线</span><br/>";
  else if (data.status == 4) h += "<span style='color:#909399'>离线</span><br/>";
  h +=
    data.isShadow == 1
      ? "设备影子： <span style='color:#67C23A'>启用</span><br/>"
      : "设备影子： <span style='color:#909399'>未启用</span><br/>";
  h += `产品名称： ${data.productName}<br/>固件版本： Version ${data.firmwareVersion}<br/>激活时间： ${data.activeTime}<br/>定位方式： `;
  if (data.locationWay == 1) h += '自动定位<br/>';
  else if (data.locationWay == 2) h += '设备定位<br/>';
  else if (data.locationWay == 3) h += '自定义位置<br/>';
  else h += '未知<br/>';
  h +=
    '是否告警： ' +
    (data.hasAlert === true
      ? "<span style='color:#F56C6C'>是</span><br/>"
      : "<span style='color:#67C23A'>否</span><br/>");
  h += `所在地址： ${data.networkAddress}<br/>`;
  dom.innerHTML = h;
  const mapDom = document.getElementById('tdtMap');
  if (mapDom) {
    const rect = mapDom.getBoundingClientRect();
    const pt = tMapChart.value.lngLatToContainerPoint(position);
    dom.style.left = `${rect.left + pt.x + 10}px`;
    dom.style.top = `${rect.top + pt.y - 40}px`;
  }
  document.body.appendChild(dom);
  dom.addEventListener('click', () => {
    router.push({ path: '/iot/device/edit', query: { t: Date.now(), deviceId: data.deviceId } });
    dom.remove();
  });
  const ciw = { dom, close: () => dom.remove() };
  if (callback) callback(ciw);
  marker.on('mouseout', () => ciw.close());
  return ciw;
}
function clearDeviceMarkers() {
  if (!tMapChart.value || !deviceMarkers.value.length) return;
  deviceMarkers.value.forEach((m: any) => {
    try {
      tMapChart.value.removeLayer(m);
      m.off('mouseover');
      m.off('mouseout');
      m.off('click');
    } catch (e: any) {
      console.warn('移除Marker失败：', e.message);
    }
  });
  deviceMarkers.value = [];
}
function destroyEchartsInstance() {
  if (echartsMapInstance.value) {
    try {
      echartsMapInstance.value.off('click');
      echartsMapInstance.value.dispose();
    } catch (error) {
      console.warn('ECharts 实例销毁失败：', error);
    } finally {
      echartsMapInstance.value = null;
    }
  }
}
function setMapOption(chart: any) {
  chart.on('click', (params: any) => {
    if (params.data.deviceId)
      router.push({ path: '/iot/device/edit', query: { t: Date.now(), deviceId: params.data.deviceId } });
  });
  const convertData = (data: any[], status: number) => {
    const res: any[] = [];
    for (let i = 0; i < data.length; i++) {
      const gc = [data[i].longitude, data[i].latitude];
      if (gc && data[i].status == status && status !== 5) {
        res.push({
          name: data[i].deviceName,
          value: gc,
          status: data[i].status,
          isShadow: data[i].isShadow,
          firmwareVersion: data[i].firmwareVersion,
          networkAddress: data[i].networkAddress,
          productName: data[i].productName,
          activeTime: data[i].activeTime ?? '',
          deviceId: data[i].deviceId,
          serialNumber: data[i].serialNumber,
          locationWay: data[i].locationWay,
          hasAlert: data[i].hasAlert,
        });
      }
      if (status == 5 && gc && data[i].hasAlert == true) {
        res.push({
          name: data[i].deviceName,
          value: gc,
          status: data[i].status,
          isShadow: data[i].isShadow,
          firmwareVersion: data[i].firmwareVersion,
          networkAddress: data[i].networkAddress,
          productName: data[i].productName,
          activeTime: data[i].activeTime ?? '',
          deviceId: data[i].deviceId,
          serialNumber: data[i].serialNumber,
          locationWay: data[i].locationWay,
          hasAlert: data[i].hasAlert,
        });
      }
    }
    return res;
  };
  const option = {
    title: {
      text: t('home.onlineDevice') + deviceList.value.filter((x: any) => x.status == 3).length + '）',
      subtext: 'Fastbee open source iot platform',
      sublink: 'https://iot.fastbee.cn',
      target: '_blank',
      color: '#303133',
      textBorderColor: '#fff',
      textBorderWidth: 10,
      fontSize: 16,
      top: 10,
      left: 'center',
    },
    tooltip: {
      trigger: 'item',
      formatter: function (params: any) {
        let h = '<div style="padding:5px;line-height:28px;">';
        h += `设备名称： <span style='color:#486FF2'>${params.data.name}</span><br/>`;
        h += `设备编号： ${params.data.serialNumber}<br/>设备状态： `;
        if (params.data.status == 1) h += "<span style='color:#E6A23C'>未激活</span><br/>";
        else if (params.data.status == 2) h += "<span style='color:#F56C6C'>禁用</span><br/>";
        else if (params.data.status == 3) h += "<span style='color:#67C23A'>在线</span><br/>";
        else if (params.data.status == 4) h += "<span style='color:#909399'>离线</span><br/>";
        h +=
          params.data.isShadow == 1
            ? "设备影子： <span style='color:#67C23A'>启用</span><br/>"
            : "设备影子： <span style='color:#909399'>未启用</span><br/>";
        h += `产品名称： ${params.data.productName}<br/>固件版本： Version ${params.data.firmwareVersion}<br/>激活时间： ${params.data.activeTime}<br/>定位方式： `;
        if (params.data.locationWay == 1) h += '自动定位<br/>';
        else if (params.data.locationWay == 2) h += '设备定位<br/>';
        else if (params.data.locationWay == 3) h += '自定义位置<br/>';
        else h += '未知<br/>';
        h +=
          '是否告警： ' +
          (params.data.hasAlert === true
            ? "<span style='color:#F56C6C'>是</span><br/>"
            : "<span style='color:#67C23A'>否</span><br/>");
        h += `所在地址： ${params.data.networkAddress}<br/></div>`;
        return h;
      },
    },
    bmap: { center: [105, 38], zoom: 5, roam: true },
    series: [
      {
        type: 'scatter',
        coordinateSystem: 'bmap',
        data: convertData(deviceList.value, 1),
        symbolSize: 15,
        itemStyle: { color: '#E6A23C' },
      },
      {
        type: 'scatter',
        coordinateSystem: 'bmap',
        data: convertData(deviceList.value, 2),
        symbolSize: 15,
        itemStyle: { color: '#F56C6C' },
      },
      {
        type: 'scatter',
        coordinateSystem: 'bmap',
        data: convertData(deviceList.value, 4),
        symbolSize: 15,
        itemStyle: { color: '#909399' },
      },
      {
        type: 'effectScatter',
        coordinateSystem: 'bmap',
        data: convertData(deviceList.value, 3),
        symbolSize: 15,
        showEffectOn: 'render',
        rippleEffect: { brushType: 'stroke', scale: 5 },
        label: { formatter: '{b}', position: 'right', show: false },
        itemStyle: { color: '#67C23A', shadowBlur: 100, shadowColor: '#333' },
        zlevel: 1,
      },
      {
        type: 'effectScatter',
        coordinateSystem: 'bmap',
        data: convertData(deviceList.value, 5),
        symbolSize: 15,
        showEffectOn: 'render',
        rippleEffect: { brushType: 'stroke', scale: 5 },
        label: { formatter: '{b}', position: 'right', show: true },
        itemStyle: { color: '#e80705', shadowBlur: 100, shadowColor: '#333' },
        zlevel: 1,
      },
    ],
  };
  chart.setOption(option);
}
async function handleMapChange(curVal: any) {
  if (!curVal || currentChange.value === curVal.id) return;
  currentChange.value = curVal.id;
  try {
    switch (curVal.id) {
      case 0:
        if (tMapChart.value) {
          const center = new (window as any).T.LngLat(tMapState.center.lng, tMapState.center.lat);
          tMapChart.value.centerAndZoom(center, tMapState.zoom);
          tMapChart.value.enableDrag(true);
          tMapChart.value.enableScrollWheelZoom(true);
        } else {
          await initTianDiTu();
        }
        break;
      case 1:
        await loadBMap();
        getBaiduMap();
        break;
      case 2:
        await loadGMap();
        getGoogleMap();
        break;
    }
  } catch (error) {
    console.log('地图切换失败：', error);
  }
}

function handleMapDropdownCommand(id: number | string) {
  const mapItem = mapSwitchDatas.value.find((item) => item.id === Number(id));
  handleMapChange(mapItem);
}
function safeResize(chartRef: any) {
  const chart = chartRef.value;
  if (!chart) return;

  const dom = chart.getDom?.();
  if (dom) {
    const rect = dom.getBoundingClientRect();
    if (rect.width === 0 || rect.height === 0) return;
  }

  try {
    chart.resize();
  } catch {
    try {
      chart.dispose?.();
    } catch (_error) {
      // ignore invalid chart instance
    }
    chartRef.value = null;
  }
}

function handleResize() {
  if (activeHomeTab.value !== HOME_TAB_DASHBOARD) return;
  if (bMapChart.value) safeResize(bMapChart);
  if (gMapChart.value) safeResize(gMapChart);
  if (pieCpuChart.value) {
    safeResize(pieCpuChart);
    updateRadius();
  }
  if (rateChart.value) safeResize(rateChart);
  if (sysChart.value) safeResize(sysChart);
  if (loginUserChart.value) safeResize(loginUserChart);
  if (mqttChart.value) safeResize(mqttChart);
  if (tMapChart.value)
    setTimeout(() => {
      tMapChart.value?.checkResize();
    }, 100);
}
// Chart drawing methods
function drawPieCpu() {
  if (!isAdmin.value || !pieCpu.value) return;
  if (!pieCpuChart.value) pieCpuChart.value = echarts.init(pieCpu.value);
  const total = (server.cpu.used || 0) + (server.cpu.sys || 0) + (server.cpu.free || 100);
  const userd = ((server.cpu.used / total) * 100).toFixed(2);
  const sys = ((server.cpu.sys / total) * 100).toFixed(2);
  const free = ((server.cpu.free / total) * 100).toFixed(2);
  const mkPie = (val: string, center: string, colors: string[]) => [
    {
      type: 'pie',
      clockwise: false,
      startAngle: 90,
      radius: radius.value,
      center: [center, '50%'],
      data: [100],
      itemStyle: { color: '#eee' },
      animation: false,
    },
    {
      type: 'pie',
      labelLine: { show: false },
      radius: radius.value,
      center: [center, '50%'],
      itemStyle: { color: '#000' },
      data: [
        {
          value: parseFloat(val),
          label: {
            formatter: `{label|${val}%}`,
            position: 'center',
            show: true,
            fontSize: '20',
            fontWeight: 'bold',
            color: '#eee',
            lineHeight: 20,
            rich: {
              label: {
                fontFamily: 'Roboto, Roboto',
                textAlign: 'center',
                fontStyle: 'normal',
                fontSize: labelFontSize.value,
                fontWeight: 'bold',
                color: '#303133',
              },
            },
          },
          itemStyle: {
            color: new echarts.graphic.LinearGradient(
              0,
              0,
              colors[2] === '1' ? 0 : 1,
              colors[2] === '1' ? 1 : 0,
              [
                { offset: 0, color: colors[0] },
                { offset: 1, color: colors[1] },
              ],
              false
            ),
            borderRadius: ['50%', '50%'],
          },
        },
        { value: 100 - parseFloat(val), itemStyle: { color: 'transparent', borderCap: 'round' } },
      ],
    },
  ];
  const option: any = {
    title: { text: '', left: 'center', top: '20px', fontSize: 18, color: '#333' },
    series: [
      ...mkPie(userd, '15%', ['#4474ec', '#92b3fa', '1']),
      ...mkPie(sys, '48%', ['#10bcff', '#81e1fd', '0']),
      ...mkPie(free, '78%', ['#17bcb8', '#75e2dc', '0']),
    ],
    graphic: [
      {
        type: 'text',
        left: 10,
        top: '15%',
        style: { text: '用户', font: '400 12px PingFangSC, PingFang SC', fill: '#606266' },
      },
      {
        type: 'text',
        left: '30%',
        top: '15%',
        style: { text: '系统', font: '400 12px PingFangSC, PingFang SC', fill: '#606266' },
      },
      {
        type: 'text',
        left: '60%',
        top: '15%',
        style: { text: '空闲', font: '400 12px PingFangSC, PingFang SC', fill: '#606266' },
      },
    ],
  };
  pieCpuChart.value.setOption(option);
}
function drawPieMemery() {
  if (!pieMemery.value) return;
  if (!rateChart.value) rateChart.value = echarts.init(pieMemery.value);
  const val = server.mem.used / (server.mem.used + server.mem.free) || 0;
  const option = {
    title: [
      {
        text: (val * 100).toFixed(0) + '%',
        x: 'center',
        y: '35%',
        textStyle: {
          fontFamily: 'Roboto, Roboto',
          fontWeight: 'bold',
          fontStyle: 'normal',
          fontSize: 24,
          lineHeight: 28,
          color: '#303133',
          alignment: 'center',
        },
      },
      {
        text: `已用：{a|${server.mem.used}}GB\n剩余：{a|${server.mem.free}} GB`,
        x: 'center',
        y: '50%',
        borderColor: '#fff',
        textStyle: {
          fontWeight: 'normal',
          fontSize: 12,
          color: '#444',
          rich: {
            a: {
              fontFamily: 'PingFangSC, PingFang SC',
              fontStyle: 'normal',
              textAlign: 'center',
              fontWeight: '400',
              fontSize: 12,
              lineHeight: 17,
              color: '#606266',
            },
          },
        },
      },
    ],
    polar: { center: ['50%', '50%'], radius: ['65%', '90%'] },
    angleAxis: { max: 100, show: false },
    radiusAxis: {
      type: 'category',
      show: true,
      axisLabel: { show: false },
      axisLine: { show: false },
      axisTick: { show: false },
    },
    series: [
      {
        data: [
          {
            value: val * 100,
            name: '已使用',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#887BF2' },
                { offset: 1, color: '#BDB2FA' },
              ]),
              borderRadius: ['50%', '50%'],
            },
          },
        ],
        name: '',
        type: 'bar',
        roundCap: true,
        showBackground: true,
        backgroundStyle: { color: '#eaeaf4' },
        coordinateSystem: 'polar',
      },
    ],
  };
  rateChart.value.setOption(option);
}
function drawPieDisk() {
  if (!pieDisk.value) return;
  if (!sysChart.value) sysChart.value = echarts.init(pieDisk.value);
  const sf = server.sysFiles?.[0];
  if (!sf) return;
  const one = parseFloat(String(sf.used).replace('GB', '') || '0');
  const two = parseFloat(String(sf.free).replace('GB', '') || '0');
  const val = one / (one + two) || 0;
  const option = {
    title: [
      {
        text: (val * 100).toFixed(0) + '%',
        x: 'center',
        y: '35%',
        textStyle: {
          fontFamily: 'Roboto, Roboto',
          fontWeight: 'bold',
          fontStyle: 'normal',
          fontSize: 24,
          lineHeight: 28,
          color: '#303133',
          alignment: 'center',
        },
      },
      {
        text: `{a|已用：${sf.used}}GB\n{a|剩余：${sf.free}} GB`,
        x: 'center',
        y: '50%',
        borderColor: '#fff',
        textStyle: {
          fontWeight: 'normal',
          fontSize: 12,
          color: '#444',
          rich: {
            a: {
              fontFamily: 'PingFangSC, PingFang SC',
              fontStyle: 'normal',
              textAlign: 'center',
              fontWeight: '400',
              fontSize: 12,
              lineHeight: 17,
              color: '#606266',
            },
          },
        },
      },
    ],
    polar: { center: ['50%', '50%'], radius: ['65%', '90%'] },
    angleAxis: { max: 100, show: false },
    radiusAxis: {
      type: 'category',
      show: true,
      axisLabel: { show: false },
      axisLine: { show: false },
      axisTick: { show: false },
    },
    series: [
      {
        data: [
          {
            value: val * 100,
            name: '已使用',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#F286D8' },
                { offset: 1, color: '#FFC3F1' },
              ]),
              borderRadius: ['50%', '50%'],
            },
          },
        ],
        name: '',
        type: 'bar',
        roundCap: true,
        showBackground: true,
        backgroundStyle: { color: '#eaeaf4' },
        coordinateSystem: 'polar',
      },
    ],
  };
  sysChart.value.setOption(option);
}
function drawLine() {
  if (!lineChart.value) return;
  if (!loginUserChart.value) loginUserChart.value = echarts.init(lineChart.value);
  const option = {
    title: {
      text: t('views.index.394840-16'),
      fontFamily: 'PingFangSC, PingFang SC',
      lineHeight: 22,
      fontWeight: 600,
      fontSize: 16,
      color: '#303133',
      fontStyle: 'normal',
      textAlign: 'left',
    },
    grid: {
      left: '3%', // 左侧距离
      right: '10%', // 右侧距离
      top: '20%', // 顶部距离（留出标题空间）
      bottom: '2%', // X 轴距离底部 15%
      containLabel: true, // 确保坐标轴标签完整显示
      backgroundColor: 'transparent',
    },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: linechart.date,
      axisLabel: { formatter: (value: string) => dayjs(value).format('YYYY.MM.DD') },
    },
    yAxis: [
      {
        type: 'value',
        splitNumber: 4,
        splitLine: { lineStyle: { type: 'dashed', color: '#DDD' } },
        axisLine: { show: false, lineStyle: { color: '#333' } },
        nameTextStyle: { color: '#999' },
        splitArea: { show: false },
      },
    ],
    series: [
      {
        name: t('views.index.394840-16'),
        data: linechart.counts,
        radius: '55%',
        type: 'line',
        smooth: true,
        showSymbol: false,
        lineStyle: { width: 3, color: '#8095d8' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 1,
            x2: 0,
            y2: 0,
            colorStops: [
              { offset: 0, color: 'rgba(96,116,208,0)' },
              { offset: 1, color: 'rgba(70,130,180,0.3)' },
            ],
            global: false,
          },
        },
      },
    ],
  };
  loginUserChart.value.setOption(option);
}

function initHomeDashboardOnce() {
  if (dashboardInitialized.value) {
    nextTick(() => {
      refreshChartsAfterScreenChange();
      handleResize();
    });
    return;
  }
  dashboardInitialized.value = true;
  init();
  getLocation();
  getNoticeList();
  getEventStatisticData();
  getDeviceCountStatisticData();
  getFunctionStatisticData();
  getMqttStats();
  statisticMqtt();
  getConfigKey('sys.logo.config').then((response: any) => {
    if (response.code === 200 && response.msg !== '' && response.msg !== '{}') {
      Object.assign(systemForm, JSON.parse(response.msg));
    }
  });
  startScroll();
  getAllDevice();
  nextTick(() => {
    drawPieCpu();
    observeChartContainers();
    window.addEventListener('resize', handleResize);
  });
  document.addEventListener('visibilitychange', handleVisibilityChange);
  window.addEventListener('pageshow', handlePageShow);
}

watch(activeHomeTab, (tab) => {
  if (tab === HOME_TAB_DASHBOARD) {
    nextTick(() => initHomeDashboardOnce());
  }
});

watch(hasAiChatPermission, (allowed) => {
  if (!allowed && activeHomeTab.value === HOME_TAB_AI) {
    activeHomeTab.value = HOME_TAB_DASHBOARD;
  }
});

// Lifecycle hooks
onMounted(() => {
  if (activeHomeTab.value === HOME_TAB_DASHBOARD) {
    initHomeDashboardOnce();
  }
});
onActivated(() => {
  if (activeHomeTab.value !== HOME_TAB_DASHBOARD || !dashboardInitialized.value) {
    return;
  }
  // 页面激活时，恢复图表和地图状态
  refreshChartsAfterScreenChange();
  if (tMapChart.value) {
    const center = new (window as any).T.LngLat(tMapState.center.lng, tMapState.center.lat);
    tMapChart.value.centerAndZoom(center, tMapState.zoom);
    tMapChart.value.enableDrag(true);
    tMapChart.value.enableScrollWheelZoom(true);
  }
});
onBeforeUnmount(() => {
  stopScroll();
  intervalIds.forEach((id) => clearInterval(id));
  intervalIds.clear();
  chartInstances.forEach((chart) => {
    if (chart && typeof chart.dispose === 'function') chart.dispose();
  });
  chartInstances.clear();
  window.removeEventListener('resize', handleResize);
  document.removeEventListener('visibilitychange', handleVisibilityChange);
  window.removeEventListener('pageshow', handlePageShow);
  if (mapResizeObserver) {
    mapResizeObserver.disconnect();
    mapResizeObserver = null;
  }
  if (chartResizeObserver) {
    chartResizeObserver.disconnect();
    chartResizeObserver = null;
  }
});
</script>

<style lang="scss" scoped>
.home-page-container {
  padding: 10px;

  .home-tab-bar {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    padding: 0 10px;
  }

  .home-dashboard-content {
    min-width: 0;
  }

  .statistics-container {
    margin: 10px 0px 20px !important;

    .statistics-item {
      margin-bottom: 10px;
    }
  }

  // 设备数据统计显示
  .card-panel {
    height: 98px;
    border-radius: 8px;
    background: #fff;
    box-shadow: 0 2px 3px rgba(0, 0, 0, 0.1);
    padding: 24px;
    transition:
      transform 0.3s ease,
      box-shadow 0.3s ease;
    cursor: pointer;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 12px rgba(0, 0, 0, 0.15);
    }
    .device {
      .card-panel-icon {
        color: #08bdff;
      }
      .card-icon {
        background-color: #e5f8ff;
        &:hover .card-panel-icon {
          color: #007aff;
        }
      }
    }
    .product {
      .card-panel-icon {
        color: #f385d9;
      }
      .card-icon {
        background-color: #ffeef8;
        &:hover .card-panel-icon {
          color: #007aff;
        }
      }
    }
    .function {
      .card-panel-icon {
        color: #feaf31;
      }
      .card-icon {
        background-color: #fdf0d7;
        &:hover .card-panel-icon {
          color: #007aff;
        }
      }
    }
    .monitor {
      .card-panel-icon {
        color: #1cbdb5;
      }
      .card-icon {
        background-color: #dbf9f7;
        &:hover .card-panel-icon {
          color: #007aff;
        }
      }
    }
    .alert {
      .card-panel-icon {
        color: #f66b6c;
      }
      .card-icon {
        background-color: #ffe8e8;
        &:hover .card-panel-icon {
          color: #007aff;
        }
      }
    }
    .reports {
      .card-panel-icon {
        color: #a076ef;
      }
      .card-icon {
        background-color: #eae8fe;
        &:hover .card-panel-icon {
          color: #007aff;
        }
      }
    }

    .card-content {
      display: flex;
      flex-direction: column;
      justify-content: flex-start;
      align-items: flex-start;
      height: 100%;

      .card-icon {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 48px;
        height: 48px;
        margin-bottom: 15px;
        padding: 10px;
        border-radius: 10px;
        .card-panel-icon {
          width: 80%;
          height: 80%;
          transition: fill 0.3s ease;
        }
      }

      .card-data {
        display: flex;
        flex-direction: column;
        justify-content: left;
        float: left;
        margin-left: -10px;

        .card-title {
          font-weight: 400;
          font-size: 13px;
          color: #606266;
          line-height: 18px;
          text-align: left;
          font-style: normal;
        }

        .card-panel-num {
          width: 14px;
          height: 28px;
          text-align: left;
          margin-top: 2px;
          font-family: Roboto, Roboto;
          font-weight: bold;
          font-size: 24px;
          font-style: normal;
          color: #303133;
          line-height: 28px;
        }
      }
    }

    @media screen and (max-width: 968px) {
      height: 100px;
      padding: 10px;
      margin-bottom: 10px;
      .card-content {
        .card-icon {
          width: 40px;
          height: 40px;
          margin: 20px 0;
        }
        .card-data {
          .card-title {
            font-size: 12px;
            margin-top: 20px;
          }
          .card-panel-num {
            margin-bottom: 10px;
            font-size: 10px;
          }
        }
      }
    }
    @media screen and (min-width: 1920px) {
      height: 125px;
      padding: 10px;
      margin-bottom: 10px;
      .card-content {
        .card-icon {
          width: 60px;
          height: 60px;
          margin: 20px;
        }
        .card-data {
          .card-title {
            font-size: 22px;
            margin: 30px 0 5px 10px;
          }
          .card-panel-num {
            margin: 0px 0 5px 10px;
            font-size: 22px;
          }
        }
      }
    }
    @media (max-width: 1180px) {
      margin-bottom: 15px;
    }
  }

  // 天气
  $primary-gradient-start: #a1c4fd;
  $primary-gradient-end: #c2e9fb;
  $secondary-color: #000;
  $icon-color: #000;
  $shadow-color: rgba(0, 0, 0, 0.1);
  $hover-shadow-color: rgba(0, 0, 0, 0.2);
  $aqi-good-color: #4caf50;
  $aqi-moderate-color: #ffeb3b;
  $aqi-unhealthy-color: #f44336;

  .weather-card {
    height: 208px;
    background: linear-gradient(135deg, var(--background-start) 0%, var(--background-end) 100%);
    border-radius: 10px;
    color: $secondary-color;
    box-shadow: 0 4px 20px $shadow-color;
    transition:
      transform 0.3s,
      box-shadow 0.3s;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    .weather-header {
      text-align: left;
      margin-bottom: 10px;
      h2 {
        font-weight: 600;
        font-size: 18px;
        color: #303133;
        line-height: 25px;
        text-align: left;
        font-style: normal;
      }
      .date-week {
        font-weight: 400;
        font-size: 13px;
        color: #606266;
        line-height: 18px;
        text-align: left;
        font-style: normal;
      }
    }
    @media screen and (min-width: 1920px) {
      height: 270px;
      padding: 10px;
      margin-bottom: 10px;
    }

    .weather-main {
      display: flex;
      justify-content: center;
      align-items: center;
      margin: 30px 0;
      .weather-icon {
        width: 100px;
        height: 100px;
        flex-shrink: 0;
      }
    }

    .weather-details {
      height: 100%;
      width: 100%;
      display: flex;
      flex-direction: row;
      align-items: center;

      .detail-item {
        display: flex;
        align-items: center;
        gap: 2px;
        color: #606266;

        .detail-text {
          font-weight: 500;
          font-size: 12px;
          line-height: 18px;
          text-align: left;
          font-style: normal;
        }
      }
    }

    .air-quality {
      margin-top: 15px;
      padding: 5px 10px;
      display: flex;
      justify-content: center;
      align-items: center;
      width: 140px;
      height: 22px;
      border-radius: 2px;
      border: 1px solid #67c23a;
      .aqi-info {
        display: flex;
        align-items: center;
        gap: 10px;
        width: 105px;
        height: 18px;
        font-family:
          PingFangSC,
          PingFang SC;
        font-weight: 400;
        font-size: 13px;
        color: #67c23a;
        line-height: 18px;
        text-align: left;
        font-style: normal;
        .aqi-value {
          font-size: 12px;
          font-weight: bold;
          padding: 5px;
          border-radius: 5px;
          color: #4caf50;
          background-color: inherit;
        }
        &.aqi-good {
          background-color: $aqi-good-color;
        }
        &.aqi-moderate {
          background-color: $aqi-moderate-color;
        }
        &.aqi-unhealthy {
          background-color: $aqi-unhealthy-color;
        }
        .aqi-level {
          font-size: 12px;
          font-weight: bold;
          color: #4caf50;
          background-color: inherit;
        }
      }
    }

    .low-temperature {
      font-weight: bold;
      font-size: 40px;
      color: #303133;
      line-height: 47px;
      text-align: left;
      font-style: normal;
    }
    .high-temperature {
      font-weight: 400;
      font-size: 14px;
      color: #606266;
      line-height: 16px;
      text-align: left;
      font-style: normal;
    }
    .weather-description {
      font-family:
        PingFangSC,
        PingFang SC;
      font-weight: 500;
      font-size: 14px;
      color: #303133;
      line-height: 20px;
      text-align: left;
      font-style: normal;
      margin: 0px 2px;
    }

    @media screen and (max-width: 968px) {
      .weather-card {
        padding: 10px;
        height: 400px;
      }
    }
  }

  .map-flex {
    display: flex;
    flex-direction: column;

    // 地图
    .map-card {
      position: relative;
      background: #ffffff;
      border-radius: 10px;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
      transition:
        transform 0.3s ease,
        box-shadow 0.3s ease;
      height: 460px;

      .map-container {
        width: 100%;
        height: 460px;
        position: relative;
        border-radius: 10px;
        overflow: hidden;
        .map {
          width: 100%;
          height: 100%;
        }
      }

      .map-toolbar {
        position: absolute;
        right: 10px;
        bottom: 10px;
        cursor: pointer;
      }
    }

    @media screen and (max-width: 968px) {
      .map-card {
        padding: 2px;
      }
      .card-header {
        padding: 18px 12px;
        .card-title {
          font-size: 16px;
          i {
            font-size: 18px;
          }
        }
      }
      .map-container {
        height: 250px;
        width: 100%;
      }
    }

    @media (max-width: 480px) {
      .map-card {
        padding: 10px;
        border-radius: 10px;
      }
      .card-header {
        padding: 15px 10px;
        .card-title {
          font-size: 14px;
          i {
            font-size: 16px;
          }
        }
      }
      .map-container {
        height: 220px;
      }
    }

    // 使用率
    .rate-card {
      height: 440px;
      padding: 4px;
      width: 100%;
      background: #ffffff;
      border-radius: 10px;
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
      overflow: hidden;
      transition:
        transform 0.3s ease,
        box-shadow 0.3s ease;

      .chart-title {
        font-family:
          PingFangSC,
          PingFang SC;
        font-weight: 600;
        font-size: 16px;
        color: #303133;
        line-height: 22px;
        text-align: left;
        font-style: normal;
      }
      .pieCpu {
        height: 190px;
        width: 100%;
        margin: 20px 0px;
        display: flex;
        flex: 1;
        background: #f6f7fb;
      }
      @media screen and (max-width: 968px) {
        height: 100%;
        width: 100%;
        margin: 20px 0px;
        .pieCpu {
          height: 176px;
          margin: 20px 0px;
          background: #f6f7fb;
          display: flex;
          flex-direction: row;
          flex-wrap: wrap;
          justify-content: space-around;
        }
      }
      @media (min-width: 968px) {
        height: 100%;
        width: 100%;
        margin: 20px 0px;
      }
      .pieMemery {
        background: #f6f7fb;
        height: 176px;
        margin: 20px 20px 20px 0px;
      }
      .pieDisk {
        background: #f6f7fb;
        height: 176px;
        margin: 20px 0px 20px 0px;
      }
    }
  }

  // 信息栏
  .message-card {
    height: 300px;
    margin-bottom: 20px;
    border-radius: 10px;
    padding: 4px;
    box-shadow: 0 4px 20px $shadow-color;
    transition:
      transform 0.3s,
      box-shadow 0.3s;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    .notice-bar {
      overflow: hidden;
      position: relative;
      .item-wrap {
        display: flex;
        flex-direction: row;
        margin-top: 18px;
        .left-wrap {
          flex: 1;
          font-weight: 400;
          font-size: 14px;
          color: #303133;
          line-height: 20px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        .right-wrap {
          font-weight: 400;
          font-size: 12px;
          color: #909399;
          line-height: 17px;
          margin-left: 16px;
        }
      }
    }
    .animating {
      animation: scroll 10s linear infinite;
    }
    .message-title {
      font-family:
        PingFangSC,
        PingFang SC;
      font-weight: 600;
      font-size: 18px;
      color: #303133;
      line-height: 22px;
      text-align: left;
      font-style: normal;
      margin: 0px 0 6px;
    }
  }
  //信息完

  // 登录用户数量
  .line-card {
    height: 320px;
    width: 100%;
    border-radius: 10px;
    padding: 4px;
    color: $secondary-color;
    margin-bottom: 20px;
    box-shadow: 0 4px 20px $shadow-color;
    transition:
      transform 0.3s,
      box-shadow 0.3s;
  }

  // 柱状图，mqtt状态数据
  .card-container {
    height: 360px;
    padding: 4px;
    margin-bottom: 20px;
    border-radius: 10px;
    box-shadow: 0 4px 20px $shadow-color;
    overflow: hidden;
    transition:
      transform 0.3s,
      box-shadow 0.3s;
  }

  @media screen and (max-width: 968px) {
    .card-container {
      .card {
        flex-direction: column;
        height: auto;
        .card-image {
          width: 100%;
          height: auto;
        }
        .card-content {
          width: 100%;
        }
      }
    }
  }

  @media (max-width: 480px) {
    .card-container {
      .card {
        .card-content {
          padding: 10px;
          .author {
            font-size: 1em;
          }
          .content {
            font-size: 0.9em;
          }
          .footer {
            font-size: 0.8em;
          }
        }
        .card-image {
          .overlay .thumb {
            width: 40px;
            height: 40px;
          }
        }
      }
    }
  }

  //小程序，h5
  //小程序，h5
  .phone-card {
    border-radius: 10px;
    box-shadow: 0 4px 20px $shadow-color;
    transition:
      transform 0.3s,
      box-shadow 0.3s;

    .mini-program {
      margin: 8px 0px 35px;
      font-family:
        PingFangSC,
        PingFang SC;
      font-weight: 600;
      font-size: 14px;
      color: #303133;
      line-height: 20px;
      text-align: left;
      font-style: normal;
    }
    .web-site {
      width: 70px;
      font-weight: bold;
      display: table-cell;
      font-family:
        PingFangSC,
        PingFang SC;
      font-weight: 500;
      font-size: 12px;
      color: #606266;
      line-height: 24px;
      text-align: left;
      font-style: normal;
    }
    .other-site {
      display: table-cell;
      font-family:
        PingFangSC,
        PingFang SC;
      font-weight: 400;
      font-size: 12px;
      color: #606266;
      line-height: 24px;
      text-align: left;
      font-style: normal;
    }
  }
  .phone {
    height: 650px;
    width: 320px;
    margin-left: 100px;
    background-image: url('../assets/images/phone.png');
    background-size: cover;

    .frame-remark {
      font-family:
        PingFangSC,
        PingFang SC;
      font-weight: 400;
      font-size: 12px;
      color: #909399;
      line-height: 17px;
      text-align: center;
      font-style: normal;
      margin: 20px auto;
    }
    @media screen and (max-width: 968px) {
      height: 530px;
      width: 260px;
      background-image: url('../assets/images/phone.png');
      background-size: cover;
    }
  }
  .phone {
    height: 650px;
    width: 320px;
    margin-left: 100px;
    background-image: url('../assets/images/phone.png');
    background-size: cover;
    .frame-remark {
      font-family:
        PingFangSC,
        PingFang SC;
      font-weight: 400;
      font-size: 12px;
      color: #909399;
      line-height: 17px;
      text-align: center;
      font-style: normal;
      margin: 20px auto;
    }
    @media screen and (max-width: 968px) {
      height: 530px;
      width: 260px;
      background-image: url('../assets/images/phone.png');
      background-size: cover;
    }
  }

  .phone-container {
    height: 635px;
    width: 291px;
    position: relative;
    border-radius: 35px;
    top: 8px;
    left: 15px;
    background-color: #fff;
    padding-bottom: 0%;
    overflow: hidden;

    .phone-iframe {
      width: 375px;
      height: 812px;
      border: 0;
      transform-origin: left top;
      transform: scale(0.776);
      display: block;
    }

    @media screen and (max-width: 968px) {
      height: 519px;
      width: 240px;
      position: relative;
      border-radius: 30px;
      top: 5px;
      bottom: 10px;
      left: 10px;
      background-color: #fff;
      padding-bottom: 0%;

      .phone-iframe {
        transform: scale(0.64);
      }
    }
  }

  .content {
    line-height: 24px;
    padding: 10px;
    border: 1px solid #eee;
    border-radius: 10px;
  }

  .description {
    font-size: 12px;
    tr {
      line-height: 20px;
    }
  }

  //底部
  .footer-container {
    font-family:
      PingFangSC,
      PingFang SC;
    font-weight: 400;
    font-size: 12px;
    color: #909399;
    line-height: 24px;
    text-align: center;
    font-style: normal;
    margin: 10px 10px 0px 10px;
  }
}
</style>
<style lang="scss">
.map-dropdown {
  min-width: 150px;
}

.tdt-map-title {
  position: absolute;
  top: 10px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10 !important;
  color: #000;
  font-size: 18px;
  text-align: center;
  padding: 8px 16px;
  border-radius: 4px;
  pointer-events: none;
}

.tdt-map-subtitle {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

#tdtMapWrapper {
  height: 100% !important;
  overflow: hidden;
}

.map-container {
  height: 100%;
  overflow: hidden;
}

#tdtMap,
#map {
  height: 100%;
  width: 100%;
  overflow: hidden;
}
</style>
