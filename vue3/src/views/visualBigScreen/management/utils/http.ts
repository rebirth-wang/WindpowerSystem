import i18n from '@/lang'
import { getMessageApi } from '@vb/utils/runtimeContext'

/**
 * * 请求失败统一处理
 */
export const httpErrorHandle = (msg?: string) => {
  const messageApi = getMessageApi()
  const text = msg || i18n.global.t('visualBigScreen.management-674210-1704')

  if (messageApi && typeof messageApi.error === 'function') {
    messageApi.error(text)
    return
  }

  console.error(text)
}