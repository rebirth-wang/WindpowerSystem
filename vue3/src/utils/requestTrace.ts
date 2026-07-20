import type { InternalAxiosRequestConfig } from 'axios';

export const REQUEST_ID_HEADER = 'X-Request-Id';

export function createRequestId(): string {
  const timePart = Date.now().toString(36);
  const bytes = new Uint8Array(4);

  if (typeof crypto !== 'undefined' && crypto.getRandomValues) {
    crypto.getRandomValues(bytes);
  } else {
    for (let index = 0; index < bytes.length; index += 1) {
      bytes[index] = Math.floor(Math.random() * 256);
    }
  }

  const randomPart = Array.from(bytes, (item) => item.toString(16).padStart(2, '0')).join('');
  return `fb-${timePart}-${randomPart}`;
}

export function attachRequestId(config: InternalAxiosRequestConfig): string {
  const headers = config.headers as any;
  const existingRequestId = headers?.[REQUEST_ID_HEADER] || headers?.['x-request-id'];
  const requestId = existingRequestId || createRequestId();

  headers[REQUEST_ID_HEADER] = requestId;
  (config as any).__requestId = requestId;

  return requestId;
}
