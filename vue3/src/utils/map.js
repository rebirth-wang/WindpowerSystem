import { getConfigFull } from '@/api/system/config';
import BaiduMap from 'vue-baidu-map-3x';

function setMeta(reject) {
  const protocolStr = document.location.protocol;
  if (protocolStr === 'https:') {
    const meta = document.createElement('meta');
    meta.httpEquiv = 'Content-Security-Policy';
    meta.content = 'upgrade-insecure-requests';
    meta.onerror = reject;
    document.head.appendChild(meta);
  }
}

// Base64 解密
function decodeBase64(str) {
  try {
    return window.atob(str);
  } catch (e) {
    console.error('Base64 解密失败:', e);
    return str;
  }
}

// 统一获取地图 Key（自动处理加密）
async function getMapKey(configKey) {
  const res = await getConfigFull(configKey);
  const { configValue } = res.data || {};
  const { isEncryption } = res.data || {};
  if (isEncryption !== 1) {
    console.log('当前参数无需解密');
    return configValue;
  }
  if (!configValue) {
    throw new Error(`${configKey} 未获取到 key`);
  }
  return decodeBase64(configValue);
}

/**
 * ===============================
 * 百度地图初始化(组态) - Vue3版本
 * ===============================
 */
export async function initBaiduMap(app) {
  try {
    const ak = await getMapKey('sys.env.baidumap.key');
    if (app) {
      app.use(BaiduMap, { ak });
    }
    return true;
  } catch (error) {
    console.error('初始化百度地图失败:', error);
    throw error;
  }
}

/**
 * ===============================
 * 百度地图 JS SDK 加载
 * ===============================
 */
let baiduMapPromise = null;

export function loadBMap() {
  if (baiduMapPromise) return baiduMapPromise;

  baiduMapPromise = new Promise(async (resolve, reject) => {
    if (typeof window.BMap !== 'undefined') {
      resolve(window.BMap);
      return;
    }
    try {
      const ak = await getMapKey('sys.env.baidumap.key');

      window.onBMapCallback = function () {
        resolve(window.BMap);
      };

      const script = document.createElement('script');
      script.type = 'text/javascript';
      script.src = `https://api.map.baidu.com/api?v=3.0&ak=${ak}&callback=onBMapCallback`;
      script.onerror = reject;

      document.head.appendChild(script);
    } catch (e) {
      console.error('加载百度地图失败:', e);
      reject(e);
    }
  });

  return baiduMapPromise;
}

/**
 * ===============================
 * 天地图加载
 * ===============================
 */
export function loadTianDiTu() {
  return new Promise(async (resolve, reject) => {
    setMeta(reject);

    if (window.T && window.T.Map && window.T.LngLat) {
      resolve(window.T);
      return;
    }

    try {
      const tk = await getMapKey('sys.env.tianmap.key');
      window.onTianDiTuCallback = function () {
        if (window.T && window.T.Map && window.T.LngLat) {
          resolve(window.T);
        } else {
          reject(new Error('天地图核心对象缺失'));
        }
      };

      const script = document.createElement('script');
      script.src = `https://api.tianditu.gov.cn/api?v=4.0&tk=${tk}&callback=onTianDiTuCallback`;
      script.type = 'text/javascript';
      script.onload = () => resolve(window.T);
      script.onerror = () => reject(new Error('天地图API加载失败'));
      document.head.appendChild(script);
    } catch (e) {
      reject(e);
    }
  });
}

/**
 * ===============================
 * 谷歌地图加载
 * ===============================
 */
let googleMapPromise = null;

export function loadGMap() {
  if (googleMapPromise) return googleMapPromise;

  googleMapPromise = new Promise(async (resolve, reject) => {
    if (typeof window.google !== 'undefined') {
      resolve(window.google);
      return;
    }

    try {
      const ak = await getMapKey('sys.env.googlemap.key');

      window.onGMapCallback = function () {
        resolve(window.google);
      };

      const script = document.createElement('script');
      script.type = 'text/javascript';
      script.async = true;
      script.defer = true;
      script.src = `https://maps.googleapis.com/maps/api/js?key=${ak}&callback=onGMapCallback&loading=async&libraries=marker`;
      script.onerror = reject;

      document.head.appendChild(script);
    } catch (e) {
      console.error('加载谷歌地图失败:', e);
      reject(e);
    }
  });

  return googleMapPromise;
}
