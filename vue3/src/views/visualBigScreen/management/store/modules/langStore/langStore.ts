import { defineStore } from 'pinia'
import { lang } from '@vb/settings/designSetting'
import { LangStateType } from './langStore.d'
import { LangEnum } from '@vb/enums/styleEnum'
import { setLocalStorage, getLocalStorage, reloadRoutePage, setCookie, getCookie } from '@vb/utils'
import { StorageEnum } from '@vb/enums/storageEnum'
import { useSettingStore } from '@vb/store/modules/settingStore/settingStore'

const { GO_LANG_STORE } = StorageEnum
const storageLang: LangStateType = getLocalStorage(GO_LANG_STORE)
const cookieLangMap: Record<string, LangEnum> = {
  'zh-CN': LangEnum.ZH,
  'en-US': LangEnum.EN,
  ZH: LangEnum.ZH,
  EN: LangEnum.EN
}
const langCookieMap: Record<LangEnum, string> = {
  [LangEnum.ZH]: 'zh-CN',
  [LangEnum.EN]: 'en-US'
}

// 语言
export const useLangStore = defineStore({
  id: 'useLangStore',
  state: (): LangStateType =>
    storageLang || {
      lang: cookieLangMap[getCookie('language')] || lang
    },
  getters: {
    getLang(): LangEnum {
      return this.lang
    }
  },
  actions: {
    changeLang(lang: LangEnum): void {
      const settingStore = useSettingStore()
      if (this.lang === lang) return
      this.lang = lang
      setCookie('language', langCookieMap[lang], 365)
      setLocalStorage(GO_LANG_STORE, this.$state)

      if (settingStore.getChangeLangReload) {
        reloadRoutePage()
      }
    }
  }
})
