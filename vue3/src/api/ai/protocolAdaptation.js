import request, { download } from '@/utils/request';

const PROTOCOL_ADAPTATION_LONG_TIMEOUT = 300000;

export function listProtocolAdaptationTask(query) {
  return request({
    url: '/ai/protocol/adaptation/list',
    method: 'get',
    params: query,
  });
}

export function getProtocolAdaptationTask(taskId) {
  return request({
    url: `/ai/protocol/adaptation/${taskId}`,
    method: 'get',
  });
}

export function addProtocolAdaptationTask(data) {
  return request({
    url: '/ai/protocol/adaptation',
    method: 'post',
    data,
  });
}

export function updateProtocolAdaptationTask(data) {
  return request({
    url: '/ai/protocol/adaptation',
    method: 'put',
    data,
  });
}

export function changeProtocolAdaptationTaskStatus(taskId, taskStatus) {
  return request({
    url: '/ai/protocol/adaptation/changeStatus',
    method: 'put',
    data: {
      taskId,
      taskStatus,
    },
  });
}

export function removeProtocolAdaptationTask(taskId) {
  return request({
    url: `/ai/protocol/adaptation/${taskId}`,
    method: 'delete',
  });
}

export function uploadProtocolAdaptationArtifact(taskId, data) {
  return request({
    url: `/ai/protocol/adaptation/${taskId}/upload`,
    method: 'post',
    data,
    timeout: PROTOCOL_ADAPTATION_LONG_TIMEOUT,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

export function parseProtocolAdaptationDocument(taskId) {
  return request({
    url: `/ai/protocol/adaptation/${taskId}/parse`,
    method: 'post',
    timeout: PROTOCOL_ADAPTATION_LONG_TIMEOUT,
  });
}

export function validateProtocolAdaptationDsl(taskId) {
  return request({
    url: `/ai/protocol/adaptation/${taskId}/validate`,
    method: 'post',
    timeout: PROTOCOL_ADAPTATION_LONG_TIMEOUT,
  });
}

export function exportProtocolAdaptationWorkbook(taskId) {
  return request({
    url: `/ai/protocol/adaptation/${taskId}/workbook/export`,
    method: 'post',
  });
}

export function importProtocolAdaptationWorkbook(taskId, data) {
  return request({
    url: `/ai/protocol/adaptation/${taskId}/workbook/import`,
    method: 'post',
    data,
    timeout: PROTOCOL_ADAPTATION_LONG_TIMEOUT,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

export function generateProtocolAdaptationCodePackage(taskId) {
  return request({
    url: `/ai/protocol/adaptation/${taskId}/generate`,
    method: 'post',
    timeout: PROTOCOL_ADAPTATION_LONG_TIMEOUT,
  });
}

export function autoRunProtocolAdaptationTask(taskId) {
  return request({
    url: `/ai/protocol/adaptation/${taskId}/auto-run`,
    method: 'post',
    timeout: PROTOCOL_ADAPTATION_LONG_TIMEOUT,
  });
}

export function verifyProtocolGenerationRecord(recordId) {
  return request({
    url: `/ai/protocol/adaptation/generation/${recordId}/verify`,
    method: 'post',
    timeout: PROTOCOL_ADAPTATION_LONG_TIMEOUT,
  });
}

export function downloadProtocolGenerationFile(recordId, fileType, filename) {
  return download(`/ai/protocol/adaptation/generation/${recordId}/download/${fileType}`, {}, filename);
}

export function downloadProtocolAdaptationArtifact(artifactId, filename) {
  return download(`/ai/protocol/adaptation/artifact/${artifactId}/download`, {}, filename);
}

export function getProtocolAdaptationArtifactContent(artifactId) {
  return request({
    url: `/ai/protocol/adaptation/artifact/${artifactId}/content`,
    method: 'get',
  });
}

export function confirmProtocolGenerationRecord(recordId) {
  return request({
    url: `/ai/protocol/adaptation/generation/${recordId}/confirm`,
    method: 'post',
  });
}

export function listProtocolAdaptationArtifacts(taskId) {
  return request({
    url: `/ai/protocol/adaptation/${taskId}/artifacts`,
    method: 'get',
  });
}

export function listProtocolAdaptationGenerationRecords(taskId) {
  return request({
    url: `/ai/protocol/adaptation/${taskId}/generationRecords`,
    method: 'get',
  });
}

export function getProtocolGenerationRecord(recordId) {
  return request({
    url: `/ai/protocol/adaptation/generation/${recordId}`,
    method: 'get',
  });
}
