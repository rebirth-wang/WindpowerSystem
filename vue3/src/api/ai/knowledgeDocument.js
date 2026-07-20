import request from '@/utils/request';

export function listKnowledgeDocument(query) {
  return request({
    url: '/ai/knowledge/document/list',
    method: 'get',
    params: query,
  });
}

export function getKnowledgeDocument(documentId) {
  return request({
    url: `/ai/knowledge/document/${documentId}`,
    method: 'get',
  });
}

export function updateKnowledgeDocument(data) {
  return request({
    url: '/ai/knowledge/document',
    method: 'put',
    data,
  });
}

export function changeKnowledgeDocumentStatus(documentId, status) {
  return request({
    url: '/ai/knowledge/document/changeStatus',
    method: 'put',
    data: {
      documentId,
      status,
    },
  });
}

export function reparseKnowledgeDocument(documentId) {
  return request({
    url: `/ai/knowledge/document/reparse/${documentId}`,
    method: 'post',
  });
}

export function getKnowledgeDocumentSnapshot(documentId, previewSize = 30) {
  return request({
    url: `/ai/knowledge/document/${documentId}/snapshot`,
    method: 'get',
    params: {
      previewSize,
    },
  });
}

export function removeKnowledgeDocument(documentId) {
  return request({
    url: `/ai/knowledge/document/${documentId}`,
    method: 'delete',
  });
}

export function refreshKnowledgeDocument(documentId) {
  return request({
    url: `/ai/knowledge/refresh/document/${documentId}`,
    method: 'post',
  });
}

export function listKnowledgeSegments(documentId) {
  return request({
    url: `/ai/knowledge/source/document/${documentId}/segments`,
    method: 'get',
  });
}
