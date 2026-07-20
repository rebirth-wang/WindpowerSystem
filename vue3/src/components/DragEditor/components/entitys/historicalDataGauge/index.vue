<template>
  <div
    class="hstorical-data-gauge"
    :style="{
      background: datas.setStyle.backColor,
      marginTop: datas.setStyle.marginTop + 'px',
      marginBottom: datas.setStyle.marginBottom + 'px',
    }"
  >
    <div class="no-data" v-if="datas.setConfig.attributes.length === 0">
      <span>{{ $t('dragEditor.565720-29') }}</span>
    </div>
    <div class="content" v-else>
      <div class="grid-container" :style="{ gridTemplateColumns: `repeat(${datas.setStyle.rowNum}, minmax(0, 1fr))` }">
        <div class="grid-col" v-for="item in datas.setConfig.attributes" :key="item.templateId">
          <div class="panel">
            <div
              class="gauge"
              :style="{ width: datas.setStyle.width + 'px', height: datas.setStyle.width + 'px' }"
              :id="`gaugeChart_${item.templateId}`"
            ></div>
          </div>
          <div
            class="name"
            :style="{ color: datas.setStyle.nameColor, fontSize: `${(datas.setStyle.width - 137) / (93 / 4) + 12}px` }"
          >
            {{ item.templateName }}
          </div>
        </div>
      </div>
    </div>
    <div v-show="false">{{ attributes }} {{ numColor }} {{ loopColor }} {{ gaugeWidth }}</div>
    <!-- 删除组件 -->
    <slot name="deles" />
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, watch } from 'vue';
import * as echarts from 'echarts';
defineOptions({ name: 'historicalDataGauge' });

const props = defineProps<{ datas: any }>();
const myChart = reactive<Record<number, echarts.ECharts>>({});
const timers: number[] = [];

const attributes = computed(() => props.datas.setConfig.attributes);
const numColor = computed(() => props.datas.setStyle.numColor);
const loopColor = computed(() => props.datas.setStyle.loopColor);
const gaugeWidth = computed(() => props.datas.setStyle.width);

function clearGaugeCharts() {
  timers.splice(0).forEach((timer) => window.clearInterval(timer));
  Object.keys(myChart).forEach((key) => {
    myChart[Number(key)]?.dispose();
    delete myChart[Number(key)];
  });
}

function getGaugeChart() {
  clearGaugeCharts();
  const list = props.datas.setConfig.attributes || [];
  if (list.length === 0) return;

  list.forEach((item: any, index: number) => {
    const dom = document.getElementById('gaugeChart_' + item.templateId);
    if (!dom) return;

    myChart[index] = echarts.init(dom);
    const option = {
      series: [
        {
          title: {
            offsetCenter: [0, '115%'],
            fontSize: 16,
          },
          type: 'gauge',
          colorBy: 'data',
          splitNumber: 10,
          radius: '100%',
          axisLine: {
            lineStyle: {
              width: 8,
              color: [
                [0.2, '#409EFF'],
                [0.8, '#12d09f'],
                [1, '#F56C6C'],
              ],
              opacity: 0.3,
            },
          },
          min: item.min,
          max: item.max,
          pointer: {
            icon: 'triangle',
            length: '60%',
            width: 7,
          },
          axisTick: {
            distance: 4,
          },
          splitLine: {
            distance: 4,
          },
          axisLabel: {
            fontSize: 10,
            distance: 10,
          },
          progress: {
            show: true,
            width: 8,
          },
          detail: {
            valueAnimation: true,
            formatter: '{value}' + (item.unit || ''),
            offsetCenter: [0, '80%'],
            fontSize: 20,
          },
          data: [
            {
              value: item.min,
            },
          ],
        },
      ],
    };

    const timer = window.setInterval(() => {
      myChart[index]?.setOption({
        series: [
          {
            data: [
              {
                value: item.min,
              },
            ],
          },
        ],
      });
    }, 2000);

    timers.push(timer);
    myChart[index].setOption(option);
  });
}

function updateNumColor() {
  const list = props.datas.setConfig.attributes || [];
  list.forEach((_item: any, index: number) => {
    const chart = myChart[index];
    if (!chart) return;
    const currentOption: any = chart.getOption();
    currentOption.series[0].detail = { color: props.datas.setStyle.numColor };
    chart.setOption(currentOption);
  });
}

function updateLoopColor() {
  const list = props.datas.setConfig.attributes || [];
  list.forEach((_item: any, index: number) => {
    const chart = myChart[index];
    if (!chart) return;
    const currentOption: any = chart.getOption();
    currentOption.series[0].progress = { itemStyle: { color: props.datas.setStyle.loopColor } };
    currentOption.series[0].pointer = { itemStyle: { color: props.datas.setStyle.loopColor } };
    chart.setOption(currentOption);
  });
}

function updateGaugeWidth() {
  const list = props.datas.setConfig.attributes || [];
  list.forEach((_item: any, index: number) => {
    const chart = myChart[index];
    if (!chart) return;
    chart.resize({
      width: props.datas.setStyle.width,
      height: props.datas.setStyle.width,
    });
    const currentOption: any = chart.getOption();
    currentOption.series[0].detail = { fontSize: (props.datas.setStyle.width - 137) / (93 / 6) + 14 };
    chart.setOption(currentOption);
  });
}

onMounted(() => nextTick(getGaugeChart));
watch(attributes, () => nextTick(getGaugeChart), { deep: true });
watch(numColor, () => nextTick(updateNumColor));
watch(loopColor, () => nextTick(updateLoopColor));
watch(gaugeWidth, () => nextTick(updateGaugeWidth));
onBeforeUnmount(clearGaugeCharts);
</script>

<style lang="scss" scoped>
.hstorical-data-gauge {
  position: relative;

  .no-data {
    padding: 20px;
    margin: 12px 20px;
    border: 2px dotted #ddd;
    border-radius: 5px;
    text-align: center;
    font-size: 14px;
    line-height: 20px;
    color: #999;
  }

  .content {
    .grid-container {
      display: grid;
      grid-template-columns: repeat(1, minmax(0, 1fr));
      grid-gap: 16px;
      margin: 0;
      padding: 16px;

      .grid-col {
        .panel {
          display: flex;
          flex-direction: column;
          align-items: center;

          .gauge {
            height: 230px;
            width: 230px;
          }
        }
        .name {
          margin: 5px 0 0 0;
          height: 20px;
          text-align: center;
          font-size: 16px;
          line-height: 20px;
        }
      }
    }
  }
}
</style>
