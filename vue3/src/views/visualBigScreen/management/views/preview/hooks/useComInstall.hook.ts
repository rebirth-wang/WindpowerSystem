import { ref } from 'vue'
import { ChartEditStorageType } from '../index.d'
import { CreateComponentType, CreateComponentGroupType } from '@vb/packages/index.d'
import { fetchChartComponent } from '@vb/packages/index'
import { getAppInstance } from '@vb/utils/runtimeContext'

export const useComInstall = (localStorageInfo: ChartEditStorageType) => {
  const show = ref(false)

  // 注册组件(一开始无法获取 app 实例)
  const intervalTiming = setInterval(() => {
    const app = getAppInstance()
    if (app?.component) {
      clearInterval(intervalTiming)

      const intComponent = (target: CreateComponentType) => {
        if (!app.component(target.chartConfig.chartKey)) {
          app.component(target.chartConfig.chartKey, fetchChartComponent(target.chartConfig))
        }
      }

      localStorageInfo.componentList.forEach(async (e: CreateComponentType | CreateComponentGroupType) => {
        if (e.isGroup) {
          ;(e as CreateComponentGroupType).groupList.forEach(groupItem => {
            intComponent(groupItem)
          })
        } else {
          intComponent(e as CreateComponentType)
        }
      })
      show.value = true
    }
  }, 200)

  return {
    show
  }
}
