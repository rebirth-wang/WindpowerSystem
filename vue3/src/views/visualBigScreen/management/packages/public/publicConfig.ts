import { getUUID } from '@vb/utils'
import { RequestConfigType } from '@vb/store/modules/chartEditStore/chartEditStore.d'
import i18n from '@/lang'
import { BaseEvent, EventLife } from '@vb/enums/eventEnum'
import {
  RequestHttpEnum,
  RequestDataTypeEnum,
  RequestHttpIntervalEnum,
  RequestContentTypeEnum,
  RequestBodyEnum
} from '@vb/enums/httpEnum'
import {
  ChartFrameEnum,
  PublicConfigType,
  CreateComponentType,
  CreateComponentGroupType
} from '@vb/packages/index.d'
import { chartInitConfig } from '@vb/settings/designSetting'
import cloneDeep from 'lodash/cloneDeep'

// 请求基础属性
export const requestConfig: RequestConfigType = {
  requestDataType: RequestDataTypeEnum.STATIC,
  requestHttpType: RequestHttpEnum.GET,
  requestUrl: '',
  requestInterval: undefined,
  requestIntervalUnit: RequestHttpIntervalEnum.SECOND,
  requestContentType: RequestContentTypeEnum.DEFAULT,
  requestParamsBodyType: RequestBodyEnum.NONE,
  requestSQLContent: {
    sql: 'select * from  where'
  },
  requestParams: {
    Body: {
      'form-data': {},
      'x-www-form-urlencoded': {},
      json: '',
      xml: ''
    },
    Header: {},
    Params: {}
  }
}

// 单实例类
export class PublicConfigClass implements PublicConfigType {
  public id = getUUID()
  public isGroup = false
  // 基本信息
  public attr = { ...chartInitConfig, zIndex: -1 }
  // 基本样式
  public styles = {
    // 使用滤镜
    filterShow: false,
    // 色相
    hueRotate: 0,
    // 饱和度
    saturate: 1,
    // 对比度
    contrast: 1,
    // 亮度
    brightness: 1,
    // 透明
    opacity: 1,

    // 旋转
    rotateZ: 0,
    rotateX: 0,
    rotateY: 0,

    // 倾斜
    skewX: 0,
    skewY: 0,

    // 混合模式
    blendMode: 'normal',

    // 动画
    animations: []
  }
  // 预览
  public preview = {
    overFlowHidden: false
  }
  // 状态
  public status = {
    lock: false,
    hide: false
  }
  // 请求
  public request = cloneDeep(requestConfig)
  // 数据过滤
  public filter = undefined
  // 事件
  public events = {
    baseEvent: {
      [BaseEvent.ON_CLICK]: undefined,
      [BaseEvent.ON_DBL_CLICK]: undefined,
      [BaseEvent.ON_MOUSE_ENTER]: undefined,
      [BaseEvent.ON_MOUSE_LEAVE]: undefined
    },
    advancedEvents: {
      [EventLife.VNODE_MOUNTED]: undefined,
      [EventLife.VNODE_BEFORE_MOUNT]: undefined
    },
    interactEvents: []
  }
}

// 多选成组类
export class PublicGroupConfigClass extends PublicConfigClass implements CreateComponentGroupType {
  // 成组
  public isGroup = true
  // 名称
  public chartConfig = {
    key: 'group',
    chartKey: 'group',
    conKey: 'group',
    category: 'group',
    categoryName: 'group',
    package: 'group',
    chartFrame: ChartFrameEnum.COMMON,
    title: i18n.global.t('visualBigScreen.management-674210-1703'),
    image: ''
  }
  // 组成员列表
  public groupList: Array<CreateComponentType> = []
  // ---- 原有 ---
  // key
  public key = 'group'
  // 配置
  public option = {}
  // 标识
  public id = getUUID()
  // 基本信息
  public attr = { w: 0, h: 0, x: 0, y: 0, offsetX: 0, offsetY: 0, zIndex: -1 }
}
