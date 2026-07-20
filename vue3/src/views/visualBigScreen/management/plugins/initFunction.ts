import { setHtmlTheme } from '@vb/utils'

/**
 * * 页面初始化就执行的函数
 */
export const initFunction = () => {
  // 初始化主题（与子项目一致）
  setHtmlTheme()

  // 避免焦点停留在 aria-hidden 容器引发浏览器告警
  document.addEventListener(
    'focusin',
    () => {
      const activeEl = document.activeElement as HTMLElement | null
      if (!activeEl) return
      const hiddenAncestor = activeEl.closest('[aria-hidden="true"]')
      if (hiddenAncestor) {
        activeEl.blur?.()
      }
    },
    true
  )

  const blurIfFocusedInAriaHidden = () => {
    const activeEl = document.activeElement as HTMLElement | null
    if (!activeEl) return
    if (activeEl.closest('[aria-hidden="true"]')) {
      activeEl.blur?.()
    }
  }

  new MutationObserver(() => {
    blurIfFocusedInAriaHidden()
  }).observe(document.body, {
    subtree: true,
    childList: true,
    attributes: true,
    attributeFilter: ['aria-hidden']
  })

  // 捕获全局错误
  window.addEventListener('unhandledrejection', event => {
    console.warn(`UNHANDLED PROMISE REJECTION: ${event.reason}`)
  })
}