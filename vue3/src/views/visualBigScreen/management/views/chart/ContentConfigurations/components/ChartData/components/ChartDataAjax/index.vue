<template>
  <div class="go-chart-configurations-data-ajax">
    <n-card class="n-card-shallow">
      <setting-item-box :name="t('visualBigScreen.management-674210-820')">
        <setting-item :name="t('visualBigScreen.management-674210-268')">
          <n-tag :bordered="false" type="primary" style="border-radius: 5px">
            {{ requestContentTypeText }}
          </n-tag>
        </setting-item>

        <setting-item :name="t('visualBigScreen.management-674210-823')">
          <n-input
            size="small"
            :placeholder="targetData.request.requestHttpType || t('visualBigScreen.management-674210-692')"
            :disabled="true"
          ></n-input>
        </setting-item>

        <setting-item :name="t('visualBigScreen.management-674210-824')">
          <n-input
            size="small"
            :placeholder="`${targetData.request.requestInterval || t('visualBigScreen.management-674210-692')}`"
            :disabled="true"
          >
            <template #suffix>{{ requestIntervalLabelMap[targetData.request.requestIntervalUnit] }}</template>
          </n-input>
        </setting-item>

        <setting-item :name="t('visualBigScreen.management-674210-825')">
          <n-input
            size="small"
            :placeholder="`${GlobalRequestInterval || t('visualBigScreen.management-674210-692')} `"
            :disabled="true"
          >
            <template #suffix>{{ requestIntervalLabelMap[GlobalRequestIntervalUnit] }}</template>
          </n-input>
        </setting-item>
      </setting-item-box>

      <setting-item-box :name="t('visualBigScreen.management-674210-826')" :alone="true">
        <n-input
          size="small"
          :placeholder="requestOriginUrl || t('visualBigScreen.management-674210-692')"
          :disabled="true"
        >
          <template #prefix>
            <n-icon :component="PulseIcon" />
          </template>
        </n-input>
      </setting-item-box>

      <setting-item-box :name="t('visualBigScreen.management-674210-827')" :alone="true">
        <n-input
          size="small"
          :placeholder="targetData.request.requestUrl || t('visualBigScreen.management-674210-692')"
          :disabled="true"
        >
          <template #prefix>
            <n-icon :component="FlashIcon" />
          </template>
        </n-input>
      </setting-item-box>

      <div class="edit-text" @click="requestModelHandle">
        <div class="go-absolute-center">
          <n-button type="primary" secondary>{{ t('visualBigScreen.management-674210-797') }}</n-button>
        </div>
      </div>
    </n-card>

    <setting-item-box :alone="true">
      <template #name>
        {{ t('visualBigScreen.management-674210-798') }}
        <n-tooltip trigger="hover">
          <template #trigger>
            <n-icon size="21" :depth="3">
              <help-outline-icon></help-outline-icon>
            </n-icon>
          </template>
          {{ t('visualBigScreen.management-674210-799') }}
        </n-tooltip>
      </template>
      <n-button type="primary" ghost @click="sendHandle">
        <template #icon>
          <n-icon>
            <flash-icon />
          </n-icon>
        </template>
        {{ t('visualBigScreen.management-674210-800') }}
      </n-button>
    </setting-item-box>

    <!-- 底部数据展示 -->
    <chart-data-matching-and-show :show="showMatching && !loading" :ajax="true"></chart-data-matching-and-show>

    <!-- 骨架图 -->
    <go-skeleton :load="loading" :repeat="3"></go-skeleton>

    <!-- 请求配置model -->
    <chart-data-request
      v-model:modelShow="requestShow"
      :targetData="targetData"
      @sendHandle="sendHandle"
    ></chart-data-request>
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs, computed, onBeforeUnmount, watchEffect, toRaw } from 'vue';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { icon } from '@vb/plugins/icon';
import { useDesignStore } from '@vb/store/modules/designStore/designStore';
import { SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting';
import { ChartDataRequest } from '../ChartDataRequest/index';
import { RequestHttpIntervalEnum, RequestContentTypeEnum } from '@vb/enums/httpEnum';
import { customizeHttp } from '@vb/api/http';
import { ChartDataMatchingAndShow } from '../ChartDataMatchingAndShow';
import { useTargetData } from '../../../hooks/useTargetData.hook';
import { newFunctionHandle } from '@vb/utils';

const { HelpOutlineIcon, FlashIcon, PulseIcon } = icon.ionicons5;
const { targetData, chartEditStore } = useTargetData();
const { t } = useI18n();

const {
  requestOriginUrl,
  requestInterval: GlobalRequestInterval,
  requestIntervalUnit: GlobalRequestIntervalUnit,
} = toRefs(chartEditStore.getRequestGlobalConfig);
const designStore = useDesignStore();

// 是否展示数据分析
const loading = ref(false);
const requestShow = ref(false);
const showMatching = ref(false);

let firstFocus = 0;
let lastFilter: any = undefined;

const requestContentTypeText = computed(() => {
  return targetData.value.request.requestContentType === RequestContentTypeEnum.DEFAULT
    ? t('visualBigScreen.management-674210-821')
    : t('visualBigScreen.management-674210-822');
});

const requestIntervalLabelMap = computed(() => ({
  [RequestHttpIntervalEnum.SECOND]: t('visualBigScreen.management-674210-429'),
  [RequestHttpIntervalEnum.MINUTE]: t('visualBigScreen.management-674210-428'),
  [RequestHttpIntervalEnum.HOUR]: t('visualBigScreen.management-674210-427'),
  [RequestHttpIntervalEnum.DAY]: t('visualBigScreen.management-674210-423'),
}));

// 请求配置 model
const requestModelHandle = () => {
  requestShow.value = true;
};

// 发送请求
const sendHandle = async () => {
  if (!targetData.value?.request) return;
  loading.value = true;
  try {
    const res = await customizeHttp(toRaw(targetData.value.request), toRaw(chartEditStore.getRequestGlobalConfig));
    loading.value = false;
    if (res) {
      const { data } = res;
      if (!data && !targetData.value.filter) {
        ElMessage.warning(t('visualBigScreen.management-674210-802'));
        showMatching.value = true;
        return;
      }
      targetData.value.option.dataset = newFunctionHandle(data, res, targetData.value.filter);
      showMatching.value = true;
      return;
    }
    ElMessage.warning(t('visualBigScreen.management-674210-789'));
  } catch (error) {
    console.error(error);
    loading.value = false;
    ElMessage.warning(t('visualBigScreen.management-674210-790'));
  }
};

// 颜色
const themeColor = computed(() => {
  return designStore.getAppTheme;
});

watchEffect(() => {
  const filter = targetData.value?.filter;
  if (lastFilter !== filter && firstFocus) {
    lastFilter = filter;
    sendHandle();
  }
  firstFocus++;
});

onBeforeUnmount(() => {
  lastFilter = null;
});
</script>

<style lang="scss" scoped>
@include go('chart-configurations-data-ajax') {
  .n-card-shallow {
    &.n-card {
      @extend .go-background-filter;
      :deep(.n-card-content) {
        padding: 10px;
      }
    }
    .edit-text {
      position: absolute;
      top: 0px;
      left: 0px;
      width: 325px;
      height: 270px;
      cursor: pointer;
      opacity: 0;
      transition: all 0.3s;
      @extend .go-background-filter;
      backdrop-filter: blur(2px) !important;
    }
    &:hover {
      border-color: v-bind('themeColor');
      .edit-text {
        opacity: 1;
      }
    }
  }
}
</style>
