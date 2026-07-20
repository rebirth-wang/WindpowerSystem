<template>
  <n-modal v-model:show="modelShowRef" @afterLeave="closeHandle">
    <n-list bordered class="go-system-info">
      <template #header>
        <n-space justify="space-between">
          <n-h3 class="go-mb-0">{{ t('visualBigScreen.management-674210-1665') }}</n-h3>
          <n-icon size="20" class="go-cursor-pointer" @click="closeHandle">
            <close-icon></close-icon>
          </n-icon>
        </n-space>
      </template>

      <n-list-item>
        <n-space class="go-my-2" :size="20">
          <n-text class="item-left">{{ t('visualBigScreen.management-674210-1666') }}</n-text>
          <n-text>
            {{ t('visualBigScreen.management-674210-1667') }}
            <n-a href="https://gitee.com/beecue/fastbee" target="_blank">https://gitee.com/beecue/fastbee</n-a>
            {{ t('visualBigScreen.management-674210-1668') }}
          </n-text>
        </n-space>
      </n-list-item>

      <n-list-item>
        <n-space class="go-my-2" :size="20">
          <n-text class="item-left">{{ t('visualBigScreen.management-674210-1669') }}</n-text>
          <n-text>
            {{ t('visualBigScreen.management-674210-1670') }}
            <n-text type="error">{{ t('visualBigScreen.management-674210-1671') }}</n-text>
            {{ t('visualBigScreen.management-674210-1672') }}
          </n-text>
        </n-space>
      </n-list-item>

      <n-list-item>
        <n-space class="go-mt-2" :size="20">
          <n-text class="item-left">{{ t('visualBigScreen.management-674210-1673') }}</n-text>
          <n-text>{{ t('visualBigScreen.management-674210-1674') }}</n-text>
        </n-space>
      </n-list-item>
    </n-list>
  </n-modal>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { icon } from '@vb/plugins/icon'

const props = defineProps({
  modelShow: Boolean
})

const emit = defineEmits(['update:modelShow'])
const { HelpOutlineIcon, CloseIcon } = icon.ionicons5
const { t } = useI18n()
const modelShowRef = ref(false)

watch(
  () => props.modelShow,
  newValue => {
    modelShowRef.value = newValue
  }
)

const closeHandle = () => {
  emit('update:modelShow', false)
}
</script>

<style lang="scss" scoped>
.go-system-info {
  min-width: 100px;
  max-width: 60vw;
  padding-bottom: 20px;
  backdrop-filter: blur(8px);

  .item-left {
    width: 200px;
  }

  :deep(.n-list-item:not(:last-child)) {
    border-bottom: 0;
  }
}
</style>
