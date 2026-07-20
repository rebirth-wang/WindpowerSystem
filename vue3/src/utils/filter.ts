/**
 * 全局过滤器（Vue3 中全局过滤器已废除，改为普通函数导出）
 *
 * 时间戳转换日期
 */

const globalFilter = {
  // 时间戳转换日期
  formatDate: (value: number | string): string => {
    const date = new Date(value);
    const y = date.getFullYear();
    let MM: number | string = date.getMonth() + 1;
    MM = MM < 10 ? '0' + MM : MM;
    let d: number | string = date.getDate();
    d = d < 10 ? '0' + d : d;
    let h: number | string = date.getHours();
    h = h < 10 ? '0' + h : h;
    let m: number | string = date.getMinutes();
    m = m < 10 ? '0' + m : m;
    let s: number | string = date.getSeconds();
    s = s < 10 ? '0' + s : s;
    return y + '-' + MM + '-' + d + ' ' + h + ':' + m + ':' + s;
  },
};

export default globalFilter;
