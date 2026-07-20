import request from '@/utils/request';

const KNOWLEDGE_VERSION_LONG_TIMEOUT = 120000;

export function listKnowledgeVersion(query) {
  return request({
    url: '/ai/knowledge/version/list',
    method: 'get',
    params: query,
  });
}

export function buildKnowledgeVersion(knowledgeBaseId) {
  return request({
    url: '/ai/knowledge/version/build/' + knowledgeBaseId,
    method: 'post',
    timeout: KNOWLEDGE_VERSION_LONG_TIMEOUT,
  });
}

export function publishKnowledgeVersion(versionId) {
  return request({
    url: '/ai/knowledge/version/publish/' + versionId,
    method: 'put',
    timeout: KNOWLEDGE_VERSION_LONG_TIMEOUT,
  });
}

export function previewKnowledgeVersionQuality(versionId) {
  return request({
    url: '/ai/knowledge/version/quality/' + versionId,
    method: 'get',
    timeout: KNOWLEDGE_VERSION_LONG_TIMEOUT,
  });
}

export function rollbackKnowledgeVersion(versionId) {
  return request({
    url: '/ai/knowledge/version/rollback/' + versionId,
    method: 'put',
    timeout: KNOWLEDGE_VERSION_LONG_TIMEOUT,
  });
}

export function removeKnowledgeVersion(versionId) {
  return request({
    url: '/ai/knowledge/version/' + versionId,
    method: 'delete',
  });
}
