<template>
  <div class="scene-list-detail">
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane :label="$t('scene.detail.index.209809-0')" name="overview" lazy>
        <scene-overview :sceneModels="sceneModels" />
      </el-tab-pane>
      <el-tab-pane :label="$t('scene.detail.index.209809-1')" name="scada" v-if="isShowScada === true" lazy>
        <template #label>
          <span v-hasPermi="['scene:model:scada:run']">{{ $t('scene.detail.index.209809-1') }}</span>
        </template>
        <scene-scada ref="scadaRef" :sceneModels="sceneModels" />
      </el-tab-pane>
      <el-tab-pane :label="$t('scene.detail.index.209809-2')" name="video" lazy>
        <template #label>
          <span v-hasPermi="['scene:SipRelation:list']">{{ $t('scene.detail.index.209809-2') }}</span>
        </template>
        <scene-video ref="videoRef" :sceneModels="sceneModels" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import { getSceneModelDetail } from '@/api/scene/list.js';
import sceneOverview from './overview.vue';
import sceneScada from './scada.vue';
import sceneVideo from './video.vue';
import defaultSettings from '@/settings';

const { proxy } = getCurrentInstance() as any;
const route = useRoute();

const activeTab = ref('overview');
const isShowScada = ref((defaultSettings as any).isShowScada);
const scadaRef = ref<any>(null);
const videoRef = ref<any>(null);
const sceneModels = reactive<any>({
  sceneModelDeviceVOList: [],
  deviceInfo: {},
});

/** 获取详情 */
function getDetail() {
  proxy.$modal.loading(proxy.$t('scene.detail.index.209809-3'));
  const id = route.query.sceneModelId;
  getSceneModelDetail(id).then((res: any) => {
    if (res.code === 200) {
      Object.assign(sceneModels, res.data);
      sceneModels.sceneModelDeviceVOList = [
        { id: '-1', name: proxy.$t('scene.detail.index.209809-4') },
        ...res.data.sceneModelDeviceVOList,
      ];
      mqttSubscribe(sceneModels.sceneModelDeviceVOList);
    }
    proxy.$modal.closeLoading();
  });
}

/** 标签页切换 */
function handleTabClick(tab: any) {
  if (tab.paneName !== 'video') {
    videoRef.value?.handleClose();
  }
  if (tab.paneName !== 'scada') {
    const scadaComp = scadaRef.value;
    if (scadaComp?.$refs?.sceneScada) {
      const copmRef = scadaComp.$refs.sceneScada;
      if (copmRef?.$refs?.spirit) {
        copmRef.$refs.spirit.forEach((item: any) => {
          if (item.$vnode?.tag?.includes('ViewInlineVideo')) {
            item.handleCloseJessibuca();
          }
        });
      }
    }
  }
}

/** mqtt订阅主题 */
function mqttSubscribe(list: any[]) {
  let topics: string[] = [];
  for (let i = 1; i < list.length; i++) {
    if (list[i].variableType === 1) {
      const topicStatus = '/' + list[i].productId + '/' + list[i].serialNumber + '/status/post';
      const topicMonitor = '/' + list[i].productId + '/' + list[i].serialNumber + '/monitor/post';
      const serviceTop = '/' + list[i].productId + '/' + list[i].serialNumber + '/ws/service';
      topics.push(topicStatus, topicMonitor, serviceTop);
    } else {
      const topicScene = '/' + list[i].sceneModelId + '/' + list[i].id + '/scene/report';
      topics.push(topicScene);
    }
  }
  proxy?.$mqttTool.subscribe(topics);
}

/** mqtt取消订阅主题 */
function mqttUnSubscribe(list: any[]) {
  let topics: string[] = [];
  for (let i = 1; i < list.length; i++) {
    if (list[i].variableType === 1) {
      const topicStatus = '/' + list[i].productId + '/' + list[i].serialNumber + '/status/post';
      const topicMonitor = '/' + list[i].productId + '/' + list[i].serialNumber + '/monitor/post';
      const serviceTop = '/' + list[i].productId + '/' + list[i].serialNumber + '/ws/service';
      topics.push(topicStatus, topicMonitor, serviceTop);
    } else {
      const topicScene = '/' + list[i].sceneModelId + '/' + list[i].id + '/scene/report';
      topics.push(topicScene);
    }
  }
  proxy?.$mqttTool.unsubscribe(topics);
}

onMounted(() => {
  getDetail();
});

onUnmounted(() => {
  mqttUnSubscribe(sceneModels.sceneModelDeviceVOList);
});
</script>

<style lang="scss" scoped>
.scene-list-detail {
  width: 100%;
  padding: 15px 15px 0 15px;
}
</style>
