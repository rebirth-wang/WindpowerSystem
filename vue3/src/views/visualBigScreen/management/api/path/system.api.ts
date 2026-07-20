import { http } from '@vb/api/http'
import { httpErrorHandle } from '@vb/utils'
import { RequestHttpEnum, ModuleTypeEnum } from '@vb/enums/httpEnum'

// 获取验证码
export const getCodeImg = async () => {
  try {
    const res = await http(RequestHttpEnum.GET)(`captchaImage`)
    return res
  } catch (err) {
    httpErrorHandle()
  }
}

// * 登录
export const loginApi = async (data: object) => {
  try {
    const res = await http(RequestHttpEnum.POST)(`login`, data)
    return res
  } catch (err) {
    httpErrorHandle()
  }
}

// 根据物美token置换此项目需要的缓存
export const getUserInfoApi = async () => {
  try {
    const res = await http(RequestHttpEnum.GET)(`getInfo`)
    return res
  } catch (err) {
    httpErrorHandle()
  }
}

// * 登出
export const logoutApi = async () => {
  try {
    const res = await http(RequestHttpEnum.POST)(`logout`)
    return res
  } catch (err) {
    httpErrorHandle()
  }
}

// * 获取 oss 上传接口
export const ossUrlApi = async (data: object) => {
  try {
    const res = `${ModuleTypeEnum.PROJECT}/upload`
    return res
  } catch (err) {
    httpErrorHandle()
  }
}

// 根据参数键名查询参数值
export const getConfigKey = async (key: string) => {
  try {
    const res = await http(RequestHttpEnum.GET)(`system/config/configKey/` + key)
    return res
  } catch (err) {
    httpErrorHandle()
  }
}
