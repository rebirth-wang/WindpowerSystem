// 如果需要手动映射type和组件的关系，请在这里配置
const viewRegisterMap = {
  'straight-line': 'view-line',
  'vertical-line': 'view-line',
  'chart-line': 'view-chart',
  'chart-line-step': 'view-chart',
  'chart-bar': 'view-chart',
  'chart-pie': 'view-chart-pie',
  'chart-gauge': 'view-chart-gauge',
  'chart-temp': 'view-chart-temp',
  'chart-map': 'view-chart-map',
  'chart-water': 'view-chart-water',
  image: 'view-image',
  panel: 'view-panel',
  text: 'view-text',
  select: 'view-select',
  triangle: 'view-triangle',
  rect: 'view-rect',
  circular: 'view-circular',
  'line-segment': 'view-line-segment',
  'bizier-curve': 'view-bizier-curve',
  'line-wave': 'view-line-wave',
  'view-line-wave': 'view-line-wave',
  '3-d-model': 'view-3d-model',
  '3d-model': 'view-3d-model',
  '3dmodel': 'view-3d-model',
};

function normalizeType(type) {
  return String(type || '')
    .replaceAll('_', '-')
    .replace(/([a-z0-9])([A-Z])/g, '$1-$2')
    .toLowerCase();
}

// 优先匹配map，否则将自动匹配
export function parseViewName(component) {
  const type = normalizeType(component?.type);
  const infoType = normalizeType(component?.info?.type);

  let viewName = viewRegisterMap[type] || viewRegisterMap[infoType];

  // svg 静态图形一般靠 info.type 决定具体视图
  if (!viewName && type === 'svg') {
    viewName = viewRegisterMap[infoType] || `view-${infoType}`;
  }

  // static 里有些交互/表单组件也按 info.type 渲染
  if (!viewName && type === 'static') {
    viewName = viewRegisterMap[infoType] || `view-${infoType}`;
  }

  if (!viewName) {
    viewName = `view-${type}`;
  }
  return viewName;
}

// 判断两个值大小
export function judgeSize(formula, value1, value2) {
  value1 = Number(value1) || 0;
  value2 = Number(value2) || 0;
  let isTrue = false;
  if (formula == '>') {
    if (value1 > value2) {
      isTrue = true;
    }
  } else if (formula == '>=') {
    if (value1 >= value2) {
      isTrue = true;
    }
  } else if (formula == '=') {
    if (value1 == value2) {
      isTrue = true;
    }
  } else if (formula == '<=') {
    if (value1 <= value2) {
      isTrue = true;
    }
  } else if (formula == '<') {
    if (value1 < value2) {
      isTrue = true;
    }
  } else if (formula == '!=') {
    if (value1 != value2) {
      isTrue = true;
    }
  }
  return isTrue;
}

// 判断是否处在自定义范围内
export function isInCustomRange(value1, start, end) {
  value1 = Number(value1) || 0;
  start = Number(start) || 0;
  end = Number(end) || 0;
  let isTrue = false;
  if (value1 >= start && value1 <= end) {
    isTrue = true;
  }
  return isTrue;
}

// 判断是否不在自定义范围内
export function isNotInCustomRange(value1, start, end) {
  value1 = Number(value1) || 0;
  start = Number(start) || 0;
  end = Number(end) || 0;
  let isTrue = false;
  if (value1 < start || value1 > end) {
    isTrue = true;
  }
  return isTrue;
}

function add0(m) {
  return m < 10 ? '0' + m : m;
}

// 获取当前时间
export function getNowTime() {
  // 年
  let year = new Date().getFullYear();
  // 月份是从0月开始获取的，所以要+1;
  let month = new Date().getMonth() + 1;
  // 日
  let day = new Date().getDate();
  // 时
  let hour = new Date().getHours();
  // 分
  let minute = new Date().getMinutes();
  // 秒
  let second = new Date().getSeconds();
  return year + '-' + add0(month) + '-' + add0(day) + ' ' + add0(hour) + ':' + add0(minute) + ':' + add0(second);
}

// 获取当前时间前2小时
export function getTime(val) {
  var frontOneHour = new Date(new Date().getTime() - val * 60 * 60 * 1000);
  // 年
  let year = frontOneHour.getFullYear();
  // 月份是从0月开始获取的，所以要+1;
  let month = frontOneHour.getMonth() + 1;
  // 日
  let day = frontOneHour.getDate();
  // 时
  let hour = frontOneHour.getHours();
  // 分
  let minute = frontOneHour.getMinutes();
  // 秒
  let second = frontOneHour.getSeconds();
  return year + '-' + add0(month) + '-' + add0(day) + ' ' + add0(hour) + ':' + add0(minute) + ':' + add0(second);
}

// 数据引擎说明
export function getEchartExplain() {
  return '<p><span style="color: rgb(255, 153, 0);">响应示例</span><span style="color: rgb(0, 102, 204);">-</span><a href="https://www.isqqw.com/" rel="noopener noreferrer" target="_blank" style="background-color: rgb(255, 255, 255); color: rgb(0, 102, 204);">echartData</a></p><p>{</p><p>&nbsp;"msg": "操作成功",</p><p>&nbsp;"code": 200,</p><p>&nbsp;"<span style="color: rgb(0, 102, 204);">data</span>": {</p><p>&nbsp;&nbsp;"<span style="color: rgb(0, 102, 204);">xdata</span>": ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],</p><p>&nbsp;&nbsp;"<span style="color: rgb(0, 102, 204);">ydata</span>": [150, 230, 224, 218, 135, 147, 260]</p><p>&nbsp;&nbsp;}</p><p>}</p><p><span style="color: rgb(255, 153, 0);">代码视图</span></p><p>option = {</p><p>&nbsp;xAxis: {</p><p>&nbsp;&nbsp;	type: "category",</p><p>&nbsp;	data: <a href="https://www.isqqw.com/" rel="noopener noreferrer" target="_blank" style="color: rgb(0, 102, 204); background-color: rgb(255, 255, 255);">echartData</a><span style="color: rgb(0, 102, 204);">.data.xdata</span></p><p>&nbsp;},</p><p>&nbsp;yAxis: {</p><p>&nbsp;&nbsp;	type: "value"</p><p>&nbsp;},</p><p>&nbsp;series: [</p><p>&nbsp;&nbsp;{</p><p>&nbsp;&nbsp;&nbsp;	data: <a href="https://www.isqqw.com/" rel="noopener noreferrer" target="_blank" style="color: rgb(0, 102, 204); background-color: rgb(255, 255, 255);">echartData</a><span style="color: rgb(0, 102, 204);">.data.ydata</span>,</p><p>&nbsp;&nbsp;&nbsp;	type: "line"</p><p>&nbsp;&nbsp;}</p><p>&nbsp;]</p><p>};</p>';
}

/**
 * 生成随机整数
 * @param {*} min
 * @param {*} max
 */
export function getRandomInt(min, max) {
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

/**
 * 生成随机浮点数
 * @param {*} min
 * @param {*} max
 * @param {*} dp 小数点位数
 */
export function getRandomFloat(min, max, dp) {
  return parseFloat((Math.random() * (max - min) + min).toFixed(dp));
}

/**
 * 生成随机元素,值1
 * @param {*} str  值1,值2,值3......，中间用英文逗号隔开
 */
export function getRandomString(str) {
  const arr = str.split(',');
  return arr[Math.floor(Math.random() * arr.length)];
}

/**
 * 根据单位转成时间毫秒
 * @param {*} time  时间值
 * @param {*} unit  单位
 */
export function convertToMilliseconds(time, unit) {
  switch (unit) {
    case 's':
      return time * 1000;
    case 'm':
      return time * 60 * 1000;
    case 'h':
      return time * 3600 * 1000;
    default:
      return time;
  }
}

/** 从 vue-router LocationQuery 读取字符串（兼容 string | string[] | undefined） */
export function getRouteQueryString(query, key, defaultValue = '') {
  const raw = query?.[key];
  if (raw == null || raw === '') return defaultValue;
  if (Array.isArray(raw)) return raw[0] != null && raw[0] !== '' ? String(raw[0]) : defaultValue;
  return String(raw);
}

/** 从 query 读取数字，非法或缺失时返回 defaultValue */
export function getRouteQueryNumber(query, key, defaultValue = 0) {
  const s = getRouteQueryString(query, key, '');
  if (s === '') return defaultValue;
  const n = Number(s);
  return Number.isFinite(n) ? n : defaultValue;
}

/** 组态页 type：1 产品 2 场景 3 独立（默认 3） */
export function getScadaRouteType(query, defaultValue = 3) {
  return getRouteQueryNumber(query, 'type', defaultValue);
}

/** v-for 的 index 在 TS 中为 string | number，统一转为 number */
export function normalizeTopoIndex(index) {
  if (typeof index === 'number' && Number.isFinite(index)) return index;
  const n = Number.parseInt(String(index), 10);
  return Number.isFinite(n) ? n : 0;
}

/**
 * 与画布 v-for、右侧图层面板 draggable 一致：数组越靠后越在上层，
 * 按当前数组顺序重写各组件 zIndex，并更新 topo 根上的 zIndexTop/zIndexBottom（与拖拽重排后视觉一致）
 */
export function syncZIndexFromComponentsOrder(components, topoDataRoot) {
  if (!components?.length) return;
  components.forEach((c, i) => {
    if (c?.style) c.style.zIndex = i + 1;
  });
  topoDataRoot.zIndexTop = components.length;
  topoDataRoot.zIndexBottom = 1;
}

/**
 * 将选中项整段移到数组末尾（置顶）或开头（置底），顺序与图层面板拖拽结果一致。
 * @param {any[]} components
 * @param {Record<string, any>|null|undefined} selectedComponentMap
 * @param {'置顶'|'置底'} mode
 * @param {Record<string, any>} topoDataRoot
 */
export function reorderComponentsByStackEdge(components, selectedComponentMap, mode, topoDataRoot) {
  if (!components?.length || !selectedComponentMap) return;
  const keys = Object.keys(selectedComponentMap);
  if (!keys.length) return;

  const keySet = new Set(keys);
  const selectedOrdered = components.filter((c) => c?.identifier && keySet.has(c.identifier));
  if (!selectedOrdered.length) return;

  const rest = components.filter((c) => !c?.identifier || !keySet.has(c.identifier));
  const next = mode === '置顶' ? [...rest, ...selectedOrdered] : [...selectedOrdered, ...rest];

  components.splice(0, components.length, ...next);
  syncZIndexFromComponentsOrder(components, topoDataRoot);
}

/** 从 style 解析旋转角与水平/垂直镜像（兼容 scale 与 rotateX/Y(180deg) 旧写法） */
export function parseComponentTransform(style) {
  const rotate = Number(style?.transform) || 0;
  let scaleX = 1;
  let scaleY = 1;
  const str = String(style?.transformType || '');
  const scaleMatch = str.match(/scale\s*\(\s*(-?\d+(?:\.\d+)?)\s*,\s*(-?\d+(?:\.\d+)?)\s*\)/);
  if (scaleMatch) {
    scaleX = Number(scaleMatch[1]) < 0 ? -1 : 1;
    scaleY = Number(scaleMatch[2]) < 0 ? -1 : 1;
  }
  if (/rotateY\s*\(\s*180deg\s*\)/i.test(str)) scaleX = -1;
  if (/rotateX\s*\(\s*180deg\s*\)/i.test(str)) scaleY = -1;
  return { rotate, scaleX, scaleY };
}

/** 生成 transformType 字符串 */
export function buildComponentTransformType(rotate, scaleX, scaleY) {
  const rot = `rotate(${rotate}deg)`;
  if (scaleX === 1 && scaleY === 1) return rot;
  return `${rot} scale(${scaleX}, ${scaleY})`;
}

/** 水平/垂直镜像切换（再次点击恢复） */
export function toggleComponentMirror(style, axis) {
  if (!style) return;
  const { rotate, scaleX, scaleY } = parseComponentTransform(style);
  const nextX = axis === 'x' ? (scaleX === -1 ? 1 : -1) : scaleX;
  const nextY = axis === 'y' ? (scaleY === -1 ? 1 : -1) : scaleY;
  style.transform = rotate;
  style.transformType = buildComponentTransformType(rotate, nextX, nextY);
}

/** 旋转时保留当前镜像 */
export function applyComponentRotation(style, nextRotate) {
  if (!style) return;
  const { scaleX, scaleY } = parseComponentTransform(style);
  style.transform = nextRotate;
  style.transformType = buildComponentTransformType(nextRotate, scaleX, scaleY);
}
