import { defineStore } from 'pinia'
import { systemSetting } from '@vb/settings/systemSetting'
import { asideCollapsedWidth } from '@vb/settings/designSetting'
import { SettingStoreType, ToolsStatusEnum } from './settingStore.d'
import { setLocalStorage, getLocalStorage } from '@vb/utils'
import { StorageEnum } from '@vb/enums/storageEnum'
const { GO_SETTING_STORE } = StorageEnum

const storageSetting: SettingStoreType = getLocalStorage(GO_SETTING_STORE)

// 全局设置
export const useSettingStore = defineStore({
  id: 'useSettingStore',
  state: (): SettingStoreType => storageSetting || systemSetting,
  getters: {
    getHidePackageOneCategory(): boolean {
      return this.hidePackageOneCategory
    },
    getChangeLangReload(): boolean {
      return this.changeLangReload
    },
    getAsideAllCollapsed(): boolean {
      return this.asideAllCollapsed
    },
    getAsideCollapsedWidth(): number {
      return this.asideAllCollapsed ? 0 : asideCollapsedWidth
    },
    getChartMoveDistance(): number {
      return this.chartMoveDistance
    },
    getChartAlignRange(): number {
      return this.chartAlignRange
    },
    getChartToolsStatus(): ToolsStatusEnum {
      return this.chartToolsStatus
    },
    getChartToolsStatusHide(): boolean {
      return this.chartToolsStatusHide
    }
  },
  actions: {
    setItem<T extends keyof SettingStoreType, K extends SettingStoreType[T]>(key: T, value: K): void {
      this.$patch(state => {
        state[key] = value
      })
      setLocalStorage(GO_SETTING_STORE, this.$state)
    }
  }
})
