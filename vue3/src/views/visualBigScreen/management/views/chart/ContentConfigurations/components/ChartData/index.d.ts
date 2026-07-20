import {
  RequestHttpEnum,
  RequestHttpIntervalEnum,
  RequestDataTypeEnum,
  RequestContentTypeEnum,
  RequestParamsTypeEnum,
  SelectHttpTimeNameObj
} from '@vb/enums/httpEnum'

// 匹配结果
export enum DataResultEnum {
  NULL = 0,
  SUCCESS = 1,
  FAILURE = 2
}

export enum TimelineTitleEnum {
  FILTER = 'FILTER',
  MAPPING = 'MAPPING',
  CONTENT = 'CONTENT'
}

export const TimelineTitleLangKey = {
  [TimelineTitleEnum.FILTER]: 'visualBigScreen.management-674210-817',
  [TimelineTitleEnum.MAPPING]: 'visualBigScreen.management-674210-818',
  [TimelineTitleEnum.CONTENT]: 'visualBigScreen.management-674210-819'
}

export const RequestContentTypeLangKey = {
  [RequestContentTypeEnum.DEFAULT]: 'visualBigScreen.management-674210-821',
  [RequestContentTypeEnum.SQL]: 'visualBigScreen.management-674210-822'
}

export const RequestParamsTypeLangKey = {
  [RequestParamsTypeEnum.PARAMS]: 'visualBigScreen.management-674210-660',
  [RequestParamsTypeEnum.BODY]: 'visualBigScreen.management-674210-885',
  [RequestParamsTypeEnum.HEADER]: 'visualBigScreen.management-674210-886'
}

export enum SelectCreateDataEnum {
  STATIC = 'STATIC',
  AJAX = 'AJAX',
  Pond = 'Pond'
}

export const SelectCreateDataLangKey = {
  [SelectCreateDataEnum.STATIC]: 'visualBigScreen.management-674210-849',
  [SelectCreateDataEnum.AJAX]: 'visualBigScreen.management-674210-850',
  [SelectCreateDataEnum.Pond]: 'visualBigScreen.management-674210-851'
}

export interface SelectCreateDataType {
  label: string
  value: RequestDataTypeEnum
  disabled?: boolean
}

// ajax 请求类型
export interface SelectHttpType {
  label: RequestHttpEnum
  value: RequestHttpEnum
  disabled?: boolean
  style?: object
}

// 类型选项
export const selectTypeOptions: SelectHttpType[] = [
  {
    label: RequestHttpEnum.GET,
    value: RequestHttpEnum.GET,
    style: {
      color: 'greenyellow',
      fontWeight: 'bold'
    }
  },
  {
    label: RequestHttpEnum.POST,
    value: RequestHttpEnum.POST,
    style: {
      color: 'skyblue',
      fontWeight: 'bold'
    }
  },
  {
    label: RequestHttpEnum.PUT,
    value: RequestHttpEnum.PUT,
    style: {
      color: 'goldenrod',
      fontWeight: 'bold'
    }
  },
  {
    label: RequestHttpEnum.PATCH,
    value: RequestHttpEnum.PATCH,
    style: {
      color: 'violet',
      fontWeight: 'bold'
    }
  },
  {
    label: RequestHttpEnum.DELETE,
    value: RequestHttpEnum.DELETE,
    disabled: true,
    style: {
      fontWeight: 'bold'
    }
  }
]

// ajax 请求间隔
export interface SelectHttpTimeType {
  label: string
  value: RequestHttpIntervalEnum
  disabled?: boolean
}

// 时间选项
export const selectTimeOptions: SelectHttpTimeType[] = [
  {
    label: SelectHttpTimeNameObj[RequestHttpIntervalEnum.SECOND],
    value: RequestHttpIntervalEnum.SECOND
  },
  {
    label: SelectHttpTimeNameObj[RequestHttpIntervalEnum.MINUTE],
    value: RequestHttpIntervalEnum.MINUTE
  },
  {
    label: SelectHttpTimeNameObj[RequestHttpIntervalEnum.HOUR],
    value: RequestHttpIntervalEnum.HOUR
  },
  {
    label: SelectHttpTimeNameObj[RequestHttpIntervalEnum.DAY],
    value: RequestHttpIntervalEnum.DAY
  }
]
