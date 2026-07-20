import request from '@/utils/request';

// 获取证书有效期
export function getLicenseInfo() {
  return request({
    url: '/license/validate',
    method: 'get',
  });
}

//安装许可证
export function installLicense(data) {
  return request({
    url: '/license/install',
    method: 'post',
    data,
    timeout: 10000,
  });
}

//获取服务器信息
export function getServerInfo(query) {
  return request({
    url: '/license/getServerInfo',
    method: 'get',
    params: query,
  });
}
