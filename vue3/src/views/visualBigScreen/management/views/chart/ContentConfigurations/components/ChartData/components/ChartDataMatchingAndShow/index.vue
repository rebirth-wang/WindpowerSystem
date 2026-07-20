<template>
  <n-timeline class="go-chart-configurations-timeline">
    <!-- 处理 echarts 的数据映射 -->
    <n-timeline-item
      v-if="isCharts && dimensionsAndSource"
      type="info"
      :title="timelineTitleMap[TimelineTitleEnum.MAPPING]"
    >
      <n-table striped>
        <thead>
          <tr>
            <th v-for="item in tableTitle" :key="item">{{ item }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in dimensionsAndSource" :key="index">
            <td>{{ item.field }}</td>
            <td>{{ item.mapping }}</td>
            <td>
              <n-space v-if="item.result === 0">
                <n-badge dot type="success"></n-badge>
                <n-text>{{ t('visualBigScreen.management-674210-831') }}</n-text>
              </n-space>
              <n-space v-else>
                <n-badge dot :type="item.result === 1 ? 'success' : 'error'"></n-badge>
                <n-text>{{
                  item.result === 1
                    ? t('visualBigScreen.management-674210-832')
                    : t('visualBigScreen.management-674210-833')
                }}</n-text>
              </n-space>
            </td>
          </tr>
        </tbody>
      </n-table>
    </n-timeline-item>
    <!-- 处理 vcharts 的数据映射 -->
    <n-timeline-item v-if="isVChart" type="info" :title="timelineTitleMap[TimelineTitleEnum.MAPPING]">
      <n-table striped>
        <thead>
          <tr>
            <th v-for="item in vchartTableTitle" :key="item">{{ item }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in fieldList" :key="item.field">
            <td>
              <n-ellipsis style="width: 70px; max-width: 240px">
                {{ item.field }}
              </n-ellipsis>
            </td>
            <td v-if="isArray(item.mapping)">
              <n-space :size="4" vertical>
                <n-input
                  v-for="(mappingItem, index) in item.mapping"
                  :key="index"
                  v-model:value="item.mapping[index]"
                  type="tiny"
                  size="small"
                  :placeholder="t('visualBigScreen.management-674210-834')"
                  @change="() => (item.result = matchingHandle(item.mapping[index]))"
                />
              </n-space>
            </td>
            <td v-else>
              <n-input
                v-model:value="item.mapping"
                type="text"
                size="small"
                :placeholder="t('visualBigScreen.management-674210-834')"
              />
            </td>
          </tr>
        </tbody>
      </n-table>
    </n-timeline-item>
    <n-timeline-item v-show="filterShow" color="#97846c" :title="timelineTitleMap[TimelineTitleEnum.FILTER]">
      <n-space :size="18" vertical>
        <n-text depth="3">{{ t('visualBigScreen.management-674210-788') }}</n-text>
        <chart-data-monaco-editor></chart-data-monaco-editor>
      </n-space>
    </n-timeline-item>
    <n-timeline-item type="success" :title="timelineTitleMap[TimelineTitleEnum.CONTENT]">
      <n-space vertical>
        <n-space class="source-btn-box">
          <n-upload
            v-model:file-list="uploadFileListRef"
            :show-file-list="false"
            :customRequest="customRequest"
            @before-upload="beforeUpload"
          >
            <n-space>
              <n-button v-if="!ajax" class="sourceBtn-item" :disabled="noData">
                <template #icon>
                  <n-icon>
                    <document-add-icon />
                  </n-icon>
                </template>
                {{ t('visualBigScreen.management-674210-836') }}
              </n-button>
            </n-space>
          </n-upload>
          <div>
            <n-button class="sourceBtn-item" :disabled="noData" @click="download">
              <template #icon>
                <n-icon>
                  <document-download-icon />
                </n-icon>
              </template>
              {{ t('visualBigScreen.management-674210-837') }}
            </n-button>
            <n-tooltip trigger="hover">
              <template #trigger>
                <n-icon class="go-ml-1" size="21" :depth="3">
                  <help-outline-icon></help-outline-icon>
                </n-icon>
              </template>
              <span>{{ t('visualBigScreen.management-674210-838') }}</span>
            </n-tooltip>
          </div>
        </n-space>
        <n-card size="small">
          <n-code :code="toString(displaySource)" language="json"></n-code>
        </n-card>
      </n-space>
    </n-timeline-item>
  </n-timeline>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { ChartFrameEnum } from '@vb/packages/index.d'
import { RequestDataTypeEnum } from '@vb/enums/httpEnum'
import { icon } from '@vb/plugins/icon'
import { DataResultEnum, TimelineTitleEnum, TimelineTitleLangKey } from '../../index.d'
import { ChartDataMonacoEditor } from '../ChartDataMonacoEditor'
import { useFile } from '../../hooks/useFile.hooks'
import { useTargetData } from '../../../hooks/useTargetData.hook'
import { toString, isArray } from '@vb/utils'

const { targetData } = useTargetData()
const { t } = useI18n()
defineProps({
  show: {
    type: Boolean,
    required: false
  },
  ajax: {
    type: Boolean,
    required: true
  }
})

// 表格标题
const tableTitle = computed(() => [
  t('visualBigScreen.management-674210-828'),
  t('visualBigScreen.management-674210-829'),
  t('visualBigScreen.management-674210-830')
])
const vchartTableTitle = computed(() => [
  t('visualBigScreen.management-674210-828'),
  t('visualBigScreen.management-674210-835')
])

const { HelpOutlineIcon } = icon.ionicons5
const { DocumentAddIcon, DocumentDownloadIcon } = icon.carbon

const source = ref()
const dimensions = ref()
const dimensionsAndSource = ref()
const noData = ref(false)
const displaySource = computed(() => {
  return noData.value ? t('visualBigScreen.management-674210-841') : source.value
})
const timelineTitleMap = computed(() => ({
  [TimelineTitleEnum.FILTER]: t(TimelineTitleLangKey[TimelineTitleEnum.FILTER]),
  [TimelineTitleEnum.MAPPING]: t(TimelineTitleLangKey[TimelineTitleEnum.MAPPING]),
  [TimelineTitleEnum.CONTENT]: t(TimelineTitleLangKey[TimelineTitleEnum.CONTENT])
}))

// 映射列表, 注意内部的mapping是响应式的，上方需要修改
const fieldList = ref<
  Array<{
    field: string
    mapping: string[]
    result: DataResultEnum
  }>
>([])

const { uploadFileListRef, customRequest, beforeUpload, download } = useFile(targetData)

// 是否展示过滤器
const filterShow = computed(() => {
  return targetData.value.request.requestDataType !== RequestDataTypeEnum.STATIC
})

// 是支持 dataset 的图表类型
const isCharts = computed(() => {
  return targetData.value.chartConfig.chartFrame === ChartFrameEnum.ECHARTS
})
// 是支持 vchart 的图表类型
const isVChart = computed(() => {
  return targetData.value.chartConfig.chartFrame === ChartFrameEnum.VCHART
})

// 处理映射列表状态结果
const matchingHandle = (mapping: string) => {
  for (let i = 0; i < source.value.length; i++) {
    if (source.value[i][mapping] === undefined) {
      return DataResultEnum.FAILURE
    }
  }
  return DataResultEnum.SUCCESS
}

// 处理映射列表
const dimensionsAndSourceHandle = () => {
  try {
    // 去除首项数据轴标识
    return dimensions.value.map((dimensionsItem: string, index: number) => {
      return index === 0
        ? {
            // 字段
            field: t('visualBigScreen.management-674210-839'),
            // 映射
            mapping: dimensionsItem,
            // 结果
            result: DataResultEnum.NULL
          }
        : {
            field: t('visualBigScreen.management-674210-840', [index]),
            mapping: dimensionsItem,
            result: matchingHandle(dimensionsItem)
          }
    })
  } catch (error) {
    return []
  }
}

// 处理 vchart 映射列表
const initFieldListHandle = () => {
  if (targetData.value?.option) {
    fieldList.value = []
    // 所有名称，找到其中中 Field 结尾 的 key 和值
    for (const key in targetData.value.option) {
      if (key.endsWith('Field')) {
        const value = targetData.value.option[key]
        const item = {
          field: key,
          mapping: value,
          result: DataResultEnum.SUCCESS
        }
        if (item.mapping === undefined) {
          item.result = DataResultEnum.FAILURE
        }
        fieldList.value.push(item)
      }
    }
  }
}

watch(
  () => targetData.value?.option?.dataset,
  (
    newData?: {
      source: any
      dimensions: any
    } | null
  ) => {
    noData.value = false
    if (newData && targetData?.value?.chartConfig?.chartFrame === ChartFrameEnum.ECHARTS) {
      // 只有 DataSet 数据才有对应的格式
      source.value = newData
      dimensions.value = newData.dimensions
      dimensionsAndSource.value = dimensionsAndSourceHandle()
    } else if (newData && targetData?.value?.chartConfig?.chartFrame === ChartFrameEnum.VCHART) {
      source.value = newData
      initFieldListHandle()
    } else if (newData !== undefined && newData !== null) {
      dimensionsAndSource.value = null
      source.value = newData
      fieldList.value = []
    } else {
      noData.value = true
      source.value = undefined
    }
    if (isArray(newData)) {
      dimensionsAndSource.value = null
    }
  },
  {
    immediate: true
  }
)
</script>

<style lang="scss" scoped>
@include go('chart-configurations-timeline') {
  @include deep() {
    pre {
      white-space: pre-wrap;
      word-wrap: break-word;
    }
  }
  .source-btn-box {
    margin-top: 10px !important;
  }
}
</style>
