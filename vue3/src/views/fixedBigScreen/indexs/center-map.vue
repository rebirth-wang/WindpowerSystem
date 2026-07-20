<template>
  <div class="centermap">
    <div class="btn-group">
      <el-radio v-model="type" :value="1" style="color: #fff" @change="handleMapType">中国地图</el-radio>
      <el-radio v-model="type" :value="2" style="color: #fff" @change="handleMapType">世界地图</el-radio>
    </div>

    <div style="height: 740px">
      <div
        v-loading="loading"
        element-loading-background="transparent"
        ref="mapRef"
        style="height: 721px; width: 781px; padding: 10px"
      ></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';
import { listAllDeviceShort } from '@/api/iot/device';
import chinaJson from '@/assets/data/json/bigScreen/china.json';
import worldJson from '@/assets/data/json/bigScreen/world.json';

const loading = ref(false);
const type = ref(1);
const deviceList = ref<any[]>([]);
const mapRef = ref<HTMLElement | null>(null);
let myChart: echarts.ECharts | null = null;
let timer: ReturnType<typeof setInterval> | null = null;

onMounted(() => {
  getAllDevice();
});

onBeforeUnmount(() => {
  clearData();
});

function clearData() {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
}

// 轮询
function switper() {
  if (timer) return;
  timer = setInterval(() => {
    getAllDevice();
  }, 120000);
}

// 查询所有设备
function getAllDevice() {
  loading.value = true;
  listAllDeviceShort({}).then((res: any) => {
    if (res.code === 200) {
      deviceList.value = res.rows;
      initMap();
      loadMap();
      switper();
    }
    loading.value = false;
  });
}

// 加载地图
function initMap() {
  if (!mapRef.value) return;
  myChart = echarts.init(mapRef.value);
  echarts.registerMap('china', chinaJson as any);
  echarts.registerMap('world', worldJson as any);
  // 单击事件
  myChart.on('click', (params: any) => {
    const url = params.data.remark;
    if (url) {
      window.open(url, '_blank');
    }
  });
}

// 加载地图
function loadMap() {
  if (!myChart) return;
  const option = {
    title: {},
    tooltip: { show: false },
    geo: {
      map: type.value === 1 ? 'china' : 'world',
      roam: true,
      zoom: 1.2,
      label: {
        emphasis: { show: false },
      },
      itemStyle: {
        areaColor: '#ffffff',
        borderColor: '#a3c5f6',
        borderWidth: 1,
        emphasis: { areaColor: '#ffffff' },
      },
    },
    series: [
      {
        type: 'scatter',
        coordinateSystem: 'geo',
        data: convertData(deviceList.value, 1),
        symbolSize: 12,
        itemStyle: { color: '#E6A23C' },
        tooltip: {
          show: true,
          trigger: 'item',
          backgroundColor: 'rgba(58,73,116,0.7)',
          textStyle: { color: 'rgba(65,235,246,1)' },
          formatter: tipFormat,
        },
      },
      {
        type: 'scatter',
        coordinateSystem: 'geo',
        data: convertData(deviceList.value, 2),
        symbolSize: 12,
        itemStyle: { color: '#F56C6C' },
        tooltip: {
          show: true,
          trigger: 'item',
          backgroundColor: 'rgba(58,73,116,0.7)',
          textStyle: { color: 'rgba(65,235,246,1)' },
          formatter: tipFormat,
        },
      },
      {
        type: 'scatter',
        coordinateSystem: 'geo',
        data: convertData(deviceList.value, 4),
        symbolSize: 12,
        itemStyle: { color: '#909399' },
        tooltip: {
          show: true,
          trigger: 'item',
          backgroundColor: 'rgba(58,73,116,0.7)',
          textStyle: { color: 'rgba(65,235,246,1)' },
          formatter: tipFormat,
        },
      },
      {
        type: 'scatter',
        coordinateSystem: 'geo',
        data: convertData(deviceList.value, 3),
        symbolSize: 12,
        itemStyle: { color: '#67C23A' },
        tooltip: {
          show: true,
          trigger: 'item',
          backgroundColor: 'rgba(58,73,116,0.7)',
          textStyle: { color: 'rgba(65,235,246,1)' },
          formatter: tipFormat,
        },
      },
    ],
  };
  myChart.setOption(option);
}

// 格式化数据
function convertData(data: any[], status: number) {
  const res: any[] = [];
  for (let i = 0; i < data.length; i++) {
    const geoCoord = [data[i].longitude, data[i].latitude];
    if (geoCoord && data[i].status == status) {
      res.push({
        name: data[i].deviceName,
        value: geoCoord,
        serialNumber: data[i].serialNumber,
        status: data[i].status,
        isShadow: data[i].isShadow,
        firmwareVersion: data[i].firmwareVersion,
        networkAddress: data[i].networkAddress,
        productName: data[i].productName,
        activeTime: data[i].activeTime == null ? '' : data[i].activeTime,
        deviceId: data[i].deviceId,
        locationWay: data[i].locationWay,
        remark: data[i].remark,
      });
    }
  }
  return res;
}

// 弹框格式
function tipFormat(params: any) {
  let htmlStr = '<div style="padding:5px;line-height:28px;">';
  htmlStr += "设备名称： <span style='color:#FFF'>" + params.data.name + '</span><br />';
  htmlStr += '设备编号： ' + params.data.serialNumber + '<br />';
  htmlStr += '设备状态： ';
  if (params.data.status == 1) {
    htmlStr += "<span style='color:#E6A23C'>未激活</span>" + '<br />';
  } else if (params.data.status == 2) {
    htmlStr += "<span style='color:#F56C6C'>禁用</span>" + '<br />';
  } else if (params.data.status == 3) {
    htmlStr += "<span style='color:#67C23A'>在线</span>" + '<br />';
  } else if (params.data.status == 4) {
    htmlStr += "<span style='color:#909399'>离线</span>" + '<br />';
  }
  if (params.data.isShadow == 1) {
    htmlStr += '设备影子： ' + "<span style='color:#67C23A'>启用</span>" + '<br />';
  } else {
    htmlStr += '设备影子： ' + "<span style='color:#909399'>未启用</span>" + '<br />';
  }
  htmlStr += '产品名称： ' + params.data.productName + '<br />';
  htmlStr += '固件版本： Version ' + params.data.firmwareVersion + '<br />';
  htmlStr += '激活时间： ' + params.data.activeTime + '<br />';
  htmlStr += '定位方式： ';
  if (params.data.locationWay == 1) {
    htmlStr += '自动定位' + '<br />';
  } else if (params.data.locationWay == 2) {
    htmlStr += '设备定位' + '<br />';
  } else if (params.data.locationWay == 3) {
    htmlStr += '自定义位置' + '<br />';
  } else {
    htmlStr += '未知' + '<br />';
  }
  htmlStr += '所在地址： ' + params.data.networkAddress + '<br />';
  htmlStr += '</div>';
  return htmlStr;
}

// 地图类型切换
function handleMapType() {
  loadMap();
}
</script>

<style lang="scss" scoped>
.centermap {
  position: relative;

  .btn-group {
    position: absolute;
    top: 20px;
    left: 15px;
    z-index: 99999;
  }

  .maptitle {
    height: 60px;
    display: flex;
    justify-content: center;
    padding-top: 10px;
    box-sizing: border-box;

    .titletext {
      font-size: 28px;
      font-weight: 900;
      letter-spacing: 6px;
      background: linear-gradient(92deg, #0072ff 0%, #00eaff 48.8525390625%, #01aaff 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      margin: 0 10px;
    }

    .zuo,
    .you {
      background-size: 100% 100%;
      width: 29px;
      height: 20px;
      margin-top: 8px;
    }

    .zuo {
      background: url('../../../assets/images/bigScreen/xiezuo.png') no-repeat;
    }

    .you {
      background: url('../../../assets/images/bigScreen/xieyou.png') no-repeat;
    }
  }
}
</style>
