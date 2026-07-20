<template>
  <div class="flow-data-bridge">
    <el-form :model="curNode.data" label-width="80px" size="small">
      <el-form-item :label="$t('ruleengine.editor.index.807357-18')">
        <el-switch v-model="debug" :active-value="1" :inactive-value="0" @change="syncConfigJson" />
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
            <span>{{ $t('ruleengine.editor.components.form.flow-data-bridge.807357-0') }}</span>
            <el-tooltip :content="$t('ruleengine.editor.components.form.flow-data-bridge.807357-1')" placement="top">
              <el-icon style="margin-left: 3px; color: #486ff2"><QuestionFilled /></el-icon>
            </el-tooltip>
          </span>
        </template>
        <el-select
          v-model="curNode.data.id"
          :placeholder="$t('ruleengine.editor.components.form.flow-data-bridge.807357-2')"
          style="width: 100%"
          clearable
          @change="changeBridge"
        >
          <el-option v-for="item in dataBaseBridgeList" :key="item.id" :label="item.name" :value="item.id"></el-option>
        </el-select>
        <div class="form-hint">{{ $t('ruleengine.editor.components.form.flow-data-bridge.807357-3') }}</div>
      </el-form-item>
      <div class="form-divider">
        <span>{{ $t('ruleengine.editor.components.form.flow-data-bridge.807357-4') }}</span>
      </div>
      <el-form-item :label="$t('views.iot.bridge.index.525282-8')" prop="enable">
        <el-switch v-model="curNode.data.enable" active-value="1" inactive-value="0" @change="syncConfigJson" />
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
            :value="dict.value"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <el-form :model="curNode.data.dbform" label-width="80px" v-if="curNode.data.direction">
      <el-form-item :label="$t('views.iot.bridge.index.525282-48')">
        <el-select
          v-model="curNode.data.dbform.type"
          :placeholder="$t('views.iot.bridge.index.525282-49')"
          style="width: 100%"
          @change="syncConfigJson"
        >
          <el-option
            v-for="dict in database_type"
            :key="dict.value"
            :value="dict.label"
            :disabled="dict.value == 2"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('views.iot.bridge.index.525282-50')" prop="databaseSource">
        <el-select
          v-model="curNode.data.dbform.databaseSource"
          :placeholder="$t('views.iot.bridge.index.525282-51')"
          style="width: 100%"
          @change="syncConfigJson"
        >
          <el-option v-for="item in dbOptions" :key="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('views.iot.bridge.index.525282-52')" prop="host">
        <el-input
          v-model="curNode.data.dbform.host"
          :placeholder="$t('views.iot.bridge.index.525282-81')"
          style="width: 100%"
          @input="syncConfigJson"
        />
      </el-form-item>
      <el-form-item :label="$t('views.iot.bridge.index.525282-42')" prop="username">
        <el-input
          v-model="curNode.data.dbform.username"
          :placeholder="$t('views.iot.bridge.index.525282-43')"
          style="width: 100%"
          @input="syncConfigJson"
        />
      </el-form-item>
      <el-form-item :label="$t('views.iot.bridge.index.525282-44')" prop="password">
        <el-input
          v-model="curNode.data.dbform.password"
          :placeholder="$t('views.iot.bridge.index.525282-45')"
          type="password"
          style="width: 100%"
          @input="syncConfigJson"
        />
      </el-form-item>
      <el-form-item :label="$t('views.iot.bridge.index.525282-53')" prop="dataBaseName">
        <el-input
          v-model="curNode.data.dbform.dataBaseName"
          :placeholder="$t('views.iot.bridge.index.525282-54')"
          style="width: 100%"
          @input="syncConfigJson"
        />
      </el-form-item>
      <el-form-item label="SQL" prop="sql">
        <el-input
          v-model="curNode.data.dbform.sql"
          type="textarea"
          :placeholder="$t('views.iot.bridge.index.525282-55')"
          :rows="3"
          style="width: 100%"
          :autosize="{ minRows: 3, maxRows: 5 }"
          @input="syncConfigJson"
        />
      </el-form-item>
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
import { View, QuestionFilled } from '@element-plus/icons-vue';
import { listBridge } from '@/api/iot/bridge';
import debugLog from '../debug-log.vue';

const { t } = useI18n();
const route = useRoute();
const ruleEditorStore = useRuleEditorStore();
const { curNode } = storeToRefs(ruleEditorStore);
const { database_type, bridging_direction } = useDict('database_type', 'bridging_direction');

const debugLogRef = ref();
const dataBaseBridgeList = ref<any[]>([]);
const dbOptions = ['MySQL', 'SQLServer', 'Oracle', 'PostgreSQL'];
const debug = ref(0);

const defaultDbform = () => ({
  type: '',
  databaseSource: '',
  host: '',
  username: '',
  password: '',
  dataBaseName: '',
  sql: '',
});

onMounted(() => {
  getDataBridgetList();
  if (!curNode.value.data.dbform) {
    curNode.value.data.dbform = defaultDbform();
    curNode.value.data = { ...curNode.value.data, direction: null, name: null, enable: 1, debug: 0 };
  }
  debug.value = curNode.value.data.debug || 0;
  syncConfigJson();
});

const getDataBridgetList = () => {
  listBridge({ pageNum: 1, pageSize: 999, type: 5 }).then((res: any) => {
    if (res.code === 200) dataBaseBridgeList.value = res.rows;
  });
};

const showDebugLog = () => {
  debugLogRef.value.openLog = true;
  debugLogRef.value.handleViewLog(Number(route.query.id), curNode.value.id);
};

const syncConfigJson = () => {
  if (!curNode.value.data.dbform) curNode.value.data.dbform = defaultDbform();
  curNode.value.data.debug = debug.value;
  curNode.value.data.configJson = JSON.stringify(curNode.value.data.dbform);
};

const reset = () => {
  curNode.value.data = { ...curNode.value.data, direction: null, name: null, enable: 1, debug: 0 };
  curNode.value.data.dbform = defaultDbform();
  debug.value = 0;
  syncConfigJson();
};

const changeDirection = () => {
  curNode.value.data.dbform = defaultDbform();
  syncConfigJson();
};

const changeBridge = (bridgeId: any) => {
  if (!bridgeId) {
    reset();
    curNode.value.name = null;
    return;
  }
  const selectedItem = dataBaseBridgeList.value.find((item: any) => item.id === bridgeId);
  if (selectedItem) {
    curNode.value.name = selectedItem.name;
    const dbform = JSON.parse(selectedItem.configJson || '{}');
    curNode.value.data = {
      id: selectedItem.id,
      name: selectedItem.name,
      enable: Number(selectedItem.enable) || 1,
      status: selectedItem.status,
      type: selectedItem.type,
      direction: selectedItem.direction,
      route: selectedItem.route || '',
      debug: Number(debug.value),
      dbform: dbform,
      configJson: selectedItem.configJson || JSON.stringify(dbform),
    };
    syncConfigJson();
  }
};
</script>

<style lang="scss" scoped>
.flow-data-bridge {
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
