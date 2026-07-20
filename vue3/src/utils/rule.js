import { menuList } from '@/views/ruleengine/editor/js/menu.js';

// 扁平化菜单数据并提取 type
function flattenMenuAndExtractTypes(menuData) {
  const flatten = (items) => {
    return items.reduce((acc, item) => {
      if (item.type) {
        acc.push(item.type);
      }
      if (item.children) {
        acc = acc.concat(flatten(item.children));
      }
      return acc;
    }, []);
  };

  return [...new Set(flatten(menuData))];
}

// 如果需要手动映射type和组件的关系，请在这里配置
const viewMap = {};

function barToUpperCase(str) {
  return str
    .split('-')
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join('');
}

// 优先匹配 map，否则将自动匹配
export function parseViewName(component) {
  var viewName = viewMap[component.type];
  if (viewName) {
    viewName = 'Flow' + barToUpperCase(viewName);
  } else {
    viewName = 'Flow' + barToUpperCase(component.type);
  }
  return viewName;
}

// 返回唯一标识
export function getUUID() {
  return Math.random().toString(36).substr(3, 10);
}

// 获取nodeId
const typeList = flattenMenuAndExtractTypes(menuList);

export function getNodeId(type) {
  const uuid = getUUID();
  if (typeList.indexOf(type) !== -1) {
    const str = type.replace(/-/g, '');
    return `${str}_${uuid}`;
  } else {
    return uuid;
  }
}
