<template>
  <!-- 预览界面 -->
  <div v-loading="loading" id="topo-render" @mousewheel="mouseWheel" @mousemove="pageMouseMove" @touchstart="pageTouch">
    <div :style="rotateStyle">
      <div
        v-if="
          configData && configData[selected] && configData[selected][pageIndex] && configData[selected][pageIndex].layer
        "
        id="render-box"
        class="render-box"
        ref="imageTofile"
        :style="layerStyle"
        v-loading="loadingContro"
        :element-loading-text="loadingText"
        @contextmenu.prevent="onContextmenu"
        @mousemove="mouseMove"
        @mousedown="mouseDown"
        @mouseup="mouseUp"
        @mouseenter="mouseEnter"
        @mouseleave="mouseLeave"
      >
        <div
          v-for="(component, index) in configData[selected][pageIndex].components"
          :key="index"
          class="render-comp"
          :data-comp-id="component.identifier"
          @click.stop="handleCompThrottleClick(component)"
          @dblclick.stop="handleCompDblClick(component)"
          @touchstart.stop
          :class="{ 'render-comp-clickable': component.action && component.action.length !== 0 }"
          :style="{
            left: component.style.position.x + 'px',
            top: component.style.position.y + 'px',
            width: component.style.position.w + 'px',
            height: component.style.position.h + 'px',
            display: component.style.visible === false || component.__animationVisible === false ? 'none' : 'block',
            backgroundColor:
              component.type == 'flow-bar' || component.type == 'flow-bar-dynamic'
                ? 'transparent'
                : component.style.backColor,
            zIndex: getRenderZIndex(component),
            transform: component.style.transformType,
            opacity: component.__animationDim
              ? (component.style.opacity === undefined || component.style.opacity === ''
                  ? 1
                  : Number(component.style.opacity)) * 0.55
              : component.style.opacity,
            filter: component.__animationDim ? 'brightness(0.65)' : undefined,
            borderRadius: component.style.borderRadius + 'px',
            boxShadow: '0 0 ' + component.style.boxShadowWidth + 'px 0 ' + component.style.boxShadowColor,
          }"
        >
          <component
            ref="spirit"
            v-bind:is="parseView(component)"
            :detail="component"
            :httpPublicSetting="configData[selected][pageIndex].httpPublicSetting"
          />
        </div>
        <div v-if="configData[selected][pageIndex].components.length === 0">
          <el-empty style="padding-top: 80px" :description="$t('topo.topoRender.038944-0')"></el-empty>
        </div>
      </div>
      <div v-else>
        <el-empty style="padding-top: 80px" :description="t('topo.topoRender.038944-25')"></el-empty>
      </div>
    </div>

    <!-- 组态预览小窗口 -->
    <el-dialog
      :title="smallWindName"
      :visible="isSmallWind"
      @update:visible="isSmallWind = $event"
      :close-on-click-modal="false"
      :width="smallWindWidth + 'px'"
    >
      <iframe
        :style="{
          width: smallWindWidth - 40 + 'px',
          height: smallWindHeight + 'px',
          border: '#c0c4cc 0.5px solid',
          borderRadius: '5px',
        }"
        :src="smallWindUrl"
      ></iframe>
    </el-dialog>

    <!-- 放大，缩小，还原按钮 -->
    <div class="topo-zoom" id="topo-zoom">
      <div class="topo-zoom-btn" id="topo-zoom-btn">
        <div
          type="button"
          :title="
            configData &&
            configData[selected] &&
            configData[selected][pageIndex] &&
            configData[selected][pageIndex].layer &&
            !configData[selected][pageIndex].layer.dragZoom &&
            !isMobile
              ? ''
              : t('topo.topoRender.038944-26')
          "
          class="amplify"
          id="amplify"
          @mouseenter="mouseEnter"
          @mouseleave="mouseLeave"
          @click="handleAmplify"
        >
          <i class="el-icon-zoom-in icon"></i>
        </div>
        <div
          type="button"
          :title="
            configData &&
            configData[selected] &&
            configData[selected][pageIndex] &&
            configData[selected][pageIndex].layer &&
            !configData[selected][pageIndex].layer.dragZoom &&
            !isMobile
              ? ''
              : t('topo.topoRender.038944-27')
          "
          class="reduction"
          id="reduction"
          @mouseenter="mouseEnter"
          @mouseleave="mouseLeave"
          @click="handleReduction"
        >
          <i class="el-icon-zoom-out icon"></i>
        </div>
        <div
          type="button"
          :title="
            configData &&
            configData[selected] &&
            configData[selected][pageIndex] &&
            configData[selected][pageIndex].layer &&
            !configData[selected][pageIndex].layer.dragZoom &&
            !isMobile
              ? ''
              : t('topo.topoRender.038944-28')
          "
          class="reset"
          id="reset"
          @mouseenter="mouseEnter"
          @mouseleave="mouseLeave"
          @click="handleReset"
        >
          <i class="el-icon-full-screen icon"></i>
        </div>
        <!-- 移动端旋转按钮 -->
        <div
          v-if="isMobile"
          type="button"
          :title="
            configData &&
            configData[selected] &&
            configData[selected][pageIndex] &&
            configData[selected][pageIndex].layer &&
            !configData[selected][pageIndex].layer.dragZoom &&
            !isMobile
              ? ''
              : t('topo.topoRender.038944-29')
          "
          class="rotate"
          id="rotate"
          @mouseenter="mouseEnter"
          @mouseleave="mouseLeave"
          @click="handleRotate"
        >
          <i class="el-icon-refresh-left icon"></i>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, getCurrentInstance } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useTopoEditorStore } from '@/stores/modules/topoEditor';
import html2canvas from 'html2canvas';
import { useI18n } from 'vue-i18n';

import { getByGuid, getDeviceStatus, verifyUserPassword } from '@/api/scada/topo';
import { serviceInvoke } from '@/api/iot/runstatus';
import { parseViewName, getScadaRouteType, getRouteQueryString, getRouteQueryNumber } from '@/utils/topo/topoUtil';
import { getUserId } from '@/utils/auth';

// 导入 TopoBase 的内容
import {
  ViewPanel,
  ViewText,
  ViewVarCalc,
  ViewNumPanel,
  ViewImageSwitch,
  ViewBtn,
  ViewFlowBarDynamic,
  ViewVideo,
  ViewVideoMp4,
  ViewVideoPlay,
  ViewInlineVideo,
  ViewTimer,
  ViewMap,
  ViewWeather,
  ViewWarn,
  ViewOrder,
  View3DModel,
  ViewVR,
  ViewTimeTable,
  ViewFlowBar,
  ViewHistory,
  ViewSelect,
  ViewTriangle,
  ViewRect,
  ViewCircular,
  ViewLine,
  ViewLineSegment,
  ViewBizierCurve,
  ViewLineWave,
  ViewChartWater,
  ViewChart,
  ViewChartPie,
  ViewChartGauge,
  ViewChartTemp,
  ViewChartMap,
  ViewChartWrapper,
  ViewImage,
  ViewUsrTable,
  ViewRealData,
  ViewComponent,
  ViewKnobSwitch,
  ViewLuckDraw,
  ViewSvgImage,
  ViewSvgStatic,
  ViewThreeJs,
  ViewReliefBall,
} from './topoBase';

// 定义 props
interface Props {
  defaultValue?: number;
  isShare?: boolean;
  fullScreemTip?: boolean;
  isContextmenu?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  defaultValue: 100,
  isShare: false,
  fullScreemTip: false,
  isContextmenu: true,
});

// 创建 store 实例
const topoEditorStore = useTopoEditorStore();
const route = useRoute();

// 使用 i18n
const { t } = useI18n();

// 响应式数据
const baseApi = ref(import.meta.env.VITE_APP_BASE_API || '');
const loading = ref(false);
const scadaGuid = ref(''); // guid
const configData = ref({}); // 组件数据
const bindDeviceList = ref([]); // 被绑定设备
const selectRadio = ref(1);
const width = ref(0);
const height = ref(0);
const ishow = ref(true);
const keyVertical = ref(0);
const loadingContro = ref(false);
const loadingText = ref(''); // 将在onMounted中设置
const bindList = ref([]);
// 组态小窗口
const isSmallWind = ref(false);
const smallWindUrl = ref('');
const smallWindWidth = ref(960);
const smallWindHeight = ref(600);
const smallWindName = ref(''); // 将在onMounted中设置
const newZoom = ref(0.2);
const displacement = ref({
  scale: 1,
  moveable: false,
  pageX: 0,
  pageY: 0,
  pageX2: 0,
  pageY2: 0,
  originScale: 1,
});
const password = ref('');
const mouseOperate = ref(''); // 鼠标操作
const x = ref(0); // 当前鼠标x
const y = ref(0); // 当前鼠标y
const l = ref(0); // 当前鼠标l
const tVal = ref(0); // 当前鼠标t
const mouseTimeer = ref<any>(null); //鼠标计时器
const isMobile = ref(window.innerWidth <= 768);
const screenWidth = ref(window.innerWidth);
const rotate = ref(0); // 移动端旋转
const pageIndex = ref(0); // 当前分组

// Refs
const imageTofile = ref();

// 计算属性
const selected = computed(() => {
  const forcePcRaw = route.query.forcePc;
  const forcePcVal = Array.isArray(forcePcRaw) ? forcePcRaw[0] : forcePcRaw;
  if (forcePcVal !== undefined && forcePcVal !== null && forcePcVal !== '') {
    let parentWidth = 0;
    try {
      parentWidth = window.parent && window.parent !== window ? window.parent.innerWidth : window.innerWidth;
    } catch (e) {
      parentWidth = window.innerWidth;
    }
    if (parentWidth > 768) {
      return 'pcConfig';
    }
  }
  if (!isMobile.value && configData.value?.pcChecked === true) {
    return 'pcConfig';
  }
  if (!isMobile.value && configData.value?.pcChecked === false) {
    return 'mdConfig';
  }
  if (isMobile.value && configData.value?.mdChecked === true) {
    return 'mdConfig';
  }
  if (isMobile.value && configData.value?.mdChecked === false) {
    return 'pcConfig';
  }
  return 'pcConfig';
});

const layerStyle = computed(() => {
  var styles = [];
  // 组件在能拖拽时缩放
  const dragZoom = configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom;
  if (dragZoom) {
    styles.push(`transform: scale(${selectRadio.value})`);
  }
  styles.push(`transform-origin: 0 0`);
  if (selected.value === 'mdConfig' && !configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom) {
    styles.push('margin: 0 auto; right:0');
  }
  if (configData.value?.[selected.value]?.[pageIndex.value]?.layer?.backColor) {
    styles.push(`background-color: ${configData.value?.[selected.value]?.[pageIndex.value].layer.backColor}`);
  }
  if (configData.value?.[selected.value]?.[pageIndex.value]?.layer?.backgroundImage) {
    styles.push(
      `background-image: url("${baseApi.value}${configData.value?.[selected.value]?.[pageIndex.value].layer.backgroundImage}")`
    );
  }
  if (configData.value?.[selected.value]?.[pageIndex.value]?.layer?.width > 0) {
    styles.push(`width: ${configData.value?.[selected.value]?.[pageIndex.value].layer.width}px`);
  }
  if (configData.value?.[selected.value]?.[pageIndex.value]?.layer?.height > 0) {
    styles.push(`height: ${configData.value?.[selected.value]?.[pageIndex.value].layer.height}px`);
  }
  styles.push('overflow:hidden');
  var style = styles.join(';');
  return style;
});

const rotateStyle = computed(() => {
  var styles = [];
  styles.push(`transform: rotate(${rotate.value}deg)`);
  styles.push(`transform-origin: 0 0`);
  if (
    configData.value?.[selected.value]?.[pageIndex.value]?.layer?.height > 0 &&
    configData.value?.[selected.value]?.[pageIndex.value]?.layer?.width &&
    rotate.value == -90
  ) {
    styles.push(
      `translate: 0px ${configData.value?.[selected.value]?.[pageIndex.value].layer.width * selectRadio.value}px`
    );
  }
  if (
    configData.value?.[selected.value]?.[pageIndex.value]?.layer?.height > 0 &&
    configData.value?.[selected.value]?.[pageIndex.value]?.layer?.width &&
    rotate.value == -180
  ) {
    styles.push(
      `translate: ${configData.value?.[selected.value]?.[pageIndex.value].layer.width * selectRadio.value}px ${configData.value?.[selected.value]?.[pageIndex.value].layer.height * selectRadio.value}px`
    );
  }
  if (
    configData.value?.[selected.value]?.[pageIndex.value]?.layer?.height > 0 &&
    configData.value?.[selected.value]?.[pageIndex.value]?.layer?.width &&
    rotate.value == -270
  ) {
    styles.push(
      `translate: ${configData.value?.[selected.value]?.[pageIndex.value].layer.height * selectRadio.value}px 0px`
    );
  }
  var style = styles.join(';');
  return style;
});

// 方法
// 从 store 映射 mutations
const setMqttData = (data: any) => topoEditorStore.setMqttData(data);
const setDeviceStatus = (data: any) => topoEditorStore.setDeviceStatus(data);

// 从全局属性获取必要的对象
const instance = getCurrentInstance();
const proxy = instance?.proxy as any;
const globalProperties = instance?.appContext.config.globalProperties;
const $mqttTool: any = globalProperties?.$mqttTool;
const $modal: any = globalProperties?.$modal;
const $notify: any = globalProperties?.$notify;
const $contextmenu: any = globalProperties?.$contextmenu;
const $router: any = globalProperties?.$router;

/** 单击脚本里的 app，兼容 Vue2 组件实例上的 $prompt / $message */
function createScriptApp() {
  const base = proxy ?? {};
  return {
    ...base,
    $t: base.$t ?? ((key: string) => t(key)),
    $prompt: (content: string, title?: string | Record<string, any>, options?: Record<string, any>) => {
      if (title != null && typeof title === 'object') {
        return $modal.prompt(content, undefined, title);
      }
      return $modal.prompt(content, typeof title === 'string' ? title : undefined, options);
    },
    $message: (msg: string | { type?: string; message?: string }) => {
      if (typeof msg === 'string') {
        $modal.msg(msg);
        return;
      }
      const message = msg?.message ?? '';
      const type = msg?.type;
      if (type === 'success') $modal.msgSuccess(message);
      else if (type === 'warning') $modal.msgWarning(message);
      else if (type === 'error') $modal.msgError(message);
      else $modal.msg(message);
    },
  };
}

// 连接mqtt消息服务器
async function connectMqtt() {
  if ($mqttTool.client == null) {
    await $mqttTool.connect();
  }
  // 删除所有message事件监听器
  if (window.location.pathname === '/scada/topo/fullscreen') {
    $mqttTool.client.removeAllListeners('message');
  }
  // 添加message事件监听器
  mqttCallback();
}

// mqtt回调处理
function mqttCallback() {
  $mqttTool.client.on('message', (topic: string, message: any, buffer: any) => {
    let topics = topic.split('/');
    let productId = topics[1];
    let deviceNum = topics[2];
    message = JSON.parse(message.toString());
    if (!message) {
      return;
    }
    if (topics[3] == 'status') {
      const data = {
        serialNumber: deviceNum,
        status: message.status,
      };
      setDeviceStatus(data);
      console.log('接收到【设备状态】主题：', topic);
      console.log('接收到【设备状态】内容：', message);
    }
    if (topics[4] == 'reply') {
      $modal.notifySuccess(message);
    }
    if (topic.endsWith('ws/service')) {
      message = message.message;
      const data = {
        productId: productId,
        serialNumber: deviceNum,
        message: message,
      };
      setMqttData(data);
      console.log('接收到【物模型】主题：', topic);
      console.log('接收到【物模型】内容：', message);
    }
    if (topic.endsWith('scene/report')) {
      const data = {
        sceneModelId: productId,
        sceneModelDeviceId: deviceNum,
        message: message,
      };
      setMqttData(data);
      console.log('接收到【场景】主题：', topic);
      console.log('接收到【场景】内容：', message);
    }
  });
}

// mqtt订阅主题
function mqttSubscribe(list: any[]) {
  const topics = getSubscribeTopic(list);
  $mqttTool.subscribe(topics);
}

// mqtt取消订阅主题
function mqttUnSubscribe(list: any[]) {
  const topics = getSubscribeTopic(list);
  $mqttTool.unsubscribe(topics);
}

// 获取订阅主题
function getSubscribeTopic(list: any[]) {
  const type = getScadaRouteType(route.query);
  let topics: string[] = [];
  if (list && list.length !== 0) {
    list.forEach((item) => {
      if (type === 2 && (item.variableType === 2 || item.variableType === 3)) {
        const topicScene = '/' + item.sceneModelId + '/' + item.sceneModelDeviceId + '/scene/report';
        topics.push(topicScene);
      } else {
        const topicStatus = '/' + item.productId + '/' + item.serialNumber + '/status/post';
        const topicMonitor = '/' + item.productId + '/' + item.serialNumber + '/monitor/post';
        const topicService = '/' + item.productId + '/' + item.serialNumber + '/ws/service';
        topics.push(topicStatus);
        topics.push(topicMonitor);
        topics.push(topicService);
      }
    });
  }
  return topics;
}

// 获取组态数据
function getZtData() {
  loading.value = true;
  const scadaType = getScadaRouteType(route.query);
  pageIndex.value = getRouteQueryNumber(route.query, 'pageIndex', 0);
  const pageIndexName = getRouteQueryString(route.query, 'pageIndexName');
  const serialNumber = getRouteQueryString(route.query, 'serialNumber');
  let params: any = { guid: scadaGuid.value, pageName: pageIndexName || '', initializedData: true };
  if (scadaType === 1) {
    params = { ...params, serialNumber: serialNumber };
    // serialNumber 为空代表从产品进来无需获取数据
    if (!serialNumber) {
      params.initializedData = false;
    }
  }
  getByGuid(params).then((res: any) => {
    const { code, data } = res;
    if (code === 200) {
      let configDataLocal = JSON.parse(data.scadaData) || {};
      if (data.scadaData) {
        if (configDataLocal?.pcConfig && configDataLocal?.mdConfig) {
          configData.value = configDataLocal;
        } else {
          const dataconfig = {
            pcConfig: [{ ...configDataLocal }],
            mdConfig: [
              {
                name: '--',
                layer: {
                  backColor: '',
                  backgroundImage: '',
                  widthHeightRatio: '',
                  width: 1600,
                  height: 900,
                },
                components: [],
              },
            ],
            pcChecked: true,
            mdChecked: false,
          };
          configData.value = dataconfig;
        }
      } else {
        configData.value = {};
      }
      if (window.location.pathname === '/scada/topo/fullscreen') {
        document.title = data.pageName; // 修改title值
      }
      if (Object.keys(configData.value).length !== 0) {
        if (data.bindDeviceList && data.bindDeviceList.length !== 0) {
          let list: any[] = [];
          if (scadaType === 1) {
            list = data.bindDeviceList.map((item: any) => ({
              ...item,
              serialNumber: getRouteQueryString(route.query, 'serialNumber'),
            }));
          } else {
            list = data.bindDeviceList;
          }
          mqttSubscribe(list); // 订阅mqtt主题
          bindDeviceList.value = list;
        }
        // 双指缩放
        if (window.screen.width < 1366) {
          rotate.value = 0;
          // 获取放大或缩小的区域DOM
          document.body.addEventListener('touchstart', function (event: TouchEvent) {
            var touches = event.touches;
            var events = touches[0];
            var events2 = touches[1];
            // 第一个触摸点的坐标
            displacement.value.pageX = events.pageX;
            displacement.value.pageY = events.pageY;
            displacement.value.moveable = true;
            if (events2) {
              displacement.value.pageX2 = events2.pageX;
              displacement.value.pageY2 = events2.pageY;
            }
            displacement.value.originScale = displacement.value.scale || 1;
          });
          document.addEventListener('touchmove', function (event: TouchEvent) {
            if (!displacement.value.moveable) {
              return;
            }
            event.preventDefault();
            var touches = event.touches;
            var events = touches[0];
            var events2 = touches[1];
            // 双指移动
            if (events2) {
              // 第2个指头坐标在touchmove时候获取
              if (!displacement.value.pageX2) {
                displacement.value.pageX2 = events2.pageX;
              }
              if (!displacement.value.pageY2) {
                displacement.value.pageY2 = events2.pageY;
              }
              // 双指缩放比例计算
              var zoom =
                getDistance(
                  {
                    x: events.pageX,
                    y: events.pageY,
                  },
                  {
                    x: events2.pageX,
                    y: events2.pageY,
                  }
                ) /
                getDistance(
                  {
                    x: displacement.value.pageX,
                    y: displacement.value.pageY,
                  },
                  {
                    x: displacement.value.pageX2,
                    y: displacement.value.pageY2,
                  }
                );
              // 图像应用缩放效果,使用定时器实现限流防抖
              setTimeout(() => {
                // console.log("zoom",zoom);
                if (zoom > 1) {
                  newZoom.value = newZoom.value + 0.01;
                  if (newZoom.value > 0.8) {
                    if (configData.value?.[selected.value]?.[pageIndex.value].layer.dragZoom) {
                      newZoom.value = 1;
                    }
                  }
                } else {
                  newZoom.value = newZoom.value - 0.01;
                  if (newZoom.value < 0.2) {
                    newZoom.value = 0.2;
                  }
                }
                if (is_andriod_ios().ios) {
                  document.body.style.transform = 'scale(' + newZoom.value + ')';
                } else {
                  document.body.style.zoom = newZoom.value + ''; // Convert number to string
                }
              }, 100);
            }
          });
        } else {
          //缩放
          if (sessionStorage.getItem('selectedValue-' + scadaGuid.value) != 'undefined') {
            rotate.value = Number(sessionStorage.getItem('rotate-' + scadaGuid.value));
          } else if (props.defaultValue) {
            rotate.value = 0;
          }
          setTimeout(() => {
            initLeftTop();
          }, 200);
        }
      }
      if (
        configData.value?.[selected.value]?.[pageIndex.value]?.layer &&
        !configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom
      ) {
        nextTick(() => {
          reset();
        });
      }
    }

    // 判断是否开启dragZoom, 未开启就保持分辨率不变
    const layer = configData.value?.[selected.value]?.[pageIndex.value]?.layer;
    if (layer && layer.dragZoom) {
      selectRadio.value = selected.value === 'pcConfig' ? screenWidth.value / layer.width : 1;
    } else {
      selectRadio.value = 1;
    }
    loading.value = false;
  });
}

// 鼠标离开
function mouseLeave(e: MouseEvent) {
  mouseOperate.value = 'default';
  //toolTip.style.opacity = '0';
}

// 鼠标进来
function mouseEnter(e: MouseEvent) {
  mouseOperate.value = 'default';
  if (e.target && (e.target as HTMLElement).id === 'render-box') {
    let idDoc = document.getElementById('render-box');
    if (idDoc && !configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom) {
      idDoc.style.cursor = 'default';
    } else if (idDoc) {
      idDoc.style.cursor = 'move';
    }
  }
}

// 鼠标被按下
function mouseDown(e: MouseEvent) {
  e.stopPropagation();
  e.preventDefault();
  if (e.target && (e.target as HTMLElement).id === 'render-box') {
    let idDown = document.getElementById('render-box');
    if (idDown && !configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom) {
      idDown.style.cursor = 'pointer';
    } else if (idDown) {
      idDown.style.cursor = 'move';
    }
    x.value = e.clientX;
    y.value = e.clientY;
    if (idDown) {
      l.value = idDown.offsetLeft;
      tVal.value = idDown.offsetTop;
    }
    mouseOperate.value = 'render-box';
  }
}

// 可以拖拽状态下鼠标移动
function mouseMove(e: MouseEvent) {
  e.preventDefault();
  // 使用可选链判断 layer 和 dragZoom
  if (!configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom) {
    return;
  }
  let point = document.getElementById('render-box');
  if (mouseOperate.value == 'render-box' && point) {
    let nx = e.clientX;
    let ny = e.clientY;

    let nl = nx - (x.value - l.value);
    let nt = ny - (y.value - tVal.value);

    point.style.left = nl + 'px';
    point.style.top = nt + 'px';
    sessionStorage.setItem('boxLeft-' + scadaGuid.value, point?.style?.left || '');
    sessionStorage.setItem('boxTop-' + scadaGuid.value, point?.style?.top || '');
    // e.stopImmediatePropagation(); // 在组合式API中可能不需要
  }
}

// 页面鼠标移动,出现按钮
function pageMouseMove(e: MouseEvent) {
  if (isMobile.value) return;
  nextTick(() => {
    if (!configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom && !isMobile.value) {
      return;
    }
    clearTimeout(mouseTimeer.value);
    let toopButton = document.getElementById('topo-zoom');
    if (toopButton) {
      toopButton.style.opacity = '0.3';
    }
    mouseTimeer.value = setTimeout(() => {
      if (toopButton) {
        toopButton.style.opacity = '0';
      }
    }, 3000);
  });
}

function pageTouch(e: TouchEvent) {
  if (!isMobile.value) return;
  nextTick(() => {
    if (!configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom && !isMobile.value) {
      return;
    }
    clearTimeout(mouseTimeer.value);
    let toopButton = document.getElementById('topo-zoom');
    if (toopButton) {
      toopButton.style.opacity = '0.3';
    }
    mouseTimeer.value = setTimeout(() => {
      if (toopButton) {
        toopButton.style.opacity = '0';
      }
    }, 3000);
  });
}

// 鼠标被抬起
function mouseUp(e: MouseEvent) {
  mouseOperate.value = 'default';
  if (e.target && (e.target as HTMLElement).id === 'render-box') {
    let idDoc = document.getElementById('render-box');
    if (idDoc && !configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom) {
      idDoc.style.cursor = 'default';
    } else if (idDoc) {
      idDoc.style.cursor = 'move';
    }
  }
}

function mouseWheel(e: WheelEvent) {
  // 滚动事件
  if (!configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom) {
    return;
  }

  if ((e as any).wheelDelta >= 120) {
    // TypeScript中wheelDelta可能不存在
    selectRadio.value = selectRadio.value + 0.01;
  } else if ((e as any).wheelDelta <= -120) {
    if (selectRadio.value > 0.3) {
      selectRadio.value = selectRadio.value - 0.01;
    }
  }

  saveSf();
}

//点击放大
function handleAmplify() {
  // 使用可选链判断
  if (!configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom && !isMobile.value) {
    return;
  }
  selectRadio.value = selectRadio.value + 0.01;
  saveSf();
}

//点击缩小
function handleReduction() {
  if (!configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom && !isMobile.value) {
    return;
  }

  selectRadio.value = selectRadio.value - 0.01;
  saveSf();
}

//点击还原
function reset() {
  const layer = configData.value?.[selected.value]?.[pageIndex.value]?.layer;
  const layerWidth = layer?.width;
  if (layer && layer.dragZoom) {
    selectRadio.value = selected.value === 'pcConfig' && layerWidth ? screenWidth.value / layerWidth : 1;
  } else {
    selectRadio.value = 1;
  }
  rotate.value = 0;
  let point = document.getElementById('render-box');
  if (point && point.style) {
    point.style.left = 0 + 'px';
    point.style.top = 0 + 'px';
    sessionStorage.setItem('boxLeft-' + scadaGuid.value, point.style.left);
    sessionStorage.setItem('boxTop-' + scadaGuid.value, point.style.top);
    saveSf();
  }
}

function handleReset() {
  if (!configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom && !isMobile.value) {
    return;
  }
  reset();
}

function handleRotate() {
  if (!configData.value?.[selected.value]?.[pageIndex.value]?.layer?.dragZoom && !isMobile.value) {
    return;
  }
  rotate.value = rotate.value - 90 === -360 ? 0 : rotate.value - 90;
  const layer = configData.value?.[selected.value]?.[pageIndex.value]?.layer;
  const layerWidth = layer?.width;
  if (layer && layer.dragZoom) {
    switch (rotate.value) {
      case 0:
        selectRadio.value =
          selected.value === 'pcConfig' && layerWidth ? screenWidth.value / layerWidth : selectRadio.value;
        break;
      case -90:
        selectRadio.value =
          selected.value === 'pcConfig' && layerWidth ? window.innerHeight / layerWidth : selectRadio.value;
        break;
      case -180:
        selectRadio.value =
          selected.value === 'pcConfig' && layerWidth ? screenWidth.value / layerWidth : selectRadio.value;
        break;
      case -270:
        selectRadio.value =
          selected.value === 'pcConfig' && layerWidth ? window.innerHeight / layerWidth : selectRadio.value;
        break;
    }
  } else {
    selectRadio.value = 1;
  }
  saveSf();
}

// 初始化左上角样式
function initLeftTop() {
  let renderApp = document.getElementById('topo-render');
  // if (!this.configData.layer.dragZoom) {
  //     renderApp.style.overflow = 'auto';
  //     return;
  // }
  // renderApp.style.overflow = 'hidden';
  if (sessionStorage.getItem('boxLeft-' + scadaGuid.value) != 'undefined') {
    let point = document.getElementById('render-box');
    if (point && point.style) {
      if (sessionStorage.getItem('boxLeft-' + scadaGuid.value)) {
        point.style.left = sessionStorage.getItem('boxLeft-' + scadaGuid.value) || '';
        point.style.top = sessionStorage.getItem('boxTop-' + scadaGuid.value) || '';
      }
      if (!sessionStorage.getItem('boxLeft-' + scadaGuid.value) && selected.value === 'mdConfig') {
        point.style.left = `calc(50% - ${configData.value?.[selected.value]?.[pageIndex.value]?.layer?.width / 2}px)`;
        point.style.top = '0';
      }
    }
  }
}

// 鼠标右键菜单响应
function onContextmenu(event: Event) {
  if (props.isContextmenu) {
    $contextmenu({
      items: [
        {
          label: t('topo.topoRender.038944-5'),
          divided: true,
          icon: 'el-icon-download',
          onClick: () => {
            generateImage();
          },
        },
        {
          label: t('topo.topoRender.038944-6'),
          icon: 'el-icon-full-screen',
          onClick: () => {
            clickFullscreen();
          },
        },
        {
          label: t('scada.topoMain.320129-7'),
          icon: 'el-icon-refresh',
          onClick: () => {
            $router.go(0);
          },
        },
      ],
      x: (event as MouseEvent).clientX,
      y: (event as MouseEvent).clientY,
      customClass: 'scada-render-context-menu', // 自定义菜单 class
      zIndex: 9999, // 菜单样式 z-index
      minWidth: 160, // 主菜单最小宽度
    });
  }
  return true;
}

// 生成图片
function generateImage() {
  $modal.loading(t('topo.topoRender.038944-7'));
  // 手动创建一个 canvas 标签
  const canvas = document.createElement('canvas');
  // 获取父标签，意思是这个标签内的 DOM 元素生成图片
  let canvasBox = imageTofile.value;
  // 获取父级的宽高
  const width = parseInt(window.getComputedStyle(canvasBox).width);
  const height = parseInt(window.getComputedStyle(canvasBox).height);
  // 宽高 * 2 并放大 2 倍 是为了防止图片模糊
  canvas.width = width * 2;
  canvas.height = height * 2;
  canvas.style.width = width + 'px';
  canvas.style.height = height + 'px';
  const options = {
    backgroundColor: null,
    canvas: canvas,
    useCORS: true,
  };
  html2canvas(canvasBox, options).then((canvas: HTMLCanvasElement) => {
    const dataURL = canvas.toDataURL('image/png');
    downloadImage(dataURL);
    $modal.closeLoading();
  });
}

//下载图片
function downloadImage(url: string) {
  let a = document.createElement('a');
  a.href = url;
  a.download = document.title;
  a.click();
}

// 全屏展示
function clickFullscreen() {
  let element = document.getElementById('app'); // 指定全屏区域元素
  console.log(element);
  if (element && element.requestFullscreen) {
    element.requestFullscreen();
  } else if (element && (element as any).webkitRequestFullScreen) {
    (element as any).webkitRequestFullScreen();
  } else if (element && (element as any).mozRequestFullScreen) {
    (element as any).mozRequestFullScreen();
  } else if (element && (element as any).msRequestFullscreen) {
    (element as any).msRequestFullscreen(); // IE11
  }
}

//保存缩放
function saveSf() {
  sessionStorage.setItem('rotate-' + getRouteQueryString(route.query, 'guid'), String(rotate.value));
}

const viewMap: Record<string, any> = {
  'view-panel': ViewPanel,
  'view-text': ViewText,
  'view-var-calc': ViewVarCalc,
  'view-num-panel': ViewNumPanel,
  'view-image-switch': ViewImageSwitch,
  'view-btn': ViewBtn,
  'view-flow-bar-dynamic': ViewFlowBarDynamic,
  'view-video': ViewVideo,
  'view-video-mp4': ViewVideoMp4,
  'view-video-play': ViewVideoPlay,
  'view-inline-video': ViewInlineVideo,
  'view-timer': ViewTimer,
  'view-map': ViewMap,
  'view-weather': ViewWeather,
  'view-warn': ViewWarn,
  'view-order': ViewOrder,
  'view-3d-model': View3DModel,
  'view-vr': ViewVR,
  'view-time-table': ViewTimeTable,
  'view-flow-bar': ViewFlowBar,
  'view-history': ViewHistory,
  'view-select': ViewSelect,
  'view-triangle': ViewTriangle,
  'view-rect': ViewRect,
  'view-circular': ViewCircular,
  'view-line': ViewLine,
  'view-line-segment': ViewLineSegment,
  'view-bizier-curve': ViewBizierCurve,
  'view-line-wave': ViewLineWave,
  'view-chart-water': ViewChartWater,
  'view-chart': ViewChart,
  'view-chart-pie': ViewChartPie,
  'view-chart-gauge': ViewChartGauge,
  'view-chart-temp': ViewChartTemp,
  'view-chart-map': ViewChartMap,
  'view-chart-wrapper': ViewChartWrapper,
  'view-image': ViewImage,
  'view-usr-table': ViewUsrTable,
  'view-real-data': ViewRealData,
  'view-component': ViewComponent,
  'view-knob-switch': ViewKnobSwitch,
  'view-luck-draw': ViewLuckDraw,
  'view-svg-image': ViewSvgImage,
  'view-svg-static': ViewSvgStatic,
  'view-three-js': ViewThreeJs,
  'view-relief-ball': ViewReliefBall,
};

function parseView(component: any) {
  return viewMap[parseViewName(component)] || ViewText;
}

function getRenderZIndex(component: any) {
  const zIndex = Number(component?.style?.zIndex);
  if ((component?.type === 'VR' || component?.type === '3D-model') && zIndex < 0) {
    return 0;
  }
  return Number.isFinite(zIndex) ? zIndex : component?.style?.zIndex;
}

// 组件单击点击节流
async function handleCompThrottleClick(component: any) {
  setTimeout(async () => {
    const { dataEvent = {}, dataBind } = component || {};
    if (!dataEvent.passwordEnabled) {
      return doClickComponent(component);
    }
    const isActionVar = dataEvent.djType === 1;
    if (!isActionVar) {
      return processPasswordValidation(component);
    }
    let serialNumber = dataEvent.serialNumber || (dataBind && dataBind.serialNumber);
    if (!serialNumber) {
      return processPasswordValidation(component);
    }
    try {
      const isOnline = await getDeviceStatusPromise(serialNumber);
      if (!isOnline) {
        $modal.msgWarning(t('topo.topoRender.038944-24'));
        return;
      }
    } catch (err) {
      console.error('获取设备状态失败', err);
    }
    processPasswordValidation(component);
  }, 300);
}

// 处理密码验证逻辑
function processPasswordValidation(component: any) {
  // 用户密码类型
  if (component.dataEvent.passwordType === 'user') {
    $modal
      .prompt(t('topo.topoRender.038944-8'), t('systemPrompt'), {
        confirmButtonText: t('confirm'),
        cancelButtonText: t('cancel'),
        inputType: 'password',
        inputValidator: (value: string) => {
          if (!value) {
            return t('topo.topoRender.038944-9');
          }
          password.value = value;
          return true;
        },
      })
      .then(() => {
        inputpassword(component);
      })
      .catch(() => {});
  }
  // 自定义密码类型
  else if (component.dataEvent.passwordType === 'custom') {
    $modal
      .prompt(t('topo.topoRender.038944-8'), t('systemPrompt'), {
        confirmButtonText: t('confirm'),
        cancelButtonText: t('cancel'),
        inputType: 'password',
        inputValidator: (value: string) => {
          if (!value) {
            return t('topo.topoRender.038944-9');
          }
          if (value !== component.dataEvent.customPassword) {
            return t('topo.topoRender.038944-10');
          }
          return true;
        },
      })
      .then(() => {
        doClickComponent(component);
      })
      .catch(() => {
        // 用户取消输入
      });
  }
}

// 验证用户密码输入
function inputpassword(component: any) {
  const params = { password: password.value, userId: getUserId() };
  verifyUserPassword(params).then((res: any) => {
    if (res.code == 200) {
      $modal.msgSuccess(res.msg);
      setTimeout(() => {
        doClickComponent(component);
      }, 500);
    } else {
      $modal.msgError(res.msg);
    }
  });
}

// 点击事件
function doClickComponent(component: any) {
  const { componentShow, dataEvent = {}, dataAction = {} } = component;
  const {
    djAction,
    djType,
    modelName,
    modelValue,
    modelTip,
    sceneModelId,
    writeType,
    funValue,
    externalLink,
    openModel,
    scadaName,
    scadaGuid: compScadaGuid,
    scadaIndexName,
    scadaIndex,
    windowWidth,
    windowHeight,
    switchControl,
  } = dataEvent;
  const { switchValue } = dataAction;
  if (componentShow && componentShow.indexOf('单击') !== -1 && djAction) {
    if (djType === 1) {
      if (writeType === 1) {
        if (modelValue) {
          controChange(dataEvent);
        } else {
          $modal.msgWarning(t('topo.topoRender.038944-23'));
        }
      } else if (writeType === 3) {
        const funStr = 'function (app, issord) {\n' + funValue + '\n' + '}';
        const fun: any = eval('(' + funStr + ')');
        const issueOrder = (val: any) => {
          const tempDataEvent = { ...dataEvent, modelValue: val };
          controChange(tempDataEvent);
        };
        const value = fun(createScriptApp(), issueOrder);
        if (value) {
          const tempDataEvent = { ...dataEvent, modelValue: value };
          controChange(tempDataEvent);
        }
      } else {
        let tipMsg = t('topo.topoRender.038944-16');
        if (modelTip) {
          tipMsg = t('topo.topoRender.038944-17') + modelTip;
        }
        $modal
          .prompt(tipMsg, modelName, {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            closeOnClickModal: false,
            closeOnPressEscape: false,
            inputErrorMessage: t('topo.topoRender.038944-18'),
            inputValidator: (value: string) => {
              if (!value) {
                return t('topo.topoRender.038944-18');
              }
              return true;
            },
          })
          .then(({ value }: { value: string }) => {
            const tempDataEvent = { ...dataEvent, modelValue: value };
            controChange(tempDataEvent);
          });
      }
    } else if (djType === 2) {
      window.open(externalLink);
    } else if (djType === 3) {
      const type = getScadaRouteType(route.query);
      const share = getRouteQueryString(route.query, 'share');
      if (compScadaGuid) {
        const queryParams: any = {
          guid: compScadaGuid,
          type,
          sceneModelId: sceneModelId,
          ...(share ? { share } : {}),
          forcePc: !isMobile.value && openModel !== 1 && openModel !== 2 ? 1 : undefined,
        };
        const routeUrl = $router.resolve({
          path: '/scada/topo/fullscreen',
          query: queryParams,
        });
        if (openModel === 1) {
          window.open(routeUrl.href, '_self');
        } else if (openModel === 2) {
          window.open(routeUrl.href, '_blank');
        } else {
          isSmallWind.value = true;
          if (windowWidth) {
            smallWindWidth.value = windowWidth;
          }
          if (windowHeight) {
            smallWindHeight.value = windowHeight;
          }
          smallWindName.value = scadaName;
          smallWindUrl.value = window.location.origin + routeUrl.href;
        }
      } else {
        $modal.msgWarning(t('topo.topoRender.038944-14'));
      }
    } else if (djType === 4) {
      let tempModelValue = '0';
      if (switchControl === 1 && switchValue === '关') {
        tempModelValue = '1';
      }
      if (switchControl === 2 && switchValue === '关') {
        tempModelValue = '0';
      }
      if (switchControl === 1 && switchValue === '开') {
        tempModelValue = '0';
      }
      if (switchControl === 2 && switchValue === '开') {
        tempModelValue = '1';
      }
      component.dataEvent.modelValue = tempModelValue;
      controChange(dataEvent);
    }
    if (djType === 5) {
      if (scadaIndex !== undefined && scadaIndex !== '' && scadaIndex !== null) {
        const type = getScadaRouteType(route.query);
        const share = getRouteQueryString(route.query, 'share');
        const queryObj: any = {
          guid: compScadaGuid,
          pageIndex: scadaIndex,
          pageIndexName: scadaIndexName,
          type,
          sceneModelId: sceneModelId,
          ...(share ? { share } : {}),
          forcePc: !isMobile.value ? 1 : undefined,
        };
        const routeUrl = $router.resolve({
          path: '/scada/topo/fullscreen',
          query: queryObj,
        });
        if (openModel === 1) {
          window.open(routeUrl.href, '_self');
        } else if (openModel === 2) {
          window.open(routeUrl.href, '_blank');
        } else {
          isSmallWind.value = true;
          if (windowWidth) {
            smallWindWidth.value = windowWidth;
          }
          if (windowHeight) {
            smallWindHeight.value = windowHeight;
          }
          smallWindName.value = scadaIndexName;
          smallWindUrl.value = window.location.origin + routeUrl.href;
        }
      } else {
        $modal.msgWarning(t('topo.topoRender.038944-22'));
      }
    }
  }
}

// 控制变化/下发指令
async function controChange(data: any) {
  const type = getScadaRouteType(route.query);
  const routeSerial = getRouteQueryString(route.query, 'serialNumber');
  const serialNum = type === 1 ? routeSerial : data.serialNumber;
  let isOnline = await getDeviceStatusPromise(serialNum);
  if (!isOnline) {
    $modal.msgWarning(t('topo.topoRender.038944-20'));
    return;
  }
  const command: any = {};
  command[data.identifier] = data.modelValue;
  let params = {
    sceneModelId: data.sceneModelId,
    variableType: data.variableType,
    productId: data.productId,
    serialNumber: serialNum,
    slaveId: data.slaveId,
    identifier: data.identifier,
    modelName: data.modelName,
    remoteCommand: command,
    isShadow: false,
  };
  serviceInvoke(params)
    .then((res: any) => {
      if (res.code === 200) {
        $modal.msgSuccess(t('topo.topoRender.038944-21'));
      } else {
        $modal.msgError(res.msg);
      }
    })
    .catch((err: any) => {
      console.log('指令下发失败', err);
    });
}

function handleCompDblClick(component: any) {
  for (var i = 0; i < component.action.length; i++) {
    var item = component.action[i];
    if (item === 'dblclick') {
      //this.$message('组件双击了');
    }
  }
}

// 判断是在手机还是电脑
function isMobile_pc() {
  let flag = navigator.userAgent.match(
    /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i
  );
  return flag;
}

// 判断是在安卓还是ios打开
function is_andriod_ios() {
  var u = navigator.userAgent;
  return {
    //移动终端浏览器版本信息
    trident: u.indexOf('Trident') > -1, //IE内核
    presto: u.indexOf('Presto') > -1, //opera内核
    webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
    gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
    mobile:
      !!u.match(/AppleWebKit.*Mobile/i) ||
      !!u.match(
        /MIDP|SymbianOS|NOKIA|SAMSUNG|LG|NEC|TCL|Alcatel|BIRD|DBTEL|Dopod|PHILIPS|HAIER|LENOVO|MOT-|Nokia|SonyEricsson|SIE-|Amoi|ZTE/
      ), //是否为移动终端
    ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
    android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
    iPhone: u.indexOf('iPhone') > -1 || u.indexOf('Mac') > -1, //是否为iPhone或者QQHD浏览器
    iPad: u.indexOf('iPad') > -1, //是否iPad
    webApp: u.indexOf('Safari') == -1, //是否web应该程序，没有头部与底部
  };
}

// Math.hypot() 计算参数的平方根
function getDistance(start: { x: number; y: number }, stop: { x: number; y: number }) {
  return Math.hypot(stop.x - start.x, stop.y - start.y);
}

// 获取设备状态 Promise 版本
function getDeviceStatusPromise(serialNumber: string) {
  return new Promise<boolean>((resolve, reject) => {
    const query = {
      serialNumber: serialNumber,
    };
    getDeviceStatus(query)
      .then((res: any) => {
        resolve(res.data === 3 ? true : false);
      })
      .catch((err: any) => {
        reject(false);
      });
  });
}

function handleResize() {
  isMobile.value = window.innerWidth <= 768;
  screenWidth.value = window.innerWidth;
}

// 生命周期钩子
onMounted(() => {
  if (!isMobile.value) {
    if (props.fullScreemTip) {
      if (typeof $notify === 'function') {
        $notify({
          title: t('topo.topoRender.038944-3'),
          message: t('topo.topoRender.038944-4'),
          position: 'top-left',
          duration: 4500,
        });
      } else {
        $modal.msg(t('topo.topoRender.038944-4'));
      }
    }
  }
  scadaGuid.value = getRouteQueryString(route.query, 'guid');
  // console.log('是否是手机', this.isMobile);
  getZtData();
  connectMqtt(); // 连接mqtt
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  if (bindDeviceList.value.length !== 0) {
    mqttUnSubscribe(bindDeviceList.value); // 取消mqtt订阅
  }
  window.removeEventListener('resize', handleResize);
});

// 导出需要在模板中使用的函数
defineExpose({
  handleCompThrottleClick,
  handleCompDblClick,
  onContextmenu,
  mouseWheel,
  pageMouseMove,
  pageTouch,
  mouseMove,
  mouseDown,
  mouseUp,
  mouseEnter,
  mouseLeave,
  handleAmplify,
  handleReduction,
  handleReset,
  handleRotate,
  generateImage,
  clickFullscreen,
  parseView,
});
</script>

<style lang="scss" scoped>
#topo-render {
  position: relative;
  width: 100%;
  height: 100%;
  transform-origin: center top;

  .render-box {
    overflow: auto;
    background-color: #ffffff;
    background-clip: padding-box;
    background-origin: padding-box;
    background-repeat: no-repeat;
    background-size: 100% 100%;
    height: 100%;
    position: absolute;

    .render-comp {
      position: absolute;
    }

    .render-comp-clickable {
      cursor: pointer;
    }
  }

  ::-webkit-scrollbar-thumb {
    background-color: #e1e1e1;
  }

  ::-webkit-scrollbar-thumb:hover {
    background-color: #a5a2a2;
  }

  ::-webkit-scrollbar {
    width: 5px;
    height: 5px;
    position: absolute;
  }

  ::-webkit-scrollbar-track {
    background-color: #fff;
  }
  .topo-zoom {
    position: fixed;
    bottom: 50px;
    right: 50px;
    display: flex;
    flex-direction: column;
    opacity: 0;

    transition: opacity 0.3s;
    .topo-zoom-btn {
      position: relative;
      .amplify,
      .reduction,
      .reset,
      .rotate {
        width: 40px;
        height: 40px;
        background-color: #000;
        border-radius: 10px;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-bottom: 10px;

        color: #fff;
        font-size: 20px;
      }
    }
  }
}

:global(.mx-context-menu.scada-render-context-menu) {
  padding: 4px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

:global(.mx-context-menu.scada-render-context-menu .mx-context-menu-item) {
  min-height: 32px;
  padding: 5px 14px;
  box-sizing: border-box;
}

:global(.mx-context-menu.scada-render-context-menu .mx-context-menu-item .label) {
  color: #303133;
  font-size: 14px;
  line-height: 22px;
  padding-inline-end: 8px;
}

:global(.mx-context-menu.scada-render-context-menu .mx-context-menu-item-separator),
:global(.mx-context-menu.scada-render-context-menu .mx-context-menu-item-sperator) {
  padding: 2px 0;
}

:global(.mx-context-menu.scada-render-context-menu .mx-context-menu-item:hover) {
  background-color: #f5f7fa;
}
</style>
