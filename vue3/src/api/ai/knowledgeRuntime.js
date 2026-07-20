import request from '@/utils/request';

const KNOWLEDGE_RUNTIME_LONG_TIMEOUT = 120000;

export function getKnowledgeRuntimeStatus(knowledgeBaseId) {
  return request({
    url: '/ai/knowledge/runtime/' + knowledgeBaseId,
    method: 'get',
    timeout: KNOWLEDGE_RUNTIME_LONG_TIMEOUT,
  });
}

export function rebuildKnowledgeRuntime(knowledgeBaseId) {
  return request({
    url: '/ai/knowledge/runtime/rebuild/' + knowledgeBaseId,
    method: 'post',
    timeout: KNOWLEDGE_RUNTIME_LONG_TIMEOUT,
  });
}
