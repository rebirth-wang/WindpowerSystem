<template>
  <div class="go-chart-data-display">
    <n-scrollbar style="max-height: 570px">
      <div class="go-mr-3">
        <div>
          <setting-item-box :name="t('visualBigScreen.management-674210-881')">
            <setting-item :name="t('visualBigScreen.management-674210-882')">
              <n-input
                size="small"
                :placeholder="targetData?.dataPondName || t('visualBigScreen.management-674210-692')"
                :disabled="true"
              >
              </n-input>
            </setting-item>
            <setting-item :name="t('visualBigScreen.management-674210-883')">
              <n-input
                size="small"
                :placeholder="requestHttpType || t('visualBigScreen.management-674210-692')"
                :disabled="true"
              ></n-input>
            </setting-item>
          </setting-item-box>

          <setting-item-box>
            <setting-item :name="t('visualBigScreen.management-674210-824')">
              <n-input
                size="small"
                :placeholder="`${requestInterval || t('visualBigScreen.management-674210-692')}`"
                :disabled="true"
              >
                <template #suffix>
                  {{ targetData && SelectHttpTimeNameObj[requestIntervalUnit] }}
                </template>
              </n-input>
            </setting-item>
            <setting-item :name="t('visualBigScreen.management-674210-825')">
              <n-input
                size="small"
                :placeholder="`${globalData?.requestInterval || t('visualBigScreen.management-674210-692')}`"
                :disabled="true"
              >
                <template #suffix> {{ globalData && SelectHttpTimeNameObj[globalData.requestIntervalUnit] }} </template>
              </n-input>
            </setting-item>
          </setting-item-box>

          <setting-item-box :name="t('visualBigScreen.management-674210-826')" :alone="true">
            <n-input
              size="small"
              :placeholder="globalData?.requestOriginUrl || t('visualBigScreen.management-674210-692')"
              :disabled="true"
            >
              <template #prefix>
                <n-icon :component="PulseIcon" />
              </template>
            </n-input>
          </setting-item-box>

          <setting-item-box :name="t('visualBigScreen.management-674210-796')" :alone="true">
            <n-input
              size="small"
              :placeholder="requestUrl || t('visualBigScreen.management-674210-692')"
              :disabled="true"
            >
              <template #prefix>
                <n-icon :component="FlashIcon" />
              </template>
            </n-input>
          </setting-item-box>
        </div>
        <n-divider />
        <setting-item-box :name="t('visualBigScreen.management-674210-268')">
          <setting-item :name="t('visualBigScreen.management-674210-884')">
            <n-input size="small" :placeholder="requestContentTypeText" :disabled="true"></n-input>
          </setting-item>
          <setting-item
            :name="t('visualBigScreen.management-674210-887')"
            v-if="requestContentType === RequestContentTypeEnum.DEFAULT"
          >
            <n-input size="small" :placeholder="targetData && requestParamsBodyType" :disabled="true"></n-input>
          </setting-item>
        </setting-item-box>
        <div v-if="requestContentType === RequestContentTypeEnum.DEFAULT">
          <n-tabs type="line" animated v-model:value="tabValue">
            <n-tab
              v-for="item in RequestParamsTypeEnum"
              :key="item"
              :name="item"
              :tab="t(RequestParamsTypeLangKey[item])"
            >
              {{ t(RequestParamsTypeLangKey[item]) }}
            </n-tab>
          </n-tabs>
          <!-- 各个页面 -->
          <div class="go-mt-3">
            <div v-if="tabValue !== RequestParamsTypeEnum.BODY">
              <display-table class="go-my-3" :target="requestParams[tabValue]"> </display-table>
            </div>

            <!-- 选择了 body -->
            <div v-else>
              <!-- 为 none 时 -->
              <n-card class="go-mt-3 go-pb-3" v-if="requestParamsBodyType === RequestBodyEnum['NONE']">
                <n-text depth="3">{{ t('visualBigScreen.management-674210-888') }}</n-text>
              </n-card>

              <!-- 具有对象属性时 -->
              <template
                v-else-if="
                  requestParamsBodyType === RequestBodyEnum['FORM_DATA'] ||
                  requestParamsBodyType === RequestBodyEnum['X_WWW_FORM_URLENCODED']
                "
              >
                <display-table
                  class="go-my-3"
                  :target="requestParams[RequestParamsTypeEnum.BODY][requestParamsBodyType]"
                ></display-table>
              </template>

              <!-- json  -->
              <template v-else-if="requestParamsBodyType === RequestBodyEnum['JSON']">
                <n-card size="small" style="padding-bottom: 7px">
                  <n-code
                    :code="
                      requestParams[RequestParamsTypeEnum.BODY][requestParamsBodyType] ||
                      t('visualBigScreen.management-674210-652')
                    "
                    language="json"
                  ></n-code>
                </n-card>
              </template>

              <!-- xml  -->
              <template v-else-if="requestParamsBodyType === RequestBodyEnum['XML']">
                <n-code
                  :code="requestParams[RequestParamsTypeEnum.BODY][requestParamsBodyType] || ''"
                  language="html"
                ></n-code>
              </template>
            </div>
          </div>
        </div>
        <!-- SQL 请求 -->
        <div v-else>
          <setting-item-box :name="t('visualBigScreen.management-674210-889')">
            <n-text>sql</n-text>
          </setting-item-box>
          <setting-item-box :name="t('visualBigScreen.management-674210-890')">
            <n-code :code="requestSQLContent.sql || ''" language="sql"></n-code>
          </setting-item-box>
        </div>
      </div>
    </n-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed, PropType, ref, toRefs } from 'vue'
import { useI18n } from 'vue-i18n'
import { icon } from '@vb/plugins/icon'
import { SettingItemBox, SettingItem } from '@vb/components/Pages/ChartItemSetting'
import { RequestDataPondItemType, RequestGlobalConfigType } from '@vb/store/modules/chartEditStore/chartEditStore.d'
import displayTable from './displayTable.vue'
import {
  RequestBodyEnum,
  RequestParamsTypeEnum,
  SelectHttpTimeNameObj,
  RequestContentTypeEnum
} from '@vb/enums/httpEnum'
import { RequestContentTypeLangKey, RequestParamsTypeLangKey } from '../../../../index.d'

const props = defineProps({
  globalData: Object as PropType<RequestGlobalConfigType>,
  targetData: Object as PropType<RequestDataPondItemType>
})

const { FlashIcon, PulseIcon } = icon.ionicons5
const { t } = useI18n()
const {
  requestUrl,
  requestInterval,
  requestHttpType,
  requestContentType,
  requestSQLContent,
  requestParams,
  requestParamsBodyType,
  requestIntervalUnit
} = toRefs((props.targetData as RequestDataPondItemType).dataPondRequestConfig)

const tabValue = ref<RequestParamsTypeEnum>(RequestParamsTypeEnum.PARAMS)
const requestContentTypeText = computed(() =>
  requestContentType.value !== undefined ? t(RequestContentTypeLangKey[requestContentType.value]) : ''
)
</script>

<style lang="scss" scoped>
@include go('chart-data-display') {
  flex: 1;
}
</style>
