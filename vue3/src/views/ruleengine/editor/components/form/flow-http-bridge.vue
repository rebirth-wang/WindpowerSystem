<template>
  <div class="flow-http-bridge">
    <el-form :model="curNode.data" label-width="80px" size="small">
      <el-form-item :label="$t('ruleengine.editor.index.807357-18')">
        <el-switch v-model="debug" :active-value="1" :inactive-value="0" @change="getSyncData" />
        <el-button
          type="primary"
          link
          size="small"
          @click="showDebugLog"
          style="color: #486ff2; margin-left: 20px; height: 26px; font-size: 13px"
        >
          <el-icon><View /></el-icon>
          {{ $t('look') }}
        </el-button>
      </el-form-item>
      <el-form-item>
        <template #label>
          <span class="span-box">
            <span>{{ $t('ruleengine.editor.components.form.flow-http-bridge.807357-0') }}</span>
            <el-tooltip :content="$t('ruleengine.editor.components.form.flow-http-bridge.807357-1')" placement="top">
              <el-icon style="margin-left: 3px; color: #486ff2"><QuestionFilled /></el-icon>
            </el-tooltip>
          </span>
        </template>
        <el-select
          v-model="curNode.data.id"
          :placeholder="$t('ruleengine.editor.components.form.flow-http-bridge.807357-2')"
          style="width: 100%"
          clearable
          @change="changeBridge"
        >
          <el-option v-for="item in httpBridgeList" :key="item.id" :label="item.name" :value="item.id"></el-option>
        </el-select>
        <div class="form-hint">{{ $t('ruleengine.editor.components.form.flow-http-bridge.807357-3') }}</div>
      </el-form-item>
      <div class="form-divider">
        <span>{{ $t('ruleengine.editor.components.form.flow-http-bridge.807357-4') }}</span>
      </div>
      <el-form-item :label="$t('views.iot.bridge.index.525282-8')" prop="enable">
        <el-switch v-model="curNode.data.enable" active-value="1" inactive-value="0" />
      </el-form-item>
      <el-form-item :label="$t('views.iot.bridge.index.525282-16')" prop="direction">
        <el-select
          v-model="curNode.data.direction"
          :placeholder="$t('views.iot.bridge.index.525282-24')"
          style="width: 100%"
          clearable
          @change="changeDirection"
        >
          <el-option
            v-for="dict in bridging_direction"
            :key="dict.value"
            :label="dict.label"
            :value="Number(dict.value)"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>

    <el-form :model="curNode.data.httpform" label-width="80px" v-if="curNode.data.direction">
      <div v-if="curNode.data.direction == 2">
        <el-form-item :label="$t('views.iot.bridge.index.525282-25')">
          <el-input
            :placeholder="$t('views.iot.bridge.index.525282-26')"
            v-model="curNode.data.httpform.hostUrlbody"
            style="width: 100%"
            @input="getSyncData"
          >
            <template #prepend>
              <el-select
                v-model="hostUrlhead"
                :placeholder="$t('views.iot.bridge.index.525282-27')"
                style="width: 90px"
                @change="getSyncData"
              >
                <el-option v-for="item in urlOptions" :key="item" :value="item"></el-option>
              </el-select>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-28')" prop="method">
          <el-select
            v-model="curNode.data.httpform.method"
            :placeholder="$t('views.iot.bridge.index.525282-27')"
            style="width: 100%"
            @change="getSyncData"
          >
            <el-option v-for="item in options" :key="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-29')">
          <div v-if="isShowHeader" class="header-container">
            <div v-for="(item, index) in requestHeadersMap" :key="index" class="param-group">
              <el-col :span="24" class="param-col">
                <el-input
                  size="small"
                  :placeholder="$t('views.iot.bridge.index.525282-30')"
                  v-model="item.key"
                  clearable
                  class="param-input"
                  @input="getSyncData"
                >
                  <template #prepend>{{ $t('views.iot.bridge.index.525282-32') }}:</template>
                </el-input>
              </el-col>
              <el-col :span="24" class="param-col">
                <el-input
                  size="small"
                  :placeholder="$t('views.iot.bridge.index.525282-31')"
                  v-model="item.value"
                  clearable
                  class="param-input"
                  @input="getSyncData"
                >
                  <template #prepend>{{ $t('views.iot.bridge.index.525282-33') }}:</template>
                </el-input>
              </el-col>
              <el-col :span="24" class="action-row">
                <el-button size="small" plain type="danger" @click="handleRemoveAction(index)" class="delete-btn">
                  <el-icon><Delete /></el-icon>
                  {{ $t('del') }}
                </el-button>
              </el-col>
            </div>
          </div>
          <div>
            +
            <a style="color: #486ff2" @click="handleAddAction()">{{ $t('views.iot.bridge.index.525282-98') }}</a>
          </div>
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-35')" class="header-container">
          <div v-for="(item, index) in requestQuerysMap" :key="index">
            <el-row v-if="isShowParams">
              <el-col :span="24">
                <el-input
                  size="small"
                  :placeholder="$t('views.iot.bridge.index.525282-30')"
                  v-model="item.key"
                  @input="getSyncData"
                >
                  <template #prepend>{{ $t('views.iot.bridge.index.525282-32') }}:</template>
                </el-input>
              </el-col>
              <el-col :span="24">
                <el-input
                  size="small"
                  :placeholder="$t('views.iot.bridge.index.525282-31')"
                  v-model="item.value"
                  @input="getSyncData"
                >
                  <template #prepend>{{ $t('views.iot.bridge.index.525282-33') }}:</template>
                </el-input>
              </el-col>
              <div class="delete-wrap">
                <el-button size="small" plain type="danger" style="padding: 5px" @click="handleRemoveQuerys(index)">
                  <el-icon><Delete /></el-icon>
                  {{ $t('del') }}
                </el-button>
              </div>
            </el-row>
          </div>
          <div>
            +
            <a style="color: #486ff2" @click="handleAddQuerys()">{{ $t('views.iot.bridge.index.525282-34') }}</a>
          </div>
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-109')" prop="requestConfig" class="header-container">
          <div v-if="isShowConfig">
            <div v-for="(item, index) in requestConfigMap" :key="index">
              <el-row>
                <el-col :span="24">
                  <el-input
                    size="small"
                    :placeholder="$t('views.iot.bridge.index.525282-30')"
                    v-model="item.key"
                    @input="getSyncData"
                  >
                    <template #prepend>{{ $t('views.iot.bridge.index.525282-32') }}:</template>
                  </el-input>
                </el-col>
                <el-col :span="24">
                  <el-input
                    size="small"
                    :placeholder="$t('views.iot.bridge.index.525282-31')"
                    v-model="item.value"
                    @input="getSyncData"
                  >
                    <template #prepend>{{ $t('views.iot.bridge.index.525282-33') }}:</template>
                  </el-input>
                </el-col>
                <div class="delete-wrap">
                  <el-button size="small" plain type="danger" style="padding: 5px" @click="handleRemoveConfig(index)">
                    <el-icon><Delete /></el-icon>
                    {{ $t('del') }}
                  </el-button>
                </div>
              </el-row>
            </div>
          </div>
          <div>
            +
            <a style="color: #486ff2" @click="handleAddConfig()">{{ $t('views.iot.bridge.index.525282-99') }}</a>
          </div>
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-36')" prop="requestBody">
          <el-input
            v-model="curNode.data.httpform.requestBody"
            type="textarea"
            rows="6"
            :placeholder="$t('views.iot.bridge.index.525282-37')"
            style="width: 100%"
            :autosize="{ minRows: 3, maxRows: 5 }"
            @input="getSyncData"
          />
        </el-form-item>
      </div>
      <div v-show="curNode.data.direction === 1">
        <el-form-item :label="$t('views.iot.bridge.index.525282-82')" prop="route">
          <el-select
            v-model="curNode.data.httpform.route"
            :placeholder="$t('views.iot.bridge.index.525282-82')"
            style="width: 100%"
            @change="getSyncData"
          >
            <el-option
              v-for="dict in bridge_entry"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
      </div>
    </el-form>
    <debug-log :log-list="curNode.data.debugLog" ref="debugLogRef"></debug-log>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { useRuleEditorStore } from '@/stores/modules/ruleEditor';
import { storeToRefs } from 'pinia';
import { useDict } from '@/utils/dict/useDict';
import { View, QuestionFilled, Delete } from '@element-plus/icons-vue';
import { listBridge } from '@/api/iot/bridge';
import debugLog from '../debug-log.vue';

const { t } = useI18n();
const route = useRoute();
const ruleEditorStore = useRuleEditorStore();
const { curNode } = storeToRefs(ruleEditorStore);
const { bridge_entry, bridging_direction } = useDict('bridge_entry', 'bridging_direction');

const debugLogRef = ref();
const httpBridgeList = ref<any[]>([]);
const requestHeadersMap = reactive<any[]>([]);
const requestQuerysMap = reactive<any[]>([]);
const requestConfigMap = reactive<any[]>([]);
const options = ['POST', 'PUT', 'GET'];
const hostUrlhead = ref('http://');
const urlOptions = ['http://', 'https://'];
const isShowHeader = ref(false);
const isShowParams = ref(false);
const isShowConfig = ref(false);
const debug = ref(0);

const defaultHttpform = () => ({
  hostUrlbody: '',
  method: 'POST',
  requestBody: '',
  route: '',
  requestHeaders: '{}',
  requestQuerys: '{}',
  requestConfig: '{}',
});

onMounted(() => {
  getHttpBridgetList();
  if (curNode.value.data.configJson) getConfigJson();
  if (!curNode.value.data.httpform) {
    curNode.value.data = { ...curNode.value.data, direction: null, name: null, enable: 0, debug: 0 };
    curNode.value.data.httpform = defaultHttpform();
    requestHeadersMap.length = 0;
    requestQuerysMap.length = 0;
    requestConfigMap.length = 0;
    hostUrlhead.value = 'http://';
  }
  nextTick(() => {
    debug.value = curNode.value.data.debug || 0;
    getSyncData();
  });
});

const getHttpBridgetList = () => {
  listBridge({ pageNum: 1, pageSize: 999, type: 3 }).then((res: any) => {
    if (res.code === 200) httpBridgeList.value = res.rows.filter((item: any) => item.direction !== 1);
  });
};

const getConfigJson = () => {
  isShowHeader.value = true;
  isShowConfig.value = true;
  isShowParams.value = true;
  requestHeadersMap.length = 0;
  curNode.value.data.httpform = JSON.parse(curNode.value.data.configJson);
  if (curNode.value.data.httpform.requestHeaders) {
    try {
      const obj = JSON.parse(curNode.value.data.httpform.requestHeaders);
      for (const key in obj) {
        if (obj.hasOwnProperty(key)) requestHeadersMap.push({ key, value: obj[key] });
      }
    } catch (e) {
      console.error('解析requestHeaders失败:', e);
    }
  }
  requestQuerysMap.length = 0;
  if (curNode.value.data.httpform.requestQuerys) {
    try {
      const obj = JSON.parse(curNode.value.data.httpform.requestQuerys);
      for (const key in obj) {
        if (obj.hasOwnProperty(key)) requestQuerysMap.push({ key, value: obj[key] });
      }
    } catch (e) {
      console.error('解析requestQuerys失败:', e);
    }
  }
  requestConfigMap.length = 0;
  if (curNode.value.data.httpform.requestConfig) {
    try {
      const obj = JSON.parse(curNode.value.data.httpform.requestConfig);
      for (const key in obj) {
        if (obj.hasOwnProperty(key)) requestConfigMap.push({ key, value: obj[key] });
      }
    } catch (e) {
      console.error('解析requestConfig失败:', e);
    }
  }
  if (curNode.value.data.httpform.hostUrl) {
    const isHttps = curNode.value.data.httpform.hostUrl.startsWith('https://');
    hostUrlhead.value = isHttps ? 'https://' : 'http://';
    curNode.value.data.httpform.hostUrlbody = curNode.value.data.httpform.hostUrl.replace(hostUrlhead.value, '');
  }
};

const getSyncData = () => {
  if (!curNode.value.data.httpform) curNode.value.data.httpform = defaultHttpform();
  curNode.value.data.httpform.requestHeaders =
    requestHeadersMap.length > 0
      ? JSON.stringify(Object.fromEntries(requestHeadersMap.map((i: any) => [i.key, i.value])))
      : '{}';
  curNode.value.data.httpform.requestQuerys =
    requestQuerysMap.length > 0
      ? JSON.stringify(Object.fromEntries(requestQuerysMap.map((i: any) => [i.key, i.value])))
      : '{}';
  curNode.value.data.httpform.requestConfig =
    requestConfigMap.length > 0
      ? JSON.stringify(Object.fromEntries(requestConfigMap.map((i: any) => [i.key, i.value])))
      : '{}';
  curNode.value.data.httpform.hostUrl = hostUrlhead.value + (curNode.value.data.httpform.hostUrlbody || '');
  curNode.value.data.httpform.name = curNode.value.data.name;
  curNode.value.data.route = curNode.value.data.httpform.route;
  curNode.value.data.debug = debug.value;
  curNode.value.data.configJson = JSON.stringify(curNode.value.data.httpform);
};

const reset = () => {
  curNode.value.data = { ...curNode.value.data, direction: null, name: null, enable: 0, debug: 0 };
  curNode.value.data.httpform = defaultHttpform();
  requestHeadersMap.length = 0;
  requestQuerysMap.length = 0;
  requestConfigMap.length = 0;
  hostUrlhead.value = 'http://';
  debug.value = 0;
  getSyncData();
};

const changeDirection = () => {
  curNode.value.data.httpform = defaultHttpform();
  requestHeadersMap.length = 0;
  requestQuerysMap.length = 0;
  requestConfigMap.length = 0;
  isShowHeader.value = false;
  isShowConfig.value = false;
  isShowParams.value = false;
  getSyncData();
};

const changeBridge = (bridgeId: any) => {
  if (!bridgeId) {
    reset();
    curNode.value.name = null;
    return;
  }
  const selectedItem = httpBridgeList.value.find((item: any) => item.id === bridgeId);
  if (selectedItem) {
    const httpform = JSON.parse(selectedItem.configJson);
    curNode.value.name = selectedItem.name;
    curNode.value.data = {
      id: selectedItem.id,
      name: selectedItem.name,
      enable: Number(selectedItem.enable),
      status: selectedItem.status,
      type: selectedItem.type,
      direction: selectedItem.direction,
      route: selectedItem.route,
      debug: Number(debug.value),
      httpform: httpform,
      configJson: selectedItem.configJson,
    };
    getConfigJson();
    getSyncData();
  }
};

const handleAddAction = () => {
  isShowHeader.value = true;
  requestHeadersMap.push({ key: '', value: '' });
  getSyncData();
};
const handleAddQuerys = () => {
  isShowParams.value = true;
  requestQuerysMap.push({ key: '', value: '' });
  getSyncData();
};
const handleAddConfig = () => {
  isShowConfig.value = true;
  requestConfigMap.push({ key: '', value: '' });
  getSyncData();
};
const handleRemoveAction = (index: number) => {
  requestHeadersMap.splice(index, 1);
  getSyncData();
};
const handleRemoveQuerys = (index: number) => {
  requestQuerysMap.splice(index, 1);
  getSyncData();
};
const handleRemoveConfig = (index: number) => {
  requestConfigMap.splice(index, 1);
  getSyncData();
};

const showDebugLog = () => {
  debugLogRef.value.openLog = true;
  debugLogRef.value.handleViewLog(Number(route.query.id), curNode.value.id);
};
</script>

<style lang="scss" scoped>
.flow-http-bridge {
  width: 100%;
  padding: 0 10px;
}
.form-divider {
  position: relative;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.form-divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background-color: #e5e6eb;
  z-index: 1;
}
.form-divider span {
  position: relative;
  z-index: 2;
  padding: 0 15px;
  background-color: #fff;
  font-size: 12px;
  color: #86909c;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.form-hint {
  margin-top: 5px;
  font-size: 12px;
  color: #86909c;
  line-height: 1.5;
}
.span-box {
  display: inline-flex;
  align-items: center;
}
</style>
