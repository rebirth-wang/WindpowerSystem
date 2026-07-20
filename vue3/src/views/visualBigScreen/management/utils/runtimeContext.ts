import type { App } from 'vue'

type LoadingBarApi = {
  start?: () => void
  finish?: () => void
  error?: () => void
}

type MessageApi = {
  error?: (content: string) => void
}

type KeyboardActiveState = {
  ctrl: boolean
  space: boolean
}

let appInstance: App<Element> | null = null
let loadingBarApi: LoadingBarApi | null = null
let dialogApi: any = null
let messageApi: MessageApi | null = null

const keyboardActive: KeyboardActiveState = {
  ctrl: false,
  space: false
}

const spacePressHoldListeners = new Set<(isHold: boolean) => void>()

export const setAppInstance = (app: App<Element>) => {
  appInstance = app
}

export const getAppInstance = () => appInstance

export const setLoadingBarApi = (api: LoadingBarApi) => {
  loadingBarApi = api
}

export const getLoadingBarApi = () => loadingBarApi

export const setDialogApi = (api: any) => {
  dialogApi = api
}

export const getDialogApi = () => dialogApi

export const setMessageApi = (api: MessageApi) => {
  messageApi = api
}

export const getMessageApi = () => messageApi

export const getKeyboardActive = () => keyboardActive

export const setKeyboardActive = (key: keyof KeyboardActiveState, value: boolean) => {
  keyboardActive[key] = value
}

export const emitSpacePressHold = (isHold: boolean) => {
  spacePressHoldListeners.forEach(listener => listener(isHold))
}

export const onSpacePressHold = (listener: (isHold: boolean) => void) => {
  spacePressHoldListeners.add(listener)
  return () => {
    spacePressHoldListeners.delete(listener)
  }
}
