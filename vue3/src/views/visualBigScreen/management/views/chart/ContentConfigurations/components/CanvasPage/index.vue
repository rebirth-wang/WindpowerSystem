<template>
  <div class="go-canvas-setting">
    <n-form inline :label-width="45" size="small" label-placement="left">
      <n-form-item :label="t('visualBigScreen.management-674210-250')">
        <!-- 尺寸选择 -->
        <n-input-number
          size="small"
          v-model:value="canvasConfig.width"
          :disabled="editCanvas.lockScale"
          :validator="validator"
          @update:value="changeSizeHandle"
        ></n-input-number>
      </n-form-item>
      <n-form-item :label="t('visualBigScreen.management-674210-336')">
        <n-input-number
          size="small"
          v-model:value="canvasConfig.height"
          :disabled="editCanvas.lockScale"
          :validator="validator"
          @update:value="changeSizeHandle"
        ></n-input-number>
      </n-form-item>
    </n-form>

    <div class="upload-box">
      <n-upload
        v-model:file-list="uploadFileListRef"
        :show-file-list="false"
        :customRequest="customRequest"
        :onBeforeUpload="beforeUploadHandle"
      >
        <n-upload-dragger>
          <img
            v-if="canvasConfig.backgroundImage"
            class="upload-show"
            :src="canvasConfig.backgroundImage"
            :alt="t('visualBigScreen.management-674210-252')"
          />
          <div class="upload-img" v-show="!canvasConfig.backgroundImage">
            <img src="@vb/assets/images/canvas/noImage.png" />
            <n-text class="upload-desc" depth="3">
              {{ t('visualBigScreen.management-674210-690', [backgroundImageSize]) }}
            </n-text>
          </div>
        </n-upload-dragger>
      </n-upload>
    </div>
    <n-space vertical :size="12">
      <n-space>
        <n-text>{{ t('visualBigScreen.management-674210-253') }}</n-text>
        <div class="picker-height">
          <div class="canvas-color-picker">
            <n-color-picker
              v-if="!switchSelectColorLoading"
              size="small"
              v-model:value="canvasConfig.background"
              :showPreview="true"
              :swatches="swatchesColors"
            ></n-color-picker>
          </div>
        </div>
      </n-space>
      <n-space>
        <n-text>{{ t('visualBigScreen.management-674210-675') }}</n-text>
        <n-select
          size="small"
          style="width: 250px"
          v-model:value="selectColorValue"
          :disabled="!canvasConfig.backgroundImage"
          :options="selectColorOptions"
          @update:value="selectColorValueHandle"
        />
      </n-space>
      <n-space>
        <n-text>{{ t('visualBigScreen.management-674210-676') }}</n-text>
        <n-button class="clear-btn" size="small" :disabled="!canvasConfig.backgroundImage" @click="clearImage">
          {{ t('visualBigScreen.management-674210-677') }}
        </n-button>
        <n-button class="clear-btn" size="small" :disabled="!canvasConfig.background" @click="clearColor">
          {{ t('visualBigScreen.management-674210-678') }}
        </n-button>
      </n-space>
      <n-space>
        <n-text>{{ t('visualBigScreen.management-674210-679') }}</n-text>
        <n-button-group>
          <n-button
            v-for="item in previewTypeList"
            :key="item.key"
            :type="canvasConfig.previewScaleType === item.key ? 'primary' : 'tertiary'"
            ghost
            size="small"
            @click="selectPreviewType(item.key)"
          >
            <n-tooltip :show-arrow="false" trigger="hover">
              <template #trigger>
                <n-icon class="select-preview-icon" size="18">
                  <component :is="item.icon"></component>
                </n-icon>
              </template>
              {{ item.desc }}
            </n-tooltip>
          </n-button>
        </n-button-group>
      </n-space>
    </n-space>

    <!-- 滤镜 -->
    <styles-setting :isCanvas="true" :chartStyles="canvasConfig"></styles-setting>
    <n-divider style="margin: 10px 0"></n-divider>

    <!-- 主题选择和全局配置 -->
    <n-tabs class="tabs-box" size="small" type="segment">
      <n-tab-pane
        v-for="item in globalTabList"
        :key="item.key"
        :name="item.key"
        size="small"
        display-directive="show:lazy"
      >
        <template #tab>
          <n-space>
            <span>{{ item.title }}</span>
            <n-icon size="16" class="icon-position">
              <component :is="item.icon"></component>
            </n-icon>
          </n-space>
        </template>
        <component :is="item.render"></component>
      </n-tab-pane>
    </n-tabs>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { backgroundImageSize } from '@vb/settings/designSetting'
import { swatchesColors } from '@vb/settings/chartThemes/index'
import { FileTypeEnum } from '@vb/enums/fileTypeEnum'
import { useChartEditStore } from '@vb/store/modules/chartEditStore/chartEditStore'
import { EditCanvasConfigEnum } from '@vb/store/modules/chartEditStore/chartEditStore.d'
import StylesSetting from '@vb/components/Pages/ChartItemSetting/StylesSetting.vue'
import { UploadCustomRequestOptions } from 'naive-ui'
import { loadAsyncComponent, fetchRouteParamsLocation } from '@vb/utils'
import { PreviewScaleEnum } from '@vb/enums/styleEnum'
import { ResultEnum } from '@vb/enums/httpEnum'
import { icon } from '@vb/plugins/icon'
import { uploadFile } from '@vb/api/path'

const { ColorPaletteIcon } = icon.ionicons5
const { ScaleIcon, FitToScreenIcon, FitToHeightIcon, FitToWidthIcon } = icon.carbon

const { t } = useI18n()
const chartEditStore = useChartEditStore()
const canvasConfig = chartEditStore.getEditCanvasConfig
const editCanvas = chartEditStore.getEditCanvas

const uploadFileListRef = ref()
const switchSelectColorLoading = ref(false)
const selectColorValue = ref(0)

const ChartThemeColor = loadAsyncComponent(() => import('./components/ChartThemeColor/index.vue'))
const VChartThemeColor = loadAsyncComponent(() => import('./components/VChartThemeColor/index.vue'))

// 默认应用类型
const selectColorOptions = computed(() => [
  {
    label: t('visualBigScreen.management-674210-686'),
    value: 0
  },
  {
    label: t('visualBigScreen.management-674210-687'),
    value: 1
  }
])

const globalTabList = computed(() => [
  {
    key: 'ChartTheme',
    title: t('visualBigScreen.management-674210-680'),
    icon: ColorPaletteIcon,
    render: ChartThemeColor
  },
  {
    key: 'VChartTheme',
    title: t('visualBigScreen.management-674210-691'),
    icon: ColorPaletteIcon,
    render: VChartThemeColor
  }
])

const previewTypeList = computed(() => [
  {
    key: PreviewScaleEnum.FIT,
    title: t('visualBigScreen.management-674210-681'),
    icon: ScaleIcon,
    desc: t('visualBigScreen.management-674210-682')
  },
  {
    key: PreviewScaleEnum.SCROLL_Y,
    title: t('visualBigScreen.management-674210-697'),
    icon: FitToWidthIcon,
    desc: t('visualBigScreen.management-674210-683')
  },
  {
    key: PreviewScaleEnum.SCROLL_X,
    title: t('visualBigScreen.management-674210-698'),
    icon: FitToHeightIcon,
    desc: t('visualBigScreen.management-674210-684')
  },
  {
    key: PreviewScaleEnum.FULL,
    title: t('visualBigScreen.management-674210-699'),
    icon: FitToScreenIcon,
    desc: t('visualBigScreen.management-674210-685')
  }
])

watch(
  () => canvasConfig.selectColor,
  newValue => {
    selectColorValue.value = newValue ? 0 : 1
  },
  {
    immediate: true
  }
)

// 画布尺寸规则
const validator = (x: number) => x > 50

// 修改尺寸
const changeSizeHandle = () => {
  chartEditStore.computedScale()
}

// 上传图片前置处理
//@ts-ignore
const beforeUploadHandle = async ({ file }) => {
  uploadFileListRef.value = []
  const type = file.file.type
  const size = file.file.size

  if (size > 1024 * 1024 * backgroundImageSize) {
    ElMessage.warning(t('visualBigScreen.management-674210-237', [backgroundImageSize]))
    return false
  }
  if (type !== FileTypeEnum.PNG && type !== FileTypeEnum.JPEG && type !== FileTypeEnum.GIF) {
    ElMessage.warning(t('visualBigScreen.management-674210-238'))
    return false
  }
  return true
}

// 应用颜色
const selectColorValueHandle = (value: number) => {
  canvasConfig.selectColor = value == 0
}

// 清除背景
const clearImage = () => {
  chartEditStore.setEditCanvasConfig(EditCanvasConfigEnum.BACKGROUND_IMAGE, undefined)
  chartEditStore.setEditCanvasConfig(EditCanvasConfigEnum.SELECT_COLOR, true)
}

// 启用/关闭 颜色（强制更新）
const switchSelectColorHandle = () => {
  switchSelectColorLoading.value = true
  setTimeout(() => {
    switchSelectColorLoading.value = false
  })
}

// 清除颜色
const clearColor = () => {
  chartEditStore.setEditCanvasConfig(EditCanvasConfigEnum.BACKGROUND, undefined)
  if (canvasConfig.backgroundImage) {
    chartEditStore.setEditCanvasConfig(EditCanvasConfigEnum.SELECT_COLOR, false)
  }
  switchSelectColorHandle()
}

// 自定义上传操作
const customRequest = (options: UploadCustomRequestOptions) => {
  const { file } = options
  nextTick(async () => {
    if (file.file) {
      // 修改名称
      const newNameFile = new File([file.file], `${fetchRouteParamsLocation()}_index_background.png`, {
        type: file.file.type
      })
      let uploadParams = new FormData()
      uploadParams.append('object', newNameFile)

      const uploadRes = await uploadFile(uploadParams)

      if (uploadRes && uploadRes.code === ResultEnum.SUCCESS) {
        chartEditStore.setEditCanvasConfig(
          EditCanvasConfigEnum.BACKGROUND_IMAGE,
          `${import.meta.env.VITE_APP_BASE_API}${uploadRes.data.url}?time=${new Date().getTime()}`
        )
        chartEditStore.setEditCanvasConfig(EditCanvasConfigEnum.SELECT_COLOR, false)
        return
      }
      ElMessage.error(t('visualBigScreen.management-674210-689'))
    } else {
      ElMessage.error(t('visualBigScreen.management-674210-689'))
    }
  })
}

// 选择适配方式
const selectPreviewType = (key: PreviewScaleEnum) => {
  chartEditStore.setEditCanvasConfig(EditCanvasConfigEnum.PREVIEW_SCALE_TYPE, key)
}
</script>

<style lang="scss" scoped>
$uploadWidth: 326px;
$uploadHeight: 193px;
@include go(canvas-setting) {
  padding-top: 20px;
  .upload-box {
    cursor: pointer;
    margin-bottom: 20px;
    @include deep() {
      .n-upload-dragger {
        padding: 5px;
        width: $uploadWidth;
        background-color: rgba(0, 0, 0, 0);
      }
    }
    .upload-show {
      width: -webkit-fill-available;
      height: $uploadHeight;
      border-radius: 5px;
    }
    .upload-img {
      display: flex;
      flex-direction: column;
      align-items: center;
      img {
        height: 150px;
      }
      .upload-desc {
        padding: 10px 0;
      }
    }
  }
  .icon-position {
    padding-top: 2px;
  }
  .picker-height {
    min-height: 35px;
    width: 250px;
  }

  .canvas-color-picker {
    width: 100%;
  }
  .clear-btn {
    padding-left: 2.25em;
    padding-right: 2.25em;
  }
  .select-preview-icon {
    box-sizing: content-box;
    padding-right: 0.68em;
    padding-left: 0.68em;
  }
  .tabs-box {
    margin-top: 20px;
  }
}
</style>
