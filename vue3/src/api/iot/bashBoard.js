import request from '@/utils/request';

// 查询大屏配置数据
export function getBashBoardMatchsData(query) {
  return request({
    url: '/bashBoard/matchsData',
    method: 'get',
    params: query,
  });
}

// 生成视频（MP4）的链接
export function getVideoUrl(deviceId, channelId) {
  return request({
    url: '/sip/mediaserver/matchsData/' + deviceId + '/' + channelId,
    method: 'get',
  });
}
