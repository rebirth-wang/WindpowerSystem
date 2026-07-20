<template>
  <el-dialog
    class="go-modal-box"
    :model-value="showRef"
    :show-close="false"
    append-to-body
    width="82vw"
    @close="closeHandle"
    @update:model-value="handleDialogModelUpdate"
  >
    <el-card shadow="never">
      <div class="list-content">
        <!-- 标题 -->
        <div class="list-content-top title-row">
          <span>{{ cardData?.title || cardData?.id || t('visualBigScreen.management-674210-13') }}</span>
        </div>
        <!-- 顶部按钮 -->
        <div class="list-content-top tool-row">
          <mac-os-control-btn :narrow="true" :hidden="['close']" @remove="closeHandle"></mac-os-control-btn>
        </div>
        <!-- 中间 -->
        <div class="list-content-img">
          <el-image fit="contain" :src="cardData?.image" :alt="cardData?.title">
            <template #error>
              <img class="image-error" :src="requireErrorImg()" />
            </template>
          </el-image>
        </div>
      </div>
      <template #footer>
        <div class="list-footer">
          <span class="list-footer-time">
            {{ t('visualBigScreen.management-674210-102') }}：{{ cardData?.time }}，
            {{ t('visualBigScreen.management-674210-30') }}：{{ cardData?.updateTime }}
          </span>
          <div class="list-footer-tools">
            <span class="list-footer-status">
              <span
                class="status-dot go-animation-twinkle"
                :class="cardData?.release ? 'is-release' : 'is-unrelease'"
              ></span>
              {{
                cardData?.release ? t('visualBigScreen.management-674210-0') : t('visualBigScreen.management-674210-1')
              }}
            </span>
            <el-tooltip :content="t('visualBigScreen.management-674210-4')" placement="bottom">
              <el-button @click="editHandle">
                <el-icon><hammer-icon /></el-icon>
              </el-button>
            </el-tooltip>
          </div>
        </div>
      </template>
    </el-card>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { requireErrorImg } from '@vb/utils'
import { icon } from '@vb/plugins/icon'
import { MacOsControlBtn } from '@vb/components/Tips/MacOsControlBtn'

const { HammerIcon } = icon.ionicons5
const { t } = useI18n()
const showRef = ref(false)
const emit = defineEmits(['close', 'edit'])

const props = defineProps({
  modalShow: {
    required: true,
    type: Boolean
  },
  cardData: {
    required: true,
    type: Object
  }
})

watch(
  () => props.modalShow,
  newValue => {
    showRef.value = newValue
  },
  {
    immediate: true
  }
)

const handleDialogModelUpdate = (value: boolean) => {
  showRef.value = value
  if (!value) {
    closeHandle()
  }
}

// 编辑处理
const editHandle = () => {
  emit('edit', props.cardData)
}

// 关闭对话框
const closeHandle = () => {
  emit('close')
}
</script>

<style lang="scss" scoped>
$contentHeight: 80vh;
$imageHeight: calc(80vh - 110px);
$contentWidth: 82vw;

.go-modal-box {
  width: $contentWidth;
  height: $contentHeight;

  .el-card {
    border: none;

    :deep(.el-card__footer) {
      border-top: none;
      padding: 0px 20px 10px;
    }
  }

  .list-content {
    margin-top: 6px;
    border-radius: 8px;
    overflow: hidden;
    background: rgba(148, 163, 184, 0.08);

    &-top {
      position: absolute;
      top: 16px;
      left: 0;
      padding-left: 10px;
      height: 22px;
      width: $contentWidth;
    }

    .title-row {
      justify-content: center;
      align-items: center;
      display: flex;
      font-size: 14px;
      color: var(--el-text-color-primary);
    }

    .tool-row {
      display: flex;
      align-items: center;
      justify-content: flex-start;
    }

    &-img {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 24px 0;

      :deep(.el-image) {
        height: $imageHeight;
        min-height: 200px;
        width: 100%;
      }

      :deep(.el-image__inner) {
        border-radius: 8px;
      }

      .image-error {
        height: $imageHeight;
        min-height: 200px;
        width: 100%;
        object-fit: contain;
        border-radius: 8px;
      }
    }
  }

  .list-footer {
    min-height: 30px;
    line-height: 30px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;

    &-time {
      flex: 1;
      min-width: 0;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }

    &-tools {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    &-status {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      white-space: nowrap;
    }
  }

  .status-dot {
    display: inline-block;
    width: 8px;
    height: 8px;
    border-radius: 50%;
  }

  .status-dot.is-release {
    background-color: #34c749;
  }

  .status-dot.is-unrelease {
    background-color: #fcbc40;
  }
}
</style>
