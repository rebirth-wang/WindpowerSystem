import { defineAsyncComponent, AsyncComponentLoader } from 'vue'
import { AsyncLoading, AsyncSkeletonLoading } from '@vb/components/GoLoading'
import { getAppInstance } from '@vb/utils/runtimeContext'

/**
 * * 动态注册组件
 */
export const componentInstall = <T>(key: string, node: T) => {
  const app = getAppInstance()
  if (!app || !key || !node) return
  if (!app.component(key)) {
    app.component(key, node)
  }
}

/**
 * * 异步加载组件
 * @param loader
 * @returns
 */
export const loadAsyncComponent = (loader: AsyncComponentLoader<any>) =>
  defineAsyncComponent({
    loader,
    loadingComponent: AsyncLoading,
    delay: 20,
  })
  
export const loadSkeletonAsyncComponent = (loader: AsyncComponentLoader<any>) =>
  defineAsyncComponent({
    loader,
    loadingComponent: AsyncSkeletonLoading,
    delay: 20,
  })
