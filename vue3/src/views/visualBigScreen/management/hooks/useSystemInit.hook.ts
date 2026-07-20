import { useSystemStore } from '@vb/store/modules/systemStore/systemStore'
import { SystemStoreEnum } from '@vb/store/modules/systemStore/systemStore.d'
import { ossUrlApi } from '@vb/api/path'

// * 初始化
export const useSystemInit = async () => {
  const systemStore = useSystemStore()

  // 获取 OSS 信息的 url 地址，用来拼接展示图片的地址
  const getOssUrl = async () => {
    const res = await ossUrlApi({})
    systemStore.setItem(SystemStoreEnum.FETCH_INFO, {
      OSSUrl: res
    })
  }

  // 执行
  getOssUrl()
}
