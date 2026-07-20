import JsZip from 'jszip';
import { saveAs } from 'file-saver';

/**
 * 根据提供的JSON 数据生成 JSON 格式的 zip 文件列表。
 */
export const generateJsonZipFiles = (jsonData) => {
  const files = [];
  for (const [module, objects] of jsonData.entries()) {
    for (const [lang, jsonObject] of Object.entries(objects)) {
      files.push({
        folderName: lang,
        fileName: module,
        fileType: 'json',
        fileData: convertToJsonBlob(jsonObject),
      });
    }
  }
  return files;
};

/**
 * 解析JSON Zip数据
 */
export const parseJsonZipData = (jsonMap, langFileNameEnum) => {
  const dataMap = new Map();
  for (const [module, rows] of jsonMap.entries()) {
    const fileObjects = {};
    const cellIndexMap = {};
    rows.forEach((row, rowNumber) => {
      if (rowNumber === 0) {
        row.forEach((cell, colNumber) => {
          if (colNumber !== 0) {
            fileObjects[langFileNameEnum[cell]] = {};
            cellIndexMap[colNumber] = langFileNameEnum[cell];
          }
        });
      } else {
        Object.keys(cellIndexMap)
          .map((item) => Number(item))
          .map((colNumber) => {
            const key = row[0];
            const value = row[colNumber];
            const lang = cellIndexMap[colNumber];
            fileObjects[lang][key] = value;
          });
      }
    });
    dataMap.set(module, fileObjects);
  }
  return dataMap;
};

export const convertToJsonBlob = (jsonObject) => {
  const jsonString = JSON.stringify(jsonObject, null, 2);
  return new Blob([jsonString], { type: 'application/json' });
};

/**
 * 将多个文件下载并打包成一个 zip 文件。
 */
export function downloadFiles2Zip(params) {
  const zip = new JsZip();
  params.files.map((file) => handleEachFile(file, zip));
  zip.generateAsync({ type: 'blob' }).then((blob) => {
    saveAs(blob, `${params.zipName}.zip`);
  });
}

/**
 * 处理每个文件，将其添加到zip文件中。
 */
export const handleEachFile = ({ folderName, fileName, fileType, fileData }, zip) => {
  if (folderName) {
    zip.folder(folderName)?.file(`${fileName}.${fileType}`, fileData);
  } else {
    zip.file(`${fileName}.${fileType}`, fileData);
  }
};
