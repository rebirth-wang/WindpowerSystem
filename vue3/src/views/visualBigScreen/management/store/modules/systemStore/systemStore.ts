import { defineStore } from 'pinia'
import { SystemStoreType, UserInfoType, FetchInfoType } from './systemStore.d'
import { setLocalStorage, getLocalStorage, setCookie } from '@vb/utils'
import { StorageEnum } from '@vb/enums/storageEnum'

const { GO_SYSTEM_STORE,ADMIN_TOKEN } = StorageEnum

const storageSystem: SystemStoreType = getLocalStorage(GO_SYSTEM_STORE)

// 系统数据记录
export const useSystemStore = defineStore({
  id: 'useSystemStore',
  state: (): SystemStoreType => storageSystem || {
    userInfo: {
      userId: undefined,
      userName: undefined,
      userToken: undefined,
      nickName: undefined
    },
    fetchInfo: {
      OSSUrl: undefined
    }
  },
  getters: {
    getUserInfo(): UserInfoType {
      return this.userInfo
    },
    getFetchInfo(): FetchInfoType {
      return this.fetchInfo
    },
  },
  actions: {
    setItem<T extends keyof SystemStoreType, K extends SystemStoreType[T]>(key: T, value: K): void {
      this.$patch(state => {
        state[key] = value
      });
      setLocalStorage(GO_SYSTEM_STORE, this.$state)
    },
    setToken(value: string): void {
      setCookie(ADMIN_TOKEN,value,1)
    }
  }
})