<template>
  <n-modal v-model:show="showRef" class="go-create-modal" @afterLeave="closeHandle">
    <n-space size="large">
      <n-card class="card-box" hoverable>
        <template #header>
          <n-text class="card-box-tite">{{ t('visualBigScreen.management-674210-23') }}</n-text>
        </template>
        <template #header-extra>
          <n-text @click="closeHandle">
            <n-icon size="20">
              <component :is="CloseIcon"></component>
            </n-icon>
          </n-text>
        </template>
        <n-space class="card-box-content" justify="center">
          <n-button
            size="large"
            :disabled="item.disabled"
            v-for="item in typeList"
            :key="item.key"
            @click="btnHandle(item.key)"
          >
            <component :is="item.title"></component>
            <template #icon>
              <n-icon size="18">
                <component :is="item.icon"></component>
              </n-icon>
            </template>
          </n-button>
        </n-space>
        <template #action></template>
      </n-card>
    </n-space>
  </n-modal>
</template>

<script lang="ts" setup>
import { ref, watch, shallowRef } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { icon } from '@vb/plugins/icon'
import { PageEnum, ChartEnum } from '@vb/enums/pageEnum'
import { ResultEnum } from '@vb/enums/httpEnum'
import { fetchPathByName, routerTurnByPath, getUUID } from '@vb/utils'
import { createProjectApi } from '@vb/api/path'

const { FishIcon, CloseIcon } = icon.ionicons5
const { StoreIcon, ObjectStorageIcon } = icon.carbon
const { t } = useI18n()
const showRef = ref(false)

const emit = defineEmits(['close'])
const props = defineProps({
  show: Boolean
})

const typeList = shallowRef([
  {
    title: t('visualBigScreen.management-674210-26'),
    key: ChartEnum.CHART_HOME_NAME,
    icon: FishIcon,
    disabled: false
  },
  {
    title: t('visualBigScreen.management-674210-28'),
    key: PageEnum.BASE_HOME_TEMPLATE_NAME,
    icon: ObjectStorageIcon,
    disabled: true
  },
  {
    title: t('visualBigScreen.management-674210-29'),
    key: PageEnum.BASE_HOME_TEMPLATE_MARKET_NAME,
    icon: StoreIcon,
    disabled: true
  }
])

watch(
  () => props.show,
  newValue => {
    showRef.value = newValue
  }
)

// 关闭对话框
const closeHandle = () => {
  emit('close', false)
}

// 处理按钮点击
const btnHandle = async (key: string) => {
  switch (key) {
    case ChartEnum.CHART_HOME_NAME:
      try {
        // 新增项目
        const res = await createProjectApi({
          // 项目名称
          projectName: getUUID(),
          // remarks
          remarks: null,
          // 图片地址
          indexImage: null
        })
        if (res && res.code === ResultEnum.SUCCESS) {
          ElMessage.success(t('visualBigScreen.management-674210-21'))

          const { id } = res.data
          const path = fetchPathByName(ChartEnum.CHART_HOME_NAME, 'href')
          routerTurnByPath(path, [id], undefined, true)
          closeHandle()
        }
      } catch (error) {
        ElMessage.error(t('visualBigScreen.management-674210-22'))
      }
      break
  }
}
</script>
<style lang="scss" scoped>
$cardWidth: 570px;

@include go('create-modal') {
  position: fixed;
  top: 200px;
  left: 50%;
  transform: translateX(-50%);
  .card-box {
    width: $cardWidth;
    cursor: pointer;
    border: 1px solid rgba(0, 0, 0, 0);
    @extend .go-transition;
    &:hover {
      @include hover-border-color('hover-border-color');
    }
    &-tite {
      font-size: 14px;
    }
    &-content {
      padding: 0px 10px;
      width: 100%;
    }
  }
}
</style>
