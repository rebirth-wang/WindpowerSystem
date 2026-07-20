/**
 * Common utility functions
 */

/** Deep clone an object or array */
export const deepClone = <T>(obj: T): T => {
  if (typeof obj === 'object' && obj !== null) {
    const objClone: any = Array.isArray(obj) ? [] : {};
    for (const key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        if (obj[key] && typeof obj[key] === 'object') {
          objClone[key] = deepClone(obj[key]);
        } else {
          objClone[key] = obj[key];
        }
      }
    }
    return objClone;
  }
  return obj;
};

/**
 * Format a Date / timestamp to string.
 * @param arg - Date, timestamp, or date string
 * @param format - Pattern using Y M D h m s placeholders
 */
export function formatDate(arg: Date | number | string | null, format?: string): string | null {
  if (!arg) return null;
  const now = new Date(arg as any);
  format = format || 'Y.M.D h:m';

  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const date = String(now.getDate()).padStart(2, '0');
  const hour = String(now.getHours()).padStart(2, '0');
  const minute = String(now.getMinutes()).padStart(2, '0');
  const second = String(now.getSeconds()).padStart(2, '0');

  return format
    .replace('Y', String(year))
    .replace('M', month)
    .replace('D', date)
    .replace('h', hour)
    .replace('m', minute)
    .replace('s', second);
}

/** Keep a fixed number of decimal places (truncate, not round) */
export function keepDecimal(value: number, decimals: number): number {
  const factor = Math.pow(10, decimals);
  return Math.trunc(value * factor) / factor;
}

type TimeUnit = 'second' | 'minute' | 'hour' | 'day';

/** Auto-scale a time value to a human-friendly unit */
export function calcTimeText(
  value: number | string,
  type: TimeUnit | string,
  accuracy: number
): { type: string; value: number } {
  let v = typeof value === 'string' ? parseInt(value) : value;
  if (parseInt(type as string) === 0) type = 'minute';
  const factor = Math.pow(10, accuracy);

  if (type === 'second' && v >= 60) {
    v /= 60;
    type = 'minute';
  }
  if (type === 'minute' && v >= 60) {
    v /= 60;
    type = 'hour';
  }
  if (type === 'hour' && v >= 24) {
    v /= 24;
    type = 'day';
  }

  return { type, value: Math.trunc(factor * v) / factor };
}

/** Convert a full base64 data-URL to a Blob */
export function base64toBlob(dataurl: string): Blob {
  const arr = dataurl.split(',');
  const mime = arr[0].match(/:(.*?);/)![1];
  const bstr = atob(arr[1]);
  const n = bstr.length;
  const u8arr = new Uint8Array(n);
  for (let i = 0; i < n; i++) {
    u8arr[i] = bstr.charCodeAt(i);
  }
  return new Blob([u8arr], { type: mime });
}

/** Download a file from a Blob */
export async function downFileByBlob(blob: Blob, fileName?: string): Promise<void> {
  const isValid = await blobValidate(blob);
  if (!isValid) throw new Error('Invalid blob (JSON response detected)');
  const blobUrl = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.download = fileName || '下载文件';
  link.style.display = 'none';
  link.href = blobUrl;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

/** Check if a blob is binary (not a JSON error response) */
export async function blobValidate(data: Blob): Promise<boolean> {
  try {
    const text = await data.text();
    const payload = JSON.parse(text);
    return !isAjaxResponsePayload(payload);
  } catch {
    return true;
  }
}

function isAjaxResponsePayload(payload: any): boolean {
  if (!payload || typeof payload !== 'object' || Array.isArray(payload)) {
    return false;
  }
  const hasCode = Object.prototype.hasOwnProperty.call(payload, 'code');
  const hasMessage =
    Object.prototype.hasOwnProperty.call(payload, 'msg') || Object.prototype.hasOwnProperty.call(payload, 'message');
  return hasCode && hasMessage;
}

interface DataSpec {
  min?: number;
  max?: number;
  minNum?: number;
  maxNum?: number;
  trueText?: string;
  falseText?: string;
  trueName?: string;
  falseName?: string;
  maxLength?: number;
  unit?: string;
  enums?: any[] | string;
  [key: string]: any;
}

/** Format IoT data-type range description */
export function getDataRange(datatype: string, specsStr: string): string | undefined {
  const specs: DataSpec = JSON.parse(specsStr) || {};
  specs.minNum = specs.min == undefined ? specs.maxNum : specs.min;
  specs.maxNum = specs.max == undefined ? specs.maxNum : specs.max;
  specs.trueName = specs.trueText == undefined ? specs.trueName : specs.trueText;
  specs.falseName = specs.falseText == undefined ? specs.falseName : specs.falseText;
  switch (datatype) {
    case 'string':
      return `最大${specs.maxLength || '--'}位字符`;
    case 'integer':
    case 'decimal':
      return `最小值为：${specs.minNum == undefined ? '--' : specs.minNum}，最大值为：${specs.maxNum || '--'}，单位为：${specs.unit || '--'}`;
    case 'bool':
      return `${specs.trueName || '--'}为true，${specs.falseName || '--'}为false`;
    case 'enum': {
      const enums: any[] = Array.isArray(specs.enums) ? specs.enums : JSON.parse((specs.enums as string) || '[]');
      return enums.map((str: any) => `${str.label}为${str.value || str.key}`).join(',');
    }
  }
}

interface ModelItem {
  datatype: string;
  length?: number;
  rangeMin?: number | string;
  rangeMax?: number | string;
  unit?: string;
  trueName?: string;
  falseName?: string;
  enums?: any[] | string;
  [key: string]: any;
}

/** Format thing-model data range description */
export function getModelRange(item: ModelItem): string | undefined {
  switch (item.datatype) {
    case 'string':
      return `最大${item.length || '--'}位字符`;
    case 'integer':
    case 'decimal':
      return `最小值为：${item.rangeMin || '--'}，最大值为：${item.rangeMax || '--'}，单位为：${item.unit || '--'}`;
    case 'bool':
      return `${item.trueName || '--'}为true，${item.falseName || '--'}为false`;
    case 'enum': {
      const enums: any[] = Array.isArray(item.enums) ? item.enums : JSON.parse((item.enums as string) || '[]');
      return enums.map((str: any) => `${str.label}为${str.value || str.key}`).join(',');
    }
  }
}

/** Throttle — execute at most once per `delay` ms */
export function throttle<T extends (...args: any[]) => any>(fn: T, delay: number): (...args: Parameters<T>) => void {
  let timer: ReturnType<typeof setTimeout> | null = null;
  return function (this: any, ...args: Parameters<T>) {
    if (!timer) {
      timer = setTimeout(() => {
        fn.apply(this, args);
        timer = null;
      }, delay);
    }
  };
}

/** Debounce — wait `delay` ms of inactivity before executing */
export function debounce<T extends (...args: any[]) => any>(fn: T, delay: number): (...args: Parameters<T>) => void {
  let timer: ReturnType<typeof setTimeout> | null = null;
  return function (this: any, ...args: Parameters<T>) {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
}

/** Build a tree structure from flat data */
export function handleTree(data: any[], id?: string, parentId?: string, children?: string): any[] {
  const config = {
    id: id || 'id',
    parentId: parentId || 'parentId',
    childrenList: children || 'children',
  };

  const childrenListMap: Record<string, any[]> = {};
  const nodeIds: Record<string, any> = {};
  const tree: any[] = [];

  for (const d of data) {
    const pid = d[config.parentId];
    if (childrenListMap[pid] == null) {
      childrenListMap[pid] = [];
    }
    nodeIds[d[config.id]] = d;
    childrenListMap[pid].push(d);
  }

  for (const d of data) {
    const pid = d[config.parentId];
    if (nodeIds[pid] == null) {
      tree.push(d);
    }
  }

  function adaptToChildrenList(o: any) {
    if (childrenListMap[o[config.id]] !== null) {
      o[config.childrenList] = childrenListMap[o[config.id]];
    }
    if (o[config.childrenList]) {
      for (const c of o[config.childrenList]) {
        adaptToChildrenList(c);
      }
    }
  }

  for (const t of tree) adaptToChildrenList(t);
  return tree;
}

/** Find a tree node by id (BFS) */
export function getNodeById(tree: any, id: any, idName?: string): any | null {
  idName = idName || 'id';
  const arr: any[] = Array.isArray(tree) ? deepClone(tree) : [tree];
  let result: any = null;
  while (arr.length) {
    const item = arr.pop();
    if (item && item[idName] === id) {
      result = item;
      break;
    } else if (item?.children?.length) {
      arr.push(...item.children);
    }
  }
  return result;
}

/** Find a tree node by id (recursive DFS) */
export function findNodeFromTreeById(root: any, id: any, idName?: string): any | null {
  idName = idName || 'id';
  if (!root) return null;
  const type = Object.prototype.toString.call(root).slice(8, -1);
  if (type === 'Object') {
    if (root[idName] && root[idName] === id) return root;
    return findNodeFromTreeById(root.children || null, id, idName);
  } else if (type === 'Array') {
    const needNode = root.find((x: any) => !!x && x[idName] === id);
    if (needNode) return needNode;
    let result: any = null;
    root.some((item: any) => {
      if (item?.children?.length) {
        result = findNodeFromTreeById(item.children, id, idName);
        if (result) return true;
      }
    });
    return result;
  }
  return null;
}

/** Get the path of names from root to the node with given id */
export function getPathById(tree: any, id: any, idName?: string, path?: string[]): string[] | undefined {
  idName = idName || 'id';
  const nodes: any[] = Array.isArray(tree) ? tree : [tree];
  if (!path) path = [];
  for (let i = 0; i < nodes.length; i++) {
    const tempPath = [...path, nodes[i].name];
    if (nodes[i][idName] === id) return tempPath;
    if (nodes[i].children) return getPathById(nodes[i].children, id, idName, tempPath);
  }
}

/** Copy text to clipboard (legacy execCommand approach) */
export function copyText(copytext: string): { type: string; message: string } {
  const text = document.createElement('input');
  text.setAttribute('readonly', 'readonly');
  text.value = copytext;
  document.body.appendChild(text);
  text.setSelectionRange(0, text.value.length);
  text.select();
  document.execCommand('copy');
  if (document.body.removeChild(text)) {
    return { type: 'success', message: '复制成功' };
  } else {
    return { type: 'error', message: '复制失败' };
  }
}

/** Export 2D array data to a CSV file */
export function exportToExcel(data: string[][], fileName: string): void {
  const csv = convertToCSV(data);
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.setAttribute('href', url);
  link.setAttribute('download', `${fileName}.csv`);
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

/** Convert a 2D array to CSV string */
export function convertToCSV(data: string[][]): string {
  return data.map((row) => row.map((cell) => `"${cell}"`).join(',')).join('\n');
}

/** Find user path in a cascader option tree */
export function getUserPath(options: any[], ids: any[]): any[] | false {
  let res: any[] = [];
  const isOpt = options.some((opt) => {
    let hasId = false;
    const resIds: number[] = [];
    ids.forEach((id) => {
      if (id == opt.userId) {
        resIds.push(parseInt(id));
        hasId = true;
      } else if (hasId) {
        resIds.push(parseInt(id));
      }
    });
    if (hasId) {
      res = resIds;
      return true;
    } else if (opt.children) {
      const hasChild = getUserPath(opt.children, ids);
      if (hasChild) {
        res = [opt.userId].concat(hasChild);
        return true;
      }
    }
    return false;
  });
  return isOpt ? res : false;
}

/** Convert hex string to signed integer */
export function hex2int(hexStr: string): number {
  let twoStr = parseInt(hexStr, 16).toString(2);
  const bitNum = hexStr.length * 4;
  while (twoStr.length < bitNum) {
    twoStr = '0' + twoStr;
  }
  if (twoStr.substring(0, 1) === '0') {
    return parseInt(twoStr, 2);
  } else {
    let two = parseInt(twoStr, 2) - 1;
    twoStr = two.toString(2);
    let unsign = twoStr.substring(1, bitNum);
    unsign = unsign.replace(/0/g, 'z').replace(/1/g, '0').replace(/z/g, '1');
    return -parseInt(unsign, 2);
  }
}

/** Convert signed integer to 4-char uppercase hex string */
export function int2hex(value: string | number): string {
  const hexInt = typeof value === 'string' ? parseInt(value, 10) : value;
  let hex: string;
  const LENGTH_04 = 4;
  const LENGTH_15 = 15;

  if (hexInt >= 0) {
    hex = hexInt.toString(16).toLowerCase();
  } else {
    let twoStr = (-hexInt - 1).toString(2);
    const pad = '000000000000000';
    twoStr = pad.slice(0, LENGTH_15 - twoStr.length) + twoStr;
    twoStr = twoStr.replace(/0/g, 'z').replace(/1/g, '0').replace(/z/g, '1');
    hex = parseInt('1' + twoStr, 2)
      .toString(16)
      .toLowerCase();
  }

  if (hex.length !== LENGTH_04) {
    const pad = '0000';
    hex = pad.slice(0, LENGTH_04 - hex.length) + hex;
  }
  return hex.toUpperCase();
}
