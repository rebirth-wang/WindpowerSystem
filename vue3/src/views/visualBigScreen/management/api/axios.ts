import axios, { AxiosResponse, AxiosRequestConfig, Axios, AxiosError, InternalAxiosRequestConfig } from 'axios';
import { ElMessage } from 'element-plus';
import i18n from '@/lang';
import { getToken } from '@/utils/auth';
import { ResultEnum } from '@vb/enums/httpEnum';
import { PageEnum, ErrorPageNameMap } from '@vb/enums/pageEnum';
import { redirectErrorPage, routerTurnByName, isPreview } from '@vb/utils';
import { fetchAllowList } from './axios.config';

export interface MyResponseType<T> {
  code: ResultEnum;
  data: T;
  message: string;
}

export interface MyRequestInstance extends Axios {
  <T = any>(config: AxiosRequestConfig): Promise<MyResponseType<T>>;
}

const axiosInstance = axios.create({
  baseURL: `${import.meta.env.VITE_APP_BASE_API}`,
  timeout: ResultEnum.TIMEOUT,
}) as unknown as MyRequestInstance;

axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 白名单校验
    if (fetchAllowList.includes(config.url || '')) return config;

    // 获取 token
    const token = getToken();
    // 重新登录
    if (!token) {
      routerTurnByName(PageEnum.BASE_LOGIN_NAME);
      return config;
    }

    // Axios v1 的 headers 是 AxiosHeaders，优先使用 set，避免直接索引赋值在部分场景不生效
    config.headers.set?.('Authorization', `Bearer ${token}`);
    if (!config.headers.get?.('Authorization')) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (err: AxiosError) => {
    return Promise.reject(err);
  }
);

// 响应拦截器
axiosInstance.interceptors.response.use(
  (res: AxiosResponse) => {
    // 预览页面错误不进行处理
    if (isPreview()) {
      return Promise.resolve(res.data);
    }
    const { code } = res.data as { code: number };

    if (code === undefined || code === null) return Promise.resolve(res.data);

    // 成功
    if (code === ResultEnum.SUCCESS) {
      return Promise.resolve(res.data);
    }

    // 登录过期
    if (code === ResultEnum.TOKEN_OVERDUE) {
      ElMessage.error(i18n.global.t('http.token_overdue_message'));
      routerTurnByName(PageEnum.BASE_LOGIN_NAME);
      return Promise.resolve(res.data);
    }

    // 固定错误码重定向
    if (ErrorPageNameMap.get(code)) {
      redirectErrorPage(code);
      return Promise.resolve(res.data);
    }

    // 提示错误
    ElMessage.error(i18n.global.t((res.data as any).msg));
    return Promise.resolve(res.data);
  },
  (err: AxiosError) => {
    const status = err.response?.status;
    switch (status) {
      case 401:
        routerTurnByName(PageEnum.BASE_LOGIN_NAME);
        return Promise.reject(err);

      default:
        return Promise.reject(err);
    }
  }
);

export default axiosInstance;
