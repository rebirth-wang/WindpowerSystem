<template>
  <div class="flow-dev-execute">
    <el-form :model="curNode.data" label-width="68px" size="small">
      <el-form-item :label="$t('ruleengine.editor.index.807357-18')">
        <el-switch v-model="curNode.data.debug" :active-value="1" :inactive-value="0"></el-switch>
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
      <el-form-item :label="$t('ruleengine.editor.components.form.flow-dev-excute.807357-0')">
        <el-select
          style="width: 100%"
          v-model="curNode.data.deviceId"
          :placeholder="$t('pleaseSelect')"
          filterable
          @change="handleDeviceChange"
        >
          <el-option
            v-for="item in deviceList"
            :key="item.serialNumber"
            :label="item.deviceName"
            :value="item.serialNumber"
          >
            <span style="float: left">{{ item.deviceName }}</span>
            <span style="float: right">
              <dict-tag :options="iot_device_status" size="small" :value="item.status" />
            </span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('ruleengine.editor.components.form.flow-dev-excute.807357-1')">
        <el-select
          style="width: 100%"
          v-model="curNode.data.type"
          :placeholder="$t('pleaseSelect')"
          @change="handleTypeChange"
        >
          <el-option
            v-for="dict in trigger_type"
            :key="dict.value + 'type'"
            :label="dict.label"
            :value="Number(dict.value)"
            :disabled="['3', '5', '6'].includes(dict.value)"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        :label="$t('ruleengine.editor.components.form.flow-dev-excute.807357-2')"
        v-if="curNode.data.type === 1"
      >
        <el-select
          style="width: 100%"
          v-model="curNode.data.modelId"
          :placeholder="$t('pleaseSelect')"
          filterable
          @change="handleModelChange"
        >
          <el-option
            v-for="(item, index) in thingsModel.properties"
            :key="index + 'property'"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        :label="$t('ruleengine.editor.components.form.flow-dev-excute.807357-2')"
        v-if="curNode.data.type === 2"
      >
        <el-select
          style="width: 100%"
          v-model="curNode.data.modelId"
          :placeholder="$t('pleaseSelect')"
          filterable
          @change="handleModelChange"
        >
          <el-option
            v-for="(item, index) in thingsModel.functions"
            :key="index + 'functions'"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        :label="$t('ruleengine.editor.components.form.flow-dev-excute.807357-3')"
        v-if="curNode.data.modelId"
      >
        <el-input
          v-if="
            Object.keys(modelVale).length !== 0 &&
            (modelVale.datatype.type === 'integer' || modelVale.datatype.type === 'decimal')
          "
          v-model="curNode.data.value"
          :placeholder="$t('scene.index.670805-21')"
          :max="modelVale.datatype.max"
          :min="modelVale.datatype.min"
          type="number"
        >
          <template v-if="modelVale.datatype.unit" #append>{{ modelVale.datatype.unit }}</template>
        </el-input>
        <el-switch
          v-if="Object.keys(modelVale).length !== 0 && modelVale.datatype.type === 'bool'"
          v-model="curNode.data.value"
          :active-text="modelVale.datatype.trueText"
          :inactive-text="modelVale.datatype.falseText"
          active-value="1"
          inactive-value="0"
        ></el-switch>
        <el-select
          v-if="Object.keys(modelVale).length !== 0 && modelVale.datatype.type === 'enum'"
          v-model="curNode.data.value"
          :placeholder="$t('pleaseSelect')"
          style="width: 100%"
        >
          <el-option
            v-for="(item, index) in modelVale.datatype.enumList"
            :key="index + 'things'"
            :label="item.text"
            :value="item.value"
          ></el-option>
        </el-select>
        <el-input
          v-if="Object.keys(modelVale).length !== 0 && modelVale.datatype.type === 'string'"
          v-model="curNode.data.value"
          :placeholder="$t('scene.index.670805-22')"
          :max="modelVale.datatype.maxLength"
        />
      </el-form-item>
    </el-form>
    <!-- 调试日志组件 -->
    <debug-log :log-list="curNode.data.debugLog" ref="debugLogRef"></debug-log>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { useRuleEditorStore } from '@/stores/modules/ruleEditor';
import { storeToRefs } from 'pinia';
import { useDict } from '@/utils/dict/useDict';
import { View } from '@element-plus/icons-vue';
import { listDeviceShort } from '@/api/iot/device';
import { cacheJsonThingsModel } from '@/api/iot/model';
import debugLog from '../debug-log.vue';

const { t } = useI18n();
const route = useRoute();
const ruleEditorStore = useRuleEditorStore();
const { curNode } = storeToRefs(ruleEditorStore);
const { iot_device_status, trigger_type } = useDict('iot_device_status', 'trigger_type');

const debugLogRef = ref();
const deviceList = ref<any[]>([]);
const thingsModel = reactive<any>({});
const modelVale = ref<any>({});

onMounted(() => {
  getDeviceList();
});

// 获取设备列表
const getDeviceList = () => {
  const params = { pageNum: 1, pageSize: 999999 };
  listDeviceShort(params).then((res: any) => {
    if (res.code === 200) {
      deviceList.value = res.rows;
      const { deviceId } = curNode.value.data || {};
      if (deviceId) {
        getThingsModel(deviceId);
      }
    }
  });
};

// 设备选择
const handleDeviceChange = (val: any) => {
  curNode.value.data = { ...curNode.value.data, type: null, modelId: '' };
  getThingsModel(val);
};

// 获取物模型
const getThingsModel = (serialNum: any) => {
  const device = deviceList.value.find((item: any) => item.serialNumber === serialNum) || {};
  cacheJsonThingsModel(device.productId).then((res: any) => {
    if (res.code === 200) {
      const parsed = JSON.parse(res.data);
      // 过滤监测数据和只读数据
      if (parsed.properties) {
        parsed.properties = parsed.properties.filter((item: any) => item.isMonitor == 0 && item.isReadonly == 0);
        for (let i = 0; i < parsed.properties.length; i++) {
          if (parsed.properties[i].datatype.params) {
            parsed.properties[i].datatype.params = parsed.properties[i].datatype.params.filter(
              (item: any) => item.isReadonly == 0
            );
          }
        }
      }
      if (parsed.functions) {
        parsed.functions = parsed.functions.filter((item: any) => item.isReadonly == 0);
        for (let i = 0; i < parsed.functions.length; i++) {
          if (parsed.functions[i].datatype.params) {
            parsed.functions[i].datatype.params = parsed.functions[i].datatype.params.filter(
              (item: any) => item.isReadonly == 0
            );
          }
        }
      }
      Object.assign(thingsModel, parsed);
      // 默认有时
      const { modelId, type } = curNode.value.data;
      if (modelId) {
        let models: any[] = [];
        if (type === 1) models = thingsModel.properties;
        if (type === 2) models = thingsModel.functions;
        for (let i = 0; i < models.length; i++) {
          if (models[i].id == modelId) {
            modelVale.value = models[i];
          }
        }
      }
    }
  });
};

// 属性类型选择
const handleTypeChange = () => {
  curNode.value.data = { ...curNode.value.data, modelId: '' };
};

// 物模型选择
const handleModelChange = (val: any) => {
  let models: any[] = [];
  if (curNode.value.data.type === 1) models = thingsModel.properties;
  if (curNode.value.data.type === 2) models = thingsModel.functions;
  for (let i = 0; i < models.length; i++) {
    if (models[i].id === val) {
      modelVale.value = models[i];
    }
  }
  curNode.value.data = { ...curNode.value.data, value: '' };
};

// 查看日志
const showDebugLog = () => {
  debugLogRef.value.openLog = true;
  const id = Number(route.query.id);
  const curNodeId = curNode.value.id;
  debugLogRef.value.handleViewLog(id, curNodeId);
};
</script>

<style lang="scss" scoped>
.flow-dev-execute {
  width: 100%;
  padding: 0 10px;
}
</style>
