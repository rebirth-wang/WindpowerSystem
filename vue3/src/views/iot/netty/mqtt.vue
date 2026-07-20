<template>
  <div class="iot-netty-mqtt">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
        <el-card style="height: 750px">
          <h3 style="font-weight: bold; margin-top: 0px; margin-bottom: 20px">{{ $t('netty.mqtt.564432-0') }}</h3>
          <el-row :gutter="20" class="panel-group">
            <el-col :span="24" class="card-panel-col" style="margin-bottom: 17px">
              <div class="card-panel">
                <div class="card-panel-icon-wrapper icon-orange">
                  <svg-icon icon-class="guide" class-name="card-panel-icon" />
                </div>
                <div class="card-panel-description">
                  <div>
                    <div class="card-panel-text">{{ $t('netty.mqtt.564432-1') }}</div>
                    <span class="card-panel-num">{{ staticData['send_total'] || 0 }}</span>
                  </div>
                </div>
              </div>
            </el-col>
            <el-col :span="24" class="card-panel-col" style="margin-bottom: 18px">
              <div class="card-panel">
                <div class="card-panel-icon-wrapper icon-green">
                  <svg-icon icon-class="receiver" class-name="card-panel-icon" />
                </div>
                <div class="card-panel-description">
                  <div>
                    <div class="card-panel-text">{{ $t('netty.mqtt.564432-2') }}</div>
                    <span class="card-panel-num">{{ staticData['receive_total'] || 0 }}</span>
                  </div>
                </div>
              </div>
            </el-col>
            <el-col :span="24" class="card-panel-col" style="margin-bottom: 17px">
              <div class="card-panel">
                <div class="card-panel-icon-wrapper icon-orange">
                  <svg-icon icon-class="authenticate" class-name="card-panel-icon" />
                </div>
                <div class="card-panel-description">
                  <div class="card-panel-text">{{ $t('netty.mqtt.564432-3') }}</div>
                  <span class="card-panel-num">{{ staticData['auth_total'] || 0 }}</span>
                </div>
              </div>
            </el-col>
            <el-col :span="24" class="card-panel-col" style="margin-bottom: 18px">
              <div class="card-panel">
                <div class="card-panel-icon-wrapper icon-green">
                  <svg-icon icon-class="connect" class-name="card-panel-icon" />
                </div>
                <div class="card-panel-description">
                  <div class="card-panel-text">{{ $t('netty.mqtt.564432-4') }}</div>
                  <span class="card-panel-num">{{ staticData['connect_total'] || 0 }}</span>
                </div>
              </div>
            </el-col>
            <el-col :span="24" class="card-panel-col" style="margin-bottom: 17px">
              <div class="card-panel">
                <div class="card-panel-icon-wrapper icon-orange">
                  <svg-icon icon-class="subscribe" class-name="card-panel-icon" />
                </div>
                <div class="card-panel-description">
                  <div class="card-panel-text">{{ $t('netty.mqtt.564432-5') }}</div>
                  <span class="card-panel-num">{{ staticData['subscribe_total'] || 0 }}</span>
                </div>
              </div>
            </el-col>
            <el-col :span="24" class="card-panel-col" style="margin-bottom: 17px">
              <div class="card-panel">
                <div class="card-panel-icon-wrapper icon-green">
                  <svg-icon icon-class="message" class-name="card-panel-icon" />
                </div>
                <div class="card-panel-description">
                  <div>
                    <div class="card-panel-text">{{ $t('netty.mqtt.564432-6') }}</div>
                    <span class="card-panel-num">{{ staticData['today_received'] || 0 }}</span>
                  </div>
                </div>
              </div>
            </el-col>
            <el-col :span="24" class="card-panel-col" style="margin-bottom: 17px">
              <div class="card-panel">
                <div class="card-panel-icon-wrapper icon-orange">
                  <svg-icon icon-class="log" class-name="card-panel-icon" />
                </div>
                <div class="card-panel-description">
                  <div class="card-panel-text">{{ $t('netty.mqtt.564432-7') }}</div>
                  <span class="card-panel-num">{{ staticData['today_send'] || 0 }}</span>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="24" :lg="16" :xl="16">
        <el-card style="margin-bottom: 19px; height: 370px">
          <div ref="pieTotalRef" style="height: 291px"></div>
        </el-card>
        <el-card style="height: 360px">
          <div ref="statsChartRef" style="height: 320px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue';
import { getNettyMqttStats, statisticNettyMqtt } from '@/api/iot/netty';
import * as echarts from 'echarts';

const { proxy } = getCurrentInstance() as any;

const stats = ref<any>({});
const staticData = ref<any>({});
const pieTotalRef = ref<HTMLElement | null>(null);
const statsChartRef = ref<HTMLElement | null>(null);

function statisticMqtt() {
  statisticNettyMqtt().then((response: any) => {
    staticData.value = response.data;
    totalMqtt();
  });
}

function getMqttStats() {
  getNettyMqttStats().then((response: any) => {
    stats.value = response.data;
    drawStats();
  });
}

function totalMqtt() {
  if (!pieTotalRef.value) return;
  let myChart = echarts.init(pieTotalRef.value);
  myChart.setOption({
    title: {
      text: proxy.$t('netty.mqtt.564432-8'),
      left: 'left',
      textStyle: { fontSize: 18, color: '#000', fontWeight: 800 },
    },
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'right' },
    color: ['#b3f1fb', '#bdcafa'],
    series: [
      {
        name: proxy.$t('netty.mqtt.564432-9'),
        type: 'pie',
        radius: '55%',
        label: { show: true },
        labelLine: { position: 'inner', show: false },
        data: [
          { value: staticData.value['send_total'], name: proxy.$t('netty.mqtt.564432-10') },
          { value: staticData.value['receive_total'], name: proxy.$t('netty.mqtt.564432-11') },
        ],
      },
    ],
  });
}

function drawStats() {
  if (!statsChartRef.value) return;
  let myChart = echarts.init(statsChartRef.value);
  myChart.setOption({
    title: { text: proxy.$t('netty.mqtt.564432-12'), textStyle: { fontSize: 18, color: '#000', fontWeight: 800 } },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: {
      data: [proxy.$t('netty.mqtt.564432-18'), proxy.$t('netty.mqtt.564432-19')],
      right: '15',
      icon: 'rect',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: { color: 'rgba(0,0,0,0.65)' },
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', boundaryGap: [0, 0.01] },
    yAxis: {
      type: 'category',
      axisLabel: { fontSize: 14 },
      data: [
        proxy.$t('netty.mqtt.564432-13'),
        proxy.$t('netty.mqtt.564432-14'),
        proxy.$t('netty.mqtt.564432-15'),
        proxy.$t('netty.mqtt.564432-16'),
        proxy.$t('netty.mqtt.564432-17'),
      ],
    },
    series: [
      {
        name: proxy.$t('netty.mqtt.564432-18'),
        type: 'bar',
        itemStyle: { color: '#0bb9ff' },
        data: [
          stats.value['connection_count'],
          stats.value['session_count'],
          stats.value['subscription_count'],
          stats.value['retain_count'],
          stats.value['retain_count'],
        ],
      },
      {
        name: proxy.$t('netty.mqtt.564432-19'),
        type: 'bar',
        itemStyle: { color: '#4a6ff8' },
        data: [
          stats.value['connection_total'],
          stats.value['session_total'],
          stats.value['subscription_total'],
          stats.value['retain_total'],
          stats.value['retain_total'],
        ],
      },
    ],
  });
}

onMounted(() => {
  getMqttStats();
  statisticMqtt();
});
</script>

<style lang="scss" scoped>
.panel-group {
  .card-panel-col {
    margin-bottom: 15px;
  }
  .card-panel {
    height: 78px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    color: #666;
    border: 1px solid #eee;
    border-radius: 5px;
    background-color: #fff;
    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }
      .icon-green {
        background: #34bfa3;
      }
      .icon-orange {
        background: #e6a23c;
      }
    }
    .icon-green {
      color: #34bfa3;
    }
    .icon-orange {
      color: #e6a23c;
    }
    .card-panel-icon-wrapper {
      float: left;
      margin: 10px;
      padding: 10px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }
    .card-panel-icon {
      float: left;
      font-size: 30px;
    }
    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 15px;
      margin-left: 0;
      .card-panel-text {
        line-height: 14px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 14px;
        margin-bottom: 12px;
        text-align: right;
      }
      .card-panel-num {
        font-size: 18px;
      }
    }
  }
}
.iot-netty-mqtt {
  padding: 20px;
}
</style>
