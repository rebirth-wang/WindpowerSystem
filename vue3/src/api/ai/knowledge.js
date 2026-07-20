import request from '@/utils/request';

export function listKnowledge(query) {
  return request({
    url: '/ai/knowledge/list',
    method: 'get',
    params: query,
  });
}

export function getKnowledge(knowledgeBaseId) {
  return request({
    url: '/ai/knowledge/' + knowledgeBaseId,
    method: 'get',
  });
}

export function addKnowledge(data) {
  return request({
    url: '/ai/knowledge',
    method: 'post',
    data,
  });
}

export function updateKnowledge(data) {
  return request({
    url: '/ai/knowledge',
    method: 'put',
    data,
  });
}

export function changeKnowledgeStatus(knowledgeBaseId, status) {
  return request({
    url: '/ai/knowledge/changeStatus',
    method: 'put',
    data: {
      knowledgeBaseId,
      status,
    },
  });
}

export function removeKnowledge(knowledgeBaseId) {
  return request({
    url: '/ai/knowledge/' + knowledgeBaseId,
    method: 'delete',
  });
}

export function uploadKnowledgeTemplate(data) {
  return request({
    url: '/ai/knowledge/template/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

export function rebuildCodebaseGuide(knowledgeBaseId) {
  return request({
    url: '/ai/knowledge/codebase/rebuild/' + knowledgeBaseId,
    method: 'post',
    timeout: 120000,
  });
}

export function uploadCodebaseGuideSnapshot(data) {
  return request({
    url: '/ai/knowledge/codebase/upload',
    method: 'post',
    data,
    timeout: 120000,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}
