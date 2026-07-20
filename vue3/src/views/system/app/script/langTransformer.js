// 获取语言包数据, new Map({fileName,{key:{ [lang]:value}}})
export const getLangJson = () => {
  // Vite 使用 import.meta.glob 替代 require.context
  const allModules = import.meta.glob('@/lang/**/*.json', { eager: true });
  const modules = new Map();
  Object.keys(allModules).forEach((key) => {
    // key 格式: /src/lang/zh-CN/xxx.json
    const match = key.match(/\/lang\/([^/]+)\/(.+)\.json$/);
    if (!match) return;
    const lang = match[1];
    const fileName = match[2];
    const obj = modules.get(fileName) || {};
    const mod = allModules[key].default || allModules[key];
    Object.entries(mod).forEach(([k, value]) => {
      const item = obj[k] || {};
      item[lang] = value;
      obj[k] = item;
    });
    modules.set(fileName, obj);
  });
  return modules;
};

// 转换为excel数据: [sheeNames:表,sheeData:单表数据]
export const transoformToExcel = (array2, langs) => {
  const sheetNames = [];
  const sheetData = [];
  for (const [sheetName, arr] of array2.entries()) {
    sheetNames.push(sheetName);
    sheetData.push(transformToExcelSheet(arr, langs));
  }
  return [sheetNames, sheetData];
};

// 转换为excel单表数据：[{'键值':'key','中文':'中文','英文':'English'}]
export const transformToExcelSheet = (jsonData, langs) => {
  let data = [];
  for (const [key, obj] of Object.entries(jsonData)) {
    let item = { 键值: key };
    for (const [lang, label] of Object.entries(langs)) {
      item[label] = obj[lang] || '';
    }
    data.push(item);
  }
  return data;
};
