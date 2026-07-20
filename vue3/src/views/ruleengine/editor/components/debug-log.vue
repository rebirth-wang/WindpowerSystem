<template>
  <div class="panel-wrap">
    <el-dialog
      :title="title"
      v-model="openLog"
      width="800px"
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div
        ref="logContainer"
        v-loading="logLoading"
        :element-loading-text="$t('script.349087-39')"
        style="
          border: 1px solid #ccc;
          border-radius: 4px;
          height: 480px;
          background-color: #181818;
          color: #fff;
          padding: 10px;
          line-height: 20px;
          overflow: auto;
        "
      >
        <pre style="white-space: pre-wrap; word-wrap: break-word" v-if="stepMsg">{{ stepMsg }}</pre>
        <el-empty v-if="!stepMsg" :description="$t('ruleengine.editor.components.debugLog.807357-0')" />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelLog">{{ $t('script.349087-38') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import { getRuleElComponentLog } from '@/api/rule/el.js';

const { t } = useI18n();

const openLog = ref(false);
const title = ref(t('script.349087-40'));
const logLoading = ref(false);
const stepMsg = ref('');
const logList = ref<any[]>([]);
const logContainer = ref<HTMLElement>();

/** 查看日志 */
const handleViewLog = (id: number, curNodeId: string) => {
  logLoading.value = true;
  getRuleElComponentLog(id, curNodeId).then((res: any) => {
    if (res.code === 200) {
      logList.value = res.data;
      let formattedText = '';

      logList.value.forEach((item: any, index: number) => {
        let stepArr: any[] = [];
        try {
          stepArr = JSON.parse(item.stepMsg);
        } catch (err) {
          console.error(`解析stepMsg失败（第${index + 1}条记录）`, err);
          return;
        }

        formattedText += `===== 第${index + 1}条记录（id: ${item.id}） =====\n`;

        stepArr.forEach((step: any, stepIndex: number) => {
          formattedText += `${stepIndex + 1}\n`;
          formattedText += `开始时间：${step.startTime || '无'}\n`;
          formattedText += `错误信息：${step.error || '无'}\n`;
          formattedText += `结束时间：${step.endTime || '无'}\n\n`;
        });

        formattedText += '\n';
      });

      stepMsg.value = formattedText;
      openLog.value = true;
      title.value = t('script.349087-40');
      logLoading.value = false;
      nextTick(() => {
        if (logContainer.value) {
          logContainer.value.scroll({ top: logContainer.value.scrollHeight });
        }
      });
    }
  });
};

/** 取消日志按钮 */
const cancelLog = () => {
  stepMsg.value = '';
  openLog.value = false;
};

defineExpose({ openLog, handleViewLog });
</script>
