import request from '@/utils/request';

export function getDevRecord(deviceId, channelId, query) {
  return request({
    url: '/common/player/playback/query',
    method: 'get',
    params: {
      deviceId,
      channelId,
      startTime: query.start,
      endTime: query.end,
    },
  });
}

export function getServerRecord(query) {
  return request({
    url: '/record/serverRecord/list',
    method: 'get',
    params: query,
  });
}

export function getServerRecordByDate(query) {
  return request({
    url: '/record/serverRecord/date/list',
    method: 'get',
    params: query,
  });
}

export function getServerRecordByStream(query) {
  return request({
    url: '/record/serverRecord/stream/list',
    method: 'get',
    params: query,
  });
}

export function getServerRecordByApp(query) {
  return request({
    url: '/record/serverRecord/app/list',
    method: 'get',
    params: query,
  });
}

export function getServerRecordByFile(query) {
  return request({
    url: '/record/serverRecord/file/list',
    method: 'get',
    params: query,
  });
}

export function getServerRecordByDevice(query) {
  return request({
    url: '/record/serverRecord/device/list',
    method: 'get',
    params: query,
  });
}

export function startPlayRecord(deviceId, channelId, record) {
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

export function getServerRecordByChannel(query) {
  return request({
    url: '/record/serverRecord/channel/list',
    method: 'get',
    params: query,
  });
}

export function startDownloadRecord(deviceId, channelId, query) {
  return request({
    url: '/record/download/' + deviceId + '/' + channelId,
    method: 'get',
    params: query,
  });
}

export function uploadRecord(query) {
  return request({
    url: '/record/upload',
    method: 'get',
    params: query,
  });
}
