<template>
  <div class="topo-unit-lib-bar">
    <div class="header-bar">
      <div class="pc-wrap" :class="selected === 'pcConfig' ? 'pc_active' : ''" @click="handlePlatformTab('pcConfig')">
        <input type="checkbox" id="pcConfig" v-model="configData.pcChecked" @change="handlePcCheckedChange" />
        <span class="text" :class="configData.pcChecked ? 'text_active' : ''">
          {{ $t('scada.topo.components.unitLibBar.index.875001-0') }}
        </span>
      </div>
      <div class="mb-wrap" :class="selected === 'mdConfig' ? 'mb_active' : ''" @click="handlePlatformTab('mdConfig')">
        <input type="checkbox" id="mdConfig" v-model="configData.mdChecked" @change="handleMdCheckedChange" />
        <span class="text" :class="configData.mdChecked ? 'text_active' : ''">
          {{ $t('scada.topo.components.unitLibBar.index.875001-1') }}
        </span>
      </div>
      <div class="help-wrap">
        <el-popover trigger="hover" placement="right-start" effect="light" width="500">
          <p v-html="$t('scada.topo.components.unitLibBar.index.875001-2')"></p>
          <p v-html="$t('scada.topo.components.unitLibBar.index.875001-3')"></p>
          <p>{{ $t('scada.topo.components.unitLibBar.index.875001-4') }}</p>
          <p v-html="$t('scada.topo.components.unitLibBar.index.875001-5')"></p>
          <p v-html="$t('scada.topo.components.unitLibBar.index.875001-6')"></p>
          <template #reference>
            <el-icon style="color: #858585">
              <QuestionFilled />
            </el-icon>
          </template>
        </el-popover>
      </div>
    </div>

    <el-tabs class="unit-lib-comp-box" v-model="tabsActiveName" tab-position="left" @tab-click="handleTabsClick">
      <el-tab-pane name="1">
        <template #label>
          <div class="tab-item">
            <img
              v-if="tabsActiveName === '1'"
              width="24"
              height="24"
              src="@/assets/images/scada/edit_basic_active.png"
            />
            <img v-else width="24" height="24" src="@/assets/images/scada/edit_basic_disable.png" />
            <span class="name" :class="{ active: tabsActiveName === '1' }">
              {{ $t('scada.topo.components.unitLibBar.index.875001-12') }}
            </span>
          </div>
        </template>
        <el-menu unique-opened :default-openeds="['base']">
          <el-sub-menu index="base">
            <template #title>
              <span>{{ $t('scada.topo.components.unitLibBar.index.875001-13') }}</span>
            </template>
            <data-panel class="menu-panel" :data="jsonBase.items" />
          </el-sub-menu>
          <el-sub-menu index="shape">
            <template #title>
              <span>{{ $t('scada.topo.components.unitLibBar.index.875001-14') }}</span>
            </template>
            <data-panel class="menu-panel" :data="jsonShape.items" />
          </el-sub-menu>
          <el-sub-menu index="interaction">
            <template #title>
              <span>{{ $t('scada.topo.components.unitLibBar.index.875001-15') }}</span>
            </template>
            <data-panel class="menu-panel" :data="jsonInteraction.items" />
          </el-sub-menu>
        </el-menu>
      </el-tab-pane>
      <el-tab-pane name="2">
        <template #label>
          <div class="tab-item">
            <img
              v-if="tabsActiveName === '2'"
              width="24"
              height="24"
              src="@/assets/images/scada/edit_static_active.png"
            />
            <img v-else width="24" height="24" src="@/assets/images/scada/edit_static_disable.png" />
            <span class="name" :class="{ active: tabsActiveName === '2' }">
              {{ $t('scada.topo.components.unitLibBar.index.875001-16') }}
            </span>
          </div>
        </template>
        <data-panel class="tab-panel" :data="jsonChart.items" />
      </el-tab-pane>
      <el-tab-pane name="3">
        <template #label>
          <div class="tab-item">
            <img
              v-if="tabsActiveName === '3'"
              width="24"
              height="24"
              src="@/assets/images/scada/edit_gallery_active.png"
            />
            <img v-else width="24" height="24" src="@/assets/images/scada/edit_gallery_disable.png" />
            <span class="name" :class="{ active: tabsActiveName === '3' }">
              {{ $t('scada.topo.components.unitLibBar.index.875001-17') }}
            </span>
          </div>
        </template>
        <el-menu unique-opened @open="handleGalleryOpen">
          <el-sub-menu
            v-for="galleryDict in dict.type.scada_gallery_type"
            :index="galleryDict.dictValue + ''"
            :key="galleryDict.dictValue"
          >
            <template #title>
              <span>{{ galleryDict.dictLabel }}</span>
            </template>
            <data-panel
              class="menu-panel"
              v-loading="loading"
              element-loading-background="transparent"
              :isMore="true"
              :data="gallerys"
              :total="galleryTotal"
              @moreClick="handleMoreClick('gallery', galleryDict.dictValue)"
            />
          </el-sub-menu>
        </el-menu>
      </el-tab-pane>
      <el-tab-pane name="4">
        <template #label>
          <div class="tab-item">
            <img
              v-if="tabsActiveName === '4'"
              width="24"
              height="24"
              src="@/assets/images/scada/edit_chart_active.png"
            />
            <img v-else width="24" height="24" src="@/assets/images/scada/edit_chart_disable.png" />
            <span class="name" :class="{ active: tabsActiveName === '4' }">
              {{ $t('scada.topo.components.unitLibBar.index.875001-18') }}
            </span>
          </div>
        </template>
        <el-menu unique-opened @open="handleEchartOpen">
          <el-sub-menu
            v-for="echartDict in dict.type.scada_echart_type"
            :index="echartDict.dictValue"
            :key="echartDict.dictValue"
          >
            <template #title>
              <span>{{ echartDict.dictLabel }}</span>
            </template>
            <data-panel
              v-if="echarts.length > 0"
              class="menu-panel"
              v-loading="loading"
              element-loading-background="transparent"
              :isMore="true"
              :data="echarts"
              :total="echartTotal"
              @moreClick="handleMoreClick('echart', echartDict.dictValue)"
            />
            <el-empty v-else :image-size="80" :description="$t('noData')"></el-empty>
          </el-sub-menu>
        </el-menu>
      </el-tab-pane>
      <el-tab-pane name="5">
        <template #label>
          <div class="tab-item">
            <img
              v-if="tabsActiveName === '5'"
              width="24"
              height="24"
              src="@/assets/images/scada/edit_model_active.png"
            />
            <img v-else width="24" height="24" src="@/assets/images/scada/edit_model_disable.png" />
            <span class="name" :class="{ active: tabsActiveName === '5' }">
              {{ $t('scada.topo.components.unitLibBar.index.875001-19') }}
            </span>
          </div>
        </template>
        <data-panel
          v-if="models.length > 0"
          class="tab-panel"
          v-loading="loading"
          element-loading-background="transparent"
          :data="models"
        />
        <el-empty v-else :image-size="100" :description="$t('noData')"></el-empty>
      </el-tab-pane>
      <el-tab-pane name="6">
        <template #label>
          <div class="tab-item">
            <img
              v-if="tabsActiveName === '6'"
              width="24"
              height="24"
              src="@/assets/images/scada/edit_more_active.png"
            />
            <img v-else width="24" height="24" src="@/assets/images/scada/edit_more_disable.png" />
            <span class="name" :class="{ active: tabsActiveName === '6' }">{{ $t('more') }}</span>
          </div>
        </template>
        <data-panel
          v-if="components.length > 0"
          class="tab-panel"
          v-loading="loading"
          element-loading-background="transparent"
          :data="components"
        />
        <el-empty v-else :image-size="100" :description="$t('noData')"></el-empty>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, getCurrentInstance, computed, onMounted } from 'vue';
import { QuestionFilled } from '@element-plus/icons-vue';
import { useAppStore } from '@/stores/modules/app';
import { useTopoEditorStore } from '@/stores/modules/topoEditor';

import { listGallery } from '@/api/scada/gallery';
import { listEchart } from '@/api/scada/echart';
import { listModel } from '@/api/scada/model';
import { listComponent } from '@/api/scada/component';
import { getDicts } from '@/api/system/dict/data';

import DataPanel from './dataPanel.vue';
import jsonBase from './unitMenu/base.json';
import jsonShape from './unitMenu/shape.json';
import jsonChart from './unitMenu/chart.json';
import jsonInteraction from './unitMenu/interaction.json';

const appStore = useAppStore();
const topoEditorStore = useTopoEditorStore();
const { proxy } = getCurrentInstance() as any;

// 计算属性
const sidebar = computed(() => appStore.sidebar);
const selected = computed(() => topoEditorStore.selected);
const configData = computed(() => topoEditorStore.topoData);

const tabsActiveName = ref('1');

// 数据
const loading = ref(false);
const gallerys = ref<any[]>([]);
const galleryPageNum = ref(1);
const galleryPageSize = ref(10);
const galleryTotal = ref(0);
const echarts = ref<any[]>([]);
const echartPageNum = ref(1);
const echartPageSize = ref(10);
const echartTotal = ref(0);
const models = ref<any[]>([]);
const components = ref<any[]>([]);

// 字典数据
const dict = reactive({
  type: {
    scada_gallery_type: [],
    scada_echart_type: [],
  },
});

// 初始化字典
const initDicts = async () => {
  try {
    const [galleryRes, echartRes]: any = await Promise.all([
      getDicts('scada_gallery_type'),
      getDicts('scada_echart_type'),
    ]);

    if (galleryRes.code === 200) {
      dict.type.scada_gallery_type = galleryRes.data || [];
    }
    if (echartRes.code === 200) {
      dict.type.scada_echart_type = echartRes.data || [];
    }
  } catch (error) {
    console.error('获取字典失败:', error);
  }
};

// 页面挂载后加载字典
onMounted(() => {
  initDicts();
});

// 平台切换tab
function handlePlatformTab(tab: string) {
  topoEditorStore.setSelected(tab);
  topoEditorStore.setPageIndex(0);
}

// pc端选择
function handlePcCheckedChange() {
  if (configData.value.pcChecked) {
    const content = proxy.$t('scada.topo.components.unitLibBar.index.875001-7');
    proxy
      .$confirm(content, proxy.$t('tips'), {
        confirmButtonText: proxy.$t('confirm'),
        cancelButtonText: proxy.$t('cancel'),
        type: 'warning',
        dangerouslyUseHTMLString: true,
      })
      .then(() => {
        topoEditorStore.topoData.pcChecked = true;
      })
      .catch(() => {
        topoEditorStore.topoData.pcChecked = false;
      });
  }
  if (!configData.value.pcChecked) {
    if (!configData.value.mdChecked) {
      proxy
        .$confirm(proxy.$t('scada.topo.components.unitLibBar.index.875001-8'), proxy.$t('tips'), {
          confirmButtonText: proxy.$t('confirm'),
          cancelButtonText: proxy.$t('cancel'),
          type: 'warning',
          dangerouslyUseHTMLString: true,
        })
        .then(() => {
          topoEditorStore.topoData.pcChecked = true;
        })
        .catch(() => {
          topoEditorStore.topoData.pcChecked = true;
        });
    } else {
      proxy.$message.warning(proxy.$t('scada.topo.components.unitLibBar.index.875001-9'));
    }
  }
}

// 移动端选择
function handleMdCheckedChange() {
  console.log(configData.value);
  if (configData.value.mdChecked) {
    const content = proxy.$t('scada.topo.components.unitLibBar.index.875001-10');
    proxy
      .$confirm(content, proxy.$t('tips'), {
        confirmButtonText: proxy.$t('confirm'),
        cancelButtonText: proxy.$t('cancel'),
        type: 'warning',
        dangerouslyUseHTMLString: true,
      })
      .then(() => {
        topoEditorStore.topoData.mdChecked = true;
      })
      .catch(() => {
        topoEditorStore.topoData.mdChecked = false;
      });
  }
  if (!configData.value.mdChecked) {
    if (!configData.value.pcChecked) {
      proxy
        .$confirm(proxy.$t('scada.topo.components.unitLibBar.index.875001-8'), proxy.$t('tips'), {
          confirmButtonText: proxy.$t('confirm'),
          cancelButtonText: proxy.$t('cancel'),
          type: 'warning',
          dangerouslyUseHTMLString: true,
        })
        .then(() => {
          topoEditorStore.topoData.mdChecked = true;
        })
        .catch(() => {
          topoEditorStore.topoData.mdChecked = true;
        });
    } else {
      proxy.$message.success(proxy.$t('scada.topo.components.unitLibBar.index.875001-11'));
    }
  }
}

// 图片组件打开
function handleGalleryOpen(key: string) {
  galleryPageNum.value = 1;
  galleryPageSize.value = 10;
  galleryTotal.value = 0;
  getGalleryDatas(key);
}

// 图表组件打开
function handleEchartOpen(key: string) {
  echartPageNum.value = 1;
  echartPageSize.value = 10;
  echartTotal.value = 0;
  getEchartDatas(key);
}

// tabs点击事件
function handleTabsClick(tab: any) {
  if (tab.props.name === '5') {
    getModel();
  } else if (tab.props.name === '6') {
    getComponent();
  }
}

// 获取图库数据
function getGalleryDatas(name: string) {
  loading.value = true;
  const params = {
    pageNum: galleryPageNum.value,
    pageSize: galleryPageSize.value,
    categoryName: name,
    moduleGuid: proxy.$t('scada.gallery.309456-5'),
    orderByColumn: 'id',
    isAsc: 'desc',
  };
  listGallery(params).then((res: any) => {
    if (res.code === 200) {
      if (res.rows.length > 0) {
        let list = res.rows.map((item: any) => {
          let newJsonItem = getImgJsonItem();
          newJsonItem.text = item.fileName;
          newJsonItem.icon = item.resourceUrl;
          newJsonItem.info.style.url = item.resourceUrl;
          return newJsonItem;
        });
        if (galleryPageNum.value === 1) {
          gallerys.value = list;
        } else {
          gallerys.value = gallerys.value.concat(list);
        }
        galleryTotal.value = res.total;
      } else {
        gallerys.value = [];
        galleryTotal.value = 0;
      }
    }
    loading.value = false;
  });
}

// 获取图表数据
function getEchartDatas(name: string) {
  loading.value = true;
  const params = {
    pageNum: echartPageNum.value,
    pageSize: echartPageSize.value,
    echartType: name,
  };
  listEchart(params).then((res: any) => {
    if (res.code === 200) {
      if (res.rows.length > 0) {
        let list = res.rows.map((item: any) => {
          let newJsonItem = getEchartJsonItem();
          newJsonItem.text = item.echartName;
          newJsonItem.icon = item.echartImgae;
          newJsonItem.info.dataBind.echartOption = `echartId-${item.id}`;
          return newJsonItem;
        });
        if (echartPageNum.value === 1) {
          echarts.value = list;
        } else {
          echarts.value = echarts.value.concat(list);
        }
        echartTotal.value = res.total;
      } else {
        echarts.value = [];
        echartTotal.value = 0;
      }
    }
    loading.value = false;
  });
}

// 模型数据
function getModel() {
  loading.value = true;
  const params = {
    pageNum: 1,
    pageSize: 9999,
  };
  listModel(params).then((res: any) => {
    if (res.code === 200) {
      if (res.rows.length > 0) {
        let list = res.rows.map((item: any) => {
          let newJsonItem = getModelJsonItem() as any;
          newJsonItem.text = item.modelName;
          newJsonItem.icon = item.imageUrl;
          newJsonItem.info.dataBind.model3dType = item.modelType;
          newJsonItem.info.dataBind.model3dUrl = item.modelUrl;
          newJsonItem.info.dataBind.model3dName = item.modelName;
          return newJsonItem;
        });
        models.value = list;
      } else {
        models.value = [];
      }
    }
    loading.value = false;
  });
}

// 组件数据
function getComponent() {
  loading.value = true;
  const params = {
    pageNum: 1,
    pageSize: 9999,
  };
  listComponent(params).then((res: any) => {
    if (res.code === 200) {
      if (res.rows.length > 0) {
        let list = res.rows.map((item: any) => {
          let newJsonItem = getComponentJsonItem();
          newJsonItem.text = item.componentName;
          newJsonItem.icon = item.componentImage;
          if (item && item.id) {
            newJsonItem.info.dataBind.id = item.id;
          }
          return newJsonItem;
        });
        components.value = list;
      } else {
        components.value = [];
      }
    }
    loading.value = false;
  });
}

// 生成图片json
function getImgJsonItem() {
  let newJson = {
    text: '图片',
    icon: '',
    type: 'service',
    info: {
      type: 'image',
      componentShow: ['动画', '单击', '组件颜色', '滤镜渲染', '状态开关', '参数绑定'],
      action: ['click'],
      hdClassName: '',
      dataBind: {
        dataType: 1,
        modelName: '',
        modelValue: '',
        identifier: '',
        simValue: '',
        simInterval: 1,
        filter: 0,
        funValue: '// 数据处理逻辑编辑\r\n// 以返回值作为绑定结果\r\nconst result = value;\r\nreturn result;',
        sceneModelId: '',
        sceneModelDeviceId: '',
        variableType: '',
        productId: '',
        serialNumber: '',
        statusType: 1,
        shutImageUrl: '',
        openImageUrl: '',
        warnImageUrl: '',
        stateList: [],
      },
      dataEvent: {
        djAction: false,
        action: '1',
        sceneModelId: '',
        variableType: '',
        productId: '',
        serialNumber: '',
        slaveId: '',
        identifier: '',
        modelName: '',
        modelValue: '',
        redirectUrl: '',
        ztPage: '',
        djType: 1,
        modelTip: '',
        writeType: 1,
        funValue:
          "/*\r\nreturn 的值是脚本的最终输出。\r\n\r\n1. 返回节点数据，例如固定值：\r\nreturn 4\r\n\r\n2. 返回可变数据：\r\napp.$prompt('请输入', '温度', {\r\n    confirmButtonText: '确认',\r\n    cancelButtonText: '取消',\r\n    closeOnClickModal: false,\r\n    closeOnPressEscape: false,\r\n    inputValidator: (value) => {\r\n        if (!value) {\r\n            return '变量值不能为空';\r\n        }\r\n    },\r\n}).then(({ value }) => {\r\n    issord(value);\r\n});\r\n以上两种返回结果展示效果不同：\r\n- app 是应用实例，通过它可以对整个应用进行操作\r\n- issord 是下发函数，下发对应变量值\r\n- 对于固定值：发送值不显示\r\n- 对于输入值：根据代码弹框，并输入值发送\r\n*/\r\nreturn;",
        externalLink: '',
        scadaGuid: '',
        openModel: 1,
        windowWidth: '',
        windowHeight: '',
        switchControl: 1,
        scadaIndex: '',
        passwordEnabled: false,
        passwordType: 'user',
      },
      dataAction: {
        xyAction: false,
        xzAction: false,
        ssAction: false,
        hdAction: false,
        maAction: false,
        sceneModelId: '',
        sceneModelDeviceId: '',
        variableType: '',
        productId: '',
        serialNumber: '',
        identifier: '',
        modelName: '',
        paramJudge: '',
        paramJudgeData: '',
        paramJudgeDatarangeMin: '',
        paramJudgeDatarangeMax: '',
        rotationSpeed: '中',
        duration: '',
        translateList: [],
      },
      style: {
        position: {
          x: 200,
          y: 200,
          w: 100,
          h: 100,
        },
        backColor: 'rgba(255,255,255,0)',
        foreColor: 'rgba(255,255,255,0)',
        zIndex: 1,
        transform: 0,
        url: '',
        transformType: 'rotate(0deg)',
        isFilter: false,
      },
    },
  };
  return newJson;
}

// 生成图表json
function getEchartJsonItem() {
  let newJson = {
    text: '',
    icon: '',
    type: 'service',
    info: {
      type: 'chart-wrapper',
      componentShow: ['自定义echarts', '参数绑定'],
      action: [],
      dataBind: {
        dataType: 4,
        statusType: 1,
        simValue: '',
        simInterval: 1,
        filter: 0,
        funValue: '// 数据处理逻辑编辑\r\n// 以返回值作为绑定结果\r\nconst result = value;\r\nreturn result;',
        echartOption: '',
        echartRun: 0,
        echartData: '',
        modelMaps: [],
        httpSetting: {
          type: 'get',
          time: 30,
          unit: 's',
          params: {},
          body: {
            type: 1,
            value: '',
          },
          headers: {},
        },
        httpFilter: '',
        httpSettingId: '',
      },
      style: {
        position: {
          x: 0,
          y: 0,
          w: 350,
          h: 250,
        },
        backColor: 'rgba(255, 255, 255, 1)',
        foreColor: 'rgba(0, 0, 0, 1)',
        zIndex: 1,
        transform: 0,
        transformType: 'rotate(0deg)',
      },
    },
  };
  return newJson;
}

// 生成模型json
function getModelJsonItem() {
  let newJson = {
    text: '',
    icon: '',
    type: 'service',
    info: {
      type: '3D-model',
      componentShow: ['三维模型'],
      action: [],
      dataBind: {
        model3dType: '',
        model3dUrl: '',
        model3dName: '',
        orbitControls: [1, 2, 3, 4, 5],
        cameraX: 0,
        cameraY: 0,
        cameraZ: 20,
      },
      style: {
        position: {
          x: 0,
          y: 0,
          w: 960,
          h: 500,
        },
        backColor: '#00ffff',
        zIndex: 1,
        transform: 0,
        transformType: 'rotate(0deg)',
      },
    },
  };
  return newJson;
}

// 生成组件json
function getComponentJsonItem() {
  let newJson = {
    text: '',
    icon: '',
    type: 'service',
    info: {
      type: 'component',
      componentShow: ['动画'],
      action: [],
      dataBind: {},
      dataAction: {
        xyAction: false,
        xzAction: false,
        ssAction: false,
        hdAction: false,
        maAction: false,
        sceneModelId: '',
        sceneModelDeviceId: '',
        variableType: '',
        productId: '',
        serialNumber: '',
        identifier: '',
        modelName: '',
        paramJudge: '',
        paramJudgeData: '',
        paramJudgeDatarangeMin: '',
        paramJudgeDatarangeMax: '',
        rotationSpeed: '中',
        duration: '',
        translateList: [],
      },
      style: {
        position: {
          x: 0,
          y: 0,
          w: 350,
          h: 250,
        },
        backColor: 'rgba(255, 255, 255, 1)',
        foreColor: 'rgba(0, 0, 0, 1)',
        zIndex: 1,
        transform: 0,
        transformType: 'rotate(0deg)',
      },
    },
  };
  return newJson;
}

function onDragstart(event: DragEvent, info: any) {
  var infoJson = JSON.stringify(info.info);
  event.dataTransfer?.setData('my-info', infoJson);
}

// 点击更多
function handleMoreClick(tab: string, menu: string) {
  if (tab === 'gallery') {
    if (galleryPageNum.value * galleryPageSize.value < galleryTotal.value) {
      galleryPageNum.value = galleryPageNum.value + 1;
      getGalleryDatas(menu);
    }
  }
  if (tab === 'echart') {
    if (echartPageNum.value * echartPageSize.value < echartTotal.value) {
      echartPageNum.value = echartPageNum.value + 1;
      getEchartDatas(menu);
    }
  }
}
</script>

<style lang="scss" scoped>
.topo-unit-lib-bar {
  height: 100%;
  display: flex;
  flex-direction: column;

  .header-bar {
    display: flex;
    flex-direction: row;
    height: 40px;

    .pc-wrap,
    .mb-wrap {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f2f3f4;
      font-size: 15px;
      border-left: 1px solid #dcdfe6;
      border-bottom: 1px solid #dcdfe6;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      cursor: pointer;

      input[type='checkbox'] {
        width: 15px;
        height: 15px;
        position: relative;
        cursor: pointer;
      }

      input[type='checkbox']::after {
        position: absolute;
        top: 0;
        color: #2c3e50;
        width: 15px;
        height: 15px;
        display: inline-block;
        visibility: visible;
        padding-left: 0px;
        text-align: center;
        content: ' ';
        border-radius: 3px;
      }

      input[type='checkbox']:checked::after {
        content: '✓';
        color: #fff;
        font-size: 10px;
        line-height: 14.5px;
        background-color: #486ff2;
        border-color: #486ff2;
      }

      .text {
        margin-left: 4px;
      }

      .text_active {
        color: #486ff2;
      }
    }

    .pc_active,
    .mb_active {
      background: #fff;
      border-bottom: #fff;
    }

    .help-wrap {
      display: flex;
      flex-direction: row;
      align-items: center;
      justify-content: center;
      background: #f5f7fa;
      padding: 0 5px;
      border-left: 1px solid #dcdfe6;
      border-right: 1px solid #dcdfe6;
      border-bottom: 1px solid #dcdfe6;
    }
  }

  .unit-lib-comp-box {
    flex: 1;
    border-right: #dcdfe6 solid 1px;

    .tab-panel {
      padding: 10px;
    }

    .menu-panel {
      padding: 0 10px;
    }

    :deep(.el-tabs__header) {
      margin: 0;
      background: #f2f3f4;

      .el-tabs__item {
        height: 85px;
        padding: 20px 16px;

        .tab-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          font-size: 14px;

          .name {
            font-size: 14px;
            color: #323232;
            line-height: 24px;
          }
        }

        &.is-active {
          color: #486ff2;
          background: #ffffff;
        }
      }

      .el-tabs__active-bar {
        background: transparent;
      }

      .el-tabs__nav-wrap::after {
        background-color: transparent;
      }
    }

    .active {
      color: #486ff2 !important;
    }

    :deep(.el-tabs__content) {
      height: 100%;
      overflow-y: auto;

      .el-submenu__title {
        height: 46px;
        line-height: 46px;
        font-size: 13px;
      }
    }
  }
}
</style>
