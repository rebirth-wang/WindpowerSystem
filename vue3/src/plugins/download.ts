import axios from 'axios';
import { ElMessage } from 'element-plus';
import { saveAs } from 'file-saver';
import { getToken } from '@/utils/auth';
import errorCode from '@/utils/errorCode';
import { blobValidate } from '@/utils/ruoyi';

const baseURL = import.meta.env.VITE_APP_BASE_API;

export default {
  name(name: string, isDelete: boolean = true) {
    const url = baseURL + '/common/download?fileName=' + encodeURIComponent(name) + '&delete=' + isDelete;
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: { Authorization: 'Bearer ' + getToken() },
    }).then(async (res) => {
      const isLogin = await blobValidate(res.data);
      if (isLogin) {
        const blob = new Blob([res.data]);
        this.saveAs(blob, decodeURIComponent(res.headers['download-filename']));
      } else {
        this.printErrMsg(res.data);
      }
    });
  },
  resource(resource: string) {
    const url = baseURL + '/common/download/resource?resource=' + encodeURIComponent(resource);
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: { Authorization: 'Bearer ' + getToken() },
    }).then(async (res) => {
      const isLogin = await blobValidate(res.data);
      if (isLogin) {
        const blob = new Blob([res.data]);
        this.saveAs(blob, decodeURIComponent(res.headers['download-filename']));
      } else {
        this.printErrMsg(res.data);
      }
    });
  },
  // 下载产品json文件
  downloadProductJson() {
    const url = baseURL + '/iot/product/downloadJson';
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: { Authorization: 'Bearer ' + getToken() },
    }).then(async (res) => {
      const blob = new Blob([res.data], { type: 'application/json' });
      saveAs(blob, 'product.json');
    });
  },
  zip(url: string, name: string) {
    const fullUrl = baseURL + url;
    axios({
      method: 'get',
      url: fullUrl,
      responseType: 'blob',
      headers: { Authorization: 'Bearer ' + getToken() },
    }).then(async (res) => {
      const isLogin = await blobValidate(res.data);
      if (isLogin) {
        const blob = new Blob([res.data], { type: 'application/zip' });
        this.saveAs(blob, name);
      } else {
        this.printErrMsg(res.data);
      }
    });
  },
  saveAs(text: any, name: string, opts?: any) {
    saveAs(text, name, opts);
  },
  async printErrMsg(data: Blob) {
    const resText = await data.text();
    const rspObj = JSON.parse(resText);
    const errMsg = (errorCode as any)[rspObj.code] || rspObj.msg || (errorCode as any)['default'];
    ElMessage.error(errMsg);
  },
};
