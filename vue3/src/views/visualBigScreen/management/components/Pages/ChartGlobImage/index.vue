<template>
  <img class="list-img" v-lazy="imageInfo" :alt="t('visualBigScreen.management-674210-1688')" />
</template>

<script setup lang="ts">
import { ref, PropType, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { fetchImages } from '@vb/packages'
import { ConfigType } from '@vb/packages/index.d'

const { t } = useI18n()

const props = defineProps({
  chartConfig: {
    type: Object as PropType<ConfigType>,
    required: true
  }
})

const imageInfo = ref('')

// 获取图片
const fetchImageUrl = async () => {
  imageInfo.value = await fetchImages(props.chartConfig)
}

watch(
  () => props.chartConfig.key,
  () => fetchImageUrl(),
  {
    immediate: true
  }
)
</script>
