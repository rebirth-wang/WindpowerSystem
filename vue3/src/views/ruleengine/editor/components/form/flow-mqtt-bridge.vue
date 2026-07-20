<template>
  <div class="flow-mqtt-bridge">
    <el-form :model="curNode.data" label-width="68px" size="small">
      <el-form-item :label="$t('ruleengine.editor.index.807357-18')">
        <el-switch v-model="debug" :active-value="1" :inactive-value="0" @change="getSyncData" />
        <el-button type="primary" size="small" plain @click="showDebugLog" style="margin-left: 20px; height: 26px">
          <el-icon><View /></el-icon>
          查看
        </el-button>
      </el-form-item>
      <el-form-item>
        <template #label>
          <span class="span-box">
            <span>{{ $t('ruleengine.editor.components.form.flow-mqtt-bridge.807357-0') }}</span>
            <el-tooltip :content="$t('ruleengine.editor.components.form.flow-mqtt-bridge.807357-1')" placement="top">
              <el-icon style="margin-left: 3px; color: #486ff2"><QuestionFilled /></el-icon>
            </el-tooltip>
          </span>
        </template>
        <el-select
          v-model="curNode.data.id"
          :placeholder="$t('ruleengine.editor.components.form.flow-mqtt-bridge.807357-2')"
          style="width: 100%"
          clearable
          @change="changeBridge"
        >
          <el-option v-for="item in mqttBridgeList" :key="item.id" :label="item.name" :value="item.id"></el-option>
        </el-select>
        <div class="form-hint">{{ $t('ruleengine.editor.components.form.flow-mqtt-bridge.807357-3') }}</div>
      </el-form-item>
      <div class="form-divider">
        <span>{{ $t('ruleengine.editor.components.form.flow-mqtt-bridge.807357-4') }}</span>
      </div>
      <el-form-item :label="$t('views.iot.bridge.index.525282-8')" prop="enable">
        <el-switch v-model="curNode.data.enable" active-value="1" inactive-value="0" @change="getSyncData" />
      </el-form-item>
      <el-form-item :label="$t('views.iot.bridge.index.525282-16')" prop="direction">
        <el-select
          v-model="curNode.data.direction"
          :placeholder="$t('views.iot.bridge.index.525282-24')"
          style="width: 100%"
          clearable
          @change="handleDirectionChange"
        >
          <el-option
            v-for="dict in bridging_direction"
            :key="dict.value"
            :label="dict.label"
            :value="Number(dict.value)"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form :model="curNode.data" label-width="120px" v-if="curNode.data.direction">
        <el-form-item :label="$t('views.iot.bridge.index.525282-38')" prop="hostUrlbody">
          <el-input
            :placeholder="$t('views.iot.bridge.index.525282-39')"
            v-model="curNode.data.mqttform.hostUrlbody"
            style="width: 100%"
            @input="getSyncData"
          >
            <template #prepend>tcp://</template>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-40')" prop="clientId">
          <el-input
            v-model="curNode.data.mqttform.clientId"
            :placeholder="$t('views.iot.bridge.index.525282-41')"
            style="width: 100%"
            @input="getSyncData"
          />
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-42')" prop="username">
          <el-input
            v-model="curNode.data.mqttform.username"
            :placeholder="$t('views.iot.bridge.index.525282-43')"
            style="width: 100%"
            @input="getSyncData"
          />
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-44')" prop="password">
          <el-input
            v-model="curNode.data.mqttform.password"
            :placeholder="$t('views.iot.bridge.index.525282-45')"
            type="password"
            style="width: 100%"
            @input="getSyncData"
          />
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-100')" prop="route" v-if="curNode.data.direction === 1">
          <el-input
            v-model="curNode.data.mqttform.route"
            :placeholder="$t('views.iot.bridge.index.525282-101')"
            style="width: 100%"
            @input="getSyncData"
          />
        </el-form-item>
        <el-form-item :label="$t('views.iot.bridge.index.525282-102')" prop="route" v-else>
          <el-input
            v-model="curNode.data.mqttform.route"
            :placeholder="$t('views.iot.bridge.index.525282-103')"
            style="width: 100%"
            @input="getSyncData"
          />
        </el-form-item>
        <div class="advanced-header" v-if="curNode.data.direction" @click="isExpanded = !isExpanded">
          <span>{{ $t('views.iot.bridge.index.525282-104') }}</span>
          <el-icon :class="{ 'rotate-180': isExpanded }"><ArrowDown /></el-icon>
        </div>
        <el-collapse-transition>
          <div v-show="isExpanded && curNode.data.direction">
            <el-form-item :label="$t('views.iot.bridge.index.525282-105')">
              <el-select v-model="curNode.data.mqttform.version" style="width: 100%" @change="getSyncData">
                <el-option
                  v-for="dict in mqtt_version"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('views.iot.bridge.index.525282-106')">
              <el-input
                v-model="curNode.data.mqttform.timeout"
                type="number"
                :placeholder="$t('views.iot.bridge.index.525282-108')"
                style="width: 100%"
                @input="getSyncData"
              ></el-input>
            </el-form-item>
            <el-form-item label="Keep Alive">
              <el-input
                v-model="curNode.data.mqttform.keepalive"
                type="number"
                :placeholder="$t('views.iot.bridge.index.525282-108')"
                style="width: 100%"
                @input="getSyncData"
              ></el-input>
            </el-form-item>
            <el-form-item :label="$t('views.iot.bridge.index.525282-107')">
              <el-switch v-model="curNode.data.mqttform.automaticReconnect" @change="getSyncData"></el-switch>
            </el-form-item>
            <el-form-item label="Clean Session">
              <el-switch v-model="curNode.data.mqttform.cleanSession" @change="getSyncData"></el-switch>
            </el-form-item>
          </div>
        </el-collapse-transition>
      </el-form>
    </el-form>
    <debug-log :log-list="curNode.data.debugLog" ref="debugLogRef"></debug-log>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { useRuleEditorStore } from '@/stores/modules/ruleEditor';
import { storeToRefs } from 'pinia';
import { useDict } from '@/utils/dict/useDict';
import { View, QuestionFilled, ArrowDown } from '@element-plus/icons-vue';
import { listBridge } from '@/api/iot/bridge';
import debugLog from '../debug-log.vue';

const { t } = useI18n();
const route = useRoute();
const ruleEditorStore = useRuleEditorStore();
const { curNode } = storeToRefs(ruleEditorStore);
const { mqtt_version, bridging_direction } = useDict('mqtt_version', 'bridging_direction');

const debugLogRef = ref();
const mqttBridgeList = ref<any[]>([]);
const isExpanded = ref(false);
const debug = ref(0);

const defaultMqttform = () => ({
  hostUrl: '',
  hostUrlbody: '',
  clientId: '',
  username: '',
  password: '',
  route: '',
  version: 0,
  timeout: '',
  keepalive: '',
  automaticReconnect: false,
  cleanSession: false,
});

onMounted(() => {
  getMqttBridgetList();
  debug.value = curNode.value.data.debug || 0;
  if (!curNode.value.data.mqttform) {
    curNode.value.data.mqttform = defaultMqttform();
    curNode.value.data = { ...curNode.value.data, direction: null, route: '', name: null, enable: 1, debug: 0 };
  }
  getSyncData();
});

const getMqttBridgetList = () => {
  listBridge({ pageNum: 1, pageSize: 999, type: 4 }).then((res: any) => {
    if (res.code === 200) mqttBridgeList.value = res.rows.filter((item: any) => item.direction !== 1);
  });
};

const reset = () => {
  curNode.value.data = { ...curNode.value.data, direction: null, route: '', name: null, enable: 1, debug: 0 };
  curNode.value.data.mqttform = defaultMqttform();
  debug.value = 0;
  getSyncData();
};

const handleDirectionChange = () => {
  if (curNode.value.data.direction) {
    curNode.value.data.mqttform = defaultMqttform();
  }
  getSyncData();
};

const getSyncData = () => {
  if (!curNode.value.data.mqttform) curNode.value.data.mqttform = defaultMqttform();
  curNode.value.data.mqttform.name = curNode.value.data.name || '';
  curNode.value.data.route = curNode.value.data.mqttform.route || '';
  curNode.value.data.debug = debug.value;
  curNode.value.data.mqttform.hostUrl = 'tcp://' + curNode.value.data.mqttform.hostUrlbody;
  curNode.value.data.configJson = JSON.stringify(curNode.value.data.mqttform);
};

const changeBridge = (bridgeId: any) => {
  if (!bridgeId) {
    reset();
    curNode.value.name = '';
    return;
  }
  const selectedItem = mqttBridgeList.value.find((item: any) => item.id === bridgeId);
  if (selectedItem) {
    const mqttform = JSON.parse(selectedItem.configJson || '{}');
    curNode.value.name = selectedItem.name;
    curNode.value.data = {
      id: selectedItem.id,
      name: selectedItem.name,
      enable: Number(selectedItem.enable) || 1,
      status: selectedItem.status,
      type: selectedItem.type,
      direction: selectedItem.direction,
      route: selectedItem.route || '',
      debug: Number(debug.value),
      mqttform: mqttform,
      configJson: selectedItem.configJson || JSON.stringify(mqttform),
    };
    getSyncData();
  }
};

const showDebugLog = () => {
  debugLogRef.value.openLog = true;
  debugLogRef.value.handleViewLog(Number(route.query.id), curNode.value.id);
};
</script>

<style lang="scss" scoped>
.flow-mqtt-bridge {
  width: 100%;
  padding: 0 10px;
}
.advanced-header {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 10px 0;
  color: #409eff;
  border-bottom: 1px solid #eee;
  margin-left: 30px;
}
.advanced-header .el-icon {
  margin-left: 8px;
  transition: transform 0.3s;
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
.rotate-180 {
  transform: rotate(180deg);
}
</style>
