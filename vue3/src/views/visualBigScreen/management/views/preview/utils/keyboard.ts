import { setKeyboardActive } from '@vb/utils/runtimeContext'

// 处理键盘记录
export const keyRecordHandle = () => {
  setKeyboardActive('ctrl', false)
  setKeyboardActive('space', false)

  document.onkeydown = (e: KeyboardEvent) => {
    const { keyCode } = e
    if (keyCode == 32 && e.target == document.body) e.preventDefault()

    if ([17, 32].includes(keyCode)) {
      switch (keyCode) {
        case 17:
          setKeyboardActive('ctrl', true)
          break
        case 32:
          setKeyboardActive('space', true)
          const previewBoxDom = document.querySelector('.go-preview') as HTMLElement
          if (previewBoxDom && previewBoxDom.style.position === 'absolute') {
            previewBoxDom.style.cursor = 'move'
          }
          break
      }
    }
  }

  document.onkeyup = (e: KeyboardEvent) => {
    const { keyCode } = e
    if (keyCode == 32 && e.target == document.body) e.preventDefault()

    if ([17, 32].includes(keyCode)) {
      switch (keyCode) {
        case 17:
          setKeyboardActive('ctrl', false)
          break
        case 32:
          setKeyboardActive('space', false)
          break
      }
    }

    const previewBoxDom = document.querySelector('.go-preview') as HTMLElement
    if (previewBoxDom) {
      previewBoxDom.style.cursor = 'default'
    }
  }
}
