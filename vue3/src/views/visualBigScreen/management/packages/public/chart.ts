import merge from 'lodash/merge'
import pick from 'lodash/pick'
import { EchartsDataType } from '../index.d'
import { globalThemeJson } from '@vb/settings/chartThemes/index'
import type VChart from 'vue-echarts'

const LEGACY_STYLE_KEYS = new Set(['label', 'lineStyle', 'itemStyle', 'areaStyle'])

const normalizeLegacyStyle = (value: any) => {
  if (!value || typeof value !== 'object' || Array.isArray(value)) return
  if (value.normal && typeof value.normal === 'object' && !Array.isArray(value.normal)) {
    Object.keys(value.normal).forEach(key => {
      if (value[key] === undefined) value[key] = value.normal[key]
    })
    delete value.normal
  }
  if (value.textStyle && typeof value.textStyle === 'object' && !Array.isArray(value.textStyle)) {
    Object.keys(value.textStyle).forEach(key => {
      if (value[key] === undefined) value[key] = value.textStyle[key]
    })
    delete value.textStyle
  }
}

const sanitizeEchartsLegacyOption = (node: any) => {
  if (!node || typeof node !== 'object') return
  if (Array.isArray(node)) {
    node.forEach(item => sanitizeEchartsLegacyOption(item))
    return
  }
  normalizeLegacyStyle(node)
  Object.keys(node).forEach(key => {
    const value = node[key]
    if (LEGACY_STYLE_KEYS.has(key)) normalizeLegacyStyle(value)
    sanitizeEchartsLegacyOption(value)
  })
}

const cloneOption = <T>(data: T): T => {
  if (typeof structuredClone === 'function') {
    try {
      return structuredClone(data)
    } catch {
      return merge({}, data)
    }
  }
  try {
    return JSON.parse(JSON.stringify(data))
  } catch {
    return merge({}, data)
  }
}

/**
 * * 合并 color 和全局配置项
 * @param option 配置
 * @param themeSetting 设置
 * @param excludes 排除元素
 * @returns object
 */
export const mergeTheme = <T, U>(option: T, themeSetting: U, includes: string[]) => {
  const merged = merge({}, pick(themeSetting, includes), option)
  const safeMerged = cloneOption(merged)
  sanitizeEchartsLegacyOption(safeMerged)
  return safeMerged
}

/**
 * * ECharts option 统一前置处理
 * @param option
 * @return option
 */
export const echartOptionProfixHandle = (option: any, includes: string[] = []) => {
  option['backgroundColor'] = 'rgba(0,0,0,0)'
  return mergeTheme(option, globalThemeJson, includes)
}

/**
 * * 设置数据
 * @param option
 * @return option
 */
export const setData = (option: any, data: EchartsDataType) => {
  option.dataset = data
  return option
}

/**
 * * 配置公共 setOption 方法
 * @param instance
 * @param data
 */
export const setOption = <T extends typeof VChart | undefined, D>(instance: T, data: D, notMerge = true) => {
  if (!instance) return
  const option = instance.getOption()
  option.dataset = null
  const nextOption = cloneOption(data)
  sanitizeEchartsLegacyOption(nextOption)
  if (nextOption && typeof nextOption === 'object') {
    ;(nextOption as any).renderer = 'canvas'
  }
  instance.setOption(nextOption, {
    notMerge: notMerge
  })
}
