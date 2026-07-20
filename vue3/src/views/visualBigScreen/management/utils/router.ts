import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import i18n from '@/lang'
import { ResultEnum, RequestHttpHeaderEnum } from '@vb/enums/httpEnum'
import { ErrorPageNameMap, PageEnum, PreviewEnum } from '@vb/enums/pageEnum'
import { docPath, giteeSourceCodePath } from '@vb/settings/pathConst'
import { SystemStoreEnum, SystemStoreUserInfoEnum } from '@vb/store/modules/systemStore/systemStore.d'
import { StorageEnum } from '@vb/enums/storageEnum'
import { clearLocalStorage, getLocalStorage, clearCookie, getCookie } from './storage'
import router from '@/router'
import { logoutApi, getUserInfoApi } from '@vb/api/path/system.api'
import { useSystemStore } from '@vb/store/modules/systemStore/systemStore'

/**
 * * 根据名字跳转路由
 * @param pageName
 * @param isReplace
 * @param windowOpen
 */
export const routerTurnByName = (pageName: string, isReplace?: boolean, windowOpen?: boolean) => {
  if (windowOpen) {
    const path = fetchPathByName(pageName, 'href')
    openNewWindow(path)
    return
  }
  if (isReplace) {
    router.replace({
      name: pageName
    })
    return
  }
  router.push({
    name: pageName
  })
}

/**
 * * 根据名称获取路由信息
 * @param pageName
 * @param pageName
 */
export const fetchPathByName = (pageName: string, p?: string) => {
  try {
    const pathData = router.resolve({
      name: pageName
    })
    return p ? (pathData as any)[p] : pathData
  } catch (error) {
    ElMessage.warning(i18n.global.t('visualBigScreen.management-674210-1647'))
  }
}

/**
 * * 根据路径跳转路由
 * @param path
 * @param query
 * @param isReplace
 * @param windowOpen
 */
export const routerTurnByPath = (
  path: string,
  query?: Array<string | number>,
  isReplace?: boolean,
  windowOpen?: boolean
) => {
  let fullPath = ''
  if (query?.length) {
    fullPath = `${path}/${query.join('/')}`
  }
  if (windowOpen) {
    return openNewWindow(fullPath)
  }
  if (isReplace) {
    router.replace({
      path: fullPath
    })
    return
  }
  router.push({
    path: fullPath
  })
}

/**
 * * 错误页重定向
 * @param icon
 * @returns
 */
export const redirectErrorPage = (code: ResultEnum) => {
  if (!code) return false
  const pageName = ErrorPageNameMap.get(code)
  if (!pageName) return false
  routerTurnByName(pageName)
}

/**
 * * 重新加载当前路由页面
 */
export const reloadRoutePage = () => {
  routerTurnByName(PageEnum.RELOAD_NAME)
}

/**
 * * 退出登录
 */
export const logout = async () => {
  try {
    const res = await logoutApi()
    if (res && res.code === ResultEnum.SUCCESS) {
      ElMessage.success(i18n.global.t('visualBigScreen.management-674210-40'))
      clearCookie(RequestHttpHeaderEnum.COOKIE)
      clearCookie(StorageEnum.ADMIN_TOKEN)
      clearLocalStorage(StorageEnum.GO_SYSTEM_STORE)
      routerTurnByName(PageEnum.BASE_LOGIN_NAME)
    }
  } catch (error) {
    ElMessage.success(i18n.global.t('visualBigScreen.management-674210-41'))
  }
}

/**
 * * 新开页面
 * @param url
 */
export const openNewWindow = (url: string) => {
  return window.open(url, '_blank')
}

/**
 * * 打开项目文档
 * @param url
 */
export const openDoc = () => {
  openNewWindow(docPath)
}

/**
 * * 打开码云仓库地址
 * @param url
 */
export const openGiteeSourceCode = () => {
  openNewWindow(giteeSourceCodePath)
}

/**
 * * 判断是否是预览页
 * @returns boolean
 */
export const isPreview = () => {
  const hash = (window.location.hash || '').toLowerCase()
  const pathname = (window.location.pathname || '').toLowerCase()
  const href = (window.location.href || '').toLowerCase()
  return hash.includes('preview') || pathname.includes('/preview') || href.includes('/preview')
}

/**
 * * 获取当前路由下的参数
 * @returns object
 */
export const fetchRouteParams = () => {
  try {
    const route = useRoute()
    return route.params
  } catch (error) {
    ElMessage.warning(i18n.global.t('visualBigScreen.management-674210-1647'))
  }
}

/**
 * * 通过硬解析获取当前路由下的参数
 * @returns object
 */
export const fetchRouteParamsLocation = () => {
  try {
    // 1) 优先使用 vue-router 当前路由参数
    const current = router.currentRoute?.value
    const routeId = current?.params?.id
    if (Array.isArray(routeId)) {
      const id = routeId[0]
      if (id) return String(id)
    } else if (routeId) {
      return String(routeId)
    }

    // 2) hash 模式兜底
    const hashPath = document.location.hash?.split('?')[0] || ''
    const hashId = hashPath.split('/').pop() || ''
    if (hashId) return hashId

    // 3) history 模式兜底
    const path = document.location.pathname?.split('?')[0] || ''
    const pathId = path.split('/').pop() || ''
    if (pathId) return pathId

    return ''
  } catch (error) {
    ElMessage.warning(i18n.global.t('visualBigScreen.management-674210-1647'))
    return ''
  }
}

/**
 * * 回到主页面
 * @param confirm
 */
export const goHome = () => {
  routerTurnByName(PageEnum.BASE_HOME_NAME)
}

/**
 * * 判断是否登录
 * @return boolean
 */
export const loginCheck = async () => {
  try {
    const token = getCookie(StorageEnum.ADMIN_TOKEN)
    if (typeof token === 'undefined' || token == null || token === '') {
      return true
    } else {
      const info = getLocalStorage(StorageEnum.GO_SYSTEM_STORE)
      if (info != null) {
        const userInfo = info[SystemStoreEnum.USER_INFO]
        if (userInfo != null) {
          return false
        } else {
          const res = (await getUserInfoApi()) as unknown as LoginInfoResponseType
          if (res.code === ResultEnum.SUCCESS) {
            const systemStore = useSystemStore()
            const { nickName, userName, userId, avatar } = res.user
            // 存储到 pinia
            systemStore.setItem(SystemStoreEnum.USER_INFO, {
              [SystemStoreUserInfoEnum.USER_TOKEN]: token,
              [SystemStoreUserInfoEnum.USER_ID]: userId,
              [SystemStoreUserInfoEnum.USER_NAME]: userName,
              [SystemStoreUserInfoEnum.NICK_NAME]: nickName,
              [SystemStoreUserInfoEnum.AVATAR]: avatar
            })
            return false
          } else {
            return true
          }
        }
      }
    }
    return false
  } catch (error) {
    return false
  }
}

/**
 * * 预览地址
 * @returns
 */
export const previewPath = (id?: string | number) => {
  const { origin, pathname, href } = document.location
  const path = fetchPathByName(PreviewEnum.CHART_PREVIEW_NAME, 'href')
  const idValue = id || fetchRouteParamsLocation()
  const isHashRouter = /\/#\//.test(href)
  const previewPath = isHashRouter ? `${origin}${pathname}${path}/${idValue}` : `${origin}${path}/${idValue}`
  return previewPath
}
