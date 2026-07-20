import request from '@/utils/request';

// 查询监控设备通道信息列表
export function listChannel(query) {
  return request({
    url: '/sip/channel/list',
    method: 'get',
    params: query,
  });
}

// 查询监控设备通道信息详细
export function getChannel(channelId) {
  return request({
    url: '/sip/channel/' + channelId,
    method: 'get',
  });
}

// 新增监控设备通道信息
export function addChannel(createNum, data) {
  return request({
    url: '/sip/channel/' + createNum,
    method: 'post',
    data: data,
  });
}

// 修改监控设备通道信息
export function updateChannel(data) {
  return request({
    url: '/sip/channel',
    method: 'put',
    data: data,
  });
}

// 删除监控设备通道信息
export function delChannel(channelId) {
  return request({
    url: '/sip/channel/' + channelId,
    method: 'delete',
  });
}

// 开始播放
export function startPlay(deviceId, channelId) {
  return request({
    url: '/sip/player/play/' + deviceId + '/' + channelId,
    method: 'get',
  });
}

// 回放播放
export function playback(deviceId, channelId, query) {
  return request({
    url: '/sip/player/playback/' + deviceId + '/' + channelId,
    method: 'get',
    params: query,
  });
}

// 关闭流
export function closeStream(deviceId, channelId, streamId) {
  return request({
    url: '/sip/player/closeStream/' + deviceId + '/' + channelId + '/' + streamId,
    method: 'get',
  });
}

// 强制关闭拉流
export function closeStreamDirect(deviceId, channelId, streamId) {
  return request({
    url: '/sip/player/closeStreamDirect/' + deviceId + '/' + channelId + '/' + streamId,
    method: 'get',
  });
}

// 倍速播放
export function playbackSpeed(deviceId, channelId, streamId, query) {
  return request({
    url: '/sip/player/playbackSpeed/' + deviceId + '/' + channelId + '/' + streamId,
    method: 'get',
    params: query,
  });
}

// 监控设备绑定设备或场景
export function binding(data) {
  return request({
    url: '/iot/relation/addOrUp',
    method: 'post',
    data: data,
  });
}
// 通过设备或场景查询监控设备通道信息
export function listRelDeviceOrScene(query) {
  return request({
    url: '/sip/channel/listRelDeviceOrScene',
    method: 'get',
    params: query,
  });
}

export function getPushUrl(deviceId, channelId) {
  return request({
    url: '/sip/talk/getPushUrl/' + deviceId + '/' + channelId,
    method: 'get',
  });
}

export function startBroadcast(deviceId, channelId) {
  return request({
    url: '/sip/talk/broadcast/' + deviceId + '/' + channelId,
    method: 'get',
  });
}

// 开始代理拉流
export function startProxy(deviceSipId, channelSipId) {
  return request({
    url: '/sip/channel/proxy/start?deviceSipId=' + deviceSipId + '&channelSipId=' + channelSipId,
    method: 'get',
  });
}

// 停止代理拉流
export function stopProxy(deviceSipId, channelSipId) {
  return request({
    url: '/sip/channel/proxy/stop?deviceSipId=' + deviceSipId + '&channelSipId=' + channelSipId,
    method: 'get',
  });
}

// 查询监控设备的分享链接列表
export function listShareLink(deviceId, channelId) {
  return request({
    url: '/sip/player/getUrl/' + deviceId + '/' + channelId,
    method: 'get',
  });
}
