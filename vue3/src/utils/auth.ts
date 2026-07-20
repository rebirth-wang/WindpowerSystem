import Cookies from 'js-cookie';

const TokenKey = 'Admin-Token';
const TokenExpireTimeKey = 'Admin-Token-Expire-Time';
const UserIdKey = 'userId';

type TokenExpires = number | Date | string;

function parseExpireDate(expires?: TokenExpires): Date | undefined {
  if (expires === undefined) return undefined;

  if (typeof expires === 'number') {
    return new Date(Date.now() + expires * 24 * 60 * 60 * 1000);
  }

  if (expires instanceof Date) {
    return Number.isNaN(expires.getTime()) ? undefined : expires;
  }

  const normalized = expires.includes(' ') ? expires.replace(' ', 'T') : expires;
  const expireDate = new Date(normalized);
  return Number.isNaN(expireDate.getTime()) ? undefined : expireDate;
}

function setStoredExpireTime(expireDate?: Date): void {
  try {
    if (!expireDate) {
      localStorage.removeItem(TokenExpireTimeKey);
      return;
    }

    localStorage.setItem(TokenExpireTimeKey, String(expireDate.getTime()));
  } catch (_e) {
    // localStorage may be unavailable in private or restricted browser contexts.
  }
}

function isTokenExpired(): boolean {
  try {
    const storedExpireTime = localStorage.getItem(TokenExpireTimeKey);
    if (!storedExpireTime) return false;

    const expireTime = Number(storedExpireTime);
    return Number.isFinite(expireTime) && expireTime <= Date.now();
  } catch (_e) {
    return false;
  }
}

export function getToken(): string | undefined {
  if (isTokenExpired()) {
    removeToken();
    return undefined;
  }

  return Cookies.get(TokenKey);
}

export function setToken(token: string, expires?: TokenExpires): string | undefined {
  const expireDate = parseExpireDate(expires);
  setStoredExpireTime(expireDate);

  return Cookies.set(TokenKey, token, {
    ...(expireDate !== undefined && { expires: expireDate }),
  });
}

export function removeToken(): void {
  Cookies.remove(TokenKey);
  try {
    localStorage.removeItem(TokenExpireTimeKey);
  } catch (_e) {
    // localStorage may be unavailable in private or restricted browser contexts.
  }
}

export function getUserId(): string | undefined {
  return Cookies.get(UserIdKey);
}

export function setUserId(userId: string | number): string | undefined {
  return Cookies.set(UserIdKey, String(userId));
}

export function removeUserId(): void {
  Cookies.remove(UserIdKey);
}
