import request from '@/utils/request';

// 开始播放
export function playCom(deviceId, channelId, record) {
  return request({
    url: '/common/player/play',
    method: 'get',
    params: {
      deviceId,
      channelId,
      record,
    },
  });
}

// 直播播放停止
export function stopPlayCom(deviceId, channelId, record) {
  return request({
    url: '/common/player/play/stop',
    method: 'get',
    params: {
      deviceId,
      channelId,
      record,
    },
  });
}

// 设备录像查询
export function queryRecordCom(query) {
  return request({
    url: '/common/player/playback/query',
    method: 'get',
    params: query,
  });
}

// 设备录像回放播放
export function playbackCom(query) {
  return request({
    url: '/common/player/playback',
    method: 'get',
    params: query,
  });
}

// 设备录像回放停止
export function stopPlaybackCom(query) {
  return request({
    url: '/common/player/playback/stop',
    method: 'get',
    params: query,
  });
}

// 回放暂停
export function pausePlaybackCom(query) {
  return request({
    url: '/common/player/playback/pause',
    method: 'get',
    params: query,
  });
}

// 回放恢复
export function resumePlaybackCom(query) {
  return request({
    url: '/common/player/playback/resume',
    method: 'get',
    params: query,
  });
}

// 录像回放定位
export function seekPlaybackCom(query) {
  return request({
    url: '/common/player/playback/seek',
    method: 'get',
    params: query,
  });
}

// 录像倍速播放
export function speedPlaybackCom(query) {
  return request({
    url: '/common/player/playback/speed',
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

// 获取分享链接
export function listShareLink(deviceId, channelId) {
  return request({
    url: '/sip/player/getUrl/' + deviceId + '/' + channelId,
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

// 获取推流地址
export function getPushUrl(deviceId, channelId) {
  return request({
    url: '/sip/talk/getPushUrl/' + deviceId + '/' + channelId,
    method: 'get',
  });
}

// 开始对讲
export function startBroadcast(deviceId, channelId) {
  return request({
    url: '/sip/talk/broadcast/' + deviceId + '/' + channelId,
    method: 'get',
  });
}

// 停止对讲
export function stopBroadcast(deviceId, channelId) {
  return request({
    url: '/sip/talk/broadcast/stop/' + deviceId + '/' + channelId,
    method: 'get',
  });
}

// 开始代理拉流
export function startProxy(deviceId, channelId) {
  return request({
    url: '/common/player/proxy/start?deviceId=' + deviceId + '&channelId=' + channelId,
    method: 'get',
  });
}

// 停止代理拉流
export function stopProxy(deviceId, channelId) {
  return request({
    url: '/common/player/proxy/stop?deviceId=' + deviceId + '&channelId=' + channelId,
    method: 'get',
  });
}

// 代理拉流中
export function pullProxy(deviceId, channelId) {
  return request({
    url: '/common/player/proxy/pulling?deviceId=' + deviceId + '&channelId=' + channelId,
    method: 'get',
  });
}
