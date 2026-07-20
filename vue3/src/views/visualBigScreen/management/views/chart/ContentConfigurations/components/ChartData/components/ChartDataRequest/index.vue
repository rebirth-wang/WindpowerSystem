<template>
  <n-modal
    class="go-chart-data-request"
    v-model:show="modelShowRef"
    :mask-closable="false"
    :closeOnEsc="true"
    :onEsc="onEsc"
  >
    <n-card :bordered="false" role="dialog" size="small" aria-modal="true" style="width: 1000px; height: 800px">
      <template #header></template>
      <template #header-extra></template>
      <n-scrollbar style="max-height: 718px">
        <div class="go-pr-3">
          <n-space vertical>
            <request-global-config></request-global-config>
            <request-target-config :target-data-request="targetData?.request"></request-target-config>
          </n-space>
        </div>
      </n-scrollbar>
      <!-- 底部 -->
      <template #action>
        <n-space justify="space-between">
          <div>
            <n-text>「 {{ chartConfig.categoryName }} 」</n-text>
            <n-text>——&nbsp;</n-text>
            <n-tag type="primary" :bordered="false" style="border-radius: 5px">
              {{ requestContentTypeText }}
            </n-tag>
          </div>
          <div>
            <n-button class="go-mr-3" @click="closeHandle">{{ t('visualBigScreen.management-674210-111') }}</n-button>
            <n-button type="primary" @click="closeAndSendHandle">
              {{ saveBtnText || t('visualBigScreen.management-674210-809') }}
            </n-button>
          </div>
        </n-space>
      </template>
    </n-card>
  </n-modal>
</template>

<script script lang="ts" setup>
import { computed, ref, toRefs, PropType, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { RequestGlobalConfig } from './components/RequestGlobalConfig';
import { RequestTargetConfig } from './components/RequestTargetConfig';
import { useSync } from '@vb/views/chart/hooks/useSync.hook';
import { CreateComponentType } from '@vb/packages/index.d';
import { RequestContentTypeLangKey } from '../../index.d';

const props = defineProps({
  modelShow: Boolean,
  targetData: Object as PropType<CreateComponentType>,
  saveBtnText: String || null,
});
const emit = defineEmits(['update:modelShow', 'sendHandle']);

const { dataSyncUpdate } = useSync();
const { t } = useI18n();

// 解构基础配置
const { chartConfig } = toRefs(props.targetData as CreateComponentType);
const { requestContentType } = toRefs((props.targetData as CreateComponentType).request);
const modelShowRef = ref(false);
const requestContentTypeText = computed(() => t(RequestContentTypeLangKey[requestContentType.value]));

watch(
  () => props.modelShow,
  (newValue) => {
    modelShowRef.value = newValue;
  },
  {
    immediate: true,
  }
);

const closeHandle = () => {
  emit('update:modelShow', false);
};

const closeAndSendHandle = () => {
  emit('update:modelShow', false);
  emit('sendHandle');
  dataSyncUpdate();
};

const onEsc = () => {
  closeHandle();
};
</script>

<style lang="scss" scoped>
@include go('chart-data-request') {
  &.n-card.n-modal,
  .n-card {
    @extend .go-background-filter;
  }
  .n-card-shallow {
    background-color: rgba(0, 0, 0, 0) !important;
  }
  @include deep() {
    .n-card-content {
      padding-bottom: 5px;
    }
  }
}
</style>
