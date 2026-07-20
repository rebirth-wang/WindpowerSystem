export function isExternal(path: string): boolean {
  return /^(https?:|mailto:|tel:)/.test(path);
}

export function validUsername(str: string): boolean {
  const valid_map = ['admin', 'editor'];
  return valid_map.indexOf(str.trim()) >= 0;
}

export function validURL(url: string): boolean {
  const reg =
    /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/;
  return reg.test(url);
}

export function validLowerCase(str: string): boolean {
  const reg = /^[a-z]+$/;
  return reg.test(str);
}

export function validUpperCase(str: string): boolean {
  const reg = /^[A-Z]+$/;
  return reg.test(str);
}

export function validAlphabets(str: string): boolean {
  const reg = /^[A-Za-z]+$/;
  return reg.test(str);
}

export function validEmail(email: string): boolean {
  const reg =
    /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return reg.test(email);
}

export function isString(str: any): boolean {
  return typeof str === 'string' || str instanceof String;
}

export function isArray(arg: any): boolean {
  return Array.isArray(arg);
}

export function checkNumber(rule: any, value: any, callback: any) {
  if (!value) {
    return callback(new Error('不能为空'));
  }
  setTimeout(() => {
    if (Number(value)) {
      if (value < 1 || value > 64) {
        callback(new Error('值范围为1-64'));
      } else {
        callback();
      }
    }
  }, 100);
}

export function checkNumberAddr(rule: any, value: any, callback: any) {
  if (!value) {
    return callback(new Error('不能为空'));
  }
  setTimeout(() => {
    if (Number(value)) {
      if (value < 1 || value > 65536) {
        callback(new Error('值范围为1-65536'));
      } else {
        callback();
      }
    }
  }, 100);
}
