import axios from 'axios';
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { ElNotification, ElMessageBox, ElMessage, ElLoading } from 'element-plus';
import { getToken } from '@/utils/auth';
import errorCode from '@/utils/errorCode';
import { tansParams, blobValidate } from '@/utils/ruoyi';
import { saveAs } from 'file-saver';
import { useUserStore } from '@/stores/modules/user';
import { useSettingsStore } from '@/stores/modules/settings';
import router from '@/router';
import { attachRequestId } from '@/utils/requestTrace';

/** API response wrapper used by RuoYi backend */
export interface ApiResponse<T = any> {
  code: number;
  msg: string;
  data: T;
  rows?: T[];
  total?: number;
}

let downloadLoadingInstance: any;
const pendingMap = new Map<string, AbortController>();

/** Controls the re-login dialog to prevent showing multiple times */
export const isRelogin = { show: false };

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8';

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 20000,
});

/** Request interceptor — attach token, language header, and prevent duplicate submissions */
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    attachRequestId(config);

    // Attach language header from settings store
    try {
      const settingsStore = useSettingsStore();
      config.headers.language = settingsStore.language;
    } catch (_e) {
      // Store not initialized yet — ignore
    }

    const isToken = config.headers?.isToken === false;
    const isRepeatSubmit = config.headers?.repeatSubmit === false;

    if (getToken() && !isToken) {
      config.headers['Authorization'] = 'Bearer ' + getToken();
    }

    // Support share token from URL params (for SCADA sharing)
    const search = new URLSearchParams(window.location.search);
    const share = search.get('share');
    if (share && !isToken) {
      config.headers['Authorization'] = 'Bearer ' + share;
    }

    // Serialize GET params
    if (config.method === 'get' && config.params) {
      const param = tansParams(config.params);
      config.url = param ? `${config.url}?${param}` : config.url;
      config.params = {};
    }

    // Prevent duplicate POST/PUT submissions
    if (!isRepeatSubmit && (config.method === 'post' || config.method === 'put')) {
      const controller = new AbortController();
      config.signal = controller.signal;
      const key = `${config.method}${config.url}?${JSON.stringify(config.data)}`;
      if (pendingMap.has(key)) {
        pendingMap.get(key)!.abort();
        pendingMap.delete(key);
      } else {
        pendingMap.set(key, controller);
      }
    }

    return config;
  },
  (error) => Promise.reject(error)
);

/**
 * Response interceptor — unwrap API envelope and handle error codes.
 * NOTE: The interceptor returns `res.data` (ApiResponse) instead of the full
 * AxiosResponse. This is intentional (RuoYi convention) so callers receive
 * the business payload directly. We use `as any` to satisfy Axios's strict
 * interceptor signature while preserving this behaviour.
 */
service.interceptors.response.use(
  ((res: AxiosResponse<ApiResponse>) => {
    const code = res.data.code || 200;
    const msg = errorCode[code] || res.data.msg || errorCode['default'];

    // Pass through binary responses without processing
    if (res.request.responseType === 'blob' || res.request.responseType === 'arraybuffer') {
      return res.data;
    }

    if (code === 401) {
      if (!isRelogin.show) {
        isRelogin.show = true;
        ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            isRelogin.show = false;
            const userStore = useUserStore();
            userStore.LogOut().then(() => {
              router.push('/login');
            });
          })
          .catch(() => {
            isRelogin.show = false;
          });
      }
      return Promise.reject('无效的会话，或者会话已过期，请重新登录。');
    } else if (code === 500) {
      ElMessage.error(msg);
      return Promise.reject(new Error(msg || 'error'));
    } else if (code === 601) {
      ElMessage.warning(msg);
      return Promise.reject('error');
    } else if (code === 403) {
      ElNotification.error({ title: msg });
      return Promise.reject({ code });
    } else if (code !== 200) {
      ElNotification.error({ title: msg });
      return Promise.reject('error');
    } else {
      return res.data;
    }
  }) as any,
  (error) => {
    let { message } = error;
    if (message === 'Network Error') {
      message = '后端接口连接异常';
    } else if (message?.includes('timeout')) {
      message = '系统接口请求超时';
    } else if (message?.includes('Request failed with status code')) {
      message = '系统接口' + message.substr(message.length - 3) + '异常';
    }
    if (message !== 'canceled') {
      console.error(message, error);
      ElMessage.error({ message, duration: 5000 });
    }
    return Promise.reject(error);
  }
);

/** Download file via POST request */
export function download(url: string, params: any, filename: string, config?: AxiosRequestConfig) {
  downloadLoadingInstance = ElLoading.service({ text: '正在下载数据，请稍候', background: 'rgba(0, 0, 0, 0.7)' });
  return service
    .post(url, params, {
      transformRequest: [
        (params: any) => {
          return tansParams(params);
        },
      ],
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      responseType: 'blob',
      ...config,
    })
    .then(async (data: any) => {
      const isValid = await blobValidate(data);
      if (isValid) {
        const blob = new Blob([data]);
        saveAs(blob, filename);
      } else {
        const resText = await data.text();
        const rspObj = JSON.parse(resText);
        const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default'];
        ElMessage.error(errMsg);
      }
      downloadLoadingInstance.close();
    })
    .catch((error: any) => {
      console.error(error);
      ElMessage.error('下载文件出现错误，请联系管理员！');
      downloadLoadingInstance.close();
    });
}

export default service;
