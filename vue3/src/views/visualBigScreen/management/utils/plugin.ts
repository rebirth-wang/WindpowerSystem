import { DialogEnum } from '@vb/enums/pluginEnum'
import { maskClosable } from '@vb/settings/designSetting'
import { ElMessageBox } from 'element-plus'
import i18n from '@/lang'
import { getLoadingBarApi } from '@vb/utils/runtimeContext'

const GO_DIALOG_CLASS = 'go-visual-big-screen-dialog'
const GO_DIALOG_MODAL_CLASS = 'go-visual-big-screen-dialog-modal'
const GO_DIALOG_Z_INDEX = 2147483000

const mergeClassName = (...classNames: Array<string | undefined>) => classNames.filter(Boolean).join(' ')

const releaseDialogFocus = () => {
  const activeEl = document.activeElement as HTMLElement | null
  activeEl?.blur?.()
}

// * 开启加载
export const loadingStart = () => {
  getLoadingBarApi()?.start?.()
}

// * 加载结束
export const loadingFinish = () => {
  setTimeout(() => {
    getLoadingBarApi()?.finish?.()
  })
}

// * 加载错误
export const loadingError = () => {
  setTimeout(() => {
    getLoadingBarApi()?.error?.()
  })
}

/**
 * * render 对话框
 * @param { Object} params 配置参数, 详见 https://www.naiveui.com/zh-CN/light/components/dialog
 * ```
 * 最简易的 demo
 * goDialog({
 *    onPositiveCallback: () => {}
 * })
 * ```
 */
export const goDialog = (params: {
  // 基本
  type?: DialogEnum
  // 标题
  title?: string | (() => any)
  // 提示
  message?: string
  // 确定提示词
  positiveText?: string
  // 取消提示词
  negativeText?: string
  // 是否不展示取消按钮
  closeNegativeText?: boolean
  // 点击遮罩是否关闭
  isMaskClosable?: boolean
  // 回调
  onPositiveCallback: Function
  onNegativeCallback?: Function
  // 异步
  promise?: boolean
  promiseResCallback?: Function
  promiseRejCallback?: Function
  [T: string]: any
}) => {
  const {
    type,
    title,
    message,
    positiveText,
    negativeText,
    closeNegativeText,
    isMaskClosable,
    onPositiveCallback,
    onNegativeCallback,
    promise,
    promiseResCallback,
    promiseRejCallback,
    ...rest
  } = params

  const typeObj = {
    [DialogEnum.DELETE]: {
      type: 'warning',
      message: message || i18n.global.t('visualBigScreen.management-674210-14')
    },
    [DialogEnum.WARNING]: {
      type: 'warning',
      message: message || i18n.global.t('visualBigScreen.management-674210-1630')
    },
    [DialogEnum.ERROR]: {
      type: 'error',
      message: message || i18n.global.t('visualBigScreen.management-674210-1630')
    },
    [DialogEnum.SUCCESS]: {
      type: 'success',
      message: message || i18n.global.t('visualBigScreen.management-674210-1630')
    }
  }
  const currentType = type || DialogEnum.WARNING
  const currentDialog = typeObj[currentType]

  // 避免 aria-hidden 场景下保留焦点导致告警
  releaseDialogFocus()

  ElMessageBox({
    ...rest,
    customClass: mergeClassName(GO_DIALOG_CLASS, rest.customClass),
    modalClass: mergeClassName(GO_DIALOG_MODAL_CLASS, rest.modalClass),
    customStyle: {
      ...(rest.customStyle || {}),
      zIndex: GO_DIALOG_Z_INDEX
    },
    zIndex: GO_DIALOG_Z_INDEX,
    title: typeof title === 'function' ? title() : title || i18n.global.t('tips'),
    message: currentDialog.message,
    type: currentDialog.type as any,
    showCancelButton: !closeNegativeText,
    confirmButtonText: positiveText || i18n.global.t('confirm'),
    cancelButtonText: negativeText || i18n.global.t('cancel'),
    closeOnClickModal: isMaskClosable ?? maskClosable,
    closeOnPressEscape: isMaskClosable ?? maskClosable,
    autofocus: false,
    distinguishCancelAndClose: true,
    beforeClose: async (action, instance, done) => {
      if (action === 'confirm') {
        if (promise && onPositiveCallback) {
          instance.confirmButtonLoading = true
          try {
            const res = await onPositiveCallback()
            promiseResCallback && promiseResCallback(res)
            done()
          } catch (err) {
            promiseRejCallback && promiseRejCallback(err)
          } finally {
            instance.confirmButtonLoading = false
          }
          return
        }
        onPositiveCallback && onPositiveCallback()
        done()
        return
      }

      if (action === 'cancel') {
        onNegativeCallback && onNegativeCallback()
        done()
        return
      }

      done()
    }
  }).catch(() => undefined)
}
