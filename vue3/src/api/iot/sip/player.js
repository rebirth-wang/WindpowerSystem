import request from '@/utils/request';

// 开始播放
export function startPlay(deviceId, channelId) {
  return request({
    url: '/sip/player/play/' + deviceId + '/' + channelId,
    method: 'get',
  });
}

// 获取流信息
export function getStreaminfo(deviceId, channelId) {
  return request({
    url: '/sip/player/getUrl/' + deviceId + '/' + channelId,
    method: 'get',
  });
}

export function playback(deviceId, channelId, query) {
  return request({
    url: '/sip/player/playback/' + deviceId + '/' + channelId,
    method: 'get',
    params: query,
  });
}

export function closeStream(deviceId, channelId, streamId) {
  return request({
    url: '/sip/player/closeStream/' + deviceId + '/' + channelId + '/' + streamId,
    method: 'get',
  });
}

export function closeStreamDirect(deviceId, channelId, streamId) {
  return request({
    url: '/sip/player/closeStreamDirect/' + deviceId + '/' + channelId + '/' + streamId,
    method: 'get',
  });
}

export function listShareLink(deviceId, channelId) {
  return request({
    url: '/sip/player/getUrl/' + deviceId + '/' + channelId,
    method: 'get',
  });
}

export function playbackPause(deviceId, channelId, streamId) {
  return request({
    url: '/sip/player/playbackPause/' + deviceId + '/' + channelId + '/' + streamId,
    method: 'get',
  });
}

export function playbackReplay(deviceId, channelId, streamId) {
  return request({
    url: '/sip/player/playbackReplay/' + deviceId + '/' + channelId + '/' + streamId,
    method: 'get',
  });
}

export function playbackSeek(deviceId, channelId, streamId, query) {
  return request({
    url: '/sip/player/playbackSeek/' + deviceId + '/' + channelId + '/' + streamId,
    method: 'get',
    params: query,
  });
}

//倍速播放
export function playbackSpeed(deviceId, channelId, streamId, query) {
  return request({
    url: '/sip/player/playbackSpeed/' + deviceId + '/' + channelId + '/' + streamId,
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

export function stopBroadcast(deviceId, channelId) {
  return request({
    url: '/sip/talk/broadcast/stop/' + deviceId + '/' + channelId,
    method: 'get',
  });
}
