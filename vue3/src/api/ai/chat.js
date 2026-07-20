import request, { download } from '@/utils/request';
import { getToken } from '@/utils/auth';
import { useSettingsStore } from '@/stores/modules/settings';
import Cookies from 'js-cookie';

const CHAT_FILE_STREAM_TIMEOUT = 300000;

// 获取默认模型路由
export function getDefaultChatRoute() {
  return request({
    url: '/ai/chat/route/default',
    method: 'get',
  });
}

// 获取会话模式
export function listChatModes() {
  return request({
    url: '/ai/chat/modes',
    method: 'get',
  });
}

// 获取 AI 会话运行时能力
export function getChatRuntimeInfo() {
  return request({
    url: '/ai/chat/runtime',
    method: 'get',
  });
}

// 查询当前账号 AI 会话观测统计
export function getChatObservabilityStats(params) {
  return request({
    url: '/ai/chat/observability',
    method: 'get',
    params,
  });
}

// 发送 AI 会话消息
export function sendChatMessage(data) {
  return request({
    url: '/ai/chat/send',
    method: 'post',
    data,
  });
}

export async function uploadChatFileStream(data, options = {}) {
  return sendChatMultipartStreamByUrl('/ai/chat/send/stream', data, {
    ...options,
    timeout: options.timeout || CHAT_FILE_STREAM_TIMEOUT,
  });
}

export function downloadThingModelWorkbook(artifactCode, filename) {
  return download(`/ai/chat/thing-model/workbook/${artifactCode}/download`, {}, filename || '物模型导入模板.xlsx');
}

export function downloadRequirementEvaluationReport(artifactCode, filename) {
  return download(
    `/ai/chat/requirement-evaluation/report/${artifactCode}/download`,
    {},
    filename || 'AI需求评估结果.docx'
  );
}

async function sendChatMessageStreamByUrl(url, data, options = {}) {
  const { onEvent, timeout = 120000, signal, idleTimeout = 0, shouldCloseOnIdle } = options;
  const controller = new AbortController();
  let idleTimer = null;
  let closedByIdle = false;
  const relayAbort = () => controller.abort();
  const clearIdleTimer = () => {
    if (idleTimer) {
      window.clearTimeout(idleTimer);
      idleTimer = null;
    }
  };
  const scheduleIdleTimer = (parsed) => {
    if (!idleTimeout || typeof shouldCloseOnIdle !== 'function' || !shouldCloseOnIdle(parsed)) {
      return;
    }
    clearIdleTimer();
    idleTimer = window.setTimeout(() => {
      closedByIdle = true;
      controller.abort();
    }, idleTimeout);
  };
  if (signal) {
    if (signal.aborted) {
      controller.abort();
    } else {
      signal.addEventListener('abort', relayAbort, { once: true });
    }
  }
  const timer = window.setTimeout(() => controller.abort(), timeout);
  try {
    const headers = buildChatStreamHeaders({
      'Content-Type': 'application/json;charset=utf-8',
    });

    const response = await fetch(`${import.meta.env.VITE_APP_BASE_API}${url}`, {
      method: 'POST',
      headers,
      body: JSON.stringify(data),
      signal: controller.signal,
    });

    if (!response.ok) {
      throw await buildStreamHttpError(response);
    }

    const contentType = response.headers.get('content-type') || '';
    if (!contentType.includes('text/event-stream')) {
      throw await buildStreamHttpError(response);
    }

    const reader = response.body?.getReader();
    if (!reader) {
      throw new Error('流式响应读取失败');
    }

    const decoder = new TextDecoder('utf-8');
    let buffer = '';
    const emitEvent = (parsed) => {
      if (!parsed || typeof onEvent !== 'function') {
        return false;
      }
      const shouldStop = onEvent(parsed) === false;
      if (!shouldStop) {
        scheduleIdleTimer(parsed);
      }
      return shouldStop;
    };
    while (true) {
      const { value, done } = await reader.read();
      if (done) {
        break;
      }
      buffer += decoder.decode(value, { stream: true });
      const blocks = buffer.split('\n\n');
      buffer = blocks.pop() || '';
      for (const block of blocks) {
        const parsed = parseSseBlock(block);
        if (emitEvent(parsed)) {
          controller.abort();
          return;
        }
      }
    }

    if (buffer.trim()) {
      const parsed = parseSseBlock(buffer);
      if (emitEvent(parsed)) {
        controller.abort();
      }
    }
  } catch (error) {
    if (closedByIdle && error?.name === 'AbortError') {
      return;
    }
    throw error;
  } finally {
    clearIdleTimer();
    window.clearTimeout(timer);
    if (signal) {
      signal.removeEventListener('abort', relayAbort);
    }
  }
}

async function sendChatMultipartStreamByUrl(url, data, options = {}) {
  const { onEvent, timeout = 120000, signal, idleTimeout = 0, shouldCloseOnIdle } = options;
  const controller = new AbortController();
  let idleTimer = null;
  let closedByIdle = false;
  const relayAbort = () => controller.abort();
  const clearIdleTimer = () => {
    if (idleTimer) {
      window.clearTimeout(idleTimer);
      idleTimer = null;
    }
  };
  const scheduleIdleTimer = (parsed) => {
    if (!idleTimeout || typeof shouldCloseOnIdle !== 'function' || !shouldCloseOnIdle(parsed)) {
      return;
    }
    clearIdleTimer();
    idleTimer = window.setTimeout(() => {
      closedByIdle = true;
      controller.abort();
    }, idleTimeout);
  };
  if (signal) {
    if (signal.aborted) {
      controller.abort();
    } else {
      signal.addEventListener('abort', relayAbort, { once: true });
    }
  }
  const timer = window.setTimeout(() => controller.abort(), timeout);
  try {
    const headers = buildChatStreamHeaders();

    const response = await fetch(`${import.meta.env.VITE_APP_BASE_API}${url}`, {
      method: 'POST',
      headers,
      body: data,
      signal: controller.signal,
    });

    if (!response.ok) {
      throw await buildStreamHttpError(response);
    }

    const contentType = response.headers.get('content-type') || '';
    if (!contentType.includes('text/event-stream')) {
      throw await buildStreamHttpError(response);
    }

    const reader = response.body?.getReader();
    if (!reader) {
      throw new Error('流式响应读取失败');
    }

    const decoder = new TextDecoder('utf-8');
    let buffer = '';
    const emitEvent = (parsed) => {
      if (!parsed || typeof onEvent !== 'function') {
        return false;
      }
      const shouldStop = onEvent(parsed) === false;
      if (!shouldStop) {
        scheduleIdleTimer(parsed);
      }
      return shouldStop;
    };
    while (true) {
      const { value, done } = await reader.read();
      if (done) {
        break;
      }
      buffer += decoder.decode(value, { stream: true });
      const blocks = buffer.split('\n\n');
      buffer = blocks.pop() || '';
      for (const block of blocks) {
        const parsed = parseSseBlock(block);
        if (emitEvent(parsed)) {
          controller.abort();
          return;
        }
      }
    }

    if (buffer.trim()) {
      const parsed = parseSseBlock(buffer);
      if (emitEvent(parsed)) {
        controller.abort();
      }
    }
  } catch (error) {
    if (closedByIdle && error?.name === 'AbortError') {
      return;
    }
    throw error;
  } finally {
    clearIdleTimer();
    window.clearTimeout(timer);
    if (signal) {
      signal.removeEventListener('abort', relayAbort);
    }
  }
}

function buildChatStreamHeaders(baseHeaders = {}) {
  const headers = { ...baseHeaders };
  const language = resolveCurrentLanguage();
  if (language) {
    headers.language = language;
  }
  const token = getToken();
  const search = new URLSearchParams(window.location.search);
  const share = search.get('share');
  if (share) {
    headers.Authorization = `Bearer ${share}`;
  } else if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  return headers;
}

function resolveCurrentLanguage() {
  try {
    const settingsStore = useSettingsStore();
    const language = normalizeLanguage(settingsStore?.language);
    if (language) {
      return language;
    }
  } catch (_error) {
    // Pinia 未初始化时回退到语言 Cookie。
  }
  return normalizeLanguage(Cookies.get('language') || window.navigator?.language);
}

function normalizeLanguage(language) {
  const value = String(language || '').trim();
  if (!value) {
    return 'zh-CN';
  }
  if (/^en/i.test(value)) {
    return 'en-US';
  }
  if (/^zh/i.test(value)) {
    return 'zh-CN';
  }
  return value;
}

// 发送 AI 流式会话消息
export async function sendChatMessageStream(data, options = {}) {
  return sendChatMessageStreamByUrl('/ai/chat/send/stream', data, options);
}

// 继续执行上一轮澄清后的流式会话
export async function resumeChatMessageStream(data, options = {}) {
  return sendChatMessageStreamByUrl('/ai/chat/send/stream/resume', data, options);
}

function parseSseBlock(block) {
  const lines = (block || '').split(/\r?\n/);
  let eventName = 'message';
  const dataLines = [];
  lines.forEach((line) => {
    if (!line) {
      return;
    }
    if (line.startsWith('event:')) {
      eventName = line.slice(6).trim() || 'message';
      return;
    }
    if (line.startsWith('data:')) {
      dataLines.push(line.slice(5).trim());
    }
  });
  if (!dataLines.length) {
    return null;
  }
  const raw = dataLines.join('\n');
  let payload = raw;
  try {
    payload = JSON.parse(raw);
  } catch (_error) {
    payload = { content: raw };
  }
  return {
    event: eventName,
    data: payload,
  };
}

async function buildStreamHttpError(response) {
  try {
    const text = await response.text();
    if (!text) {
      return new Error(`流式请求失败：HTTP ${response.status}`);
    }
    const json = JSON.parse(text);
    return new Error(json?.msg || `流式请求失败：HTTP ${response.status}`);
  } catch (_error) {
    return new Error(`流式请求失败：HTTP ${response.status}`);
  }
}

// 查询当前用户会话列表
export function listChatSession(query) {
  return request({
    url: '/ai/chat/session/list',
    method: 'get',
    params: query,
  });
}

// 查询当前会话消息
export function listChatMessage(query) {
  return request({
    url: '/ai/chat/message/list',
    method: 'get',
    params: query,
  });
}
