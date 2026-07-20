import request from '@/utils/request';

// 查询会话记录列表
export function listChatRecord(query) {
  return request({
    url: '/ai/chatRecord/list',
    method: 'get',
    params: query,
  });
}

// 查询会话记录详情
export function getChatRecord(sessionId) {
  return request({
    url: '/ai/chatRecord/' + sessionId,
    method: 'get',
  });
}

// 归档会话记录
export function archiveChatRecord(sessionId) {
  return request({
    url: '/ai/chatRecord/' + sessionId + '/archive',
    method: 'put',
  });
}

// 取消归档会话记录
export function unarchiveChatRecord(sessionId) {
  return request({
    url: '/ai/chatRecord/' + sessionId + '/unarchive',
    method: 'put',
  });
}

// 重命名会话记录
export function renameChatRecord(sessionId, sessionTitle) {
  return request({
    url: '/ai/chatRecord/' + sessionId + '/rename',
    method: 'put',
    data: { sessionTitle },
  });
}

// 删除会话记录
export function removeChatRecord(sessionIds) {
  return request({
    url: '/ai/chatRecord/' + sessionIds,
    method: 'delete',
  });
}
