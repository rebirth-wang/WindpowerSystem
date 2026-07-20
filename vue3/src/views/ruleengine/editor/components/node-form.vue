<template>
  <div class="node-form">
    <div class="tabs-wrap">
      <div class="tab-item" @click="handleChangeTab(0)" :class="{ 'tab-item-active': tabIndex == 0 }">
        {{ $t('ruleengine.editor.components.nodeForm.807357-0') }}
      </div>
      <div class="tab-item" @click="handleChangeTab(1)" :class="{ 'tab-item-active': tabIndex == 1 }">
        {{ $t('ruleengine.editor.components.nodeForm.807357-1') }}
      </div>
    </div>
    <div class="node-form-body">
      <div class="style-wrap" v-show="tabIndex === 0">
        <el-form
          :model="ruleEditorStore.curNode"
          label-width="80px"
          size="small"
          v-show="activeElement.type === 'node'"
        >
          <el-form-item :label="$t('ruleengine.editor.components.nodeForm.807357-2')">
            <el-input v-model="ruleEditorStore.curNode.name" clearable @input="changeNodeName"></el-input>
          </el-form-item>
          <el-form-item :label="$t('ruleengine.editor.components.nodeForm.807357-3')">
            <el-input v-model="ruleEditorStore.curNode.ico" clearable></el-input>
          </el-form-item>
          <el-form-item :label="$t('ruleengine.editor.components.nodeForm.807357-4')">
            <el-select
              style="width: 100%"
              v-model="ruleEditorStore.curNode.state"
              :placeholder="$t('ruleengine.editor.components.nodeForm.807357-5')"
            >
              <el-option
                v-for="dict in rule_visualization_triggering_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>

        <el-form
          :model="ruleEditorStore.curLine"
          label-width="60px"
          size="small"
          v-show="activeElement.type === 'line'"
        >
          <el-form-item :label="$t('ruleengine.editor.components.nodeForm.807357-2')">
            <el-input v-model="ruleEditorStore.curLine.label" clearable></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div class="property-wrap" v-show="tabIndex === 1">
        <component :key="getKey()" :is="formComponents[id]" />
        <div
          class="form-wrap"
          v-if="activeElement.type === 'node' && activeElement.isCom && ruleEditorStore.curNode.type !== 'condition'"
        >
          <el-form :model="ruleEditorStore.curNode.data" label-position="top" label-width="60px" size="small">
            <el-form-item
              :label="$t('ruleengine.editor.components.nodeForm.807357-6')"
              v-if="activeElement.isShowExecute"
            >
              <el-select
                style="width: 100%"
                v-model="ruleEditorStore.curNode.data.cond"
                :placeholder="$t('ruleengine.editor.components.nodeForm.807357-5')"
              >
                <el-option v-for="item in triggersList" :key="item.id" :label="item.label" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, markRaw } from 'vue';
import { useI18n } from 'vue-i18n';
import { useDict } from '@/utils/dict/useDict';
import { useRuleEditorStore } from '@/stores/modules/ruleEditor';
import { getUUID } from '@/utils/rule';

import FlowDevTrigger from './form/flow-dev-trigger.vue';
import FlowDevExecute from './form/flow-dev-execute.vue';
import FlowCondition from './form/flow-condition.vue';
import FlowDelay from './form/flow-delay.vue';
import FlowAlarm from './form/flow-alarm.vue';
import FlowLine from './form/flow-line.vue';
import FlowComment from './form/flow-comment.vue';
import FlowProductTrigger from './form/flow-product-trigger.vue';
import FlowProductExecute from './form/flow-product-execute.vue';
import FlowScheduledTrigger from './form/flow-scheduled-trigger.vue';
import FlowCustomTrigger from './form/flow-custom-trigger.vue';
import FlowBridge from './form/flow-bridge.vue';
import FlowScript from './form/flow-script.vue';

const { t } = useI18n();
const ruleEditorStore = useRuleEditorStore();
const { rule_visualization_triggering_status } = useDict('rule_visualization_triggering_status');

const formComponents: Record<string, any> = {
  FlowDevTrigger: markRaw(FlowDevTrigger),
  FlowDevExecute: markRaw(FlowDevExecute),
  FlowCondition: markRaw(FlowCondition),
  FlowDelay: markRaw(FlowDelay),
  FlowAlarm: markRaw(FlowAlarm),
  FlowLine: markRaw(FlowLine),
  FlowComment: markRaw(FlowComment),
  FlowProductTrigger: markRaw(FlowProductTrigger),
  FlowProductExecute: markRaw(FlowProductExecute),
  FlowScheduledTrigger: markRaw(FlowScheduledTrigger),
  FlowCustomTrigger: markRaw(FlowCustomTrigger),
  FlowBridge: markRaw(FlowBridge),
  FlowScript: markRaw(FlowScript),
};

const props = defineProps<{
  id: string;
  activeElement: any;
}>();

const tabIndex = ref(0);
const triggersList = ref([
  { id: 1, label: t('ruleengine.editor.components.nodeForm.807357-11') },
  { id: 2, label: t('ruleengine.editor.components.nodeForm.807357-12') },
]);

const handleChangeTab = (index: number) => {
  tabIndex.value = index;
};

const getKey = () => {
  return `id-${getUUID()}`;
};

const changeNodeName = (val: string) => {
  if (ruleEditorStore.curNode.data) {
    ruleEditorStore.curNode.data.name = val;
  }
};
</script>

<style lang="scss" scoped>
.node-form {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;

  .tabs-wrap {
    height: 44px;
    display: flex;
    border-bottom: 1px solid #dcdfe6;

    .tab-item {
      flex: 1;
      height: 44px;
      text-align: center;
      line-height: 44px;
      color: #323232;
      font-size: 14px;
      font-weight: 500;
    }

    .tab-item:hover {
      cursor: pointer;
    }

    .tab-item-active {
      color: #486ff2;
      border-bottom: #486ff2 solid 2px;
    }
  }

  .node-form-body {
    flex: 1;
    overflow-y: auto;
    padding: 20px 15px 0 0;

    .style-wrap {
      height: 100%;
    }

    .property-wrap {
      height: 100%;
      width: 100%;

      .form-wrap {
        margin-top: 40px;
        margin-left: 20px;
      }
    }
  }
}
</style>
