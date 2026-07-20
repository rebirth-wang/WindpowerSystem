import { defineStore } from 'pinia';
import { ref } from 'vue';

/** A cached dictionary entry (key → resolved value list) */
export interface DictEntry {
  key: string;
  value: any[];
}

export const useDictStore = defineStore('dict', () => {
  const dict = ref<DictEntry[]>([]);

  function setDict(data: DictEntry) {
    if (data.key !== null && data.key !== '') {
      dict.value.push({
        key: data.key,
        value: data.value,
      });
    }
  }

  function removeDict(key: string) {
    try {
      for (let i = 0; i < dict.value.length; i++) {
        if (dict.value[i].key == key) {
          dict.value.splice(i, 1);
          return true;
        }
      }
    } catch (e) {
      console.error(e);
    }
  }

  function cleanDict() {
    dict.value = [];
  }

  return {
    dict,
    setDict,
    removeDict,
    cleanDict,
  };
});
