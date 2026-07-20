<template>
  <div class="alarm-wrap">
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
      <el-form-item :label="$t('ruleengine.editor.components.form.flow-alarm.807357-0')">
        <el-select
          style="width: 100%"
          v-model="curNode.data.source"
          :placeholder="$t('ruleengine.editor.components.form.flow-alarm.807357-1')"
        >
          <el-option v-for="item in actionList" :key="item.id" :label="item.label" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        :label="$t('ruleengine.editor.components.form.flow-alarm.807357-2')"
        v-if="curNode.data.source === 4"
      >
        <el-input
          style="width: 100%"
          type="number"
          v-model.number="curNode.data.notifyCount"
          :placeholder="$t('ruleengine.editor.components.form.flow-alarm.807357-3')"
        ></el-input>
      </el-form-item>
      <el-form-item
        :label="$t('ruleengine.editor.components.form.flow-alarm.807357-4')"
        v-if="curNode.data.source === 5"
      >
        <el-select
          style="width: 100%"
          v-model="curNode.data.id"
          :placeholder="$t('ruleengine.editor.components.form.flow-alarm.807357-1')"
        >
          <el-option
            v-for="item in sceneList"
            :key="item.sceneId"
            :label="item.sceneName"
            :value="item.sceneId"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <!-- 调试日志组件 -->
    <debug-log :log-list="curNode.data.debugLog" ref="debugLogRef"></debug-log>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { useRuleEditorStore } from '@/stores/modules/ruleEditor';
import { storeToRefs } from 'pinia';
import { View } from '@element-plus/icons-vue';
import { listScene } from '@/api/iot/scene';
import debugLog from '../debug-log.vue';

const { t } = useI18n();
const route = useRoute();
const ruleEditorStore = useRuleEditorStore();
const { curNode } = storeToRefs(ruleEditorStore);

const debugLogRef = ref();
const sceneList = ref<any[]>([]);

const actionList = [
  { id: 4, label: t('ruleengine.editor.components.form.flow-alarm.807357-5') },
  { id: 5, label: t('ruleengine.editor.components.form.flow-alarm.807357-6') },
];

onMounted(() => {
  getListScene();
});

// 获取场景列表
const getListScene = () => {
  const params = { pageNum: 1, pageSize: 99999, hasAlert: 1 };
  listScene(params).then((res: any) => {
    sceneList.value = res.rows;
  });
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
.alarm-wrap {
  width: 100%;
  padding: 0 10px;
}
</style>
