<template>
  <div class="flow-scheduled-trigger">
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
      <el-form-item :label="$t('ruleengine.editor.components.form.flow-scheduled-trigger.807357-0')">
        <!--定时-->
        <el-row :gutter="10">
          <el-col :span="10">
            <el-time-picker
              style="width: 100%; margin-bottom: 10px"
              v-model="curNode.data.timerTimeValue"
              size="small"
              value-format="HH:mm"
              format="HH:mm"
              :placeholder="$t('device.device-timer.433369-19')"
              @change="timeChange($event)"
              :disabled="curNode.data.isAdvance == 1"
            ></el-time-picker>
          </el-col>
          <el-col :span="12">
            <el-select
              v-model="curNode.data.timerWeekValue"
              :placeholder="$t('pleaseSelect')"
              multiple
              style="width: 100%"
              @change="weekChange($event)"
              size="small"
              :disabled="curNode.data.isAdvance == 1"
            >
              <el-option
                v-for="dict in variable_operation_week"
                :key="dict.value"
                :label="dict.label"
                :value="Number(dict.value)"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>

        <el-checkbox
          v-model="curNode.data.isAdvance"
          :true-value="1"
          :false-value="0"
          @change="customerCronChange($event)"
          size="small"
          style="width: 100%"
        >
          {{ $t('scene.index.670805-15') }}
        </el-checkbox>
        <el-input
          v-model="curNode.data.cronExpression"
          :placeholder="$t('scene.index.670805-16')"
          :disabled="curNode.data.isAdvance == 0"
          size="small"
        >
          <template #append>
            <el-button type="primary" @click="handleShowCron(curNode.data)" :disabled="curNode.data.isAdvance == 0">
              {{ $t('scene.index.670805-17') }}
            </el-button>
          </template>
        </el-input>
      </el-form-item>
    </el-form>
    <!-- CRON表达式生成器 -->
    <el-dialog
      :title="$t('scene.index.670805-38')"
      v-model="openCron"
      append-to-body
      destroy-on-close
      class="scrollbar"
    >
      <crontab
        @hide="openCron = false"
        @fill="crontabFill"
        :expression="expression"
        style="padding-bottom: 10px"
      ></crontab>
    </el-dialog>
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
import { useDict } from '@/utils/dict/useDict';
import { View } from '@element-plus/icons-vue';
import Crontab from '@/components/Crontab/index.vue';
import debugLog from '../debug-log.vue';

const { t } = useI18n();
const route = useRoute();
const ruleEditorStore = useRuleEditorStore();
const { curNode } = storeToRefs(ruleEditorStore);
const { variable_operation_week } = useDict('variable_operation_week');

const debugLogRef = ref();
const openCron = ref(false);
const expression = ref('');

onMounted(() => {
  initDefaultValues();
});

const initDefaultValues = () => {
  if (!curNode.value.data) {
    curNode.value.data = {};
  }
  if (curNode.value.data.isAdvance === undefined || curNode.value.data.isAdvance === null) {
    curNode.value.data.isAdvance = 0;
  }
};

/** 确定后回传值 */
const crontabFill = (value: string) => {
  curNode.value.data.cronExpression = value;
};

/** 星期改变事件 **/
const weekChange = (_data: any) => {
  gentCronExpression();
};

/** 时间改变事件 **/
const timeChange = (_data: any) => {
  gentCronExpression();
};

/**自定义cron表达式选项改变事件 */
const customerCronChange = () => {};

/** 生成cron表达式**/
const gentCronExpression = () => {
  let hour = '00';
  let minute = '00';
  if (curNode.value.data.timerTimeValue != null && curNode.value.data.timerTimeValue != '') {
    hour = curNode.value.data.timerTimeValue.substring(0, 2);
    minute = curNode.value.data.timerTimeValue.substring(3);
  }
  let week: any = '*';
  if (curNode.value.data.timerWeekValue && curNode.value.data.timerWeekValue.length > 0) {
    let order = curNode.value.data.timerWeekValue.slice().sort();
    for (let i = 0; i < order.length; i++) {
      if (order[i] != 7) {
        order[i] = order[i] + 1;
      } else {
        order[i] = 1;
      }
    }
    week = order;
  }
  curNode.value.data.cronExpression = '0 ' + minute + ' ' + hour + ' ? * ' + week;
};

/** cron表达式按钮操作 */
const handleShowCron = (item: any) => {
  expression.value = item.cronExpression;
  openCron.value = true;
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
.flow-scheduled-trigger {
  width: 100%;
  padding: 0 10px;
}
</style>
